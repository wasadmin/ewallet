package zw.co.esolutions.ewallet.tellerweb;

import java.util.List;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;

public class ConfirmApprovalBean extends PageCodeBase{

	private Customer customer;
	private List<MobileProfile> mobileProfileList;
	private List<BankAccount> bankAccountList;
	private ContactDetails contactDetails;
	private String customerId;
	
	public ConfirmApprovalBean() {
		
	}

	public Customer getCustomer() {
		if(this.customer == null) {
			try {
				this.customer = super.getCustomerService().findCustomerById(this.getCustomerId());
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<MobileProfile> getMobileProfileList() {
		return mobileProfileList;
	}

	public void setMobileProfileList(List<MobileProfile> mobileProfileList) {
		this.mobileProfileList = mobileProfileList;
	}

	public List<BankAccount> getBankAccountList() {
		return bankAccountList;
	}

	public void setBankAccountList(List<BankAccount> bankAccountList) {
		this.bankAccountList = bankAccountList;
	}

	public ContactDetails getContactDetails() {
		return contactDetails;
	}

	public void setContactDetails(ContactDetails contactDetails) {
		this.contactDetails = contactDetails;
	}
	
	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String approveCustomer() {
		return "success";
	}
	
	public String disapproveCustomer() {
		return "success";
	}
	
	public String doBack() {
		//gotoPage(/csr/approveCustomer.jspx);
		return "success";
	}
}
