package zw.co.esolutions.ewallet.csrweb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails;
import zw.co.esolutions.ewallet.csrweb.enums.Title;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.CustomerAutoRegStatus;
import zw.co.esolutions.ewallet.customerservices.service.CustomerClass;
import zw.co.esolutions.ewallet.customerservices.service.Gender;
import zw.co.esolutions.ewallet.customerservices.service.MaritalStatus;
//import zw.co.esolutions.ewallet.customerservices.service.MobileNetwork;
import zw.co.esolutions.ewallet.customerservices.service.MobileNetworkOperator;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.enums.MobileProfileStatus;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.referralservices.service.Referral;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.JsfUtil;
import zw.co.esolutions.ewallet.util.NumberUtil;
import zw.co.esolutions.ewallet.util.StringUtil;

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
	private Date defaultDateOfBirth;
	private MobileProfile profile1 = new MobileProfile();
	private MobileProfile profile2 = new MobileProfile();
	private MobileProfile profile3 = new MobileProfile();
	public List<SelectItem> networkList;
	private String selectedNetwork1;
	private String selectedNetwork2;
	private String selectedNetwork3;
	private String referralCode;
	
	
	
	private static Logger LOG ;
		
		static{
			try {
				PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
				LOG = Logger.getLogger(RegisterCustomerBean.class);
			} catch (Exception e) {
				System.err.println("Failed to initilise logger for " + RegisterCustomerBean.class);
			}
		}
		
	
	
	public RegInfo getRegInfo() {
		if (regInfo == null) {
			regInfo = new RegInfo();
			regInfo.setCustomer(new Customer());
			regInfo.setContactDetails(new ContactDetails());
			//set Default Country
			regInfo.getContactDetails().setCountry("ZIMBABWE");
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
			titleList.add(new SelectItem("", "--select--"));
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
			genderList = JsfUtil.getSelectItemsAsList(Gender.values(),true);
			
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
			maritalStatusList.add(new SelectItem("", "--select--"));
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
			customerClassList.add(new SelectItem("", "--select--"));
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
			networkList.add(new SelectItem("", "--select--"));
			for (MobileNetworkOperator network: MobileNetworkOperator.values()) {
				networkList.add(new SelectItem(network.name(), network.name()));
			}
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
//		LOG.debug">>>>>>>>>>");
//		if (bankList == null || bankList.isEmpty()) {
//			bankList = new ArrayList<SelectItem>();
//			bankList.add(new SelectItem("none", "--select--"));
//			LOG.debug">>>>>>>>>>bankList: " + bankList);
//			try {
//				List<Bank> banks = bankService.getBank();
//				if (banks != null) {
//					for (Bank bank: banks) {
//						LOG.debug">>>>>>>>>>bank: " + bank.getName());
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
		dateOfBirth = (dateOfBirth == null)? getDefaultDateOfBirth() : dateOfBirth;
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
		/*
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
	*/
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public void putVariablesInSessionScope() {
		super.getSessionScope().put("regInfo", getRegInfo());
	}
	
	public String cancel() {
		super.cleanUpSessionScope();
		gotoPage("/csr/csrHome.jspx");
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
				getRegInfo().getCustomer().setNationalId(StringUtil.formatNationalId(getRegInfo().getCustomer().getNationalId()));
				getRegInfo().getCustomer().setCustomerAutoRegStatus(CustomerAutoRegStatus.NEVER); 
				
				Profile profile = super.getProfileService().getProfileByUserName(super.getJaasUserName());
				if (profile == null || profile.getId() == null) {
					super.setErrorMessage("You are not logged in. Log in first.");
					return "failure";
				}
				
				getRegInfo().getMobileProfileList().clear();
				getRegInfo().getCustomer().setBranchId(profile.getBranchId());
				/*
				 * Uncomment this later
				 */
			getRegInfo().getCustomer().setCustomerLastBranch(profile.getBranchId());
			
				
				if (profile1.getMobileNumber() != null && !profile1.getMobileNumber().trim().equals("")) {
					profile1.setNetwork(MobileNetworkOperator.valueOf(selectedNetwork1));
					profile1.setPrimary(true);
					/*
	    			 * Uncomment later
	    			 */
	    			profile1.setMobileProfileEditBranch(profile.getBranchId());
					getRegInfo().getMobileProfileList().add(profile1);
				}
				LOG.debug("1 number pf mobileprofile list  ....."+regInfo.getMobileProfileList().size());
				if (profile2.getMobileNumber() != null && !profile2.getMobileNumber().trim().equals("")) {
					profile2.setNetwork(MobileNetworkOperator.valueOf(selectedNetwork2));
					profile2.setMobileProfileEditBranch(profile.getBranchId());
					getRegInfo().getMobileProfileList().add(profile2);
				}
				LOG.debug("2 number pf mobileprofile list  ....."+regInfo.getMobileProfileList().size());
				if (profile3.getMobileNumber() != null && !profile3.getMobileNumber().trim().equals("")) {
					profile3.setNetwork(MobileNetworkOperator.valueOf(selectedNetwork3));
					profile3.setMobileProfileEditBranch(profile.getBranchId());
					getRegInfo().getMobileProfileList().add(profile3);
				}
				LOG.debug(" number pf mobileprofile list  ....."+regInfo.getMobileProfileList().size());
				for (MobileProfile mobileProfile: regInfo.getMobileProfileList()) {
		    		//format mobile number
					LOG.debug(" number pf mobileprofile list  ....."+regInfo.getMobileProfileList().size());
					LOG.debug("mobilr number>>>>>>>>>>>>>>>>>>>>>>>>>>>"+mobileProfile.getMobileNumber());
		    		try {
		    			mobileProfile.setMobileNumber(NumberUtil.formatMobileNumber(mobileProfile.getMobileNumber()));
		    			//check existence
		    			MobileProfile mobileCheck=super.getCustomerService().getMobileProfileByMobileNumber(mobileProfile.getMobileNumber());
		    			if(mobileCheck!=null && mobileCheck.getMobileNumber() != null && !mobileCheck.getStatus().toString().equalsIgnoreCase(MobileProfileStatus.DELETED.toString())){
		    				super.setErrorMessage("Mobile Number " + mobileProfile.getMobileNumber() + " is already registered.");
		    				return "failure";
		    			}
		    			
		    			/*if (super.getCustomerService().getMobileProfileByMobileNumber(mobileProfile.getMobileNumber()) != null) {
		    				super.setErrorMessage("Mobile Number " + mobileProfile.getMobileNumber() + " is already registered.");
		    				return "failure";
		    			}*/
		    			if(validateMobileAndNetwork(mobileProfile)){
		    				//valid
		    			}else{
		    				// Network and number do not match return
		    				super.setErrorMessage("Mobile Number "+mobileProfile.getMobileNumber()+" not in the "+mobileProfile.getNetwork().name() +" network.");
		    				return "failure";
		    			}
		    		} catch (Exception e) {
						super.setErrorMessage("Mobile Number is not in correct format.");
						return "failure";
					}
		    		
		    		if (referralCode != null && !referralCode.trim().equals("")) {
		    			Referral referral = super.getReferralService().getReferralByReferredMobileAndCode(mobileProfile.getMobileNumber(), Integer.parseInt(referralCode));
		    			if (referral != null && referral.getId() != null) {
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
		gotoPage("/csr/registerCustomer2.jspx");
		return "next";
	}
	private boolean validateMobileAndNetwork(MobileProfile mobileProfile) {
		String mobileNumber=mobileProfile.getMobileNumber();
		if(MobileNetworkOperator.ECONET.equals(mobileProfile.getNetwork())){
			return mobileNumber.startsWith("26377");
		}else if(MobileNetworkOperator.NETONE.equals(mobileProfile.getNetwork())){
			return mobileNumber.startsWith("26371");
		} else if(MobileNetworkOperator.TELECEL.equals(mobileProfile.getNetwork())){
			return mobileNumber.startsWith("26373");
		}
		return false;
	}
		
	public Date getDefaultDateOfBirth() {
		if(defaultDateOfBirth==null){
			defaultDateOfBirth=DateUtil.add(new Date(), Calendar.YEAR, -18);
		}
		return defaultDateOfBirth;
	}
	public void setDefaultDateOfBirth(Date defaultDateOfBirth) {
		this.defaultDateOfBirth = defaultDateOfBirth;
	}
	
	
}
