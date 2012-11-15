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

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.enums.BankAccountType;
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
public class BankAccountRegStatisticsReportBean extends PageCodeBase{
	private static Logger LOG;
	
	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(BankAccountRegStatisticsReportBean.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + BankAccountRegStatisticsReportBean.class);
		}
	}
	
	private void log4(String message) {
		LOG.debug(message);
	}
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
	public BankAccountRegStatisticsReportBean() {
		this.initializeDates();
	}
	
	public String generateReport() {
		this.setFromDate(DateUtil.getBeginningOfDay(this.getFromDate()));
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		System.out.println(">>>>>>>>>>>>>>> Tapinda");
		String fileId = null;
		String fileName = "bank_accounts_registration_statistics_report";
		String msg = this.checkAttributes();
		String query = null;
		String bankQuery = "";
		String branchQuery = "";
		String cQuery = "";
		String queryExt = null;
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("sourceFile", fileName+".xml");
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
		
		String qry = "SELECT bra.name as branch, b.type,b.status,Count(b.type) as accounts FROM BANKIF.BANKACCOUNT as b LEFT JOIN BANKIF.BANKBRANCH as bra ON b.branch_id=bra.id  WHERE  ";
		qry += " bra.BANK_ID='"+ getBankItem() + "' ";
		if(this.getFromDate() != null && this.getToDate() != null) {
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
			
			qry += " AND b.dateCreated >= '"+DateUtil.convertDateToTimestamp(DateUtil.getBeginningOfDay(this.getFromDate()))+"' " +
			"AND b.dateCreated <= '"+DateUtil.convertDateToTimestamp(DateUtil.getEndOfDay(this.getToDate()))+"' ";

			
		}
		try {
			
			//Checking for To Date to Display
			this.initializeDisplayableDates();
			
			 
				//parameters.put(this.getBankItem(), bankService.findBankById(this.getBankItem()).getName());
				if(this.getBranchItem().equalsIgnoreCase("all")) {
					this.setBankBranchList(bankService.getBankBranchByBank(this.getBankItem()));
					if(!(this.getBankBranchList() == null || this.getBankBranchList().isEmpty())) {
//						for(BankBranch bb : this.getBankBranchList()) {
//							parameters.put(bb.getId(), bb.getName());							
//						}
						parameters.put("reportTitle", bankService.findBankById(this.getBankItem()).getName()+" " +
								"Statistics");
						parameters.put("pageHeader", "All "+ bankService.findBankById(this.getBankItem()).getName()
								+ " Branches Statistics from "+ DateUtil.convertToDateWithTime(this.getFromDate())
								+ " to "+ DateUtil.convertToDateWithTime(this.getToDate()));
					}
					
									
					
				} else {
					BankBranch bb = bankService.findBankBranchById(this.getBranchItem());
//					parameters.put(bb.getId(), bb.getName());
					parameters.put("reportTitle", bankService.findBankById(this.getBankItem()).getName()+" " +
					"Statistics");
					parameters.put("pageHeader", bb.getBank().getName()+ " : "+ bankService.findBankBranchById(
							this.getBranchItem()).getName()+ " Branch Statistics from "+ DateUtil.convertToDateWithTime(this.getFromDate())
					+ " to "+ DateUtil.convertToDateWithTime(this.getToDate()));
					qry += " AND  b.branch_id ='"+this.getBranchItem()+"' "; 
					
					
				}
			
				
				
			
			
			qry += " GROUP BY bra.name,b.type,b.status ORDER BY bra.name,b.type ";
			
			System.out.println(">>>>>>>>>>>>>>>> Query = "+qry);
			fileId = GenerateReportUtil.getJasperUserId(super.getJaasUserName());
			parameters.put("fileId", fileId);
			
			super.setInformationMessage("Report successfully generated.");
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage("An error has occured. Report not generated.");
			return "failure";
		}
		ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
		try {
			ctx.redirect("/EWalletReportsWebICE/jspviewer/viewer.jsp?pdfName="+URLEncryptor.encryptUrl(fileId+fileName+".pdf")+"&query="+URLEncryptor.encryptUrl(qry)+"&"+EWalletConstants.MASTER_REPORT+"="+URLEncryptor.encryptUrl(MapUtil.convertAttributesMapToString(parameters))+"&pageName="+URLEncryptor.encryptUrl("bankAccountRegStatisticsReport.jspx"));
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
		this.generateReport();
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
	
	private String appendAccountTypes(BankAccountType type) {
		String append = null;
		if(BankAccountType.AGENT_COMMISSION_SOURCE.equals(type)) {
			append = "2c";
		} else  if(BankAccountType.BANK_TO_MOBILE_CONTROL.equals(type)) {
			append = "3c";
		} else  if(BankAccountType.BANK_TO_NON_MOBILE_CONTROL.equals(type)) {
			append = "4c";
		}else  if(BankAccountType.BRANCH_CASH_ACCOUNT.equals(type)) {
			append = "5c";
		}else  if(BankAccountType.MOBILE_TO_BANK_CONTROL.equals(type)) {
			append = "6c";
		} else  if(BankAccountType.PAYOUT_CONTROL.equals(type)) {
			append = "7c";
		} else  if(BankAccountType.POOL_CONTROL.equals(type)) {
			append = "8c";
		} else  if(BankAccountType.TARIFFS_CONTROL.equals(type)) {
			append = "9c";
		} 
		
		//Actual Bank Accounts
		else  if(BankAccountType.CHEQUE.equals(type)) {
			append = "1b";
		}else  if(BankAccountType.CURRENT.equals(type)) {
			append = "2b";
		}else  if(BankAccountType.SAVINGS.equals(type)) {
			append = "3b";
		} 
		
		//Ewallets
		else  if(BankAccountType.E_WALLET.equals(type)) {
			append = "e";
		}else  if(BankAccountType.EWALLET_BALANCING_SUSPENSE_ACCOUNT.equals(type)) { // Already implemented
			append = "1e";
		}else  if(BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT.equals(type)) {// Already implemented
			append = "2e";
		}else  if(BankAccountType.AGENT_COMMISSION_SUSPENSE.equals(type)) {//
			append = "3e";
		}else  if(BankAccountType.AGENT_EWALLET.equals(type)) {//
			append = "4e";
		}
		return append;
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
			if(this.getFromDate() == null && this.getToDate() != null) {
				buffer.append("From Date, ");
			} if(this.getFromDate() != null && this.getToDate() == null) {
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
		if(this.bankItem == null) {
			try {
				this.bankItem = new BankServiceSOAPProxy().findBankBranchById(new ProfileServiceSOAPProxy().getProfileByUserName(super.getJaasUserName()).getBranchId()).getBank().getId();
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
		System.out.println(">>>>>>>>>>>>>.. Bank Item = "+this.bankItem);
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
		Date date = new Date();
		if(this.getFromDate() == null) {
			this.setFromDate(DateUtil.getBeginningOfDay(DateUtil.add(date, Calendar.MONTH, -EWalletConstants.REPORT_MONTHS)));
		}
		if(this.getToDate() == null) {
			this.setToDate(DateUtil.getEndOfDay(date));
		}
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
