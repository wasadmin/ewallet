/**
 * 
 */
package zw.co.esolutions.ewallet.tellerweb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.process.ProcessServiceSOAPProxy;
import zw.co.esolutions.ewallet.process.ProcessTransaction;
import zw.co.esolutions.ewallet.process.TransactionStatus;
import zw.co.esolutions.ewallet.process.TransactionType;
import zw.co.esolutions.ewallet.process.TxnFamily;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.util.DateUtil;

/**
 * @author tauttee
 *
 */
public class ViewAllTransfersBean extends PageCodeBase{

	private Date fromDate;
	private Date toDate;
	private List<SelectItem> bankMenu;
	private String bankItem;
	private List<SelectItem> branchMenu;
	private String branchItem;
	private List<Bank> bankList;
	private List<BankBranch> bankBranchList;
	private boolean latestReport;
	private boolean disableBranchMenu;
	private List<SelectItem> txnTypeMenu;
	private String txnTypeItem;
	private List<ProcessTransaction> results;
	private String tableHeader;
	private String bankName;
	private BankBranch bankBranch;
	private String tellerId;
	private List<SelectItem> txnStatusMenu;
	private String txnStatusItem;
	private long totalAmount;
	private long totalCount;
	
	/**
	 * 
	 */
	public ViewAllTransfersBean() {
		this.initializeDates();
		if(this.bankName == null) {
			try {
				this.bankName = super.getBankService().findBankBranchById(
						super.getProfileService().getProfileByUserName(super.getJaasUserName()).getBranchId()).getBank().getName();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
	
	public String search() {
		String msg = this.checkAttributes();
		TransactionStatus status = null;
		TransactionType transactionType = null;
		TxnFamily family = null;
		ProcessServiceSOAPProxy ps = new ProcessServiceSOAPProxy();
		
		if(msg != null) {
			//report error
			super.setInformationMessage(msg);
			return "failure";
		}
		//1. check for all transactionTypess
		if(this.getTxnTypeItem().equalsIgnoreCase("all")) {
			family  = TxnFamily.TRANSFERS;
		}
		
		if (this.getBranchItem() != null) {
			if (this.getBankItem().equalsIgnoreCase("nothing")
					|| this.getBranchItem().equalsIgnoreCase("nothing")) {
				super
						.setErrorMessage("You cannot continue without Bank and/or Branch selected. Consult your adminstrator.");
				return "failure";
			}
		}
		// toDate must be greater than fromDate
		if(!DateUtil.isDayBeforeOrEqual(fromDate, toDate)) {
			super.setErrorMessage("From Date must be a day before(or the same with) To Date.");
			return "failure";
		}
		// Check for ToDate
		if(this.getToDate() != null) {
			if(!DateUtil.isDayBefore(DateUtil.getBeginningOfDay(getToDate()), DateUtil.getEndOfDay(new Date()))) {
				super.setErrorMessage("To Date cannot come after this Today's Date.");
				return "failure";
			}
		}
		
		if(!this.getTxnTypeItem().equalsIgnoreCase("all")) {
			transactionType = TransactionType.valueOf(this.getTxnTypeItem());
			this.setTableHeader(this.getTxnTypeItem()+": Search Results ");
		} else {
			this.setTableHeader(" Search Results ");
		}
		
		if(!"all".equalsIgnoreCase(this.getTxnStatusItem())) {
			status = TransactionStatus.valueOf(this.getTxnStatusItem());
		}
        try {
			
        	if(this.getBankItem().equalsIgnoreCase("all")) {
				
				this.bankItem = null;
				this.branchItem = null;
				
			} else {
				
				if(this.getBranchItem().equalsIgnoreCase("all")) {
					this.branchItem = null;
				} 
			}
			this.results = ps.getProcessTransactionsByApplicableParameters(DateUtil.convertToXMLGregorianCalendar(this.getFromDate()), 
					DateUtil.convertToXMLGregorianCalendar(this.getToDate()), status, transactionType, family, getTellerId(), bankItem, branchItem);
			this.totalAmount = ps.getTotalAmountByApplicableParameters(DateUtil.convertToXMLGregorianCalendar(this.getFromDate()), 
					DateUtil.convertToXMLGregorianCalendar(this.getToDate()), status, transactionType, family, getTellerId(), bankItem, branchItem);
			if(this.results != null) {
				this.totalCount = this.results.size();
			}
			
		} catch (Exception e) {
			super.setErrorMessage(ERROR_MESSAGE);
			return "failure";
		} 
		if(this.getResults() == null || this.getResults().isEmpty()) {
			super.setInformationMessage("No Results found.");
		} else { 
			super.setInformationMessage("Results found.");
		}
		
		return "success";
	}
	
	public String searchLatest() {
		this.setLatestReport(true);
		this.setFromDate(new Date());
		this.setToDate(new Date());
		this.search();
		return "success";
	}
	
	public void handleBankValueChange(ValueChangeEvent event) {
//		String item = (String)event.getNewValue();
//		if(item != null) {
//			try {
//				if(!item.equalsIgnoreCase("all")) {
//					this.setBankBranchList(super.getBankService().getBankBranchByBank(item));
//					if(this.getBankBranchList() == null || this.getBankBranchList().isEmpty()) {
//						this.setBranchMenu(new ArrayList<SelectItem>());
//						this.getBranchMenu().add(new SelectItem("nothing", "No Branches"));
//					} else {
//						this.getBranchMenu().add(new SelectItem("all", "ALL"));
//						for(BankBranch bb : this.getBankBranchList()) {
//							this.getBranchMenu().add(new SelectItem(bb.getId(), bb.getName()));
//						}
//					}
//				} else {
//					this.setDisableBranchMenu(true);
//				}
//				
//			} catch (Exception e) {
//				
//			}
//		}
	}
	
	/*
	 * Method for missing fields
	 */
	private String checkAttributes(){
		StringBuffer buffer = new StringBuffer("The following values are required: ");
		int length = 0;
		if(this.getBankItem().equalsIgnoreCase("none")) {
			buffer.append("Bank, ");
		}if(this.getTxnTypeItem().equalsIgnoreCase("none")) {
			buffer.append("Transaction Type, ");
		} if( !this.getBankItem().equalsIgnoreCase("all")) {
			if(this.getBranchItem().equalsIgnoreCase("none")) {
				buffer.append("Branch, ");
			} 
		} if(!this.isLatestReport()) {
			if(this.getFromDate() == null) {
				buffer.append("From Date, ");
			} if(this.getToDate() == null) {
				buffer.append("To Date, ");
			}
		}
		length = buffer.toString().length();
				
		if(length > 40) {
			buffer.replace(length-2, length, " ");
			return buffer.toString();
		}
		return null;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	
	public List<SelectItem> getBankMenu() {
		if(this.bankMenu == null) {
			bankMenu = new ArrayList<SelectItem>();
			if(this.getBankList() == null || this.getBankList().isEmpty()) {
				bankMenu.add(new SelectItem("nothing", "No Banks"));
			} else {
				//bankMenu.add(new SelectItem("none", "<--select-->"));
				//bankMenu.add(new SelectItem("all", "All"));
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
	public List<SelectItem> getBranchMenu() {
		if(this.branchMenu == null) {
			this.branchMenu = new ArrayList<SelectItem>();
			if(this.getBankList() == null || this.getBankList().isEmpty()) {
				branchMenu.add(new SelectItem("nothing", "No Branches"));
			} else {
//				branchMenu.add(new SelectItem("none", "<--select-->"));
//				branchMenu.add(new SelectItem("all", "All"));
//				for(BankBranch bk : this.getBankBranchList()) {
//					branchMenu.add(new SelectItem(bk.getId(),bk.getName()));
//				}
				if(this.getBankBranch() != null) {
					branchMenu.add(new SelectItem(this.getBankBranch().getId(),
							this.getBankBranch().getName()));
				}
			}
			
		}
		return branchMenu;
	}
	public void setBranchMenu(List<SelectItem> branchMenu) {
		this.branchMenu = branchMenu;
	}
	public String getBranchItem() {
		if(this.branchItem == null && this.getBankBranch() != null) {
			this.branchItem = this.getBankBranch().getId();
		}
		return branchItem;
	}
	public void setBranchItem(String branchItem) {
		this.branchItem = branchItem;
	}

	public void setLatestReport(boolean latestReport) {
		this.latestReport = latestReport;
	}

	public boolean isLatestReport() {
		return latestReport;
	}

	public List<Bank> getBankList() {
		if(this.bankList == null || this.bankList.isEmpty()) {
			try {
				//this.bankList = super.getBankService().getBank();
				this.bankList = super.getBankService().getBankByName(this.bankName);
			} catch (Exception e) {
				
			}
		}
		return bankList;
	}

	public void setBankList(List<Bank> bankList) {
		this.bankList = bankList;
	}

	public List<BankBranch> getBankBranchList() {
		if(this.bankBranchList == null || this.bankBranchList.isEmpty()) {
			try {
				this.bankBranchList = super.getBankService().getBankBranchByBank(super.getBankService().getBankByName(this.bankName).get(0).getId());
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return bankBranchList;
	}

	public void setBankBranchList(List<BankBranch> bankBranchList) {
		this.bankBranchList = bankBranchList;
	}

	public void setDisableBranchMenu(boolean disableBranchMenu) {
		this.disableBranchMenu = disableBranchMenu;
	}

	public boolean isDisableBranchMenu() {
		return disableBranchMenu;
	}

	public List<SelectItem> getTxnTypeMenu() {
		if(this.txnTypeMenu == null) {
			this.txnTypeMenu = new ArrayList<SelectItem>();
			this.txnTypeMenu.add(new SelectItem("none", "<--select-->"));
			this.txnTypeMenu.add(new SelectItem("all", "ALL"));
			TransactionType[] txnArray = {TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER, TransactionType.EWALLET_TO_EWALLET_TRANSFER, 
					TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER, TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER, 
					TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER, TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER };
			//this.txnTypeMenu.add(new SelectItem("all", "All Transactions"));
			for(TransactionType type : txnArray) {
					this.txnTypeMenu.add(new SelectItem(type.toString(), type.toString().replace("_", " ")));
				
			}
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

	public void setResults(List<ProcessTransaction> results) {
		this.results = results;
	}

	public List<ProcessTransaction> getResults() {
		return results;
	}

	public void setTableHeader(String tableHeader) {
		this.tableHeader = tableHeader;
	}

	public String getTableHeader() {
		if(this.tableHeader == null) {
			tableHeader = "Search Results";
		}
		return tableHeader;
	}

	public void setBankBranch(BankBranch bankBranch) {
		this.bankBranch = bankBranch;
	}

	public BankBranch getBankBranch() {
		if(this.bankBranch == null) {
			try {
				Profile p = super.getProfileService().getProfileByUserName(super.getJaasUserName());
				bankBranch = super.getBankService().findBankBranchById(p.getBranchId());
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return bankBranch;
	}

	@SuppressWarnings("unchecked")
	public String viewPostings() {
		try {
			super.getRequestScope().put("messageId", super.getRequestParam().get("messageId"));
			super.getRequestScope().put("fromPage", "viewAllTransfers.jspx");
		} catch (Exception e) {
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
			// TODO: handle exception
		}
		/*this.setBankBranch(null);
		this.setResults(null);
		this.setBankBranchList(null);
		this.setBankItem(null);
		this.setBranchMenu(null);
		this.setDisableBranchMenu(false);
		this.setFromDate(null);
		this.setTableHeader(null);
		this.setToDate(null);
		this.setLatestReport(false);
		this.setTxnTypeItem(null);*/
		super.gotoPage("/teller/viewPostings.jspx");
		return "success";
	}

	public void setTellerId(String tellerId) {
		this.tellerId = tellerId;
	}

	public String getTellerId() {
		if(tellerId == null) {
			try {
				Profile p = new ProfileServiceSOAPProxy().getProfileByUserName(super.getJaasUserName());
				if(p.getRole().getRoleName().equalsIgnoreCase("teller")) {
					tellerId = p.getId();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return tellerId;
	}
	
	/**
	 * @param txnStatusMenu the txnStatusMenu to set
	 */
	public void setTxnStatusMenu(List<SelectItem> txnStatusMenu) {
		this.txnStatusMenu = txnStatusMenu;
	}

	/**
	 * @return the txnStatusMenu
	 */
	public List<SelectItem> getTxnStatusMenu() {
		if(this.txnStatusMenu == null || this.txnStatusMenu.isEmpty()) {
			this.txnStatusMenu = new ArrayList<SelectItem>();
			this.txnStatusMenu.add(new SelectItem("all", "ALL"));
			this.txnStatusMenu.add(new SelectItem(TransactionStatus.COMPLETED.toString(), "SUCCESSFUL"));
			this.txnStatusMenu.add(new SelectItem(TransactionStatus.FAILED.toString(), "FAILED"));
			this.txnStatusMenu.add(new SelectItem(TransactionStatus.MANUAL_RESOLVE.toString(), "EXCEPTIONS"));
			//this.txnStatusMenu.add(new SelectItem(TransactionStatus.AWAITING_COLLECTION.toString(), "FUNDS AWAITING COLLECTION"));
			//this.txnStatusMenu.add(new SelectItem("", "SENT TO HOST"));
			//this.txnStatusMenu.add(new SelectItem(TransactionStatus.BANK_REQUEST.toString(), "AWAITING HOST RESPONSE"));
		}
		return txnStatusMenu;
	}

	public void setTxnStatusItem(String txnStatusItem) {
		this.txnStatusItem = txnStatusItem;
	}

	public String getTxnStatusItem() {
		return txnStatusItem;
	}

	public void setTotalAmount(long totalAmount) {
		this.totalAmount = totalAmount;
	}

	public long getTotalAmount() {
		return totalAmount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public long getTotalCount() {
		return totalCount;
	}
	
	private void initializeDates() {
		this.setFromDate(new Date());
		this.setToDate(new Date());
	}
}
