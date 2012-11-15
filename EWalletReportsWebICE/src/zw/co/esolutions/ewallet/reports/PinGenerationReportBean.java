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
import zw.co.esolutions.ewallet.audit.AuditEvents;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.CustomerClass;
import zw.co.esolutions.ewallet.enums.CustomerStatus;
import zw.co.esolutions.ewallet.reports.util.GenerateReportUtil;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.JsfUtil;
import zw.co.esolutions.ewallet.util.MapUtil;
import zw.co.esolutions.ewallet.util.NumberUtil;
import zw.co.esolutions.ewallet.util.URLEncryptor;

public class PinGenerationReportBean extends PageCodeBase{
	
	private Date fromDate;
	private Date toDate;
	private List<SelectItem> bankMenu;
	private String bankItem;
	private List<SelectItem> branchMenu;
	private String branchItem;
	private List<Bank> bankList;
	private List<BankBranch> bankBranchList;
	private boolean disableBranchMenu;
	private String mobileNumber;
	private String customerClassItem;
	private List<SelectItem> customerClass;
	private boolean latestReport;
	private List<SelectItem> reportMenu;
	private String reportItem;
	
	public PinGenerationReportBean() {
		this.initializeDates();
	}
	
	public String generateReport() {
		
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		
		String fileId = null;
		String fileName = "";
		String msg = this.checkAttributes();
		String bankQuery = "";
		String branchQuery = "";
		String bank = "";
		String branch = "";
		String customerClassQuery = "";
		String subCustomerQuery = "";
		String subProfileQuery = "";
		String query = "";
		String mobile = "";
		String queryExt = " at.entityId = m.id AND m.customer_id = c.id AND m.mobileNumber = at.instanceName " +
				"AND c.branchId = bc.id AND bc.bank_id = b.id AND a.id = at.activity_id ";
		String orderBy = "ORDER by at.time DESC , b.name ASC, bc.name ASC, c.lastName ASC ";
		
		Map<String, String> parameters = new HashMap<String, String>();
		
		if(msg != null) {
			//report error
			super.setInformationMessage(msg);
			return "failure";
		}
		
		if (this.getBranchItem() != null) {
			if (this.getBankItem().equalsIgnoreCase("nothing")
					|| this.getBranchItem().equalsIgnoreCase("nothing")) {
				super.setErrorMessage("You cannot continue without Bank and/or Branch selected. Consult your adminstrator.");
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
			if(!DateUtil.isDayBefore(DateUtil.getBeginningOfDay(getFromDate()), DateUtil.getEndOfDay(getToDate()))) {
				super.setErrorMessage("From Date must be a day before(or the same with) To Date.");
				return "failure";
			}
			
		} 
		
		if(super.SUMMARY_REPORT.equalsIgnoreCase(this.getReportItem())) {
			fileName = "pin_reset_summary_report";
		} else if(super.DETAILED_REPORT.equalsIgnoreCase(this.getReportItem())){
			fileName = "pin_reset_report";
		}
		
		System.out.println("The file name is "+fileName);
		parameters.put("sourceFile", fileName+".xml");
		
		if(getMobileNumber()!=null&& !getMobileNumber().equals("")){
			
			try {
				mobile = NumberUtil.formatMobile(getMobileNumber());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
//		if(getLastName()!=null && !getLastName().equals("")){
//			queryExt = queryExt + "AND c.lastname LIKE '%"+getLastName().toUpperCase()+"%' ";
//		}
		
		try {
			
			if(!this.getBankItem().equalsIgnoreCase("all")) {
				bank = this.getBankItem();
				subProfileQuery = "(select * FROM "+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE AS m where m.primary = '"+1+"' AND m.bankId = '"+bank+"' AND m.mobileNumber LIKE '%"+mobile+"%')";
			} else {
				this.setBranchItem("all");
				subProfileQuery = "(select * FROM "+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE AS m where m.primary = '"+1+"' AND m.mobileNumber LIKE '%"+mobile+"%')";
			}
			if(!(this.getBranchItem().equalsIgnoreCase("all") || this.getBranchItem().equalsIgnoreCase("none"))) {
				branch = this.getBranchItem();
			}
			subCustomerQuery = "(select * FROM "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER where branchId LIKE '%"+branch+"%' AND status = '"+CustomerStatus.ACTIVE+"') ";
//			String auditTrailQuery = "";
			query = "SELECT b.name AS bankName, c.id AS customerId, c.lastname, c.branchId, c.firstnames,c.customerClass, b.id AS bankId,bc.name AS branchName, at.instanceName, at.userName,at.time,a.name FROM "+
			"(select * from "+EWalletConstants.DATABASE_SCHEMA+".AUDITTRAIL at where at.ACTIVITY_ID IN " +
					"(SELECT a.id FROM "+EWalletConstants.DATABASE_SCHEMA+".ACTIVITY a WHERE a.NAME LIKE '%"+AuditEvents.RESET_MOBILE_PROFILE_PIN+"%' OR a.NAME LIKE '%"+AuditEvents.APPROVE_MOBILE_PROFILE+"%')" +
							"AND at.time >= '"+DateUtil.convertDateToTimestamp(DateUtil.getBeginningOfDay(getFromDate()))+"' AND at.time <= '"+DateUtil.convertDateToTimestamp(DateUtil.getEndOfDay(getToDate()))+"') AS at"+", "+subCustomerQuery+" AS c, "+subProfileQuery+" AS m ,"
			+EWalletConstants.DATABASE_SCHEMA+".BANK as b, "+EWalletConstants.DATABASE_SCHEMA+".ACTIVITY as a, "+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH as bc WHERE ";
			
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
				
				parameters.put("reportTitle", "eWallet Pin Generation for all Banks ");
				
				parameters.put("pageHeader", "All Banks : Pin Generation from "+ DateUtil.convertToDateWithTime(this.getFromDate())
						+" to "+DateUtil.convertToDateWithTime(this.getToDate()));
				
			} else {
				parameters.put(this.getBankItem(), bankService.findBankById(this.getBankItem()).getName());
				if(this.getBranchItem().equalsIgnoreCase("all")) {
					this.setBankBranchList(bankService.getBankBranchByBank(this.getBankItem()));
					if(!(this.getBankBranchList() == null || this.getBankBranchList().isEmpty())) {
						for(BankBranch bb : this.getBankBranchList()) {
							parameters.put(bb.getId(), bb.getName());							
						}
						parameters.put("reportTitle", parameters.get(this.getBankItem()).toString()+" " +
								"Ewallet Pin Resets");
						parameters.put("pageHeader", "All "+ parameters.get(this.getBankItem()).toString()
								+ " EWallet Pin Resets from :"+ DateUtil.convertToDateWithTime(this.getFromDate())
								+ " to "+ DateUtil.convertToDateWithTime(this.getToDate()));
					}
					bankQuery =  "AND b.id = '"+this.getBankItem()+"' ";
					
				} else {
					BankBranch bb = bankService.findBankBranchById(this.getBranchItem());
					parameters.put(bb.getId(), bb.getName());
					parameters.put("reportTitle", parameters.get(this.getBankItem()).toString()+" " +
					"EWallet Pin Resets");
					parameters.put("pageHeader", parameters.get(this.getBankItem()).toString()+ " : "+ bankService.findBankBranchById(
							this.getBranchItem()).getName()+ " Branch Pin Resets From :"+ DateUtil.convertToDateWithTime(this.getFromDate())
					+ " to "+ DateUtil.convertToDateWithTime(this.getToDate()));
					
					branchQuery = "AND c.branchId = '"+this.getBranchItem()+"' ";  
					 
					bankQuery =  "AND b.id = '"+this.getBankItem()+"' ";
					
					
				}
			}
			if(!("".equals(customerClassItem)|| customerClassItem == null)){
				customerClassQuery = " c.CUSTOMERCLASS ='"+customerClassItem+"' AND ";
			}
			query = query + customerClassQuery + queryExt + bankQuery + branchQuery + orderBy ;
			
			fileId = GenerateReportUtil.getJasperUserId(super.getJaasUserName());
			parameters.put("fileId", fileId);
			//Putting pin state parameters 
			parameters.put(AuditEvents.APPROVE_MOBILE_PROFILE,"NEW PIN");
			parameters.put(AuditEvents.RESET_MOBILE_PROFILE_PIN,"RESET");
			
			super.setInformationMessage("Report successfully generated.");
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage("An error has occured. Report not generated.");
			return "failure";
		}
		ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
		try {
			ctx.redirect("/EWalletReportsWebICE/jspviewer/viewer.jsp?pdfName="+URLEncryptor.encryptUrl(fileId+fileName+".pdf")+"&query="+URLEncryptor.encryptUrl(query)+"&"+EWalletConstants.MASTER_REPORT+"="+URLEncryptor.encryptUrl(MapUtil.convertAttributesMapToString(parameters))+"&pageName="+URLEncryptor.encryptUrl("pinGenerationReport.jspx"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "success";
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
	
	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
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
	
	public void setLatestReport(boolean latestReport) {
		this.latestReport = latestReport;
	}

	public boolean isLatestReport() {
		return latestReport;
	}
	
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
	
	public void setReportMenu(List<SelectItem> reportMenu) {
		this.reportMenu = reportMenu;
	}

	public List<SelectItem> getReportMenu() {
		if(this.reportMenu == null) {
			this.reportMenu = new ArrayList<SelectItem>();
			reportMenu.add(new SelectItem(super.SUMMARY_REPORT, super.SUMMARY_REPORT));
			reportMenu.add(new SelectItem(super.DETAILED_REPORT, super.DETAILED_REPORT));
		}
		return reportMenu;
	}

	public void setReportItem(String reportItem) {
		this.reportItem = reportItem;
	}

	public String getReportItem() {
		if(this.reportItem == null) {
			reportItem = super.SUMMARY_REPORT;
		}
		return reportItem;
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
	
	private void initializeDates() {
		try {
			this.setFromDate(DateUtil.getBeginningOfDay(DateUtil.add(new Date(), Calendar.DAY_OF_MONTH, -1)));
			this.setToDate(DateUtil.getEndOfDay(new Date()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
