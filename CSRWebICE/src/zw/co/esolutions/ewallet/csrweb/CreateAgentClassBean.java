package zw.co.esolutions.ewallet.csrweb;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.agentservice.service.AgentClass;
import zw.co.esolutions.ewallet.agentservice.service.AgentServiceSOAPProxy;
import zw.co.esolutions.ewallet.agentservice.service.Exception_Exception;

public class CreateAgentClassBean extends PageCodeBase{
	
	private AgentClass agentClass = new AgentClass();
//	private List<SelectItem> bankList;
	private String selectedBank;

	public AgentClass getAgentClass() {
		return agentClass;
	}

	public void setAgentClass(AgentClass agentClass) {
		this.agentClass = agentClass;
	}
	
	public void setSelectedBank(String selectedBank) {
		this.selectedBank = selectedBank;
	}
	public String getSelectedBank() {
		return selectedBank;
	}
	
	@SuppressWarnings("unchecked")
	public String submit(){
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		
		try {
			String a = agentClass.getName().toUpperCase();
			if(agentService.getAgentClassByName(a)!= null){
				setErrorMessage("Agent class already exists ");
				return "failure";
			}
			System.out.println("the values getting into the "+ getJaasUserName()+ " "+ agentClass.getDescription());
			AgentClass agntClass = agentService.createAgentClass(agentClass, getJaasUserName());
			getRequestParam().put("agentClassId", agntClass.getId());
			setInformationMessage("Agent class "+agentClass.getName()+" was successfuly created");
			gotoPage("/csr/viewAgentClass.jspx");
			agentClass = new AgentClass();
		} catch (Exception_Exception e) {
			e.printStackTrace();
		}
		return "success";
	}
	
	public String cancel(){
		gotoPage("/csr/csrHome.jspx");
		return "success";
	}
}
