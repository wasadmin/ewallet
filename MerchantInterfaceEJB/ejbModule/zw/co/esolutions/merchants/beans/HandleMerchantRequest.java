package zw.co.esolutions.merchants.beans;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.mcommerce.xml.Messages;
import zw.co.esolutions.merchants.session.MerchantRequestProcessor;
import zw.co.esolutions.merchants.util.MerchantException;

/**
 * Message-Driven Bean implementation class for: HandleMerchantRequest
 * 
 */
@MessageDriven(activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") }, mappedName = "jms/MERCHANT.REQ.Q")
public class HandleMerchantRequest implements MessageListener {

	@EJB
	private MerchantRequestProcessor merchantRequestProcessor;

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

	/**
	 * Default constructor.
	 */
	public HandleMerchantRequest() {

	}

	/**
	 * @see MessageListener#onMessage(Message)
	 */
	public void onMessage(Message message) {
		LOG.debug("GOT a message");
		if (message instanceof TextMessage) {

			TextMessage jmsTextMessage = (TextMessage) message;

			try {
				String xmlMessage = jmsTextMessage.getText();
				Messages messages = merchantRequestProcessor.handleMerchantRequest(xmlMessage);
				if(messages == null){
					LOG.error("GOT a null after MARSHALL : " + messages);
					return;
				}
				boolean sent = merchantRequestProcessor.submitMessageToQueue(messages.getIsoMsg(), messages.getMetadata());
			} catch (JMSException e) {
				LOG.warn("Failed to extract text from the JMS Text Message");
				e.printStackTrace(System.err);
			} catch (MerchantException e) {
				LOG.fatal("Exception thrown in processing merchant request", e);
				e.printStackTrace(System.err);
			} catch(Exception e){
				LOG.warn("Failed to extract text from the JMS Text Message");
				e.printStackTrace(System.err);
			}

		} else {
			LOG.warn("Received an unexpected message type : IGNORE");
		}
	}

}
