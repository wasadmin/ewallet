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
public class CashMovementReportBean extends PageCodeBase{

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
	
	
	
	/**
	 * 
	 */
	public CashMovementReportBean() {
		this.initializeDates();
	}
	
	public String generateReport() {
		this.setFromDate(DateUtil.getBeginningOfDay(this.getFromDate()));
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		String fileId = null;
		String orderBy = null;
		String queryExtension = null;
		String fileName = "cash_movement_report";
		String fileNameConn = "cash_movement_sub_report";
		String msg = this.checkAttributes();
		String query = null;
		String entitiesExt = "";
		Map<String, String> parameters = new HashMap<String, String>();
		Map<String, String> subReportParameters = new HashMap<String, String>();
		parameters.put("sourceFile", fileName+".xml");
		subReportParameters.put("sourceFile", fileNameConn+".xml");
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
		// Check for ToDate
		if(this.getToDate() != null) {
			if(!DateUtil.isDayBefore(DateUtil.getBeginningOfDay(getToDate()), DateUtil.getEndOfDay(new Date()))) {
				super.setErrorMessage("To Date cannot come after this Today's Date.");
				return "failure";
			}
		}
		
		//Start Appending Queries
		if(this.getFromDate() != null && this.getToDate() != null) {
			// toDate must be greater than fromDate
			if(!DateUtil.isDayBefore(DateUtil.getBeginningOfDay(getFromDate()), DateUtil.getEndOfDay(getToDate()))) {
				super.setErrorMessage("From Date must be a day before(or the same with) To Date.");
				return "failure";
			}
			
			//Dates
			queryExtension = "AND "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.dateCreated >= '"+DateUtil.convertDateToTimestamp(DateUtil.getBeginningOfDay(this.getFromDate()))+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.dateCreated <= '"+DateUtil.convertDateToTimestamp(DateUtil.getEndOfDay(this.getToDate()))+"' ";
		}
		entitiesExt =  EWalletConstants.DATABASE_SCHEMA+".BANK, "+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH, "+EWalletConstants.DATABASE_SCHEMA+".PROFILE, ";
		
		if(queryExtension == null) {
			queryExtension = "AND "+EWalletConstants.DATABASE_SCHEMA+".PROFILE.id = "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.profileId ";
		} else {
			queryExtension = queryExtension + "AND "+EWalletConstants.DATABASE_SCHEMA+".PROFILE.id = "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.profileId ";
		}
		
		if(queryExtension == null) {
			queryExtension =  "AND ("+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.status = '"+TransactionStatus.COMPLETED+"' " +
			"AND ("+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType = '"+TransactionType.DEPOSIT+"' "+
			"OR "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType = '"+TransactionType.WITHDRAWAL+"' "+
			"OR "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType = '"+TransactionType.WITHDRAWAL_NONHOLDER+"')) ";
		} else {
			queryExtension = queryExtension+ "AND ("+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.status = '"+TransactionStatus.COMPLETED+"' " +
			"AND ("+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType = '"+TransactionType.DEPOSIT+"' "+
			"OR "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType = '"+TransactionType.WITHDRAWAL+"' "+
			"OR "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType = '"+TransactionType.WITHDRAWAL_NONHOLDER+"')) ";
		}
		
		
		
		try {
			
			parameters.put("fromDate", DateUtil.convertDateToLongString(DateUtil.getBeginningOfDay(this.getFromDate())));
			parameters.put("toDate", DateUtil.convertDateToLongString(DateUtil.getEndOfDay(this.getToDate())));
			parameters.put("schema", EWalletConstants.DATABASE_SCHEMA);
			
			//Checking for To Date to Display
			this.initializeDisplayableDates();
			
			subReportParameters.put("profileId", "");
			subReportParameters.put("fromDate", DateUtil.convertDateToLongString(DateUtil.getBeginningOfDay(this.getFromDate())));
			subReportParameters.put("toDate", DateUtil.convertDateToLongString(DateUtil.getEndOfDay(this.getToDate())));
			subReportParameters.put("schema", EWalletConstants.DATABASE_SCHEMA);
			subReportParameters.put("branchId", "");
			//parameters.put(EWalletConstants.SUBREPORT, EWalletConstants.SUBREPORT);
			
						
			if(this.getBankItem().equalsIgnoreCase("all")) {
				for(Bank bnk : bankService.getBank()) {
				    if(bnk != null) {
				    	parameters.put(bnk.getId(), bnk.getName());
				    	for(BankBranch bh : bankService.getBankBranchByBank(bnk.getId())) {
				    		if(bh != null) {
				    			parameters.put(bh.getId(), bh.getName());
				    		}
				    	}
				    }
				    
				}
				
				parameters.put("reportTitle", "Cash Movement for all Banks");
		        parameters.put("pageHeader", "All Banks : Branch Cash Movement from "+DateUtil.convertToDateWithTime(this.getFromDate())+
				" to "+DateUtil.convertToDateWithTime(this.getToDate()));
		        
				query = "SELECT "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.dateCreated, profileId, valueDate, messageId, transactionType, sourceMobile, sourceAccountNumber, targetMobile, destinationAccountNumber, amount, utilityAccount, "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.status, responseCode, narrative, "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.branchId, fromBankId, transactionLocationType, transactionLocationId, balance, customerName  FROM "
						+entitiesExt+		
						EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION WHERE "+
						EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.messageId = "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.messageId " +
								"AND "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.fromBankId = "+EWalletConstants.DATABASE_SCHEMA+".BANK.id " +
								"AND "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionLocationId = "+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH.id ";
				 		
				orderBy ="ORDER BY "+EWalletConstants.DATABASE_SCHEMA+".BANK.NAME ASC, "+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH.NAME ASC , " +
						""+EWalletConstants.DATABASE_SCHEMA+".PROFILE.lastName ASC, "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType ASC";
				
			} else {
				parameters.put(this.getBankItem(), bankService.findBankById(this.getBankItem()).getName());
				if(this.getBranchItem().equalsIgnoreCase("all")) {
					this.setBankBranchList(bankService.getBankBranchByBank(this.getBankItem()));
					if(!(this.getBankBranchList() == null || this.getBankBranchList().isEmpty())) {
						for(BankBranch bb : this.getBankBranchList()) {
							parameters.put(bb.getId(), bb.getName());							
						}
						parameters.put("reportTitle", parameters.get(this.getBankItem()).toString()+" " +
								"Cash Movement");
						parameters.put("pageHeader", "All "+parameters.get(this.getBankItem()).toString()+
								" Branches Cash Movement from "+DateUtil.convertToDateWithTime(this.getFromDate())+
								" to "+DateUtil.convertToDateWithTime(this.getToDate()));
					}
					
					query = "SELECT "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.dateCreated, profileId, valueDate, messageId, transactionType, sourceMobile, sourceAccountNumber, targetMobile, destinationAccountNumber, amount, utilityAccount, "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.status, responseCode, narrative, "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.branchId, fromBankId, transactionLocationType, transactionLocationId, balance, customerName FROM " +
							entitiesExt+
							""+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION WHERE " +
							"fromBankId = '"+this.getBankItem()+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.fromBankId = "+EWalletConstants.DATABASE_SCHEMA+".BANK.id "+
							"AND "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionLocationId = "+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH.id ";
							
					orderBy ="ORDER BY "+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH.NAME ASC, "+EWalletConstants.DATABASE_SCHEMA+".PROFILE.lastName ASC, "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType ASC";
					
				} else {
					BankBranch bb = bankService.findBankBranchById(this.getBranchItem());
					parameters.put(bb.getId(), bb.getName());
					parameters.put("reportTitle", parameters.get(this.getBankItem()).toString()+" " +
					"Cash Movement");
			        parameters.put("pageHeader",parameters.get(this.getBankItem()).toString()+
					" : "+bankService.findBankBranchById(this.getBranchItem()).getName()+" Branch Cash Movement from "+DateUtil.convertToDateWithTime(this.getFromDate())+
					" to "+DateUtil.convertToDateWithTime(this.getToDate()));
			      
					query = "SELECT "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.dateCreated, profileId, valueDate, messageId, transactionType, sourceMobile, sourceAccountNumber, targetMobile, destinationAccountNumber, amount, utilityAccount, "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.status, responseCode, narrative, "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.branchId, fromBankId, transactionLocationType, transactionLocationId, balance, customerName FROM "+
					entitiesExt+
					EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION WHERE " +
					""+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionLocationId = '"+this.getBranchItem()+"' " +
					"AND "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionLocationId = "+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH.id "+
					"AND "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.fromBankId = "+EWalletConstants.DATABASE_SCHEMA+".BANK.id ";
					orderBy ="ORDER BY "+EWalletConstants.DATABASE_SCHEMA+".PROFILE.lastName ASC, "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType ASC";
					
				}
			}
			
			//orderBy = "ORDER BY "+EWalletConstants.DATABASE_SCHEMA+".PROFILE.lastName ASC";
			
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
			ctx.redirect("/EWalletReportsWebICE/jspviewer/viewer.jsp?pdfName="+URLEncryptor.encryptUrl(fileId+fileName+".pdf")+"&query="+URLEncryptor.encryptUrl(query)+"&"+EWalletConstants.MASTER_REPORT+"="+URLEncryptor.encryptUrl(MapUtil.convertAttributesMapToString(parameters))+""+"&"+EWalletConstants.SUBREPORT+"="+URLEncryptor.encryptUrl(MapUtil.convertAttributesMapToString(subReportParameters))+"&pageName="+URLEncryptor.encryptUrl("cashMovementReport.jspx"));
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
		if(this.getFromDate() == null) {
			buffer.append("From Date, ");
			
		}
		if(this.getToDate() == null) {
			buffer.append("To Date, ");
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

	
	private void initializeDates() {
		this.setFromDate(new Date());
		this.setToDate(new Date());
	}
	
	private void initializeDisplayableDates() {
		if(!this.isLatestReport()) {
			if(DateUtil.isDayBefore(DateUtil.getBeginningOfDay(this.getToDate()), DateUtil.getBeginningOfDay(new Date()))) {
				this.setToDate(DateUtil.getEndOfDay(this.getToDate()));
			} else if(DateUtil.isDayBeforeOrEqual(DateUtil.getBeginningOfDay(this.getToDate()), DateUtil.getBeginningOfDay(new Date()))) {
				this.setToDate(new Date());
			} else {
				this.setToDate(DateUtil.getEndOfDay(this.getToDate()));
			}
		}
	}

	
}
