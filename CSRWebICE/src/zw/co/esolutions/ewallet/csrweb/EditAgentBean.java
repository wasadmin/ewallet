package zw.co.esolutions.ewallet.csrweb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.agentservice.service.Agent;
import zw.co.esolutions.ewallet.agentservice.service.AgentServiceSOAPProxy;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetailsServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.CustomerStatus;
import zw.co.esolutions.ewallet.enums.Title;
import zw.co.esolutions.ewallet.customerservices.service.Gender;
import zw.co.esolutions.ewallet.agentservice.service.AgentClass;
import zw.co.esolutions.ewallet.agentservice.service.AgentClassStatus;
import zw.co.esolutions.ewallet.agentservice.service.AgentType;
import zw.co.esolutions.ewallet.agentservice.service.ProfileStatus;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.JsfUtil;

public class EditAgentBean extends PageCodeBase{
	
	
	private String agentId;
	private Agent agent;
	private Customer customer;
	private ContactDetails contactDetails;
	private List<SelectItem> agentTypeList;
	private List<SelectItem> agentTypes;
	private Date dateOfBirth;
	private List<SelectItem> titleList;
	private List<SelectItem> genderList;
	private String selectedGender;
	private String selectedTitle;
	private String selectedAgentType;
	private List<SelectItem> classList;
	private String selectedClass;
	
	public ContactDetails getContactDetails() {
		
		ContactDetailsServiceSOAPProxy contactDetailsService = new ContactDetailsServiceSOAPProxy();
		
		if (contactDetails == null || contactDetails.getId() == null) {
			if (this.getAgentId() != null) {
				try {
					contactDetails = contactDetailsService.findContactDetailsById(getCustomer().getContactDetailsId());
				} catch (Exception e) {
					e.printStackTrace();
					
				}
			} else {
				contactDetails = new ContactDetails();
			}
		} 
		return contactDetails;
	}
	public void setContactDetails(ContactDetails contactDetails) {
		this.contactDetails = contactDetails;
	}
	public String getAgentId() {
		return agentId;
	}
	
	public Customer getCustomer() {
		
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		try{
			agent = agentService.findAgentById(getAgentId());
			customer = customerService.findCustomerById(agent.getCustomerId());
			selectedClass = getAgent().getAgentClass().getId();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public List<SelectItem> getClassList() {
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		if (classList == null) {
			classList = new ArrayList<SelectItem>();
			classList.add(new SelectItem("none", "--Select--"));
			try {
				List<AgentClass> result = filterAgents(agentService.getAllAgentClasses());
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

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public String getSelectedClass() {
		return selectedClass;
	}
	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}
	public Agent getAgent() {
		
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		try{
			if (agent == null || agent.getId() == null) {
				if (this.getAgentId() != null) {
					agent = agentService.findAgentById(getAgentId());
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return agent;
	}
	public void setAgent(Agent agent) {
		this.agent = agent;
	}
	
	public String getSelectedGender() {
		selectedGender = getCustomer().getGender().name();
		return selectedGender;
	}
	public void setSelectedGender(String selectedGender) {
		this.selectedGender = selectedGender;
	}
	public String getSelectedAgentType() {
		selectedAgentType = getAgent().getAgentType().name();
		return selectedAgentType;
	}
	public void setSelectedAgentType(String selectedAgentType) {
		this.selectedAgentType = selectedAgentType;
	}
	public List<SelectItem> getAgentTypes() {
		if(this.agentTypes == null) {
			this.agentTypes = new ArrayList<SelectItem>();
			this.agentTypes.add(new SelectItem("none", "--select--"));
			for(AgentType aType : this.getAgentTypeArray()) {
				this.agentTypes.add(new SelectItem(aType.toString(), aType.toString().replace("_", " ")));
			}
		}
		return agentTypes;
	}
	public void setAgentTypes(List<SelectItem> agentTypes) {
		this.agentTypes = agentTypes;
	}
	public List<SelectItem> getAgentTypeList() {
		agentTypeList = JsfUtil.getSelectItemsAsList(AgentType.values(), true);
		return agentTypeList;
	}
	
	public void setAgentTypeList(List<SelectItem> agentTypeList) {
		this.agentTypeList = agentTypeList;
	}
	
	public void doEditAction(ActionEvent event) {
		agentId = (String) event.getComponent().getAttributes().get("agentId");
	}
	
	public Date getDateOfBirth() {
		if(dateOfBirth==null){
			dateOfBirth = DateUtil.convertToDate(customer.getDateOfBirth());
		}
		return dateOfBirth;
	}
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	public List<SelectItem> getTitleList() {
		if (titleList == null) {
			titleList = JsfUtil.getSelectItemsAsList(Title.values(), true);
		}
		return titleList;
	}
	public void setTitleList(List<SelectItem> titleList) {
		this.titleList = titleList;
	}
	public String getSelectedTitle() {
		selectedTitle = getCustomer().getTitle();
		return selectedTitle;
	}
	public void setSelectedTitle(String selectedTitle) {
		this.selectedTitle = selectedTitle;
	}
	public List<SelectItem> getGenderList() {
		if (genderList == null) {
			genderList = JsfUtil.getSelectItemsAsList(Gender.values(), true);
		}
		return genderList;
	}
	public void setGenderList(List<SelectItem> genderList) {
		this.genderList = genderList;
	}
	
	@SuppressWarnings("unchecked")
	public String submit() {
		
		if(selectedAgentType.equals("none")){
			setErrorMessage("Select Agent Type");
			return "failure";
		}
		if(selectedClass.equals("none")){
			setErrorMessage("Select Class");
			return "failure";
		}
		if(selectedGender.equals("none")){
			setErrorMessage("Select Gender");
			return "failure";
		}
		if(selectedTitle.equals("none")){
			setErrorMessage("Select Title");
			return "failure";
		}
		
		ContactDetailsServiceSOAPProxy contactDetailsService = new ContactDetailsServiceSOAPProxy();
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		
		try {

			contactDetails = contactDetailsService.updateContactDetails(getContactDetails());
			customer.setDateOfBirth(DateUtil.convertToXMLGregorianCalendar(getDateOfBirth()));
			getCustomer().setStatus(CustomerStatus.AWAITING_APPROVAL);
			this.getAgent().setAgentType(AgentType.valueOf(selectedAgentType));
			this.getCustomer().setTitle(selectedTitle);
			this.getCustomer().setGender(Gender.valueOf(selectedGender));
			agent.setStatus(ProfileStatus.AWAITING_EDIT_APPROVAL);
			
			
			customer = super.getCustomerService().updateCustomer(getCustomer(), getJaasUserName());
			agent = agentService.updateAgent(agent,super.getJaasUserName());
			contactDetails = contactDetailsService.updateContactDetails(contactDetails);
		}  catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage("Failed to update Agent details");
			return "failure";
		}
		cleanUpSessionScope();
		this.getRequestScope().remove("agentId");
		super.setInformationMessage("Agent "+agent.getAgentName() + " with number "+agent.getAgentNumber()+" updated successfully.");
		super.getRequestScope().put("agentId", agent.getId());
		super.gotoPage("/csr/viewAgent.jspx");
		return "submit";
	}
	
	@SuppressWarnings("unchecked")
	public String cancel() {
		super.getRequestScope().put("bankAccount", this.getAgent().getId());
		super.gotoPage("/csr/viewAgent.jspx");
		return "cancel";
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
	
	private AgentType [] getAgentTypeArray(){
		return new AgentType[]{
			AgentType.AGENT,
			AgentType.SUPER_AGENT,
			AgentType.SUB_AGENT,
		};
	}
}
