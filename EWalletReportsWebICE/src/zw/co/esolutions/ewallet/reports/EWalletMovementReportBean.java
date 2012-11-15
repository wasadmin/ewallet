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

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.enums.BankAccountType;
import zw.co.esolutions.ewallet.reports.util.GenerateReportUtil;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.MapUtil;
import zw.co.esolutions.ewallet.util.URLEncryptor;

public class EWalletMovementReportBean extends PageCodeBase {

	private Date fromDate = new Date();
	private Date toDate = new Date();
	public EWalletMovementReportBean() {
		super();
		this.initializeDates();
	}

	private List<SelectItem> bankMenu;
	private String selectedBank;
	
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
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		List<Bank> banks = bankService.getBank();
		bankMenu = new ArrayList<SelectItem>();
		if(banks != null){
			this.bankMenu.add(new SelectItem("all", "ALL"));
			for(Bank b: banks){
				bankMenu.add(new SelectItem(b.getId(),b.getName()));
			}
		}
		return bankMenu;
	}
	public void setBankMenu(List<SelectItem> bankMenu) {
		this.bankMenu = bankMenu;
	}
	public String getSelectedBank() {
		return selectedBank;
	}
	public void setSelectedBank(String selectedBank) {
		this.selectedBank = selectedBank;
	}
	
	public String generateReport(){
		String msg = this.validateFields();
		String fileId = null;
		String query = "SELECT c.id AS customerId, b.name AS bnkName, c.CUSTOMERCLASS, ba.id AS accountId, t.narrative, t.amount, ab.amount AS balance, ab.id AS balanceId FROM "+
				"(select * from "+EWalletConstants.DATABASE_SCHEMA+".BANKACCOUNT where type = '"+BankAccountType.E_WALLET+"' OR type = '"+BankAccountType.AGENT_EWALLET+"') as ba "+
				"LEFT OUTER JOIN (select * from "+EWalletConstants.DATABASE_SCHEMA+".TRANSACTION where dateCreated >= '"+DateUtil.convertDateToTimestamp(this.getFromDate())+"' AND dateCreated <= '"+DateUtil.convertDateToTimestamp(this.getToDate())+"') AS t "+ 
				"ON ba.id = t.ACCOUNTID, "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER AS c, "+EWalletConstants.DATABASE_SCHEMA+".BANK as b, "+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH as bc, (select * from "+EWalletConstants.DATABASE_SCHEMA+".BANKACCOUNT where type = '"+BankAccountType.E_WALLET+"' OR type = '"+BankAccountType.AGENT_EWALLET+"') as acc LEFT OUTER JOIN "+
				"(select * from "+EWalletConstants.DATABASE_SCHEMA+".ACCOUNTBALANCE where id IN (SELECT aba.id From "+EWalletConstants.DATABASE_SCHEMA+".ACCOUNTBALANCE as aba, (SELECT a.accountId, MAX(a.dateCreated) AS latestDate FROM "+EWalletConstants.DATABASE_SCHEMA+".ACCOUNTBALANCE as a WHERE a.dateCreated < '"+DateUtil.convertDateToTimestamp(this.getFromDate())+"' GROUP BY a.accountId) AS ac WHERE aba.accountId = ac.accountId AND aba.dateCreated = ac.latestDate) ) AS ab "+
				"ON acc.id = ab.ACCOUNTID  "+
				"WHERE ";
		String queryExt = "c.id = ba.accountHolderId AND c.branchId = bc.id AND bc.bank_id = b.id AND acc.id = ba.id ";
		String orderBy = "ORDER by b.name ASC, c.CUSTOMERCLASS ASC ";
		//queryExt = queryExt+"AND (ba.id = '47GS131378443436316' OR ba.id = 'OOE2131038646994921') ";
		if(msg!=null){
			super.setErrorMessage(msg);
			return "failure";
		}
		
		Map<String, String> parameters = new HashMap<String, String>();
		Map<String, String> subReportParameters = new HashMap<String, String>();
		
		String fileName = "ewallet_movement_report";
		parameters.put("sourceFile", fileName+".xml");
		
		String fileNameConn = "ewallet_movement_sub_report";
		subReportParameters.put("sourceFile", fileNameConn+".xml");
		
		try {
			parameters.put("fromDate", DateUtil.convertDateToLongString(this.getFromDate()));
			parameters.put("toDate", DateUtil.convertDateToLongString(this.getToDate()));
			parameters.put("schema", EWalletConstants.DATABASE_SCHEMA);
			
			subReportParameters.put("fromDate", DateUtil.convertDateToLongString(this.getFromDate()));
			subReportParameters.put("toDate", DateUtil.convertDateToLongString(this.getToDate()));
			subReportParameters.put("schema", EWalletConstants.DATABASE_SCHEMA);
			
			//parameters.put(EWalletConstants.SUBREPORT, EWalletConstants.SUBREPORT);
			
			if("all".equalsIgnoreCase(this.getSelectedBank())) {
				parameters.put("reportTitle", "E-Wallet Movement Report for all Banks");
		        parameters.put("pageHeader", "All Banks : E-Wallet Movement Report from "+DateUtil.convertToDateWithTime(this.getFromDate())+
				" to "+DateUtil.convertToDateWithTime(this.getToDate()));
		        orderBy = "ORDER BY b.name ASC, c.customerClass ASC, ba.id ASC ";
			} else {
				Bank bk = new BankServiceSOAPProxy().findBankById(this.getSelectedBank());
				parameters.put("reportTitle", bk.getName()+" " +
				"E-Wallet Movement Report");
				parameters.put("pageHeader", "All "+bk.getName()+
				" Branches E-Wallet Movement Report from "+DateUtil.convertToDateWithTime(this.getFromDate())+
				" to "+DateUtil.convertToDateWithTime(this.getToDate()));
				
				queryExt = queryExt + "AND b.id = '"+this.getSelectedBank()+"' ";
				
				orderBy = "ORDER BY c.customerClass ASC, ba.id ASC ";
			}
			fileId = GenerateReportUtil.getJasperUserId(super.getJaasUserName());
			parameters.put("fileId", fileId);
			
			if(fileId == null) {
				super.setInformationMessage("No results to display.");
				return "failure";
			}
			
			query = query + queryExt + orderBy;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
		try {
			//ctx.redirect("/EWalletReportsWebICE/jspviewer/viewer.jsp?pdfName="+URLEncryptor.encryptUrl(fileId+fileName+".pdf")+"&query="+URLEncryptor.encryptUrl(query)+"&"+EWalletConstants.MASTER_REPORT+"="+URLEncryptor.encryptUrl(MapUtil.convertAttributesMapToString(parameters))+""+"&"+EWalletConstants.SUBREPORT+"="+URLEncryptor.encryptUrl(MapUtil.convertAttributesMapToString(subReportParameters))+"&pageName="+URLEncryptor.encryptUrl("ewalletMovementReport.jspx"));
			ctx.redirect("/EWalletReportsWebICE/jspviewer/viewer.jsp?pdfName="+URLEncryptor.encryptUrl(fileId+fileName+".pdf")+"&query="+URLEncryptor.encryptUrl(query)+"&"+EWalletConstants.MASTER_REPORT+"="+URLEncryptor.encryptUrl(MapUtil.convertAttributesMapToString(parameters))+"&pageName="+URLEncryptor.encryptUrl("ewalletMovementReport.jspx"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "success";
	}
	
	public String validateFields(){
		if(selectedBank==null || "".equals(selectedBank)){
			return "Please select a financial institution";
		}else if(fromDate==null){
			return "Please enter a from date";
		}else if(toDate==null){
			return "Please enter a to date";
		}else{
			Calendar c = Calendar.getInstance();
			c.setTime(this.getToDate());
			Date current = new Date();
			System.out.println(" From date = "+this.getFromDate()+" , To date = "+this.getToDate());
			if(c.after(current)){
				return "To Date cannot be after than the current date";
			}else if(c.before(this.getFromDate())){
				return "From Date cannot be after the To Date";
				
			}else{
				return null;
			}
		}
		
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
}
