package zw.co.esolutions.ewallet.customerservices.model;

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
import zw.co.esolutions.ewallet.enums.CustomerAutoRegStatus;
import zw.co.esolutions.ewallet.enums.CustomerClass;
import zw.co.esolutions.ewallet.enums.CustomerStatus;
import zw.co.esolutions.ewallet.enums.Gender;
import zw.co.esolutions.ewallet.enums.MaritalStatus;
import zw.co.esolutions.ewallet.util.MapUtil;
import static javax.persistence.EnumType.STRING;

@Entity
@NamedQueries ({
	@NamedQuery(name="getCustomerByLastName", query="SELECT c FROM Customer c WHERE c.lastName LIKE :lastName"),
	@NamedQuery(name="getCustomerByDateOfBirth", query="SELECT c FROM Customer c WHERE c.dateOfBirth =: dateOfBirth"),
	@NamedQuery(name="getCustomerByGender", query="SELECT c FROM Customer c WHERE c.gender =: gender"),
	@NamedQuery(name="getCustomerByBranchId", query="SELECT c FROM Customer c WHERE c.branchId =: branchId"),
	@NamedQuery(name="getCustomerByStatusAndLastBranch",query="Select c FROM Customer c WHERE c.customerLastBranch =: customerLastBranch AND c.status = :status ORDER BY c.dateCreated ASC"),
	@NamedQuery(name="getCustomerByStatus", query="SELECT c FROM Customer c WHERE c.status = :status ORDER BY c.dateCreated ASC")
})

public class Customer implements Auditable{
	
	@Id @Column(length=30) private String id;
	@Column(length=10) private String title;
	@Column(length=30) private String lastName;
	@Column(length=30) private String firstNames;
	@Column(length=30) private String nationalId;
	@Column(length=30) private String contactDetailsId;
	@Column(length=40) private String customerLastBranch;
	@Column private Date dateOfBirth;
	@Enumerated(EnumType.STRING) @Column(length = 50) private Gender gender;
	@Enumerated(EnumType.STRING) @Column(length = 50) private MaritalStatus maritalStatus;
	@Column(length=30) private String branchId;
	@Enumerated(EnumType.STRING) @Column(length = 50) private CustomerClass customerClass;
	@Enumerated(EnumType.STRING) @Column(length = 50) private CustomerStatus status;
	@OneToMany(mappedBy="customer") private List<MobileProfile> mobileProfiles;
	@Column private Date dateCreated;
	
	@Enumerated(EnumType.STRING) @Column(length = 10)
	private CustomerAutoRegStatus customerAutoRegStatus;
	@Version @Column private long version;
	private transient boolean approvable;
	private transient boolean suspended;
	private transient boolean activate;
	private transient boolean deregister; 
	private transient boolean renderApproval;
	private transient boolean active;
	private transient boolean pendingApproval;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getFirstNames() {
		return firstNames;
	}
	public void setFirstNames(String firstNames) {
		this.firstNames = firstNames;
	}
	public String getNationalId() {
		return nationalId;
	}
	public void setNationalId(String nationalId) {
		this.nationalId = nationalId;
	}
	public String getContactDetailsId() {
		return contactDetailsId;
	}
	public void setContactDetailsId(String contactDetailsId) {
		this.contactDetailsId = contactDetailsId;
	}
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public Gender getGender() {
		return gender;
	}
	public void setGender(Gender gender) {
		this.gender = gender;
	}
	public MaritalStatus getMaritalStatus() {
		return maritalStatus;
	}
	public void setMaritalStatus(MaritalStatus maritalStatus) {
		this.maritalStatus = maritalStatus;
	}
	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}
	public String getBranchId() {
		return branchId;
	}
	public void setCustomerClass(CustomerClass customerClass) {
		this.customerClass = customerClass;
	}
	public CustomerClass getCustomerClass() {
		return customerClass;
	}
	public void setStatus(CustomerStatus status) {
		this.status = status;
	}
	public CustomerStatus getStatus() {
		return status;
	}
	public void setMobileProfiles(List<MobileProfile> mobileProfiles) {
		this.mobileProfiles = mobileProfiles;
	}
	public List<MobileProfile> getMobileProfiles() {
		return mobileProfiles;
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
		if(this.lastName != null) {
			this.setLastName(this.lastName.toUpperCase());
		}
		if(this.firstNames != null) {
			this.setFirstNames(this.firstNames.toUpperCase());
		}
		if(this.nationalId != null) {
			this.setNationalId(this.nationalId.toUpperCase());
		}
	}
	public void setApprovable(boolean approvable) {
		this.approvable = approvable;
	}
	
	public boolean isApprovable() {
		if (CustomerStatus.AWAITING_APPROVAL.equals(status)) {
			return true;
		} 
		return false;
	}
	@Override
	public Map<String, String> getAuditableAttributesMap() {
		Map<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put("title", getTitle());
		attributesMap.put("lastName", getLastName());
		attributesMap.put("firstNames", getFirstNames());
		attributesMap.put("nationalId", getNationalId());
		if(getDateOfBirth()!=null){
		attributesMap.put("dateOfBirth", getDateOfBirth().toString());
		}
		if(getGender()!=null){
		attributesMap.put("gender", getGender().toString());
		}
		if(getMaritalStatus()!=null){
		attributesMap.put("maritalStatus", getMaritalStatus().toString());
		}
		if(getCustomerClass()!=null){
		attributesMap.put("customerClass", getCustomerClass().toString());
		}
		if(getStatus()!=null){
		attributesMap.put("status", getStatus().toString());
		}
		return attributesMap;
	}
	@Override
	public String getAuditableAttributesString() {
		return MapUtil.convertAttributesMapToString(getAuditableAttributesMap());
	}
	@Override
	public String getEntityName() {
		return "CUSTOMER";
	}
	@Override
	public String getInstanceName() {
		return getFirstNames()+" "+getLastName();
	}
	public boolean isSuspended() {
		if (CustomerStatus.ACTIVE.equals(status)) {
			return true;
		} 
		return false;
	}
	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}
	public boolean isActivate() {
		if (CustomerStatus.SUSPENDED.equals(status)) {
			return true;
		} 
		return false;
	}
	public void setActivate(boolean activate) {
		this.activate = activate;
	}
	public boolean isDeregister() {
		if (CustomerStatus.ACTIVE.equals(status)) {
			return true;
		} 
		return false;
	}
	public void setDeregister(boolean deregister) {
		this.deregister = deregister;
	}
	
	public String getCustomerLastBranch() {
		return customerLastBranch;
	}
	public void setCustomerLastBranch(String customerLastBranch) {
		this.customerLastBranch = customerLastBranch;
	}
	public boolean isRenderApproval() {
		return renderApproval;
	}
	public void setRenderApproval(boolean renderApproval) {
		this.renderApproval = renderApproval;
	}
	public boolean isActive() {
		if (CustomerStatus.ACTIVE.equals(status)) {
			return true;
		} 
		return false;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public boolean isPendingApproval() {
		if (CustomerStatus.AWAITING_APPROVAL.equals(status) 
				|| CustomerStatus.DISAPPROVED.equals(status) || CustomerStatus.ACTIVE.equals(status)) {
			return true;
		} 
		return false;
	}
	public void setPendingApproval(boolean pendingApproval) {
		this.pendingApproval = pendingApproval;
	}
	public CustomerAutoRegStatus getCustomerAutoRegStatus() {
		return customerAutoRegStatus;
	}
	public void setCustomerAutoRegStatus(CustomerAutoRegStatus customerAutoRegStatus) {
		this.customerAutoRegStatus = customerAutoRegStatus;
	}
	
}
