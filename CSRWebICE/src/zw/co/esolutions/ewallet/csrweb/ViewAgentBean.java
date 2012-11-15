package zw.co.esolutions.ewallet.csrweb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.csr.msg.MessageAction;
import zw.co.esolutions.ewallet.csr.msg.MessageSync;
import zw.co.esolutions.ewallet.agentservice.service.Agent;
import zw.co.esolutions.ewallet.agentservice.service.AgentServiceSOAPProxy;
import zw.co.esolutions.ewallet.agentservice.service.AgentType;
import zw.co.esolutions.ewallet.agentutil.AgentUtil;
import zw.co.esolutions.ewallet.agentutil.SearchInfo;
import zw.co.esolutions.ewallet.bankservices.service.AccountBalance;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountStatus;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.bankservices.service.Exception_Exception;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetailsServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.CustomerStatus;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfileStatus;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.profileservices.service.ProfileStatus;
import zw.co.esolutions.ewallet.sms.MessageSender;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EncryptAndDecrypt;

public class ViewAgentBean extends PageCodeBase{
	
	private String agentId;
	private Agent agent;
	private Customer customer;
	private List<MobileProfile> mobileProfileList;
	private List<BankAccount> bankAccountList;
	private ContactDetails contactDetails;
	private Date dateOfBirth = new Date();
	private boolean approved;
	private boolean auditor;
	private Agent superAgent;
	private String subAgentId;
	private boolean subAgentTab;
	private List<SearchInfo> subAgentList;
	private boolean deRegisterClient;
	
	private static Logger LOG ;
	
	static{
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(ViewCustomerBean.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + ViewCustomerBean.class);
		}
	}
	
	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}
	
	public String getAgentId() {
		if (agentId == null) {
			agentId = (String) super.getRequestScope().get("agentId");
		}
		if (agentId == null) {
			agentId = (String) super.getRequestParam().get("agentId");
		}
		return agentId;
	}
	
	public Agent getSuperAgent() {
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		if(getAgent().getSuperAgentId() != null){
			superAgent = agentService.findAgentById(getAgent().getSuperAgentId());
		}else{
			superAgent = new Agent();
		}
		return superAgent;
	}

	public void setSuperAgent(Agent superAgent) {
		this.superAgent = superAgent;
	}
	public void setAgent(Agent agent) {
		this.agent = agent;
	}
	
	public Agent getAgent() {
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		
		if (agent == null) {
			if(getAgentId() != null){
				try {
					agent = agentService.findAgentById(agentId);
				} catch (Exception e) {
					e.printStackTrace();
					super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
				}
			}else{
				try {
					String customerId = (String) super.getRequestScope().get("customerId");
					agent = agentService.getAgentByCustomerId(customerId);
				} catch (Exception e) {
					e.printStackTrace();
					super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
				}
			}
			
		}
		return agent;
	}
	
	public Customer getCustomer() {
		
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		
		String customerId = getAgent().getCustomerId();
		try{
			customer = customerService.findCustomerById(customerId);
			
			this.setDeRegisterClient(new ProfileServiceSOAPProxy().canDo(getJaasUserName(), "DEREGISTERCUSTOMER"));

		}catch(Exception e){
			e.printStackTrace();
		}
		
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public void setContactDetails(ContactDetails contactDetails) {
		this.contactDetails = contactDetails;
	}
	
	public ContactDetails getContactDetails() {
		
		ContactDetailsServiceSOAPProxy contactDetailsService = new ContactDetailsServiceSOAPProxy();
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		
		Customer customer = customerService.findCustomerById(getAgent().getCustomerId());
		
		if (contactDetails == null && this.getAgent() != null) {
			try {
				contactDetails = contactDetailsService.findContactDetailsById(customer.getContactDetailsId());
			} catch (Exception e) {
				e.printStackTrace();
				
			}
		}
		return contactDetails;
	}
	public List<MobileProfile> getMobileProfileList() {
		
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		if (mobileProfileList == null && this.getAgentId() != null) {
			mobileProfileList = customerService.getMobileProfileByCustomer(getAgent().getCustomerId());
		}
		return mobileProfileList;
	}
	public void setMobileProfileList(List<MobileProfile> mobileProfileList) {
		this.mobileProfileList = mobileProfileList;
	}
	
	public List<BankAccount> getBankAccountList() {
		
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		
		if (bankAccountList == null && this.getAgentId() != null) {
			try {
				bankAccountList = bankService.getBankAccountByAccountHolderIdAndOwnerType(getAgent().getCustomerId(), 
						OwnerType.AGENT);
			} catch (Exception e) {
				
			}
		}
		return bankAccountList;
	}
	public void setBankAccountList(List<BankAccount> bankAccountList) {
		this.bankAccountList = bankAccountList;
	}
	
	public String approve() {
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		Agent agent = null;
		try {
			String agentId = (String) super.getRequestParam().get("agentId");
	
			agent = agentService.findAgentById(agentId);
			agent = agentService.approveAgent(agent, getJaasUserName());
		
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage(agent.toString() + " approved successfully.");
		return "approve";
	}
	
	public String reject() {
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		Agent agent = null;
		try {
			
			String agentId = (String) super.getRequestParam().get("agentId");
	
			agent = agentService.findAgentById(agentId);
			agent = agentService.rejectAgent(agent,getJaasUserName());
		
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage(agent.toString() + " has been rejected.");
		return "rejected";
	}
	
	public String approveAll() {
		System.out.println("Now running approve agent>>>>>>>>>>>>>>>>>>");
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		ProfileServiceSOAPProxy profileService = getProfileService();
		MobileProfile primaryMobile = null;
		String agentId = (String) super.getRequestParam().get("agentId");
		Profile profile = null;
		try {
			agent = agentService.findAgentById(agentId);
			customer = customerService.findCustomerById(agent.getCustomerId());
			customer.setStatus(CustomerStatus.ACTIVE);
			
//			profile = profileService.findProfileById(agent.getProfileId());
//			//Approving profile to be uncommented when connection to LDAP has been sorted 
//			profile = profileService.approveProfile(profile, getJaasUserName());
			customer = customerService.updateCustomer(customer, super.getJaasUserName());
			agent = agentService.approveAgent(agent, getJaasUserName());
			
			MessageSync.populateAndSync(customer, MessageAction.CREATE);
			
			//Sycning Msg
			MessageSync.populateAndSync(this.getContactDetails(), MessageAction.CREATE);
			
			List<MobileProfile> profiles = customerService.getMobileProfileByCustomer(agent.getCustomerId());
			for (MobileProfile mobileProfile: profiles) {
				if(!mobileProfile.getStatus().name().equals(MobileProfileStatus.ACTIVE.name())){
					mobileProfile.setStatus(MobileProfileStatus.ACTIVE);
					customerService.approveMobileNumber(mobileProfile, super.getJaasUserName());
					String notification = this.getAgentNotification(mobileProfile,agent.getAgentNumber());
					
					System.out.println("Now syncing agent >>>>>>>>>>>>>>>>>>>>>>>>>>"+agent.getStatus().name());
					if(agent.getStatus().name().equals(ProfileStatus.AWAITING_EDIT_APPROVAL.name())){
						MessageSync.populateAndSync(mobileProfile, MessageAction.UPDATE);
					}else{
						MessageSync.populateAndSync(mobileProfile, MessageAction.CREATE, notification);
					}
					
				}
				
				if(mobileProfile.isPrimary()) {
					primaryMobile = mobileProfile;
				}
				mobileProfileList = customerService.getMobileProfileByCustomer(getCustomer().getId());
			}
			List<BankAccount> accounts = bankService.getBankAccountByAccountHolderIdAndOwnerType(getCustomer().getId(), 
					OwnerType.AGENT);
			for (BankAccount account: accounts) {
				account.setStatus(BankAccountStatus.ACTIVE);
				bankService.approveBankAccount(account, getJaasUserName());
				MessageSync.populateAndSync(account, MessageAction.CREATE);
			}
			
			System.out.println("Agent status is "+agent.getStatus().name());
			MessageSync.populateAndSync(agent, MessageAction.CREATE);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		
		setInformationMessage("Agent approved successfuly ");
		return "approveAll";
	}
		
	public String rejectAll() {
		
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		
		String agentId = (String) super.getRequestParam().get("agentId");
		
		try {
			agent = agentService.findAgentById(agentId);
			agent = agentService.rejectAgent(agent, getJaasUserName());
			customer = customerService.findCustomerById(agent.getCustomerId());
			customer.setStatus(CustomerStatus.DISAPPROVED);
			customerService.rejectCustomer(customer, super.getJaasUserName());
			List<MobileProfile> profiles = customerService.getMobileProfileByCustomer(getCustomer().getId());
			for (MobileProfile profile: profiles) {
				profile.setStatus(MobileProfileStatus.DISAPPROVED);
				customerService.rejectMobileNumber(profile,super.getJaasUserName());
			}
			List<BankAccount> accounts = bankService.getBankAccountByAccountHolderIdAndOwnerType(getCustomer().getId(), 
					OwnerType.AGENT);
			for (BankAccount account: accounts) {
				account.setStatus(BankAccountStatus.DISAPPROVED);
				bankService.rejectBankAccount(account, super.getJaasUserName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		setInformationMessage("Agent Rejected ");
		return "rejectAll";
	}
	
	public String approveMobileProfile() {
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		
		String mobileProfileId = (String) super.getRequestParam().get("mobileProfileId");
		
		try {
			MobileProfile mobile = customerService.findMobileProfileById(mobileProfileId);
			mobile.setStatus(MobileProfileStatus.ACTIVE);
			mobile = customerService.approveMobileNumber(mobile, getJaasUserName());
//			MobileService.approveEMobileProfile(mobile, getJaasUserName());
//			MessageSync.populateAndSync(mobile, MessageAction.CREATE);
//			this.notifyCustomer(mobile);
			mobileProfileList = customerService.getMobileProfileByCustomer(getCustomer().getId());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "approveMobileProfile";
	}
	
	public String rejectMobileProfile() {
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		
		String mobileProfileId = (String) super.getRequestParam().get("mobileProfileId");
		
		try {
			MobileProfile mobile = customerService.findMobileProfileById(mobileProfileId);
			mobile.setStatus(MobileProfileStatus.DISAPPROVED);
			mobileProfileList = customerService.getMobileProfileByCustomer(getCustomer().getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "rejectMobileProfile";
	}
	
	public String approveBankAccount() {
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		
		String accountId = (String) super.getRequestParam().get("accountId");
		BankAccount account = bankService.findBankAccountById(accountId);
		account.setStatus(BankAccountStatus.ACTIVE);
		try {
			bankService.editBankAccount(account, super.getJaasUserName());
//			MessageSync.populateAndSync(account, MessageAction.CREATE);
			bankAccountList = bankService.getBankAccountByAccountHolderIdAndOwnerType(getCustomer().getId(), 
					OwnerType.AGENT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "approveBankAccount";
	}
	
	public String rejectBankAccount() {
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		
		String accountId = (String) super.getRequestParam().get("accountId");
		BankAccount account = bankService.findBankAccountById(accountId);
		account.setStatus(BankAccountStatus.DISAPPROVED);
		try { 
			bankService.editBankAccount(account, super.getJaasUserName());
			bankAccountList = bankService.getBankAccountByAccountHolderIdAndOwnerType(getCustomer().getId(), 
					OwnerType.AGENT);
		} catch (Exception_Exception e) {
			e.printStackTrace();
		}
		return "rejectBankAccount";
	}
	public Date getDateOfBirth() {
		if(dateOfBirth == null){
			dateOfBirth = DateUtil.convertToDate(getCustomer().getDateOfBirth());
		}
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	
	public boolean isApproved() {
		if(getCustomer().getStatus().name().equals(ProfileStatus.AWAITING_APPROVAL.name())
				|| getAgent().getStatus().name().equals(ProfileStatus.AWAITING_EDIT_APPROVAL.name())){
			return true;
		}
		return false;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public String ok() {
		this.gotoPage("/csr/searchAgent.jspx");
		return "ok";
	}
	
	@SuppressWarnings("unchecked")
	public String edit() {
		this.gotoPage("/csr/editAgent.jspx");
		getRequestParam().put("agentId", getAgentId());
		return "edit";
	}
	
	public boolean checkProfileAccessRight(String accessRight) {
		
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		
		return profileService.canDo(super.getJaasUserName(), accessRight);
	}
	
	public boolean isAuditor() {
		return auditor;
	}
	
	public void setAuditor(boolean auditor) {
		this.auditor = auditor;
	}
	
	@SuppressWarnings("unchecked")
	public String editMobileProfile() {
		gotoPage("/csr/editMobileProfile.jspx");
		getRequestParam().put("agentId", getAgentId());
		return "editMobileProfile";
	}
	
	@SuppressWarnings("unchecked")
	public String editBankAccount() {
		gotoPage("/csr/editBankAccount.jspx");
		getRequestParam().put("agentId", getAgentId());
		return "editBankAccount";
	}
	
	public String viewLogs() {
		gotoPage("/csr/viewLogs.jspx");
		return "viewLogs";
	}
	
	public String editAccount(){
		gotoPage("/csr/editAccount.jspx");
		return "success";
	}
	
	public boolean isSubAgentTab() {
		try{
			if(getAgent().getAgentType().equals(AgentType.SUPER_AGENT)){
				subAgentTab = true ;
			}else{
				subAgentTab = false ; 
			}
		}catch(Exception e){
			
		}
		return subAgentTab;
	}

	public void setSubAgentTab(boolean subAgentTab) {
		this.subAgentTab = subAgentTab;
	}

	public String getSubAgentId() {
		return subAgentId;
	}

	public void setSubAgentId(String subAgentId) {
		this.subAgentId = subAgentId;
	}
	
	public List<SearchInfo> getSubAgentList() {
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		List<Agent> tmp;
		try{
			Agent a = getAgent();
			if(a.getAgentType().equals(AgentType.SUPER_AGENT)){
				tmp = agentService.getAllSubAgents(a.getId());
				subAgentList = AgentUtil.populateSearchList(tmp);
			}else{
				subAgentList = new ArrayList<SearchInfo>();
			}
			
		}catch(Exception e){
			
		}
		return subAgentList;
	}

	public void setSubAgentList(List<SearchInfo> subAgentList) {
		this.subAgentList = subAgentList;
	}
	
	private String getAgentNotification(MobileProfile mp , String agentNumber) throws Exception{
		String code = EncryptAndDecrypt.decrypt(mp.getSecretCode(), mp.getMobileNumber());
		String message = "Welcome to ZB Mobile Banking.[nl]Your password is " + code + " and your agent number is " +
				agentNumber+".[nl]You can now send your requests to 440.";
		return message;
	}
	
	public String resetMobileProfilePin() {
		String mobileProfileId = (String) super.getRequestParam().get("mobileProfileId");
		MobileProfile mobile = super.getCustomerService().findMobileProfileById(mobileProfileId);
		try {
			mobile = super.getCustomerService().resetMobileProfilePin(mobile, super.getJaasUserName());
			String notification = this.getNotification(mobile);
			MessageSync.populateAndSync(mobile, MessageAction.UPDATE, notification);
			mobileProfileList = super.getCustomerService().getMobileProfileByCustomer(getCustomer().getId());
			super.setInformationMessage("Mobile profile pin has been successfully reset.");
		} catch (Exception e) {
			super.setErrorMessage("Error occurred");
			e.printStackTrace();
		}
		return "";
	}
	
	public String coldMobile(){
		CustomerServiceSOAPProxy customerService= new CustomerServiceSOAPProxy();
		String mobileProfileId = (String) super.getRequestParam().get("mobileProfileId");
		MobileProfile mobile = customerService.findMobileProfileById(mobileProfileId);
	
		try {
			mobile=customerService.coldMobileNumber(mobile, getJaasUserName());
			mobileProfileList = customerService.getMobileProfileByCustomer(getCustomer().getId());
			mobile.setStatus(MobileProfileStatus.ACTIVE);
			MessageSync.populateAndSync(mobile, MessageAction.UPDATE);
			super.setInformationMessage("Mobile profile has been successfully activated.");
		} catch (Exception e) {
			super.setErrorMessage("Error occurred");
			e.printStackTrace();
		}
		return "coldMobile";
	
	}
	public String hotMobile(){
		String mobileProfileId = (String) super.getRequestParam().get("mobileProfileId");
		MobileProfile mobile = super.getCustomerService().findMobileProfileById(mobileProfileId);
	
		try {
			mobile=super.getCustomerService().hotMobileNumber(mobile, getJaasUserName());
			mobileProfileList = super.getCustomerService().getMobileProfileByCustomer(getCustomer().getId());
			mobile.setStatus(MobileProfileStatus.HOT);
			MessageSync.populateAndSync(mobile, MessageAction.UPDATE);
			super.setInformationMessage("Mobile profile has been successfully halted.");
		} catch (Exception e) {
			super.setErrorMessage("Error occurred");
			e.printStackTrace();
		}
		return "hotMobile";
	}
	
	private String getNotification(MobileProfile mp) throws Exception {
		
		String code = EncryptAndDecrypt.decrypt(mp.getSecretCode(), mp.getMobileNumber());
		String message = "Welcome to ZB Mobile Banking.[nl]Your password is " + code + ".[nl]You can now send your requests to 440.";
		return message;
	}
	
	public String deRegisterAgent(){
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		
		if(this.isAccountBalanceAvailable(getCustomer())){
			super.setErrorMessage("Cannot deregister Agent: there is still money in agent account");
			return "failure";
		}
		try {
			LOG.debug("$$$$$        DEREGISTER AGENT..");
			
			agentId = (String) super.getRequestParam().get("agentId");
			LOG.debug("AgentID is : " + agentId);
			
			LOG.debug("BEGIN deregister agent..");
			agent = agentService.deregisterAgent(agentId, getJaasUserName());
			LOG.debug("Agent deregistered.. sync agent now");
			agent.setStatus(zw.co.esolutions.ewallet.agentservice.service.ProfileStatus.DELETED);
			MessageSync.populateAndSync(agent, MessageAction.DELETE);
			
			LOG.debug("BEGIN deregister customer..");
			List<MobileProfile> mobiles = super.getCustomerService().getMobileProfileByCustomer(agent.getCustomerId());
			
			LOG.debug("Mobile profiles found   "+ mobiles);
			customer = customerService.deregisterCustomer(getCustomer(), getJaasUserName());
			
			LOG.debug("Customer and MobileProfiles deregistered.. sync them..");
			customer.setStatus(CustomerStatus.DELETED);
			MessageSync.populateAndSync(customer, MessageAction.DELETE);
			this.syncMobileProfileDeRegistrations(mobiles);

			LOG.debug("Done with customer now working with bank accounts");
			BankServiceSOAPProxy bankService=new BankServiceSOAPProxy();
			List<BankAccount> deletedAccs = bankService.deRegisterBankAccountsByOwnerId(customer.getId(), getJaasUserName());
			this.syncBankDeletes(deletedAccs);
			LOG.debug("All done");
			
			super.setInformationMessage("Agent has been successfully deregistered.");
			
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage("An error has occurred. Operation aborted");
		}
		
		return "";
	}
	
	public void syncBankDeletes(List<BankAccount> deletedAccs) throws Exception{
		for(BankAccount account: deletedAccs){
			account.setStatus(BankAccountStatus.DELETED);
			LOG.debug(">>>>>>>>>account number"+account.getAccountNumber());
			LOG.debug(">>>>>>>>>status"+account.getStatus());
			LOG.debug(">>>>>>>>>id "+account.getStatus());
			
			MessageSync.populateAndSync(account, MessageAction.DELETE);
		}
		LOG.debug("SUCCESSFULLY SYNCED BANKACCOUNTS..");
	}
	
	
	public void syncMobileProfileDeRegistrations(List <MobileProfile> mobiles) throws Exception{
		for(MobileProfile mobile : mobiles ){
			mobile.setStatus(MobileProfileStatus.DELETED);
			LOG.debug(">>>>>>>>>>>>mobile number "+mobile.getMobileNumber());
			LOG.debug(">>>>>>>>>>>status>>>>>>>>>>>>>>"+mobile.getStatus());
			LOG.debug(">>>>>>>>>>>mobile id>>>>>>>>>>>>>>"+mobile.getId());
			
			MessageSync.populateAndSync(mobile, MessageAction.DELETE);
						
		}
		
		LOG.debug("SUCCESSFULLY SYNCED MOBILE PROFILES..");

	}

	public void setDeRegisterClient(boolean deRegisterClient) {
		this.deRegisterClient = deRegisterClient;
	}

	public boolean isDeRegisterClient() {
		return deRegisterClient;
	}

	private boolean isAccountBalanceAvailable(Customer customer){
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		List<BankAccount> bankAccountList = getBankAccountList();
		if(bankAccountList.isEmpty()){
			return false;
		}
		try{
			for(BankAccount ba:bankAccountList){
				AccountBalance balance = bankService.getClosingBalance(ba.getId(),DateUtil.convertToXMLGregorianCalendar(new Date()));
				if(balance == null || balance.getId() == null){
					continue;
				}
				if(balance.getAmount() > 0L){
					return true;
				}
			}
		}catch (Exception e) {
			
		}
		
		return false;
	}
	
}
