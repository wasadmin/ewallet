package zw.co.esolutions.ewallet.alertsservices.model;

import java.io.Serializable;
import java.lang.String;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.*;
import zw.co.esolutions.ewallet.audit.Auditable;
import zw.co.esolutions.ewallet.enums.AlertOptionStatus;
import zw.co.esolutions.ewallet.util.MapUtil;

/**
 * Entity implementation class for Entity: AlertOption
 *
 */
@Entity
@NamedQueries({
	@NamedQuery(name="getAlertOptionByStatus",query="SELECT a FROM AlertOption a WHERE a.status =:status"),
	@NamedQuery(name="getAlertOptionByBankAccountId",query="SELECT a FROM AlertOption a WHERE a.bankAccountId =:bankAccountId"),
	@NamedQuery(name="getAlertOptionByMobileProfileId",query="SELECT a FROM AlertOption a WHERE a.mobileProfileId =:mobileProfileId"),
	@NamedQuery(name="getAlertOptionByTransationTypeId",query="SELECT a FROM AlertOption a WHERE a.transactionType.id =:transactionTypeId"),
	@NamedQuery(name="getAlertOptionByBankAccountAndMobileProfileAndTransactionType",query="SELECT a FROM AlertOption a WHERE a.bankAccountId =:bankAccountId AND a.mobileProfileId =:mobileProfileId AND a.transactionType.id =:transactionTypeId")
})
public class AlertOption implements Serializable,Auditable {

	   
	@Id
	@Column(length=30)
	private String id;
	@Column(length=30)
	private String bankAccountId;
	@Column(length=30)
	private String mobileProfileId;
	@Enumerated(EnumType.STRING)
	@Column(length=50)
	private AlertOptionStatus status;
	@ManyToOne
	private TransactionType transactionType;
	
	@Version
	private int version;
	
	private static final long serialVersionUID = 1L;

	public AlertOption() {
		super();
	}   
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}   
	public String getBankAccountId() {
		return this.bankAccountId;
	}

	public void setBankAccountId(String bankAccountId) {
		this.bankAccountId = bankAccountId;
	}   
	public String getMobileProfileId() {
		return this.mobileProfileId;
	}

	public void setMobileProfileId(String mobileProfileId) {
		this.mobileProfileId = mobileProfileId;
	}   
	public AlertOptionStatus getStatus() {
		return this.status;
	}

	public void setStatus(AlertOptionStatus status) {
		this.status = status;
	}
	public TransactionType getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}
	
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	@Override
	public Map<String, String> getAuditableAttributesMap() {
		Map<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put("alertOptionStatus",getStatus().toString());
		return attributesMap;
	}
	@Override
	public String getAuditableAttributesString() {
		return MapUtil.convertAttributesMapToString(getAuditableAttributesMap());
	}
	@Override
	public String getEntityName() {
		return "BANK ACCOUNT";
	}
	@Override
	public String getInstanceName() {
		return "Alert Option:"+bankAccountId;
	}
   
	
}
