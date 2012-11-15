package zw.co.esolutions.ewallet.bankservices.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;

import zw.co.esolutions.ewallet.enums.TransactionCategory;
import zw.co.esolutions.ewallet.enums.TransactionType;

/**
 * Entity implementation class for Entity: Transaction
 * 
 */
@Entity
@NamedQueries({ @NamedQuery(name = "getTransaction", query = "SELECT t FROM Transaction t"), @NamedQuery(name = "getTransactionByProcessMessageId", query = "SELECT t FROM Transaction t " + "WHERE t.processTxnReference = :messageId ORDER BY t.dateCreated ASC"),
@NamedQuery(name = "getTransactionByAccountIdAndTxnRef", query = "SELECT t FROM Transaction t where t.accountId = :accountId AND t.processTxnReference = :processTxnReference"),
@NamedQuery(name = "getTransactionSummaryItemByTransactionTypeAndDateRange", query = "SELECT NEW zw.co.esolutions.ewallet.bankservices.model.TransactionSummaryItem(t.type, COUNT(t), SUM(t.amount)) FROM Transaction t WHERE t.accountId = :accountId AND t.type = :txnType AND t.dateCreated >= :txnStartDate AND t.dateCreated <= :txnEndDate GROUP BY t.type") })
public class Transaction implements Serializable {

	@Id
	@Column(length = 30)
	private String id;
	@Enumerated(EnumType.STRING)
	@Column(length = 50)
	private TransactionType type;
	@Column(length = 250)
	private String narrative;
	@Column(length = 30)
	private String accountId;
	private long amount;
	private Date transactionDate;
	@Column(length = 30)
	private String processTxnReference;
	private Date dateCreated;
	@Column(length = 30)
	private String oldMessageId;
	@Enumerated(EnumType.STRING)
	@Column(length = 50)
	private TransactionCategory transactionCategory;
	@Version
	private long version;

	public TransactionType getType() {
		return type;
	}

	public void setType(TransactionType type) {
		this.type = type;
	}

	public String getNarrative() {
		if(this.narrative == null) {
			if(this.amount < 0) {
				this.narrative = "Debit";
			} else {
				this.narrative = "Credit";
			}
		}
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

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	private static final long serialVersionUID = 1L;

	public Transaction() {
		super();
	}

	public Transaction(String id, TransactionType type, String narrative, String accountId, long amount, Date transactionDate, String processTxnReference, Date dateCreated, String oldMessageId, TransactionCategory transactionCategory) {
		super();
		this.id = id;
		this.type = type;
		this.narrative = narrative;
		this.accountId = accountId;
		this.amount = amount;
		this.transactionDate = transactionDate;
		this.processTxnReference = processTxnReference;
		this.dateCreated = dateCreated;
		this.oldMessageId = oldMessageId;
		this.transactionCategory = transactionCategory;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setProcessTxnReference(String processTxnReference) {
		this.processTxnReference = processTxnReference;
	}

	public String getProcessTxnReference() {
		return processTxnReference;
	}

	public void setOldMessageId(String oldMessageId) {
		this.oldMessageId = oldMessageId;
	}

	public String getOldMessageId() {
		return oldMessageId;
	}

	public void setTransactionCategory(TransactionCategory transactionCategory) {
		this.transactionCategory = transactionCategory;
	}

	public TransactionCategory getTransactionCategory() {
		return transactionCategory;
	}

}
