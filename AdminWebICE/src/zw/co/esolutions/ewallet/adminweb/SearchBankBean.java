package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;

public class SearchBankBean extends PageCodeBase {
	
	private List<SelectItem> criteriaList;
	private String selectedCriteria;
	private String searchField;
	private List<Bank> bankList;
	
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
	public List<Bank> getBankList() {
		if (bankList == null) {
			bankList = new ArrayList<Bank>();
		}
		return bankList;
	}
	public void setBankList(List<Bank> bankList) {
		this.bankList = bankList;
	}
	
	public String search() {
		try {
			if (searchField != null || !searchField.equals("")) {
				if ("CODE".equals(selectedCriteria)) {
					Bank bank = super.getBankService().getBankByCode(searchField);
					bankList = new ArrayList<Bank>();
					if (bank != null) {
						bankList.add(bank);
						setBankList(getBankList());
					}
				} else if("NAME".equals(selectedCriteria)) {
					List<Bank> bankArray = super.getBankService().getBankByName(searchField);
					if (bankArray != null) {
						setBankList(bankArray);
					} else {
						bankList = new ArrayList<Bank>();
						super.setInformationMessage("No results found.");
					}
				} 
			} else {
				super.setErrorMessage("Search field cannot be blank.");
				return "failure";
			}
		} catch (Exception e) {
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		return "search";
	}
	
	public String listAll() {
		try {
			List<Bank> allBanks = super.getBankService().getBank();
			if (allBanks != null) {
				setBankList(allBanks);
			} else {
				bankList = new ArrayList<Bank>();
				super.setInformationMessage("No results found.");
			}
		} catch (Exception e) {
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		return "listAll";
	}
	
	public String viewBank() {
		return "viewBank";
	}
	
}
