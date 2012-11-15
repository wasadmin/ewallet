package zw.co.esolutions.ewallet.process.timers;
import java.util.Date;

import javax.ejb.Local;

@Local
public interface NonHolderTxfProcessor {
	
	void scheduleTimer(String dayOfMonth, String hour, String min,
			boolean isForThisMonth, Date runDate) throws Exception;
	void cancelTimer() throws Exception;
}
