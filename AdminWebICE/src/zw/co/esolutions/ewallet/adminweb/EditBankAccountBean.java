package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountClass;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountLevel;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountType;
import zw.co.esolutions.ewallet.msg.MessageAction;
import zw.co.esolutions.ewallet.msg.MessageSync;

public class EditBankAccountBean extends PageCodeBase {
	
	private String bankAccountId;
	private BankAccount bankAccount;
	private List<SelectItem> accountTypeList;
	private List<SelectItem> accountLevelList;
	private List<SelectItem> accountClassList;
	private String selectedLevel;
	private String selectedClass;
	private String selectedAccountType;
	
	public void setBankAccountId(String bankAccountId) {
		this.bankAccountId = bankAccountId;
	}
	public String getBankAccountId() {
		return bankAccountId;
	}
	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}
	public BankAccount getBankAccount() {
		if (bankAccount == null || bankAccount.getId() == null) {
			if (this.getBankAccountId() != null) {
				bankAccount = super.getBankService().findBankAccountById(bankAccountId);
				this.setSelectedAccountType(bankAccount.getType().name());
				this.setSelectedClass(bankAccount.getAccountClass().name());
				this.setSelectedLevel(bankAccount.getLevel().name());
			} else {
				bankAccount = new BankAccount();
			}
		}
		return bankAccount;
	}
	
	public List<SelectItem> getAccountTypeList() {
		if (accountTypeList == null) {
			accountTypeList = new ArrayList<SelectItem>();
			accountTypeList.add(new SelectItem("none", "--Select--"));
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
	public void setAccountLevelList(List<SelectItem> accountLevelList) {
		this.accountLevelList = accountLevelList;
	}
	public List<SelectItem> getAccountLevelList() {
		if (accountLevelList == null) {
			accountLevelList = new ArrayList<SelectItem>();
			accountLevelList.add(new SelectItem("none", "--Select--"));
			for (BankAccountLevel level: BankAccountLevel.values()) {
				accountLevelList.add(new SelectItem(level.name(), level.name()));
			}
		}
		return accountLevelList;
	}
	public void setAccountClassList(List<SelectItem> accountClassList) {
		this.accountClassList = accountClassList;
	}
	public List<SelectItem> getAccountClassList() {
		if (accountClassList == null) {
			accountClassList = new ArrayList<SelectItem>();
			accountClassList.add(new SelectItem("none", "--Select--"));
			for (BankAccountClass accountClass: BankAccountClass.values()) {
				accountClassList.add(new SelectItem(accountClass.name(), accountClass.name()));
			}
		}
		return accountClassList;
	}

	public String getSelectedAccountType() {
		return selectedAccountType;
	}
	public void setSelectedAccountType(String selectedAccountType) {
		this.selectedAccountType = selectedAccountType;
	}
	
	public void setSelectedLevel(String selectedLevel) {
		this.selectedLevel = selectedLevel;
	}
	public String getSelectedLevel() {
		return selectedLevel;
	}
	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}
	public String getSelectedClass() {
		return selectedClass;
	}
	
	public void doEditAction(ActionEvent event) {
		bankAccountId = (String) event.getComponent().getAttributes().get("bankAccountId");
	}
	
	@SuppressWarnings("unchecked")
	public String submit() {
	
		if ("none".equals(selectedAccountType)) {
			super.setErrorMessage("Please select the account type.");
			return "failure";
		}
		if ("none".equals(selectedClass)) {
			super.setErrorMessage("Please select the account class.");
			return "failure";
		}
		if ("none".equals(selectedLevel)) {
			super.setErrorMessage("Please select the account level.");
			return "failure";
		}
		
		try {
			this.getBankAccount().setAccountClass(BankAccountClass.valueOf(selectedClass));
			this.getBankAccount().setLevel(BankAccountLevel.valueOf(selectedLevel));
			this.getBankAccount().setType(BankAccountType.valueOf(selectedAccountType));
						
			bankAccount = super.getBankService().editBankAccount(bankAccount, super.getJaasUserName());
			MessageSync.populateAndSync(bankAccount, MessageAction.UPDATE);
		}  catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage(bankAccount.getType() + " Account updated successfully.");
		super.getRequestScope().put("bankAccount", bankAccount.getId());
		super.gotoPage("admin/viewBankAccount.jspx");
		return "submit";
	}
	
	@SuppressWarnings("unchecked")
	public String cancel() {
		super.getRequestScope().put("bankAccount", this.getBankAccount().getId());
		super.gotoPage("admin/viewBankAccount.jspx");
		return "cancel";
	}
	
}
