package zw.co.esolutions.ewallet.alertsservices.model;

import java.io.Serializable;
import java.lang.String;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.*;

import zw.co.esolutions.ewallet.audit.Auditable;
import zw.co.esolutions.ewallet.enums.TransactionTypeStatus;
import zw.co.esolutions.ewallet.util.MapUtil;

/**
 * Entity implementation class for Entity: TransactionType
 *
 */
@Entity
@NamedQueries({
	@NamedQuery(name="getAllTransactionTypes",query="SELECT t FROM TransactionType t WHERE t.status =:enabled OR t.status =:disabled"),
	@NamedQuery(name="getTransactionTypeByCode",query="SELECT t FROM TransactionType t WHERE t.transactionCode =:transactionCode"),
	@NamedQuery(name="getTransactionTypeByStatus",query="SELECT t FROM TransactionType t WHERE t.status =:status")
})
public class TransactionType implements Serializable,Auditable {
	   
	@Id
	@Column(length=30)
	private String id;
	@Column(length=30)
	private String transactionCode;
	@Column(length=200)
	private String description;
	@Enumerated(EnumType.STRING)
	@Column(length=50)
	private TransactionTypeStatus status;
	@OneToMany(mappedBy="transactionType")
	private List<AlertOption> alertOptions;
	
	@Version
	private int version;
	
	private static final long serialVersionUID = 1L;

	public TransactionType() {
		super();
	}   
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}   
	public String getTransactionCode() {
		return this.transactionCode;
	}

	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}   
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}   
	public TransactionTypeStatus getStatus() {
		return this.status;
	}

	public void setStatus(TransactionTypeStatus status) {
		this.status = status;
	}
	public List<AlertOption> getAlertOptions() {
		return alertOptions;
	}
	public void setAlertOptions(List<AlertOption> alertOptions) {
		this.alertOptions = alertOptions;
	}
	
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	
	public void setFieldsToUpper(){
		this.description = this.description.toUpperCase();
	}
	
	@Override
	public Map<String, String> getAuditableAttributesMap() {
		Map<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put("transactionCode", getTransactionCode());
		attributesMap.put("description", getDescription());
		attributesMap.put("status", getStatus().toString());
		return attributesMap;
	}
	@Override
	public String getAuditableAttributesString() {
		return MapUtil.convertAttributesMapToString(getAuditableAttributesMap());
	}
	@Override
	public String getEntityName() {
		return "TRANSACTION TYPE";
	}
	@Override
	public String getInstanceName() {
		return getTransactionCode();
	}
   
	
}
