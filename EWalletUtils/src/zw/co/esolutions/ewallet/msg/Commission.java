/**
 * 
 */
package zw.co.esolutions.ewallet.msg;

import java.io.Serializable;

import zw.co.esolutions.ewallet.enums.TransactionType;

/**
 * @author blessing
 * 
 */
public class Commission implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6516879643005031810L;
	private String reference;
	private String txnReference;
	private long commissionAmount;
	private String sourceCustomerAccount;
	private String sourceEq3Account;
	private String targetEq3Account;
	private String narrative;
	private TransactionType transactionType;
	private String agentNumber;
	
	public Commission(String reference, String txnReference, long commissionAmount, String sourceCustomerAccount, String sourceEq3Account, String targetEq3Account, String narrative, TransactionType transactionType, String agentNumber) {
		super();
		this.reference = reference;
		this.txnReference = txnReference;
		this.commissionAmount = commissionAmount;
		this.sourceCustomerAccount = sourceCustomerAccount;
		this.sourceEq3Account = sourceEq3Account;
		this.targetEq3Account = targetEq3Account;
		this.narrative = narrative;
		this.transactionType = transactionType;
		this.setAgentNumber(agentNumber);
	}

	@Override
	public String toString() {
		return this.getTransactionType() + " " + this.getSourceCustomerAccount() + " " + this.getReference() + " " + this.getTxnReference() + " " + this.getSourceEq3Account() + " " + this.getTargetEq3Account() + " " + this.getCommissionAmount();
	}

	/**
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * @return the txnReference
	 */
	public String getTxnReference() {
		return txnReference;
	}

	/**
	 * @return the commissionAmount
	 */
	public long getCommissionAmount() {
		return commissionAmount;
	}

	/**
	 * * @return the transactionType
	 */
	public TransactionType getTransactionType() {
		return transactionType;
	}

	/**
	 * @return the sourceCustomerAccount
	 */
	public String getSourceCustomerAccount() {
		return sourceCustomerAccount;
	}

	/**
	 * @param sourceCustomerAccount the sourceCustomerAccount to set
	 */
	public void setSourceCustomerAccount(String sourceCustomerAccount) {
		this.sourceCustomerAccount = sourceCustomerAccount;
	}

	/**
	 * @return the sourceEq3Account
	 */
	public String getSourceEq3Account() {
		return sourceEq3Account;
	}

	/**
	 * @param sourceEq3Account the sourceEq3Account to set
	 */
	public void setSourceEq3Account(String sourceEq3Account) {
		this.sourceEq3Account = sourceEq3Account;
	}

	/**
	 * @return the targetEq3Account
	 */
	public String getTargetEq3Account() {
		return targetEq3Account;
	}

	/**
	 * @param targetEq3Account the targetEq3Account to set
	 */
	public void setTargetEq3Account(String targetEq3Account) {
		this.targetEq3Account = targetEq3Account;
	}

	/**
	 * @return the narrative
	 */
	public String getNarrative() {
		return narrative;
	}

	/**
	 * @param narrative the narrative to set
	 */
	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}

	/**
	 * @param reference the reference to set
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}

	/**
	 * @param txnReference the txnReference to set
	 */
	public void setTxnReference(String txnReference) {
		this.txnReference = txnReference;
	}

	/**
	 * @param commissionAmount the commissionAmount to set
	 */
	public void setCommissionAmount(long commissionAmount) {
		this.commissionAmount = commissionAmount;
	}

	/**
	 * @param transactionType the transactionType to set
	 */
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public void setAgentNumber(String agentNumber) {
		this.agentNumber = agentNumber;
	}

	public String getAgentNumber() {
		return agentNumber;
	}

}
