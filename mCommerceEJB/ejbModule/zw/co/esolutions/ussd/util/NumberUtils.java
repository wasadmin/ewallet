package zw.co.esolutions.ussd.util;

import java.text.DecimalFormat;

public class NumberUtils {
	
	public static DecimalFormat moneyFormat = new DecimalFormat("#,##0.00");
	
	public static boolean validateAmount(String amountStr){
		double amount = 0;
		try{
			amount = Double.parseDouble(amountStr);
		}catch (NumberFormatException e) {
			return false;
		}
		if(amount <= 0){
			return false;
		}
		return true;
	}
	
	public static boolean validateTxtAmount(String amountStr){
		int number = 0;
		int fraction = 0;
		try{
			String[] tokens = amountStr.split("\\.");
			if(tokens.length == 1) {
				number = Integer.parseInt(tokens[0]);
			} else {
				number = Integer.parseInt(tokens[0]);
				fraction = Integer.parseInt(tokens[1]);
				if(fraction > 1) {
					return false;
				}
			}
			
		}catch (NumberFormatException e) {
			return false;
		}
		if(number <= 0){
			return false;
		}
		return true;
	}
	
	public static boolean validatePin(String pinStr){
		long pin = 0;
		try{
			pin = Long.parseLong(pinStr);
		}catch (NumberFormatException e) {
			return false;
		}
		if(pin <= 0){
			return false;
		}
		return true;
	}
	
	public static boolean validateAccount(String accStr){
		long acc = 0;
		try{
			acc = Long.parseLong(accStr);
		}catch (NumberFormatException e) {
			return false;
		}
		if(acc <= 0){
			return false;
		}
		return true;
	}
	
	public static long getAmountInCents(String amountStr){
		
		double amount = 0;
		
		try{
			amount = Double.parseDouble(amountStr);
		}catch (NumberFormatException e) {
		
		}
		
		return (long)(amount *100);
	}
	
	public static String getFormattedAmount(String amountStr){

		double amount = 0;
		try{
			amount = Double.parseDouble(amountStr);
		}catch (NumberFormatException e) {
		
		}
		return moneyFormat.format(amount);
		
	}
	
	public static void main(String ...strings ){
		System.out.println("************ format " +  validateTxtAmount("10"));
	}

}
