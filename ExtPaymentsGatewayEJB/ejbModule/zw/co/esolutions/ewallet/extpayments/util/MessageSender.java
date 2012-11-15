package zw.co.esolutions.ewallet.extpayments.util;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

public class MessageSender {
	
	public MessageSender() {
	
	}
	
	public static boolean sendTextToQueueDestination(Connection jmsConnection,  String message, Queue destinationQueueName, Logger LOG) {
		
		Session jmsSession = null;
		MessageProducer jmsProducer = null;
		try {
			jmsSession = jmsConnection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			jmsProducer = jmsSession.createProducer(destinationQueueName);
			TextMessage jmsTextMessage = jmsSession.createTextMessage(message);
			
			jmsProducer.send(jmsTextMessage);
			LOG.info("MSG SENT TO QUEUE : \n " + message );
		} catch (JMSException e) {
			LOG.fatal("FAILED TO MSG TO QUEUE : [" + destinationQueueName +" ]\n " + message , e);
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
