package zw.co.esolutions.ewallet.csrweb;

import java.util.ArrayList;
import java.util.List;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfileStatus;
import zw.co.esolutions.ewallet.profileservices.service.Profile;

public class ApproveMobileProfileBean extends PageCodeBase {
	
	private List<MobileProfile> mobileProfileList;
	private Profile profile;
	
	public Profile getProfile() {
		if(this.profile == null) {
			try {
				this.profile = super.getProfileService().getProfileByUserName(super.getJaasUserName());
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return profile;
	}
	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	
	public List<MobileProfile> getMobileProfileList() {
		if (mobileProfileList == null || mobileProfileList.isEmpty()) {
			mobileProfileList = super.getCustomerService().getMobileProfileByStatus(MobileProfileStatus.AWAITING_APPROVAL.name());
			if (mobileProfileList == null || mobileProfileList.isEmpty()) {
				super.setInformationMessage("No mobile profiles awaiting approval");
				mobileProfileList = new ArrayList<MobileProfile>();
			} else {
				super.setInformationMessage(mobileProfileList.size() + " mobile profiles awaiting approval");
			}
		}
		return mobileProfileList;
	}
	public void setProfiles(List<MobileProfile> mobileProfileList) {
		this.mobileProfileList = mobileProfileList;
	}
	
	public String viewCustomer() {
		gotoPage("/csr/viewCustomer.jspx");
		return "viewCustomer";
	}
	
}
