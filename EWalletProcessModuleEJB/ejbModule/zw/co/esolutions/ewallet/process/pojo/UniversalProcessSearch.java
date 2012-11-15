package zw.co.esolutions.ewallet.process.pojo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;

import zw.co.esolutions.ewallet.enums.TransactionLocationType;
import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.enums.TransferType;
import zw.co.esolutions.ewallet.enums.TxnFamily;
import zw.co.esolutions.ewallet.enums.TxnSuperType;

public class UniversalProcessSearch implements Serializable {

	private Date fromDate;
	private Date toDate;
	private TransactionStatus status;
	private zw.co.esolutions.ewallet.enums.TransactionType txnType;
	private TxnFamily txnFamily;
	private String tellerId;
	private String bankId;
	private String branchId;
	private String messageId;
	private String oldMessageId; // Left Here
	private String utilityAccount;
	private String sourceMobile;
	private String targetMobile;
	private String customerId;
	private String sourceMobileId;
	private String targetMobileId;
	private String responseCode;
	private Date valueDate;
	private String bankReference;
	private int passwordRetryCount;
	private Date timeout;
	private String narrative;
	private String tariffId;
	private String transactionLocationId;
	private TransactionLocationType transactionLocationType;
	private TransferType transferType;
	private String toBankId;
	private String sourceAccountNumber;
	private String destinationAccountNumber;
	private String dayEndId;
	private String agentCommissionId;
	private TxnSuperType txnSuperType;
	private String bankMerchantId;
	private boolean manualResolve;
	private String transactionClass;
	private String nonTellerId;
	private String fromEwalletAccount;
	private String toEwalletAccount;
	private String sourceEQ3AccountNumber;
	private String destinationEQ3AccountNumber;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5138573661409424609L;
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	public TransactionStatus getStatus() {
		return status;
	}
	public void setStatus(TransactionStatus status) {
		this.status = status;
	}
	public zw.co.esolutions.ewallet.enums.TransactionType getTxnType() {
		return txnType;
	}
	public void setTxnType(zw.co.esolutions.ewallet.enums.TransactionType txnType) {
		this.txnType = txnType;
	}
	public TxnFamily getTxnFamily() {
		return txnFamily;
	}
	public void setTxnFamily(TxnFamily txnFamily) {
		this.txnFamily = txnFamily;
	}
	public String getTellerId() {
		return tellerId;
	}
	public void setTellerId(String tellerId) {
		this.tellerId = tellerId;
	}
	public String getBankId() {
		return bankId;
	}
	public void setBankId(String bankId) {
		this.bankId = bankId;
	}
	public String getBranchId() {
		return branchId;
	}
	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getOldMessageId() {
		return oldMessageId;
	}
	public void setOldMessageId(String oldMessageId) {
		this.oldMessageId = oldMessageId;
	}
	public String getUtilityAccount() {
		return utilityAccount;
	}
	public void setUtilityAccount(String utilityAccount) {
		this.utilityAccount = utilityAccount;
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
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getCustomerId() {
		return customerId;
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
	public String getNarrative() {
		return narrative;
	}
	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}
	public String getTariffId() {
		return tariffId;
	}
	public void setTariffId(String tariffId) {
		this.tariffId = tariffId;
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
	public void setTransactionLocationType(
			TransactionLocationType transactionLocationType) {
		this.transactionLocationType = transactionLocationType;
	}
	public TransferType getTransferType() {
		return transferType;
	}
	public void setTransferType(TransferType transferType) {
		this.transferType = transferType;
	}
	public String getToBankId() {
		return toBankId;
	}
	public void setToBankId(String toBankId) {
		this.toBankId = toBankId;
	}
	public String getSourceAccountNumber() {
		return sourceAccountNumber;
	}
	public void setSourceAccountNumber(String sourceAccountNumber) {
		this.sourceAccountNumber = sourceAccountNumber;
	}
	public String getDestinationAccountNumber() {
		return destinationAccountNumber;
	}
	public void setDestinationAccountNumber(String destinationAccountNumber) {
		this.destinationAccountNumber = destinationAccountNumber;
	}
	public String getDayEndId() {
		return dayEndId;
	}
	public void setDayEndId(String dayEndId) {
		this.dayEndId = dayEndId;
	}
	public String getAgentCommissionId() {
		return agentCommissionId;
	}
	public void setAgentCommissionId(String agentCommissionId) {
		this.agentCommissionId = agentCommissionId;
	}
	public TxnSuperType getTxnSuperType() {
		return txnSuperType;
	}
	public void setTxnSuperType(TxnSuperType txnSuperType) {
		this.txnSuperType = txnSuperType;
	}
	public String getBankMerchantId() {
		return bankMerchantId;
	}
	public void setBankMerchantId(String bankMerchantId) {
		this.bankMerchantId = bankMerchantId;
	}
	public void setManualResolve(boolean manualResolve) {
		this.manualResolve = manualResolve;
	}
	public boolean isManualResolve() {
		return manualResolve;
	}
	public String getTransactionClass() {
		return transactionClass;
	}
	public void setTransactionClass(String transactionClass) {
		this.transactionClass = transactionClass;
	}
	public String getNonTellerId() {
		return nonTellerId;
	}
	public void setNonTellerId(String nonTellerId) {
		this.nonTellerId = nonTellerId;
	}
	public String getFromEwalletAccount() {
		return fromEwalletAccount;
	}
	public void setFromEwalletAccount(String fromEwalletAccount) {
		this.fromEwalletAccount = fromEwalletAccount;
	}
	public String getToEwalletAccount() {
		return toEwalletAccount;
	}
	public void setToEwalletAccount(String toEwalletAccount) {
		this.toEwalletAccount = toEwalletAccount;
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
	
}
