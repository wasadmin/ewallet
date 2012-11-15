package zw.co.esolutions.ewallet.adminweb;

import java.util.List;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileStatus;
import zw.co.esolutions.ewallet.util.EmailSender;

public class ApproveProfileBean extends PageCodeBase {
	
	private List<Profile> profileList;
	
	
	public List<Profile> getProfileList() {
		if (profileList == null) {
			try {
				profileList = super.getProfileService().getProfileByStatus(ProfileStatus.AWAITING_APPROVAL);
				profileList.addAll(super.getProfileService().getProfileByStatus(ProfileStatus.AWAITING_EDIT_APPROVAL));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return profileList;
	}

	public void setProfileList(List<Profile> profileList) {
		this.profileList = profileList;
	}

	public String approve() {
		Profile profile = null;
		try {
			
			String profileId = (String) super.getRequestParam().get("profileId");
	
			profile = super.getProfileService().findProfileById(profileId);
			ProfileStatus oldStatus = profile.getStatus();
			profile = super.getProfileService().approveProfile(profile, super.getJaasUserName());
		
			//Send Reg Email
			if(oldStatus.equals(ProfileStatus.AWAITING_APPROVAL)){
				//Email the password
				String msg = "\nPassword: "+profile.getUserPassword()+"\n";
				String subject = "EWallet Password";
				String[] recepients = {profile.getEmail()};
				EmailSender es = new EmailSender();
				es.postMail(recepients, "ewallet@zb.co.zw", subject, msg, null);
			}
			//Refresh the list
			profileList = null;
			this.getProfileList();
			super.setInformationMessage(profile.getUserName() + " approved successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		
		return "approve";
	}
	
	public String reject() {
		Profile profile = null;
		try {
			
			String profileId = (String) super.getRequestParam().get("profileId");
			
			profile = super.getProfileService().findProfileById(profileId);
			profile = super.getProfileService().rejectProfile(profile, super.getJaasUserName());
			
			//Refresh the list
			profileList = null;
			this.getProfileList();
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage(profile.getUserName() + " has been rejected.");
		return "rejected";
	}
	
		
	public String viewDetails(){
		super.gotoPage("admin/viewProfile.jspx");
		profileList=null;
		return "viewProfile";
	}

}
