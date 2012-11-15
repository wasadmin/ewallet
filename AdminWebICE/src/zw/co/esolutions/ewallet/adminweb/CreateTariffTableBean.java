package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.tariffservices.service.AgentType;
import zw.co.esolutions.ewallet.tariffservices.service.CustomerClass;
import zw.co.esolutions.ewallet.tariffservices.service.EWalletException_Exception;
import zw.co.esolutions.ewallet.tariffservices.service.TariffStatus;
import zw.co.esolutions.ewallet.tariffservices.service.TariffTable;
import zw.co.esolutions.ewallet.tariffservices.service.TariffType;
import zw.co.esolutions.ewallet.tariffservices.service.TransactionType;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;

public class CreateTariffTableBean extends PageCodeBase {
	
	//instances
	private TariffTable tariffTable = new TariffTable();
	private List<SelectItem> tariffTypeMenu;
	private String tariffTypeItem;
	private List<SelectItem> transactionTypeMenu;
	private String transactionTypeItem;
	private List<SelectItem> tariffCustomerClassMenu;
	private String tariffCustomerClassItem;
	private Date effectiveDate;
	private List<SelectItem> bankMenu;
	private String bankItem;
	private List<Bank> bankList;
	private List<SelectItem> ownerTypeMenu;
	private String ownerTypeItem;
	private String tariffTableId;
	
	public CreateTariffTableBean() {
		super();
		this.initializeDateFiels();
	}

	@SuppressWarnings("unchecked")
	public String createTariffTableAction() {
		String report = this.checkAttributes();
		Bank bank = null;
		if(report != null) {
			//Report Error.
			super.setErrorMessage(report);
			return "failure";
		}
		try {
			if(this.getBankItem().equalsIgnoreCase("nothing")) {
				this.setErrorMessage("No banks. An error occured.");
				return "failure";
			}
			if(!"all".equalsIgnoreCase(this.getBankItem())) {
				bank = super.getBankService().findBankById(this.getBankItem());
				report = this.createCommissionTable(this.getTariffTable(), bank);
			} else {
				StringBuffer buffer = new StringBuffer();
				for(Bank bk : this.getBankList()) {
					buffer.append(this.createCommissionTable(this.getTariffTable(), bk));
				}
				report = buffer.toString();
			}
	   } catch (Exception e) {
		e.printStackTrace();
	   }
	     //Report
		if(report != null) {
			//Report Error.
			super.setErrorMessage(report);
			return "failure";
		}
		
		report = bank.getName()+": Commission Table "+this.getTariffTable().getTransactionType().toString().replace("_", " ")+" ["+this.getTariffTable().getTariffType().toString()+" " +
				"- "+this.getTariffTable().getCustomerClass().toString()+"] Successfully Created.";
		super.setInformationMessage(report);
		this.setTariffTable(new TariffTable());
		super.getRequestScope().put("createTariffTable", "createTariffTable");
		super.getRequestScope().put("tariffTableId",this.getTariffTableId());
		super.gotoPage("/admin/viewCommissionTable.jspx");
		this.cleanBean();
	   return "success";
  }
	
/*private String createCommissionTable(TariffTable table, Bank bank) {
   String report = null;
	try {//Setting Dates and Enums and BankId
			table.setBankId(bank.getId());
			table.setCustomerClass(CustomerClass.valueOf(this.getTariffCustomerClassItem()));
			if(this.getOwnerTypeItem().equalsIgnoreCase(AgentType.BANK_BRANCH.toString())) {
				table.setAgentType(AgentType.BANK_BRANCH);
			} else {
				table.setAgentType(AgentType.AGENT);
			}
			table.setTariffType(TariffType.valueOf(this.getTariffTypeItem()));
			table.setTransactionType(TransactionType.valueOf(this.getTransactionTypeItem()));
			table.setEffectiveDate(DateUtil.convertToXMLGregorianCalendar(this.getEffectiveDate()));
			
			if(this.reportDateErrors() != null) {
				super.setErrorMessage(this.reportDateErrors());
				return "failure";
			}
			table = super.getTariffService().createCommissionTable(table, super.getJaasUserName());
			if(table == null || table.getId() == null) {
				report = bank.getName()+": This Commission Table already exists. You can edit it only.";
			}
			
		} catch (EWalletException_Exception e) {
			if(TariffStatus.AWAITING_APPROVAL.toString().equals(e.getMessage())) {
				report = bank.getName()+": Commission Table already existing. Awaiting approval. ";
			} else if(TariffStatus.DRAFT.toString().equals(e.getMessage())) {
				report = bank.getName()+": Commission Table already existing. In Draft status. ";
			} else {
				e.printStackTrace();
				report = PageCodeBase.ERROR_MESSAGE;
			}
		}catch (Exception e) {
			e.printStackTrace();
			report = PageCodeBase.ERROR_MESSAGE;
			
		} 
		
		//Report
		if(report != null) {
			//Report Error.
			super.setErrorMessage(report);
			return "failure";
		}
		//super.getRequestScope().put("createTariffTable", "createTariffTable");
		report = bank.getName()+": Commission Table "+table.getTransactionType().toString().replace("_", " ")+" ["+table.getTariffType().toString()+" " +
				"- "+table.getCustomerClass().toString()+"] Successfully Created.";
		super.setInformationMessage(report);
		this.setTariffTable(new TariffTable());
		return "success";
	}*/
	
	private String createCommissionTable(TariffTable table, Bank bank) {
		   String report = null;
			try {//Setting Dates and Enums and BankId
					table.setBankId(bank.getId());
					table.setCustomerClass(CustomerClass.valueOf(this.getTariffCustomerClassItem()));
					if(this.getOwnerTypeItem().equalsIgnoreCase(AgentType.BANK_BRANCH.toString()) ||
							this.getTransactionTypeItem().equalsIgnoreCase(TransactionType.AGENT_CASH_DEPOSIT.name())) {
						table.setAgentType(AgentType.BANK_BRANCH);
					
					}else {
						table.setAgentType(AgentType.AGENT);
					}
					table.setTariffType(TariffType.valueOf(this.getTariffTypeItem()));
					table.setTransactionType(TransactionType.valueOf(this.getTransactionTypeItem()));
					table.setEffectiveDate(DateUtil.convertToXMLGregorianCalendar(this.getEffectiveDate()));
					
					if(this.reportDateErrors() != null) {
						return this.reportDateErrors();
					}
					table = super.getTariffService().createCommissionTable(table, super.getJaasUserName());
					if(table == null || table.getId() == null) {
						report = bank.getName()+": This Commission Table already exists. You can edit it only.";
					} else {
						this.setTariffTableId(table.getId());
						this.setTariffTable(table);
					}
					
				} catch (EWalletException_Exception e) {
					if(TariffStatus.AWAITING_APPROVAL.toString().equals(e.getMessage())) {
						report = bank.getName()+": Commission Table already existing. Awaiting approval. ";
					} else if(TariffStatus.DRAFT.toString().equals(e.getMessage())) {
						report = bank.getName()+": Commission Table already existing. In Draft status. ";
					} else {
						e.printStackTrace();
						report = PageCodeBase.ERROR_MESSAGE;
					}
				}catch (Exception e) {
					e.printStackTrace();
					report = PageCodeBase.ERROR_MESSAGE;
					
				} 
				
				return report;
			}
	
	
	public String cancelTariffTableCreation() {
		this.setTariffTable(new TariffTable());
		this.initializeDateFiels();
		super.gotoPage("/admin/adminHome.jspx");
		return "success";
	}
	
		
	/*
	 * Method for missing fields
	 */
	private String checkAttributes(){
		StringBuffer buffer = new StringBuffer("The following values are required: ");
		int length = 0;
		if(this.getTariffTypeItem().equalsIgnoreCase("none")) {
			buffer.append("Tariff Type, ");
		}  if(this.getTariffCustomerClassItem().equalsIgnoreCase("none")) {
			buffer.append("Account Class, ");
		}if(this.getTransactionTypeItem().equalsIgnoreCase("none")) {
			buffer.append("Transaction Type, ");
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
	
	private void initializeDateFiels() {
		if(this.getTariffTable().getEffectiveDate() == null || this.getTariffTable().getEndDate() == null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			this.setEffectiveDate(cal.getTime());
			
		}
	}
	
	private String reportDateErrors() {
		String report = null;
		if(!DateUtil.isDayBeforeOrEqual(DateUtil.getBeginningOfDay(new Date()),
				DateUtil.getBeginningOfDay(this.getEffectiveDate()))) {
			//report = "Effective Date must be a present or future date.";
			
		}
		return report;
		
	}

	public void setTariffTable(TariffTable tariffTable) {
		this.tariffTable = tariffTable;
	}

	public TariffTable getTariffTable() {
		return tariffTable;
	}

	public List<SelectItem> getTariffTypeMenu() {
		if(this.tariffTypeMenu == null) {
			this.tariffTypeMenu = new ArrayList<SelectItem>();
			this.tariffTypeMenu.add(new SelectItem("none", "--select--"));
			for(TariffType type : TariffType.values()) {
				this.tariffTypeMenu.add(new SelectItem(type.toString(), type.toString().replace("_", " ")));
			}
		}
		return tariffTypeMenu;
	}

	public void setTariffTypeMenu(List<SelectItem> tariffTypeMenu) {
		this.tariffTypeMenu = tariffTypeMenu;
	}

	public String getTariffTypeItem() {
		return tariffTypeItem;
	}

	public void setTariffTypeItem(String tariffTypeItem) {
		this.tariffTypeItem = tariffTypeItem;
	}

	public List<SelectItem> getTransactionTypeMenu() {
		if(this.transactionTypeMenu == null) {
			this.transactionTypeMenu = new ArrayList<SelectItem>();
			this.transactionTypeMenu.add(new SelectItem("none", "--select--"));
			for(TransactionType trans : super.getBranchTariffTnxs()) {
				
					if(TransactionType.BALANCE.equals(trans)) {
						this.transactionTypeMenu.add(new SelectItem(trans.toString(), TransactionType.BALANCE_REQUEST.toString().replace("_", " ")));
					} else {
						this.transactionTypeMenu.add(new SelectItem(trans.toString(), trans.toString().replace("_", " ")));
					}
				
			}
		}
		return transactionTypeMenu;
	}

	public void setTransactionTypeMenu(List<SelectItem> transactionTypeMenu) {
		this.transactionTypeMenu = transactionTypeMenu;
	}

	public String getTransactionTypeItem() {
		return transactionTypeItem;
	}

	public void setTransactionTypeItem(String transactionTypeItem) {
		this.transactionTypeItem = transactionTypeItem;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public List<SelectItem> getTariffCustomerClassMenu() {
		if(this.tariffCustomerClassMenu == null) {
			this.tariffCustomerClassMenu = new ArrayList<SelectItem>();
			this.tariffCustomerClassMenu.add(new SelectItem("none", "--select--"));
			for(CustomerClass ac : CustomerClass.values()) {
				if(CustomerClass.AGENT.equals(ac)) {
					continue;
				}
				this.tariffCustomerClassMenu.add(new SelectItem(ac.toString(), ac.toString()));
			}
		}
		return tariffCustomerClassMenu;
	}

	public void setTariffCustomerClassMenu(List<SelectItem> tariffCustomerClassMenu) {
		this.tariffCustomerClassMenu = tariffCustomerClassMenu;
	}

	public String getTariffCustomerClassItem() {
		return tariffCustomerClassItem;
	}

	public void setTariffCustomerClassItem(String tariffCustomerClassItem) {
		this.tariffCustomerClassItem = tariffCustomerClassItem;
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
				//this.bankMenu.add(new SelectItem("all", "ALL"));
			}
		}
		return bankMenu;
	}

	public void setBankItem(String bankItem) {
		this.bankItem = bankItem;
	}

	public String getBankItem() {
		if(this.bankItem == null) {
			try {
				Profile p = super.getProfileService().getProfileByUserName(super.getJaasUserName());
				if(p != null) {
					this.bankItem = super.getBankService().findBankBranchById(p.getBranchId()).getBank().getId();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
	
	public void handleTransactionTypeValueChange(ValueChangeEvent event) {
		String item = (String)event.getNewValue();
		if(item != null) {
			try {
				if(item.equalsIgnoreCase(AgentType.BANK_BRANCH.toString())) {
					
						this.setTransactionTypeMenu(new ArrayList<SelectItem>());
						this.getTransactionTypeMenu().add(new SelectItem("", "--select--"));
						for(TransactionType trans : super.getBranchTariffTnxs()) {
								if(TransactionType.BALANCE.equals(trans)) {
									this.transactionTypeMenu.add(new SelectItem(trans.toString(), TransactionType.BALANCE_REQUEST.toString().replace("_", " ")));
								} else { 
									this.getTransactionTypeMenu().add(new SelectItem(trans.toString(), trans.toString().replace("_", " ")));
								}
							
						}
						
						// Filter Customer Classes
						this.setTariffCustomerClassMenu(new ArrayList<SelectItem>());
						this.getTariffCustomerClassMenu().add(new SelectItem("none", "--select--"));
						for(CustomerClass ac : CustomerClass.values()) {
							if(CustomerClass.AGENT.equals(ac)) {
								continue;
							}
							this.getTariffCustomerClassMenu().add(new SelectItem(ac.toString(), ac.toString()));
						}
					
				} else if(item.equalsIgnoreCase(AgentType.AGENT.toString())) {
					
					this.setTransactionTypeMenu(new ArrayList<SelectItem>());
					this.getTransactionTypeMenu().add(new SelectItem("", "--select--"));
					for(TransactionType trans : super.getAgentCustomerChargesTnxs()) {
							if(TransactionType.BALANCE.equals(trans)) {
								this.transactionTypeMenu.add(new SelectItem(trans.toString(), TransactionType.BALANCE_REQUEST.toString().replace("_", " ")));
							} else { 
								this.getTransactionTypeMenu().add(new SelectItem(trans.toString(), trans.toString().replace("_", " ")));
							}
						
					}
					
					// Filter Customer Classes
					this.setTariffCustomerClassMenu(new ArrayList<SelectItem>());
					this.getTariffCustomerClassMenu().add(new SelectItem("none", "--select--"));
					for(CustomerClass ac : CustomerClass.values()) {
						if(CustomerClass.AGENT.equals(ac)) {
							continue;
						}
						this.getTariffCustomerClassMenu().add(new SelectItem(ac.toString(), ac.toString()));
					}
				
			   }else if(item.equalsIgnoreCase(EWalletConstants.AGENT_COMMISSION)) {
					
					this.setTransactionTypeMenu(new ArrayList<SelectItem>());
					this.getTransactionTypeMenu().add(new SelectItem("", "--select--"));
					for(TransactionType trans : super.getAgentCommissionEarnedTnxs()) {
							if(TransactionType.BALANCE.equals(trans)) {
								this.transactionTypeMenu.add(new SelectItem(trans.toString(), TransactionType.BALANCE_REQUEST.toString().replace("_", " ")));
							} else { 
								this.getTransactionTypeMenu().add(new SelectItem(trans.toString(), trans.toString().replace("_", " ")));
							}
						
					}
					
					// Filter Customer Classes
					this.setTariffCustomerClassMenu(new ArrayList<SelectItem>());
					for(CustomerClass ac : CustomerClass.values()) {
						if(CustomerClass.AGENT.equals(ac)) {
							this.getTariffCustomerClassMenu().add(new SelectItem(ac.toString(), ac.toString()));
						}
					}
					
					this.setTariffTypeMenu(new ArrayList<SelectItem>());
					this.tariffTypeMenu.add(new SelectItem("none", "--select--"));
					for(TariffType type : TariffType.values()) {
						if(TariffType.FIXED_AMOUNT.equals(type) || TariffType.SCALED.equals(type) ||
								TariffType.FIXED_PERCENTAGE.equals(type)) {
							this.tariffTypeMenu.add(new SelectItem(type.toString(), type.toString().replace("_", " ")));
						}
					}
				
			   }else if(item.equalsIgnoreCase(EWalletConstants.AGENT_CHARGE)) {
					
					this.setTransactionTypeMenu(new ArrayList<SelectItem>());
					this.getTransactionTypeMenu().add(new SelectItem("", "--select--"));
					for(TransactionType trans : super.getAgentChargesTnxs()) {
							if(TransactionType.BALANCE.equals(trans)) {
								this.transactionTypeMenu.add(new SelectItem(trans.toString(), TransactionType.BALANCE_REQUEST.toString().replace("_", " ")));
							} else { 
								this.getTransactionTypeMenu().add(new SelectItem(trans.toString(), trans.toString().replace("_", " ")));
							}
						
					}
					
					// Filter Customer Classes
					this.setTariffCustomerClassMenu(new ArrayList<SelectItem>());
					for(CustomerClass ac : CustomerClass.values()) {
						if(CustomerClass.AGENT.equals(ac)) {
							this.getTariffCustomerClassMenu().add(new SelectItem(ac.toString(), ac.toString()));
						}
					}
				
			   }
				
				if(!item.equalsIgnoreCase(EWalletConstants.AGENT_COMMISSION)) {
					this.setTariffTypeMenu(null); //Set to null so that it loads the default for Charges
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public List<SelectItem> getOwnerTypeMenu() {
		if(this.ownerTypeMenu == null) {
			this.ownerTypeMenu = new ArrayList<SelectItem>();
			this.ownerTypeMenu.add(new SelectItem(AgentType.BANK_BRANCH.toString(), " BANK - Charges "));
			this.ownerTypeMenu.add(new SelectItem(EWalletConstants.AGENT_CHARGE, " AGENT - Charges "));
			this.ownerTypeMenu.add(new SelectItem(AgentType.AGENT.toString(), " AGENT CUSTOMER - Charges "));
			this.ownerTypeMenu.add(new SelectItem(EWalletConstants.AGENT_COMMISSION, " AGENT - Commission "));
			
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

	public void setTariffTableId(String tariffTableId) {
		this.tariffTableId = tariffTableId;
	}

	public String getTariffTableId() {
		return tariffTableId;
	}
	
	private void cleanBean() {
		tariffTable = new TariffTable();
		tariffTypeMenu = null;
		tariffTypeItem = null;
		transactionTypeMenu = null;
		transactionTypeItem = null;
		tariffCustomerClassMenu = null;
		tariffCustomerClassItem = null;
		effectiveDate = null;
		bankMenu = null;
		bankItem = null;
		bankList = null;
		ownerTypeMenu = null;
		ownerTypeItem = null;
		tariffTableId = null;
	}
	
}
