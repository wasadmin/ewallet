package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.enums.AgentType;
import zw.co.esolutions.ewallet.limitservices.service.BankAccountClass;
import zw.co.esolutions.ewallet.limitservices.service.EWalletException_Exception;
import zw.co.esolutions.ewallet.limitservices.service.Limit;
import zw.co.esolutions.ewallet.limitservices.service.LimitPeriodType;
import zw.co.esolutions.ewallet.limitservices.service.LimitStatus;
import zw.co.esolutions.ewallet.limitservices.service.LimitValueType;
import zw.co.esolutions.ewallet.limitservices.service.TransactionType;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.MoneyUtil;

public class CreateLimitBean extends PageCodeBase{

	
	//Instances
	private Limit limit = new Limit();
	private List<SelectItem> accountClassMenu;
	private String accountClassItem;
	private List<SelectItem> typeMenu;
	private String typeItem;
	private List<SelectItem> periodTypeMenu;
	private String periodTypeItem;
	private Date effectiveDate;
	private double minValue;
	private double maxValue;
	private List<SelectItem> bankMenu;
	private String bankItem;
	private List<Bank> bankList;
	private List<SelectItem> ownerTypeMenu;
	private String ownerTypeItem;
	
	public CreateLimitBean() {
		super();
		this.initializeDateFields();
	}

	public String createLimitAction() {
		StringBuffer infoBuffer = new StringBuffer("");
		StringBuffer errorBuffer = new StringBuffer("");
		String infoReport = null;
		String errorReport =null;
		
		try {
			if(this.getBankItem().equalsIgnoreCase("nothing")) {
				this.setErrorMessage("No banks. An error occured.");
				return "failure";
			}
			
			this.getLimit().setMinValue(MoneyUtil.convertToCents(this.getMinValue()));
			this.getLimit().setMaxValue(MoneyUtil.convertToCents(this.getMaxValue()));
			errorReport = this.checkAttributes();
			if(errorReport != null) { 
				//Report Error.
				super.setErrorMessage(errorReport);
				return "failure";
			}
			
			if(this.getLimit().getMinValue() > this.getLimit().getMaxValue()) {
				super.setErrorMessage("Maximum value cannot be less than mimimum value.");
				return "failure";
			}
			
			
			//Setting Dates and Enums
			this.getLimit().setPeriodType(LimitPeriodType.valueOf(this.getPeriodTypeItem()));
			
			if(!("all".equals(this.getAccountClassItem()))) {
				this.getLimit().setAccountClass(BankAccountClass.valueOf(this.getAccountClassItem()));
			}
			this.getLimit().setType(TransactionType.valueOf(this.getTypeItem()));
			this.getLimit().setValueType(LimitValueType.ABSOLUTE);
			this.getLimit().setEffectiveDate(DateUtil.convertToXMLGregorianCalendar(this.getEffectiveDate()));
			
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>> Effective Date = "+DateUtil.convertToShortUploadDateFormatNumbersOnly(this.getEffectiveDate()));
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>> Account Class = "+this.getAccountClassItem());
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>> Bank Item = "+this.getBankItem());
			
			//1. Creating a Limit for a Bank
			if(( !"all".equalsIgnoreCase(this.getAccountClassItem()) && !"all".equalsIgnoreCase(this.getBankItem()))) {
				System.out.println(">>>>>>>>>>>>>>> In Create Limit for Bank");
				Bank bank = null;
				try {
					this.getLimit().setBankId(this.getBankItem());
					this.setLimit(super.getLimitService().createLimit(this.getLimit(), super.getJaasUserName()));
					System.out.println(">>>>>>>>>>>> Limit Created = "+getLimit());
					if(this.getLimit() == null || this.getLimit().getId() == null) {
						bank = super.getBankService().findBankById(this.getBankItem());
						errorReport = bank.getName()+": This limit already exists. You can edit it only.";
					} else {
						infoReport = "Limit: ["+this.getLimit().getType().toString().replace("_", " ")+" - "+this.getLimit().getAccountClass().toString().replace("_", " ")+
						" : "+this.getLimit().getPeriodType().toString().replace("_", " ")+"], Successfully Created.";
					}
				} catch (EWalletException_Exception ew) {
					if(bank == null || bank.getId() == null) {
						bank = super.getBankService().findBankById(this.getBankItem());
					}
					if(ew.getMessage().equalsIgnoreCase(LimitStatus.AWAITING_APPROVAL.toString())) {
						errorReport = bank.getName()+": Limit already existing, awaiting approval.";
					} else {
						ew.printStackTrace();
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(infoReport != null) {
					super.setInformationMessage(infoReport);
					return "success";
				} else if(errorReport != null) {
					super.setErrorMessage(errorReport);
					return "failure";
				}
			} 
			
			//2. Creating Limit For all banks but one account class
			else if((!"all".equalsIgnoreCase(this.getAccountClassItem())) && 
					"all".equalsIgnoreCase(this.getBankItem())) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>> All banks but 1 account class");
				for(Bank bank : this.getBankList()) {
					try {
						this.getLimit().setBankId(bank.getId());
						Limit l = super.getLimitService().createLimit(this.getLimit(), super.getJaasUserName());
						if(l == null) {
							errorBuffer.append(bank.getName()+": This limit already exists. You can edit it only. \n");
						}  else {
							infoBuffer.append(bank.getName()+": This limit created successfully. \n");
						}
					} catch (EWalletException_Exception e) {
						if(e.getMessage().equalsIgnoreCase(LimitStatus.AWAITING_APPROVAL.toString())) {
							errorBuffer.append(bank.getName()+": Limit already existing, waiting for approval. \n");
						}  else {
							e.printStackTrace();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				
			} 
			
			//3. Creating Limits For all banks and Many account class
			else if(("all".equalsIgnoreCase(this.getAccountClassItem())) && 
					"all".equalsIgnoreCase(this.getBankItem())) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>> All banks n All account Classes");
				for(Bank bank : this.getBankList()) {
					this.getLimit().setBankId(bank.getId());
					String report1 = this.createBatchLimits(this.getLimit());
					if(report1.endsWith("_")) {
						errorBuffer.append(bank.getName()+ ": "+report1.replace("_", ""));
					} else {
						infoBuffer.append(bank.getName()+" : "+report1);
					}
					
					
				}
								
			} 	
			
			//4. Creating Batch Limits for a Single Bank
			else {
								
				this.getLimit().setBankId(this.getBankItem());
				Bank bank = super.getBankService().findBankById(this.getBankItem());
				String report = this.createBatchLimits(this.getLimit());
				if(report.endsWith("_")) {
					errorBuffer.append(bank.getName()+": "+report.replace("_", ""));
				} else {
					infoBuffer.append(bank.getName()+": "+report);
				}
				
			}
			
			// Assigning report msgs
			if(!errorBuffer.equals("")) {
				errorReport = errorBuffer.toString();
			} 
			if(!infoBuffer.equals("")) {
				infoReport = infoBuffer.toString();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			errorReport = "An Error Has Occurred. Operation Arboted.";
			
		} 
		
		infoBuffer = null;
		errorBuffer = null;
		
		//Report
		
		if(infoReport != null && errorReport != null) {
			//Report Error.
			super.setErrorMessage(errorReport);
			super.setInformationMessage(infoReport);
			return "success";
		} else if(errorReport != null) {
			super.setErrorMessage(errorReport);
			return "failure";
		} 
		
		super.setInformationMessage(infoReport);
		this.setLimit(new Limit());
		return "success";
	}
	
	public String cancelLimitCreation() {
		this.setLimit(new Limit());
		this.initializeDateFields();
		super.gotoPage("/admin/adminHome.jspx");
		return "success";
	}
	
	/*
	 * Method for missing fields
	 */
	private String checkAttributes(){
		StringBuffer buffer = new StringBuffer("The following values are required: ");
		int length = 0;
		if(this.getTypeItem().equalsIgnoreCase("")) {
			buffer.append("Limit Type, ");
		}  if(this.getAccountClassItem().equalsIgnoreCase("")) {
			buffer.append("Account Class, ");
		}  if(this.getPeriodTypeItem().equalsIgnoreCase("")) {
			buffer.append("Period, ");
		} if(this.getBankItem().equalsIgnoreCase("")) {
			buffer.append("Financial Institution, ");
		} if(this.getLimit().getMaxValue() < 0l) {
			buffer.append("Value, ");
		}  if(this.getEffectiveDate()== null) {
			buffer.append("Effective Date, ");
		} 
		length = buffer.toString().length();
				
		if(length > 40) {
			buffer.replace(length-2, length, " ");
			return buffer.toString();
		}
		return null;
	}
	
	private void initializeDateFields() {
		if(this.getLimit().getEffectiveDate() == null || this.getLimit().getEndDate() == null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			this.setEffectiveDate(cal.getTime());
			
		}
	}
	
	
	public void setLimit(Limit limit) {
		this.limit = limit;
	}

	public Limit getLimit() {
		return limit;
	}

	public List<SelectItem> getTypeMenu() {
		if(this.typeMenu == null) {
			this.typeMenu = new ArrayList<SelectItem>();
			this.typeMenu.add(new SelectItem("", "--select--"));
			for(TransactionType trans : super.getBranchLimitTnxs()) {
					if(TransactionType.BALANCE.equals(trans)) {
						this.typeMenu.add(new SelectItem(trans.toString(), "ACCOUNT BALANCE"));
					} else { 
						this.typeMenu.add(new SelectItem(trans.toString(), trans.toString().replace("_", " ")));
					}
				
			}
		}
		return typeMenu;
	}

	public void setTypeMenu(List<SelectItem> typeMenu) {
		this.typeMenu = typeMenu;
	}

	public String getTypeItem() {
		return typeItem;
	}

	public void setTypeItem(String typeItem) {
		this.typeItem = typeItem;
	}

	public Date getEffectiveDate() {
		if(this.effectiveDate == null) {
			this.effectiveDate = new Date();
		}
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public List<SelectItem> getAccountClassMenu() {
		if(this.accountClassMenu == null) {
			this.accountClassMenu = new ArrayList<SelectItem>();
			//this.accountClassMenu.add(new SelectItem("", "--select--"));
			//this.accountClassMenu.add(new SelectItem("all", "ALL"));
			for(BankAccountClass accountClass : BankAccountClass.values()) {
				if(BankAccountClass.NONE.equals(accountClass)) {
					this.accountClassMenu.add(new SelectItem(accountClass.toString(), accountClass.toString()));
				}
			}
		}
		return accountClassMenu;
	}

	public void setAccountClassMenu(List<SelectItem> accountClassMenu) {
		this.accountClassMenu = accountClassMenu;
	}

	public String getAccountClassItem() {
		return accountClassItem;
	}

	public void setAccountClassItem(String accountClassItem) {
		this.accountClassItem = accountClassItem;
	}

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	public List<SelectItem> getPeriodTypeMenu() {
		if(this.periodTypeMenu == null) {
			this.periodTypeMenu = new ArrayList<SelectItem>();
			this.periodTypeMenu.add(new SelectItem("", "--select--"));
			for(LimitPeriodType pt : LimitPeriodType.values()) {
				this.periodTypeMenu.add(new SelectItem(pt.toString(), pt.toString()));
			}
		}
		return periodTypeMenu;
	}

	public void setPeriodTypeMenu(List<SelectItem> periodTypeMenu) {
		this.periodTypeMenu = periodTypeMenu;
	}

	public String getPeriodTypeItem() {
		return periodTypeItem;
	}

	public void setPeriodTypeItem(String periodTypeItem) {
		this.periodTypeItem = periodTypeItem;
	}
	
//	private String getBankId() {
//		String bankId = null;
//		Profile profile = null;
//		Bank bank = null;
//		try {
//			profile = this.super.getProfileService().getProfileByUserName(super.getJaasUserName());
//			bank = this.super.getBankService().findBankBranchById(profile.getBranchId()).getBank();
//			bankId = bank.getId();
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		return bankId;
//	}
	
	private String createBatchLimits(Limit limit) {
		String result = "";
		StringBuffer createdMsg = new StringBuffer("Created class(es): ");
		StringBuffer existingMsg = new StringBuffer("Already existing class(es): ");
		boolean limitExists = false;
		boolean limitCreated = false;
		Limit l;
		try {
			//For all Banks & All account classes
			
			//For one bank and all account classes
			for(BankAccountClass acc : BankAccountClass.values()) {
				if((BankAccountClass.NONE.equals(acc))) {
					limit.setAccountClass(acc);
					try {
						l = super.getLimitService().createLimit(limit, super.getJaasUserName());
						if(l == null) {
							limitExists = true;
							existingMsg.append(acc.toString().replace("_", " ")+", ");
						} else {
							limitCreated = true;
							createdMsg.append(acc.toString().replace("_", " ")+", ");
						}
					} catch (EWalletException_Exception  e) {
						if(e.getMessage().equalsIgnoreCase(LimitStatus.AWAITING_APPROVAL.toString())) {
							existingMsg.append(acc.toString().replace("_", " ")+"awaiting approval. ,");
						} else {
							e.printStackTrace();
						}
					}
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		int length = 0;
		if(limitExists && limitCreated) {
			length = createdMsg.toString().length();
			result = existingMsg.toString()+createdMsg.replace(length-2, length, " ").toString()+".";
		} else if(limitExists) {
			length = existingMsg.toString().length();
			result = existingMsg.replace(length-2, length, " ").toString()+"._";// The _ used for error messaging
		} else if(limitCreated) {
			result = limit.getPeriodType().toString()+", "+limit.getType().toString().replace("_", " ")+" Limits for all account classes successfully created.";
		}
		if(result.equalsIgnoreCase("")) {
			result = "All account classes exist for this limit : "+limit.getType().toString().replace("_", " ");
		}
		return result;
	}

	public void setBankMenu(List<SelectItem> bankMenu) {
		this.bankMenu = bankMenu;
	}

	public List<SelectItem> getBankMenu() {
		if(this.bankMenu == null || this.bankMenu.isEmpty()) {
			this.bankMenu = new ArrayList<SelectItem>();
			if(this.getBankList() == null || this.getBankList().isEmpty()) {
				this.bankMenu.add(new SelectItem("nothing", "No Banks"));
			} else {
				for(Bank b: this.getBankList()) {
					this.bankMenu.add(new SelectItem(b.getId(), b.getName()));
				}
				this.bankMenu.add(new SelectItem("all", "ALL"));
			}
		}
		System.out.println("&&&&&&&&&&&&&& Bank Menu:"+bankMenu);
		return bankMenu;
	}

	public void setBankItem(String bankItem) {
		this.bankItem = bankItem;
	}

	public String getBankItem() {
		if(this.bankItem == null) {
			Profile p = super.getProfileService().getProfileByUserName(super.getJaasUserName());
			if(p != null) {
				try{
					this.bankItem = super.getBankService().findBankBranchById(p.getBranchId()).getBank().getId();
				}catch(Exception e){
					e.printStackTrace();
					this.bankItem="";
				}
			}
		}
		return this.bankItem;
	}

	public void setBankList(List<Bank> bankList) {
		this.bankList = bankList;
	}
	
	
	//Retrieving ZB & ZBBS
	public List<Bank> getBankList() {
		if(this.bankList == null || this.bankList.isEmpty()) {
			List< Bank> banks = super.getBankService().getBank();
			if(banks == null || banks.isEmpty()) {
				return null;
			} else {
				this.bankList = new ArrayList<Bank>();
				for(Bank bk : banks) {
					if(bk.getName().contains(EWalletConstants.SYSTEM_BANKS_DELIMITER)) {
						this.bankList.add(bk);
					}
				}
			}
		}
		return bankList;
	}

	public void handleLimitTypeValueChange(ValueChangeEvent event) {
		String item = (String)event.getNewValue();
		if(item != null) {
			try {
				if(item.equalsIgnoreCase(AgentType.BANK_BRANCH.toString())) {
					
						this.setTypeMenu(new ArrayList<SelectItem>());
						this.getTypeMenu().add(new SelectItem("", "--select--"));
						for(TransactionType trans : super.getBranchLimitTnxs()) {
								if(TransactionType.BALANCE.equals(trans)) {
									this.getTypeMenu().add(new SelectItem(trans.toString(), "ACCOUNT BALANCE"));
								} else { 
									this.getTypeMenu().add(new SelectItem(trans.toString(), trans.toString().replace("_", " ")));
								}
							
						}
					
				} else if(item.equalsIgnoreCase(AgentType.AGENT.toString())) {
					
					this.setTypeMenu(new ArrayList<SelectItem>());
					this.getTypeMenu().add(new SelectItem("", "--select--"));
					for(TransactionType trans : super.getAgentLimitTnxs()) {
							if(TransactionType.BALANCE.equals(trans)) {
								this.getTypeMenu().add(new SelectItem(trans.toString(), "ACCOUNT BALANCE"));
							} else { 
								this.getTypeMenu().add(new SelectItem(trans.toString(), trans.toString().replace("_", " ")));
							}
						
					}
				
			}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public List<SelectItem> getOwnerTypeMenu() {
		if(this.ownerTypeMenu == null) {
			this.ownerTypeMenu = new ArrayList<SelectItem>();
			this.ownerTypeMenu.add(new SelectItem(AgentType.BANK_BRANCH.toString(), " BANK - Limit"));
			this.ownerTypeMenu.add(new SelectItem(AgentType.AGENT.toString(), " AGENT - Limit"));
			
		}
		return ownerTypeMenu;
	}

	public void setOwnerTypeMenu(List<SelectItem> ownerTypeMenu) {
		this.ownerTypeMenu = ownerTypeMenu;
	}

	public String getOwnerTypeItem() {
		if(this.ownerTypeItem == null) {
			this.ownerTypeItem = AgentType.BANK_BRANCH.toString();
		}
		return ownerTypeItem;
	}

	public void setOwnerTypeItem(String ownerTypeItem) {
		this.ownerTypeItem = ownerTypeItem;
	}
}
