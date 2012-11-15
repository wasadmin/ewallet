package zw.co.esolutions.ewallet.process.timers;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebService;

/**
 * Session Bean implementation class StatementServiceImpl
 */
@Stateless
@WebService(endpointInterface="zw.co.esolutions.ewallet.process.timers.StatementService", serviceName="StatementService", portName="StatementServiceSOAP")
public class StatementServiceImpl implements StatementService {
	
	@EJB
	private AgentStatementProcessor asp;
	
    public StatementServiceImpl() {
        // TODO Auto-generated constructor stub
    }

	public boolean checkTimer(String description) {
		return asp.checkTimer("Agent_Statement_Timer");
		
	}

}
