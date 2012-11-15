/**
 * 
 */
package zw.co.esolutions.ewallet.reports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.enums.FailureReasons;
import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.reports.util.GenerateReportUtil;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.MapUtil;
import zw.co.esolutions.ewallet.util.URLEncryptor;

/**
 * @author tauttee
 *
 */
public class FailureByReasonReportBean extends PageCodeBase{

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
	private List<SelectItem> txnMenu;
	private String txnItem;
	private String reasonItem;
	private List<SelectItem> reasonMenu;
	private boolean flatReport;
	
	/**
	 * 
	 */
	public FailureByReasonReportBean() {
		this.initializeDates();
	}
	
	public String generateReport() {
		this.setFromDate(DateUtil.getBeginningOfDay(this.getFromDate()));
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		
		String fileId = null;
		String orderBy = null;
		String fileName = "failure_by_reason_report";
		String msg = this.checkAttributes();
		String query = null;
		String reasonQuery = "";
		String queryExtension =  null;
		String branchQuery = "(SELECT bc.id FROM "+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH AS bc) ";
		TransactionStatus status = TransactionStatus.FAILED;
		Map<String, String> parameters = new HashMap<String, String>();
		if(msg != null) {
			//report error
			super.setInformationMessage(msg);
			return "failure";
		}
		
		if(this.isFlatReport()) {
			
			fileName = "failure_by_reason_flat_report";
		} else {
			fileName = "failure_by_reason_report";
		}
		parameters.put("sourceFile", fileName+".xml");
		
		if (this.getBranchItem() != null) {
			if (this.getBankItem().equalsIgnoreCase("nothing")
					|| this.getBranchItem().equalsIgnoreCase("nothing")) {
				super
						.setErrorMessage("You cannot continue without Bank and/or Branch selected. Consult your adminstrator.");
				return "failure";
			}
		}
		
		// Check for ToDate
		if(this.getToDate() != null) {
			if(!DateUtil.isDayBefore(DateUtil.getBeginningOfDay(getToDate()), DateUtil.getEndOfDay(new Date()))) {
				super.setErrorMessage("To Date cannot come after this Today's Date.");
				return "failure";
			}
		}
		
		// toDate must be greater than fromDate
		if(!DateUtil.isDayBefore(DateUtil.getBeginningOfDay(getFromDate()), DateUtil.getEndOfDay(getToDate()))) {
			super.setErrorMessage("From Date must be a day before(or the same with) To Date.");
			return "failure";
		}
		
		
		try {
			//Dates
			queryExtension = "AND p.dateCreated >= '"+DateUtil.convertDateToTimestamp(this.getFromDate())+"' " +
								"AND p.dateCreated <= '"+DateUtil.convertDateToTimestamp(this.getToDate())+"' ";
			
			
			if(!this.getTxnItem().equalsIgnoreCase("all")) {
				queryExtension = queryExtension+" AND p.transactionType = '"+this.getTxnItem()+"' ";
			} else {
				queryExtension = queryExtension + "AND "+super.getAllTxnForProcessTxnQuery();
			}
			
			
			//Checking for To Date to Display
			this.initializeDisplayableDates();
			
			if("all".equalsIgnoreCase(this.getReasonItem())) {
				reasonQuery = "AND (";
				for(FailureReasons res : FailureReasons.values()) {
					if(reasonQuery.endsWith("(")) {
						
						reasonQuery = reasonQuery +"p.narrative LIKE '"+"%"+res.getDescription()+"%"+"' ";
						
					} else {
						reasonQuery = reasonQuery + "OR p.narrative LIKE '"+"%"+res.getDescription()+"%"+"' ";
					}
				}
				reasonQuery = reasonQuery +") ";
			} else {
				reasonQuery = "AND p.narrative LIKE '"+"%"+this.getReasonItem()+"%"+"' ";
			}
			
			queryExtension = queryExtension + reasonQuery +"AND p.status = '"+status+"' ";
			
			if(this.getBankItem().equalsIgnoreCase("all")) {
								
				parameters.put("reportTitle", "Transaction Failures for all Banks");
		        parameters.put("pageHeader", "All Banks : Branch Transaction Failures from "+DateUtil.convertToDateWithTime(this.getFromDate())+
				" to "+DateUtil.convertToDateWithTime(this.getToDate()));
		        
				query = "SELECT p.UTILITYNAME as utilityName, p.DESTINATIONEQ3ACCOUNTNUMBER as eq3TargetAccount, b.name AS bName, bc.name AS branchName, p.profileId, p.dateCreated, p.valueDate, p.messageId, p.transactionType, p.sourceMobile, p.sourceAccountNumber, p.targetMobile, p.destinationAccountNumber, p.amount, p.utilityAccount, p.status, p.responseCode, p.narrative, p.branchId, p.fromBankId, p.transactionLocationType, p.transactionLocationId, p.balance, p.customerName FROM "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION AS p, " +
						""+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH bc, " +
			            ""+EWalletConstants.DATABASE_SCHEMA+".BANK b WHERE " +
			            " (b.id = p.fromBankId) " +
						"AND (bc.id = p.transactionLocationId OR bc.id = p.branchId) "; 
	
				orderBy ="ORDER BY b.name ASC, CASE  WHEN p.transactionLocationId IN "+branchQuery+" THEN bc.name ELSE p.transactionLocationId end ASC, p.transactionLocationType ASC, p.dateCreated DESC";
				
				
			} else {
				parameters.put(this.getBankItem(), bankService.findBankById(this.getBankItem()).getName());
				if(this.getBranchItem().equalsIgnoreCase("all")) {
					parameters = super.populateSMS(parameters, this.getBankItem());
					parameters.put("reportTitle", parameters.get(this.getBankItem()).toString()+" " +
					"Transaction Failures");
					parameters.put("pageHeader", "All "+parameters.get(this.getBankItem()).toString()+
					" Branches Transaction Failures from "+DateUtil.convertToDateWithTime(this.getFromDate())+
					" to "+DateUtil.convertToDateWithTime(this.getToDate()));
					
					query = "SELECT p.UTILITYNAME as utilityName, p.DESTINATIONEQ3ACCOUNTNUMBER as eq3TargetAccount, b.name AS bName, bc.name AS branchName, p.profileId, p.dateCreated, p.valueDate, p.messageId, p.transactionType, p.sourceMobile, p.sourceAccountNumber, p.targetMobile, p.destinationAccountNumber, p.amount, p.utilityAccount, p.status, p.responseCode, p.narrative, p.branchId, p.fromBankId, p.transactionLocationType, p.transactionLocationId, p.balance, p.customerName " +
					"FROM "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION AS p, " +
					""+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH AS bc, " +
		            ""+EWalletConstants.DATABASE_SCHEMA+".BANK AS b WHERE " +
		            " (b.id = p.fromBankId) " +
					"AND fromBankId = '"+this.getBankItem()+"' "+
					"AND (bc.id = p.transactionLocationId OR bc.id = p.branchId) ";
			
					orderBy ="ORDER BY CASE  WHEN p.transactionLocationId IN "+branchQuery+" THEN bc.name ELSE p.transactionLocationId end ASC, p.transactionLocationType ASC, p.dateCreated DESC";					//Clear BankId Parameter
			
					
					//Clear Bank and Branch Infor
					parameters.remove(this.getBankItem());
					
				} else {
					BankBranch bb = bankService.findBankBranchById(this.getBranchItem());
					parameters.put(bb.getId(), bb.getName());
					parameters.put("reportTitle", parameters.get(this.getBankItem()).toString()+" " +
					"Transaction Failures");
			        parameters.put("pageHeader",parameters.get(this.getBankItem()).toString()+
					" : "+bankService.findBankBranchById(this.getBranchItem()).getName()+" Branch Transaction Failures from "+DateUtil.convertToDateWithTime(this.getFromDate())+
					" to "+DateUtil.convertToDateWithTime(this.getToDate()));
			        
			        query = "SELECT p.UTILITYNAME as utilityName, p.DESTINATIONEQ3ACCOUNTNUMBER as eq3TargetAccount, b.name AS bName, bc.name AS branchName, p.profileId, p.dateCreated, p.valueDate, p.messageId, p.transactionType, p.sourceMobile, p.sourceAccountNumber, p.targetMobile, p.destinationAccountNumber, p.amount, p.utilityAccount, p.status, p.responseCode, p.narrative, p.branchId, p.fromBankId, p.transactionLocationType, p.transactionLocationId, p.balance, p.customerName FROM "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION AS p WHERE " +
					"p.transactionLocationId = '"+this.getBranchItem()+"' ";
					
					orderBy ="ORDER BY p.dateCreated DESC, p.transactionLocationType ASC, p.transactionType ASC ";
					
					
					//Clear Bank and Branch Infor
					parameters.remove(this.getBankItem());
					parameters.remove(bb.getId());
				}
			}
			
			if(this.isFlatReport()) {
				orderBy ="ORDER BY p.dateCreated DESC ";
			}
			
			if(queryExtension != null) {
				query = query + queryExtension + orderBy;
			} else {
				query = query + orderBy;
			}
			
			fileId = GenerateReportUtil.getJasperUserId(super.getJaasUserName());
			parameters.put("fileId", fileId);	
			
			if(fileId == null) {
				super.setInformationMessage("No results to display.");
				return "failure";
			}
			
			super.setInformationMessage("Report successfully generated.");
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage("An error has occured. Report not generated.");
			return "failure";
		}
		ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
		try {
			ctx.redirect("/EWalletReportsWebICE/jspviewer/viewer.jsp?pdfName="+URLEncryptor.encryptUrl(fileId+fileName+".pdf")+"&query="+URLEncryptor.encryptUrl(query)+"&"+EWalletConstants.MASTER_REPORT+"="+URLEncryptor.encryptUrl(MapUtil.convertAttributesMapToString(parameters))+"&pageName="+URLEncryptor.encryptUrl("failureByReasonReport.jspx"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "success";
	}
	
	public String generateLatest() {
		Date date = new Date();
		this.setLatestReport(true);
		this.setFromDate(date);
		this.setToDate(date);
		String msg = this.checkAttributes();
		if(msg != null) {
			//report error
			super.setErrorMessage(msg);
			return "failure";
		} 
		this.generateReport();
		
		
		return "success";
	}
	
	public void handleBankValueChange(ValueChangeEvent event) {
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		String item = (String)event.getNewValue();
		if(item != null) {
			try {
				if(!(item.equalsIgnoreCase("all") || item.equalsIgnoreCase("none"))) {
					
					this.setBankBranchList(bankService.getBankBranchByBank(item));
					if(this.getBankBranchList() == null || this.getBankBranchList().isEmpty()) {
						this.setDisableBranchMenu(true);
						this.setBranchMenu(new ArrayList<SelectItem>());
						this.getBranchMenu().add(new SelectItem("nothing", "No Branches"));
					} else {
						this.setDisableBranchMenu(false);
						this.setBranchMenu(new ArrayList<SelectItem>());
						this.getBranchMenu().add(new SelectItem("all", "ALL"));
						for(BankBranch bb : this.getBankBranchList()) {
							this.getBranchMenu().add(new SelectItem(bb.getId(), bb.getName()));
						}
					}
				} else {
					this.setDisableBranchMenu(true);
					this.setBranchMenu(null);
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
		} if( !this.getBankItem().equalsIgnoreCase("all")) {
			if(this.getBranchItem().equalsIgnoreCase("none")) {
				buffer.append("Branch, ");
			} 
		} 
		if(!this.isLatestReport()) {
			if(this.getFromDate() == null) {
				buffer.append("From Date, ");
			} if(this.getToDate() == null) {
				buffer.append("To Date, ");
			}
		}
		if(this.isLatestReport()) {
			if(this.getFromDate() == null) {
				buffer.append("From Date, ");
			}
		} 
		if(this.getReasonItem() == null ) {
			buffer.append("Reason, ");
		} 
		if(this.getReasonItem() != null && "".equalsIgnoreCase(this.getReasonItem()) ) {
			buffer.append("Reason, ");
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

	/**
	 * @param txnMenu the txnMenu to set
	 */
	public void setTxnMenu(List<SelectItem> txnMenu) {
		this.txnMenu = txnMenu;
	}

	/**
	 * @return the txnMenu
	 */
	public List<SelectItem> getTxnMenu() {
		if(this.txnMenu == null) {
			this.txnMenu = new ArrayList<SelectItem>();
			this.txnMenu.add(new SelectItem("all", "ALL"));
			for(TransactionType txnType : super.getTxnTypes()) {
				if(TransactionType.BALANCE.equals(txnType)) {
					//Do Nothing Here
				} else {
					this.txnMenu.add(new SelectItem(txnType.toString(), txnType.toString()));
				}
			}
		}
		return txnMenu;
	}

	/**
	 * @param txnItem the txnItem to set
	 */
	public void setTxnItem(String txnItem) {
		this.txnItem = txnItem;
	}

	/**
	 * @return the txnItem
	 */
	public String getTxnItem() {
		return txnItem;
	}

	/**
	 * @param reason the reason to set
	 */
	public void setReasonItem(String reasonItem) {
		this.reasonItem = reasonItem;
	}

	/**
	 * @return the reason
	 */
	public String getReasonItem() {
		return reasonItem;
	}

	/**
	 * @param reasonMenu the reasonMenu to set
	 */
	public void setReasonMenu(List<SelectItem> reasonMenu) {
		this.reasonMenu = reasonMenu;
	}

	/**
	 * @return the reasonMenu
	 */
	public List<SelectItem> getReasonMenu() {
		if(this.reasonMenu == null || this.reasonMenu.isEmpty()) {
			this.reasonMenu = new ArrayList<SelectItem>();
			this.reasonMenu.add(new SelectItem("all","ALL"));
			for(FailureReasons res : FailureReasons.values()) {
				this.reasonMenu.add(new SelectItem(res.getDescription(), res.name().toString().replace("_", " ")));
			}
		}
		return reasonMenu;
	}
	
	public void setFlatReport(boolean flatReport) {
		this.flatReport = flatReport;
	}

	public boolean isFlatReport() {
		return flatReport;
	}

	private void initializeDates() {
		this.setFromDate(DateUtil.getBusinessDayBeginningOfDay(DateUtil.add(new Date(), Calendar.DAY_OF_MONTH, -1)));
		this.setToDate(DateUtil.getBusinessDayEndOfDay(new Date()));
	}
	
	private void initializeDisplayableDates() {
		if(!this.isLatestReport()) {
			if(DateUtil.isDayBefore(DateUtil.getBeginningOfDay(this.getToDate()), DateUtil.getBeginningOfDay(new Date()))) {
				this.setToDate(DateUtil.getBusinessDayEndOfDay(this.getToDate()));
			} else if(DateUtil.isDayBeforeOrEqual(DateUtil.getBeginningOfDay(this.getToDate()), DateUtil.getBeginningOfDay(new Date()))) {
				this.setToDate(DateUtil.getBusinessDayEndOfDay(new Date()));
			} else {
				this.setToDate(DateUtil.getBusinessDayEndOfDay(this.getToDate()));
			}
		}
	}
}
