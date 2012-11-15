package zw.co.esolutions.ewallet.csrweb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.agentservice.service.Agent;
import zw.co.esolutions.ewallet.agentservice.service.AgentClass;
import zw.co.esolutions.ewallet.agentservice.service.AgentClassStatus;
import zw.co.esolutions.ewallet.agentservice.service.AgentLevel;
import zw.co.esolutions.ewallet.agentservice.service.AgentServiceSOAPProxy;
import zw.co.esolutions.ewallet.agentservice.service.AgentType;
import zw.co.esolutions.ewallet.agentutil.AgentUtil;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.CustomerAutoRegStatus;
import zw.co.esolutions.ewallet.customerservices.service.CustomerClass;
import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.Gender;
import zw.co.esolutions.ewallet.customerservices.service.MaritalStatus;
import zw.co.esolutions.ewallet.customerservices.service.MobileNetworkOperator;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.enums.MobileProfileStatus;
import zw.co.esolutions.ewallet.enums.Title;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.profileservices.service.Role;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.JsfUtil;
import zw.co.esolutions.ewallet.util.NumberUtil;

public class CreateAgentBean extends PageCodeBase{
	private Agent agent = new Agent();
	private ContactDetails contactDetails = new ContactDetails();
	private Customer customer = new Customer();
	private MobileProfile mobileProfile1=new MobileProfile();
	private MobileProfile mobileProfile2=new MobileProfile();
	private MobileProfile mobileProfile3=new MobileProfile();
	private Profile profile = new Profile();
	private BankAccount bankAccount = new BankAccount();
	private List<SelectItem> agentTypes;
	private Date dateOfBirth;
	private String agentMobileNumber;

	private List<SelectItem> titleList;
	private String selectedTitle;
	private List<SelectItem> genderList;
	private String selectedGender;
	private List<SelectItem> networkList;
	private String selectedType;
	private List<SelectItem> classList;
	private String selectedClass;
//	private List<MobileProfile> mobileProfileList;
	
	private String selectedNetwork1;
	private String selectedNetwork2;
	private String selectedNetwork3;
	private RegInfo regInfo;
	private String country;

	public RegInfo getRegInfo() {
		if (regInfo == null) {
			regInfo = new RegInfo();
			regInfo.setAgent(agent);
			regInfo.setCustomer(customer);
			regInfo.setContactDetails(contactDetails);
			regInfo.setBankAccountList(new ArrayList<BankAccount>());
			regInfo.setMobileProfileList(new ArrayList<MobileProfile>());
			regInfo.setProfile(profile);
		}
		return regInfo;
	}

	public void setRegInfo(RegInfo regInfo) {
		this.regInfo = regInfo;
	}
	
	public String getAgentNumber() {
		String agentNumber = null;
		if (agentNumber == null) {
			agentNumber = (String) super.getRequestScope().get("agentNumber");
		}
		if (agentNumber == null) {
			agentNumber = (String) super.getRequestParam().get("agentNumber");
		}
		System.out.println("The value of the agentNumber is "+agentNumber);

		return agentNumber;
	}
	
	public String getCountry() {
		country = "Zimbabwe";
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
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
	
	public String getSelectedType() {
		return selectedType;
	}

	public void setSelectedType(String selectedType) {
		this.selectedType = selectedType;
	}

	public MobileProfile getMobileProfile1() {
		return mobileProfile1;
	}

	public void setMobileProfile1(MobileProfile mobileProfile1) {
		this.mobileProfile1 = mobileProfile1;
	}

	public MobileProfile getMobileProfile2() {
		return mobileProfile2;
	}

	public void setMobileProfile2(MobileProfile mobileProfile2) {
		this.mobileProfile2 = mobileProfile2;
	}

	public MobileProfile getMobileProfile3() {
		return mobileProfile3;
	}

	public void setMobileProfile3(MobileProfile mobileProfile3) {
		this.mobileProfile3 = mobileProfile3;
	}

	public BankAccount getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}
	
	public String getAgentMobileNumber() {
		return agentMobileNumber;
	}

	public void setAgentMobileNumber(String agentMobileNumber) {
		this.agentMobileNumber = agentMobileNumber;
	}

	public List<SelectItem> getAgentTypes() {
		
		if(this.agentTypes == null) {
			this.agentTypes = new ArrayList<SelectItem>();
			this.agentTypes.add(new SelectItem("none", "--select--"));
			for(AgentType aType : AgentUtil.getAgentTypeArray()) {
				this.agentTypes.add(new SelectItem(aType.toString(), aType.toString().replace("_", " ")));
			}
		}
		return agentTypes;
	}

	public Profile getProfile() {
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		if(profile == null){
			profile = profileService.getProfileByUserName(getJaasUserName());
		}
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
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
		if(genderList==null){
			genderList=JsfUtil.getSelectItemsAsList(Gender.values(), true);
		}
		
		return genderList;
	}
	
	public void setGenderList(List<SelectItem> genderList) {
		this.genderList = genderList;
	}
	
	public List<SelectItem> getClassList() {
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		if (classList == null) {
			classList = new ArrayList<SelectItem>();
			classList.add(new SelectItem("none", "--Select--"));
			try {
				List<AgentClass> result = filterAgents(agentService.getAllAgentClasses());
//				List<AgentClass> result = agentService.getAllAgentClasses();
				if (result != null) {
					for (AgentClass agentClass: result) {
						classList.add(new SelectItem(agentClass.getId(), agentClass.getName()));
					}
				}
			} catch(Exception e) {
				super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			}
		}
		return classList;
	}

	public void setClassList(List<SelectItem> classList) {
		this.classList = classList;
	}


	public String getSelectedClass() {
		return selectedClass;
	}


	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public ContactDetails getContactDetails() {
		return contactDetails;
	}

	public void setContactDetails(ContactDetails contactDetails) {
		this.contactDetails = contactDetails;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getSelectedGender() {
		return selectedGender;
	}

	public void setSelectedGender(String selectedGender) {
		this.selectedGender = selectedGender;
	}

	public List<SelectItem> getNetworkList() {
		if(networkList==null){
			networkList=JsfUtil.getSelectItemsAsList(MobileNetworkOperator.values(),true);
		}
		return networkList;
	}

	public void setNetworkList(List<SelectItem> networkList) {
		this.networkList = networkList;
	}

	public void setAgentTypes(List<SelectItem> agentTypes) {
		this.agentTypes = agentTypes;
	}
	
	public Date getDateOfBirth() {
		dateOfBirth = (dateOfBirth == null)? new Date() : dateOfBirth;
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	private List<AgentClass> filterAgents(List<AgentClass> classList){
		List<AgentClass> result = new ArrayList<AgentClass>();
		for(AgentClass agentClass : classList){
			if(agentClass.getStatus().equals(AgentClassStatus.ACTIVE)){
				result.add(agentClass);
			}
		}
		return result;
	}
	
	public String cancel() {
		super.gotoPage("/csr/csrHome.jspx");
		return "cancel";
	}
	
	public String next() {
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		
		if(selectedType.equals("none")){
			setErrorMessage("Select Agent Type");
			return "failure";
		}
		if(selectedClass.equals("none")){
			setErrorMessage("Select Agent Class");
			return "failure";
		}

		if(selectedNetwork1.equals("none")){
			setErrorMessage("Select Mobile Network");
			return "failure";
		}
		
		try{
			checkExistence(getRegInfo().getAgent());
		}catch(Exception e ){
			super.setErrorMessage("Agent already exists !");
			return "failure";
		}
		
		try {				
			if(validateFields()){
			//Setting contact details 
				getRegInfo().getContactDetails().setCountry(getCountry());
			//Setting variable for agent
				
				if(getSelectedGender()== null){
					getRegInfo().getCustomer().setGender(Gender.UNSPECIFIED);
				}else{
					getRegInfo().getCustomer().setGender(Gender.valueOf(selectedGender));
				}
				if(getRegInfo().getAgent().getAgentName()== null){
					getRegInfo().getCustomer().setTitle(Title.valueOf(selectedTitle).name());
					getRegInfo().getCustomer().setBranchId(getProfile().getBranchId());
					getRegInfo().getCustomer().setMaritalStatus(MaritalStatus.UNSPECIFIED);
					getRegInfo().getCustomer().setCustomerClass(CustomerClass.AGENT);
					getRegInfo().getCustomer().setCustomerAutoRegStatus(CustomerAutoRegStatus.NEVER); 
					getRegInfo().getCustomer().setDateOfBirth(DateUtil.convertToXMLGregorianCalendar(dateOfBirth));
					
					getRegInfo().getAgent().setAgentLevel(AgentLevel.INDIVIDUAL);
					String agentName = customer.getLastName()+" "+customer.getFirstNames();
					getRegInfo().getAgent().setAgentName(agentName.toUpperCase());
					
					getRegInfo().getContactDetails().setContactName(agentName.toUpperCase());
					
				}else{
					getRegInfo().getAgent().setAgentLevel(AgentLevel.CORPORATE);
					getRegInfo().getCustomer().setFirstNames(agent.getAgentName());
					getRegInfo().getCustomer().setLastName(agent.getAgentName());
					getRegInfo().getContactDetails().setContactName(agent.getAgentName());
				}
				
				if(selectedType == null){
					getRegInfo().getAgent().setAgentType(AgentType.SUB_AGENT);
					Agent superAgent = agentService.getAgentByAgentNumber(getAgentNumber());
					getRegInfo().getAgent().setSuperAgentId(superAgent.getId());
				}else{
					getRegInfo().getAgent().setAgentType(AgentType.valueOf(selectedType));
				}
				AgentClass agentClass = agentService.findAgentClassById(selectedClass);
				getRegInfo().getAgent().setAgentClass(agentClass);
				
			//Setting variable for profile 
					getRegInfo().getProfile().setFirstNames(customer.getFirstNames());
					getRegInfo().getProfile().setLastName(customer.getLastName());
					getRegInfo().getProfile().setUserName(mobileProfile1.getMobileNumber());
					getRegInfo().getProfile().setMobileNumber(NumberUtil.formatMobileNumber(mobileProfile1.getMobileNumber()));
	    			//profile.setId(GenerateKey.generateEntityId());
					getRegInfo().getProfile().setChangePassword(false);
	
					if(getRegInfo().getAgent().getAgentType().equals(AgentType.SUPER_AGENT)){
						Role role = profileService.getRoleByRoleName("SUPER_AGENT");
						getRegInfo().getProfile().setRole(role);					
					}else if(getRegInfo().getAgent().getAgentType().equals(AgentType.AGENT)){
						Role role = profileService.getRoleByRoleName("INDIVIDUAL_AGENT");
						getRegInfo().getProfile().setRole(role);
					}else if(getRegInfo().getAgent().getAgentType().equals(AgentType.SUB_AGENT)){
						Role role = profileService.getRoleByRoleName("SUB_AGENT");
						getRegInfo().getProfile().setRole(role);
					}
				
				if (mobileProfile1.getMobileNumber() != null && !mobileProfile1.getMobileNumber().trim().equals("")) {
					mobileProfile1.setNetwork(MobileNetworkOperator.valueOf(selectedNetwork1));
					mobileProfile1.setPrimary(true);
					getRegInfo().getMobileProfileList().add(mobileProfile1);
				}
				MobileProfile mobileProfile = regInfo.getMobileProfileList().get(0);
		    		//format mobile number
		    		try {
		    			mobileProfile.setMobileNumber(NumberUtil.formatMobileNumber(mobileProfile.getMobileNumber()));
		    			//check existence
		    			MobileProfile mobileCheck = customerService.getMobileProfileByMobileNumber(mobileProfile.getMobileNumber());
		    			if(mobileCheck!=null && mobileCheck.getMobileNumber() != null && !mobileCheck.getStatus().toString().equalsIgnoreCase(MobileProfileStatus.DELETED.toString())){
		    				super.setErrorMessage("Mobile Number " + mobileProfile.getMobileNumber() + " is already registered.");
		    				return "failure";
		    			}
		    		} catch (Exception e) {
		    			e.printStackTrace();
						super.setErrorMessage("Mobile Number is not in correct format.");
						return "failure";
					}
				
				this.putVariablesInSessionScope();
			}	
		} catch(Exception e) {
			e.printStackTrace(System.out);
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		gotoPage("/csr/createAgent2.jspx");
		return "next";
	}
	
	@SuppressWarnings("unchecked")
	public void putVariablesInSessionScope() {
		super.getSessionScope().put("regInfo", getRegInfo());
	}
	
	public void processValueChange(ValueChangeEvent event) {
		String agentType = (String) event.getNewValue();
		if(agentType.equals("SUPER_AGENT")){
		
		}else{
			
		}
	}
	
	private void checkExistence(Agent agent) throws Exception{
		
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		Profile profile = null;
		
//		if(agent.getAgentName()==null){
		System.out.println("Check mobile number "+mobileProfile1.getMobileNumber());
//			profile = profileService.getProfileByUserName(mobileProfile1.getMobileNumber());
//		}else{
//			profile = profileService.getProfileByUserName(agent.getAgentName());
//		}
		
//		if(profile != null && profile.getId()!= null){
//			throw new Exception("Profile already exists !!!");	
//		}		
	}
	
	private boolean validateFields(){
		if(selectedClass.equals("none")){
			setInformationMessage("Enter Agent Class");
			return false;
		}
		if(selectedTitle.equals("none")){
			setInformationMessage("Enter Title");
			return false;
		}
		if(selectedType.equals("none")){
			setInformationMessage("Enter selected Type");
			return false;
		}
//		if(selectedGender.equals("none")){
//			setInformationMessage("Enter selected Gender");
//			return false;
//		}
		if(selectedNetwork1.equals("none")){
			setInformationMessage("Enter Mobile Network");
			return false;
		}
		return true;
	}
}
