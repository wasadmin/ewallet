package zw.co.esolutions.ewallet.agentservice.service.proxy;

import zw.co.esolutions.ewallet.agentservice.service.AgentServiceSOAPProxy;

public class AgentServiceProxy {

private static AgentServiceSOAPProxy proxy;
	
	public static final AgentServiceSOAPProxy getInstance() {
		if(proxy == null) {
			proxy = new AgentServiceSOAPProxy();
		}
		return proxy;
	}
}
