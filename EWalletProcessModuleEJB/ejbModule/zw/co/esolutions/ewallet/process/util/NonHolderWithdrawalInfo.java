package zw.co.esolutions.ewallet.process.util;

import java.io.Serializable;

import zw.co.esolutions.ewallet.enums.TransactionStatus;

public class NonHolderWithdrawalInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private TransactionStatus status;
	private String targetMobile;
	private String secretCode;
	private double amount;
	private String reference;
	
	public TransactionStatus getStatus() {
		return status;
	}
	public void setStatus(TransactionStatus status) {
		this.status = status;
	}
	public String getTargetMobile() {
		return targetMobile;
	}
	public void setTargetMobile(String targetMobile) {
		this.targetMobile = targetMobile;
	}
	public String getSecretCode() {
		return secretCode;
	}
	public void setSecretCode(String secretCode) {
		this.secretCode = secretCode;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getReference() {
		return reference;
	}
	
}
