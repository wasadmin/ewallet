package zw.co.esolutions.ewallet.msg;

import java.io.Serializable;
import java.util.Date;

import zw.co.esolutions.ewallet.enums.MobileNetworkOperator;
import zw.co.esolutions.ewallet.enums.TransactionLocationType;
import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.enums.TransactionType;

public class RequestInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String messageId;
	private TransactionType transactionType;
	private String sourceMobile;
	private String targetMobile;
	private long amount;
	private String passwordParts;
	private String secretCode;
	private String customerUtilityAccount;
	private String utilityName;
	private String bankCode;
	private String sourceMobileProfileId;
	private String bankPrefix;
	private String sourceBankId;
	private String targetBankId;
	private TransactionLocationType locationType;
	private String sourceAccountNumber;
	private String destinationAccountNumber;
	private MobileNetworkOperator mno;
	private TransactionStatus status;
	private String oldPin;
	private String newPin;
	private String profileId;
	private String bankMerchantId;
	private boolean requiresPhoneValidation;
	private String oldReference;
	private Date summaryDate;
	private String agentNumber;
	private String branchId;
	private String accountName;
	private Date dateCreated;
	private String paymentRef;
	private String beneficiaryName;
	private String destinationBankName;
	private String targetBankCode;
	private String bouquetName;
	private String bouquetCode;
	private int numberOfMonths;
	private long commission;
	
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

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public String getPasswordParts() {
		return passwordParts;
	}

	public void setPasswordParts(String passwordParts) {
		this.passwordParts = passwordParts;
	}

	public void setSecretCode(String secretCode) {
		this.secretCode = secretCode;
	}

	public String getSecretCode() {
		return secretCode;
	}

	public void setUtilityName(String utilityName) {
		this.utilityName = utilityName;
	}

	public String getUtilityName() {
		return utilityName;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankCode() {
		return bankCode;
	}

	public String getBankPrefix() {
		return bankPrefix;
	}

	public void setBankPrefix(String bankPrefix) {
		this.bankPrefix = bankPrefix;
	}

	@Override
	public String toString() {
		return "REQ INFO DATA : TXNTYPE " + getTransactionType() + " BANKCODE " + getBankCode() + " SRCMOB : " + getSourceMobile() + " TARMOB : " + getTargetMobile() + " SEC_CODE " + getSecretCode() + " AMT " + getAmount() + " PSWD " + getPasswordParts();
	}

	public TransactionLocationType getLocationType() {
		return locationType;
	}

	public void setLocationType(TransactionLocationType locationType) {
		this.locationType = locationType;
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

	public void setMno(MobileNetworkOperator mno) {
		this.mno = mno;
	}

	public MobileNetworkOperator getMno() {
		return mno;
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public void setStatus(TransactionStatus status) {
		this.status = status;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public String getCustomerUtilityAccount() {
		return customerUtilityAccount;
	}

	public void setCustomerUtilityAccount(String customerUtilityAccount) {
		this.customerUtilityAccount = customerUtilityAccount;
	}

	public String getSourceMobileProfileId() {
		return sourceMobileProfileId;
	}

	public void setSourceMobileProfileId(String sourceMobileProfileId) {
		this.sourceMobileProfileId = sourceMobileProfileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public String getProfileId() {
		return profileId;
	}

	/**
	 * @return the sourceBankId
	 */
	public String getSourceBankId() {
		return sourceBankId;
	}

	/**
	 * @param sourceBankId the sourceBankId to set
	 */
	public void setSourceBankId(String sourceBankId) {
		this.sourceBankId = sourceBankId;
	}

	public String getTargetBankId() {
		return targetBankId;
	}

	public void setTargetBankId(String targetBankId) {
		this.targetBankId = targetBankId;
	}

	/**
	 * @return the oldPin
	 */
	public String getOldPin() {
		return oldPin;
	}

	/**
	 * @param oldPin the oldPin to set
	 */
	public void setOldPin(String oldPin) {
		this.oldPin = oldPin;
	}

	/**
	 * @return the newPin
	 */
	public String getNewPin() {
		return newPin;
	}

	/**
	 * @param newPin the newPin to set
	 */
	public void setNewPin(String newPin) {
		this.newPin = newPin;
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

	public boolean isRequiresPhoneValidation() {
		return requiresPhoneValidation;
	}

	public void setRequiresPhoneValidation(boolean requiresPhoneValidation) {
		this.requiresPhoneValidation = requiresPhoneValidation;
	}

	public void setOldReference(String oldReference) {
		this.oldReference = oldReference;
	}

	public String getOldReference() {
		return oldReference;
	}

	public void setSummaryDate(Date summaryDate) {
		this.summaryDate = summaryDate;
	}

	public Date getSummaryDate() {
		return summaryDate;
	}

	public String getAgentNumber() {
		return agentNumber;
	}

	public void setAgentNumber(String agentNumber) {
		this.agentNumber = agentNumber;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateCreated() {
		return dateCreated;
	}
	
	public String getPaymentRef() {
		return paymentRef;
	}

	public void setPaymentRef(String paymentRef) {
		this.paymentRef = paymentRef;
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

	public void setTargetBankCode(String targetBankCode) {
		this.targetBankCode = targetBankCode;
	}

	public String getTargetBankCode() {
		return targetBankCode;
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

	public void setNumberOfMonths(Integer numberOfMonths) {
		this.numberOfMonths = numberOfMonths;
	}

	public Integer getNumberOfMonths() {
		return numberOfMonths;
	}

	public void setCommission(long commission) {
		this.commission = commission;
	}

	public long getCommission() {
		return commission;
	}


}
