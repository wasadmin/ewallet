package zw.co.esolutions.ewallet.process.timers;

import java.util.Date;

public interface MonthlyProcessorLocal {

	Date getCurrentDispatchTime();
	void scheduleTimer(String dayOfMonth, String hour, String min,
			boolean isForThisMonth, Date runDate) throws Exception;
	void cancelTimer() throws Exception;
	void scheduleBatchTimer(Date timerDate) throws Exception;
	void scheduleNextMonthBatchTimer(Date timerDate) throws Exception;

}
