package zw.co.esolutions.ewallet.agentutil;

import java.util.ArrayList;
import java.util.List;

import zw.co.esolutions.ewallet.agentservice.service.Agent;
import zw.co.esolutions.ewallet.agentservice.service.AgentLevel;
import zw.co.esolutions.ewallet.agentservice.service.AgentType;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;

public class AgentUtil {
	
	public static  List<SearchInfo> populateSearchList(List<Agent> agentList){
		
		CustomerServiceSOAPProxy customerService  = new CustomerServiceSOAPProxy();
		List<SearchInfo> result = new ArrayList<SearchInfo>();
		
		if(agentList.size() > 0){
			for(Agent agent:agentList){
				
				Customer c = customerService.findCustomerById(agent.getCustomerId());
				if(c!=null && c.getId() !=null){
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
		}
		
		return result;
	}
	
	public static AgentType [] getAgentTypeArray(){
		return new AgentType[]{
			AgentType.AGENT,
//			AgentType.SUPER_AGENT,
		};
	}
}
