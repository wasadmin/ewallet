package zw.co.esolutions.ewallet.reports.util;

import java.util.Date;

import zw.co.esolutions.ewallet.enums.TransactionType;

public class AccountStatementPojo {

	TransactionType transactionType;
	long amount;
	Date valueDate;
	String narrative;
	String messageId;
	long sumBefore;
	String accountId;
	
	public TransactionType getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public Date getValueDate() {
		return valueDate;
	}
	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}
	public String getNarrative() {
		return narrative;
	}
	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public long getSumBefore() {
		return sumBefore;
	}
	public void setSumBefore(long sumBefore) {
		this.sumBefore = sumBefore;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	
}
