package zw.co.esolutions.ewallet.transaction.pojo;

import java.io.Serializable;

public class TxnPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3461515537157829231L;
	private String posting;
	private String accountNumber;
	private long amount;
	public TxnPojo() {
		
	}
	public String getPosting() {
		return posting;
	}
	public void setPosting(String posting) {
		this.posting = posting;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	
}
