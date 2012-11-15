package zw.co.esolutions.ewallet.customerservices.model;

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
import javax.persistence.Version;

import zw.co.esolutions.ewallet.audit.Auditable;
import zw.co.esolutions.ewallet.enums.MobileNetworkOperator;
import zw.co.esolutions.ewallet.enums.MobileProfileStatus;
import zw.co.esolutions.ewallet.util.MapUtil;
import javax.persistence.JoinColumn;

@Entity
@NamedQueries ({
	@NamedQuery(name="getMobileProfileByMobileNumber", query="SELECT m FROM MobileProfile m WHERE m.mobileNumber =: mobileNumber AND m.status <> :status"),
	@NamedQuery(name="getMobileProfileByStatus", query="SELECT m FROM MobileProfile m WHERE m.status =: status"),
	@NamedQuery(name="getMobileProfileByBankIdMobileNumberAndStatus", query="SELECT m FROM MobileProfile m WHERE m.bankId = :bankId AND m.mobileNumber = :mobileNumber AND m.status =:status"),
	@NamedQuery(name="getMobileProfileByCustomer", query="SELECT m FROM MobileProfile m WHERE m.customer.id =: customer_id AND m.status <> :status"),
	@NamedQuery(name="getMobileProfileByBankAndMobileNumber", query="SELECT m FROM MobileProfile m WHERE m.bankId = :bankId AND m.mobileNumber = :mobileNumber AND m.status <> :status"),
	@NamedQuery(name="getMobileProfileListByMobileNumber", query="SELECT m FROM MobileProfile m WHERE m.mobileNumber = :mobileNumber AND m.status <> :status")
})

public class MobileProfile implements Auditable{
	
	@Id @Column(length=30) private String id;
	@Column(length=30) private String mobileNumber;
	@Column(length=60) private String secretCode;
	@Column private int passwordRetryCount;
	@Enumerated(EnumType.STRING) @Column(length = 50) private MobileProfileStatus status;
	@Column private Date timeout;
	@ManyToOne @Column @JoinColumn(name="CUSTOMER_ID", referencedColumnName = "id")
	private Customer customer;
	@Column(length=30) private String referralCode;
	@Column(length=30, name="mobileBranchId") 
	private String branchId;
	@Column private boolean referralProcessed;
	@Enumerated(EnumType.STRING) private MobileNetworkOperator network;
	@Column 
	private boolean primary;
	@Column(length=30) private String bankId;
	@Column private Date dateCreated;
	@Version @Column private long version;
	private transient boolean approvable;
	private transient boolean coldable;
	private transient boolean hottable;
	private transient boolean active;
	private transient boolean locked;
	private transient String responseCode;
	private @Column(length=30)  String mobileProfileEditBranch;
	private transient boolean mobileProfileRenderApproval;
	private transient boolean pendingApproval;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getSecretCode() {
		return secretCode;
	}
	public void setSecretCode(String secretCode) {
		this.secretCode = secretCode;
	}
	public int getPasswordRetryCount() {
		return passwordRetryCount;
	}
	public void setPasswordRetryCount(int passwordRetryCount) {
		this.passwordRetryCount = passwordRetryCount;
	}
	public MobileProfileStatus getStatus() {
		return status;
	}
	public void setStatus(MobileProfileStatus status) {
		this.status = status;
	}
	public Date getTimeout() {
		return timeout;
	}
	public void setTimeout(Date timeout) {
		this.timeout = timeout;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public Customer getCustomer() {
		return customer;
	}
	public String getReferralCode() {
		return referralCode;
	}
	public void setReferralCode(String referralCode) {
		this.referralCode = referralCode;
	}
	public boolean isReferralProcessed() {
		return referralProcessed;
	}
	public void setReferralProcessed(boolean referralProcessed) {
		this.referralProcessed = referralProcessed;
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
	public void setNetwork(MobileNetworkOperator network) {
		this.network = network;
	}
	public MobileNetworkOperator getNetwork() {
		return network;
	}
	public void setPrimary(boolean primary) {
		this.primary = primary;
	}
	public boolean isPrimary() {
		return primary;
	}
	public void setBankId(String bankId) {
		this.bankId = bankId;
	}
	public String getBankId() {
		return bankId;
	}
	public boolean isApprovable() {
		if (MobileProfileStatus.AWAITING_APPROVAL.equals(status)) {
			return true;
		} 
		return false;
	}
	
	public boolean isHottable(){
		if (MobileProfileStatus.ACTIVE.equals(status)) {
			return true;
		} 
		return false;
	}
	
	public boolean isColdable(){
		if (MobileProfileStatus.HOT.equals(status)) {
			return true;
		} 
		return false;
	}
	@Override
	public Map<String, String> getAuditableAttributesMap() {
		Map<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put("mobileNumber", getMobileNumber());
		attributesMap.put("status", getStatus().toString());
		return attributesMap;
	}
	@Override
	public String getAuditableAttributesString() {
		return MapUtil.convertAttributesMapToString(getAuditableAttributesMap());
	}
	@Override
	public String getEntityName() {
		return "MOBILE PROFILE";
	}
	@Override
	public String getInstanceName() {
		return getMobileNumber();
	}
	public void setApprovable(boolean approvable) {
		this.approvable = approvable;
	}
	public void setColdable(boolean coldable) {
		this.coldable = coldable;
	}
	public void setHottable(boolean hottable) {
		this.hottable = hottable;
	}
	public String getBranchId() {
		return branchId;
	}
	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getMobileProfileEditBranch() {
		return mobileProfileEditBranch;
	}
	public void setMobileProfileEditBranch(String mobileProfileEditedBranch) {
		this.mobileProfileEditBranch = mobileProfileEditedBranch;
	}
	public boolean isMobileProfileRenderApproval() {
		return mobileProfileRenderApproval;
	}
	public void setMobileProfileRenderApproval(boolean mobileProfileRenderApproval) {
		this.mobileProfileRenderApproval = mobileProfileRenderApproval;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public boolean isLocked() {
		if(MobileProfileStatus.LOCKED.equals(status)){
			return true;
		}
		return false;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	public boolean isPendingApproval() {
		if(MobileProfileStatus.AWAITING_APPROVAL.equals(status)|| MobileProfileStatus.DISAPPROVED.equals(status)){
			return true;
		}
		return false;
	}
	public void setPendingApproval(boolean pendingApproval) {
		this.pendingApproval = pendingApproval;
	}
	
}
