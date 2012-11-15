package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.List;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.msg.MessageAction;
import zw.co.esolutions.ewallet.msg.MessageSync;

public class ApproveBankAccountBean extends PageCodeBase{

	private List<BankAccount> accountList;
	private BankAccount bankAccount;
	
	public List<BankAccount> getAccountList() {
		
			try{
				accountList = super.getBankService().getBankAccountsAwaitingApproval();
				
				if(accountList==null){
					accountList = new ArrayList<BankAccount>();
				}
				
			}catch(Exception e){e.printStackTrace();}
		
		return accountList;
	}
	public void setAccountList(List<BankAccount> accountList) {
		this.accountList = accountList;
	}
	
	public BankAccount getBankAccount() {
		if(bankAccount==null){
			String bankAccountId = (String)super.getRequestParam().get("bankAccountId");
			bankAccount = super.getBankService().findBankAccountById(bankAccountId);
		}
		return bankAccount;
	}
	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}
	
	public String goToApprovePage(){
		super.gotoPage("admin/approveBankAccountView.jspx");
		return "approvePage";
	}
	
	@SuppressWarnings("unchecked")
	public String approve(){
		try{
			bankAccount = super.getBankService().approveBankAccount(bankAccount, super.getJaasUserName());
			super.setInformationMessage(bankAccount.getAccountNumber()+" has been approved.");
			super.getRequestScope().put("bankAccountId", bankAccount.getId());
			super.gotoPage("admin/viewBankAccount.jspx");
			MessageSync.populateAndSync(bankAccount, MessageAction.UPDATE);
			accountList = null;
		}catch(Exception e){
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
		}
		return "approved";
	}
	
	@SuppressWarnings("unchecked")
	public String reject(){
		try{
			bankAccount = super.getBankService().rejectBankAccount(bankAccount, super.getJaasUserName());
			super.setInformationMessage(bankAccount.getAccountNumber()+" has been rejected.");
			super.getRequestScope().put("bankAccountId", bankAccount.getId());
			super.gotoPage("admin/viewBankAccount.jspx");
			MessageSync.populateAndSync(bankAccount, MessageAction.UPDATE);
			accountList = null;
		}catch(Exception e){
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
		}
		return "rejected";
	}
	
}
