/**
 * 
 */
package zw.co.esolutions.ewallet.reports;

import java.io.IOException;
import java.util.ArrayList;
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
import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.reports.util.GenerateReportUtil;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.MapUtil;
import zw.co.esolutions.ewallet.util.URLEncryptor;

/**
 * @author tauttee
 *
 */
public class ReconciliationReportBean extends PageCodeBase{

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
	private Profile profile;
	private BankBranch bankBranch;
		
	/**
	 * 
	 */
	public ReconciliationReportBean() {
		this.initializeDates();
		if(this.getBankBranch() != null) {
			this.setBankItem(this.getBankBranch().getBank().getId());
			this.setBranchItem("all");
		}
		
	}
	
	public String generateReportNormal() {
		String msg = this.checkAttributes();
		if(msg != null) {
			//report error
			super.setErrorMessage(msg);
			return "failure";
		}else{
			if(generateNormalDays()){
				return generateReport();
			}else{
				return "failure";
			}
		}
		
	}
	
	public String generateReportBusiness() {
		String msg = this.checkAttributes();
		if(msg != null) {
			//report error
			super.setErrorMessage(msg);
			return "failure";
		}else{
			if(generateBusinessDays()){
				return generateReport();
			}else{
				return "failure";
			}
		}
		
	}
	
	private boolean generateBusinessDays(){
		
		this.setFromDate(DateUtil.getBusinessDayBeginningOfDay(this.getFromDate()));
		this.setToDate(DateUtil.getBusinessDayEndOfDay(getToDate()));
		
		if(!DateUtil.isDayBefore(getFromDate(), getToDate())) {
			super.setErrorMessage("From Date must be atleast a day before or the same with To Date.");
			return false;
		}
		
		return true;
	}
	
	private boolean generateNormalDays(){
		
		this.setFromDate(DateUtil.getBeginningOfDay(this.getFromDate()));
		this.setToDate(DateUtil.getEndOfDay(getToDate()));
		
		if(!DateUtil.isDayBefore(getFromDate(), getToDate())) {
			super.setErrorMessage("From Date must be atleast a day before or the same with To Date.");
			return false;
		}
		
		return true;
	}
	
	public String generateReport() {
		
		String msg = this.checkAttributes();
		if(msg != null) {
			//report error
			super.setErrorMessage(msg);
			return "failure";
		}
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		
		String fileId = null;
		String orderBy = null;
		String queryExtension = null;
		String fileName = "reconciliation_report";
		//String msg = this.checkAttributes();
		String query = null;
		String minMaxExt = "";
		String txnStatusExt = "";
		String branchQuery = "(SELECT bc.id FROM "+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH AS bc) ";
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("sourceFile", fileName+".xml");
		
		
		if (this.getBranchItem() != null) {
			if (this.getBankItem().equalsIgnoreCase("nothing")
					|| this.getBranchItem().equalsIgnoreCase("nothing")) {
				super
						.setErrorMessage("You cannot continue without Bank and/or Branch selected. Consult your adminstrator.");
				return "failure";
			}
			
		}
		
		
		//Start Appending Queries
		
		//Dates
		queryExtension = "AND p.dateCreated >= '"+DateUtil.convertDateToTimestamp(this.getFromDate())+"' " +
							"AND p.dateCreated <= '"+DateUtil.convertDateToTimestamp(this.getToDate())+"' ";
		
		queryExtension = queryExtension + "AND p.status  IN ('"+TransactionStatus.COMPLETED+"', '"+TransactionStatus.AWAITING_COLLECTION+"') AND "+super.getReconciliationTxnTypeQuery();
			
		try {
			List<Bank> banks = new BankServiceSOAPProxy().getBank();
			if(banks != null && !banks.isEmpty()) {
				for(Bank bk : banks) {
					if(bk.getName().contains(EWalletConstants.SYSTEM_BANKS_DELIMITER)) {
						parameters.put(bk.getId(), bk.getName());
					}
				}
			}
			if(super.getJaasUserName() != null) {
				BankBranch branch = new BankServiceSOAPProxy().findBankBranchById(new ProfileServiceSOAPProxy().getProfileByUserName(super.getJaasUserName()).getBranchId());
				if(branch != null) {
					parameters.put("bankName", branch.getBank().getName());
				}
			}
			//Checking for To Date to Display
			//this.initializeDisplayableDates();
			
			if(this.getBankItem().equalsIgnoreCase("all")) {
				
				parameters.put("reportTitle", "Reconciliation Report for all Banks");
		        parameters.put("pageHeader", "All Banks : Branch Reconciliation Report from "+DateUtil.convertToDateWithTime(this.getFromDate())+
				" to "+DateUtil.convertToDateWithTime(this.getToDate()));
		        
				query = "SELECT p.dateCreated, p.valueDate, p.messageId, p.transactionType, p.amount, p.fromBankId AS bankId, p.tariffAmount  FROM "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION AS p, " +
						""+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH AS bc, " +			
						""+EWalletConstants.DATABASE_SCHEMA+".BANK AS b WHERE " +
						" (b.id = p.fromBankId) " +
						"AND (bc.id = p.transactionLocationId OR bc.id = p.branchId) "; 
				
				//orderBy ="ORDER BY b.name ASC, CASE  WHEN p.transactionLocationId IN "+branchQuery+" THEN bc.name end ASC, p.transactionLocationId ASC, p.dateCreated DESC";
				
				orderBy ="ORDER BY b.name ASC, p.transactionType ASC, CASE  WHEN p.transactionLocationId IN "+branchQuery+" THEN bc.name ELSE p.transactionLocationId end ASC";
				
			} else {
				parameters.put(this.getBankItem(), bankService.findBankById(this.getBankItem()).getName());
				if(this.getBranchItem().equalsIgnoreCase("all")) {
					
					parameters = super.populateSMS(parameters, this.getBankItem());
					parameters.put("reportTitle", parameters.get(this.getBankItem()).toString()+" " +
							"Reconciliation Report");
					parameters.put("pageHeader", "All "+parameters.get(this.getBankItem()).toString()+
							" Branches Reconciliation Report from "+DateUtil.convertToDateWithTime(this.getFromDate())+
							" to "+DateUtil.convertToDateWithTime(this.getToDate()));
					
					
					query = "SELECT p.dateCreated, p.valueDate, p.messageId, p.transactionType, p.amount, p.fromBankId AS bankId, p.tariffAmount FROM " +
							""+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH AS bc, " +
							""+EWalletConstants.DATABASE_SCHEMA+".BANK AS b, " +
							EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION AS p WHERE " +
							" (b.id = p.fromBankId) " +
							"AND fromBankId = '"+this.getBankItem()+"' "+
							"AND (bc.id = p.transactionLocationId OR bc.id = p.branchId) ";
							//"AND bc.id = p.branchId ";
					
					//orderBy ="ORDER BY p.transactionLocationId ASC, bc.name ASC, p.dateCreated DESC ";
					orderBy ="ORDER BY p.transactionType ASC, CASE  WHEN p.transactionLocationId IN "+branchQuery+" THEN bc.name ELSE p.transactionLocationId end ASC";
					
				} else {
					BankBranch bb = bankService.findBankBranchById(this.getBranchItem());
					parameters.put(bb.getId(), bb.getName());
					parameters.put("reportTitle", parameters.get(this.getBankItem()).toString()+" " +
					"Reconciliation Report");
			        parameters.put("pageHeader",parameters.get(this.getBankItem()).toString()+
					" : "+bankService.findBankBranchById(this.getBranchItem()).getName()+" Branch Reconciliation Report from "+DateUtil.convertToDateWithTime(this.getFromDate())+
					" to "+DateUtil.convertToDateWithTime(this.getToDate()));
			        
			        //Clear Bank and Branch
			        parameters.remove(bb.getId());
			        
			        
//					query = "SELECT p.dateCreated, valueDate, messageId, transactionType, sourceMobile, sourceAccountNumber, targetMobile, destinationAccountNumber, amount, utilityAccount, p.status, responseCode, narrative, p.branchId, fromBankId, transactionLocationType, transactionLocationId, balance, customerName FROM p, " +
//							""+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER, "+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE WHERE " +
//					        "p.branchId = '"+this.getBranchItem()+"' ";
					
					query = "SELECT p.dateCreated, p.valueDate, p.messageId, p.transactionType, p.amount, p.fromBankId AS bankId, p.tariffAmount FROM "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION AS p WHERE " +
					"p.transactionLocationId = '"+this.getBranchItem()+"' ";
					//orderBy ="ORDER BY customerName ASC";
					orderBy ="ORDER BY p.transactionType ASC, ";
					
				}
			}
			
			if(queryExtension != null) {
				query = query + minMaxExt + txnStatusExt + queryExtension+orderBy;
			} else {
				query = query + minMaxExt + txnStatusExt + orderBy;
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
			ctx.redirect("/EWalletReportsWebICE/jspviewer/viewer.jsp?pdfName="+URLEncryptor.encryptUrl(fileId+fileName+".pdf")+"&query="+URLEncryptor.encryptUrl(query)+"&"+EWalletConstants.MASTER_REPORT+"="+URLEncryptor.encryptUrl(MapUtil.convertAttributesMapToString(parameters))+"&pageName="+URLEncryptor.encryptUrl("reconciliationReport.jspx"));
		} catch (IOException e) {
			e.printStackTrace();
		}
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
//						for(BankBranch bb : this.getBankBranchList()) {
//							this.getBranchMenu().add(new SelectItem(bb.getId(), bb.getName()));
//						}
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
		int initialLength = buffer.length();
		if(this.getBankItem().equalsIgnoreCase("none")) {
			buffer.append("Bank, ");
		} if( !this.getBankItem().equalsIgnoreCase("all")) {
			if(this.getBranchItem().equalsIgnoreCase("none")) {
				buffer.append("Branch, ");
			} 
		} 
		if(this.getFromDate() == null) {
			buffer.append("From Date, ");
			
		}
		if(this.getToDate() == null) {
			buffer.append("To Date, ");
		}
		int length = buffer.toString().length();
				
		if(length > initialLength) {
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
			if(this.getBankBranch() == null) {
				this.branchMenu.add(new SelectItem("none", "<--select-->"));
			} else {
				this.setDisableBranchMenu(false);
				this.setBranchMenu(new ArrayList<SelectItem>());
				this.getBranchMenu().add(new SelectItem("all", "ALL"));
				/*for(BankBranch bb : this.getBankBranchList()) {
					this.getBranchMenu().add(new SelectItem(bb.getId(), bb.getName()));
				}*/
			}
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
		if((this.bankBranchList == null || this.bankBranchList.isEmpty()) && this.getBankBranch() != null) {
			try {
				this.bankBranchList = new BankServiceSOAPProxy().getBankBranchByBank(this.getBankBranch().getBank().getId());
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

	private void initializeDates() {
		try {
			this.setFromDate(DateUtil.getBeginningOfDay(new Date()));
			this.setToDate(DateUtil.getEndOfDay(new Date()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param profile the profile to set
	 */
	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	/**
	 * @return the profile
	 */
	public Profile getProfile() {
		if(this.profile == null) {
			try {
				ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
				this.profile = ps.getProfileByUserName(super.getJaasUserName());
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return profile;
	}

	/**
	 * @param bankBranch the bankBranch to set
	 */
	public void setBankBranch(BankBranch bankBranch) {
		this.bankBranch = bankBranch;
	}

	/**
	 * @return the bankBranch
	 */
	public BankBranch getBankBranch() {
		if(this.bankBranch == null && this.getProfile() != null) {
			try {
				BankServiceSOAPProxy bs = new BankServiceSOAPProxy();
				this.bankBranch = bs.findBankBranchById(this.getProfile().getBranchId());
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return bankBranch;
	}
	
}
