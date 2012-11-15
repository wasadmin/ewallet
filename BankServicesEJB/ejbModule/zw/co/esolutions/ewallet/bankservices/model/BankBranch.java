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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import zw.co.esolutions.ewallet.audit.Auditable;
import zw.co.esolutions.ewallet.enums.BankBranchStatus;
import zw.co.esolutions.ewallet.util.MapUtil;

@Entity
@NamedQueries ({
	@NamedQuery(name="getBankBranchByBank", query="SELECT b FROM BankBranch b WHERE b.bank.id =: bank_id AND b.status <> :status"),
	@NamedQuery(name="getBankBranchByStatus", query="SELECT b FROM BankBranch b WHERE b.status =: status"),
	@NamedQuery(name="getBankBranchByName", query="SELECT b FROM BankBranch b WHERE b.name LIKE: name AND b.status <> :status"),
	@NamedQuery(name="getBankBranchByCode", query="SELECT b FROM BankBranch b WHERE b.code =: code AND b.status <> :status"),
	@NamedQuery(name="getBankBranch", query="SELECT b FROM BankBranch b WHERE b.status <> :status ORDER BY b.name")
})
public class BankBranch implements Auditable{
	
	@Id @Column(length=30) private String id;
	@Column(length=40) private String name;
	@Column(length=20) private String code;
	@Column(length=30) private String contactDetailsId;
	@Enumerated(EnumType.STRING)@Column(length=50) private BankBranchStatus status;
	@ManyToOne @Column private Bank bank;
	@OneToMany(mappedBy="branch") private List<BankAccount> accounts;
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
	public void setBank(Bank bank) {
		this.bank = bank;
	}
	public Bank getBank() {
		return bank;
	}
	
	public BankBranchStatus getStatus() {
		return status;
	}
	public void setStatus(BankBranchStatus status) {
		this.status = status;
	}
	public void setAccounts(List<BankAccount> accounts) {
		this.accounts = accounts;
	}
	public List<BankAccount> getAccounts() {
		return accounts;
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
	public void setFieldsToUpperCase() {
		this.setName(this.name.toUpperCase());
		this.setCode(this.code.toUpperCase());
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
		return "BANK BRANCH";
	}
	@Override
	public String getInstanceName() {
		return this.getName();
	}
}
