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

import zw.co.esolutions.config.bank.BankInfo;
import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.msg.MerchantResponse;
import zw.co.esolutions.ewallet.msg.RequestInfo;
import zw.co.esolutions.ewallet.sms.MessageSender;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.SystemConstants;
import zw.co.esolutions.mcommerce.centralswitch.model.MessageTransaction;
import zw.co.esolutions.mcommerce.centralswitch.processes.ProcessSMSRequest;
import zw.co.esolutions.mcommerce.xml.ISOMarshaller;
import zw.co.esolutions.mcommerce.xml.ISOMarshallerException;
import zw.co.esolutions.mcommerce.xml.ISOMsg;
import zw.co.esolutions.mcommerce.xml.Messages;

/**
 * Message-Driven Bean implementation class for: HandleMerchantResponse
 * 
 */
@MessageDriven(activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") })
public class HandleMerchantResponse implements MessageListener {

	@EJB
	private ProcessSMSRequest messageProcessor;

	@Resource(mappedName = "jms/EWalletQCF")
	private QueueConnectionFactory jmsQueueConnectionFactory;

	@Resource(mappedName = EWalletConstants.ECONET_SMS_OUT_QUEUE)
	private Queue deadLetterQueue;

	private Connection jmsConnection;

	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/switch.log.properties");
			LOG = Logger.getLogger(HandleMerchantResponse.class);
		} catch (Exception e) {
		}
	}

	/**
	 * Default constructor.
	 */
	public HandleMerchantResponse() {
		// TODO Auto-generated constructor stub
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
		if (message instanceof TextMessage) {
			TextMessage jmsText = (TextMessage) message;
			try {
				String replyText = jmsText.getText();
				this.handleMerchantResponse(replyText);
			} catch (JMSException e) {
				LOG.error("FAILED to extract text from JMS Text Message : ", e);
			}
		} else {
			LOG.error("Unexpected JMS Message TYPE : " + message.getClass().getCanonicalName());
		}
	}

	private void handleMerchantResponse(String replyISO) {
		LOG.info("RECEIVED MERCHANT RESPONSE : \n" + replyISO);
		try {
			Messages messages = ISOMarshaller.unmarshal(replyISO);
			ISOMsg isoMsg = messages.getIsoMsg();
			MessageTransaction txn = messageProcessor.findMessageTransactionByUUID(isoMsg.getRetrievalReference());
			if (txn == null) {
				LOG.warn("Could not find matching txn for merchant response : " + isoMsg.getRetrievalReference());
				return ;
			}
			String pcode = isoMsg.getFieldValue(SystemConstants.ISO_FIELD_PROCESSING_CODE);
			TransactionStatus currentTxnState = txn.getStatus();
			if (TransactionStatus.ACCOUNT_VALIDATION_RQST.equals(currentTxnState)) {
				LOG.debug("TXN is expecting a VALIDATION RESPONSE : check this is the right response");
				if (SystemConstants.PCODE30.equalsIgnoreCase(pcode)) {
					this.processValidationResponse(txn, isoMsg);
				} else {
					LOG.fatal("TXN is expecting a validation response but got a [" + pcode + "] : DISCARD THIS RESPONSE");
				}
			} else if (TransactionStatus.CREDIT_REQUEST.equals(currentTxnState)) {
				LOG.debug("TXN is expecting a CREDIT RESPONSE : check if this response is right");
				if (SystemConstants.PCODEU5.equalsIgnoreCase(pcode)) {
					String merchantRef = isoMsg.getFieldValue(SystemConstants.ISO_FIELD_RETRIEVAL_REF);
					txn.setMerchantRef(merchantRef);
					this.processCreditResponseResponse(txn, isoMsg);
					
				} else {
					LOG.fatal("TXN is expecting a validation response but got a [" + pcode + "] : DISCARD THIS RESPONSE");
				
				}
			} else {
				LOG.warn("GOT an UNEXPECTED merchant response for TXN : " + txn.getUuid() + " | " + currentTxnState + " : DISCARD");
			}
		} catch (ISOMarshallerException e) {
			LOG.fatal("FAILED to parse Merchant Response:", e);
		} catch (Exception e) {
			LOG.fatal("FAILED to process Merchant Response:", e);
		}
		
	}

	private void processCreditResponseResponse(MessageTransaction txn, ISOMsg isoMsg) throws Exception {

		String responseCode = isoMsg.getFieldValue(SystemConstants.ISO_FIELD_RESPONSE_CODE);
		String narrative = isoMsg.getFieldValue(SystemConstants.ISO_FIELD_ADDITIONAL_DATA_ISO);
		
		MerchantResponse merchantResponse = messageProcessor.populateMerchantResponse(txn);
		merchantResponse.setMerchantResponseCode(responseCode);
		merchantResponse.setNarrative(narrative);
		if (SystemConstants.RESPONSE_CODE_OK_SHORT.equalsIgnoreCase(responseCode)) {

			merchantResponse.setResponseCode(ResponseCode.E000);
		} else {
			merchantResponse.setResponseCode(ResponseCode.E903);
		}
		BankInfo bankInfo = ISOMarshaller.unmarshalBankInfo("/opt/eSolutions/conf/bank.zb.config.xml");

		if (bankInfo == null) {
			LOG.fatal("Bank Config for " + txn.getUuid() + " could not be loaded");
			return;
		}
		LOG.debug("DONE Loading bank info");

		boolean sent = MessageSender.sendObjectToDestination(jmsConnection, merchantResponse, "", bankInfo.getRequestQueueName(), LOG);
		if (sent) {
			txn = messageProcessor.promoteTransactionStatus(txn, TransactionStatus.CREDIT_RESPONSE, narrative);
		} else {
			txn = messageProcessor.promoteTransactionStatus(txn, TransactionStatus.MANUAL_RESOLVE, narrative);
		}
		
		txn.setResponseCode(responseCode);
		txn.setNarrative(narrative);
		txn = messageProcessor.updateTransaction(txn);

		return;

	}

	private MerchantResponse processValidationResponse(MessageTransaction txn, ISOMsg isoMsg) throws Exception {
		String responseCode = isoMsg.getFieldValue(SystemConstants.ISO_FIELD_RESPONSE_CODE);
		String narrative = isoMsg.getFieldValue(SystemConstants.ISO_FIELD_ADDITIONAL_DATA_ISO);

		if (SystemConstants.RESPONSE_CODE_OK_SHORT.equalsIgnoreCase(responseCode)) {
			RequestInfo requestInfo = messageProcessor.populateRequestInfo(txn);
			
			BankInfo bankInfo = ISOMarshaller.unmarshalBankInfo("/opt/eSolutions/conf/bank.zb.config.xml");

			if (bankInfo == null) {
				LOG.fatal("Bank Config for " + txn.getUuid() + " could not be loaded");
				return null;
			}
			LOG.debug("DONE Loading bank info");

			boolean sent = MessageSender.sendObjectToDestination(jmsConnection, requestInfo, "", bankInfo.getRequestQueueName(), LOG);
			if (sent) {
				txn = messageProcessor.promoteTransactionStatus(txn, TransactionStatus.BANK_REQUEST, narrative);
			} else {
				txn = messageProcessor.promoteTransactionStatus(txn, TransactionStatus.MANUAL_RESOLVE, narrative);
			}
			txn.setResponseCode(responseCode);
			txn.setNarrative(narrative);
			
			messageProcessor.updateTransaction(txn);
		} else {
			String textMessage = "Your " + txn.getUtilityName() + " bill pay failed. Reason : " + narrative + ". ZB e-Wallet";
			MessageSender.sendSMSToMobileDestination(jmsConnection, textMessage, txn.getSourceMobileNumber(), deadLetterQueue, LOG);
			txn = messageProcessor.promoteTransactionStatus(txn, TransactionStatus.FAILED, narrative);
			txn.setResponseCode(responseCode);
			txn.setNarrative(narrative);
		}
		return null;
	}

}
