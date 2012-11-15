package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.persistence.EntityExistsException;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountClass;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountLevel;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountStatus;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountType;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.merchantservices.service.Exception_Exception;
import zw.co.esolutions.ewallet.merchantservices.service.Merchant;
import zw.co.esolutions.ewallet.merchantservices.service.MerchantStatus;
import zw.co.esolutions.ewallet.msg.MessageAction;
import zw.co.esolutions.ewallet.msg.MessageSync;

public class CreateBankAccountBean extends PageCodeBase {
	
	private List<SelectItem> ownerTypeList;
	private List<SelectItem> accountHolderList;
	private List<SelectItem> accountTypeList;
	private String selectedOwnerType;
	private String selectedAccountHolder;
	private String accountName;
	private String accountNumber;
	private BankAccountType accountType;
	private String selectedAccountType;
	
	public List<SelectItem> getOwnerTypeList() {
		if (ownerTypeList == null) {
			ownerTypeList = new ArrayList<SelectItem>();
			ownerTypeList.add(new SelectItem("", "--Select--"));
			for (OwnerType type: OwnerType.values()) {
				if (type.equals(OwnerType.CUSTOMER)) {
					continue;
				}
				if (type.equals(OwnerType.BANK_MERCHANT)) {
					continue;
				}					
				ownerTypeList.add(new SelectItem(type.name(), type.name()));
			}
		}
		return ownerTypeList;
	}
	public void setOwnerTypeList(List<SelectItem> ownerTypeList) {
		this.ownerTypeList = ownerTypeList;
	}
	public List<SelectItem> getAccountHolderList() {
		if (accountHolderList == null || accountHolderList.get(0) == null) {
			accountHolderList = new ArrayList<SelectItem>();
			accountHolderList.add(new SelectItem("", "--Select--"));
			if (OwnerType.BANK.name().equals(selectedOwnerType)) {
				List<Bank> banks = super.getBankService().getBank();
				if (banks != null) {
					for (Bank bank: banks) {
						accountHolderList.add(new SelectItem(bank.getId(), bank.getName()));
					}
				}
			} else if (OwnerType.BANK_BRANCH.name().equals(selectedOwnerType)) {
				List<BankBranch> branches = super.getBankService().getBankBranch();
				if (branches != null) {
					for (BankBranch branch: branches) {
						accountHolderList.add(new SelectItem(branch.getId(), branch.getBank().getName() + ": " + branch.getName()));
					}
				}
			}
			
			else {
				List<Bank> banks = super.getBankService().getBank();
				if (banks != null) {
					for (Bank bank: banks) {
						accountHolderList.add(new SelectItem(bank.getId(), bank.getName()));
					}
				}
				List<BankBranch> branches = super.getBankService().getBankBranch();
				if (branches != null) {
					for (BankBranch branch: branches) {
						accountHolderList.add(new SelectItem(branch.getId(), branch.getName()));
					}
				}
			}
		} 
		return accountHolderList;
	}
	public void setAccountHolderList(List<SelectItem> accountHolderList) {
		this.accountHolderList = accountHolderList;
	}
	public List<SelectItem> getAccountTypeList() {
		if (accountTypeList == null) {
			accountTypeList = new ArrayList<SelectItem>();
			accountTypeList.add(new SelectItem("", "--Select--"));
			for(BankAccountType type:BankAccountType.values()){
				if(type.equals(BankAccountType.SUSPENSE)){
					continue;
				}
				accountTypeList.add(new SelectItem(type.name(),type.name()));
			}
			
		}
		return accountTypeList;
	}
	public void setAccountTypeList(List<SelectItem> accountTypeList) {
		this.accountTypeList = accountTypeList;
	}
	public String getSelectedOwnerType() {
		return selectedOwnerType;
	}
	public void setSelectedOwnerType(String selectedOwnerType) {
		this.selectedOwnerType = selectedOwnerType;
	}
	public String getSelectedAccountHolder() {
		return selectedAccountHolder;
	}
	public void setSelectedAccountHolder(String selectedAccountHolder) {
		this.selectedAccountHolder = selectedAccountHolder;
	}
		
	public BankAccountType getAccountType() {
		return accountType;
	}
	public void setAccountType(BankAccountType accountType) {
		this.accountType = accountType;
	}
	public String getSelectedAccountType() {
		return selectedAccountType;
	}
	public void setSelectedAccountType(String selectedAccountType) {
		this.selectedAccountType = selectedAccountType;
	}
	
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public void processOwnerTypeValueChange(ValueChangeEvent event) {
		String ownerType = (String) event.getNewValue();
		accountHolderList = new ArrayList<SelectItem>();
		accountHolderList.add(new SelectItem("none", "--Select--"));
		
		if (OwnerType.BANK.name().equals(ownerType)) {
			List<Bank> banks = super.getBankService().getBank();
			if (banks != null) {
				for (Bank bank: banks) {
					accountHolderList.add(new SelectItem(bank.getId(), bank.getName()));
				}
			}
		} else if (OwnerType.BANK_BRANCH.name().equals(ownerType)) {
			List<BankBranch> branches = super.getBankService().getBankBranch();
			if (branches != null) {
				for (BankBranch branch: branches) {
					accountHolderList.add(new SelectItem(branch.getId(), branch.getBank().getName() + ": " + branch.getName()));
				}
			}
		} 
		this.getAccountHolderList();
	}
	
	
	@SuppressWarnings("unchecked")
	public String submit() {
		if ("none".equals(selectedOwnerType)) {
			super.setErrorMessage("Please select the owner type.");
			return "failure";
		}
		if ("none".equals(selectedAccountHolder)) {
			super.setErrorMessage("Please select the account holder.");
			return "failure";
		}
		if ("none".equals(selectedAccountType)) {
			super.setErrorMessage("Please select the account type.");
			return "failure";
		}
		BankAccount account = new BankAccount();
		try {
			account.setAccountClass(BankAccountClass.SYSTEM);
			account.setAccountHolderId(selectedAccountHolder);
			//account.setAccountNumber(GenerateKey.generateEntityId());
			account.setAccountNumber(accountNumber);
			account.setLevel(BankAccountLevel.CORPORATE);
			account.setStatus(BankAccountStatus.AWAITING_APPROVAL);
			account.setType(BankAccountType.valueOf(selectedAccountType));
			//account.setAccountName(bank.getName());
			account.setAccountName(accountName);
			account.setOwnerType(OwnerType.valueOf(selectedOwnerType));
			
			BankAccount acc = super.getBankService().getBankAccountByAccountHolderAndTypeAndOwnerType(account.getAccountHolderId(), account.getType(), account.getOwnerType(), account.getAccountNumber());
			if(acc!=null){
				super.setErrorMessage("Sorry. This account already exists.");
				return "failure";
			}
			
			BankAccount accountCheck = super.getBankService().getUniqueBankAccountByAccountNumber(account.getAccountNumber());
			if (accountCheck != null) {
				super.setErrorMessage("Bank Account with account number " + account.getAccountNumber() + " already exists.");
				return "failure";
			}
			
			System.out.println(">>>>>>>>>>>>>>>"+account.getAccountHolderId());
			account = super.getBankService().createBankAccount(account, super.getJaasUserName());
			MessageSync.populateAndSync(account, MessageAction.CREATE);
			super.setInformationMessage(account.getType() + " Account successfully created.");
			super.getRequestScope().put("bankAccountId", account.getId());
			super.gotoPage("/admin/viewBankAccount.jspx");
		} catch (EntityExistsException e) {
			super.setErrorMessage("Sorry. This account already exists.");
			return "failure";
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		return "submit";
	}
	
	public String cancel() {
		super.gotoPage("/admin/adminHome.jspx");
		return "cancel";
	}
	
}
