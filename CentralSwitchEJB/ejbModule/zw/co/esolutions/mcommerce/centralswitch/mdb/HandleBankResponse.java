package zw.co.esolutions.mcommerce.centralswitch.mdb;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.MessageDriven;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.ewallet.enums.MobileNetworkOperator;
import zw.co.esolutions.ewallet.enums.ResponseType;
import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.msg.MerchantRequest;
import zw.co.esolutions.ewallet.msg.MerchantResponse;
import zw.co.esolutions.ewallet.msg.NotificationInfo;
import zw.co.esolutions.ewallet.msg.RequestInfo;
import zw.co.esolutions.ewallet.msg.ResponseInfo;
import zw.co.esolutions.ewallet.sms.MessageSender;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.SystemConstants;
import zw.co.esolutions.mcommerce.centralswitch.model.MessageTransaction;
import zw.co.esolutions.mcommerce.centralswitch.processes.ProcessBankResponse;
import zw.co.esolutions.mcommerce.centralswitch.processes.ProcessSMSRequest;
import zw.co.esolutions.mcommerce.centralswitch.processes.ProcessUtil;
import zw.co.esolutions.mcommerce.centralswitch.util.ISO8583Processor;
import zw.co.esolutions.topup.ws.impl.WsResponse;

/**
 * Message-Driven Bean implementation class for: HandleBankResponse
 * 
 */
@MessageDriven(activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") }, mappedName = "jms/SWITCH.IN.QUEUE")
public class HandleBankResponse implements MessageListener {

	@EJB
	private ProcessSMSRequest messageProcessor;
	@EJB
	private ProcessBankResponse processBankResponse;
	
	@EJB
	private ProcessUtil switchProcessUtil;

	@Resource(mappedName = "jms/EWalletQCF")
	private QueueConnectionFactory jmsQueueConnectionFactory;

	@Resource(mappedName = EWalletConstants.FROM_SWITCH_TO_ZB_QUEUE)
	private Queue transactionsQueue;

	@Resource(mappedName = EWalletConstants.ECONET_SMS_OUT_QUEUE)
	private Queue notificationsQueue;

	private Connection jmsConnection;

	/**
	 * Default constructor.
	 */
	public HandleBankResponse() {
		// TODO Auto-generated constructor stub
	}

	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/switch.log.properties");
			LOG = Logger.getLogger(HandleBankResponse.class);
		} catch (Exception e) {
		}
	}

	@PostConstruct
	public void initialise() {
		try {
			jmsConnection = jmsQueueConnectionFactory.createConnection();
		} catch (JMSException e) {
			LOG.fatal("Failed to initialise MDB " + this.getClass().getSimpleName(), e);
			e.printStackTrace(System.err);
			throw new EJBException(e);
		}
	}

	@PreDestroy
	public void cleanUp() {
		if (jmsConnection != null) {
			try {
				jmsConnection.close();
			} catch (JMSException e) {
				// Ignore this guy
			}
		}
	}

	
	/**
	 * @see MessageListener#onMessage(Message)
	 */
	public void onMessage(Message message) {

		try {
			LOG.debug("Handle Switch Transaction Request has consumed.");
			ObjectMessage msg = (ObjectMessage) message;
			Object obj = msg.getObject();
			if (obj instanceof RequestInfo) {
				RequestInfo requestInfo = (RequestInfo) obj;
				this.processTransactionRequest(requestInfo);
			} else if (obj instanceof ResponseInfo) {
				ResponseInfo responseInfo = (ResponseInfo) obj;
				this.processTransactionResponse(responseInfo);
			} else if (obj instanceof MerchantRequest) {
				MerchantRequest merchantRequest = (MerchantRequest) obj;
				this.processMerchantRequest(merchantRequest);
			} else if (obj instanceof MerchantResponse) {
				MerchantResponse merchantResponse = (MerchantResponse) obj;
				this.processMerchantResponse(merchantResponse);
			} else {
				LOG.debug("Unknown message picked up from switch queue Canonical Class : " + obj.getClass().getCanonicalName() + " Simple Name : " + obj.getClass().getSimpleName());
			}
		} catch (JMSException e) {
			LOG.debug("JMS Exception Thrown : Message " + e.getMessage() + " : ", e);
		}
	}

	// Router Logic
	private void processTransactionRequest(RequestInfo requestInfo) {
		return;
	}

	private void processTransactionResponse(ResponseInfo responseInfo) {

		if (ResponseType.NOTIFICATION.equals(responseInfo.getResponseType())) {
			this.handleNotificationResponse(responseInfo);
			return;
		}

		if (ResponseType.TOPUP_REQUEST.equals(responseInfo.getResponseType())) {
			this.handleTopupRequest(responseInfo);
			return;
		}

		if (ResponseType.BILLPAY_REQUEST.equals(responseInfo.getResponseType())) {
			this.handleMerchantBillPayRequest(responseInfo);
			return;
		}

		if (ResponseType.REVERSAL_RESPONSE.equals(responseInfo.getResponseType())) {
			this.handleReversalResponse(responseInfo);
			return;
		}
	}

	private void processMerchantRequest(MerchantRequest merchantRequest) {
		return;
	}

	private void processMerchantResponse(MerchantResponse merchantResponse) {
		return;
	}

	// MDB code
	private void handleNotificationResponse(ResponseInfo responseInfo) {
		try {
			LOG.debug("Notification MDB Has consumed...");

			if (!ResponseType.NOTIFICATION.equals(responseInfo.getResponseType())) {
				LOG.debug("Wrong MDB Consumed....." + responseInfo.getResponseType());
				return;
			}
			LOG.debug("Processing notifications ...");
			List<NotificationInfo> notifications = processBankResponse.process(responseInfo);
			LOG.debug("DONE Handling TXN Advice : Do the notification now!");
			if (notifications != null) {
				LOG.debug("Notifications to send : " + notifications.size());
				for (NotificationInfo notificationInfo : notifications) {
					try {
						// Set MNO For the notification
						String mobileNumber = notificationInfo.getReceiptientMobileNumber();
						notificationInfo.setMno(this.getMNO(mobileNumber));

						boolean sent = MessageSender.sendSMSToMobileDestination(jmsConnection, notificationInfo.getNarrative(), notificationInfo.getReceiptientMobileNumber(), notificationsQueue, LOG);
						if (sent) {
							LOG.debug("Notification sent......" + notificationInfo);
						} else {
							LOG.fatal("FAILED to send notification into SMPP OUT Queue queue.");
						}
						
					} catch (Exception e) {
						LOG.debug("Failed to send notification : [" + notificationInfo + "] Reason " + e.getMessage());
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handleTopupRequest(ResponseInfo responseInfo) {
		LOG.debug(">>>>>>>>>>>>>>>>>>>>HandleTopupRequest MDB Has consumed!!!!!!");
		try {

			if (!zw.co.esolutions.ewallet.enums.ResponseType.TOPUP_REQUEST.equals(responseInfo.getResponseType())) {
				throw new Exception(">>>>>>> ERROR: WRONG MDB HAS CONSUMED!! : HandleTopupRequest");
			}
			// retrieve txn and update
			MessageTransaction txn = messageProcessor.findMessageTransactionByUUID(responseInfo.getRequestInfo().getMessageId());
			if (txn != null) {
				LOG.debug("Found a matching transaction !!!!!!");
				txn = messageProcessor.promoteTransactionStatus(txn, TransactionStatus.BANK_RESPONSE, "Debit Transaction Successful");
				LOG.debug("Done updating txn to BANK RESPONSE");

				WsResponse response = null;
				try {
					response = this.switchProcessUtil.sendTopupRequest(responseInfo);

				} catch (Exception e) {
					LOG.debug("Topup request failed " + e.getMessage());
					LOG.debug(e);
				}
				if (response != null) {
					String respCode = response.getResponseCode();
					if (SystemConstants.RESPONSE_CODE_OK_SHORT.equalsIgnoreCase(respCode) || SystemConstants.RESPONSE_CODE_OK_LONG.equalsIgnoreCase(respCode)) {
						LOG.debug("Topup Successful for Number" + responseInfo.getRequestInfo().getSourceMobile());

						txn.setResponseCode(ResponseCode.E000.name());
						txn = this.messageProcessor.updateTransaction(txn);
						LOG.debug("Done updating transaction response code to E000.");

						txn = messageProcessor.promoteTransactionStatus(txn, TransactionStatus.COMPLETED, response.getNarrative());
						LOG.debug("Done promoting transaction to successful.");
						long bal = (long) (response.getAirtimeBalance() * 100);
						responseInfo.setMerchantAccBalance(bal);
						responseInfo.setResponseCode(ResponseCode.E000);
						responseInfo.setNarrative(response.getNarrative());
					} else {
						LOG.debug("TOPUP Transaction failed for txn " + txn.getUuid() + " for source mobile " + txn.getSourceMobileNumber() + " AND BENEFICIARY MOBILE " + txn.getDestinationBankAccount());
						txn.setResponseCode(ResponseCode.E900.name());
						responseInfo.setResponseCode(ResponseCode.E900);
						responseInfo.setNarrative(response.getNarrative());

						txn = this.messageProcessor.updateTransaction(txn);
						LOG.debug("Done updating transaction response code to E900.");

						txn = messageProcessor.promoteTransactionStatus(txn, TransactionStatus.FAILED, response.getNarrative());
						LOG.debug("Done promoting transaction to failed.");
					}
				} else {
					LOG.debug("Webservice call returns Response of  NULL for number " + responseInfo.getRequestInfo().getSourceMobile());
					txn.setResponseCode(ResponseCode.E900.name());
					responseInfo.setResponseCode(ResponseCode.E900);
					responseInfo.setNarrative("Credit Transaction Failed.");

					txn = this.messageProcessor.updateTransaction(txn);
					LOG.debug("Done updating transaction response code to E900.");

					txn = messageProcessor.promoteTransactionStatus(txn, TransactionStatus.FAILED, responseInfo.getNarrative());
					LOG.debug("Done promoting transaction to failed.");
				}
				LOG.debug("Done sending Topup Credit Request.");
				responseInfo.setResponseType(ResponseType.MERCHANT_RESPONSE);
				LOG.debug("Done setting ResponseType MERCHANT_RESPONSE .");

				MerchantResponse merchantResponse = this.messageProcessor.populateTopupMerchantResponse(responseInfo, response);
				LOG.debug("Done populating merchant response.");

				boolean sent = MessageSender.sendObjectToDestination(jmsConnection, merchantResponse, merchantResponse.getResponseType().toString(), transactionsQueue, LOG);
				if (sent) {
					LOG.debug("STEP F Done putting message into FROM_SWITCH_TO_ZB_QUEUE queue.");
				} else {
					LOG.fatal("FAILED to put MERCHANT RESPONSE into FROM_SWITCH_TO_ZB_QUEUE queue.");
				}
			} else {
				LOG.debug("Could not find a matching transaction " + responseInfo.getRequestInfo().getMessageId());
			}
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void handleReversalResponse(ResponseInfo responseInfo) {
		try {
			LOG.debug("Reversal Response MDB Has consumed...");

			if (!ResponseType.REVERSAL_RESPONSE.equals(responseInfo.getResponseType())) {
				LOG.debug("Wrong MDB Consumed....." + responseInfo.getResponseType());
				return;
			}
			LOG.debug("Processing notifications ...");
			List<NotificationInfo> notifications = processBankResponse.processReversal(responseInfo);
			LOG.debug("Notifications to send : " + notifications.size());
			if (notifications != null) {
				for (NotificationInfo notificationInfo : notifications) {
					// Set MNO For the notification
					String mobileNumber = notificationInfo.getReceiptientMobileNumber();
					notificationInfo.setMno(this.getMNO(mobileNumber));

					boolean sent = MessageSender.sendSMSToMobileDestination(jmsConnection, notificationInfo.getNarrative(), notificationInfo.getReceiptientMobileNumber(), notificationsQueue, LOG);
					if (sent) {
						LOG.debug("Notification sent......" + notificationInfo);
					} else {
						LOG.fatal("FAILED to send notification into SMPP OUT Queue queue.");
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handleMerchantBillPayRequest(ResponseInfo responseInfo) {
		LOG.debug(">>>>>>>>>>>>>>>>>>>>HandleMerchantBillPayRequest MDB Has consumed!!!!!!");
		try {

			if (responseInfo == null) {
				return;
			}
			// retrieve txn and update
			MessageTransaction txn = messageProcessor.findMessageTransactionByUUID(responseInfo.getRequestInfo().getMessageId());
			
			if (txn != null) {
				LOG.debug("Found OG TXN for MERCHANT CREDIT RQST " + txn.getUuid());
		
				if (ResponseCode.E000.equals(responseInfo.getResponseCode())) {
					LOG.debug("STEP E Found a matching transaction !!!!!!");
					txn = messageProcessor.promoteTransactionStatus(txn, TransactionStatus.BANK_RESPONSE, "Debit Transaction Successful");
					LOG.debug("Done updating txn to BANK RESPONSE");
					
					if(txn.isOnline()){
						LOG.debug("BILLPAY is ONLINE for " + txn.getUtilityName() + " customer account number " + txn.getCustomerUtilityAccount());
						txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
						txn.setStatus(TransactionStatus.CREDIT_REQUEST);
						txn = messageProcessor.updateTransaction(txn);
						LOG.debug("Done updating transaction response code to E000.");
	
						txn = messageProcessor.promoteTransactionStatus(txn, TransactionStatus.CREDIT_REQUEST, "Credit Request");
						LOG.debug("Done promoting transaction to " + txn.getStatus());
						MessageSender.sendTextToQueueDestination(jmsConnection, ISO8583Processor.convertToISO8583XMLString(txn, false), txn.getTransactionRoutingInfo().getMerchantRequestQueueName(), LOG);
						return;
					}else{
						LOG.debug("BILLPAY Successful for " + responseInfo.getRequestInfo().getUtilityName() + " customer account number " + responseInfo.getRequestInfo().getCustomerUtilityAccount());
						txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
						txn = this.messageProcessor.updateTransaction(txn);
						LOG.debug("Done updating transaction response code to E000.");
	
						txn = messageProcessor.promoteTransactionStatus(txn, TransactionStatus.COMPLETED, "Transaction complete.");
						LOG.debug("Done promoting transaction to successful.");
	
						responseInfo.setResponseCode(ResponseCode.E000);
						responseInfo.setNarrative("Transaction complete.");
					}
				} else {
					LOG.debug("STEP D.1 Found a matching transaction !!!!!!");

					txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
					responseInfo.setResponseCode(ResponseCode.E900);
					responseInfo.setNarrative("Credit Transaction Incomplete, to be reconciled.");

					txn = this.messageProcessor.updateTransaction(txn);
					LOG.debug("Done updating transaction response code to E900.");

					txn = messageProcessor.promoteTransactionStatus(txn, TransactionStatus.FAILED, responseInfo.getNarrative());
					LOG.debug("Done promoting transaction to failed.");
				}

			} else {
				// LOG.debug("Webservice call returns CreditResponse of  NULL for number "
				// + responseInfo.getRequestInfo().getSourceMobile());
				LOG.debug("Could not find a matching transaction " + responseInfo.getRequestInfo().getMessageId());
			}
			LOG.debug("Done sending Top up  Credit Request.");
			responseInfo.setResponseType(ResponseType.MERCHANT_RESPONSE);
			LOG.debug("Done setting ResponseType MERCHANT_RESPONSE .");

			MerchantResponse merchantResponse = this.messageProcessor.populateTopupMerchantResponse(responseInfo, null);
			if (merchantResponse != null) {
				LOG.debug("Done populating merchant response  code in string." + merchantResponse.getResponseCode().toString());
				LOG.debug("Done populating merchant response  response type." + merchantResponse.getResponseType().toString());
				LOG.debug("Done populating merchant response code." + merchantResponse.getResponseCode());
			}

			boolean sent = MessageSender.sendObjectToDestination(jmsConnection, merchantResponse, merchantResponse.getResponseType().toString(), transactionsQueue, LOG);
			if (sent) {
				LOG.debug("STEP F Done putting message into FROM_SWITCH_TO_ZB_QUEUE queue.");
			} else {
				LOG.fatal("FAILED to put MERCHANT RESPONSE into FROM_SWITCH_TO_ZB_QUEUE queue.");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private MobileNetworkOperator getMNO(String mobileNumber) {
		if (mobileNumber.startsWith("26373")) {
			return MobileNetworkOperator.TELECEL;
		} else if (mobileNumber.startsWith("26371")) {
			return MobileNetworkOperator.NETONE;
		} else if (mobileNumber.startsWith("26377")) {
			return MobileNetworkOperator.ECONET;
		} else {
			return null;
		}
	}

}
