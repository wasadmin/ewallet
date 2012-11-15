package zw.co.esolutions.ewallet.process;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountClass;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountLevel;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountStatus;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountType;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetailsServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.CustomerAutoRegStatus;
import zw.co.esolutions.ewallet.customerservices.service.CustomerClass;
import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.CustomerStatus;
import zw.co.esolutions.ewallet.customerservices.service.Exception_Exception;
import zw.co.esolutions.ewallet.customerservices.service.Gender;
import zw.co.esolutions.ewallet.customerservices.service.MaritalStatus;
import zw.co.esolutions.ewallet.customerservices.service.MobileNetworkOperator;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfileStatus;
import zw.co.esolutions.ewallet.msg.MessageAction;
import zw.co.esolutions.ewallet.msg.RequestInfo;
import zw.co.esolutions.ewallet.sms.MessageSender;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.NumberUtil;
import zw.co.esolutions.ewallet.util.SystemConstants;
import zw.co.esolutions.mcommerce.msg.BankAccount.AccountClass;
import zw.co.esolutions.mcommerce.msg.BankAccount.AccountLevel;
import zw.co.esolutions.mcommerce.msg.BankAccount.AccountType;
import zw.co.esolutions.mcommerce.msg.BankAccount.Status;
import zw.co.esolutions.mcommerce.msg.BankRegistrationMessage;
import zw.co.esolutions.mcommerce.msg.BankRegistrationMessage.MessageCommand;
import zw.co.esolutions.mcommerce.msg.BankRegistrationMessageDocument;

/**
 * Session Bean implementation class AutoRegServiceImpl
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class AutoRegServiceImpl implements AutoRegService {

    /**
     * Default constructor. 
     */
    public AutoRegServiceImpl() {

    }
    private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(AutoRegServiceImpl.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + AutoRegServiceImpl.class);
		}
	}
	
	public ResponseCode processNonHolderAutoRegistration(RequestInfo requestInfo) {
		
		Customer customer = null;
		ContactDetails contactDetails = null;
		MobileProfile mobileProfile = null;
		BankAccount bankAccount = null;
		
		try {
			
			//Process Econet target mobile
			if (zw.co.esolutions.ewallet.enums.MobileNetworkOperator.ECONET.equals(NumberUtil.getMNO(requestInfo.getTargetMobile()))) {	
				LOG.debug("Verify non-existence of account");
				BankAccount account = new BankServiceSOAPProxy()
					.getBankAccountByAccountNumberAndOwnerType(requestInfo.getTargetMobile(), OwnerType.CUSTOMER);
				if (account != null && account.getId() != null) {
					LOG.debug("This account already exists");
					return ResponseCode.E840;
				} else {
					LOG.debug("Verification successful");
							
					LOG.debug("&&&&&&&&&      BEGIN AUTO-REG");
					customer = this.processCustomerAutoReg(requestInfo);
					LOG.debug("CUSTOMER AUTO-REG SUCCESSFUL");
					
					contactDetails = this.processContactDetailsAutoReg(customer);
					LOG.debug("CONTACT DETAILS AUTO-REG SUCCESSFUL");
					
					mobileProfile = this.processMobileProfileAutoReg(customer, requestInfo);
					LOG.debug("MOBILEPROFILE AUTO-REG SUCCESSFUL");
			
					bankAccount = this.processBankAccountAutoReg(customer, requestInfo, mobileProfile);
					LOG.debug("BANKACCOUNT AUTO-REG SUCCESSFUL");
					
					this.synchAutoRegDetails(customer, mobileProfile, bankAccount);
			
					LOG.debug("&&&&&&&&&      END AUTO-REG: Success");
				}
			} else {
				LOG.debug("******* Not Econet Number, Skip auto-reg");
				return ResponseCode.E839;
			}
				
		} catch (Exception e) {
			LOG.error("AUTO-REG THREW EXCEPTION   " + e.getMessage());
			
			this.rollbackAutoReg(customer, contactDetails, mobileProfile, bankAccount);
			
			return ResponseCode.E505;	//General Transaction Error
		}
		return ResponseCode.E000;
	}
	
	@Override
	public Customer processCustomerAutoReg(RequestInfo requestInfo) throws Exception {
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		MobileProfile sourceMobileProfile = customerService.findMobileProfileById(requestInfo.getSourceMobileProfileId());
		if(sourceMobileProfile == null || sourceMobileProfile.getId() == null){
			throw new Exception("Source Mobile not found.");
		}
		Customer customer = new Customer();
		customer.setBranchId(sourceMobileProfile.getCustomer().getBranchId());
		LOG.debug("&&&&&&&&             Found branchId > " + customer.getBranchId());
		customer.setCustomerClass(CustomerClass.REGULAR);
		customer.setCustomerLastBranch(customer.getBranchId());
		customer.setFirstNames(SystemConstants.AUTOREG_NAME);
		customer.setGender(Gender.UNSPECIFIED);
		customer.setLastName(SystemConstants.AUTOREG_NAME);
		customer.setMaritalStatus(MaritalStatus.UNSPECIFIED);
		customer.setStatus(CustomerStatus.ACTIVE);
		customer.setCustomerAutoRegStatus(CustomerAutoRegStatus.YES);
		customer.setTitle("NONE");
		customer.setDateOfBirth(sourceMobileProfile.getDateCreated());
		
		try {
			customer = customerService.createCustomer(customer, EWalletConstants.SYSTEM);
			LOG.debug("Customer auto reg successful");
			
			return customer;
		} catch (Exception_Exception e) {
			LOG.debug("Create customer failed " + e.getMessage());
			throw e;
		}catch (Exception e) {
			LOG.debug("Create customer failed " + e.getMessage());
			throw e;
		}
		
	}

	@Override
	public MobileProfile processMobileProfileAutoReg(Customer customer, RequestInfo requestInfo) throws Exception {
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		MobileProfile sourceMobileProfile = customerService.findMobileProfileById(requestInfo.getSourceMobileProfileId());
		if(sourceMobileProfile == null || sourceMobileProfile.getId() == null){
			throw new Exception("Source Mobile not found.");
		}
		MobileProfile mobileProfile = new MobileProfile();
		mobileProfile.setBankId(sourceMobileProfile.getBankId());
		mobileProfile.setBranchId(customer.getBranchId());
		mobileProfile.setCustomer(customer);
		mobileProfile.setMobileNumber(requestInfo.getTargetMobile());
		mobileProfile.setMobileProfileEditBranch(mobileProfile.getBranchId());
		mobileProfile.setNetwork(MobileNetworkOperator.fromValue(NumberUtil.getMNO(mobileProfile.getMobileNumber()).name()));
		mobileProfile.setPrimary(true);
		mobileProfile.setStatus(MobileProfileStatus.ACTIVE);
		try {
			mobileProfile = customerService.createMobileProfile(mobileProfile, SystemConstants.SOURCE_APPLICATION_BANK, EWalletConstants.SYSTEM);
			LOG.debug("Mobile Profile auto reg successful");
			return mobileProfile;
		} catch (Exception_Exception e) {
			LOG.debug("Create mobile profile failed " + e.getMessage());
			throw e;
		} catch (Exception e) {
			LOG.debug("Create mobile profile failed " + e.getMessage());
			throw e;
		}
	}

	@Override
	public BankAccount processBankAccountAutoReg(Customer customer, RequestInfo requestInfo, MobileProfile mobileProfile) throws Exception {
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		BankBranch bankBranch = bankService.findBankBranchById(mobileProfile.getBranchId());
		if(bankBranch == null || bankBranch.getId()==null){
			LOG.debug("BankAccount Auto Reg Failed : Reason BankBranch not found...");
			throw new Exception("BankBranch not found.");
		}
		
		BankAccount bankAccount = new BankAccount();
		bankAccount.setAccountClass(BankAccountClass.NONE);
		bankAccount.setAccountHolderId(customer.getId());
		bankAccount.setAccountName(customer.getLastName() + " " + customer.getFirstNames());
		bankAccount.setAccountNumber(requestInfo.getTargetMobile());
		bankAccount.setBankAccountLastBranch(mobileProfile.getBranchId());
		bankAccount.setBankReferenceId(mobileProfile.getBankId());
		bankAccount.setBranch(bankBranch);
		bankAccount.setCode("PRI");
		bankAccount.setLevel(BankAccountLevel.INDIVIDUAL);
		bankAccount.setOwnerType(OwnerType.CUSTOMER);
		bankAccount.setPrimaryAccount(true);
		bankAccount.setRegistrationBranchId(mobileProfile.getBranchId());
		bankAccount.setStatus(BankAccountStatus.ACTIVE);
		bankAccount.setType(BankAccountType.E_WALLET);
		
		try {
			bankAccount = bankService.createBankAccount(bankAccount, EWalletConstants.SYSTEM);
			LOG.debug("Bank Account auto reg successful");
			return bankAccount;
		} catch (Exception e) {
			LOG.debug("Create Bank Account failed " + e.getMessage());
			throw e;
		} 
	}

	@Override
	public void deleteCustomer(Customer customer) {
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		Customer c = customerService.findCustomerById(customer.getId());
		if(c == null || c.getId() == null ){
			LOG.debug("Customer to delete does not exist");
		}else{
			try {
				customerService.deregisterCustomer(c, EWalletConstants.SYSTEM);
			} catch (Exception_Exception e) {
				LOG.debug("Deleting customer threw an Exception" + e.getMessage() + " Ignore");
				LOG.error(e);
			}
		}
		
	}

	@Override
	public void deleteMobileProfile(MobileProfile mobileProfile) {
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		MobileProfile mp = customerService.findMobileProfileById(mobileProfile.getId());
		if(mp == null || mp.getId() == null){
			LOG.debug("Mobile Profile to delete does not exist.");
		}else{
			try {
				customerService.deRegisterMobileProfile(mp, EWalletConstants.SYSTEM);
			} catch (Exception_Exception e) {
				LOG.debug("Deleting customer threw an Exception" + e.getMessage() + " Ignore");
				LOG.error(e);
			}
		}
	}

	
	public void synchAutoRegDetails(Customer customer, MobileProfile mobileProfile, BankAccount bankAccount) throws Exception {
		try {
			LOG.debug("Running sync for AUTOREG");
			this.populateAndSync(customer, MessageAction.CREATE);
			LOG.debug("Done Synch Customer");
			this.populateAndSync(mobileProfile, MessageAction.CREATE);
			LOG.debug("Done sysnch MobileProfile");
			this.populateAndSync(bankAccount, MessageAction.CREATE);
			LOG.debug("Done Synch BankAccount");
		} catch (Exception e) {
			LOG.debug("Synch Process has failed...", e);
			throw e;
		}
	}
	
	private zw.co.esolutions.mcommerce.msg.BankAccount populateAndSync(BankAccount account, zw.co.esolutions.ewallet.msg.MessageAction action) throws Exception {
	   	BankRegistrationMessageDocument doc = BankRegistrationMessageDocument.Factory.newInstance();
	   	BankRegistrationMessage msg = doc.addNewBankRegistrationMessage();
	   	msg.setMessageCommand(MessageCommand.Enum.forString(action.toString()));
	   	zw.co.esolutions.mcommerce.msg.BankAccount ba = msg.addNewBankAccount(); 
	   	
	   	ba.setId(account.getId());
	   	ba.setAccountClass(AccountClass.Enum.forString(account.getAccountClass().toString()));
	   	ba.setAccountHolderId(account.getAccountHolderId());
	   	ba.setAccountLevel(AccountLevel.Enum.forString(account.getLevel().toString()));
	   	ba.setAccountName(account.getAccountName());
	   	ba.setAccountNumber(account.getAccountNumber());
	   	ba.setAccountType(AccountType.Enum.forString(account.getType().toString()));
	   	ba.setBankReferenceId(account.getBankReferenceId());
	   	if (account.getBranch() != null) {
	   		ba.setBranchId(account.getBranch().getId());
	   	}
	   	ba.setDateCreated(DateUtil.convertToCalendar(DateUtil.convertToDate((XMLGregorianCalendar)account.getDateCreated())));
	   	ba.setStatus(Status.Enum.forString(account.getStatus().toString()));
	   	ba.setOwnerType(zw.co.esolutions.mcommerce.msg.BankAccount.OwnerType.Enum.forString(account.getOwnerType().toString()));
	   	ba.setAccountCode(account.getCode());
	   	ba.setPrimaryAccount(account.isPrimaryAccount());
	   	
	   	this.sync(doc.toString());
	    
	   	return ba;
	}
	
	private zw.co.esolutions.mcommerce.msg.Customer populateAndSync(Customer customer, MessageAction action) throws Exception {
		BankRegistrationMessageDocument doc = BankRegistrationMessageDocument.Factory.newInstance();
	   	BankRegistrationMessage msg = doc.addNewBankRegistrationMessage();
	   	msg.setMessageCommand(MessageCommand.Enum.forString(action.toString()));
	   	zw.co.esolutions.mcommerce.msg.Customer c = msg.addNewCustomer();
	   	c.setId(customer.getId());
	   	c.setBranchId(customer.getBranchId());
	   	c.setContactDetailsId(customer.getContactDetailsId());
	   	c.setCustomerClass(zw.co.esolutions.mcommerce.msg.Customer.CustomerClass.Enum.forString(customer.getCustomerClass().toString()));
	   	c.setDateCreated(DateUtil.convertToCalendar(DateUtil.convertToDate(customer.getDateCreated())));
	   	c.setDateOfBirth(DateUtil.convertToCalendar(DateUtil.convertToDate(customer.getDateOfBirth())));
	   	c.setFirstNames(customer.getFirstNames());
	   	c.setGender(zw.co.esolutions.mcommerce.msg.Customer.Gender.Enum.forString(customer.getGender().toString()));
	   	c.setLastName(customer.getLastName());
	   	c.setMaritalStatus(zw.co.esolutions.mcommerce.msg.Customer.MaritalStatus.Enum.forString(customer.getMaritalStatus().toString()));
	   	c.setNationalId(customer.getNationalId());
	   	c.setStatus(zw.co.esolutions.mcommerce.msg.Customer.Status.Enum.forString(customer.getStatus().toString()));
	   	c.setTitle(customer.getTitle());
	   	c.setCustomerAutoRegStatus(customer.getCustomerAutoRegStatus().name());
	   	
	   	this.sync(doc.toString());
	   	
		return c;
	}
	
	private zw.co.esolutions.mcommerce.msg.MobileProfile populateAndSync(MobileProfile profile, zw.co.esolutions.ewallet.msg.MessageAction action) throws Exception {
		BankRegistrationMessageDocument doc = BankRegistrationMessageDocument.Factory.newInstance();
	   	BankRegistrationMessage msg = doc.addNewBankRegistrationMessage();
	   	msg.setMessageCommand(MessageCommand.Enum.forString(action.toString()));
	   	zw.co.esolutions.mcommerce.msg.MobileProfile mp = msg.addNewMobileProfile();
	   	mp.setId(profile.getId());
	  	mp.setCustomerId(profile.getCustomer().getId());
	   	mp.setDateCreated(DateUtil.convertToCalendar(DateUtil.convertToDate(profile.getDateCreated())));
	   	mp.setMobileNetworkOperator(zw.co.esolutions.mcommerce.msg.MobileProfile.MobileNetworkOperator.Enum.forString(profile.getNetwork().toString()));
	   	mp.setMobileNumber(profile.getMobileNumber());
	   	mp.setReferralCode(profile.getReferralCode());
	   	mp.setReferralProcessed(profile.isReferralProcessed());
	   	mp.setSecretCode(profile.getSecretCode());
	   	mp.setStatus(zw.co.esolutions.mcommerce.msg.MobileProfile.Status.Enum.forString(profile.getStatus().toString()));
	 	mp.setBankId(profile.getBankId());
	 	mp.setPrimary(profile.isPrimary());
	 	this.sync(doc.toString());
	   	
		return mp;
	}
	
	public zw.co.esolutions.mcommerce.msg.MobileProfile populateAndSync(MobileProfile profile, MessageAction action, String notification) throws Exception {
		BankRegistrationMessageDocument doc = BankRegistrationMessageDocument.Factory.newInstance();
	   	BankRegistrationMessage msg = doc.addNewBankRegistrationMessage();
	   	msg.setMessageCommand(MessageCommand.Enum.forString(action.toString()));
	   	zw.co.esolutions.mcommerce.msg.MobileProfile mp = msg.addNewMobileProfile();
	   	LOG.debug("Mobile profile status in sync mobile logic      "+profile.getStatus().toString());
	   	mp.setId(profile.getId());
	  	mp.setCustomerId(profile.getCustomer().getId());
	   	mp.setDateCreated(DateUtil.convertToCalendar(DateUtil.convertToDate(profile.getDateCreated())));
	   	mp.setMobileNetworkOperator(zw.co.esolutions.mcommerce.msg.MobileProfile.MobileNetworkOperator.Enum.forString(profile.getNetwork().toString()));
	   	mp.setMobileNumber(profile.getMobileNumber());
	   	mp.setReferralCode(profile.getReferralCode());
	   	mp.setReferralProcessed(profile.isReferralProcessed());
	   	mp.setSecretCode(profile.getSecretCode());
	   	mp.setStatus(zw.co.esolutions.mcommerce.msg.MobileProfile.Status.Enum.forString(profile.getStatus().toString()));
	 	mp.setBankId(profile.getBankId());
	 	mp.setPrimary(profile.isPrimary());
	 	mp.setNotification(notification);
	  	LOG.debug("message sync >>>>>>>>>>>>>>>>>>>>>>mobile profile status >>>>>>>>>>>>>>>>>>>>"+mp.getStatus());
	 	
	  	this.sync(doc.toString());
	   	
		return mp;
	}
	
	private void sync(String message) throws Exception {
		//MessageSender.send(EWalletConstants.FROM_EWALLET_TO_SWITCH_REG_QUEUE, message);
	}

	@Override
	public ContactDetails processContactDetailsAutoReg(Customer customer)
			throws Exception {
		ContactDetailsServiceSOAPProxy contactDetailsService = new ContactDetailsServiceSOAPProxy();
		
		ContactDetails contactDetails = new ContactDetails();
		contactDetails.setCountry("ZIMBABWE");
		contactDetails.setOwnerType("CUSTOMER");
		contactDetails.setOwnerId(customer.getId());
		contactDetails.setContactName("AUTO REG");
		
		try {
			contactDetails = contactDetailsService.createContactDetails(contactDetails, EWalletConstants.SYSTEM);
			customer.setContactDetailsId(contactDetails.getId());
			customer = new CustomerServiceSOAPProxy().updateCustomer(customer, EWalletConstants.SYSTEM);
		} catch (Exception e) {
			LOG.error("Failed to create auto-reg contact details: " + e.getMessage());
			throw e;
		}
		LOG.debug("Contact details created successfully");
		return contactDetails;
	}
	
	private void rollbackAutoReg(Customer customer, ContactDetails contactDetails, MobileProfile mobileProfile, BankAccount bankAccount) {
		try {
			if (bankAccount != null) {
				new BankServiceSOAPProxy().deleteBankAccount(bankAccount, EWalletConstants.SYSTEM);
				LOG.debug("BankAccount Auto-Reg ROLLBACK DONE");
			}
			
			if (mobileProfile != null) {
				new CustomerServiceSOAPProxy().deleteMobileProfile(mobileProfile, EWalletConstants.SYSTEM);
				LOG.debug("MobileProfile Auto-Reg ROLLBACK DONE");
			}
			
			if (customer != null) {
				new CustomerServiceSOAPProxy().deleteCustomer(customer, EWalletConstants.SYSTEM);
				LOG.debug("Customer Auto-Reg ROLLBACK DONE");

				new ContactDetailsServiceSOAPProxy().deleteContactDetails(contactDetails);
				LOG.debug("ContactDetails Auto-Reg ROLLBACK DONE");

			}
			LOG.debug("@@@      Auto-Reg ROLLBACK Successful");

		} catch (Exception e) {
			LOG.error("@@@         Auto-Reg Rollback has thrown an Exception");
		
		}
	}
	
}
