package zw.co.esolutions.mcommerce.refgen.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Version;


/**
 * Entity implementation class for Entity: Counter
 * 
 */
@Entity
@IdClass(zw.co.esolutions.mcommerce.refgen.model.CounterPK.class)
public class Counter implements Serializable {

	@Id
	@Column(name = "bankCode", length = 25)
	private String bankCode;

	@Column(name = "counterValue")
	private long count;
	
	@Column(name = "maxValue")
	private long maxValue;
	
	@Column(name = "minValue")
	private long minValue;
	@Version
	private long version;

	@Id
	@Column(name = "counterDate", length = 16)
	private String date;

	public long getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(long maxValue) {
		this.maxValue = maxValue;
	}

	public long getMinValue() {
		return minValue;
	}

	public void setMinValue(long minValue) {
		this.minValue = minValue;
	}
	
	
	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	private static final long serialVersionUID = 1L;

	public Counter() {
		super();
	}

	public Counter(String bankCode, long count, String date) {
		super();
		this.bankCode = bankCode;
		this.count = count;
		this.date = date;
	}

	/**
	 * @return the bankCode
	 */
	public String getBankCode() {
		return bankCode;
	}

	/**
	 * @param bankCode the bankCode to set
	 */
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

}
