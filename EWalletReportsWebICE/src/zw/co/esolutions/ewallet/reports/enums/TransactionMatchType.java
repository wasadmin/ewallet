package zw.co.esolutions.ewallet.reports.enums;

public enum TransactionMatchType {
	
	EQUATION_ONLY("EQUATION ONLY : Transactions found in Equation CSV file only, but not in E-WALLET"), 
	EWALLET_ONLY("E-WALLET ONLY : Transactions found in E-Wallet only, but not in Equation CSV file"), 
	MISMATCH_AMOUNTS("DIFFERENT AMOUNTS : Transactions found in Both Equation CSV file  and E-WALLET"), 
	FAILED_IN_EWALLET("FAILED EWALLET POSTINGS : Transactions with postings in Equation but failed in E-WALLET"), 
	BANK_REQUEST("BANK REQUEST : Transactions with postings in Equation but E-WALLET status is BANK REQUEST"), 
	CREDIT_REQUEST("CREDIT REQUEST : Transactions posted in both Equation and E-Wallet, but E-WALLET status is CREDIT REQUEST"),
	MANUAL_RESOLVE("MANUAL RESOLVE : Transactions posted in Equation but requires MANUAL RESOLVE"),
	TIMEOUT("TIMEOUT : Transactions posted in Equation but timed out in E-WALLET"),
	OTHER("OTHER : Transactions with postings in both Equation and E-Wallet but of other stati"),
	PERFECT_MATCH("COMPLETED : Transactions found in Both Equation and E-Wallet");
	
	TransactionMatchType(String description) {
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
