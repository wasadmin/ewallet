package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityExistsException;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountClass;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountLevel;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountStatus;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountType;
import zw.co.esolutions.ewallet.msg.MessageAction;
import zw.co.esolutions.ewallet.msg.MessageSync;

public class CreateBankSuspenseAccountBean extends PageCodeBase {
	
	private List<SelectItem> accountHolderList;
	private List<SelectItem> accountForList;
	private String selectedAccountHolder;
	private String selectedAccountFor;
	private String accountNumber;
	
	
	public List<SelectItem> getAccountHolderList() {
		if (accountHolderList == null || accountHolderList.get(0) == null) {
			accountHolderList = new ArrayList<SelectItem>();
			accountHolderList.add(new SelectItem("none", "--Select--"));

			List<Bank> banks = super.getBankService().getBank();
			if (banks != null) {
				for (Bank bank: banks) {
					accountHolderList.add(new SelectItem(bank.getId(), bank.getName()));
				}
			}
		} 
		return accountHolderList;
	}
	public void setAccountHolderList(List<SelectItem> accountHolderList) {
		this.accountHolderList = accountHolderList;
	}
	
	public String getSelectedAccountHolder() {
		return selectedAccountHolder;
	}
	public void setSelectedAccountHolder(String selectedAccountHolder) {
		this.selectedAccountHolder = selectedAccountHolder;
	}
	
	public void setAccountForList(List<SelectItem> accountForList) {
		this.accountForList = accountForList;
	}
	public List<SelectItem> getAccountForList() {
		if (accountForList == null || accountForList.isEmpty()) {
			accountForList = new ArrayList<SelectItem>();
			accountForList.add(new SelectItem("none", "--Select--"));

			List<Bank> banks = super.getBankService().getBank();
			if (banks != null) {
				for (Bank bank: banks) {
					accountForList.add(new SelectItem(bank.getId(), bank.getName()));
				}
			}
		} 
		return accountForList;
	}
	public void setSelectedAccountFor(String selectedAccountFor) {
		this.selectedAccountFor = selectedAccountFor;
	}
	public String getSelectedAccountFor() {
		return selectedAccountFor;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	
	public String submit() {
		if ("none".equals(selectedAccountHolder)) {
			super.setErrorMessage("Please select the account holder.");
			return "failure";
		}
		if ("none".equals(selectedAccountFor)) {
			super.setErrorMessage("Please select the targeted bank.");
			return "failure";
		}
		@SuppressWarnings("unused")
		Bank sourceBank = super.getBankService().findBankById(selectedAccountHolder);
		Bank targetBank = super.getBankService().findBankById(selectedAccountFor);
		BankAccount account = new BankAccount();
		try {
			account.setAccountClass(BankAccountClass.SYSTEM);
			account.setAccountHolderId(selectedAccountHolder);
			account.setAccountNumber(accountNumber);
			account.setLevel(BankAccountLevel.CORPORATE);
			account.setStatus(BankAccountStatus.ACTIVE);
			account.setType(BankAccountType.SUSPENSE);
			account.setBankReferenceId(selectedAccountFor);
			account.setAccountName(targetBank.getName());
			
			account = super.getBankService().createBankAccount(account, super.getJaasUserName());
			MessageSync.populateAndSync(account, MessageAction.CREATE);
		} catch (EntityExistsException e) {
			super.setErrorMessage("Sorry. This account already exists.");
			return "failure";
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage(account.getType() + " Account successfully created.");
		return "submit";
	}
	
	public String cancel() {
		super.gotoPage("/admin/adminHome.jspx");
		return "cancel";
	}
}
