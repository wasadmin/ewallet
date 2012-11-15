/**
 * 
 */
package zw.co.esolutions.ewallet.queues;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import zw.co.esolutions.ewallet.serviceexception.EWalletException;

/**
 * @author tauttee
 *
 */
public class LoadQueueManager {
	
	private static QueueConnectionFactory getConnectionFactory(String qCF) {
		QueueConnectionFactory connectionFactory = null;
		try {
			InitialContext ctx = new InitialContext();
			connectionFactory = (QueueConnectionFactory) ctx.lookup(qCF);
		} catch (NamingException e) {
			e.printStackTrace();
		} 
		return connectionFactory;
	}

	private static Queue getQueue(String queueName) {
		Queue queue = null;
		try {
			InitialContext ctx = new InitialContext();
			queue = (Queue) ctx.lookup(queueName);
		} catch (NamingException e) {
			e.printStackTrace();
		} 
		return queue;
	}

	
	public static String loadQueue(String queueCFName, String queueName, Serializable msg, String msgType) throws EWalletException {
		try {
			QueueConnection connection = LoadQueueManager.getConnectionFactory(queueCFName).createQueueConnection();
			QueueSession session = connection.createQueueSession(false,	Session.AUTO_ACKNOWLEDGE);
			MessageProducer producer = session.createProducer(getQueue(queueName));
			ObjectMessage objMsg = session.createObjectMessage();

			if (msg == null) {
				return null;
			}
			
			objMsg.setObject(msg);
			objMsg.setStringProperty("messageType", msgType);
			producer.send(objMsg);
			
			System.out.println(">>>>>Sent Message >> " + msg);
			session.close();
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return "000";
	}
	
	
	
}
