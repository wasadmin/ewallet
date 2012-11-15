package zw.co.esolutions.ewallet.tellerweb;

import java.util.ArrayList;
import java.util.List;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.CustomerStatus;
import zw.co.esolutions.ewallet.profileservices.service.Profile;

public class ApproveCustomerBean extends PageCodeBase{

	private Profile profile;
	private List<Customer> customerList;
	
	public ApproveCustomerBean() {
		
	}
	
	@SuppressWarnings("unchecked")
	public String viewCustomer() {
		super.getRequestScope().put("customerId", super.getRequestParam().get("customerId"));
		super.gotoPage("/teller/viewCustomer.jspx");
		return "success";
	}
	
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

	public void setCustomerList(List<Customer> customerList) {
		this.customerList = customerList;
	}

	public List<Customer> getCustomerList() {
		if (customerList == null || customerList.isEmpty()) {
			customerList = super.getCustomerService().getCustomerByStatus(CustomerStatus.AWAITING_APPROVAL);
			if (customerList == null || customerList.isEmpty()) {
				customerList = new ArrayList<Customer>();
				super.setInformationMessage("No results found.");
			} else {
				super.setInformationMessage("Results found (" + customerList.size() + ")");
			}
		}
		return customerList;
	}

	
}
