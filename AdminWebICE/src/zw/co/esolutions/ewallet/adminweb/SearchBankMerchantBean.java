package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;

import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountType;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.merchantservices.service.BankMerchant;
import zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchant;
import zw.co.esolutions.ewallet.merchantservices.service.Merchant;

public class SearchBankMerchantBean extends PageCodeBase{

	private List<SelectItem> criteriaList;
	private String selectedCriteria;
	private String searchField;
	private List<BankMerchant> bankMerchantList;
	
	public List<SelectItem> getCriteriaList() {
		if (criteriaList == null) {
			criteriaList = new ArrayList<SelectItem>();
			criteriaList.add(new SelectItem("CODE", "CODE"));
			criteriaList.add(new SelectItem("NAME", "NAME"));
		}
		return criteriaList;
	}
	public void setCriteriaList(List<SelectItem> criteriaList) {
		this.criteriaList = criteriaList;
	}
	public String getSelectedCriteria() {
		return selectedCriteria;
	}
	public void setSelectedCriteria(String selectedCriteria) {
		this.selectedCriteria = selectedCriteria;
	}
	public String getSearchField() {
		return searchField;
	}
	public void setSearchField(String searchField) {
		this.searchField = searchField;
	}
	public List<BankMerchant> getBankMerchantList() {
		if(bankMerchantList==null){
			bankMerchantList = new ArrayList<BankMerchant>();
		}
		return bankMerchantList;
	}
	public void setBankMerchantList(List<BankMerchant> bankMerchantList) {
		this.bankMerchantList = bankMerchantList;
	}
	
	public String search(){
		try {
			if(searchField !=null && !searchField.equals("")){
				if("CODE".equals(selectedCriteria)){
					Merchant merchant = super.getMerchantService().getMerchantByShortName(searchField);
					if (merchant != null) {
						bankMerchantList = new ArrayList<BankMerchant>();
						bankMerchantList.addAll(super.getMerchantService().getBankMerchantByMerchantId(merchant.getId()));
					}
				}else if("NAME".equals(selectedCriteria)){
					List<Merchant> merchants = super.getMerchantService().getMerchantByName(searchField.toUpperCase());
					if (merchants != null) {
						for (Merchant m: merchants) {
							bankMerchantList = new ArrayList<BankMerchant>();
							bankMerchantList.addAll(super.getMerchantService().getBankMerchantByMerchantId(m.getId()));
						}
					}
				}
			}else{
				super.setErrorMessage("Search field cannot be blank.");
				return "failure";
			}
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		return "search";
	}
	
	public String listAll() {
		try {
			List<Merchant> merchants = super.getMerchantService().getAllMerchants();
			if (merchants != null) {
				bankMerchantList = new ArrayList<BankMerchant>();
				for (Merchant merchant: merchants) {
					bankMerchantList.addAll(super.getMerchantService().getBankMerchantByMerchantId(merchant.getId()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		return "listAll";
	}
	
	public String deleteBankMerchant() {
		String bankMerchantId = (String) super.getRequestParam().get("bankMerchantId");
		try {
			BankMerchant bankMerchant = super.getMerchantService().findBankMerchantById(bankMerchantId);
			Merchant merchant = bankMerchant.getMerchant();
		//	String branchId = super.getProfileService().getProfileByUserName(super.getJaasUserName()).getBranchId();
		//	String bankId = super.getBankService().findBankBranchById(branchId).getId();
						
			List<CustomerMerchant> customerMerchantList = super.getMerchantService().getCustomerMerchantByBankMerchantId(bankMerchant.getId());
			
			if (customerMerchantList != null) {
				for (CustomerMerchant customerMerchant: customerMerchantList) {
					super.getMerchantService().deleteCustomerMerchant(customerMerchant, super.getJaasUserName());
				}
			}
			
			BankAccount bankMerchantAccount = super.getBankService()
				.getBankAccountByAccountHolderAndTypeAndOwnerType(bankMerchant.getId(), BankAccountType.MERCHANT_SUSPENSE, OwnerType.BANK_MERCHANT, null);
			if (bankMerchantAccount != null) {
				super.getBankService().deleteBankAccount(bankMerchantAccount, super.getJaasUserName());
			}
			
			super.getMerchantService().deleteBankMerchant(bankMerchant, super.getJaasUserName());
			super.getMerchantService().deleteMerchant(merchant, super.getJaasUserName());
			
			List<Merchant> merchants = super.getMerchantService().getAllMerchants();
			if (merchants != null) {
				bankMerchantList = new ArrayList<BankMerchant>();
				for (Merchant m: merchants) {
					bankMerchantList.addAll(super.getMerchantService().getBankMerchantByMerchantId(m.getId()));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage("Merchant has been deleted successfully.");
		return "deleteBankMerchant";
	}
	
}
