package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.tariffservices.service.AgentType;
import zw.co.esolutions.ewallet.tariffservices.service.CustomerClass;
import zw.co.esolutions.ewallet.tariffservices.service.TariffStatus;
import zw.co.esolutions.ewallet.tariffservices.service.TariffTable;
import zw.co.esolutions.ewallet.tariffservices.service.TariffType;
import zw.co.esolutions.ewallet.tariffservices.service.TransactionType;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;

public class ViewAllTariffTablesBean extends PageCodeBase{
	
	private List<SelectItem> searchMenu;
	private String searchItem;
	private List<SelectItem> criterionMenu;
	private String criterionItem;
	private List<TariffTable> tariffTableList;
	private Date fromDate;
	private Date toDate;
	private boolean disableDates;
	private String errorMsg;
	private String inforMessage;
	private List<SelectItem> bankMenu;
	private String bankItem;
	private List<Bank> bankList;
	
	public ViewAllTariffTablesBean() {
		super();
		if(this.getSearchItem() == null) {
			this.setSearchItem("effective");
		}
		if(this.getFromDate() == null && this.getCriterionItem() == null) {
			this.setDisableDates(true);
			
		}
	}
	private void cleanBean() {
		searchMenu = null;
		searchItem = null;
		criterionMenu = null;
		criterionItem = null;
		tariffTableList = null;
		fromDate = null;
		toDate = null;
		disableDates = false;
		errorMsg = null;
		inforMessage = null;
		bankMenu = null;
		bankItem = null;
		bankList = null;
	}
	public String search() {
        try {
        	if("none".equalsIgnoreCase(this.getBankItem())) {
        		super.setErrorMessage("No Financial Institutions Found.");
        		return "failure";
        	}if("all".equalsIgnoreCase(this.getBankItem())) {
        		this.setBankItem("");
        	}
        	if(this.getSearchItem().equalsIgnoreCase("tariffStatus")) {
				this.setTariffTableList(super.getTariffService().getTariffTableByTariffStatusAndBankId(TariffStatus.valueOf(this.getCriterionItem()), this.getBankItem()));
			}
        	else if(this.getSearchItem().equalsIgnoreCase("effective")) {
				this.setTariffTableList(super.getTariffService().getEffectiveTariffTablesForBank(this.getBankItem()));
			}else if(this.getSearchItem().equalsIgnoreCase("tariffType")) {
				this.setTariffTableList(super.getTariffService().getTariffTableByTariffTypeAndBankId(TariffType.valueOf(this.getCriterionItem()), this.getBankItem()));
				
			}else if(this.getSearchItem().equalsIgnoreCase("CustomerClass")) {
				this.setTariffTableList(super.getTariffService().getTariffTableByCustomerClassAndBankId(
						CustomerClass.valueOf(this.getCriterionItem()), this.getBankItem()));
			}else if(this.getSearchItem().equalsIgnoreCase("txnType")) {
				this.setTariffTableList(super.getTariffService().getTariffTableByTransactionTypeAndBankId(
						TransactionType.valueOf(this.getCriterionItem()), this.getBankItem()));
			}else if(this.getSearchItem().equalsIgnoreCase("agentType")) {
				
			}else if(this.getSearchItem().equalsIgnoreCase("dateRange")) {
				if(this.getFromDate() == null || this.getToDate() == null) {
					this.setErrorMsg("From Date and To Date are required fields.");
					return "failure";
				}
				System.out.println(">>>>>>>>>>>>>>>>>>>>> Dates = "+this.getFromDate());
				if(!DateUtil.isDayBefore(this.getFromDate(), this.getToDate())) {
					this.setErrorMsg("To Date must come after From Date.");
					return "failure";
				}
				
				this.setTariffTableList(super.getTariffService().getTariffTableByDateRangeAndBankId(DateUtil.convertToXMLGregorianCalendar(this.getFromDate()), DateUtil.convertToXMLGregorianCalendar(this.getToDate()), this.getBankItem()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(this.getTariffTableList() == null || this.getTariffTableList().isEmpty()) {
			this.setInforMessage("No Commission Tables Found.");
			return "failure";
		}
		this.setInforMessage(this.getTariffTableList().size()+" Commission Table(s) found. ");
		return "success";
	}
	
	public String viewAll() {
		try {
			this.setTariffTableList(super.getTariffService().getAllTariffTables());
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(this.getTariffTableList() == null || this.getTariffTableList().isEmpty()) {
			this.setInforMessage("No Commission Tables Found.");
			return "failure";
		}
		this.setInforMessage(this.getTariffTableList().size()+" Commission Table(s) found. ");
		return "success";
		
	}
	
	@SuppressWarnings("unchecked")
	public String viewTariffTable() {
		super.getRequestScope().put("tariffTableId", super.getRequestParam().get("tariffTableId"));
		super.gotoPage("/admin/viewCommissionTable.jspx");
		this.cleanBean();
		return "success";
	}
	
	public void handleSearchValueChange(ValueChangeEvent event) {
		String item = (String)event.getNewValue();
		try {

			this.setDisableDates(true);
			this.getCriterionMenu().removeAll(this.getCriterionMenu());
			this.setInforMessage(null);
			this.setErrorMsg(null);
			if(item.equalsIgnoreCase("effective")) {
				this.getCriterionMenu().add(new SelectItem("onDate", "Effective"));
				
			} if(item.equalsIgnoreCase("tariffType")) {
				for(TariffType type : TariffType.values()) {
					this.getCriterionMenu().add(new SelectItem(type.toString(), type.toString().replace("_", " ")));
				}
			} if(item.equalsIgnoreCase("customerClass")) {
				for(CustomerClass aClass : CustomerClass.values()) {
					this.getCriterionMenu().add(new SelectItem(aClass.toString(), aClass.toString().replace("_", " ")));
				}
			} if(item.equalsIgnoreCase("txnType")) {
				for(TransactionType trans : super.getTariffTnxs()) {
					
					
						this.getCriterionMenu().add(new SelectItem(trans.toString(), trans.toString().replace("_", " ")));
					
					
				}
			} if(item.equalsIgnoreCase("agentType")) {
				for(AgentType aType : AgentType.values()) {
					this.getCriterionMenu().add(new SelectItem(aType.toString(), aType.toString().replace("_", " ")));
				}
			} if(item.equalsIgnoreCase("dateRange")) {
				//this.getCriterionMenu().remove(0);
				this.getCriterionMenu().add(new SelectItem("dateRange", "Dates"));
				this.setDisableDates(false);
			} if(item.equalsIgnoreCase("tariffStatus")) {
				for(TariffStatus txn : TariffStatus.values()) {
					this.getCriterionMenu().add(new SelectItem(txn.toString(), txn.toString().replace("_", " ")));
				}
			} 
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public List<SelectItem> getSearchMenu() {
		if(searchMenu == null) {
			searchMenu = new ArrayList<SelectItem>();
			searchMenu.add(new SelectItem("effective", " Effective Tables"));
			searchMenu.add(new SelectItem("tariffType", " Tariff Type"));
			searchMenu.add(new SelectItem("customerClass", " Customer Class"));
			searchMenu.add(new SelectItem("txnType", " Transaction Type"));
			searchMenu.add(new SelectItem("tariffStatus", " Status"));
			//searchMenu.add(new SelectItem("agentType", " Agent Type"));
			searchMenu.add(new SelectItem("dateRange", " Date Range"));
		}
		return searchMenu;
	}

	public void setSearchMenu(List<SelectItem> searchMenu) {
		this.searchMenu = searchMenu;
	}

	public String getSearchItem() {
		return searchItem;
	}

	public void setSearchItem(String searchItem) {
		this.searchItem = searchItem;
	}

	
	public List<TariffTable> getTariffTableList() {
		return tariffTableList;
	}

	public void setTariffTableList(List<TariffTable> tariffTableList) {
		this.tariffTableList = tariffTableList;
	}

	public Date getFromDate() {
		/*if(this.fromDate == null) {
			this.fromDate = new Date(System.currentTimeMillis());
			this.fromDate = DateUtil.addDays(this.fromDate, -7);
		}*/
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		/*if(this.toDate == null) {
			this.toDate = new Date(System.currentTimeMillis());
		}*/
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public boolean isDisableDates() {
		return disableDates;
	}

	public void setDisableDates(boolean disableDates) {
		this.disableDates = disableDates;
	}

	public List<SelectItem> getCriterionMenu() {
		if(criterionMenu == null) {
		   criterionMenu = new ArrayList<SelectItem>();
		   //criterionMenu.add(new SelectItem("none", "--select--"));
		   this.getCriterionMenu().add(new SelectItem("onDate", "Effective"));
		}
		return criterionMenu;
	}

	public void setCriterionMenu(List<SelectItem> criterionMenu) {
		this.criterionMenu = criterionMenu;
	}

	public String getCriterionItem() {
		return criterionItem;
	}

	public void setCriterionItem(String criterionItem) {
		this.criterionItem = criterionItem;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setInforMessage(String inforMessage) {
		this.inforMessage = inforMessage;
	}

	public String getInforMessage() {
		return inforMessage;
	}
	
	public List<SelectItem> getBankMenu() {
		if(this.bankMenu == null) {
			bankMenu = new ArrayList<SelectItem>();
			if(this.getBankList() == null || this.getBankList().isEmpty()) {
				bankMenu.add(new SelectItem("nothing", "No Banks"));
			} else {
				//bankMenu.add(new SelectItem("none", "<--select-->"));
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

	/**
	 * @param bankItem the bankItem to set
	 */
	public void setBankItem(String bankItem) {
		this.bankItem = bankItem;
	}

	/**
	 * @return the bankItem
	 */
	public String getBankItem() {
		return bankItem;
	}

	/**
	 * @param bankList the bankList to set
	 */
	public void setBankList(List<Bank> bankList) {
		this.bankList = bankList;
	}

	/**
	 * @return the bankList
	 */
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

}

