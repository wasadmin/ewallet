package zw.co.esolutions.merchants.util;

public class PaymentResponse {
	
	private PaymentRequest paymentRequest;

	private String responseCode;

	private String narrative;

	public PaymentResponse() {
		super();
	}

	public PaymentResponse(PaymentRequest paymentRequest, String responseCode, String narrative) {
		super();
		this.paymentRequest = paymentRequest;
		this.responseCode = responseCode;
		this.narrative = narrative;
	}

	public PaymentRequest getPaymentRequest() {
		return paymentRequest;
	}

	public void setPaymentRequest(PaymentRequest paymentRequest) {
		this.paymentRequest = paymentRequest;
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
	
	@Override
	public String toString() {
		return "RESP INFO [" + this.getResponseCode() +" | " + this.getNarrative() + " | " + this.getPaymentRequest() +"]";
	}
}
