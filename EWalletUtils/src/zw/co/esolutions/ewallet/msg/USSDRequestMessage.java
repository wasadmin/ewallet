/**
 * 
 */
package zw.co.esolutions.ewallet.msg;

import java.io.Serializable;

import zw.co.esolutions.ewallet.enums.MobileNetworkOperator;
import zw.co.esolutions.ewallet.enums.TransactionLocationType;
import zw.co.esolutions.ewallet.enums.USSDTransactionType;

/**
 * @author blessing
 *
 */
public class USSDRequestMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1128404496565326891L;
	
	/**
	 * required.
	 */
	private String uuid;
	/**
	 * required
	 * 
	 */
	private String sourceMobileNumber;
	/**
	 * required for topup
	 */
	private String targetMobileNumber;
	
	/**
	 * required
	 */
	private String sourceBankAccount;
	/**
	 * required for transfer
	 */
	private String targetBankAccount;
	
	/**
	 * required for Bill payment
	 */
	private String utilityName;
	private long amount;
	private String customerUtilityAccount;
	
	/**
	 * really required.
	 */
	private String sourceBankId;
	private String targetBankId;
	
	/**
	 * required to identify the txn 
	 * origin. May be required when more MNOs join
	 */
	private USSDTransactionType transactionType;
	private final TransactionLocationType transactionLocationType = TransactionLocationType.USSD;
	private MobileNetworkOperator mno;
	
	/**
	 * required for pin change requests.
	 */
	private String oldPin;
	private String newPin;
	
	/**
	 * @return the sourceMobileNumber
	 */
	
	private String secretCode;
	private String agentNumber;
	private String refCode;
	private String ddmmyyDate; 
	private String paymentRef;
	private String beneficiaryName;
	private String destinationBankName;
	
	//Used for 6* or 2* value is either 6 0r 2
	String bankAlias;
	
	public String getAgentNumber() {
		return agentNumber;
	}
	public void setAgentNumber(String agentNumber) {
		this.agentNumber = agentNumber;
	}
	
	public String getSourceMobileNumber() {
		return sourceMobileNumber;
	}
	/**
	 * @param sourceMobileNumber the sourceMobileNumber to set
	 */
	public void setSourceMobileNumber(String sourceMobileNumber) {
		this.sourceMobileNumber = sourceMobileNumber;
	}
	/**
	 * @return the targetMobileNumber
	 */
	public String getTargetMobileNumber() {
		return targetMobileNumber;
	}
	/**
	 * @param targetMobileNumber the targetMobileNumber to set
	 */
	public void setTargetMobileNumber(String targetMobileNumber) {
		this.targetMobileNumber = targetMobileNumber;
	}
	/**
	 * @return the sourceBankAccount
	 */
	public String getSourceBankAccount() {
		return sourceBankAccount;
	}
	/**
	 * @param sourceBankAccount the sourceBankAccount to set
	 */
	public void setSourceBankAccount(String sourceBankAccount) {
		this.sourceBankAccount = sourceBankAccount;
	}
	/**
	 * @return the targetBankAccount
	 */
	public String getTargetBankAccount() {
		return targetBankAccount;
	}
	/**
	 * @param targetBankAccount the targetBankAccount to set
	 */
	public void setTargetBankAccount(String targetBankAccount) {
		this.targetBankAccount = targetBankAccount;
	}
	/**
	 * @return the utilityName
	 */
	public String getUtilityName() {
		return utilityName;
	}
	/**
	 * @param utilityName the utilityName to set
	 */
	public void setUtilityName(String utilityName) {
		this.utilityName = utilityName;
	}
	/**
	 * @return the customerUtilityAccount
	 */
	public String getCustomerUtilityAccount() {
		return customerUtilityAccount;
	}
	/**
	 * @param customerUtilityAccount the customerUtilityAccount to set
	 */
	public void setCustomerUtilityAccount(String customerUtilityAccount) {
		this.customerUtilityAccount = customerUtilityAccount;
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
	/**
	 * @return the targetBankId
	 */
	public String getTargetBankId() {
		return targetBankId;
	}
	/**
	 * @param targetBankId the targetBankId to set
	 */
	public void setTargetBankId(String targetBankId) {
		this.targetBankId = targetBankId;
	}
	
	/**
	 * @return the mno
	 */
	public MobileNetworkOperator getMno() {
		return mno;
	}
	/**
	 * @param mno the mno to set
	 */
	public void setMno(MobileNetworkOperator mno) {
		this.mno = mno;
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
	 * @return the transactionLocationType
	 */
	public TransactionLocationType getTransactionLocationType() {
		return transactionLocationType;
	}
	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}
	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	/**
	 * @return the transactionType
	 */
	public USSDTransactionType getTransactionType() {
		return transactionType;
	}
	/**
	 * @param transactionType the transactionType to set
	 */
	public void setTransactionType(USSDTransactionType transactionType) {
		this.transactionType = transactionType;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public void setSecretCode(String secretCode) {
		this.secretCode = secretCode;
	}
	public String getSecretCode() {
		return secretCode;
	}
	public void setRefCode(String refCode) {
		this.refCode = refCode;
	}
	public String getRefCode() {
		return refCode;
	}
	public void setDdmmyyDate(String ddmmyyDate) {
		this.ddmmyyDate = ddmmyyDate;
	}
	public String getDdmmyyDate() {
		return ddmmyyDate;
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
	public String getBankAlias() {
		return bankAlias;
	}
	public void setBankAlias(String bankAlias) {
		this.bankAlias = bankAlias;
	}
	
	
}
