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
import zw.co.esolutions.ewallet.agentutil.AgentUtil;
import zw.co.esolutions.ewallet.agentutil.SearchInfo;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.enums.ProfileStatus;
import zw.co.esolutions.ewallet.util.JsfUtil;

import com.icesoft.faces.component.ext.HtmlInputText;

public class SearchAgentBean extends PageCodeBase{
	
	private List<SelectItem> criteriaList;
	private String selectedCriteria;
	private String searchField;
	private List<SearchInfo> agentList;
	private boolean selectMenu;
	private boolean agentLevelMenu;
	private boolean agentTypeMenu;
	private boolean corpDataTable;
	private boolean indvDataTable;
	private HtmlInputText searchInput = new HtmlInputText();
	private List<SelectItem> agentStatus;
	private List<SelectItem> agentLevel;
	private List<SelectItem> agentType;
	private AgentType selectedType;
	
	public SearchAgentBean(){
		indvDataTable = true;
	}
	
	public List<SelectItem> getCriteriaList() {
		if (criteriaList == null) {
			criteriaList = new ArrayList<SelectItem>();
			criteriaList.add(new SelectItem("none", "--Select--"));
			criteriaList.add(new SelectItem("AGENTNUMBER", "AGENT NUMBER"));
			criteriaList.add(new SelectItem("LASTNAME", "LASTNAME"));
			criteriaList.add(new SelectItem("STATUS", "STATUS"));
			criteriaList.add(new SelectItem("AGENTLEVEL", "AGENT LEVEL"));
			criteriaList.add(new SelectItem("AGENTTYPE", "AGENT TYPE"));
		}
		return criteriaList;
	}
	public void setCriteriaList(List<SelectItem> criteriaList) {
		this.criteriaList = criteriaList;
	}
	public String getSelectedCriteria() {
		return selectedCriteria;
	}
	public void setSelectedCriteria(String selectedCriteria) {
		this.selectedCriteria = selectedCriteria;
	}
	public String getSearchField() {
		return searchField;
	}
	
	public boolean isAgentTypeMenu() {
		return agentTypeMenu;
	}

	public void setAgentTypeMenu(boolean agentTypeMenu) {
		this.agentTypeMenu = agentTypeMenu;
	}

	public boolean isAgentLevelMenu() {
		return agentLevelMenu;
	}
	public void setAgentLevelMenu(boolean agentLevelMenu) {
		this.agentLevelMenu = agentLevelMenu;
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
	public List<SearchInfo> getAgentList() {
		if (agentList == null) {
			agentList = new ArrayList<SearchInfo>();
		}
		return agentList;
	}
	public void setAgentList(List<SearchInfo> agentList) {
		this.agentList = agentList;
	}
	
	public List<SelectItem> getAgentStatus() {
		if(this.agentStatus == null) {
			this.agentStatus = new ArrayList<SelectItem>();
			this.agentStatus.add(new SelectItem("none", "--select--"));
			for(ProfileStatus status : getProfileStatusArray()) {
				this.agentStatus.add(new SelectItem(status.toString(), status.toString().replace("_", " ")));
			}
		}
		return agentStatus;
	}
	
	public List<SelectItem> getAgentType() {
		if(this.agentType == null) {
			this.agentType = new ArrayList<SelectItem>();
			this.agentType.add(new SelectItem("none", "--select--"));
			for(AgentType type : getAgentTypeArray()) {
				this.agentType.add(new SelectItem(type.toString(), type.toString().replace("_", " ")));
			}
		}
		return agentType;
	}

	public void setAgentType(List<SelectItem> agentType) {
		this.agentType = agentType;
	}

	public List<SelectItem> getAgentLevel() {
		agentLevel = JsfUtil.getSelectItemsAsList(AgentLevel.values(), true);
		return agentLevel;
	}
	public void setAgentLevel(List<SelectItem> agentLevel) {
		this.agentLevel = agentLevel;
	}
	public void setAgentStatus(List<SelectItem> agentStatus) {
		this.agentStatus = agentStatus;
	}
	
	public boolean isCorpDataTable() {
		return corpDataTable;
	}
	public void setCorpDataTable(boolean corpDataTable) {
		this.corpDataTable = corpDataTable;
	}
	public boolean isIndvDataTable() {
		return indvDataTable;
	}
	public void setIndvDataTable(boolean indvDataTable) {
		this.indvDataTable = indvDataTable;
	}
	public AgentType getSelectedType() {
		return selectedType;
	}
	public void setSelectedType(AgentType selectedType) {
		this.selectedType = selectedType;
	}
	
	public void processValueChange(ValueChangeEvent event) {
		String searchType = (String) event.getNewValue();
		if(searchType.equals("STATUS")){
			selectMenu = true;
			agentLevelMenu = false;
			corpDataTable = false;
//			indvDataTable = true;
			agentTypeMenu = false;
			searchInput.setRendered(false);
		}else if(searchType.equals("AGENTLEVEL")){
			agentLevelMenu = true;
			corpDataTable = true;
//			indvDataTable = false;
			agentTypeMenu = false;
			selectMenu = false;
			searchInput.setRendered(false);
		}else if(searchType.equals("AGENTTYPE")){
			agentTypeMenu = true;
			selectMenu = false;
			agentLevelMenu = false;
			corpDataTable = false;
//			indvDataTable = true;
			searchInput.setRendered(false);
		}else{
			selectMenu = false;
			agentLevelMenu = false;
			corpDataTable = false;
			indvDataTable = true;
			agentTypeMenu = false;
			searchInput.setRendered(true);
		}
	}
	
	public String search() {
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		searchField = searchField.toUpperCase();
		
		try {
			if(selectedCriteria.equals("none")){
				setErrorMessage("Select a search criteria");
				return "failure";
			}
			if (searchField != null || !searchField.equals("")) {
				if ("AGENTNUMBER".equals(selectedCriteria)) {
					
					Agent agent = agentService.getAgentByAgentNumber(searchField);
					
					if (agent != null) {
						List<Agent> tmpList = new ArrayList<Agent>();
						tmpList.add(agent);
						setAgentList(AgentUtil.populateSearchList(tmpList));
						super.setInformationMessage("Found "+tmpList.get(0).getAgentName()+"");
					}else{
						super.setInformationMessage("No Agent with number "+searchField+" found");
					}
				} else if("LASTNAME".equals(selectedCriteria)) {
					List<Customer> cList = customerService.getCustomerByLastName(searchField);
					List<Agent> aList = new ArrayList<Agent>();
					if (cList != null && cList.size()!=0) {
						for(Customer customer:cList ){
							Agent agent = agentService.getAgentByCustomerId(customer.getId());
							if(agent != null && agent.getId()!= null){
								aList.add(agent);
							}
							
						}
						if(aList.size()>0){
							setAgentList(AgentUtil.populateSearchList(aList));
							super.setInformationMessage("Found "+aList.size()+" Agent(s)");
						}else{
							agentList = new ArrayList<SearchInfo>();
							super.setInformationMessage("No results found.");
						}
						
					} else {
						agentList = new ArrayList<SearchInfo>();
						super.setInformationMessage("No results found.");
					}
					
				} else if("STATUS".equals(selectedCriteria)) {
					if(!searchField.equals("NONE")){
						List<Agent> aList = agentService.getAgentByStatus(searchField);
						if(aList != null){
							setAgentList(AgentUtil.populateSearchList(aList));
							super.setInformationMessage("Found "+aList.size()+" Agent(s)");
						}else{
							agentList = new ArrayList<SearchInfo>();
							super.setInformationMessage("No results found.");
						}
					}else{
						setErrorMessage("Select search option");
					}
					
				}else if("AGENTLEVEL".equals(selectedCriteria)) {
					if(!searchField.equals("NONE")){
						List<Agent> agentArray = agentService.getAgentByLevel(searchField);
						if (agentArray != null) {
							setAgentList(AgentUtil.populateSearchList(agentArray));
							super.setInformationMessage("Found "+agentArray.size()+" Agent(s)");
						} else {
							agentList = new ArrayList<SearchInfo>();
							super.setInformationMessage("No results found.");
						}
					}else{
						setErrorMessage("Select search option");
					}
					
				}else if("AGENTTYPE".equals(selectedCriteria)) {
					if(!searchField.equals("NONE")){
						List<Agent> agentArray = agentService.getAgentByAgentType(searchField);
						if (agentArray != null) {
							setAgentList(AgentUtil.populateSearchList(agentArray));
							super.setInformationMessage("Found "+agentArray.size()+" Agent(s)");
						} else {
							agentList = new ArrayList<SearchInfo>();
							super.setInformationMessage("No results found.");
						}
					}else {
						agentList = new ArrayList<SearchInfo>();
						super.setInformationMessage("No results found.");
					}		
				}
			} else {
				super.setErrorMessage("Search field cannot be blank.");
				return "failure";
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		return "search";
	}
	
	public String listAll() {
		
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		
		try {
			List<Agent> aList = agentService.getAllAgents();
			if (aList != null) {
				
				List<SearchInfo> allAgents = AgentUtil.populateSearchList(aList);
				setAgentList(allAgents);
				super.setInformationMessage("Found "+aList.size()+" Agent(s)");
			} else {
				agentList = new ArrayList<SearchInfo>();
				super.setInformationMessage("No results found.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		return "listAll";
	}
	
	public String viewAgent() {
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		String agentId = (String) super.getRequestParam().get("agentId");
		Agent agent = agentService.findAgentById(agentId);
		
		if(agent.getAgentLevel().equals(AgentLevel.CORPORATE)){
			if(agent.getAgentType().equals(AgentType.SUB_AGENT)){
				this.gotoPage("/csr/viewCorporateSubAgent.jspx");
			}else{
				this.gotoPage("/csr/viewCorporateAgent.jspx");
			}
		}else{
			if(agent.getAgentType().equals(AgentType.SUB_AGENT)){
				this.gotoPage("/csr/viewIndSubAgent.jspx");
			}else{
				this.gotoPage("/csr/viewAgent.jspx");
			}
		}
		
		return "viewAgent";
	}
	
	private ProfileStatus [] getProfileStatusArray(){
		return new ProfileStatus[]{
				ProfileStatus.ACTIVE,
				ProfileStatus.APPROVED,
				ProfileStatus.AWAITING_APPROVAL,
				ProfileStatus.AWAITING_EDIT_APPROVAL,
				ProfileStatus.DELETED,
				ProfileStatus.DISABLED,
				ProfileStatus.DISAPPROVED,
				ProfileStatus.INACTIVE
		};
	}
	
	private AgentType []getAgentTypeArray(){
		return new AgentType[]{
			AgentType.AGENT,
			AgentType.SUB_AGENT,
			AgentType.SUPER_AGENT
		};
	}
}
