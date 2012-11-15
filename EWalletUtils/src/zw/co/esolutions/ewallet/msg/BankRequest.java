package zw.co.esolutions.ewallet.msg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import zw.co.esolutions.ewallet.enums.MobileNetworkOperator;
import zw.co.esolutions.ewallet.enums.TransactionType;

public class BankRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long amount;
	private String sourceMobileNumber;
	private String targetMobileNumber;
	private String reference;
	private String sourceBankCode;
	private MobileNetworkOperator mobileNetworkOperator;

	private List<Commission> commissions;
	private zw.co.esolutions.ewallet.enums.TransactionType transactionType;
	private long referrerCommission;
	private long referredCommission;
	private String sourceAccountNumber;
	private String targetAccountNumber;
	private boolean reversal;
	private String acquirerId;
	private String currencyISOCode;
	private String customerUtilityAccount;
	private String secretCode;
	private String cardNumber;
	private String cardSequence;
	private String targetBankCode;
	private String utilityName;
	private String narrative;
	private String agentNumber;
	private String originalTxnReference;
	private String paymentRef;
	private String beneficiaryName;
	private String targetBankName;
	private String bouquetCode;
	
	public BankRequest() {
		super();
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
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

	public String getSourceAccountNumber() {
		return sourceAccountNumber;
	}

	public void setSourceAccountNumber(String sourceAccountNumber) {
		this.sourceAccountNumber = sourceAccountNumber;
	}

	public zw.co.esolutions.ewallet.enums.TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(zw.co.esolutions.ewallet.enums.TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	@Override
	public String toString() {
		return this.getSourceBankCode() + "|" + getTransactionType().toString() + "|" + this.getTargetAccountNumber() + "|" + this.getSourceAccountNumber() + "|" + this.getSourceMobileNumber() + "/" + this.getTargetMobileNumber() + "|" + this.getAmount();
	}

	/**
	 * @return the reversal
	 */
	public boolean isReversal() {
		return reversal;
	}

	/**
	 * @param reversal
	 *            the reversal to set
	 */
	public void setReversal(boolean reversal) {
		this.reversal = reversal;
	}

	/**
	 * @return the acquirerId
	 */
	public String getAcquirerId() {
		return acquirerId;
	}

	/**
	 * @param acquirerId
	 *            the acquirerId to set
	 */
	public void setAcquirerId(String acquirerId) {
		this.acquirerId = acquirerId;
	}

	public String getCurrencyISOCode() {
		return currencyISOCode;
	}

	/**
	 * @param currencyISOCode
	 *            the currencyISOCode to set
	 */
	public void setCurrencyISOCode(String currencyISOCode) {
		this.currencyISOCode = currencyISOCode;
	}

	/**
	 * @return the customerUtilityAccount
	 */
	public String getCustomerUtilityAccount() {
		return customerUtilityAccount;
	}

	/**
	 * @param customerUtilityAccount
	 *            the customerUtilityAccount to set
	 */
	public void setCustomerUtilityAccount(String customerUtilityAccount) {
		this.customerUtilityAccount = customerUtilityAccount;
	}

	/**
	 * @return the secretCode
	 */
	public String getSecretCode() {
		return secretCode;
	}

	/**
	 * @param secretCode
	 *            the secretCode to set
	 */
	public void setSecretCode(String secretCode) {
		this.secretCode = secretCode;
	}

	/**
	 * @return the cardNumber
	 */
	public String getCardNumber() {
		return cardNumber;
	}

	/**
	 * @param cardNumber
	 *            the cardNumber to set
	 */
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	/**
	 * @return the cardSequence
	 */
	public String getCardSequence() {
		return cardSequence;
	}

	/**
	 * @param cardSequence
	 *            the cardSequence to set
	 */
	public void setCardSequence(String cardSequence) {
		this.cardSequence = cardSequence;
	}

	/**
	 * @return the targetBankCode
	 */
	public String getTargetBankCode() {
		return targetBankCode;
	}

	/**
	 * @param targetBankCode
	 *            the targetBankCode to set
	 */
	public void setTargetBankCode(String targetBankCode) {
		this.targetBankCode = targetBankCode;
	}

	/**
	 * @return the utilityName
	 */
	public String getUtilityName() {
		return utilityName;
	}

	/**
	 * @param utilityName
	 *            the utilityName to set
	 */
	public void setUtilityName(String utilityName) {
		this.utilityName = utilityName;
	}

	/**
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * @param reference
	 *            the reference to set
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}

	/**
	 * @return the sourceMobileNumber
	 */
	public String getSourceMobileNumber() {
		return sourceMobileNumber;
	}

	/**
	 * @param sourceMobileNumber
	 *            the sourceMobileNumber to set
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
	 * @param targetMobileNumber
	 *            the targetMobileNumber to set
	 */
	public void setTargetMobileNumber(String targetMobileNumber) {
		this.targetMobileNumber = targetMobileNumber;
	}

	/**
	 * @return the sourceBankCode
	 */
	public String getSourceBankCode() {
		return sourceBankCode;
	}

	/**
	 * @param sourceBankCode
	 *            the sourceBankCode to set
	 */
	public void setSourceBankCode(String sourceBankCode) {
		this.sourceBankCode = sourceBankCode;
	}

	/**
	 * @return the targetAccountNumber
	 */
	public String getTargetAccountNumber() {
		return targetAccountNumber;
	}

	/**
	 * @param targetAccountNumber
	 *            the targetAccountNumber to set
	 */
	public void setTargetAccountNumber(String targetAccountNumber) {
		this.targetAccountNumber = targetAccountNumber;
	}

	public String getNarrative() {
		if(narrative != null){
			return narrative;
		}
		if(TransactionType.AGENT_CASH_DEPOSIT.equals(this.getTransactionType())){
			narrative = this.getAgentNumber()+ "/" + this.getSourceMobileNumber() + "/" + this.getTargetMobileNumber();
		}
		if(TransactionType.AGENT_CUSTOMER_DEPOSIT.equals(this.getTransactionType())){
			narrative = this.getAgentNumber()+ "/" + this.getSourceMobileNumber() + "/" + this.getTargetMobileNumber();
		}
		if(TransactionType.AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL.equals(this.getTransactionType())){
			narrative = this.getAgentNumber()+ "/" + this.getSourceMobileNumber() + "/" + this.getTargetMobileNumber() + "/" + this.getOriginalTxnReference();
		}
		if(TransactionType.AGENT_CUSTOMER_WITHDRAWAL.equals(this.getTransactionType())){
			narrative = this.getAgentNumber()+ "/" + this.getSourceMobileNumber() + "/" + this.getTargetMobileNumber();
		}
		if(TransactionType.AGENT_EMONEY_TRANSFER.equals(this.getTransactionType())){
			narrative = this.getAgentNumber()+ "/" + this.getSourceMobileNumber() + "/" + this.getTargetMobileNumber();
		}
		if(TransactionType.AGENT_EWALLET_TO_BANKACCOUNT_TRANSFER.equals(this.getTransactionType())){
			narrative = this.getAgentNumber()+ "/" + this.getSourceMobileNumber();
		}
		if(TransactionType.AGENT_CUSTOMER_WITHDRAWAL.equals(this.getTransactionType())){
			narrative = this.getAgentNumber()+ "/" + this.getSourceMobileNumber() + "/" + this.getTargetMobileNumber();
		}
		else if (TransactionType.BILLPAY.equals(this.getTransactionType()) || TransactionType.EWALLET_BILLPAY.equals(this.getTransactionType())) {
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
			narrative = this.getMobileNetworkOperator() + "/" + this.getSourceMobileNumber() + "/" + this.getTargetMobileNumber();
		} 
		else if (TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER.equals(this.getTransactionType())) {
			narrative = this.getMobileNetworkOperator() + "/" + this.getSourceMobileNumber();
		} 
		else if (TransactionType.EWALLET_TO_EWALLET_TRANSFER.equals(this.getTransactionType())) {
			narrative = this.getMobileNetworkOperator() + "/" + this.getSourceMobileNumber() + "/" + this.getTargetMobileNumber();
		} 
		else if (TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER.equals(this.getTransactionType())) {
			narrative = this.getMobileNetworkOperator() + "/" + this.getSourceMobileNumber() + "/" + this.getTargetMobileNumber();
		}		
		else if (TransactionType.WITHDRAWAL.equals(this.getTransactionType())) {
			narrative = this.getMobileNetworkOperator() + "/" + this.getSourceMobileNumber();
		} 
		else if (TransactionType.WITHDRAWAL_NONHOLDER.equals(this.getTransactionType())) {
			narrative = this.getMobileNetworkOperator() + "/" + this.getSourceMobileNumber() + "/" + this.getOriginalTxnReference();
		} 
		else if (TransactionType.DEPOSIT.equals(this.getTransactionType())) {
			narrative = this.getMobileNetworkOperator() + "/" + this.getSourceMobileNumber();
		} 

		return narrative;
	}

	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}

	/**
	 * @return the commissions
	 */
	public List<Commission> getCommissions() {
		return commissions;
	}

	/**
	 * @param commissions the commissions to set
	 */
	public void setCommissions(List<Commission> commissions) {
		this.commissions = commissions;
	}
	
	public boolean addCommission(Commission commission){
		if(commissions == null){
			commissions = new ArrayList<Commission>();
		}
		return commissions.add(commission);
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

	public void setOriginalTxnReference(String originalTxnReference) {
		this.originalTxnReference = originalTxnReference;
	}

	public String getOriginalTxnReference() {
		return originalTxnReference;
	}
	
	public long getTotalCommissionAmount(){
		long total = 0L;
		if(this.getCommissions() != null){
			for(Commission commission : this.getCommissions()){
				total = total + commission.getCommissionAmount();
			}
		}
		System.out.println("Total charges amount is " + total);
		return total;
	}

	public String getAgentNumber() {
		return agentNumber;
	}

	public void setAgentNumber(String agentNumber) {
		this.agentNumber = agentNumber;
	}

	public void setPaymentRef(String paymentRef) {
		this.paymentRef = paymentRef;
	}

	public String getPaymentRef() {
		return paymentRef;
	}

	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}

	public String getBeneficiaryName() {
		return beneficiaryName;
	}

	public void setTargetBankName(String targetBankName) {
		this.targetBankName = targetBankName;
	}

	public String getTargetBankName() {
		return targetBankName;
	}

	public void setBouquetCode(String bouquetCode) {
		this.bouquetCode = bouquetCode;
	}

	public String getBouquetCode() {
		return bouquetCode;
	}
}
