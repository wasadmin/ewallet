package zw.co.esolutions.ewallet.csrweb;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.audit.AuditEvents;
import zw.co.esolutions.ewallet.audittrailservices.service.Exception_Exception;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.util.PasswordUtil;
import zw.co.esolutions.ewallet.util.SystemConstants;
import zw.co.esolutions.security.loginmodule.LoginService;

public class ForceChangePasswordBean extends PageCodeBase{

	private Profile profile;
	private String currentPassword;
	private String newPassword1;
	private String newPassword2;
	private Subject user;
	private static LoginContext lc;
	@EJB
	private LoginService ls;
	
	
	public Profile getProfile() {
		if (profile == null) {
			String username = (String)super.getRequestScope().get("username");
			profile = super.getProfileService().getProfileByUserName(username.trim());
		}
		return profile;
	}
	public void setProfile(Profile profile) {
		this.profile = profile;
	}
	public String getCurrentPassword() {
		return currentPassword;
	}
	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}
	public String getNewPassword1() {
		return newPassword1;
	}
	public void setNewPassword1(String newPassword1) {
		this.newPassword1 = newPassword1;
	}
	public String getNewPassword2() {
		return newPassword2;
	}
	public void setNewPassword2(String newPassword2) {
		this.newPassword2 = newPassword2;
	}
	
	
	
	public String submit() {
		try {
			if (newPassword1.equals(newPassword2)) {
				System.out.println("channging password >>>>>>>>>>>>>>>>   "+newPassword1);
					if(PasswordUtil.validatePassword(newPassword1)){
					
						profile = super.getProfileService().getProfileByUserName(profile.getUserName());
						
						//System.out.println("Current Pass: "+currentPassword);
						String result = super.getProfileService().changeProfilePassword(profile,currentPassword,newPassword1, profile.getUserName());
						System.out.println("Change Password Result:"+result);
						if(result.equals(SystemConstants.INVALID_OLD_PASSWORD)){
							super.setErrorMessage("Old Password is incorrect.");
							return "failure";
						}else if(result.equals(SystemConstants.PASSWORD_IN_HISTORY)){
							super.setErrorMessage("Password has already been used");
							return "failure";
						}
					}else{
						super.setErrorMessage("Password strength below the required.");
						return "failure";
					}
				} else {
					super.setErrorMessage("New Passwords do not match.");
					return "failure";
				}			
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage("Password changed successfully.");
		try {
			lc = ls.createLoginContext("EWalletLogin", profile.getUserName(), newPassword1);
			lc.login();
			user = lc.getSubject();
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("CurrentUser",user);
			this.auditLogin();
			super.gotoPage("csr/csrHome.jspx");
		} catch (LoginException e) {
			e.printStackTrace();
		}
		return "submit";
	}
	
	private void auditLogin(){
		try {
			String ip = "";
			
			//Get IP Address
			HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
			ip = request.getRemoteAddr();
			
			super.getAuditService().logActivityWithNarrative(profile.getUserName(), AuditEvents.LOGIN, "CSR APPLICATION USER LOGIN FROM IP ADDRESS:"+ip);
		} catch (Exception_Exception e) {
			e.printStackTrace();
		}
	}
}
