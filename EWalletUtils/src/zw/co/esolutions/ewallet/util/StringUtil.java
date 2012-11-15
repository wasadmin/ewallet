package zw.co.esolutions.ewallet.util;

public class StringUtil {
	public static String formatNationalId(String idNumber){
	
			idNumber = idNumber.toUpperCase() ;
			StringBuffer temp = new StringBuffer();
			for(int i =0;i<idNumber.length();i++){
				Character c =idNumber.charAt(i);
				if(Character.isDigit(c) || Character.isLetter(c)){
					if(Character.isLetter(c)){
						temp.append( c );  
					}else{
						temp.append(c);
					}
				}
			}
			return temp.toString();
		
	}
	
	
	
	
	public static void main(String ...args){
		System.out.println("****************************************** " + formatNationalId("32-155722W-42"));
		//System.out.println("****************************************** " + formatMobileNumberWithZero("0912107231"));
	
	}
	
	
}
