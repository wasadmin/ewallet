package zw.co.esolutions.mobile.thread.utils;

import zw.co.esolutions.ewallet.msg.MobileWebRequestMessage;
import zw.co.esolutions.ewallet.sms.MessageSender;
import zw.co.esolutions.ewallet.util.EWalletConstants;

public class MobileWebMessageSenderThread implements Runnable {
	
	private MobileWebRequestMessage mobileWebMsg;
	
	public MobileWebMessageSenderThread(MobileWebRequestMessage mobileWebMsg) {
		this.mobileWebMsg = mobileWebMsg;
	}
	
	public void run() {
		try {
			MessageSender.send(EWalletConstants.FROM_USSD_TO_SWITCH_QUEUE, mobileWebMsg);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
