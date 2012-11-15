package zw.co.esolutions.ewallet.msg;

import java.io.Serializable;

public class BulkBatchItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String targetMobileNumber;
	private long amount;
	private String sourceAccount;
	private String firstName;
	private String lastName;
	private String status;
	private String narrative;
	private String paymentReference;
	private String batchReference;
	public String getTargetMobileNumber() {
		return targetMobileNumber;
	}
	public void setTargetMobileNumber(String targetMobileNumber) {
		this.targetMobileNumber = targetMobileNumber;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public String getSourceAccount() {
		return sourceAccount;
	}
	public void setSourceAccount(String sourceAccount) {
		this.sourceAccount = sourceAccount;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getNarrative() {
		return narrative;
	}
	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}
	public String getPaymentReference() {
		return paymentReference;
	}
	public void setPaymentReference(String paymentReference) {
		this.paymentReference = paymentReference;
	}
	public String getBatchReference() {
		return batchReference;
	}
	public void setBatchReference(String batchReference) {
		this.batchReference = batchReference;
	}
	

}
