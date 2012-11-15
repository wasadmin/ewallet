/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.econet.ussdgateway.client;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 *
 * @author oswin
 */
public class DateUtil {

    public static String generateISO8601Date(Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.toString("YYYY-MM-dd'T'HH:mm:ss.SSSZ");
    }

    public static Date getDateFromISO8601(String date) {
        DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime();
        return dateTimeFormatter.parseDateTime(date).toDate();
    }

    public static Date parseXmlDate(String xmlDate) {
    	Date date = null;
        SimpleDateFormat ISO8601DATEFORMAT = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ssZ");
        xmlDate = xmlDate.replaceAll("\\+0([0-9]){1}\\:00", "+0$100");
        try {
            date = ISO8601DATEFORMAT.parse(xmlDate);
        } catch (ParseException e) {
        } 
        return date;
    }

    public static String formatXmlDate(Date date) {
        SimpleDateFormat ISO8601DATEFORMAT = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ssZ");
        return ISO8601DATEFORMAT.format(date);
    }
}
