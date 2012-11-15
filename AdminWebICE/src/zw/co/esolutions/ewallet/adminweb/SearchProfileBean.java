package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.profileservices.service.Profile;

public class SearchProfileBean extends PageCodeBase {
	
	private List<SelectItem> criteriaList;
	private List<SelectItem> branchList;
	private String selectedCriteria;
	private String selectedBranch;
	private String searchField;
	private List<Profile> profileList;
	private boolean showBranchSelection;
		
	public List<SelectItem> getBranchList() {
		if(branchList==null){
			//Profile profile = super.getProfileService().getProfileByUserName(super.getJaasUserName());
			//Bank bank = super.getBankService().findBankBranchById(profile.getBranchId()).getBank();
			List<BankBranch> bankBranches = super.getBankService().getBankBranch();
			branchList = new ArrayList<SelectItem>();
			branchList.add(new SelectItem("","--Select--"));
			for(BankBranch b:bankBranches){
				branchList.add(new SelectItem(b.getId(),b.getName()+" : "+b.getBank().getName()));
			}
		}
		return branchList;
	}
	public void setBranchList(List<SelectItem> branchList) {
		this.branchList = branchList;
	}
	public List<SelectItem> getCriteriaList() {
		if (criteriaList == null) {
			criteriaList = new ArrayList<SelectItem>();
			criteriaList.add(new SelectItem("userName", "USERNAME"));
			criteriaList.add(new SelectItem("lastName", "LASTNAME"));
			criteriaList.add(new SelectItem("userRole", "USER ROLE"));
			criteriaList.add(new SelectItem("branch", "BRANCH"));			
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
	public List<Profile> getProfileList() {
		if (profileList == null) {
			profileList = new ArrayList<Profile>();
		}
		return profileList;
	}
	public void setProfileList(List<Profile> profileList) {
		this.profileList = profileList;
	}
	
	public String getSelectedBranch() {
		return selectedBranch;
	}
	public void setSelectedBranch(String selectedBranch) {
		this.selectedBranch = selectedBranch;
	}
	public boolean isShowBranchSelection() {
		return showBranchSelection;
	}
	public void setShowBranchSelection(boolean showBranchSelection) {
		this.showBranchSelection = showBranchSelection;
	}
	public String submit() {
		try {
			
			if ("userName".equals(selectedCriteria)) {
				Profile profile = super.getProfileService().getProfileByUserName(searchField);
				if (profile != null) {
					profileList = new ArrayList<Profile>();
					profileList.add(profile);
				} else {
					profileList = null;
				}
			} else if ("lastName".equals(selectedCriteria)) {
				profileList = super.getProfileService().getProfileByLastName(searchField);
			} else if ("userRole".equals(selectedCriteria)) {
				profileList = super.getProfileService().getProfileByUserRole(searchField);
			}else if("branch".equals(selectedCriteria)){
				if(!selectedBranch.equals("")){
					profileList = super.getProfileService().getProfileByBranchId(selectedBranch);
				}
			}
			
			if (profileList == null || profileList.isEmpty()) {
				profileList = new ArrayList<Profile>();
				super.setErrorMessage("Oops! No results found.");
				return "failure";
			}
		} catch (Exception e) {
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage("Profile(s) found.");
		return "submit";
	}
	
	public String viewAll(){
		try {
			profileList = super.getProfileService().getAllProfiles();
			if (profileList == null || profileList.isEmpty()) {
				profileList = new ArrayList<Profile>();
				super.setErrorMessage("Oops! No results found.");
				return "failure";
			}
		} catch (Exception e) {
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage("Profile(s) found.");
		return "viewAll";
	}
	
	
	
	public String viewProfile() {
		profileList=new ArrayList<Profile>();
		searchField = "";
		return "viewProfile";
	}
	
	public void criteriaAction(ValueChangeEvent event){
		String s = (String)event.getNewValue();
		if(s.equals("branch")){
			this.showBranchSelection=true;
		}else{
			this.showBranchSelection=false;
		}
		profileList = new ArrayList<Profile>();
	}
	
}
