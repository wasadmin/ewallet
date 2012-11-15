package zw.co.esolutions.ewallet.csrweb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.AccountBalance;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountStatus;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.bankservices.service.Exception_Exception;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails;
import zw.co.esolutions.ewallet.csr.msg.MessageAction;
import zw.co.esolutions.ewallet.csr.msg.MessageSync;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.CustomerStatus;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfileStatus;
import zw.co.esolutions.ewallet.enums.ResponseType;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchant;
import zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchantStatus;
import zw.co.esolutions.ewallet.msg.RequestInfo;
import zw.co.esolutions.ewallet.msg.ResponseInfo;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.profileservices.service.RoleAccessRight;
import zw.co.esolutions.ewallet.sms.MessageSender;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.EncryptAndDecrypt;

public class ViewCustomerBean extends PageCodeBase {
	private String customerId;
	private Customer customer;
	private ContactDetails contactDetails;
	private List<MobileProfile> mobileProfileList;
	private List<BankAccount> bankAccountList;
	private boolean approver;
	private boolean auditor;
	private boolean editCustomers;
	private boolean editAccount;
	private boolean editMobiles;
	private boolean deRegisterClient;
	private boolean deRegisterCustomerMobileProfile;
	private boolean deRegisterCustomerBankAccount;
	private boolean suspendClient;
	private boolean hotClientMobile;
	private boolean coldClientMobile;
	private boolean renderChangePrimary;
	private boolean approveMobile;
	
	
private static Logger LOG ;
	
	static{
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(ViewCustomerBean.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + ViewCustomerBean.class);
		}
	}
	
	
	
	private List<CustomerMerchant> customerMerchantList;
	private String fromPage;
	public String getCustomerId() {
		//if (customerId == null) {
		//LOG.debug(" before customer id "+customerId);
			customerId = (String) super.getRequestScope().get("customerId");
		//}
		//	LOG.debug("after before customer id "+customerId);
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public Customer getCustomer() {
		if (this.getCustomerId() != null) {
			ProfileServiceSOAPProxy proxy= new ProfileServiceSOAPProxy();
			Profile approverProfile=proxy.getProfileByUserName(getJaasUserName());
			String profileBranchId=approverProfile.getBranchId();
			//LOG.debug("Getting customer nw "+customerId);
			//LOG.debug(getCustomerId()+"Work done     ");
			//customer=null;
			customer = super.getCustomerService().findCustomerById(getCustomerId());
			approver = proxy.canDo(getJaasUserName(), "APPROVE");
			auditor = proxy.canDo(getJaasUserName(), "AUDIT");
			editAccount=proxy.canDo(getJaasUserName(), "EDITBANKACCOUNT");
			editCustomers=proxy.canDo(getJaasUserName(), "EDITCUSTOMER");
			editMobiles=proxy.canDo(getJaasUserName(), "EDITMOBILEPROFILE");
			deRegisterClient=proxy.canDo(getJaasUserName(), "DEREGISTERCUSTOMER");
			deRegisterCustomerMobileProfile=proxy.canDo(getJaasUserName(), "DEREGISTERCUSTOMERMOBILEPROFILE");
			deRegisterCustomerBankAccount=proxy.canDo(getJaasUserName(), "DEREGISTERCUSTOMERBANKACCOUNT");
			suspendClient=proxy.canDo(getJaasUserName(), "SUSPENDCUSTOMER");
			hotClientMobile=proxy.canDo(getJaasUserName(), "HOTMOBILE");
			coldClientMobile=proxy.canDo(getJaasUserName(), "COLDMOBILE");
			/*
			 * Uncomment latter
			 */
			boolean result=processRenderButton(profileBranchId, customer.getCustomerLastBranch());
			//LOG.debug("customer result is      "+result);
			customer.setRenderApproval(result);
		}
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public ContactDetails getContactDetails() {
		if (contactDetails == null && this.getCustomer() != null) {
			contactDetails = super.getContactService().findContactDetailsById(customer.getContactDetailsId());
		}
		return contactDetails;
	}
	public void setContactDetails(ContactDetails contactDetails) {
		this.contactDetails = contactDetails;
	}
	public List<MobileProfile> getMobileProfileList() {
		if (this.getCustomerId() != null ){
			ProfileServiceSOAPProxy proxy= new ProfileServiceSOAPProxy();
			CustomerServiceSOAPProxy customerProxy = new CustomerServiceSOAPProxy(); 
			Profile capturerProfile = proxy.getProfileByUserName(getJaasUserName()); 
			List<MobileProfile> mobileList= new ArrayList<MobileProfile>();
			mobileProfileList = new ArrayList<MobileProfile>();
			mobileList = customerProxy.getMobileProfileByCustomer(getCustomerId()); 
			for(MobileProfile mobile  : mobileList){
				boolean result=processRenderButton(capturerProfile.getBranchId(), mobile.getMobileProfileEditBranch());
				//LOG.debug("----------------mobileNumber"+mobile.getMobileNumber()+"----------result----------"+result);
				mobile.setMobileProfileRenderApproval(result);
				mobileProfileList.add(mobile);
				//add code to set boolean value to render values
				//LOG.debug("   mobile hottable "+mobile.isHottable() +"        mobile coldable  "+mobile.isColdable()+ "  Mobile Number "+mobile.getMobileNumber());
			}
		}
		return mobileProfileList;
	}
	public void setMobileProfileList(List<MobileProfile> mobileProfileList) {
		this.mobileProfileList = mobileProfileList;
	}
	
	public List<BankAccount> getBankAccountList() {
		//System.out.println(">>>>>bank account list >>>>>>"+bankAccountList);
		//System.out.println(">>>>>bank account list >>>>>>"+bankAccountList.size());
		if ( this.getCustomerId() != null  ){
			ProfileServiceSOAPProxy proxy= new ProfileServiceSOAPProxy();
			BankServiceSOAPProxy bankService= new BankServiceSOAPProxy();
			Profile capturerProfile=proxy.getProfileByUserName(getJaasUserName());
			List<BankAccount> bankAccList;
			bankAccountList = new ArrayList<BankAccount>();
			try {
				bankAccList = bankService.getBankAccountByAccountHolderIdAndOwnerType(this.getCustomerId(), 
						OwnerType.CUSTOMER);
				for(BankAccount account : bankAccList){
					boolean result=processRenderButton(capturerProfile.getBranchId(), account.getBankAccountLastBranch());
					//LOG.debug(" Bank account value      accountNumber  "+account.getAccountNumber());
					account.setBankAccRenderApprovable(result);
					/*if(BankAccountStatus.AWAITING_APPROVAL.equals(account.getStatus())) {
						account.setApprovable(true);
					}*/
					bankAccountList.add(account);
				}
			} catch (Exception e) {
				
			}
		}
		return bankAccountList;
	}
	public void setBankAccountList(List<BankAccount> bankAccountList) {
		this.bankAccountList = bankAccountList;
	}
	
	
	public String ok() {
		super.gotoPage("/csr/searchCustomer.jspx");
		return "ok";
	}
	
	 
		public void setFromPage(String fromPage) {
			this.fromPage = fromPage;
		}

		/**
		 * @return the fromPage
		 */
		public String getFromPage() {
			this.fromPage=(String)super.getRequestScope().get("fromPage");
			return fromPage;
		}
	
	@SuppressWarnings("unchecked")
	public String edit() {
		super.getRequestScope().put("customerId", super.getRequestParam().get("customerId"));
		this.setFieldsToNull();
		
		super.gotoPage("/csr/editCustomer.jspx");
		return "edit";
	}
	
	public String approveCustomer() {
		String customerId = (String) super.getRequestParam().get("customerId");
		Customer c = super.getCustomerService().findCustomerById(customerId);
		c.setStatus(CustomerStatus.ACTIVE);
		try {
			customer = super.getCustomerService().approveCustomer(c, super.getJaasUserName());
			MessageSync.populateAndSync(c, MessageAction.CREATE);
			super.setInformationMessage("Customer details have been successfully approved.Please proceed to verify the mobile profiles and bank accounts");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "viewCustomer";
	}
	
	public String rejectCustomer() {
		Customer c = super.getCustomerService().findCustomerById(customerId);
		c.setStatus(CustomerStatus.DISAPPROVED);
		try {
			customer = super.getCustomerService().rejectCustomer(c, super.getJaasUserName());
			super.setInformationMessage("Customer registration has been successfully disapproved.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "rejectCustomer";
	}
	
	public String approveAll() {
		String customerId = (String) super.getRequestParam().get("customerId");
		customer = super.getCustomerService().findCustomerById(customerId);
		customer.setStatus(CustomerStatus.ACTIVE);
		try {
			customer = super.getCustomerService().approveCustomer(customer, super.getJaasUserName());
			MessageSync.populateAndSync(customer, MessageAction.CREATE);
			
			//Sycning Msg
			//MessageSync.populateAndSync(this.getContactDetails(), MessageAction.CREATE);
			
			List<MobileProfile> profiles = super.getCustomerService().getMobileProfileByCustomer(customerId);
			for (MobileProfile profile: profiles) {
				profile.setStatus(MobileProfileStatus.ACTIVE);
				super.getCustomerService().approveMobileNumber(profile, getJaasUserName());
				String notification = this.getNotification(profile);
				MessageSync.populateAndSync(profile, MessageAction.CREATE, notification);
				mobileProfileList = super.getCustomerService().getMobileProfileByCustomer(customerId);
			}
			List<BankAccount> accounts = super.getBankService().getBankAccountByAccountHolderIdAndOwnerType(customerId, 
					OwnerType.CUSTOMER);
			for (BankAccount account: accounts) {
				account.setStatus(BankAccountStatus.ACTIVE);
				super.getBankService().editBankAccount(account, super.getJaasUserName());
				MessageSync.populateAndSync(account, MessageAction.CREATE);
			}
			super.setInformationMessage("Customer registration has been successfully approved. All mobile profiles and bank accounts have been successfully approved.");
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
			List<BankAccount> accounts =  super.getBankService().getBankAccountByAccountHolderIdAndOwnerType(customerId, 
					OwnerType.CUSTOMER);
			for (BankAccount account: accounts) {
				account.setStatus(BankAccountStatus.DISAPPROVED);
				 super.getBankService().rejectBankAccount(account, super.getJaasUserName());
			}
			super.setInformationMessage("Customer registration has been successfully disapproved.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "rejectAll";
	}
	
	public String approveMobileProfile() {
		String mobileProfileId = (String) super.getRequestParam().get("mobileProfileId");
		MobileProfile mobile = super.getCustomerService().findMobileProfileById(mobileProfileId);
		Customer c = mobile.getCustomer();
		if(!c.getStatus().name().equals(CustomerStatus.ACTIVE.name())){
			super.setErrorMessage("Approve customer details first ");
			return "failure";
		}
		mobile.setStatus(MobileProfileStatus.ACTIVE);
		try {
		mobile=	super.getCustomerService().approveMobileNumber(mobile, super.getJaasUserName());
			String notification = this.getNotification(mobile);
			MessageSync.populateAndSync(mobile, MessageAction.CREATE, notification);
			mobileProfileList = super.getCustomerService().getMobileProfileByCustomer(customerId);
			super.setInformationMessage("Mobile profile has been successfully approved.");
		} catch (Exception e) {
			super.setErrorMessage("Error occurred");
			e.printStackTrace();
		}
		return "approveMobileProfile";
	}
	
	public String resetMobileProfilePin() {
		String mobileProfileId = (String) super.getRequestParam().get("mobileProfileId");
		MobileProfile mobile = super.getCustomerService().findMobileProfileById(mobileProfileId);
		try {
			mobile = super.getCustomerService().resetMobileProfilePin(mobile, super.getJaasUserName());
			String notification = this.getNotification(mobile);
			MessageSync.populateAndSync(mobile, MessageAction.UPDATE, notification);
			mobileProfileList = super.getCustomerService().getMobileProfileByCustomer(customerId);
			super.setInformationMessage("Mobile profile pin has been successfully reset.");
		} catch (Exception e) {
			super.setErrorMessage("Error occurred");
			e.printStackTrace();
		}
		return "";
	}
	
	
	public String unLockMobile() {
		LOG.debug(" in unlock");
		String mobileProfileId = (String) super.getRequestParam().get("mobileProfileId");
		MobileProfile mobile = super.getCustomerService().findMobileProfileById(mobileProfileId);
		try {
			mobile.setStatus(MobileProfileStatus.ACTIVE);
			mobile = super.getCustomerService().unLockMobileProfile(mobile, getJaasUserName());
			MessageSync.populateAndSync(mobile, MessageAction.UPDATE);
			this.notifyUnLock(mobile);
			mobileProfileList = super.getCustomerService().getMobileProfileByCustomer(customerId);
			super.setInformationMessage("Mobile profile pin has been successfully unlocked.");
			LOG.debug("done unlocking   ");
			return "done3";
		} catch (Exception e) {
			super.setErrorMessage("Error occurred");
			e.printStackTrace();
		}
		return "unLocked";
	}
	
	
	
	public boolean isCanResetMobileProfilePin(){
		return super.getProfileService().canDo(getJaasUserName(), "resetMobileProfilePin");
	}
	
	
	public boolean isCanUnLockMobileProfile(){
		return super.getProfileService().canDo(getJaasUserName(), "canUnLockMobileProfile");
	}
	
	public String coldMobile(){
		CustomerServiceSOAPProxy customerService= new CustomerServiceSOAPProxy();
		String mobileProfileId = (String) super.getRequestParam().get("mobileProfileId");
		MobileProfile mobile = customerService.findMobileProfileById(mobileProfileId);
	
		try {
			mobile=customerService.coldMobileNumber(mobile, getJaasUserName());
			mobileProfileList = customerService.getMobileProfileByCustomer(getCustomerId());
			mobile.setStatus(MobileProfileStatus.ACTIVE);
			MessageSync.populateAndSync(mobile, MessageAction.UPDATE);
			super.setInformationMessage("Mobile profile has been successfully activated.");
		} catch (Exception e) {
			super.setErrorMessage("Error occurred");
			e.printStackTrace();
		}
		return "coldMobile";
	
	}
	public String hotMobile(){
		String mobileProfileId = (String) super.getRequestParam().get("mobileProfileId");
		MobileProfile mobile = super.getCustomerService().findMobileProfileById(mobileProfileId);
	
		try {
			mobile=super.getCustomerService().hotMobileNumber(mobile, getJaasUserName());
			mobileProfileList = super.getCustomerService().getMobileProfileByCustomer(getCustomerId());
			mobile.setStatus(MobileProfileStatus.HOT);
			MessageSync.populateAndSync(mobile, MessageAction.UPDATE);
			super.setInformationMessage("Mobile profile has been successfully halted.");
		} catch (Exception e) {
			super.setErrorMessage("Error occurred");
			e.printStackTrace();
		}
		return "hotMobile";
	}
	
	public String rejectMobileProfile() {
		String mobileProfileId = (String) super.getRequestParam().get("mobileProfileId");
		MobileProfile mobile = super.getCustomerService().findMobileProfileById(mobileProfileId);
		mobile.setStatus(MobileProfileStatus.DISAPPROVED);
		try {
			super.getCustomerService().rejectMobileNumber(mobile, super.getJaasUserName());
			mobileProfileList = super.getCustomerService().getMobileProfileByCustomer(customerId);
			super.setInformationMessage("Mobile profile has been successfully disapproved.");
		} catch (Exception e) {
			super.setErrorMessage("Error occurred");
			e.printStackTrace();
		}
		return "rejectMobileProfile";
	}
	
	public String approveBankAccount() {
		String accountId = (String) super.getRequestParam().get("accountId");
		BankAccount account = super.getBankService().findBankAccountById(accountId);
		Customer c = super.getCustomerService().findCustomerById(account.getAccountHolderId());
		if(!c.getStatus().name().equals(CustomerStatus.ACTIVE.name())){
			super.setErrorMessage("Approve customer details first ");
			return "failure";
		}
		
		List<MobileProfile> mList = c.getMobileProfiles();
		for(MobileProfile mobile : mList){
			if(mobile.isPrimary() && !mobile.getStatus().name().equals(MobileProfileStatus.ACTIVE.name())){
				super.setErrorMessage("Approve Mobile Profile first ");
				return "failure";
			}
		}
		account.setStatus(BankAccountStatus.ACTIVE);
		try {
			account=super.getBankService().approveBankAccount(account, super.getJaasUserName());
			account.setStatus(BankAccountStatus.ACTIVE);
			MessageSync.populateAndSync(account, MessageAction.CREATE);
			bankAccountList = super.getBankService().getBankAccountByAccountHolderIdAndOwnerType(customerId, 
					OwnerType.CUSTOMER);
			super.setInformationMessage("Bank Account has been successfully approved.");
			
		} catch (Exception e) {
			super.setErrorMessage("Error occurred");
			e.printStackTrace();
		}
		return "approveBankAccount";
	}
	
	public String rejectBankAccount() {
		String accountId = (String) super.getRequestParam().get("accountId");
		BankAccount account = super.getBankService().findBankAccountById(accountId);
		account.setStatus(BankAccountStatus.DISAPPROVED);
		try { 
			super.getBankService().rejectBankAccount(account, super.getJaasUserName());
			bankAccountList = super.getBankService().getBankAccountByAccountHolderIdAndOwnerType(customerId, 
					OwnerType.CUSTOMER);
			super.setInformationMessage("Bank Account has been successfully disapproved.");
			
		} catch (Exception_Exception e) {
			super.setErrorMessage("Error occurred");
			e.printStackTrace();
		}
		return "rejectBankAccount";
	}
	
	public boolean checkProfileAccessRight(String accessRight) {
		ProfileServiceSOAPProxy proxy= new ProfileServiceSOAPProxy();
		Profile profile = proxy.getProfileByUserName(super.getJaasUserName());
		List<RoleAccessRight> parList = proxy.getRoleAccessRightByRole(profile.getRole().getId());
		for (RoleAccessRight par: parList) {
			//LOG.debug(" Action name     "+par.getAccessRight().getActionName());
			if (par.getAccessRight().getActionName().equals(accessRight)) {
				return par.isCanDo();
				
			}
		}
		return false;
	}
	public void setApprover(boolean approver) {
		this.approver = approver;
	}
	public boolean isApprover() {
		//LOG.debug("Print boolean value "+this.approver);
		return approver;
		
	}
	
	public boolean isAuditor() {
		//LOG.debug("Print boolean value auditor "+this.auditor);
		return auditor;
	}
	
	public void setAuditor(boolean auditor) {
		this.auditor = auditor;
	}
	
	public void setCustomerMerchantList(List<CustomerMerchant> customerMerchantList) {
		this.customerMerchantList = customerMerchantList;
	}
	public List<CustomerMerchant> getCustomerMerchantList() {
		if (customerMerchantList == null || customerMerchantList.isEmpty()) {
			if (this.getCustomerId() != null) {
				try {
					customerMerchantList = super.getMerchantService().getCustomerMerchantByCustomerId(customerId);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				customerMerchantList = new ArrayList<CustomerMerchant>(); 
			}
		}
		return customerMerchantList;
	}
	public String editMobileProfile() {
		gotoPage("/csr/editMobileProfile.jspx");
		return "editMobileProfile";
	}
	
	public String addMobileProfile(){
		gotoPage("/csr/addMobileProfile.jspx");
		return "";
	}
	
	public String configureAlert(){
		gotoPage("/csr/configureAlerts.jspx");
		return "";
	}
	
	public String editBankAccount() {
		gotoPage("/csr/editBankAccount.jspx");
		return "editBankAccount";
	}
	
	public String addBankAccount(){
		LOG.debug("Add bankAccount navigationg");
		gotoPage("/csr/addBankAccount.jspx");
		return "addBankAccount";
	}
	
	public String viewLogs() {
		gotoPage("/csr/viewLogs.jspx");
		return "viewLogs";
	}
	
	private void notifyCustomer(MobileProfile mp) throws Exception {
		
		String code = EncryptAndDecrypt.decrypt(mp.getSecretCode(), mp.getMobileNumber());
		String message = "Welcome to ZB Mobile Banking.[nl]Your password is " + code + ".[nl]You can now send your requests to 440.";

		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setTransactionType(TransactionType.CUSTOMER_ACTIVATION);
		requestInfo.setSourceMobile(mp.getMobileNumber());
		ResponseInfo responseInfo = new ResponseInfo(message, ResponseCode.E000, requestInfo, ResponseType.NOTIFICATION, 0L, 0L, 0L, null);
//		ResponseInfo responseInfo = new ResponseInfo(response, bankResp.getResponseCode(), requestInfo, ResponseType.NOTIFICATION);
		LOG.debug("Sending REPLY (to SWITCH) for transaction...." + mp.getMobileNumber());
		MessageSender.send(EWalletConstants.FROM_EWALLET_TO_SWITCH_QUEUE, responseInfo, ResponseType.NOTIFICATION.toString());
	}
	
    private String getNotification(MobileProfile mp) throws Exception {
		
		String code = EncryptAndDecrypt.decrypt(mp.getSecretCode(), mp.getMobileNumber());
		String message = "Welcome to ZB Mobile Banking.[nl]Your password is " + code + ".[nl]You can now send your requests to 440.";
		return message;
	}
	
	private void notifyReset(MobileProfile mp) throws Exception {
		
		String code = EncryptAndDecrypt.decrypt(mp.getSecretCode(), mp.getMobileNumber());
		String message = "Your password has been reset to " + code + ".[nl]You can now send your requests to 440.";
		
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setTransactionType(TransactionType.CUSTOMER_ACTIVATION);
		requestInfo.setSourceMobile(mp.getMobileNumber());
		ResponseInfo responseInfo = new ResponseInfo(message, ResponseCode.E000, requestInfo, ResponseType.NOTIFICATION, 0L, 0L, 0L, null);
//		ResponseInfo responseInfo = new ResponseInfo(response, bankResp.getResponseCode(), requestInfo, ResponseType.NOTIFICATION);
		LOG.debug("Sending REPLY (to SWITCH) for transaction...." + mp.getMobileNumber());
		MessageSender.send(EWalletConstants.FROM_EWALLET_TO_SWITCH_QUEUE, responseInfo, ResponseType.NOTIFICATION.toString());
	}
	
	private void notifyUnLock(MobileProfile mp) throws Exception {
		String message = "Your mobile profile has been unlocked. .[nl]You can now send your requests to 440.";
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setTransactionType(TransactionType.CUSTOMER_ACTIVATION);
		requestInfo.setSourceMobile(mp.getMobileNumber());
		ResponseInfo responseInfo = new ResponseInfo(message, ResponseCode.E000, requestInfo, ResponseType.NOTIFICATION, 0L, 0L, 0L, null);
//		ResponseInfo responseInfo = new ResponseInfo(response, bankResp.getResponseCode(), requestInfo, ResponseType.NOTIFICATION);
		LOG.debug("Sending REPLY (to SWITCH) for transaction...." + mp.getMobileNumber());
		MessageSender.send(EWalletConstants.FROM_EWALLET_TO_SWITCH_QUEUE, responseInfo, ResponseType.NOTIFICATION.toString());
	}
	
	
	
	public String suspendCustomer(){
		//LOG.debug("Suspending ");
		CustomerServiceSOAPProxy proxy= new CustomerServiceSOAPProxy();
		//LOG.debug("suspending");
		customer.setStatus(CustomerStatus.SUSPENDED);
		try {
		Customer customerResult=proxy.suspendCustomer(customer, getJaasUserName());
		customerResult.setStatus(CustomerStatus.SUSPENDED);
		MessageSync.populateAndSync(customerResult, MessageAction.UPDATE);
		//LOG.debug("Customer status "+customerResult.getStatus());
			super.setInformationMessage("Customer has been successfully suspended.");
		} catch (Exception e) {
			e.printStackTrace();
			LOG.debug("eror has occurred");
			super.setErrorMessage("An error has occurred. Operation aborted");
			
		}
		
		return "";
	}
	
	
	public String activateCustomer(){
		CustomerServiceSOAPProxy proxy= new CustomerServiceSOAPProxy();
		try {
			customer=proxy.activateCustomer(customer, getJaasUserName());
			customer.setStatus(CustomerStatus.ACTIVE);
			MessageSync.populateAndSync(customer, MessageAction.UPDATE);
			LOG.debug("Customer has been successfully activated.");
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage("An error has occurred. Operation aborted");
		}
		
		return "";
	}
	
	public String deRegisterCustomer(){
		CustomerServiceSOAPProxy proxy= new CustomerServiceSOAPProxy();
		if(this.isAccountBalanceAvailable(getCustomer())){
			LOG.debug("Account has balance");
			super.setErrorMessage("Cannot deregister Customer: there is still money in customer account");
			return "failure";
		}
		try {
			
			List <MobileProfile> mobiles=super.getCustomerService().getMobileProfileByCustomer(customer.getId());
			
			//LOG.debug("Customer   "+getCustomer());
			customer=proxy.deregisterCustomer(getCustomer(), getJaasUserName());
			customer.setStatus(CustomerStatus.DELETED);
			MessageSync.populateAndSync(customer, MessageAction.DELETE);
			LOG.debug("Done with customer now working with bank accounts");
			syncMobileProfileDeRegistrations(mobiles);
			BankServiceSOAPProxy bankService=new BankServiceSOAPProxy();
			//LOG.debug("nw acting on banks");
			List<BankAccount> deletedAccs=bankService.deRegisterBankAccountsByOwnerId(customer.getId(), getJaasUserName());
			syncBankDeletes(deletedAccs);
			//LOG.debug("All done");
			super.setInformationMessage("Customer has been successfully deregistered.");
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage("An error has occurred. Operation aborted");
		}
		
		return "";
	}
	
	
	
	public void syncBankDeletes(List<BankAccount> deletedAccs) throws Exception{
		for(BankAccount account: deletedAccs){
			account.setStatus(BankAccountStatus.DELETED);
		//	LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>BBBBBBBBB>>>> account number"+account.getAccountNumber());
		//	LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>BBBBBBBB>>>><> status"+account.getStatus());
		//	LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>BBBBBBBB>>>><> id "+account.getStatus());
			MessageSync.populateAndSync(account, MessageAction.DELETE);
		}
	}
	
	
	public void syncMobileProfileDeRegistrations(List <MobileProfile> mobiles) throws Exception{
		for(MobileProfile mobile : mobiles ){
			mobile.setStatus(MobileProfileStatus.DELETED);
			LOG.debug("MMMMM>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>mobile number "+mobile.getMobileNumber());
			LOG.debug("MMMMMMMMMMMMMMMMM>>>>>>>>>>>>>>>>>>>status>>>>>>>>>>>>>>"+mobile.getStatus());
			LOG.debug("MMMMMMMMMMMMMMMMM>>>>>>>>>>>>>>>>>>>movile id>>>>>>>>>>>>>>"+mobile.getId());
				MessageSync.populateAndSync(mobile, MessageAction.DELETE);
			
		}
	}
	
	
	public String addCustomerMerchant() {
		gotoPage("/csr/createCustomerMerchant.jspx");
		return "addCustomerMerchant";
	}
	
	public String editCustomerMerchant() {
		gotoPage("/csr/editCustomerMerchant.jspx");
		return "editCustomerMerchant";
	}
	
	public String approveCustomerMerchant() {
		String customerMerchantId = (String) super.getRequestParam().get("customerMerchantId");
		CustomerMerchant customerMerchant;
		try {
		
			customerMerchant = super.getMerchantService().findCustomerMerchantById(customerMerchantId);
			this.approveOrReject(customerMerchant, CustomerMerchantStatus.ACTIVE);
			
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage("Customer Merchant " + customerMerchant.getBankMerchant().getMerchant().getShortName() + " approved successfully.");
		return "approveCustomerMerchant";
	}
	
	public String approveAllCustomerMerchants() {
		try {
			List<CustomerMerchant> customerMerchantList = super.getMerchantService().getCustomerMerchantByBankId(getCustomerId());
			if (customerMerchantList != null) {
				for (CustomerMerchant customerMerchant: customerMerchantList) {
					if (CustomerMerchantStatus.AWAITING_APPROVAL.equals(customerMerchant.getStatus())) {
						this.approveOrReject(customerMerchant, CustomerMerchantStatus.ACTIVE);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage("Bank Merchants approved successfully.");
		return "approveAllBankMerchants";
	}
		
	private CustomerMerchant approveOrReject(CustomerMerchant customerMerchant, CustomerMerchantStatus status) throws Exception {
		
		customerMerchant.setStatus(status);
		super.getMerchantService().editCustomerMerchant(customerMerchant, super.getJaasUserName());
		//MessageSync.populateAndSync(customerMerchant, MessageAction.UPDATE);
		
		return customerMerchant;
	}
	
	public String rejectCustomerMerchant() {
		String customerMerchantId = (String) super.getRequestParam().get("customerMerchantId");
		CustomerMerchant customerMerchant;
		try {
		
			customerMerchant = super.getMerchantService().findCustomerMerchantById(customerMerchantId);
			this.approveOrReject(customerMerchant, CustomerMerchantStatus.DISAPPROVED);
			
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage("Customer Merchants rejected successfully.");
		return "rejectCustomerMerchant";
	}
	
	public String deleteCustomerMerchant() {
		String customerMerchantId = (String) super.getRequestParam().get("customerMerchantId");
		CustomerMerchant customerMerchant = super.getMerchantService().findCustomerMerchantById(customerMerchantId);
		try {
			super.getMerchantService().deleteCustomerMerchant(customerMerchant, getJaasUserName());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.setInformationMessage("Customer merchant " + customerMerchant.getBankMerchant().getMerchant().getShortName() + " deleted successfully.");
		return "deleteCustomerMerchant";
	}
	
	public String back(){
		gotoPage("/csr/"+this.getFromPage());
		//gotoPage("/csr/customerSearch.jspx");
		return "";
	}
	
	public boolean processRenderButton(String profileBranchId, String lastBranchId){
		//LOG.debug("Profile Branch id  "+profileBranchId  +"        lastbranchId "+lastBranchId);
		//LOG.debug("bbbddd>>>>>>>>>>>>>>>>>>>>>>>>>>>value   "+profileBranchId.equalsIgnoreCase(lastBranchId));
		if(lastBranchId==null){
			return false;
		}
			if(profileBranchId.equalsIgnoreCase(lastBranchId)){
				return true;
			}
			
			return false;
		}
	
	
	
	
	
	
	
	public String deRegisterMobileProfile(){
		String mobileProfileId = (String) super.getRequestParam().get("mobileProfileId");
		MobileProfile mobileProfile = super.getCustomerService().findMobileProfileById(mobileProfileId);
		mobileProfile.setStatus(MobileProfileStatus.DELETED);
	
		try {
			/*
			 * 
			 * add logic to work on this	
			 */
			//super.getCustomerService().hotMobileNumber(mobile, getJaasUserName());
			mobileProfile=super.getCustomerService().deRegisterMobileProfile(mobileProfile, getJaasUserName());
			LOG.debug("sync mobile profile>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> "+mobileProfile.getStatus().toString());
			snycMobileProfileDelete(mobileProfile);
			mobileProfileList = super.getCustomerService().getMobileProfileByCustomer(getCustomerId());
			super.setInformationMessage("Mobile profile has been successfully de-registered.");
		} catch (Exception e) {
			super.setErrorMessage("Error occurred");
			e.printStackTrace();
		}
		
		return "deRegisterMobileProfile";
	}
	
	public String deRegisterBankAccount(){
		
		if(this.isAccountBalanceAvailable(getCustomer())){
			super.setErrorMessage("Cannot deregister account: there is still money in customer account");
			return "failure";
		}
		String accountId = (String) super.getRequestParam().get("accountId");
		BankAccount account = super.getBankService().findBankAccountById(accountId);
		account.setStatus(BankAccountStatus.DELETED);
		try { 
			/*
			 * add logic for this later
			 */
			//super.getBankService().rejectBankAccount(account, super.getJaasUserName());
			account=super.getBankService().deRegisterBankAccount(account, getJaasUserName());
			MessageSync.populateAndSync(account, MessageAction.DELETE);
			bankAccountList = super.getBankService().getBankAccountByAccountHolderIdAndOwnerType(customerId, 
					OwnerType.CUSTOMER);
			super.setInformationMessage("Bank Account has been successfully de-registered.");
			
		} catch (Exception e) {
			super.setErrorMessage("Error occurred");
			e.printStackTrace();
		}
	
		return "deRegisterBankAccounk";
	}
	
	
	
	public void snycMobileProfileDelete(MobileProfile mobileProfile) throws Exception{
		LOG.debug(".......2 in sync method :::::::::::::::::"+mobileProfile.getMobileNumber());
		zw.co.esolutions.mcommerce.msg.MobileProfile result=MessageSync.populateAndSync(mobileProfile, MessageAction.DELETE);
		LOG.debug("...............2 and the result is "+result);
	}
	public boolean isEditCustomers() {
		return editCustomers;
	}
	public void setEditCustomers(boolean editCustomer) {
		this.editCustomers = editCustomer;
	}
	public boolean isEditAccount() {
		return editAccount;
	}
	public void setEditAccount(boolean editAccount) {
		this.editAccount = editAccount;
	}
	public boolean isEditMobiles() {
		return editMobiles;
	}
	public void setEditMobile(boolean editMobile) {
		this.editMobiles = editMobile;
	}
	public boolean isDeRegisterClient() {
		return deRegisterClient;
	}
	public void setDeRegisterClient(boolean deRegisterClient) {
		this.deRegisterClient = deRegisterClient;
	}
	public boolean isDeRegisterCustomerMobileProfile() {
		return deRegisterCustomerMobileProfile;
	}
	public void setDeRegisterCustomerMobileProfile(boolean deRegisterCustomerMobileProfile) {
		this.deRegisterCustomerMobileProfile = deRegisterCustomerMobileProfile;
	}
	public boolean isDeRegisterCustomerBankAccount() {
		return deRegisterCustomerBankAccount;
	}
	public void setDeRegisterCustomerBankAccount(boolean deRegisterCustomerBankAccount) {
		this.deRegisterCustomerBankAccount = deRegisterCustomerBankAccount;
	}
	public boolean isSuspendClient() {
		return suspendClient;
	}
	public void setSuspendClient(boolean suspendClient) {
		this.suspendClient = suspendClient;
	}
	public void setEditMobiles(boolean editMobiles) {
		this.editMobiles = editMobiles;
	}
	public boolean isHotClientMobile() {
		return hotClientMobile;
	}
	public void setHotClientMobile(boolean hotClientMobile) {
		this.hotClientMobile = hotClientMobile;
	}
	public boolean isColdClientMobile() {
		return coldClientMobile;
	}
	public void setColdClientMobile(boolean coldClientMobile) {
		this.coldClientMobile = coldClientMobile;
	}
	
	@SuppressWarnings("unchecked")
	public String changePrimaryAccountValue() {
		String id = (String) super.getRequestParam().get("bankAccountId");
		Profile profile = null;
		try {
			if (id != null) {
				profile = super.getProfileService().getProfileByUserName(super.getJaasUserName());
				BankAccount bankAccount = super.getBankService().findBankAccountById(id);
				List<BankAccount> otherAccounts = getBankService().getBankAccountByAccountHolderIdAndOwnerType(
						bankAccount.getAccountHolderId(), bankAccount.getOwnerType());
				if (bankAccount.isPrimaryAccount()) {
					return "nothing";
				} else {
					bankAccount.setPrimaryAccount(true);
					bankAccount.setStatus(BankAccountStatus.AWAITING_APPROVAL);
					
					if(otherAccounts !=null && !otherAccounts.isEmpty()){
						for(BankAccount ba : otherAccounts){
							if(!ba.getId().equals(id)){
								if(ba.isPrimaryAccount()){
									ba.setPrimaryAccount(false);
									ba.setBankAccountLastBranch(profile.getBranchId());
									ba.setStatus(BankAccountStatus.AWAITING_APPROVAL);
									ba = super.getBankService().editBankAccount(ba, getJaasUserName());
									//System.out.println("########################################>>>>>>>>>>> Other Accounts Owner Type = "+ba.getOwnerType());
								}
							}
						}
					}
				}
				bankAccount.setBankAccountLastBranch(profile.getBranchId());
				bankAccount = super.getBankService().editBankAccount(bankAccount, getJaasUserName());
				//System.out.println("##########################################>>>>>>>>>>>>> Changed bank acc Owner Type = "+bankAccount.getOwnerType());
				
				customerId = bankAccount.getAccountHolderId();
			
			} else {
				super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
				return "failure";
			}
		} catch (Exception e) {
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.getRequestScope().put("customerId", customerId);
		super.setInformationMessage("Bank accounts updated successfully.");
		
		return "changeCanDoValue";
	}
	
	public boolean isRenderChangePrimary() {
		ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
		try {
			renderChangePrimary = ps.canDo(super.getJaasUserName(), "ACCOUNTAPPROVAL");
		} catch (Exception e) {
			
		}
		
		return renderChangePrimary;
	}

	public void setRenderChangePrimary(
			boolean renderChangePrimary) {
		this.renderChangePrimary = renderChangePrimary;
	}
	
	private void setFieldsToNull() {
		this.setContactDetails(null);
		this.setCustomer(null);
		this.setCustomerId(null);
		this.setMobileProfileList(null);
		this.setBankAccountList(null);
	}
	public boolean isApproveMobile() {
		
		return approveMobile;
	}
	public void setApproveMobile(boolean approveMobile) {
		this.approveMobile = approveMobile;
	}
	
	private boolean isAccountBalanceAvailable(Customer customer){
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		List<BankAccount> bankAccountList = getBankAccountList();
		if(bankAccountList.isEmpty()){
			LOG.debug("NO accounts found");
			return false;
		}
		try{
			for(BankAccount ba:bankAccountList){
				AccountBalance balance = bankService.getClosingBalance(ba.getId(),DateUtil.convertToXMLGregorianCalendar(new Date()));
				if(balance == null || balance.getId() == null){
					LOG.debug("No balances found ");
					continue;
				}
				if(balance.getAmount() > 0L){
					LOG.debug("Account has balance ");
					return true;
				}
			}
		}catch (Exception e) {
			
		}
		LOG.debug("NO accounts found");
		return false;
	}
	
}
