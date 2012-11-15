package zw.co.esolutions.ewallet.reports;

import java.io.IOException;
import java.sql.Timestamp;
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
import zw.co.esolutions.ewallet.customerservices.service.CustomerClass;
import zw.co.esolutions.ewallet.reports.util.GenerateReportUtil;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.JsfUtil;
import zw.co.esolutions.ewallet.util.MapUtil;
import zw.co.esolutions.ewallet.util.URLEncryptor;

public class SubscriberListReportBean extends PageCodeBase {
	private List<SelectItem> bankList;
	private List<SelectItem> branchList;
	private String selectedBank;
	private String selectedBranch;
	private Date fromDate;
	private Date toDate;
	private boolean latestReport;
	private String customerClassItem;
	private List<SelectItem> customerClass;
	
	public SubscriberListReportBean() {
		super();
		this.initializeDates();
	}
	
	public List<SelectItem> getBankList() {
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		if (bankList == null) {
			bankList = new ArrayList<SelectItem>();
			bankList.add(new SelectItem("all", "ALL BANKS"));
			List<Bank> banks = bankService.getBank();
			if (banks != null) {
				for (Bank bank: banks) {
					bankList.add(new SelectItem(bank.getId(), bank.getName()));
				}
			}
		}
		return bankList;
	}
	public void setBankList(List<SelectItem> bankList) {
		this.bankList = bankList;
	}
	public List<SelectItem> getBranchList() {
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		if (branchList == null) {
			branchList = new ArrayList<SelectItem>();
			branchList.add(new SelectItem("all", "ALL BRANCHES"));
			List<BankBranch> branches = bankService.getBankBranch();
			if (branches != null) {
				for (BankBranch branch: branches) {
					branchList.add(new SelectItem(branch.getId(), branch.getName()));
				}
			}
		}
		return branchList;
	}
	public void setBranchList(List<SelectItem> branchList) {
		this.branchList = branchList;
	}
	public String getSelectedBank() {
		return selectedBank;
	}
	public void setSelectedBank(String selectedBank) {
		this.selectedBank = selectedBank;
	}
	public String getSelectedBranch() {
		return selectedBranch;
	}
	public void setSelectedBranch(String selectedBranch) {
		this.selectedBranch = selectedBranch;
	}
	public Date getFromDate() {
		if (fromDate == null) {
			fromDate = new Date();
		}
		this.fromDate = DateUtil.getBeginningOfDay(this.fromDate);
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		if (toDate == null) {
			toDate = new Date();
		}
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	
	/**
	 * @param latestReport the latestReport to set
	 */
	public void setLatestReport(boolean latestReport) {
		this.latestReport = latestReport;
	}

	/**
	 * @return the latestReport
	 */
	public boolean isLatestReport() {
		return latestReport;
	}

	public void processBankValueChange(ValueChangeEvent event) {
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		String value = (String) event.getNewValue();
		if (value != null && !value.equals("all")) {
			try {
				branchList = new ArrayList<SelectItem>();
				List<BankBranch> branches = bankService.getBankBranchByBank(value);
				if (branches != null) {
					for (BankBranch branch: branches) {
						branchList.add(new SelectItem(branch.getId(), branch.getName()));
					}
				}
			} catch(Exception e) {
				
			}
		}
	}
	
	public String submit() {
		String orderBy = " AND ("+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE.customer_id = "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.id " +
		"AND "+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE.primary = '"+1+"') "+
		"ORDER BY "+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE.bankId ASC, "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.branchId ASC";
		String attributes = EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE.bankId, "+"LASTNAME, FIRSTNAMES, DATEOFBIRTH, NATIONALID, BRANCHID, "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.dateCreated";
		String entities = ""+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE, ";
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		String reportQuery = "SELECT "+attributes+" FROM "+entities+""+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER"; 
		String customerClassQuery = "";
		
		List<String> reportQueries = new ArrayList<String>();
		Map<String, String> parameters = new HashMap<String, String>();
		
		Timestamp fromDateX = null;
		Timestamp toDateX = null;
		
		if (fromDate == null || toDate == null) {
			super.setErrorMessage("From Date and To Date cannot be null");
			return "failure";
		} else {
			
			// Check for ToDate
			if(this.getToDate() != null) {
				if(!DateUtil.isDayBefore(DateUtil.getBeginningOfDay(getToDate()), DateUtil.getEndOfDay(new Date()))) {
					super.setErrorMessage("To Date cannot come after this Today's Date.");
					return "failure";
				}
			}
			
			//Checking for To Date to Display
			this.initializeDisplayableDates();
			
			fromDateX = new Timestamp(fromDate.getTime());
			toDateX = new Timestamp(toDate.getTime());
			reportQuery = "SELECT "+attributes+" FROM "+entities+""+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER WHERE "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.DATECREATED >= '" + fromDateX + "' AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.DATECREATED <= '" + toDateX + "'";
		}
		
		if(!("".equals(customerClassItem)|| customerClassItem == null)){
			customerClassQuery = " AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.CUSTOMERCLASS = '"+customerClassItem+"' ";
		}
		if ("all".equals(selectedBank)) {
			for(Bank bk : new BankServiceSOAPProxy().getBank()) {
				parameters.put(bk.getId(), bk.getName());
			}
			reportQuery = "SELECT "+attributes+" FROM "+entities+""+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER WHERE "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.DATECREATED >= '" + fromDateX + "' AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.DATECREATED <= '" + toDateX + "' "+customerClassQuery;
		} else {
			parameters.put(selectedBank, new BankServiceSOAPProxy().findBankById(selectedBank).getName());
			if (!"all".equals(selectedBranch)) {
				reportQuery = "SELECT "+attributes+" FROM "+entities+""+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER WHERE "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.DATECREATED >= '" + fromDateX + "' AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.DATECREATED <= '" + toDateX + "' AND BRANCHID = '" + selectedBranch+"' "+customerClassQuery;
			} else {
				/*List<BankBranch> branches = bankService.getBankBranchByBank(selectedBank);
				if (branches != null) {
					for (BankBranch branch: branches) {
						reportQuery = "SELECT "+attributes+" FROM "+entities+""+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER WHERE "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.DATECREATED >= '" + fromDateX + "' AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.DATECREATED <= '" + toDateX + "' AND BRANCHID = '" + branch.getId()+"' ";
						reportQueries.add(reportQuery);
					}
				}*/
				
				reportQuery = "SELECT "+attributes+" FROM "+entities+""+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER WHERE "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.DATECREATED >= '" + fromDateX + "' AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.DATECREATED <= '" + toDateX + "' AND " +EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE.bankId = '" + this.selectedBank+"' "+customerClassQuery;
				reportQueries.add(reportQuery);
			}
		}
		
		
		List<BankBranch> branches = bankService.getBankBranch();
		
		if (branches != null) {
			for (BankBranch branch: branches) {
				parameters.put(branch.getId(), branch.getBank().getName() + ": " + branch.getName());
				
			}
		} else {
			
		}
		
		@SuppressWarnings("unused")
		Timestamp date = new Timestamp(System.currentTimeMillis());
		
		String fileName = "customers_list";
		
		String reportTitle = "E-Wallet Customers List";
		
		String pageHeader = "List Of New Customers from "+DateUtil.convertToDateWithTime(fromDate)+" to "+DateUtil.convertToDateWithTime(toDate);
		
		String fileId = null;
				
		parameters.put("sourceFile", fileName+".xml");
		parameters.put("reportTitle", reportTitle);
		parameters.put("pageHeader", pageHeader);
		parameters.put("fromDate", DateUtil.convertDateToLongString(fromDate));
		parameters.put("toDate", DateUtil.convertDateToLongString(toDate));
		
		@SuppressWarnings("unused")
		String orderByParameter = "BRANCHID";
		
		try {fileId = GenerateReportUtil.getJasperUserId(super.getJaasUserName());
						parameters.put("fileId", fileId);
			
			
			if(fileId == null) {
				super.setInformationMessage("No results to display.");
				return "failure";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		
		super.setInformationMessage("Report generated successfully.");
		ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
		try {
			if (reportQueries.isEmpty()) {
				reportQuery = reportQuery + orderBy;
				
				ctx.redirect("/EWalletReportsWebICE/jspviewer/viewer.jsp?pdfName="+URLEncryptor.encryptUrl(fileId+fileName+".pdf")+"&query="+URLEncryptor.encryptUrl(reportQuery)+"&"+EWalletConstants.MASTER_REPORT+"="+URLEncryptor.encryptUrl(MapUtil.convertAttributesMapToString(parameters))+"&pageName="+URLEncryptor.encryptUrl("customersReport.jspx"));
			} else {
				for (String query: reportQueries) {
					query = query + orderBy;
					
					ctx.redirect("/EWalletReportsWebICE/jspviewer/viewer.jsp?pdfName="+URLEncryptor.encryptUrl(fileId+fileName+".pdf")+"&query="+URLEncryptor.encryptUrl(query)+"&"+EWalletConstants.MASTER_REPORT+"="+URLEncryptor.encryptUrl(MapUtil.convertAttributesMapToString(parameters))+"&pageName="+URLEncryptor.encryptUrl("customersReport.jspx"));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "success";
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
	
	public String getCustomerClassItem() {
		return customerClassItem;
	}

	public void setCustomerClassItem(String customerClassItem) {
		this.customerClassItem = customerClassItem;
	}

	public List<SelectItem> getCustomerClass() {
		customerClass = JsfUtil.getSelectItemsAsList(CustomerClass.values(), true);
		return customerClass;
	}

	public void setCustomerClass(List<SelectItem> customerClass) {
		this.customerClass = customerClass;
	}
}
