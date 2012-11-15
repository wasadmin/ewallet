package zw.co.esolutions.ewallet.process.model;

import java.io.Serializable;
import java.lang.String;
import javax.persistence.*;

import java.util.Date;
import java.util.List;

import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.process.model.DayEnd;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.FetchType;

/**
 * Entity implementation class for Entity: DayEndSummary
 *
 */
@Entity

@NamedQueries({@NamedQuery(name="getDayEndSummary", query = "SELECT d FROM DayEndSummary d"),@NamedQuery(name="getDayEndSummaryById", query = "SELECT d FROM DayEndSummary d WHERE d.id = :id"),
@NamedQuery(name="getDayEndSummaryByTransactionType", query = "SELECT d FROM DayEndSummary d WHERE d.transactionType = :transactionType"),
@NamedQuery(name="getDayEndSummaryByNumberOfTxn", query = "SELECT d FROM DayEndSummary d WHERE d.numberOfTxn = :numberOfTxn"),
@NamedQuery(name="getDayEndSummaryByValueOfTxns", query = "SELECT d FROM DayEndSummary d WHERE d.valueOfTxns = :valueOfTxns"),
@NamedQuery(name="getDayEndSummariesByDayEnd",query="SELECT d FROM DayEndSummary d WHERE d.dayEnd.dayEndId= : dayEndId"),
@NamedQuery(name="getDayEndSummaryByDayEnd", query = "SELECT d FROM DayEndSummary d WHERE d.dayEnd.dayEndId = :dayEnd_dayEndId")})
public class DayEndSummary implements Serializable {

	   
	@Id
	private String id;
	@Enumerated(EnumType.STRING)
	@Column(length = 50)
	private TransactionType transactionType;
	private long numberOfTxn;
	private long valueOfTxns;
	private static final long serialVersionUID = 1L;
	@Temporal(TemporalType.DATE)
	private Date dateCreated;
	@ManyToOne
	@JoinColumn(name="dayEnd")
	private DayEnd dayEnd;
	@OneToMany(fetch=FetchType.LAZY, mappedBy = "dayEndSummary")
	private List<ProcessTransaction> processtransactionList;
	public DayEndSummary() {
		super();
	}   
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}   
	
	public long getNumberOfTxn() {
		return this.numberOfTxn;
	}

	public void setNumberOfTxn(long numberOfTxn) {
		this.numberOfTxn = numberOfTxn;
	}   
	public long getValueOfTxns() {
		return this.valueOfTxns;
	}

	public void setValueOfTxns(long valueOfTxns) {
		this.valueOfTxns = valueOfTxns;
	}
	public List<ProcessTransaction> getProcesstransactionList() {
		return this.processtransactionList;
	}
	public void setProcesstransactionList(
			List<ProcessTransaction> processtransactionList) {
		this.processtransactionList = processtransactionList;
	}
	/** 
	 */
	public DayEnd getDayEnd() {
		return dayEnd;
	}
	/** 
	 */
	public void setDayEnd(DayEnd dayEnd) {
		this.dayEnd = dayEnd;
	}
	public TransactionType getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
   
}
