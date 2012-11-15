package zw.co.esolutions.mobile.banking.msg;

import java.io.Serializable;
import java.util.Date;

public class Pares implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3519049394865562283L;
	private String ussdSessionId;
	private String[] bankAccounts;
	private String narrative;
	private boolean verificationResult;
	private boolean agent;
	private boolean autoPinChange;
	private Date transactionTimestamp;
	
	public Pares() {
		super();
		
	}
	
	
	public String getUssdSessionId() {
		return ussdSessionId;
	}
	public void setUssdSessionId(String ussdSessionId) {
		this.ussdSessionId = ussdSessionId;
	}
	public String[] getBankAccounts() {
		return bankAccounts;
	}
	public void setBankAccounts(String[] bankAccounts) {
		this.bankAccounts = bankAccounts;
	}
	public String getNarrative() {
		return narrative;
	}
	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}
	public boolean isVerificationResult() {
		return verificationResult;
	}
	public void setVerificationResult(boolean verificationResult) {
		this.verificationResult = verificationResult;
	}
	public Date getTransactionTimestamp() {
		return transactionTimestamp;
	}
	public void setTransactionTimestamp(Date transactionTimestamp) {
		this.transactionTimestamp = transactionTimestamp;
	}


	public boolean isAgent() {
		return agent;
	}


	public void setAgent(boolean agent) {
		this.agent = agent;
	}


	public void setAutoPinChange(boolean autoPinChange) {
		this.autoPinChange = autoPinChange;
	}


	public boolean isAutoPinChange() {
		return autoPinChange;
	}

}
