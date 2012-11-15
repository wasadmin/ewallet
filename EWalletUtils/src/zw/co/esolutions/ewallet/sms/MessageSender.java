package zw.co.esolutions.ewallet.sms;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import zw.co.esolutions.ewallet.msg.BankRequest;
import zw.co.esolutions.ewallet.msg.BulkBatchResponse;
import zw.co.esolutions.ewallet.msg.ResponseInfo;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.mcommerce.msg.MessageDocument;
import zw.co.esolutions.mcommerce.msg.MobileTerminatedShortMessage;

public class MessageSender {
	
	public MessageSender() {
	}
	
	public static boolean sendSMSToMobileDestination(Connection jmsConnection,  String message, String targetMobile, Destination destination, Logger LOG) {
		Session jmsSession = null;
		MessageProducer jmsProducer = null;
		message = message.replace("..", ".");
		try {
			jmsSession = jmsConnection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			jmsProducer = jmsSession.createProducer(destination);
			
			MessageDocument mdoc = MessageDocument.Factory.newInstance();
			zw.co.esolutions.mcommerce.msg.Message xmlMsg = mdoc.addNewMessage();
			MobileTerminatedShortMessage mtsm = xmlMsg.addNewMobileTerminatedShortMessage();
			mtsm.setShortMessage(message);
			mtsm.setSubscriberId(targetMobile);
			TextMessage jmsTextMessage = jmsSession.createTextMessage(mdoc.toString());
			
			jmsProducer.send(jmsTextMessage);
			LOG.info("MSG SENT TO QUEUE : \n " + mdoc.toString());
		} catch (JMSException e) {
			LOG.fatal("FAILED TO PUSH REG MSG TO SMPP OUT QUEUE \n : " + message , e);
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
	
	public static boolean sendObjectToDestination(Connection jmsConnection,  Serializable message, String messageSelector, Destination destination, Logger LOG) {
		Session jmsSession = null;
		MessageProducer jmsProducer = null;
		try {
			jmsSession = jmsConnection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			jmsProducer = jmsSession.createProducer(destination);
			ObjectMessage jmsObjectMessage = jmsSession.createObjectMessage(message);
			if (messageSelector == null || "".equalsIgnoreCase(messageSelector.trim())) {
				LOG.debug("No message selector here : proceed");
			}else{
				jmsObjectMessage.setStringProperty("transactionType", messageSelector);
			}
			jmsProducer.send(jmsObjectMessage);
			LOG.info("MSG SENT TO QUEUE : \n " + message );
		} catch (JMSException e) {
			LOG.fatal("FAILED TO PUSH MSG TO QUEUE \n : " + message , e);
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
	
	public static boolean sendObjectToDestination(Connection jmsConnection,  Serializable message, String messageSelector, String destinationQueueName, Logger LOG) {
		Session jmsSession = null;
		MessageProducer jmsProducer = null;
		try {
			jmsSession = jmsConnection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			Queue queue = jmsSession.createQueue(destinationQueueName);
			jmsProducer = jmsSession.createProducer(queue);
			ObjectMessage jmsObjectMessage = jmsSession.createObjectMessage(message);
			if (messageSelector == null || "".equalsIgnoreCase(messageSelector.trim())) {
				LOG.debug("No message selector here : proceed");
			}else{
				jmsObjectMessage.setStringProperty("transactionType", messageSelector);
			}
			jmsProducer.send(jmsObjectMessage);
			LOG.info("MSG SENT TO QUEUE : \n " + message );
		} catch (JMSException e) {
			LOG.fatal("FAILED TO PUSH REG MSG TO QUEUE \n : " + message , e);
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
	
	public static boolean sendTextToQueueDestination(Connection jmsConnection,  String message, String destinationQueueName, Logger LOG) {
		
		Session jmsSession = null;
		MessageProducer jmsProducer = null;
		try {
			jmsSession = jmsConnection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			Queue queue = jmsSession.createQueue(destinationQueueName);
			jmsProducer = jmsSession.createProducer(queue);
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

	public static void send(String fromEwalletToBankmedQueue, BankRequest bankRequest) {
		// TODO Auto-generated method stub
		
	}

	public static void send(String fromEwalletToSwitchQueue, ResponseInfo responseInfo, String string) {
		// TODO Auto-generated method stub
		
	}

	public static void send(String queueName, BulkBatchResponse batchResponse) {
		// TODO Auto-generated method stub
		
	}

//	public static void send(String fromEwalletToSwitchRegQueue, String message) {
		// TODO Auto-generated method stub
		
//	}


	public static void send(String queueJndiName, Serializable object) throws Exception {

	InitialContext ctx = new InitialContext();
		QueueConnectionFactory qcf = (QueueConnectionFactory) ctx.lookup(EWalletConstants.EWALLET_QCF);
		QueueConnection qc = qcf.createQueueConnection();
		Queue destination = (Queue) ctx.lookup(queueJndiName);
		QueueSession session = qc.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
	   	MessageProducer producer = session.createProducer(destination);
	   	ObjectMessage objMsg = session.createObjectMessage();
	   	objMsg.setObject(object);
	   	producer.send(objMsg);
	   	producer.close();
		session.close();
	   	qc.close();
	}
	
	public static void send(String queueJndiName, String message) throws Exception {
		
	   	sendSMPPRequest(queueJndiName, message, EWalletConstants.EWALLET_QCF);
	}

//	public static void send(zw.co.esolutions.ewallet.msg.NotificationInfo notificationInfo) throws Exception {
//		send(notificationInfo.getDestinationQueue(), notificationInfo.getReceiptientMobileNumber(), notificationInfo.getNarrative());
//	}

	public static void sendSMPPRequest(String queueJndiName, String message, String queueConnectionFactory) throws Exception {
		InitialContext ctx = new InitialContext();
		QueueConnectionFactory qcf = (QueueConnectionFactory) ctx.lookup(queueConnectionFactory);
		QueueConnection qc = qcf.createQueueConnection();
		Queue destination = (Queue) ctx.lookup(queueJndiName);
		QueueSession session = qc.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
	   	MessageProducer producer = session.createProducer(destination);
	 	TextMessage textMsg = session.createTextMessage(message);
	 	producer.send(textMsg);
	   	try {
			System.out.println(textMsg.getText());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		producer.close();
		session.close();
	   	qc.close();
	}
}
