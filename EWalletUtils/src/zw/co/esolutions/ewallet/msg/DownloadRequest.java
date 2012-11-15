/**
 * 
 */
package zw.co.esolutions.ewallet.msg;

import java.io.Serializable;

/**
 * @author wasadmin
 *
 */
public class DownloadRequest implements Serializable {

	private String bankCode;
	private String command;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public DownloadRequest() {
		// TODO Auto-generated constructor stub
	}

	public String getBankCode() {
		
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

}
