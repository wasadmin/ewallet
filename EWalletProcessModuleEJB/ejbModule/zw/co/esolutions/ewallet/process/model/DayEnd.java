package zw.co.esolutions.ewallet.process.model;

import java.io.Serializable;
import java.lang.String;
import java.util.Date;
import javax.persistence.*;
import java.util.List;
import javax.persistence.FetchType;

import zw.co.esolutions.ewallet.enums.DayEndStatus;

/**
 * Entity implementation class for Entity: DayEnd
 *
 */
@Entity

@NamedQueries({@NamedQuery(name="getDayEnd", query = "SELECT d FROM DayEnd d WHERE d.dayEndId= :dayEndId"),@NamedQuery(name="getDayEndByDayEndId", query = "SELECT d FROM DayEnd d WHERE d.dayEndId = :dayEndId"),
@NamedQuery(name="getDayEndByDayEndDate", query = "SELECT d FROM DayEnd d WHERE d.dayEndDate = :dayEndDate"),
@NamedQuery(name="getDayEndByDateCreated", query = "SELECT d FROM DayEnd d WHERE d.dateCreated = :dateCreated"),
@NamedQuery(name="getDayEndByCashTendered", query = "SELECT d FROM DayEnd d WHERE d.cashTendered = :cashTendered"),
@NamedQuery(name="getDayEndByStatus", query = "SELECT d FROM DayEnd d WHERE d.status = :status"),
@NamedQuery(name="getDayEndByTellerIdAndDateCreatedAndStatus",query="SELECT d FROM DayEnd d WHERE d.tellerId= :tellerId AND dateCreated= :dateCreated AND d.status= :status ORDER BY d.dateCreated DESC"),
@NamedQuery(name="getDayEndByTellerId", query = "SELECT d FROM DayEnd d WHERE d.tellerId = :tellerId"),
@NamedQuery(name="getDayEndsByDayEndStatusAndProfileId",query="SELECT d FROM DayEnd d WHERE d.status =: status AND d.tellerId= :tellerId"),
@NamedQuery(name="getDayEndByDayEndStatusAndBranch",query="SELECT d FROM DayEnd d WHERE  d.branchId= :branchId AND d.status= :status"),
@NamedQuery(name="getDayEndByTellerIdAndDayEndDate",query="SELECT d FROM DayEnd d WHERE d.tellerId=:tellerId AND d.dayEndDate= :dayEndDate"),
@NamedQuery(name="getDayEndByDayEndStatus",query="SELECT d FROM DayEnd WHERE d.status =: status ORDER BY d.dateCreated DESC "),
@NamedQuery(name="getDayEndsByDayEndStatusAndDateRangeAndTeller",query="SELECT d FROM DayEnd d WHERE  d.tellerId= :tellerId AND d.status= :status AND d.dateCreated >= :fromDate AND d.dateCreated <= :toDate ORDER BY d.dateCreated DESC"),
@NamedQuery(name="getDayEndsByDayEndStatusAndDateRangeAndBranch",query="SELECT d FROM DayEnd d WHERE  d.branchId= :branchId AND d.status= :status AND d.dateCreated >= :fromDate AND d.dateCreated <= :toDate ORDER BY d.dateCreated DESC"),
@NamedQuery(name="getDayEndByDayEndStatusAndDateRange",query="SELECT d FROM DayEnd d WHERE d.status =: status d.dateCreated >= :fromDate AND d.dateCreated <= :toDate ORDER BY d.dateCreated DESC "),
@NamedQuery(name="getDayEndByBranchId", query = "SELECT d FROM DayEnd d WHERE d.branchId = :branchId")})
public class DayEnd implements Serializable {

	   
	@Id
	private String dayEndId;
	@Temporal(TemporalType.DATE)
	private Date dayEndDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateCreated;
	
	private long cashTendered;
	@Column(length=60)
	@Enumerated(EnumType.STRING)
	private DayEndStatus status;
	@Column(length=60)
	private String tellerId;
	@Column(length=60)
	private String branchId;
	private static final long serialVersionUID = 1L;
	@OneToMany(mappedBy="dayEnd",fetch=FetchType.LAZY)
	private List<DayEndSummary> dayendsummaryList;
	public DayEnd() {
		super();
	}   
	public String getDayEndId() {
		return this.dayEndId;
	}

	public void setDayEndId(String dayEndId) {
		this.dayEndId = dayEndId;
	}   
	public Date getDayEndDate() {
		return this.dayEndDate;
	}

	public void setDayEndDate(Date dayEndDate) {
		this.dayEndDate = dayEndDate;
	}   
	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}   
	public long getCashTendered() {
		return this.cashTendered;
	}

	public void setCashTendered(long cashTendered) {
		this.cashTendered = cashTendered;
	}   
	
	public String getTellerId() {
		return this.tellerId;
	}

	public DayEndStatus getStatus() {
		return status;
	}
	public void setStatus(DayEndStatus status) {
		this.status = status;
	}
	public void setTellerId(String tellerId) {
		this.tellerId = tellerId;
	}   
	public String getBranchId() {
		return this.branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}
	public List<DayEndSummary> getDayendsummaryList() {
		return this.dayendsummaryList;
	}
	public void setDayendsummaryList(List<DayEndSummary> dayendsummaryList) {
		this.dayendsummaryList = dayendsummaryList;
	}
   
}
