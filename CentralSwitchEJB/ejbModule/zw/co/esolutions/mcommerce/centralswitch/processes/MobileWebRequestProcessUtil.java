package zw.co.esolutions.mcommerce.centralswitch.processes;

import javax.ejb.Local;

import zw.co.esolutions.ewallet.msg.MobileWebRequestMessage;
import zw.co.esolutions.ewallet.msg.RequestInfo;
import zw.co.esolutions.mcommerce.centralswitch.model.MessageTransaction;
import zw.co.esolutions.ussd.web.services.WebSession;

@Local
public interface MobileWebRequestProcessUtil {
	MessageTransaction populateMessageTransaction(MobileWebRequestMessage webRequest) throws Exception;

	RequestInfo populateRequestInfo(MessageTransaction messageTransaction, MobileWebRequestMessage webRequest) throws Exception;

	MessageTransaction createMessageTransaction(MessageTransaction messageTransaction) throws Exception;

	WebSession createMobileWebSession(RequestInfo requestInfo,
			MobileWebRequestMessage ussdRequest);
}
