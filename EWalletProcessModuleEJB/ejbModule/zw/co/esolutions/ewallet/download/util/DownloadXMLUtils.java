package zw.co.esolutions.ewallet.download.util;

import org.apache.log4j.Logger;

import zw.co.esolutions.ewallet.bankservices.service.BankAccountLevel;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountStatus;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountType;
import zw.co.esolutions.ewallet.bankservices.service.BankStatus;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.customerservices.service.CustomerStatus;
import zw.co.esolutions.ewallet.customerservices.service.Gender;
import zw.co.esolutions.ewallet.customerservices.service.MaritalStatus;
import zw.co.esolutions.ewallet.customerservices.service.MobileNetworkOperator;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfileStatus;
import zw.co.esolutions.ewallet.enums.TariffTableType;
import zw.co.esolutions.ewallet.limitservices.service.BankAccountClass;
import zw.co.esolutions.ewallet.limitservices.service.LimitPeriodType;
import zw.co.esolutions.ewallet.limitservices.service.LimitStatus;
import zw.co.esolutions.ewallet.limitservices.service.LimitValueType;
import zw.co.esolutions.ewallet.limitservices.service.TransactionType;
import zw.co.esolutions.ewallet.merchantservices.service.MerchantStatus;
import zw.co.esolutions.ewallet.tariffservices.service.TariffStatus;
import zw.co.esolutions.ewallet.tariffservices.service.TariffType;
import zw.co.esolutions.ewallet.tariffservices.service.TariffValueType;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.MoneyUtil;
import zw.co.esolutions.mcommerce.msg.Bank;
import zw.co.esolutions.mcommerce.msg.BankAccount;
import zw.co.esolutions.mcommerce.msg.BankBranch;
import zw.co.esolutions.mcommerce.msg.ContactDetails;
import zw.co.esolutions.mcommerce.msg.Customer;
import zw.co.esolutions.mcommerce.msg.Limit;
import zw.co.esolutions.mcommerce.msg.Merchant;
import zw.co.esolutions.mcommerce.msg.MobileProfile;
import zw.co.esolutions.mcommerce.msg.Tariff;
import zw.co.esolutions.mcommerce.msg.TariffTable;

public class DownloadXMLUtils {
	public static zw.co.esolutions.ewallet.customerservices.service.MobileProfile copyMobileProfile(MobileProfile xmlMobileProfile, zw.co.esolutions.ewallet.customerservices.service.MobileProfile mobileProfile) {
		mobileProfile.setMobileNumber(xmlMobileProfile.getMobileNumber());
		mobileProfile.setDateCreated(DateUtil.convertToXMLGregorianCalendar(xmlMobileProfile.getDateCreated()));
		if(xmlMobileProfile.getMobileNetworkOperator() != null) {
			mobileProfile.setNetwork(MobileNetworkOperator.fromValue(xmlMobileProfile.getMobileNetworkOperator().toString()));
		}
		mobileProfile.setPasswordRetryCount(0);
		mobileProfile.setPrimary(xmlMobileProfile.getPrimary());
		Logger.getLogger(DownloadXMLUtils.class).debug(">>>>>>>>>>>>>> In Copry Mobile Profile. Mobile Status "+xmlMobileProfile.getStatus());
		if(xmlMobileProfile.getStatus() != null) {
			mobileProfile.setStatus(MobileProfileStatus.valueOf(xmlMobileProfile.getStatus().toString()));
		}
		mobileProfile.setSecretCode(xmlMobileProfile.getSecretCode());
		mobileProfile.setReferralProcessed(xmlMobileProfile.getReferralProcessed());
		mobileProfile.setTimeout(DateUtil.convertToXMLGregorianCalendar(xmlMobileProfile.getTimeout()));
		mobileProfile.setReferralCode(xmlMobileProfile.getReferralCode());
		mobileProfile.setBankId(xmlMobileProfile.getBankId());
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
		return customer;
	}

	public static zw.co.esolutions.ewallet.bankservices.service.BankAccount copyBankAccount(BankAccount xmlBankAccount, zw.co.esolutions.ewallet.bankservices.service.BankAccount bankAccount) {
		
		if(xmlBankAccount.getStatus() != null) {
			bankAccount.setAccountClass(zw.co.esolutions.ewallet.bankservices.service.BankAccountClass.valueOf(xmlBankAccount.getAccountClass().toString()));
		} else {
			bankAccount.setAccountClass(zw.co.esolutions.ewallet.bankservices.service.BankAccountClass.NONE);
		}
		bankAccount.setAccountHolderId(xmlBankAccount.getAccountHolderId());
		bankAccount.setAccountName(xmlBankAccount.getAccountName());
		bankAccount.setAccountNumber(xmlBankAccount.getAccountNumber());
		bankAccount.setBankReferenceId(xmlBankAccount.getBankReferenceId());
		bankAccount.setCardNumber(xmlBankAccount.getCardNumber());
		bankAccount.setCardSequence(xmlBankAccount.getCardSequence());
		bankAccount.setCode(xmlBankAccount.getAccountCode());
		bankAccount.setDateCreated(DateUtil.convertToXMLGregorianCalendar(xmlBankAccount.getDateCreated()));
		if(xmlBankAccount.getAccountLevel() != null) {
			bankAccount.setLevel(BankAccountLevel.valueOf(xmlBankAccount.getAccountLevel().toString()));
		}
		if(xmlBankAccount.getStatus() != null) {
			bankAccount.setStatus(BankAccountStatus.valueOf(xmlBankAccount.getStatus().toString()));
		}
		if(xmlBankAccount.getAccountType() != null) { 
			bankAccount.setType(BankAccountType.valueOf(xmlBankAccount.getAccountType().toString()));
		}
		if(xmlBankAccount.getOwnerType() != null) {
			bankAccount.setOwnerType(OwnerType.valueOf(xmlBankAccount.getOwnerType().toString()));
		}
		bankAccount.setPrimaryAccount(xmlBankAccount.getPrimaryAccount());
		bankAccount.setRegistrationBranchId(xmlBankAccount.getRegistrationBranchId());
		
		return bankAccount;
	}

	public static zw.co.esolutions.ewallet.bankservices.service.Bank copyBank(Bank xmlBank, zw.co.esolutions.ewallet.bankservices.service.Bank bank) {
		bank.setCode(xmlBank.getCode());
		bank.setDateCreated(DateUtil.convertToXMLGregorianCalendar(xmlBank.getDateCreated().getTime()));
		bank.setName(xmlBank.getBankName());
		bank.setContactDetailsId(xmlBank.getContactDetailsId());
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
		
		return bankBranch;
	}

	public static zw.co.esolutions.ewallet.limitservices.service.Limit copyLimit(Limit xmlLimit, zw.co.esolutions.ewallet.limitservices.service.Limit limit) {
		limit.setAccountClass(BankAccountClass.valueOf(xmlLimit.getLimitAccountClass().toString()));
		limit.setBankId(xmlLimit.getBankId());
		limit.setDateCreated(DateUtil.convertToXMLGregorianCalendar(xmlLimit.getDateCreated()));
		limit.setEffectiveDate(DateUtil.convertToXMLGregorianCalendar(xmlLimit.getEffectiveDate()));
		limit.setEndDate(DateUtil.convertToXMLGregorianCalendar(xmlLimit.getEndDate()));
		limit.setMaxValue(MoneyUtil.convertToCents(xmlLimit.getMaxValue()));
		limit.setMinValue(MoneyUtil.convertToCents(xmlLimit.getMinValue()));
		if(xmlLimit.getPeriodType() != null) {
			limit.setPeriodType(LimitPeriodType.valueOf(xmlLimit.getPeriodType().toString()));
		}
		if(xmlLimit.getStatus() != null) {
			limit.setStatus(LimitStatus.valueOf(xmlLimit.getStatus().toString()));
		}
		if(xmlLimit.getLimitType() != null) {
			limit.setType(TransactionType.valueOf(xmlLimit.getLimitType().toString()));
		}
		if(xmlLimit.getLimitValueType() != null) {
			limit.setValueType(LimitValueType.valueOf(xmlLimit.getLimitValueType().toString()));
		}
		limit.setOldLimitId(xmlLimit.getOldLimitId());
		return limit;
	}

	public static zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails copyContactDetails(ContactDetails xmlContactDetails, zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails contact) {
		contact.setCity(xmlContactDetails.getCity());
		contact.setContactName(xmlContactDetails.getContactName());
		contact.setCountry(xmlContactDetails.getCountry());
		contact.setDateCreated(DateUtil.convertToXMLGregorianCalendar(xmlContactDetails.getDateCreated()));
		contact.setEmail(xmlContactDetails.getEmail());
		contact.setFax(xmlContactDetails.getFax());
		contact.setOwnerId(xmlContactDetails.getOwnerId());
		contact.setOwnerType(xmlContactDetails.getOwnerType());
		contact.setStreet(xmlContactDetails.getStreet());
		contact.setSuburb(xmlContactDetails.getSurburb());
		contact.setTelephone(xmlContactDetails.getTelephone());
		return contact;
	}

	public static zw.co.esolutions.ewallet.tariffservices.service.Tariff copyTariff(Tariff xmlTariff, zw.co.esolutions.ewallet.tariffservices.service.Tariff tariff) {
		tariff.setDateCreated(DateUtil.convertToXMLGregorianCalendar(xmlTariff.getDateCreated()));
		tariff.setLowerLimit(MoneyUtil.convertToCents(xmlTariff.getLowerLimit()));
		tariff.setUpperLimit(MoneyUtil.convertToCents(xmlTariff.getUpperLimit()));
		tariff.setValue(MoneyUtil.convertToCents(xmlTariff.getValue()));
		tariff.setOldTariffId(xmlTariff.getOldTariffId());
		if(xmlTariff.getValueType() != null) {
			tariff.setValueType(TariffValueType.valueOf(xmlTariff.getValueType().toString()));
		}
		return tariff;
	}

	public static zw.co.esolutions.ewallet.tariffservices.service.TariffTable copyTariffTable(TariffTable xmlTariffTable, zw.co.esolutions.ewallet.tariffservices.service.TariffTable tariffTable) {
//		tariffTable.setOwnerType(zw.co.esolutions.ewallet.tariffservices.service.OwnerType.valueOf(xmlTariffTable.getOwnerType().toString()));
		tariffTable.setBankId(xmlTariffTable.getBankId());
//		tariffTable.setCustomerClass(xmlTariffTable.getCustomerClass().toString());
		tariffTable.setDateCreated(DateUtil.convertToXMLGregorianCalendar(xmlTariffTable.getDateCreated()));
		tariffTable.setEffectiveDate(DateUtil.convertToXMLGregorianCalendar(xmlTariffTable.getEffectiveDate()));
		tariffTable.setEndDate(DateUtil.convertToXMLGregorianCalendar(xmlTariffTable.getEndDate()));
		if(xmlTariffTable.getStatus() != null) {
			tariffTable.setStatus(TariffStatus.valueOf(xmlTariffTable.getStatus().toString()));
		}
		if(xmlTariffTable.getTariffType() != null) {
			tariffTable.setTariffType(TariffType.valueOf(xmlTariffTable.getTariffType().toString()));
		}
		if(xmlTariffTable.getTransactionType() != null) {
			tariffTable.setTransactionType(zw.co.esolutions.ewallet.tariffservices.service.TransactionType.valueOf(xmlTariffTable.getTransactionType().toString()));
		}
		tariffTable.setOldTariffTableId(xmlTariffTable.getOldTariffTableId());
//		tariffTable.setOwnerId(xmlTariffTable.getOwnerId());
//		tariffTable.setType(TariffTableType.valueOf(xmlTariffTable.getTariffTableType()));
		return tariffTable;
	}
	public static zw.co.esolutions.ewallet.merchantservices.service.Merchant copyMerchant(Merchant xmlMerchant, zw.co.esolutions.ewallet.merchantservices.service.Merchant merchant) {
		merchant.setId(xmlMerchant.getId());
		merchant.setContactDetailsId(xmlMerchant.getContactDetailsId());
		merchant.setDateCreated(DateUtil.convertToXMLGregorianCalendar(xmlMerchant.getDateCreated().getTime()));
		merchant.setName(xmlMerchant.getName());
		merchant.setShortName(xmlMerchant.getShortName());
		if(xmlMerchant.getStatus() != null) {
			merchant.setStatus(MerchantStatus.valueOf(xmlMerchant.getStatus().toString()));
		}
	    
		return merchant;
	}
}