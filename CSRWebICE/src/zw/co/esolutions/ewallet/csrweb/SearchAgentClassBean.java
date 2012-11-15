package zw.co.esolutions.ewallet.csrweb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.agentservice.service.AgentClass;
import zw.co.esolutions.ewallet.agentservice.service.AgentServiceSOAPProxy;
import zw.co.esolutions.ewallet.agentservice.service.AgentType;
import zw.co.esolutions.ewallet.agentservice.service.Exception_Exception;
import zw.co.esolutions.ewallet.enums.AgentClassStatus;

import com.icesoft.faces.component.ext.HtmlInputText;

public class SearchAgentClassBean extends PageCodeBase{
	
	private List<SelectItem> criteriaList;
	private String selectedCriteria;
	private String searchField;
	private List<AgentClass> agentClassList;
	private boolean selectMenu;
	private HtmlInputText searchInput = new HtmlInputText();
	private List<SelectItem> agentClassStatus;
	private AgentType selectedType;
	private String agentClassId;
	private boolean commandLink ;
	private List<AgentClass> approvalList;
	
	public List<SelectItem> getCriteriaList() {
		if (criteriaList == null) {
			criteriaList = new ArrayList<SelectItem>();
			criteriaList.add(new SelectItem("none", "--Select--"));
			criteriaList.add(new SelectItem("CLASS_NAME", "CLASS NAME"));
			criteriaList.add(new SelectItem("STATUS", "STATUS"));
		}
		return criteriaList;
	}
	public void setCriteriaList(List<SelectItem> criteriaList) {
		this.criteriaList = criteriaList;
	}
	
	
	public List<AgentClass> getApprovalList() {
		
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		try{
			approvalList = agentService.getAgentClassByStatus("AWAITING_APPROVAL");
			if(approvalList == null){
				approvalList = new ArrayList<AgentClass>();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return approvalList;
	}
	public void setApprovalList(List<AgentClass> approvalList) {
		this.approvalList = approvalList;
	}
	public void setAgentClassList(List<AgentClass> agentClassList) {
		this.agentClassList = agentClassList;
	}
	public String getAgentClassId() {
		if (agentClassId == null) {
			agentClassId = (String) super.getRequestScope().get("agentClassId");
		}
		if (agentClassId == null) {
			agentClassId = (String) super.getRequestParam().get("agentClassId");
		}
		return agentClassId;
	}
	public void setAgentClassId(String agentClassId) {
		this.agentClassId = agentClassId;
	}
	public String getSelectedCriteria() {
		return selectedCriteria;
	}
	
	public boolean isCommandLink() {
		return commandLink;
	}
	public void setCommandLink(boolean commandLink) {
		this.commandLink = commandLink;
	}
	public void setSelectedCriteria(String selectedCriteria) {
		this.selectedCriteria = selectedCriteria;
	}
	public String getSearchField() {
		return searchField;
	}
	
	public boolean isSelectMenu() {
		return selectMenu;
	}
	public void setSelectMenu(boolean selectMenu) {
		this.selectMenu = selectMenu;
	}
	public HtmlInputText getSearchInput() {
		return searchInput;
	}
	public void setSearchInput(HtmlInputText searchInput) {
		this.searchInput = searchInput;
	}
	public void setSearchField(String searchField) {
		this.searchField = searchField;
	}
	public List<AgentClass> getAgentClassList() {
		if (agentClassList == null) {
			agentClassList = new ArrayList<AgentClass>();
		}
		return agentClassList;
	}
	public void setAgentList(List<AgentClass> agentClassList) {
		this.agentClassList = agentClassList;
	}
	
	public List<SelectItem> getAgentStatus() {
		if(this.agentClassStatus == null) {
			this.agentClassStatus = new ArrayList<SelectItem>();
			this.agentClassStatus.add(new SelectItem("none", "--select--"));
			for(AgentClassStatus status : getAgentClassStatusArray()) {
				this.agentClassStatus.add(new SelectItem(status.toString(), status.toString().replace("_", " ")));
			}
		}
		return agentClassStatus;
	}
	public void setAgentStatus(List<SelectItem> agentClassStatus) {
		this.agentClassStatus = agentClassStatus;
	}
	public AgentType getSelectedType() {
		return selectedType;
	}
	public void setSelectedType(AgentType selectedType) {
		this.selectedType = selectedType;
	}
	
	public String search() {
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		searchField = searchField.toUpperCase();
		try {
			if(selectedCriteria.equals("none")){
				setErrorMessage("Select search criterior");
				return "failure";
			}
			if (!searchField.isEmpty()) {
				if ("CLASS_NAME".equals(selectedCriteria)) {
					AgentClass agentClass = agentService.getAgentClassByName(searchField);
					agentClassList = new ArrayList<AgentClass>();
					if (agentClass != null) {
						agentClassList.add(agentClass);
						setAgentList(getAgentClassList());
						setInformationMessage("Found "+agentClassList.get(0).getName()+" Agent Class");
					}else{
						setInformationMessage("No results to display at the time");
					}
				} else if("STATUS".equals(selectedCriteria)) {
					if(!searchField.equalsIgnoreCase("NONE")){
						if(searchField.equals("AWAITING_APPROVAL")){
							commandLink = true;
						}
						List<AgentClass> agentArray = agentService.getAgentClassByStatus(searchField);
						if (agentArray != null) {
							setAgentList(agentArray);
							super.setInformationMessage("Found "+agentArray.size()+" Agent Classes");
						} else {
							agentClassList = new ArrayList<AgentClass>();
							super.setInformationMessage("No results found.");
						}
					}else{
						setErrorMessage("Select status to search");
						return "failure";
					}
				} 
			} else {
				super.setErrorMessage("Search field cannot be blank.");
				return "failure";
			}
		} catch (Exception e) {
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		return "search";
	}
	
	public void processValueChange(ValueChangeEvent event) {
		String searchType = (String) event.getNewValue();
		if(searchType.equals("STATUS")){
			selectMenu = true;
			searchInput.setRendered(false);
		}else{
			selectMenu = false;
			searchInput.setRendered(true);
		}
	}
	
	public String listAll() {
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		try {
			List<AgentClass> allAgents = agentService.getAllAgentClasses();
			if (allAgents != null) {
				setAgentList(allAgents);
				super.setInformationMessage("Found "+allAgents.size()+" classes");
			} else {
				agentClassList = new ArrayList<AgentClass>();
				super.setInformationMessage("No results found.");
			}
		} catch (Exception e) {
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		return "listAll";
	}
	
	public String viewAgentClass() {
		this.gotoPage("/csr/viewAgentClass.jspx");
		return "viewAgent";
	}
	
	public String approve(){
		
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		AgentClass agentClass = agentService.findAgentClassById(getAgentClassId());
		try {
			agentService.approveAgentClass(agentClass, getJaasUserName());
			gotoPage("/csr/agentClassApproval.jspx");
		} catch (Exception_Exception e) {
			e.printStackTrace(System.out);
		}
		return "success";
	}
	
	public String reject(){
		
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		AgentClass agentClass = agentService.findAgentClassById(getAgentClassId());
		try {
			agentService.rejectAgentClass(agentClass, getJaasUserName());
			gotoPage("/csr/agentClassApproval.jspx");
		} catch (Exception_Exception e) {
			e.printStackTrace();
		}
		
		return "success";
	}
	
	private AgentClassStatus [] getAgentClassStatusArray(){
		return new AgentClassStatus[]{
				AgentClassStatus.ACTIVE,
				AgentClassStatus.AWAITING_APPROVAL,
				AgentClassStatus.DELETED,
				AgentClassStatus.DISAPPROVED,
		};
	}
}
