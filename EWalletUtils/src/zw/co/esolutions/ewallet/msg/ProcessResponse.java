package zw.co.esolutions.ewallet.msg;

import java.io.Serializable;

public class ProcessResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String responseCode;
	private String messageId;
	private long amount;
	private long balance;
	private String narrative;
	/**
	 * 
	 */
	
	public ProcessResponse() {
		// TODO Auto-generated constructor stub
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public long getBalance() {
		return balance;
	}

	public void setBalance(long balance) {
		this.balance = balance;
	}

	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}

	public String getNarrative() {
		return narrative;
	}

}
