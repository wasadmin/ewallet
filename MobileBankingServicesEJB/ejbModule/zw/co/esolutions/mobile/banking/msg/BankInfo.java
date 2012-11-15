/**
 * 
 */
package zw.co.esolutions.mobile.banking.msg;

import java.io.Serializable;

/**
 * @author taurai
 *
 */
public class BankInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4734727696632727619L;
	private String bankName;
	private String bankCode;
	/**
	 * 
	 */
	public BankInfo() {
		// TODO Auto-generated constructor stub
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

}
