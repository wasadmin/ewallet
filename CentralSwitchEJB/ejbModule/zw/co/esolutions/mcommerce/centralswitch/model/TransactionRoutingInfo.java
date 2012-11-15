package zw.co.esolutions.mcommerce.centralswitch.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Entity implementation class for Entity: TransactionRoutingInfo
 * 
 */
//@Entity
@Embeddable
public class TransactionRoutingInfo implements Serializable {

	@Column(length = 30)
	private String bankRequestQueueName;

	@Column(length = 30)
	private String bankReplyQueueName;

	@Column(length = 30)
	private String merchantRequestQueueName;

	@Column(length = 30)
	private String merchantReplyQueueName;

	@Column(length = 30)
	private String bankName;
	
	@Column(length = 10)
	private String zswCode;
	
	private int zesaPayCode;
	private int zesaPayBranch;
	private boolean applyVendorSignature;
	private boolean accountValidationEnabled;
	private boolean straightThroughEnabled;
	private boolean notificationEnabled;

	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return this.bankName +"|"+ this.bankRequestQueueName +"|" + this.bankReplyQueueName + "|" + this.merchantReplyQueueName + "|" + this.merchantRequestQueueName + "|" + this.zesaPayBranch + "|" + this.zesaPayBranch + "|" + this.zesaPayCode;
	}
	
	public TransactionRoutingInfo() {
		super();
	}

	public String getBankRequestQueueName() {
		return bankRequestQueueName;
	}

	public void setBankRequestQueueName(String bankRequestQueueName) {
		this.bankRequestQueueName = bankRequestQueueName;
	}

	public String getBankReplyQueueName() {
		return bankReplyQueueName;
	}

	public void setBankReplyQueueName(String bankReplyQueueName) {
		this.bankReplyQueueName = bankReplyQueueName;
	}

	public String getMerchantReplyQueueName() {
		return merchantReplyQueueName;
	}

	public void setMerchantReplyQueueName(String merchantReplyQueueName) {
		this.merchantReplyQueueName = merchantReplyQueueName;
	}

	public boolean isAccountValidationEnabled() {
		return accountValidationEnabled;
	}

	public void setAccountValidationEnabled(boolean accountValidationEnabled) {
		this.accountValidationEnabled = accountValidationEnabled;
	}

	public boolean isStraightThroughEnabled() {
		return straightThroughEnabled;
	}

	public void setStraightThroughEnabled(boolean straightThroughEnabled) {
		this.straightThroughEnabled = straightThroughEnabled;
	}

	public boolean isNotificationEnabled() {
		return notificationEnabled;
	}

	public void setNotificationEnabled(boolean notificationEnabled) {
		this.notificationEnabled = notificationEnabled;
	}

	public String getMerchantRequestQueueName() {
		return merchantRequestQueueName;
	}

	public void setMerchantRequestQueueName(String merchantRequestQueueName) {
		this.merchantRequestQueueName = merchantRequestQueueName;
	}

	public int getZesaPayCode() {
		return zesaPayCode;
	}

	public void setZesaPayCode(int zesaPayCode) {
		this.zesaPayCode = zesaPayCode;
	}

	public int getZesaPayBranch() {
		return zesaPayBranch;
	}

	public void setZesaPayBranch(int zesaPayBranch) {
		this.zesaPayBranch = zesaPayBranch;
	}

	public boolean isApplyVendorSignature() {
		return applyVendorSignature;
	}

	public void setApplyVendorSignature(boolean applyVendorSignature) {
		this.applyVendorSignature = applyVendorSignature;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getZswCode() {
		return zswCode;
	}

	public void setZswCode(String zswCode) {
		this.zswCode = zswCode;
	}

}
