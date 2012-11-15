package zw.co.esolutions.bankif.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import zw.co.esolutions.ewallet.enums.MobileNetworkOperator;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.msg.BankRequest;
import javax.persistence.Enumerated;
import static javax.persistence.EnumType.STRING;
import javax.persistence.OneToOne;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.CascadeType.REFRESH;
import javax.persistence.JoinTable;
import javax.persistence.OrderBy;

/**
 * Entity implementation class for Entity: BankRequestMessage
 * 
 */
@Entity
@NamedQuery(name = "findBankRequestInfoByReference", query = "SELECT b FROM BankRequestMessage b WHERE b.reference = :reference")
public class BankRequestMessage implements Serializable {

	@Id
	@Column(length = 30)
	private String id;
	@Column(unique = true, length = 30)
	private String reference;
	@Column(length = 30)
	private String sourceCardNumber;
	@Column(length = 30)
	private String sourceCardSequence;
	@Column(length = 30)
	private String sourceAccountNumber;
	@Column(length = 20)
	private String sourceMobileNumber;
	@Column(length = 20)
	private String targetMobileNumber;
	@Column(length = 30)
	private String targetAccountNumber;
	
	@Column(name = "totalChargesAmount")
	private long totalChargesAmount;
	
	@Transient
	private MobileNetworkOperator mobileNetworkOperator;
	
	@Transient
	private String narrative;
	
	@Column(length = 5)
	private String processingCode;

	@Column(length = 50)
	@Enumerated(STRING)
	private TransactionType transactionType;
	
	@Column(name = "amount")
	private long amount;
	
	@Column(name = "chargeAmount")
	private long chargeAmount;

	@Column(name = "balance")
	private long balance;

	@Column(name = "ledgerBalance")
	private long ledgerBalance;

	@Column(name = "valueDate")
	private Date valueDate;
	
	@Column(length = 30)
	private String bankReference;

	@Column(name = "additionalData", length = 250)
	private String additionalData;

	@Column(length = 30)
	private String secretCode;
	
	@Column(length = 30)
	private String utilityBankAccount;
	
	@Column(length = 20)
	private String utilityName;
	
	@Column(length = 20)
	private String customerUtilityAccount;
	
	@Column(length = 10)
	private String sourceBankCode;
	
	@Column(length = 10)
	private String targetBankCode;
	
	@Column(length = 25)
	private String acquirerId;

	@Column(name = "transactionDate")
	private Date transactionDate;
	
	@ManyToOne
	@JoinColumn(name = "responseCode", referencedColumnName = "responseCode")
	private BankResponseCode bankResponseCode;

	@Column(name = "systemTraceAuditNumber")
	private long systemTraceAuditNumber;
	
	@Column(length = 2)
	private String tarrifResponseCode;

	@Column(length = 5)
	private String currencyISONumeric;
	
	@Column(length = 5)
	private String currencyISOAlphabetic;

	@Column(length = 30)
	private String status;

	@Column(length = 10)
	private String messageType;
	
	@Column(length = 100)
	private String beneficiaryName;
	
	@Column(length = 60)
	private String destinationBankName;
	
	@Column(length = 40)
	private String paymentRef;
	
	@Column(length = 50)
	private String bouquetCode;

	@OneToMany(mappedBy = "bankRequest")
	private List<CommissionMessage> commissionMessage;

	private static final long serialVersionUID = 1L;

	public BankRequestMessage() {
		super();
	}

	public BankRequestMessage(BankRequest bankRequest) {
		this.setAcquirerId(bankRequest.getAcquirerId() == null ? "e-Solutions" : bankRequest.getAcquirerId());
		this.setAmount(bankRequest.getAmount());
		this.setCurrencyISOAlphabetic(bankRequest.getCurrencyISOCode());
		this.setCurrencyISONumeric(bankRequest.getCurrencyISOCode());
		this.setCustomerUtilityAccount(bankRequest.getCustomerUtilityAccount());
		this.setReference(bankRequest.getReference());
		this.setSecretCode(bankRequest.getSecretCode());
		this.setSourceAccountNumber(bankRequest.getSourceAccountNumber());
		this.setSourceBankCode(bankRequest.getSourceBankCode());
		this.setSourceCardNumber(bankRequest.getCardNumber());
		this.setSourceCardSequence(bankRequest.getCardSequence());
		this.setSourceMobileNumber(bankRequest.getSourceMobileNumber());
		this.setTargetAccountNumber(bankRequest.getTargetAccountNumber());
		this.setTargetBankCode(bankRequest.getTargetBankCode());
		this.setTargetMobileNumber(bankRequest.getTargetMobileNumber());
		this.setTransactionDate(new Date(System.currentTimeMillis()));
		this.setMobileNetworkOperator(bankRequest.getMobileNetworkOperator());
		this.setNarrative(bankRequest.getNarrative());
		this.setTotalChargesAmount(bankRequest.getTotalCommissionAmount());
		if (this.getValueDate() == null) {
			this.setValueDate(this.getTransactionDate());
		}
		// is this correct?
		this.setUtilityBankAccount(bankRequest.getTargetAccountNumber());
		this.setUtilityName(bankRequest.getUtilityName());
		
		this.setPaymentRef(bankRequest.getPaymentRef());
		this.setBeneficiaryName(bankRequest.getBeneficiaryName());
	}

	public String getReference() {
		return this.reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSourceCardNumber() {
		return sourceCardNumber;
	}

	public void setSourceCardNumber(String sourceCardNumber) {
		this.sourceCardNumber = sourceCardNumber;
	}

	public String getSourceCardSequence() {
		return sourceCardSequence;
	}

	public void setSourceCardSequence(String sourceCardSequence) {
		this.sourceCardSequence = sourceCardSequence;
	}

	public String getSourceAccountNumber() {
		return sourceAccountNumber;
	}

	public void setSourceAccountNumber(String sourceAccountNumber) {
		this.sourceAccountNumber = sourceAccountNumber;
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

	public String getTargetAccountNumber() {
		return targetAccountNumber;
	}

	public void setTargetAccountNumber(String targetAccountNumber) {
		this.targetAccountNumber = targetAccountNumber;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public long getChargeAmount() {
		return chargeAmount;
	}

	public void setChargeAmount(long chargeAmount) {
		this.chargeAmount = chargeAmount;
	}

	public long getBalance() {
		return balance;
	}

	public void setBalance(long balance) {
		this.balance = balance;
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

	public String getSecretCode() {
		return secretCode;
	}

	public void setSecretCode(String secretCode) {
		this.secretCode = secretCode;
	}

	public String getUtilityBankAccount() {
		return utilityBankAccount;
	}

	public void setUtilityBankAccount(String utilityBankAccount) {
		this.utilityBankAccount = utilityBankAccount;
	}

	public String getUtilityName() {
		return utilityName;
	}

	public void setUtilityName(String utilityName) {
		this.utilityName = utilityName;
	}

	public String getCustomerUtilityAccount() {
		return customerUtilityAccount;
	}

	public void setCustomerUtilityAccount(String customerUtilityAccount) {
		this.customerUtilityAccount = customerUtilityAccount;
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

	public String getTarrifResponseCode() {
		return tarrifResponseCode;
	}

	public void setTarrifResponseCode(String tarrifResponseCode) {
		this.tarrifResponseCode = tarrifResponseCode;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getAcquirerId() {
		return acquirerId;
	}

	public void setAcquirerId(String acquirerId) {
		this.acquirerId = acquirerId;
	}

	// public TransactionType getTransactionType() {
	// return transactionType;
	// }
	//
	// public void setTransactionType(TransactionType transactionType) {
	// this.transactionType = transactionType;
	// }

	public String getNarrative() {
		
		if(narrative != null){
			return this.narrative;
		}
		
		if(this.getMobileNetworkOperator() == null){
			this.setMobileNetworkOperator(MobileNetworkOperator.ECONET);
		}
		if (TransactionType.BILLPAY.equals(this.getTransactionType()) || TransactionType.EWALLET_BILLPAY.equals(this.getTransactionType())) {
			narrative = this.getUtilityName() + "/" + this.getCustomerUtilityAccount() + "/" + this.getSourceMobileNumber();
		}
		else if (TransactionType.TOPUP.equals(this.getTransactionType()) || TransactionType.EWALLET_TOPUP.equals(this.getTransactionType())) {
			narrative = this.getMobileNetworkOperator() + "/" + this.getSourceMobileNumber() + "/" + this.getTargetMobileNumber();
		}
		else if (TransactionType.BALANCE.equals(this.getTransactionType()) || TransactionType.MINI_STATEMENT.equals(this.getTransactionType())) {
			narrative = this.getMobileNetworkOperator() + "/" + this.getSourceMobileNumber();
		} 
		else if (TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER.equals(this.getTransactionType())) {
			narrative = this.getMobileNetworkOperator() + "/" + this.getSourceMobileNumber();
		} 
		else if (TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER.equals(this.getTransactionType())) {
			narrative = this.getMobileNetworkOperator() + "/" + this.getSourceMobileNumber() + "/" + this.getTargetMobileNumber(); 
		} 
		else if (TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER.equals(this.getTransactionType())) {
			narrative = this.getSourceMobileNumber() + "/" + this.getTargetMobileNumber();
		} 
		else if (TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER.equals(this.getTransactionType())) {
			narrative = this.getMobileNetworkOperator() + "/" + this.getSourceMobileNumber();
		} 
		else if (TransactionType.EWALLET_TO_EWALLET_TRANSFER.equals(this.getTransactionType())) {
			narrative = this.getMobileNetworkOperator() + "/" + this.getSourceMobileNumber() + "/" + this.getTargetMobileNumber();
		} 
		else if (TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER.equals(this.getTransactionType())) {
			narrative = this.getSourceMobileNumber() + "/" + this.getTargetMobileNumber();
		}		
		else if (TransactionType.WITHDRAWAL.equals(this.getTransactionType())) {
			narrative = this.getMobileNetworkOperator() + "/" + this.getSourceMobileNumber();
		} 
		else if (TransactionType.WITHDRAWAL_NONHOLDER.equals(this.getTransactionType())) {
			narrative = this.getTargetMobileNumber() + "/" + this.getSourceMobileNumber();
		} 
		else if (TransactionType.DEPOSIT.equals(this.getTransactionType())) {
			narrative = this.getMobileNetworkOperator() + "/" + this.getSourceMobileNumber();
		} 

		return narrative;
	}

	public String getCurrencyISONumeric() {
		return currencyISONumeric;
	}

	public void setCurrencyISONumeric(String currencyISONumeric) {
		this.currencyISONumeric = currencyISONumeric;
	}

	public String getCurrencyISOAlphabetic() {
		return currencyISOAlphabetic;
	}

	public void setCurrencyISOAlphabetic(String currencyISOAlphabetic) {
		this.currencyISOAlphabetic = currencyISOAlphabetic;
	}

	/**
	 * @return the processingCode
	 */
	public String getProcessingCode() {
		return processingCode;
	}

	/**
	 * @param processingCode
	 *            the processingCode to set
	 */
	public void setProcessingCode(String processingCode) {
		this.processingCode = processingCode;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the systemTraceAuditNumber
	 */
	public long getSystemTraceAuditNumber() {
		return systemTraceAuditNumber;
	}

	/**
	 * @param systemTraceAuditNumber
	 *            the systemTraceAuditNumber to set
	 */
	public void setSystemTraceAuditNumber(long systemTraceAuditNumber) {
		this.systemTraceAuditNumber = systemTraceAuditNumber;
	}

	/**
	 * @return the messageType
	 */
	public String getMessageType() {
		return messageType;
	}

	/**
	 * @param messageType
	 *            the messageType to set
	 */
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	/**
	 * @return the responseCode
	 */
	public BankResponseCode getResponseCode() {
		return bankResponseCode;
	}

	/**
	 * @param bankResponseCode
	 *            the responseCode to set
	 */
	public void setResponseCode(BankResponseCode bankResponseCode) {
		this.bankResponseCode = bankResponseCode;
	}

	/**
	 * @return the transactionType
	 */
	public TransactionType getTransactionType() {
		return transactionType;
	}

	/**
	 * @param transactionType
	 *            the transactionType to set
	 */
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	/**
	 * @return the ledgerBalance
	 */
	public long getLedgerBalance() {
		return ledgerBalance;
	}

	/**
	 * @param ledgerBalance
	 *            the ledgerBalance to set
	 */
	public void setLedgerBalance(long ledgerBalance) {
		this.ledgerBalance = ledgerBalance;
	}

	/**
	 * @return the additionalData
	 */
	public String getAdditionalData() {
		return additionalData;
	}

	/**
	 * @param additionalData
	 *            the additionalData to set
	 */
	public void setAdditionalData(String additionalData) {
		this.additionalData = additionalData;
	}

	/**
	 * @return the bankResponseCode
	 */
	public BankResponseCode getBankResponseCode() {
		return bankResponseCode;
	}

	/**
	 * @param bankResponseCode the bankResponseCode to set
	 */
	public void setBankResponseCode(BankResponseCode bankResponseCode) {
		this.bankResponseCode = bankResponseCode;
	}

	/**
	 * @return the commissionMessage
	 */
	public List<CommissionMessage> getCommissionInfo() {
		return commissionMessage;
	}

	/**
	 * @param commissionMessage the commissionMessage to set
	 */
	public void setCommissionInfo(List<CommissionMessage> commissionMessage) {
		this.commissionMessage = commissionMessage;
	}

	/**
	 * @return the mobileNetworkOperator
	 */
	public MobileNetworkOperator getMobileNetworkOperator() {
		return mobileNetworkOperator == null ? MobileNetworkOperator.ECONET : this.mobileNetworkOperator;
	}

	/**
	 * @param mobileNetworkOperator the mobileNetworkOperator to set
	 */
	public void setMobileNetworkOperator(MobileNetworkOperator mobileNetworkOperator) {
		this.mobileNetworkOperator = mobileNetworkOperator;
	}

	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}

	public long getTotalChargesAmount() {
		return totalChargesAmount;
	}

	public void setTotalChargesAmount(long totalChargesAmount) {
		this.totalChargesAmount = totalChargesAmount;
	}

	public String getBeneficiaryName() {
		return beneficiaryName;
	}

	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}

	public String getDestinationBankName() {
		return destinationBankName;
	}

	public void setDestinationBankName(String destinationBankName) {
		this.destinationBankName = destinationBankName;
	}

	public String getPaymentRef() {
		return paymentRef;
	}

	public void setPaymentRef(String paymentRef) {
		this.paymentRef = paymentRef;
	}

	public void setBouquetCode(String bouquetCode) {
		this.bouquetCode = bouquetCode;
	}

	public String getBouquetCode() {
		return bouquetCode;
	}

}
