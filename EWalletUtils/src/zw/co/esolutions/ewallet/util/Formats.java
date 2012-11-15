package zw.co.esolutions.ewallet.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class Formats {

	
	public static SimpleDateFormat merchantDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	public static SimpleDateFormat tautteeShortDateTimeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
	public static java.text.SimpleDateFormat rfc3339DateFormat = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat shortUploadDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
	public static SimpleDateFormat shortUploadDateFormatNumbersOnly = new SimpleDateFormat("dd-MM-yyyy");
	public static SimpleDateFormat yearMonthAndDayDateFormat = new SimpleDateFormat("yyyyMMdd");
	public static SimpleDateFormat shortDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	public static SimpleDateFormat billingDateFormat = new SimpleDateFormat("MM/yyyy");
	public static SimpleDateFormat longDateTimeFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy hh:mm aa");
	public static SimpleDateFormat shortDateTimeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
	public static SimpleDateFormat longDateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
	public static SimpleDateFormat fullMonthDateFormat = new SimpleDateFormat("dd MMMM yyyy");
	public static SimpleDateFormat yearOnlyDateFormat = new SimpleDateFormat("yyyy");
	public static SimpleDateFormat shortPlainDateFormat = new SimpleDateFormat("ddMMyyyy");
	public static SimpleDateFormat yearAndMonthDateFormat = new SimpleDateFormat("yyyy MMMM");
	public static SimpleDateFormat SFIDateFormat = new SimpleDateFormat("yyyyMMdd");
	public static SimpleDateFormat fileTimePrefix = new SimpleDateFormat("yyyyMMdd_HHmm");
	public static SimpleDateFormat short2DigitYearPlainDateFormat = new SimpleDateFormat("ddMMyy");
	public static SimpleDateFormat yearFormatTwoDigit = new SimpleDateFormat("yy");
	public static SimpleDateFormat equationDateFormat = new SimpleDateFormat("MMddHHmmss");

	public static DecimalFormat twoDigitIntFormat = new DecimalFormat("00");
	public static DecimalFormat threeDigitIntFormat = new DecimalFormat("000");
	public static DecimalFormat fourDigitIntFormat = new DecimalFormat("0000");
	public static DecimalFormat fiveDigitIntFormat = new DecimalFormat("00000");
	public static DecimalFormat sixDigitIntFormat = new DecimalFormat("000000");
	public static DecimalFormat sevenDigitIntFormat = new DecimalFormat("0000000");
	public static DecimalFormat eightDigitIntFormat = new DecimalFormat("00000000");
	public static DecimalFormat nineDigitIntFormat = new DecimalFormat("000000000");
	public static DecimalFormat tenDigitIntFormat = new DecimalFormat("0000000000");
	
	public static DecimalFormat intFormat = new DecimalFormat("0");
	public static DecimalFormat moneyFormat = new DecimalFormat("#,##0.00");
	public static DecimalFormat moneyFormatNoCommas = new DecimalFormat("0.00");
	public static DecimalFormat threeDecimalFormat = new DecimalFormat("0.000");
	public static DecimalFormat sixDecimalFormat = new DecimalFormat("0.000000");
	
	public static SimpleDateFormat dayMonthNoTimeFormat = new SimpleDateFormat("dd MMM");
	public static SimpleDateFormat equationReconDateFormat = new SimpleDateFormat("yyMMdd");
	
}
