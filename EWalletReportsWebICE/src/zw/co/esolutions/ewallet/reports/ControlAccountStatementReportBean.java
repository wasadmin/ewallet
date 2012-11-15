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
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountStatus;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.enums.BankAccountType;
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
public class ControlAccountStatementReportBean extends PageCodeBase{
	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(ControlAccountStatementReportBean.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + ControlAccountStatementReportBean.class);
		}
	}
	
	private void log4(String message) {
		LOG.debug(message);
	}
	private Date fromDate;
	private Date toDate;
	private List<SelectItem> bankMenu;
	private String bankItem;
	private List<Bank> bankList;
	private Profile profile;
	private BankBranch bankBranch;
		
	/**
	 * 
	 */
	public ControlAccountStatementReportBean() {
		this.initializeDates();
		if(this.getBankBranch() != null) {
			this.setBankItem(this.getBankBranch().getBank().getId());
			
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
		
		String fileId = null;
		String query = null;
		String queryExtension = null;
		String fileName = "pool_account_statement_report";
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("sourceFile", fileName+".xml");
		
		
		
		try {
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
				
			}
			String subQuery = "SELECT t.id FROM BANKIF.TRANSACTION as t WHERE t.transactiondate >= '"+ DateUtil.convertDateToTimestamp(DateUtil.getBeginningOfDay((this.getFromDate()))) +"' and t.transactiondate <='"+DateUtil.convertDateToTimestamp(DateUtil.getEndOfDay(this.getToDate())) +"' " +
			   "AND (t.narrative LIKE '%"+"Charge"+"%' OR t.narrative LIKE '%"+"charge"+"%' OR t.narrative LIKE '%"+"CHARGE"+"%') ";
			queryExtension = "AND (ba.branch_id = bh.id AND bh.bank_id = '"+this.getBankItem()+"' AND ba.type = '"+BankAccountType.E_WALLET+"') " +
					"AND t.id NOT IN ("+subQuery+") ";
			query ="SELECT t.transactiondate as valueDate,t.type as transactionType , bh.bank_id as bankId ,t.processtxnreference as messageId,t.amount, t.accountId, t.narrative FROM BANKIF.TRANSACTION as t, BANKIF.BANKACCOUNT as ba, BANKIF.BANKBRANCH as bh WHERE t.accountid = ba.id "+ queryExtension + " and t.transactiondate >= '"+ DateUtil.convertDateToTimestamp(DateUtil.getBeginningOfDay((this.getFromDate()))) +"' and t.transactiondate <='"+DateUtil.convertDateToTimestamp(DateUtil.getEndOfDay(this.getToDate())) +"' ORDER BY t.transactiondate DESC";
			parameters.put("bankName", new BankServiceSOAPProxy().findBankById(this.getBankItem()).getName());
			//this.initializeDisplayableDates();
			parameters.put("fromDate", DateUtil.convertDateToLongString(DateUtil.getBeginningOfDay(this.getFromDate())));
			parameters.put("toDate", DateUtil.convertDateToLongString(this.getToDate()));
			parameters.put("asAtDate", DateUtil.convertDateToLongString(this.getToDate()));
			fileId = GenerateReportUtil.getJasperUserId(super.getJaasUserName());
			parameters.put("fileId", fileId);
			BankAccount acc = null;
			List<BankAccount> accounts = new BankServiceSOAPProxy().getBankAccountsByMinAttributes(this.getBankItem(), null, zw.co.esolutions.ewallet.bankservices.service.BankAccountType.POOL_CONTROL, null, null, BankAccountStatus.ACTIVE, 0, 0);
			
			loop : for(BankAccount ba : accounts) {
				acc = ba;
				break loop;
			}
			parameters.put("reportTitle", acc.getAccountName() +" : "+acc.getAccountNumber()+" ["+acc.getType().toString()+"] " +
			"Account Statement");
			parameters.put("pageHeader", "Account Statement for Period : "+DateUtil.convertToDateWithTime(this.getFromDate())+
			" to "+DateUtil.convertToDateWithTime(this.getToDate()));
			this.log4(">>>>>>>>>> Query "+query);
			super.setInformationMessage("Report successfully generated.");
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage("An error has occured. Report not generated.");
			return "failure";
		}
		try {
			ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
			
			ctx.redirect("/EWalletReportsWebICE/jspviewer/viewer.jsp?pdfName="+URLEncryptor.encryptUrl(fileId+fileName+".pdf")+"&query="+URLEncryptor.encryptUrl(query)+"&"+EWalletConstants.MASTER_REPORT+"="+URLEncryptor.encryptUrl(MapUtil.convertAttributesMapToString(parameters))+"&pageName="+URLEncryptor.encryptUrl("poolControlAccountStatementReport.jspx"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "success";
	}
	
	
	private String checkAttributes(){
		StringBuffer buffer = new StringBuffer("The following values are required: ");
		int initialLength = buffer.length();
		if(this.getBankItem().equalsIgnoreCase("none")) {
			buffer.append("Bank, ");
		}	if(this.getFromDate() == null) {
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
				for(Bank bk : this.getBankList()) {
					bankMenu.add(new SelectItem(bk.getId(),bk.getName()+" EWALLET POOL ACCOUNT "));
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
	
	public void initializeDisplayableDates() {
		if(true) {
			if(DateUtil.isDayBefore(DateUtil.getBeginningOfDay(this.getToDate()), DateUtil.getBeginningOfDay(new Date()))) {
				this.setToDate(DateUtil.getEndOfDay(this.getToDate()));
			} else if(DateUtil.isDayBeforeOrEqual(DateUtil.getBeginningOfDay(this.getToDate()), DateUtil.getBeginningOfDay(new Date()))) {
				this.setToDate(new Date());
			} else {
				this.setToDate(DateUtil.getEndOfDay(this.getToDate()));
			}
			this.setFromDate(DateUtil.getBeginningOfDay(this.getFromDate()));
		}
	}
	
}
