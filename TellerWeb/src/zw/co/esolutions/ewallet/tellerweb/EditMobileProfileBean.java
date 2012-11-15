package zw.co.esolutions.ewallet.tellerweb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
//import zw.co.esolutions.ewallet.customerservices.service.MobileNetwork;
import zw.co.esolutions.ewallet.customerservices.service.MobileNetworkOperator;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfileStatus;
import zw.co.esolutions.ewallet.msg.MessageAction;
import zw.co.esolutions.ewallet.msg.MessageSync;

public class EditMobileProfileBean extends PageCodeBase {
	private String mobileProfileId;
	private MobileProfile mobileProfile;
	private List<SelectItem> networkList;
	private String selectedNetwork;
	
	public String getMobileProfileId() {
		if (mobileProfileId == null) {
			mobileProfileId = (String) super.getRequestParam().get("mobileProfileId");
		}
		return mobileProfileId;
	}
	public void setMobileProfileId(String mobileProfileId) {
		this.mobileProfileId = mobileProfileId;
	}
	public MobileProfile getMobileProfile() {
		if (mobileProfile == null || mobileProfile.getId() == null) {
			if (mobileProfileId != null) {
				mobileProfile = super.getCustomerService().findMobileProfileById(mobileProfileId);
				this.setSelectedNetwork(mobileProfile.getNetwork().name());
			} else {
				mobileProfile = new MobileProfile();
				mobileProfile.setCustomer(new Customer());
			}
		}
		return mobileProfile;
	}
	public void setMobileProfile(MobileProfile mobileProfile) {
		this.mobileProfile = mobileProfile;
	}
	
	public List<SelectItem> getNetworkList() {
		if (networkList == null) {
			networkList = new ArrayList<SelectItem>();
			/*for (MobileNetwork network: MobileNetwork.values()) {
				networkList.add(new SelectItem(network.name(), network.name()));
			}*/
		}
		return networkList;
	}
	public void setNetworkList(List<SelectItem> networkList) {
		this.networkList = networkList;
	}
	public String getSelectedNetwork() {
		return selectedNetwork;
	}
	public void setSelectedNetwork(String selectedNetwork) {
		this.selectedNetwork = selectedNetwork;
	}
	
	public String editPrimaryMobile() {
		String id = (String) super.getRequestParam().get("mobileProfileId");
		mobileProfile = super.getCustomerService().findMobileProfileById(mobileProfileId);
		List<MobileProfile> profiles = super.getCustomerService().getMobileProfileByCustomer(mobileProfile.getCustomer().getId());
		
		try {
			for (MobileProfile mp: profiles) {
				if (!mobileProfile.isPrimary()) {
					if (mp.isPrimary()) {
						mp.setPrimary(false);
						super.getCustomerService().updateMobileProfile(mp, super.getJaasUserName());
						MessageSync.populateAndSync(mp, MessageAction.UPDATE);
						
						mobileProfile.setPrimary(true);
						mobileProfile = super.getCustomerService().updateMobileProfile(mobileProfile, super.getJaasUserName());
						MessageSync.populateAndSync(mobileProfile, MessageAction.UPDATE);
					}
				} 
			}
		} catch (Exception e) {
			
		}
		return "editPrimaryMobile";
	}
	@SuppressWarnings("unchecked")
	public String submit() {
		getMobileProfile().setNetwork(MobileNetworkOperator.valueOf(selectedNetwork));
		try {
			mobileProfile.setStatus(MobileProfileStatus.AWAITING_APPROVAL);
			mobileProfile = super.getCustomerService().updateMobileProfile(mobileProfile, super.getJaasUserName());
			MessageSync.populateAndSync(mobileProfile, MessageAction.UPDATE);
		} catch (Exception e) {
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage("Mobile Profile updated successfully.");
		super.getRequestScope().put("customerId", getMobileProfile().getCustomer().getId());
		super.gotoPage("/teller/viewCustomer.jspx");
		return "submit";
	}
	
	@SuppressWarnings("unchecked")
	public String cancel() {
		super.getRequestScope().put("customerId", getMobileProfile().getCustomer().getId());
		super.gotoPage("/teller/viewCustomer.jspx");
		return "cancel";
	}
}
