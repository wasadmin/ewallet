/*
 * Using the field passwordExpiryDate as 
 * the profileExpiryDate
 * */
package zw.co.esolutions.ewallet.profileservices.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;

import zw.co.esolutions.ewallet.audit.Auditable;
import zw.co.esolutions.ewallet.enums.ProfileStatus;
import zw.co.esolutions.ewallet.util.MapUtil;

@Entity
@NamedQueries({
	@NamedQuery(name="getAllProfiles", query="SELECT p FROM Profile p WHERE p.status <> :status"),
	@NamedQuery(name="getProfileByUserName", query="SELECT p FROM Profile p WHERE p.userName = :userName AND p.status <> :status"),
	@NamedQuery(name="getProfileByUserNameAndStatus", query="SELECT p FROM Profile p WHERE p.userName = :userName AND p.status = :status"),
	@NamedQuery(name="getProfileByUserRole", query="SELECT p FROM Profile p WHERE p.role.roleName LIKE: roleName AND p.status <> :status ORDER BY p.userName ASC"),
	@NamedQuery(name="getProfileByBranchId", query="SELECT p FROM Profile p WHERE p.branchId =: branchId AND p.status <> :status ORDER BY p.userName ASC"),
	@NamedQuery(name="getProfileByLastName", query="SELECT p FROM Profile p WHERE p.lastName LIKE: lastName AND p.status <> :status ORDER BY p.userName ASC"),
	@NamedQuery(name="getProfileByStatus", query="SELECT p FROM Profile p WHERE p.status = :status ORDER BY p.userName ASC"),
	@NamedQuery(name="getAllLoggedOnProfiles", query="SELECT p FROM Profile p WHERE p.loggedOn =:loggedOn  ORDER BY p.userName ASC"),
	@NamedQuery(name="getProfileByIP", query="SELECT p FROM Profile p WHERE p.ipAddress =: ipAddress ORDER BY p.userName ASC")
})
public class Profile implements Auditable{
	
	@Id @Column(length=30) private String id;
	@Column(length=30) private String lastName;
	@Column(length=30) private String firstNames;
	@Column(length=30) private String userName;
	@Enumerated(EnumType.STRING)@Column(length=70) private ProfileStatus status;
	@Column(length=30) private String userPassword;
	@Column(length=30) private String email;
	@Column(length=30) private String phoneNumber;
	@Column(length=30) private String mobileNumber;
	@Column private Date lastLoginDate;
	@Column private int loginAttempts;
	@Column private Date passwordExpiryDate;
	@Column private boolean changePassword;
	@Column private boolean loggedIn;
	@Column(length=30) private String ipAddress;
	@Column(length=30) private String branchId;
	@ManyToOne(fetch=FetchType.EAGER)
	private Role role;
	@Column private Date dateCreated;
	@Version @Column private long version;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void setStatus(ProfileStatus status) {
		this.status = status;
	}
	public ProfileStatus getStatus() {
		return status;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public int getLoginAttempts() {
		return loginAttempts;
	}
	public void setLoginAttempts(int loginAttempts) {
		this.loginAttempts = loginAttempts;
	}
	public Date getPasswordExpiryDate() {
		return passwordExpiryDate;
	}
	public void setPasswordExpiryDate(Date passwordExpiryDate) {
		this.passwordExpiryDate = passwordExpiryDate;
	}
	public String getBranchId() {
		return branchId;
	}
	public void setBranchId(String branchId) {
		this.branchId = branchId;
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
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public void setFieldsToUpperCase() {
		this.setLastName(this.lastName.toUpperCase());
		this.setFirstNames(this.firstNames.toUpperCase());
		this.setUserName(this.userName.toUpperCase());
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public Date getLastLoginDate() {
		return lastLoginDate;
	}
	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}
	
	public boolean isChangePassword() {
		return changePassword;
	}
	public void setChangePassword(boolean changePassword) {
		this.changePassword = changePassword;
	}
	@Override
	public Map<String, String> getAuditableAttributesMap() {
		Map<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put("username", getUserName());
		attributesMap.put("firstName", getFirstNames());
		attributesMap.put("lastName", getLastName());
		attributesMap.put("userRole", getRole().getRoleName());
		attributesMap.put("status", getStatus().toString());
		attributesMap.put("email", getEmail());
		attributesMap.put("ipAddress", getIpAddress());
		return attributesMap;
	}
	@Override
	public String getAuditableAttributesString() {
		Map<String,String> attributesMap = this.getAuditableAttributesMap();
		return MapUtil.convertAttributesMapToString(attributesMap);
	}
	@Override
	public String getEntityName() {
		return "PROFILE";
	}
	@Override
	public String getInstanceName() {
		return getUserName();
	}

	public String toString(){
		return getLastName()+" "+getFirstNames();
	}
	
	public boolean isLoggedIn() {
		return loggedIn;
	}
	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
}
