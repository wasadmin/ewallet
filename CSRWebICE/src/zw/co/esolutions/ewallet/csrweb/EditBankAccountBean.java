package zw.co.esolutions.ewallet.csrweb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountClass;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountStatus;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountType;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.csr.msg.MessageAction;
import zw.co.esolutions.ewallet.csr.msg.MessageSync;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;

public class EditBankAccountBean extends PageCodeBase {
	private String bankAccountId;
	private BankAccount bankAccount;
	private List<SelectItem> accountClassList;
	private String selectedAccountClass;
	private List<SelectItem> accountTypeList;
	private String selectedAccountType;
	private List<SelectItem> branchList;
	private String selectedBranch;
	
	public String getBankAccountId() {
		//if (bankAccountId == null) {
			bankAccountId = (String) super.getRequestParam().get("accountId");
		//}
		return bankAccountId;
	}
	public void setBankAccountId(String bankAccountId) {
		this.bankAccountId = bankAccountId;
	}
	public BankAccount getBankAccount() {
		if (bankAccount == null || bankAccount.getId() == null) {
			if (getBankAccountId() != null) {
				bankAccount = super.getBankService().findBankAccountById(getBankAccountId());
				this.setSelectedAccountClass(bankAccount.getAccountClass().name());
				this.setSelectedAccountType(bankAccount.getType().name());
				this.setSelectedBranch(bankAccount.getBranch().getId());
				//System.out.println(">>>>>>>>>Accont Name : " + bankAccount.getAccountName());
			} else {
				bankAccount = new BankAccount();
			}
		}
		return bankAccount;
	}
	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}
	public List<SelectItem> getAccountClassList() {
		if (accountClassList == null) {
			accountClassList = new ArrayList<SelectItem>();
			for (BankAccountClass accountClass: BankAccountClass.values()) {
				if (accountClass.equals(BankAccountClass.SYSTEM)) {
					continue;
				}
				accountClassList.add(new SelectItem(accountClass.name(), accountClass.name()));
			}
		}
		return accountClassList;
	}
	public void setAccountClassList(List<SelectItem> accountClassList) {
		this.accountClassList = accountClassList;
	}
	public String getSelectedAccountClass() {
		return selectedAccountClass;
	}
	public void setSelectedAccountClass(String selectedAccountClass) {
		this.selectedAccountClass = selectedAccountClass;
	}
	public List<SelectItem> getAccountTypeList() {
		if (accountTypeList == null) {
			accountTypeList = new ArrayList<SelectItem>();
			accountTypeList.add(new SelectItem("none", "--select--"));
			accountTypeList.add(new SelectItem("E_WALLET", "E_WALLET"));
			accountTypeList.add(new SelectItem("SAVINGS", "SAVINGS"));
			accountTypeList.add(new SelectItem("CHEQUE", "CHEQUE"));
			accountTypeList.add(new SelectItem("CURRENT", "CURRENT"));
		}
		return accountTypeList;
	}
	public void setAccountTypeList(List<SelectItem> accountTypeList) {
		this.accountTypeList = accountTypeList;
	}
	public String getSelectedAccountType() {
		return selectedAccountType;
	}
	public void setSelectedAccountType(String selectedAccountType) {
		this.selectedAccountType = selectedAccountType;
	}
	public List<SelectItem> getBranchList() {
		if (branchList == null || branchList.isEmpty()) {
			branchList = new ArrayList<SelectItem>();
			branchList.add(new SelectItem("none", "--select--"));
			try {
				Profile profile = super.getProfileService().getProfileByUserName(super.getJaasUserName());
				BankBranch b = super.getBankService().findBankBranchById(profile.getBranchId());
				List<BankBranch> branches = super.getBankService().getBankBranchByBank(b.getBank().getId());
				if (branches != null) {
					for (BankBranch branch: branches) {
						branchList.add(new SelectItem(branch.getId(), branch.getName()));
					}
				}
			} catch (Exception e) {
				
			}
		}
		return branchList;
	}
	public void setBranchList(List<SelectItem> branchList) {
		this.branchList = branchList;
	}
	public String getSelectedBranch() {
		return selectedBranch;
	}
	public void setSelectedBranch(String selectedBranch) {
		this.selectedBranch = selectedBranch;
	}
	
	@SuppressWarnings("unchecked")
	public String submit() {
		getBankAccount().setAccountClass(BankAccountClass.valueOf(selectedAccountClass));
		getBankAccount().setType(BankAccountType.valueOf(selectedAccountType));
		getBankAccount().setBranch(super.getBankService().findBankBranchById(selectedBranch));
		
		try {
			ProfileServiceSOAPProxy profileService=new ProfileServiceSOAPProxy();
			Profile capturerProfile=profileService.getProfileByUserName(getJaasUserName());
			getBankAccount().setBankAccountLastBranch(capturerProfile.getBranchId());
			getBankAccount().setStatus(BankAccountStatus.AWAITING_APPROVAL);
			super.getBankService().editBankAccount(bankAccount, super.getJaasUserName());
			MessageSync.populateAndSync(bankAccount, MessageAction.UPDATE);
		} catch (Exception e) { 
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage("Bank Account updated successfully");
		super.getRequestScope().put("customerId", getBankAccount().getAccountHolderId());
		gotoPage("/csr/viewCustomer.jspx");
		return "submit";
	}
	
	@SuppressWarnings("unchecked")
	public String cancel() {
		super.getRequestScope().put("customerId", getBankAccount().getAccountHolderId());
		gotoPage("/csr/viewCustomer.jspx");
		return "cancel";
	}
	
}
