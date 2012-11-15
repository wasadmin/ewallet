package zw.co.esolutions.mcommerce.centralswitch.mdb;

import java.util.Date;

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

import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfileStatus;
import zw.co.esolutions.ewallet.enums.MobileNetworkOperator;
import zw.co.esolutions.ewallet.enums.MobileWebTransactionType;
import zw.co.esolutions.ewallet.enums.TransactionLocationType;
import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.enums.USSDTransactionType;
import zw.co.esolutions.ewallet.msg.MobileWebRequestMessage;
import zw.co.esolutions.ewallet.msg.RequestInfo;
import zw.co.esolutions.ewallet.msg.USSDRequestMessage;
import zw.co.esolutions.ewallet.sms.MessageSender;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.Formats;
import zw.co.esolutions.ewallet.util.NumberUtil;
import zw.co.esolutions.mcommerce.centralswitch.model.MessageTransaction;
import zw.co.esolutions.mcommerce.centralswitch.model.TransactionRoutingInfo;
import zw.co.esolutions.mcommerce.centralswitch.processes.MobileWebRequestProcessUtil;
import zw.co.esolutions.mcommerce.centralswitch.processes.ProcessSMSRequest;
import zw.co.esolutions.mcommerce.centralswitch.processes.USSDRequestProcessUtil;
import zw.co.esolutions.mcommerce.centralswitch.util.ISO8583Processor;
import zw.co.esolutions.mcommerce.refgen.service.ReferenceGeneratorServiceSOAPProxy;

/**
 * Message-Driven Bean implementation class for: HandleUSSDRequest
 * 
 */
@MessageDriven(activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") }, mappedName = EWalletConstants.FROM_USSD_TO_SWITCH_QUEUE)
public class HandleUSSDRequest implements MessageListener {

	@EJB
	private USSDRequestProcessUtil ussdProcessUtil;

	@EJB
	private MobileWebRequestProcessUtil mobileProcessUtil;
	
	@EJB
	private ProcessSMSRequest requestProcessor;

	@EJB
	private ProcessSMSRequest messageProcessor;

	@Resource(mappedName = "jms/EWalletQCF")
	private QueueConnectionFactory jmsQueueConnectionFactory;

	@Resource(mappedName = EWalletConstants.ECONET_SMS_OUT_QUEUE)
	private Queue smsReplyQueue;

	private Connection jmsConnection;

	/**
	 * Default constructor.
	 */
	public HandleUSSDRequest() {
		// TODO Auto-generated constructor stub
	}

	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/switch.log.properties");
			LOG = Logger.getLogger(HandleUSSDRequest.class);
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
			LOG.info("RECEIVED a USSD / MOBIWEB RQST : >");
			ObjectMessage msg = (ObjectMessage) message;

			if (msg.getObject() instanceof MobileWebRequestMessage) {
				LOG.debug("Processing a MOBIWEB RQST");
				MobileWebRequestMessage mobileWebRequest = (MobileWebRequestMessage)msg.getObject();
				this.handleMobileWebRequest(mobileWebRequest);
				LOG.debug("DONE Processing a MOBIWEB RQST");
			} else if (msg.getObject() instanceof USSDRequestMessage) {
				LOG.debug("Processing a USSD RQST");
				USSDRequestMessage ussdRequest = (USSDRequestMessage) msg.getObject();
				this.handleUSSDRequest(ussdRequest);
				LOG.debug("DONE Processing a USSD RQST");
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			LOG.fatal("Exception Thrown Message ::: " + e.getMessage());
			e.printStackTrace(System.err);
		}

	}

	private void handleUSSDRequest(USSDRequestMessage ussdRequest) throws Exception {

		// Route to Handle Ussd Messsage
		USSDTransactionType txnType = ussdRequest.getTransactionType();

		if (USSDTransactionType.TOPUP_TXT.equals(txnType)) {
			ussdRequest.setTransactionType(USSDTransactionType.TOPUP);
		}

		if (USSDTransactionType.AGENT_CUSTOMER_NON_HOLDER_WITHDRAWAL.equals(ussdRequest.getTransactionType())) {
			if (!isNonHolderValid(ussdRequest)) {
				return;
			}
		}
		String sessionId = ussdRequest.getUuid();
		MessageTransaction txn = this.processUSSDRequest(ussdRequest);
		RequestInfo requestInfo = this.ussdProcessUtil.populateRequestInfo(txn, ussdRequest);
		if (USSDTransactionType.TOPUP_TXT.equals(txnType)) {
			// Change Type to Txt
			
			if (txn != null) {
				txn.setTransactionType(TransactionType.valueOf(txnType.toString()));
				txn = messageProcessor.updateTransaction(txn);
			}
		}
		if (TransactionType.CHANGE_PASSCODE.equals(txn.getTransactionType())) {
			// pin change has to be handled separately
			LOG.debug("Submitting a pin change request.......");
			txn = messageProcessor.validatePinChangeRequest(txn);
			
		} else {

			// Transactions to notify via Notifications
			if (TransactionType.BALANCE.equals(txn.getTransactionType())) {
				ussdRequest.setUuid(sessionId);
				
				this.ussdProcessUtil.createUSSDTransaction(requestInfo, ussdRequest);
			}

			LOG.debug("START SEND TO BANK QUEUE");
			requestInfo.setLocationType(TransactionLocationType.USSD);
			
			TransactionRoutingInfo routingInfo = txn.getTransactionRoutingInfo();
			 
			LOG.debug("Routing Info : " + routingInfo);

			TransactionType theTxnType = requestInfo.getTransactionType();
			TransactionStatus nextTxnStatus ;
			
			if (TransactionType.MERCHANT_REG.equals(theTxnType) || TransactionType.EWALLET_BILLPAY.equals(theTxnType) || TransactionType.BILLPAY.equals(theTxnType)) {
				if(routingInfo.isStraightThroughEnabled()){
					LOG.info("Submitting FOR ACCOUNT VALIDATION : [" + txn.getUuid() + " | " + theTxnType + " | " + txn.getUtilityName() + "]");
					nextTxnStatus = TransactionStatus.ACCOUNT_VALIDATION_RQST;
					txn.setStatus(nextTxnStatus);
					MessageSender.sendTextToQueueDestination(jmsConnection, ISO8583Processor.convertToISO8583XMLString(txn, false), routingInfo.getMerchantRequestQueueName(), LOG);
				}else{
					LOG.info("TXN is merchant txn but is not straight thru : SEND TO BANK");
					nextTxnStatus = TransactionStatus.BANK_REQUEST;
					txn.setStatus(nextTxnStatus);
					MessageSender.sendObjectToDestination(jmsConnection, requestInfo, "", routingInfo.getBankRequestQueueName(), LOG);
				}
			} else {
				LOG.debug("SEND TO BANK ");
				nextTxnStatus = TransactionStatus.BANK_REQUEST;
				txn.setStatus(nextTxnStatus);
				MessageSender.sendObjectToDestination(jmsConnection, requestInfo, "",routingInfo.getBankRequestQueueName(), LOG);
			}
			
			txn = requestProcessor.updateTransaction(txn);
			txn = requestProcessor.promoteTransactionStatus(txn, nextTxnStatus, txn.getNarrative());
		}

	
		
	}

	private void handleMobileWebRequest(MobileWebRequestMessage mobileWebRequest) throws Exception{

		LOG.debug("Handling RQST from Mobile Web");

		
		
		if (MobileWebTransactionType.AGENT_CUSTOMER_NON_HOLDER_WITHDRAWAL.equals(mobileWebRequest.getTransactionType())) {
			if (!isNonHolderValid(mobileWebRequest)) {
				return;
			}
		}

		String sessionId = mobileWebRequest.getUuid();
		MobileWebTransactionType txnType = mobileWebRequest.getTransactionType();

		if (MobileWebTransactionType.TOPUP_TXT.equals(txnType)) {
			mobileWebRequest.setTransactionType(MobileWebTransactionType.TOPUP);
		}
		LOG.debug(">>>>>>>>>>>>>>>>>> About to Call processMobileWebRequest <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

		MessageTransaction txn = this.processMobileWebRequest(mobileWebRequest);
		RequestInfo requestInfo = this.mobileProcessUtil.populateRequestInfo(txn, mobileWebRequest);
		if (MobileWebTransactionType.TOPUP_TXT.equals(txnType)) {
			// Change Type to Txt
			
			if (txn != null) {
				txn.setTransactionType(TransactionType.valueOf(txnType.toString()));
				txn = messageProcessor.updateTransaction(txn);
			}
		}

		if (TransactionType.CHANGE_PASSCODE.equals(requestInfo.getTransactionType())) {

			// pin change has to be handled separately

			mobileWebRequest.setUuid(sessionId);

			// Transactions to notify via Notifications
			this.mobileProcessUtil.createMobileWebSession(requestInfo, mobileWebRequest);
			LOG.debug("Submitting a pin change request.......");
//			SMSProcessorResponse response = 
//				messageProcessor.processSMSRequestRequest(requestInfo);
//			if(response != null ){
//				if(response.isReturnToSender()){
//					MessageSender.sendSMSToMobileDestination(jmsConnection, response.getNarrative(), requestInfo.getSourceMobile(), smsReplyQueue, LOG);
//				}
//				if(response.isSendToBank()){
//					MessageSender.sendObjectToDestination(jmsConnection, response.getRequestInfo(), response.getRequestInfo().getTransactionType().name(), transactionsQueue, LOG);
//				}
//			}else{
//				LOG.warn("NULL Response found");
//			}
		} else {

			mobileWebRequest.setUuid(sessionId);

			// Transactions to notify via Notifications
			this.mobileProcessUtil.createMobileWebSession(requestInfo, mobileWebRequest);

			LOG.debug("START SEND TO BANK QUEUE");
			// requestInfo.setLocationType(TransactionLocationType.SMS);
//			MessageSender.sendObjectToDestination(jmsConnection, requestInfo, requestInfo.getTransactionType().name(), transactionsQueue, LOG);
			TransactionType theTxnType = requestInfo.getTransactionType();
			TransactionRoutingInfo routingInfo = txn.getTransactionRoutingInfo();
			TransactionStatus nextTxnStatus ;
			
			if (TransactionType.MERCHANT_REG.equals(theTxnType) || TransactionType.EWALLET_BILLPAY.equals(theTxnType) || TransactionType.BILLPAY.equals(theTxnType)) {
				if(routingInfo.isStraightThroughEnabled()){
					LOG.info("Submitting FOR ACCOUNT VALIDATION : [" + txn.getUuid() + " | " + theTxnType + " | " + txn.getUtilityName() + "]");
					nextTxnStatus = TransactionStatus.ACCOUNT_VALIDATION_RQST;
					txn.setStatus(nextTxnStatus);
					MessageSender.sendTextToQueueDestination(jmsConnection, ISO8583Processor.convertToISO8583XMLString(txn, false), routingInfo.getMerchantRequestQueueName(), LOG);
				}else{
					LOG.info("TXN is merchant txn but is not straight thru : SEND TO BANK");
					nextTxnStatus = TransactionStatus.BANK_REQUEST;
					txn.setStatus(nextTxnStatus);
					MessageSender.sendObjectToDestination(jmsConnection, requestInfo, "", routingInfo.getBankRequestQueueName(), LOG);
				}
			} else {
				LOG.debug("SEND TO BANK ");
				nextTxnStatus = TransactionStatus.BANK_REQUEST;
				txn.setStatus(nextTxnStatus);
				MessageSender.sendObjectToDestination(jmsConnection, requestInfo, "",routingInfo.getBankRequestQueueName(), LOG);
			}
			
			txn = requestProcessor.updateTransaction(txn);
			txn = requestProcessor.promoteTransactionStatus(txn, nextTxnStatus, txn.getNarrative());
			LOG.debug(">>>>>>>>> Request Info Sent to Bank : " + requestInfo.getTransactionType() + " Src Mobile : " + requestInfo.getSourceMobile());
		}

		return;

	
		
	}

	public MessageTransaction processUSSDRequest(USSDRequestMessage ussdRequest) throws Exception {
		LOG.debug("Now in processUSSDRequest...");

		ReferenceGeneratorServiceSOAPProxy proxy = new ReferenceGeneratorServiceSOAPProxy();
		String date = Formats.yearFormatTwoDigit.format(new Date(System.currentTimeMillis()));
		ussdRequest.setUuid(proxy.generateUUID(ussdRequest.getMno().name(), ussdRequest.getMno().name().substring(0, 1), date, 0L, 1000000000L - 1L));

		LOG.debug("Generated UUID = " + ussdRequest.getUuid());

		this.resolveMobileProfilePasswdCounts(ussdRequest);

		if (USSDTransactionType.BALANCE.equals(ussdRequest.getTransactionType())) {

			return this.processUSSDBalanceRequest(ussdRequest);

		} else if (USSDTransactionType.BILLPAY.equals(ussdRequest.getTransactionType())) {

			return this.processUSSDBillPayRequest(ussdRequest);

		} else if (USSDTransactionType.CHANGE_PASSCODE.equals(ussdRequest.getTransactionType())) {

			return this.processUSSDPinChangeRequest(ussdRequest);

		} else if (USSDTransactionType.MINI_STATEMENT.equals(ussdRequest.getTransactionType())) {

			return this.processUSSDMiniStatementRequest(ussdRequest);

		} else if (USSDTransactionType.TOPUP.equals(ussdRequest.getTransactionType())) {

			return this.processUSSDTopupRequest(ussdRequest);

		} else if (USSDTransactionType.TRANSFER.equals(ussdRequest.getTransactionType())) {

			return this.processUSSDTransferRequest(ussdRequest);

		} else if (USSDTransactionType.AGENT_CUSTOMER_WITHDRAWAL.equals(ussdRequest.getTransactionType())) {

			return this.processUSSDAgentCustomerWithdrawalRequest(ussdRequest);

		} else if (USSDTransactionType.AGENT_CUSTOMER_DEPOSIT.equals(ussdRequest.getTransactionType())) {

			return this.processUSSDAgentCustomerDepositRequest(ussdRequest);

		} else if (USSDTransactionType.AGENT_CUSTOMER_NON_HOLDER_WITHDRAWAL.equals(ussdRequest.getTransactionType())) {

			return this.processUSSDAgentCustomerNonHolderWithRequest(ussdRequest);

		} else if (USSDTransactionType.AGENT_TRANSFER.equals(ussdRequest.getTransactionType())) {

			return this.processUSSDAgentTransferRequest(ussdRequest);

		} else if (USSDTransactionType.AGENT_SUMMARY.equals(ussdRequest.getTransactionType())) {

			return this.processUSSDAgentSummaryRequest(ussdRequest);

		} else if (USSDTransactionType.REGISTER_MERCHANT.equals(ussdRequest.getTransactionType())) {

			return this.processUSSDMerchantRegRequest(ussdRequest);

		} else if (USSDTransactionType.RTGS.equals(ussdRequest.getTransactionType())) {

			return this.processUSSDRtgsRequest(ussdRequest);

		} else {
			LOG.debug("Unknown Transaction Type : " + ussdRequest.getTransactionType());
			return null;
		}

	}

	public MessageTransaction processMobileWebRequest(MobileWebRequestMessage webRequest) throws Exception {
		LOG.debug("Now in processMobileWebRequest...");

		ReferenceGeneratorServiceSOAPProxy proxy = new ReferenceGeneratorServiceSOAPProxy();
		String date = Formats.yearFormatTwoDigit.format(new Date(System.currentTimeMillis()));
		webRequest.setUuid(proxy.generateUUID(webRequest.getMno().name(), webRequest.getMno().name().substring(0, 1), date, 0L, 1000000000L - 1L));

		LOG.debug("Generated UUID = " + webRequest.getUuid());

		this.resolveMobileProfilePasswdCounts(webRequest);
		
		MessageTransaction txn;

		if (MobileWebTransactionType.BALANCE.equals(webRequest.getTransactionType())) {

			txn = this.processMobileWebBalanceRequest(webRequest);

		} else if (MobileWebTransactionType.BILLPAY.equals(webRequest.getTransactionType())) {

			txn = this.processMobileWebBillPayRequest(webRequest);

		} else if (MobileWebTransactionType.CHANGE_PASSCODE.equals(webRequest.getTransactionType())) {

			txn = this.processMobileWebPinChangeRequest(webRequest);

		} else if (MobileWebTransactionType.MINI_STATEMENT.equals(webRequest.getTransactionType())) {

			txn = this.processMobileWebMiniStatementRequest(webRequest);

		} else if (MobileWebTransactionType.TOPUP.equals(webRequest.getTransactionType())) {

			txn = this.processMobileWebTopupRequest(webRequest);

		} else if (MobileWebTransactionType.TRANSFER.equals(webRequest.getTransactionType())) {

			txn = this.processMobileWebTransferRequest(webRequest);

		} else if (MobileWebTransactionType.AGENT_CUSTOMER_WITHDRAWAL.equals(webRequest.getTransactionType())) {

			txn = this.processMobileWebAgentCustomerWithdrawalRequest(webRequest);

		} else if (MobileWebTransactionType.AGENT_CUSTOMER_DEPOSIT.equals(webRequest.getTransactionType())) {

			txn = this.processMobileWebAgentCustomerDepositRequest(webRequest);

		} else if (MobileWebTransactionType.AGENT_CUSTOMER_NON_HOLDER_WITHDRAWAL.equals(webRequest.getTransactionType())) {

			txn = this.processMobileWebAgentCustomerNonHolderWithRequest(webRequest);

		} else if (MobileWebTransactionType.AGENT_TRANSFER.equals(webRequest.getTransactionType())) {

			txn = this.processMobileWebAgentTransferRequest(webRequest);

		} else if (MobileWebTransactionType.AGENT_SUMMARY.equals(webRequest.getTransactionType())) {

			txn = this.processMobileWebAgentSummaryRequest(webRequest);

		} else if (MobileWebTransactionType.REGISTER_MERCHANT.equals(webRequest.getTransactionType())) {

			txn = this.processMobileWebMerchantRegRequest(webRequest);

		} else if (MobileWebTransactionType.RTGS.equals(webRequest.getTransactionType())) {

			txn = this.processMobileWebRtgsRequest(webRequest);

		} else {
			LOG.debug("Unknown Transaction Type : " + webRequest.getTransactionType());
			return null;
		}

		return txn;
	}

	private MessageTransaction processUSSDBalanceRequest(USSDRequestMessage ussdRequest) throws Exception {
		

		LOG.debug("Now in processUSSDBalanceRequest...");

		LOG.debug("START POPULATE MESSAGE_TRANSACTION");

		MessageTransaction messageTransaction = ussdProcessUtil.populateMessageTransaction(ussdRequest);

		LOG.debug("END POPULATE MESSAGE_TRANSACTION");

		messageTransaction.setNarrative("Balance Request");
		messageTransaction.setTransactionType(TransactionType.BALANCE);

		LOG.debug("TRANSACTION TYPE : " + messageTransaction.getTransactionType().name());
		LOG.debug("START CREATE MESSAGE_TRANSACTION");

		messageTransaction = ussdProcessUtil.createMessageTransaction(messageTransaction);

		LOG.debug("END CREATE MESSAGE_TRANSACTION");

		LOG.debug("START POPULATE REQUEST_INFO");
		
		return messageTransaction;
	}

	private MessageTransaction processMobileWebBalanceRequest(MobileWebRequestMessage webRequest) throws Exception {
		LOG.debug("Now in processMobileWebBalanceRequest...");

		LOG.debug("START POPULATE MESSAGE_TRANSACTION");

		MessageTransaction messageTransaction = mobileProcessUtil.populateMessageTransaction(webRequest);

		LOG.debug("END POPULATE MESSAGE_TRANSACTION");

		messageTransaction.setNarrative("Balance Request");
		messageTransaction.setTransactionType(TransactionType.BALANCE);

		LOG.debug("TRANSACTION TYPE : " + messageTransaction.getTransactionType().name());
		LOG.debug("START CREATE MESSAGE_TRANSACTION");

		messageTransaction = mobileProcessUtil.createMessageTransaction(messageTransaction);

		LOG.debug("END CREATE MESSAGE_TRANSACTION");

		LOG.debug("START POPULATE REQUEST_INFO");

		return messageTransaction;
	}
	
	private MessageTransaction processUSSDBillPayRequest(USSDRequestMessage ussdRequest) throws Exception {
		LOG.debug("Now in processUSSDBillPayRequest...");

		LOG.debug("START POPULATE MESSAGE_TRANSACTION");

		MessageTransaction messageTransaction = ussdProcessUtil.populateMessageTransaction(ussdRequest);

		LOG.debug("END POPULATE MESSAGE_TRANSACTION");

		messageTransaction.setNarrative("Bill Payment Request");
		messageTransaction.setTransactionType(TransactionType.BILLPAY);

		LOG.debug("TRANSACTION TYPE : " + messageTransaction.getTransactionType().name());

		LOG.debug("START CREATE MESSAGE_TRANSACTION");

		messageTransaction = ussdProcessUtil.createMessageTransaction(messageTransaction);

		LOG.debug("END CREATE MESSAGE_TRANSACTION");

		return messageTransaction;
	}

	private MessageTransaction processMobileWebBillPayRequest(MobileWebRequestMessage webRequest) throws Exception {
		LOG.debug("Now in processMobileWebBillPayRequest...");
		LOG.debug("START POPULATE MESSAGE_TRANSACTION");
		MessageTransaction messageTransaction = mobileProcessUtil.populateMessageTransaction(webRequest);
		LOG.debug("END POPULATE MESSAGE_TRANSACTION");
		messageTransaction.setNarrative("Bill Payment Request");
		messageTransaction.setTransactionType(TransactionType.BILLPAY);
		LOG.debug("TRANSACTION TYPE : " + messageTransaction.getTransactionType().name());
		LOG.debug("START CREATE MESSAGE_TRANSACTION");
		messageTransaction = mobileProcessUtil.createMessageTransaction(messageTransaction);
		LOG.debug("END CREATE MESSAGE_TRANSACTION");
		return messageTransaction;
	}

	private MessageTransaction processUSSDPinChangeRequest(USSDRequestMessage ussdRequest) throws Exception {
		
		LOG.debug("Now in processUSSDPinChangeRequest...");

		LOG.debug("START POPULATE MESSAGE_TRANSACTION");

		MessageTransaction messageTransaction = ussdProcessUtil.populateMessageTransaction(ussdRequest);

		LOG.debug("END POPULATE MESSAGE_TRANSACTION");

		messageTransaction.setNarrative("Change Passcode");
		messageTransaction.setTransactionType(TransactionType.CHANGE_PASSCODE);

		LOG.debug("TRANSACTION TYPE : " + messageTransaction.getTransactionType().name());
		LOG.debug("START CREATE MESSAGE_TRANSACTION");

		messageTransaction = ussdProcessUtil.createMessageTransaction(messageTransaction);

		LOG.debug("END CREATE MESSAGE_TRANSACTION");
		return messageTransaction;
	}

	private MessageTransaction processMobileWebPinChangeRequest(MobileWebRequestMessage webRequest) throws Exception {
	
		LOG.debug("Now in processMobileWebPinChangeRequest...");

		LOG.debug("START POPULATE MESSAGE_TRANSACTION");

		MessageTransaction messageTransaction = mobileProcessUtil.populateMessageTransaction(webRequest);

		LOG.debug("END POPULATE MESSAGE_TRANSACTION");

		messageTransaction.setNarrative("Change Passcode");
		messageTransaction.setTransactionType(TransactionType.CHANGE_PASSCODE);

		LOG.debug("TRANSACTION TYPE : " + messageTransaction.getTransactionType().name());
		LOG.debug("START CREATE MESSAGE_TRANSACTION");

		messageTransaction = mobileProcessUtil.createMessageTransaction(messageTransaction);

		LOG.debug("END CREATE MESSAGE_TRANSACTION");

		LOG.debug("START POPULATE REQUEST_INFO");

		
		return messageTransaction;
	}

	private MessageTransaction processUSSDMiniStatementRequest(USSDRequestMessage ussdRequest) throws Exception {
		
		LOG.debug("Now in processUSSDMiniStatementRequest...");

		LOG.debug("START POPULATE MESSAGE_TRANSACTION");

		MessageTransaction messageTransaction = ussdProcessUtil.populateMessageTransaction(ussdRequest);

		LOG.debug("END POPULATE MESSAGE_TRANSACTION");

		messageTransaction.setNarrative("Ministatement Request");
		messageTransaction.setTransactionType(TransactionType.MINI_STATEMENT);

		LOG.debug("TRANSACTION TYPE : " + messageTransaction.getTransactionType().name());
		LOG.debug("START CREATE MESSAGE_TRANSACTION");

		messageTransaction = ussdProcessUtil.createMessageTransaction(messageTransaction);

		LOG.debug("END CREATE MESSAGE_TRANSACTION");


		return messageTransaction;
	}

	private MessageTransaction processMobileWebMiniStatementRequest(MobileWebRequestMessage webRequest) throws Exception {
		
		LOG.debug("Now in processMobileWebMiniStatementRequest...");

		LOG.debug("START POPULATE MESSAGE_TRANSACTION");

		MessageTransaction messageTransaction = mobileProcessUtil.populateMessageTransaction(webRequest);

		LOG.debug("END POPULATE MESSAGE_TRANSACTION");

		messageTransaction.setNarrative("Ministatement Request");
		messageTransaction.setTransactionType(TransactionType.MINI_STATEMENT);

		LOG.debug("TRANSACTION TYPE : " + messageTransaction.getTransactionType().name());
		LOG.debug("START CREATE MESSAGE_TRANSACTION");

		messageTransaction = mobileProcessUtil.createMessageTransaction(messageTransaction);

		LOG.debug("END CREATE MESSAGE_TRANSACTION");

		LOG.debug("START POPULATE REQUEST_INFO");

		
		return messageTransaction;
	}

	private MessageTransaction processUSSDTopupRequest(USSDRequestMessage ussdRequest) throws Exception {
		
		LOG.debug("Now in processUSSDTopupRequest...");

		LOG.debug("START POPULATE MESSAGE_TRANSACTION");

		MessageTransaction messageTransaction = ussdProcessUtil.populateMessageTransaction(ussdRequest);

		LOG.debug("END POPULATE MESSAGE_TRANSACTION");

		messageTransaction.setNarrative("Topup Request");
		messageTransaction.setTransactionType(TransactionType.TOPUP);

		LOG.debug("TRANSACTION TYPE : " + messageTransaction.getTransactionType().name());
		LOG.debug("START CREATE MESSAGE_TRANSACTION");

		messageTransaction = ussdProcessUtil.createMessageTransaction(messageTransaction);

		LOG.debug("END CREATE MESSAGE_TRANSACTION");

		
		return messageTransaction;
	}

	private MessageTransaction processMobileWebTopupRequest(MobileWebRequestMessage webRequest) throws Exception {
		LOG.debug("Now in processMobileWebTopupRequest...");

		LOG.debug("START POPULATE MESSAGE_TRANSACTION");

		MessageTransaction messageTransaction = mobileProcessUtil.populateMessageTransaction(webRequest);

		LOG.debug("END POPULATE MESSAGE_TRANSACTION");

		messageTransaction.setNarrative("Topup Request");
		messageTransaction.setTransactionType(TransactionType.TOPUP);

		LOG.debug("TRANSACTION TYPE : " + messageTransaction.getTransactionType().name());
		LOG.debug("START CREATE MESSAGE_TRANSACTION");

		messageTransaction = mobileProcessUtil.createMessageTransaction(messageTransaction);

		LOG.debug("END CREATE MESSAGE_TRANSACTION");

		
		return messageTransaction;
	}

	private MessageTransaction processUSSDTransferRequest(USSDRequestMessage ussdRequest) throws Exception {
		LOG.debug("Now in processUSSDTransferRequest...");

		LOG.debug("START POPULATE MESSAGE_TRANSACTION");

		MessageTransaction messageTransaction = ussdProcessUtil.populateMessageTransaction(ussdRequest);
		messageTransaction.setNarrative("Transfer Request");

		LOG.debug("END POPULATE MESSAGE_TRANSACTION");

		// check transfer type
		if (messageTransaction.getTransactionType() != null) {

			LOG.debug("Transaction Type : " + messageTransaction.getTransactionType().name());
			LOG.debug("START CREATE MESSAGE_TRANSACTION");

			messageTransaction = ussdProcessUtil.createMessageTransaction(messageTransaction);

			LOG.debug("END CREATE MESSAGE_TRANSACTION");

			return messageTransaction;

		} else {
			LOG.debug("TRANSACTION TYPE is null");
			throw new Exception("Transaction type is null");
		}
	
	}

	private MessageTransaction processMobileWebTransferRequest(MobileWebRequestMessage webRequest) throws Exception {
		LOG.debug("Now in processMobileWebTransferRequest...");

		LOG.debug("START POPULATE MESSAGE_TRANSACTION");

		MessageTransaction messageTransaction = mobileProcessUtil.populateMessageTransaction(webRequest);
		messageTransaction.setNarrative("Transfer Request");

		LOG.debug("END POPULATE MESSAGE_TRANSACTION");

		// check transfer type
		if (messageTransaction.getTransactionType() != null) {

			LOG.debug("Transaction Type : " + messageTransaction.getTransactionType().name());
			LOG.debug("START CREATE MESSAGE_TRANSACTION");

			messageTransaction = mobileProcessUtil.createMessageTransaction(messageTransaction);

			LOG.debug("END CREATE MESSAGE_TRANSACTION");

			return messageTransaction;
		} else {
			LOG.debug("TRANSACTION TYPE is null");
			throw new Exception("Transaction type is null");
		}
		
	}

	private MessageTransaction processUSSDAgentCustomerDepositRequest(USSDRequestMessage ussdRequest) throws Exception {
		
		LOG.debug("Now in processUSSDAgentCustomerDepositRequest...");

		LOG.debug("START POPULATE MESSAGE_TRANSACTION");

		MessageTransaction messageTransaction = ussdProcessUtil.populateMessageTransaction(ussdRequest);
		messageTransaction.setNarrative("Agent Customer Deposit Request");
		messageTransaction.setTransactionType(TransactionType.AGENT_CUSTOMER_DEPOSIT);

		LOG.debug("END POPULATE MESSAGE_TRANSACTION");

		// check transfer type
		if (messageTransaction.getTransactionType() != null) {

			LOG.debug("Transaction Type : " + messageTransaction.getTransactionType().name());
			LOG.debug("START CREATE MESSAGE_TRANSACTION");

			messageTransaction = ussdProcessUtil.createMessageTransaction(messageTransaction);

			LOG.debug("END CREATE MESSAGE_TRANSACTION");

			return messageTransaction;
		} else {
			LOG.debug("TRANSACTION TYPE is null");
			throw new Exception("Transaction type is null");
		}

	}

	private MessageTransaction processMobileWebAgentCustomerDepositRequest(MobileWebRequestMessage mobileRequest) throws Exception {
		LOG.debug("Now in processMobileWebAgentCustomerDepositRequest...");

		LOG.debug("START POPULATE MESSAGE_TRANSACTION");

		MessageTransaction messageTransaction = mobileProcessUtil.populateMessageTransaction(mobileRequest);
		messageTransaction.setNarrative("Agent Customer Deposit Request");
		messageTransaction.setTransactionType(TransactionType.AGENT_CUSTOMER_DEPOSIT);

		LOG.debug("END POPULATE MESSAGE_TRANSACTION");

		// check transfer type
		if (messageTransaction.getTransactionType() != null) {

			LOG.debug("Transaction Type : " + messageTransaction.getTransactionType().name());
			LOG.debug("START CREATE MESSAGE_TRANSACTION");

			messageTransaction = mobileProcessUtil.createMessageTransaction(messageTransaction);

			LOG.debug("END CREATE MESSAGE_TRANSACTION");

			return messageTransaction;

		} else {
			LOG.debug("TRANSACTION TYPE is null");
			throw new Exception("Transaction type is null");
		}

		
	}

	private MessageTransaction processUSSDAgentCustomerWithdrawalRequest(USSDRequestMessage ussdRequest) throws Exception {
		LOG.debug("Now in processUSSDAgentCustomerWithdrwalRequest...");

		LOG.debug("START POPULATE MESSAGE_TRANSACTION");

		MessageTransaction messageTransaction = ussdProcessUtil.populateMessageTransaction(ussdRequest);
		messageTransaction.setNarrative("Agent Customer Withdrawal Request");
		messageTransaction.setTransactionType(TransactionType.AGENT_CUSTOMER_WITHDRAWAL);

		LOG.debug("END POPULATE MESSAGE_TRANSACTION");

		// check transfer type
		if (messageTransaction.getTransactionType() != null) {

			LOG.debug("Transaction Type : " + messageTransaction.getTransactionType().name());
			LOG.debug("START CREATE MESSAGE_TRANSACTION");

			messageTransaction = ussdProcessUtil.createMessageTransaction(messageTransaction);

			LOG.debug("END CREATE MESSAGE_TRANSACTION");
			
			return messageTransaction;

		} else {
			LOG.debug("TRANSACTION TYPE is null");
			throw new Exception("Transaction type is null");
		}

	}

	private MessageTransaction processMobileWebAgentCustomerWithdrawalRequest(MobileWebRequestMessage webRequest) throws Exception {
		LOG.debug("Now in processMobileWebAgentCustomerWithdrawalRequest...");

		LOG.debug("START POPULATE MESSAGE_TRANSACTION");

		MessageTransaction messageTransaction = mobileProcessUtil.populateMessageTransaction(webRequest);
		messageTransaction.setNarrative("Agent Customer Withdrawal Request");
		messageTransaction.setTransactionType(TransactionType.AGENT_CUSTOMER_WITHDRAWAL);

		LOG.debug("END POPULATE MESSAGE_TRANSACTION");

		// check transfer type
		if (messageTransaction.getTransactionType() != null) {

			LOG.debug("Transaction Type : " + messageTransaction.getTransactionType().name());
			LOG.debug("START CREATE MESSAGE_TRANSACTION");

			messageTransaction = mobileProcessUtil.createMessageTransaction(messageTransaction);

			LOG.debug("END CREATE MESSAGE_TRANSACTION");

			return messageTransaction;
		} else {
			LOG.debug("TRANSACTION TYPE is null");
			throw new Exception("Transaction type is null");
		}

	
	}

	private MessageTransaction processUSSDAgentCustomerNonHolderWithRequest(USSDRequestMessage ussdRequest) throws Exception {
		LOG.debug("Now in processUSSDAgentCustomerNonHolderWthRequest...");

		LOG.debug("START POPULATE MESSAGE_TRANSACTION");

		MessageTransaction messageTransaction = ussdProcessUtil.populateMessageTransaction(ussdRequest);
		messageTransaction.setNarrative("Agent Customer Non Holder Withdrawal Request");
		messageTransaction.setTransactionType(TransactionType.AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL);

		LOG.debug("END POPULATE MESSAGE_TRANSACTION");

		// check transfer type
		if (messageTransaction.getTransactionType() != null) {

			LOG.debug("Transaction Type : " + messageTransaction.getTransactionType().name());
			LOG.debug("START CREATE MESSAGE_TRANSACTION");

			messageTransaction = ussdProcessUtil.createMessageTransaction(messageTransaction);

			LOG.debug("END CREATE MESSAGE_TRANSACTION");

			return messageTransaction;

		} else {
			LOG.debug("TRANSACTION TYPE is null");
			throw new Exception("Transaction type is null");
		}

	}

	private MessageTransaction processMobileWebAgentCustomerNonHolderWithRequest(MobileWebRequestMessage webRequest) throws Exception {
		LOG.debug("Now in processMobileAgentCustomerNonHolderWithRequest...");

		LOG.debug("START POPULATE MESSAGE_TRANSACTION");

		MessageTransaction messageTransaction = mobileProcessUtil.populateMessageTransaction(webRequest);
		messageTransaction.setNarrative("Agent Customer Non Holder Withdrawal Request");
		messageTransaction.setTransactionType(TransactionType.AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL);

		LOG.debug("END POPULATE MESSAGE_TRANSACTION");

		// check transfer type
		if (messageTransaction.getTransactionType() != null) {

			LOG.debug("Transaction Type : " + messageTransaction.getTransactionType().name());
			LOG.debug("START CREATE MESSAGE_TRANSACTION");

			messageTransaction = mobileProcessUtil.createMessageTransaction(messageTransaction);

			LOG.debug("END CREATE MESSAGE_TRANSACTION");

			return messageTransaction;

		} else {
			LOG.debug("TRANSACTION TYPE is null");
			throw new Exception("Transaction type is null");
		}
	}

	private MessageTransaction processUSSDAgentTransferRequest(USSDRequestMessage ussdRequest) throws Exception {
		LOG.debug("Now in processUSSDAgentTransferRequest...");

		LOG.debug("START POPULATE MESSAGE_TRANSACTION");

		MessageTransaction messageTransaction = ussdProcessUtil.populateMessageTransaction(ussdRequest);
		messageTransaction.setNarrative("Agent Transfer Request");
		messageTransaction.setTransactionType(TransactionType.AGENT_EWALLET_TO_BANKACCOUNT_TRANSFER);

		LOG.debug("END POPULATE MESSAGE_TRANSACTION");

		// check transfer type
		if (messageTransaction.getTransactionType() != null) {

			LOG.debug("Transaction Type : " + messageTransaction.getTransactionType().name());
			LOG.debug("START CREATE MESSAGE_TRANSACTION");

			messageTransaction = ussdProcessUtil.createMessageTransaction(messageTransaction);

			LOG.debug("END CREATE MESSAGE_TRANSACTION");
			
			return messageTransaction;

		} else {
			LOG.debug("TRANSACTION TYPE is null");
			throw new Exception("Transaction type is null");
		}

		
	}

	private MessageTransaction processMobileWebAgentTransferRequest(MobileWebRequestMessage webRequest) throws Exception {
		LOG.debug("Now in processMobileWebAgentTransferRequest...");

		LOG.debug("START POPULATE MESSAGE_TRANSACTION");

		MessageTransaction messageTransaction = mobileProcessUtil.populateMessageTransaction(webRequest);
		messageTransaction.setNarrative("Agent Transfer Request");
		messageTransaction.setTransactionType(TransactionType.AGENT_EWALLET_TO_BANKACCOUNT_TRANSFER);

		LOG.debug("END POPULATE MESSAGE_TRANSACTION");

		// check transfer type
		if (messageTransaction.getTransactionType() != null) {

			LOG.debug("Transaction Type : " + messageTransaction.getTransactionType().name());
			LOG.debug("START CREATE MESSAGE_TRANSACTION");

			messageTransaction = mobileProcessUtil.createMessageTransaction(messageTransaction);

			LOG.debug("END CREATE MESSAGE_TRANSACTION");

			return messageTransaction;
		} else {
			LOG.debug("TRANSACTION TYPE is null");
			throw new Exception("Transaction type is null");
		}

		
	}

	private MessageTransaction processUSSDAgentSummaryRequest(USSDRequestMessage ussdRequest) throws Exception {
		Date fullDate;
		LOG.debug("Now in processUSSDAgentSummaryRequest...");

		LOG.debug("START POPULATE MESSAGE_TRANSACTION");

		MessageTransaction messageTransaction = ussdProcessUtil.populateMessageTransaction(ussdRequest);
		messageTransaction.setNarrative("Agent Summary Request");
		messageTransaction.setTransactionType(TransactionType.AGENT_SUMMARY);
		fullDate = Formats.short2DigitYearPlainDateFormat.parse(ussdRequest.getDdmmyyDate());
		messageTransaction.setSummaryDate(fullDate);
		LOG.debug("Date parsed successfully.. proceed");
		LOG.debug("END POPULATE MESSAGE_TRANSACTION");

		// check transfer type
		if (messageTransaction.getTransactionType() != null) {

			LOG.debug("Transaction Type : " + messageTransaction.getTransactionType().name());
			LOG.debug("START CREATE MESSAGE_TRANSACTION");

			messageTransaction = ussdProcessUtil.createMessageTransaction(messageTransaction);

			LOG.debug("END CREATE MESSAGE_TRANSACTION");

			return messageTransaction;
		} else {
			LOG.debug("TRANSACTION TYPE is null");
			throw new Exception("Transaction type is null");
		}
	}

	private MessageTransaction processMobileWebAgentSummaryRequest(MobileWebRequestMessage webRequest) throws Exception {
		Date fullDate;
		LOG.debug("Now in processMobileWebAgentSummaryRequest...");

		LOG.debug("START POPULATE MESSAGE_TRANSACTION");

		MessageTransaction messageTransaction = mobileProcessUtil.populateMessageTransaction(webRequest);
		messageTransaction.setNarrative("Agent Summary Request");
		messageTransaction.setTransactionType(TransactionType.AGENT_SUMMARY);
		fullDate = Formats.short2DigitYearPlainDateFormat.parse(webRequest.getDdmmyyDate());
		messageTransaction.setSummaryDate(fullDate);
		LOG.debug("Date parsed successfully.. proceed");
		LOG.debug("END POPULATE MESSAGE_TRANSACTION");

		// check transfer type
		if (messageTransaction.getTransactionType() != null) {

			LOG.debug("Transaction Type : " + messageTransaction.getTransactionType().name());
			LOG.debug("START CREATE MESSAGE_TRANSACTION");

			messageTransaction = mobileProcessUtil.createMessageTransaction(messageTransaction);

			LOG.debug("END CREATE MESSAGE_TRANSACTION");

			return messageTransaction;
		} else {
			LOG.debug("TRANSACTION TYPE is null");
			throw new Exception("Transaction type is null");
		}
	}

	private MessageTransaction processUSSDMerchantRegRequest(USSDRequestMessage ussdRequest) throws Exception {
		LOG.debug("Now in processUSSDMerchantRegRequest...");

		LOG.debug("START POPULATE MESSAGE_TRANSACTION");

		MessageTransaction messageTransaction = ussdProcessUtil.populateMessageTransaction(ussdRequest);
		messageTransaction.setNarrative("Merchant Registration Request");
		messageTransaction.setTransactionType(TransactionType.MERCHANT_REG);
		LOG.debug("END POPULATE MESSAGE_TRANSACTION");

		// check transfer type
		if (messageTransaction.getTransactionType() != null) {

			LOG.debug("Transaction Type : " + messageTransaction.getTransactionType().name());
			LOG.debug("START CREATE MESSAGE_TRANSACTION");

			messageTransaction = ussdProcessUtil.createMessageTransaction(messageTransaction);

			LOG.debug("END CREATE MESSAGE_TRANSACTION");

			return messageTransaction;

		} else {
			LOG.debug("TRANSACTION TYPE is null");
			throw new Exception("Transaction type is null");
		}

		
	}

	private MessageTransaction processMobileWebMerchantRegRequest(MobileWebRequestMessage webRequest) throws Exception {
		LOG.debug("Now in processMobileWebMerchantRegRequest...");

		LOG.debug("START POPULATE MESSAGE_TRANSACTION");

		MessageTransaction messageTransaction = mobileProcessUtil.populateMessageTransaction(webRequest);
		messageTransaction.setNarrative("Merchant Registration Request");
		messageTransaction.setTransactionType(TransactionType.MERCHANT_REG);
		LOG.debug("END POPULATE MESSAGE_TRANSACTION");

		// check transfer type
		if (messageTransaction.getTransactionType() != null) {

			LOG.debug("Transaction Type : " + messageTransaction.getTransactionType().name());
			LOG.debug("START CREATE MESSAGE_TRANSACTION");

			messageTransaction = mobileProcessUtil.createMessageTransaction(messageTransaction);

			LOG.debug("END CREATE MESSAGE_TRANSACTION");

			return messageTransaction;
		} else {
			LOG.debug("TRANSACTION TYPE is null");
			throw new Exception("Transaction type is null");
		}

	}

	private MessageTransaction processUSSDRtgsRequest(USSDRequestMessage ussdRequest) throws Exception {
		LOG.debug("Now in processUSSDRtgsRequest...");

		LOG.debug("START POPULATE MESSAGE_TRANSACTION");

		MessageTransaction messageTransaction = ussdProcessUtil.populateMessageTransaction(ussdRequest);
		messageTransaction.setNarrative("RTGS Request");
		messageTransaction.setTransactionType(TransactionType.RTGS);
		LOG.debug("END POPULATE MESSAGE_TRANSACTION");

		// check transfer type
		if (messageTransaction.getTransactionType() != null) {

			LOG.debug("Transaction Type : " + messageTransaction.getTransactionType().name());
			LOG.debug("START CREATE MESSAGE_TRANSACTION");

			messageTransaction = ussdProcessUtil.createMessageTransaction(messageTransaction);

			LOG.debug("END CREATE MESSAGE_TRANSACTION");

			return messageTransaction;
		} else {
			LOG.debug("TRANSACTION TYPE is null");
			throw new Exception("Transaction type is null");
		}

	}

	private MessageTransaction processMobileWebRtgsRequest(MobileWebRequestMessage webRequest) throws Exception {
		LOG.debug("Now in processUSSDRtgsRequest...");

		LOG.debug("START POPULATE MESSAGE_TRANSACTION");

		MessageTransaction messageTransaction = mobileProcessUtil.populateMessageTransaction(webRequest);
		messageTransaction.setNarrative("RTGS Request");
		messageTransaction.setTransactionType(TransactionType.RTGS);
		LOG.debug("END POPULATE MESSAGE_TRANSACTION");

		// check transfer type
		if (messageTransaction.getTransactionType() != null) {

			LOG.debug("Transaction Type : " + messageTransaction.getTransactionType().name());
			LOG.debug("START CREATE MESSAGE_TRANSACTION");

			messageTransaction = mobileProcessUtil.createMessageTransaction(messageTransaction);

			LOG.debug("END CREATE MESSAGE_TRANSACTION");

			return messageTransaction;
		} else {
			LOG.debug("TRANSACTION TYPE is null");
			throw new Exception("Transaction type is null");
		}

		
	}

	private boolean isNonHolderValid(USSDRequestMessage ussdRequest) {
		boolean isValid = false;
		String response = "";
		try {
			// reject econet number
			MessageTransaction msgtxn = messageProcessor.findMessageTransactionByUUID(ussdRequest.getRefCode());
			if (msgtxn != null) {
				String targetMobile = msgtxn.getTargetMobileNumber();
				if (targetMobile != null) {
					if (MobileNetworkOperator.ECONET.equals(NumberUtil.getMNO(targetMobile))) {
						LOG.debug("ECONET target number trying nonholder withdrawal..REJECT");
						response = "Request Rejected. Econet numbers should use normal withdrawal process.";

					} else {
						LOG.debug("NON-ECONET target mobile.. PROCEED");
						isValid = true;
					}
				}
			} else {
				response = "Request Rejected. Invalid details supplied.";
			}
		} catch (Exception e) {
			LOG.debug(" Exception :: " + e.getMessage());
		}
		if (!isValid) {
			this.sendNotification(ussdRequest, response);
		}
		return isValid;
	}

	private boolean isNonHolderValid(MobileWebRequestMessage webRequest) {
		boolean isValid = false;
		String response = "";
		try {
			// reject econet number
			MessageTransaction msgtxn = messageProcessor.findMessageTransactionByUUID(webRequest.getRefCode());
			if (msgtxn != null) {
				String targetMobile = msgtxn.getTargetMobileNumber();
				if (targetMobile != null) {
					if (MobileNetworkOperator.ECONET.equals(NumberUtil.getMNO(targetMobile))) {
						LOG.debug("ECONET target number trying nonholder withdrawal..REJECT");
						response = "Request Rejected. Econet numbers should use normal withdrawal process.";

					} else {
						LOG.debug("NON-ECONET target mobile.. PROCEED");
						isValid = true;
					}
				}
			} else {
				response = "Request Rejected. Invalid details supplied.";
			}
		} catch (Exception e) {
			LOG.debug(" Exception :: " + e.getMessage());
		}
		if (!isValid) {
			this.sendNotification(webRequest, response);
		}
		return isValid;
	}

	private void sendNotification(MobileWebRequestMessage webRequest, String narrative) {
		try {
			narrative += " ZB e-Wallet";
			MessageSender.sendSMSToMobileDestination(jmsConnection, narrative, webRequest.getSourceMobileNumber(), smsReplyQueue, LOG);
		} catch (Exception e) {
			LOG.debug("Exception Thrown : " + e.getMessage());
		}
	}

	private void sendNotification(USSDRequestMessage ussdRequest, String narrative) {
		try {
			narrative += " ZB e-Wallet";
			MessageSender.sendSMSToMobileDestination(jmsConnection, narrative, ussdRequest.getSourceMobileNumber(), smsReplyQueue, LOG);
		} catch (Exception e) {
			LOG.debug("Exception Thrown : " + e.getMessage());
		}
	}

	private void resolveMobileProfilePasswdCounts(USSDRequestMessage ussdRequest) {
		try {
			CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
			MobileProfile mp = customerService.getMobileProfileByBankIdMobileNumberAndStatus(ussdRequest.getSourceBankId(), ussdRequest.getSourceMobileNumber(), MobileProfileStatus.ACTIVE);

			if (mp != null) {
				if (mp.getPasswordRetryCount() > 0) {
					LOG.debug(">>>>>>>>>Password Retry Counts For " + ussdRequest.getSourceMobileNumber() + " = " + mp.getPasswordRetryCount());
					mp.setPasswordRetryCount(0);
					mp.setStatus(MobileProfileStatus.ACTIVE);
					mp = customerService.updateMobileProfile(mp, EWalletConstants.SYSTEM);
					LOG.debug(">>>>>>>>>>>> Done upadating password retry count = " + mp.getPasswordRetryCount());
				}
			}
		} catch (Exception e) {
			LOG.debug("Exception Thrown : " + e.getMessage());
		}
	}

	private void resolveMobileProfilePasswdCounts(MobileWebRequestMessage webRequest) {
		try {
			CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
			MobileProfile mp = customerService.getMobileProfileByBankIdMobileNumberAndStatus(webRequest.getSourceBankId(), webRequest.getSourceMobileNumber(), MobileProfileStatus.ACTIVE);

			if (mp != null) {
				if (mp.getPasswordRetryCount() > 0) {
					LOG.debug(">>>>>>>>>Password Retry Counts For " + webRequest.getSourceMobileNumber() + " = " + mp.getPasswordRetryCount());
					mp.setPasswordRetryCount(0);
					mp.setStatus(MobileProfileStatus.ACTIVE);
					mp = customerService.updateMobileProfile(mp, EWalletConstants.SYSTEM);
					LOG.debug(">>>>>>>>>>>> Done upadating password retry count = " + mp.getPasswordRetryCount());
				}
			}
		} catch (Exception e) {
			LOG.debug("Exception Thrown : " + e.getMessage());
		}
	}

}
