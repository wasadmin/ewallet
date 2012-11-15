package zw.co.esolutions.ewallet.csrweb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.agentservice.service.Agent;
import zw.co.esolutions.ewallet.agentservice.service.AgentServiceSOAPProxy;
import zw.co.esolutions.ewallet.agentservice.service.Exception_Exception;
import zw.co.esolutions.ewallet.agentservice.service.ProfileStatus;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountType;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.csr.msg.MessageAction;
import zw.co.esolutions.ewallet.csr.msg.MessageSync;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;

public class EditAccountBean extends PageCodeBase{
	
	private String bankAccountId;
	private BankAccount bankAccount;
	private List<SelectItem> accountTypeList;
	private String selectedAccountType;
	private List<SelectItem> branchList;
	private String selectedBranch;
	String accountName;
	String accountNumber;
	
	
	public String getBankAccountId() {
		if (bankAccountId == null) {
			bankAccountId = (String) super.getRequestParam().get("accountId");
		}
		return bankAccountId;
	}
	public void setBankAccountId(String bankAccountId) {
		this.bankAccountId = bankAccountId;
	}
	public BankAccount getBankAccount() {
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		if (bankAccount == null || bankAccount.getId() == null) {
			if (getBankAccountId() != null) {
				bankAccount = bankService.findBankAccountById(getBankAccountId());
				this.setSelectedAccountType(bankAccount.getType().name());
				this.setSelectedBranch(bankAccount.getBranch().getId());
				this.setAccountName(bankAccount.getAccountName());
				this.setAccountNumber(bankAccount.getAccountNumber());
			} else {
				bankAccount = new BankAccount();
			}
		}
		return bankAccount;
	}
	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}
	
	public List<SelectItem> getAccountTypeList() {
		if (accountTypeList == null) {
			accountTypeList = new ArrayList<SelectItem>();
			accountTypeList.add(new SelectItem("none", "--select--"));
			accountTypeList.add(new SelectItem("E_WALLET", "E_WALLET"));
			accountTypeList.add(new SelectItem("AGENT_EWALLET", "AGENT EWALLET ACCOUNT"));
			accountTypeList.add(new SelectItem("AGENT_COMMISSION_SUSPENSE", "COMMISSION ACCOUNT"));
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
		if(selectedAccountType == null){
			selectedAccountType = getBankAccount().getType().name();
		}
		return selectedAccountType;
	}
	public void setSelectedAccountType(String selectedAccountType) {
		this.selectedAccountType = selectedAccountType;
	}
	public List<SelectItem> getBranchList() {
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		if (branchList == null) {
			branchList = new ArrayList<SelectItem>();
			branchList.add(new SelectItem("none", "--select--"));
			try {
				Profile profile = profileService.getProfileByUserName(super.getJaasUserName());
				BankBranch b = bankService.findBankBranchById(profile.getBranchId());
				List<BankBranch> branches = bankService.getBankBranchByBank(b.getBank().getId());
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
	
	public String submit() {
		if(selectedAccountType.equals("none")){
			setErrorMessage("Select Account Type");
			return "failure";
		}
		if(selectedBranch.equals("none")){
			setErrorMessage("Select Branch");
			return "failure";
		}
		
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		
		BankAccount account = getBankAccount();

		account.setType(BankAccountType.valueOf(selectedAccountType));
		account.setBranch(bankService.findBankBranchById(selectedBranch));
		
		try {
			BankAccount b = getBankService().getUniqueBankAccountByAccountNumber(getBankAccount().getAccountNumber());
	
			if(b!=null && b.getId() != null ){
				if(!b.getId().equals(getBankAccount().getId())){
					setErrorMessage("Invalid account number ");
					return "failure";
				}
				
			}
			
			Agent a = agentService.getAgentByCustomerId(getBankAccount().getAccountHolderId());
			a.setStatus(ProfileStatus.AWAITING_EDIT_APPROVAL);
			agentService.updateAgent(a, getJaasUserName());
			
			bankService.editBankAccount(account, super.getJaasUserName());
			MessageSync.populateAndSync(getBankAccount(), MessageAction.UPDATE);
			MessageSync.populateAndSync(a, MessageAction.UPDATE);
		} catch (Exception e) { 
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		
		super.setInformationMessage("Bank Account updated successfully");
		cleanUpSessionScope();
		super.getRequestScope().remove("agentId");
		nextPage();
		return "submit";
	}
	
	public String cancel() {
		nextPage();
		return "cancel";
	}
	
	@SuppressWarnings("unchecked")
	public String nextPage() {
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		Agent agent = null;
		try {
			agent = agentService.getAgentByCustomerId(getBankAccount().getAccountHolderId());
		} catch (Exception_Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.getRequestScope().put("agentId", agent.getId());
		this.gotoPage("/csr/viewAgent.jspx");
//		if(agent.getAgentLevel().equals(AgentLevel.CORPORATE)){
//			if(agent.getAgentType().equals(AgentType.SUB_AGENT)){
//				this.gotoPage("/admin/viewCorporateSubAgent.jspx");
//			}else{
//				this.gotoPage("/admin/viewCorporateAgent.jspx");
//			}
//		}else{
//			if(agent.getAgentType().equals(AgentType.SUB_AGENT)){
//				this.gotoPage("/admin/viewIndSubAgent.jspx");
//			}else{
//				this.gotoPage("/admin/viewAgent.jspx");
//			}
//		}
		
		return "viewAgent";
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
	
}
