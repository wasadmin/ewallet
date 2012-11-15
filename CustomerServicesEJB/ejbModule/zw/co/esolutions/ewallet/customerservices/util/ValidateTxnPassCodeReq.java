package zw.co.esolutions.ewallet.customerservices.util;

import java.io.Serializable;

public class ValidateTxnPassCodeReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mobileNumber;
	private Integer firstIndex;
	private Integer secondIndex;
	private Integer firstValue;
	private Integer secondValue;
	
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public Integer getFirstIndex() {
		return firstIndex;
	}
	public void setFirstIndex(Integer firstIndex) {
		this.firstIndex = firstIndex;
	}
	public Integer getSecondIndex() {
		return secondIndex;
	}
	public void setSecondIndex(Integer secondIndex) {
		this.secondIndex = secondIndex;
	}
	public Integer getFirstValue() {
		return firstValue;
	}
	public void setFirstValue(Integer firstValue) {
		this.firstValue = firstValue;
	}
	public Integer getSecondValue() {
		return secondValue;
	}
	public void setSecondValue(Integer secondValue) {
		this.secondValue = secondValue;
	}
	
}
