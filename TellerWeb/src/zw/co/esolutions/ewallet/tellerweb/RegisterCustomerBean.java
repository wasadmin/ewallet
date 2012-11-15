package zw.co.esolutions.ewallet.tellerweb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.CustomerClass;
import zw.co.esolutions.ewallet.customerservices.service.Gender;
import zw.co.esolutions.ewallet.customerservices.service.MaritalStatus;
//import zw.co.esolutions.ewallet.customerservices.service.MobileNetwork;
import zw.co.esolutions.ewallet.customerservices.service.MobileNetworkOperator;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.referralservices.service.Referral;
import zw.co.esolutions.ewallet.tellerweb.enums.Title;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.NumberUtil;

public class RegisterCustomerBean extends PageCodeBase {
	private RegInfo regInfo;
	private List<SelectItem> titleList;
	private String selectedTitle;
	private List<SelectItem> genderList;
	private String selectedGender;
	private List<SelectItem> maritalStatusList;
	private String selectedMaritalStatus;
	private List<SelectItem> customerClassList;
	private String selectedCustomerClass;
//	private List<SelectItem> bankList;
//	private String selectedBank;
//	private List<SelectItem> branchList;
//	private String selectedBranch;
//	private List<SelectItem> accountClassList;
//	private String selectedAccountClass;
	private Date dateOfBirth;
	private MobileProfile profile1 = new MobileProfile();
	private MobileProfile profile2 = new MobileProfile();
	private MobileProfile profile3 = new MobileProfile();
	public List<SelectItem> networkList;
	private String selectedNetwork1;
	private String selectedNetwork2;
	private String selectedNetwork3;
	private String referralCode;
	
	public RegInfo getRegInfo() {
		if (regInfo == null) {
			regInfo = new RegInfo();
			regInfo.setCustomer(new Customer());
			regInfo.setContactDetails(new ContactDetails());
			regInfo.setBankAccountList(new ArrayList<BankAccount>());
			regInfo.setMobileProfileList(new ArrayList<MobileProfile>());
		
		}
		return regInfo;
	}
	public void setRegInfo(RegInfo regInfo) {
		this.regInfo = regInfo;
	}
	
	public List<SelectItem> getTitleList() {
		if (titleList == null) {
			titleList = new ArrayList<SelectItem>();
			titleList.add(new SelectItem("none", "--select--"));
			for (Title title: Title.values()) {
				titleList.add(new SelectItem(title.name(), title.name()));
			}
		}
		return titleList;
	}
	public void setTitleList(List<SelectItem> titleList) {
		this.titleList = titleList;
	}
	public String getSelectedTitle() {
		return selectedTitle;
	}
	public void setSelectedTitle(String selectedTitle) {
		this.selectedTitle = selectedTitle;
	}
	public List<SelectItem> getGenderList() {
		if (genderList == null) {
			genderList = new ArrayList<SelectItem>();
			genderList.add(new SelectItem("none", "--select--"));
			for (Gender gender: Gender.values()) {
				genderList.add(new SelectItem(gender.name(), gender.name()));
			}
		}
		return genderList;
	}
	public void setGenderList(List<SelectItem> genderList) {
		this.genderList = genderList;
	}
	public String getSelectedGender() {
		return selectedGender;
	}
	public void setSelectedGender(String selectedGender) {
		this.selectedGender = selectedGender;
	}
	public List<SelectItem> getMaritalStatusList() {
		if (maritalStatusList == null) {
			maritalStatusList = new ArrayList<SelectItem>();
			maritalStatusList.add(new SelectItem("none", "--select--"));
			for (MaritalStatus maritalStatus: MaritalStatus.values()) {
				maritalStatusList.add(new SelectItem(maritalStatus.name(), maritalStatus.name()));
			}
		}
		return maritalStatusList;
	}
	public void setMaritalStatusList(List<SelectItem> maritalStatusList) {
		this.maritalStatusList = maritalStatusList;
	}
	public String getSelectedMaritalStatus() {
		return selectedMaritalStatus;
	}
	public void setSelectedMaritalStatus(String selectedMaritalStatus) {
		this.selectedMaritalStatus = selectedMaritalStatus;
	}
	public List<SelectItem> getCustomerClassList() {
		if (customerClassList == null) {
			customerClassList = new ArrayList<SelectItem>();
			customerClassList.add(new SelectItem("none", "--select--"));
			for (CustomerClass customerClass: CustomerClass.values()) {
				customerClassList.add(new SelectItem(customerClass.name(), customerClass.name()));
			}
		}
		return customerClassList;
	}
	public void setCustomerClassList(List<SelectItem> customerClassList) {
		this.customerClassList = customerClassList;
	}
	public String getSelectedCustomerClass() {
		return selectedCustomerClass;
	}
	public void setSelectedCustomerClass(String selectedCustomerClass) {
		this.selectedCustomerClass = selectedCustomerClass;
	}
	public List<SelectItem> getNetworkList() {
		if (networkList == null) {
			networkList = new ArrayList<SelectItem>();
			networkList.add(new SelectItem("none", "--select--"));
			/*for (MobileNetwork network: MobileNetwork.values()) {
				networkList.add(new SelectItem(network.name(), network.name()));
			}*/
		}
		return networkList;
	}
	public void setNetworkList(List<SelectItem> networkList) {
		this.networkList = networkList;
	}
	public String getSelectedNetwork1() {
		return selectedNetwork1;
	}
	public void setSelectedNetwork1(String selectedNetwork1) {
		this.selectedNetwork1 = selectedNetwork1;
	}
	public String getSelectedNetwork2() {
		return selectedNetwork2;
	}
	public void setSelectedNetwork2(String selectedNetwork2) {
		this.selectedNetwork2 = selectedNetwork2;
	}
	public String getSelectedNetwork3() {
		return selectedNetwork3;
	}
	public void setSelectedNetwork3(String selectedNetwork3) {
		this.selectedNetwork3 = selectedNetwork3;
	}
	public MobileProfile getProfile1() {
		return profile1;
	}
	public void setProfile1(MobileProfile profile1) {
		this.profile1 = profile1;
	}
	public MobileProfile getProfile2() {
		return profile2;
	}
	public void setProfile2(MobileProfile profile2) {
		this.profile2 = profile2;
	}
	public MobileProfile getProfile3() {
		return profile3;
	}
	public void setProfile3(MobileProfile profile3) {
		this.profile3 = profile3;
	}
	
	public void setReferralCode(String referralCode) {
		this.referralCode = referralCode;
	}
	public String getReferralCode() {
		return referralCode;
	}
	
//	public List<SelectItem> getBankList() {
//		System.out.println(">>>>>>>>>>");
//		if (bankList == null || bankList.isEmpty()) {
//			bankList = new ArrayList<SelectItem>();
//			bankList.add(new SelectItem("none", "--select--"));
//			System.out.println(">>>>>>>>>>bankList: " + bankList);
//			try {
//				List<Bank> banks = bankService.getBank();
//				if (banks != null) {
//					for (Bank bank: banks) {
//						System.out.println(">>>>>>>>>>bank: " + bank.getName());
//						bankList.add(new SelectItem(bank.getId(), bank.getName()));
//					}
//				}
//			} catch(Exception e) {
//				super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
//			}
//		}
//		return bankList;
//	}
//	public void setBankList(List<SelectItem> bankList) {
//		this.bankList = bankList;
//	}
//	public String getSelectedBank() {
//		return selectedBank;
//	}
//	public void setSelectedBank(String selectedBank) {
//		this.selectedBank = selectedBank;
//	}
//	public List<SelectItem> getBranchList() {
//		if (branchList == null) {
//			branchList = new ArrayList<SelectItem>();
//			branchList.add(new SelectItem("none", "--select--"));
//			try {
//				List<BankBranch> branches = bankService.getBankBranch();
//				if (branches != null) {
//					for (BankBranch branch: branches) {
//						branchList.add(new SelectItem(branch.getId(), branch.getName()));
//					}
//				}
//			} catch (Exception e) {
//				
//			}
//		}
//		return branchList;
//	}
//	public void setBranchList(List<SelectItem> branchList) {
//		this.branchList = branchList;
//	}
//	public String getSelectedBranch() {
//		return selectedBranch;
//	}
//	public void setSelectedBranch(String selectedBranch) {
//		this.selectedBranch = selectedBranch;
//	}
//	public List<SelectItem> getAccountClassList() {
//		if (accountClassList == null) {
//			accountClassList = new ArrayList<SelectItem>();
//			accountClassList.add(new SelectItem("none", "--select--"));
//			for (BankAccountClass accountClass: BankAccountClass.values()) {
//				accountClassList.add(new SelectItem(accountClass.name(), accountClass.name()));
//			}
//		}
//		return accountClassList;
//	}
//	public void setAccountClassList(List<SelectItem> accountClassList) {
//		this.accountClassList = accountClassList;
//	}
//	public String getSelectedAccountClass() {
//		return selectedAccountClass;
//	}
//	public void setSelectedAccountClass(String selectedAccountClass) {
//		this.selectedAccountClass = selectedAccountClass;
//	}
//	
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public Date getDateOfBirth() {
		dateOfBirth = (dateOfBirth == null)? new Date() : dateOfBirth;
		return dateOfBirth;
	}
//	public void processBankValueChange(ValueChangeEvent event) {
//		String value = (String) event.getNewValue();
//		if (value != null && !value.equals("none")) {
//			try {
//				branchList = new ArrayList<SelectItem>();
//				List<BankBranch> branches = bankService.getBankBranchByBank(value);
//				if (branches != null) {
//					for (BankBranch branch: branches) {
//						branchList.add(new SelectItem(branch.getId(), branch.getName()));
//					}
//				}
//			} catch(Exception e) {
//				
//			}
//		}
//	}
	public boolean fieldsAreValid() {
		if (selectedTitle == null || selectedTitle.equals("none")) {
			super.setErrorMessage("Please select the title.");
			return false;
		}
		if (selectedGender == null || selectedGender.equals("none")) {
			super.setErrorMessage("Please select the gender.");
			return false;
		}
		if (selectedMaritalStatus == null || selectedMaritalStatus.equals("none")) {
			super.setErrorMessage("Please select the marital status.");
			return false;
		}
		if (selectedCustomerClass == null || selectedCustomerClass.equals("none")) {
			super.setErrorMessage("Please select the customer class.");
			return false;
		}
		if (profile1.getMobileNumber() != null && selectedNetwork1.equals("none")) {
			if (!profile1.getMobileNumber().trim().equals("")) {
				super.setErrorMessage("Please select the network for mobile 1.");
				return false;
			}
		}
		if (profile2.getMobileNumber() != null && selectedNetwork2.equals("none")) {
			if (!profile2.getMobileNumber().trim().equals("")) {
				super.setErrorMessage("Please select the network for mobile 2.");
				return false;
			}
		}
		if (profile3.getMobileNumber() != null && selectedNetwork3.equals("none")) {
			if (!profile3.getMobileNumber().trim().equals("")) {
				super.setErrorMessage("Please select the network for mobile 3.");
				return false;
			}
		}
//		if (profile1.getMobileNumber() == null && !selectedNetwork1.equals("none")) {
//			super.setErrorMessage("Please enter the number for mobile 1.");
//			return false;
//		}
//		if (profile2.getMobileNumber() == null && !selectedNetwork2.equals("none")) {
//			super.setErrorMessage("Please enter the number for mobile 2.");
//			return false;
//		}
//		if (profile3.getMobileNumber() == null && !selectedNetwork3.equals("none")) {
//			super.setErrorMessage("Please enter the number for mobile 3.");
//			return false;
//		}
//		if (selectedBank == null || selectedBank.equals("none")) {
//			super.setErrorMessage("Please select the bank.");
//			return false;
//		}
//		if (selectedBranch == null || selectedBranch.equals("none")) {
//			super.setErrorMessage("Please select the branch.");
//			return false;
//		}
//		if (selectedAccountClass == null || selectedAccountClass.equals("none")) {
//			super.setErrorMessage("Please select the account class.");
//			return false;
//		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public void putVariablesInSessionScope() {
		super.getSessionScope().put("regInfo", getRegInfo());
	}
	
	public String cancel() {
		super.cleanUpSessionScope();
		gotoPage("/teller/csrHome.jspx");
		return "cancel";
	}
	
	public String next() {
		try {
			if (this.fieldsAreValid()) {
				getRegInfo().getCustomer().setTitle(selectedTitle);
				getRegInfo().getCustomer().setGender(Gender.valueOf(selectedGender));
				getRegInfo().getCustomer().setMaritalStatus(MaritalStatus.valueOf(selectedMaritalStatus));
				getRegInfo().getCustomer().setCustomerClass(CustomerClass.valueOf(selectedCustomerClass));
				getRegInfo().getCustomer().setDateOfBirth(DateUtil.convertToXMLGregorianCalendar(dateOfBirth));
				
				Profile profile = super.getProfileService().getProfileByUserName(super.getJaasUserName());
				if (profile == null) {
					super.setErrorMessage("You are not logged in. Log in first.");
					return "failure";
				}
				getRegInfo().getCustomer().setBranchId(profile.getBranchId());
				
				if (profile1.getMobileNumber() != null && !profile1.getMobileNumber().trim().equals("")) {
					profile1.setNetwork(MobileNetworkOperator.valueOf(selectedNetwork1));
					profile1.setPrimary(true);
					getRegInfo().getMobileProfileList().add(profile1);
				}
				if (profile2.getMobileNumber() != null && !profile2.getMobileNumber().trim().equals("")) {
					profile2.setNetwork(MobileNetworkOperator.valueOf(selectedNetwork2));
					getRegInfo().getMobileProfileList().add(profile2);
				}
				if (profile3.getMobileNumber() != null && !profile3.getMobileNumber().trim().equals("")) {
					profile3.setNetwork(MobileNetworkOperator.valueOf(selectedNetwork3));
					getRegInfo().getMobileProfileList().add(profile3);
				}
				
				for (MobileProfile mobileProfile: regInfo.getMobileProfileList()) {
		    		//format mobile number
		    		try {
		    			mobileProfile.setMobileNumber(NumberUtil.formatMobileNumber(mobileProfile.getMobileNumber()));
		    			//check existence
		    			if (super.getCustomerService().getMobileProfileByMobileNumber(mobileProfile.getMobileNumber()) != null) {
		    				super.setErrorMessage("Mobile Number " + mobileProfile.getMobileNumber() + " is already registered.");
		    				return "failure";
		    			}
		    		} catch (Exception e) {
						super.setErrorMessage("Mobile Number is not in correct format.");
						return "failure";
					}
		    		
		    		if (referralCode != null && !referralCode.trim().equals("")) {
		    			Referral referral = super.getReferralService().getReferralByReferredMobileAndCode(mobileProfile.getMobileNumber(), Integer.parseInt(referralCode));
		    			if (referral != null) {
		    				getRegInfo().setReferral(referral);
		    			}
		    		}
	    		}
				
				//process referral
//				String referralCode = getRegInfo().getMobileProfile().getReferralCode();
//				String mobileNumber = getRegInfo().getMobileProfile().getMobileNumber();
//				if (referralCode != null) {
//					Referral referral = referralService.getReferralByReferredMobileAndCode(mobileNumber, Integer.parseInt(referralCode));
//					if (referral != null) {
//						if (!referral.getStatus().equals(ReferralStatus.NEW)) {
//							super.setErrorMessage("Referral Code is in an invalid state: " + referral.getStatus().name());
//							return "failure";
//						}
//					} else {
//						super.setErrorMessage("ERROR: Referral Code does not exist.");
//						return "failure";
//					}
//				}
				
				this.putVariablesInSessionScope();
				
			} else {
				return "failure";
			}
		} catch(Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		gotoPage("/teller/registerCustomer2.jspx");
		return "next";
	}

	
}
