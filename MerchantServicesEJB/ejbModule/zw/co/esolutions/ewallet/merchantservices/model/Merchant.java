package zw.co.esolutions.ewallet.merchantservices.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.*;

import zw.co.esolutions.ewallet.audit.Auditable;
import zw.co.esolutions.ewallet.enums.BankMerchantStatus;
import zw.co.esolutions.ewallet.enums.MerchantStatus;
import zw.co.esolutions.ewallet.util.MapUtil;

/**
 * Entity implementation class for Entity: Merchant
 *
 */
@Entity

@NamedQueries({
	@NamedQuery(name="getMerchantByShortName", query = "SELECT m FROM Merchant m WHERE m.shortName = :shortName AND m.status <> :statusDeleted"),
	@NamedQuery(name="getMerchantByStatus", query = "SELECT m FROM Merchant m WHERE m.status = :status"),
	@NamedQuery(name="getMerchantByName", query = "SELECT m FROM Merchant m WHERE m.name LIKE :name AND m.status <> :statusDeleted"),
	@NamedQuery(name="getAllMerchant", query = "SELECT m FROM Merchant m WHERE m.status <> :statusDeleted ORDER BY m.name ASC")
	})
public class Merchant implements Serializable,Auditable {
	
	private static final long serialVersionUID = 1L;
	@Id
	@Column(length=30)
	private String id;
	
	@Column(length=40)
	private String name;
	
	@Column(length=20)
	private String shortName;
	
	@Enumerated(EnumType.STRING)@Column(length=50)
	private MerchantStatus status;
	
	@Column(length=30)
	private String contactDetailsId;
	
	@Column
	private Date dateCreated;
	
	@Version
	@Column
	private long version;
	
	@OneToMany(mappedBy="merchant")
	private List<BankMerchant> bankMerchants;
	
	private transient boolean approvable;

	public Merchant() {
		super();
	}

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

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getContactDetailsId() {
		return contactDetailsId;
	}

	public void setContactDetailsId(String contactDetailsId) {
		this.contactDetailsId = contactDetailsId;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}
   
	public MerchantStatus getStatus() {
		return status;
	}

	public void setStatus(MerchantStatus status) {
		this.status = status;
	}

	public void setFieldsToUpper(){
		this.setName(this.name.toUpperCase());
		this.setShortName(this.shortName.toUpperCase());
	}

	@Override
	public Map<String, String> getAuditableAttributesMap() {
		Map<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put("name", getName());
		attributesMap.put("shortName", getShortName());
		attributesMap.put("status", getStatus().toString());
		return attributesMap;
	}

	@Override
	public String getAuditableAttributesString() {
		return MapUtil.convertAttributesMapToString(getAuditableAttributesMap());
	}

	@Override
	public String getEntityName() {
		return "MERCHANT";
	}

	@Override
	public String getInstanceName() {
		return getName();
	}

	public List<BankMerchant> getBankMerchants() {
		return bankMerchants;
	}

	public void setBankMerchants(List<BankMerchant> bankMerchants) {
		this.bankMerchants = bankMerchants;
	}
	
	public void setApprovable(boolean approvable) {
		this.approvable = approvable;
	}
	public boolean isApprovable() {
		if (BankMerchantStatus.AWAITING_APPROVAL.equals(status)) {
			return true;
		} 
		return false;
	}
	
	
}
