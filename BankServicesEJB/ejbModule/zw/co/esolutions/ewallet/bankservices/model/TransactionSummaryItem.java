package zw.co.esolutions.ewallet.bankservices.model;

import java.io.Serializable;

import zw.co.esolutions.ewallet.enums.TransactionType;

public class TransactionSummaryItem implements Serializable{
	private static final long serialVersionUID = 2140069986719276735L;
	
	private TransactionType transactionType;
	private long numberOfTransactions;
	private long totalTxnValue;
	
	public TransactionSummaryItem(TransactionType transactionType, long numberOfTransactions, long totalTxnValue) {
		super();
		this.transactionType = transactionType;
		this.numberOfTransactions = numberOfTransactions;
		this.totalTxnValue = totalTxnValue;
	}

	public TransactionSummaryItem() {
		
	}
	
	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public long getNumberOfTransactions() {
		return numberOfTransactions;
	}

	public void setNumberOfTransactions(long numberOfTransactions) {
		this.numberOfTransactions = numberOfTransactions;
	}

	public long getTotalTxnValue() {
		return totalTxnValue;
	}

	public void setTotalTxnValue(long totalTxnValue) {
		this.totalTxnValue = totalTxnValue;
	}
	
	@Override
	public String toString() {
		return "TXN TYPE  : " + this.getTransactionType() + " VALUE/QTY" + this.getTotalTxnValue() +"/"+this.getNumberOfTransactions();
	}
}
