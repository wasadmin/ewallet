package zw.co.esolutions.mcommerce.centralswitch.util;

import java.io.Serializable;

import zw.co.esolutions.ewallet.msg.RequestInfo;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.mcommerce.centralswitch.model.TransactionRoutingInfo;

public class SMSParseResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6725116677162734289L;
	private RequestInfo  requestInfo;
	private TransactionRoutingInfo txnMetaData;
	private ResponseCode responseCode;
	private String narrative;
	
	public SMSParseResponse(RequestInfo requestInfo, ResponseCode responseCode, String narrative, TransactionRoutingInfo txnMetaData) {
		super();
		this.requestInfo = requestInfo;
		this.responseCode = responseCode;
		this.narrative = narrative;
		this.txnMetaData = txnMetaData;
	}

	public RequestInfo getRequestInfo() {
		return requestInfo;
	}

	public void setRequestInfo(RequestInfo requestInfo) {
		this.requestInfo = requestInfo;
	}

	public ResponseCode getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(ResponseCode responseCode) {
		this.responseCode = responseCode;
	}

	public String getNarrative() {
		return narrative;
	}

	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}

	public TransactionRoutingInfo getTxnMetaData() {
		return txnMetaData;
	}

	public void setTxnMetaData(TransactionRoutingInfo txnMetaData) {
		this.txnMetaData = txnMetaData;
	}

}
