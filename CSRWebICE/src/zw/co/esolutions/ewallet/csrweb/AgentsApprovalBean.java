package zw.co.esolutions.ewallet.csrweb;

import java.util.ArrayList;
import java.util.List;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.agentservice.service.Agent;
import zw.co.esolutions.ewallet.agentservice.service.AgentLevel;
import zw.co.esolutions.ewallet.agentservice.service.AgentServiceSOAPProxy;
import zw.co.esolutions.ewallet.agentservice.service.ProfileStatus;
import zw.co.esolutions.ewallet.agentutil.SearchInfo;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;

public class AgentsApprovalBean extends PageCodeBase{
	
	private List<SearchInfo> agentList;
	
	public List<SearchInfo> getAgentList() {
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		
		try{
			List<Agent> aList = agentService.getAgentByStatus(ProfileStatus.AWAITING_APPROVAL.name());
			List<Agent> tmpList = agentService.getAgentByStatus(ProfileStatus.AWAITING_EDIT_APPROVAL.name());
			for(Agent a : tmpList){
				aList.add(a);
			}
			
			if (aList == null) {
				agentList = new ArrayList<SearchInfo>();
				super.setInformationMessage("No Agents to approve");
			}else{
				agentList = populateSearchList(aList);
			}

		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return agentList;
	}
	public void setAgentList(List<SearchInfo> agentList) {
		this.agentList = agentList;
	}
	
	public String viewAgent() {
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		String agentId = (String) super.getRequestParam().get("agentId");
		Agent agent = agentService.findAgentById(agentId);
		
		if(agent.getAgentLevel().equals(AgentLevel.CORPORATE)){
			this.gotoPage("/csr/viewCorporateAgent.jspx");
		}else{
			this.gotoPage("/csr/viewAgent.jspx");
		}
		
		return "viewAgent";
	}
	
	private List<SearchInfo> populateSearchList(List<Agent> agentList){
		
		CustomerServiceSOAPProxy customerService  = new CustomerServiceSOAPProxy();
		List<SearchInfo> result = new ArrayList<SearchInfo>();
		
		if(agentList.size() > 0){
			for(Agent agent:agentList){
				
				Customer c = customerService.findCustomerById(agent.getCustomerId());
				List<MobileProfile> mobileProfile = customerService.getMobileProfileByCustomer(c.getId());
				SearchInfo searchInfo = new SearchInfo();
				searchInfo.setAgentId(agent.getId());
				searchInfo.setAgentType(agent.getAgentType().name());
				
				if(agent.getAgentLevel().equals(AgentLevel.CORPORATE)){
					searchInfo.setAgentName(c.getLastName());
				}else{
					searchInfo.setAgentName(c.getLastName()+" "+c.getFirstNames());
				}
				
				searchInfo.setAgentNumber(agent.getAgentNumber());
				if(mobileProfile.size()!=0){
					searchInfo.setMobileNumber(mobileProfile.get(0).getMobileNumber());
				}
				searchInfo.setStatus(agent.getStatus().name());
				
				result.add(searchInfo);
			}
		}
		
		return result;
	}
}
