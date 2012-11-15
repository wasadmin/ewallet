package zw.co.esolutions.mobile.banking.msg;

import java.io.Serializable;
import java.util.Date;

public class Veres implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4680859367281353003L;
	private String ussdSessionId;
	private boolean enrolmentResult;
	private Date transactionTimestamp;
	
	public Veres() {
		super();
		
	}

	public String getUssdSessionId() {
		return ussdSessionId;
	}

	public void setUssdSessionId(String ussdSessionId) {
		this.ussdSessionId = ussdSessionId;
	}

	public boolean isEnrolmentResult() {
		return enrolmentResult;
	}

	public void setEnrolmentResult(boolean enrolmentResult) {
		this.enrolmentResult = enrolmentResult;
	}

	public Date getTransactionTimestamp() {
		return transactionTimestamp;
	}

	public void setTransactionTimestamp(Date transactionTimestamp) {
		this.transactionTimestamp = transactionTimestamp;
	}

}
