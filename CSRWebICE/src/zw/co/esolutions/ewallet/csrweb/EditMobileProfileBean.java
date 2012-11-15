package zw.co.esolutions.ewallet.csrweb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.agentservice.service.Agent;
import zw.co.esolutions.ewallet.agentservice.service.AgentServiceSOAPProxy;
import zw.co.esolutions.ewallet.agentservice.service.ProfileStatus;
import zw.co.esolutions.ewallet.csr.msg.MessageAction;
import zw.co.esolutions.ewallet.csr.msg.MessageSync;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.CustomerClass;
import zw.co.esolutions.ewallet.customerservices.service.MobileNetworkOperator;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfileStatus;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;

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
			for (MobileNetworkOperator network: MobileNetworkOperator.values()) {
				networkList.add(new SelectItem(network.name(), network.name()));
			}
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
			AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
			ProfileServiceSOAPProxy profileService=new ProfileServiceSOAPProxy();
			Profile capturerProfile=profileService.getProfileByUserName(getJaasUserName());
			Agent agent = agentService.getAgentByCustomerId(mobileProfile.getCustomer().getId());
			System.out.println(">>>>>>>>>>>>>>>>> "+ agent.getAgentName());
			mobileProfile.setStatus(MobileProfileStatus.AWAITING_APPROVAL);
			mobileProfile.setMobileProfileEditBranch(capturerProfile.getBranchId());
			mobileProfile = super.getCustomerService().updateMobileProfile(mobileProfile, super.getJaasUserName());
			MessageSync.populateAndSync(mobileProfile, MessageAction.UPDATE);
			agent.setStatus(ProfileStatus.AWAITING_EDIT_APPROVAL);
			agentService.updateAgent(agent, getJaasUserName());
			MessageSync.populateAndSync(agent, MessageAction.UPDATE);
		} catch (Exception e) {
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage("Mobile Profile updated successfully.");
		super.getRequestScope().put("customerId", getMobileProfile().getCustomer().getId());
		if(getMobileProfile().getCustomer().getCustomerClass().name().equals(CustomerClass.AGENT.name())){
			super.gotoPage("/csr/viewAgent.jspx");
			return "submit";
		}
		super.gotoPage("/csr/viewCustomer.jspx");
		return "submit";
	}
	
	@SuppressWarnings("unchecked")
	public String cancel() {
		super.getRequestScope().put("customerId", getMobileProfile().getCustomer().getId());
		if(getMobileProfile().getCustomer().getCustomerClass().name().equals(CustomerClass.AGENT.name())){
			super.gotoPage("/csr/viewAgent.jspx");
			return "submit";
		}
		super.gotoPage("/csr/viewCustomer.jspx");
		return "cancel";
	}
}
