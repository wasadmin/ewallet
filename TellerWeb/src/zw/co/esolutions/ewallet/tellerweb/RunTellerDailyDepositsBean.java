/**
 * 
 */
package zw.co.esolutions.ewallet.tellerweb;

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
public class RunTellerDailyDepositsBean extends PageCodeBase{

	private Date txnDate;
	private boolean disableRun;
	private List<ProcessTransaction> results;
	private List<ProcessTransaction> agentDeposits;
	private String bankName;
	private TransactionType transactionType = TransactionType.DEPOSIT;
	private TransactionType agentDepositT =TransactionType.AGENT_CASH_DEPOSIT;
	private Profile profile;
	private BankBranch branch;
	private String userName;
	private String msg;
	private long totalAmount;
	private long totalCount;
	
	
	public RunTellerDailyDepositsBean() {
		super();
		if(this.txnDate == null) {
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
			super.getRequestScope().put("fromPage", "viewTellerDailyDeposits.jspx");
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
		if(this.getProfile() == null) {
			try {
				this.setProfile(super.getProfileService().getProfileByUserName(super.getJaasUserName()));
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		if((this.results == null || this.results.isEmpty()) && this.getProfile() != null) {
			Bank bank = super.getBankService().findBankBranchById(this.getProfile().getBranchId()).getBank();
			try {
				this.bankName = bank.getName();
				this.results = super.getProcessService().getTellerDayEnd(bankName, this.getProfile().getId(),
						DateUtil.convertToXMLGregorianCalendar(this.getTxnDate()), transactionType);
				this.agentDeposits=super.getProcessService().getTellerDayEnd(bankName, this.getProfile().getId(),
						DateUtil.convertToXMLGregorianCalendar(this.getTxnDate()), agentDepositT);
				results.addAll(agentDeposits);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		if(this.results == null || this.results.isEmpty()) {
			this.results = null;
			this.setMsg("There are no deposits for this teller.");
		} 
		System.out.println(">>>>>>>>>>>>>>>>>> Deposits List "+results);
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
		long depositTotal=0;
		long agentDepositTotal=0;
		if(this.totalAmount == 0 && this.getProfile() != null) {
			try {
				depositTotal = super.getProcessService().getTellerDayEndTotal(bankName, this.getProfile().getId()
						, DateUtil.convertToXMLGregorianCalendar(this.getTxnDate()), transactionType);
				agentDepositTotal = super.getProcessService().getTellerDayEndTotal(bankName, this.getProfile().getId()
						, DateUtil.convertToXMLGregorianCalendar(this.getTxnDate()), agentDepositT);
				totalAmount=depositTotal + agentDepositTotal;
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
			totalCount = 0;
		} else {
			totalCount = results.size();
		}
		return totalCount;
	}

}
