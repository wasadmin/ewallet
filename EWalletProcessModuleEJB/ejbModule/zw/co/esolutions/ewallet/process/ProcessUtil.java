package zw.co.esolutions.ewallet.process;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.jws.WebParam;

import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountType;
import zw.co.esolutions.ewallet.bankservices.service.TransactionPostingInfo;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.enums.TxnFamily;
import zw.co.esolutions.ewallet.msg.BankRequest;
import zw.co.esolutions.ewallet.msg.BankResponse;
import zw.co.esolutions.ewallet.msg.ProcessResponse;
import zw.co.esolutions.ewallet.msg.RequestInfo;
import zw.co.esolutions.ewallet.msg.ResponseInfo;
import zw.co.esolutions.ewallet.process.model.DayEnd;
import zw.co.esolutions.ewallet.process.model.ProcessTransaction;
import zw.co.esolutions.ewallet.process.model.TransactionCharge;
import zw.co.esolutions.ewallet.process.model.TransactionState;
import zw.co.esolutions.ewallet.process.pojo.ManualPojo;
import zw.co.esolutions.ewallet.process.pojo.PostingsRequestWrapper;
import zw.co.esolutions.ewallet.process.pojo.UniversalProcessSearch;
import zw.co.esolutions.ewallet.process.util.CustomerRegResponse;
import zw.co.esolutions.ewallet.referralservices.service.ReferralStatus;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.tariffservices.service.Tariff;

@Local
public interface ProcessUtil {

	RequestInfo populateRequestWithMerchantInfo(RequestInfo info) throws Exception;

	ProcessTransaction populateProcessTransaction(RequestInfo requestInfo) throws Exception;

	RequestInfo populateRequestInfo(ProcessTransaction txn);

	
	ResponseCode postBankAccountToNonHolderTransfer(ProcessTransaction txn) throws Exception;
	
	ResponseCode postBankAccountToEWalletTransfer(ProcessTransaction txn) throws Exception;

	ResponseCode postBankAccountToBankAccountTransfer(ProcessTransaction txn) throws Exception;
	
	ResponseCode postEWalletTopupRequest(ProcessTransaction txn) throws Exception ;
	
	ResponseCode postNonHolderWithdrawal(ProcessTransaction txn) throws Exception;
	
	ResponseCode postEWalletDepositTxn(ProcessTransaction txn) throws Exception;
	
	ResponseCode postEWalletAgentDepositTxn(ProcessTransaction txn) throws Exception;
	
	ResponseCode postEWalletToNonHolderTransfer(ProcessTransaction txn) throws Exception;
	
	ResponseCode postEWalletToEWalletTransfer(ProcessTransaction txn) throws Exception;
	
	ResponseCode postEWalletAccountToBankAccountTransfer(ProcessTransaction txn) throws Exception;
	
	ResponseCode postEWalletWithdrawal(ProcessTransaction txn) throws Exception;
	
	ResponseCode postTellerFloatToEwallet(ProcessTransaction processTransaction, BankAccount sourceBankAccount, BankAccount targetBankAccount) throws Exception;
	
	ProcessTransaction postEWalletReversal(ProcessTransaction txn) throws Exception;
	
	ResponseCode postEwalletBillPayRequest(ProcessTransaction txn) throws Exception;
	
	ResponseCode postDayEndCashTendered(ProcessTransaction trxn) throws Exception;

	ResponseCode postDayEndUnderPost(ProcessTransaction trxn) throws Exception;

	ResponseCode postDayEndOverPost(ProcessTransaction trxn) throws Exception;
	
	
	ProcessTransaction createProcessTransaction(ProcessTransaction tranx) throws Exception;

	ProcessTransaction updateProcessTransaction(ProcessTransaction tranx) throws Exception;

	TransactionState updateTransactionState(TransactionState tranxState) throws Exception;

	String processMobileTxnPasscodeRequest(ProcessTransaction tranx) throws Exception;

	String processPinChangeRequest(ProcessTransaction txn, String oldPin, String newPin) throws Exception;

	ProcessTransaction promoteTxnState(ProcessTransaction txn, TransactionStatus status, String narrative) throws Exception;

	List<TransactionState> getTransactionStatesByProcessTxnId(String processTxnId) throws Exception;

	TransactionState getLatestTransactionStateByProcessTxnId(String processTxnId) throws Exception;

	boolean passcodeIsValid(RequestInfo requestInfo, ProcessTransaction txn) throws Exception;

	String processPasswordRetry(ProcessTransaction txn) throws Exception;

	boolean customerAccountIsActive(ProcessTransaction txn) throws Exception;

	TransactionState getTransactionState(TransactionStatus status, String targetMobile, String secretCode, long amount, String reference) throws Exception;

	long processEWalletBalanceRequest(ProcessTransaction txn, String bankAccountNumber) throws Exception;

	ProcessTransaction validateEWalletToNonHolderTransferReq(ProcessTransaction txn) throws Exception;

	String initializeWithdrawalDetails(RequestInfo info) throws Exception;

	String validateRegisteredWithdrawal(ProcessTransaction txn) throws Exception;

	String processWithdrawalReq(ProcessTransaction processTxn) throws Exception;

	BankRequest populateBankRequest(ProcessTransaction txn) throws Exception;

	ProcessTransaction getProcessTransactionByMessageId(String messageId) throws Exception;

	ProcessTransaction updateProcessTxnWithBankRespInfo(ProcessTransaction txn, BankResponse bankResp) throws Exception;



	List<ProcessTransaction> getXLatestProcessTransactionsByAccountNumber(int number, String accountNumber) throws Exception;

	String processMiniStatementRequest(ProcessTransaction txn) throws Exception;

	ProcessTransaction handleEwalletToEwalletTransfer(RequestInfo info) throws Exception;

//	void runEWalletsMonthlyAccountBalance(Date date);
	
	

	List<ProcessTransaction> getProcessTransactionBySourceMobileId(String sourceMobileId) throws Exception;

	ProcessTransaction getLatestProcessTransactionBySourceMobileId(String sourceMobileId) throws Exception;

	MobileProfile getMobileProfile(@WebParam(name = "mobileNumber") String mobileNumber);

	String processReferralRequest(RequestInfo info) throws Exception;

	boolean customerHasTransactedAtLeastOnce(String mobileId);

	List<ProcessTransaction> getProcessTransactionBySourceMobileIdAndTransactionTypeAndStatus(String sourceMobileId, zw.co.esolutions.ewallet.enums.TransactionType messageType, TransactionStatus status);

	BankRequest checkAndProcessPendingReferral(RequestInfo info) throws Exception;

	String promoteReferralState(String referralId, ReferralStatus status) throws Exception;

	long getCustomerRunningBalance(String mobileProfileId, BankAccountType accountType);

	String getUserName(String profileId);

	long getRunningBalance(String mobileNumber, BankAccountType accountType);

	ProcessTransaction validateBankAccountToNonHolderTransferReq(ProcessTransaction txn) throws Exception;

	ProcessTransaction validateBankAccountToEWalletTransferReq(ProcessTransaction txn) throws Exception;

	BankRequest processBankAccountBalanceRequest(ProcessTransaction txn) throws Exception;

	BankAccount getCustomerBankAccountByDeatails(String bankId, String mobileNumber) throws Exception;

	String processDepositTxnResponse(BankResponse bankResp) throws Exception;

	ProcessTransaction findProcessTransactionById(String txnId);

	ProcessTransaction validateBankAccountToBankAccountTransferReq(ProcessTransaction txn) throws Exception;

	ProcessTransaction validateEwalletAccountToBankAccountTransferReq(ProcessTransaction txn) throws Exception;

//
//	public void doDayEndBookEntry(long amount, zw.co.esolutions.ewallet.enums.BankAccountType targetAccount, TransactionType deposit, TransactionActionType actionType, String reference, String message);
//
//	void doDayEndBookEntry(DayEnd dayEnd, long amount, zw.co.esolutions.ewallet.enums.BankAccountType bankAccountType, TransactionType txnType, TransactionActionType actionType, String reference, String message) throws Exception;
//
//	ResponseCode postTopupRequest(ProcessTransaction txn) throws Exception;



	List<ProcessTransaction> getProcessTransactionsByDayEndId(DayEnd dayEnd);

	TransactionCharge createTransactionCharge(TransactionCharge transactionCharge) throws Exception;

	TransactionCharge updateTransactionCharge(TransactionCharge transactionCharge) throws Exception;

	void deleteTransactionCharge(TransactionCharge transactionCharge) throws Exception;

	TransactionCharge findtTransactionChargeById(String transactionChargeId);

	List<TransactionCharge> getTransactionChargesByMessageId(String messageId);

	List<TransactionCharge> getTransactionChargeByProcessTransactionId(String processTransactionId);

	List<TransactionCharge> getTransactionChargeByTariffId(String tariffId);

	ResponseCode postCustomerDepositByAgentTxns(ProcessTransaction txn) throws Exception;

	ResponseCode postCustomerWidthdrawalByAgentTxns(ProcessTransaction txn) throws Exception;

	ResponseCode postTrasnferEmoneyToSubAgentTxns(ProcessTransaction txn) throws Exception;

	ResponseCode postAgentWidthdrawTxns(ProcessTransaction txn) throws Exception;

	// ResponseCode postAgentCommissionTxns(ProcessTransaction txn) throws
	// Exception;

	ResponseCode postAgentRegistersCustomerTxns(ProcessTransaction txn) throws Exception;

	ResponseCode postAgentRegistersSubAgentTxns(ProcessTransaction txn) throws Exception;

	ProcessTransaction addApplicableTariffToProcessTxn(ProcessTransaction txn) throws Exception;

	ResponseCode processSweepAgentCommission(RequestInfo requestInfo) throws Exception;

	ProcessTransaction addApplicableAgentCommissionToProcessTxn(ProcessTransaction txn, String agentMobileProfileId) throws Exception;

	CustomerRegResponse processCustomerMerchatRegistration(ProcessTransaction txn) throws Exception;

	ProcessTransaction validateBillPayRequest(ProcessTransaction txn, BankAccount bankAccount) throws Exception;

	ProcessTransaction validateEWalletToEWalletTransfer(ProcessTransaction txn) throws Exception;

	ProcessTransaction validateTopupRequest(ProcessTransaction txn, BankAccount bankAccount) throws Exception;

	boolean checkIfProcessTransactionsSuccessful(DayEnd dayEnd);

	ProcessTransaction populateProcessTransactionWithManualInfor(ManualPojo manual) throws Exception;

	String generateReference(TransactionType txnType);

	BankRequest populateAdjustmentBankRequest(ProcessTransaction txn, boolean isReversal) throws Exception;

	BankResponse waitForBankResponse(BankRequest bankRequest) throws Exception;

	TransactionCharge populateTransactionCharge(ProcessTransaction txn, ManualPojo manual) throws Exception;

	ResponseCode rollbackEWalletAndEQ3Postings(ProcessTransaction txn) throws Exception;

	ProcessTransaction updateProcessTransactionWith(String processTxnId, String responseCode, Date valueDate, String narrative) throws Exception;

	DayEnd editTellerDayEnd(DayEnd dayEnd, String userName);

	ProcessTransaction populateEquationAccoountsByTransactionType(ProcessTransaction tranx) throws Exception;
	
	ResponseCode postAdjustmentTxnsToEwallet(ManualPojo manual, ProcessTransaction txn) throws Exception; 

	long getDailyAmountByCustomerIdAndTxnType(String customerId, zw.co.esolutions.ewallet.enums.TransactionType txnType);
	
	List<ProcessTransaction> getProcessTransactionsNotCompletedByOldMessageId(ManualPojo manual); 

	public List<ProcessTransaction> getProcessTransactionsByAllAttributes(UniversalProcessSearch universal);
	
	boolean checkTellerDayEndsPendingApproval(String tellerId) ;
	
	List<ProcessTransaction> getStartOfDayTxnByProfileIdAndDayEndSummaryAndStatus(String profileId, TransactionStatus awaiting_approval);
	
	List<ProcessTransaction> getStartDayTxnsByTellerAndDateRangeAndStatus(String tellerId, Date fromDate, Date toDate) throws Exception ;
	
	List<ProcessTransaction> getStartOfDayTransactionByTransactionTypeAndTellerAndDateRange(zw.co.esolutions.ewallet.enums.TransactionType transactionType, String tellerId, Date fromDate, Date toDate, TransactionStatus txnStatus) throws Exception ;
	
	List<ProcessTransaction> getStartOfDayTransactionByTransactionTypeAndBranchAndStatus(zw.co.esolutions.ewallet.enums.TransactionType transactionType, String branchId, TransactionStatus txnStatus) throws Exception;

	 
	
	long getTotalAmountByApplicableParameters(Date fromDate, Date toDate, TransactionStatus status, zw.co.esolutions.ewallet.enums.TransactionType txnType, TxnFamily txnFamily, String tellerId, String bankId, String branchId);
	
	List<ProcessTransaction> getProcessTransactionsByApplicableParameters(Date fromDate, Date toDate, TransactionStatus status, zw.co.esolutions.ewallet.enums.TransactionType txnType, TxnFamily txnFamily, String tellerId, String bankId, String branchId) ;

	long getTellerSubTotalByTransactionTypeAndDateRangeAndStatus(String profileId, Date fromDate, Date toDate, zw.co.esolutions.ewallet.enums.TransactionType withdrawalNonholder);
	
	boolean checkIfStartofDayExistsAndApproved(String profileId, Date toDay) ;
	
	void runDailyNHCollectionReversal(Date date,String recipient);

	void rollbackEQ3Postings(ProcessTransaction txn) throws Exception;
	
//	void runExpiredTxnReversal(Date date);

	List<ProcessTransaction> getTimedOutTxns(Date date);
	
	PostingsRequestWrapper populateReversalPostingsResponse(
			ProcessTransaction txn);
	
	public ProcessTransaction updateFailedEwalletTxn(ProcessTransaction txn, String narrative,TransactionStatus status) throws Exception;
		
	ProcessTransaction updateDayEndTxnState(ProcessTransaction trxn,
			TransactionStatus status, String string) throws Exception;

	List<TransactionPostingInfo> populateTransactionPostingInfoReversal(
			ProcessTransaction txn);

	ProcessTransaction handleAgentCustomerDepositRequest(RequestInfo info)
			throws Exception;

	ProcessTransaction validateAgentCustomerDepositRequest(
			ProcessTransaction txn) throws Exception;

	ResponseCode postAgentCustomerDeposit(ProcessTransaction txn)
			throws Exception;
			
	ProcessTransaction updateAdjustmentTxnState(ProcessTransaction txn,
			TransactionStatus status, String narrative) throws Exception;
	
	ProcessTransaction handleAgentCustomerWithdrawalRequest(RequestInfo info)
			throws Exception;

	ProcessTransaction validateAgentCustomerWithdrawalRequest(
			ProcessTransaction txn) throws Exception;

	ResponseCode postAgentCustomerWithdrawal(ProcessTransaction txn)
			throws Exception;

	ProcessTransaction handleAgentCustomerNonHolderWithdrawalRequest(
			RequestInfo info, ProcessTransaction txn) throws Exception;

	ProcessTransaction validateAgentCustomerNonHolderWithdrawalRequest(
			ProcessTransaction txn) throws Exception;

	ResponseCode postAgentCustomerNonHolderWithdrawal(ProcessTransaction txn)
			throws Exception;

	ProcessResponse validateOriginalNonHolderTransfer(RequestInfo info);

	ProcessTransaction validateAgentEwalletAccountToBankAccountTransferReq(
			ProcessTransaction txn) throws Exception;

	ResponseCode postAgentEWalletAccountToBankAccountTransfer(
			ProcessTransaction txn) throws Exception;

	long getTariffAmount(Tariff tariff, long amount) throws Exception;
	
	void runAgentCommsionSweep(String recipient);
	
	ResponseCode postAgentCommissionSweep(ProcessTransaction txn) throws Exception;
	
	void sendReport(String recipients , String subject , String title , String txn,String heading);
	
	public boolean submitRequest(BankRequest bankRequest, boolean reversal);
	
	public boolean submitResponse(ResponseInfo responseInfo) ;
}
