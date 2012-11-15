package zw.co.esolutions.ewallet.bankservices.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

/**
 * Entity implementation class for Entity: AccountBalance
 * 
 */
@Entity
@NamedQueries({ @NamedQuery(name = "getAccountBalance", query = "SELECT a FROM AccountBalance a"),
@NamedQuery(name = "getRunningBalance", query = "SELECT a FROM AccountBalance a WHERE a.accountId = :accountId ORDER BY a.dateCreated DESC, a.postingSequence DESC") })
public class AccountBalance implements Serializable {

	@Id
	@Column(length = 30)
	private String id;
	
	@Column(length = 30)
	private String accountId;
	
	private int postingSequence;
	
	private long amount;
	private Date balanceDate;
	private Date dateCreated;
	@Column(length = 30)
	private String transactionRef;
	@Version
	private long version;

	private static final long serialVersionUID = 1L;

	public AccountBalance() {
		super();
	}

	public AccountBalance(String id, String accountId, long amount, Date balanceDate, Date dateCreated, String transactionRef, int postingSequence) {
		super();
		this.id = id;
		this.accountId = accountId;
		this.amount = amount;
		this.balanceDate = balanceDate;
		this.dateCreated = dateCreated;
		this.transactionRef = transactionRef;
		this.postingSequence = postingSequence;
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

	public Date getBalanceDate() {
		return balanceDate;
	}

	public void setBalanceDate(Date balanceDate) {
		this.balanceDate = balanceDate;
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

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setTransactionRef(String transactionRef) {
		this.transactionRef = transactionRef;
	}

	public int getPostingSequence() {
		return postingSequence;
	}

	public void setPostingSequence(int postingSequence) {
		this.postingSequence = postingSequence;
	}

	public String getTransactionRef() {
		return transactionRef;
	}
	
	@Override
	public String toString() {
		return this.getId() + "/"+this.getAccountId() +"/"+this.getAmount() +"/"+this.getBalanceDate() + "/"+this.getDateCreated() +"/"+this.getPostingSequence() +"/"+this.getTransactionRef() +"/"+ this.getVersion();
	}
}
