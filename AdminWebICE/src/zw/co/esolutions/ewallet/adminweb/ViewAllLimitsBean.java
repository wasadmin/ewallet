package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.limitservices.service.BankAccountClass;
import zw.co.esolutions.ewallet.limitservices.service.Limit;
import zw.co.esolutions.ewallet.limitservices.service.TransactionType;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;

public class ViewAllLimitsBean extends PageCodeBase {
	
	private List<SelectItem> searchMenu;
	private String searchItem;
	private List<Limit> limitList;
	private List<SelectItem> optionsMenu;
	private String optionsItem;
	private boolean disableOptions;
	private List<SelectItem> bankMenu;
	private String bankItem;
	private List<Bank> bankList;
	
	public ViewAllLimitsBean() {
		super();
		
	}

	public void handleSearchMenuValueChange(ValueChangeEvent event) {
		String value = null;
		try {
			value = (String)event.getNewValue();
			if(!value.equalsIgnoreCase("none")) {
				
				
				if(value.equalsIgnoreCase("accountClass")) {
					this.setOptionsMenu(new ArrayList<SelectItem>());
					this.setDisableOptions(false);
					
					for(BankAccountClass accountClassClass : BankAccountClass.values()) {
						if(!(BankAccountClass.SYSTEM.equals(accountClassClass))) {
							this.getOptionsMenu().add(new SelectItem(accountClassClass.toString(), accountClassClass.toString()));
						}
					}
				} if (value.equalsIgnoreCase("type")) {
					this.setOptionsMenu(new ArrayList<SelectItem>());
					this.setDisableOptions(false);
					
					for(TransactionType trans : super.getLimitTnxs()) {
						
							if(TransactionType.BALANCE.equals(trans)) {
								this.getOptionsMenu().add(new SelectItem(trans.toString(), "ACCOUNT BALANCE"));
							} else {
								this.getOptionsMenu().add(new SelectItem(trans.toString(), trans.toString().replace("_", " ")));
							}
						
					}
					
				} if (value.equalsIgnoreCase("effective")) {
					this.setDisableOptions(true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String search() {
		String report = null;
		try {
			report = this.checkAttributes();
			if(report != null) {
				super.setErrorMessage(report);
				return "failure";
			}
			String bankId = "";
			if(!this.getBankItem().equalsIgnoreCase("all")) {
				bankId = this.getBankItem();
			}
			if(this.getSearchItem().equalsIgnoreCase("accountClass")) {
				this.setLimitList(super.getLimitService().getLimitByAccountClassAndBankId(BankAccountClass.valueOf(getOptionsItem()), bankId));
				
			} if(this.getSearchItem().equalsIgnoreCase("type")) {
				this.setLimitList(super.getLimitService().getLimitByTypeAndBankId(TransactionType.valueOf(this.getOptionsItem()), bankId));
				
			} if (this.getSearchItem().equalsIgnoreCase("effective")) {
				this.setLimitList(super.getLimitService().getEffectiveLimitsByBankId(DateUtil.convertToXMLGregorianCalendar(new Date()), bankId));
				
			} 
			if(this.getLimitList() == null || this.getLimitList().isEmpty()) {
				super.setErrorMessage("No limits found for this search.");
				return "failure";
			}
			
		} catch (Exception e) {
			super.setErrorMessage("Error Occurred. Contact Adminstrator. ");
			return "failure";
		}
		super.setInformationMessage(this.getLimitList().size()+" Limit(s) found.");
		return "success";
	}
	
	public String viewAll() {
		try {
			System.out.println(".......>>>>>>>>>>>>>>>>>> Bank Id = "+this.getBankId());
			this.setLimitList(super.getLimitService().getAllLimitsByBankId(""));
			if(this.getLimitList() == null || this.getLimitList().isEmpty()) {
				super.setInformationMessage("No Limits found.");
				return "failure";
			}
			
		} catch (Exception e) {
			super.setErrorMessage("Error Occurred. Contact Adminstrator. ");
			return "failure";
		}
		super.setInformationMessage(this.getLimitList().size()+" Limit(s) found.");
		return "success";
	}
	
	@SuppressWarnings("unchecked")
	public String viewLimit() {
		super.getRequestScope().put("limitId", super.getRequestParam().get("limitId"));
		this.limitList = null;
		return "success";
	}
	
		
	/*
	 * Method for missing fields
	 */
	private String checkAttributes(){
		StringBuffer buffer = new StringBuffer("The following values are required: ");
		int length = 0;
		if(this.getSearchItem().equalsIgnoreCase("none")) {
			buffer.append("Search Criterion, ");
		} if(!this.getSearchItem().equalsIgnoreCase("none") && !this.getSearchItem().equalsIgnoreCase("effective")) {
			if(this.getOptionsItem().equals("none")) {
				buffer.append("Options, ");
			}
		}
		length = buffer.toString().length();
				
		if(length > 40) {
			buffer.replace(length-2, length, " ");
			return buffer.toString();
		}
		return null;
	}

	public List<SelectItem> getSearchMenu() {
		if(this.searchMenu == null) {
			this.searchMenu = new ArrayList<SelectItem>();
			this.searchMenu.add(new SelectItem("none", "--select--"));
			this.searchMenu.add(new SelectItem("accountClass", " Account Class"));
			this.searchMenu.add(new SelectItem("type", " Type of Limit"));
			this.searchMenu.add(new SelectItem("effective", " Effective Limits"));
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

	
	public List<Limit> getLimitList() {
		return limitList;
	}

	public void setLimitList(List<Limit> limitList) {
		this.limitList = limitList;
	}

	public List<SelectItem> getOptionsMenu() {
		if(optionsMenu == null) {
			this.optionsMenu = new ArrayList<SelectItem>();
			this.optionsMenu.add(new SelectItem("none", "--select--"));
			this.setDisableOptions(true);
		}
		return optionsMenu;
	}

	public void setOptionsMenu(List<SelectItem> optionsMenu) {
		this.optionsMenu = optionsMenu;
	}

	public String getOptionsItem() {
		return optionsItem;
	}

	public void setOptionsItem(String optionsItem) {
		this.optionsItem = optionsItem;
	}

	public void setDisableOptions(boolean disableOptions) {
		this.disableOptions = disableOptions;
	}

	public boolean isDisableOptions() {
		return disableOptions;
	}

	private String getBankId() {
		String bankId = null;
		Profile profile = null;
		Bank bank = null;
		try {
			profile = super.getProfileService().getProfileByUserName(super.getJaasUserName());
			bank = super.getBankService().findBankBranchById(profile.getBranchId()).getBank();
			bankId = bank.getId();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return bankId;
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

}
