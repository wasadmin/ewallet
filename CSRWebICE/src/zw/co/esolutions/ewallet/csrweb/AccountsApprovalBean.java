package zw.co.esolutions.ewallet.csrweb;

import java.util.ArrayList;
import java.util.List;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountStatus;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;

public class AccountsApprovalBean extends PageCodeBase{
	
	private List<BankAccount> accountList = new ArrayList<BankAccount>();

	public List<BankAccount> getAccountList() {
		BankServiceSOAPProxy bankService = getBankService();
		List<BankAccount> tmpList = null;
		try{
			tmpList = bankService.getBankAccountByStatus(BankAccountStatus.AWAITING_APPROVAL);
			if(tmpList!= null && !tmpList.isEmpty()){
				for(BankAccount ba : tmpList){
					if(OwnerType.CUSTOMER.equals(ba.getOwnerType())){
						accountList.add(ba);
					}
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		return accountList;
	}

	public void setAccountList(List<BankAccount> accountList) {
		this.accountList = accountList;
	}
	
	@SuppressWarnings("unchecked")
	public String viewCustomer() {

		BankServiceSOAPProxy bankService = getBankService();
		String customerId = null;
		try{
			String bankAccountId = (String)super.getRequestParam().get("bankAccountId");
			BankAccount ba = bankService.findBankAccountById(bankAccountId);
			customerId = ba.getAccountHolderId();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		super.getRequestScope().put("customerId",customerId );
		super.gotoPage("/csr/viewCustomer.jspx");
		return "viewCustomer";
	}
	
}
