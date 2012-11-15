package zw.co.esolutions.ewallet.csrweb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.csr.msg.MessageAction;
import zw.co.esolutions.ewallet.csr.msg.MessageSync;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.MobileNetworkOperator;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfileStatus;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.util.NumberUtil;
import zw.co.esolutions.ewallet.util.SystemConstants;

public class AddMobileProfile extends PageCodeBase {
		private String customerId;
		private Customer customer;
		private String mobileProfileId;
		private MobileProfile mobileProfile;
		private List<SelectItem> networkList;
		private String selectedNetwork;
		private Bank bank;
		private BankAccount bankAccount;
		
		

		private static Logger LOG ;
		
		static{
			try {
				PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
				LOG = Logger.getLogger(AddMobileProfile.class);
			} catch (Exception e) {
				System.err.println("Failed to initilise logger for " + AddMobileProfile.class);
			}
		}
		
		
		
		
		public String getCustomerId() {
			if(this.customerId==null){
				customerId=(String) getRequestScope().get("customerId");
			}
			return customerId;
		}
		public void setCustomerId(String customerId) {
			this.customerId = customerId;
		}
		public Customer getCustomer() {
			if(customer==null && getCustomerId()!=null){
				CustomerServiceSOAPProxy proxy= new CustomerServiceSOAPProxy();
				this.customer=proxy.findCustomerById(getCustomerId());
			}
			return customer;
		}
		public void setCustomer(Customer customer) {
			this.customer = customer;
		}
		public String getMobileProfileId() {
			
			return mobileProfileId;
		}
		public void setMobileProfileId(String mobileProfileId) {
			this.mobileProfileId = mobileProfileId;
		}
		public MobileProfile getMobileProfile() {
			if(mobileProfile==null){
				mobileProfile= new MobileProfile();
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
		public void setBank(Bank bank) {
			this.bank = bank;
		}
		public Bank getBank() {
			if (bank == null || bank.getId() == null) {
				Profile profile = super.getProfileService().getProfileByUserName(super.getJaasUserName());
				bank = super.getBankService().findBankBranchById(profile.getBranchId()).getBank();
			}
			return bank;
		}
		
		@SuppressWarnings("unchecked")
		public String submit() {
			ProfileServiceSOAPProxy profileService=new ProfileServiceSOAPProxy();
			Profile capturerProfile=profileService.getProfileByUserName(getJaasUserName());
			getMobileProfile().setNetwork(MobileNetworkOperator.valueOf(selectedNetwork));
			
				mobileProfile.setStatus(MobileProfileStatus.AWAITING_APPROVAL);
				try {
					LOG.debug("Formating mobilr number");
					mobileProfile
							.setMobileNumber(NumberUtil
									.formatMobileNumber(mobileProfile
											.getMobileNumber()));
					mobileProfile.setBankId(this.getBank().getId());
					mobileProfile.setMobileProfileEditBranch(capturerProfile.getBranchId());
					LOG.debug("End number format    LL Mobile Number: "+mobileProfile.getMobileNumber());
					LOG.debug("End number format    LL Network: "+mobileProfile.getNetwork().name());
				} catch (Exception e) {
					super.setErrorMessage(e.getMessage());
					return "";
				}
			
				
				try {
					LOG.debug("Saving mobile profile");
					mobileProfile.setCustomer(customer);
					CustomerServiceSOAPProxy customerService= new CustomerServiceSOAPProxy();
					mobileProfile = customerService.createMobileProfile(mobileProfile,SystemConstants.SOURCE_APPLICATION_BANK, super.getJaasUserName());
					LOG.debug("Done saving profile");
					super.setInformationMessage("Mobile Profile has been successfully created.");
				} catch (Exception e) {
					super.setErrorMessage(e.getMessage());
					e.printStackTrace();
					return "";
				}
    	 		
				LOG.debug("Navigating");
		
			super.getRequestScope().put("customerId", getMobileProfile().getCustomer().getId());
			super.gotoPage("/csr/viewCustomer.jspx");
			return "submit";
		}
		
		@SuppressWarnings("unchecked")
		public String cancel() {
			//super.getRequestScope().put("customerId", getMobileProfile().getCustomer().getId());
			super.gotoPage("/csr/viewCustomer.jspx");
			return "cancel";
		
	}
		public BankAccount getBankAccount() {
			return bankAccount;
		}
		public void setBankAccount(BankAccount bankAccount) {
			this.bankAccount = bankAccount;
		}

		
}
