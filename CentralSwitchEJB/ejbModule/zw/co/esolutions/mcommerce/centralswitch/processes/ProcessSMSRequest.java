package zw.co.esolutions.mcommerce.centralswitch.processes;
import javax.ejb.Local;

import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.msg.MerchantResponse;
import zw.co.esolutions.ewallet.msg.RequestInfo;
import zw.co.esolutions.ewallet.msg.ResponseInfo;
import zw.co.esolutions.mcommerce.centralswitch.model.MessageTransaction;
import zw.co.esolutions.topup.ws.impl.WsResponse;

@Local
public interface ProcessSMSRequest {

	MessageTransaction parseSMSRequestMessage(String smsRequestText);
	
	MessageTransaction checkRegistration(MessageTransaction txn);
	
	MessageTransaction findMessageTransactionByUUID(String uuid);
	
	MessageTransaction createTransaction(MessageTransaction txn);
	
	MessageTransaction promoteTransactionStatus(MessageTransaction txn, TransactionStatus vereq, String narrative);
	
	MessageTransaction updateTransaction(MessageTransaction txn);
	
	MessageTransaction generatePasswordParts(MessageTransaction txn);
	
	MessageTransaction handleTapRequest(MessageTransaction txn);
	
	MessageTransaction validatePinChangeRequest(MessageTransaction requestInfo);
	
	MessageTransaction handlePasscodeResponse(MessageTransaction pareq);
	
	RequestInfo populateRequestInfo(MessageTransaction txn);

	MerchantResponse populateTopupMerchantResponse(ResponseInfo responseInfo, WsResponse response) ;

	MerchantResponse populateMerchantResponse(MessageTransaction txn);

	MessageTransaction populatePAREQInfo(MessageTransaction txn);

}
