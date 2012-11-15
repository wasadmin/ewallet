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
import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.enums.TxnGrp;
import zw.co.esolutions.ewallet.merchantservices.service.Merchant;
import zw.co.esolutions.ewallet.merchantservices.service.MerchantServiceSOAPProxy;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.reports.util.GenerateReportUtil;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.MapUtil;
import zw.co.esolutions.ewallet.util.MoneyUtil;
import zw.co.esolutions.ewallet.util.NumberUtil;
import zw.co.esolutions.ewallet.util.URLEncryptor;

/**
 * @author tauttee
 *
 */
public class TxnLogReportBean extends PageCodeBase{

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
	private List<SelectItem> utilityMenu;
	private String utilityItem;
	private String sourceMobile;
	private String sourceAccountNumber;
	private String messageId;
	private String narrative;
	private long maxAmount;
	private long minAmount;
	private double maxValue;
	private double minValue;
	private String txnStatusItem;
	private List<SelectItem> txnStatusMenu;
	private Profile profile;
	private BankBranch bankBranch;
	private List<SelectItem> txnGrpMenu;
	private String txnGrpItem;
	private boolean flatReport;
	private List<SelectItem> reportMenu;
	private String reportItem;
		
	/**
	 * 
	 */
	public TxnLogReportBean() {
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
			super.setErrorMessage("From Date must be at least a day before or the same with To Date.");
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
		
		this.setMaxAmount(MoneyUtil.convertToCents(this.getMaxValue()));
		this.setMinAmount(MoneyUtil.convertToCents(this.getMinValue()));
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		
		String fileId = null;
		String orderBy = null;
		String queryExtension = null;
		String fileName = "txn_log_report";
		//String msg = this.checkAttributes();
		String query = null;
		String minMaxExt = "";
		String txnStatusExt = "";
		String branchQuery = "(SELECT bc.id FROM "+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH AS bc) ";
		Map<String, String> parameters = new HashMap<String, String>();
		
		if(this.isFlatReport()) {
			
			if(super.SUMMARY_REPORT.equalsIgnoreCase(this.getReportItem())) {
				fileName = "txn_log_flat_summary_report";
			} else if(super.DETAILED_REPORT.equalsIgnoreCase(this.getReportItem())) {
				fileName = "txn_log_flat_report";
			}
		} else {
			fileName = "txn_log_report";
			if(super.SUMMARY_REPORT.equalsIgnoreCase(this.getReportItem())) {
				fileName = "txn_log_summary_report";
			} else if(super.DETAILED_REPORT.equalsIgnoreCase(this.getReportItem())){
				fileName = "txn_log_report";
				if("BILL_PAY".equalsIgnoreCase(this.getTxnGrpItem())){
					fileName = "txn_log_billpay_report";
				}
			}
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
		
		
		//Start Appending Queries
		
		//Dates
		if(this.getMessageId() == null || "".equalsIgnoreCase(this.getMessageId())) {		
			queryExtension = "AND p.dateCreated >= '"+DateUtil.convertDateToTimestamp(this.getFromDate())+"' " +
							"AND p.dateCreated <= '"+DateUtil.convertDateToTimestamp(this.getToDate())+"' ";
		}
		
		
		//Utility Name
		if(!"none".equalsIgnoreCase(this.getUtilityItem())) {
			if(queryExtension == null) {
				queryExtension = "AND p.utilityName = '"+this.getUtilityItem()+"' ";
			} else {
				queryExtension = queryExtension +"AND p.utilityName = '"+this.getUtilityItem()+"' ";
			}
				
		}
		
		if("all".equalsIgnoreCase(this.getTxnGrpItem()) && "all".equalsIgnoreCase(this.getTxnItem())) {
			if(queryExtension == null) {
				queryExtension = "AND "+super.getAllTxnForProcessTxnQuery();
			} else {
				queryExtension = queryExtension+"AND "+super.getAllTxnForProcessTxnQuery();
			}
		} else if("all".equalsIgnoreCase(this.getTxnGrpItem()) && (!"all".equalsIgnoreCase(this.getTxnItem()))) {
			if(!"none".equalsIgnoreCase(this.getTxnItem())) {
				if(!this.getTxnItem().equalsIgnoreCase("all")) {
					TransactionType txnType = TransactionType.valueOf(this.getTxnItem());
					if(queryExtension == null) {
						if(/*TransactionType.TOPUP.equals(txnType) || TransactionType.BILLPAY.equals(txnType) ||*/
								TransactionType.BALANCE_REQUEST.equals(txnType)) {
							
							queryExtension = "AND "+super.getTxnGrpQuery(super.getTxnGroupByTxn(txnType).toString());
						} else {
							queryExtension = "AND p.transactionType = '"+this.getTxnItem()+"' ";
						}
					} else {
						if(/*TransactionType.TOPUP.equals(txnType) || TransactionType.BILLPAY.equals(txnType) || */
								TransactionType.BALANCE_REQUEST.equals(txnType)) {
							queryExtension = queryExtension+"AND "+super.getTxnGrpQuery(super.getTxnGroupByTxn(txnType).toString());
						} else {
							queryExtension = queryExtension+"AND p.transactionType = '"+this.getTxnItem()+"' ";
						}
						
					}
				} else {
					if(queryExtension == null) {
						queryExtension = "AND "+super.getTxnGrpQuery(this.getTxnGrpItem());
					} else {
						queryExtension = queryExtension+"AND "+super.getTxnGrpQuery(this.getTxnGrpItem());
					}
				}
			} 
		} else {
		//Transaction Type
		if(!"none".equalsIgnoreCase(this.getTxnItem())) {
			if(!this.getTxnItem().equalsIgnoreCase("all")) {
				TransactionType txnType = TransactionType.valueOf(this.getTxnItem());
				if(queryExtension == null) {
					if(/*TransactionType.TOPUP.equals(txnType) || TransactionType.BILLPAY.equals(txnType) ||*/
							TransactionType.BALANCE_REQUEST.equals(txnType)) {
						queryExtension = "AND "+super.getTxnGrpQuery(this.getTxnGrpItem());
					} else {
						queryExtension = "AND p.transactionType = '"+this.getTxnItem()+"' ";
					}
				} else {
					if(/*TransactionType.TOPUP.equals(txnType) || TransactionType.BILLPAY.equals(txnType) ||*/ 
							TransactionType.BALANCE_REQUEST.equals(txnType)) {
						queryExtension = queryExtension+"AND "+super.getTxnGrpQuery(this.getTxnGrpItem());
					} else {
						queryExtension = queryExtension+"AND p.transactionType = '"+this.getTxnItem()+"' ";
					}
					
				}
			} else {
				if(queryExtension == null) {
					queryExtension = "AND "+super.getTxnGrpQuery(this.getTxnGrpItem());
				} else {
					queryExtension = queryExtension+"AND "+super.getTxnGrpQuery(this.getTxnGrpItem());
				}
			}
		} 
		}
		
	
		
		//Source Mobile
		if(!(this.getSourceMobile() == null || "".equalsIgnoreCase(this.getSourceMobile()))) {
			try {
				this.setSourceMobile(NumberUtil.formatMobileNumber(this.getSourceMobile()));
			} catch (Exception e) {
				
			}
			if(queryExtension == null) {
				queryExtension = "AND p.sourceMobile LIKE '"+"%"+this.getSourceMobile()+"%"+"' ";
			} else {
				queryExtension = queryExtension +"AND p.sourceMobile LIKE '"+"%"+this.getSourceMobile()+"%"+"' ";
			}
		}
		
		//Source Account
		if(!(this.getSourceAccountNumber() == null || "".equalsIgnoreCase(this.getSourceAccountNumber()))) {
			if(queryExtension == null) {
				queryExtension = "AND p.sourceAccountNumber = '"+this.getSourceAccountNumber()+"' ";
			} else {
				queryExtension = queryExtension +"AND p.sourceAccountNumber = '"+this.getSourceAccountNumber()+"' ";
			}
		}
		
		//MessageId
		if(!(this.getMessageId() == null || "".equalsIgnoreCase(this.getMessageId()))) {
			if(queryExtension == null) {
				queryExtension = "AND p.messageId = '"+this.getMessageId()+"' ";
			} else {
				queryExtension = queryExtension +"AND p.messageId = '"+this.getMessageId()+"' ";
			}
		}
		
		//Narrative
		if(!(this.getNarrative() == null || "".equalsIgnoreCase(this.getNarrative()))) {
			if(queryExtension == null) {
				queryExtension = "AND p.narrative LIKE '"+"%"+this.getNarrative()+"%"+"' ";
			} else {
				queryExtension = queryExtension +"AND p.narrative LIKE '"+"%"+this.getNarrative()+"%"+"' ";
			}
		}
		
		try {
			
			if(super.getJaasUserName() != null) {
				BankBranch branch = new BankServiceSOAPProxy().findBankBranchById(new ProfileServiceSOAPProxy().getProfileByUserName(super.getJaasUserName()).getBranchId());
				if(branch != null) {
					parameters.put("bankName", branch.getBank().getName());
				}
			}
			//Checking for To Date to Display
			//this.initializeDisplayableDates();
			
			//Initialize minMaxQueries
			if(this.getMinAmount() < 0 && this.getMaxAmount() < 0) {
				super.setErrorMessage("Both minimum and maximum amount cannot be negative");
				return "failure";
			}else if(this.getMinAmount() < 0) {
				super.setErrorMessage("Minimum amount cannot be negative");
				return "failure";
			} else if (this.getMaxAmount() < 0) {
				super.setErrorMessage("Maximum amount cannot be negative");
				return "failure";
			} else if(this.getMaxAmount() == 0) {
				
				if(this.getMinAmount() == 0) {
					minMaxExt = "";
				} else {
					minMaxExt = "AND p.amount >= '"+this.getMinAmount()+"' ";
				}
			} else if(this.getMaxAmount() > 0) {
				
				if(this.getMinAmount() == 0) {
					minMaxExt = "AND p.amount <= '"+this.getMaxAmount()+"' ";
				} else {
					if(this.getMinAmount() <= this.getMaxAmount()) {
						minMaxExt = "AND p.amount >= '"+this.getMinAmount()+"' " +
							"AND p.amount <= '"+this.getMaxAmount()+"' ";
					} else {
						super.setErrorMessage("Minimum amount cannot be greater than maximum amount.");
						return "failure";
					}
				}
			}
			
			if(!"all".equalsIgnoreCase(this.getTxnStatusItem())) {
				txnStatusExt = "AND p.status = '"+TransactionStatus.valueOf(this.getTxnStatusItem())+"' ";
			}
			parameters.put("STATUS_PARAM", this.getTxnStatusItem().replace("_", " ").toUpperCase());
			
			if(this.getBankItem().equalsIgnoreCase("all")) {
				
				parameters.put("reportTitle", "Transaction Log for all Banks");
		        parameters.put("pageHeader", "All Banks : Branch Transaction Logs from "+DateUtil.convertToDateWithTime(this.getFromDate())+
				" to "+DateUtil.convertToDateWithTime(this.getToDate()));
		        
				query = "SELECT p.UTILITYNAME as utilityName, p.DESTINATIONEQ3ACCOUNTNUMBER as eq3TargetAccount, b.name AS bName, bc.name AS branchName, p.profileId, p.dateCreated, p.valueDate, p.messageId, p.transactionType, p.sourceMobile, p.sourceAccountNumber, p.targetMobile, p.destinationAccountNumber, p.amount, p.utilityAccount, p.status, p.responseCode, p.narrative, p.branchId, p.fromBankId, p.transactionLocationType, p.transactionLocationId, p.balance, p.customerName ,p.merchantRef FROM "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION AS p, " +
						""+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH AS bc, " +			
						""+EWalletConstants.DATABASE_SCHEMA+".BANK AS b WHERE " +
						" (b.id = p.fromBankId) " +
						"AND (bc.id = p.transactionLocationId OR bc.id = p.branchId) "; 
				
				orderBy ="ORDER BY b.name ASC, CASE  WHEN p.transactionLocationId IN "+branchQuery+" THEN bc.name ELSE p.transactionLocationId end ASC, p.transactionLocationType ASC, p.dateCreated DESC";
				
			} else {
				parameters.put(this.getBankItem(), bankService.findBankById(this.getBankItem()).getName());
				if(this.getBranchItem().equalsIgnoreCase("all")) {
					
					parameters = super.populateSMS(parameters, this.getBankItem());
					parameters.put("reportTitle", parameters.get(this.getBankItem()).toString()+" " +
							"Transaction Log");
					parameters.put("pageHeader", "All "+parameters.get(this.getBankItem()).toString()+
							" Branches Transaction Log from "+DateUtil.convertToDateWithTime(this.getFromDate())+
							" to "+DateUtil.convertToDateWithTime(this.getToDate()));
					
					//Clear BankId Parameter
					parameters.remove(this.getBankItem());
					
					query = "SELECT p.UTILITYNAME as utilityName, p.DESTINATIONEQ3ACCOUNTNUMBER as eq3TargetAccount, b.name AS bName, bc.name AS branchName, p.profileId, p.dateCreated, p.valueDate, p.messageId, p.transactionType, p.sourceMobile, p.sourceAccountNumber, p.targetMobile, p.destinationAccountNumber, p.amount, p.utilityAccount, p.status, p.responseCode, p.narrative, p.branchId, p.fromBankId, p.transactionLocationType, p.transactionLocationId, p.balance, p.customerName,p.merchantRef FROM " +
							""+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH AS bc, " +
							""+EWalletConstants.DATABASE_SCHEMA+".BANK AS b, " +
							EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION AS p WHERE " +
							" (b.id = p.fromBankId) " +
							"AND fromBankId = '"+this.getBankItem()+"' "+
							"AND (bc.id = p.transactionLocationId OR bc.id = p.branchId) ";
							//"AND bc.id = p.branchId ";
					
					orderBy ="ORDER BY CASE  WHEN p.transactionLocationId IN "+branchQuery+" THEN bc.name ELSE p.transactionLocationId end ASC, p.transactionLocationType ASC, p.dateCreated DESC";
					
				} else {
					BankBranch bb = bankService.findBankBranchById(this.getBranchItem());
					parameters.put(bb.getId(), bb.getName());
					parameters.put("reportTitle", parameters.get(this.getBankItem()).toString()+" " +
					"Transaction Log");
			        parameters.put("pageHeader",parameters.get(this.getBankItem()).toString()+
					" : "+bankService.findBankBranchById(this.getBranchItem()).getName()+" Branch Transaction Logs from "+DateUtil.convertToDateWithTime(this.getFromDate())+
					" to "+DateUtil.convertToDateWithTime(this.getToDate()));
			        
			        //Clear Bank and Branch
			        parameters.remove(this.getBankItem());
			        parameters.remove(bb.getId());
			        
			
					query = "SELECT p.UTILITYNAME as utilityName, p.DESTINATIONEQ3ACCOUNTNUMBER as eq3TargetAccount, b.name AS bName, bc.name AS branchName, p.profileId, p.dateCreated, p.valueDate, p.messageId, p.transactionType, p.sourceMobile, p.sourceAccountNumber, p.targetMobile, p.destinationAccountNumber, p.amount, p.utilityAccount, p.status, p.responseCode, p.narrative, p.branchId, p.fromBankId, p.transactionLocationType, p.transactionLocationId, p.balance, p.customerName,p.merchantRef FROM "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION AS p WHERE " +
					"p.transactionLocationId = '"+this.getBranchItem()+"' ";
					
					orderBy ="ORDER BY p.dateCreated DESC, p.transactionLocationType ASC, p.transactionType ASC ";
					
				}
			}
			
			if(this.isFlatReport()) {
				orderBy ="ORDER BY p.dateCreated DESC ";
			}
			if(queryExtension != null) {
				query = query + minMaxExt + txnStatusExt + queryExtension + orderBy;
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
			ctx.redirect("/EWalletReportsWebICE/jspviewer/viewer.jsp?pdfName="+URLEncryptor.encryptUrl(fileId+fileName+".pdf")+"&query="+URLEncryptor.encryptUrl(query)+"&"+EWalletConstants.MASTER_REPORT+"="+URLEncryptor.encryptUrl(MapUtil.convertAttributesMapToString(parameters))+"&pageName="+URLEncryptor.encryptUrl("txnLogReport.jspx"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "success";
	}
	
	public void handleTxnGrpValueChange(ValueChangeEvent event) {
		String item = (String)event.getNewValue();
		if(item != null) {
			try {
				this.setTxnMenu(new ArrayList<SelectItem>());
				this.txnMenu.add(new SelectItem("all", "ALL"));
				if(!(item.equalsIgnoreCase("all") || item.equalsIgnoreCase("none"))) {
					if(TxnGrp.DEPOSITS.equals(TxnGrp.valueOf(item))) {
							for(TransactionType txn : super.getDeposits()) {
							txnMenu.add(new SelectItem(txn.toString(), txn.toString()));
						}
					} else if(TxnGrp.NON_HOLDER_TXNS.equals(TxnGrp.valueOf(item))) {
						for(TransactionType txn : super.getNonHolderTxns()) {
							txnMenu.add(new SelectItem(txn.toString(), txn.toString()));
						}
					} else if(TxnGrp.TOPUP.equals(TxnGrp.valueOf(item))) {
						/*if(super.getTopupTxns().length < 2) {
							this.txnMenu.remove(0);
						}*/
						for(TransactionType txn : super.getTopupTxns()) {
							txnMenu.add(new SelectItem(txn.toString(), txn.toString()));
						}
					} else if(TxnGrp.TRANSFERS.equals(TxnGrp.valueOf(item))) {
						for(TransactionType txn : super.getTransfers()) {
							txnMenu.add(new SelectItem(txn.toString(), txn.toString()));
						}
					} else if(TxnGrp.WITHDRAWALS.equals(TxnGrp.valueOf(item))){
						for(TransactionType txn : super.getWithdrawals()) {
							txnMenu.add(new SelectItem(txn.toString(), txn.toString()));
						}
					} else if(TxnGrp.BILL_PAY.equals(TxnGrp.valueOf(item))) {
						if(super.getBillTxns().length < 2) {
							this.txnMenu.remove(0);
						}
						for(TransactionType txn : super.getBillTxns()) {
							txnMenu.add(new SelectItem(txn.toString(), txn.toString()));
						}
					}
				} else {
					this.setTxnItem("all");
					this.getTxnItem();
					for(TransactionType txn : super.getTxnLogTxnTypes()) {
						txnMenu.add(new SelectItem(txn.toString(), txn.toString()));
					}
				}
				
			} catch (Exception e) {
				
			}
		}
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
				for(BankBranch bb : this.getBankBranchList()) {
					this.getBranchMenu().add(new SelectItem(bb.getId(), bb.getName()));
				}
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
	
	/**
	 * @param utilityMenu the utilityMenu to set
	 */
	public void setUtilityMenu(List<SelectItem> utilityMenu) {
		this.utilityMenu = utilityMenu;
	}

	/**
	 * @return the utilityMenu
	 */
	public List<SelectItem> getUtilityMenu() {
		if(this.utilityMenu == null) {
			this.utilityMenu = new ArrayList<SelectItem>();
			this.utilityMenu.add(new SelectItem("none", "--select--"));
			
			try{
				MerchantServiceSOAPProxy merchantService = new MerchantServiceSOAPProxy();
				List<Merchant> merchants = merchantService.getAllMerchants();
				if(merchants != null){
					for(Merchant m: merchants){
						this.utilityMenu.add(new SelectItem(m.getShortName(),m.getName()));
					}
				}
			}catch(Exception e){e.printStackTrace();}
		}
		return utilityMenu;
	}

	/**
	 * @param utilityItem the utilityItem to set
	 */
	public void setUtilityItem(String utilityItem) {
		this.utilityItem = utilityItem;
	}

	/**
	 * @return the utilityItem
	 */
	public String getUtilityItem() {
		return utilityItem;
	}

	/**
	 * @param sourceMobile the sourceMobile to set
	 */
	public void setSourceMobile(String sourceMobile) {
		this.sourceMobile = sourceMobile;
	}

	/**
	 * @return the sourceMobile
	 */
	public String getSourceMobile() {
		return sourceMobile;
	}

	/**
	 * @param sourceAccountNumber the sourceAccountNumber to set
	 */
	public void setSourceAccountNumber(String sourceAccountNumber) {
		this.sourceAccountNumber = sourceAccountNumber;
	}

	/**
	 * @return the sourceAccountNumber
	 */
	public String getSourceAccountNumber() {
		return sourceAccountNumber;
	}

	/**
	 * @param messageId the messageId to set
	 */
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	/**
	 * @return the messageId
	 */
	public String getMessageId() {
		return messageId;
	}

	/**
	 * @param narrative the narrative to set
	 */
	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}

	/**
	 * @return the narrative
	 */
	public String getNarrative() {
		return narrative;
	}

	private void initializeDates() {
		try {
			this.setFromDate(DateUtil.getBusinessDayBeginningOfDay(DateUtil.add(new Date(), Calendar.DAY_OF_MONTH, -1)));
			this.setToDate(DateUtil.getBusinessDayEndOfDay(new Date()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*private void initializeDisplayableDates() {
		if(!this.isLatestReport()) {
			if(DateUtil.isDayBefore(DateUtil.getBeginningOfDay(this.getToDate()), DateUtil.getBeginningOfDay(new Date()))) {
				this.setToDate(DateUtil.getBusinessDayEndOfDay(this.getToDate()));
			} else if(DateUtil.isDayBeforeOrEqual(DateUtil.getBeginningOfDay(this.getToDate()), DateUtil.getBeginningOfDay(new Date()))) {
				this.setToDate(DateUtil.getBusinessDayEndOfDay(new Date()));
			} else {
				this.setToDate(DateUtil.getBusinessDayEndOfDay(this.getToDate()));
			}
		}
	}*/

	/**
	 * @param maxAmount the maxAmount to set
	 */
	public void setMaxAmount(long maxAmount) {
		this.maxAmount = maxAmount;
	}

	/**
	 * @return the maxAmount
	 */
	public long getMaxAmount() {
		return maxAmount;
	}

	/**
	 * @param minAmount the minAmount to set
	 */
	public void setMinAmount(long minAmount) {
		this.minAmount = minAmount;
	}

	/**
	 * @return the minAmount
	 */
	public long getMinAmount() {
		return minAmount;
	}

	/**
	 * @param txnStatusItem the txnStatusItem to set
	 */
	public void setTxnStatusItem(String txnStatusItem) {
		this.txnStatusItem = txnStatusItem;
	}

	/**
	 * @return the txnStatusItem
	 */
	public String getTxnStatusItem() {
		if(this.txnStatusItem == null) {
			this.txnStatusItem = "all";
		}
		return txnStatusItem;
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
			this.txnStatusMenu.add(new SelectItem(TransactionStatus.AWAITING_COLLECTION.toString(), "FUNDS AWAITING COLLECTION"));
			//this.txnStatusMenu.add(new SelectItem("", "SENT TO HOST"));
			this.txnStatusMenu.add(new SelectItem(TransactionStatus.BANK_REQUEST.toString(), "AWAITING HOST RESPONSE"));
		}
		return txnStatusMenu;
	}

	/**
	 * @param maxValue the maxValue to set
	 */
	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * @return the maxValue
	 */
	public double getMaxValue() {
		return maxValue;
	}

	/**
	 * @param minValue the minValue to set
	 */
	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	/**
	 * @return the minValue
	 */
	public double getMinValue() {
		return minValue;
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
	
	/**
	 * @param txnGrpMenu the txnGrpMenu to set
	 */
	public void setTxnGrpMenu(List<SelectItem> txnGrpMenu) {
		this.txnGrpMenu = txnGrpMenu;
	}

	/**
	 * @return the txnGrpMenu
	 */
	public List<SelectItem> getTxnGrpMenu() {
		if(this.txnGrpMenu == null) {
			this.txnGrpMenu = new ArrayList<SelectItem>();
			this.txnGrpMenu.add(new SelectItem("all", "ALL"));
			for(TxnGrp grp : TxnGrp.values()) {
				if(!TxnGrp.BALANCE_REQUEST.equals(grp)) {
					this.txnGrpMenu.add(new SelectItem(grp.toString(), grp.toString()));
				}
			}
		}
		return txnGrpMenu;
	}

	/**
	 * @param txnGrpItem the txnGrpItem to set
	 */
	public void setTxnGrpItem(String txnGrpItem) {
		this.txnGrpItem = txnGrpItem;
	}

	/**
	 * @return the txnGrpItem
	 */
	public String getTxnGrpItem() {
		return txnGrpItem;
	}

	public void setFlatReport(boolean flatReport) {
		this.flatReport = flatReport;
	}

	public boolean isFlatReport() {
		return flatReport;
	}

	public void setReportMenu(List<SelectItem> reportMenu) {
		this.reportMenu = reportMenu;
	}

	public List<SelectItem> getReportMenu() {
		if(this.reportMenu == null) {
			this.reportMenu = new ArrayList<SelectItem>();
			reportMenu.add(new SelectItem(super.SUMMARY_REPORT, super.SUMMARY_REPORT));
			reportMenu.add(new SelectItem(super.DETAILED_REPORT, super.DETAILED_REPORT));
		}
		return reportMenu;
	}

	public void setReportItem(String reportItem) {
		this.reportItem = reportItem;
	}

	public String getReportItem() {
		if(this.reportItem == null) {
			reportItem = super.SUMMARY_REPORT;
		}
		return reportItem;
	}
}
