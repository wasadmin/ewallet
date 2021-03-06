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
public class TransactionCountReportBean extends PageCodeBase{

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
	public TransactionCountReportBean() {
		this.initializeDates();
	}
	
	public String generateLatest() {
		Date date = new Date();
		this.setLatestReport(true);
		this.setFromDate(date);
		this.setToDate(date);
		this.generateReport();
		return "success";
	}
	
	public String generateReport() {
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		
		String fileId = null;
		String orderBy = "";
		String queryExt = null;
		String subQuery = "";
		String bankQuery = "";
		String branchQuery = "";
		String tempBranchSum = "";
		String tempBranchCount = "";
		String txnTypeQuery = ""; 
		String fileName = "transaction_totals_report";
		String msg = this.checkAttributes();
		String query = null;
		TransactionStatus status = TransactionStatus.COMPLETED;
		Map<String, String> parameters = new HashMap<String, String>();
		
		
		
		parameters.put("sourceFile", fileName+".xml");
		
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
			queryExt = "AND "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.dateCreated >= '"+DateUtil.convertDateToTimestamp(DateUtil.getBeginningOfDay(this.getFromDate()))+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.dateCreated <= '"+DateUtil.convertDateToTimestamp(DateUtil.getEndOfDay(this.getToDate()))+"' ";
		}
		
		
		try {
			
			//Checking for To Date to Display
			this.initializeDisplayableDates();
			
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
				
				parameters.put("reportTitle", "Transaction Totals for all Banks");
		        parameters.put("pageHeader", "All Banks : Branch Transaction Totals from "+DateUtil.convertToDateWithTime(this.getFromDate())+
				" to "+DateUtil.convertToDateWithTime(this.getToDate()));
		        
				orderBy ="ORDER BY "+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH.NAME ASC ";
				
			} else {
				parameters.put(this.getBankItem(), bankService.findBankById(this.getBankItem()).getName());
				if(this.getBranchItem().equalsIgnoreCase("all")) {
					this.setBankBranchList(bankService.getBankBranchByBank(this.getBankItem()));
					if(!(this.getBankBranchList() == null || this.getBankBranchList().isEmpty())) {
						for(BankBranch bb : this.getBankBranchList()) {
							parameters.put(bb.getId(), bb.getName());							
						}
						parameters.put("reportTitle", parameters.get(this.getBankItem()).toString()+" " +
								"Transaction Totals");
						parameters.put("pageHeader", "All "+parameters.get(this.getBankItem()).toString()+
								" Branches Transaction Totals from "+DateUtil.convertToDateWithTime(this.getFromDate())+
								" to "+DateUtil.convertToDateWithTime(this.getToDate()));
					}
					
					bankQuery = "AND fromBankId = '"+this.getBankItem()+"' ";
					orderBy ="ORDER BY "+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH.NAME ASC ";
					
				} else {
					BankBranch bb = bankService.findBankBranchById(this.getBranchItem());
					parameters.put(bb.getId(), bb.getName());
					parameters.put("reportTitle", parameters.get(this.getBankItem()).toString()+" " +
					"Transaction Totals");
			        parameters.put("pageHeader",parameters.get(this.getBankItem()).toString()+
					" : "+bankService.findBankBranchById(this.getBranchItem()).getName()+" Branch Transaction Totals from "+DateUtil.convertToDateWithTime(this.getFromDate())+
					" to "+DateUtil.convertToDateWithTime(this.getToDate()));
			        
					branchQuery = "AND "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.branchId = '"+this.getBranchItem()+"' ";
					orderBy ="ORDER BY "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.customerName ASC";
					
				}
			}
			
			//Building Count Query
			for(TransactionType txnType : TransactionType.values()) {
        	   String sAppender = appendTxnTypeSum(txnType);
        	   String cAppender = appendTxnTypeCount(txnType);
        	   if(this.isTxnInList(txnType)) {
        		   if("".equalsIgnoreCase(subQuery)) {
        			   if(queryExt == null) {
        				  
        				   String cTempQuery = "(SELECT COUNT("+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType) AS "+cAppender+" "+
		        			"FROM "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION WHERE " +
							""+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType = '"+txnType+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.status = '"+status+"' " +
							""+bankQuery+" " +
							""+branchQuery+") ";
        				   
        				   String sTempQuery = "(SELECT SUM("+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.amount) AS "+sAppender+" "+
		        			"FROM "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION WHERE " +
							""+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType = '"+txnType+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.status = '"+status+"' " +
							""+bankQuery+" " +
							""+branchQuery+") ";
        				   
        				   subQuery = cTempQuery +", "+ sTempQuery;
        				   
        			   } else {
        				   String cTempQuery = "(SELECT COUNT("+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType) AS "+cAppender+" "+
		        			"FROM "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION WHERE " +
							""+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType = '"+txnType+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.status = '"+status+"' " +
							""+queryExt+" " +
							""+bankQuery+" " +
							""+branchQuery+") ";
       				   
       				      String sTempQuery = "(SELECT SUM("+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.amount) AS "+sAppender+" "+
		        			"FROM "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION WHERE " +
							""+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType = '"+txnType+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.status = '"+status+"' " +
							""+queryExt+" " +
							""+bankQuery+" " +
							""+branchQuery+") ";
       				   
       				   subQuery = subQuery + ", "+cTempQuery +", "+ sTempQuery;
        			   }
        		   } else {
        			   if(queryExt == null) {
        				   String cTempQuery = "(SELECT COUNT("+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType) AS "+cAppender+" "+
		        			"FROM "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION WHERE " +
							""+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType = '"+txnType+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.status = '"+status+"' " +
							""+bankQuery+" " +
							""+branchQuery+") ";
      				   
      				      String sTempQuery = "(SELECT SUM("+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.amount) AS "+sAppender+" "+
		        			"FROM "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION WHERE " +
							""+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType = '"+txnType+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.status = '"+status+"' " +
							""+bankQuery+" " +
							""+branchQuery+") ";
      				   
      				   subQuery = subQuery + ", "+cTempQuery +", "+ sTempQuery; 
        			   } else {
        				   String cTempQuery = "(SELECT COUNT("+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType) AS "+cAppender+" "+
		        			"FROM "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION WHERE " +
							""+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType = '"+txnType+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.status = '"+status+"' " +
							""+queryExt+" " +
							""+bankQuery+" " +
							""+branchQuery+") ";
      				   
      				      String sTempQuery = "(SELECT SUM("+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.amount) AS "+sAppender+" "+
		        			"FROM "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION WHERE " +
							""+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType = '"+txnType+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.status = '"+status+"' " +
							""+queryExt+" " +
							""+bankQuery+" " +
							""+branchQuery+") ";
      				   
      				   subQuery = subQuery + ", "+cTempQuery +", "+ sTempQuery; 
        			   }
				  }
        	   }
	        	
	        }
			
			txnTypeQuery = "AND ("+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType = '"+TransactionType.BALANCE+"' " +
			"OR "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType = '"+TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER+"' " +
			"OR "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType = '"+TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER+"' " +
			"OR "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType = '"+TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER+"' " +
			"OR "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType = '"+TransactionType.BILLPAY+"' " +
			"OR "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType = '"+TransactionType.DEPOSIT+"' " +
			"OR "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType = '"+TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER+"' " +
			"OR "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType = '"+TransactionType.EWALLET_TO_EWALLET_TRANSFER+"' " +
			"OR "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType = '"+TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER+"' " +
			"OR "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType = '"+TransactionType.MINI_STATEMENT+"' " +
			"OR "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType = '"+TransactionType.TOPUP+"' " +
			"OR "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType = '"+TransactionType.WITHDRAWAL+"' " +
			"OR "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.transactionType = '"+TransactionType.WITHDRAWAL_NONHOLDER+"') ";
			
			
			//Building and Merging Sub Queries
			if(queryExt == null) {
				tempBranchCount = "(SELECT COUNT("+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.branchId) AS branchCount "+
    			"FROM "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION WHERE " +
				EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.status = '"+status+"' ";
				
				
				tempBranchSum = "(SELECT SUM("+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.amount) AS branchSum "+
    			"FROM "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION WHERE " +
				EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.status = '"+status+"' ";
				
				
			} else {
				tempBranchCount = "(SELECT COUNT("+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.branchId) AS branchCount "+
    			"FROM "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION WHERE " +
				EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.status = '"+status+"' " +
				queryExt;
				
				tempBranchSum = "(SELECT SUM("+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.amount) AS branchSum "+
    			"FROM "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION WHERE " +
				EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.status = '"+status+"' " +
				queryExt;
				
			}
			
			tempBranchCount = tempBranchCount +txnTypeQuery + bankQuery+branchQuery+") ";
			tempBranchSum = tempBranchSum +txnTypeQuery + bankQuery+branchQuery+") ";
			
			subQuery = subQuery+", "+tempBranchCount+", "+tempBranchSum;
			
			orderBy = "";
			
			//Building Compound Query
			query =  "SELECT DISTINCT("+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.fromBankId) AS bankId "+subQuery+"" +
			"FROM  "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION, "+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH WHERE " +
			""+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.status = '"+status+"' "+
			branchQuery+" " +
			bankQuery+" "+
			orderBy;
			
			
			
			
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
			ctx.redirect("/EWalletReportsWebICE/jspviewer/viewer.jsp?pdfName="+URLEncryptor.encryptUrl(fileId+fileName+".pdf")+"&query="+URLEncryptor.encryptUrl(query)+"&"+EWalletConstants.MASTER_REPORT+"="+URLEncryptor.encryptUrl(MapUtil.convertAttributesMapToString(parameters))+"&pageName="+URLEncryptor.encryptUrl("transactionTotalsReport.jspx"));
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
				if(!item.equalsIgnoreCase("all")) {
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
					this.setBranchMenu(null);
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
		if(this.isLatestReport()) {
			if(this.getFromDate() == null) {
				buffer.append("From Date, ");
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
		if(this.fromDate != null) {
			this.fromDate = DateUtil.getBeginningOfDay(this.fromDate);
		}
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
	
	private String appendTxnTypeCount(TransactionType txnType) {
		String append = null;
		if(TransactionType.BALANCE.equals(txnType)) {
			append = "balCount";
		} 	else if(TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER.equals(txnType)) {
			append = "btbCount";
		}	else if(TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER.equals(txnType)) {
			append = "bteCount";
		}	 else if(TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER.equals(txnType)) {
			append = "btnCount";
		}	 else if(TransactionType.BILLPAY.equals(txnType)) {
			append = "billCount";
		}	 else if(TransactionType.DEPOSIT.equals(txnType)) {
			append = "depositCount";
		}	 else if(TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER.equals(txnType)) {
			append = "etbCount";
		}	 else if(TransactionType.EWALLET_TO_EWALLET_TRANSFER.equals(txnType)) {
				append = "eteCount";
		} 	else if(TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER.equals(txnType)) {
			append = "etnCount";
		}	 else if(TransactionType.MINI_STATEMENT.equals(txnType)) {
			append = "miniCount";
		}	 else if(TransactionType.TOPUP.equals(txnType)) {
			append = "topupCount";
		}	 else if(TransactionType.WITHDRAWAL.equals(txnType)) {
			append = "withCount";
		}	 else if(TransactionType.WITHDRAWAL_NONHOLDER.equals(txnType)) {
			append = "withNonCount";
		}
		return append;
	}
	
	private String appendTxnTypeSum(TransactionType txnType) {
		String append = null;
		if(TransactionType.BALANCE.equals(txnType)) {
			append = "balSum";
		} 	else if(TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER.equals(txnType)) {
			append = "btbSum";
		}	else if(TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER.equals(txnType)) {
			append = "bteSum";
		}	 else if(TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER.equals(txnType)) {
			append = "btnSum";
		}	 else if(TransactionType.BILLPAY.equals(txnType)) {
			append = "billSum";
		}	 else if(TransactionType.DEPOSIT.equals(txnType)) {
			append = "depositSum";
		}	 else if(TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER.equals(txnType)) {
			append = "etbSum";
		}	 else if(TransactionType.EWALLET_TO_EWALLET_TRANSFER.equals(txnType)) {
			append = "eteSum";
		}	else if(TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER.equals(txnType)) {
			append = "etnSum";
		}	 else if(TransactionType.MINI_STATEMENT.equals(txnType)) {
			append = "miniSum";
		}	 else if(TransactionType.TOPUP.equals(txnType)) {
			append = "topupSum";
		}	 else if(TransactionType.WITHDRAWAL.equals(txnType)) {
			append = "withSum";
		}	 else if(TransactionType.WITHDRAWAL_NONHOLDER.equals(txnType)) {
			append = "withNonSum";
		}
		return append;
	}
	
	private boolean isTxnInList(TransactionType txnType) {
		boolean isIn = (TransactionType.BALANCE.equals(txnType) || TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER.equals(txnType) ||
				TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER.equals(txnType) || TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER.equals(txnType) ||
				TransactionType.DEPOSIT.equals(txnType) || TransactionType.BILLPAY.equals(txnType) ||
				TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER.equals(txnType) || TransactionType.EWALLET_TO_EWALLET_TRANSFER.equals(txnType) ||
				TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER.equals(txnType) || TransactionType.TOPUP.equals(txnType) || 
				TransactionType.MINI_STATEMENT.equals(txnType) || TransactionType.WITHDRAWAL.equals(txnType) || 
				TransactionType.WITHDRAWAL_NONHOLDER.equals(txnType));
		return isIn;
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
