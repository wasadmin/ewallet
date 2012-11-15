package zw.co.esolutions.ewallet.msg;


import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.sms.MessageSender;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.mcommerce.msg.BankRegistrationMessage;
import zw.co.esolutions.mcommerce.msg.BankRegistrationMessageDocument;
import zw.co.esolutions.mcommerce.msg.BankAccount.AccountClass;
import zw.co.esolutions.mcommerce.msg.BankAccount.AccountLevel;
import zw.co.esolutions.mcommerce.msg.BankAccount.AccountType;
import zw.co.esolutions.mcommerce.msg.BankAccount.OwnerType;
import zw.co.esolutions.mcommerce.msg.BankAccount.Status;
import zw.co.esolutions.mcommerce.msg.BankRegistrationMessage.MessageCommand;
import zw.co.esolutions.mcommerce.msg.Customer.CustomerClass;
import zw.co.esolutions.mcommerce.msg.Customer.Gender;
import zw.co.esolutions.mcommerce.msg.Customer.MaritalStatus;
import zw.co.esolutions.mcommerce.msg.MobileProfile.MobileNetworkOperator;

public class MessageSync {
	
	public static zw.co.esolutions.mcommerce.msg.Bank populateAndSync(Bank bank, MessageAction action) throws Exception {
		BankRegistrationMessageDocument doc = BankRegistrationMessageDocument.Factory.newInstance();
    	BankRegistrationMessage msg = doc.addNewBankRegistrationMessage();
    	msg.setMessageCommand(MessageCommand.Enum.forString(action.toString()));
    	zw.co.esolutions.mcommerce.msg.Bank b = msg.addNewBank();
    	
    	b.setId(bank.getId());
    	b.setBankName(bank.getName());
    	b.setCode(bank.getCode());
    	b.setContactDetailsId(bank.getContactDetailsId());
    	b.setDateCreated(DateUtil.convertToCalendar(DateUtil.convertToDate(bank.getDateCreated())));
    	b.setStatus(zw.co.esolutions.mcommerce.msg.Bank.Status.Enum.forString(bank.getStatus().toString()));
    	MessageSync.sync(doc.toString());
    	
    	return b;
    	
	}
	
	public static zw.co.esolutions.mcommerce.msg.BankBranch populateAndSync(BankBranch branch, MessageAction action) throws Exception {
		BankRegistrationMessageDocument doc = BankRegistrationMessageDocument.Factory.newInstance();
    	BankRegistrationMessage msg = doc.addNewBankRegistrationMessage();
    	msg.setMessageCommand(MessageCommand.Enum.forString(action.toString()));
    	zw.co.esolutions.mcommerce.msg.BankBranch b = msg.addNewBankBranch();
    	
    	b.setId(branch.getId());
    	b.setBankId(branch.getBank().getId());
    	b.setBranchCode(branch.getCode());
    	b.setBranchName(branch.getName());
    	b.setContactDetailsId(branch.getContactDetailsId());
    	b.setDateCreated(DateUtil.convertToCalendar(DateUtil.convertToDate(branch.getDateCreated())));
    	
    	MessageSync.sync(doc.toString());
    	
    	return b;
    	
	}
	
	public static zw.co.esolutions.mcommerce.msg.BankAccount populateAndSync(BankAccount account, MessageAction action) throws Exception {
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
	   	ba.setBranchId(account.getBranch().getId());
	   	ba.setDateCreated(DateUtil.convertToCalendar(DateUtil.convertToDate(account.getDateCreated())));
	   	ba.setStatus(Status.Enum.forString(account.getStatus().toString()));
	   	ba.setOwnerType(OwnerType.Enum.forString(account.getOwnerType().toString()));
	   	ba.setCardNumber(account.getCardNumber());
	   	ba.setAccountCode(account.getCode());
	   	ba.setPrimaryAccount(account.isPrimaryAccount());
	   	ba.setRegistrationBranchId(account.getRegistrationBranchId());
	   	MessageSync.sync(doc.toString());
	    
	   	return ba;
	}
	
	public static zw.co.esolutions.mcommerce.msg.Customer populateAndSync(Customer customer, MessageAction action) throws Exception {
		BankRegistrationMessageDocument doc = BankRegistrationMessageDocument.Factory.newInstance();
	   	BankRegistrationMessage msg = doc.addNewBankRegistrationMessage();
	   	msg.setMessageCommand(MessageCommand.Enum.forString(action.toString()));
	   	zw.co.esolutions.mcommerce.msg.Customer c = msg.addNewCustomer();
		
	   	String gender = null;
	   	if(customer.getGender().toString().equalsIgnoreCase("M")) {
	   		gender = "MALE";
	   	} else {
	   		gender = "FEMALE";
	   	}
	   	
	   	c.setId(customer.getId());
	   	c.setBranchId(customer.getBranchId());
	   	c.setContactDetailsId(customer.getContactDetailsId());
	   	c.setCustomerClass(CustomerClass.Enum.forString(customer.getCustomerClass().toString()));
	   	c.setDateCreated(DateUtil.convertToCalendar(DateUtil.convertToDate(customer.getDateCreated())));
	   	c.setDateOfBirth(DateUtil.convertToCalendar(DateUtil.convertToDate(customer.getDateOfBirth())));
	   	c.setFirstNames(customer.getFirstNames());
	   	c.setGender(Gender.Enum.forString(gender));
	   	c.setLastName(customer.getLastName());
	   	c.setMaritalStatus(MaritalStatus.Enum.forString(customer.getMaritalStatus().toString()));
	   	c.setNationalId(customer.getNationalId());
	   	c.setStatus(zw.co.esolutions.mcommerce.msg.Customer.Status.Enum.forString(customer.getStatus().toString()));
	   	c.setTitle(customer.getTitle());
	   	
	   	MessageSync.sync(doc.toString());
	   	
		return c;
	}
	
	public static zw.co.esolutions.mcommerce.msg.MobileProfile populateAndSync(MobileProfile profile, MessageAction action) throws Exception {
		BankRegistrationMessageDocument doc = BankRegistrationMessageDocument.Factory.newInstance();
	   	BankRegistrationMessage msg = doc.addNewBankRegistrationMessage();
	   	msg.setMessageCommand(MessageCommand.Enum.forString(action.toString()));
	   	zw.co.esolutions.mcommerce.msg.MobileProfile mp = msg.addNewMobileProfile();
	   	
	   	mp.setId(profile.getId());
	  	mp.setCustomerId(profile.getCustomer().getId());
	   	mp.setDateCreated(DateUtil.convertToCalendar(DateUtil.convertToDate(profile.getDateCreated())));
	   	mp.setMobileNetworkOperator(MobileNetworkOperator.Enum.forString(profile.getNetwork().toString()));
	   	mp.setMobileNumber(profile.getMobileNumber());
	   	mp.setReferralCode(profile.getReferralCode());
	   	mp.setReferralProcessed(profile.isReferralProcessed());
	   	mp.setSecretCode(profile.getSecretCode());
	   	mp.setStatus(zw.co.esolutions.mcommerce.msg.MobileProfile.Status.Enum.forString(profile.getStatus().toString()));
	 	mp.setBankId(profile.getBankId());
	 	mp.setPrimary(profile.isPrimary());
	   	
	   	MessageSync.sync(doc.toString());
	   	
		return mp;
	}
	
	public static zw.co.esolutions.mcommerce.msg.ContactDetails populateAndSync(ContactDetails contact, MessageAction action) throws Exception {
		BankRegistrationMessageDocument doc = BankRegistrationMessageDocument.Factory.newInstance();
	   	BankRegistrationMessage msg = doc.addNewBankRegistrationMessage();
	   	msg.setMessageCommand(MessageCommand.Enum.forString(action.toString()));
	   	zw.co.esolutions.mcommerce.msg.ContactDetails c = msg.addNewContactDetails();
	   	
	   	c.setId(contact.getId());
	   	c.setCity(contact.getCity());
	   	c.setContactName(contact.getContactName());
	   	c.setCountry(contact.getCountry());
	   	c.setDateCreated(DateUtil.convertToCalendar(DateUtil.convertToDate(contact.getDateCreated())));
	   	c.setEmail(contact.getEmail());
	   	c.setFax(contact.getFax());
	   	c.setOwnerId(contact.getOwnerId());
	   	c.setOwnerType(contact.getOwnerType());
	   	c.setStreet(contact.getStreet());
	   	c.setSurburb(contact.getSuburb());
	   	c.setTelephone(contact.getTelephone());
	   	   		   	
	   	MessageSync.sync(doc.toString());
	   	
		return c;
	}
	
	public static void sync(String message) throws Exception {
		MessageSender.send(EWalletConstants.FROM_EWALLET_TO_SWITCH_REG_QUEUE, message);
	}
}
