/**
 * 
 */
package zw.co.esolutions.mobile.msgs;

/**
 * @author taurai
 *
 */
public class LoginInfo {
	private String parts;
	private String sourceMobile;
	private String passcodePrompt;
	
	public LoginInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public LoginInfo(String pin, String sourceMobile, String passcodePrompt) {
		super();
		this.parts = pin;
		this.sourceMobile = sourceMobile;
		this.passcodePrompt = passcodePrompt;
	}
	public String getParts() {
		return parts;
	}
	public void setParts(String parts) {
		this.parts = parts;
	}
	public String getSourceMobile() {
		return sourceMobile;
	}
	public void setSourceMobile(String sourceMobile) {
		this.sourceMobile = sourceMobile;
	}
	public String getPasscodePrompt() {
		return passcodePrompt;
	}
	public void setPasscodePrompt(String passcodePrompt) {
		this.passcodePrompt = passcodePrompt;
	}

}
