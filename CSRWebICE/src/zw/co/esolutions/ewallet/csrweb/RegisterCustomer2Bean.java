package zw.co.esolutions.ewallet.csrweb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.AccountBalance;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountClass;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountLevel;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountStatus;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountType;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.CustomerStatus;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfileStatus;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.referralservices.service.Referral;
import zw.co.esolutions.ewallet.referralservices.service.ReferralStatus;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.GenerateKey;
import zw.co.esolutions.ewallet.util.NumberUtil;
import zw.co.esolutions.ewallet.util.SystemConstants;

public class RegisterCustomer2Bean extends PageCodeBase {
	private RegInfo regInfo;
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
	private boolean renderInfo;
	private boolean renderInfo2;
	private boolean renderInfo3;
	private String ewalletAccountNumber;
	
private static Logger LOG ;
	
	static{
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(RegisterCustomer2Bean.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + RegisterCustomer2Bean.class);
		}
	}
	
	
	public String getEwalletAccountNumber() {
		LOG.debug("primary mobile is "+getPrimaryMobile());
		this.ewalletAccountNumber=getPrimaryMobile().getMobileNumber();
		return ewalletAccountNumber;
	}
	public void setEwalletAccountNumber(String ewalletAccountNumber) {
		this.ewalletAccountNumber = ewalletAccountNumber;
	}
	
	public boolean isRenderInfo2() {
		return renderInfo2;
	}
	public void setRenderInfo2(boolean renderInfo2) {
		this.renderInfo2 = renderInfo2;
	}
	public boolean isRenderInfo3() {
		return renderInfo3;
	}
	public void setRenderInfo3(boolean renderInfo3) {
		this.renderInfo3 = renderInfo3;
	}
	public void processValueChange1(ValueChangeEvent event){
		String newValue=(String) event.getNewValue();
		if(newValue.equalsIgnoreCase(BankAccountType.E_WALLET.name())){
			renderInfo2=false;
		}else{
			renderInfo2=true;
		}
	}
	public void processValueChange2(ValueChangeEvent event){
		String newValue=(String) event.getNewValue();
		
		if(newValue.equalsIgnoreCase(BankAccountType.E_WALLET.name())){
			renderInfo3=false;
		}else{
			renderInfo3=true;
		}
	}
	public void processValueChange(ValueChangeEvent event){
		String newValue=(String) event.getNewValue();
		LOG.debug("New value account 1  "+newValue);
		
		if(newValue.equalsIgnoreCase(BankAccountType.E_WALLET.name())){
			renderInfo=false;
		}else{
			renderInfo=true;
		}
	}
	public boolean isRenderInfo() {
		return renderInfo;
	}
	public void setRenderInfo(boolean renderInfo) {
		this.renderInfo = renderInfo;
	}

	private List<SelectItem> primaryAccountMenu;
	private String primaryAccountItem;
	
	public RegisterCustomer2Bean() {
		super();
		this.setPrimaryAccountItem(BankAccountType.E_WALLET.toString());
		renderInfo=true;
		renderInfo2=true;
		renderInfo3=true;
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
		/*if(account1==null){
			this.account1=new BankAccount();
			
			account1.setAccountNumber(getPrimaryMobile().getMobileNumber());
		}*/
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
	private MobileProfile getPrimaryMobile() {
		MobileProfile primaryMobileProfile=null;
		LOG.debug("1primary mobile number is "+primaryMobileProfile);
		for (MobileProfile mobileProfile: getRegInfo().getMobileProfileList()) {
    		//format mobile number
    		try {
    			if(mobileProfile != null) {
    				LOG.debug(mobileProfile.getMobileNumber()+"2primary mobile number is "+mobileProfile.isPrimary());
    				if(mobileProfile.isPrimary()) {
    					mobileProfile.setMobileNumber(NumberUtil.formatMobileNumber(mobileProfile.getMobileNumber()));
    	    			mobileProfile.setBankId(this.getBank().getId());
    	    			
    					primaryMobileProfile = mobileProfile;
    					LOG.debug("23 primary mobile number is "+primaryMobileProfile);
    					
    				}
    			}
    		} catch (Exception e) {
				super.setErrorMessage("Mobile Number is not in correct format.");
			
			}
    		
		}
		
		
		return primaryMobileProfile;
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
			//accountTypeList.add(new SelectItem("E_WALLET", "E_WALLET"));
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
		this.selectedAccountType1=BankAccountType.E_WALLET.toString();
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
		if (branchList == null || branchList.isEmpty()) {
			branchList = new ArrayList<SelectItem>();
			branchList.add(new SelectItem("none", "--select--"));
			try {
				Profile profile = super.getProfileService().getProfileByUserName(super.getJaasUserName());
				BankBranch b = super.getBankService().findBankBranchById(profile.getBranchId());
				List<BankBranch> branches = super.getBankService().getBankBranchByBank(b.getBank().getId());
				
				if (branches != null) {
					for (BankBranch branch: branches) {
						LOG.debug("branch id and name   "+branch.getId()+"     name "+branch.getName());
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
		LOG.debug(";;;;;;;;;;;;;;;;;;First phase::::::::::::::::::::: in submit method");
		ProfileServiceSOAPProxy profileProxy= new ProfileServiceSOAPProxy();
		Profile capturerProfile=profileProxy.getProfileByUserName(getJaasUserName());
		getRegInfo().getBankAccountList().clear();
		if (getRegInfo() == null) {
			super.setErrorMessage("Error: Customer details are null.");
			return "failure";
		}
	
		
			account1.setType(BankAccountType.valueOf(selectedAccountType1));
			account1.setAccountNumber(getEwalletAccountNumber());
			account1.setAccountClass(BankAccountClass.valueOf(selectedAccountClass1));
			LOG.debug(" set my capturer last brach acount 1     >>>>>"+capturerProfile.getBranchId());
			account1.setBankAccountLastBranch(capturerProfile.getBranchId());
			if (!"none".equals(selectedBranch1)) {
				account1.setBranch(super.getBankService().findBankBranchById(selectedBranch1));
			} else {
				super.setErrorMessage("Please select the branch for primary account.");
				return "failure";
			}
			getRegInfo().getBankAccountList().add(account1);
		
		if (!"none".equals(selectedAccountType2)) {
			account2.setType(BankAccountType.valueOf(selectedAccountType2));
			account2.setAccountClass(BankAccountClass.valueOf(selectedAccountClass2));
			account2.setBankAccountLastBranch(capturerProfile.getBranchId());
			try{
				account2.setAccountNumber(NumberUtil.validateAccountNumber(account2.getAccountNumber()));
			}catch(Exception e){
				super.setErrorMessage(e.getMessage());
				return "";
			}
			
			//LOG.debug(" set my capturer last brach acount 1     >>>>>"+capturerProfile.getBranchId());
			
			if (!"none".equals(selectedBranch2)) {
				account2.setBranch(super.getBankService().findBankBranchById(selectedBranch2));
			} else {
				super.setErrorMessage("Please select the branch for second account.");
				return "failure";
			}
			getRegInfo().getBankAccountList().add(account2);
		}
		
		LOG.debug("werwwwwA:::::::::::::::::::Lets start the check please:::::::: list size "+getRegInfo().getBankAccountList().size());
		String bankResult=checkIfAccountsExist(getRegInfo().getBankAccountList(),getRegInfo());
		LOG.debug("      whats the result       "+bankResult);
		if(!(ResponseCode.E000.toString().equalsIgnoreCase(bankResult))){
			super.setErrorMessage(bankResult);
			
			return "";
		}
	
		/*if (!"none".equals(selectedAccountType3)) {
			account3.setType(BankAccountType.valueOf(selectedAccountType3));
			account3.setAccountClass(BankAccountClass.valueOf(selectedAccountClass3));
			account3.setBankAccountLastBranch(capturerProfile.getBranchId());
			LOG.debug(" set my capturer last brach acount 1     >>>>>"+capturerProfile.getBranchId());
			
			if (!"none".equals(selectedBranch3)) {
				account3.setBranch(super.getBankService().findBankBranchById(selectedBranch3));
			} else {
				super.setErrorMessage("Please select the branch for third account.");
				return "failure";
			}
			getRegInfo().getBankAccountList().add(account3);
		}*/
		
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
		LOG.debug("...>>>>>>>>>>>>>>>>>>>>>>>>>>>>>adding customer>>>>>>>>>>>>>>>>>>>");
		String result=this.registerCustomer(regInfo);
		LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>returned result from result::::"+result);
		if(ResponseCode.E000.name().equalsIgnoreCase(result)){
		
		super.setInformationMessage("Customer has been registered successfully.");
		
		super.getRequestScope().put("fromPage", "registerCustomer.jspx");
		
		gotoPage("/csr/viewCustomer.jspx");
		
		}else{
			super.setInformationMessage(ResponseCode.valueOf(result).getDescription());
		}
		return "submit";
	}
	
	private String checkIfAccountsExist(List<BankAccount> bankAccountList,RegInfo regInfo) {
		LOG.debug(" nw cjhecking  list size is "+bankAccountList.size());
		List<String> existingAccount= new ArrayList<String>();
		BankServiceSOAPProxy bankService= new BankServiceSOAPProxy();
		Customer customer = regInfo.getCustomer();
		String result="The follwing accounts have already been registered: ";
		BankBranch bankBranch;
		for(BankAccount bankAccount : bankAccountList){
			if (BankAccountType.E_WALLET.equals(bankAccount.getType())) {
    			String accountNumber =bankAccount.getAccountNumber();
    			LOG.debug("be4 account number        "+accountNumber);
    			try {
					accountNumber=NumberUtil.formatMobileNumber(accountNumber);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			bankAccount.setAccountNumber(accountNumber);
    			LOG.debug("after account number        "+accountNumber);
    		}
			bankAccount.setOwnerType(OwnerType.CUSTOMER);
			bankAccount.setAccountHolderId(customer.getId());
			bankBranch = bankService.findBankBranchById(bankAccount.getBranch().getId());
			BankAccount account = bankService.getUniqueBankAccountByAccountNumber(bankAccount.getAccountNumber());
			
			if (account != null && account.getAccountNumber() != null && !account.getStatus().toString().equalsIgnoreCase(BankAccountStatus.DELETED.toString())) {
				String append =", ";
				if(existingAccount.size()>=1){
					result+=append;
					}
				LOG.debug("Account isnt null....afta find by acc num      "+bankAccount.getAccountNumber());
				existingAccount.add(account.getAccountNumber());
				result+=account.getAccountNumber();
				
			}
			
		}
		if(existingAccount.size()>0){
			result+=". Please add other accounts.";
			return result;
		}
		return ResponseCode.E000.toString();
	}
	
	public String registerCustomer(RegInfo regInfo) {
    	try {
    		LOG.debug("registerCustomer methd>>>>>>>>>>>>>>>>>");
    		if (regInfo != null) {
	    		Customer customer = regInfo.getCustomer();
	    		ContactDetails contactDetails = regInfo.getContactDetails();
	    		contactDetails.setId(GenerateKey.generateEntityId());
	    		customer.setContactDetailsId(contactDetails.getId());
	    		customer.setStatus(CustomerStatus.AWAITING_APPROVAL);
	    		customer = super.getCustomerService().createCustomer(customer, super.getJaasUserName());
	    		LOG.debug("Customer id in persiting customer "+customer.getId());
	    		super.getRequestScope().put("customerId", customer.getId());
	    		MobileProfile primaryMobileProfile = null;
	    		
   	    		for (MobileProfile mobileProfile: regInfo.getMobileProfileList()) {
		    		//format mobile number
   	    			
   	    			LOG.debug(" persisting mobile profiles ::::: size "+regInfo.getMobileProfileList().size());
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
		    		
		    		
		    		
		    		mobileProfile.setCustomer(customer);
			    
			    	mobileProfile.setStatus(MobileProfileStatus.AWAITING_APPROVAL);
			    	LOG.debug(" trying to add mobile ::::::::::"+mobileProfile.getMobileNumber());
		    		mobileProfile = super.getCustomerService().createMobileProfile(mobileProfile,SystemConstants.SOURCE_APPLICATION_BANK, super.getJaasUserName());
		    		LOG.debug(" dione trying to add mobile ::::::::::"+mobileProfile.getMobileNumber());
		    	 		
	    		}
	    		int count=0;
	    		for (BankAccount bankAccount: regInfo.getBankAccountList()) {
	    			count++;
	    			LOG.debug(count+">>>>>>>>>>>>>>>bank account creation "+bankAccount.getAccountNumber()+">>>>>>>>>>>"+bankAccount.getAccountName());
	    			ProfileServiceSOAPProxy profileProxy= new ProfileServiceSOAPProxy();
	    			Profile capturerProfile=profileProxy.getProfileByUserName(super.getJaasUserName());
		    		bankAccount.setOwnerType(OwnerType.CUSTOMER);
	    			bankAccount.setAccountHolderId(customer.getId());
		    		bankAccount.setLevel(BankAccountLevel.INDIVIDUAL);
		    		bankAccount.setStatus(BankAccountStatus.AWAITING_APPROVAL);
		    		bankAccount.setAccountName(customer.getLastName() + " " + customer.getFirstNames());
		    		/*
		    		 * setting registration branch
		    		 */
		    		bankAccount.setRegistrationBranchId(regInfo.getCustomer().getBranchId());
		    		bankAccount.setBankAccountLastBranch(capturerProfile.getBranchId());
		    		/*if (BankAccountType.E_WALLET.equals(bankAccount.getType())) {
		    			LOG.debug(">>>>>>>>>>>>>>EWALLET account >>>>>>>>>>>>>"+bankAccount.getAccountNumber()+">>>>>"+bankAccount.getAccountName());
		    			bankAccount.setCode("ewallet");
		    			if(primaryMobileProfile != null) {
		    				bankAccount.setAccountNumber(primaryMobileProfile.getMobileNumber());
		    			} else {
		    				super.setErrorMessage(" No primary mobile number.");
		    				return "failure";
		    			}
		    		}*/
		    		
		    		if (BankAccountType.E_WALLET.equals(bankAccount.getType())) {
		    			//LOG.debug("formatting   nw       "+accountNumber);
		    			String accountNumber =bankAccount.getAccountNumber();
		    			LOG.debug("formatting   nw       "+accountNumber);
		    			accountNumber=NumberUtil.formatMobileNumber(accountNumber);
		    			bankAccount.setAccountNumber(accountNumber);
		    			LOG.debug("formatting   nw       "+accountNumber);
		    		
		    		}
		    		
		    		LOG.debug(">>>>>>>>>>>>>>>>>>>>>creating bankaccount>>>>>>>>>>>>> "+bankAccount.getAccountNumber());
		    		bankAccount = super.getBankService().createCustomerBankAccount(bankAccount,SystemConstants.SOURCE_APPLICATION_BANK, super.getJaasUserName());
		    		LOG.debug(">>>>>>>>>>done>>>>>>>>>>>creating bankaccount>>>>>>>>>>>>>done");
		    		
		    		if(bankAccount != null && BankAccountType.E_WALLET.equals(bankAccount.getType())) {
		    			LOG.debug(">>>>>>>>>> init >>>>>>>>>>> creating account balance >>>>>>>>>>>>>done");
		    			AccountBalance bal = new AccountBalance();
		    			bal.setAccountId(bankAccount.getId());
		    			bal.setBalanceDate(DateUtil.convertToXMLGregorianCalendar(new Date(System.currentTimeMillis())));
		    			bal.setAmount(0l);
		    			bal = new BankServiceSOAPProxy().createAccountBalance(bal);
		    			LOG.debug(">>>>>>>>>> done >>>>>>>>>>> creating account balance >>>>>>>>>>>>>done");
		    		}
		    		//Create Alert Options
		    		super.getAlertsService().createAlertOptionsForAccount(bankAccount.getId(), primaryMobileProfile.getId(), super.getJaasUserName());
	    		} 
	    		
	    		//Upadate Contact Details
	    		//contactDetails = super.getContactService().findContactDetailsById(contactDetails.getId());
	    		LOG.debug("################"+contactDetails);
	    		contactDetails.setOwnerType(OwnerType.CUSTOMER.toString());
	    		contactDetails.setOwnerId(customer.getId());
	    		contactDetails.setContactName(customer.getLastName()+" "+customer.getFirstNames());
	    		//contactDetails.setTelephone(primaryMobileProfile.getMobileNumber());
	    		contactDetails = super.getContactService().createContactDetails(contactDetails,super.getJaasUserName());
	    		
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
		gotoPage("/csr/csrHome.jspx");
		return "cancel";
	}
	
	public String back() {
		gotoPage("/csr/registerCustomer.jspx");
		return "back";
	}
	
}
