/**
 * 
 */
package zw.co.esolutions.topup.ws.util;

import java.io.Serializable;

/**
 * @author wasadmin
 *
 */
public class WSResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private WSRequest request;
	private double airtimeBalance;
	private String responseCode;
	private String narrative;
	private double initialBalance;
	/**
	 * 
	 */
	public WSResponse() {
		// TODO Auto-generated constructor stub
	}
	public WSRequest getRequest() {
		return request;
	}
	public void setRequest(WSRequest request) {
		this.request = request;
	}
	public double getAirtimeBalance() {
		return airtimeBalance;
	}
	public void setAirtimeBalance(double airtimeBalance) {
		this.airtimeBalance = airtimeBalance;
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
	public double getInitialBalance() {
		return initialBalance;
	}
	public void setInitialBalance(double initialBalance) {
		this.initialBalance = initialBalance;
	}

}
