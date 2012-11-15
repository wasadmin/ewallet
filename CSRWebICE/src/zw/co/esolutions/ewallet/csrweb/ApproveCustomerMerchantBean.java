package zw.co.esolutions.ewallet.csrweb;

import java.util.ArrayList;
import java.util.List;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchant;
import zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchantStatus;
import zw.co.esolutions.ewallet.profileservices.service.Profile;

public class ApproveCustomerMerchantBean extends PageCodeBase {
	private List<CustomerMerchant> customerMerchantList;
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
	
	public List<CustomerMerchant> getcustomerMerchantList() {
		if (customerMerchantList == null || customerMerchantList.isEmpty()) {
			try {
				customerMerchantList = super.getMerchantService().getCustomerMerchantByStatus(CustomerMerchantStatus.AWAITING_APPROVAL);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (customerMerchantList == null || customerMerchantList.isEmpty()) {
				super.setInformationMessage("No bank accounts awaiting approval.");
				customerMerchantList = new ArrayList<CustomerMerchant>();
			} else {
				super.setInformationMessage(customerMerchantList.size() + " bank accounts found.");
			}
		}
		return customerMerchantList;
	}
	public void setCustomerMerchantList(List<CustomerMerchant> customerMerchantList) {
		this.customerMerchantList = customerMerchantList;
	}
	
	@SuppressWarnings("unchecked")
	public String viewCustomer() {
		super.getRequestScope().put("customerId", super.getRequestParam().get("customerId"));
		gotoPage("/csr/viewCustomer.jspx");
		return "viewCustomer";
	}
}
