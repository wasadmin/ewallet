package zw.co.esolutions.ewallet.process.util;

import java.io.Serializable;

import zw.co.esolutions.ewallet.sms.ResponseCode;

public class CustomerRegResponse implements Serializable {
	
	private final ResponseCode responseCode;
	private final String narrative;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2357051900244751545L;

	public CustomerRegResponse(ResponseCode responseCode, String narrative) {
		this.responseCode = responseCode;
		this.narrative = narrative;
	}

	/**
	 * @return the responseCode
	 */
	public ResponseCode getResponseCode() {
		return responseCode;
	}

	/**
	 * @return the narrative
	 */
	public String getNarrative() {
		return narrative;
	}

}
