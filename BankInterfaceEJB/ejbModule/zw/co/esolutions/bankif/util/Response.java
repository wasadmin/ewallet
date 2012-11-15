/**
 * 
 */
package zw.co.esolutions.bankif.util;

import zw.co.esolutions.ewallet.sms.ResponseCode;

/**
 * @author blessing
 *
 */
public class Response {
	private ResponseCode responseCode;
	private String message;
	
	public Response(ResponseCode responseCode, String message) {
		super();
		this.responseCode = responseCode;
		this.message = message;
	}

	public Response(ResponseCode responseCode) {
		this(responseCode, responseCode.getDescription());
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
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
}
