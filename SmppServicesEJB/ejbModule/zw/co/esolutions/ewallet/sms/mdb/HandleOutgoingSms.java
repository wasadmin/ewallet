package zw.co.esolutions.ewallet.sms.mdb;

import java.util.Properties;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;

import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.ewallet.activemq.ActiveMqSender;
import zw.co.esolutions.ewallet.enums.MobileNetworkOperator;
import zw.co.esolutions.ewallet.sms.MessageSender;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.SystemConstants;
import zw.co.esolutions.mcommerce.gatewayservices.smpp.SMSSender;
import zw.co.esolutions.mcommerce.msg.MessageDocument;
import zw.co.esolutions.mcommerce.msg.MobileTerminatedShortMessage;

/**
 * Message-Driven Bean implementation class for: OutgoingSmsHandler
 *
 */
@MessageDriven(
		activationConfig = { @ActivationConfigProperty(
				propertyName = "destinationType", propertyValue = "javax.jms.Queue"
		) },
		mappedName = EWalletConstants.ECONET_SMS_OUT_QUEUE)
public class HandleOutgoingSms implements MessageListener {

	//@EJB private SmppService smppService;
	
	//@EJB 
	//private Transceiver transceiver;
	@EJB
	private SMSSender smsSender;
	
	private Properties config = SystemConstants.configParams;
	private static Logger LOG;
	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/switch.log.properties");
			LOG = Logger.getLogger(HandleOutgoingSms.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + HandleOutgoingSms.class);
		}
	}
    /**
     * Default constructor. 
     */
    public HandleOutgoingSms() {
        // TODO Auto-generated constructor stub
    }
	
	/**
     * @see MessageListener#onMessage(Message)
     */
    public void onMessage(Message message) {
        MessageDocument doc;
        zw.co.esolutions.mcommerce.msg.Message msg;
        MobileTerminatedShortMessage mtsm=null;
    	
    	LOG.debug(">>>>>>>>>>>>>>>>>>>>Picking up outgoing SMS");
    	try {
			if(message != null) {
				if(message instanceof TextMessage) {
					TextMessage text = (TextMessage)message;
					System.out.println("Message Picked ... " + text.getText());
					doc = MessageDocument.Factory.parse(text.getText());
					msg = doc.getMessage();					
					mtsm = msg.getMobileTerminatedShortMessage();				    
				    /*if(!mtsm.getSubscriberId().startsWith("+")) {
				    	mtsm.setSubscriberId("+" + mtsm.getSubscriberId());
				    }
					this.smppService.sendMessage(mtsm.getSubscriberId(), mtsm.getShortMessage());*/
					this.processOutgoingMessage(mtsm);
				}
			}
		} catch (Exception e) {
			LOG.debug("Failed to send message ",e );
			if(mtsm != null){
				try {
					MessageSender.send(EWalletConstants.ECONET_SMS_OUT_QUEUE, mtsm.getSubscriberId(),mtsm.getShortMessage());
				} catch (Exception e1) {
					LOG.debug("Failed to return message to queue", e1);
				}
			}
		}
        
    }
    
    private void processOutgoingMessage(MobileTerminatedShortMessage mtsm) throws Exception{
    	MobileNetworkOperator mno = this.getMNO(mtsm.getSubscriberId());
    	
    	if(MobileNetworkOperator.ECONET.equals(mno)){
    		LOG.debug(">>>>>>>>Received Econet Msg");
    		ActiveMqSender activeMqSender = new ActiveMqSender(config.getProperty("SYSTEM_ACTIVEMQ_URL"),config.getProperty("SYSTEM_ACTIVEMQ_USER"),config.getProperty("SYSTEM_ACTIVEMQ_PASSWORD"), config.getProperty("SYSTEM_ECONET_OUT_QUEUE"));
    		LOG.debug("ActiveMQ connection initialized successfully");
    		String newMtsmString = this.convertEWalletMTSMToMPGMTSM(mtsm);
    		
    		activeMqSender.sendMessage(newMtsmString);
    		LOG.debug("Message sent to ActiveMQ successfully.");
    		//Close ActiveMQ connection
    		activeMqSender.cleanUp();
    	}else if(MobileNetworkOperator.NETONE.equals(mno)){
    		LOG.debug(">>>>>>>>Received NetOne Msg");
    		
    		this.send(mtsm);
    		LOG.debug(">>>>>>>>Sent NetOne message ");
    	}else if(MobileNetworkOperator.TELECEL.equals(mno)){
    		LOG.debug(">>>>>>>>Received Telecel Msg");
    		this.send(mtsm);
    		LOG.debug(">>>>>>>>Sent telecel message ");
    	}
    }
    
    private void send(MobileTerminatedShortMessage mtsm) throws Exception{
    	//transceiver.initialize();
    	smsSender.sendMessage("ZB eWallet", mtsm.getSubscriberId(), mtsm.getShortMessage());
    	//transceiver.cleanup();
    }
    
    private String convertEWalletMTSMToMPGMTSM(MobileTerminatedShortMessage mtsm){
    	zw.co.esolutions.msg.MessageDocument doc = zw.co.esolutions.msg.MessageDocument.Factory.newInstance();
    	zw.co.esolutions.msg.Message msg = doc.addNewMessage();
        zw.co.esolutions.msg.MobileTerminatedShortMessage newMtsm = msg.addNewMobileTerminatedShortMessage();
        
        newMtsm.setSubscriberId(mtsm.getSubscriberId());
        newMtsm.setShortMessage(mtsm.getShortMessage());
        newMtsm.setDateRequested(mtsm.getDateRequested());
        newMtsm.setUuid(mtsm.getUuid());
        newMtsm.setDeliveryReportRequired(mtsm.getDeliveryReportRequired());
        newMtsm.setUserMessageId(mtsm.getUserMessageId());
        
        return doc.toString();
    }

    private MobileNetworkOperator getMNO(String mobileNumber){
    	System.out.println(">>>>> Mobile Number: "+mobileNumber);
		if(mobileNumber.startsWith("26373")){
			return MobileNetworkOperator.TELECEL;
		}else if(mobileNumber.startsWith("073")){
			return MobileNetworkOperator.TELECEL;
		}else if(mobileNumber.startsWith("26371")){
			return MobileNetworkOperator.NETONE;
		}else if(mobileNumber.startsWith("071")){
			return MobileNetworkOperator.NETONE;
		}else if(mobileNumber.startsWith("26377")){
			return MobileNetworkOperator.ECONET;
		}else if(mobileNumber.startsWith("077")){
			return MobileNetworkOperator.ECONET;
		}else{
			return null;
		}
	}
}
