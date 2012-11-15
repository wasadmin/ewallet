package zw.co.esolutions.ewallet.msg;

import java.io.Serializable;

public class SynchronisationResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String responseCode;
	private String narrative;

	public SynchronisationResponse() {
		// TODO Auto-generated constructor stub
	}
	
	public SynchronisationResponse(String responseCode, String narrative) {
		super();
		this.responseCode = responseCode;
		this.narrative = narrative;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getNarrative() {
		return narrative;
	}

	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}

}
