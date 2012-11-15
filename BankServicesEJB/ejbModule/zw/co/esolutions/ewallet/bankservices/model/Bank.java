package zw.co.esolutions.ewallet.bankservices.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import zw.co.esolutions.ewallet.audit.Auditable;
import zw.co.esolutions.ewallet.enums.BankStatus;
import zw.co.esolutions.ewallet.util.MapUtil;

@Entity
@NamedQueries ({
	@NamedQuery(name="getBankByName", query="SELECT b FROM Bank b WHERE b.name LIKE: name"),
	@NamedQuery(name="getBankByCode", query="SELECT b FROM Bank b WHERE b.code = :code"),
	@NamedQuery(name="getBank", query="SELECT b FROM Bank b"),
	@NamedQuery(name="getBankByContactDetailsId", query="SELECT b FROM Bank b WHERE b.contactDetailsId = :contactDetailsId"),
	@NamedQuery(name="getBankByStatus", query="SELECT b FROM Bank b WHERE b.status = :status")
})

public class Bank implements Auditable{
	
	/**
	 * 
	 */	
	@Id @Column(length=30) private String id;
	@Column(length=40) private String name;
	@Column(length=20) private String code;
	@Column(length=30) private String contactDetailsId;
	@Enumerated(EnumType.STRING)@Column(length=50) private BankStatus status;
	@OneToMany(mappedBy="bank") private List<BankBranch> branches;
	@Column private Date dateCreated;
	@Version @Column private long version;
	
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
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public void setContactDetailsId(String contactDetailsId) {
		this.contactDetailsId = contactDetailsId;
	}
	public String getContactDetailsId() {
		return contactDetailsId;
	}
	public void setBranches(List<BankBranch> branches) {
		this.branches = branches;
	}
	public List<BankBranch> getBranches() {
		return branches;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setVersion(long version) {
		this.version = version;
	}
	public long getVersion() {
		return version;
	}
	
	public BankStatus getStatus() {
		return status;
	}
	public void setStatus(BankStatus status) {
		this.status = status;
	}
	public void setFieldsToUpperCase() {
		if(this.name != null) {
			this.setName(this.name.toUpperCase());
		}
		if(this.name != null) {
			this.setCode(this.code.toUpperCase());
		}
	}
	@Override
	public Map<String, String> getAuditableAttributesMap() {
		Map<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put("name", getName());
		attributesMap.put("code", getCode());
		attributesMap.put("status", getStatus().toString());
		return attributesMap;
	}
	@Override
	public String getAuditableAttributesString() {
		return MapUtil.convertAttributesMapToString(getAuditableAttributesMap());
	}
	@Override
	public String getEntityName() {
		return "BANK";
	}
	@Override
	public String getInstanceName() {
		return this.getName();
	}
	
	
}
