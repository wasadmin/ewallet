package zw.co.esolutions.ewallet.process.timers;
import javax.ejb.Local;

@Local
public interface AgentStatementProcessor {
	
	public void cancelTimer(String description) throws Exception;
	
	
	public boolean checkTimer(String description);
	
}
