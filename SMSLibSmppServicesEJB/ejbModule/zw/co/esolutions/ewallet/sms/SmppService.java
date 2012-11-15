package zw.co.esolutions.ewallet.sms;

import org.smslib.IQueueSendingNotification;

public interface SmppService extends IQueueSendingNotification {

	boolean loadConfiguration() throws Exception;

	void sendMessage(String phoneNumber, String responseMsg) throws Exception;

}
