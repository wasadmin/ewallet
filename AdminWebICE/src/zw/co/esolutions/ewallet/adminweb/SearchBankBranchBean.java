package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;

public class SearchBankBranchBean extends PageCodeBase{

	private List<SelectItem> criteriaList;
	private String selectedCriteria;
	private String searchField;
	private List<BankBranch> branchList;
	
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
	public List<BankBranch> getBranchList() {
		if(branchList==null){
			branchList = new ArrayList<BankBranch>();
		}
		return branchList;
	}
	public void setBranchList(List<BankBranch> branchList) {
		this.branchList = branchList;
	}	
	
	public String search(){
		if (searchField != null || !searchField.equals("")) {
			if ("CODE".equals(selectedCriteria)) {
				BankBranch bankBranch = super.getBankService().getBankBranchByCode(searchField);
				branchList = new ArrayList<BankBranch>();
				if(bankBranch!=null){
					branchList.add(bankBranch);
				}
			}else if("NAME".equals(selectedCriteria)) {
				branchList = super.getBankService().getBankBranchByName(searchField);
			}
			
			if(this.getBranchList().size()==0){
				super.setInformationMessage("No results found");
			}
		}else{
			super.setErrorMessage("Search field cannot be blank.");
			return "failure";
		}
		return "searchBankBranch";
	}
	
	public String viewAll(){
		branchList = super.getBankService().getBankBranch();
		if(this.getBranchList().size()==0){
			super.setInformationMessage("No results found");
		}
		return "viewAll";
	}
	
	public String viewBranch(){
		branchList = null;
		super.gotoPage("/admin/viewBankBranch.jspx");
		return "viewBranch";
	}
	
}
