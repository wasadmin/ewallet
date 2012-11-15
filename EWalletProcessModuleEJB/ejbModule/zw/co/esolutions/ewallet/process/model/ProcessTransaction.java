package zw.co.esolutions.ewallet.process.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import zw.co.esolutions.ewallet.enums.TransactionLocationType;
import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.enums.TransferType;
import zw.co.esolutions.ewallet.enums.TxnSuperType;

@Entity    
@NamedQueries({@NamedQuery(name="getProcessTransaction", query = "SELECT p FROM ProcessTransaction p"),@NamedQuery(name="getProcessTransactionById", query = "SELECT p FROM ProcessTransaction p WHERE p.id = :id"),
@NamedQuery(name="getProcessTransactionByMessageId", query = "SELECT p FROM ProcessTransaction p WHERE p.messageId = :messageId"),
@NamedQuery(name="getProcessTransactionByMessageType", query = "SELECT p FROM ProcessTransaction p WHERE p.transactionType = :transactionType"),
@NamedQuery(name="getProcessTransactionBySourceMobile", query = "SELECT p FROM ProcessTransaction p WHERE p.sourceMobile = :sourceMobile ORDER BY p.dateCreated DESC"),
@NamedQuery(name="getProcessTransactionByTargetMobile", query = "SELECT p FROM ProcessTransaction p WHERE p.targetMobile = :targetMobile"),
@NamedQuery(name="getProcessTransactionByAmount", query = "SELECT p FROM ProcessTransaction p WHERE p.amount = :amount"),
@NamedQuery(name="getProcessTransactionByBalance", query = "SELECT p FROM ProcessTransaction p WHERE p.balance = :balance"),
@NamedQuery(name="getProcessTransactionByResponseCode", query = "SELECT p FROM ProcessTransaction p WHERE p.responseCode = :responseCode"),
@NamedQuery(name="getProcessTransactionByValueDate", query = "SELECT p FROM ProcessTransaction p WHERE p.valueDate = :valueDate"),
@NamedQuery(name="getProcessTransactionByBankReference", query = "SELECT p FROM ProcessTransaction p WHERE p.bankReference = :bankReference"),
@NamedQuery(name="getProcessTransactionByPasswordRetryCount", query = "SELECT p FROM ProcessTransaction p WHERE p.passwordRetryCount = :passwordRetryCount"),
@NamedQuery(name="getProcessTransactionByTimeout", query = "SELECT p FROM ProcessTransaction p WHERE p.timeout = :timeout"),
@NamedQuery(name="getProcessTransactionByPasscodePrompt", query = "SELECT p FROM ProcessTransaction p WHERE p.passcodePrompt = :passcodePrompt"),
@NamedQuery(name="getProcessTransactionBySecretCode", query = "SELECT p FROM ProcessTransaction p WHERE p.secretCode = :secretCode"),
@NamedQuery(name="getProcessTransactionByNarrative", query = "SELECT p FROM ProcessTransaction p WHERE p.narrative = :narrative"),
@NamedQuery(name="getProcessTransactionByReversalReason", query = "SELECT p FROM ProcessTransaction p WHERE p.reversalReason = :reversalReason"),
@NamedQuery(name="getProcessTransactionByUtilityAccount", query = "SELECT p FROM ProcessTransaction p WHERE p.utilityAccount = :utilityAccount"),
@NamedQuery(name="getProcessTransactionByUtilityName", query = "SELECT p FROM ProcessTransaction p WHERE p.utilityName = :utilityName"),
@NamedQuery(name="getProcessTransactionByDateCreated", query = "SELECT p FROM ProcessTransaction p WHERE p.dateCreated = :dateCreated"),
@NamedQuery(name="getProcessTransactionByVersion", query = "SELECT p FROM ProcessTransaction p WHERE p.version = :version"),
@NamedQuery(name="getProcessTransactionByStatus", query = "SELECT p FROM ProcessTransaction p WHERE p.status = :status"),
@NamedQuery(name="getNonHolderTxfDueForReversal", query = "SELECT p FROM ProcessTransaction p WHERE p.status = :status AND p.dateCreated <= :date "),
@NamedQuery(name="getExpiredTxnForReversal", query = "SELECT p FROM ProcessTransaction p WHERE p.status = :status AND p.dateCreated <= :date "),
@NamedQuery(name="getXLatestProcessTransactionByAccountNumber", query = "SELECT p FROM ProcessTransaction p WHERE (p.sourceAccountNumber = :accountNumber OR p.destinationAccountNumber = :accountNumber) AND (p.transactionType <> :balance AND p.transactionType <> :mini) AND (p.status = :statusCompleted OR p.status = :statusAwaitingCollection) ORDER BY p.dateCreated DESC"),
@NamedQuery(name="getProcessTransactionsWithinDateRange", query = "SELECT p FROM ProcessTransaction p WHERE p.dateCreated >= :fromDate AND p.dateCreated <= :toDate AND p.status = :status ORDER BY p.dateCreated DESC"),



@NamedQuery(name="getBankProcessTransactionsWithinDateRange", query = "SELECT p FROM ProcessTransaction p WHERE p.dateCreated >= :fromDate AND p.dateCreated <= :toDate ORDER BY p.dateCreated DESC"),

@NamedQuery(name="getBankProcessTransactionsWithinDateRangeByMsgType", query = "SELECT p FROM ProcessTransaction p WHERE p.dateCreated >= :fromDate AND p.dateCreated <= :toDate  AND p.transactionType = :transactionType ORDER BY p.dateCreated DESC "),

@NamedQuery(name="getBankProcessTransactionsWithinDateRangeByBankId", query = "SELECT p FROM ProcessTransaction p WHERE p.dateCreated >= :fromDate AND p.dateCreated <= :toDate  AND p.fromBankId = :bankId ORDER BY p.dateCreated DESC"),

@NamedQuery(name="getBankProcessTransactionsWithinDateRangeByMsgTypeByBankId", query = "SELECT p FROM ProcessTransaction p WHERE p.dateCreated >= :fromDate AND p.dateCreated <= :toDate AND p.transactionType = :transactionType AND p.fromBankId = :bankId ORDER BY p.dateCreated DESC "),


@NamedQuery(name="getBankProcessTransactionsWithinDateRangeByMsgTypeByBankIdAndBranchId", query = "SELECT p FROM ProcessTransaction p WHERE p.dateCreated >= :fromDate AND p.dateCreated <= :toDate  AND p.transactionType = :transactionType AND p.fromBankId = :bankId AND p.transactionLocationId = :branchId ORDER BY p.dateCreated DESC "),

@NamedQuery(name="getBankProcessTransactionsWithinDateRangeByBankIdAndBranchId", query = "SELECT p FROM ProcessTransaction p WHERE p.dateCreated >= :fromDate AND p.dateCreated <= :toDate  AND p.fromBankId = :bankId and p.transactionLocationId = :branchId ORDER BY p.dateCreated DESC"),


@NamedQuery(name="getProcessTransactionsWithinDateRangeByBankId", query = "SELECT p FROM ProcessTransaction p WHERE p.dateCreated >= :fromDate AND p.dateCreated <= :toDate AND p.status = :status AND p.fromBankId = :bankId ORDER BY p.dateCreated DESC"),
@NamedQuery(name="getProcessTransactionsWithinDateRangeByBankIdAndBranchId", query = "SELECT p FROM ProcessTransaction p WHERE p.dateCreated >= :fromDate AND p.dateCreated <= :toDate AND p.status = :status AND p.fromBankId = :bankId and p.transactionLocationId = :branchId ORDER BY p.dateCreated DESC"),
@NamedQuery(name="getProcessTransactionsWithinDateRangeByMsgType", query = "SELECT p FROM ProcessTransaction p WHERE p.dateCreated >= :fromDate AND p.dateCreated <= :toDate AND p.status = :status AND p.transactionType = :transactionType ORDER BY p.dateCreated DESC "),
@NamedQuery(name="getProcessTransactionsWithinDateRangeByMsgTypeByBankId", query = "SELECT p FROM ProcessTransaction p WHERE p.dateCreated >= :fromDate AND p.dateCreated <= :toDate AND p.status = :status AND p.transactionType = :transactionType AND p.fromBankId = :bankId ORDER BY p.dateCreated DESC "),
@NamedQuery(name="getProcessTransactionsWithinDateRangeByMsgTypeByBankIdAndBranchId", query = "SELECT p FROM ProcessTransaction p WHERE p.dateCreated >= :fromDate AND p.dateCreated <= :toDate AND p.status = :status AND p.transactionType = :transactionType AND p.fromBankId = :bankId AND p.transactionLocationId = :branchId ORDER BY p.dateCreated DESC "),
@NamedQuery(name="getTellerDayEnd", query = "SELECT p FROM ProcessTransaction p WHERE p.dateCreated >= :fromDate AND p.dateCreated <= :toDate AND p.status = :status AND p.transactionType = :transactionType AND p.fromBankId = :bankId AND p.profileId = :profileId ORDER BY p.dateCreated DESC "),
@NamedQuery(name="getTellerDailyTransactions", query = "SELECT p FROM ProcessTransaction p WHERE p.dateCreated >= :fromDate AND p.dateCreated <= :toDate AND p.transactionType = :transactionType AND p.profileId = :profileId ORDER BY p.dateCreated DESC "),
@NamedQuery(name="getTellerDayEndTotal", query = "SELECT SUM(p.amount) FROM ProcessTransaction p WHERE p.dateCreated >= :fromDate AND p.dateCreated <= :toDate AND p.status = :status AND p.transactionType = :transactionType AND p.fromBankId = :bankId AND p.profileId = :profileId"),


@NamedQuery(name="getTellerTransactionTotal", query = "SELECT SUM(p.amount) FROM ProcessTransaction p WHERE p.dateCreated >= :fromDate AND p.dateCreated <= :toDate AND p.transactionType = :transactionType AND p.profileId = :profileId"),
@NamedQuery(name="getStartOfDayTxnByProfileIdAndStatusAndDayEndSummary",query="SELECT p FROM ProcessTransaction p WHERE p.dayEndSummary IS NULL AND p.status = :statusAwaitingApproval AND p.transactionType = :transactionType AND p.profileId = :profileId"),
@NamedQuery(name="getStartOfDayTxnByProfileIdAndStatusAndDateAndDayEndSummary",query="SELECT p FROM ProcessTransaction p WHERE p.dayEndSummary IS NULL  AND p.dateCreated >= :fromDate AND p.dateCreated <= :toDate AND (p.status = :statusBankReq OR p.status = :statusManResolve OR p.status = :statusCompl) AND p.transactionType = :transactionType AND p.profileId = :profileId"),
@NamedQuery(name="getProcessTransactionBySourceMobileId", query = "SELECT p FROM ProcessTransaction p WHERE p.sourceMobileId = :sourceMobileId ORDER BY p.dateCreated DESC"),
@NamedQuery(name="getProcessTransactionByTargetMobileId", query = "SELECT p FROM ProcessTransaction p WHERE p.targetMobileId = :targetMobileId"),
@NamedQuery(name="getPocessTransactionsByDateRangeAndStatus",query="SELECT p FROM ProcessTransaction p WHERE p.dateCreated >= :fromDate AND p.dateCreated <= :toDate AND p.status = :status ORDER BY p.dateCreated DESC "),
@NamedQuery(name="getProcessTransactionsByBranch",query="SELECT p FROM ProcessTransaction p WHERE p.branchId= :branchId ORDER BY p.dateCreated DESC"),
@NamedQuery(name="getProcessTransactionsByTellerIdAndDateRange", query="SELECT p FROM ProcessTransaction p WHERE  p.dateCreated >= :fromDate AND p.dateCreated <= :toDate AND p.profileId = :tellerId ORDER BY p.dateCreated DESC"),
@NamedQuery(name="getProcessTransactionByTellerIdAndDateRangeAndStatus", query="SELECT p FROM ProcessTransaction p WHERE p.profileId= :tellerId AND p.dateCreated >= :fromDate AND p.dateCreated <= :toDate AND p.status = :status ORDER BY BY p.dateCreated DESC"),
@NamedQuery(name="getProcessTransactionsByTellerIdAndDayEndSummary", query="SELECT p FROM ProcessTransaction p WHERE p.profileId =:tellerId AND p.dayEndSummary.id= :dayEndSummaryId ORDER BY p.dateCreated DESC"),
@NamedQuery(name="getProcessTransactionBySourceMobileIdAndMessageTypeAndStatus", query = "SELECT p FROM ProcessTransaction p WHERE p.sourceMobileId = :sourceMobileId AND p.transactionType = :transactionType AND p.status = :status"),
@NamedQuery(name="getProcessTransactionsByBranchAndDateRangeAndStatus",query="SELECT p FROM ProcessTransaction p WHERE p.branchId= :branchId AND p.dateCreated >= :fromDate AND p.dateCreated <= :toDate AND p.status =:status"),
@NamedQuery(name="getProcessTransactionsByTellerID",query="SELECT p FROM ProcessTransaction p WHERE p.profileId= :tellerId"),
@NamedQuery(name="getProcessTransactionsByDayEndId",query="SELECT p FROM ProcessTransaction p WHERE p.dayEndId= :dayEndId AND (p.transactionType =:dayEndReceipts OR p.transactionType = :dayEndPayOuts OR p.transactionType = :dayEndUnderPost OR p.transactionType = :dayEndOverPost OR p.transactionType = :dayEndFloats OR p.transactionType = :dayEndCashTendered) "),
@NamedQuery(name="getProcessTransactionsByTransactionStatus",query="SELECT p FROM ProcessTransaction p WHERE p.status = :status"),
@NamedQuery(name="getTransactionByTellerIdAndDayEndSummary",query="SELECT p FROM ProcessTransaction p WHERE p.dayEndSummary.id =:dayEndSummary AND p.profileId =:tellerId"),
@NamedQuery(name="getProcessTransactionsByNullDayEndSummaryAndDate",query="SELECT p FROM ProcessTransaction p WHERE p.dayEndSummary IS NULL and p.dateCreates=:dayEnddate"),
@NamedQuery(name="getTransactionByTellerIdAndNullDayEndSummary",query="SELECT p FROM ProcessTransaction p WHERE p.dayEndSummary IS NULL AND p.profileId =:tellerId AND p.dateCreated <= :dayEndDate AND p.status = :statusCompl"),
@NamedQuery(name="getTransactionByTellerIdAndNullDayEndSummaryDayEndDate",query="SELECT p FROM ProcessTransaction p WHERE p.dayEndSummary IS NULL AND p.profileId =:tellerId AND p.dateCreated >= :fromDate AND p.dateCreated <= :toDate AND (p.status = :statusBankReq OR p.status = :statusManResolve OR p.status = :statusCompl)"),
@NamedQuery(name="getProcessTransactionByStatusAndTargetMobileAndAmount", query = "SELECT p FROM ProcessTransaction p WHERE p.targetMobileId = :targetMobileId AND p.secretCode = :secretCode AND p.status = :status AND p.amount = :amount AND p.messageId = :reference"),
@NamedQuery(name = "getStartOfDayTransactionByTransactionTypeAndBranchAndStatus", query = "SELECT p FROM ProcessTransaction p WHERE p.branchId =:branchId AND p.transactionType =:transactionType AND p.status = :txnStatus"),
@NamedQuery(name = "getStartOfDayTransactionByTransactionTypeAndTellerAndDateRange", query = "SELECT p FROM ProcessTransaction p WHERE p.dayEndSummary IS NULL AND p.dayEndId IS NULL AND p.profileId =:tellerId AND  p.dateCreated >= :fromDate AND p.dateCreated <= :toDate AND p.transactionType= :transactionType AND p.status = :txnStatus"),
@NamedQuery(name="getStartDayTxnsByTellerAndDateRangeAndStatus",query="SELECT p FROM ProcessTransaction p WHERE p.dayEndSummary IS NULL AND p.dayEndId IS NULL AND p.dateCreated >= :fromDate AND p.dateCreated <= :toDate AND (p.status = :statusBankReq OR p.status = :statusManResolve OR p.status = :statusCompl OR p.status = :statusAWT) AND p.transactionType = :transactionType AND p.profileId = :profileId"),
@NamedQuery(name="getStartDayTxnsByTellerAndDateRangeAndApprovalStatus",query="SELECT p FROM ProcessTransaction p WHERE p.dayEndSummary IS NULL  AND p.dateCreated >= :fromDate AND p.dateCreated <= :toDate AND (p.status = :statusBankReq OR p.status = :statusManResolve OR p.status = :statusCompl) AND p.transactionType = :transactionType AND p.profileId = :profileId"),
@NamedQuery(name = "getTellerNetCashByTransactionTypeAndDateRangeAndStatus", query = "SELECT SUM(p.amount) FROM ProcessTransaction p WHERE p.dayEndSummary IS NULL AND p.dateCreated >= :fromDate AND p.dateCreated <= :toDate AND (p.status = :statusBankReq OR p.status = :statusManResolve OR p.status = :statusCompl OR p.status = :statusAWT) AND p.transactionType = :transactionType AND p.profileId = :profileId"),
@NamedQuery(name="getProcessTransactionByTargetMobileAndAmount", query = "SELECT p FROM ProcessTransaction p WHERE p.targetMobileId = :targetMobileId AND p.secretCode = :secretCode AND p.amount = :amount AND p.messageId = :reference"),
@NamedQuery(name = "getTellerNetCashByTransactionTypeAndStatus", query = "SELECT SUM(p.amount) FROM ProcessTransaction p WHERE p.dayEndSummary IS NULL AND p.status = :statusCompl AND p.transactionType = :transactionType AND p.profileId = :profileId"),
@NamedQuery(name = "sampleTxn", query = "SELECT p FROM ProcessTransaction p WHERE p.status = :status AND p.customerName = :customerName "),
@NamedQuery(name="getTransactionsByTellerIdDateRangeAndTransactionType",query="SELECT p FROM ProcessTransaction p WHERE p.dayEndSummary IS NULL  AND p.profileId =:tellerId AND  p.dateCreated >= :fromDate AND p.dateCreated <= :toDate AND p.transactionType= :transactionType")})
public class ProcessTransaction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(length = 30)
	private String id;
	@Column(length = 30)
	private String messageId;
	@Enumerated(EnumType.STRING)
	@Column(length = 50)
	private TransactionType transactionType;
	@Column(length = 30)
	private String customerId;
	@Column(length = 30)
	private String sourceMobile;
	@Column(length = 30)
	private String targetMobile;
	@Column(length = 30)
	private String sourceMobileId;
	@Column(length = 30)
	private String targetMobileId;
	@Column
	private long amount;
	@Column
	private long balance;
	@Column(length = 30)
	private String responseCode;
	@Column
	private Date valueDate;
	@Column(length = 30)
	private String bankReference;
	@Column
	private int passwordRetryCount;
	@Column
	private Date timeout;
	@Column(length = 10)
	private String passcodePrompt;
	@Column(length = 50)
	private String secretCode;
	@Column(length = 100)
	private String narrative;
	@Column(length = 30)
	private String utilityAccount;
	@Column(length = 50)
	private String utilityName;
	@Column(length = 30)
	private String tariffId;
	@Column(length = 30)
	private String transactionLocationId;
	@Column(length = 50)
	@Enumerated(EnumType.STRING)
	private TransactionLocationType transactionLocationType;
	@Column(length = 50)
	@Enumerated(EnumType.STRING)
	private TransferType transferType;
	@Column(length = 30)
	private String fromBankId;
	@Column(length = 30)
	private String toBankId;
	@Column
	private long tariffAmount;
	@Column(length = 30)
	private String branchId;
	@Column(length = 50)
	private String customerName;
	@Enumerated(EnumType.STRING)
	@Column(length = 50)
	private TransactionStatus status;
	@Column
	private Date dateCreated;
	@Version
	private long version;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "processTransaction")
	private List<TransactionState> states;
	@Column(length = 50)
	private String toCustomerName;
	@Column(length = 30)
	private String profileId;
	@Column(length = 30)
	private String sourceAccountNumber;
	@Column(length = 30)
	private String destinationAccountNumber;
	@Column(length = 30)
	private String sourceEQ3AccountNumber;
	@Column(length = 30)
	private String destinationEQ3AccountNumber;
	@Column
	private long referrerCommission;
	@Column
	private long referredCommission;
	@Column(length = 30)
	private String dayEndId;
	@Column(length = 30)
	private String agentCommissionId;
	@Column
	private long agentCommissionAmount;
	@Enumerated(EnumType.STRING)
	@Column(length = 50)
	private TxnSuperType txnSuperType;
	
	@Column(length = 50)
	private String bankMerchantId;
	
	@ManyToOne
	@JoinColumn(name="dayEndSummary")
	private DayEndSummary dayEndSummary;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "processTransaction")
	private List<TransactionCharge> transactionCharges;
	
	@Column(length = 30)
	private String oldMessageId;
	
	@Column(length = 30)
	private String transactionClass;
	
	@Column(length = 30)
	private String nonTellerId;
	
	@Column(length = 10)
	private String agentNumber;
	
	@Column(length = 20)
	private String merchantRef;
	
	@Column(length = 50)
	private String bouquetName;
	
	@Column(length = 20)
	private String bouquetCode;
	
	@Column(length = 3)
	private int numberOfMonths;
	
	@Column(length = 10)
	private long commission;
	
	private boolean timedOut;
	
	private boolean collectionTimeOut;
	
	public String getTransactionLocationId() {
		return transactionLocationId;
	}

	public void setTransactionLocationId(String transactionLocationId) {
		this.transactionLocationId = transactionLocationId;
	}

	public TransactionLocationType getTransactionLocationType() {
		return transactionLocationType;
	}

	public void setTransactionLocationType(TransactionLocationType transactionLocationType) {
		this.transactionLocationType = transactionLocationType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getSourceMobile() {
		return sourceMobile;
	}

	public void setSourceMobile(String sourceMobile) {
		this.sourceMobile = sourceMobile;
	}

	public String getTargetMobile() {
		return targetMobile;
	}

	public void setTargetMobile(String targetMobile) {
		this.targetMobile = targetMobile;
	}

	public long getAmount() {
		return amount;
	}

	public long getReferrerCommission() {
		return referrerCommission;
	}

	public void setReferrerCommission(long referrerCommission) {
		this.referrerCommission = referrerCommission;
	}

	public long getReferredCommission() {
		return referredCommission;
	}

	public void setReferredCommission(long referredCommission) {
		this.referredCommission = referredCommission;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public long getBalance() {
		return balance;
	}

	public void setBalance(long balance) {
		this.balance = balance;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	public String getBankReference() {
		return bankReference;
	}

	public void setBankReference(String bankReference) {
		this.bankReference = bankReference;
	}

	public int getPasswordRetryCount() {
		return passwordRetryCount;
	}

	public void setPasswordRetryCount(int passwordRetryCount) {
		this.passwordRetryCount = passwordRetryCount;
	}

	public Date getTimeout() {
		return timeout;
	}

	public void setTimeout(Date timeout) {
		this.timeout = timeout;
	}

	public String getPasscodePrompt() {
		return passcodePrompt;
	}

	public void setPasscodePrompt(String passcodePrompt) {
		this.passcodePrompt = passcodePrompt;
	}

	public String getNarrative() {
		return narrative;
	}

	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}

	public String getUtilityAccount() {
		return utilityAccount;
	}

	public void setUtilityAccount(String utilityAccount) {
		this.utilityAccount = utilityAccount;
	}

	public String getUtilityName() {
		return utilityName;
	}

	public void setUtilityName(String utilityName) {
		this.utilityName = utilityName;
	}

	public List<TransactionState> getStates() {
		return states;
	}

	public void setStates(List<TransactionState> states) {
		this.states = states;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public void setSecretCode(String secretCode) {
		this.secretCode = secretCode;
	}

	public String getSecretCode() {
		return secretCode;
	}

	public String getTariffId() {
		return tariffId;
	}

	public void setTariffId(String tariffId) {
		this.tariffId = tariffId;
	}

	public long getTariffAmount() {
		return tariffAmount;
	}

	public void setTariffAmount(long tariffAmount) {
		this.tariffAmount = tariffAmount;
	}

	public TransferType getTransferType() {
		return transferType;
	}

	public void setTransferType(TransferType transferType) {
		this.transferType = transferType;
	}

	public String getFromBankId() {
		return fromBankId;
	}

	public void setFromBankId(String fromBankId) {
		this.fromBankId = fromBankId;
	}

	public String getToBankId() {
		return toBankId;
	}

	public void setToBankId(String toBankId) {
		this.toBankId = toBankId;
	}

	public void setStatus(TransactionStatus status) {
		this.status = status;
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerName() {
		return customerName;
	}

	public String getToCustomerName() {
		return toCustomerName;
	}

	public void setToCustomerName(String toCustomerName) {
		this.toCustomerName = toCustomerName;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public String getProfileId() {
		return profileId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setSourceMobileId(String sourceMobileId) {
		this.sourceMobileId = sourceMobileId;
	}

	public String getSourceMobileId() {
		return sourceMobileId;
	}

	public void setTargetMobileId(String targetMobileId) {
		this.targetMobileId = targetMobileId;
	}

	public String getTargetMobileId() {
		return targetMobileId;
	}

	public void setSourceAccountNumber(String sourceAccountNumber) {
		this.sourceAccountNumber = sourceAccountNumber;
	}

	public String getSourceAccountNumber() {
		return sourceAccountNumber;
	}

	public void setDestinationAccountNumber(String destinationAccountNumber) {
		this.destinationAccountNumber = destinationAccountNumber;
	}

	public String getDestinationAccountNumber() {
		return destinationAccountNumber;
	}

	public void setTxnSuperType(TxnSuperType txnSuperType) {
		this.txnSuperType = txnSuperType;
	}

	public TxnSuperType getTxnSuperType() {
		return txnSuperType;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	
	public DayEndSummary getDayEndSummary() {
		return dayEndSummary;
	}

	/** 
	 */
	public void setDayEndSummary(DayEndSummary dayEndSummary) {
		this.dayEndSummary = dayEndSummary;
	}

	public void setAgentCommissionId(String agentCommissionId) {
		this.agentCommissionId = agentCommissionId;
	}

	public String getAgentCommissionId() {
		return agentCommissionId;
	}

	public void setDayEndId(String dayEndId) {
		this.dayEndId = dayEndId;
	}

	public String getDayEndId() {
		return dayEndId;
	}

	public void setAgentCommissionAmount(long agentCommissionAmount) {
		this.agentCommissionAmount = agentCommissionAmount;
	}

	public long getAgentCommissionAmount() {
		return agentCommissionAmount;
	}

	public void setTransactionCharges(List<TransactionCharge> transactionCharges) {
		this.transactionCharges = transactionCharges;
	}

	public List<TransactionCharge> getTransactionCharges() {
		return transactionCharges;
	}

	/**
	 * @return the bankMerchantId
	 */
	public String getBankMerchantId() {
		return bankMerchantId;
	}

	/**
	 * @param bankMerchantId the bankMerchantId to set
	 */
	public void setBankMerchantId(String bankMerchantId) {
		this.bankMerchantId = bankMerchantId;
	}

	public String getOldMessageId() {
		return oldMessageId;
	}

	public void setOldMessageId(String oldMessageId) {
		this.oldMessageId = oldMessageId;
	}

	public void setTransactionClass(String transactionClass) {
		this.transactionClass = transactionClass;
	}

	public String getTransactionClass() {
		return transactionClass;
	}

	public void setNonTellerId(String nonTellerId) {
		this.nonTellerId = nonTellerId;
	}

	public String getNonTellerId() {
		return nonTellerId;
	}

	public String getSourceEQ3AccountNumber() {
		return sourceEQ3AccountNumber;
	}

	public void setSourceEQ3AccountNumber(String sourceEQ3AccountNumber) {
		this.sourceEQ3AccountNumber = sourceEQ3AccountNumber;
	}

	public String getDestinationEQ3AccountNumber() {
		return destinationEQ3AccountNumber;
	}

	public void setDestinationEQ3AccountNumber(String destinationEQ3AccountNumber) {
		this.destinationEQ3AccountNumber = destinationEQ3AccountNumber;
	}

	public String getAgentNumber() {
		return agentNumber;
	}

	public void setAgentNumber(String agentNumber) {
		this.agentNumber = agentNumber;
	}

	public boolean isTimedOut() {
		return timedOut;
	}

	public void setTimedOut(boolean timedOut) {
		this.timedOut = timedOut;
	}

	public boolean isCollectionTimeOut() {
		return collectionTimeOut;
	}

	public void setCollectionTimeOut(boolean collectionTimeOut) {
		this.collectionTimeOut = collectionTimeOut;
	}

	public String getMerchantRef() {
		return merchantRef;
	}

	public void setMerchantRef(String merchantRef) {
		this.merchantRef = merchantRef;
	}

	public void setBouquetName(String bouquetName) {
		this.bouquetName = bouquetName;
	}

	public String getBouquetName() {
		return bouquetName;
	}

	public void setBouquetCode(String bouquetCode) {
		this.bouquetCode = bouquetCode;
	}

	public String getBouquetCode() {
		return bouquetCode;
	}

	public void setNumberOfMonths(int numberOfMonths) {
		this.numberOfMonths = numberOfMonths;
	}

	public int getNumberOfMonths() {
		return numberOfMonths;
	}

	public void setCommission(long commission) {
		this.commission = commission;
	}

	public long getCommission() {
		return commission;
	}
	
}
