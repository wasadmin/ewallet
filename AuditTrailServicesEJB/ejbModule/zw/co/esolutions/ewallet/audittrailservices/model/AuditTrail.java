package zw.co.esolutions.ewallet.audittrailservices.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Entity implementation class for Entity: AuditTrail
 *
 */
@Entity
@NamedQueries({
	@NamedQuery(name="getByEntityNameAndInstanceName", query="SELECT a FROM AuditTrail a WHERE a.entityName = :entityName AND a.instanceName = :instanceName ORDER BY a.time DESC"),
	@NamedQuery(name="getByEntityNameAndEntityId", query="SELECT a FROM AuditTrail a WHERE a.entityName = :entityName AND a.entityID = :entityID ORDER BY a.time DESC"),
	@NamedQuery(name="getByEntityNameAndInstanceNameAndTimePeriod",query="SELECT a FROM AuditTrail a WHERE a.entityName = :entityName AND a.instanceName = :instanceName AND a.time BETWEEN :startTime AND :endTime ORDER BY a.time DESC"),
	@NamedQuery(name="getByUsername", query="SELECT a FROM AuditTrail a WHERE a.username = :username ORDER BY a.time DESC"),
	@NamedQuery(name="getByUsernameAndTimePeriod",query="SELECT a FROM AuditTrail a WHERE a.username = :username AND a.time BETWEEN :startTime AND :endTime ORDER BY a.time DESC"),
	@NamedQuery(name="getByActivityAndTimePeriod",query="SELECT a FROM AuditTrail a WHERE a.activity.id = :activityId AND a.time BETWEEN :startTime AND :endTime ORDER BY a.time DESC"),
	@NamedQuery(name="getByTimePeriod",query="SELECT a FROM AuditTrail a WHERE a.time BETWEEN :startTime AND :endTime ORDER BY a.time DESC")
})
public class AuditTrail implements Serializable {

	   
	@Id
	@Column(length=50)
	private String auditTrailID;
	@Column(length=100)
	private String instanceName;
	@Column(length=50)
	private String entityName;
	@Column(length=50)
	private String entityID;
	@Temporal(TemporalType.TIMESTAMP)
	private Date time;
	@Column(length=2000)
	private String narrative;
	private static final long serialVersionUID = 1L;
	@Column(length=50)
	private String username;
	@ManyToOne private Activity activity;

	public AuditTrail() {
		super();
	}   
	public String getAuditTrailID() {
		return this.auditTrailID;
	}

	public void setAuditTrailID(String auditTrailID) {
		this.auditTrailID = auditTrailID;
	}   
	
	public Activity getActivity() {
		return activity;
	}
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	public Date getTime() {
		return this.time;
	}

	public void setTime(Date time) {
		this.time = time;
	}   
	public String getNarrative() {
		return this.narrative;
	}

	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public String getEntityID() {
		return entityID;
	}
	public void setEntityID(String entityID) {
		this.entityID = entityID;
	}
	public String getInstanceName() {
		return instanceName;
	}
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}
	
	
   
}
