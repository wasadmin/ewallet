package zw.co.esolutions.mcommerce.centralswitch.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.Version;

import zw.co.esolutions.ewallet.enums.MobileNetworkOperator;
import zw.co.esolutions.ewallet.enums.TransactionLocationType;
import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.enums.TransferType;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.CascadeType.ALL;

@Entity
@NamedQueries({ @NamedQuery(name = "getProcessTransaction", query = "SELECT p FROM MessageTransaction p"), 
	@NamedQuery(name = "getProcessTransactionById", query = "SELECT p FROM MessageTransaction p WHERE p.id = :id"), 
	@NamedQuery(name = "getProcessTransactionByMessageId", query = "SELECT p FROM MessageTransaction p WHERE p.messageReference = :messageId"),
	@NamedQuery(name = "getProcessTransactionByMessageType", query = "SELECT p FROM MessageTransaction p WHERE p.transactionType = :transactionType"), 
	@NamedQuery(name = "getProcessTransactionBySourceMobile", query = "SELECT p FROM MessageTransaction p WHERE p.sourceMobile = :sourceMobile ORDER BY p.dateCreated DESC"), 
	@NamedQuery(name = "getProcessTransactionByTargetMobile", query = "SELECT p FROM MessageTransaction p WHERE p.targetMobile = :targetMobile"), @NamedQuery(name = "getProcessTransactionByAmount", query = "SELECT p FROM MessageTransaction p WHERE p.amount = :amount"),
	@NamedQuery(name = "getProcessTransactionByBalance", query = "SELECT p FROM MessageTransaction p WHERE p.balance = :balance"), @NamedQuery(name = "getProcessTransactionByResponseCode", query = "SELECT p FROM MessageTransaction p WHERE p.responseCode = :responseCode"), 
	@NamedQuery(name = "getProcessTransactionByValueDate", query = "SELECT p FROM MessageTransaction p WHERE p.valueDate = :valueDate"), @NamedQuery(name = "getProcessTransactionByBankReference", query = "SELECT p FROM MessageTransaction p WHERE p.bankReference = :bankReference"), 
	@NamedQuery(name = "getProcessTransactionByPasswordRetryCount", query = "SELECT p FROM MessageTransaction p WHERE p.passwordRetryCount = :passwordRetryCount"), @NamedQuery(name = "getProcessTransactionByTimeout", query = "SELECT p FROM MessageTransaction p WHERE p.timeout = :timeout"), 
	@NamedQuery(name = "getProcessTransactionByPasscodePrompt", query = "SELECT p FROM MessageTransaction p WHERE p.passcodePrompt = :passcodePrompt"), @NamedQuery(name = "getProcessTransactionBySecretCode", query = "SELECT p FROM MessageTransaction p WHERE p.secretCode = :secretCode"), 
	@NamedQuery(name = "getProcessTransactionBySourceMobileIdAndStatusAndSourceBankCode", query = "SELECT p FROM MessageTransaction p WHERE p.sourceMobileId =: sourceMobileId AND p.status = : status AND p.sourceBankCode = : sourceBankCode ORDER BY p.dateCreated DESC"),
	@NamedQuery(name = "getProcessTransactionByNarrative", query = "SELECT p FROM MessageTransaction p WHERE p.narrative = :narrative"), @NamedQuery(name = "getProcessTransactionByReversalReason", query = "SELECT p FROM MessageTransaction p WHERE p.reversalReason = :reversalReason"), 
	@NamedQuery(name = "getProcessTransactionByUtilityAccount", query = "SELECT p FROM MessageTransaction p WHERE p.utilityAccount = :utilityAccount"), @NamedQuery(name = "getProcessTransactionByUtilityName", query = "SELECT p FROM MessageTransaction p WHERE p.utilityName = :utilityName"), 
	@NamedQuery(name = "getProcessTransactionByDateCreated", query = "SELECT p FROM MessageTransaction p WHERE p.dateCreated = :dateCreated"), @NamedQuery(name = "getProcessTransactionByVersion", query = "SELECT p FROM MessageTransaction p WHERE p.version = :version"), @NamedQuery(name = "getProcessTransactionByStatus", query = "SELECT p FROM MessageTransaction p WHERE p.status = :status"), 
	@NamedQuery(name = "getXLatestProcessTransactionBySourceMobileId", query = "SELECT p FROM MessageTransaction p WHERE p.sourceMobileId = :sourceMobileId AND " + "p.transactionType <> :type1 AND p.transactionType <> :type2 AND p.transactionType <> :type3 AND p.transactionType <> :type4 AND p.status = :status ORDER BY p.dateCreated DESC"), @NamedQuery(name = "getProcessTransactionsWithinDateRange", query = "SELECT p FROM MessageTransaction p WHERE p.dateCreated >= :fromDate AND " + "p.dateCreated <= :toDate AND p.status = :status ORDER BY p.dateCreated DESC"), 
	@NamedQuery(name = "getProcessTransactionsWithinDateRangeByBankId", query = "SELECT p FROM MessageTransaction p WHERE p.dateCreated >= :fromDate AND " + "p.dateCreated <= :toDate AND p.status = :status AND p.fromBankId = :bankId ORDER BY p.dateCreated DESC GROUPBY(p.branchId)"), @NamedQuery(name = "getProcessTransactionsWithinDateRangeByBankIdAndBranchId", query = "SELECT p FROM MessageTransaction p WHERE p.dateCreated >= :fromDate AND " + "p.dateCreated <= :toDate AND p.status = :status AND p.fromBankId = :bankId and p.branchId = :branchId ORDER BY p.dateCreated DESC GROUPBY(p.branchId)"), 
	@NamedQuery(name = "getProcessTransactionsWithinDateRangeByMsgType", query = "SELECT p FROM MessageTransaction p WHERE p.dateCreated >= :fromDate AND " + "p.dateCreated <= :toDate AND p.status = :status AND p.transactionType = :transactionType ORDER BY p.dateCreated DESC "), @NamedQuery(name = "getProcessTransactionsWithinDateRangeByMsgTypeByBankId", query = "SELECT p FROM MessageTransaction p WHERE p.dateCreated >= :fromDate AND " + "p.dateCreated <= :toDate AND p.status = :status AND p.transactionType = :transactionType AND p.fromBankId = :bankId ORDER BY p.dateCreated DESC "), @NamedQuery(name = "getProcessTransactionsWithinDateRangeByMsgTypeByBankIdAndBranchId", query = "SELECT p FROM MessageTransaction p WHERE p.dateCreated >= :fromDate AND " + "p.dateCreated <= :toDate AND p.status = :status AND p.transactionType = :transactionType AND p.fromBankId = :bankId AND " + "p.branchId = :branchId ORDER BY p.dateCreated DESC "), 
	@NamedQuery(name = "getTellerDayEnd", query = "SELECT p FROM MessageTransaction p WHERE p.dateCreated >= :fromDate AND " + "p.dateCreated <= :toDate AND p.status = :status AND p.transactionType = :transactionType AND " + "p.fromBankId = :bankId AND p.profileId = :profileId ORDER BY p.dateCreated DESC "), @NamedQuery(name = "getTellerDayEndTotal", query = "SELECT SUM(p.amount) FROM MessageTransaction p WHERE p.dateCreated >= :fromDate AND " + "p.dateCreated <= :toDate AND p.status = :status AND p.transactionType = :transactionType AND " + "p.fromBankId = :bankId AND p.profileId = :profileId"), @NamedQuery(name = "getProcessTransactionBySourceMobileId", query = "SELECT p FROM MessageTransaction p WHERE p.sourceMobileId = :sourceMobileId ORDER BY p.dateCreated DESC"), 
	@NamedQuery(name = "getProcessTransactionByTargetMobileId", query = "SELECT p FROM MessageTransaction p WHERE p.targetMobileId = :targetMobileId"), @NamedQuery(name = "getProcessTransactionBySourceMobileIdAndStatus", query = "SELECT p FROM MessageTransaction p WHERE p.sourceMobileId = :sourceMobileId AND p.status = :status ORDER BY p.dateCreated DESC"), @NamedQuery(name = "getProcessTransactionBySourceMobileIdAndMessageTypeAndStatus", query = "SELECT p FROM MessageTransaction p WHERE p.sourceMobileId = :sourceMobileId AND p.transactionType = :transactionType AND p.status = :status"), 
	@NamedQuery(name = "getProcessTransactionByStatusAndTargetMobileAndAmount", query = "SELECT p FROM MessageTransaction p WHERE p.targetMobileId = :targetMobileId AND p.secretCode = :secretCode AND p.status = :status AND p.amount = :amount AND p.messageReference = :reference"),
	@NamedQuery(name = "getLatestTransactionPendingAuthorisation", query = "SELECT p FROM MessageTransaction p WHERE p.sourceMobileId =: sourceMobileId AND p.status = : status ORDER BY p.dateCreated DESC")
})
public class MessageTransaction implements Serializable {

	private static final long serialVersionUID = -5901060060654871229L;

	@Id
	@Column(length = 30)
	private String uuid;

	@Enumerated(EnumType.STRING)
	@Column(length = 40)
	private TransactionType transactionType;
	
	@Column(length = 30)
	private String customerId;
	
	@Column(length = 30)
	private String sourceMobileId;
	
	@Column(length = 30)
	private String targetMobileId;
	
	@Column(length = 30)
	private String sourceMobileNumber;
	
	@Column(length = 30)
	private String targetMobileNumber;
	
	@Column
	private long amount;
	
	@Column(length = 30)
	private String responseCode;

	@Column
	private Date summaryDate;
	
	@Column(length = 30)
	private String bankReference;
	
	@Column
	private int passwordRetryCount;
	
	@Column
	private Timestamp timeout;
	
	@Column(length = 10)
	private String passcodePrompt;
	
	@Column(length = 50)
	private String secretCode;
	
	@Column(length = 250)
	private String narrative;

	@Column(length = 50)
	private String utilityName;
	
	@Column(length = 30)
	private String transactionLocationId;
	
	@Column(length = 40)
	@Enumerated(EnumType.STRING)
	private TransactionLocationType transactionLocationType;
	
	@Column(length = 20)
	@Enumerated(EnumType.STRING)
	private TransferType transferType;
	
	@Column(length = 50)
	private String customerName;

	@Enumerated(EnumType.STRING)
	@Column(length = 40)
	private TransactionStatus status;
	
	@OneToMany(mappedBy = "transaction", fetch = FetchType.LAZY)
	private List<TransactionState> states;

	@Column(length = 50)
	private String toCustomerName;
	
	@Column(length = 30)
	private String customerUtilityAccount;

	@Column(length = 30)
	private String sourceBankId;

	@Column(length = 30, name = "sourceBankPrefix")
	private String sourceBankPrefix;

	@Column(length = 30, name = "sourceBankCode")
	private String sourceBankCode;

	@Column(length = 30, name = "targetBankCode")
	private String targetBankCode;

	@Column(length = 30)
	private String targetBankId;

	@Column(length = 30)
	private String sourceBankAccount;
	
	@Column(length = 30)
	private String destinationBankAccount;
	
	@Enumerated(EnumType.STRING)
	private MobileNetworkOperator mno;
		
	@Column
	private Timestamp dateCreated;
	
	@Column(length = 30)
	private String oldMessageId;
	
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
	
	@Embedded
	private TransactionRoutingInfo transactionRoutingInfo;
	
	@Version
	private long version;
	
	//Transient fields
	@Transient
	private String oldPin;
	
	@Transient
	private String newPin;
	
	@Transient
	private String passwordParts;
	
	@Transient
	private boolean agentTransaction;
	
	@Transient
	private boolean validateAgentNumber;
	
	@Transient
	private String tapCommand;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getSourceMobileNumber() {
		return sourceMobileNumber;
	}

	public void setSourceMobileNumber(String sourceMobileNumber) {
		this.sourceMobileNumber = sourceMobileNumber;
	}

	public String getTargetMobileNumber() {
		return targetMobileNumber;
	}

	public void setTargetMobileNumber(String targetMobileNumber) {
		this.targetMobileNumber = targetMobileNumber;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
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

	public Timestamp getTimeout() {
		return timeout;
	}

	public void setTimeout(Timestamp timeout) {
		this.timeout = timeout;
	}

	public String getPasscodePrompt() {
		return passcodePrompt;
	}

	public void setPasscodePrompt(String passcodePrompt) {
		this.passcodePrompt = passcodePrompt;
	}

	public String getSecretCode() {
		return secretCode;
	}

	public void setSecretCode(String secretCode) {
		this.secretCode = secretCode;
	}

	public String getNarrative() {
		return narrative;
	}

	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}

	public String getCustomerUtilityAccount() {
		return customerUtilityAccount;
	}

	public void setCustomerUtilityAccount(String customerUtilityAccount) {
		this.customerUtilityAccount = customerUtilityAccount;
	}

	public String getUtilityName() {
		return utilityName;
	}

	public void setUtilityName(String utilityName) {
		this.utilityName = utilityName;
	}

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

	public TransferType getTransferType() {
		return transferType;
	}

	public void setTransferType(TransferType transferType) {
		this.transferType = transferType;
	}

	public String getSourceBankId() {
		return sourceBankId;
	}

	public void setSourceBankId(String sourceBankId) {
		this.sourceBankId = sourceBankId;
	}

	public String getTargetBankId() {
		return targetBankId;
	}

	public void setTargetBankId(String targetBankId) {
		this.targetBankId = targetBankId;
	}

	public Timestamp getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Timestamp dateCreated) {
		this.dateCreated = dateCreated;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public List<TransactionState> getStates() {
		return states;
	}

	public void setStates(List<TransactionState> states) {
		this.states = states;
	}

	public String getSourceBankPrefix() {
		return sourceBankPrefix;
	}

	public void setSourceBankPrefix(String sourceBankPrefix) {
		this.sourceBankPrefix = sourceBankPrefix;
	}

	public String getSourceBankCode() {
		return sourceBankCode;
	}

	public void setSourceBankCode(String sourceBankCode) {
		this.sourceBankCode = sourceBankCode;
	}

	public String getTargetBankCode() {
		return targetBankCode;
	}

	public void setTargetBankCode(String targetBankCode) {
		this.targetBankCode = targetBankCode;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public String getSourceMobileId() {
		return sourceMobileId;
	}

	public void setSourceMobileId(String sourceMobileId) {
		this.sourceMobileId = sourceMobileId;
	}

	public String getTargetMobileId() {
		return targetMobileId;
	}

	public void setTargetMobileId(String targetMobileId) {
		this.targetMobileId = targetMobileId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public void setStatus(TransactionStatus status) {
		this.status = status;
	}

	public String getToCustomerName() {
		return toCustomerName;
	}

	public void setToCustomerName(String toCustomerName) {
		this.toCustomerName = toCustomerName;
	}

	public String getSourceBankAccount() {
		return sourceBankAccount;
	}

	public void setSourceBankAccount(String sourceBankAccount) {
		this.sourceBankAccount = sourceBankAccount;
	}

	public String getDestinationBankAccount() {
		return destinationBankAccount;
	}

	public void setDestinationBankAccount(String destinationBankAccount) {
		this.destinationBankAccount = destinationBankAccount;
	}

	public void setMno(MobileNetworkOperator mno) {
		this.mno = mno;
	}

	public MobileNetworkOperator getMno() {
		return mno;
	}

	public Date getSummaryDate() {
		return summaryDate;
	}

	public void setSummaryDate(Date summaryDate) {
		this.summaryDate = summaryDate;
	}

	public void setOldMessageId(String oldMessageId) {
		this.oldMessageId = oldMessageId;
	}

	public String getOldMessageId() {
		return oldMessageId;
	}

	public void setAgentNumber(String agentNumber) {
		this.agentNumber = agentNumber;
	}

	public String getAgentNumber() {
		return agentNumber;
	}

	public TransactionRoutingInfo getTransactionRoutingInfo() {
		return transactionRoutingInfo;
	}

	public void setTransactionRoutingInfo(TransactionRoutingInfo transactionRoutingInfo) {
		this.transactionRoutingInfo = transactionRoutingInfo;
	}

	public boolean isOnline() {
		if(this.getTransactionRoutingInfo() == null){
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>Transaction Routing Info is NULL so this txn is not straight thru");
			return false;
		}else{
			boolean online = this.getTransactionRoutingInfo().isStraightThroughEnabled();
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>IS ONLINE : " + online);
			return online;
		}
	}

	public String getOldPin() {
		return oldPin;
	}

	public void setOldPin(String oldPin) {
		this.oldPin = oldPin;
	}

	public String getNewPin() {
		return newPin;
	}

	public void setNewPin(String newPin) {
		this.newPin = newPin;
	}

	public boolean isAgentTransaction() {
		return agentTransaction;
	}

	public void setAgentTransaction(boolean agentTransaction) {
		this.agentTransaction = agentTransaction;
	}

	public boolean isValidateAgentNumber() {
		return validateAgentNumber;
	}

	public void setValidateAgentNumber(boolean validateAgentNumber) {
		this.validateAgentNumber = validateAgentNumber;
	}

	public String getTapCommand() {
		return tapCommand;
	}

	public void setTapCommand(String tapCommand) {
		this.tapCommand = tapCommand;
	}

	public String getPasswordParts() {
		return passwordParts;
	}

	public void setPasswordParts(String passwordParts) {
		this.passwordParts = passwordParts;
	}

	public String getMerchantRef() {
		return merchantRef;
	}

	public void setMerchantRef(String merchantRef) {
		this.merchantRef = merchantRef;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setNumberOfMonths(Integer numberOfMonths) {
		this.numberOfMonths = numberOfMonths;
	}

	public Integer getNumberOfMonths() {
		return numberOfMonths;
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

	public void setCommission(long commission) {
		this.commission = commission;
	}

	public long getCommission() {
		return commission;
	}
}
