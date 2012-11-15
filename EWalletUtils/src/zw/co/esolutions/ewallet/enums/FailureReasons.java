package zw.co.esolutions.ewallet.enums;

public enum FailureReasons {
	INSUFFICIENT_FUNDS("Insufficient funds"),
	DOWN_SYSTEM("down"),
	AMOUNT_BELOW_MINIMUM_LIMIT("below minimum"),
	AMOUNT_ABOVE_MAXIMUM_LIMIT("above maximum"),
	FULL_ACCOUNT("full"),
	LOW_ACCOUNT_BALANCE("Account balance is low"),
	NO_TRANSACTION_CHARGE("No charge"),
	TIMEOUT("Timed");
	FailureReasons(String description) {
		
		this.description = description;
		
	}
	
	private String description;
	
	public  String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
}
