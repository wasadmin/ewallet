package zw.co.esolutions.ewallet.csrweb;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.util.PasswordUtil;
import zw.co.esolutions.ewallet.util.SystemConstants;

public class ChangeProfilePasswordBean extends PageCodeBase {
	
	private Profile profile;
	private String currentPassword;
	private String newPassword1;
	private String newPassword2;
	
	
	public Profile getProfile() {
		if (profile == null) {
			System.out.println(">>>>>>>>>>>"+super.getJaasUserName());
			profile = super.getProfileService().getProfileByUserName(super.getJaasUserName().trim());
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
					if(PasswordUtil.validatePassword(newPassword1)){
						profile = super.getProfileService().getProfileByUserName(super.getJaasUserName());
						String result = super.getProfileService().changeProfilePassword(profile,currentPassword,newPassword1, super.getJaasUserName());
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
		super.gotoPage("csr/changeProfilePassword.jspx");
		return "submit";
	}
	
	public String cancel() {
		super.gotoPage("csr/csrHome.jspx");
		return "cancel";
	}
}
