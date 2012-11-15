package zw.co.esolutions.ewallet.sms;

import java.util.HashMap;
import java.util.Map;

public class SMSFactory {
	
	private static Map<String, String> instance;
	
	static {
		instance = new HashMap<String, String>();
	}
	
	public static void setInstance(Map<String, String> instance) {
		SMSFactory.instance = instance;
	}

	public static Map<String, String> getInstance() {
		instance.put("000", "SUCCESS");
		instance.put("700", "Mobile profile does not exist");
		instance.put("701", "Error posting non-holder transfer transactions. Transaction rolled back.");
		instance.put("702", "Mobile profile is not active.");
		instance.put("703", "Passcode is not valid.");
		instance.put("704", "Failed to generate transaction passcode.");
		instance.put("705", "Transaction has timed out.");
		instance.put("706", "Transaction state invalid.");
		instance.put("707", "Passcode entered 3 times incorrectly. Mobile Profile locked.");
		instance.put("708", "Transaction not found.");
		instance.put("709", "Unsupported Operation.");
		instance.put("710", "Bank Account is not active.");
		instance.put("711", "Transaction already performed.");
		instance.put("712", "This customer is not active. Please activate your account.");
		instance.put("713", "Customer already activated.");
		instance.put("714", "Sorry. Customer cannot be activated. Status is invalid.");
		
		instance.put("777", "An error has occured. Transaction not completed.");
		
		instance.put("800", "Isufficient funds to make a transfer.");
		instance.put("801", "Insufficient funds to make a withdrawal transaction.");
		instance.put("802", "Deposit amount is below minimum.");
		instance.put("803", "Deposit amount will exceed maximum account balance.");
		instance.put("804", "Withdrawal amount is below minimum.");
		instance.put("805", "Withdrawal amount is above maximum.");
		instance.put("806", "Withdrawal transaction will lead to an account balance below minimum balance.");
		instance.put("807", "Transfer amount will execeed maximum balance in the destination account.");
		instance.put("808", "No money in the account.");
		instance.put("809", "Balance in account is below minimum.");
		instance.put("810", "Balance in account is low and cannot allow withdrwal of this amount.");
		instance.put("811", "Deposit amount exceeding maximum deposit.");
		instance.put("812", "Account is full, cannot accept any deposit.");
		instance.put("813", "Account is full, cannot accept any transfer.");
		instance.put("813", "Transfer amount is below mimimum.");
		instance.put("814", "Transfer amount is above maximum.");
		instance.put("8", "");
		instance.put("8", "");
		instance.put("8", "");
		instance.put("8", "");
		instance.put("8", "");
		instance.put("8", "");
		instance.put("8", "");
		instance.put("8", "");
		instance.put("8", "");
		instance.put("8", "");
		instance.put("8", "");
		instance.put("8", "");
		instance.put("8", "");
		instance.put("8", "");
		instance.put("8", "");
		instance.put("8", "");
		instance.put("900", "Error Occured.");
		return instance;
	}
	
	public static void main(String... args) {
		System.out.println(SMSFactory.getInstance().get("000"));
	}
}
