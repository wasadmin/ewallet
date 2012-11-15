/**
 * 
 */
package zw.co.esolutions.mobile.transactions.utils;

/**
 * @author taurai
 *
 */
public class FormatParts {
	
	public static String formatPasswordPart(int number) {
		if (number == 1) {
			return "1st";
		} else if (number == 2) {
			return "2nd";
		} else if (number == 3) {
			return "3rd";
		} else if (number == 5) {
			return "last";
		}
		return number + "th";
	}

}
