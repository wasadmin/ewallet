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
import zw.co.esolutions.ewallet.process.ProcessTransaction;
import zw.co.esolutions.ewallet.process.TransactionStatus;
import zw.co.esolutions.ewallet.process.TransactionType;
import zw.co.esolutions.ewallet.util.DateUtil;

/**
 * @author tauttee
 *
 */
public class TransactionBean extends PageCodeBase{

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
	
	/**
	 * 
	 */
	public TransactionBean() {
		
	}
	
	public String search() {
		System.out.println(">>>>>>>>>>>>>>> Tapinda");
		String msg = this.checkAttributes();
		TransactionType transactionType = null;
		TransactionStatus status = TransactionStatus.COMPLETED;
		
		if(msg != null) {
			//report error
			super.setInformationMessage(msg);
			return "failure";
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
		
		if(!this.getTxnTypeItem().equalsIgnoreCase("all")) {
			transactionType = TransactionType.valueOf(this.getTxnTypeItem());
			this.setTableHeader(this.getTxnTypeItem()+": Search Results ");
		} else {
			this.setTableHeader(" Search Results ");
		}
		
        try {
			
			if(this.getBankItem().equalsIgnoreCase("all")) {
				
				//1. check for all transactionTypess
				if(this.getTxnTypeItem().equalsIgnoreCase("all")) {
					//Pass fromDate, toDate, status
					this.results = super.getProcessService().getProcessTransactionsWithinDateRange(DateUtil.convertToXMLGregorianCalendar(this.getFromDate()), DateUtil.convertToXMLGregorianCalendar(this.getToDate()), status);
;					 
					
				}
				//2. check with msgITypes
				else {
					//Pass fromDate, toDate, transactionType, status
					transactionType = TransactionType.valueOf(this.getTxnTypeItem());
					this.results = super.getProcessService().getProcessTransactionsWithinDateRangeByMsgType(DateUtil.convertToXMLGregorianCalendar(this.getFromDate()), 
							DateUtil.convertToXMLGregorianCalendar(this.getToDate()), transactionType, status);
				}
				
			} else {
				
				if(this.getBranchItem().equalsIgnoreCase("all")) {
					//1. check for all transactionTypess
					if(this.getTxnTypeItem().equalsIgnoreCase("all")) {
						//Pass fromDate, toDate, status, bankId
						this.results = super.getProcessService().getProcessTransactionsWithinDateRangeByBankId(DateUtil.convertToXMLGregorianCalendar(this.getFromDate()),
								DateUtil.convertToXMLGregorianCalendar(this.getToDate()), this.getBankItem(), status);
						
						
					}
					//2. check with msgITypes
					else {
						//Pass fromDate, toDate, transactionType, status, bankId
						this.results = super.getProcessService().getProcessTransactionsWithinDateRangeByMsgTypeByBankId(DateUtil.convertToXMLGregorianCalendar(this.getFromDate()), 
								DateUtil.convertToXMLGregorianCalendar(this.getToDate()), transactionType, this.getBankItem(), status);
					}
				} else {
					//1. check for all transactionTypess
					if(this.getTxnTypeItem().equalsIgnoreCase("all")) {
						//Pass fromDate, toDate, status, bankId, branchId
						this.results = super.getProcessService().getProcessTransactionsWithinDateRangeByBankIdAndBranchId(DateUtil.convertToXMLGregorianCalendar(this.getFromDate()), 
								DateUtil.convertToXMLGregorianCalendar(this.getToDate()), this.getBankItem(), this.getBranchItem(), status);
						
						
					}
					//2. check with msgITypes
					else {
						//Pass fromDate, toDate, transactionType, status, bankId, branchId
						this.results = super.getProcessService().getProcessTransactionsWithinDateRangeByMsgTypeByBankIdAndBranchId(DateUtil.convertToXMLGregorianCalendar(this.getFromDate()), 
								DateUtil.convertToXMLGregorianCalendar(this.getToDate()), transactionType, this.getBankItem(), this.getBranchItem(), status);
					}
				}
			}
			super.setInformationMessage("Results found.");
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage("An error has occured. Operation arboted.");
			return "failure";
		} 
		if(this.getResults() == null || this.getResults().isEmpty()) {
			super.setInformationMessage("No Results found.");
		} else { 
			super.setInformationMessage("Results found.");
		}
		
		return "success";
	}
	
	@SuppressWarnings("unchecked")
	public String viewPostings() {
		try {
			super.getRequestScope().put("messageId", super.getRequestParam().get("messageId"));
			super.getRequestScope().put("fromPage", "searchTransactions.jspx");
		} catch (Exception e) {
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
			// TODO: handle exception
		}
		super.gotoPage("/teller/viewPostings.jspx");
		return "success";
	}
	
	public String searchLatest() {
		this.setLatestReport(true);
		TransactionType transactionType = null;
		String msg = this.checkAttributes();
		if(msg != null) {
			//report error
			super.setErrorMessage(msg);
			return "failure";
		} 
		
		if (this.getBranchItem() != null) {
			if (this.getBankItem().equalsIgnoreCase("nothing")
					|| this.getBranchItem().equalsIgnoreCase("nothing")) {
				super
						.setErrorMessage("You cannot continue without Bank and/or Branch selected. Consult your adminstrator.");
				return "failure";
			}
		}
		
		if(!this.getTxnTypeItem().equalsIgnoreCase("all")) {
			transactionType = TransactionType.valueOf(this.getTxnTypeItem());
			this.setTableHeader(this.getTxnTypeItem()+": Search Results ");
		} else {
			this.setTableHeader(" Search Results ");
		}
		TransactionStatus status = TransactionStatus.COMPLETED;
		this.setFromDate(new Date());
		this.setToDate(new Date());
		
        try {
			
			if(this.getBankItem().equalsIgnoreCase("all")) {
				
				//1. check for all transactionTypess
				if(this.getTxnTypeItem().equalsIgnoreCase("all")) {
					//Pass fromDate, toDate, status
					this.results = super.getProcessService().getProcessTransactionsWithinDateRange(DateUtil.convertToXMLGregorianCalendar(this.getFromDate()), DateUtil.convertToXMLGregorianCalendar(this.getToDate()), status);
;					 
					
				}
				//2. check with msgITypes
				else {
					//Pass fromDate, toDate, transactionType, status
					transactionType = TransactionType.valueOf(this.getTxnTypeItem());
					this.results = super.getProcessService().getProcessTransactionsWithinDateRangeByMsgType(DateUtil.convertToXMLGregorianCalendar(this.getFromDate()), 
							DateUtil.convertToXMLGregorianCalendar(this.getToDate()), transactionType, status);
				}
				
			} else {
				
				if(this.getBranchItem().equalsIgnoreCase("all")) {
					//1. check for all transactionTypess
					if(this.getTxnTypeItem().equalsIgnoreCase("all")) {
						//Pass fromDate, toDate, status, bankId
						this.results = super.getProcessService().getProcessTransactionsWithinDateRangeByBankId(DateUtil.convertToXMLGregorianCalendar(this.getFromDate()),
								DateUtil.convertToXMLGregorianCalendar(this.getToDate()), this.getBankItem(), status);
						
						
					}
					//2. check with msgITypes
					else {
						//Pass fromDate, toDate, transactionType, status, bankId
						this.results = super.getProcessService().getProcessTransactionsWithinDateRangeByMsgTypeByBankId(DateUtil.convertToXMLGregorianCalendar(this.getFromDate()), 
								DateUtil.convertToXMLGregorianCalendar(this.getToDate()), transactionType, this.getBankItem(), status);
					}
				} else {
					//1. check for all transactionTypess
					if(this.getTxnTypeItem().equalsIgnoreCase("all")) {
						//Pass fromDate, toDate, status, bankId, branchId
						this.results = super.getProcessService().getProcessTransactionsWithinDateRangeByBankIdAndBranchId(DateUtil.convertToXMLGregorianCalendar(this.getFromDate()), 
								DateUtil.convertToXMLGregorianCalendar(this.getToDate()), this.getBankItem(), this.getBranchItem(), status);
						
						
					}
					//2. check with msgITypes
					else {
						//Pass fromDate, toDate, transactionType, status, bankId, branchId
						this.results = super.getProcessService().getProcessTransactionsWithinDateRangeByMsgTypeByBankIdAndBranchId(DateUtil.convertToXMLGregorianCalendar(this.getFromDate()), 
								DateUtil.convertToXMLGregorianCalendar(this.getToDate()), transactionType, this.getBankItem(), this.getBranchItem(), status);
					}
				}
			}
			//super.setInformationMessage("Results found.");
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage("An error has occured. Operation arboted.");
			return "failure";
		}
		if(this.getResults() == null || this.getResults().isEmpty()) {
			super.setInformationMessage("No Results found.");
		} else { 
			super.setInformationMessage("Results found.");
		}
		return "success";
	}
	
	public void handleBankValueChange(ValueChangeEvent event) {
		String item = (String)event.getNewValue();
		System.out.println(" item >>>>>>>>>"+item );
	
		if(item != null) {
			try {
				if(!item.equalsIgnoreCase("all")) {
					System.out.println("----------------------------------------");
					this.setBankBranchList(super.getBankService().getBankBranchByBank(item));
					if(this.getBankBranchList() == null || this.getBankBranchList().isEmpty()) {
						System.out.println("empty list");
						this.setBranchMenu(new ArrayList<SelectItem>());
						this.getBranchMenu().add(new SelectItem("nothing", "No Branches"));
					} else {
						System.out.println("list as some thing");
						this.getBranchMenu().add(new SelectItem("all", "ALL"));
						for(BankBranch bb : this.getBankBranchList()) {
							this.getBranchMenu().add(new SelectItem(bb.getId(), bb.getName()));
						}
					}
				} else {
					this.setDisableBranchMenu(true);
				}
				
			} catch (Exception e) {
				
			}
		}
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
				bankMenu.add(new SelectItem("none", "<--select-->"));
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
	public List<SelectItem> getBranchMenu() {
		if(this.branchMenu == null) {
			this.branchMenu = new ArrayList<SelectItem>();
			this.branchMenu.add(new SelectItem("none", "<--select-->"));
		}
		return branchMenu;
	}
	public void setBranchMenu(List<SelectItem> branchMenu) {
		this.branchMenu = branchMenu;
	}
	public String getBranchItem() {
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
				this.bankList = super.getBankService().getBank();
			} catch (Exception e) {
				
			}
		}
		return bankList;
	}

	public void setBankList(List<Bank> bankList) {
		this.bankList = bankList;
	}

	public List<BankBranch> getBankBranchList() {
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
			this.txnTypeMenu.add(new SelectItem("all", "All Transactions"));
			for(TransactionType type : TransactionType.values()) {
				if(!(TransactionType.PIN.equals(type) || 
						TransactionType.CHANGE_PASSCODE.equals(type) ||
						TransactionType.CUSTOMER_ACTIVATION.equals(type) ||
						TransactionType.PASSCODE.equals(type) ||
						TransactionType.REFERRAL.equals(type) ||
						TransactionType.TARIFF.equals(type) ||
						TransactionType.BALANCE.equals(type))) { 
					this.txnTypeMenu.add(new SelectItem(type.toString(), type.toString().replace("_", " ")));
				}
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

}
