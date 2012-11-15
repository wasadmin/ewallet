package zw.co.esolutions.ewallet.process.util;

public class RefCommInfo {
	private String referralId;
	private String referrerMobile;
	private String referredMobile;
	private long referrerCommission;
	private long referredCommission;
	
	public void setReferralId(String referralId) {
		this.referralId = referralId;
	}
	public String getReferralId() {
		return referralId;
	}
	public String getReferrerMobile() {
		return referrerMobile;
	}
	public void setReferrerMobile(String referrerMobile) {
		this.referrerMobile = referrerMobile;
	}
	public String getReferredMobile() {
		return referredMobile;
	}
	public void setReferredMobile(String referredMobile) {
		this.referredMobile = referredMobile;
	}
	public long getReferrerCommission() {
		return referrerCommission;
	}
	public void setReferrerCommission(long referrerCommission) {
		this.referrerCommission = referrerCommission;
	}
	public long getReferredCommission() {
		return referredCommission;
	}
	public void setReferredCommission(long referredCommission) {
		this.referredCommission = referredCommission;
	}
	
}
