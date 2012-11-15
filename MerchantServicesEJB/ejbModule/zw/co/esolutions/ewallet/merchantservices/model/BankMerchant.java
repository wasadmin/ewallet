package zw.co.esolutions.ewallet.merchantservices.model;

import java.io.Serializable;
import java.lang.String;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.*;

import zw.co.esolutions.ewallet.audit.Auditable;
import zw.co.esolutions.ewallet.enums.BankMerchantStatus;
import zw.co.esolutions.ewallet.enums.CustomerStatus;
import zw.co.esolutions.ewallet.merchantservices.model.Merchant;
import zw.co.esolutions.ewallet.util.MapUtil;
import static javax.persistence.EnumType.STRING;

/**
 * Entity implementation class for Entity: BankMerchant
 *
 */
@Entity
@NamedQueries({
	@NamedQuery(name="getBankMerchantByStatus", query = "SELECT b FROM BankMerchant b WHERE b.status = :status"),
	@NamedQuery(name="getBankMerchantByStatusAndBankIdAndMerchantId", query = "SELECT b FROM BankMerchant b WHERE b.status = :status AND b.bankId =:bankId AND b.merchant.id =:merchantId"),
	@NamedQuery(name="getBankMerchantByBankIdAndMerchantId", query = "SELECT b FROM BankMerchant b WHERE b.bankId =:bankId AND b.merchant.id =:merchantId AND b.status <> :statusDeleted"),
	@NamedQuery(name="getBankMerchantByBankId", query = "SELECT b FROM BankMerchant b WHERE b.bankId = :bankId AND b.status <> :statusDeleted"),
	@NamedQuery(name="getBankMerchantByMerchantId", query = "SELECT b FROM BankMerchant b WHERE b.merchant.id = :merchant_id AND b.status <> :statusDeleted"),
	@NamedQuery(name="getBankMerchantByBankIdAndShortNameAndStatus", query = "SELECT b FROM BankMerchant b WHERE b.bankId = :bankId AND b.merchant.shortName = :shortName AND b.status = :status")
	})
public class BankMerchant implements Serializable,Auditable {

	   
	@Id
	@Column(length = 30)
	private String id;
	
	@Column(length = 30)
	private String bankId;
	
	@ManyToOne
	@JoinColumn(name="merchantId", referencedColumnName = "id")
	private Merchant merchant;
	
	@Column(length = 30)
	private String merchantSuspenseAccount;
	
	private boolean enabled;

	@Version
	private long version;
	
	@Enumerated(STRING)
	@Column(length = 30)
	private BankMerchantStatus status;
	
	@OneToMany(mappedBy="bankMerchant") 
	private List<CustomerMerchant> customerMerchants;
	
	private static final long serialVersionUID = 1L;
	
	private transient boolean approvable;
	@Column
	private Date dateCreated;

	public BankMerchant() {
		super();
	}   
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}   
	public String getBankId() {
		return this.bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}   
	public Merchant getMerchant() {
		return this.merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}   
	public String getMerchantSuspenseAccount() {
		return this.merchantSuspenseAccount;
	}

	public void setMerchantSuspenseAccount(String merchantSuspenseAccount) {
		this.merchantSuspenseAccount = merchantSuspenseAccount;
	}   
	public boolean getEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public long getVersion() {
		return version;
	}
	public void setVersion(long version) {
		this.version = version;
	}
	public BankMerchantStatus getStatus() {
		return status;
	}
	public void setStatus(BankMerchantStatus status) {
		this.status = status;
	}
	
	@Override
	public Map<String, String> getAuditableAttributesMap() {
		Map<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put("merchantSuspenseAccount", getMerchantSuspenseAccount());
		attributesMap.put("status", getStatus().toString());
		return attributesMap;
	}
	@Override
	public String getAuditableAttributesString() {
		return MapUtil.convertAttributesMapToString(getAuditableAttributesMap());
	}
	@Override
	public String getEntityName() {
		return "BANK MERCHANT";
	}
	@Override
	public String getInstanceName() {
		return getMerchantSuspenseAccount();
	}
	public void setCustomerMerchants(List<CustomerMerchant> customerMerchants) {
		this.customerMerchants = customerMerchants;
	}
	public List<CustomerMerchant> getCustomerMerchants() {
		return customerMerchants;
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
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
   
}
