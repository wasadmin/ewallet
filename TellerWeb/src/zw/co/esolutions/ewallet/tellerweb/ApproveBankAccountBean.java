package zw.co.esolutions.ewallet.tellerweb;

import java.util.ArrayList;
import java.util.List;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountLevel;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountStatus;
import zw.co.esolutions.ewallet.profileservices.service.Profile;

public class ApproveBankAccountBean extends PageCodeBase {
	private List<BankAccount> bankAccountList;
	private Profile profile;
	
	public Profile getProfile() {
		if(this.profile == null) {
			try {
				this.profile = super.getProfileService().getProfileByUserName(super.getJaasUserName());
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return profile;
	}
	public void setProfile(Profile profile) {
		this.profile = profile;
	} 
	
	public List<BankAccount> getBankAccountList() {
		if (bankAccountList == null) {
			bankAccountList = super.getBankService().getBankAccountByLevelAndStatus(BankAccountLevel.INDIVIDUAL, BankAccountStatus.AWAITING_APPROVAL);
			if (bankAccountList == null || bankAccountList.isEmpty()) {
				super.setInformationMessage("No bank accounts awaiting approval.");
				bankAccountList = new ArrayList<BankAccount>();
			} else {
				super.setInformationMessage(bankAccountList.size() + " bank accounts found.");
			}
		}
		return bankAccountList;
	}
	public void setBankAccountList(List<BankAccount> bankAccountList) {
		this.bankAccountList = bankAccountList;
	}
	
	public String viewCustomer() {
		gotoPage("/teller/viewCustomer.jspx");
		return "viewCustomer";
	}
}
