package zw.co.esolutions.bankif.mdbs;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.bankif.session.ProcessBankRequest;
import zw.co.esolutions.bankif.session.ProcessBankResponse;
import zw.co.esolutions.bankif.util.Response;
import zw.co.esolutions.ewallet.msg.BankRequest;
import zw.co.esolutions.ewallet.msg.BankResponse;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.EWalletConstants;

/**
 * Message-Driven Bean implementation class for: HandleBankRequest
 * 
 */
@MessageDriven(activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") }, mappedName = EWalletConstants.FROM_EWALLET_TO_BANKMED_QUEUE)
public class HandleBankRequest implements MessageListener {
	
	@Resource(mappedName = EWalletConstants.FROM_BANKMED_TO_BANK_SYS_QUEUE)
	private Queue bankRequestsQueue;
	
	@Resource(mappedName = EWalletConstants.FROM_BANKMED_TO_EWALLET_QUEUE)
	private Queue bankResponsesQueue;
	
	@Resource(mappedName = EWalletConstants.EWALLET_QCF)
	private QueueConnectionFactory jmsQueueConnectionFactory;
	
	private Connection jmsConnection;
	
	@EJB
	private ProcessBankRequest processBankRequest;

	@EJB
	private ProcessBankResponse processBankResponse;

	public HandleBankRequest() {
	}

	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/hostInterface.log.properties");
			LOG = Logger.getLogger(HandleBankRequest.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + HandleBankRequest.class);
		}
	}

	@PostConstruct
	public void initialise(){
		try {
			jmsConnection = jmsQueueConnectionFactory.createConnection();
		} catch (JMSException e) {
			LOG.fatal("Failed to initialise MDB " + this.getClass().getSimpleName(), e);
			e.printStackTrace(System.err);
		}
	}

	@PreDestroy
	public void cleanUp(){
		if(jmsConnection != null){
			try {
				jmsConnection.close();
			} catch (JMSException e) {
				//Ignore this guy
			}
		}
	} 

	
	/**
	 * @see MessageListener#onMessage(Message)
	 */
	public void onMessage(Message message) {
		if (message instanceof ObjectMessage) {
			ObjectMessage objMsg = (ObjectMessage) message;
			try {
				BankRequest bankRequest = null;
				try {
					bankRequest = (BankRequest) objMsg.getObject();
					LOG.debug("Done casting to bank request.." + bankRequest);
				} catch (Exception e) {
					LOG.error("Failed to cast object message to bank request..");
					return;
				}
				LOG.debug("Got a bank request with commissions : " + bankRequest.getCommissions());

				if(bankRequest.getSourceAccountNumber() == null){
					LOG.debug("Got a null source of destination account SRC : " + bankRequest.getSourceAccountNumber() +" DEST : " + bankRequest.getTargetAccountNumber());
					BankResponse bankResponse = new BankResponse();
					bankResponse.setBankRequest(bankRequest);
					bankResponse.setResponseCode(ResponseCode.E201);
					bankResponse.setNarrative(ResponseCode.E201.getDescription());
					this.sendBankReplyMessage(bankResponse);
					LOG.info("FAILURE RETURNS >> No src acc " + bankRequest.getReference());
					return;
				}
				
				if (bankRequest.getSourceAccountNumber().equalsIgnoreCase(bankRequest.getTargetAccountNumber()) && !bankRequest.isReversal()) {
					this.doEwalletAccountsPosting(bankRequest);
				} else {
					this.postTransactionToEQ(bankRequest);					
				}
			} catch (Exception e) {				
				LOG.error(e);
			}
		}
	}

	private void doEwalletAccountsPosting(BankRequest bankRequest) throws Exception {				
		LOG.debug("Do not post to EQ3 " + bankRequest.getTransactionType());
		BankResponse response = processBankRequest.processEWalletToEWalletBankRequest(bankRequest);
		LOG.debug("Done doing fake post, now to process charges ");
		List<Response> responses = processBankResponse.processTransactionCharge(bankRequest.getReference());
		if (responses == null || responses.isEmpty()) {
			LOG.debug("No charges ");
		} else {
			LOG.debug("Charges Count " + responses.size());
			for (Response chargeResponse : responses) {
				this.postChargeItem(chargeResponse);
			}
			LOG.debug("Done posting charges " + responses.size());
		}
		this.sendBankReplyMessage(response);
		LOG.debug("Done sending response to eWallet System ");
		LOG.info(response);
	}
	
	private void postTransactionToEQ(BankRequest bankRequest) throws Exception {				
		Response response = processBankRequest.processBankRequest(bankRequest);
		if(response == null){
			return;
		}
		if (ResponseCode.E000.equals(response.getResponseCode())) {
			this.sendRequestToBank(response.getMessage());
			LOG.info("REQUEST SENT  >> \n\t" + response.getMessage());
		} else if (ResponseCode.E838.equals(response.getResponseCode())) {
			this.sendRequestToBank(response.getMessage());
			LOG.info("REVERSAL SENT >> \n\t" + response.getMessage());
		} else {
			LOG.fatal("Failed to create bank message... " + response.getResponseCode() + " : " + response.getMessage());
		}		
	}

	private void postChargeItem(Response chargeResponse) {
		try {
			if (ResponseCode.E000.equals(chargeResponse.getResponseCode())) {
				this.sendRequestToBank(chargeResponse.getMessage());
				LOG.info("CHARGES SENT >> \n\t" + chargeResponse.getMessage());
			} else {
				LOG.fatal("Charge message could not be created  : >> " + chargeResponse.getResponseCode() + " " + chargeResponse.getMessage());
			}
		} catch (Exception e) {
			LOG.fatal("Failed to post Charge message to EQ3  : >> " + chargeResponse.getResponseCode() + " " + chargeResponse.getMessage());
			LOG.debug(e);
		}
	}
	
	private void sendBankReplyMessage(BankResponse bankResponse) throws Exception{
		try {
			Session jmsSession = jmsConnection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			MessageProducer jmsProducer = jmsSession.createProducer(bankResponsesQueue);
			ObjectMessage jmsObjectMessage = jmsSession.createObjectMessage(bankResponse);
			jmsProducer.send(jmsObjectMessage);
		} catch (JMSException e) {
			LOG.fatal("FAILED TO SUBMIT REPLY TO EWT " + bankResponse.getBankRequest().getReference(), e);
			e.printStackTrace(System.err);
			throw new Exception(e);
		}
	}
	
	private void sendRequestToBank(String text) throws Exception{
		try {
			Session jmsSession = jmsConnection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			MessageProducer jmsProducer = jmsSession.createProducer(bankRequestsQueue);
			TextMessage jmsText = jmsSession.createTextMessage(text);
			jmsProducer.send(jmsText);
		} catch (JMSException e) {
			LOG.error("Failed to submit message to bank", e);
			e.printStackTrace(System.err);
			throw new Exception(e);
		}
	}
}
