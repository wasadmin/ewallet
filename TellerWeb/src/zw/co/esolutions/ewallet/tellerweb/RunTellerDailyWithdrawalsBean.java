/**
 * 
 */
package zw.co.esolutions.ewallet.tellerweb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.process.ProcessTransaction;
import zw.co.esolutions.ewallet.process.TransactionType;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.util.DateUtil;

/**
 * @author tauttee
 *
 */
public class RunTellerDailyWithdrawalsBean extends PageCodeBase{

	private Date txnDate;
	private boolean disableRun;
	private List<ProcessTransaction> results;
	private String bankName;
	private TransactionType transactionType = TransactionType.WITHDRAWAL;
	private TransactionType transactionType1 = TransactionType.WITHDRAWAL_NONHOLDER;
	private Profile profile;
	private BankBranch branch;
	private String userName;
	private String msg;
	private long totalAmount;
	private long totalCount;
	
	
	public RunTellerDailyWithdrawalsBean() {
		super();
		if(this.txnDate == null) {
//			this.setTxnDate((Date)super.getRequestScope().get("txnDate"));
//			this.setDisableRun(((Boolean)super.getRequestScope().get("disableRun")).booleanValue());
//			//this.setUserName("");
			try {
				this.setTxnDate((Date)super.getRequestScope().get("txnDate"));
				this.setDisableRun(((Boolean)super.getRequestScope().get("disableRun")).booleanValue());
			} catch (NullPointerException e) {
				this.setTxnDate(new Date());
				this.setDisableRun(true);
			}
			this.setUserName(super.getJaasUserName());
			
		}
	}
	
	@SuppressWarnings("unchecked")
	public String viewPostings() {
		try {
			super.getRequestScope().put("messageId", super.getRequestParam().get("messageId"));
			super.getRequestScope().put("fromPage", "viewTellerDailyWithdrawals.jspx");
		} catch (Exception e) {
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
			// TODO: handle exception
		}
		this.setResults(null);
		super.gotoPage("/teller/viewPostings.jspx");
		return "success";
	}
	
	
	public String toHome() {
		super.gotoPage("/teller/tellerHome.jspx");
		return "success";
	}

	public void setTxnDate(Date txnDate) {
		this.txnDate = txnDate;
	}

	public Date getTxnDate() {
		if(this.txnDate == null) {
			this.txnDate = new Date();
		}
		return txnDate;
	}

	public void setDisableRun(boolean disableRun) {
		this.disableRun = disableRun;
	}

	public boolean isDisableRun() {
		return disableRun;
	}

	public void setResults(List<ProcessTransaction> results) {
		this.results = results;
	}

	public List<ProcessTransaction> getResults() {
		List<ProcessTransaction> tempRes = null;
		List<ProcessTransaction> tempRes1 = null;
		if((this.results == null || this.results.isEmpty()) && this.getProfile() != null) {
			try {
				Bank bank = super.getBankService().findBankBranchById(this.getProfile().getBranchId()).getBank();
				this.bankName = bank.getName();
				tempRes = super.getProcessService().getTellerDayEnd(bankName, this.getProfile().getId(),
						DateUtil.convertToXMLGregorianCalendar(this.getTxnDate()), transactionType);
				tempRes1 = super.getProcessService().getTellerDayEnd(bankName, this.getProfile().getId(),
						DateUtil.convertToXMLGregorianCalendar(this.getTxnDate()), transactionType1);
				if(tempRes != null || tempRes1 != null) {
					if(!tempRes.isEmpty() || !tempRes1.isEmpty()) {
						this.results = new ArrayList<ProcessTransaction>();
						if(!tempRes.isEmpty()) {
							this.results.addAll(tempRes);
						} if (!tempRes1.isEmpty()) {
							this.results.addAll(tempRes1);
						}
					}
				}
				
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		if(this.results == null || this.results.isEmpty()) {
			this.setMsg("There are no withdrawalss for this teller.");
		} 
		return results;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public Profile getProfile() {
		if(this.profile == null) {
			// Find profile by user name
			try {
				this.profile = super.getProfileService().getProfileByUserName(this.getUserName());
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return profile;
	}

	public void setBranch(BankBranch branch) {
		this.branch = branch;
	}

	public BankBranch getBranch() {
		if(this.branch == null && this.getProfile() != null) {
			try {
				this.branch = super.getBankService().findBankBranchById(this.getProfile().getBranchId());
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return branch;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		this.userName = this.getJaasUserName();
		return userName;
	}

	public void setTotalAmount(long totalAmount) {
		this.totalAmount = totalAmount;
	}

	public long getTotalAmount() {
		if(this.totalAmount == 0 && this.getProfile() != null) {
			try {
				totalAmount = super.getProcessService().getTellerDayEndTotal(bankName, this.getProfile().getId()
						, DateUtil.convertToXMLGregorianCalendar(this.getTxnDate()), transactionType);
				totalAmount = totalAmount + super.getProcessService().getTellerDayEndTotal(bankName, this.getProfile().getId()
						, DateUtil.convertToXMLGregorianCalendar(this.getTxnDate()), transactionType1);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return totalAmount;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public long getTotalCount() {
		if(results == null || results.isEmpty()) {
			totalCount = 0l;
		} else {
			totalCount = results.size();
		}
		return totalCount;
	}

}
