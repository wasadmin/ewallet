package zw.co.esolutions.ewallet.profileservices.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.*;

import zw.co.esolutions.ewallet.audit.Auditable;
import zw.co.esolutions.ewallet.util.MapUtil;

/**
 * Entity implementation class for Entity: Bulletin
 *
 */
@Entity
@NamedQueries({
	@NamedQuery(name="getCurrentBulletin",query="SELECT b FROM Bulletin b where  b.status =:status AND b.expirationDate <= :expirationDate"),	
	@NamedQuery(name="getBulletinByStatus",query="SELECT b FROM Bulletin b where b.status =:status ORDER BY b.dateCreated DESC"),
	@NamedQuery(name="getBulletinByApprover",query="SELECT b FROM Bulletin b WHERE b.approverId =:approverId ORDER BY b.dateCreated DESC"),
	@NamedQuery(name="getBulletinByInitiator",query="SELECT b FROM Bulletin b WHERE b.initiatorId =:initiatorId ORDER BY b.dateCreated DESC"),
	@NamedQuery(name="getBulletinByAllFields",query="SELECT b FROM Bulletin b WHERE b.status LIKE :status AND b.initiatorId LIKE :initiatorId " +
			"b.approverId LIKE:approverId AND (b.dateCreated >=: dateCreated AND b.dateCreated <=:expirationDate) OR (b.expirationDate <=:expirationDate AND p.expirationDate>=:dateCreated) " +
			"ORDER BY b.dateCreated DESC")
})
public class Bulletin implements Serializable,Auditable{

	
	private static final long serialVersionUID = 1L;
	@Id
	@Column(length = 30)
	private String id;
	@Column(length = 30)
	private String approverId;
	@Column(length = 30)
	private String initiatorId;
	@Column(length = 30)
	private String subject;
	@Column(length = 250)
	private String message;
	private Date dateCreated;
	private Date expirationDate;
	@Column(length = 20)
	private String status;
	@Version 
	@Column 
	private long version;
	
	public Bulletin() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getApproverId() {
		return approverId;
	}

	public void setApproverId(String approverId) {
		this.approverId = approverId;
	}

	public String getInitiatorId() {
		return initiatorId;
	}

	public void setInitiatorId(String initiatorId) {
		this.initiatorId = initiatorId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	@Override
	public String getEntityName() {
		return "BULLETIN";
	}

	@Override
	public Map<String, String> getAuditableAttributesMap() {
		Map<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put("message", getMessage());
		attributesMap.put("status", getStatus());
		attributesMap.put("initiatorId", getInitiatorId());
		attributesMap.put("subject",getSubject());
		return attributesMap;
	}

	@Override
	public String getAuditableAttributesString() {
		Map<String,String> attributesMap = this.getAuditableAttributesMap();
		return MapUtil.convertAttributesMapToString(attributesMap);
	}

	@Override
	public String getInstanceName() {
		return getId();
	}
	
	public void setFieldsToUpperCase() {
		this.setMessage(this.message.toUpperCase());
		this.setStatus(this.status.toUpperCase());
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSubject() {
		return subject;
	}
}
