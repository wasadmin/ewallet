package zw.co.esolutions.ewallet.util;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordUtil {
	
	  protected static Random r = new java.util.Random();

	  /*
	   * Set of characters that is valid. Must be printable, memorable, and "won't
	   * break HTML" (i.e., not ' <', '>', '&', '=', ...). or break shell commands
	   * (i.e., not ' <', '>', '$', '!', ...). I, L and O are good to leave out,
	   * as are numeric zero and one.
	   */
	  protected static char[] goodChar = { 'a', 'b', 'c', 'd', 'e', 'f', 'g',
	      'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
	      'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
	      'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
	      '2', '3', '4', '5', '6', '7', '8', '9', '+', '-', '@', };
	  
	  protected static char[] goodCode = { '0', '1','2', '3', '4', '5', '6', '7', '8', '9',};

	  /* Generate a Password object with a random password. */
	  public static String getPassword(int length) {
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < length; i++) {
	      sb.append(goodChar[r.nextInt(goodChar.length)]);
	    }
	    return sb.toString();
	  }
	  
	  public static String getCode(int length) {
		    StringBuffer sb = new StringBuffer();
		    for (int i = 0; i < length; i++) {
		      sb.append(goodCode[r.nextInt(goodCode.length)]);
		    }
		    return sb.toString();
		  }
	  
	  public static boolean validatePassword(final String password){
		  if (password==null || password.length()<8) {
	            return false;
	        }
	        // make sure it has at least 1 word char {a-z,A-Z}
	        if (!Pattern.matches(".*[a-zA-Z]+.*", password)) {
	            return false;
	        }
	        // make sure it has at least 1 digit {0-9}
	        if (!Pattern.matches(".*[0-9]+.*", password)) {
	            return false;
	        }
	        // make sure it has at least 1 special character
	        if (!Pattern.matches(".*[!@#$%]+.*", password)) {
	            return false;
	        }
	       
	        return true;
	  }

	  public static void main(String[] argv) {
	    for (int i = 0; i < 20; i++) {
	      System.out.println(PasswordUtil.getCode(6));
	    }
	  }
}
