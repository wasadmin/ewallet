package zw.co.esolutions.ewallet.msg;

import javax.xml.datatype.XMLGregorianCalendar;

import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.limitservices.service.Limit;
import zw.co.esolutions.ewallet.merchantservices.service.BankMerchant;
import zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchant;
import zw.co.esolutions.ewallet.merchantservices.service.Merchant;
import zw.co.esolutions.ewallet.sms.MessageSender;
import zw.co.esolutions.ewallet.tariffservices.service.Tariff;
import zw.co.esolutions.ewallet.tariffservices.service.TariffTable;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.MoneyUtil;
import zw.co.esolutions.mcommerce.msg.BankAccount.AccountClass;
import zw.co.esolutions.mcommerce.msg.BankAccount.AccountLevel;
import zw.co.esolutions.mcommerce.msg.BankAccount.AccountType;
import zw.co.esolutions.mcommerce.msg.BankAccount.Status;
import zw.co.esolutions.mcommerce.msg.BankRegistrationMessage;
import zw.co.esolutions.mcommerce.msg.BankRegistrationMessage.MessageCommand;
import zw.co.esolutions.mcommerce.msg.BankRegistrationMessageDocument;
import zw.co.esolutions.mcommerce.msg.Limit.LimitAccountClass;
import zw.co.esolutions.mcommerce.msg.Limit.LimitType;
import zw.co.esolutions.mcommerce.msg.Limit.LimitValueType;
import zw.co.esolutions.mcommerce.msg.Limit.PeriodType;
import zw.co.esolutions.mcommerce.msg.Tariff.ValueType;
import zw.co.esolutions.mcommerce.msg.TariffTable.AgentType;
import zw.co.esolutions.mcommerce.msg.TariffTable.TariffType;
import zw.co.esolutions.mcommerce.msg.TariffTable.TransactionType;

public class MessageSync {
	
	public static zw.co.esolutions.mcommerce.msg.Limit populateAndSync(Limit limit, MessageAction action) throws Exception {
		BankRegistrationMessageDocument doc = BankRegistrationMessageDocument.Factory.newInstance();
    	BankRegistrationMessage msg = doc.addNewBankRegistrationMessage();
    	msg.setMessageCommand(MessageCommand.Enum.forString(action.toString()));
    	msg.setBankId(limit.getBankId());
    	zw.co.esolutions.mcommerce.msg.Limit l= msg.addNewLimit();
    	
    	l.setId(limit.getId());
    	l.setBankId(limit.getBankId());
    	l.setLimitAccountClass(LimitAccountClass.Enum.forString(limit.getAccountClass().toString()));
    	l.setLimitType(LimitType.Enum.forString(limit.getType().toString()));
    	l.setLimitValueType(LimitValueType.Enum.forString(limit.getValueType().toString()));
    	l.setPeriodType(PeriodType.Enum.forString(limit.getPeriodType().toString()));
    	l.setStatus(zw.co.esolutions.mcommerce.msg.Limit.Status.Enum.forString(
    			limit.getStatus().toString()));
    	l.setEffectiveDate(DateUtil.convertToCalendar(DateUtil.convertToDate(limit.getEffectiveDate())));
    	l.setEndDate(DateUtil.convertToCalendar(DateUtil.convertToDate(limit.getEndDate())));
    	l.setMaxValue(MoneyUtil.convertToDollars(limit.getMaxValue()));
    	l.setMinValue(MoneyUtil.convertToDollars(limit.getMinValue()));
    	l.setDateCreated(DateUtil.convertToCalendar(DateUtil.convertToDate(limit.getDateCreated())));
    	l.setOldLimitId(limit.getOldLimitId());
    	MessageSync.sync(doc.toString()); 
    	
    	return l;
    	
	}
	
	public static zw.co.esolutions.mcommerce.msg.TariffTable populateAndSync(TariffTable table, MessageAction action) throws Exception {
		BankRegistrationMessageDocument doc = BankRegistrationMessageDocument.Factory.newInstance();
    	BankRegistrationMessage msg = doc.addNewBankRegistrationMessage();
    	msg.setMessageCommand(MessageCommand.Enum.forString(action.toString()));
    	msg.setBankId(table.getBankId());
    	zw.co.esolutions.mcommerce.msg.TariffTable t = msg.addNewTariffTable();
    	
    	t.setAgentType(AgentType.Enum.forString(table.getAgentType().toString()));
    	t.setBankId(table.getBankId());
    	t.setCustomerClass(zw.co.esolutions.mcommerce.msg.TariffTable.CustomerClass.Enum.forString(
    			table.getCustomerClass().toString()));
    	t.setEffectiveDate(DateUtil.convertToCalendar(DateUtil.convertToDate(table.getEffectiveDate())));
    	t.setEndDate(DateUtil.convertToCalendar(DateUtil.convertToDate(table.getEndDate())));
    	t.setId(table.getId());
    	t.setStatus(zw.co.esolutions.mcommerce.msg.TariffTable.Status.Enum.forString(
    			table.getStatus().toString()));
    	t.setTariffType(TariffType.Enum.forString(table.getTariffType().toString()));
    	t.setTransactionType(TransactionType.Enum.forString(table.getTransactionType().toString()));
    	t.setDateCreated(DateUtil.convertToCalendar(DateUtil.convertToDate(table.getDateCreated())));
    	t.setOldTariffTableId(table.getOldTariffTableId());
    	MessageSync.sync(doc.toString());
    	
    	return t;
    	
	}
	
	public static zw.co.esolutions.mcommerce.msg.Tariff populateAndSync(Tariff tariff, MessageAction action) throws Exception {
	   	BankRegistrationMessageDocument doc = BankRegistrationMessageDocument.Factory.newInstance();
	   	BankRegistrationMessage msg = doc.addNewBankRegistrationMessage();
	   	msg.setMessageCommand(MessageCommand.Enum.forString(action.toString()));
	   	zw.co.esolutions.mcommerce.msg.Tariff t = msg.addNewTariff(); 
	   	
	   	t.setId(tariff.getId());
	   	t.setOldTariffId(tariff.getOldTariffId());
	   	t.setLowerLimit(MoneyUtil.convertToDollars(tariff.getLowerLimit()));
	   	t.setTariffTableId(tariff.getTariffTable().getId());
	   	t.setUpperLimit(MoneyUtil.convertToDollars(tariff.getUpperLimit()));
	   	t.setValue(MoneyUtil.convertToDollars(tariff.getValue()));
	   	t.setValueType(ValueType.Enum.forString(tariff.getValueType().toString()));
	   	t.setDateCreated(DateUtil.convertToCalendar(DateUtil.convertToDate(tariff.getDateCreated())));
	   	
	   	MessageSync.sync(doc.toString());
	    
	   	return t;
	}
	
	public static void sync(String message) throws Exception {
		MessageSender.send(EWalletConstants.FROM_EWALLET_TO_SWITCH_REG_QUEUE, message.toString());
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
    	b.setDateCreated(DateUtil.convertToCalendar(DateUtil.convertToDate((XMLGregorianCalendar)bank.getDateCreated())));
    	
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
    	b.setDateCreated(DateUtil.convertToCalendar(DateUtil.convertToDate((XMLGregorianCalendar)branch.getDateCreated())));
    	b.setStatus(zw.co.esolutions.mcommerce.msg.BankBranch.Status.Enum.forString(branch.getStatus().toString()));
    	
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
	   	if (account.getBranch() != null) {
	   		ba.setBranchId(account.getBranch().getId());
	   	}
	   	ba.setDateCreated(DateUtil.convertToCalendar(DateUtil.convertToDate((XMLGregorianCalendar)account.getDateCreated())));
	   	ba.setStatus(Status.Enum.forString(account.getStatus().toString()));
	   	
	   	MessageSync.sync(doc.toString());
	    
	   	return ba;
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
	
	public static zw.co.esolutions.mcommerce.msg.Merchant populateAndSync(Merchant merchant, MessageAction action) throws Exception {
	   	BankRegistrationMessageDocument doc = BankRegistrationMessageDocument.Factory.newInstance();
	   	BankRegistrationMessage msg = doc.addNewBankRegistrationMessage();
	   	msg.setMessageCommand(MessageCommand.Enum.forString(action.toString()));
	   	zw.co.esolutions.mcommerce.msg.Merchant m = msg.addNewMerchant();
	   	
	   	m.setId(merchant.getId());
	   	m.setContactDetailsId(merchant.getContactDetailsId());
	   	m.setStatus(zw.co.esolutions.mcommerce.msg.Merchant.Status.Enum.forString(merchant.getStatus().name()));
	   	m.setName(merchant.getName());
	   	m.setShortName(merchant.getShortName());
	   	m.setDateCreated(DateUtil.convertToCalendar(DateUtil.convertToDate(merchant.getDateCreated())));
	   
	   	MessageSync.sync(doc.toString());
	    
	   	return m;
	}
	
}
