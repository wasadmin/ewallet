package zw.co.esolutions.mobile.banking.msg;

import java.io.Serializable;
import java.util.Date;

public class TransactionResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2522438377393821929L;
	private String ussdSessionId;
	private String narrative;
	private Date transactionTimestamp;
	
	public TransactionResponse() {
		super();
		
	}
	public String getUssdSessionId() {
		return ussdSessionId;
	}
	public void setUssdSessionId(String ussdSessionId) {
		this.ussdSessionId = ussdSessionId;
	}
	public String getNarrative() {
		return narrative;
	}
	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}
	public Date getTransactionTimestamp() {
		return transactionTimestamp;
	}
	public void setTransactionTimestamp(Date transactionTimestamp) {
		this.transactionTimestamp = transactionTimestamp;
	}

}
