/**
 * 
 */
package zw.co.esolutions.mobile.banking.msg;

import java.io.Serializable;

/**
 * @author taurai
 *
 */
public class MerchantInfo implements Serializable {

	/**
	 * 
	 */
	private String merchantName;
	private String utilityAccount;
	private static final long serialVersionUID = -7829603661821366538L;

	/**
	 * 
	 */
	public MerchantInfo() {
		// TODO Auto-generated constructor stub
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getUtilityAccount() {
		return utilityAccount;
	}

	public void setUtilityAccount(String utilityAccount) {
		this.utilityAccount = utilityAccount;
	}

}
