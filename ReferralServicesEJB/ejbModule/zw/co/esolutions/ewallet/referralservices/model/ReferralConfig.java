package zw.co.esolutions.ewallet.referralservices.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.persistence.NamedQuery;
import javax.persistence.NamedQueries;

import zw.co.esolutions.ewallet.audit.Auditable;
import zw.co.esolutions.ewallet.enums.ReferralConfigStatus;
import zw.co.esolutions.ewallet.util.MapUtil;

@Entity
@NamedQueries({@NamedQuery(name="getReferralConfig", query = "SELECT r FROM ReferralConfig r"),@NamedQuery(name="getReferralConfigById", query = "SELECT r FROM ReferralConfig r WHERE r.id = :id"),
@NamedQuery(name="getReferralConfigByAmount", query = "SELECT r FROM ReferralConfig r WHERE r.amount = :amount"),
@NamedQuery(name="getReferralConfigByReferrerRatio", query = "SELECT r FROM ReferralConfig r WHERE r.referrerRatio = :referrerRatio"),
@NamedQuery(name="getReferralConfigByReferredRatio", query = "SELECT r FROM ReferralConfig r WHERE r.referredRatio = :referredRatio"),
@NamedQuery(name="getReferralConfigByMaxReferrals", query = "SELECT r FROM ReferralConfig r WHERE r.maxReferrals = :maxReferrals"),
@NamedQuery(name="getReferralConfigByDateCreated", query = "SELECT r FROM ReferralConfig r WHERE r.dateCreated = :dateCreated"),
@NamedQuery(name="getReferralConfigByVersion", query = "SELECT r FROM ReferralConfig r WHERE r.version = :version"),
@NamedQuery(name="getReferralConfigByDateFrom", query = "SELECT r FROM ReferralConfig r WHERE r.dateFrom = :dateFrom"),
@NamedQuery(name="getReferralConfigByDateTo", query = "SELECT r FROM ReferralConfig r WHERE r.dateTo = :dateTo"),
@NamedQuery(name="getActiveReferralConfig", query = "SELECT r FROM ReferralConfig r WHERE r.dateFrom <= :date AND (r.dateTo >= :date OR r.dateTo IS NULL)"),
@NamedQuery(name="getReferralConfigBetweenDates", query = "SELECT r FROM ReferralConfig r WHERE r.dateCreated BETWEEN :dateFrom AND :dateTo")
})
public class ReferralConfig implements Auditable{
	@Id @Column(length=30) private String id;
	@Column private long amount;
	@Column private int referrerRatio;
	@Column private int referredRatio;
	@Column private int maxReferrals;
	@Column private Date dateFrom;
	@Column private Date dateTo;
	@Column private Date dateCreated;
	@Version private long version;
	@Enumerated(EnumType.STRING) 
	@Column(length = 50)
	private ReferralConfigStatus status;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public int getReferrerRatio() {
		return referrerRatio;
	}
	public void setReferrerRatio(int referrerRatio) {
		this.referrerRatio = referrerRatio;
	}
	public int getReferredRatio() {
		return referredRatio;
	}
	public void setReferredRatio(int referredRatio) {
		this.referredRatio = referredRatio;
	}
	public int getMaxReferrals() {
		return maxReferrals;
	}
	public void setMaxReferrals(int maxReferrals) {
		this.maxReferrals = maxReferrals;
	}
	public Date getDateFrom() {
		return dateFrom;
	}
	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}
	public Date getDateTo() {
		return dateTo;
	}
	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
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
		
	public ReferralConfigStatus getStatus() {
		return status;
	}
	public void setStatus(ReferralConfigStatus status) {
		this.status = status;
	}
	
	@Override
	public Map<String, String> getAuditableAttributesMap() {
		Map<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put("amount", getAmount()+"");
		attributesMap.put("referrerRatio", getReferrerRatio()+"");
		attributesMap.put("referredRatio", getReferredRatio()+"");
		attributesMap.put("maxReferrals", getMaxReferrals()+"");
		attributesMap.put("dateFrom", getDateFrom().toString());
		attributesMap.put("dateTo", getDateTo().toString());
		return attributesMap;
	}
	@Override
	public String getAuditableAttributesString() {
		return MapUtil.convertAttributesMapToString(getAuditableAttributesMap());
	}
	@Override
	public String getEntityName() {
		return "REFERRALCONFIG";
	}
	@Override
	public String getInstanceName() {
		return getAmount()+" "+getDateCreated();
	}
	
	
}
