/**
 * 
 */
package zw.co.esolutions.ewallet.tariffservices.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
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
import zw.co.esolutions.ewallet.enums.AgentType;
import zw.co.esolutions.ewallet.enums.CustomerClass;
import zw.co.esolutions.ewallet.enums.TariffStatus;
import zw.co.esolutions.ewallet.enums.TariffType;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.util.MapUtil;

/**
 * @author tauttee
 *
 */
@Entity
@NamedQueries( { @NamedQuery(name = "getTariffTable", query = "SELECT t FROM TariffTable t ORDER BY t.bankId DESC, t.dateCreated DESC"),
	@NamedQuery(name = "getAllTariffTablesByBankId", query = "SELECT t FROM TariffTable t WHERE t.bankId = :bankId"),
	@NamedQuery(name = "getTariffTableByTariffTypeAndBankId", query = "SELECT t FROM TariffTable t WHERE t.tariffType = :tariffType " +
			"AND t.bankId LIKE :bankId ORDER BY t.bankId DESC, t.dateCreated DESC"), 
	@NamedQuery(name = "getTariffTableByCustomerClassAndBankId", query = "SELECT t FROM TariffTable t WHERE t.customerClass = :customerClass " +
			"AND t.bankId LIKE :bankId ORDER BY t.bankId DESC, t.dateCreated DESC"), 
	@NamedQuery(name = "getTariffTableByTariffStatusAndBankId", query = "SELECT t FROM TariffTable t WHERE t.status = :status " +
			"AND t.bankId LIKE :bankId ORDER BY t.bankId DESC, t.dateCreated DESC"), 
	@NamedQuery(name = "getTariffTableByTransactionTypeAndBankId", query = "SELECT t FROM TariffTable t WHERE t.transactionType = :transactionType " +
			"AND t.bankId LIKE :bankId ORDER BY t.bankId DESC, t.dateCreated DESC"), 
	@NamedQuery(name = "getTariffTableByAgentType", query = "SELECT t FROM TariffTable t WHERE t.agentType = :agentType " +
			"ORDER BY t.dateCreated DESC"), 
	@NamedQuery(name = "getTariffTableByTariffTypeEffectiveDateAndBankId", query = "SELECT t FROM TariffTable t WHERE t.tariffType = :tariffType " +
			"AND t.effectiveDate = :effectiveDate AND t.agentType = :agentType AND t.bankId = :bankId ORDER BY t.dateCreated DESC"), 
    @NamedQuery(name = "getTariffTableByTransactionTypeEffectiveDateAgentTypeAndBankId", query = "SELECT t FROM TariffTable t WHERE t.transactionType = :transactionType " +
			"AND t.effectiveDate = :effectiveDate AND t.agentType = :agentType " +
			"AND t.bankId = :bankId ORDER BY t.dateCreated DESC"), 
	@NamedQuery(name = "getTariffTableByTariffTypeTransactionAgentTypeAndBankId", query = "SELECT t FROM TariffTable t WHERE t.transactionType = :transactionType " +
					"AND t.tariffType = :type AND t.agentType = :agentType " +
					"AND t.bankId = :bankId ORDER BY t.dateCreated DESC"), 
	@NamedQuery(name = "getTariffTableByTariffTypeTransactionTypeEffectiveDateAndBankId", query = "SELECT t FROM TariffTable t WHERE t.tariffType = :tariffType " +
			"AND t.transactionType = :transactionType AND t.effectiveDate = :effectiveDate AND t.agentType = :agentType AND t.bankId = :bankId ORDER BY t.dateCreated DESC"), 
	@NamedQuery(name = "getTariffTableByTariffTypeTransactionTypeEffectiveDateEndDateAndBankId", query = "SELECT t FROM TariffTable t WHERE t.tariffType = :tariffType " +
			"AND t.transactionType = :transactionType AND t.effectiveDate = :effectiveDate AND t.agentType = :agentType " +
			"AND t.endDate = :endDate AND t.bankId = :bankId ORDER BY t.dateCreated DESC"),
	@NamedQuery(name = "getTariffTableByAllAttributes", query = "SELECT t FROM TariffTable t WHERE t.transactionType = :transactionType AND t.effectiveDate = :effectiveDate AND t.agentType = :agentType " +
					"AND t.customerClass = :customerClass AND t.endDate = :endDate " +
					"AND t.bankId = :bankId ORDER BY t.dateCreated DESC"),
	@NamedQuery(name = "getTariffTableByCustomerClass", query = "SELECT t FROM TariffTable t WHERE t.customerClass = :customerClass " +
					"ORDER BY t.dateCreated DESC"),
	@NamedQuery(name = "getTariffTableByDateRangeAndBankId", query = "SELECT t FROM TariffTable t WHERE (t.effectiveDate <= :fromDate " +
					"AND t.effectiveDate <= :toDate AND t.endDate >= toDate AND t.endDate >= :fromDate) OR (t.effectiveDate <= :fromDate AND (t.endDate IS NULL OR t.endDate >= :toDate)) " +
					"AND t.bankId LIKE :bankId AND (t.status = :active OR t.status = :inActive) ORDER BY t.bankId DESC, t.dateCreated DESC"), 
	@NamedQuery(name = "getTariffTableByDateRangeAndBankIdRevised", query = "SELECT t FROM TariffTable t WHERE (t.effectiveDate >= :fromDate " +
							"AND t.effectiveDate <= :toDate AND t.endDate <= toDate AND t.endDate >= :fromDate) OR (t.effectiveDate >= :fromDate AND (t.endDate IS NULL OR (t.endDate >= :toDate))) " +
							"AND t.bankId LIKE :bankId AND (t.status = :active OR t.status = :inActive) ORDER BY t.bankId DESC, t.dateCreated DESC"),
	@NamedQuery(name = "getEffectiveTariffTablesForBank", query = "SELECT t FROM TariffTable t WHERE t.effectiveDate <= :fromDate AND (t.endDate IS NULL OR t.endDate >= :toDate) " +
					"AND t.bankId LIKE :bankId AND (t.status = :active OR t.status = :inActive) ORDER BY t.bankId DESC, t.transactionType ASC"),
	@NamedQuery(name = "getActiveTariffTableByTypesAndClass", query = "SELECT t FROM TariffTable t WHERE t.tariffType = :tariffType " +
					"AND t.endDate IS NULL AND t.agentType = :agentType AND t.customerClass = :customerClass " +
					"AND t.transactionType = :transactionType")
	})
public class TariffTable implements Serializable,Auditable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(length = 30)
	protected String id;
	@Column(length = 30)
	private String oldTariffTableId;
	@Column(length = 30)
	private String bankId;
	@Enumerated(EnumType.STRING)
	@Column(length = 50)
	protected TransactionType transactionType;
	@Enumerated(EnumType.STRING)
	@Column(length = 50)
	protected TariffType tariffType;
	@Enumerated(EnumType.STRING)
	@Column(length = 50)
	protected AgentType agentType;
	@Enumerated(EnumType.STRING)
	@Column(length = 50)
	protected CustomerClass customerClass;
	@Enumerated(EnumType.STRING)
	@Column(length = 50)
	private TariffStatus status;
	protected Date effectiveDate;
	protected Date endDate;
	protected Date dateCreated;
	@OneToMany(mappedBy = "tariffTable")
	protected List<Tariff> tariffs;
	@Version
	protected long version;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public TransactionType getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}
	public TariffType getTariffType() {
		return tariffType;
	}
	public void setTariffType(TariffType tariffType) {
		this.tariffType = tariffType;
	}
	public Date getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
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
	public void setTariffs(List<Tariff> tariffs) {
		this.tariffs = tariffs;
	}
	public List<Tariff> getTariffs() {
		return tariffs;
	}
	public AgentType getAgentType() {
		return agentType;
	}
	public void setAgentType(AgentType agentType) {
		this.agentType = agentType;
	}
	public CustomerClass getCustomerClass() {
		return customerClass;
	}
	public void setCustomerClass(CustomerClass customerClass) {
		this.customerClass = customerClass;
	}
	public void setBankId(String bankId) {
		this.bankId = bankId;
	}
	public String getBankId() {
		return bankId;
	}
	public void setStatus(TariffStatus status) {
		this.status = status;
	}
	public TariffStatus getStatus() {
		return status;
	}
	public void setOldTariffTableId(String oldTariffTableId) {
		this.oldTariffTableId = oldTariffTableId;
	}
	public String getOldTariffTableId() {
		return oldTariffTableId;
	}
	@Override
	public Map<String, String> getAuditableAttributesMap() {
		Map<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put("transactionType", getTransactionType().toString());
		attributesMap.put("tariffType", getTariffType().toString());
		attributesMap.put("agentType", getAgentType().toString());
		attributesMap.put("customerClass",getCustomerClass().toString());
		attributesMap.put("status", getStatus().toString());
		attributesMap.put("effectiveDate", getEffectiveDate().toString());
		if(getEndDate() != null) {
			attributesMap.put("endDate", getEndDate().toString());
		}
		return attributesMap;
	}
	@Override
	public String getAuditableAttributesString() {
		return MapUtil.convertAttributesMapToString(getAuditableAttributesMap());
	}
	@Override
	public String getEntityName() {
		return "COMMISSION TABLE";
	}
	@Override
	public String getInstanceName() {
		return getTransactionType().toString();
	}
	
	
}
