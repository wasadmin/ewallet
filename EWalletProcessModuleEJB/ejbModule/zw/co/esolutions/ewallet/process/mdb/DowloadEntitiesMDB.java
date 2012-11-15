package zw.co.esolutions.ewallet.process.mdb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.xmlbeans.XmlException;

import zw.co.esolutions.ewallet.download.session.DownloadProcessUtil;
import zw.co.esolutions.ewallet.msg.DownloadResponse;
import zw.co.esolutions.ewallet.msg.SynchronisationResponse;
import zw.co.esolutions.ewallet.sms.MessageSender;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.mcommerce.msg.BankRegistrationMessage;
import zw.co.esolutions.mcommerce.msg.Message;

/**
 * Message-Driven Bean implementation class for: RegistrationMDB
 * 
 */
@MessageDriven(activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"), @ActivationConfigProperty(propertyName = "messageSelector", propertyValue = "transactionType = 'DOWNLOAD_TYPE'") }, mappedName = EWalletConstants.FROM_SWITCH_REG_TO_ZB_EWALLET_QUEUE)
public class DowloadEntitiesMDB implements MessageListener {

	@EJB
	private DownloadProcessUtil downloadProcessUtil;

	/**
	 * Default constructor.
	 */
	public DowloadEntitiesMDB() {
		super();
	}

	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(DowloadEntitiesMDB.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + DowloadEntitiesMDB.class);
		}
	}

	/**
	 * @see MessageListener#onMessage(Message)
	 */
	public void onMessage(javax.jms.Message message) {
		SynchronisationResponse response = null;
		LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> DownloadEntities MDB Consumed ");
		if (!(message instanceof TextMessage)) {

			try {
				ObjectMessage msg = (ObjectMessage) message;
				DownloadResponse res = (DownloadResponse) msg.getObject();

				// if(res.getDownloadRequest() == null) {
				// //It's a push from switch
				// } else {
				// //It's a download initiated from the bank side
				// }
				String xml = res.getDoc().toString();
				LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> DownloadEntities MDB message =  " + xml);

				BankRegistrationMessage bankRegistrationMessage = res.getDoc().getBankRegistrationMessage();

				if (bankRegistrationMessage != null) {
					BankRegistrationMessage.MessageCommand.Enum action = bankRegistrationMessage.getMessageCommand();

					response = downloadProcessUtil.processRegMessage(res, action);

					LOG.debug(">>>>>>>>>>>>>>>>>>>>> Response = " + response);
					if (!(response == null || ResponseCode.E000.name().equalsIgnoreCase(response.getResponseCode()))) {
						LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Pushing back " + response.getNarrative());
						this.pushToOriginatorQueue(res);
					}

				}

			} catch (JMSException e) {
				LOG.debug("JMS Exception Thrown : Message " + e.getMessage() + " : ", e);
			} catch (XmlException e) {
				LOG.debug("XML Exception Thrown : Message " + e.getMessage() + " : ", e);
			} catch (Exception e) {
				LOG.debug("JMS Exception Thrown : Message " + e.getMessage() + " : ", e);
			}
		}
	}

	private void pushToOriginatorQueue(DownloadResponse response) {
		try {
			//MessageSender.send(EWalletConstants.FROM_SWITCH_TO_ZB_QUEUE, response, response.getDownloadRequest().getCommand());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
