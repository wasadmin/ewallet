package zw.co.esolutions.ewallet.csr.msg;


import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.ewallet.agentservice.service.Agent;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.merchantservices.service.BankMerchant;
import zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchant;
import zw.co.esolutions.ewallet.sms.MessageSender;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.mcommerce.msg.BankAccount.AccountClass;
import zw.co.esolutions.mcommerce.msg.BankAccount.AccountLevel;
import zw.co.esolutions.mcommerce.msg.BankAccount.AccountType;
import zw.co.esolutions.mcommerce.msg.BankAccount.OwnerType;
import zw.co.esolutions.mcommerce.msg.BankAccount.Status;
import zw.co.esolutions.mcommerce.msg.BankRegistrationMessage;
import zw.co.esolutions.mcommerce.msg.BankRegistrationMessage.MessageCommand;
import zw.co.esolutions.mcommerce.msg.BankRegistrationMessageDocument;
import zw.co.esolutions.mcommerce.msg.Customer.CustomerClass;
import zw.co.esolutions.mcommerce.msg.Customer.Gender;
import zw.co.esolutions.mcommerce.msg.Customer.MaritalStatus;
import zw.co.esolutions.mcommerce.msg.MobileProfile.MobileNetworkOperator;

public class MessageSync {
	
	
	private static Logger LOG ;
	
	static{
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(MessageSync.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + MessageSync.class);
		}
	}
	
	
	
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
	   	
	   	LOG.debug(".....>>>>>>>>>>>>>>>>> Account ID "+account.getId());
	   	ba.setId(account.getId());
		LOG.debug(".....>>>>>>>>>>>>>>>>> Account Class "+account.getAccountClass());
	   	ba.setAccountClass(AccountClass.Enum.forString(account.getAccountClass().toString()));
		LOG.debug(".....>>>>>>>>>>>>>>>>> Account Holder ID "+account.getAccountHolderId());
	   	ba.setAccountHolderId(account.getAccountHolderId());
		LOG.debug(".....>>>>>>>>>>>>>>>>> Account Level "+account.getLevel());
	   	ba.setAccountLevel(AccountLevel.Enum.forString(account.getLevel().toString()));
		LOG.debug(".....>>>>>>>>>>>>>>>>> Account Name "+account.getAccountName());
	   	ba.setAccountName(account.getAccountName());
		LOG.debug(".....>>>>>>>>>>>>>>>>> Account Number "+account.getAccountNumber());
	   	ba.setAccountNumber(account.getAccountNumber());
		LOG.debug(".....>>>>>>>>>>>>>>>>> Account Type"+account.getType());
	   	ba.setAccountType(AccountType.Enum.forString(account.getType().toString()));
		LOG.debug(".....>>>>>>>>>>>>>>>>> Account Bank reference ID "+account.getBankReferenceId());
	   	ba.setBankReferenceId(account.getBankReferenceId());
		LOG.debug(".....>>>>>>>>>>>>>>>>> Account Branch  "+account.getBranch());
	   	ba.setBranchId(account.getBranch().getId());
	   	ba.setDateCreated(DateUtil.convertToCalendar(DateUtil.convertToDate(account.getDateCreated())));
		LOG.debug(".....>>>>>>>>>>>>>>>>> Account Status "+account.getStatus());
	   	ba.setStatus(Status.Enum.forString(account.getStatus().toString()));
		LOG.debug(".....>>>>>>>>>>>>>>>>> Account Owner Type "+account.getOwnerType());
	   	ba.setOwnerType(OwnerType.Enum.forString(account.getOwnerType().toString()));
	   	ba.setCardNumber(account.getCardNumber());
		LOG.debug(".....>>>>>>>>>>>>>>>>> Account Code "+account.getCode());
	   	ba.setAccountCode(account.getCode());
		LOG.debug(".....>>>>>>>>>>>>>>>>> Account Is Primary "+account.isPrimaryAccount());
	   	ba.setPrimaryAccount(account.isPrimaryAccount());
		LOG.debug(".....>>>>>>>>>>>>>>>>> Account Reg Branch Id "+account.getRegistrationBranchId());
	   	ba.setRegistrationBranchId(account.getRegistrationBranchId());
	   	MessageSync.sync(doc.toString());
	    
	   	return ba;
	}
	
	public static zw.co.esolutions.mcommerce.msg.Customer populateAndSync(Customer customer, MessageAction action) throws Exception {
		BankRegistrationMessageDocument doc = BankRegistrationMessageDocument.Factory.newInstance();
	   	BankRegistrationMessage msg = doc.addNewBankRegistrationMessage();
	   	msg.setMessageCommand(MessageCommand.Enum.forString(action.toString()));
	   	zw.co.esolutions.mcommerce.msg.Customer c = msg.addNewCustomer();
		
//	   	String gender = null;
//	   	if(customer.getGender().toString().equalsIgnoreCase("M")) {
//	   		gender = "MALE";
//	   	} else {
//	   		gender = "FEMALE";
//	   	}
	   	
	   	c.setId(customer.getId());
	   	c.setBranchId(customer.getBranchId());
	   	c.setContactDetailsId(customer.getContactDetailsId());
	   	c.setCustomerClass(CustomerClass.Enum.forString(customer.getCustomerClass().toString()));
	   	c.setDateCreated(DateUtil.convertToCalendar(DateUtil.convertToDate(customer.getDateCreated())));
	   	c.setDateOfBirth(DateUtil.convertToCalendar(DateUtil.convertToDate(customer.getDateOfBirth())));
	   	c.setFirstNames(customer.getFirstNames());
	   	c.setGender(Gender.Enum.forString(customer.getGender().toString()));
	   	c.setLastName(customer.getLastName());
	   	c.setMaritalStatus(MaritalStatus.Enum.forString(customer.getMaritalStatus().toString()));
	   	c.setNationalId(customer.getNationalId());
	   	c.setStatus(zw.co.esolutions.mcommerce.msg.Customer.Status.Enum.forString(customer.getStatus().toString()));
	   	c.setTitle(customer.getTitle());
	   	c.setCustomerAutoRegStatus(customer.getCustomerAutoRegStatus().name());
	   	
	   	MessageSync.sync(doc.toString());
	   	
		return c;
	}
	
	public static zw.co.esolutions.mcommerce.msg.MobileProfile populateAndSync(MobileProfile profile, MessageAction action, String notification) throws Exception {
		BankRegistrationMessageDocument doc = BankRegistrationMessageDocument.Factory.newInstance();
	   	BankRegistrationMessage msg = doc.addNewBankRegistrationMessage();
	   	msg.setMessageCommand(MessageCommand.Enum.forString(action.toString()));
	   	zw.co.esolutions.mcommerce.msg.MobileProfile mp = msg.addNewMobileProfile();
	   	LOG.debug("Mobile profile status in sync mobile logic      "+profile.getStatus().toString());
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
	 	mp.setNotification(notification);
	  	LOG.debug("message sync >>>>>>>>>>>>>>>>>>>>>>mobile profile status >>>>>>>>>>>>>>>>>>>>"+mp.getStatus());
	 		MessageSync.sync(doc.toString());
	   	
		return mp;
	}
	
	public static zw.co.esolutions.mcommerce.msg.MobileProfile populateAndSync(MobileProfile profile, MessageAction action) throws Exception {
		BankRegistrationMessageDocument doc = BankRegistrationMessageDocument.Factory.newInstance();
	   	BankRegistrationMessage msg = doc.addNewBankRegistrationMessage();
	   	msg.setMessageCommand(MessageCommand.Enum.forString(action.toString()));
	   	zw.co.esolutions.mcommerce.msg.MobileProfile mp = msg.addNewMobileProfile();
	   	LOG.debug("Mobile profile status in sync mobile logic      "+profile.getStatus().toString());
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
	 	LOG.debug("message sync >>>>>>>>>>>>>>>>>>>>>>mobile profile status >>>>>>>>>>>>>>>>>>>>"+mp.getStatus());
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
	
	public static zw.co.esolutions.mcommerce.msg.BankMerchant populateAndSync(BankMerchant bankMerchant, MessageAction action) throws Exception {
	   	BankRegistrationMessageDocument doc = BankRegistrationMessageDocument.Factory.newInstance();
	   	BankRegistrationMessage msg = doc.addNewBankRegistrationMessage();
	   	msg.setMessageCommand(MessageCommand.Enum.forString(action.toString()));
	   	zw.co.esolutions.mcommerce.msg.BankMerchant bm = msg.addNewBankMerchant();
	   	
	   	bm.setId(bankMerchant.getId());
	   	bm.setEnabled(bankMerchant.isEnabled());
	   	bm.setBankId(bankMerchant.getBankId());
	   	bm.setMerchantId(bankMerchant.getMerchant().getId());
	   	bm.setStatus(bankMerchant.getStatus().name());
	   	bm.setSuspenseAccount(bankMerchant.getMerchantSuspenseAccount());
	   	bm.setDateCreated(DateUtil.convertToCalendar(DateUtil.convertToDate(bankMerchant.getDateCreated())));
	   
	   	MessageSync.sync(doc.toString());
	    
	   	return bm;
	}
	
	public static zw.co.esolutions.mcommerce.msg.CustomerMerchant populateAndSync(CustomerMerchant customerMerchant, MessageAction action) throws Exception {
	   	BankRegistrationMessageDocument doc = BankRegistrationMessageDocument.Factory.newInstance();
	   	BankRegistrationMessage msg = doc.addNewBankRegistrationMessage();
	   	msg.setMessageCommand(MessageCommand.Enum.forString(action.toString()));
	   	zw.co.esolutions.mcommerce.msg.CustomerMerchant cm = msg.addNewCustomerMerchant();
	   	
	   	cm.setId(customerMerchant.getId());
	   	cm.setBankId(customerMerchant.getBankId());
	   	cm.setBankMerchantId(customerMerchant.getBankMerchant().getId());
	   	cm.setStatus(customerMerchant.getStatus().name());
	   	cm.setAccountNumber(customerMerchant.getCustomerAccountNumber());
	   	cm.setCustomerId(customerMerchant.getCustomerId());
	   	cm.setDateCreated(DateUtil.convertToCalendar(DateUtil.convertToDate(customerMerchant.getDateCreated())));
	   
	   	MessageSync.sync(doc.toString());
	    
	   	return cm;
	}
	
	public static zw.co.esolutions.mcommerce.msg.Agent populateAndSync(Agent agent , MessageAction action) throws Exception{
		BankRegistrationMessageDocument doc = BankRegistrationMessageDocument.Factory.newInstance();
	   	BankRegistrationMessage msg = doc.addNewBankRegistrationMessage();
		msg.setMessageCommand(MessageCommand.Enum.forString(action.toString()));
		zw.co.esolutions.mcommerce.msg.Agent agnt = msg.addNewAgent();
		
		agnt.setId(agent.getId());
		agnt.setAgentClassId(agent.getAgentClass().getId());
		agnt.setAgentLevel(agent.getAgentLevel().name());
		agnt.setAgentName(agent.getAgentName());
		agnt.setAgentNumber(agent.getAgentNumber());
		agnt.setAgentType(agent.getAgentType().name());
		agnt.setCustomerId(agent.getCustomerId());
		agnt.setProfileId(agent.getProfileId());
		agnt.setStatus(agent.getStatus().name());
		agnt.setSuperAgentId(agent.getSuperAgentId());
		
		MessageSync.sync(doc.toString());
		
		return agnt;
	}
}
