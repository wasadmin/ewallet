/**
 * 
 */
package zw.co.esolutions.ewallet.util;

/**
 * @author taurai
 *
 */
public class KeyWordUtil {

	public static boolean isEwallet(String ewallet) {
	boolean value = false;
	loop : for(String str : keyArr) {
			if(str.equalsIgnoreCase(ewallet)) {
				value = true;
				break loop;
			}
		}
	return value;
	}
	
	private static String[] keyArr = {"ewallet", "wallet", "ewal", "wal", "e", "ew", "w", "wlt", "ewlt"};
	
	public static void main(String[] args) {
		System.out.println(">>>>>>>>>>>. Ewallet = "+isEwallet("w"));
	}
}
