/**
 * 
 */
package zw.co.esolutions.topup.ws.util;

import zw.co.esolutions.topup.ws.util.ServiceCommand;

/**
 * @author blessing
 * 
 */
public class ReversalRequest {
	protected WSRequest requestToReverse;
	protected ServiceCommand serviceCommand;

	/**
	 * 
	 */
	public ReversalRequest() {
		// TODO Auto-generated constructor stub
	}

	public WSRequest getRequestToReverse() {
		return requestToReverse;
	}

	public void setRequestToReverse(WSRequest requestToReverse) {
		this.requestToReverse = requestToReverse;
	}

	public ServiceCommand getServiceCommand() {
		return serviceCommand;
	}

	public void setServiceCommand(ServiceCommand serviceCommand) {
		this.serviceCommand = serviceCommand;
	}

}
