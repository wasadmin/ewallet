package zw.co.esolutions.ewallet.process.timers;
import java.util.Date;

import javax.ejb.Local;

@Local
public interface AgentCommissionProcessor {
	void scheduleTimer(Date runDate) throws Exception;
	void cancelTimer() throws Exception;
}
