package zw.co.esolutions.ewallet.csrweb;

import javax.faces.event.ActionEvent;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.agentservice.service.AgentClass;
import zw.co.esolutions.ewallet.agentservice.service.AgentServiceSOAPProxy;
import zw.co.esolutions.ewallet.agentservice.service.Exception_Exception;

public class EditAgentClassBean extends PageCodeBase{
	
	private String agentClassId;
	private AgentClass agentClass;
	private String name;
	private String description;
	
	
	public String getName() {
		if(name== null){
			name = getAgentClass().getName();
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		if(description == null){
			description = getAgentClass().getDescription();
		}
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAgentClassId() {
		return agentClassId;
	}
	
	public void setAgentClassId(String agentClassId) {
		this.agentClassId = agentClassId;
	}
	
	public AgentClass getAgentClass() {
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		if (getAgentClassId() != null) {
			try {
				agentClass = agentService.findAgentClassById(agentClassId);
			} catch (Exception e) {
				e.printStackTrace();
				super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			}
		}
		return agentClass;
	}
	
	public void setAgentClass(AgentClass agentClass) {
		this.agentClass = agentClass;
	}
	
	public void doEditAction(ActionEvent event) {
		agentClassId = (String) event.getComponent().getAttributes().get("agentClassId");
	}
	
	public String submit(){
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		try {
			agentClass.setName(getName());
			agentClass.setDescription(description);
			System.out.println("###33 agentClass values "+agentClass.getName()+" "+agentClass.getDescription());
			agentClass = agentService.updateAgentClass(agentClass, getJaasUserName());
		} catch (Exception_Exception e) {
			e.printStackTrace();
		}
		
		gotoPage("/csr/viewAgentClass.jspx");
		return "success";
	}
	
	public String cancel(){
		gotoPage("/csr/csrHome.jspx");
		return "success";
	}
}
