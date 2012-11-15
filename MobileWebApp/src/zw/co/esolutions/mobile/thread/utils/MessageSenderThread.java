package zw.co.esolutions.mobile.thread.utils;

import zw.co.esolutions.ewallet.msg.USSDRequestMessage;
import zw.co.esolutions.ewallet.sms.MessageSender;
import zw.co.esolutions.ewallet.util.EWalletConstants;

public class MessageSenderThread implements Runnable {
	
	private USSDRequestMessage ussdMsg;
	
	public MessageSenderThread(USSDRequestMessage ussdMsg) {
		this.ussdMsg = ussdMsg;
	}
	
	public void run() {
		try {
			MessageSender.send(EWalletConstants.FROM_USSD_TO_SWITCH_QUEUE, ussdMsg);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
