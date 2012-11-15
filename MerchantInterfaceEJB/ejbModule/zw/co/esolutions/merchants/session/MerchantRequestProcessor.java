package zw.co.esolutions.merchants.session;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.ibm.icu.util.Calendar;

import zw.co.esolutions.mcommerce.refgen.service.ReferenceGeneratorServiceSOAPProxy;
import zw.co.esolutions.mcommerce.xml.ISOMarshaller;
import zw.co.esolutions.mcommerce.xml.ISOMarshallerException;
import zw.co.esolutions.mcommerce.xml.ISOMsg;
import zw.co.esolutions.mcommerce.xml.Messages;
import zw.co.esolutions.mcommerce.xml.MetaData;
import zw.co.esolutions.merchants.beans.HandleMerchantRequest;
import zw.co.esolutions.merchants.beans.TransactionState;
import zw.co.esolutions.merchants.util.InterfaceConstants;
import zw.co.esolutions.merchants.util.MerchantException;
import zw.co.esolutions.merchants.util.PaymentRequest;
import zw.co.esolutions.merchants.util.PaymentResponse;

/**
 * Session Bean implementation class MerchantRequestProcessor
 */
@Stateless
@LocalBean
public class MerchantRequestProcessor {

	@PersistenceContext(unitName = "MPG")
	private EntityManager em;

	@Resource(mappedName = "jms/MPGQCF")
	private javax.jms.QueueConnectionFactory connectionFactory;

	@EJB
	private MerchantPaymentProcessor paymentProcessor;

	private Connection jmsConnection;

	/**
	 * Default constructor.
	 */
	public MerchantRequestProcessor() {

	}

	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/merchantsGateway.log.properties");
			LOG = Logger.getLogger(HandleMerchantRequest.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + HandleMerchantRequest.class);
			e.printStackTrace(System.err);
		}
	}

	@PostConstruct
	public void initialize() {
		try {
			jmsConnection = connectionFactory.createConnection();
		} catch (JMSException e) {
			e.printStackTrace(System.err);
		}
	}

	@PreDestroy
	public void cleanup() {
		try {
			jmsConnection.close();
		} catch (JMSException e) {
			e.printStackTrace(System.err);
		}
	}

//	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean submitMessageToQueue(ISOMsg isoMsg, MetaData metaData){
		String destinationQueueName = metaData.getReplyQueue();
		String replyMessage;
		try {
			replyMessage = ISOMarshaller.marshal(isoMsg, metaData);
		} catch (ISOMarshallerException e1) {
			LOG.fatal("FAILED TO MSG TO QUEUE : [" + destinationQueueName +" ]\n ", e1);
			e1.printStackTrace(System.err);
			return false;
		}
		Session jmsSession = null;
		MessageProducer jmsProducer = null;
		try {
			jmsSession = jmsConnection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			Queue queue = jmsSession.createQueue(destinationQueueName);
			jmsProducer = jmsSession.createProducer(queue);
			TextMessage jmsTextMessage = jmsSession.createTextMessage(replyMessage);
			jmsProducer.send(jmsTextMessage);
			LOG.info("MSG SENT TO QUEUE : \n " + replyMessage );
			return true;
		} catch (JMSException e) {
			LOG.fatal("FAILED TO MSG TO QUEUE : [" + destinationQueueName +" ]\n " + replyMessage , e);
			e.printStackTrace(System.err);
			return false;
		}finally{
			try {
				if(jmsProducer != null){
					jmsProducer.close();
				}
			} catch (Exception e) {
				LOG.warn("Failed to close JSM Producer");
			}
			try {
				if(jmsConnection != null){
					jmsSession.close();
				}
			} catch (Exception e) {
				LOG.warn("Failed to close JSM Connection");
			}
			
		}
		
	} 
	
//	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Messages handleMerchantRequest(String xmlMessage) throws MerchantException {
		Messages messages = null;
		try {
			LOG.info("RECEIVED REQUEST : \n " + xmlMessage);
			messages = ISOMarshaller.unmarshal(xmlMessage);
			LOG.debug("Done unmarshalling MSG : ");

			ISOMsg isoMsg = messages.getIsoMsg();
			// set the necessary fields before persisting
			String mti = isoMsg.getMti();
			
			isoMsg.setTxnRef(mti + isoMsg.getFieldValue(InterfaceConstants.ISO_FIELD_PROCESSING_CODE) + isoMsg.getRetrievalReference());
			isoMsg.setISOField(InterfaceConstants.ISO_FIELD_RETRIEVAL_REF, isoMsg.getTxnRef());
			if (InterfaceConstants.MTI_TXN_RQST.equalsIgnoreCase(mti)) {
				LOG.debug("[NEW TXN] : " + mti);
				messages = this.process0200Request(messages);
			} else if (InterfaceConstants.MTI_REVERSAL_RQST.equalsIgnoreCase(mti)) {
				LOG.debug("[REVERSAL] : " + mti);
				messages = this.process0420Request(messages);
			}
			return messages;
			
			
		} catch (ISOMarshallerException e) {
			LOG.error("Marshaling Exception : ");
			e.printStackTrace(System.err);
//			throw new MerchantException(e);
			return null;
		} catch (Exception e) {
			LOG.error("Other Exception thrown : ");
			e.printStackTrace(System.err);
//			throw new MerchantException(e);
			if(messages != null){
				ISOMsg isoMsg = messages.getIsoMsg();
				isoMsg.setISOField(InterfaceConstants.ISO_FIELD_RESPONSE_CODE, "05");
				isoMsg.setISOField(InterfaceConstants.ISO_FIELD_ADDITIONAL_DATA_ISO, "Merchant host system is not available");
				return messages;
			}
			return null;
		}
		
	}

	private Messages process0420Request(Messages messages) throws Exception {
		ISOMsg isoMsg = messages.getIsoMsg();

		String pcode = isoMsg.getFieldValue(InterfaceConstants.ISO_FIELD_PROCESSING_CODE);
		if (InterfaceConstants.PCODEU5.equalsIgnoreCase(pcode)) {
			LOG.debug("Processing reversal request");
			String creditTxnRef = InterfaceConstants.MTI_TXN_RQST + InterfaceConstants.PCODEU5 + isoMsg.getRetrievalReference();
			LOG.debug("ORIG REF : "+creditTxnRef);
			ISOMsg originalTxn = em.find(ISOMsg.class, creditTxnRef);
			if (originalTxn != null) {
				LOG.debug("Original Transaction found");
				em.persist(isoMsg);
				if (InterfaceConstants.STATUS_SUCCESSFUL.equalsIgnoreCase(originalTxn.getStatus())) {
					LOG.debug("Original txn was successful : Proceed to do reversal");
					isoMsg = this.handleReversalRequest(isoMsg);
				} else {
					LOG.debug("Original txn failed : REVERSAL can not be done");
					isoMsg = this.promoteTxnState(originalTxn, InterfaceConstants.STATUS_REVERSAL_FAILED, "Original transaction failed");
					isoMsg.setISOField(InterfaceConstants.ISO_FIELD_ADDITIONAL_DATA_ISO, "Original transaction failed");
					isoMsg.setISOField(InterfaceConstants.ISO_FIELD_RESPONSE_CODE, "91");
					isoMsg = em.merge(isoMsg);
				}
			} else {
				LOG.debug("Original Transaction could not be found");
				isoMsg.setISOField(InterfaceConstants.ISO_FIELD_ADDITIONAL_DATA_ISO, "No matching posting");
				isoMsg.setISOField(InterfaceConstants.ISO_FIELD_RESPONSE_CODE, "91");
			}
		} else {
			LOG.warn("We only do reversals for " + InterfaceConstants.PCODEU5 + " TXNS ");
		}
		return messages;
	}

	private Messages process0200Request(Messages messages) throws Exception {
		ISOMsg isoMsg = messages.getIsoMsg();
		isoMsg.setMetaData(messages.getMetadata());

		String pcode = isoMsg.getFieldValue(InterfaceConstants.ISO_FIELD_PROCESSING_CODE);

		ISOMsg existing = em.find(ISOMsg.class, isoMsg.getTxnRef());

		if (existing != null) {
			LOG.warn("Duplicate Request");
			isoMsg.setMti(InterfaceConstants.MTI_TXN_RESP);
			isoMsg.setISOField(InterfaceConstants.ISO_FIELD_RESPONSE_CODE, "05");
			isoMsg.setISOField(InterfaceConstants.ISO_FIELD_ADDITIONAL_DATA_ISO, "Duplicate request");
			return messages;
		} else {
			LOG.debug("Persist new reques into MPG : ");
			em.persist(isoMsg);
			if (InterfaceConstants.PCODE30.equalsIgnoreCase(pcode)) {
				LOG.debug("The new requestion is an ACCOUNT VALIDATION RQST");
				isoMsg = this.handleValidationRequest(isoMsg);
				LOG.debug("Done processing validation request");
			} else if (InterfaceConstants.PCODEU5.equalsIgnoreCase(pcode)) {
				LOG.debug("The new requestion is a CREDIT RQST");
				isoMsg = this.handleCreditRequest(isoMsg);

			} else {
				LOG.warn("Unknown request type : " + pcode);
			}
			messages.setIsoMsg(isoMsg);
			return messages;
		}

	}

	private ISOMsg handleCreditRequest(ISOMsg isoMsg) throws Exception {
		isoMsg = this.promoteTxnState(isoMsg, InterfaceConstants.STATUS_CREDIT_REQUEST, "Credit Request");
		LOG.debug("Done promoting txn : " + isoMsg.getMti() + " | " + isoMsg.getRetrievalReference() + " | " + InterfaceConstants.STATUS_CREDIT_REQUEST);
		PaymentRequest paymentRequest = this.populatePaymentRequest(isoMsg, InterfaceConstants.TXN_TYPE_CREDIT);
		PaymentResponse paymentResponse = paymentProcessor.postCreditTransaction(paymentRequest);
		LOG.debug("CREDIT RESPONSE : " + paymentResponse);

		isoMsg.setISOField(InterfaceConstants.ISO_FIELD_RESPONSE_CODE, paymentResponse.getResponseCode());
		isoMsg.setISOField(InterfaceConstants.ISO_FIELD_ADDITIONAL_DATA_ISO, paymentResponse.getNarrative());
		//setting merchant ref
		isoMsg.setISOField(InterfaceConstants.ISO_FIELD_RETRIEVAL_REF, paymentRequest.getMerchantRef());

		if (InterfaceConstants.RC_OK.equalsIgnoreCase(paymentResponse.getResponseCode())) {
			isoMsg = this.promoteTxnState(isoMsg, InterfaceConstants.STATUS_SUCCESSFUL, paymentResponse.getNarrative());
		} else {
			isoMsg = this.promoteTxnState(isoMsg, InterfaceConstants.STATUS_FAILED, paymentResponse.getNarrative());
		}
		isoMsg.setTxnType(InterfaceConstants.TXN_TYPE_CREDIT);
		em.merge(isoMsg);
		isoMsg.setMti(InterfaceConstants.MTI_TXN_RESP);
		return isoMsg;
	}

	private ISOMsg handleValidationRequest(ISOMsg isoMsg) throws Exception {
		isoMsg = this.promoteTxnState(isoMsg, InterfaceConstants.STATUS_VALIDATION_REQUEST, "Account Validation Request");
		LOG.debug("Done promoting txn : " + isoMsg.getMti() + " | " + isoMsg.getRetrievalReference() + " | " + InterfaceConstants.STATUS_VALIDATION_REQUEST);
		PaymentRequest paymentRequest = this.populatePaymentRequest(isoMsg, InterfaceConstants.TXN_TYPE_ACCOUNT_VALIDATION);
		PaymentResponse paymentResponse = paymentProcessor.postAccountValidation(paymentRequest);
		LOG.debug("VALIDATION RESPONSE : " + paymentResponse);

		isoMsg.setISOField(InterfaceConstants.ISO_FIELD_RESPONSE_CODE, paymentResponse.getResponseCode());
		isoMsg.setISOField(InterfaceConstants.ISO_FIELD_ADDITIONAL_DATA_ISO, paymentResponse.getNarrative());

		if (InterfaceConstants.RC_OK.equalsIgnoreCase(paymentResponse.getResponseCode())) {
			isoMsg = this.promoteTxnState(isoMsg, InterfaceConstants.STATUS_SUCCESSFUL, paymentResponse.getNarrative());
		} else {
			isoMsg = this.promoteTxnState(isoMsg, InterfaceConstants.STATUS_FAILED, paymentResponse.getNarrative());
		}
		isoMsg.setTxnType(InterfaceConstants.TXN_TYPE_ACCOUNT_VALIDATION);
		em.merge(isoMsg);
		isoMsg.setMti(InterfaceConstants.MTI_TXN_RESP);
		return isoMsg;
	}

	private ISOMsg handleReversalRequest(ISOMsg isoMsg) throws Exception {
		isoMsg = this.promoteTxnState(isoMsg, InterfaceConstants.STATUS_REVERSAL_REQUEST, "Reversal Request");
		LOG.debug("Done promoting txn : " + isoMsg.getMti() + " | " + isoMsg.getRetrievalReference() + " | " + InterfaceConstants.STATUS_REVERSAL_REQUEST);
		PaymentRequest paymentRequest = this.populatePaymentRequest(isoMsg, InterfaceConstants.TXN_TYPE_REVERSAL);
		PaymentResponse paymentResponse = paymentProcessor.postReversalTransaction(paymentRequest);
		LOG.debug("REVERSAL RESPONSE : " + paymentResponse);

		isoMsg.setISOField(InterfaceConstants.ISO_FIELD_RESPONSE_CODE, paymentResponse.getResponseCode());
		isoMsg.setISOField(InterfaceConstants.ISO_FIELD_ADDITIONAL_DATA_ISO, paymentResponse.getNarrative());

		if (InterfaceConstants.RC_OK.equalsIgnoreCase(paymentResponse.getResponseCode())) {
			isoMsg = this.promoteTxnState(isoMsg, InterfaceConstants.STATUS_REVERSAL_SUCCESSFUL, paymentResponse.getNarrative());
		} else {
			isoMsg = this.promoteTxnState(isoMsg, InterfaceConstants.STATUS_REVERSAL_FAILED, paymentResponse.getNarrative());
		}
		isoMsg.setTxnType(InterfaceConstants.TXN_TYPE_REVERSAL);
		em.merge(isoMsg);
		isoMsg.setMti(InterfaceConstants.MTI_REVERSAL_RESP);
		return isoMsg;
	}

	private PaymentRequest populatePaymentRequest(ISOMsg isoMsg, String txnType) {
		
		ReferenceGeneratorServiceSOAPProxy refService = new ReferenceGeneratorServiceSOAPProxy();
		
		PaymentRequest paymentRequest = new PaymentRequest();
		paymentRequest.setAccountNumber(isoMsg.getFieldValue(InterfaceConstants.ISO_FIELD_ACCOUNT_IDENTIFICATION_1));
		paymentRequest.setCustomerName(isoMsg.getFieldValue(InterfaceConstants.ISO_FIELD_TRANSACTION_DESCRIPTION));
		try {
			paymentRequest.setPaymentBank(Integer.parseInt(isoMsg.getFieldValue(InterfaceConstants.ISO_FIELD_CARD_ACCEPTOR_TERMINAL_ID)));
		} catch (NumberFormatException e) {
			LOG.warn("Failed to parse PAYMENT BANK : using default card services");
			paymentRequest.setPaymentBank(23);
		}
		try {
			 Date now = new Date();
		     SimpleDateFormat simpleDateformat = new SimpleDateFormat("yy");
		     String date = simpleDateformat.format(now);

			paymentRequest.setPaymentBranch(Integer.parseInt(isoMsg.getFieldValue(InterfaceConstants.ISO_FIELD_FORWARDING_INSTITUTION_ID)));
			String mRef = refService.generateUUID("ZESA", "E", date, 0L, 1000000 -1L);
//			String mRef = "E120234";
			paymentRequest.setMerchantRef(mRef.substring(1, mRef.length()));
		} catch (NumberFormatException e) {
			LOG.warn("Failed to parse payment branch : using default card services");
			paymentRequest.setPaymentBranch(4131);
		} catch (Exception e){
			LOG.warn("Failed to set reference");
		}
		String ref = isoMsg.getRetrievalReference();
		paymentRequest.setPaymentId(ref.substring(1, ref.length()));
		double amount = Long.parseLong(isoMsg.getFieldValue(InterfaceConstants.ISO_FIELD_TRANSACTION_AMOUNT));
		paymentRequest.setAmount(amount/100.0);
		return paymentRequest;
	}

	private ISOMsg promoteTxnState(ISOMsg isoMsg, String status, String narrative) throws Exception {
		ISOMsg existing = em.find(ISOMsg.class, isoMsg.getTxnRef());

		TransactionState txnState = new TransactionState(status, new Date(System.currentTimeMillis()), narrative);
		txnState.setIsoMsg(existing);
		existing.setNarrative(narrative);
		existing.setStatus(status);
		existing = em.merge(existing);

		txnState.setIsoMsg(existing);
		em.persist(txnState);
		return isoMsg;
	}
	
//	public static void main(String[] args) {
//
//        Date now = new Date();
//        SimpleDateFormat simpleDateformat = new SimpleDateFormat("yy");
//        System.out.println(simpleDateformat.format(now));
//
//    }
}