/**
 * 
 */
package zw.esolutions.esolutins.ewallet.smsserver.web;

import javax.ejb.EJB;

import pagecode.PageCodeBase;

import zw.co.esolutions.ewallet.sms.SmppService;

/**
 * @author taurai
 *
 */
public class StartSmsServerBean extends PageCodeBase {

	@EJB private SmppService smppService;
	/**
	 * 
	 */
	public StartSmsServerBean() {
		// TODO Auto-generated constructor stub
	}
	
	
	public String startServer() {
		try {
			if(!this.smppService.loadConfiguration()) {
				super.setErrorMessage("Server failed to start.");
				return "failure";
			}
		} catch (Exception e) {
			super.setErrorMessage("Server not started. An error occured.");
			return "failure";
		}
		super.setInformationMessage("Server started successfully.");
		return "success";
	}

}
