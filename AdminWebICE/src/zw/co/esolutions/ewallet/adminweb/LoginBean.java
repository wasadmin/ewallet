package zw.co.esolutions.ewallet.adminweb;

import java.io.IOException;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.audit.AuditEvents;
import zw.co.esolutions.ewallet.audittrailservices.service.Exception_Exception;
import zw.co.esolutions.ewallet.filter.LoginFilter;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.util.SystemConstants;
import zw.co.esolutions.security.loginmodule.LoginService;

public class LoginBean extends PageCodeBase {
	private String username;
	private String password;
	private Subject user;
	private static LoginContext lc;
	@EJB
	private LoginService ls;
	
	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String login(){
		
		String report;
		if((report = this.checkAttributes()) != null) {
			super.setErrorMessage(report);
			return "failure";
		}
		System.out.println("the value of the "+ ls);
		try {
			lc = ls.createLoginContext("EWalletLogin", username, password);
			lc.login();
			user = lc.getSubject();
			//Check if a user is allowed to access this app
			if(!this.appAthentication()){
				super.setErrorMessage("You are not allowed to acceess this application.");
				return "failure";
			}
			
			HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
			String ip = request.getRemoteAddr();
			Profile p = super.getProfileService().getProfileByUserName(username);
			String roleName = p.getRole().getRoleName();
			if("CSR INITIATOR".equalsIgnoreCase(roleName) || "CSR APPROVER".equalsIgnoreCase(roleName) ||
					"TELLER".equalsIgnoreCase(roleName) || "TELLER SUPERVISOR".equalsIgnoreCase(roleName)){
				if(!super.getProfileService().validateHost(p, ip)){
					super.setErrorMessage("You are not authorised to access this application from this host");
					return "failure";
				}
			}
			
			//String role = user.getPrincipals().;
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("CurrentUser",user);
			this.auditLogin();
			
		} catch (LoginException le) {
			System.out.println("Login failed "+ le.getMessage()+" LoginException");
			if(le.getMessage().equals(SystemConstants.AUTH_STATUS_CHANGE_PASSWORD)){
				super.getRequestScope().put("username", username);
				super.gotoPage("/login/forceChangePassword.jspx");
			}
			super.setErrorMessage(le.getMessage());
			return "failure";
		} catch (SecurityException se) {
		    System.err.println("Cannot create LoginContext. "+ se.getMessage()+ " SecurityException");
		    super.setErrorMessage("Login failed.");
		    return "failure";
		} catch (Exception e) {
			 System.err.println("Other Exceptions. "+ e.getMessage()+ " SecurityException");
			 super.setErrorMessage("An error has occured. Login failed.");
			 return "failure";
		}
		return "success";
	}
	
	private boolean appAthentication(){
		HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		String url = request.getRequestURI();
		String[] tokens = url.split("/");
		String appName = tokens[tokens.length - 3];	
		System.out.println("Application:"+appName.toUpperCase());
		return super.getProfileService().canDo(username, appName.toUpperCase());
	}
	
	public String logout(){
		try {
			username="";
			password="";
			System.out.println((Subject)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("CurrentUser"));
			this.auditLogout();
			this.clearLoggin();
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("CurrentUser");
			LoginFilter.setOnce(true);
			lc.logout();
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.redirect("login/login.jspx");
		return "logout1";
	}
	
	public String loginPage(){
		return "login";
	}
	
	public String clear() {
		username = "";
		password = "";
		return "clear";
	}
	
	public String cancelLogout() {
		return "cancelLogout";
	}
	
	/*
	 * Method for missing fields
	 */
	private String checkAttributes(){
		StringBuffer buffer = new StringBuffer("The following values are required: ");
		int length = 0;
		if(this.getUsername() == null || "".equalsIgnoreCase(this.getUsername())) {
			buffer.append("User name, ");
		}  if(this.getPassword() == null || "".equalsIgnoreCase(this.getPassword())) {
			buffer.append("Password, ");
		} 
		length = buffer.toString().length();
				
		if(length > 40) {
			buffer.replace(length-2, length, " ");
			return buffer.toString();
		}
		return null;
	}
	
	private void auditLogin(){
		try {
			String ip = "";
			
			//Get IP Address
			HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
			ip = request.getRemoteAddr();
			
			super.getAuditService().logActivityWithNarrative(super.getJaasUserName(), AuditEvents.LOGIN, "ADMIN APPLICATION USER LOGIN FROM IP ADDRESS:"+ip);
		} catch (Exception_Exception e) {
			e.printStackTrace();
		}
	}
	
	private void auditLogout(){
		try {
			String ip = "";
			
			//Get IP Address
			HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
			ip = request.getRemoteAddr();
			
			super.getAuditService().logActivityWithNarrative(super.getJaasUserName(), AuditEvents.LOGOUT, "ADMIN APPLICATION USER LOGOUT FROM IP ADDRESS:"+ip);
		} catch (Exception_Exception e) {
			e.printStackTrace();
		}
	}
	
	private void redirect(String path){
		try {
			if(path.startsWith("/")){
				path = path.replaceFirst("/", "");
			}
			FacesContext.getCurrentInstance().getExternalContext().redirect("/AdminWebICE/"+path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void clearLoggin(){
		try{
			String userName = super.getJaasUserName();
			Profile profile = super.getProfileService().getProfileByUserName(userName);
			profile.setLoggedIn(false);
			profile = super.getProfileService().updateProfile(profile, userName);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
