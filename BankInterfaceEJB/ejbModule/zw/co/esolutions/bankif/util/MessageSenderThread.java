package zw.co.esolutions.bankif.util;

import zw.co.esolutions.ewallet.sms.MessageSender;

public class MessageSenderThread implements Runnable {
	
	private String formattedBankReqMessage;
	private String queueName;
	
	public MessageSenderThread(String queueName, String formattedBankReqMessage) {
		this.queueName = queueName;
		this.formattedBankReqMessage = formattedBankReqMessage;
	}
	
	public void run() {
		try {
			
			MessageSender.send(queueName, formattedBankReqMessage);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
