package zw.co.esolutions.ewallet.referralservices.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import javax.persistence.NamedQuery;
import javax.persistence.NamedQueries;

import zw.co.esolutions.ewallet.enums.ReferralStatus;

@Entity
@NamedQueries({@NamedQuery(name="getReferralState", query = "SELECT r FROM ReferralState r"),@NamedQuery(name="getReferralStateById", query = "SELECT r FROM ReferralState r WHERE r.id = :id"),
@NamedQuery(name="getReferralStateByReferral", query = "SELECT r FROM ReferralState r WHERE r.referral.id = :referral_id"),
@NamedQuery(name="getReferralStateByStatus", query = "SELECT r FROM ReferralState r WHERE r.status = :status"),
@NamedQuery(name="getReferralStateByDateCreated", query = "SELECT r FROM ReferralState r WHERE r.dateCreated = :dateCreated"),
@NamedQuery(name="getReferralStateByVersion", query = "SELECT r FROM ReferralState r WHERE r.version = :version")})
public class ReferralState {
	@Id @Column(length=30) private String id;
	@ManyToOne private Referral referral;
	
	@Enumerated(EnumType.STRING) 
	@Column(length = 50)
	private ReferralStatus status;
	
	@Column private Date dateCreated;
	@Version @Column(length=30) private long version;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Referral getReferral() {
		return referral;
	}
	public void setReferral(Referral referral) {
		this.referral = referral;
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
	
	
}
