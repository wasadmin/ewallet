package zw.co.esolutions.ewallet.tellerweb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountClass;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountLevel;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountStatus;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountType;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.CustomerStatus;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfileStatus;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.referralservices.service.Referral;
import zw.co.esolutions.ewallet.referralservices.service.ReferralStatus;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.NumberUtil;

public class RegisterCustomer2Bean extends PageCodeBase {
	/*private RegInfo regInfo;
	private BankAccount account1 = new BankAccount();
	private BankAccount account2 = new BankAccount();
	private BankAccount account3 = new BankAccount();
	private List<SelectItem> accountClassList;
	private String selectedAccountClass1;
	private String selectedAccountClass2;
	private String selectedAccountClass3;
	private List<SelectItem> accountTypeList;
	private String selectedAccountType1;
	private String selectedAccountType2;
	private String selectedAccountType3;
	private List<SelectItem> branchList;
	private String selectedBranch1;
	private String selectedBranch2;
	private String selectedBranch3;
	private Bank bank;
	
	private List<SelectItem> primaryAccountMenu;
	private String primaryAccountItem;
	
	public RegisterCustomer2Bean() {
		super();
		this.setPrimaryAccountItem(BankAccountType.E_WALLET.toString());
	}
	public List<SelectItem> getPrimaryAccountMenu() {
		if(this.primaryAccountMenu == null || 
				this.primaryAccountMenu.isEmpty()) {
			this.primaryAccountMenu = new ArrayList<SelectItem>();
			for(BankAccountType type : BankAccountType.values()) {
				if(BankAccountType.SAVINGS.equals(type) || 
						BankAccountType.CURRENT.equals(type) || 
						BankAccountType.E_WALLET.equals(type) || 
						BankAccountType.CHEQUE.equals(type) ) {
					this.primaryAccountMenu.add(new SelectItem(type.toString(), type.toString().replace("_", " ")));
				}
			}
		}
		return primaryAccountMenu;
	}
	public void setPrimaryAccountMenu(List<SelectItem> primaryAccountMenu) {
		this.primaryAccountMenu = primaryAccountMenu;
	}
	public String getPrimaryAccountItem() {
		return primaryAccountItem;
	}
	public void setPrimaryAccountItem(String primaryAccountItem) {
		this.primaryAccountItem = primaryAccountItem;
	}

	public void setRegInfo(RegInfo regInfo) {
		this.regInfo = regInfo;
	}
	public RegInfo getRegInfo() {
		if (regInfo == null) {
			regInfo = (RegInfo) super.getSessionScope().get("regInfo");
		}
		return regInfo;
	}

	public BankAccount getAccount1() {
		return account1;
	}
	public void setAccount1(BankAccount account1) {
		this.account1 = account1;
	}
	public BankAccount getAccount2() {
		return account2;
	}
	public void setAccount2(BankAccount account2) {
		this.account2 = account2;
	}
	public BankAccount getAccount3() {
		return account3;
	}
	public void setAccount3(BankAccount account3) {
		this.account3 = account3;
	}
	
	public List<SelectItem> getAccountClassList() {
		if (accountClassList == null) {
			accountClassList = new ArrayList<SelectItem>();
			for (BankAccountClass accountClass: BankAccountClass.values()) {
				if (accountClass.equals(BankAccountClass.SYSTEM)) {
					continue;
				}
				accountClassList.add(new SelectItem(accountClass.name(), accountClass.name()));
			}
		}
		return accountClassList;
	}
	public void setAccountClassList(List<SelectItem> accountClassList) {
		this.accountClassList = accountClassList;
	}
	
	public String getSelectedAccountClass1() {
		return selectedAccountClass1;
	}
	public void setSelectedAccountClass1(String selectedAccountClass1) {
		this.selectedAccountClass1 = selectedAccountClass1;
	}
	public String getSelectedAccountClass2() {
		return selectedAccountClass2;
	}
	public void setSelectedAccountClass2(String selectedAccountClass2) {
		this.selectedAccountClass2 = selectedAccountClass2;
	}
	public String getSelectedAccountClass3() {
		return selectedAccountClass3;
	}
	public void setSelectedAccountClass3(String selectedAccountClass3) {
		this.selectedAccountClass3 = selectedAccountClass3;
	}
	public List<SelectItem> getAccountTypeList() {
		if (accountTypeList == null) {
			accountTypeList = new ArrayList<SelectItem>();
			accountTypeList.add(new SelectItem("none", "--select--"));
			accountTypeList.add(new SelectItem("E_WALLET", "E_WALLET"));
			accountTypeList.add(new SelectItem("SAVINGS", "SAVINGS"));
			accountTypeList.add(new SelectItem("CHEQUE", "CHEQUE"));
			accountTypeList.add(new SelectItem("CURRENT", "CURRENT"));
		}
		return accountTypeList;
	}
	public void setAccountTypeList(List<SelectItem> accountTypeList) {
		this.accountTypeList = accountTypeList;
	}
	public String getSelectedAccountType1() {
		return selectedAccountType1;
	}
	public void setSelectedAccountType1(String selectedAccountType1) {
		this.selectedAccountType1 = selectedAccountType1;
	}
	public String getSelectedAccountType2() {
		return selectedAccountType2;
	}
	public void setSelectedAccountType2(String selectedAccountType2) {
		this.selectedAccountType2 = selectedAccountType2;
	}
	public String getSelectedAccountType3() {
		return selectedAccountType3;
	}
	public void setSelectedAccountType3(String selectedAccountType3) {
		this.selectedAccountType3 = selectedAccountType3;
	}
	
	public List<SelectItem> getBranchList() {
		if (branchList == null) {
			branchList = new ArrayList<SelectItem>();
			branchList.add(new SelectItem("none", "--select--"));
			try {
				Profile profile = super.getProfileService().getProfileByUserName(super.getJaasUserName());
				BankBranch b = super.getBankService().findBankBranchById(profile.getBranchId());
				List<BankBranch> branches = super.getBankService().getBankBranchByBank(b.getBank().getId());
				if (branches != null) {
					for (BankBranch branch: branches) {
						branchList.add(new SelectItem(branch.getId(), branch.getName()));
					}
				}
			} catch (Exception e) {
				
			}
		}
		return branchList;
	}
	public void setBranchList(List<SelectItem> branchList) {
		this.branchList = branchList;
	}
	public String getSelectedBranch1() {
		return selectedBranch1;
	}
	public void setSelectedBranch1(String selectedBranch1) {
		this.selectedBranch1 = selectedBranch1;
	}
	public String getSelectedBranch2() {
		return selectedBranch2;
	}
	public void setSelectedBranch2(String selectedBranch2) {
		this.selectedBranch2 = selectedBranch2;
	}
	public String getSelectedBranch3() {
		return selectedBranch3;
	}
	public void setSelectedBranch3(String selectedBranch3) {
		this.selectedBranch3 = selectedBranch3;
	}
		
	public void setBank(Bank bank) {
		this.bank = bank;
	}
	public Bank getBank() {
		if (bank == null) {
			Profile profile = super.getProfileService().getProfileByUserName(super.getJaasUserName());
			bank = super.getBankService().findBankBranchById(profile.getBranchId()).getBank();
		}
		return bank;
	}
	public String submit() {
		if (getRegInfo() == null) {
			super.setErrorMessage("Error: Customer details are null.");
			return "failure";
		}
		if (!"none".equals(selectedAccountType1)) {
			account1.setType(BankAccountType.valueOf(selectedAccountType1));
			account1.setAccountClass(BankAccountClass.valueOf(selectedAccountClass1));
			if (!"none".equals(selectedBranch1)) {
				account1.setBranch(super.getBankService().findBankBranchById(selectedBranch1));
			} else {
				super.setErrorMessage("Please select the branch for primary account.");
				return "failure";
			}
			getRegInfo().getBankAccountList().add(account1);
		}
		if (!"none".equals(selectedAccountType2)) {
			account2.setType(BankAccountType.valueOf(selectedAccountType2));
			account2.setAccountClass(BankAccountClass.valueOf(selectedAccountClass2));
			if (!"none".equals(selectedBranch2)) {
				account2.setBranch(super.getBankService().findBankBranchById(selectedBranch2));
			} else {
				super.setErrorMessage("Please select the branch for second account.");
				return "failure";
			}
			getRegInfo().getBankAccountList().add(account2);
		}
		if (!"none".equals(selectedAccountType3)) {
			account3.setType(BankAccountType.valueOf(selectedAccountType3));
			account3.setAccountClass(BankAccountClass.valueOf(selectedAccountClass3));
			if (!"none".equals(selectedBranch3)) {
				account3.setBranch(super.getBankService().findBankBranchById(selectedBranch3));
			} else {
				super.setErrorMessage("Please select the branch for third account.");
				return "failure";
			}
			getRegInfo().getBankAccountList().add(account3);
		}
		
		//Code block for setting Primary Account
		BankAccount validPrimaryAccount = null;
		for(BankAccount acc : this.getRegInfo().getBankAccountList()) {
			if(this.getPrimaryAccountItem().equalsIgnoreCase(acc.getType().toString())) {
				validPrimaryAccount = acc;
			}
		}
		if(validPrimaryAccount == null) {
			super.setErrorMessage("Invalid primary account. No Account of type "+this.getPrimaryAccountItem().replace("_", " ")+
					" was selected for creation.");
			return "failure";
		} else {
			//Remove old account to be set the primary attribute to true
			this.getRegInfo().getBankAccountList().remove(validPrimaryAccount);
			validPrimaryAccount.setPrimaryAccount(true);
			this.getRegInfo().getBankAccountList().add(validPrimaryAccount);
		}
		
		//Do registration
		this.registerCustomer(regInfo);
		
		super.setInformationMessage("Customer has been registered successfully.");
		super.cleanUpSessionScope();
		gotoPage("/teller/registerCustomer.jspx");
		return "submit";
	}
	
	public String registerCustomer(RegInfo regInfo) {
    	try {
    		
    		if (regInfo != null) {
	    		Customer customer = regInfo.getCustomer();
	    		ContactDetails contactDetails = regInfo.getContactDetails();
	    			   
	    		MobileProfile primaryMobileProfile = null;
	    		
   	    		for (MobileProfile mobileProfile: regInfo.getMobileProfileList()) {
		    		//format mobile number
		    		try {
		    			mobileProfile.setMobileNumber(NumberUtil.formatMobileNumber(mobileProfile.getMobileNumber()));
		    			mobileProfile.setBankId(this.getBank().getId());
		    			if(mobileProfile != null) {
		    				if(mobileProfile.isPrimary()) {
		    					primaryMobileProfile = mobileProfile;
		    				}
		    			}
		    		} catch (Exception e) {
						super.setErrorMessage("Mobile Number is not in correct format.");
						return "failure";
					}
		    		
		    		contactDetails.setContactName(customer.getLastName()+" "+customer.getFirstNames());
		    		contactDetails.setTelephone(primaryMobileProfile.getMobileNumber());
		    		contactDetails = super.getContactDetailsService().createContactDetails(contactDetails,super.getJaasUserName());
		    		
		    		customer.setContactDetailsId(contactDetails.getId());
		    		customer.setStatus(CustomerStatus.AWAITING_APPROVAL);
		    		customer = super.getCustomerService().createCustomer(customer, super.getJaasUserName());
		    		
		    		mobileProfile.setCustomer(customer);
			    	mobileProfile.setSecretCode("secret");
			    	mobileProfile.setStatus(MobileProfileStatus.AWAITING_APPROVAL);
		    		mobileProfile = super.getCustomerService().createMobileProfile(mobileProfile, super.getJaasUserName());
		    	 		
	    		}
	    		
	    		for (BankAccount bankAccount: regInfo.getBankAccountList()) {
		    		bankAccount.setOwnerType(OwnerType.CUSTOMER);
	    			bankAccount.setAccountHolderId(customer.getId());
		    		bankAccount.setLevel(BankAccountLevel.INDIVIDUAL);
		    		bankAccount.setStatus(BankAccountStatus.AWAITING_APPROVAL);
		    		bankAccount.setAccountName(customer.getLastName() + " " + customer.getFirstNames());
		    		bankAccount.setRegistrationBranchId(regInfo.getCustomer().getBranchId());
		    		if (BankAccountType.E_WALLET.equals(bankAccount.getType())) {
		    			bankAccount.setCode("ewallet");
		    			if(primaryMobileProfile != null) {
		    				bankAccount.setAccountNumber(primaryMobileProfile.getMobileNumber());
		    			} else {
		    				super.setErrorMessage(" No primary mobile number.");
		    				return "failure";
		    			}
		    		}
		    		bankAccount = super.getBankService().createBankAccount(bankAccount, super.getJaasUserName());
		   
	    		} 
	    		
	    		//Upadate Contact Details
	    		contactDetails = super.getContactDetailsService().findContactDetailsById(contactDetails.getId());
	    		contactDetails.setOwnerType(OwnerType.CUSTOMER.toString());
	    		contactDetails.setOwnerId(customer.getId());
	    		contactDetails = super.getContactDetailsService().updateContactDetails(contactDetails);
	    		
	    		//process referral
	    		Referral referral = getRegInfo().getReferral();
	    		if (referral != null) {
	    			if (referral.getStatus().equals(ReferralStatus.NEW)) {
	    				if (DateUtil.daysBetween(new Date(), DateUtil.convertToDate(referral.getDateCreated())) <= 10) {
							super.getReferralService().promoteReferralState(referral, ReferralStatus.REGISTERED);
						} else {
							super.getReferralService().promoteReferralState(referral, ReferralStatus.EXPIRED);
						}
	    			}
	    		}
	    	
    		} else {
    			return ResponseCode.E715.name();
    		}
    		
    	} catch (Exception e) {
			e.printStackTrace();
			return ResponseCode.E777.name();
		}
    	return ResponseCode.E000.name();
	}
	
	public String cancel() {
		super.cleanUpSessionScope();
		gotoPage("/teller/csrHome.jspx");
		return "cancel";
	}
	
	public String back() {
		gotoPage("/teller/registerCustomer.jspx");
		return "back";
	}
	*/
}
