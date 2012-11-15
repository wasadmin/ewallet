/**
 * 
 */
package zw.co.esolutions.ewallet.csrweb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;

/**
 * @author tauttee
 *
 */
public class ViewTellerDailyTransactionsBean extends PageCodeBase{

	//private ProcessServiceSOAPProxy processService = new ProcessServiceSOAPProxy();
	
	private List<SelectItem> txnTypeMenu;
	private String txnTypeItem;
	private Date txnDate;
	private String bankName;
	//private TransactionType transactionType;
	private String userName;
	private boolean disableRun;
	
	public ViewTellerDailyTransactionsBean() {
		super();
		if(this.bankName == null) {
			try {
				this.bankName = super.getBankService().findBankBranchById(
						super.getProfileService().getProfileByUserName(super.getJaasUserName()).getBranchId()).getBank().getName();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	
	
	@SuppressWarnings("unchecked")
	public String next() {
		String profileId = null;
		if(this.getTxnTypeItem() == null || this.getTxnTypeItem().equalsIgnoreCase("none")) {
			//Report Select transaction
			super.setErrorMessage("Transaction not selected.");
			return "failure";
		}
		if(this.getTxnDate() == null) {
			//Report date required;
			super.setErrorMessage("Date required.");
			return "failure";
		}
		
		//Check for a previousRun
		try {
			this.setUserName(super.getJaasUserName());
			profileId = super.getProfileService().getProfileByUserName(this.getUserName()).getId();
			if(profileId == null) {
				super.setErrorMessage("User name not recognized in the system.");
				return "failure";
			}
//			if(this.processService.isPreviousDayEndRun(bankName, profileId, 
//					DateUtil.convertToXMLGregorianCalendar(this.getTxnDate()), transactionType)) {
//				super.setInformationMessage("Day End Run for this teller already run for "+this.getTxnTypeItem());
//				this.setDisableRun(true);
//			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		super.getRequestScope().put("txnDate", this.getTxnDate());
		super.getRequestScope().put("disableRun", new Boolean(this.isDisableRun()));
		
		if(this.getTxnTypeItem().equalsIgnoreCase("WITHDRAWALS")) {
			super.gotoPage("/csr/runTellerDayWithdrawals.jspx");
		} if(this.getTxnTypeItem().equalsIgnoreCase("Deposits")) {
			super.gotoPage("/csr/runTellerDayDeposits.jspx");
		}
		
		return "success";
	}
	
	public String clear() {
		this.setTxnTypeItem(null);
		this.setTxnDate(new Date());
		return "success";
	}
	
	public List<SelectItem> getTxnTypeMenu() {
		if(this.txnTypeMenu == null) {
			this.txnTypeMenu = new ArrayList<SelectItem>();
			this.txnTypeMenu.add(new SelectItem("none", "<--select-->"));
			this.txnTypeMenu.add(new SelectItem("DEPOSITS", "DEPOSITS"));
			this.txnTypeMenu.add(new SelectItem("WITHDRAWALS", "WITHDRAWALS"));
		}
		return txnTypeMenu;
	}
	public void setTxnTypeMenu(List<SelectItem> txnTypeMenu) {
		this.txnTypeMenu = txnTypeMenu;
	}
	public String getTxnTypeItem() {
		return txnTypeItem;
	}
	public void setTxnTypeItem(String txnTypeItem) {
		this.txnTypeItem = txnTypeItem;
	}
	public Date getTxnDate() {
		if(this.txnDate == null) {
			this.txnDate = new Date();
		}
		return txnDate;
	}
	public void setTxnDate(Date txnDate) {
		this.txnDate = txnDate;
	}

	public void setDisableRun(boolean disableRun) {
		this.disableRun = disableRun;
	}

	public boolean isDisableRun() {
		return disableRun;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}
}
