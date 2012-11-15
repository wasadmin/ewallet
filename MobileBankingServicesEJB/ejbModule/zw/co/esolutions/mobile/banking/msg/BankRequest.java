/**
 * 
 */
package zw.co.esolutions.mobile.banking.msg;

import java.io.Serializable;
import java.util.Date;

/**
 * @author taurai
 *
 */
public class BankRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4892654187104413341L;
	private String ussdSessionId;
	private Date transactionTimestamp;
	/**
	 * 
	 */
	public BankRequest() {
		// TODO Auto-generated constructor stub
	}
	public void setUssdSessionId(String ussdSessionId) {
		this.ussdSessionId = ussdSessionId;
	}
	public String getUssdSessionId() {
		return ussdSessionId;
	}
	public void setTransactionTimestamp(Date transactionTimestamp) {
		this.transactionTimestamp = transactionTimestamp;
	}
	public Date getTransactionTimestamp() {
		return transactionTimestamp;
	}

}
