/**
 * 
 */
package zw.co.esolutions.ewallet.reports;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountType;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.NumberUtil;

/**
 * @author taurai
 *
 */
public class ViewAccountStatementBean extends PageCodeBase{

	private String accountNumber;
	private List<SelectItem> bankMenu;
	private String bankItem;
	private List<Bank> bankList;
	private List<SelectItem> accTypeMenu;
	private String accTypeItem;
	private List<BankAccount> accountList;
	
	private String accountName;
	/**
	 * 
	 */
	public ViewAccountStatementBean() {
		
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	
	
	/*
	 * Method for missing fields
	 */
	private String checkAttributes(){
		StringBuffer buffer = new StringBuffer("The following values are required: ");
		int length = 0;
		if(this.getBankItem() == null || "nothing".equals(this.getBankItem())) {
			buffer.append("BANK, ");
		}
		
		length = buffer.toString().length();
				
		if(length > 40) {
			buffer.replace(length-2, length, " ");
			return buffer.toString();
		}
		return null;
	}
	
	public String search() {
		if("nothing".equalsIgnoreCase(this.getBankItem())) {
			this.setAccountList(null);
			super.setErrorMessage("No banks, you cannot continue.");
			return "failure";
		}
		/*if( (this.getAccountNumber() == null || "".equalsIgnoreCase(this.getAccountNumber())) && 
				("none".equalsIgnoreCase(this.getAccTypeItem())) ) {
			super.setErrorMessage("To continue, either Account Type or Account Number is required or both.");
			return "failure";
		}*/
		try {
			
			BankServiceSOAPProxy bs = new BankServiceSOAPProxy();
			
			if("".equalsIgnoreCase(this.getAccountName())) {
				this.setAccountName(null);
			}
			if("".equalsIgnoreCase(this.getAccountNumber())) {
				this.setAccountNumber(null);
			}
			
			// Acc Type is provided but no account Number
			if( (!"none".equalsIgnoreCase(this.getAccTypeItem())) && (this.getAccountNumber() == null || this.getAccountName() == null) ) {
				
				// Customer Accounts Require Account Number Notify
				if((isCustomerAccount() || isAgentAccount(BankAccountType.valueOf(this.getAccTypeItem())))&& (this.getAccountName() == null && this.getAccountNumber() == null)) {
					this.setAccountList(null);
					super.setErrorMessage("Accounts of type "+this.getAccTypeItem().replace("_", " ")+" require Account number or Account name.");
					return "failure";
				}
				
							
			} 
			
			BankAccountType type = null;
			if(!"none".equalsIgnoreCase(this.getAccTypeItem())) {
				type = BankAccountType.valueOf(this.getAccTypeItem());
			}
			if(this.getAccountNumber() != null){
				if(NumberUtil.validateMobileNumber(this.getAccountNumber())){
					this.setAccountNumber(NumberUtil.formatMobileNumber(this.getAccountNumber()));
				}
			}
			if(!"all".equalsIgnoreCase(this.getBankItem())) {
				this.setAccountList(bs.getBankAccountsByMinAttributes(this.getBankItem(), null, type, this.getAccountNumber(), this.getAccountName(), null, 0, 0));
			} else {
				this.setAccountList(bs.getBankAccountsByMinAttributes(null, null, type, this.getAccountNumber(), this.getAccountName(), null, 0, 0));
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		if(this.getAccountList() == null || this.getAccountList().isEmpty()) {
			this.setAccountList(null);
			super.setErrorMessage("No Account found.");
			return "failure";
		}
		super.setInformationMessage("Account(s) found.");
		return "success";
	}
	
	public String submit() {
		
		String msg = this.checkAttributes();
		if(msg != null) {
			//report error
			super.setErrorMessage(msg);
			return "failure";
		}
		//super.getRequestScope().put("accountId",super.getRequestParam().get("accountId"));
		//super.getRequestScope().put("bankId", this.getBankItem());
		super.gotoPage("/reportsweb/generateAccountStatement.jspx");
		//this.clear();
		return "success";
	}
	
	public List<SelectItem> getBankMenu() {
		if(this.bankMenu == null) {
			bankMenu = new ArrayList<SelectItem>();
			if(this.getBankList() == null || this.getBankList().isEmpty()) {
				bankMenu.add(new SelectItem("nothing", "No Banks"));
			} else {
				bankMenu.add(new SelectItem("all", "All"));
				for(Bank bk : this.getBankList()) {
					bankMenu.add(new SelectItem(bk.getId(),bk.getName()));
				}
			}
		}
		return bankMenu;
	}
	public void setBankMenu(List<SelectItem> bankMenu) {
		this.bankMenu = bankMenu;
	}
	public String getBankItem() {
		return bankItem;
	}
	public void setBankItem(String bankItem) {
		this.bankItem = bankItem;
	}
	public List<Bank> getBankList() {
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		if(this.bankList == null || this.bankList.isEmpty()) {
			try {
				List<Bank> banks = bankService.getBank();
				if(banks != null && !banks.isEmpty())  {
					bankList = new ArrayList<Bank>();
					for(Bank bk : banks) {
						if(bk.getName().contains(EWalletConstants.SYSTEM_BANKS_DELIMITER)) {
							bankList.add(bk);
						}
					}
				}
			} catch (Exception e) {
				
			}
		}
		return bankList;
	}

	public void setBankList(List<Bank> bankList) {
		this.bankList = bankList;
	}
	/**
	 * @param accTypeItem the accTypeItem to set
	 */
	public void setAccTypeItem(String accTypeItem) {
		this.accTypeItem = accTypeItem;
	}
	/**
	 * @return the accTypeItem
	 */
	public String getAccTypeItem() {
		return accTypeItem;
	}
	/**
	 * @param accTypeMenu the accTypeMenu to set
	 */
	public void setAccTypeMenu(List<SelectItem> accTypeMenu) {
		this.accTypeMenu = accTypeMenu;
	}
	/**
	 * @return the accTypeMenu
	 */
	public List<SelectItem> getAccTypeMenu() {
		if(accTypeMenu == null || accTypeMenu.isEmpty()) {
			accTypeMenu = new ArrayList<SelectItem>();
			accTypeMenu.add(new SelectItem("none", "--select--"));
			//accTypeMenu.add(new SelectItem(BankAccountType.POOL_CONTROL.toString(), BankAccountType.POOL_CONTROL.toString().replace("_", " ")));
			accTypeMenu.add(new SelectItem(BankAccountType.PAYOUT_CONTROL.toString(), BankAccountType.PAYOUT_CONTROL.toString().replace("_", " ")));
			accTypeMenu.add(new SelectItem(BankAccountType.BRANCH_CASH_ACCOUNT.toString(), BankAccountType.BRANCH_CASH_ACCOUNT.toString().replace("_", " ")));
			accTypeMenu.add(new SelectItem(BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT.toString(), BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT.toString().replace("_", " ")));
			for(BankAccountType acc : BankAccountType.values()) {
				if(!(BankAccountType.POOL_CONTROL.equals(acc) || 
						BankAccountType.PAYOUT_CONTROL.equals(acc) ||
						BankAccountType.BRANCH_CASH_ACCOUNT.equals(acc) ||
						BankAccountType.SUSPENSE.equals(acc) ||
						BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT.equals(acc))) {
					accTypeMenu.add(new SelectItem(acc.toString(), acc.toString().replace("_", " ")));
				}
			}
		}
		return accTypeMenu;
	}
	/**
	 * @param accountList the accountList to set
	 */
	public void setAccountList(List<BankAccount> accountList) {
		this.accountList = accountList;
	}
	/**
	 * @return the accountList
	 */
	public List<BankAccount> getAccountList() {
		return accountList;
	}
	
	private boolean isCustomerAccount() {
		return (BankAccountType.CHEQUE.equals(BankAccountType.valueOf(this.getAccTypeItem())) || 
				BankAccountType.CURRENT.equals(BankAccountType.valueOf(this.getAccTypeItem())) || 
				BankAccountType.SAVINGS.equals(BankAccountType.valueOf(this.getAccTypeItem())) || 
				BankAccountType.E_WALLET.equals(BankAccountType.valueOf(this.getAccTypeItem())));
	}
	/**
	 * @param accountName the accountName to set
	 */
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	/**
	 * @return the accountName
	 */
	public String getAccountName() {
		return accountName;
	}
	
	/*private void clear() {
		this.setAccountList(null);
		this.setAccountName(null);
		this.setAccountNumber(null);
		this.setAccTypeItem(null);
	}*/
}
