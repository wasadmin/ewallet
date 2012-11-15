package zw.co.esolutions.mobile.banking.msg;

import java.io.Serializable;
import java.util.Date;

public class Pareq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5391913552467075401L;
	private String ussdSessionId;
	private String password;
	private Date transactionTimestamp;
	
	public Pareq() {
		super();
		
	}
	
	public String getUssdSessionId() {
		return ussdSessionId;
	}
	public void setUssdSessionId(String ussdSessionId) {
		this.ussdSessionId = ussdSessionId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Date getTransactionTimestamp() {
		return transactionTimestamp;
	}
	public void setTransactionTimestamp(Date transactionTimestamp) {
		this.transactionTimestamp = transactionTimestamp;
	}

}
