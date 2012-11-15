package zw.co.esolutions.ewallet.util;

import java.text.DecimalFormat;

public class MoneyUtil {

	public static long convertToCents(double dollars) {
	//	dollars = Math.round((dollars * Math.pow(10, (double) 2) +1)/2) / Math.pow(10, (double) 2);
		
		//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>doubleValue>>>>>>>>>>>>>>"+doubleValue);
		
		long value = (long)(dollars*200 +1)/2;
		return value;
	}
	
	public static double convertToDollars(long cents) {
		double dollars = (double)(cents);
		return dollars/100;
	}
	
	public static String convertDollarsToPattern(double dollars) {
		DecimalFormat dFormat = Formats.moneyFormat;
		String formattedString = dFormat.format(dollars);
		return formattedString;
	}
	
	public static String convertCentsToDollarsPattern(long cents) {
		double dollars = (double)(cents);
		DecimalFormat dFormat = Formats.moneyFormat;
		
		String formattedString = dFormat.format(dollars/100);
		return "USD" + formattedString;
	}

	public static String convertCentsToDollarsPatternNoCurrency(double amount) {
		DecimalFormat dFormat = Formats.moneyFormat;
		String formattedString = dFormat.format(amount);
		return formattedString;
	}
	
	public static String convertCentsToDollarsPatternNoCurrency(long cents) {
		double dollars = (double)(cents);
		return Formats.moneyFormat.format(dollars/100);
	}

	
	public static void main(String ... args){
		double cents=0.00;
		
		long dollars=convertToCents(cents);
//		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>cents>>"+cents);
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>dollars>>"+dollars);
		
	}
	
}
