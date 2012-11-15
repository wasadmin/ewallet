package zw.co.esolutions.ewallet.extpayments.mdb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Message-Driven Bean implementation class for: HandlePaymentResponse
 *
 */
@MessageDriven(
		activationConfig = { @ActivationConfigProperty(
				propertyName = "destinationType", propertyValue = "javax.jms.Queue"
		) }, 
		mappedName = "EXTPAY.OUT.Q")
public class HandlePaymentResponse implements MessageListener {
	
	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/switch.log.properties");
			LOG = Logger.getLogger(HandlePaymentResponse.class);
		} catch (Exception e) {
		}
	}
    
	/**
     * @see MessageListener#onMessage(Message)
     */
    public void onMessage(Message message) {
        
    	LOG.debug("@EXTPAY HandlePaymentResponse consumed!");
    	
    	try {

    		if (message instanceof TextMessage) {
    			TextMessage textMessage = (TextMessage) message;
    			String text = textMessage.getText();
    			LOG.debug("@Response received: " + text);
    			
    			//Call EcoCash WebService
    			
    		}
    	} catch (Exception e) {
    		LOG.fatal(e);
			e.printStackTrace(System.err);
		}
    	
        
    }

}
