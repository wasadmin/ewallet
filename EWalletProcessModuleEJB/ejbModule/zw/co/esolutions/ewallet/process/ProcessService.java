package zw.co.esolutions.ewallet.process;

import java.util.Date;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import zw.co.esolutions.ewallet.enums.DayEndStatus;
import zw.co.esolutions.ewallet.enums.TransactionLocationType;
import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.enums.TxnFamily;
import zw.co.esolutions.ewallet.msg.DayEndApprovalResponse;
import zw.co.esolutions.ewallet.msg.ProcessResponse;
import zw.co.esolutions.ewallet.msg.RequestInfo;
import zw.co.esolutions.ewallet.process.model.DayEnd;
import zw.co.esolutions.ewallet.process.model.DayEndResponse;
import zw.co.esolutions.ewallet.process.model.DayEndSummary;
import zw.co.esolutions.ewallet.process.model.ProcessTransaction;
import zw.co.esolutions.ewallet.process.model.TransactionCharge;
import zw.co.esolutions.ewallet.process.pojo.ManualPojo;
import zw.co.esolutions.ewallet.process.pojo.ManualReturn;
import zw.co.esolutions.ewallet.process.pojo.UniversalProcessSearch;
import zw.co.esolutions.ewallet.process.util.DepositInfo;
import zw.co.esolutions.ewallet.process.util.NonHolderWithdrawalInfo;

@WebService(name = "ProcessService")
public interface ProcessService {

	ProcessResponse depositCash(@WebParam(name = "depositInfo") DepositInfo depositInfo);

	ProcessTransaction getProcessTransaction(@WebParam(name = "info") NonHolderWithdrawalInfo info) throws Exception;

	ProcessResponse confirmNonHolderWithdrawal(@WebParam(name = "txnRefId") String txnRefId, @WebParam(name = "profileId") String profileId);

	List<ProcessTransaction> getProcessTransactionsWithinDateRange(@WebParam(name = "fromDate") Date fromDate, @WebParam(name = "toDate") Date toDate, @WebParam(name = "status") TransactionStatus status);

	List<ProcessTransaction> getProcessTransactionsWithinDateRangeByMsgType(@WebParam(name = "fromDate") Date fromDate, @WebParam(name = "toDate") Date toDate, @WebParam(name = "msgType") TransactionType msgType, @WebParam(name = "status") TransactionStatus status);

	List<ProcessTransaction> getProcessTransactionsWithinDateRangeByBankId(@WebParam(name = "fromDate") Date fromDate, @WebParam(name = "toDate") Date toDate, @WebParam(name = "bankId") String bankId, @WebParam(name = "status") TransactionStatus status);

	List<ProcessTransaction> getProcessTransactionsWithinDateRangeByMsgTypeByBankId(@WebParam(name = "fromDate") Date fromDate, @WebParam(name = "toDate") Date toDate, @WebParam(name = "msgType") TransactionType msgType, @WebParam(name = "bankId") String bankId, @WebParam(name = "status") TransactionStatus status);

	List<ProcessTransaction> getProcessTransactionsWithinDateRangeByBankIdAndBranchId(@WebParam(name = "fromDate") Date fromDate, @WebParam(name = "toDate") Date toDate, @WebParam(name = "bankId") String bankId, @WebParam(name = "branchId") String branchId, @WebParam(name = "status") TransactionStatus status);

	List<ProcessTransaction> getProcessTransactionsWithinDateRangeByMsgTypeByBankIdAndBranchId(@WebParam(name = "fromDate") Date fromDate, @WebParam(name = "toDate") Date toDate, @WebParam(name = "msgType") TransactionType msgType, @WebParam(name = "bankId") String bankId, @WebParam(name = "branchId") String branchId, @WebParam(name = "status") TransactionStatus status);

	DayEnd runTellerDayEnd(@WebParam(name = "dayEnd") DayEnd dayEnd, @WebParam(name = "userName") String userName) throws Exception;

	List<ProcessTransaction> getTellerDayEnd(@WebParam(name = "bankName") String bankName, @WebParam(name = "profileId") String profileId, @WebParam(name = "txnDate") Date txnDate, @WebParam(name = "msgType") TransactionType msgType);

	long getTellerDayEndTotal(@WebParam(name = "bankName") String bankName, @WebParam(name = "profileId") String profileId, @WebParam(name = "txnDate") Date txnDate, @WebParam(name = "msgType") TransactionType msgType);

	String validateEwalletHolderWithdrawal(@WebParam(name = "sourceMobileId") String sourceMobileId, @WebParam(name = "amount") long amount, @WebParam(name = "bankId") String bankId, @WebParam(name = "locationType") TransactionLocationType locationType) throws Exception;

	ProcessResponse processEwalletWithdrawal(@WebParam(name = "requestInfo") RequestInfo info);

	ProcessTransaction getProcessTransactionByMessageId(@WebParam(name = "messageId") String messageId) throws Exception;

	List<ProcessTransaction> getProcessTransactionsByTellerID(@WebParam(name = "tellerId") String tellerId);

	List<ProcessTransaction> getProcessTransactionsByTellerIdAndDateRange(@WebParam(name = "fromDate") Date fromDate, @WebParam(name = "toDate") Date toDate, @WebParam(name = "tellerId") String tellerId);

	List<ProcessTransaction> getProcessTransactionsByTransactionStatus(@WebParam(name = "status") TransactionStatus tranStatus);

	List<ProcessTransaction> getProcessTransactionByTellerIdAndDateRangeAndStatus(@WebParam(name = "fromDate") Date fromDate, @WebParam(name = "toDate") Date toDate, @WebParam(name = "tellerId") String tellerId, @WebParam(name = "status") TransactionStatus tranStatus);

	List<ProcessTransaction> getPocessTransactionsByDateRangeAndStatus(@WebParam(name = "fromDate") Date fromDate, @WebParam(name = "toDate") Date toDate, @WebParam(name = "status") TransactionStatus tranStatus);

	List<ProcessTransaction> getProcessTransactionsByBranch(@WebParam(name = "branch") String branchId);

	List<ProcessTransaction> getProcessTransactionsByBranchAndDateRangeAndStatus(@WebParam(name = "fromDate") Date fromDate, @WebParam(name = "toDate") Date toDate, @WebParam(name = "branch") String branchId, @WebParam(name = "status") TransactionStatus tranStatus);

	List<ProcessTransaction> getProcessTransactionsByTellerIdAndDayEndSummary(@WebParam(name = "tellerId") String tellerId, @WebParam(name = "dayEndSummaryId") String dayEndSummaryId);

	List<DayEnd> getDayEndByTellerIdAndDateCreatedAndStatus(@WebParam(name = "tellerId") String tellerId, @WebParam(name = "dateCreated") Date dateCreated, @WebParam(name = "status") TransactionStatus tranStatus);

	public DayEnd createDayEnd(@WebParam(name = "dayEnd") DayEnd dayEnd, @WebParam(name = "userName") String userName) throws Exception;

	public DayEnd editDayEnd(@WebParam(name = "dayEnd") DayEnd dayEnd, @WebParam(name = "userName") String userName) throws Exception;

	public DayEndSummary createDayEndSummary(@WebParam(name = "summary") DayEndSummary summary, @WebParam(name = "userName") String userName) throws Exception;

	public DayEnd findDayEndById(@WebParam(name = "dayEndId") String dayEndId, @WebParam(name = "userName") String userName);

	public DayEndSummary editDayEndSummary(@WebParam(name = "summary") DayEndSummary summary, @WebParam(name = "userName") String userName) throws Exception;

	public DayEndSummary findDayEndSummary(@WebParam(name = "id") String id, @WebParam(name = "userName") String userName);

	public DayEnd approveDayEnd(@WebParam(name = "dayEnd") DayEnd dayEnd, @WebParam(name = "userName") String userName) throws Exception;

	public DayEnd disapproveDayEnd(@WebParam(name = "dayEnd") DayEnd dayEnd, @WebParam(name = "userName") String userName) throws Exception;

	public List<DayEnd> getDayEndByDayEndStatus(@WebParam(name = "dayEndStatus") DayEndStatus dayEndStatus, @WebParam(name = "userName") String userName);

	// public List<DayEnd>
	// getDayEndByDayEndStatusAndDateRange(@WebParam(name="dayEndStatus")DayEndStatus
	// dayEndStatus,@WebParam(name="fromDate")Date fromDate,
	// @WebParam(name="toDate")Date toDate, @WebParam(name="userName")String
	// userName);
	public List<DayEnd> getDayEndsByDayEndStatusAndDateRangeAndBranch(@WebParam(name = "fromDate") Date fromDate, @WebParam(name = "toDate") Date toDate, @WebParam(name = "dayEndStatus") DayEndStatus dayEndStatus, @WebParam(name = "branchId") String branchId) throws Exception;

	public List<DayEnd> getDayEndsByDayEndStatusAndDateRangeAndTeller(@WebParam(name = "fromDate") Date fromDate, @WebParam(name = "toDate") Date toDate, @WebParam(name = "dayEndStatus") DayEndStatus dayEndStatus, @WebParam(name = "tellerid") String tellerId) throws Exception;

	public List<DayEndSummary> getDayEndSummaryByDayEndId(@WebParam(name = "dayEndId") String dayEndId, @WebParam(name = "userName") String userName) throws Exception;

	boolean isTodaysDayEndRun(@WebParam(name = "tellerId") String tellerId, @WebParam(name = "dayEndDate") Date dayEndDate);

	DayEndResponse isPreviousDayEndRun(@WebParam(name = "profileId") String profileId, @WebParam(name = "dayEndDate") Date dayEndDate);

	List<DayEnd> getDayEndByDayEndStatusAndBranch(DayEndStatus dayEndStatus, String userName);

	DayEndResponse checkIfThereAreTrxnTomark(@WebParam(name = "userName") String profileId, @WebParam(name = "dayEndDate") Date dayEndDate) throws Exception;

	public List<DayEndSummary> getDayEndSummariesByDayEnd(@WebParam(name = "dayEndId") String dayEndId) throws Exception;

	ProcessTransaction processEquationPosting(@WebParam(name = "dayEnd") DayEnd dayEnd, @WebParam(name = "processTxn") ProcessTransaction processTxn, @WebParam(name = "userName") String userName);

	DayEnd processDayEndBookEntries(@WebParam(name = "dayEnd") DayEnd dayEnd, @WebParam(name = "userName") String userName) throws Exception;

	ProcessTransaction createProcessTransaction(@WebParam(name = "processTxn") ProcessTransaction tranx, @WebParam(name = "userName") String userName) throws Exception;

	ProcessTransaction updateProcessTxn(@WebParam(name = "processTxn") ProcessTransaction processTxn,@WebParam(name = "userName") String userName) throws Exception;

	List<ProcessTransaction> getProcessTransactionsByDayEndId(DayEnd dayEnd);

	void deleteProcessTransactionsByDayEnd(@WebParam(name = "dayEnd") DayEnd dayEnd, String userName) throws Exception;

	List<ProcessTransaction> getProcessTransactionsByApplicableParameters(@WebParam(name = "fromDate") Date fromDate, @WebParam(name = "toDate") Date toDate, @WebParam(name = "status") TransactionStatus status, @WebParam(name = "txnType") TransactionType txnType, @WebParam(name = "txnFamily") TxnFamily txnFamily, @WebParam(name = "tellerId") String tellerId, @WebParam(name = "bankId") String bankId, @WebParam(name = "branchId") String branchId);

	long getTotalAmountByApplicableParameters(@WebParam(name = "fromDate") Date fromDate, @WebParam(name = "toDate") Date toDate, @WebParam(name = "status") TransactionStatus status, @WebParam(name = "txnType") TransactionType txnType, @WebParam(name = "txnFamily") TxnFamily txnFamily, @WebParam(name = "tellerId") String tellerId, @WebParam(name = "bankId") String bankId, @WebParam(name = "branchId") String branchId);

	String validateDeposit(@WebParam(name = "sourceMobileId") String sourceMobileId, @WebParam(name = "amount") long amount, @WebParam(name = "bankId") String bankId, @WebParam(name = "locationType") TransactionLocationType locationType) throws Exception;

	long getDailyAmountByCustomerIdAndTxnType(@WebParam(name = "customerId")String customerId, @WebParam(name = "txnType")TransactionType txnType);
	
	ProcessTransaction createStartOfDayTransaction(@WebParam(name = "processTransaction")ProcessTransaction processTransaction , @WebParam(name = "username")String username) throws Exception;
	
	long getTellerNetCashPosition(@WebParam(name="profileId") String profileId, @WebParam(name="date")Date date) throws Exception;
	
	ProcessTransaction findProcessTransactionById(@WebParam(name="transactionId")String transactionId) throws Exception;
	
	List<ProcessTransaction> getStartOfDayTransactionByTransactionTypeAndBranchAndStatus(@WebParam(name="transactionType")TransactionType transactionType, @WebParam(name="branchId")String branchId, @WebParam(name="txnStatus")TransactionStatus txnStatus) throws Exception;

	ProcessTransaction approveTellerDailyFloat(@WebParam(name="transactionId")String transactionId, @WebParam(name="username")String username) throws Exception;
	
	ProcessTransaction disapproveTellerDailyFloat(@WebParam(name="transactionId")String transactionId, @WebParam(name="username")String username) throws Exception;
	
	List<ProcessTransaction> getStartOfDayTransactionByTransactionTypeAndTellerAndDateRange(@WebParam(name="transactionType")TransactionType transactionType, @WebParam(name="tellerId")String tellerId, @WebParam(name="startDate")Date startDate, @WebParam(name="endDate")Date endDate,@WebParam(name="txnStatus") TransactionStatus txnStatus) throws Exception;

	List<ProcessTransaction> getStartDayTxnsByTellerAndDateRangeAndStatus(@WebParam(name="tellerId")String tellerId, @WebParam(name="fromDate")Date fromDate, @WebParam(name="toDate")Date toDate) throws Exception;
	
	String canTellerMakeTransact(@WebParam(name="profileId")String profileId,@WebParam(name="toDay")Date toDay,@WebParam(name="trxType")TransactionType txnType);

	List<ProcessTransaction> getStartOfDayTxnByProfileIdAndDayEndSummaryAndStatus(String profileId, TransactionStatus awaiting_approval);

	boolean checkIfAnyFloatsPendingApproval(@WebParam(name="profileId")String profileId);

	boolean checkTellerDayEndsPendingApproval(@WebParam(name="profileId")String tellerId);
	
	List<ProcessTransaction> getProcessTransactionsByAllAttributes(@WebParam(name = "universalProcessPojo")
			UniversalProcessSearch universal);
	
	String reverseTransaction(@WebParam(name = "manualPojo")ManualPojo manual) throws Exception;

	String completeTransaction(@WebParam(name = "manualPojo")ManualPojo manual) throws Exception;

	ManualReturn manualResolve(@WebParam(name = "manualPojo")ManualPojo manual) throws Exception;

	String confirmManualResolve(@WebParam(name = "manualPojo")ManualPojo manual) throws Exception;
	
	List<TransactionCharge> getTransactionChargeByProcessTransactionId(@WebParam(name = "processTxnId")String processTxnId);
	public String scheduleTimer(@WebParam(name = "dayOfMonth")String dayOfMonth, @WebParam(name = "hour")String hour, @WebParam(name= "minutes")String min, @WebParam(name = "isForThisMonth")boolean isForThisMonth, @WebParam(name = "runDate")Date runDate);
	public DayEndApprovalResponse processDayEndApproval( @WebParam(name = "dayEnd")DayEnd dayEnd, @WebParam(name = "userName")String userName) throws Exception;

	ProcessTransaction approveMarkedTransaction(String messageId, TransactionStatus newStatus, String narrative, String userName) throws Exception;
	DayEnd updateDayEnd( @WebParam(name = "dayEnd")DayEnd dayEnd, @WebParam(name = "userName")String userName) throws Exception;
	public String scheduleCollectionTimer(@WebParam(name = "runDate")Date runDate);
	
	String  cancellCollectionTimer();
	
	String  saveCollectionConfigurationData(@WebParam(name = "email")String email , @WebParam(name = "timeToRun")String timeToRun ,@WebParam(name = "expPeriod") String expPeriod); 
	
	String  saveTxnReversalConfigData(@WebParam(name = "runningTime")String runningTime ,@WebParam(name = "expPeriod")String expPeriod);
	
	String  scheduleTxnReversalTimer(@WebParam(name = "runDate")Date runDate);
	
	String  cancellTxnReversalTimer();
	
	String validateAgentDeposit(@WebParam(name = "sourceMobileId") String sourceMobileId, @WebParam(name = "amount") long amount, @WebParam(name = "bankId") String bankId, @WebParam(name = "locationType") TransactionLocationType locationType) throws Exception;
	
	ProcessResponse depositAgentCash(@WebParam(name = "depositInfo") DepositInfo depositInfo);
	
	String  cancellCommissionTimer();
	
	String saveCommissionConfigData(@WebParam(name = "email")String email , @WebParam(name = "timeToRun")String timeToRun ,@WebParam(name = "expPeriod") String period);
	
	String scheduleCommissionTimer(@WebParam(name = "runDate")Date runDate);
	
	ProcessResponse processAlertRegistration(String bankAccountId, String status);
	
}
