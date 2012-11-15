package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.profileservices.service.Profile;

public class SearchLoggedOnProfileBean extends PageCodeBase{
	
	private List<SelectItem> branchList;
	private String selectedBranch;
	private String searchField;
	private List<Profile> profileList;
		
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

	public String submit() {
		try {
			
			if(selectedBranch.equals("")){
				super.setErrorMessage("Select branch to search from ");
			}else{
//				profileList = super.getProfileService().getLoggedOnProfileByBranchId(selectedBranch);
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
//			profileList = super.getProfileService().getAllLoggedOnProfiles();
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
	
	public String clearLogIn(){
		String id = (String) super.getRequestParam().get("profileId");
		try {
			if (id != null) {
				Profile profile = super.getProfileService().findProfileById(id);
//				profile.setLoggedIn(false);
				profile = super.getProfileService().updateProfile(profile,super.getJaasUserName());
				this.getProfileList();
		
			} else {
				super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
				return "failure";
			}
		} catch (Exception e) {
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage("Profile logged out successfully.");
		
		return "";
	}
}
