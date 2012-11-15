package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.limitservices.service.BankAccountClass;
import zw.co.esolutions.ewallet.limitservices.service.EWalletException_Exception;
import zw.co.esolutions.ewallet.limitservices.service.Limit;
import zw.co.esolutions.ewallet.limitservices.service.LimitPeriodType;
import zw.co.esolutions.ewallet.limitservices.service.LimitStatus;
import zw.co.esolutions.ewallet.limitservices.service.LimitValueType;
import zw.co.esolutions.ewallet.limitservices.service.TransactionType;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;

public class EditLimitBean extends PageCodeBase{

	//Instances
	private Limit limit;
	private List<SelectItem> accountClassMenu;
	private String accountClassItem;
	private List<SelectItem> typeMenu;
	private String typeItem;
	private List<SelectItem> periodTypeMenu;
	private String periodTypeItem;
	private Date effectiveDate;
	private String limitId;
	private List<SelectItem> bankMenu;
	private String bankItem;
	private List<Bank> bankList;
	
	public EditLimitBean() {
		super();
		if(this.getLimitId() == null) {
			this.setLimitId((String)super.getRequestScope().get("limitId"));
		}
		
	}

	@SuppressWarnings("unchecked")
	public String editLimitAction() {
		String report = null;
		Limit oldLimit = null;
		
		try {
			if(this.getBankItem().equalsIgnoreCase("nothing")) {
				this.setErrorMessage("No banks. An error occured.");
				return "failure";
			}
			
		  report = this.checkAttributes();
			if(report != null) {
				//Report Error.
				super.setErrorMessage(report);
				return "failure";
			}
			
			if(this.getLimit().getMinValue() > this.getLimit().getMaxValue()) {
				super.setErrorMessage("Maximum value cannot be less than mimimum value.");
				return "failure";
			}
			
			oldLimit = super.getLimitService().findLimitById(this.getLimitId());
			
			//Setting Dates and Enums
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> Old limit ID "+this.getLimitId());
			
			this.getLimit().setId(null);
			this.getLimit().setBankId(this.getBankItem());
			this.getLimit().setOldLimitId(oldLimit.getId());
			this.getLimit().setStatus(LimitStatus.AWAITING_APPROVAL);
			this.getLimit().setPeriodType(LimitPeriodType.valueOf(this.getPeriodTypeItem()));
			this.getLimit().setAccountClass(BankAccountClass.valueOf(accountClassItem));
			this.getLimit().setType(TransactionType.valueOf(this.getTypeItem()));
			this.getLimit().setValueType(LimitValueType.ABSOLUTE);
			//this.getLimit().setEffectiveDate(DateUtil.convertToXMLGregorianCalendar(this.getEffectiveDate()));
			System.out.println(">>>>>>>>>>>>>>>> New Limit ID must be Null = "+this.getLimit().getId());
			Limit lt = this.getLimit();
			lt.setEffectiveDate(DateUtil.convertToXMLGregorianCalendar(this.getEffectiveDate()));
			lt.setId(null);
			lt = super.getLimitService().editLimit(lt, super.getJaasUserName());
			System.out.println(">>>>>>>>>>>>>>>> Done creating Limit = "+lt);
			this.setLimit(lt);
			if(lt == null || lt.getId() == null) {
				report = "This limit already exists.";
			}
			this.setLimitId(this.getLimit().getId());
		} catch (EWalletException_Exception ewe) {
			if(LimitStatus.AWAITING_APPROVAL.toString().equals(ewe.getMessage())) {
				report = "Limit already existing, awaiting approval.";
			} else {
				ewe.printStackTrace();
			}
		}catch (Exception e) {
			e.printStackTrace();
			report = "An Error Has Occurred. Operation Arboted.";
			
		} 
		
		//Report
		if(report != null) {
			//Report Error.
			super.setErrorMessage(report);
			return "failure";
		}
		report = "Limit Successfully Edited.";
		super.setInformationMessage(report);
		//super.getRequestScope().put("editLimit", report);
		super.getRequestScope().put("limitId", this.getLimitId());
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> new limit ID "+(String)super.getRequestScope().get("limitId"));
		this.cleanBean();
		return "success";
	}
	
	@SuppressWarnings("unchecked")
	public String cancelLimitEditing() {
		super.getRequestScope().put("limitId", this.getLimit().getId());
		this.cleanBean();
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
		}  if(this.getLimit().getMaxValue() < 0l) {
			buffer.append("Value, ");
		}  if(this.getEffectiveDate()== null) {
			buffer.append("Effective Date, ");
		} if(this.getBankItem().equalsIgnoreCase("")) {
			buffer.append("Financial Institution, ");
		}
		length = buffer.toString().length();
				
		if(length > 40) {
			buffer.replace(length-2, length, " ");
			return buffer.toString();
		}
		return null;
	}
	
		
	public void setLimit(Limit limit) {
		this.limit = limit;
	}

	public Limit getLimit() {
		if(this.limit == null && this.getLimitId() != null) {
			try {
				this.limit =super.getLimitService().findLimitById(this.getLimitId());
			} catch (Exception e) {
				return null;
			}
		} if(this.limit == null) {
			this.limit = new Limit();
		}
		return limit;
	}

	public List<SelectItem> getTypeMenu() {
		if(this.typeMenu == null) {
			this.typeMenu = new ArrayList<SelectItem>();
			this.typeMenu.add(new SelectItem("", "--select--"));
			for(TransactionType trans : super.getLimitTnxs()) {
				
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
		if(this.typeItem == null && this.getLimit().getId() != null) {
			this.typeItem = this.getLimit().getType().toString();
		}
		return typeItem;
	}

	public void setTypeItem(String typeItem) {
		this.typeItem = typeItem;
	}

	public Date getEffectiveDate() {
		if(this.effectiveDate == null && this.getLimit().getId() != null) {
			this.effectiveDate = DateUtil.convertToDate(this.getLimit().getEffectiveDate());
			
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>> Manipulate Date = "+DateUtil.convertToShortUploadDateFormatNumbersOnly(this.effectiveDate));
			//This is because of an ICE Faces Bug, it's subtracting a Day instead of showing actual date
			this.effectiveDate = DateUtil.addDays(this.effectiveDate, 1);
		}
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>. Effective Date = "+DateUtil.convertToShortUploadDateFormatNumbersOnly(this.effectiveDate));
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public List<SelectItem> getAccountClassMenu() {
		if(this.accountClassMenu == null) {
			this.accountClassMenu = new ArrayList<SelectItem>();
			this.accountClassMenu.add(new SelectItem("", "--select--"));
			for(BankAccountClass accountClass : BankAccountClass.values()) {
				if(!(BankAccountClass.SYSTEM.equals(accountClass))) {
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
		if(this.accountClassItem == null && this.getLimit().getId() != null) {
			this.accountClassItem = this.getLimit().getAccountClass().toString();
		}
		return accountClassItem;
	}

	public void setAccountClassItem(String accountClassItem) {
		this.accountClassItem = accountClassItem;
	}


	public void setLimitId(String limitId) {
		this.limitId = limitId;
	}

	public String getLimitId() {
		if(this.limitId == null) {
			try {
				this.limitId = (String)super.getRequestScope().get("limitId");
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		System.out.println(" ############ In getLimitId, limitId = "+limitId);
		return limitId;
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
		if(this.periodTypeItem == null && this.getLimit().getId() != null) {
			this.periodTypeItem = this.getLimit().getPeriodType().toString();
		}
		return periodTypeItem;
	}

	public void setPeriodTypeItem(String periodTypeItem) {
		this.periodTypeItem = periodTypeItem;
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
				this.bankMenu.add(new SelectItem("", "--select--"));
				for(Bank b: this.getBankList()) {
					this.bankMenu.add(new SelectItem(b.getId(), b.getName()));
					
				}
				
			}
		}
		return bankMenu;
	}

	public void setBankItem(String bankItem) {
		this.bankItem = bankItem;
	}

	public String getBankItem() {
		if(this.bankItem == null) {
			if(this.getLimit() != null) {
				this.bankItem = this.getLimit().getBankId();
			}
		}
		return bankItem;
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
	
	private void cleanBean() {
		limit = null;
		accountClassMenu = null;
		accountClassItem = null;
		typeMenu = null;
		typeItem = null;
		periodTypeMenu = null;
		periodTypeItem = null;
		effectiveDate = null;
		limitId = null;
		bankMenu =null;
		bankItem = null;
		bankList = null;
	}
}

