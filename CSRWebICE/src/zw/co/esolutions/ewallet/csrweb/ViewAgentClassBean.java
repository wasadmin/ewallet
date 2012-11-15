package zw.co.esolutions.ewallet.csrweb;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.agentservice.service.AgentClass;
import zw.co.esolutions.ewallet.agentservice.service.AgentServiceSOAPProxy;

public class ViewAgentClassBean extends PageCodeBase{
	
	private AgentClass agentClass;
	private String agentClassId;
	
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
	
	@SuppressWarnings("unchecked")
	public String approve(){
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		try{
			AgentClass agentClass = agentService.findAgentClassById(getAgentClassId());
			agentService.approveAgentClass(agentClass, getJaasUserName());
			getRequestScope().put("agentClassId", agentClass.getId());
			gotoPage("/csr/agentClassApproval.jspx");
		}catch(Exception e){
			e.printStackTrace();
		}
		return "success";
	}
	
	public String reject(){
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		try{
			AgentClass agentClass = agentService.findAgentClassById(getAgentClassId());
			agentClass = agentService.rejectAgentClass(agentClass, getJaasUserName());
			gotoPage("/csr/agentClassApproval.jspx");
		}catch(Exception e){
			e.printStackTrace();
		}
		return "success";
	}
	public String getAgentClassId() {
		if (agentClassId == null) {
			agentClassId = (String) super.getRequestParam().get("agentClassId");
		}
		if (agentClassId == null) {
			agentClassId = (String) super.getRequestScope().get("agentClassId");
		}
		
		return agentClassId;
	}
	public void setAgentClassId(String agentClassId) {
		this.agentClassId = agentClassId;
	}
	
	public String ok(){
		gotoPage("/csr/csrHome.jspx");
		return "success";
	}
	
	public String edit(){
		gotoPage("/csr/editAgentClass.jspx");
		return "success";
	}
	
}
