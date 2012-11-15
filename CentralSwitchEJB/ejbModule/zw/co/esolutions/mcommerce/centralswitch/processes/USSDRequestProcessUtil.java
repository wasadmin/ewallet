package zw.co.esolutions.mcommerce.centralswitch.processes;

import javax.ejb.Local;

import zw.co.esolutions.ewallet.msg.RequestInfo;
import zw.co.esolutions.ewallet.msg.USSDRequestMessage;
import zw.co.esolutions.mcommerce.centralswitch.model.MessageTransaction;
import zw.co.esolutions.ussd.web.services.UssdTransaction;

@Local
public interface USSDRequestProcessUtil {
	MessageTransaction populateMessageTransaction(USSDRequestMessage ussdRequest) throws Exception;

	RequestInfo populateRequestInfo(MessageTransaction messageTransaction, USSDRequestMessage ussdRequest) throws Exception;

	MessageTransaction createMessageTransaction(MessageTransaction messageTransaction) throws Exception;

	UssdTransaction createUSSDTransaction(RequestInfo requestInfo,
			USSDRequestMessage ussdRequest);
}
