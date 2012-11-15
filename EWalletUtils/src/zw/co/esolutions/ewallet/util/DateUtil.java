package zw.co.esolutions.ewallet.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;


public class DateUtil {
	
	// Date formats.                                    // physical <--> logical
    public static final int FORMAT_6        = 6;        // HHMMSS <--> Date
    public static final int FORMAT_7        = 7;        // CYYMMDD <--> Date
    public static final int FORMAT_13       = 13;       // CYYMMDDHHMMSS <--> Date
    public static final int FORMAT_DTS      = 99;       // System *DTS (date timestamp) format <--> Date



    // Private data.
    static final Date NO_DATE = new Date(0);

    private static final String HHMMSS_ZEROS    = "000000";
    private static final String CYYMMDD_ZEROS   = "0000000";

    private static int format_                         = -1;

	public static Timestamp getNextTimeout(Timestamp currentTimeout, int interval){
		return new Timestamp(DateUtil.addHours(currentTimeout, interval).getTime());
	}
	
	public static XMLGregorianCalendar convertToXMLGregorianCalendar(Date date) {
		try {
			if(date == null) {
				return null;
			}
			
			XMLGregorianCalendar xmlgc = org.apache.xerces.jaxp.datatype.DatatypeFactoryImpl.newInstance()
					.newXMLGregorianCalendar();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			xmlgc.setYear(cal.get(Calendar.YEAR));
			xmlgc.setMonth(cal.get(Calendar.MONTH) + 1);
			xmlgc.setDay(cal.get(Calendar.DAY_OF_MONTH));
			xmlgc.setHour(cal.get(Calendar.HOUR_OF_DAY));
			xmlgc.setMinute(cal.get(Calendar.MINUTE));
			xmlgc.setSecond(cal.get(Calendar.SECOND));
			return xmlgc;

		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;
	}
	
	public static XMLGregorianCalendar convertToXMLGregorianCalendar(Calendar  cal) {
		try {
			if(cal == null) {
				return null;
			}
			XMLGregorianCalendar xmlgc = org.apache.xerces.jaxp.datatype.DatatypeFactoryImpl.newInstance()
					.newXMLGregorianCalendar();
			xmlgc.setYear(cal.get(Calendar.YEAR));
			xmlgc.setMonth(cal.get(Calendar.MONTH) + 1);
			xmlgc.setDay(cal.get(Calendar.DAY_OF_MONTH));
			xmlgc.setHour(cal.get(Calendar.HOUR_OF_DAY));
			xmlgc.setMinute(cal.get(Calendar.MINUTE));
			xmlgc.setSecond(cal.get(Calendar.SECOND));
			return xmlgc;

		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;
	}

	public static Date convertToDate(XMLGregorianCalendar xmlGc) {
		if (xmlGc != null) {
			Calendar cal = Calendar.getInstance();
			cal.set(xmlGc.getYear(), xmlGc.getMonth()-1, xmlGc.getDay(), xmlGc.getHour(), xmlGc.getMinute(), xmlGc.getSecond());
			return cal.getTime();
		}
		return null;

	}
	
	public static Date convertToTime(Date date, Date time) {
		Calendar calendar = Calendar.getInstance();
		Calendar calendarTime = Calendar.getInstance();
		calendar.setTime(getBeginningOfDay(date));
		calendarTime.setTime(time);
		calendar.set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE));
		return calendar.getTime();
	}
	
	public static Date dayBefore(Date date){
		if(date == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		return cal.getTime();
	}
	
	public static String convertDateToString(Date date) {
		SimpleDateFormat df = Formats.shortDateFormat;
		return df.format(date);
	}
	
	public static Date convertFromStringToDate(String date) {
		SimpleDateFormat sdf = Formats.shortPlainDateFormat;
	    try {
			return sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String convertDateToLongString(Date date) {
		SimpleDateFormat df = Formats.longDateTimeFormat;
		return df.format(date);
	}
	
	public static Date convertFromLongStringToDate(String date) {
		SimpleDateFormat sdf = Formats.longDateTimeFormat;
	    try {
			return sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String convertTimeToString(Date date) {
		StringBuffer bf = new StringBuffer();
		DateFormat df = DateFormat.getInstance();
		String str = df.format(date);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if(str.contains("PM"))  {
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			if(hour == 24) {
				hour = 23;
			}
			bf.append(hour);
			bf.append(cal.get(Calendar.MINUTE));
			if(bf.length() == 3) {
				bf.insert(0, 2);
			}
		} else if (str.contains("AM")){
			bf.append(cal.get(Calendar.HOUR_OF_DAY));
			if(bf.length() == 1) {
				bf.insert(0, 0);
			}
			bf.append(cal.get(Calendar.MINUTE));
			if(bf.length() == 3) {
				bf.insert(0, 2);
			}
		}
		
		return bf.toString();
	}
	
	public static Date convertStringDateToDate(String txt) {
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
		try {
			return df.parse(txt);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public static String convertToDateWithTime(Date date) {
		SimpleDateFormat df = Formats.tautteeShortDateTimeFormat;
		String value = df.format(date);
		if(value.endsWith("12:00 AM")){
			value = value.replace("12:00 AM", "00:00");
		}
		return value;
	}
	
	public static String convertToShortUploadDateFormatNumbersOnly(Date date) {
		SimpleDateFormat df = Formats.shortUploadDateFormatNumbersOnly;
		return df.format(date);
	}
	
	
	public static String excludeTimeOnDate(Date date) {
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
		return df.format(date);
		
	}
	
	public static Date getBeginningOfDay(Date date){
		if(date == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND,0);
		return cal.getTime();
	}
	
	public static Date getEndOfDay(Date date){
		if(date == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY,23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal.getTime();
	}
	
	
	public static Date getBusinessDayBeginningOfDay(Date date){
		if(date == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		SystemConstants.configParams.getProperty(SystemConstants.BUSINESS_DAY_START_HOUR);
		cal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(SystemConstants.configParams.getProperty(SystemConstants.BUSINESS_DAY_START_HOUR)));
		cal.set(Calendar.MINUTE,Integer.parseInt(SystemConstants.configParams.getProperty(SystemConstants.BUSINESS_DAY_START_MINUTE)));
		cal.set(Calendar.SECOND, Integer.parseInt(SystemConstants.configParams.getProperty(SystemConstants.BUSINESS_DAY_START_SECOND)));
		cal.set(Calendar.MILLISECOND,Integer.parseInt(SystemConstants.configParams.getProperty(SystemConstants.BUSINESS_DAY_START_MILLISECOND)));
		return cal.getTime();
	}
	
	public static Date getBusinessDayEndOfDay(Date date){
		if(date == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(SystemConstants.configParams.getProperty(SystemConstants.BUSINESS_DAY_END_HOUR)));
		cal.set(Calendar.MINUTE, Integer.parseInt(SystemConstants.configParams.getProperty(SystemConstants.BUSINESS_DAY_END_MINUTE)));
		cal.set(Calendar.SECOND, Integer.parseInt(SystemConstants.configParams.getProperty(SystemConstants.BUSINESS_DAY_END_SECOND)));
		cal.set(Calendar.MILLISECOND, Integer.parseInt(SystemConstants.configParams.getProperty(SystemConstants.BUSINESS_DAY_END_MILLISECOND)));
		return cal.getTime();
	}
	
	public static Calendar convertToCalendar(Date date){
		if(date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	public static long daysBetween(Date day1, Date day2) {
		Date smaller,bigger;
		if(day1 != null && day2 != null){
			if(day1.before(day2)){
				smaller = day1;
				bigger = day2;
			}else{
				bigger = day1;
				smaller = day2;
			}
			double milliSecondsInOneDay = 1000 * 60 * 60 * 24;
			double daysBetweenAsDouble = (bigger.getTime() -smaller.getTime()) / milliSecondsInOneDay;
			return Math.round(daysBetweenAsDouble);
		}
		return 0;
	}

	public static boolean isDayBeforeOrEqual(Date day1, Date day2) {
		Calendar cal = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal.setTime(day1);
		cal2.setTime(day2);
		if(daysBetween(day1, day2) == 0L) {
			return true;
		}
		return cal.before(cal2);
	}
	
	public static boolean isDayBefore(Date day1, Date day2) {
		Calendar cal = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal.setTime(day1);
		cal2.setTime(day2);
		return cal.before(cal2);
	}
	
	public static Date addHours(Date date, int hours) {
		if (date != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.add(Calendar.HOUR_OF_DAY, hours);
			return c.getTime();
		}
		return date;
	}
	
	public static Date convertDateToTimestamp(Date date) {
		Timestamp tmp = null;
		try {
			tmp = new Timestamp (date.getTime());
			return tmp;
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return null;
	}
	
	public static Date convertTimestampToDate(Timestamp tmp) {
		Date date = null;
		try {
			date = new Date(tmp.getTime());
			return date;
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return null;
	}
	
	public static String getLastDayOfNextMonth(Date date) {
		Calendar calendar =  Calendar.getInstance();
		calendar.setTime(date);                              
		calendar.add(Calendar.MONTH, 1);
		return getLastDayOfMonth(calendar.getTime());
	}
	
	public static String getLastDayOfMonth(Date date) {
		Calendar calendar =  Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		return getLastDayOfMonth(month);
		
	}
	
	public static Date getDate(String dayOfMonth, String hour, String min) {
		Calendar calendar =  Calendar.getInstance();
		calendar.setTime(new Date());
		int hr,mn,day,month,year;
		hr = Integer.parseInt(hour);
		mn= Integer.parseInt(min);
		day =Integer.parseInt(dayOfMonth);
		month = calendar.get(Calendar.MONTH);
		year = calendar.get(Calendar.YEAR);
		Calendar tmpCalendar =  Calendar.getInstance();
		tmpCalendar.set(year, month, day, hr, mn, 0);
		return tmpCalendar.getTime();
	}
	
	public static Date addDays(Date date,int days){
		if(date==null){
			date = new Date();
		}
		Calendar calendar =  Calendar.getInstance();
		calendar.setTime(getBeginningOfDay(date));
		calendar.add(Calendar.DAY_OF_MONTH,days);
		return calendar.getTime();
	}
	
	
	public static Date add(Date date, int field, int amount) {
		Calendar cal = convertToCalendar(date);
		cal.add(field, amount);
		return cal.getTime();
	}

	public static Date subtract(Date date, int field, int amount) {
		Calendar cal = convertToCalendar(date);
		
		return cal.getTime();
	}
	/*public static void main(String ...agrs){
		System.out.println("******************************* " + getReferenceYear());
	}	}*/
	public static String getLastDayOfMonth(int month) {
		Calendar calendar =  Calendar.getInstance();
		switch (month) {
		case Calendar.JANUARY: 
		case Calendar.MARCH:
		case Calendar.MAY:
		case Calendar.JULY:
		case Calendar.AUGUST:
		case Calendar.OCTOBER:
		case Calendar.DECEMBER: 
					return "31"; 
		case Calendar.FEBRUARY:
					int year = calendar.get(Calendar.YEAR);
					if((year % 4 == 0 && year % 100 != 0)||(year % 400 == 0)){  // Check for leap year
						return "29";
					}else{
						return "28";
					}

		default: return "30";
		}
//		calendar.setTime(new Date(System.currentTimeMillis()));
//		return new Integer(calendar.get(Calendar.DAY_OF_MONTH)).toString();
	
	}
	
	
	public static String getReferenceYear() {
		SimpleDateFormat sf = new SimpleDateFormat("yy");
		return sf.format(new Date());
	}
	
	public static String convertDateToSimpleDayWithNoTime(Date date) {
		SimpleDateFormat df = Formats.dayMonthNoTimeFormat;
		return df.format(date);
	}
	
	public static boolean isEqualDays(Date date1, Date date2) {
		long value = 0;
		try {
			value = daysBetween(date1, date2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(value == 0) {
			return true;
		} else {
			return false;
			
		}
	}
	
	public static boolean isEndDate(Date date) {
		long value = 0;
		try {
			value = daysBetween(date, DateUtil.getEndOfDay(date));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(value == 0) {
			return true;
		} else {
			return false;
			
		}
	}

	public static Date convertToDateWithNoTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();
	}
	
	public static Date convertEqInterfaceDateToDate(String equationDate) {
		SimpleDateFormat df = Formats.equationDateFormat;
		try {
			Date eqDate = df.parse(equationDate);
			Date currentDate = new Date();
			Calendar eqCalendar = Calendar.getInstance();
			eqCalendar.setTime(eqDate);
			Calendar currentCalendar = Calendar.getInstance();
			currentCalendar.setTime(currentDate);
			if(currentCalendar.get(Calendar.MONTH) >= eqCalendar.get(Calendar.MONTH)) {
				eqCalendar.set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
			} else {
				eqCalendar.set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR) - 1);
			}
			eqDate = eqCalendar.getTime();
			return eqDate;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public static Date convertEQDateToDate(String equationDate) {
		// Validate the physical value.
        if (equationDate == null) {
        	throw new NullPointerException("equationDate");
        }
        
        format_ = equationDate.length();

        switch(format_) {

        case FORMAT_13:
            return string13ToDate(equationDate);

        case FORMAT_7:
            return string13ToDate(equationDate + HHMMSS_ZEROS);

        case FORMAT_6:
            return string13ToDate(CYYMMDD_ZEROS + equationDate);

        case FORMAT_DTS: return null;
        
        }
        return null;
        
	}
	
	public static String convertDateToEquationDateString(Date equationDate) {
		return dateToString13(equationDate);
	}
	
	/**
    Converts a Date to a String in the format CYYMMDDHHMMSS.

    @param date         The Date.
    @return             The String.
    **/
   private static String dateToString13(Date date)
   {
       Calendar calendar = Calendar.getInstance();
       calendar.setTime(date);
       StringBuffer buffer13 = new StringBuffer(13);
       int year13 = calendar.get(Calendar.YEAR);
       buffer13.append((year13 < 2000) ? '0' : '1');
       buffer13.append(twoDigits(year13 % 100));
       buffer13.append(twoDigits(calendar.get(Calendar.MONTH) + 1));
       buffer13.append(twoDigits(calendar.get(Calendar.DAY_OF_MONTH)));
      // buffer13.append(twoDigits(calendar.get(Calendar.HOUR_OF_DAY)));
      // buffer13.append(twoDigits(calendar.get(Calendar.MINUTE)));
      // buffer13.append(twoDigits(calendar.get(Calendar.SECOND)));
       return buffer13.toString();
   }


   /**
    Converts a String in the format CYYMMDDHHMMSS to a Date

    @param string13     The String.
    @return             The Date.
    **/
   private static Date string13ToDate(String string13)
   {
       int length = string13.length();

       Calendar calendar = Calendar.getInstance();

       // If the date is all blanks or if it is a special
       // value, then return a date with all zeros.
       if (length == 0)
           return NO_DATE;
       else if (string13.charAt(0) == '*')
           return NO_DATE;

       int century = Integer.parseInt(string13.substring(0,1));
       int year    = Integer.parseInt(string13.substring(1,3));
       int month   = Integer.parseInt(string13.substring(3,5));
       int day     = Integer.parseInt(string13.substring(5,7));
       int hour    = Integer.parseInt(string13.substring(7,9));
       int minute  = Integer.parseInt(string13.substring(9,11));
       int second  = Integer.parseInt(string13.substring(11,13));

       calendar.set(Calendar.YEAR, year + ((century == 0) ? 1900 : 2000));
       calendar.set(Calendar.MONTH, month - 1);
       calendar.set(Calendar.DAY_OF_MONTH, day);
       calendar.set(Calendar.HOUR_OF_DAY, hour);
       calendar.set(Calendar.MINUTE, minute);
       calendar.set(Calendar.SECOND, second);
       calendar.set(Calendar.MILLISECOND, 0); // @A1A
       return calendar.getTime();
   }



   /**
    Returns a 2 digit String representation of the value.
    The value will be 0-padded on the left if needed.

    @param value    The value.
    @return         The 2 digit String representation.
    **/
   private static String twoDigits(int value)
   {
       if (value > 99)
           return null;

       String full = "00" + Integer.toString(value);
       return full.substring(full.length() - 2);
   }

	
	public static void main(String[] tau) {
		Date date = new Date(System.currentTimeMillis());
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		System.out.println(">>>>>>>>>>> Date = "+DateUtil.convertDateToEquationDateString(new Date()));
		//System.out.println(">>>>>>>>> Day of Month = "+DateUtil.getLastDayOfMonth(cal.get(Calendar.MONTH))+" Month = "+cal.get(Calendar.MONTH) +" January = "+Calendar.JANUARY);
		
	}
}
