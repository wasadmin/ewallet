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

import zw.co.esolutions.bankif.model.CommissionMessage;
import zw.co.esolutions.bankif.session.ProcessBankRequest;
import zw.co.esolutions.bankif.session.ProcessBankResponse;
import zw.co.esolutions.bankif.util.BankResponseHandlerResponse;
import zw.co.esolutions.bankif.util.InterfaceConstants;
import zw.co.esolutions.bankif.util.Response;
import zw.co.esolutions.bankif.zb.ZBBankMessageFactory;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.msg.BankResponse;
import zw.co.esolutions.ewallet.sms.MessageSender;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.EWalletConstants;

/**
 * Message-Driven Bean implementation class for: HandleBankResponse
 * 
 */
@MessageDriven(activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") }, mappedName = EWalletConstants.ACS_REPLY_QUEUE)
public class HandleBankResponse implements MessageListener {
	
	@Resource(mappedName = EWalletConstants.FROM_BANKMED_TO_EWALLET_QUEUE)
	private Queue bankResponsesQueue;
	
	@Resource(mappedName = EWalletConstants.FROM_BANKMED_TO_BANK_SYS_QUEUE)
	private Queue bankRequestsQueue;
	
	
	@Resource(mappedName = EWalletConstants.EWALLET_QCF)
	private QueueConnectionFactory jmsQueueConnectionFactory;
	
	private Connection jmsConnection;
	
	@EJB
	ProcessBankResponse processBankResponse;
	@EJB
	ProcessBankRequest processBankRequest;

	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/hostInterface.log.properties");
			LOG = Logger.getLogger(HandleBankResponse.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + HandleBankResponse.class);
			e.printStackTrace(System.err);
		}
	}

	/**
	 * Default constructor.
	 */
	public HandleBankResponse() {

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
	
	/**
	 * @see MessageListener#onMessage(Message)
	 */
	public void onMessage(Message message) {
		LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>.. HandleBankResponse Consumed ............ ");
		boolean isReversal = false;
		if (message instanceof TextMessage) {
			TextMessage txtMsg = (TextMessage) message;
			String receivedText;
			try {
				receivedText = txtMsg.getText();
				LOG.info("RESPONSE RECEIVED >> \n\t " + receivedText);
				if (receivedText != null) {
					
					BankResponse bankResponse = ZBBankMessageFactory.getInstance().parseBankResponse(receivedText);
					if (InterfaceConstants.MSG_TYPE_0210.equalsIgnoreCase(bankResponse.getMessageType())) {
						LOG.debug("0210 : Got a TXN RESPONSE");
						this.handle0210Response(bankResponse);
						
					} else if (InterfaceConstants.MSG_TYPE_0430.equalsIgnoreCase(bankResponse.getMessageType())) {
						LOG.debug("0430 : Got a REVERSAL RESPONSE");
						bankResponse = processBankResponse.processReversalResponse(bankResponse);
						LOG.debug("Done processing the reversal response : " + bankResponse);
						isReversal = true;
					} else if (InterfaceConstants.MSG_TYPE_0220.equalsIgnoreCase(bankResponse.getMessageType())) {
						LOG.debug("0220 : Got a TXN ALERT");
						bankResponse = processBankResponse.processTransactionAdvice(bankResponse);
						bankResponse.getBankRequest().setTransactionType(TransactionType.ALERT);
						LOG.debug(bankResponse);
					} else if (InterfaceConstants.MSG_TYPE_0230.equals(bankResponse.getMessageType())) {
						LOG.debug("0230 : Got a TXN ADVICE RESPONSE");
						this.handle0230Response(bankResponse);
						
					} else {
						// dead letter message
						LOG.warn("Unknown Message Type Dead Letter : " + bankResponse.getMessageType());
						return;
					}
					if (bankResponse != null) {
						// we got response if success process the charge.
						if(isReversal) {
							LOG.debug("In HandleBankResponse>>>>>>> Is a reveral Response. Change properties");
							bankResponse.getBankRequest().setTransactionType(TransactionType.REVERSAL);
						}
						LOG.info("Puting response into the queue for transaction " + bankResponse.getBankRequest().getReference() + " txn type " + bankResponse.getBankRequest().getTransactionType()+" Response Code = "+bankResponse.getResponseCode());

						this.sendBankReplyMessage(bankResponse);
						
					} else {
						LOG.warn("NULL Parse Result for Message : ");
					}
				}
			} catch (JMSException e) {
				LOG.error("JMS Exception thrown : Message : " + e.getMessage(), e);
			} catch (Exception e) {
				LOG.error("Exception thrown : Message : " + e.getMessage(), e);
			}
		} else {
			LOG.debug("Found a non-text message " + message.toString());
		}

	}

	
	
	private void handle0210Response(BankResponse bankResponse) throws Exception {
		LOG.debug("In Handle 0210 Response: Type : " + bankResponse.getBankRequest().getTransactionType());
		if (TransactionType.TARIFF.equals(bankResponse.getBankRequest().getTransactionType())) {
			LOG.debug("The 0210 was a txn charge...");
			processBankResponse.processTransactionChargeResponse(bankResponse);
		} else if (TransactionType.COMMISSION.equals(bankResponse.getBankRequest().getTransactionType())) {
			LOG.debug("The 0210 was a commission...");
			processBankResponse.processAgentCommissionResponse(bankResponse);
		} else {
			LOG.debug("We got a real Transaction response");
			BankResponseHandlerResponse bankResponseHandlerResponse = processBankResponse.handleBankResponse(bankResponse);
			LOG.debug("Done processing the real transaction 0210");
			if (bankResponseHandlerResponse != null && bankResponseHandlerResponse.getCommissionInfos() != null) {
				LOG.debug("Result from 0210 processing is has some commission infos");
				
				LOG.debug("Response Code For Real Txn = "+bankResponseHandlerResponse.getBankResponse().getResponseCode());
				// Check Response Code before Posting Commissions
				
				if(ResponseCode.E000.equals(bankResponseHandlerResponse.getBankResponse().getResponseCode())) {
					for (CommissionMessage commissionMessage : bankResponseHandlerResponse.getCommissionInfos()) {
						this.processTransactionCommission(commissionMessage);
					}
				}
			} else {
				LOG.debug("No commissions in the 0210 messages.");
			}
		}
	}

	private void handle0230Response(BankResponse bankResponse) throws Exception {
		LOG.debug("In Handle 0230 Response: Type : " + bankResponse.getBankRequest().getTransactionType());
		if (TransactionType.TARIFF.equals(bankResponse.getBankRequest().getTransactionType())) {
			LOG.debug("The 0230 was a txn charge...");
			processBankResponse.processTransactionChargeResponse(bankResponse);
		} else if (TransactionType.COMMISSION.equals(bankResponse.getBankRequest().getTransactionType())) {
			LOG.debug("The 0230 was a commission...");
			processBankResponse.processAgentCommissionResponse(bankResponse);
		} else {
			LOG.debug("We got a real Transaction response");
			BankResponseHandlerResponse bankResponseHandlerResponse = processBankResponse.handleBankResponse(bankResponse);
			LOG.debug("Done processing the real transaction 0230");
			if (bankResponseHandlerResponse != null && bankResponseHandlerResponse.getCommissionInfos() != null) {
				LOG.debug("Result from 0230 processing is has some commission infos");
				
				LOG.debug("Response Code For Real Txn = "+bankResponseHandlerResponse.getBankResponse().getResponseCode());
				// Check Response Code before Posting Commissions
				
				if(ResponseCode.E000.equals(bankResponseHandlerResponse.getBankResponse().getResponseCode())) {
					for (CommissionMessage commissionMessage : bankResponseHandlerResponse.getCommissionInfos()) {
						this.processTransactionCommission(commissionMessage);
					}
				}
			} else {
				LOG.debug("No commissions in the 0230 messages.");
			}
		}
	}
	
	private void processTransactionCommission(CommissionMessage commissionMessage) throws Exception {
		LOG.debug("In post charges to EQ3");
		List<Response> responses = processBankResponse.processTransactionCharge(commissionMessage.getBankRequest().getReference());
		LOG.debug("Responses to post to EQ3 " + responses);
		if (responses == null || responses.isEmpty()) {
			LOG.debug("There were no commission responses");
		} else {
			LOG.debug("Posting charges to EQ3");
			for (Response response : responses) {
				if (ResponseCode.E000.equals(response.getResponseCode())) {
					this.sendRequestToBank(response.getMessage());
					LOG.debug("SENT CHARGE TO HOST >> \n" + response.getMessage());
				} else {
					LOG.debug("Failed to post charge message " + response.getResponseCode() + " : " + response.getMessage());
				}
			}
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
