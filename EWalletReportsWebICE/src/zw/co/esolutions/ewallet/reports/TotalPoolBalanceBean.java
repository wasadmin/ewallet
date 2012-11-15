package zw.co.esolutions.ewallet.reports;

import java.io.IOException;
import java.util.ArrayList;
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
import zw.co.esolutions.ewallet.reports.util.GenerateReportUtil;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.MapUtil;
import zw.co.esolutions.ewallet.util.URLEncryptor;

public class TotalPoolBalanceBean extends PageCodeBase{
	private BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
	private List<SelectItem> bankList;
	private String selectedBank;
	
	public List<SelectItem> getBankList() {
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
	
	public String getSelectedBank() {
		return selectedBank;
	}
	public void setSelectedBank(String selectedBank) {
		this.selectedBank = selectedBank;
	}
	public String submit() {
		String sourceFile = "total_pool_balance";
		String fileId;
		String reportQuery = "SELECT * FROM "+EWalletConstants.DATABASE_SCHEMA+".BANKACCOUNT";
		
		Map<String, String> parameters = new HashMap<String, String>();
		try {
			
			@SuppressWarnings("unused")
			Long balance = new Long(0);
			
			if ("all".equals(selectedBank)) {
				
				reportQuery = "SELECT * FROM "+EWalletConstants.DATABASE_SCHEMA+".BANKACCOUNT WHERE TYPE = 'POOL_CONTROL'";
			} else {
				reportQuery = "SELECT * FROM "+EWalletConstants.DATABASE_SCHEMA+".BANKACCOUNT WHERE ACCOUNTHOLDERID = '" + selectedBank + "' AND TYPE = 'POOL_CONTROL'";
			}
			
					
			String reportTitle = "E-Wallet Pool Funds Report";
			
			String pageHeader = "Total Balance In all e-Wallets as at " + DateUtil.convertToDateWithTime(new Date());
					
			parameters.put("sourceFile", sourceFile);
			parameters.put("reportTitle", reportTitle);
			parameters.put("pageHeader", pageHeader);
			parameters.put("balBFname", "TOTAL");
			parameters.put("balBFtype", "BALANCE");
			parameters.put("balBFnarrative", "Balance b/f");
			parameters.put("toDate", DateUtil.convertToDateWithTime(new Date()));
		
			fileId  = GenerateReportUtil.getJasperUserId(super.getJaasUserName());
			parameters.put("fileId", fileId);
			
		} catch (Exception e) {
			e.printStackTrace();
			return "failure";
		}
		ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
		try {
			ctx.redirect("/EWalletReportsWebICE/jspviewer/viewer.jsp?pdfName="+URLEncryptor.encryptUrl(fileId+sourceFile+".pdf")+"&query="+URLEncryptor.encryptUrl(reportQuery)+"&"+EWalletConstants.MASTER_REPORT+"="+URLEncryptor.encryptUrl(MapUtil.convertAttributesMapToString(parameters))+"&pageName="+URLEncryptor.encryptUrl("totalPoolBalanceReport.jspx"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "submit";
	}
	
}
