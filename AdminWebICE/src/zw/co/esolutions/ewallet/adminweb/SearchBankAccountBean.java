package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.merchantservices.service.Merchant;

public class SearchBankAccountBean extends PageCodeBase {
	
	private List<SelectItem> ownerTypeList;
	private List<SelectItem> accountHolderList;
	private String selectedOwnerType;
	private String selectedAccountHolder;
	private List<BankAccount> accountList;
		
	public List<SelectItem> getOwnerTypeList() {
		if (ownerTypeList == null) {
			ownerTypeList = new ArrayList<SelectItem>();
			ownerTypeList.add(new SelectItem("none", "--Select--"));
			
			for (OwnerType type: OwnerType.values()) {
				if (type.equals(OwnerType.CUSTOMER) || type.equals(OwnerType.AGENT) || type.equals(OwnerType.BANK_MERCHANT)
						|| type.equals(OwnerType.MERCHANT)) {
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
			accountHolderList.add(new SelectItem("none", "--Select--"));
			
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
			} else if(OwnerType.MERCHANT.name().equals(selectedOwnerType)){
				try{
					List<Merchant> merchants = super.getMerchantService().getAllMerchants();
					if(merchants != null){
						for (Merchant merchant: merchants) {
							accountHolderList.add(new SelectItem(merchant.getId(), merchant.getName()));
						}
					}
				}catch (Exception e) {
					e.printStackTrace(System.out);
				}
				
			}else {
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
	
	public void setAccountList(List<BankAccount> accountList) {
		this.accountList = accountList;
	}
	public List<BankAccount> getAccountList() {
		return accountList;
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
		}  else if (OwnerType.MERCHANT.name().equals(ownerType)) {
			try{
				List<Merchant> merchants = super.getMerchantService().getAllMerchants();
				if(merchants != null){
					for (Merchant merchant: merchants) {
						accountHolderList.add(new SelectItem(merchant.getId(), merchant.getName()));
					}
				}
			}catch (Exception e) {
				e.printStackTrace(System.out);
			}
		}
		this.getAccountHolderList();
	}
	
	public String submit() {
		if ("none".equals(selectedOwnerType)) {
			super.setErrorMessage("Please select the owner type.");
			return "failure";
		}
		if ("none".equals(selectedAccountHolder)) {
			super.setErrorMessage("Please select the account holder.");
			return "failure";
		}
			
		try {
			
			accountList = super.getBankService().getBankAccountByAccountHolderIdAndOwnerType(selectedAccountHolder, zw.co.esolutions.ewallet.bankservices.service.OwnerType.valueOf(selectedOwnerType));
			if (accountList == null) {
				accountList = new ArrayList<BankAccount>();
				super.setInformationMessage("Oops! No results found.");
				return "failure";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage("Account(s) found.");
		return "submit";
	}
	
	@SuppressWarnings("unchecked")
	public String viewBankAccount() {
		String accountId = (String) super.getRequestParam().get("bankAccountId");
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>> " + accountId);
		super.getRequestScope().put("bankAccountId", accountId);
		super.gotoPage("/admin/viewBankAccount.jspx");
		return "viewBankAccount";
	}
	
}
