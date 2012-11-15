package zw.co.esolutions.mcommerce.refgen.model;

import javax.persistence.Column;

public class CounterPK {

	@Column(name = "bankCode")
	private String bankCode;

	@Column(name = "date", length = 16)
	private String date;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public CounterPK() {
	}

	public CounterPK(String bankCode, String date) {
		super();
		this.bankCode = bankCode;
		this.date = date;
	}

	@Override
	public int hashCode() {
		return this.getBankCode().hashCode() + this.getDate().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof CounterPK) {
			CounterPK counterPK = (CounterPK) o;
			return counterPK.getDate().equalsIgnoreCase(this.getDate()) && counterPK.getBankCode().equalsIgnoreCase(this.getBankCode());
		} else {
			return false;
		}
	}

	/**
	 * @return the bankCode
	 */
	public String getBankCode() {
		return bankCode;
	}

	/**
	 * @param bankCode
	 *            the bankCode to set
	 */
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

}
