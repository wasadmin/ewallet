package zw.co.esolutions.mcommerce.centralswitch.processes;

import java.util.List;

import javax.ejb.Local;
import javax.jws.WebParam;

import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.msg.RequestInfo;
import zw.co.esolutions.mcommerce.centralswitch.model.MessageTransaction;
import zw.co.esolutions.mcommerce.centralswitch.model.TransactionState;

@Local
public interface CentralSwitchProcessUtility {

	MessageTransaction populateProcessTransaction(RequestInfo requestInfo);

	MessageTransaction createProcessTransaction(MessageTransaction tranx) throws Exception;

	MessageTransaction updateProcessTransaction(MessageTransaction tranx) throws Exception;

	TransactionState updateTransactionState(TransactionState tranxState) throws Exception;

	String processMobileTxnPasscodeRequest(MessageTransaction tranx) throws Exception;

	String promoteTxnState(MessageTransaction traxn, TransactionStatus status) throws Exception;

	String checkMobileProfileEligibility(String mobileNumber) throws Exception;

	List<TransactionState> getTransactionStatesByProcessTxnId(String processTxnId) throws Exception;

	TransactionState getLatestTransactionStateByProcessTxnId(String processTxnId) throws Exception;

	boolean passcodeIsValid(RequestInfo requestInfo, MessageTransaction txn) throws Exception;

	String processPasswordRetry(MessageTransaction txn) throws Exception;

	boolean customerAccountIsActive(MessageTransaction txn) throws Exception;

	TransactionState getTransactionState(TransactionStatus status, String targetMobile, String secretCode, long amount, String reference) throws Exception;

	MessageTransaction getProcessTransactionByMessageId(String messageId) throws Exception;

	List<MessageTransaction> getXLatestProcessTransactionsBySourceMobileId(int number, String sourceMobileId) throws Exception;

	String processMiniStatementRequest(MessageTransaction txn) throws Exception;

	List<MessageTransaction> getProcessTransactionBySourceMobileId(String sourceMobileId) throws Exception;

	MessageTransaction getLatestProcessTransactionBySourceMobileId(String sourceMobileId) throws Exception;

	MobileProfile getMobileProfile(@WebParam(name = "bankCode") String bankCode, @WebParam(name = "mobileNumber") String mobileNumber);

	boolean customerHasTransactedAtLeastOnce(String mobileId);

	List<MessageTransaction> getProcessTransactionBySourceMobileIdAndMessageTypeAndStatus(String sourceMobileId, TransactionType transactionType, TransactionStatus status);

	RequestInfo populateRequestInfo(MessageTransaction txn);
}
