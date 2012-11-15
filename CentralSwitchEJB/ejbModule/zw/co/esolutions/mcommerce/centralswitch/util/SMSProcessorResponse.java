package zw.co.esolutions.mcommerce.centralswitch.util;

import zw.co.esolutions.ewallet.msg.RequestInfo;

public class SMSProcessorResponse {
	private RequestInfo requestInfo;
	private boolean sendToBank;
	private boolean returnToSender;
	private boolean submitToMerchant;
	private String narrative;
	private String destinationQueueName;

	public SMSProcessorResponse(RequestInfo requestInfo, boolean sendToBank, boolean returnToSender, boolean submitToMerchant, String narrative, String destinationQueueName) {
		super();
		this.requestInfo = requestInfo;
		this.sendToBank = sendToBank;
		this.returnToSender = returnToSender;
		this.submitToMerchant = submitToMerchant;
		this.narrative = narrative;
		this.destinationQueueName = destinationQueueName;
	}

	public RequestInfo getRequestInfo() {
		return requestInfo;
	}

	public void setRequestInfo(RequestInfo requestInfo) {
		this.requestInfo = requestInfo;
	}

	public boolean isSendToBank() {
		return sendToBank;
	}

	public void setSendToBank(boolean sendToBank) {
		this.sendToBank = sendToBank;
	}

	public boolean isReturnToSender() {
		return returnToSender;
	}

	public void setReturnToSender(boolean returnToSender) {
		this.returnToSender = returnToSender;
	}

	public String getNarrative() {
		return narrative;
	}

	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}

	public boolean isSubmitToMerchant() {
		return submitToMerchant;
	}

	public void setSubmitToMerchant(boolean submitToMerchant) {
		this.submitToMerchant = submitToMerchant;
	}

	public String getDestinationQueueName() {
		return destinationQueueName;
	}

	public void setDestinationQueueName(String destinationQueueName) {
		if(destinationQueueName != null){
			this.destinationQueueName = destinationQueueName.toUpperCase();
		}else{
			this.destinationQueueName = "";
		}
	}

}
