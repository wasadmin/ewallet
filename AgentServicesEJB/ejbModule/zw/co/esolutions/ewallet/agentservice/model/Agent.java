package zw.co.esolutions.ewallet.agentservice.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;
import javax.persistence.Version;

import zw.co.esolutions.ewallet.audit.Auditable;
import zw.co.esolutions.ewallet.enums.AgentLevel;
import zw.co.esolutions.ewallet.enums.AgentType;
import zw.co.esolutions.ewallet.enums.ProfileStatus;
import zw.co.esolutions.ewallet.util.MapUtil;

/**
 * Entity implementation class for Entity: Agent
 *
 */
@Entity

@NamedQueries({
	@NamedQuery(name = "getAgentByName", query = "SELECT a FROM Agent a WHERE a.firstname =: firstname"+
			"AND a.lastname =:lastname ORDER BY t.dateCreated DESC"),
	@NamedQuery(name = "getAgentByAgentNumber", query = "SELECT a FROM Agent a WHERE a.agentNumber = :agentNumber"),
	@NamedQuery(name = "getAgentByStatus", query = "SELECT a FROM Agent a WHERE a.status = :status"),
	@NamedQuery(name = "getAgentByCustomerId", query = "SELECT a FROM Agent a WHERE a.customerId = :customerId"),
	@NamedQuery(name = "getAgentByNationalId", query = "SELECT a FROM Agent a WHERE a.nationalId = :nationalId"),
	@NamedQuery(name = "getAllAgents", query = "SELECT a FROM Agent a"),
	@NamedQuery(name = "getAgentByAgentType", query = "SELECT a FROM Agent a WHERE a.agentType =:agentType"),
	@NamedQuery(name = "getAgentByBankId", query = "SELECT a FROM Agent a WHERE a.bankId =: bankId"),
	@NamedQuery(name = "getAgentByLastName", query = "SELECT a FROM Agent a WHERE a.lastName =: lastName"),
	@NamedQuery(name = "getSubAgentBySuperAgentId", query = "SELECT a FROM Agent a WHERE a.superAgentId = :superAgentId"),
	@NamedQuery(name = "getSubAgentByNationalId", query = "SELECT a FROM Agent a WHERE a.superAgentId =: superAgentId" +
			" AND a.nationalId =:nationalId"),
	@NamedQuery(name = "getSubAgentByStatus", query = "SELECT a FROM Agent a WHERE a.superAgentId = :superAgentId"
		+" AND a.status = :status"),
	@NamedQuery(name = "getSubAgentByAgentNumber", query = "SELECT a FROM Agent a WHERE a.superAgentId = :superAgentId" +
			" AND a.agentNumber = :agentNumber"),
	@NamedQuery(name = "getAgentByMobileNumber", query = "SELECT a FROM Agent a WHERE a.mobileNumber =: mobileNumber"),
	@NamedQuery(name = "getAgentByProfileId", query = "SELECT a FROM Agent a WHERE a.profileId = :profileId"),
	@NamedQuery(name = "getAllSubAgents", query = "SELECT a FROM Agent a WHERE a.superAgentId = :superAgentId"),
	@NamedQuery(name = "getAgentByLevel", query = "SELECT a FROM Agent a WHERE a.agentLevel = :agentLevel")
})
public class Agent implements Serializable ,Auditable{

	@Id 
	@Column(length= 30)
	private String id;
	@Column(length = 30)
	private String agentNumber;
	@Column(length = 30)
	private String agentName;
	@Column(length = 30)
	private String profileId;
	@Enumerated(EnumType.STRING)
	@Column(length = 30)
	private AgentType agentType;
	@Enumerated(EnumType.STRING)
	@Column(length = 30)
	private AgentLevel agentLevel;
	@Column(length =30)
	private String superAgentId;
	@ManyToOne
	private AgentClass agentClass;
	@Column(length = 30)
	private String customerId;
	@Enumerated(EnumType.STRING) 
	@Column(length = 30) 
	private ProfileStatus status;
	@Version
	private long version;
	private Date dateCreated; 
	@Transient
	private transient boolean approvable;
	private static final long serialVersionUID = 1L;

	public Agent() {
		super();
	}     
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	} 
	
	public AgentClass getAgentClass() {
		return agentClass;
	}
	public void setAgentClass(AgentClass agentClass) {
		this.agentClass = agentClass;
	}
	
	public long getVersion() {
		return version;
	}
	public void setVersion(long version) {
		this.version = version;
	}
	
	public AgentLevel getAgentLevel() {
		return agentLevel;
	}
	public void setAgentLevel(AgentLevel agentLevel) {
		this.agentLevel = agentLevel;
	}
	
	public String getAgentNumber() {
		return agentNumber;
	}
	public void setAgentNumber(String agentNumber) {
		this.agentNumber = agentNumber;
	}
	public String getSuperAgentId() {
		return superAgentId;
	}
	public void setSuperAgentId(String superAgentId) {
		this.superAgentId = superAgentId;
	}

	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	public AgentType getAgentType() {
		return agentType;
	}
	public void setAgentType(AgentType agentType) {
		this.agentType = agentType;
	}

	public void setFieldsToUpperCase() {
		if(this.getAgentName() != null){
			this.setAgentName(this.agentName.toUpperCase());
		}
	}
	public void setApprovable(boolean approvable) {
		this.approvable = approvable;
	}
	
	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	
	public String getProfileId() {
		return profileId;
	}
	
	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}
	
	public ProfileStatus getStatus() {
		return status;
	}
	public void setStatus(ProfileStatus status) {
		this.status = status;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	@Override
	public Map<String, String> getAuditableAttributesMap() {
		
		Map<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put("agentName", getAgentName());
		attributesMap.put("agentType", getAgentType().name());
		
		return attributesMap;
	}
	@Override
	public String getAuditableAttributesString() {
		Map<String,String> attributesMap = this.getAuditableAttributesMap();
		return MapUtil.convertAttributesMapToString(attributesMap);
	}
	@Override
	public String getEntityName() {
		return "AGENT";
	}
	@Override
	public String getInstanceName() {
		return getAgentName();
	}
}
