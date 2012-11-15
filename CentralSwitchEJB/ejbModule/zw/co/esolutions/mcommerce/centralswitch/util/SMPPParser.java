package zw.co.esolutions.mcommerce.centralswitch.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class SMPPParser {
	public static String clean(String sms) {

		String pat = "[a-zA-Z[0-9][\\*][#][.]]";
		// String ray = "2*p$ay*e`co\n ne~t#5?0%0.0^0*0 (91@22)2+6!4/6{5-\n";
		Pattern pattern = Pattern.compile(pat);
		StringBuffer buffer = new StringBuffer();
		for (Character c : sms.toCharArray()) {
			Matcher matcher = pattern.matcher(c.toString());
			if (matcher.find()){
				buffer.append(Character.toUpperCase(c));
			}
		}
		String smsRequest = buffer.toString().replace(" ", "").replace(",", "").replace('#', '*'); 
		
		smsRequest = SMPPParser.stripMessageSigning(smsRequest);
		
		Logger.getLogger(SMPPParser.class).info("Message after stripping msg signing:     " + smsRequest);
		
//		Logger.getLogger(SMPPParser.class).info(smsRequest);
		
		return smsRequest;
	}
	
	public static void main(String[] args) {
//		Logger.getLogger(SMPPParser.class).debug(clean("2*p$ay*e`co\n ne~t#5?0%0.0^0*0 (91@22)2+6!4/6{5-\n"));
		System.out.println(clean("2*p$ay*e`co\n ne~t#5?0%0.0^0*0 (91@22)2+6!4/6{5-\n"));
		System.out.println(clean("2*p$ay*dstv*73*premium.hd*1#"));

	}
	
	static String stripMessageSigning(String smsRequest) {
		
		Logger.getLogger(SMPPParser.class).info("Message b4 stripping msg signing:     " + smsRequest);
		
		return smsRequest.replace("ECONETWIRELESS", "").trim();
		
	}
}
