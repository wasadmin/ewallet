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
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.util.DateUtil;

/**
 * @author tauttee
 *
 */
public class SearchBranchTransactionsBean extends PageCodeBase{

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
	private BankBranch branch;
	private TransactionType deposit = TransactionType.DEPOSIT;
	private TransactionType withdrawal = TransactionType.WITHDRAWAL;
	private TransactionType nonholder_withdrawal = TransactionType.WITHDRAWAL_NONHOLDER;
	/**
	 * 
	 */
	public SearchBranchTransactionsBean() {
		
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
						
						if(this.isTeller()) {
							this.retrieveTellerBranchResults();
						} else {
							//Pass fromDate, toDate, status, bankId, branchId
							this.results = super.getProcessService().getProcessTransactionsWithinDateRangeByBankIdAndBranchId(DateUtil.convertToXMLGregorianCalendar(this.getFromDate()), 
									DateUtil.convertToXMLGregorianCalendar(this.getToDate()), this.getBankItem(), this.getBranchItem(), status);
						}
						
						
						
					}
					//2. check with msgITypes
					else {
						
						if(this.isTeller()) {
							String profileId = new ProfileServiceSOAPProxy().getProfileByUserName(super.getJaasUserName()).getId();
							List<ProcessTransaction> tempRes = super.getProcessService().getProcessTransactionsWithinDateRangeByMsgTypeByBankIdAndBranchId(DateUtil.convertToXMLGregorianCalendar(this.getFromDate()), 
									DateUtil.convertToXMLGregorianCalendar(this.getToDate()), transactionType, this.getBankItem(), this.getBranchItem(), status);
							this.setResults(new ArrayList<ProcessTransaction>());
							for(ProcessTransaction txn : tempRes) {
								if(profileId.equalsIgnoreCase(txn.getProfileId())) {
									results.add(txn);
								}
							}
							if(results.isEmpty()) {
								this.setResults(null);
							}
						} else {
						//Pass fromDate, toDate, transactionType, status, bankId, branchId
						this.results = super.getProcessService().getProcessTransactionsWithinDateRangeByMsgTypeByBankIdAndBranchId(DateUtil.convertToXMLGregorianCalendar(this.getFromDate()), 
								DateUtil.convertToXMLGregorianCalendar(this.getToDate()), transactionType, this.getBankItem(), this.getBranchItem(), status);
						}
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
						
						if(isTeller()) {
							this.retrieveTellerBranchResults();
						} else {
							//Pass fromDate, toDate, status, bankId, branchId
							this.results = super.getProcessService().getProcessTransactionsWithinDateRangeByBankIdAndBranchId(DateUtil.convertToXMLGregorianCalendar(this.getFromDate()), 
									DateUtil.convertToXMLGregorianCalendar(this.getToDate()), this.getBankItem(), this.getBranchItem(), status);
							
						}
						
						
					}
					//2. check with msgITypes
					else {
						if(this.isTeller()) {
							String profileId = new ProfileServiceSOAPProxy().getProfileByUserName(super.getJaasUserName()).getId();
							List<ProcessTransaction> tempRes = super.getProcessService().getProcessTransactionsWithinDateRangeByMsgTypeByBankIdAndBranchId(DateUtil.convertToXMLGregorianCalendar(this.getFromDate()), 
									DateUtil.convertToXMLGregorianCalendar(this.getToDate()), transactionType, this.getBankItem(), this.getBranchItem(), status);
							this.setResults(new ArrayList<ProcessTransaction>());
							for(ProcessTransaction txn : tempRes) {
								if(profileId.equalsIgnoreCase(txn.getProfileId())) {
									results.add(txn);
								}
							}
							if(results.isEmpty()) {
								this.setResults(null);
							}
						} else {
						//Pass fromDate, toDate, transactionType, status, bankId, branchId
						this.results = super.getProcessService().getProcessTransactionsWithinDateRangeByMsgTypeByBankIdAndBranchId(DateUtil.convertToXMLGregorianCalendar(this.getFromDate()), 
								DateUtil.convertToXMLGregorianCalendar(this.getToDate()), transactionType, this.getBankItem(), this.getBranchItem(), status);
						}
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
	
	@SuppressWarnings("unchecked")
	public String viewPostings() {
		try {
			super.getRequestScope().put("messageId", super.getRequestParam().get("messageId"));
			super.getRequestScope().put("fromPage", "searchBranchTransactions.jspx");
		} catch (Exception e) {
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
			// TODO: handle exception
		}
		super.gotoPage("/teller/viewPostings.jspx");
		return "success";
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
//				bankMenu.add(new SelectItem("none", "<--select-->"));
//				bankMenu.add(new SelectItem("all", "All"));
//				for(Bank bk : this.getBankList()) {
//					bankMenu.add(new SelectItem(bk.getId(),bk.getName()));
//				}
				if(this.getBranch() != null) {
					this.bankMenu.add(new SelectItem(this.getBranch().getBank().getId(), 
							this.getBranch().getBank().getName()));
				}
			}
		}
		return bankMenu;
	}
	public void setBankMenu(List<SelectItem> bankMenu) {
		this.bankMenu = bankMenu;
	}
	public String getBankItem() {
		if(this.bankItem == null && this.getBranch() != null) {
			this.bankItem = this.getBranch().getBank().getId();
		}
		return bankItem;
	}
	public void setBankItem(String bankItem) {
		this.bankItem = bankItem;
	}
	public List<SelectItem> getBranchMenu() {
		if(this.branchMenu == null) {
			this.branchMenu = new ArrayList<SelectItem>();
			//this.branchMenu.add(new SelectItem("none", "<--select-->"));
			if(this.getBranch() != null) {
				this.branchMenu.add(new SelectItem(this.getBranchItem(), this.getBranch().getName()));
			}
		}
		return branchMenu;
	}
	public void setBranchMenu(List<SelectItem> branchMenu) {
		this.branchMenu = branchMenu;
	}
	public String getBranchItem() {
		if(this.branchItem == null) {
		this.branchItem = super.getProfileService().getProfileByUserName(super.getJaasUserName()).getBranchId();
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
			this.txnTypeMenu.add(new SelectItem("all", "ALL"));
			for(TransactionType type : TransactionType.values()) {
				if(this.isTeller()) {
					if(TransactionType.WITHDRAWAL.equals(type) || 
							TransactionType.WITHDRAWAL_NONHOLDER.equals(type) || 
							TransactionType.DEPOSIT.equals(type)) {
						this.txnTypeMenu.add(new SelectItem(type.toString(), type.toString().replace("_", " ")));
					}
				} else {
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

	public void setBranch(BankBranch branch) {
		this.branch = branch;
	}

	public BankBranch getBranch() {
		if(this.branch == null && this.getBranchItem() != null) {
			try {
				this.branch = super.getBankService().findBankBranchById(this.getBranchItem());
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return branch;
	}
	
	private boolean isTeller() {
		boolean teller = false;
		try {
			Profile p = new ProfileServiceSOAPProxy().getProfileByUserName(super.getJaasUserName());
			if(p != null) {
				if(p.getRole().getRoleName().equalsIgnoreCase("teller")) {
					teller = true;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return teller;
	}
	
	private void retrieveTellerBranchResults() {
		System.out.println(">>>>>>>>>>>>>>>>>>>>> All Transactions ");
		List<ProcessTransaction> results1 = null;
		List<ProcessTransaction> results2 = null;
		List<ProcessTransaction> results3 = null;
		TransactionStatus status = TransactionStatus.COMPLETED;
		this.setResults(new ArrayList<ProcessTransaction>());
		try {
			String profileId = new ProfileServiceSOAPProxy().getProfileByUserName(this.getJaasUserName()).getId();
			results1 = super.getProcessService().getProcessTransactionsWithinDateRangeByMsgTypeByBankIdAndBranchId(DateUtil.convertToXMLGregorianCalendar(this.getFromDate()), 
					DateUtil.convertToXMLGregorianCalendar(this.getToDate()), deposit, this.getBankItem(), this.getBranchItem(), status);
			results2 = super.getProcessService().getProcessTransactionsWithinDateRangeByMsgTypeByBankIdAndBranchId(DateUtil.convertToXMLGregorianCalendar(this.getFromDate()), 
					DateUtil.convertToXMLGregorianCalendar(this.getToDate()), withdrawal, this.getBankItem(), this.getBranchItem(), status);
			results3 = super.getProcessService().getProcessTransactionsWithinDateRangeByMsgTypeByBankIdAndBranchId(DateUtil.convertToXMLGregorianCalendar(this.getFromDate()), 
					DateUtil.convertToXMLGregorianCalendar(this.getToDate()), nonholder_withdrawal, this.getBankItem(), this.getBranchItem(), status);
			for(ProcessTransaction txn : results1) {
				if(profileId.equalsIgnoreCase(txn.getProfileId())) {
					results.add(txn);
				}
			}
            for(ProcessTransaction txn : results2) {
            	if(profileId.equalsIgnoreCase(txn.getProfileId())) {
					results.add(txn);
				}
			}
            for(ProcessTransaction txn : results3) {
            	if(profileId.equalsIgnoreCase(txn.getProfileId())) {
					results.add(txn);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(results.isEmpty()){
			this.setResults(null);
		}
//		this.results.addAll(results1);
//		this.results.addAll(results2);
//		this.results.addAll(results3);
		
	}

}
