package zw.co.esolutions.mcommerce.gatewayservices.util;

public class SMSMessage {

	private String originator;
	private String destination;
	private String messageText;
	private String originatorService;
	private String username;
	
	public String getOriginator() {
		return originator;
	}
	public void setOriginator(String originator) {
		this.originator = originator;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getMessageText() {
		return messageText;
	}
	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}
	public String getOriginatorService() {
		return originatorService;
	}
	public void setOriginatorService(String originatorService) {
		this.originatorService = originatorService;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
}
