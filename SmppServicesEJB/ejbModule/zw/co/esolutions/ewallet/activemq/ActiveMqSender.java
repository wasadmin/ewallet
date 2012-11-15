package zw.co.esolutions.ewallet.activemq;


import java.util.Random;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.ewallet.sms.mdb.HandleOutgoingSms;

import javax.jms.*;

public class ActiveMqSender {

	private static Logger LOG;
	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/switch.log.properties");
			LOG = Logger.getLogger(HandleOutgoingSms.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + HandleOutgoingSms.class);
		}
	}
	private String mqUrl;
	private String queueName;
	 private static int ackMode = Session.AUTO_ACKNOWLEDGE;
	 private boolean transacted = false;
	
	private ActiveMQConnectionFactory connectionFactory;
	private Connection connection;
	private MessageProducer producer;
	private Destination destination;
	private Session session;
	
	public ActiveMqSender(String mqUrl,String username,String password, String queueName) {
		super();
		this.mqUrl = mqUrl;
		this.queueName = queueName;
		
		connectionFactory = new ActiveMQConnectionFactory(username, password,this.mqUrl);
        
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            this.session = connection.createSession(transacted, ackMode);
            this.destination = session.createQueue(this.queueName);

            //Setup a message producer to send message to the queue the server is consuming from
            this.producer = session.createProducer(this.destination);
            this.producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            
            
        } catch (JMSException e) {
            LOG.debug("Failed to connect to ActiveMQ",e);
        }
	}
	
	public void cleanUp(){
		try {
			this.producer.close();
			this.session.close();
			this.connection.close();
		} catch (JMSException e) {
			LOG.debug("Failed to close ActiveMQ connection",e);
		}
	}
	
	 private String createRandomString() {
	        Random random = new Random(System.currentTimeMillis());
	        long randomLong = random.nextLong();
	        return Long.toHexString(randomLong);
	  }
	 
	 public void sendMessage(String message) throws JMSException{
		 try {
			//Now create the actual message you want to send
	            TextMessage txtMessage = session.createTextMessage();
	            txtMessage.setText(message);
	            String correlationId = this.createRandomString();
	            txtMessage.setJMSCorrelationID(correlationId);
	            this.producer.send(txtMessage);
	            LOG.debug("Message sent to ActiveMQ successfully.");
		} catch (JMSException e) {
			LOG.debug("Failed to send message to ActiveMQ",e);
			throw e;
		}
	 }
	
}
