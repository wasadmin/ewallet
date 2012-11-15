package zw.co.esolutions.mcommerce.centralswitch.util;

import zw.co.esolutions.ewallet.sms.ResponseCode;

public class AuthenticationResponse {
	private ResponseCode response;
	private String narrative;

	public ResponseCode getResponse() {
		return response;
	}

	public void setResponse(ResponseCode response) {
		this.response = response;
	}

	public String getNarrative() {
		return narrative;
	}

	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}

	public AuthenticationResponse(ResponseCode response, String narrative) {
		super();
		this.response = response;
		this.narrative = narrative;
	}

	public AuthenticationResponse(ResponseCode responseCode) {
		this(responseCode, responseCode.getDescription());
	}

}
