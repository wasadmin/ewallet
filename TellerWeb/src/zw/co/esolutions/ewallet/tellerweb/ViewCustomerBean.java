package zw.co.esolutions.ewallet.tellerweb;

import java.util.List;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountStatus;
import zw.co.esolutions.ewallet.bankservices.service.Exception_Exception;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.CustomerStatus;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfileStatus;
import zw.co.esolutions.ewallet.msg.MessageAction;
import zw.co.esolutions.ewallet.msg.MessageSync;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.sms.MessageSender;

public class ViewCustomerBean extends PageCodeBase {
	private String customerId;
	private Customer customer;
	private ContactDetails contactDetails;
	private List<MobileProfile> mobileProfileList;
	private List<BankAccount> bankAccountList;
	private boolean approver;
	private boolean auditor;
	
	public String getCustomerId() {
		if (customerId == null) {
			customerId = (String) super.getRequestParam().get("customerId");
		}
		if (customerId == null) {
			customerId = (String) super.getRequestScope().get("customerId");
		}
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public Customer getCustomer() {
		if (customer == null && this.getCustomerId() != null) {
			customer = super.getCustomerService().findCustomerById(customerId);
			approver = this.checkProfileAccessRight("APPROVE");
			auditor = this.checkProfileAccessRight("AUDIT");
		}
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public ContactDetails getContactDetails() {
		if (contactDetails == null && this.getCustomer() != null) {
			contactDetails = super.getContactDetailsService().findContactDetailsById(customer.getContactDetailsId());
		}
		return contactDetails;
	}
	public void setContactDetails(ContactDetails contactDetails) {
		this.contactDetails = contactDetails;
	}
	public List<MobileProfile> getMobileProfileList() {
		if (mobileProfileList == null && this.getCustomerId() != null) {
			mobileProfileList = super.getCustomerService().getMobileProfileByCustomer(customerId);
		}
		return mobileProfileList;
	}
	public void setMobileProfileList(List<MobileProfile> mobileProfileList) {
		this.mobileProfileList = mobileProfileList;
	}
	public List<BankAccount> getBankAccountList() {
		if (bankAccountList == null && this.getCustomerId() != null) {
			try {
				bankAccountList = super.getBankService().getBankAccountByAccountHolderIdAndOwnerType(customerId, 
						OwnerType.CUSTOMER);
			} catch (Exception e) {
				
			}
		}
		return bankAccountList;
	}
	public void setBankAccountList(List<BankAccount> bankAccountList) {
		this.bankAccountList = bankAccountList;
	}
	
	public String ok() {
		super.gotoPage("/teller/searchCustomer.jspx");
		return "ok";
	}
	
	@SuppressWarnings("unchecked")
	public String edit() {
		super.getRequestScope().put("customerId", super.getRequestParam().get("customerId"));
		super.gotoPage("/teller/editCustomer.jspx");
		return "edit";
	}
	
	public String approveCustomer() {
		String customerId = (String) super.getRequestParam().get("customerId");
		Customer c = super.getCustomerService().findCustomerById(customerId);
		c.setStatus(CustomerStatus.ACTIVE);
		try {
			customer = super.getCustomerService().updateCustomer(c, super.getJaasUserName());
			MessageSync.populateAndSync(c, MessageAction.CREATE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "viewCustomer";
	}
	
	public String rejectCustomer() {
		Customer c = super.getCustomerService().findCustomerById(customerId);
		c.setStatus(CustomerStatus.DISAPPROVED);
		try {
			customer = super.getCustomerService().updateCustomer(c, super.getJaasUserName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "rejectCustomer";
	}
	public String approveAll() {
		MobileProfile primaryMobile = null;
		String customerId = (String) super.getRequestParam().get("customerId");
		customer = super.getCustomerService().findCustomerById(customerId);
		customer.setStatus(CustomerStatus.ACTIVE);
		try {
			customer = super.getCustomerService().updateCustomer(customer, super.getJaasUserName());
			MessageSync.populateAndSync(customer, MessageAction.CREATE);
			
			//Sycning Msg
			MessageSync.populateAndSync(this.getContactDetails(), MessageAction.CREATE);
			
			List<MobileProfile> profiles = super.getCustomerService().getMobileProfileByCustomer(customerId);
			for (MobileProfile profile: profiles) {
				profile.setStatus(MobileProfileStatus.ACTIVE);
				super.getCustomerService().updateMobileProfile(profile, super.getJaasUserName());
				MessageSync.populateAndSync(profile, MessageAction.CREATE);
				if(profile.isPrimary()) {
					primaryMobile = profile;
				}
				mobileProfileList = super.getCustomerService().getMobileProfileByCustomer(customerId);
			}
			List<BankAccount> accounts = super.getBankService().getBankAccountByAccountHolderIdAndOwnerType(customerId, 
					OwnerType.CUSTOMER);
			for (BankAccount account: accounts) {
				account.setStatus(BankAccountStatus.ACTIVE);
				super.getBankService().editBankAccount(account, super.getJaasUserName());
				MessageSync.populateAndSync(account, MessageAction.CREATE);
			}
			this.notifyCustomer(primaryMobile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "approveAll";
	}
	
	public String rejectAll() {
		String customerId = (String) super.getRequestParam().get("customerId");
		Customer customer = super.getCustomerService().findCustomerById(customerId);
		customer.setStatus(CustomerStatus.DISAPPROVED);
		try {
			super.getCustomerService().updateCustomer(customer, super.getJaasUserName());
			List<MobileProfile> profiles = super.getCustomerService().getMobileProfileByCustomer(customerId);
			for (MobileProfile profile: profiles) {
				profile.setStatus(MobileProfileStatus.DISAPPROVED);
				super.getCustomerService().updateCustomer(customer, super.getJaasUserName());
			}
			List<BankAccount> accounts = super.getBankService().getBankAccountByAccountHolderIdAndOwnerType(customerId, 
					OwnerType.CUSTOMER);
			for (BankAccount account: accounts) {
				account.setStatus(BankAccountStatus.DISAPPROVED);
				super.getBankService().editBankAccount(account, super.getJaasUserName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "rejectAll";
	}
	
	public String approveMobileProfile() {
		String mobileProfileId = (String) super.getRequestParam().get("mobileProfileId");
		MobileProfile mobile = super.getCustomerService().findMobileProfileById(mobileProfileId);
		mobile.setStatus(MobileProfileStatus.ACTIVE);
		try {
			super.getCustomerService().updateMobileProfile(mobile, super.getJaasUserName());
			MessageSync.populateAndSync(mobile, MessageAction.CREATE);
			this.notifyCustomer(mobile);
			mobileProfileList = super.getCustomerService().getMobileProfileByCustomer(customerId);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "approveMobileProfile";
	}
	
	public String rejectMobileProfile() {
		String mobileProfileId = (String) super.getRequestParam().get("mobileProfileId");
		MobileProfile mobile = super.getCustomerService().findMobileProfileById(mobileProfileId);
		mobile.setStatus(MobileProfileStatus.DISAPPROVED);
		try {
			super.getCustomerService().updateMobileProfile(mobile, super.getJaasUserName());
			mobileProfileList = super.getCustomerService().getMobileProfileByCustomer(customerId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "rejectMobileProfile";
	}
	
	public String approveBankAccount() {
		String accountId = (String) super.getRequestParam().get("accountId");
		BankAccount account = super.getBankService().findBankAccountById(accountId);
		account.setStatus(BankAccountStatus.ACTIVE);
		try {
			super.getBankService().editBankAccount(account, super.getJaasUserName());
			MessageSync.populateAndSync(account, MessageAction.CREATE);
			bankAccountList = super.getBankService().getBankAccountByAccountHolderIdAndOwnerType(customerId, 
					OwnerType.CUSTOMER);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "approveBankAccount";
	}
	
	public String rejectBankAccount() {
		String accountId = (String) super.getRequestParam().get("accountId");
		BankAccount account = super.getBankService().findBankAccountById(accountId);
		account.setStatus(BankAccountStatus.DISAPPROVED);
		try { 
			super.getBankService().editBankAccount(account, super.getJaasUserName());
			bankAccountList = super.getBankService().getBankAccountByAccountHolderIdAndOwnerType(customerId, 
					OwnerType.CUSTOMER);
		} catch (Exception_Exception e) {
			e.printStackTrace();
		}
		return "rejectBankAccount";
	}
	
	public boolean checkProfileAccessRight(String accessRight) {
		return super.getProfileService().canDo(super.getJaasUserName(), accessRight);
	}
	public void setApprover(boolean approver) {
		this.approver = approver;
	}
	public boolean isApprover() {
		return approver;
		
	}
	
	public boolean isAuditor() {
		return auditor;
	}
	
	public void setAuditor(boolean auditor) {
		this.auditor = auditor;
	}
	
	public String editMobileProfile() {
		gotoPage("/teller/editMobileProfile.jspx");
		return "editMobileProfile";
	}
	
	public String editBankAccount() {
		gotoPage("/teller/editBankAccount.jspx");
		return "editBankAccount";
	}
	
	public String viewLogs() {
		gotoPage("/teller/viewLogs.jspx");
		return "viewLogs";
	}
	
	private void notifyCustomer(MobileProfile mp) throws Exception {
		String message = "Welcome to ZB Mobile Banking.[nl]Your password is " + mp.getSecretCode() + ".[nl]You can now send your requests to 440.";
		
//		MessageSender.send(zw.co.esolutions.ewallet.util.EWalletConstants.FROM_EWALLET_TO_SWITCH_QUEUE, mp.getMobileNumber(), message);
	}
	
}
