/**
 * 
 */
package zw.co.esolutions.ewallet.adminweb.pojo;

import java.util.Date;

/**
 * @author tauttee
 *
 */
public class LimitPojo {

	private String id;
	private String custClass;
	private String limitType;
	private double max;
	private double min;
	private Date effectiveDate;
	private Date endDate;
	private String valueType;
	private String status;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCustClass() {
		return custClass;
	}
	public void setCustClass(String custClass) {
		this.custClass = custClass;
	}
	public String getLimitType() {
		return limitType;
	}
	public void setLimitType(String limitType) {
		this.limitType = limitType;
	}
	public double getMax() {
		return max;
	}
	public void setMax(double max) {
		this.max = max;
	}
	public double getMin() {
		return min;
	}
	public void setMin(double min) {
		this.min = min;
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
	public String getValueType() {
		return valueType;
	}
	public void setValueType(String valueType) {
		this.valueType = valueType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
