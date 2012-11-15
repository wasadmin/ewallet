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

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.customerservices.service.CustomerClass;
import zw.co.esolutions.ewallet.merchantservices.service.Merchant;
import zw.co.esolutions.ewallet.merchantservices.service.MerchantServiceSOAPProxy;
import zw.co.esolutions.ewallet.reports.util.GenerateReportUtil;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.JsfUtil;
import zw.co.esolutions.ewallet.util.MapUtil;
import zw.co.esolutions.ewallet.util.URLEncryptor;

/**
 * @author tauttee
 *
 */
public class MerchantCustomersReportBean extends PageCodeBase{

	private Date fromDate;
	private Date toDate;
	private List<SelectItem> merchantMenu;
	private String merchantItem;
	private List<Merchant> merchantList;
	private boolean latestReport;
	private String customerClassItem;
	private List<SelectItem> customerClass;
	/**
	 * 
	 */
	public MerchantCustomersReportBean() {
		this.initializeDates();
	}
	
	public String generateReport() {
		MerchantServiceSOAPProxy merchantService = new MerchantServiceSOAPProxy();
		
		String fileId = null;
		String fileName = "merchant_customers_report";
		String msg = this.checkAttributes();
		String query = null;
		String merchantQuery = "";
		String customerClassQuery = "";
		String queryExt = null;
		String orderBy = "";
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("sourceFile", fileName+".xml");
		if(msg != null) {
			//report error
			super.setInformationMessage(msg);
			return "failure";
		}
		
		
		if(this.getFromDate() != null && this.getToDate() != null && !this.isLatestReport()) {
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
			
			//Append Dates
			queryExt = "AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMERMERCHANT.dateCreated >= '"+DateUtil.convertDateToTimestamp(DateUtil.getBeginningOfDay(this.getFromDate()))+"' " +
			"AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMERMERCHANT.dateCreated <= '"+DateUtil.convertDateToTimestamp(DateUtil.getEndOfDay(this.getToDate()))+"' ";
		} else if(isLatestReport()) {
			
			//Append Dates
			queryExt = "AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMERMERCHANT.dateCreated >= '"+DateUtil.convertDateToTimestamp(DateUtil.getBeginningOfDay(this.getFromDate()))+"' " +
			"AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMERMERCHANT.dateCreated <= '"+DateUtil.convertDateToTimestamp(DateUtil.getEndOfDay(this.getToDate()))+"' ";
		}
		
		try {
			
			//Checking for To Date to Display
			this.initializeDisplayableDates();
			
			if(this.getMerchantItem().equalsIgnoreCase("all")) {
				for(Merchant m : merchantService.getAllMerchants()) {
				    if(m != null) {
				    	parameters.put(m.getId(), m.getName());
				     }
				    
				}
				
				parameters.put("reportTitle", "Registered Customers for all Merchants");
				
				parameters.put("pageHeader", "All Merchants :Registered Customers from "+DateUtil.convertToDateWithTime(this.getFromDate())+
						" to "+DateUtil.convertToDateWithTime(this.getToDate()));

				
				merchantQuery = "AND ("+EWalletConstants.DATABASE_SCHEMA+".CUSTOMERMERCHANT.customerId = "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.id " +
						"AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMERMERCHANT.bankMerchant_id = "+EWalletConstants.DATABASE_SCHEMA+".BANKMERCHANT.id "+
						"AND "+EWalletConstants.DATABASE_SCHEMA+".BANKMERCHANT.MERCHANTID = "+EWalletConstants.DATABASE_SCHEMA+".MERCHANT.id) ";
						
				orderBy = "ORDER BY "+EWalletConstants.DATABASE_SCHEMA+".MERCHANT.name ASC, "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.lastName ASC, "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.firstNames ASC, "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.status ASC ";
				
			} else {
				String name = merchantService.findMerchantById(this.getMerchantItem()).getName();
				parameters.put(this.getMerchantItem(), name);
                parameters.put("reportTitle", name+" Registered Customers");
				
				parameters.put("pageHeader", name+" :Registered Customers from "+DateUtil.convertToDateWithTime(this.getFromDate())+
						" to "+DateUtil.convertToDateWithTime(this.getToDate()));

				merchantQuery = "AND ("+EWalletConstants.DATABASE_SCHEMA+".CUSTOMERMERCHANT.customerId = "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.id " +
						"AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMERMERCHANT.bankMerchant_id = "+EWalletConstants.DATABASE_SCHEMA+".BANKMERCHANT.id "+
						"AND "+EWalletConstants.DATABASE_SCHEMA+".BANKMERCHANT.MERCHANTID = "+EWalletConstants.DATABASE_SCHEMA+".MERCHANT.id "+
						"AND "+EWalletConstants.DATABASE_SCHEMA+".MERCHANT.id = '"+this.getMerchantItem()+"' "+
						"AND "+EWalletConstants.DATABASE_SCHEMA+".BANKMERCHANT.MERCHANTID = '"+this.getMerchantItem()+"') ";
						
				orderBy = "ORDER BY "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.lastName ASC, "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.firstNames ASC, "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.status ASC ";
				
			}

			
			query =  "SELECT "+EWalletConstants.DATABASE_SCHEMA+".MERCHANT.id AS merchantId, "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMERMERCHANT.dateCreated, " +
					""+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.BRANCHID, "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.LASTNAME, " +
					""+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.FIRSTNAMES, "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.NATIONALID, " +
					""+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.DATEOFBIRTH, "+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE.mobileNumber "+    
					"FROM  "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER, "+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE, "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMERMERCHANT, " +
					EWalletConstants.DATABASE_SCHEMA+".BANKMERCHANT, "+EWalletConstants.DATABASE_SCHEMA+".MERCHANT WHERE " +
					""+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.id = "+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE.customer_id " +
					"AND "+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE.primary = '"+1+"' ";
			
			if(queryExt != null){
				query = query + queryExt;
			}
			if(!("".equals(customerClassItem)|| customerClassItem == null)){
				customerClassQuery = " AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.CUSTOMERCLASS = '"+customerClassItem+"' ";
			}
			
			query = query + customerClassQuery + merchantQuery + orderBy;
			
			
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
			ctx.redirect("/EWalletReportsWebICE/jspviewer/viewer.jsp?pdfName="+URLEncryptor.encryptUrl(fileId+fileName+".pdf")+"&query="+URLEncryptor.encryptUrl(query)+"&"+EWalletConstants.MASTER_REPORT+"="+URLEncryptor.encryptUrl(MapUtil.convertAttributesMapToString(parameters))+"&pageName="+URLEncryptor.encryptUrl("merchantCustomersReport.jspx"));
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
	
	/*
	 * Method for missing fields
	 */
	private String checkAttributes(){
		StringBuffer buffer = new StringBuffer("The following values are required: ");
		int length = 0;
		if(this.getMerchantItem().equalsIgnoreCase("none")) {
			buffer.append("Merchant, ");
		}  if(!this.isLatestReport()) {
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
	
	public List<SelectItem> getMerchantMenu() {
		if(this.merchantMenu == null) {
			merchantMenu = new ArrayList<SelectItem>();
			if(this.getMerchantList() == null || this.getMerchantList().isEmpty()) {
				merchantMenu.add(new SelectItem("nothing", "No Merchants"));
			} else {
				//merchantMenu.add(new SelectItem("none", "<--select-->"));
				merchantMenu.add(new SelectItem("all", "All"));
				for(Merchant m : this.getMerchantList()) {
					merchantMenu.add(new SelectItem(m.getId(),m.getName()+" : "+m.getStatus()));
				}
			}
		}
		return merchantMenu;
	}
	public void setMerchantMenu(List<SelectItem> bankMenu) {
		this.merchantMenu = bankMenu;
	}
	public String getMerchantItem() {
		return merchantItem;
	}
	public void setMerchantItem(String bankItem) {
		this.merchantItem = bankItem;
	}
	public void setLatestReport(boolean latestReport) {
		this.latestReport = latestReport;
	}

	public boolean isLatestReport() {
		return latestReport;
	}

	public List<Merchant> getMerchantList() {
		
		if(this.merchantList == null || this.merchantList.isEmpty()) {
			try {
				MerchantServiceSOAPProxy merchantService = new MerchantServiceSOAPProxy();
				
				merchantList = merchantService.getAllMerchants();
				
				if(merchantList == null || merchantList.isEmpty())  {
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return merchantList;
	}

	public void setMerchantList(List<Merchant> merchantList) {
		this.merchantList = merchantList;
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
