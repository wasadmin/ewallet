package zw.co.esolutions.ewallet.audittrailservices.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import zw.co.esolutions.ewallet.audit.Auditable;
import zw.co.esolutions.ewallet.util.MapUtil;

@Entity
@NamedQueries({@NamedQuery(name="getActivity", query = "SELECT a FROM Activity a"),@NamedQuery(name="getActivityById", query = "SELECT a FROM Activity a WHERE a.id = :id"),
@NamedQuery(name="getActivityByName", query = "SELECT a FROM Activity a WHERE a.name = :name ORDER BY a.name ASC"),
@NamedQuery(name="getActivityByLogged", query = "SELECT a FROM Activity a WHERE a.logged = :logged")})
public class Activity implements Auditable{
	@Id
	@Column(length=50) private String id;
	@Column(length=60) private String name;
	@Column private boolean logged;
	@OneToMany(mappedBy="activity") private List<AuditTrail> auditTrails;
	@Version private long version;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isLogged() {
		return logged;
	}
	public void setLogged(boolean logged) {
		this.logged = logged;
	}
	public long getVersion() {
		return version;
	}
	public void setVersion(long version) {
		this.version = version;
	}
	public List<AuditTrail> getAuditTrails() {
		return auditTrails;
	}
	public void setAuditTrails(List<AuditTrail> auditTrails) {
		this.auditTrails = auditTrails;
	}
	@Override
	public Map<String, String> getAuditableAttributesMap() {
		Map<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put("logged", isLogged()+"");
		return attributesMap;
	}
	@Override
	public String getAuditableAttributesString() {
		Map<String,String> attributesMap = this.getAuditableAttributesMap();
		return MapUtil.convertAttributesMapToString(attributesMap);
	}
	@Override
	public String getEntityName() {
		return "ACTIVITY";
	}
	@Override
	public String getInstanceName() {
		return this.getName();
	}
	@Override
	public String toString() {
		return this.getName();
	}
		
	
	
}
