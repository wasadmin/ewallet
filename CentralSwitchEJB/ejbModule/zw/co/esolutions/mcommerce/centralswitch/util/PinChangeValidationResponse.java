package zw.co.esolutions.mcommerce.centralswitch.util;

import zw.co.esolutions.ewallet.msg.RequestInfo;
import zw.co.esolutions.ewallet.sms.ResponseCode;

public class PinChangeValidationResponse {
	private RequestInfo requestInfo;
	private ResponseCode responseCode;
	private String narrative;
	
	public PinChangeValidationResponse(RequestInfo requestInfo, ResponseCode responseCode, String narrative) {
		super();
		this.requestInfo = requestInfo;
		this.responseCode = responseCode;
		this.narrative = narrative;
	}

	/**
	 * @return the requestInfo
	 */
	public RequestInfo getRequestInfo() {
		return requestInfo;
	}

	/**
	 * @param requestInfo the requestInfo to set
	 */
	public void setRequestInfo(RequestInfo requestInfo) {
		this.requestInfo = requestInfo;
	}

	/**
	 * @return the responseCode
	 */
	public ResponseCode getResponseCode() {
		return responseCode;
	}

	/**
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(ResponseCode responseCode) {
		this.responseCode = responseCode;
	}

	/**
	 * @return the narrative
	 */
	public String getNarrative() {
		return narrative;
	}

	/**
	 * @param narrative the narrative to set
	 */
	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}

}
