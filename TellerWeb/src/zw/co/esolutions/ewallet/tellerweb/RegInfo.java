package zw.co.esolutions.ewallet.tellerweb;

import java.io.Serializable;
import java.util.List;

import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.referralservices.service.Referral;

public class RegInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private Customer customer;
	private ContactDetails contactDetails;
	private List<MobileProfile> mobileProfileList;
	private List<BankAccount> bankAccountList;
	private Referral referral;
		
	public Customer getCustomer() {
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
	public void setContactDetails(ContactDetails contactDetails) {
		this.contactDetails = contactDetails;
	}
	public ContactDetails getContactDetails() {
		return contactDetails;
	}
	public void setReferral(Referral referral) {
		this.referral = referral;
	}
	public Referral getReferral() {
		return referral;
	}
	
}
