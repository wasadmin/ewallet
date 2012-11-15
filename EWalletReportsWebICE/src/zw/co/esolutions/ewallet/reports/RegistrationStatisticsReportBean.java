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

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.CustomerClass;
import zw.co.esolutions.ewallet.enums.BankBranchStatus;
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
public class RegistrationStatisticsReportBean extends PageCodeBase{

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
	private boolean fromInception;
	private String customerClassItem;
	private List<SelectItem> customerClass;
	
	
	/**
	 * 
	 */
	public RegistrationStatisticsReportBean() {
		this.initializeDates();
	}
	
	public String generateReport() {
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		
		String fileId = null;
		String fileName = "customer_registration_statistics_report";
		String msg = this.checkAttributes();
		String query = null;
		String bankQuery = "";
		String branchQuery = "";
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
		
		if (this.getBranchItem() != null) {
			if (this.getBankItem().equalsIgnoreCase("nothing")
					|| this.getBranchItem().equalsIgnoreCase("nothing")) {
				super
						.setErrorMessage("You cannot continue without Bank and/or Branch selected. Consult your adminstrator.");
				return "failure";
			}
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
			if(!this.isFromInception() && !DateUtil.isDayBefore(DateUtil.getBeginningOfDay(getFromDate()), DateUtil.getEndOfDay(getToDate()))) {
				super.setErrorMessage("From Date must be a day before(or the same with) To Date.");
				return "failure";
			}
			
			//Append Dates
			if(this.isFromInception()) {
				queryExt = "AND c.dateCreated <= '"+DateUtil.convertDateToTimestamp(DateUtil.getEndOfDay(this.getToDate()))+"' ";
			} else {
				queryExt = "AND c.dateCreated >= '"+DateUtil.convertDateToTimestamp(DateUtil.getBeginningOfDay(this.getFromDate()))+"' " +
				"AND c.dateCreated <= '"+DateUtil.convertDateToTimestamp(DateUtil.getEndOfDay(this.getToDate()))+"' ";
			}
			
		} else if(isLatestReport()) {
			
			//Append Dates
			queryExt = "AND c.dateCreated >= '"+DateUtil.convertDateToTimestamp(DateUtil.getBeginningOfDay(this.getFromDate()))+"' " +
			"AND c.dateCreated <= '"+DateUtil.convertDateToTimestamp(DateUtil.getEndOfDay(this.getToDate()))+"' ";
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
				
				parameters.put("reportTitle", "Statistics for all Banks");
				
				if(this.isFromInception()) {
					parameters.put("pageHeader", "All Banks : Statistics from Inception to "+DateUtil.convertToDateWithTime(this.getToDate()));
				} else  {
					parameters.put("pageHeader", "All Banks : Statistics from "+DateUtil.convertToDateWithTime(this.getFromDate())+	" to "+DateUtil.convertToDateWithTime(this.getToDate()));
				}
				bankQuery = "AND bc.bank_id = b.id ";
				orderBy = "ORDER BY b.name ASC, bc.name ASC, c.status ASC ";
				
			} else {
				parameters.put(this.getBankItem(), bankService.findBankById(this.getBankItem()).getName());
				if(this.getBranchItem().equalsIgnoreCase("all")) {
					this.setBankBranchList(bankService.getBankBranchByBank(this.getBankItem()));
					if(!(this.getBankBranchList() == null || this.getBankBranchList().isEmpty())) {
						for(BankBranch bb : this.getBankBranchList()) {
							parameters.put(bb.getId(), bb.getName());							
						}
						parameters.put("reportTitle", parameters.get(this.getBankItem()).toString()+" " +
								"Statistics");
						if(this.isFromInception()) {
							parameters.put("pageHeader", "All "+ parameters.get(this.getBankItem()).toString()
									+ " Branches Statistics from Inception to "+ DateUtil.convertToDateWithTime(this.getToDate()));
						} else {
							parameters.put("pageHeader", "All "+ parameters.get(this.getBankItem()).toString()
									+ " Branches Statistics from "+ DateUtil.convertToDateWithTime(this.getFromDate())
									+ " to "+ DateUtil.convertToDateWithTime(this.getToDate()));
						}
					}
					bankQuery =  "AND bc.bank_id = '"+this.getBankItem()+"' AND bc.bank_id = b.id ";	
					
					orderBy = "ORDER BY bc.name ASC, c.status ASC ";
					
				} else {
					BankBranch bb = bankService.findBankBranchById(this.getBranchItem());
					parameters.put(bb.getId(), bb.getName());
					parameters.put("reportTitle", parameters.get(this.getBankItem()).toString()+" " +
					"Statistics");
					
					if(this.isFromInception()) {
						parameters.put("pageHeader", parameters.get(this.getBankItem()).toString()+ " : "+ bankService.findBankBranchById(
								this.getBranchItem()).getName()+ " Branch Statistics from Inception to "+ DateUtil.convertToDateWithTime(this.getToDate()));
					} else {
						parameters.put("pageHeader", parameters.get(this.getBankItem()).toString()+ " : "+ bankService.findBankBranchById(
								this.getBranchItem()).getName()+ " Branch Statistics from "+ DateUtil.convertToDateWithTime(this.getFromDate())
						+ " to "+ DateUtil.convertToDateWithTime(this.getToDate()));
					}
					
					branchQuery = "AND c.branchId = '"+this.getBranchItem()+"' ";  
					 
					bankQuery =  "AND bc.bank_id = '"+this.getBankItem()+"' AND bc.bank_id = b.id ";	
					
					orderBy = "ORDER BY c.status ASC ";
					
					
				}
			}
			
			if(!("".equals(customerClassItem)|| customerClassItem == null)){
				customerClassQuery = " AND c.customerClass = '"+customerClassItem+"' ";
			}
			
			query =  "SELECT bc.BANK_ID AS bankId, c.status, c.branchId, b.name AS bankName, bc.code AS branchCode "+ 
					"FROM  "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER AS c, "+EWalletConstants.DATABASE_SCHEMA+".BANK as b, "+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH AS bc WHERE " +
					"bc.ID = c.BRANCHID ";
			
			
			/*String subQuery =  "SELECT c.id FROM  "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER AS c, "+EWalletConstants.DATABASE_SCHEMA+".BANK as b, "+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH AS bc WHERE " +
			"bc.ID = c.BRANCHID ";*/
			
			if(queryExt != null){
				query = query + queryExt;
				//subQuery = subQuery + queryExt;
			}
			//subQuery = subQuery + bankQuery + branchQuery;
			query = query + customerClassQuery  + bankQuery + branchQuery + orderBy;
			
			/*query =  "SELECT bc.BANK_ID AS bankId, c.status, bc.id AS branchId, b.name AS bankName, bc.code AS branchCode "+ 
			"FROM   "+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH AS bc LEFT OUTER JOIN (select * FROM "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER where id IN ("+subQuery+")"+")AS c ON c.branchId = bc.id, "+EWalletConstants.DATABASE_SCHEMA+".BANK as b WHERE " +
			"bc.bank_id = b.id AND bc.status <> '"+BankBranchStatus.DELETED+"' ";
			
			query = query + orderBy;*/
			
			
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
			ctx.redirect("/EWalletReportsWebICE/jspviewer/viewer.jsp?pdfName="+URLEncryptor.encryptUrl(fileId+fileName+".pdf")+"&query="+URLEncryptor.encryptUrl(query)+"&"+EWalletConstants.MASTER_REPORT+"="+URLEncryptor.encryptUrl(MapUtil.convertAttributesMapToString(parameters))+"&pageName="+URLEncryptor.encryptUrl("customerRegStatisticsReport.jspx"));
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

	public void setFromInception(boolean fromInception) {
		this.fromInception = fromInception;
	}

	public boolean isFromInception() {
		return fromInception;
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
