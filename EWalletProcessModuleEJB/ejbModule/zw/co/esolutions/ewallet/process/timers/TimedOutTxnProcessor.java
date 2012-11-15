package zw.co.esolutions.ewallet.process.timers;
import java.util.Date;

import javax.ejb.Local;

@Local
public interface TimedOutTxnProcessor {
	void scheduleTimer(Date runDate) throws Exception;
	void cancelTimer(String description) throws Exception;
}
