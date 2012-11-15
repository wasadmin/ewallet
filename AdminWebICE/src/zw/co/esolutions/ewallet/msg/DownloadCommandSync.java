/**
 * 
 */
package zw.co.esolutions.ewallet.msg;

import zw.co.esolutions.ewallet.sms.MessageSender;
import zw.co.esolutions.ewallet.util.EWalletConstants;


/**
 * @author wasadmin
 *
 */
public class DownloadCommandSync {

	/**
	 * 
	 */
	public DownloadCommandSync() {
		// TODO Auto-generated constructor stub
	}
	
	public static void synch(String command, String bankCode) throws Exception{
		DownloadRequest d = new DownloadRequest();
		d.setCommand(command);
		d.setBankCode(bankCode);
//		MessageSender.send(EWalletConstants.FROM_EWALLET_TO_REG_QUEUE, d);
	}

}
