package zw.co.esolutions.ewallet.sms.mdb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import zw.co.esolutions.ewallet.sms.SmppService;
import zw.co.esolutions.ewallet.util.EWalletConstants;
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

	@EJB private SmppService smppService;
    /**
     * Default constructor. 
     */
    public HandleOutgoingSms() {
        // TODO Auto-generated constructor stub
    }
	
	/**
     * @@see MessageListener#onMessage(Message)
     */
    public void onMessage(Message message) {
        MessageDocument doc = null;
        zw.co.esolutions.mcommerce.msg.Message msg = null;
        MobileTerminatedShortMessage mosn = null;
    	
    	System.out.println("Picking up outgoing SMS" + message.getClass());
    	try {
			if(message != null) {
				if(message instanceof TextMessage) {
					System.out.println("Tapinda mu if");
					TextMessage text = (TextMessage)message;
					System.out.println("The TEXT : " + text.getText() + " now to parse.");
					doc = MessageDocument.Factory.parse(text.getText());
					System.out.println("Done parsing...");
					msg = doc.getMessage();
					System.out.println("Done geting the msg..");
					
				    mosn = msg.getMobileTerminatedShortMessage();
				    
				    if(!mosn.getSubscriberId().startsWith("+")) {
				    	mosn.setSubscriberId("+" + mosn.getSubscriberId());
				    }
				    System.out.println("Got the MTSM");
					//Sending Txt message Demo
				    System.out.println(">>>>>>>>>>>>>>>>> Taitora "+mosn.getSubscriberId());
					this.smppService.sendMessage(mosn.getSubscriberId(), mosn.getShortMessage());
					
					System.out.println("Zvaenda");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
        
    }

}