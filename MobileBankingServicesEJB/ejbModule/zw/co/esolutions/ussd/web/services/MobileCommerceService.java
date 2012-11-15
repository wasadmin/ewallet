package zw.co.esolutions.ussd.web.services;
import java.util.List;
import javax.jws.WebParam;
import javax.jws.WebService;

import zw.co.esolutions.ewallet.msg.MobileWebRequestMessage;
import zw.co.esolutions.ussd.entities.USSDTransaction;
import zw.co.esolutions.ussd.entities.WebSession;

@WebService(name = "MobileCommerceService")

public interface MobileCommerceService {
	
  USSDTransaction createTransaction(@WebParam(name="ussdTransaction")USSDTransaction transaction) throws Exception;
  
  USSDTransaction updateTransaction(@WebParam(name="ussdTransaction")USSDTransaction transaction) throws Exception;
  
  void deleteTransaction(@WebParam(name="uuid")String uuid) throws Exception;
  
  USSDTransaction findTransaction(@WebParam(name="uuid")String uuid);
  
  USSDTransaction getTransactionByUSSDSessionId(@WebParam(name="ussdSessionId")String sessionId);
  
  WebSession createWebSession(@WebParam(name = "webSession")WebSession webSession);

  void deleteWebSession(@WebParam(name = "webSessionId")String id);

  WebSession findWebSessionById(@WebParam(name = "webSessionId")String id);

  WebSession getFailedWebSession(@WebParam(name = "mobileNumber")String mobileNumber, @WebParam(name = "bankId")String bankId);

  WebSession getWebSessionByReferenceId(@WebParam(name = "referenceId")String referenceId);

  WebSession updateWebSession(@WebParam(name = "webSession")WebSession webSession);

  WebSession getWebSessionByMobileAndBankAndStatus(@WebParam(name = "mobileNumber")String mobileNumber,
			@WebParam(name = "bankId")String bankId, @WebParam(name = "status")String status);
  boolean sendTransaction(@WebParam(name = "mobileWebRequest")MobileWebRequestMessage mobileWebReq);

  String getTargetBankCodeForTargetAccount(@WebParam(name = "sessionId")String sessionId,
		@WebParam(name = "targetAccount")String targetAccount, @WebParam(name = "targetMobile")String targetMobile);

  boolean isAgentMobile(@WebParam(name = "bankCode")String bankId, @WebParam(name = "formattedMobileNumber")String formattedMobileNumber);

  String validateAgentDeposit(@WebParam(name = "sourceAccount")String sourceAccount, @WebParam(name = "targetMobile")String targetMobile);

  String validateAgentTransfer(@WebParam(name = "sourceAccount")String sourceAccount, @WebParam(name = "targetAccount")String targetAccount,
		@WebParam(name = "bankCode")String bankId);

  String validateAgentCustomerWithdrawal(@WebParam(name = "targetAccount")String agentNumber);

  String getAgentNumberByMobileNumberAndBankId(@WebParam(name = "mobileNumber")String mobileNumber, @WebParam(name = "bankCode")String bankId);

  boolean isNonHolderAccount(@WebParam(name = "targetAccount")String targetAccount, @WebParam(name = "bankCode")String bankId);

  List<String> getActiveBankNames();
}
