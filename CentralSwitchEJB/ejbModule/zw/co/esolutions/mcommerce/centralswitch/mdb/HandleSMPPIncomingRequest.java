package zw.co.esolutions.mcommerce.centralswitch.mdb;

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
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.datacentre.dstv.ws.DSTVServiceSOAPProxy;
import zw.co.datacentre.dstv.ws.DstvMessage;
import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.msg.RequestInfo;
import zw.co.esolutions.ewallet.services.proxy.DstvServiceProxy;
import zw.co.esolutions.ewallet.sms.MessageSender;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.SystemConstants;
import zw.co.esolutions.mcommerce.centralswitch.model.MessageTransaction;
import zw.co.esolutions.mcommerce.centralswitch.model.TransactionRoutingInfo;
import zw.co.esolutions.mcommerce.centralswitch.processes.ProcessSMSRequest;
import zw.co.esolutions.mcommerce.centralswitch.util.ISO8583Processor;

/**
 * Message-Driven Bean implementation class for: SMPPIncomingProcessorMDB
 * 
 */
@MessageDriven(activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") }, mappedName = EWalletConstants.ECONET_SMS_IN_QUEUE)
public class HandleSMPPIncomingRequest implements MessageListener {

	@EJB
	private ProcessSMSRequest requestProcessor;

	@Resource(mappedName = "jms/EWalletQCF")
	private QueueConnectionFactory jmsQueueConnectionFactory;

	@Resource(mappedName = EWalletConstants.ECONET_SMS_OUT_QUEUE)
	private Queue notificationsQueue;

	private Connection jmsConnection;

	@PostConstruct
	public void initialise() {
		try {
			jmsConnection = jmsQueueConnectionFactory.createConnection();
		} catch (JMSException e) {
			LOG.fatal("Failed to initialise MDB " + this.getClass().getSimpleName(), e);
			e.printStackTrace(System.err);
//			throw new EJBException(e);
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
	 * Default constructor.
	 */
	public HandleSMPPIncomingRequest() {
	}

	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/switch.log.properties");
			LOG = Logger.getLogger(HandleSMPPIncomingRequest.class);
		} catch (Exception e) {
		}
	}

	/**
	 * @see MessageListener#onMessage(Message)
	 */
	public void onMessage(Message message) {

		LOG.debug("SMPP RQST received ");
		if (message instanceof TextMessage) {
			TextMessage textMessage = (TextMessage) message;
			String text;
			try {
				text = textMessage.getText();
			} catch (JMSException e1) {
				LOG.debug(e1);
				e1.printStackTrace(System.err);
				return;
			}
			try{
				LOG.debug("XML RQST from SMPP \n " + text);
				MessageTransaction txn = requestProcessor.parseSMSRequestMessage(text);
				LOG.debug("DONE Parsing Message : [" + txn + "]");
				if (txn == null) {
					LOG.debug("NULL PARSE RESULT : FORGET AND SMILE");
					return;
				}
				if (!SystemConstants.RESPONSE_CODE_OK_SHORT.equalsIgnoreCase(txn.getResponseCode())) {
					LOG.debug("Transaction Parsing failed : Return to sender [" + txn.getNarrative() + "]");
					MessageSender.sendSMSToMobileDestination(jmsConnection, txn.getNarrative(), txn.getSourceMobileNumber(), notificationsQueue, LOG);
					LOG.debug("Send resnding failed TXN Reply to sender");
					return;
				}
				LOG.debug("Transaction PARSING was successful : " + txn.getStatus());
				if (TransactionStatus.PAREQ.equals(txn.getStatus()) || TransactionType.PASSCODE.equals(txn.getTransactionType())) {
					LOG.debug("Processing a PAREQ");
					this.handlePAREQ(txn);
					LOG.debug("DONE Processing a PAREQ");
				} else if (TransactionType.CHANGE_PASSCODE.equals(txn.getTransactionType())) {
					LOG.debug("Processing a PIN CHANGE RQST");
					this.handlePinChangeRequest(txn);
					LOG.debug("DONE Processing PIN CHANGE");
				} else if (TransactionType.ADMIN_TAP.equals(txn.getTransactionType())) {
					LOG.debug("Processing an ADMIN TAP RQST");
					this.handleTapRequest(txn);
					LOG.debug("DONE Processing an ADMIN TAP RQST");
				} else if (TransactionStatus.VEREQ.equals(txn.getStatus())) {
					LOG.debug("Processing a VEREQ");
					this.handleVEREQ(txn);
					LOG.debug("DONE processing VEREQ");
				} else {
					LOG.warn("UNKNOWN RQST TYPE : " + txn.getStatus() + " | " + txn.getTransactionType());
				}
			}catch(Exception e){
				LOG.warn("Pane zvaitika but weekend hatisi kubatika ");
				e.printStackTrace(System.err);
			}
		} else {
			LOG.warn("Incorrect message type");
		}

	}

	private void handlePAREQ(MessageTransaction txn) {
		txn = requestProcessor.populatePAREQInfo(txn);
		txn = requestProcessor.handlePasscodeResponse(txn);
		
		if(SystemConstants.RESPONSE_CODE_OK_SHORT.equalsIgnoreCase(txn.getResponseCode())){
			LOG.debug("PAREQ was successful, now route the msg : to the merchant of to the bank");
			
			txn.setStatus(TransactionStatus.BANK_REQUEST);
			txn = requestProcessor.updateTransaction(txn);
			txn = requestProcessor.promoteTransactionStatus(txn, TransactionStatus.BANK_REQUEST, txn.getNarrative());
			
			TransactionType txnType = txn.getTransactionType();
			TransactionRoutingInfo txnRoutingInfo = txn.getTransactionRoutingInfo();
			TransactionStatus nextTxnStatus ;
			if (TransactionType.MERCHANT_REG.equals(txnType) || TransactionType.EWALLET_BILLPAY.equals(txnType) || TransactionType.BILLPAY.equals(txnType)) {
				if(txnRoutingInfo.isStraightThroughEnabled()){
					LOG.info("Submitting FOR ACCOUNT VALIDATION : [" + txn.getUuid() + " | " + txnType + " | " + txn.getUtilityName() + "]");
					nextTxnStatus = TransactionStatus.ACCOUNT_VALIDATION_RQST;
					txn.setStatus(nextTxnStatus);
					MessageSender.sendTextToQueueDestination(jmsConnection, ISO8583Processor.convertToISO8583XMLString(txn, false), txnRoutingInfo.getMerchantRequestQueueName(), LOG);
				}else{
					LOG.info("TXN is merchant txn but is not straight thru : SEND TO BANK");
					nextTxnStatus = TransactionStatus.BANK_REQUEST;
					txn.setStatus(nextTxnStatus);
					MessageSender.sendObjectToDestination(jmsConnection, requestProcessor.populateRequestInfo(txn), txn.getTransactionType().name(), txnRoutingInfo.getBankRequestQueueName(), LOG);
				}
			} else {
				LOG.debug("SEND TO BANK ");
				nextTxnStatus = TransactionStatus.BANK_REQUEST;
				txn.setStatus(nextTxnStatus);
				MessageSender.sendObjectToDestination(jmsConnection, requestProcessor.populateRequestInfo(txn), txn.getTransactionType().name(), txnRoutingInfo.getBankRequestQueueName(), LOG);
			}
			
			txn = requestProcessor.updateTransaction(txn);
			txn = requestProcessor.promoteTransactionStatus(txn, nextTxnStatus, txn.getNarrative());
		}
		else{
			LOG.debug("PAREQ failed : [" + txn.getNarrative() +"]");
			MessageSender.sendSMSToMobileDestination(jmsConnection, txn.getNarrative(), txn.getSourceMobileNumber(), notificationsQueue, LOG);
			LOG.debug("HERE we assume the txn had already been updated");
		}
	}

	private void handleTapRequest(MessageTransaction txn) {
		try {
			TransactionStatus nextTxnStatus;
			txn = requestProcessor.handleTapRequest(txn);
			MessageSender.sendSMSToMobileDestination(jmsConnection, txn.getNarrative(), txn.getSourceMobileNumber(), notificationsQueue, LOG);
			if (SystemConstants.RESPONSE_CODE_OK_SHORT.equalsIgnoreCase(txn.getResponseCode())) {
				LOG.debug("TAP Request processed successful");
				nextTxnStatus = TransactionStatus.COMPLETED;
			} else {
				nextTxnStatus = TransactionStatus.FAILED;
			}
			txn = requestProcessor.updateTransaction(txn);
			txn = requestProcessor.promoteTransactionStatus(txn, nextTxnStatus, txn.getNarrative());
		} catch (Exception e) {
			throw new EJBException(e);
		}

	}

	private void handlePinChangeRequest(MessageTransaction txn) {
		LOG.info("PROCESSING PIN CHANGE Request....");
		try {
			txn = requestProcessor.checkRegistration(txn);
			if (!SystemConstants.RESPONSE_CODE_OK_SHORT.equalsIgnoreCase(txn.getResponseCode())) {
				LOG.debug("PIN Change Validation Failed, RETURN to SENDER");
				MessageSender.sendSMSToMobileDestination(jmsConnection, txn.getNarrative(), txn.getSourceMobileNumber(), notificationsQueue, LOG);
				return;
			}
			// forward to the bank to do the pin change
			txn = requestProcessor.createTransaction(txn);
			txn = requestProcessor.promoteTransactionStatus(txn, TransactionStatus.VEREQ, txn.getNarrative());
			txn = requestProcessor.validatePinChangeRequest(txn);
			TransactionStatus nextStatus;
			if (SystemConstants.RESPONSE_CODE_OK_SHORT.equalsIgnoreCase(txn.getResponseCode())) {
				LOG.debug("Pin Change Validation Successful");
				nextStatus = TransactionStatus.BANK_REQUEST;
				LOG.debug("Forwarding to bank queue....[" + txn.getTransactionRoutingInfo().getBankRequestQueueName() + "]");
				RequestInfo requestInfo = requestProcessor.populateRequestInfo(txn);
				MessageSender.sendObjectToDestination(jmsConnection, requestInfo, "", txn.getTransactionRoutingInfo().getBankRequestQueueName(), LOG);
			} else {
				LOG.debug("PIN Change Validation Failed, RETURN to SENDER");
				nextStatus = TransactionStatus.FAILED;
				MessageSender.sendSMSToMobileDestination(jmsConnection, txn.getNarrative(), txn.getSourceMobileNumber(), notificationsQueue, LOG);
			}
			LOG.debug("Performing the actual PIN CHANGE");
			txn.setStatus(nextStatus);
			txn = requestProcessor.updateTransaction(txn);
			txn = requestProcessor.promoteTransactionStatus(txn, nextStatus, txn.getNarrative());

		} catch (Exception e) {
			throw new EJBException(e);
		}

	}

	private void handleVEREQ(MessageTransaction txn) {
		try {
			txn = requestProcessor.checkRegistration(txn);
			if (SystemConstants.RESPONSE_CODE_OK_SHORT.equalsIgnoreCase(txn.getResponseCode())) {
				LOG.debug("Check REG was successful : Persist the transaction : ");
				txn = requestProcessor.createTransaction(txn);
				LOG.debug("Done persisting txn : " + txn.getUuid() + "|" + txn.getTransactionType() +"|");
				
				txn = requestProcessor.promoteTransactionStatus(txn, TransactionStatus.VEREQ, txn.getNarrative());
				LOG.debug("Done promoting txn to VEREQ");
				
				if (TransactionType.BILLPAY.equals(txn.getTransactionType())) {
					LOG.debug("Validating BILLPAY txns..");
					txn = this.validateBillpayTransactions(txn);
				}
				
				if (SystemConstants.RESPONSE_CODE_OK_SHORT.equals(txn.getResponseCode())) {
					txn = requestProcessor.generatePasswordParts(txn);
					LOG.debug("Done generating password parts : " + txn.getNarrative());
				} else {
					txn.setNarrative("Request Rejected. Reason: " + txn.getNarrative() + " - ZB eWallet");
				}
				
				TransactionStatus nextTxnStatus;
				String nextRC;
				MessageSender.sendSMSToMobileDestination(jmsConnection, txn.getNarrative(), txn.getSourceMobileNumber(), notificationsQueue, LOG);
				if (SystemConstants.RESPONSE_CODE_OK_SHORT.equalsIgnoreCase(txn.getResponseCode())) {
					nextTxnStatus = TransactionStatus.VERES;
					nextRC = SystemConstants.RESPONSE_CODE_OK_SHORT;
				} else {
					nextTxnStatus = TransactionStatus.FAILED;
					nextRC = SystemConstants.RC_GENERAL_ERROR;
				}
				txn.setResponseCode(nextRC);
				txn.setStatus(nextTxnStatus);
				
				txn = requestProcessor.updateTransaction(txn);
				
				LOG.debug("Done updating txn");
				
				txn = requestProcessor.promoteTransactionStatus(txn, nextTxnStatus, txn.getNarrative());
				LOG.debug("Done promoting TXN to : " + nextTxnStatus);
				LOG.info(txn.getUuid() + "|" + txn.getTransactionType() + "|" + txn.getSourceMobileNumber() + "|" + nextTxnStatus + "|" + nextRC + "|" + txn.getNarrative());
			}else{
				LOG.info(txn.getUuid() + "|" + txn.getTransactionType() + "|" + txn.getSourceMobileNumber() + "|" + txn.getStatus() + "|" + txn.getResponseCode() + "|" + txn.getNarrative());
				MessageSender.sendSMSToMobileDestination(jmsConnection, txn.getNarrative(), txn.getSourceMobileNumber(), notificationsQueue, LOG);
			}
			
		} catch (Exception e) {
			throw new EJBException(e);
		}
	}
	
	private MessageTransaction validateBillpayTransactions(MessageTransaction txn) {
		
		try {
			
			TransactionRoutingInfo txnRoutingInfo = txn.getTransactionRoutingInfo();
			
			if (!txnRoutingInfo.isAccountValidationEnabled()) {
				LOG.debug("Online Account validation is disabled.. return");
				txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
				return txn;
			}
			
			if (EWalletConstants.MERCHANT_NAME_DSTV.equals(txn.getUtilityName())) {
				
				LOG.debug("RECEIVED >> : [" + txn.getUuid() + "|" + txn.getSourceMobileNumber() + "|" + txn.getAmount() + "|" + txn.getBouquetCode() + "|" + txn.getCustomerUtilityAccount() + "|" + txn.getNumberOfMonths() + "]");
				
				LOG.debug("Now validating DSTV bill payment.. calling WS..");
				
				DstvMessage message = new DstvMessage();
				message.setAmount(txn.getAmount());
				message.setBouquet(txn.getBouquetCode());
				message.setDstvAccount(txn.getCustomerUtilityAccount());
				message.setMonths(txn.getNumberOfMonths());
							
				message = DstvServiceProxy.getInstance().validate(message);
				
				LOG.debug(txn.getUuid() + ": DSTV validation response is: [" + message.getResponseCode() + "]");
				LOG.debug(txn.getUuid() + ": DSTV validation narrative is: [" + message.getNarrative() + "]");
				LOG.debug(txn.getSourceMobileNumber() + ": DSTV validation narrative is: [" + message.getNarrative() + "]");
				LOG.debug(txn.getUuid() + ": DSTV validation commission is: [" + message.getCommission() + "]");

				txn.setNarrative(message.getNarrative());
				txn.setResponseCode(message.getResponseCode());
				
				if (SystemConstants.RESPONSE_CODE_OK_SHORT.equals(message.getResponseCode())) {
					
					try {
						Double value = message.getCommission();
						txn.setCommission(value.longValue());
						LOG.debug(txn.getUuid() + ": Commission after parse: " + txn.getCommission());	
					} catch (Exception e) {
						e.printStackTrace();
						LOG.warn(txn.getUuid() + ": Failed to parse DStv commission..");
					}
					
					try {
						String [] tokens = message.getNarrative().split(":");
						String beneficiaryName = (tokens.length == 3)? tokens[2].trim() : tokens[1].trim();
						txn.setToCustomerName(beneficiaryName);
						LOG.debug(txn.getUuid() + ": Beneficiary after parse: " + beneficiaryName);
						
					} catch (Exception e) {
						e.printStackTrace();
						LOG.warn(txn.getUuid() + ": Failed to parse DStv beneficiary name..");
					}
					
					if (txn.getToCustomerName() == null) {
						LOG.fatal(txn.getUuid() + ": Reject this transaction.. beneficiaryName is NULL");
						txn.setResponseCode(ResponseCode.E505.name());
						txn.setNarrative(txn.getUuid() + ": Failed to retrieve account details from MultiChoice. Please try again later.");
					}
					
				}
				
			}
			
		} catch (Exception e) {
			LOG.fatal(txn.getUuid() + ": Exception validating Billpay txn: " + e.getMessage());
			txn.setResponseCode(ResponseCode.E505.name());
			txn.setNarrative(txn.getUtilityName() + " validation service is currently not available. Please try again later.");
		}

		return txn;

	}
	
}
