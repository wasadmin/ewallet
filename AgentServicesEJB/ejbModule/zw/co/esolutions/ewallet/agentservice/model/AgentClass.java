package zw.co.esolutions.ewallet.agentservice.model;

import java.io.Serializable;
import java.lang.String;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.*;

import zw.co.esolutions.ewallet.audit.Auditable;
import zw.co.esolutions.ewallet.enums.AgentClassStatus;
import zw.co.esolutions.ewallet.util.MapUtil;

/**
 * Entity implementation class for Entity: AgentClass
 *
 */
@Entity

@NamedQueries({
	@NamedQuery(name = "getAllAgentClasses", query = "SELECT a FROM AgentClass a"),
	@NamedQuery(name = "getAgentClassByName", query = "SELECT a FROM AgentClass a where a.name =: className"),
	@NamedQuery(name = "AgentsByAgentClass", query = "SELECT a FROM AgentClass a where a.id =: agentClassId"),
	@NamedQuery(name = "getAgentClassByStatus", query = "SELECT a FROM AgentClass a where a.status =: status")
})
public class AgentClass implements Serializable , Auditable{

	   
	@Id
	@Column(length = 50)
	private String id;
	@Column(length = 50)
	private String name;
	@Column(length = 50)
	private String description;
	@OneToMany(mappedBy="agentClass")
	private List<Agent> agentList;
	@Enumerated(EnumType.STRING)
	@Column(length = 50)
	private AgentClassStatus status;
//	@Column(length = 50)
//	public String tariffTableId;
	@Version
	private long version;
	private Date dateCreated; 
	
	private static final long serialVersionUID = 1L;

	public AgentClass() {
		super();
	}   
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}   
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}   
	public String getDescription() {
		return this.description;
	}
	
	public long getVersion() {
		return version;
	}
	public void setVersion(long version) {
		this.version = version;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	public void setDescription(String description) {
		this.description = description;
	}   
//	public String getTariffTableId() {
//		return this.tariffTableId;
//	}
//
//	public void setTariffTableId(String tariffTableId) {
//		this.tariffTableId = tariffTableId;
//	}
	
	public AgentClassStatus getStatus() {
		return status;
	}
	public void setStatus(AgentClassStatus status) {
		this.status = status;
	}
	public List<Agent> getAgentList() {
		return agentList;
	}
	public void setAgentList(List<Agent> agentList) {
		this.agentList = agentList;
	}
	@Override
	public Map<String, String> getAuditableAttributesMap() {
		Map<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put("name", getName());
		return attributesMap;
	}
	@Override
	public String getAuditableAttributesString() {
		Map<String,String> attributesMap = this.getAuditableAttributesMap();
		return MapUtil.convertAttributesMapToString(attributesMap);
	}
	@Override
	public String getEntityName() {
		
		return "AGENT_CLASS";
	}
	@Override
	public String getInstanceName() {
		
		return getName();
	}
	
	public void setFieldsToUpperCase() {
		this.setName(this.name.toUpperCase());
		this.setDescription(this.description.toUpperCase());		
	}
}
