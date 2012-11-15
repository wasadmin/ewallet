package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.tariffservices.service.AgentType;
import zw.co.esolutions.ewallet.tariffservices.service.CustomerClass;
import zw.co.esolutions.ewallet.tariffservices.service.EWalletException_Exception;
import zw.co.esolutions.ewallet.tariffservices.service.Tariff;
import zw.co.esolutions.ewallet.tariffservices.service.TariffStatus;
import zw.co.esolutions.ewallet.tariffservices.service.TariffTable;
import zw.co.esolutions.ewallet.tariffservices.service.TariffType;
import zw.co.esolutions.ewallet.tariffservices.service.TransactionType;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;

public class EditTariffTableBean extends PageCodeBase {
	
	
	//instances
	private TariffTable tariffTable;
	private List<SelectItem> tariffTypeMenu;
	private String tariffTypeItem;
	private List<SelectItem> transactionTypeMenu;
	private String transactionTypeItem;
	private List<SelectItem> agentTypeMenu;
	private String agentTypeItem;
	private List<SelectItem> tariffCustomerClassMenu;
	private String tariffCustomerClassItem;
	private Date effectiveDate;
	private String tariffTableId;
	private List<SelectItem> bankMenu;
	private String bankItem;
	private List<Bank> bankList;
	private List<SelectItem> ownerTypeMenu;
	private String ownerTypeItem;

	
	public EditTariffTableBean() {
		super();
		if(this.getTariffTableId() == null) {
			this.setTariffTableId((String)super.getRequestScope().get("tariffTableId"));
		}
		//this.initializeDateFiels();
	}
	
	private void cleanBean() {
		tariffTable = null;
		tariffTypeMenu = null;
		tariffTypeItem = null;
		transactionTypeMenu = null;
		transactionTypeItem = null;
		agentTypeMenu = null;
		agentTypeItem = null;
		tariffCustomerClassMenu = null;
		tariffCustomerClassItem = null;
		effectiveDate = null;
		tariffTableId = null;
		bankMenu = null;
		bankItem = null;
		bankList = null;
		ownerTypeItem = null;
		ownerTypeMenu =  null;
	}

	@SuppressWarnings("unchecked")
	public String editTariffTableAction() {
		String report = this.checkAttributes();
		@SuppressWarnings("unused")
		List<Tariff> tariffs = null;
		if(report != null) {
			//Report Error.
			super.setErrorMessage(report);
			return "failure";
		}
		try {
			
			TariffTable tb = super.getTariffService().findTariffTableById(this.getTariffTableId());
			
			//Setting Dates and Enums
			this.getTariffTable().setOldTariffTableId(tb.getId());
			this.getTariffTable().setCustomerClass(CustomerClass.valueOf(this.getTariffCustomerClassItem()));
			this.getTariffTable().setAgentType(AgentType.BANK_BRANCH);
			this.getTariffTable().setStatus(TariffStatus.AWAITING_APPROVAL);
			this.getTariffTable().setTariffType(TariffType.valueOf(this.getTariffTypeItem()));
			this.getTariffTable().setTransactionType(TransactionType.valueOf(this.getTransactionTypeItem()));
			this.getTariffTable().setEffectiveDate(DateUtil.convertToXMLGregorianCalendar(this.getEffectiveDate()));
			this.getTariffTable().setBankId(this.getBankItem());
			if(this.reportDateErrors() != null) {
				super.setErrorMessage(this.reportDateErrors());
				return "failure";
			}
			
			TariffTable tab = super.getTariffService().editCommissionTable(this.getTariffTable(), super.getJaasUserName());
			this.setTariffTable(tab);
			if(tab == null || tab.getId() == null) {
				report = "This Commission Table exists.";
			} 
			this.setTariffTableId(this.getTariffTableId());
		} catch (EWalletException_Exception e) {
			e.printStackTrace();
			if(TariffStatus.AWAITING_APPROVAL.toString().equalsIgnoreCase(e.getMessage())) {
				report = "This Commission Table already exists, awaiting approval.";
			}
			else if(TariffStatus.DRAFT.toString().equalsIgnoreCase(e.getMessage())) {
				report = "This Commission Table already exists. Draft status.";
			} else {
				report = PageCodeBase.ERROR_MESSAGE;
			}
		} catch (Exception e) {
			e.printStackTrace();
			report = PageCodeBase.ERROR_MESSAGE;
			
		} 
		
		//Report
		if(report != null) {
			//Report Error.
			super.setErrorMessage(report);
			return "failure";
		}
		report = "Commission Table Successfully Edited.";
		this.setInformationMessage(report);
		super.getRequestScope().put("editTariffTable", report);
		super.getRequestScope().put("tariffTableId", this.getTariffTable().getId());
		super.gotoPage("/admin/viewCommissionTable.jspx");
		this.cleanBean();
		return "success";
	}
	
	@SuppressWarnings("unchecked")
	public String cancelTariffTableEditing() {
		super.getRequestScope().put("tariffTableId", this.getTariffTableId());
		super.gotoPage("/admin/viewCommissionTable.jspx");
		this.cleanBean();
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
		}if(this.getAgentTypeItem().equalsIgnoreCase("none")) {
			buffer.append("Agent Type, ");
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
	
	/*private void initializeDateFiels() {
		if(this.getTariffTable().getEffectiveDate() == null || this.getTariffTable().getEndDate() == null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			this.setEffectiveDate(cal.getTime());
			
		}
	}*/
	
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
		if(this.tariffTable == null && this.getTariffTableId() != null) {
		   try {
			     this.tariffTable = super.getTariffService().findTariffTableById(this.getTariffTableId());
		   } catch (Exception e) {
			
		   }
		} if(this.tariffTable == null) {
			this.tariffTable = new TariffTable();
		}
		return tariffTable;
	}

	public List<SelectItem> getTariffTypeMenu() {
		if(this.tariffTypeMenu == null) {
			this.tariffTypeMenu = new ArrayList<SelectItem>();
			this.tariffTypeMenu.add(new SelectItem("none", "--select--"));
			if(this.isAgentCommission()) {
				for(TariffType type : TariffType.values()) {
					if(TariffType.FIXED_AMOUNT.equals(type) || TariffType.SCALED.equals(type)) {
						this.tariffTypeMenu.add(new SelectItem(type.toString(), type.toString().replace("_", " ")));
					}
				}
			} else {
			    for(TariffType type : TariffType.values()) {
				   this.tariffTypeMenu.add(new SelectItem(type.toString(), type.toString().replace("_", " ")));
			    }
			}
		}
		return tariffTypeMenu;
	}

	public void setTariffTypeMenu(List<SelectItem> tariffTypeMenu) {
		this.tariffTypeMenu = tariffTypeMenu;
	}

	public String getTariffTypeItem() {
		if(this.tariffTypeItem == null && this.getTariffTable().getId() != null) {
			this.tariffTypeItem = this.getTariffTable().getTariffType().toString();
		}
		return tariffTypeItem;
	}

	public void setTariffTypeItem(String tariffTypeItem) {
		this.tariffTypeItem = tariffTypeItem;
	}

	public List<SelectItem> getTransactionTypeMenu() {
		if(this.transactionTypeMenu == null) {
			this.transactionTypeMenu = new ArrayList<SelectItem>();
			this.transactionTypeMenu.add(new SelectItem("none", "--select--"));
			for(TransactionType trans : this.getApplicableTxnTypes()) {
				
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
		if(this.transactionTypeItem == null && this.getTariffTable().getId() != null) {
			this.transactionTypeItem = this.getTariffTable().getTransactionType().toString();
		}
		return transactionTypeItem;
	}

	public void setTransactionTypeItem(String transactionTypeItem) {
		this.transactionTypeItem = transactionTypeItem;
	}

	public Date getEffectiveDate() {
		if(this.effectiveDate == null && this.getTariffTable().getId() != null) {
			this.effectiveDate = DateUtil.convertToDate(this.getTariffTable().getEffectiveDate());
			
			//This is because of an ICE Faces Bug, it's subtracting a Day instead of showing actual date
			this.effectiveDate = DateUtil.addDays(this.effectiveDate, 1);
		}
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	
	public List<SelectItem> getAgentTypeMenu() {
		if(this.agentTypeMenu == null) {
			this.agentTypeMenu = new ArrayList<SelectItem>();
			this.agentTypeMenu.add(new SelectItem("none", "--select--"));
			for(AgentType type : AgentType.values()) {
				this.agentTypeMenu.add(new SelectItem(type.toString(), type.toString().replace("_", " ")));
			}
		}
		return agentTypeMenu;
	}

	public void setAgentTypeMenu(List<SelectItem> agentTypeMenu) {
		this.agentTypeMenu = agentTypeMenu;
	}

	public String getAgentTypeItem() {
		if(this.agentTypeItem == null && this.getTariffTable().getId() != null) {
			this.agentTypeItem = this.getTariffTable().getAgentType().toString();
		}
		return agentTypeItem;
	}

	public void setAgentTypeItem(String agentTypeItem) {
		this.agentTypeItem = agentTypeItem;
	}

	
	public List<SelectItem> getTariffCustomerClassMenu() {
		if(this.tariffCustomerClassMenu == null) {
			this.tariffCustomerClassMenu = new ArrayList<SelectItem>();
			this.tariffCustomerClassMenu.add(new SelectItem("none", "--select--"));
			if(this.isOwnerAgent()) {
				for(CustomerClass ac : CustomerClass.values()) {
					if(CustomerClass.AGENT.equals(ac)) {
						this.tariffCustomerClassMenu.add(new SelectItem(ac.toString(), ac.toString()));
					}
				}
			} else {
				for(CustomerClass ac : CustomerClass.values()) {
					if(!CustomerClass.AGENT.equals(ac)) {
						this.tariffCustomerClassMenu.add(new SelectItem(ac.toString(), ac.toString()));
					}
				}
			}
			
		}
		return tariffCustomerClassMenu;
	}

	public void setTariffCustomerClassMenu(List<SelectItem> tariffCustomerClassMenu) {
		this.tariffCustomerClassMenu = tariffCustomerClassMenu;
	}

	public String getTariffCustomerClassItem() {
		if(this.tariffCustomerClassItem == null && this.getTariffTable().getId() != null) {
			this.tariffCustomerClassItem = this.getTariffTable().getCustomerClass().toString();
		}
		return tariffCustomerClassItem;
	}

	public void setTariffCustomerClassItem(String tariffCustomerClassItem) {
		this.tariffCustomerClassItem = tariffCustomerClassItem;
	}

	public void setTariffTableId(String tariffTableId) {
		this.tariffTableId = tariffTableId;
	}

	public String getTariffTableId() {
		if(tariffTableId == null) {
			this.tariffTableId = (String)super.getRequestScope().get("tariffTableId");
		}
		return tariffTableId;
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
				this.bankMenu.add(new SelectItem("none", "--select--"));
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
			if(this.getTariffTable() != null) {
				this.bankItem = this.getTariffTable().getBankId();
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
						if(TariffType.FIXED_AMOUNT.equals(type) || TariffType.SCALED.equals(type)) {
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
					this.setTariffTypeMenu(new ArrayList<SelectItem>()); 
					for(TariffType type : TariffType.values()) {
						   this.tariffTypeMenu.add(new SelectItem(type.toString(), type.toString().replace("_", " ")));
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
		if(this.ownerTypeItem == null && this.getTariffTable().getId() != null) {
			for(TransactionType txnType : super.getAgentChargesTnxs()) {
				if(this.getTariffTable().getTransactionType().equals(txnType)) {
					return EWalletConstants.AGENT_CHARGE;
				}
			}
			for(TransactionType txnType : super.getAgentCustomerChargesTnxs()) {
				if(this.getTariffTable().getTransactionType().equals(txnType)) {
					return AgentType.AGENT.toString();
				}
			}
			for(TransactionType txnType : super.getAgentCommissionEarnedTnxs()) {
				if(this.getTariffTable().getTransactionType().equals(txnType)) {
					return EWalletConstants.AGENT_COMMISSION;
				}
			}for(TransactionType txnType : super.getBranchTariffTnxs()) {
				if(this.getTariffTable().getTransactionType().equals(txnType)) {
					return AgentType.BANK_BRANCH.toString();
				}
			}
			
		}
		return ownerTypeItem;
	}

	public void setOwnerTypeItem(String ownerTypeItem) {
		this.ownerTypeItem = ownerTypeItem;
	}
	
	private TransactionType[] getApplicableTxnTypes() {
		if(this.getTariffTable().getId() != null) {
			for(TransactionType txnType : super.getAgentChargesTnxs()) {
				if(this.getTariffTable().getTransactionType().equals(txnType)) {
					return super.getAgentChargesTnxs();
				}
			}
			for(TransactionType txnType : super.getAgentCustomerChargesTnxs()) {
				if(this.getTariffTable().getTransactionType().equals(txnType)) {
					return super.getAgentCustomerChargesTnxs();
				}
			}
			for(TransactionType txnType : super.getAgentCommissionEarnedTnxs()) {
				if(this.getTariffTable().getTransactionType().equals(txnType)) {
					return super.getAgentCommissionEarnedTnxs();
				}
			}for(TransactionType txnType : super.getBranchTariffTnxs()) {
				if(this.getTariffTable().getTransactionType().equals(txnType)) {
					return super.getBranchTariffTnxs();
				}
			}
			
		}
		return null;
	}
	
	private boolean isAgentCommission() {
		if(this.getTariffTable().getId() != null) {
			for(TransactionType txnType : super.getAgentChargesTnxs()) {
				if(this.getTariffTable().getTransactionType().equals(txnType)) {
					return false;
				}
			}
			for(TransactionType txnType : super.getAgentCustomerChargesTnxs()) {
				if(this.getTariffTable().getTransactionType().equals(txnType)) {
					return false;
				}
			}
			for(TransactionType txnType : super.getAgentCommissionEarnedTnxs()) {
				if(this.getTariffTable().getTransactionType().equals(txnType)) {
					return true;
				}
			}for(TransactionType txnType : super.getBranchTariffTnxs()) {
				if(this.getTariffTable().getTransactionType().equals(txnType)) {
					return false;
				}
			}
			
		}
		return false;
	}
	
	private boolean isOwnerAgent() {
		if(this.getTariffTable().getId() != null) {
			for(TransactionType txnType : super.getAgentChargesTnxs()) {
				if(this.getTariffTable().getTransactionType().equals(txnType)) {
					return true;
				}
			}
			for(TransactionType txnType : super.getAgentCustomerChargesTnxs()) {
				if(this.getTariffTable().getTransactionType().equals(txnType)) {
					return false;
				}
			}
			for(TransactionType txnType : super.getAgentCommissionEarnedTnxs()) {
				if(this.getTariffTable().getTransactionType().equals(txnType)) {
					return true;
				}
			}for(TransactionType txnType : super.getBranchTariffTnxs()) {
				if(this.getTariffTable().getTransactionType().equals(txnType)) {
					return false;
				}
			}
			
		}
		return false;
	}

}


