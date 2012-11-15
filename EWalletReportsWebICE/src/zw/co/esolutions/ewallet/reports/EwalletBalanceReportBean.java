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
import zw.co.esolutions.ewallet.enums.BankAccountType;
import zw.co.esolutions.ewallet.enums.CustomerStatus;
import zw.co.esolutions.ewallet.reports.util.GenerateReportUtil;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.JsfUtil;
import zw.co.esolutions.ewallet.util.MapUtil;
import zw.co.esolutions.ewallet.util.NumberUtil;
import zw.co.esolutions.ewallet.util.URLEncryptor;

public class EwalletBalanceReportBean extends PageCodeBase{
	
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
	private String lastName;
	private String mobileNumber;
	private String customerClassItem;
	private List<SelectItem> customerClass;
	
	public EwalletBalanceReportBean() {
		this.initializeDates();
	}
	
	public String generateReport() {
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		
		String fileId = null;
		String fileName = "ewallet_balance_report";
		String msg = this.checkAttributes();
		String bankQuery = "";
		String branchQuery = "";
		String bank = "";
		String branch = "";
		String customerClassQuery = "";
		String subCustomerQuery = "";
		String subProfileQuery = "";
		
		String query = "";
		String queryExt = /*" c.dateCreated < "+DateUtil.convertDateToTimestamp(DateUtil.getEndOfDay(this.getToDate()))+*/" c.id = ba.accountHolderId AND c.branchId = bc.id AND bc.bank_id = b.id AND acc.id = ba.id ";
		String orderBy = "ORDER by b.name ASC, bc.name ASC, c.lastName ASC ";
		
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
				super.setErrorMessage("You cannot continue without Bank and/or Branch selected. Consult your adminstrator.");
				return "failure";
			}
		}
		
		if(/*this.getFromDate() != null &&*/ this.getToDate() != null && !this.isLatestReport()) {
			// Check for ToDate
			if(this.getToDate() != null) {
				if(!DateUtil.isDayBefore(DateUtil.getBeginningOfDay(getToDate()), DateUtil.getEndOfDay(new Date()))) {
					super.setErrorMessage("To Date cannot come after this Today's Date.");
					return "failure";
				}
			}
			
			// toDate must be greater than fromDate
//			if(!DateUtil.isDayBefore(DateUtil.getBeginningOfDay(getFromDate()), DateUtil.getEndOfDay(getToDate()))) {
//				super.setErrorMessage("From Date must be a day before(or the same with) To Date.");
//				return "failure";
//			}
			
		} 
		
		if(getMobileNumber()!=null&& !getMobileNumber().equals("")){
			String mobile = "";
			System.out.println("####################### "+getMobileNumber());
			try {
				mobile = NumberUtil.formatMobile(getMobileNumber());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			queryExt = queryExt + "AND ba.accountNumber LIKE '%"+mobile+"%' ";
		}
		
		if(getLastName()!=null && !getLastName().equals("")){
			queryExt = queryExt + "AND c.lastname LIKE '%"+getLastName().toUpperCase()+"%' ";
		}
		
		try {
			if(!this.getBankItem().equalsIgnoreCase("all")) {
				bank = this.getBankItem();
				subProfileQuery = "(select m.mobileNumber FROM "+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE AS m where m.primary = '"+1+"' AND m.bankId = '"+bank+"' )";
			} else {
				this.setBranchItem("all");
				subProfileQuery = "(select m.mobileNumber FROM "+EWalletConstants.DATABASE_SCHEMA+".MOBILEPROFILE AS m where m.primary = '"+1+"')";
			}
			if(!(this.getBranchItem().equalsIgnoreCase("all") || this.getBranchItem().equalsIgnoreCase("none"))) {
				branch = this.getBranchItem();
			}
			subCustomerQuery = "(select * FROM "+EWalletConstants.DATABASE_SCHEMA+".CUSTOMER where branchId LIKE '%"+branch+"%' AND status = '"+CustomerStatus.ACTIVE+"' AND dateCreated <= '"+DateUtil.convertDateToTimestamp(DateUtil.getEndOfDay(this.getToDate()))+"') ";
			
			query = "SELECT b.name AS bankName, c.id AS customerId, c.lastname, c.branchId, c.firstnames, b.id AS bankId, ba.id AS accountId, ba.accountNumber AS mobileNumber, ab.amount, bc.name AS branchName, ab.id AS balanceId FROM "+
			"(select * from "+EWalletConstants.DATABASE_SCHEMA+".BANKACCOUNT where (type = '"+BankAccountType.E_WALLET+"' OR type = '"+BankAccountType.AGENT_EWALLET+"') AND accountNumber IN "+subProfileQuery+") as ba "+
			", "+subCustomerQuery+" AS c, "+EWalletConstants.DATABASE_SCHEMA+".BANK as b, "+EWalletConstants.DATABASE_SCHEMA+".BANKBRANCH as bc, (select * from "+EWalletConstants.DATABASE_SCHEMA+".BANKACCOUNT where (type = '"+BankAccountType.E_WALLET+"' OR type = '"+BankAccountType.AGENT_EWALLET+"') AND accountNumber IN "+subProfileQuery+") as acc LEFT OUTER JOIN "+
			"(select * from "+EWalletConstants.DATABASE_SCHEMA+".ACCOUNTBALANCE where id IN (SELECT aba.id From "+EWalletConstants.DATABASE_SCHEMA+".ACCOUNTBALANCE as aba, (SELECT a.accountId, MAX(a.dateCreated) AS latestDate FROM "+EWalletConstants.DATABASE_SCHEMA+".ACCOUNTBALANCE as a WHERE a.dateCreated <= '"+DateUtil.convertDateToTimestamp(DateUtil.getEndOfDay(this.getToDate()))+"' GROUP BY a.accountId) AS ac WHERE aba.accountId = ac.accountId AND aba.dateCreated = ac.latestDate) ) AS ab "+
			"ON acc.id = ab.ACCOUNTID  "+
			"WHERE ";
			
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
				
				parameters.put("reportTitle", "eWallet Balance for all Banks ");
				
				parameters.put("pageHeader", "All Banks : Balances As AT"
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
								"Ewallet Balances");
						parameters.put("pageHeader", "All "+ parameters.get(this.getBankItem()).toString()
								+ " EWallet Balances As AT :"
								+ " to "+ DateUtil.convertToDateWithTime(this.getToDate()));
					}
					bankQuery =  "AND b.id = '"+this.getBankItem()+"' ";
					
				} else {
					BankBranch bb = bankService.findBankBranchById(this.getBranchItem());
					parameters.put(bb.getId(), bb.getName());
					parameters.put("reportTitle", parameters.get(this.getBankItem()).toString()+" " +
					"EWallet Balances");
					parameters.put("pageHeader", parameters.get(this.getBankItem()).toString()+ " : "+ bankService.findBankBranchById(
							this.getBranchItem()).getName()+ " Branch EWallet Balances As AT :"
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
			
			super.setInformationMessage("Report successfully generated.");
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage("An error has occured. Report not generated.");
			return "failure";
		}
		ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
		try {
			ctx.redirect("/EWalletReportsWebICE/jspviewer/viewer.jsp?pdfName="+URLEncryptor.encryptUrl(fileId+fileName+".pdf")+"&query="+URLEncryptor.encryptUrl(query)+"&"+EWalletConstants.MASTER_REPORT+"="+URLEncryptor.encryptUrl(MapUtil.convertAttributesMapToString(parameters))+"&pageName="+URLEncryptor.encryptUrl("ewalletBalanceReport.jspx"));
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

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
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
		customerClass = JsfUtil.getCustomerSelectItemsAsList(CustomerClass.values(), true);
		return customerClass;
	}

	public void setCustomerClass(List<SelectItem> customerClass) {
		this.customerClass = customerClass;
	}
}
