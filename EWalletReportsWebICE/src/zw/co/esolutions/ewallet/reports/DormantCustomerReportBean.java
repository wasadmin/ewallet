/**
 * 
 */
package zw.co.esolutions.ewallet.reports;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
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
import javax.servlet.ServletContext;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.agentservice.service.AgentLevel;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.CustomerClass;
import zw.co.esolutions.ewallet.enums.CustomerStatus;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.process.TransactionStatus;
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
public class DormantCustomerReportBean extends PageCodeBase{

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
	private String customerClassItem;
	private List<SelectItem> customerClass;
	/**
	 * 
	 */
	public DormantCustomerReportBean() {
		this.initializeDates();
	}
	
	public String generateReport() {
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
		String fileId = null;
		String fileName = "dormant_customer_report";
		String msg = this.checkAttributes();
		String subQuery = null;
		String query = null;
		TransactionStatus status = TransactionStatus.COMPLETED;
		CustomerStatus customerStatus = CustomerStatus.ACTIVE;
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("sourceFile", fileName+".xml");
		if(msg != null) {
			//report error
			super.setErrorMessage(msg);
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
		
		
		try {
			//Checking for To Date to Display
			this.initializeDisplayableDates();
			
			if(this.getBankItem().equalsIgnoreCase("all")) {
				for(Bank bnk : bankService.getBank()) {
				    if(bnk != null) {
				    	parameters.put(bnk.getId(), bnk.getName());
				    	for(BankBranch bh : bankService.getBankBranchByBank(bnk.getId())) {
				    		if(bh != null) {
				    			parameters.put(bh.getId(), bh.getName());
				    		}
				    	}
				    }
				    
				}
				
				parameters.put("reportTitle", "Dormant Customers for all Banks");
		        parameters.put("pageHeader", "All Banks : Branch Dormant Customers from "+DateUtil.convertToDateWithTime(this.getFromDate())+
				" to "+DateUtil.convertToDateWithTime(this.getToDate()));
		        
		        subQuery = "SELECT DISTINCT sourceMobileId FROM "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION, " +
							""+EWalletConstants.DATABASE_SCHEMA+".BANK WHERE " +
							""+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.status = '"+status+"' AND "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.dateCreated >= '"+DateUtil.convertDateToTimestamp(DateUtil.getBeginningOfDay(this.getFromDate()))+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.dateCreated <= '"+DateUtil.convertDateToTimestamp(DateUtil.getEndOfDay(this.getToDate()))+"' " +
							"AND ("+EWalletConstants.DATABASE_SCHEMA+".BANK.id = fromBankId)  AND sourceMobileId IS NOT NULL "; 
				if(!this.getTxnItem().equalsIgnoreCase("all")) {
					subQuery = subQuery+" AND transactionType = '"+this.getTxnItem()+"' ";
				}
				//Retrieving Actual Customer
				if("".equals(customerClassItem)|| customerClassItem == null){
					
					query = "SELECT lastName, firstNames, mobileNumber,  branchId, bankId, "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.status FROM "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER, " +
						""+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE WHERE "+
						EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE.primary = '"+1+"' " +
						"AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.status = '"+customerStatus+"' " +
						"AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.dateCreated <= '"+DateUtil.convertDateToTimestamp(DateUtil.getBeginningOfDay(getFromDate()))+"' " +
						"AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.id =  "+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE.customer_id "+
						"AND "+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE.id NOT IN ("+subQuery+") ORDER BY lastName";
					
				}else{
					query = "SELECT lastName, firstNames, mobileNumber,  branchId, bankId, "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.status FROM "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER, " +
						""+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE WHERE "+
						EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE.primary = '"+1+"' " +
						"AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.status = '"+customerStatus+"' " +
						"AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.customerClass = '"+customerClassItem+"' " +
						"AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.dateCreated <= '"+DateUtil.convertDateToTimestamp(DateUtil.getBeginningOfDay(getFromDate()))+"' " +
						"AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.id =  "+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE.customer_id "+
						"AND "+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE.id NOT IN ("+subQuery+") ORDER BY lastName";
				}
				
				
			} else {
				parameters.put(this.getBankItem(), bankService.findBankById(this.getBankItem()).getName());
				if(this.getBranchItem().equalsIgnoreCase("all")) {
					this.setBankBranchList(bankService.getBankBranchByBank(this.getBankItem()));
					if(!(this.getBankBranchList() == null || this.getBankBranchList().isEmpty())) {
						for(BankBranch bb : this.getBankBranchList()) {
							parameters.put(bb.getId(), bb.getName());							
						}
						parameters.put("reportTitle", parameters.get(this.getBankItem()).toString()+" " +
								"Dormant Customers");
						parameters.put("pageHeader", "All "+parameters.get(this.getBankItem()).toString()+
								" Branches Dormant Customers from "+DateUtil.convertToDateWithTime(this.getFromDate())+
								" to "+DateUtil.convertToDateWithTime(this.getToDate()));
					}
					
					subQuery = "SELECT DISTINCT sourceMobileId FROM "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION WHERE " +
							"fromBankId = '"+this.getBankItem()+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.status = '"+status+"' AND dateCreated >= '"+DateUtil.convertDateToTimestamp(DateUtil.getBeginningOfDay(this.getFromDate()))+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.dateCreated <= '"+DateUtil.convertDateToTimestamp(DateUtil.getEndOfDay(this.getToDate()))+"' " +
									" AND sourceMobileId IS NOT NULL ";
					if(!this.getTxnItem().equalsIgnoreCase("all")) {
						subQuery = subQuery+" AND transactionType = '"+this.getTxnItem()+"' ";
					}
					//Retrieving Actual Customer
					if("".equals(customerClassItem)|| customerClassItem == null){
							query = "SELECT lastName, firstNames, mobileNumber,  branchId, bankId, "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.status FROM "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER, " +
							""+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE WHERE "+
							""+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE.primary = '"+1+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE.bankId = '"+this.getBankItem()+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.status = '"+customerStatus+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.dateCreated <= '"+DateUtil.convertDateToTimestamp(DateUtil.getBeginningOfDay(getFromDate()))+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.id =  "+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE.customer_id "+
							"AND "+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE.id NOT IN ("+subQuery+") ORDER BY lastName";
					}else{
						
						query = "SELECT lastName, firstNames, mobileNumber,  branchId, bankId, "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.status FROM "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER, " +
							""+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE WHERE "+
							""+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE.primary = '"+1+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE.bankId = '"+this.getBankItem()+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.status = '"+customerStatus+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.customerClass = '"+customerClassItem+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.dateCreated <= '"+DateUtil.convertDateToTimestamp(DateUtil.getBeginningOfDay(getFromDate()))+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.id =  "+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE.customer_id "+
							"AND "+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE.id NOT IN ("+subQuery+") ORDER BY lastName";
						
					}
					
				} else {
					BankBranch bb = bankService.findBankBranchById(this.getBranchItem());
					parameters.put(bb.getId(), bb.getName());
					parameters.put("reportTitle", parameters.get(this.getBankItem()).toString()+" " +
					"Dormant Customers");
			        parameters.put("pageHeader",parameters.get(this.getBankItem()).toString()+
					" : "+bankService.findBankBranchById(this.getBranchItem()).getName()+" Branch Dormant Customers from "+DateUtil.convertToDateWithTime(this.getFromDate())+
					" to "+DateUtil.convertToDateWithTime(this.getToDate()));
			        
			        subQuery = "SELECT DISTINCT sourceMobileId FROM "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION, " +
							""+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER, "+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE WHERE " +
					        ""+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.branchId = '"+this.getBranchItem()+"' " +
					        "AND "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.status = '"+status+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.dateCreated >= '"+DateUtil.convertDateToTimestamp(DateUtil.getBeginningOfDay(this.getFromDate()))+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".PROCESSTRANSACTION.dateCreated <= '"+DateUtil.convertDateToTimestamp(DateUtil.getEndOfDay(this.getToDate()))+"' " +
							"AND ("+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE.mobileNumber = sourceMobile AND "+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE.customer_id = "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.id) " +
									" AND sourceMobileId IS NOT NULL ";
					if(!this.getTxnItem().equalsIgnoreCase("all")) {
						subQuery = subQuery+" AND transactionType = '"+this.getTxnItem()+"' ";
					}
					//Retrieving Actual Customer
					if("".equals(customerClassItem)|| customerClassItem == null){
						query = "SELECT lastName, firstNames, mobileNumber,  branchId, bankId, "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.status FROM "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER, " +
							""+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE WHERE "+
							""+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE.primary = '"+1+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.status = '"+customerStatus+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.dateCreated <= '"+DateUtil.convertDateToTimestamp(DateUtil.getBeginningOfDay(getFromDate()))+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.branchId = '"+this.getBranchItem()+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.id =  "+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE.customer_id "+
							"AND "+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE.id NOT IN ("+subQuery+") ORDER BY lastName";
						
					}else{
						
						query = "SELECT lastName, firstNames, mobileNumber,  branchId, bankId, "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.status FROM "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER, " +
							""+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE WHERE "+
							""+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE.primary = '"+1+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.status = '"+customerStatus+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.customerClass = '"+customerClassItem+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.dateCreated <= '"+DateUtil.convertDateToTimestamp(DateUtil.getBeginningOfDay(getFromDate()))+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.branchId = '"+this.getBranchItem()+"' " +
							"AND "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER.id =  "+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE.customer_id "+
							"AND "+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE.id NOT IN ("+subQuery+") ORDER BY lastName";
						
					}
					
				}
			}
			fileId = GenerateReportUtil.getJasperUserId(super.getJaasUserName());
			parameters.put("fileId", fileId);
			
			if(fileId == null) {
				super.setInformationMessage("No results to display.");
				return "failure";
			}
			
			//Checking size of customer entries
			String countQuery = query.replaceAll("SELECT lastName, firstNames, mobileNumber,  branchId, bankId, BANKIF.CUSTOMER.status"," SELECT count(*) as COUNT ");
			countQuery = countQuery.replaceAll("ORDER BY lastName", " ");
			System.out.println("************** Checking the number of elements ************");
			if(checkSize(countQuery)){
				
//				File file = null;
				String fName = fileId+"_"+fileName+".pdf";
				
				FacesContext fc = FacesContext.getCurrentInstance();
				ServletContext context = (ServletContext)fc.getExternalContext().getContext();
				String appPath = (String)context.getRealPath("/")+"/";
				System.out.println("Application Path "+appPath);
				JasperPrint jasperPrint = GenerateReportUtil.generatePrintForDormantReport(query, parameters,new HashMap<String, String>(), appPath);
				byte[] byteArr = JasperExportManager.exportReportToPdf(jasperPrint);
				super.getSessionScope().put("pdfFile",byteArr);
				ctx.redirect("/EWalletReportsWebICE/DownloadServlet?fileName="+fName+"&applicationPath="+appPath);
				
				super.setInformationMessage("Report successfully generated.");
				return "download";
			}
			
			super.setInformationMessage("Report successfully generated.");
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage("An error has occured. Report not generated.");
			return "failure";
		}
		
		try {
			ctx.redirect("/EWalletReportsWebICE/jspviewer/viewer.jsp?pdfName="+URLEncryptor.encryptUrl(fileName)+"&query="+URLEncryptor.encryptUrl(query)+"&"+EWalletConstants.MASTER_REPORT+"="+URLEncryptor.encryptUrl(MapUtil.convertAttributesMapToString(parameters))+"&pageName="+URLEncryptor.encryptUrl("dormantCustomersReport.jspx"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "success";
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

	public String generateLatest() {
		this.setLatestReport(true);
		this.setToDate(new Date());
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
//						this.getBranchMenu().add(new SelectItem("all", "ALL"));
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
		StringBuffer buffer = new StringBuffer("The following values are required: ");
		int length = 0;
		if(this.getBankItem().equalsIgnoreCase("none")) {
			buffer.append("Bank, ");
		} if( !this.getBankItem().equalsIgnoreCase("all")) {
			if(this.getBranchItem().equalsIgnoreCase("none")) {
				buffer.append("Branch, ");
			} 
		}
		if(!this.isLatestReport()) {
			if(this.getFromDate() == null) {
				buffer.append("From Date, ");
			} if(this.getToDate() == null) {
				buffer.append("To Date, ");
			} 
		}
		if(this.isLatestReport()) {
			if(this.getFromDate() == null) {
				buffer.append("From Date, ");
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
//				bankMenu.add(new SelectItem("all", "All"));
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
			for(TransactionType txnType : TransactionType.values()) {
				if(TransactionType.BALANCE.equals(txnType)) {
					//Do Nothing Here
				} else {
					this.txnMenu.add(new SelectItem(txnType.toString(), txnType.toString()));
				}
			}
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
	
	private boolean checkSize(String query){
		
		System.out.println(query);
		
		try{
			System.out.println("In Try Block *********");
			Connection connection = GenerateReportUtil.establishConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
//			System.out.println(resultSet.getInt("totalCustomers")+"********************");
			
			int customerSize = 0;
			
			while(resultSet.next()){
				System.out.println("Getting the value");
				customerSize = resultSet.getInt("COUNT");
				System.out.println(customerSize);
			}
			if(customerSize>500){
				return true;
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}