package zw.co.esolutions.ewallet.msg;

import java.io.Serializable;

import zw.co.esolutions.ewallet.sms.ResponseCode;

public class SMSParseResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6725116677162734289L;
	private RequestInfo  requestInfo;
	private ResponseCode responseCode;
	private String narrative;
	
	public SMSParseResponse(RequestInfo requestInfo, ResponseCode responseCode, String narrative) {
		super();
		this.requestInfo = requestInfo;
		this.responseCode = responseCode;
		this.narrative = narrative;
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

}
