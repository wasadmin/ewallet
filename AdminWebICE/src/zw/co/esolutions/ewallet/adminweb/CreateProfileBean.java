package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.Role;
import zw.co.esolutions.ewallet.util.DateUtil;

public class CreateProfileBean extends PageCodeBase {
	
	private Profile profile = new Profile();
	private List<SelectItem> roleList;
	private List<SelectItem> bankList;
	private List<SelectItem> branchList;
	private String selectedBank;
	private String selectedBranch;
	private String selectedRole;
	private String password;
	private String selectedProfileType;
	private List<SelectItem> profileTypeList;
	private boolean showExpDate;
	private Date expirationDate;
	
	public void setProfile(Profile profile) {
		this.profile = profile;
	}
	public Profile getProfile() {
		return profile;
	}
	public void setRoleList(List<SelectItem> roleList) {
		this.roleList = roleList;
	}
	public List<SelectItem> getRoleList() {
		if (roleList == null) {
			roleList = new ArrayList<SelectItem>();
			roleList.add(new SelectItem("", "--Select--"));
			for (Role role: super.getProfileService().getActiveRoles()) {
				roleList.add(new SelectItem(role.getId(), role.getRoleName()));				
			}
		}
		return roleList;
	}
	public List<SelectItem> getBankList() {
		if (bankList == null) {
			bankList = new ArrayList<SelectItem>();
			bankList.add(new SelectItem("", "--Select--"));
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
			branchList.add(new SelectItem("", "--Select--"));
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
		return selectedBank;
	}
	public void setSelectedBranch(String selectedBranch) {
		this.selectedBranch = selectedBranch;
	}
	public String getSelectedBranch() {
		return selectedBranch;
	}
	
	public void setSelectedRole(String selectedRole) {
		this.selectedRole = selectedRole;
	}
	public String getSelectedRole() {
		return selectedRole;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPassword() {
		return password;
	}	
	
	public void processBankValueChange(ValueChangeEvent event) {
		String value = (String) event.getNewValue();
		if (value != null && !value.equals("none")) {
			try {
				branchList = new ArrayList<SelectItem>();
				branchList.add(new SelectItem("none", "--Select--"));
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
			if (selectedRole == null || selectedRole.equals("none")) {
				super.setErrorMessage("Please select the user role.");
				return "failure";
			}
			if(!this.isValidEmailAddress(profile.getEmail())){
				super.setErrorMessage("Please enter a valid email address.");
				return "failure";
			}
			
			if(!this.isValidIPAddress(profile.getIpAddress())){
				super.setErrorMessage("Please enter a valid IP address.");
				return "failure";
			}
			Profile p = super.getProfileService().getProfileByUserName(profile.getUserName());
			if(!(p == null || p.getId()==null)){
				super.setErrorMessage("User with username "+profile.getUserName()+" already exists");
				return "failure";
			}
			if (selectedBranch != null && !selectedBranch.equals("none")) {
				Role role = super.getProfileService().findRoleByRoleId(selectedRole);
				profile.setRole(role);
				profile.setPasswordExpiryDate(DateUtil.convertToXMLGregorianCalendar(getExpirationDate()));
				profile.setBranchId(selectedBranch);
								
				profile = super.getProfileService().createProfile(profile, super.getJaasUserName());
			} else {
				super.setErrorMessage("Please select the branch and the bank.");
				return "failure";
			}			
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage("Profile created successfully.");
		super.getRequestScope().put("profileId", profile.getId());
		super.gotoPage("/admin/viewProfile.jspx");
		return "submit";
	}
	
	public String cancel() {
		super.gotoPage("/admin/adminHome.jspx");
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
		if(ip== null){
			return false;
		}
		String expression = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
							"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
							"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
							"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		CharSequence inputStr = ip;
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		return matcher.matches();
	}
	
	public String getSelectedProfileType() {
		return selectedProfileType;
	}
	public void setSelectedProfileType(String selectedProfileType) {
		this.selectedProfileType = selectedProfileType;
	}
	public List<SelectItem> getProfileTypeList() {
		if (profileTypeList == null) {
			profileTypeList = new ArrayList<SelectItem>();
			profileTypeList.add(new SelectItem("PERMANANT", "PERMANANT"));
			profileTypeList.add(new SelectItem("TEMPORARY", "TEMPORARY"));
		}
		return profileTypeList;
	}
	public void setProfileTypeList(List<SelectItem> profileTypeList) {
		this.profileTypeList = profileTypeList;
	}
	
	public void profileTypeAction(ValueChangeEvent event){
		String s = (String)event.getNewValue();
		if(s.equals("TEMPORARY")){
			this.showExpDate=true;
		}else{
			this.showExpDate=false;
		}
	}
	public boolean isShowExpDate() {
		return showExpDate;
	}
	public void setShowExpDate(boolean showExpDate) {
		this.showExpDate = showExpDate;
	}
	public Date getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
	
}
