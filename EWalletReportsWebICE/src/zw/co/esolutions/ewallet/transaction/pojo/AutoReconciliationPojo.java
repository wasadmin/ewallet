/**
 * 
 */
package zw.co.esolutions.ewallet.transaction.pojo;

import java.util.Date;

import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.reports.enums.TransactionMatchType;

/**
 * @author taurai
 *
 */
public class AutoReconciliationPojo {

	private long amount;
	private String transactionType;
	private String messageId;
	private TransactionMatchType matchType;
	private String narrative;
	private String matchTypeNarrative;
	private Date transactionDate;
	private TransactionStatus status;
	private String toAccount;
	private String fromAccount;
	private String postCode;
	private Date valueDate;
	/**
	 * 
	 */
	public AutoReconciliationPojo() {
		// TODO Auto-generated constructor stub
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}
	public String getNarrative() {
		return narrative;
	}
	
	public void setMatchType(TransactionMatchType matchType) {
		this.matchType = matchType;
	}
	public TransactionMatchType getMatchType() {
		return matchType;
	}
	public void setMatchTypeNarrative(String matchTypeNarrative) {
		this.matchTypeNarrative = matchTypeNarrative;
	}
	public String getMatchTypeNarrative() {
		return matchTypeNarrative;
	}
	
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}
	public Date getTransactionDate() {
		return transactionDate;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof AutoReconciliationPojo) {
			if(((AutoReconciliationPojo)obj).getMessageId().equalsIgnoreCase(this.getMessageId())) {
				return true;
			}
		}
		return false;
	}
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}
	@Override
	public String toString() {
		
		return "Ref : "+this.getMessageId()+", Amount In Cents = "+this.getAmount();
	}
	public void setStatus(TransactionStatus status) {
		this.status = status;
	}
	public TransactionStatus getStatus() {
		return status;
	}
	public String getToAccount() {
		return toAccount;
	}
	public void setToAccount(String toAccount) {
		this.toAccount = toAccount;
	}
	public String getFromAccount() {
		return fromAccount;
	}
	public void setFromAccount(String fromAccount) {
		this.fromAccount = fromAccount;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	public String getPostCode() {
		return postCode;
	}
	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}
	public Date getValueDate() {
		return valueDate;
	}
	

}
