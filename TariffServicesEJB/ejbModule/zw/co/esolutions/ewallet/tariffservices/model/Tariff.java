/**
 * 
 */
package zw.co.esolutions.ewallet.tariffservices.model;

import java.io.Serializable;
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
import zw.co.esolutions.ewallet.enums.TariffValueType;
import zw.co.esolutions.ewallet.util.MapUtil;

/**
 * @author tauttee
 *
 */
@Entity
@NamedQueries( { @NamedQuery(name = "getTariff", query = "SELECT t FROM Tariff t ORDER BY t.dateCreated DESC"), 
	@NamedQuery(name = "getAllTariffsByBankId", query = "SELECT t FROM Tariff t WHERE t.tariffTable.bankId = :bankId ORDER BY t.dateCreated DESC"),
	@NamedQuery(name = "getTariffByTariffTableAndBankId", query = "SELECT t FROM Tariff t WHERE t.tariffTable.id = :tariffTableId AND t.tariffTable.bankId = :bankId ORDER BY t.value ASC"),
	@NamedQuery(name = "getTariffByTariffTableIdValueTypeAndBankId", query = "SELECT t FROM Tariff t WHERE t.tariffTable.id = :tariffTableId " +
			"AND t.valueType = :valueType AND t.tariffTable.bankId = :bankId"),
	@NamedQuery(name = "getTariffByTableAndValueType", query = "SELECT t FROM Tariff t WHERE t.tariffTable.id = :tariffTableId " +
			"AND t.valueType = :valueType ORDER BY t.dateCreated DESC"),
	@NamedQuery(name = "getTariffByValueType", query = "SELECT t FROM Tariff t WHERE t.valueType = :valueType"), 
	@NamedQuery(name = "getTariffByTariffTableType", query = "SELECT t FROM Tariff t WHERE t.tariffTable.type = :type " +
			"ORDER BY t.dateCreated DESC"), 
	@NamedQuery(name = "getTariffByTariffTableTransactionType", query = "SELECT t FROM Tariff t WHERE t.tariffTable.transactionType = :transactionType " +
			"ORDER BY t.dateCreated DESC"),
    @NamedQuery(name = "getTariffByTariffTableTypeTransactionTypeEffectiveDateAndValueType", query = "SELECT t FROM Tariff t WHERE t.tariffTable.type = :type " +
			"AND t.tariffTable.transactionType = :transactionType " +
			"AND t.tariffTable.effectiveDate = :effectiveDate " +
			"AND t.valueType = :valueTypeT ORDER BY t.dateCreated DESC"),
    @NamedQuery(name = "getTariffsByTariffTableId", query = "SELECT t FROM Tariff t WHERE t.tariffTable.id = :tableId"),
	@NamedQuery(name = "getTariffByTariffTableTypeTransactionTypeEffectiveDateEndDateAndValueType", query = "SELECT t FROM Tariff t WHERE t.tariffTable.type = :type " +
			"AND t.tariffTable.transactionType = :transactionType " +
			"AND t.tariffTable.effectiveDate = :effectiveDate " +
			"AND t.tariffTable.endDate = :endDate " +
			"AND t.valueType = :valueTypeT ORDER BY t.dateCreated DESC"),
	@NamedQuery(name = "getExactTariff", query = "SELECT t FROM Tariff t WHERE t.tariffTable.id = :tariffTableId " +
			"AND t.tariffTable.bankId = :bankId AND t.valueType = :valueType AND t.lowerLimit = :lowerLimit AND t.upperLimit = :upperLimit " +
			"AND t.value = :value"),
	@NamedQuery(name = "getActualTariff", query = "SELECT t FROM Tariff t WHERE t.tariffTable.transactionType = :transactionType " +
					"AND t.tariffTable.effectiveDate <= :onDateStart " +
					"AND (t.tariffTable.endDate IS NULL OR t.tariffTable.endDate >= :onDateEnd) " +
					"AND t.tariffTable.customerClass = :customerClass " +
					"AND ((t.tariffTable.tariffType = :percPlusLimits) OR (t.tariffTable.tariffType = :fixedAmount) OR (t.tariffTable.tariffType = :fixedPerc) OR ((t.tariffTable.tariffType = :scaledPerc OR t.tariffTable.tariffType = :scaled) " +
					"AND (t.lowerLimit <= :amount AND t.upperLimit >= :amount ))) AND (t.tariffTable.status = :active OR t.tariffTable.status = :inActive)" +
					"AND t.tariffTable.agentType = :agentType AND t.tariffTable.bankId = :bankId")
					
	})
public class Tariff implements Serializable,Auditable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(length = 30)
	protected String id;
	@Column(length = 30)
	private String oldTariffId;
	@Enumerated(EnumType.STRING)
	@Column(length = 50)
	protected TariffValueType valueType;
	protected long lowerLimit;
	protected long upperLimit;
	protected long value;
	protected Date dateCreated;
	@ManyToOne
	protected TariffTable tariffTable;
	@Version
	protected long version;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getLowerLimit() {
		return lowerLimit;
	}
	public void setLowerLimit(long lowerLimit) {
		this.lowerLimit = lowerLimit;
	}
	public long getUpperLimit() {
		return upperLimit;
	}
	public void setUpperLimit(long upperLimit) {
		this.upperLimit = upperLimit;
	}
	public long getValue() {
		return value;
	}
	public void setValue(long value) {
		this.value = value;
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
	public void setTariffTable(TariffTable tariffTable) {
		this.tariffTable = tariffTable;
	}
	public TariffTable getTariffTable() {
		return tariffTable;
	}
	public TariffValueType getValueType() {
		return valueType;
	}
	public void setValueType(TariffValueType valueType) {
		this.valueType = valueType;
	}
	public void setOldTariffId(String oldTariffId) {
		this.oldTariffId = oldTariffId;
	}
	public String getOldTariffId() {
		return oldTariffId;
	}
	
	@Override
	public Map<String, String> getAuditableAttributesMap() {
		Map<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put("valueType",getValueType().toString());
		attributesMap.put("lowerLimit", getLowerLimit()+"");
		attributesMap.put("upperLimit", getUpperLimit()+"");
		attributesMap.put("value", getValue()+"");
		return attributesMap;
	}
	@Override
	public String getAuditableAttributesString() {
		return MapUtil.convertAttributesMapToString(getAuditableAttributesMap());
	}
	@Override
	public String getEntityName() {
		return "COMMISSION";
	}
	@Override
	public String getInstanceName() {
		return getDateCreated()+" "+getValue();
	}

}
