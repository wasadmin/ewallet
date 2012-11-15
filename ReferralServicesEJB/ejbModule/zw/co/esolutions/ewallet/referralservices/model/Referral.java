package zw.co.esolutions.ewallet.referralservices.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import javax.persistence.NamedQuery;
import javax.persistence.NamedQueries;

import zw.co.esolutions.ewallet.audit.Auditable;
import zw.co.esolutions.ewallet.enums.ReferralStatus;
import zw.co.esolutions.ewallet.util.MapUtil;

@Entity
@NamedQueries({@NamedQuery(name="getReferral", query = "SELECT r FROM Referral r"),@NamedQuery(name="getReferralById", query = "SELECT r FROM Referral r WHERE r.id = :id"),
@NamedQuery(name="getReferralByReferrerMobileId", query = "SELECT r FROM Referral r WHERE r.referrerMobileId = :referrerMobileId"),
@NamedQuery(name="getReferralByReferredMobile", query = "SELECT r FROM Referral r WHERE r.referredMobile = :referredMobile"),
@NamedQuery(name="getReferralByCode", query = "SELECT r FROM Referral r WHERE r.code = :code"),
@NamedQuery(name="getReferralByStatus", query = "SELECT r FROM Referral r WHERE r.status = :status"),
@NamedQuery(name="getReferralByDateCreated", query = "SELECT r FROM Referral r WHERE r.dateCreated = :dateCreated"),
@NamedQuery(name="getReferralByVersion", query = "SELECT r FROM Referral r WHERE r.version = :version"),
@NamedQuery(name="getReferralByReferredMobileAndCode", query = "SELECT r FROM Referral r WHERE r.referredMobile = :referredMobile AND r.code = :code"),
@NamedQuery(name="getReferralByReferredMobileAndStatus", query = "SELECT r FROM Referral r WHERE r.referredMobile = :referredMobile AND r.status = :status")
})
public class Referral implements Auditable{
	@Id @Column(length=30) private String id;
	@Column(length=30) private String referrerMobileId;
	@Column(length=30) private String referredMobile;
	@Column private int code;
	
	@Enumerated(EnumType.STRING) 
	@Column(length = 50)
	private ReferralStatus status;
	@Column private Date dateCreated;
	@Version @Column private long version;
	@OneToMany(mappedBy="referral") private List<ReferralState> states;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getReferrerMobileId() {
		return referrerMobileId;
	}
	public void setReferrerMobileId(String referrerMobileId) {
		this.referrerMobileId = referrerMobileId;
	}
	public String getReferredMobile() {
		return referredMobile;
	}
	public void setReferredMobile(String referredMobile) {
		this.referredMobile = referredMobile;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public ReferralStatus getStatus() {
		return status;
	}
	public void setStatus(ReferralStatus status) {
		this.status = status;
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
	public void setStates(List<ReferralState> states) {
		this.states = states;
	}
	public List<ReferralState> getStates() {
		return states;
	}
	@Override
	public Map<String, String> getAuditableAttributesMap() {
		Map<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put("referredMobile", getReferredMobile());
		attributesMap.put("status", getStatus().toString());
		return attributesMap;
	}
	@Override
	public String getAuditableAttributesString() {
		return MapUtil.convertAttributesMapToString(getAuditableAttributesMap());
	}
	@Override
	public String getEntityName() {
		return "REFERRAL";
	}
	@Override
	public String getInstanceName() {
		return getReferredMobile();
	}
	
	
}
