/**
 * 
 */
package zw.co.esolutions.topup.ws.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * @author blessing
 *
 */
public class Formats {

	/**
	 * 
	 */
	public Formats() {
		// TODO Auto-generated constructor stub
	}
	
    public static SimpleDateFormat rfc3339DateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat shortDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    public static SimpleDateFormat longDateTimeFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss");
    public static SimpleDateFormat shortDateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    public static SimpleDateFormat longDateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
    public static SimpleDateFormat fullMonthDateFormat = new SimpleDateFormat("dd MMMM yyyy");
    public static SimpleDateFormat yearOnlyDateFormat = new SimpleDateFormat("yyyy");
    public static SimpleDateFormat shortPlainDateFormat = new SimpleDateFormat("ddMMyyyy");
    public static SimpleDateFormat yearAndMonthDateFormat = new SimpleDateFormat("yyyy MMMM");
    public static SimpleDateFormat yearMonthAndDayDateFormat = new SimpleDateFormat("yyyyMMdd");
    public static SimpleDateFormat yearAndShortMonthDateFormat = new SimpleDateFormat("yyyyMM");
    public static SimpleDateFormat ifxDateFormat = new SimpleDateFormat("yyyy/MM/dd");
    public static SimpleDateFormat yyMMddHHmmssSSSDateFormat = new SimpleDateFormat("yyMMddHHmmssSSS");

    public static DecimalFormat twoDigitIntFormat = new DecimalFormat("00");
    public static DecimalFormat threeDigitIntFormat = new DecimalFormat("000");
    public static DecimalFormat fourDigitIntFormat = new DecimalFormat("0000");
    public static DecimalFormat fiveDigitIntFormat = new DecimalFormat("00000");
    public static DecimalFormat sixDigitIntFormat = new DecimalFormat("000000");
    public static DecimalFormat sevenDigitIntFormat = new DecimalFormat("0000000");
    public static DecimalFormat eightDigitIntFormat = new DecimalFormat("00000000");
    public static DecimalFormat nineDigitIntFormat = new DecimalFormat("000000000");
    public static DecimalFormat elevenDigitIntFormat = new DecimalFormat("00000000000");

    public static DecimalFormat intFormat = new DecimalFormat("0");
    public static DecimalFormat moneyFormat = new DecimalFormat("#,##0.00");
    public static DecimalFormat moneyFormatNoCommas = new DecimalFormat("0.00");
    public static DecimalFormat threeDecimalFormat = new DecimalFormat("0.000");
    public static DecimalFormat sixDecimalFormat = new DecimalFormat("0.000000");

}
