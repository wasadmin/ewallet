package zw.co.esolutions.ussd.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	
	
	public static Date parseXmlDate(String xmlDate){
		Date date= null;
		SimpleDateFormat ISO8601DATEFORMAT = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ssZ");
		xmlDate = xmlDate.replaceAll("\\+0([0-9]){1}\\:00", "+0$100");
		try {
			date = ISO8601DATEFORMAT.parse(xmlDate);
		} catch (ParseException e) {
			
		}
		return date;
	}
	
	
	public static String formatXmlDate(Date date){
		SimpleDateFormat ISO8601DATEFORMAT = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ssZ");
		return ISO8601DATEFORMAT.format(date);
	}

}
