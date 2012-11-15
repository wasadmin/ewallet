package zw.co.esolutions.ewallet.merchantservices.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.*;

import zw.co.esolutions.ewallet.audit.Auditable;
import zw.co.esolutions.ewallet.enums.BankMerchantStatus;
import zw.co.esolutions.ewallet.enums.CustomerMerchantStatus;
import zw.co.esolutions.ewallet.util.MapUtil;

/**
 * Entity implementation class for Entity: CustomerMerchant
 *
 */
@Entity
@NamedQueries({
	@NamedQuery(name="getCustomerMerchantByBankId", query="SELECT c FROM CustomerMerchant c WHERE c.bankId =:bankId AND c.status <> :statusDeleted"),
	@NamedQuery(name="getCustomerMerchantByCustomerId", query="SELECT c FROM CustomerMerchant c WHERE c.customerId =:customerId AND c.status <> :statusDeleted"),
	@NamedQuery(name="getCustomerMerchantByBankMerchantId", query="SELECT c FROM CustomerMerchant c WHERE c.bankMerchant.id =: bankMerchant_id AND c.status <> :statusDeleted"),
	@NamedQuery(name="getCustomerMerchantByCustomerAccountNumber", query="SELECT c FROM CustomerMerchant c WHERE c.customerAccountNumber =:customerAccountNumber AND c.status <> :statusDeleted"),
	@NamedQuery(name="getCustomerMerchantByBankMerchantIdAndCustomerIdAndCustomerAccountNumber", query="SELECT c FROM CustomerMerchant c WHERE c.bankMerchant.id =:bankMerchantId AND c.customerId =:customerId AND c.customerAccountNumber =:customerAccountNumber AND c.status <> :statusDeleted"),
	@NamedQuery(name="getCustomerMerchantByStatus", query="SELECT c FROM CustomerMerchant c WHERE c.status =:status"),
	@NamedQuery(name="getCustomerMerchantByCustomerIdAndBankMerchantIdAndStatus", query = "SELECT c FROM CustomerMerchant c WHERE c.status =:status AND c.bankMerchant.id = :bankMerchant_id AND c.customerId = :customerId"),
	@NamedQuery(name = "getCustomerMerchantByCustomerIdAndMerchantShortNameAndStatus", query = "SELECT c FROM CustomerMerchant c WHERE c.status =:status AND c.bankMerchant.merchant.shortName = :shortName AND c.customerId = :customerId")
})
public class CustomerMerchant implements Serializable,Auditable {

	
	private static final long serialVersionUID = 1L;
	@Id
	@Column(length=30)
	private String id;
	@Column(length=30)
	private String bankId;
	@Column(length=30)
	private String customerId;
	@Column 
	@ManyToOne private BankMerchant bankMerchant;
	@Column(length=50)
	private String customerAccountNumber;
	@Enumerated(EnumType.STRING)@Column(length=50)
	private CustomerMerchantStatus status;
	@Column
	private Date dateCreated;
	@Version
	@Column
	private long version;
	
	private transient boolean approvable;
	
	public CustomerMerchant() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerAccountNumber() {
		return customerAccountNumber;
	}

	public void setCustomerAccountNumber(String customerAccountNumber) {
		this.customerAccountNumber = customerAccountNumber;
	}

	public CustomerMerchantStatus getStatus() {
		return status;
	}

	public void setStatus(CustomerMerchantStatus status) {
		this.status = status;
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

	@Override
	public Map<String, String> getAuditableAttributesMap() {
		Map<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put("customerAccountNumber", getCustomerAccountNumber());
		attributesMap.put("status", getStatus().toString());
		return attributesMap;
	}

	@Override
	public String getAuditableAttributesString() {
		return MapUtil.convertAttributesMapToString(getAuditableAttributesMap());
	}

	@Override
	public String getEntityName() {
		return "CUSTOMER MERCHANT";
	}

	@Override
	public String getInstanceName() {
		return getCustomerAccountNumber();
	}

	public void setBankMerchant(BankMerchant bankMerchant) {
		this.bankMerchant = bankMerchant;
	}

	public BankMerchant getBankMerchant() {
		return bankMerchant;
	}

	public void setApprovable(boolean approvable) {
		this.approvable = approvable;
	}
	public boolean isApprovable() {
		if (CustomerMerchantStatus.AWAITING_APPROVAL.equals(status)) {
			return true;
		} 
		return false;
	}

}
