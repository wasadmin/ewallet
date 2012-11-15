package zw.co.esolutions.mobile.banking.msg;

import java.io.Serializable;
import java.util.Date;

import zw.co.esolutions.mobile.banking.msg.enums.MobileNetworkOperator;
import zw.co.esolutions.mobile.banking.msg.enums.TransactionType;

public class TransactionRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6875255453199397757L;
	private String ussdSessionId;
	private TransactionType transactionType;
	private String sourceAccountNumber;
	private Date transactionTimestamp;
	private long transactionAmount;
	private String targetBankCode;
	private String targetAccountNumber;
	private String paymentReference;
	private String targetMobileNumber;
	private MobileNetworkOperator targetMobileNetworkOperator;
	private String targetMerchant;
	private String targetCustomerMerchantAccount;
	private String newPassword;
	private String agentNumber;
	private String beneficiaryName;
	private String secretCode;
		
	public TransactionRequest() {
		super();
		
	}
	public String getUssdSessionId() {
		return ussdSessionId;
	}
	public void setUssdSessionId(String ussdSessionId) {
		this.ussdSessionId = ussdSessionId;
	}
	public TransactionType getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}
	public String getSourceAccountNumber() {
		return sourceAccountNumber;
	}
	public void setSourceAccountNumber(String sourceAccountNumber) {
		this.sourceAccountNumber = sourceAccountNumber;
	}
	public Date getTransactionTimestamp() {
		return transactionTimestamp;
	}
	public void setTransactionTimestamp(Date transactionTimestamp) {
		this.transactionTimestamp = transactionTimestamp;
	}
	public long getTransactionAmount() {
		return transactionAmount;
	}
	public void setTransactionAmount(long transactionAmount) {
		this.transactionAmount = transactionAmount;
	}
	public String getTargetBankCode() {
		return targetBankCode;
	}
	public void setTargetBankCode(String targetBankCode) {
		this.targetBankCode = targetBankCode;
	}
	public String getTargetAccountNumber() {
		return targetAccountNumber;
	}
	public void setTargetAccountNumber(String targetAccountNumber) {
		this.targetAccountNumber = targetAccountNumber;
	}
	public String getPaymentReference() {
		return paymentReference;
	}
	public void setPaymentReference(String paymentReference) {
		this.paymentReference = paymentReference;
	}
	public String getTargetMobileNumber() {
		return targetMobileNumber;
	}
	public void setTargetMobileNumber(String targetMobileNumber) {
		this.targetMobileNumber = targetMobileNumber;
	}
	public MobileNetworkOperator getTargetMobileNetworkOperator() {
		return targetMobileNetworkOperator;
	}
	public void setTargetMobileNetworkOperator(
			MobileNetworkOperator targetMobileNetworkOperator) {
		this.targetMobileNetworkOperator = targetMobileNetworkOperator;
	}
	public String getTargetMerchant() {
		return targetMerchant;
	}
	public void setTargetMerchant(String targetMerchant) {
		this.targetMerchant = targetMerchant;
	}
	public String getTargetCustomerMerchantAccount() {
		return targetCustomerMerchantAccount;
	}
	public void setTargetCustomerMerchantAccount(
			String targetCustomerMerchantAccount) {
		this.targetCustomerMerchantAccount = targetCustomerMerchantAccount;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getAgentNumber() {
		return agentNumber;
	}
	public void setAgentNumber(String agentNumber) {
		this.agentNumber = agentNumber;
	}
	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}
	public String getBeneficiaryName() {
		return beneficiaryName;
	}
	public void setSecretCode(String secretCode) {
		this.secretCode = secretCode;
	}
	public String getSecretCode() {
		return secretCode;
	}

}
