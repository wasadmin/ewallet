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
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.xmlbeans.XmlException;

import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.mcommerce.centralswitch.processes.ProcessUtil;
import zw.co.esolutions.mcommerce.msg.BankRegistrationMessage;
import zw.co.esolutions.mcommerce.msg.BankRegistrationMessageDocument;
import zw.co.esolutions.mcommerce.msg.Message;

/**
 * Message-Driven Bean implementation class for: RegistrationMDB
 * 
 */
@MessageDriven(activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") }, mappedName = EWalletConstants.FROM_EWALLET_TO_REG_QUEUE)
public class HandleBankRegistration implements MessageListener {
	
	@Resource(mappedName = "jms/EWalletQCF")
	private QueueConnectionFactory queueConnectionFactory;
	
	@Resource(mappedName = "jms/REG.IN.QUEUE")
	private Queue regInQueue;
	
	private Connection jmsConnection;
	
	@EJB 
	private ProcessUtil switchProcessUtil;
	/**
	 * Default constructor.
	 */
	public HandleBankRegistration() {
		// TODO Auto-generated constructor stub
	}
	
	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/switch.log.properties");
			LOG = Logger.getLogger(HandleBankRegistration.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + HandleBankRegistration.class);
		}
	}
	
	@PostConstruct
	public void initialise(){
		try {
			jmsConnection = queueConnectionFactory.createConnection();
		} catch (JMSException e) {
			LOG.fatal("Failed to initialise MDB " + this.getClass().getSimpleName(), e);
			e.printStackTrace(System.err);
			throw new EJBException(e);
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
	public void onMessage(javax.jms.Message message) {
		zw.co.esolutions.ewallet.msg.SynchronisationResponse response;
		LOG.debug("Handling bank registration message "); 
		if (message instanceof TextMessage) {
			TextMessage text = (TextMessage) message;
			try {
				String xml = text.getText();
				LOG.debug("Registration MDB message =  " + xml);

				BankRegistrationMessageDocument doc = BankRegistrationMessageDocument.Factory.parse(xml);
				BankRegistrationMessage bankRegistrationMessage = doc.getBankRegistrationMessage();

				if (bankRegistrationMessage != null) {
					BankRegistrationMessage.MessageCommand.Enum action = bankRegistrationMessage.getMessageCommand();
					response = switchProcessUtil.processRegMessage(bankRegistrationMessage, action);
					LOG.debug("Done handling registration response = " + response);
					if(response == null){
						LOG.debug("Synchronisation ignored, no need to sync this object" );
					} else if (!ResponseCode.E000.name().equalsIgnoreCase(response.getResponseCode())) {
						LOG.debug("Failed to do the actual reg, Pushing back " + response.getNarrative());
						this.submitResponse(xml);
					} 
					else if (ResponseCode.E000.name().equalsIgnoreCase(response.getResponseCode())){
						LOG.debug(" successs" );
						
					}

				}

			} catch (JMSException e) {
				LOG.error("JMS Exception : Message " + e.getMessage(), e);
			} catch (XmlException e) {
				LOG.error("XML Exception : Message " + e.getMessage(), e);
			} catch (Exception e) {
				LOG.error("Exception : Message " + e.getMessage(), e);
			}
		} else {
		
//			try {
//				ObjectMessage msg = (ObjectMessage) message;
//				DownloadRequest req = (DownloadRequest) msg.getObject();
//
//				LOG.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Registration MDB message =  " + req);
//
//				if (req == null) {
//					return;
//				}
//
//				LOG.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Registration MDB message BankCode =  " + req.getBankCode() + " Downlod Command = " + req.getCommand());
//
//				if (EWalletConstants.DOWNLOAD_BANKS.equalsIgnoreCase(req.getCommand())) {
//					
//					switchProcessUtil.processBankDownloadRequest(req);
//					
//				} else if (EWalletConstants.DOWNLOAD_MERCHANTS.equalsIgnoreCase(req.getCommand())) {
//
//					switchProcessUtil.processMerchantDownloadRequest(req);
//					
//				}
//
//			} catch (JMSException e) {
//				e.printStackTrace();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		}
	}

	public boolean submitResponse(String message) {
		Session jmsSession = null;
		MessageProducer jmsProducer = null;
		try {
			jmsSession = jmsConnection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			jmsProducer = jmsSession.createProducer(regInQueue);
			TextMessage jmsTextMessage = jmsSession.createTextMessage(message);
			jmsProducer.send(jmsTextMessage);
		} catch (JMSException e) {
			LOG.fatal("FAILED TO PUSH REG MSG TO SRC QUEUE \n : " + message , e);
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
		return true;
	}

}
