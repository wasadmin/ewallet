package zw.co.esolutions.mobile.banking.msg;

import java.io.Serializable;
import java.util.Date;

public class MerchantRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4849132402732284417L;
	private String ussdSessionId;
	private boolean thirdParty;
	private Date transactionTimestamp;
	
	
	public MerchantRequest() {
		super();
		
	}
	
	public String getUssdSessionId() {
		return ussdSessionId;
	}
	public void setUssdSessionId(String ussdSessionId) {
		this.ussdSessionId = ussdSessionId;
	}
	public boolean isThirdParty() {
		return thirdParty;
	}
	public void setThirdParty(boolean thirdParty) {
		this.thirdParty = thirdParty;
	}
	public Date getTransactionTimestamp() {
		return transactionTimestamp;
	}
	public void setTransactionTimestamp(Date transactionTimestamp) {
		this.transactionTimestamp = transactionTimestamp;
	}

}
