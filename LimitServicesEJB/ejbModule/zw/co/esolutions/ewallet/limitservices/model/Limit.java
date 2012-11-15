package zw.co.esolutions.ewallet.limitservices.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;

import zw.co.esolutions.ewallet.audit.Auditable;
import zw.co.esolutions.ewallet.enums.BankAccountClass;
import zw.co.esolutions.ewallet.enums.LimitPeriodType;
import zw.co.esolutions.ewallet.enums.LimitStatus;
import zw.co.esolutions.ewallet.enums.LimitValueType;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.util.MapUtil;

/**
 * Entity implementation class for Entity: Limit
 *
 */
@Entity
@NamedQueries({@NamedQuery(name = "getLimit", query = "SELECT l FROM Limit l ORDER BY l.dateCreated DESC"), 
	@NamedQuery(name = "getLimitByBankId", query = "SELECT l FROM Limit l WHERE l.bankId LIKE :bankId AND ((l.endDate IS NULL AND (l.status NOT IN (:inactive))) OR (l.endDate IS NOT NULL AND l.status = :inactive)) ORDER BY l.type ASC"), 
	@NamedQuery(name = "getLimitByTypeAndStatusAndBankId", query = "SELECT l FROM Limit l WHERE l.type = :type AND l.bankId = :bankId AND l.status = :status ORDER BY l.dateCreated DESC"),
	@NamedQuery(name = "getLimitByValueTypeAndStatusAndBankId", query = "SELECT l FROM Limit l WHERE l.valueType = :valueType AND l.bankId = :bankId AND l.status = :status ORDER BY l.dateCreated DESC"), 
	@NamedQuery(name = "getLimitByTypeAndValueTypeAndStatus", query = "SELECT l FROM Limit l WHERE l.type = :type AND l.valueType = :valueType AND l.status = :status ORDER BY l.dateCreated DESC"), 
	@NamedQuery(name = "getLimitByAccountClassTypeValueTypeStatusPeriodTypeAndBankId", query = "SELECT l FROM Limit l WHERE l.type = :type AND l.accountClass = :accountClass AND l.valueType = :valueType " +
			"AND l.status = :status AND l.periodType = :periodType AND l.bankId = :bankId ORDER BY l.dateCreated DESC"), 
	@NamedQuery(name = "getLimitByTypeEffectiveDateStatusAndBankId", query = "SELECT l FROM Limit l WHERE l.type = :type AND l.effectiveDate = :effectiveDate AND l.status = :status " +
			"AND l.bankId = :bankId ORDER BY l.dateCreated DESC"), 
	@NamedQuery(name = "getLimitByValueTypeEffectiveDateStatusAndBankId", query = "SELECT l FROM Limit l WHERE l.valueType = :valueType AND l.effectiveDate = :effectiveDate AND l.status = :status AND " +
			"l.bankId = :bankId ORDER BY l.dateCreated DESC"),
	@NamedQuery(name = "getLimitByTypeValueTypeEffectiveDateStatusAndBankId", query = "SELECT l FROM Limit l WHERE l.type = :type AND l.valueType = :valueType AND l.effectiveDate = :effectiveDate" +
			" AND l.status = :status AND l.bankId = :bankId"), 
	@NamedQuery(name = "getLimitByTypeAndBankId", query = "SELECT l FROM Limit l WHERE l.type = :type AND l.bankId LIKE :bankId AND l.accountClass = :accountClass AND ((l.endDate IS NULL AND (l.status NOT IN (:inactive))) OR (l.endDate IS NOT NULL AND l.status = :inactive)) ORDER BY l.bankId DESC, l.type ASC"), 
	@NamedQuery(name = "getLimitByAccountClassAndBankId", query = "SELECT l FROM Limit l WHERE l.accountClass = :accountClass AND l.bankId LIKE :bankId AND ((l.endDate IS NULL AND (l.status NOT IN (:inactive))) OR (l.endDate IS NOT NULL AND l.status = :inactive)) ORDER BY l.bankId DESC, l.type ASC"),
	@NamedQuery(name = "getLimitByValueTypeAndBankId", query = "SELECT l FROM Limit l WHERE l.valueType = :valueType AND l.bankId = :bankId ORDER BY l.dateCreated DESC"), 
	@NamedQuery(name = "getEffectiveLimitsByBankId", query = "SELECT l FROM Limit l WHERE l.accountClass = :accountClass AND l.effectiveDate <= :fromDate AND (l.endDate IS NULL OR l.endDate >= :toDate) AND l.bankId LIKE :bankId " +
			"AND ((l.endDate IS NULL AND (l.status NOT IN (:inactive))) OR (l.endDate IS NOT NULL AND l.status = :inactive)) " +
			"AND l.status NOT IN (:awaitingApproval) AND l.status NOT IN (:disapproved) AND l.status NOT IN (:deleted) ORDER BY l.bankId DESC, l.type ASC"),  
	@NamedQuery(name = "getActiveLimitByAccountClassTypeValueTypeEffectiveDateEndDateStatusPeriodTypeAndBankId", query = "SELECT l FROM Limit l WHERE l.type = :type AND l.valueType = :valueType AND l.effectiveDate = :effectiveDate " +
			"AND l.periodType = :periodType AND l.endDate = :endDate AND l.status = :status AND l.accountClass = :accountClass AND l.bankId = :bankId ORDER BY l.dateCreated DESC"),
	@NamedQuery(name = "getLimitByAccountClassTypeValueTypeEffectiveDateEndDateStatusAndBankId", query = "SELECT l FROM Limit l WHERE l.type = :type AND l.valueType = :valueType AND l.effectiveDate = :effectiveDate " +
			"AND l.endDate = :endDate AND l.status = :status AND l.accountClass = :accountClass AND l.bankId = :bankId ORDER BY l.dateCreated DESC"),
	@NamedQuery(name = "getExactLimitForBank", query = "SELECT l FROM Limit l WHERE l.type = :type AND l.valueType = :valueType AND l.periodType = :periodType " +
			"AND l.status = :status AND l.accountClass = :accountClass AND l.bankId = :bankId ORDER BY l.dateCreated DESC"),
	@NamedQuery(name = "getLimitByValidityMinAttributes", query = "SELECT l FROM Limit l WHERE l.type = :type AND l.periodType = :periodType " +
			"AND l.accountClass = :accountClass AND (l.effectiveDate <= :onDateStart AND (l.endDate IS NULL OR l.endDate >= :onDateEnd)) AND l.bankId = :bankId "+
			"AND ((l.endDate IS NULL AND (l.status NOT IN (:inactive))) OR (l.endDate IS NOT NULL AND l.status = :inactive)) " +
	        "AND l.status NOT IN (:awaitingApproval) AND l.status NOT IN (:disapproved) AND l.status NOT IN (:deleted) ORDER BY l.type ASC"),  
	@NamedQuery(name = "getLimitByValidityAttributes", query = "SELECT l FROM Limit l WHERE l.type = :type AND l.periodType = :periodType " +
			"AND l.status = :status AND l.accountClass = :accountClass AND (l.effectiveDate <= :onDateStart AND (l.endDate IS NULL OR l.endDate >= :onDateEnd)) ORDER BY l.dateCreated DESC"),
	@NamedQuery(name = "getLimitByStatusAndBankId", query = "SELECT l FROM Limit l WHERE l.bankId LIKE :bankId AND l.status = :status ORDER BY l.dateCreated DESC")
	
	})
public class Limit implements Serializable,Auditable {

	   
	@Id
	@Column(length = 30)
	private String id;
	@Column(length = 30)
	private String bankId;
	@Column(length = 30)
	private String oldLimitId;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 50)
	private TransactionType type;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 50)
	private  BankAccountClass accountClass;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 50)
	private  LimitValueType valueType;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 50)	
	private LimitStatus status;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 50)
	private LimitPeriodType periodType;
	private long minValue;
	private long maxValue;
	private Date effectiveDate;
	private Date endDate;
	private Date dateCreated;
	@Version
	private long version;
	private static final long serialVersionUID = 1L;

	public Limit() {
		super();
	}   
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}   
	public Date getEffectiveDate() {
		return this.effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}   
	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}   
	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}   
	public long getVersion() {
		return this.version;
	}

	public void setVersion(long version) {
		this.version = version;
	}
	public TransactionType getType() {
		return type;
	}
	public void setType(TransactionType type) {
		this.type = type;
	}
	public LimitValueType getValueType() {
		return valueType;
	}
	public void setValueType(LimitValueType valueType) {
		this.valueType = valueType;
	}
	public void setStatus(LimitStatus status) {
		this.status = status;
	}
	public LimitStatus getStatus() {
		return status;
	}
	public void setAccountClass(BankAccountClass accountClass) {
		this.accountClass = accountClass;
	}
	public BankAccountClass getAccountClass() {
		return accountClass;
	}
	public long getMinValue() {
		return minValue;
	}
	public void setMinValue(long minValue) {
		this.minValue = minValue;
	}
	public long getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(long maxValue) {
		this.maxValue = maxValue;
	}
	public void setPeriodType(LimitPeriodType periodType) {
		this.periodType = periodType;
	}
	public LimitPeriodType getPeriodType() {
		return periodType;
	}
	public void setBankId(String bankId) {
		this.bankId = bankId;
	}
	public String getBankId() {
		return bankId;
	}
	public void setOldLimitId(String oldLimitId) {
		this.oldLimitId = oldLimitId;
	}
	public String getOldLimitId() {
		return oldLimitId;
	}
	@Override
	public Map<String, String> getAuditableAttributesMap() {
		Map<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put("transactionType", getType().toString());
		attributesMap.put("accountClass", getAccountClass().toString());
		attributesMap.put("valueType", getValueType().toString());
		attributesMap.put("status", getStatus().toString());
		attributesMap.put("periodType", getPeriodType().toString());
		attributesMap.put("minValue", getMinValue()+"");
		attributesMap.put("maxValue", getMaxValue()+"");
		attributesMap.put("effectiveDate", getEffectiveDate().toString());
		if(getEndDate() != null) {
			attributesMap.put("endDate", getEndDate().toString());
		}
		return attributesMap;
	}
	@Override
	public String getAuditableAttributesString() {
		return MapUtil.convertAttributesMapToString(getAuditableAttributesMap());
	}
	@Override
	public String getEntityName() {
		return "LIMIT";
	}
	@Override
	public String getInstanceName() {
		return getAccountClass()+" "+getValueType();
	}
	
	
   
}
