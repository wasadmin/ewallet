package zw.co.esolutions.ewallet.bankservices.model;

import java.io.Serializable;
import java.util.Date;

import zw.co.esolutions.ewallet.enums.TransactionCategory;
import zw.co.esolutions.ewallet.enums.TransactionType;

public class TransactionPostingInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String targetAccountNumber;
	private String sourceAccountNumber;
	private String srcAccountId;
	private String targetAccountId;
	private TransactionType txnType;
	private long amount;
	private String srcNarrative;
	private String targetNarrative;
	private String txnRef;
	private String originalTxnRef;
	private TransactionCategory txnCategory;
	private Date transactionDate;

	private ChargePostingInfo chargePostingInfo ;
	
	public String getTargetAccountNumber() {
		return targetAccountNumber;
	}

	public void setTargetAccountNumber(String targetAccountNumber) {
		this.targetAccountNumber = targetAccountNumber;
	}

	public String getSourceAccountNumber() {
		return sourceAccountNumber;
	}

	public void setSourceAccountNumber(String sourceAccountNumber) {
		this.sourceAccountNumber = sourceAccountNumber;
	}

	public String getSrcAccountId() {
		return srcAccountId;
	}

	public void setSrcAccountId(String srcAccountId) {
		this.srcAccountId = srcAccountId;
	}

	public String getTargetAccountId() {
		return targetAccountId;
	}

	public void setTargetAccountId(String targetAccountId) {
		this.targetAccountId = targetAccountId;
	}

	public TransactionType getTxnType() {
		return txnType;
	}

	public void setTxnType(TransactionType txnType) {
		this.txnType = txnType;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public String getTxnRef() {
		return txnRef;
	}

	public void setTxnRef(String txnRef) {
		this.txnRef = txnRef;
	}

	public String getOriginalTxnRef() {
		return originalTxnRef;
	}

	public void setOriginalTxnRef(String originalTxnRef) {
		this.originalTxnRef = originalTxnRef;
	}

	public TransactionCategory getTxnCategory() {
		return txnCategory;
	}

	public void setTxnCategory(TransactionCategory txnCategory) {
		this.txnCategory = txnCategory;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public ChargePostingInfo getChargePostingInfo() {
		return chargePostingInfo;
	}

	public void setChargePostingInfo(ChargePostingInfo chargePostingInfo) {
		this.chargePostingInfo = chargePostingInfo;
	}

	public String getSrcNarrative() {
		return srcNarrative;
	}

	public void setSrcNarrative(String srcNarrative) {
		this.srcNarrative = srcNarrative;
	}

	public String getTargetNarrative() {
		return targetNarrative;
	}

	public void setTargetNarrative(String targetNarrative) {
		this.targetNarrative = targetNarrative;
	}
	
}
