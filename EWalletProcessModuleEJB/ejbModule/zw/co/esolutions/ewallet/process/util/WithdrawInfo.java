/**
 * 
 */
package zw.co.esolutions.ewallet.process.util;

import java.io.Serializable;

/**
 * @author tauttee
 *
 */
public class WithdrawInfo implements Serializable {

	/**
	 * 
	 */
	private String mobileNumber;
	private long amount;
	private String bankCode;
	private long runningBalance;
	private static final long serialVersionUID = 1L;
	
	public WithdrawInfo() {
		super();
		
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public long getRunningBalance() {
		return runningBalance;
	}

	public void setRunningBalance(long runningBalance) {
		this.runningBalance = runningBalance;
	}
	
}
