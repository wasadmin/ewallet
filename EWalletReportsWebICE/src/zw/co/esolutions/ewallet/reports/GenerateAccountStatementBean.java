/**
 * 
 */
package zw.co.esolutions.ewallet.reports;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.reports.util.GenerateReportUtil;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.MapUtil;
import zw.co.esolutions.ewallet.util.URLEncryptor;

/**
 * @author taurai
 *
 */
public class GenerateAccountStatementBean extends PageCodeBase{

	private String accountId;
	private List<SelectItem> accountMenu;
	private Date fromDate;
	private Date toDate;
	private List<BankAccount> bankAccountList;
	private String bankId;
	private String accountNumber;
	/**
	 * 
	 */
	public GenerateAccountStatementBean() {
//		if(this.getAccountId() == null) {
//			this.setAccountId((String)super.getRequestScope().get("accountId"));
//		}
		
		//this.setAccountId((String)super.getRequestScope().get("accountId"));
		
//		if(this.getBankId() == null) {
//			this.setBankId((String)super.getRequestScope().get("bankId"));
//		}
		//this.setBankId((String)super.getRequestScope().get("bankId"));
		//tee(">>>>>>>>>>>>> Account ID = "+this.getAccountId());
		this.initializeDates();
	}
	
	
	/*
	 * Method for missing fields
	 */
	private String checkAttributes(){
		StringBuffer buffer = new StringBuffer("The following values are required: ");
		int length = 0;
		if(this.getAccountId() == null || "".equals(this.getAccountId())) {
			buffer.append("Account Number, ");
		}if(this.getFromDate() == null) {
			buffer.append("From Date, ");
			
		}
		if(this.getToDate() == null) {
			buffer.append("To Date, ");
		}
		
		length = buffer.toString().length();
				
		if(length > 40) {
			buffer.replace(length-2, length, " ");
			return buffer.toString();
		}
		return null;
	}
	
	public Date getFromDate() {
		if(fromDate==null)fromDate = new Date();
		return fromDate;
	}
	public Date getToDate() {
		if(toDate==null)toDate = new Date();
		return toDate;
	}
	/*public List<BankAccount> getBankAccountList() {
		try {
			if(bankAccountList == null && this.getAccountId() != null) {
				BankAccount acc = new BankServiceSOAPProxy().findBankAccountById(this.getAccountId());
				if(acc != null) {
					bankAccountList = new ArrayList<BankAccount>();
					bankAccountList.add(acc);
				}
				if(bankAccountList != null) {
					if(bankAccountList.isEmpty()) {
						bankAccountList = null;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bankAccountList;
	}*/
	
	public List<BankAccount> getBankAccountList() {
		try {
			if(this.getAccountId() != null) {
				BankAccount acc = new BankServiceSOAPProxy().findBankAccountById(this.getAccountId());
				if(acc != null) {
					bankAccountList = new ArrayList<BankAccount>();
					bankAccountList.add(acc);
				}
				if(bankAccountList != null) {
					if(bankAccountList.isEmpty()) {
						bankAccountList = null;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bankAccountList;
	}
	
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	public void setBankAccountList(List<BankAccount> bankAccountList) {
		this.bankAccountList = bankAccountList;
	}
	
	public String generateReport() {
		String fileId = null;
		String orderBy ="ORDER BY t.transactiondate ASC ";
		String fileName = "account_statement_report";
		String query = null;
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("sourceFile", fileName+".xml");
		String msg = this.checkAttributes();
		
		if(msg != null) {
			//report error
			//super.setErrorMessage(msg);
			//return "failure";
		}
		
		if("nothing".equalsIgnoreCase(this.getAccountId())) {
			super.setErrorMessage("You cannot generate report without accout numbers. Error.");
			return "failure";
		}
		// Check for ToDate
		if(this.getToDate() != null) {
			if(!DateUtil.isDayBefore(DateUtil.getBeginningOfDay(getToDate()), DateUtil.getEndOfDay(new Date()))) {
				super.setErrorMessage("To Date cannot come after this Today's Date.");
				return "failure";
			}
		}
		
		//Start Appending Queries
		if(this.getFromDate() != null && this.getToDate() != null) {
			// toDate must be greater than fromDate
			if(!DateUtil.isDayBefore(DateUtil.getBeginningOfDay(getFromDate()), DateUtil.getEndOfDay(getToDate()))) {
				super.setErrorMessage("From Date must be a day before(or the same with) To Date.");
				return "failure";
			}
		}
		BankAccount acc = new BankServiceSOAPProxy().findBankAccountById(this.getAccountId());
		try {
			if(acc.getBranch() != null) {
				if(acc.getBranch().getBank() != null) {
					this.setBankId(this.getBankId());
					parameters.put("bankName", acc.getBranch().getBank().getName()+"\n \n"+acc.getBranch().getName());
				} else {
					parameters.put("bankName", new BankServiceSOAPProxy().findBankById(this.getBankId()).getName());
				}
			} else {
				parameters.put("bankName", new BankServiceSOAPProxy().findBankById(this.getBankId()).getName());
			}
			
	        fileId = GenerateReportUtil.getJasperUserId(super.getJaasUserName());
			
	        parameters.put("fileId", fileId);
			
			if(fileId == null) {
				super.setInformationMessage("No results to display.");
				return "failure";
			}
			
			this.initializeDisplayableDates();
			parameters.put("fromDate", DateUtil.convertDateToLongString(DateUtil.getBeginningOfDay(this.getFromDate())));
			parameters.put("toDate", DateUtil.convertDateToLongString(this.getToDate()));
			parameters.put("asAtDate", DateUtil.convertDateToLongString(this.getToDate()));
			
			parameters.put("reportTitle", acc.getAccountName() +" : "+acc.getAccountNumber()+" ["+acc.getType().toString()+"] " +
			"Account Statement");
			parameters.put("pageHeader", "Account Statement for Period : "+DateUtil.convertToDateWithTime(this.getFromDate())+
			" to "+DateUtil.convertToDateWithTime(this.getToDate()));
			super.setInformationMessage("Report successfully generated.");
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage("An error has occured. Report not generated.");
			return "failure";
		}
		ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
		query = "SELECT t.transactiondate as valueDate,t.type as transactionType ,t.processtxnreference as messageId,t.amount, t.accountId, t.narrative, COALESCE ((SELECT SUM(tr.amount) FROM BANKIF.TRANSACTION as tr WHERE tr.TRANSACTIONDATE < '"+ DateUtil.convertDateToTimestamp(DateUtil.getBeginningOfDay(this.getFromDate())) + "' AND tr.accountid='"+ this.getAccountId() +"') ,0) as sumbefore  FROM BANKIF.TRANSACTION as t WHERE t.accountid='"+ this.getAccountId() + "' and t.transactiondate >= '"+ DateUtil.convertDateToTimestamp(DateUtil.getBeginningOfDay((this.getFromDate()))) +"' and t.transactiondate <='"+DateUtil.convertDateToTimestamp(DateUtil.getEndOfDay(this.getToDate())) +"'";
		query = query + orderBy;
		
		try {
			if(!checkSize(query)){
			 //No data to display use viaCollectionDataSource
				parameters.put(EWalletConstants.JASPER_COLLECTION_DATASOURSE, EWalletConstants.JASPER_COLLECTION_DATASOURSE);
				parameters.put("noDate","noData");
				parameters.put("accountId",acc.getId());
			}
			ctx.redirect("/EWalletReportsWebICE/jspviewer/viewer.jsp?pdfName="+URLEncryptor.encryptUrl(fileId+fileName+".pdf")+"&query="+URLEncryptor.encryptUrl(query)+"&"+EWalletConstants.MASTER_REPORT+"="+URLEncryptor.encryptUrl(MapUtil.convertAttributesMapToString(parameters))+"&pageName="+URLEncryptor.encryptUrl("viewAccountStatementReport.jspx"));
			
		} catch (IOException e) {
			e.printStackTrace();
			super.setErrorMessage("An error has occured. Report not generated.");
		}
		this.clear();
		return "success";
	}
	
	private void initializeDates() {
		this.setFromDate(new Date());
		this.setToDate(new Date());
	}
	/**
	 * @param accountId the accountId to set
	 */
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	/**
	 * @return the accountId
	 */
	public String getAccountId() {
		//accountId = (String)super.getRequestScope().get("accountId");
		return accountId;
	}
	/**
	 * @param accountMenu the accountMenu to set
	 */
	public void setAccountMenu(List<SelectItem> accountMenu) {
		this.accountMenu = accountMenu;
	}
	/**
	 * @return the accountMenu
	 */
	/*public List<SelectItem> getAccountMenu() {
		if(accountMenu == null || accountMenu.isEmpty()) {
			accountMenu = new ArrayList<SelectItem>();
			if(this.getBankAccountList() == null || this.getBankAccountList().isEmpty()) {
				accountMenu.add(new SelectItem("nothing", "No Accounts"));
			} else {
				for(BankAccount acc : this.getBankAccountList()) {
					accountMenu.add(new SelectItem(acc.getId(), acc.getAccountNumber()+"  "+acc.getAccountName()+" ["+acc.getType().toString()+" "+acc.getStatus().toString()+"]".replace("_", " ")));
				}
			}
		}
		return accountMenu;
	}*/
	
	public List<SelectItem> getAccountMenu() {
		accountMenu = new ArrayList<SelectItem>();
		if(this.getBankAccountList() == null || this.getBankAccountList().isEmpty()) {
			accountMenu.add(new SelectItem("nothing", "No Accounts"));
		} else {
			for(BankAccount acc : this.getBankAccountList()) {
				accountMenu.add(new SelectItem(acc.getId(), acc.getAccountNumber()+"  "+acc.getAccountName()+" ["+acc.getType().toString()+" "+acc.getStatus().toString()+"]".replace("_", " ")));
			}
		}
		
		return accountMenu;
	}
	
	public String doBack() {
		super.gotoPage("/reportsweb/viewAccountStatementReport.jspx");
		this.clear();
		return "success";
	}
	/**
	 * @param bankId the bankId to set
	 */
	public void setBankId(String bankId) {
		this.bankId = bankId;
	}
	/**
	 * @return the bankId
	 */
	public String getBankId() {
		//bankId = (String)super.getRequestScope().get("bankId");
		return bankId;
	}
	
	private void initializeDisplayableDates() {
		if(true) {
			if(DateUtil.isDayBefore(DateUtil.getBeginningOfDay(this.getToDate()), DateUtil.getBeginningOfDay(new Date()))) {
				this.setToDate(DateUtil.getEndOfDay(this.getToDate()));
			} else if(DateUtil.isDayBeforeOrEqual(DateUtil.getBeginningOfDay(this.getToDate()), DateUtil.getBeginningOfDay(new Date()))) {
				this.setToDate(new Date());
			} else {
				this.setToDate(DateUtil.getEndOfDay(this.getToDate()));
			}
			this.setFromDate(DateUtil.getBeginningOfDay(this.getFromDate()));
		}
	}


	public String getAccountNumber() {
		return accountNumber;
	}


	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	private void clear() {
		this.setAccountId(null);
		this.setAccountNumber(null);
		this.setFromDate(null);
		this.setToDate(null);
		this.setBankId(null);
	}
	
	public void accountAction(ActionEvent e){
		accountId = (String)e.getComponent().getAttributes().get("accountId");
		bankId = (String)e.getComponent().getAttributes().get("bankId");
		if(bankId != null) {
			if("all".equalsIgnoreCase(bankId)) {
				bankId = null;
			}
		}
		if(bankId == null && accountId != null) {
			//tee(">>>>>>>>>>>>>>>>>>>>>>>  BankId is Null and Account ID is NOT NULL.");
			try {
				BankAccount acc = new BankServiceSOAPProxy().findBankAccountById(accountId);
				if(acc != null) {
					if(super.isCustomerAccount(acc.getType())) {
						//tee(">>>>>>>>>>>>>>>>>>>>>>>      Is  Customer Account");
						bankId = acc.getBranch().getBank().getId();
					} else if(super.isBranchAccount(acc.getType())) {
						//tee(">>>>>>>>>>>>>>>>>>>>>>>      Is  Branch Account");
						bankId = acc.getBranch().getBank().getId();
					} else {
						if(OwnerType.BANK.equals(acc.getOwnerType())) {
							//tee(">>>>>>>>>>>>>>>>>>>>>>>      Is  Customer Account");
							bankId = acc.getAccountHolderId();
						} else {
							
						}
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private boolean checkSize(String query){
		
		try{
			Connection connection = GenerateReportUtil.establishConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
//			System.out.println(resultSet.getInt("totalCustomers")+"********************");
			
			if(resultSet.next()){
				return true;
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
