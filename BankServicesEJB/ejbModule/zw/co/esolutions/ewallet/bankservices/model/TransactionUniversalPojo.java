package zw.co.esolutions.ewallet.bankservices.model;

import java.io.Serializable;
import java.util.Date;

import zw.co.esolutions.ewallet.enums.TransactionCategory;
import zw.co.esolutions.ewallet.enums.TransactionType;

public class TransactionUniversalPojo implements Serializable {


	private TransactionType type;
	private String narrative;
	private String accountId;
	private Date transactionDate;
	private String processTxnReference;
	private Date fromDate;
	private Date toDate;
	private String oldMessageId;
	private TransactionCategory transactionCategory;
	/**
	 * 
	 */
	private static final long serialVersionUID = -7729802964838211718L;
	public TransactionType getType() {
		return type;
	}
	public void setType(TransactionType type) {
		this.type = type;
	}
	public String getNarrative() {
		return narrative;
	}
	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public Date getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}
	public String getProcessTxnReference() {
		return processTxnReference;
	}
	public void setProcessTxnReference(String processTxnReference) {
		this.processTxnReference = processTxnReference;
	}
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
	public String getOldMessageId() {
		return oldMessageId;
	}
	public void setOldMessageId(String oldMessageId) {
		this.oldMessageId = oldMessageId;
	}
	public void setTransactionCategory(TransactionCategory transactionCategory) {
		this.transactionCategory = transactionCategory;
	}
	public TransactionCategory getTransactionCategory() {
		return transactionCategory;
	}


}
