/**
 * 
 */
package zw.co.esolutions.merchants.util;

import javax.ejb.ApplicationException;

/**
 * @author wasadmin
 *
 */
@ApplicationException(inherited = false, rollback = false)
public class MerchantException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7660213029341865213L;

	/**
	 * 
	 */
	public MerchantException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public MerchantException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public MerchantException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MerchantException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
