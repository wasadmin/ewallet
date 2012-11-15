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
public class BankResponse implements Serializable {

	private String ussdSessionId;
	private BankInfo [] banks;
	private Date transactionTimestamp;
	/**
	 * 
	 */
	private static final long serialVersionUID = -8857790839281590687L;

	/**
	 * 
	 */
	public BankResponse() {
		// TODO Auto-generated constructor stub
	}

	public String getUssdSessionId() {
		return ussdSessionId;
	}

	public void setUssdSessionId(String ussdSessionId) {
		this.ussdSessionId = ussdSessionId;
	}

	public BankInfo[] getBanks() {
		return banks;
	}

	public void setBanks(BankInfo[] banks) {
		this.banks = banks;
	}

	public void setTransactionTimestamp(Date transactionTimestamp) {
		this.transactionTimestamp = transactionTimestamp;
	}

	public Date getTransactionTimestamp() {
		return transactionTimestamp;
	}

}
