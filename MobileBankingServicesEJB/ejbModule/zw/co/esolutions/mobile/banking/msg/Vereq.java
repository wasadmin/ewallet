package zw.co.esolutions.mobile.banking.msg;

import java.io.Serializable;
import java.util.Date;

import zw.co.esolutions.mobile.banking.msg.enums.MobileNetworkOperator;

public class Vereq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4343219385016286482L;
	private String ussdSessionId;
	private String mobileNumber;
	private String bankCode;
	private String acquirerId;
	private MobileNetworkOperator sourceMobileNetworkOperator;
	private Date transactionTimestamp;
	
	public Vereq() {
		super();
		
	}
	
	public String getUssdSessionId() {
		return ussdSessionId;
	}
	public void setUssdSessionId(String ussdSessionId) {
		this.ussdSessionId = ussdSessionId;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public MobileNetworkOperator getSourceMobileNetworkOperator() {
		return sourceMobileNetworkOperator;
	}
	public void setSourceMobileNetworkOperator(MobileNetworkOperator sourceMobileNetworkOperator) {
		this.sourceMobileNetworkOperator = sourceMobileNetworkOperator;
	}
	public Date getTransactionTimestamp() {
		return transactionTimestamp;
	}
	public void setTransactionTimestamp(Date transactionTimestamp) {
		this.transactionTimestamp = transactionTimestamp;
	}

	public void setAcquirerId(String acquirerId) {
		this.acquirerId = acquirerId;
	}

	public String getAcquirerId() {
		return acquirerId;
	}

}
