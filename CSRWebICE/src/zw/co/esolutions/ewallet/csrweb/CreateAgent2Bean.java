package zw.co.esolutions.ewallet.csrweb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.agentservice.service.Agent;
import zw.co.esolutions.ewallet.agentservice.service.AgentLevel;
import zw.co.esolutions.ewallet.agentservice.service.AgentServiceSOAPProxy;
import zw.co.esolutions.ewallet.agentservice.service.AgentType;
import zw.co.esolutions.ewallet.agentservice.service.ProfileStatus;
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
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetailsServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.CustomerClass;
import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.CustomerStatus;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfileStatus;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.GenerateKey;
import zw.co.esolutions.ewallet.util.NumberUtil;
import zw.co.esolutions.ewallet.util.SystemConstants;

public class CreateAgent2Bean extends PageCodeBase {
	
	private RegInfo regInfo;
	private BankAccount transactionAccount = new BankAccount();
	private BankAccount commisionAccount = new BankAccount();
	private BankAccount account = new BankAccount();
	private String selectedBranch1;
	private Bank bank;
	private String agentId;
	private String customerId;
	private String accountName;
	private String branchName;
	private String selectedAccountType;
	private String selectedBranch;
	private List<SelectItem> branchList;
	
	private List<SelectItem> accountTypeList;
	
	public CreateAgent2Bean() {
		super();
	}
	
	public BankAccount getTransactionAccount() {
		String accountNumber = getRegInfo().getMobileProfileList().get(0).getMobileNumber();
		
		transactionAccount.setCode("ewallet");
		transactionAccount.setAccountName(getAccountName().toUpperCase());
		transactionAccount.setPrimaryAccount(true);
		transactionAccount.setType(BankAccountType.AGENT_EWALLET);
		transactionAccount.setAccountNumber(accountNumber);
		
		return transactionAccount;
	}
	
	public void setTransactionAccount(BankAccount transactionAccount) {
		this.transactionAccount = transactionAccount;
	}
	public BankAccount getCommisionAccount() {
		commisionAccount.setType(BankAccountType.AGENT_COMMISSION_SUSPENSE);
		commisionAccount.setAccountName(getAccountName().toUpperCase());
		
		return commisionAccount;
	}
	public void setCommisionAccount(BankAccount commisionAccount) {
		this.commisionAccount = commisionAccount;
	}
	
	
	public BankAccount getAccount() {
		account.setAccountName(getAccountName().toUpperCase());
		return account;
	}

	public void setAccount(BankAccount account) {
		this.account = account;
	}

	public String getAgentId() {
		return agentId;
	}
	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}
	
	public String getAccountName() {
		if(accountName == null){
			accountName = getRegInfo().getAgent().getAgentName();
		}
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	
	public List<SelectItem> getAccountTypeList() {
		if (accountTypeList == null) {
			accountTypeList = new ArrayList<SelectItem>();
			accountTypeList.add(new SelectItem("none", "--select--"));
			accountTypeList.add(new SelectItem("SAVINGS", "SAVINGS"));
			accountTypeList.add(new SelectItem("CHEQUE", "CHEQUE"));
			accountTypeList.add(new SelectItem("CURRENT", "CURRENT"));
		}
		return accountTypeList;
	}
	public void setAccountTypeList(List<SelectItem> accountTypeList) {
		this.accountTypeList = accountTypeList;
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
	public String getSelectedAccountType() {
		return selectedAccountType;
	}
	public void setSelectedAccountType(String selectedAccountType) {
		this.selectedAccountType = selectedAccountType;
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

	public String getSelectedBranch1() {
		
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		
		try{
			Profile profile = profileService.getProfileByUserName(super.getJaasUserName());
			BankBranch b = bankService.findBankBranchById(profile.getBranchId());
			selectedBranch1 = b.getId();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return selectedBranch1;
	}
	public void setSelectedBranch1(String selectedBranch1) {
		this.selectedBranch1 = selectedBranch1;
	}
	
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public void setBank(Bank bank) {
		this.bank = bank;
	}
	public Bank getBank() {
		
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		
		if (bank == null) {
			Profile profile = profileService.getProfileByUserName(super.getJaasUserName());
			bank = bankService.findBankBranchById(profile.getBranchId()).getBank();
		}
		return bank;
	}
	
	public Profile getUserProfile(){
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
	
		Profile profile = profileService.getProfileByUserName(super.getJaasUserName());
		
		return profile;
	}
	
	public String getBranchName() {
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		
		if (branchName == null) {
			Profile profile = profileService.getProfileByUserName(super.getJaasUserName());
			branchName = bankService.findBankBranchById(profile.getBranchId()).getName();
		}
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public void processValueChange(ValueChangeEvent event) {
		String branch = (String) event.getNewValue();
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		setBranchName(bankService.findBankBranchById(branch).getName());
	}
	
	@SuppressWarnings("unchecked")
	public String submit() {
		
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		
		if (getRegInfo() == null) {
			super.setErrorMessage("Error: Agent details are null.");
			return "failure";
		}
		if (!"none".equals(selectedBranch1)) {
				transactionAccount.setBranch(bankService.findBankBranchById(selectedBranch1));
		} else {
			super.setErrorMessage("Please select the branch for primary account.");
			return "failure";
		}
		
		if(commisionAccount.getAccountNumber()==null || commisionAccount.getAccountNumber().equals("")){
			super.setErrorMessage("Please enter commission account number");
			return "failure";
		}
		if(account.getAccountNumber()==null || account.getAccountNumber().equals("")){
			super.setErrorMessage("Please enter bank account number");
			return "failure";
		}
		if(selectedAccountType.equals("none")){
			super.setErrorMessage("Please select bank account type");
			return "failure";
		}
		
		if(account.getAccountNumber().equals(commisionAccount.getAccountNumber())){
			super.setErrorMessage("Please enter different account numbers ");
			return "failure";
		}
		if(transactionAccount.getAccountNumber().equals(account.getAccountNumber())){
			super.setErrorMessage("Please enter different account number for Bank Account");
			return "failure";
		}
		if(transactionAccount.getAccountNumber().equals(commisionAccount.getAccountNumber())){
			super.setErrorMessage("Please enter different account number for Commission Account");
			return "failure";
		}
		commisionAccount.setBranch(transactionAccount.getBranch());
		getRegInfo().getBankAccountList().add(transactionAccount);
		getRegInfo().getBankAccountList().add(commisionAccount);
		getRegInfo().getBankAccountList().add(account);
		//Bank account check
		for (BankAccount bankAccount: regInfo.getBankAccountList()) {
			System.out.println(">>>>>>>>>>>>>>>> List Size is "+regInfo.getBankAccountList().size());
    		BankAccount b = bankService.getUniqueBankAccountByAccountNumber(bankAccount.getAccountNumber());
    		if(b!= null && b.getId() != null && !b.getStatus().toString().equalsIgnoreCase(BankAccountStatus.DELETED.toString())){
    			setErrorMessage("Account number with number "+b.getAccountNumber()+" already exists!");
    			getRegInfo().getBankAccountList().clear();
    			return "failure";
    		}
   
		} 
		//Do registration
		String result = this.registerAgent(regInfo);
		if(ResponseCode.E777.name().equals(result)){
			super.setInformationMessage("Agent registration failed !");
			return "failure";
		}
		super.setInformationMessage("Agent has been registered successfully.");
		Agent agent = getRegInfo().getAgent();
		super.cleanUpSessionScope();
		
		super.getRequestScope().put("agentId",agentId);
		gotoPage(nextPage(agent));
		return "submit";
	}
	
	public String registerAgent(RegInfo regInfo) {
		
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		ContactDetailsServiceSOAPProxy contactDetailsService = new ContactDetailsServiceSOAPProxy();
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		Profile profile = null;
		Customer customer = null;
		Agent agent = null;
		ContactDetails contactDetails = null;
		
		
    	try {
    		
    		 customerId = GenerateKey.generateEntityId();
    		 agentId = GenerateKey.generateEntityId();
    		 
    		if (regInfo != null) {
    			
    			profile = regInfo.getProfile();
    			customer = regInfo.getCustomer();
    			agent = regInfo.getAgent();
    			contactDetails = regInfo.getContactDetails();
    			
    			profile.setBranchId(agentId);
    			profile.setId(null);
//	    		profile = profileService.createProfile(profile, getJaasUserName());
	    		
	    		
//	    		contactDetails.setTelephone(primaryMobileProfile.getMobileNumber());
	    		contactDetails = contactDetailsService.createContactDetails(contactDetails,super.getJaasUserName());
	    		
	    		agent.setId(agentId);
	    		agent.setProfileId(profile.getId());
	    		agent.setCustomerId(customerId);
	    		agent.setAgentNumber(agentService.generateAgentNumber());
	    		agent.setStatus(ProfileStatus.AWAITING_APPROVAL);
	    		agentService.createAgent(agent, getJaasUserName());
	    		
	    		customer.setId(customerId);
	    		customer.setContactDetailsId(contactDetails.getId());
	    		customer.setStatus(CustomerStatus.AWAITING_APPROVAL);
	    		customer.setCustomerClass(CustomerClass.AGENT);
	    		customer.setBranchId(getUserProfile().getBranchId());
	    		customer = customerService.createCustomer(customer, super.getJaasUserName());
	    			   
	    		MobileProfile primaryMobileProfile = null;
	    		
   	    		for (MobileProfile mobileProfile: regInfo.getMobileProfileList()) {
		    		//format mobile number
		    		try {
		    			mobileProfile.setMobileNumber(NumberUtil.formatMobileNumber(mobileProfile.getMobileNumber()));
		    			mobileProfile.setBankId(this.getBank().getId());
		    			Customer c = customerService.findCustomerById(customerId);
			    		mobileProfile.setCustomer(c);
		    			if(mobileProfile != null) {
		    				if(mobileProfile.isPrimary()) {
		    					primaryMobileProfile = mobileProfile;
		    				}
		    			}
		    		} catch (Exception e) {
						super.setErrorMessage("Mobile Number is not in correct format.");
						e.printStackTrace();
						return "failure";
					}
		    		
			    	mobileProfile.setBranchId(getSelectedBranch1());
			    	mobileProfile.setStatus(MobileProfileStatus.AWAITING_APPROVAL);
		    		mobileProfile = customerService.createMobileProfile(mobileProfile, SystemConstants.SOURCE_APPLICATION_BANK, super.getJaasUserName());
		    	 		
	    		}
	    		
   	    		System.out.println(">>>> Bank Acc list size is "+ regInfo.getBankAccountList().size());
   	    		account.setType(BankAccountType.valueOf(selectedAccountType));
	    		for (BankAccount bankAccount: regInfo.getBankAccountList()) {
		    		bankAccount.setOwnerType(OwnerType.AGENT);
		    		bankAccount.setAccountClass(BankAccountClass.NONE);
	    			bankAccount.setAccountHolderId(customerId);
	    			BankBranch branch = bankService.findBankBranchById(getUserProfile().getBranchId());
	    			bankAccount.setBranch(branch);
	    			
	    			if(agent.getAgentType().equals(AgentLevel.INDIVIDUAL)){
	    				bankAccount.setLevel(BankAccountLevel.INDIVIDUAL);
//	    				bankAccount.setAccountName(customer.getLastName() + " " + customer.getFirstNames());
	    			}else{
	    				bankAccount.setLevel(BankAccountLevel.CORPORATE);
	    			}
		    		
		    		bankAccount.setStatus(BankAccountStatus.AWAITING_APPROVAL);		    		
		    		bankAccount.setRegistrationBranchId(regInfo.getCustomer().getBranchId());
		    	
		    		bankAccount = bankService.createBankAccount(bankAccount, super.getJaasUserName());
		   
	    		} 
	    		
	    		//Upadate Contact Details
	    		contactDetails = contactDetailsService.findContactDetailsById(contactDetails.getId());
	    		contactDetails.setOwnerType(OwnerType.AGENT.toString());
	    		contactDetails.setOwnerId(customer.getId());
	    		contactDetails = contactDetailsService.updateContactDetails(contactDetails);
    		} else {
    			return ResponseCode.E715.name();
    		}
    		
    	} catch (Exception e) {
    		e.printStackTrace(System.out);
    		System.out.println("##############################3In catch close "+contactDetails.getId()!= null);
    		try{
    			if(contactDetails.getId()!= null){
    				contactDetailsService.deleteContactDetails(contactDetails);
    				System.out.println("################### reversed contact details");
    			}
    			if(profile.getId()!=null){
    				profileService.deleteProfile(profile, getJaasUserName());
    				System.out.println("################### reversed profile");
    			}
    			Agent a = agentService.findAgentById(agentId);
    			if(a!= null && a.getId() != null){
    				System.out.println("##############  One");
    				if(!a.getAgentNumber().equals(null)){
    					System.out.println("##############  Two");
    					agentService.deleteAgent(a, getJaasUserName());
    					System.out.println("################### reversed agent");
    				}
    			}
    			Customer c = customerService.findCustomerById(customerId);
    			if(c!= null ){
    				if(!c.getId().equals(null)){
    					customerService.deleteCustomer(c, getJaasUserName());
    					System.out.println("################### reversed customer");
    					List<MobileProfile> pList = customerService.getMobileProfileByCustomer(customerId);
    					if(pList != null){
    						if(!pList.isEmpty()){
    							customerService.deleteMobileProfile(pList.get(0), getJaasUserName());
    							System.out.println("################### reversed mobile profile");
    						}
    					}
    					
    					List<BankAccount> bList = bankService.getBankAccountByAccountHolderIdAndOwnerType(customerId, OwnerType.AGENT);
    					if(bList != null){
    						if(!bList.isEmpty()){
    							for(BankAccount ba : bList){
    								bankService.deleteBankAccount(ba, getJaasUserName());
    								System.out.println("################### reversed bank account");
    							}
    						}
    					}
    				}
    			}
    			cleanUpSessionScope();
    		}catch(Exception ex){
    			
    		}
			
			return ResponseCode.E777.name();
		}
    	return ResponseCode.E000.name();
	}
	
	public String cancel() {
		super.cleanUpSessionScope();
		gotoPage("/csr/adminHome.jspx");
		return "cancel";
	}
	
	public String getSelectedBranch() {
		return selectedBranch;
	}

	public void setSelectedBranch(String selectedBranch) {
		this.selectedBranch = selectedBranch;
	}

	public String back() {
		
		Agent agent = getRegInfo().getAgent();
		String page = null;
		
		if(AgentLevel.INDIVIDUAL.equals(agent.getAgentLevel())){
			if(AgentType.SUB_AGENT.equals(agent.getAgentType())){
				page = "/csr/createIndSubAgent.jspx";
			}else{
				page = "/csr/createAgent.jspx";
			}
		}else if(AgentLevel.CORPORATE.equals(agent.getAgentLevel())){
			if(AgentType.SUB_AGENT.equals(agent.getAgentType())){
				page = "/csr/createCorporateSubAgent.jspx";
			}else{
				page = "/csr/createCorporateAgent.jspx";
			}
		}
		cleanUpSessionScope();
		gotoPage(page);
		return "back";
	}
	
	private String nextPage(Agent agent){
		String page = null;
		if(AgentLevel.INDIVIDUAL.equals(agent.getAgentLevel())){
			if(AgentType.SUB_AGENT.equals(agent.getAgentType())){
				page = "/csr/viewIndSubAgent.jspx";
			}else{
				page = "/csr/viewAgent.jspx";
			}
		}else if(AgentLevel.CORPORATE.equals(agent.getAgentLevel())){
			if(AgentType.SUB_AGENT.equals(agent.getAgentType())){
				page = "/csr/viewCorporateSubAgent.jspx";
			}else{
				page = "/csr/viewCorporateAgent.jspx";
			}
		}
		return page;
	}
	
}
