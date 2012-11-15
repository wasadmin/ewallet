package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.Role;

public class EditProfileBean extends PageCodeBase {
	
	private String profileId;
	private Profile profile;
	private BankBranch branch;
	private List<SelectItem> roleList;
	private List<SelectItem> bankList;
	private List<SelectItem> branchList;
	private String selectedBank;
	private String selectedBranch;
	private String selectedRole;
	
	public String getProfileId() {
		if (profileId == null) {
			profileId = (String) super.getRequestParam().get("profileId");
		}
		if (profileId == null) {
			profileId = (String) super.getRequestScope().get("profileId");
		}
		return profileId;
	}
	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}
	public void setProfile(Profile profile) {
		this.profile = profile;
	}
	public Profile getProfile() {
		if (profile == null || profile.getId() == null) {
			if (this.getProfileId() != null) {
				profile = super.getProfileService().findProfileById(profileId);
				setSelectedRole(profile.getRole().getRoleName());
			} else {
				profile = new Profile();
			}
		}
		return profile;
	}
	public void setBranch(BankBranch branch) {
		this.branch = branch;
	}
	public BankBranch getBranch() {
		if (branch == null || branch.getId() == null) {
			if (this.getProfile() != null) {
				try {
					branch = super.getBankService().findBankBranchById(profile.getBranchId());
					setSelectedBank(branch.getBank().getId());
					setSelectedBranch(branch.getId());
				} catch (Exception e) {
					
				}
				
			} else {
				branch = new BankBranch();
			}
		}
		return branch;
	}
	public void setRoleList(List<SelectItem> roleList) {
		this.roleList = roleList;
	}
	public List<SelectItem> getRoleList() {
		if (roleList == null) {
			roleList = new ArrayList<SelectItem>();
			for (Role role: super.getProfileService().getActiveRoles()) {
				roleList.add(new SelectItem(role.getId(), role.getRoleName()));
			}
		}
		return roleList;
	}
	public List<SelectItem> getBankList() {
		if (bankList == null) {
			bankList = new ArrayList<SelectItem>();
			try {
				List<Bank> banks = super.getBankService().getBank();
				if (banks != null) {
					for (Bank bank: banks) {
						bankList.add(new SelectItem(bank.getId(), bank.getName()));
					}
				}
			} catch(Exception e) {
				super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			}
		}
		return bankList;
	}
	public void setBankList(List<SelectItem> bankList) {
		this.bankList = bankList;
	}
	public List<SelectItem> getBranchList() {
		if (branchList == null) {
			branchList = new ArrayList<SelectItem>();
			List<BankBranch> branches = super.getBankService().getBankBranch();
			if (branches != null) {
				for (BankBranch branch: branches) {
					branchList.add(new SelectItem(branch.getId(), branch.getName()));
				}
			}
		}
		return branchList;
	}
	public void setBranchList(List<SelectItem> branchList) {
		this.branchList = branchList;
	}
	
	public void setSelectedBank(String selectedBank) {
		this.selectedBank = selectedBank;
	}
	public String getSelectedBank() {
		BankBranch branch = super.getBankService().findBankBranchById(getProfile().getBranchId());
		if(branch == null || branch.getId() == null){
			selectedBank = "589003";
		}else{
			Bank bank = branch.getBank();
			selectedBank = bank.getId();
		}
		
		return selectedBank;
	}
	public void setSelectedBranch(String selectedBranch) {
		this.selectedBranch = selectedBranch;
	}
	public String getSelectedBranch() {
		selectedBranch = getProfile().getBranchId();
		return selectedBranch;
	}
	
	public void setSelectedRole(String selectedRole) {
		this.selectedRole = selectedRole;
	}
	public String getSelectedRole() {
		selectedRole = getProfile().getRole().getId();
		return selectedRole;
	}
		
	public void edit(ActionEvent  event) {
		profileId = (String) event.getComponent().getAttributes().get("profileId");
	}
	public void processBankValueChange(ValueChangeEvent event) {
		String value = (String) event.getNewValue();
		if (value != null && !value.equals("none")) {
			try {
				branchList = new ArrayList<SelectItem>();
				branchList.add(new SelectItem("", "--select--"));
				List<BankBranch> branches = super.getBankService().getBankBranchByBank(value);
				if (branches != null) {
					for (BankBranch branch: branches) {
						branchList.add(new SelectItem(branch.getId(), branch.getName()));
					}
				}
			} catch(Exception e) {
				
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public String submit() {
		try {
			if(!this.isValidEmailAddress(profile.getEmail())){
				super.setErrorMessage("Please enter a valid email.");
				return "failure";
			}
			
			if(!this.isValidIPAddress(profile.getIpAddress())){
				super.setErrorMessage("Please enter a valid IP Address.");
				return "failure";
			}
			if (selectedRole == null || selectedRole.equals("")) {
				super.setErrorMessage("Please select the user role.");
				return "failure";
			}
			if (selectedBranch != null && !selectedBranch.equals("")) {
				Role role = super.getProfileService().findRoleByRoleId(selectedRole);
				getProfile().setRole(role);
				getProfile().setBranchId(selectedBranch);
				profile = super.getProfileService().editProfile(profile, super.getJaasUserName());
			} else {
				super.setErrorMessage("Please select the branch and the bank.");
				return "failure";
			}
		} catch (Exception e) {
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			e.printStackTrace();
			return "failure";
		}
		
		super.setInformationMessage("Profile updated successfully.");
		super.getRequestScope().put("profileId", profile.getId());
		super.gotoPage("/admin/viewProfile.jspx");
		return "submit";
	}
	
	@SuppressWarnings("unchecked")
	public String cancel() {
		super.getRequestScope().put("profileId", profileId);
		super.gotoPage("/admin/viewProfile.jspx");
		return "cancel";
	}
	
	private boolean isValidEmailAddress(String emailAddress) {
		String expression = "^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = emailAddress;
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		return matcher.matches();

	}
	
	private boolean isValidIPAddress(String ip){
		String expression = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
							"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
							"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
							"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		CharSequence inputStr = ip;
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		return matcher.matches();
	}
	
}
