package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.naming.NameAlreadyBoundException;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.profileservices.service.ProfileStatus;
import zw.co.esolutions.ewallet.util.PasswordUtil;
import zw.co.esolutions.ewallet.util.SystemConstants;

public class ViewProfileBean extends PageCodeBase {
	
	private String profileId;
	private Profile profile;
	private BankBranch branch;
	private List<SelectItem> canDoList;
	private String selectedCanDo;
	private boolean showPopup;
	private boolean showPassword;
	private boolean activatable;
	private boolean deactivatable;
	private boolean ipResetable;
	private boolean editable;
	
	public String getProfileId() {
		if (profileId == null) {
			profileId = (String) super.getRequestParam().get("profileId");
		}
		if (profileId == null) {
			profileId = (String) super.getRequestScope().get("profileId");
		}
		return profileId;
	}
	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}
	public Profile getProfile() {
		if (profile == null || profile.getId() == null) {
			if (getProfileId() != null) {
				profile = super.getProfileService().findProfileById(profileId);
			}
		}
		return profile;
	}
	public void setProfile(Profile profile) {
		this.profile = profile;
	}
	public BankBranch getBranch() {
		if (branch == null) {
			if (getProfile() != null && getProfile().getId() != null) {
				branch = super.getBankService().findBankBranchById(profile.getBranchId());
			} else {
				branch = new BankBranch();
			}
		}
		return branch;
	}
	public void setBranch(BankBranch branch) {
		this.branch = branch;
	}
	
	public List<SelectItem> getCanDoList() {
		if (canDoList == null) {
			canDoList = new ArrayList<SelectItem>();
			canDoList.add(new SelectItem("false", "false"));
			canDoList.add(new SelectItem("true", "true"));
		}
		return canDoList;
	}
	public void setCanDoList(List<SelectItem> canDoList) {
		this.canDoList = canDoList;
	}
	public String getSelectedCanDo() {
		return selectedCanDo;
	}
	public void setSelectedCanDo(String selectedCanDo) {
		this.selectedCanDo = selectedCanDo;
	}
	
	public String ok() {
		return "ok";
	}
	
	public boolean isShowPassword() {
		return showPassword;
	}
	public void setShowPassword(boolean showPassword) {
		this.showPassword = showPassword;
	}
	
	public String edit() {
		this.profileId=null;
		this.profile=null;
		this.branch = null;
		return "edit";
	}
	
	@SuppressWarnings("unchecked")
	public String resetPassword(){
		try{
			//Generate profile password
			profile.setUserPassword(PasswordUtil.getPassword(8));
			String status=super.getProfileService().resetProfilePassword(profile, super.getJaasUserName());
			if(status.equals(SystemConstants.RESET_PASSWORD_FAILURE)){
				super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
				return "failure";
			}
			
			//Email the password
			/*String msg = "\nPassword: "+profile.getUserPassword()+"\n";
			String subject = "EWallet Password";
			String[] recepients = {profile.getEmail()};
			EmailSender es = new EmailSender();
			es.postMail(recepients, "ewallet@zb.co.zw", subject, msg, null);*/
			
			this.showPassword=true;
			
			super.setInformationMessage("Profile password reset successfully.");
			super.getRequestScope().put("profileId", profile.getId());
		}catch(Exception e){
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
		}
		return "reset";
	}
	
	
	public String deleteProfile(){
		try{
			super.getProfileService().deleteProfile(profile, super.getJaasUserName());
			super.setInformationMessage("Profile deleted successfully.");
			super.gotoPage("admin/searchProfile.jspx");
		}catch(Exception e){
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
		}
		this.showPopup=false;
		this.showPassword=false;
		profile = null;
		return "reset";
	}	
	
	public String delete(){
		this.showPopup=true;
		return "delete";
	}
	
	public String hidePopup(){
		this.showPopup=false;
		return "cancel";
	}
	
	public String logs(){
		super.gotoPage("admin/viewLogs.jspx");
		return "logs";
	}
	
	public boolean isApprovable(){
		if(super.getProfileService().canDo(super.getJaasUserName(), "ApproveProfile") && (this.getProfile().getStatus().equals(ProfileStatus.AWAITING_APPROVAL)||this.getProfile().getStatus().equals(ProfileStatus.AWAITING_EDIT_APPROVAL))){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isEditable(){
		ProfileServiceSOAPProxy profileService = super.getProfileService();
		try{
			boolean a = profileService.canDo(getJaasUserName(), "EDITPROFILE");
			if(a){
				editable = true;
			}else{
				editable = false;
			}
		}catch (Exception e) {
			return false;
		}
		return editable;
	}
	
	public boolean isShowPopup() {
		return showPopup;
	}
	public void setShowPopup(boolean showPopup) {
		this.showPopup = showPopup;
	}
	@SuppressWarnings("unchecked")
	public String approve() {
		
		try {
			ProfileStatus oldStatus = profile.getStatus();
			profile = super.getProfileService().approveProfile(profile, super.getJaasUserName());
			//Send Reg Email
			if(oldStatus.equals(ProfileStatus.AWAITING_APPROVAL)){
				//Email the password
				/*String msg = "\nPassword: "+profile.getUserPassword()+"\n";
				String subject = "EWallet Password";
				String[] recepients = {profile.getEmail()};
				EmailSender es = new EmailSender();
				es.postMail(recepients, "ewallet@zb.co.zw", subject, msg, null);*/
				this.showPassword=true;
			}
			
			super.getRequestParam().put("profileId", profile.getId());
			super.setInformationMessage(profile.getUserName() + " approved successfully.");
			//super.gotoPage("admin/approveProfile.jspx");
		} catch (Exception e) {
			if(e instanceof NameAlreadyBoundException){
				super.setErrorMessage("This user already exists.");
				return "failure";
			}
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		
		return "approve";
	}
	
	@SuppressWarnings("unchecked")
	public String reject() {
		
		try {
			
			profile = super.getProfileService().rejectProfile(profile, super.getJaasUserName());		
			super.getRequestParam().put("profileId", profile.getId());
			super.setInformationMessage(profile.getUserName() + " rejected successfully.");
			super.gotoPage("admin/approveProfile.jspx");
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage(profile.getUserName() + " has been rejected.");
		return "rejected";
	}
	public void setActivatable(boolean activatable) {
		this.activatable = activatable;
	}
	public boolean isActivatable() {
		ProfileServiceSOAPProxy profileService = super.getProfileService();
		try{
			boolean a = profileService.canDo(getJaasUserName(), "ACTIVATEPROFILE");
			if(a && getProfile().getStatus().name().equals(ProfileStatus.INACTIVE.name())){
				activatable = true;
			}else{
				activatable = false;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return activatable;
	}
	public void setDeactivatable(boolean deactivatable) {
		this.deactivatable = deactivatable;
	}
	public boolean isDeactivatable() {
		ProfileServiceSOAPProxy profileService = super.getProfileService();
		try{
			boolean a = profileService.canDo(getJaasUserName(), "DEACTIVATEPROFILE");
			if(a && getProfile().getStatus().name().equals(ProfileStatus.ACTIVE.name())){
				deactivatable = true;
			}else{
				deactivatable = false;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return deactivatable;
	}	
	
	public String activateProfile(){
		ProfileServiceSOAPProxy profileService = super.getProfileService();
		try{
			Profile p = getProfile();
			p.setStatus(ProfileStatus.ACTIVE);
			profileService.updateProfile(p, getJaasUserName());
			
		}catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage("Failed to activate profile");
			return "false";
		}
		super.setInformationMessage("Profile activated sucessfuly");
		return "success";
	}
	
	public String deactivateProfile(){
		ProfileServiceSOAPProxy profileService = super.getProfileService();
		try{
			Profile p = getProfile();
			p.setStatus(ProfileStatus.INACTIVE);
			profileService.updateProfile(p, getJaasUserName());
			
		}catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage("Failed to deactivate profile");
			return "false";
		}
		super.setInformationMessage("Profile deactivated sucessfuly");
		return "success";
	}
	
	public String resetIP(){
		ProfileServiceSOAPProxy profileService = super.getProfileService();
		try{
			profileService.resetProfileIP(getProfile(), super.getJaasUserName());
		}catch (Exception e) {
			super.setErrorMessage("Failed to reset IP Address");
			return "false";
		}
		super.setInformationMessage("IP Address reset sucessfuly");
		return "success";
	}
	public boolean isIpResetable() {
		ProfileServiceSOAPProxy profileService = super.getProfileService();
		try{
			boolean a = profileService.canDo(getJaasUserName(), "EDITPROFILE");
			if(a && getProfile().getStatus().name().equals(ProfileStatus.ACTIVE.name())){
				ipResetable = true;
			}else{
				ipResetable = false;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return ipResetable;
	}
	public void setIpResetable(boolean ipResetable) {
		this.ipResetable = ipResetable;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
}
