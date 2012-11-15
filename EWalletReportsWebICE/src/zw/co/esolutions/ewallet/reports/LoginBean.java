package zw.co.esolutions.ewallet.reports;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.process.timers.StatementServiceSOAPProxy;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.util.SystemConstants;

public class LoginBean extends PageCodeBase {
	private String username;
	private String password;
	private StatementServiceSOAPProxy statementService = new StatementServiceSOAPProxy();
	
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
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		String report;
		if((report = this.checkAttributes()) != null) {
			super.setErrorMessage(report);
			return "failure";
		}
		System.out.println("******************* 1");
		try {
			System.out.println("******************* 2");
			if(!this.appAthentication()){
				System.out.println("******************* 3");
				super.setErrorMessage("Invalid login credentials.");
				return "failure";
			}
			HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
			String ip = request.getRemoteAddr();
			Profile p = profileService.getProfileByUserName(username);
			
			String roleName = p.getRole().getRoleName();
			if("CSR INITIATOR".equalsIgnoreCase(roleName) || "CSR APPROVER".equalsIgnoreCase(roleName) ||
					"TELLER".equalsIgnoreCase(roleName) || "TELLER SUPERVISOR".equalsIgnoreCase(roleName)){
				if(!profileService.validateHost(p, ip)){
					super.setErrorMessage("You are not authorised to access this application from this host");
					return "failure";
				}
			}
			System.out.println("******************* 4");
			ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
			System.out.println("******************* 5");
			String result= ps.authenticateUser(this.getUsername(), this.getPassword());
			if(!result.equalsIgnoreCase(SystemConstants.AUTH_STATUS_AUTHENTICATED)) {
				super.setErrorMessage("Invalid login credentials.");
				return "failure";
			}
			statementService.checkTimer("Agent_Statement_Timer");
			System.out.println("******************* 6");
			
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("CurrentUser",this.getUsername());
		} catch (SecurityException se) {
		    System.err.println("Cannot create LoginContext. "+ se.getMessage()+ " SecurityException");
		    super.setErrorMessage("Login failed.");
		    return "failure";
		} catch (Exception e) {
			e.printStackTrace();
			 System.err.println("Other Exceptions. "+ e.getMessage()+ " SecurityException");
			 super.setErrorMessage("An error has occured. Login failed.");
			 return "failure";
		}
		return "success";
	}
	
	private boolean appAthentication(){
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		System.out.println(">>>>>>>>>> Profile Service = "+ps);
		HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		System.out.println(">>>>>>>>>>>>> HttpRequest :: "+request);
		String url = request.getRequestURI();
		String[] tokens = url.split("/");
		String appName = tokens[tokens.length - 3];	
		System.out.println("Application : "+appName.toUpperCase());
		return ps.canDo(username, appName.toUpperCase());
	}
	
	public String logout(){
		try {
			username="";
			password="";
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("CurrentUser");
			
		} catch (Exception e) {
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
	
	private void redirect(String path){
		try {
			if(path.startsWith("/")){
				path = path.replaceFirst("/", "");
			}
			FacesContext.getCurrentInstance().getExternalContext().redirect("/EWalletReportsWebICE/"+path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
