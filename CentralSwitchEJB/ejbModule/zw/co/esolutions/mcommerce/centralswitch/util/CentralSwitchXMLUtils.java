package zw.co.esolutions.mcommerce.centralswitch.util;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.ewallet.bankservices.service.BankAccountLevel;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountStatus;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountType;
import zw.co.esolutions.ewallet.bankservices.service.BankBranchStatus;
import zw.co.esolutions.ewallet.bankservices.service.BankStatus;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.customerservices.service.CustomerAutoRegStatus;
import zw.co.esolutions.ewallet.customerservices.service.CustomerStatus;
import zw.co.esolutions.ewallet.customerservices.service.Gender;
import zw.co.esolutions.ewallet.customerservices.service.MaritalStatus;
import zw.co.esolutions.ewallet.customerservices.service.MobileNetworkOperator;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfileStatus;
import zw.co.esolutions.ewallet.merchantservices.service.BankMerchantStatus;
import zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchantStatus;
import zw.co.esolutions.ewallet.merchantservices.service.MerchantStatus;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.mcommerce.msg.Agent;
import zw.co.esolutions.mcommerce.msg.Bank;
import zw.co.esolutions.mcommerce.msg.BankAccount;
import zw.co.esolutions.mcommerce.msg.BankBranch;
import zw.co.esolutions.mcommerce.msg.Customer;
import zw.co.esolutions.mcommerce.msg.MobileProfile;

public class CentralSwitchXMLUtils {
	
	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/switch.log.properties");
			LOG = Logger.getLogger(CentralSwitchXMLUtils.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + CentralSwitchXMLUtils.class);
		}
	}

	
	
	public static zw.co.esolutions.ewallet.customerservices.service.MobileProfile copyMobileProfile(MobileProfile xmlMobileProfile, zw.co.esolutions.ewallet.customerservices.service.MobileProfile mobileProfile) {
		LOG.debug(">>>>>>>>>>>>>> In Copy Mobile Profile. Mobile Status "+xmlMobileProfile.getStatus());
		mobileProfile.setMobileNumber(xmlMobileProfile.getMobileNumber());
		mobileProfile.setDateCreated(DateUtil.convertToXMLGregorianCalendar(xmlMobileProfile.getDateCreated()));
		if(xmlMobileProfile.getMobileNetworkOperator() != null) {
			mobileProfile.setNetwork(MobileNetworkOperator.fromValue(xmlMobileProfile.getMobileNetworkOperator().toString()));
		}
		mobileProfile.setPasswordRetryCount(0);
		mobileProfile.setPrimary(xmlMobileProfile.getPrimary());
		LOG.debug(">>>>>>>>>>>>>> In Copy Mobile Profile. Mobile Status "+xmlMobileProfile.getStatus());
		if(xmlMobileProfile.getStatus() != null) {
			mobileProfile.setStatus(MobileProfileStatus.valueOf(xmlMobileProfile.getStatus().toString()));
			LOG.debug("1 Mobile profile status value::::::::in copyxml >>>>>>::::::"+mobileProfile.getStatus());
		}
		mobileProfile.setSecretCode(xmlMobileProfile.getSecretCode());
		mobileProfile.setReferralProcessed(xmlMobileProfile.getReferralProcessed());
		mobileProfile.setTimeout(DateUtil.convertToXMLGregorianCalendar(xmlMobileProfile.getTimeout()));
		mobileProfile.setReferralCode(xmlMobileProfile.getReferralCode());
		mobileProfile.setBankId(xmlMobileProfile.getBankId());
		LOG.debug("2 Mobile profile status value::::::::in copyxml >>>>>>::::::"+mobileProfile.getStatus());
		LOG.debug(">>>>>>>>>>>>>> out of  Copy Mobile Profile. Mobile Status "+xmlMobileProfile.getStatus());
		mobileProfile.setId(xmlMobileProfile.getId());
		return mobileProfile;
	}

	public static zw.co.esolutions.ewallet.customerservices.service.Customer copyCustomer(Customer xmlCustomer, zw.co.esolutions.ewallet.customerservices.service.Customer customer) {
		customer.setBranchId(xmlCustomer.getBranchId());
		customer.setContactDetailsId(xmlCustomer.getContactDetailsId());
		if(xmlCustomer.getCustomerClass() != null) {
			customer.setCustomerClass(zw.co.esolutions.ewallet.customerservices.service.CustomerClass.valueOf(xmlCustomer.getCustomerClass().toString()));
		}
		customer.setDateCreated(DateUtil.convertToXMLGregorianCalendar(xmlCustomer.getDateCreated()));
		customer.setDateOfBirth(DateUtil.convertToXMLGregorianCalendar(xmlCustomer.getDateOfBirth()));
		customer.setFirstNames(xmlCustomer.getFirstNames());
		if(xmlCustomer.getGender() != null) {
			customer.setGender(Gender.valueOf(xmlCustomer.getGender().toString()));
		}
		customer.setLastName(xmlCustomer.getLastName());
		if(xmlCustomer.getMaritalStatus() != null) {
			customer.setMaritalStatus(MaritalStatus.valueOf(xmlCustomer.getMaritalStatus().toString()));
		}
		customer.setNationalId(xmlCustomer.getNationalId());
		if(xmlCustomer.getStatus() != null) {
			customer.setStatus(CustomerStatus.valueOf(xmlCustomer.getStatus().toString()));
		}
		customer.setTitle(xmlCustomer.getTitle());
		customer.setId(xmlCustomer.getId());
		customer.setCustomerAutoRegStatus(CustomerAutoRegStatus.valueOf(xmlCustomer.getCustomerAutoRegStatus()));
		
		return customer;
	}
	
	public static zw.co.esolutions.ewallet.agentservice.service.Agent copyAgent(Agent xmlAgent, zw.co.esolutions.ewallet.agentservice.service.Agent agent) {
		LOG.debug("In Central Switch .....>>>>>>>>>>>>>>>>>Starting Copying Agent ");
		agent.setId(xmlAgent.getId());
		agent.setAgentName(xmlAgent.getAgentName());
//		agent.setAgentClass(value);
		agent.setAgentNumber(xmlAgent.getAgentNumber());
		agent.setDateCreated(DateUtil.convertToXMLGregorianCalendar(xmlAgent.getDateCreated()));
		agent.setCustomerId(xmlAgent.getCustomerId());
		agent.setProfileId(xmlAgent.getProfileId());
		agent.setSuperAgentId(xmlAgent.getSuperAgentId());
		if(xmlAgent.getAgentLevel() != null) {
			agent.setAgentLevel(zw.co.esolutions.ewallet.agentservice.service.AgentLevel.valueOf(xmlAgent.getAgentLevel().toString()));
		}
		if(xmlAgent.getAgentType()!= null) {
			agent.setAgentType(zw.co.esolutions.ewallet.agentservice.service.AgentType.valueOf(xmlAgent.getAgentType().toString()));
		}
		if(xmlAgent.getStatus() != null) {
			agent.setStatus(zw.co.esolutions.ewallet.agentservice.service.ProfileStatus.valueOf(xmlAgent.getStatus()));
		}
		LOG.debug("In Central Switch .....>>>>>>>>>>>>>>>>> Done copying agent ");
		return agent;
	}

	public static zw.co.esolutions.ewallet.bankservices.service.BankAccount copyBankAccount(BankAccount xmlBankAccount, zw.co.esolutions.ewallet.bankservices.service.BankAccount bankAccount) {
		LOG.debug("In Central Switch .....>>>>>>>>>>>>>>>>>Starting Copying Account ");
		LOG.debug(".....>>>>>>>>>>>>>>>>> Account Class "+xmlBankAccount.getStatus());
		if(xmlBankAccount.getAccountClass() != null) {
			bankAccount.setAccountClass(zw.co.esolutions.ewallet.bankservices.service.BankAccountClass.valueOf(xmlBankAccount.getAccountClass().toString()));
		} else {
			bankAccount.setAccountClass(zw.co.esolutions.ewallet.bankservices.service.BankAccountClass.NONE);
		}
		LOG.debug(".....>>>>>>>>>>>>>>>>> Account Holder ID "+xmlBankAccount.getAccountHolderId());
		bankAccount.setAccountHolderId(xmlBankAccount.getAccountHolderId());
		LOG.debug(".....>>>>>>>>>>>>>>>>> Account Name "+xmlBankAccount.getAccountName());
		bankAccount.setAccountName(xmlBankAccount.getAccountName());
		LOG.debug(".....>>>>>>>>>>>>>>>>> Account Number "+xmlBankAccount.getAccountNumber());
		bankAccount.setAccountNumber(xmlBankAccount.getAccountNumber());
		LOG.debug(".....>>>>>>>>>>>>>>>>> Account Ref Id "+xmlBankAccount.getBankReferenceId());
		bankAccount.setBankReferenceId(xmlBankAccount.getBankReferenceId());
		LOG.debug(".....>>>>>>>>>>>>>>>>> Account Card Number "+xmlBankAccount.getCardNumber());
		bankAccount.setCardNumber(xmlBankAccount.getCardNumber());
		LOG.debug(".....>>>>>>>>>>>>>>>>> Account Sequence "+xmlBankAccount.getCardSequence());
		bankAccount.setCardSequence(xmlBankAccount.getCardSequence());
		LOG.debug(".....>>>>>>>>>>>>>>>>> Account Code "+xmlBankAccount.getAccountCode());
		bankAccount.setCode(xmlBankAccount.getAccountCode());
		bankAccount.setDateCreated(DateUtil.convertToXMLGregorianCalendar(xmlBankAccount.getDateCreated()));
		if(xmlBankAccount.getAccountLevel() != null) {
			bankAccount.setLevel(BankAccountLevel.valueOf(xmlBankAccount.getAccountLevel().toString()));
		}
		LOG.debug(".....>>>>>>>>>>>>>>>>> Account Status "+xmlBankAccount.getStatus());
		if(xmlBankAccount.getStatus() != null) {
			bankAccount.setStatus(BankAccountStatus.valueOf(xmlBankAccount.getStatus().toString()));
		}
		LOG.debug(".....>>>>>>>>>>>>>>>>> Account Type "+xmlBankAccount.getAccountType());
		if(xmlBankAccount.getAccountType() != null) { 
			bankAccount.setType(BankAccountType.valueOf(xmlBankAccount.getAccountType().toString()));
		}
		LOG.debug(".....>>>>>>>>>>>>>>>>> Account Owner Type "+xmlBankAccount.getOwnerType());
		if(xmlBankAccount.getOwnerType() != null) {
			bankAccount.setOwnerType(OwnerType.valueOf(xmlBankAccount.getOwnerType().toString()));
		}
		LOG.debug(".....>>>>>>>>>>>>>>>>> Account Is Primary "+xmlBankAccount.getPrimaryAccount());
		bankAccount.setPrimaryAccount(xmlBankAccount.getPrimaryAccount());
		LOG.debug(".....>>>>>>>>>>>>>>>>> Account Reg Branch Id "+xmlBankAccount.getRegistrationBranchId());
		bankAccount.setRegistrationBranchId(xmlBankAccount.getRegistrationBranchId());
		bankAccount.setId(xmlBankAccount.getId());
		LOG.debug("In CentralSwitchXMLUtils .....>>>>>>>>>>>>>>>>> Finished Copying Account  "+bankAccount);
		
		return bankAccount;
	}

	public static zw.co.esolutions.ewallet.bankservices.service.Bank copyBank(Bank xmlBank, zw.co.esolutions.ewallet.bankservices.service.Bank bank) {
		bank.setCode(xmlBank.getCode());
		bank.setDateCreated(DateUtil.convertToXMLGregorianCalendar(xmlBank.getDateCreated().getTime()));
		bank.setName(xmlBank.getBankName());
		bank.setContactDetailsId(xmlBank.getContactDetailsId());
		bank.setId(xmlBank.getId());
		if(xmlBank.getStatus() != null) {
			bank.setStatus(BankStatus.valueOf(xmlBank.getStatus().toString()));
		}
	    
		return bank;
	}

	public static zw.co.esolutions.ewallet.bankservices.service.BankBranch copyBankBranch(BankBranch xmlBankBranch, zw.co.esolutions.ewallet.bankservices.service.BankBranch bankBranch) {
		bankBranch.setCode(xmlBankBranch.getBranchCode());
		bankBranch.setContactDetailsId(xmlBankBranch.getContactDetailsId());
		bankBranch.setDateCreated(DateUtil.convertToXMLGregorianCalendar(xmlBankBranch.getDateCreated().getTime()));
		bankBranch.setName(xmlBankBranch.getBranchName());
		bankBranch.setId(xmlBankBranch.getId());
		if(xmlBankBranch.getStatus() != null){
			bankBranch.setStatus(BankBranchStatus.valueOf(xmlBankBranch.getStatus().toString()));
		}
		return bankBranch;
	}

	public static zw.co.esolutions.ewallet.merchantservices.service.Merchant copyMerchant(zw.co.esolutions.mcommerce.msg.Merchant xmlMerchant, zw.co.esolutions.ewallet.merchantservices.service.Merchant merchant) {
		merchant.setId(xmlMerchant.getId());
	   	merchant.setContactDetailsId(xmlMerchant.getContactDetailsId());
	   	merchant.setStatus(MerchantStatus.valueOf(xmlMerchant.getStatus().toString()));
	   	merchant.setName(xmlMerchant.getName());
	   	merchant.setShortName(xmlMerchant.getShortName());
	   	merchant.setDateCreated(DateUtil.convertToXMLGregorianCalendar(xmlMerchant.getDateCreated()));
		return merchant;
	}
	
	public static zw.co.esolutions.ewallet.merchantservices.service.BankMerchant copyBankMerchant(zw.co.esolutions.mcommerce.msg.BankMerchant xmlBankMerchant, zw.co.esolutions.ewallet.merchantservices.service.BankMerchant bankMerchant) {
		bankMerchant.setId(xmlBankMerchant.getId());
	//	bankMerchant.setEnabled(xmlBankMerchant.isEnabled());
		bankMerchant.setBankId(xmlBankMerchant.getBankId());
		bankMerchant.setStatus(BankMerchantStatus.valueOf(xmlBankMerchant.getStatus().toString()));
		bankMerchant.setMerchantSuspenseAccount(xmlBankMerchant.getSuspenseAccount());
		bankMerchant.setDateCreated(DateUtil.convertToXMLGregorianCalendar(xmlBankMerchant.getDateCreated()));
		return bankMerchant;
	}
	
	public static zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchant copyCustomerMerchant(zw.co.esolutions.mcommerce.msg.CustomerMerchant xmlCustomerMerchant, zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchant customerMerchant) {
		customerMerchant.setId(xmlCustomerMerchant.getId());
		customerMerchant.setBankId(xmlCustomerMerchant.getBankId());
		customerMerchant.setStatus(CustomerMerchantStatus.valueOf(xmlCustomerMerchant.getStatus().toString()));
		customerMerchant.setCustomerAccountNumber(xmlCustomerMerchant.getAccountNumber());
		customerMerchant.setCustomerId(xmlCustomerMerchant.getCustomerId());
		customerMerchant.setDateCreated(DateUtil.convertToXMLGregorianCalendar(xmlCustomerMerchant.getDateCreated()));
		return customerMerchant;
	}
}