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
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.reports.util.GenerateReportUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.MapUtil;
import zw.co.esolutions.ewallet.util.URLEncryptor;

public class TopXCustomersReportBean extends PageCodeBase {
	private BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
	private List<SelectItem> categoryList;
	private String selectedCategory;
	private int maxNumber;
	
	public List<SelectItem> getCategoryList() {
		if (categoryList == null) {
			categoryList = new ArrayList<SelectItem>();
			categoryList.add(new SelectItem("Tariff", "Tariff"));
			categoryList.add(new SelectItem("Transaction Volume", "Transaction Volume"));
		}
		return categoryList;
	}

	public void setCategoryList(List<SelectItem> categoryList) {
		this.categoryList = categoryList;
	}

	public String getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(String selectedCategory) {
		this.selectedCategory = selectedCategory;
	}

	public int getMaxNumber() {
		return maxNumber;
	}

	public void setMaxNumber(int maxNumber) {
		this.maxNumber = maxNumber;
	}

	public String submit() {
		
		String reportQuery = null;
		
		int index = 1;
		
		if ("Tariff".equals(selectedCategory)) {
			reportQuery = "SELECT CUSTOMERNAME, SOURCEMOBILE, BRANCHID, SUM(TARIFFAMOUNT) AS \"TOTALTARIFF\" FROM "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION GROUP BY CUSTOMERNAME, SOURCEMOBILE, BRANCHID ORDER BY \"TOTALTARIFF\" DESC FETCH FIRST " + maxNumber + " ROWS ONLY";
			index = 100;
		} else if ("Commission".equals(selectedCategory)) {
			
		} else if ("Transaction Volume".equals(selectedCategory)) {
			reportQuery = "SELECT CUSTOMERNAME, SOURCEMOBILE, BRANCHID, COUNT(*) AS \"TOTALTARIFF\" FROM "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION GROUP BY CUSTOMERNAME, SOURCEMOBILE, BRANCHID ORDER BY \"TOTALTARIFF\" DESC FETCH FIRST " + maxNumber + " ROWS ONLY";
		}
		
		Map<String, String> parameters = new HashMap<String, String>();
		
		List<BankBranch> branches = bankService.getBankBranch();
		
		if (branches != null) {
			for (BankBranch branch: branches) {
				parameters.put(branch.getId(), branch.getBank().getName() + ": " + branch.getName());
				
			}
		} else {
			
		}
		
		String sourceFile = "top_x_customers";
		
		String reportTitle = "E-Wallet Top Customers Report";
		
		String pageHeader = "Top " + maxNumber + " Customers By " + selectedCategory;
				
		parameters.put("sourceFile", sourceFile);
		parameters.put("reportTitle", reportTitle);
		parameters.put("pageHeader", pageHeader);
		parameters.put("index", Integer.toString(index));
				
		String fileId = null;
		try {
			
			fileId  = GenerateReportUtil.getJasperUserId(super.getJaasUserName());
			parameters.put("fileId", fileId);
	
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
		try {
			ctx.redirect("/EWalletReportsWebICE/jspviewer/viewer.jsp?pdfName="+URLEncryptor.encryptUrl(fileId+sourceFile+".pdf")+"&query="+URLEncryptor.encryptUrl(reportQuery)+"&"+EWalletConstants.MASTER_REPORT+"="+URLEncryptor.encryptUrl(MapUtil.convertAttributesMapToString(parameters))+"&pageName="+URLEncryptor.encryptUrl("topXCustomersReport.jspx"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.setInformationMessage("Report generated successfully.");
		return "submit";
	}
}
