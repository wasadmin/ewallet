/**
 * 
 */
package zw.co.esolutions.topup.ws.util;

/**
 * @author blessing
 * 
 */
public class ReversalResponse {
	protected ReversalRequest reversalRequest;
	protected double airtimeBalance;
	protected String responseCode;
	protected String narrative;
	protected double initialBalance;

	/**
	 * 
	 */
	public ReversalResponse() {
		// TODO Auto-generated constructor stub
	}

	public ReversalRequest getReversalRequest() {
		return reversalRequest;
	}

	public void setReversalRequest(ReversalRequest reversalRequest) {
		this.reversalRequest = reversalRequest;
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
