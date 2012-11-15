package zw.co.esolutions.ewallet.adminweb;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;

public class ViewBankAccountBean extends PageCodeBase {
	
	private String bankAccountId;
	private BankAccount bankAccount;
		
	public void setBankAccountId(String bankAccountId) {
		this.bankAccountId = bankAccountId;
	}
	public String getBankAccountId() {
		if (bankAccountId == null) {
			bankAccountId = (String) super.getRequestParam().get("bankAccountId");
		}
		if (bankAccountId == null) {
			bankAccountId = (String) super.getRequestScope().get("bankAccountId");
		}
		return bankAccountId;
	}
		
	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}
	public BankAccount getBankAccount() {
		if (bankAccount == null && this.getBankAccountId() != null) {
			bankAccount = super.getBankService().findBankAccountById(bankAccountId);
		}
		return bankAccount;
	}
	public String ok() {
		super.gotoPage("/admin/searchBankAccount.jspx");
		return "ok";
	}
	
	public String edit() {
		super.gotoPage("/admin/editBankAccount.jspx");
		return "edit";
	}
	
	public String logs(){
		super.gotoPage("/admin/viewLogs.jspx");
		return "logs";
	}
	
	public boolean isEditable(){
		if(super.getProfileService().canDo(super.getJaasUserName(), "EDITBANKACCOUNT")){
			return true;
		}else{
			return false;
		}
	}
}
