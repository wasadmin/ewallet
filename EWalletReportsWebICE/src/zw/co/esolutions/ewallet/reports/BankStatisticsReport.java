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
import zw.co.esolutions.ewallet.enums.BankAccountStatus;
import zw.co.esolutions.ewallet.enums.BankAccountType;
import zw.co.esolutions.ewallet.reports.util.GenerateReportUtil;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.MapUtil;
import zw.co.esolutions.ewallet.util.URLEncryptor;

public class BankStatisticsReport extends PageCodeBase{
	
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
	private Map<String,Object> attributesMap;
	
	/**
	 * 
	 */
	public BankStatisticsReport() {
		this.initializeDates();
	}
	
	public String generateReport() {
		this.setFromDate(DateUtil.getBeginningOfDay(this.getFromDate()));
		String fileId = null;
		String fileName = "bank_statistics_report";
		String msg = this.checkAttributes();
		String query = null;
		String queryHead ="SELECT BNK.NAME AS BANK,BR.NAME AS BRANCH,BR.CODE AS BRANCH_CODE,PR.STATUS AS STATUS,COUNT(PR.STATUS) AS NO_OF_TXN , SUM(PR.AMOUNT) AS TOTAL_AMOUNT FROM BANKIF.PROCESSTRANSACTION AS PR LEFT JOIN BANKIF.BANK AS BNK ON PR.FROMBANKID = BNK.ID LEFT JOIN BANKIF.BANKBRANCH AS BR ON PR.BRANCHID=BR.ID ";
		String queryTail = " GROUP BY BNK.NAME,BR.NAME,BR.CODE,PR.STATUS HAVING COUNT(PR.STATUS) > 0  ORDER BY BNK.NAME,BR.NAME,BR.CODE,PR.STATUS ";
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("sourceFile", fileName+".xml");
		parameters.put("reportTitle", "Transactions Statistics Report");
		//parameters.put("fromDate", DateUtil.convertDateToLongString(DateUtil.getBeginningOfDay(this.getFromDate())));
		//parameters.put("toDate", DateUtil.convertDateToLongString(DateUtil.getEndOfDay(this.getToDate())));
		parameters.put("pageHeader", "Transactions Statistics from "+DateUtil.convertToDateWithTime(this.getFromDate())+
				" to "+DateUtil.convertToDateWithTime(this.getToDate()));
		if(msg != null) {
			super.setInformationMessage(msg);
			return "failure";
		}
		
		
			// Check for ToDate
			if(this.getToDate() != null) {
				if(!DateUtil.isDayBefore(DateUtil.getBeginningOfDay(getToDate()), DateUtil.getEndOfDay(new Date()))) {
					super.setErrorMessage("To Date cannot come after today's Date.");
					return "failure";
				}
			}
			
			// toDate must be greater than fromDate
			if(!DateUtil.isDayBefore(DateUtil.getBeginningOfDay(getFromDate()), DateUtil.getEndOfDay(getToDate()))) {
				super.setErrorMessage("From Date must be a day before(or the same with) To Date.");
				return "failure";
			}
			
			String queryWHERE = " WHERE ";
			int initialLength = queryWHERE.length();
			if(attributesMap.get("bank") != null){
				queryWHERE  += " PR.FROMBANKID = '" + attributesMap.get("bank") + "' ";
			}if(attributesMap.get("branch") != null){
				if(queryWHERE.length() > initialLength ){
					queryWHERE += " AND ";
				}
				queryWHERE  += " PR.BRANCHID = '" + attributesMap.get("branch") + "' ";
			}
			if(attributesMap.get("fromDate") != null){
				if(queryWHERE.length() > initialLength ){
					queryWHERE += " AND ";
				}
				queryWHERE  += " PR.DATECREATED >= '"+DateUtil.convertDateToTimestamp(DateUtil.getBeginningOfDay(this.getFromDate()))+"' " +
				" AND PR.DATECREATED <= '"+DateUtil.convertDateToTimestamp(DateUtil.getEndOfDay(this.getToDate()))+"' ";
			}
			
			if(queryWHERE.length() == initialLength ){
				queryWHERE = "";
			}
			
		query = queryHead +  queryWHERE +  queryTail;
		fileId = GenerateReportUtil.getJasperUserId(super.getJaasUserName());
		parameters.put("fileId", fileId);
			
		super.setInformationMessage("Report successfully generated.");
		
		ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
		try {
			ctx.redirect("/EWalletReportsWebICE/jspviewer/viewer.jsp?pdfName="+URLEncryptor.encryptUrl(fileId+fileName+".pdf")+"&query="+URLEncryptor.encryptUrl(query)+"&"+EWalletConstants.MASTER_REPORT+"="+URLEncryptor.encryptUrl(MapUtil.convertAttributesMapToString(parameters))+"&pageName="+URLEncryptor.encryptUrl("bankStatisticsReport.jspx"));
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
						this.getBranchMenu().add(new SelectItem("none", "No Branches"));
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
		attributesMap  = new HashMap<String, Object>();
		StringBuffer buffer = new StringBuffer("The following values are required: ");
		int initialLength = buffer.length();
		
		if(!this.getBankItem().equalsIgnoreCase("all")) {
			attributesMap.put("bank", this.getBankItem());
		} 
		if(!this.getBranchItem().equalsIgnoreCase("none") && !this.getBranchItem().equalsIgnoreCase("all")) {
			attributesMap.put("branch", this.getBranchItem());
		} 
		
	   if(this.getFromDate() == null) {
				buffer.append("From Date, ");
	   }else {
		  	if(this.getToDate() == null){
		  		this.setToDate(new Date());
		  	}
		  	attributesMap.put("fromDate", this.getFromDate());
		  	attributesMap.put("toDate", this.getToDate());
	   } 
		
		
		int finallength = buffer.length();
				
		if(finallength > initialLength) {
			buffer.replace(finallength-2, finallength, " ");
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
		if(this.toDate != null) {
			this.toDate = DateUtil.getEndOfDay(this.toDate);
		}
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
		Date date = new Date();
		if(this.getFromDate() == null) {
			this.setFromDate(DateUtil.getBeginningOfDay(DateUtil.add(date, Calendar.MONTH, -EWalletConstants.REPORT_MONTHS)));
		}
		if(this.getToDate() == null) {
			this.setToDate(DateUtil.getEndOfDay(date));
		}
	}

	
	

}
