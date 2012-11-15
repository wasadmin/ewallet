/**
 * 
 */
package zw.co.esolutions.ewallet.msg;

import java.io.Serializable;

import zw.co.esolutions.ewallet.enums.MerchantRequestType;

/**
 * @author blessing
 * 
 */
public class MerchantRequest implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String reference;
	private long amount;
	private String merchantId;
	private String merchantName;
	private String customerUtilityAccount;
	private String sourceMobileNumber;
	private String targetMobileNumber;
	private String merchantRef;
	private MerchantRequestType merchantRequestType;

	/**
	 * 
	 */
	public MerchantRequest() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * @param reference
	 *            the reference to set
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}

	/**
	 * @return the amount
	 */
	public long getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(long amount) {
		this.amount = amount;
	}

	/**
	 * @return the merchantId
	 */
	public String getMerchantId() {
		return merchantId;
	}

	/**
	 * @param merchantId
	 *            the merchantId to set
	 */
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	/**
	 * @return the merchantName
	 */
	public String getMerchantName() {
		return merchantName;
	}

	/**
	 * @param merchantName
	 *            the merchantName to set
	 */
	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	/**
	 * @return the customerUtilityAccount
	 */
	public String getCustomerUtilityAccount() {
		return customerUtilityAccount;
	}

	/**
	 * @param customerUtilityAccount
	 *            the customerUtilityAccount to set
	 */
	public void setCustomerUtilityAccount(String customerUtilityAccount) {
		this.customerUtilityAccount = customerUtilityAccount;
	}

	/**
	 * @return the sourceMobileNumber
	 */
	public String getSourceMobileNumber() {
		return sourceMobileNumber;
	}

	/**
	 * @param sourceMobileNumber
	 *            the sourceMobileNumber to set
	 */
	public void setSourceMobileNumber(String sourceMobileNumber) {
		this.sourceMobileNumber = sourceMobileNumber;
	}

	/**
	 * @return the targetMobileNumber
	 */
	public String getTargetMobileNumber() {
		return targetMobileNumber;
	}

	/**
	 * @param targetMobileNumber
	 *            the targetMobileNumber to set
	 */
	public void setTargetMobileNumber(String targetMobileNumber) {
		this.targetMobileNumber = targetMobileNumber;
	}

	/**
	 * @return the merchantRequestType
	 */
	public MerchantRequestType getMerchantRequestType() {
		return merchantRequestType;
	}

	/**
	 * @param merchantRequestType the merchantRequestType to set
	 */
	public void setMerchantRequestType(MerchantRequestType merchantRequestType) {
		this.merchantRequestType = merchantRequestType;
	}

	public String getMerchantRef() {
		return merchantRef;
	}

	public void setMerchantRef(String merchantRef) {
		this.merchantRef = merchantRef;
	}
	
}
