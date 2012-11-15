package zw.co.esolutions.mobile.banking.msg;

import java.io.Serializable;
import java.util.Date;

public class MerchantResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6162653664824221173L;
	private String ussdSessionId;
	private MerchantInfo[] merchants;
	private Date transactionTimestamp;
	
	public MerchantResponse() {
		super();
		
	}
	
	public String getUssdSessionId() {
		return ussdSessionId;
	}
	public void setUssdSessionId(String ussdSessionId) {
		this.ussdSessionId = ussdSessionId;
	}
	public MerchantInfo[] getMerchants() {
		return merchants;
	}
	public void setMerchants(MerchantInfo[] merchants) {
		this.merchants = merchants;
	}
	public Date getTransactionTimestamp() {
		return transactionTimestamp;
	}
	public void setTransactionTimestamp(Date transactionTimestamp) {
		this.transactionTimestamp = transactionTimestamp;
	}

}
