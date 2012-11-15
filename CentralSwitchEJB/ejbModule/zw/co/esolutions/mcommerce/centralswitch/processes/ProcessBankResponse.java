package zw.co.esolutions.mcommerce.centralswitch.processes;
import java.util.List;

import javax.ejb.Local;

import zw.co.esolutions.ewallet.msg.NotificationInfo;
import zw.co.esolutions.ewallet.msg.RequestInfo;
import zw.co.esolutions.ewallet.msg.ResponseInfo;

@Local
public interface ProcessBankResponse {

	List<NotificationInfo> process(ResponseInfo responseInfo) throws Exception;
	
	List<NotificationInfo> processReversal(ResponseInfo responseInfo) throws Exception;
	
	public List<NotificationInfo> processPinChangeAdvice(ResponseInfo requestInfo) throws Exception;

}
