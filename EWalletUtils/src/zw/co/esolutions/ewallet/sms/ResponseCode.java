package zw.co.esolutions.ewallet.sms;

public enum ResponseCode {
	
	
	E000("Success"),
	E400("Invalid pin"),
	E401("Mobile Profile not active"),
	E402("Customer not active"),
	E403("Mobile Profile locked"),
	
	E500("Duplicate Transaction Found."),
	E501("Reversal Failed. Original Transaction not found."),
	E502("Reversal Failed. Original Transaction was not successful."),
	E503("Undefined processing code for the transaction."),
	E504("Amount and/or source account could not match with original request."),
	E505("General Transaction Error."),
	E506("Unknown Transaction Type"),
	E507("Transaction Summary has no transactions"),
	
	E700("Your phone is not registered."),
	E701("Error posting non-holder transfer transactions. Transaction rolled back."),
	E702("Mobile profile is not active. Please contact your nearest branch."),
	E703("Wrong password parts supplied."),
	E704("Failed to generate transaction passcode."),
	E705("Timeout. You took too long to submit your password."),
	E706("Transaction state invalid."),
	E707("Your phone is currently locked. Please retry after a few hours."),
	E708("Transaction not found."),
	E709("Unsupported Operation."),
	E710("Bank Account is not active."),
	E711("Transaction already performed."),
	E712("Your account is not activated. Please contact your nearest branch."),
	E713("Customer already activated."),
	E714("Sorry. Customer cannot be activated. Status is invalid."),
	E715("Error. Insufficient parameters passed."),
	E716("Congratulations! You have successfully activated your profile. Enjoy using e-Wallet."),
	E717("This mobile is already registered on e-Wallet."),
	E718("You have not transacted at least once."),
	E719("Maximum number of referrals for this mobile reached."),
	E720("Requested Bank is not available on Mobile Commerce."),
	E721("Invalid source mobile number."),
	E722("No pending requests found for your mobile."),
	E723("Unknown request type."),
	E724("No primary account registered. Please contact your nearest branch."),
	E725("Invalid amount"),
	
	E777("An error has occured. Operation aborted."),
	E778("Agent must transfer to agent-registered bank account only."),
	E779("Agent Bank Account Not Registered."),
	
	E800("Insufficient funds to make a transfer."),
	E801("Insufficient funds to make a withdrawal transaction."),
	E802("Deposit amount is below minimum."),
	E803("Deposit amount will exceed maximum account balance."),
	E804("Withdrawal amount is below minimum."),
	E805("Withdrawal amount is above maximum."),
	E806("Balance in account is below minimum. Withdrawal cannot be performed."),
	E807("Transfer amount will be above maximum balance in the destination account."),
	E808("Insufficient funds."),
	E809("Balance in account is below minimum."),
	E810("Account balance is low. Transaction not performed."),
	E811("Deposit amount is above maximum deposit."),
	E812("Account is full, cannot accept any deposit."),
	E813("Account is full, cannot accept any transfer."),
	E814("Transfer amount is below minimum."),
	E815("Transfer amount is above maximum."),
	E816("Message not sent to bank."),
		
	E817("No charge is set for your transaction. Transaction stopped."),
	E818("Deposit failed."),
	E819("Pin Change Request Failed."),
	E820("System is down, no limits found. Transaction failed."),
	E821("System is down, no commissions found. Transaction failed."),
	E822("No commissions found for this transaction. Transaction failed."),
	E823("No limit found for this transaction. Transaction failed."),
	E824("Deposit amount is below minimum."),
	E825("Deposit amount is above maximum."),
	E826("No daily limit found for this transaction. Transaction failed."),
	E827("No monthly limit found for this transaction. Transaction failed."),
	E828("No yearly limit found for this transaction. Transaction failed."),
	E829("Transaction amount now above minimum daily limit. Transaction failed."),
	E830("Transaction timeout."),
	E831("Transaction not honoured."),
	E832("No accoount held"),
	E833("Currency not supported"),
	E834("Account Closed"),
	E835("Duplicate Transaction"),
	E836("EWallet Postings Failed. EWallet and EQ3 Transactions Rolled back."),
	E837("EWallet Postings Failed. Error occured during transaction rollback."),
	E838("Reversal requests posted to EQ3"),
	E839("This is a non-Econet number, cannot be auto-registered"),
	E840("Account already active"),
	E841("Withdrawal failed."),
	E842("Non holder withdrawal failed."),
	
	
	E900("Sorry, an unexpected error occurred."), 
	E901("Merchant is not available on e-Solutions Mobile Commerce"), 
	E902("merchant is not registered for Bank"),
	E903(""),
	
	E200("Destination mobile validation failed."),
	E201("Invalid source or destination accoount."),

	E600("No transactions found in reversal request."),
	E601("Failed to reverse main transaction."),
	E602("Failed to reverse transaction charge.");

	ResponseCode(String description) {
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
