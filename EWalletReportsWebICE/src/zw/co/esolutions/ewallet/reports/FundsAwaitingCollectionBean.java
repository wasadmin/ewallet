package zw.co.esolutions.ewallet.reports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.reports.util.GenerateReportUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.MapUtil;
import zw.co.esolutions.ewallet.util.URLEncryptor;

public class FundsAwaitingCollectionBean extends PageCodeBase {
	private List<SelectItem> bankList;
	private List<SelectItem> branchList;
	private String selectedBank;
	private String selectedBranch;
	
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
	
public String submit() {
	String fileId = null;
	String fileName = "funds_awaiting_collection";
	BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		String reportQuery = "SELECT * FROM "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION WHERE STATUS = 'AWAITING_COLLECTION' ORDER BY DATECREATED"; 
		
		List<String> reportQueries = new ArrayList<String>();
	
		if ("all".equals(selectedBank)) {
			reportQuery = "SELECT * FROM "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION WHERE STATUS = 'AWAITING_COLLECTION' ORDER BY DATECREATED";
		} else {
			if (!"all".equals(selectedBranch)) {
				reportQuery = "SELECT * FROM "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION WHERE STATUS = 'AWAITING_COLLECTION'" + " AND BRANCHID = '" + selectedBranch + "' ORDER BY DATECREATED";
			} else {
				List<BankBranch> branches = bankService.getBankBranchByBank(selectedBank);
				if (branches != null) {
					for (BankBranch branch: branches) {
						reportQuery = "SELECT * FROM "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION WHERE STATUS = 'AWAITING_COLLECTION'" + " AND BRANCHID = '" + branch.getId() + "' ORDER BY DATECREATED";
						reportQueries.add(reportQuery);
					}
				}
			}
		}
		
		Map<String, String> parameters = new HashMap<String, String>();
		
		List<BankBranch> branches = bankService.getBankBranch();
		
		if (branches != null) {
			for (BankBranch branch: branches) {
				parameters.put(branch.getId(), branch.getBank().getName() + ": " + branch.getName());
				
			}
		} else {
			
		}
		
		String reportTitle = "E-Wallet Non-Holder Transfers";
		
		String pageHeader = "List Of Transfers Awaiting Collection";
				
		parameters.put("sourceFile", fileName+".xml");
		parameters.put("reportTitle", reportTitle);
		parameters.put("pageHeader", pageHeader);
				
		try {
			fileId = GenerateReportUtil.getJasperUserId(super.getJaasUserName());
			parameters.put("fileId", fileId);
			
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		
		super.setInformationMessage("Report generated successfully.");
		ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
		try {
			if (reportQueries.isEmpty()) {
				
				ctx.redirect("/EWalletReportsWebICE/jspviewer/viewer.jsp?pdfName="+URLEncryptor.encryptUrl(fileId+fileName+".pdf")+"&query="+URLEncryptor.encryptUrl(reportQuery)+"&"+EWalletConstants.MASTER_REPORT+"="+URLEncryptor.encryptUrl(MapUtil.convertAttributesMapToString(parameters))+"&pageName="+URLEncryptor.encryptUrl("fundsAwaitingCollectionReport.jspx"));
			} else {
				for (String query: reportQueries) {
					
					ctx.redirect("/EWalletReportsWebICE/jspviewer/viewer.jsp?pdfName="+URLEncryptor.encryptUrl(fileId+fileName+".pdf")+"&query="+URLEncryptor.encryptUrl(query)+"&"+EWalletConstants.MASTER_REPORT+"="+URLEncryptor.encryptUrl(MapUtil.convertAttributesMapToString(parameters))+"&pageName="+URLEncryptor.encryptUrl("fundsAwaitingCollectionReport.jspx"));
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "success";
		
	}
	
}
