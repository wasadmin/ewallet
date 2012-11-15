package zw.co.esolutions.ewallet.process.timers;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.ewallet.process.BatchProcessingBeanLocal;
import zw.co.esolutions.ewallet.process.ConfigurationsLocal;
import zw.co.esolutions.ewallet.process.ProcessUtil;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.SystemConstants;

/**
 * Session Bean implementation class MonthlyProcessor
 */
@Stateless
@Local
public class MonthlyProcessor implements MonthlyProcessorLocal, TimedObject {

	@Resource
	private TimerService timerService;
	@EJB
	private ConfigurationsLocal config;
	@EJB
	private ProcessUtil processUtil;
	
	@EJB private BatchProcessingBeanLocal batchProcess;
	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(MonthlyProcessor.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + MonthlyProcessor.class);
		}
	}
	
	private final static String MONTHLY_TIMER = "Monthly Timer";

	public MonthlyProcessor() {
		// TODO Auto-generated constructor stub
	}
	
	public void ejbTimeout(Timer timer) {
		LOG.debug("***************** running monthly processes *******************");
		/*Date thisDate = new Date();
		Date currentBatchDate = null;
		Map<String, String> configMap = config.getConfigMap();
		String strDate = null, dayOfMonth = null, hour = null, minute = null, month = null;
		
		Batch timerBatch = null;
		
		if (configMap != null) {
			strDate = configMap.get(SystemConstants.MONTHLY_PROCESSING_LAST_CHARGING_DATE);
			LOG.debug("In  MonthlyProcessor >>>>>>>>>>>>>>>>>>>>> Start date = "+strDate);
			dayOfMonth = configMap.get(SystemConstants.MONTHLY_PROCESSING_DAY);
			LOG.debug("In  MonthlyProcessor >>>>>>>>>>>>>>>>>>>>> Day of Month = "+dayOfMonth);
			month = configMap.get(SystemConstants.MONTH_);
			LOG.debug("In  MonthlyProcessor >>>>>>>>>>>>>>>>>>>>> Day of Month = "+month);
			hour = configMap.get(SystemConstants.MONTHLY_PROCESSING_HOUR);
			LOG.debug("In  MonthlyProcessor >>>>>>>>>>>>>>>>>>>>> Hour = "+hour);
			minute = configMap.get(SystemConstants.MONTHLY_PROCESSING_MINUTE);
			LOG.debug("In  MonthlyProcessor >>>>>>>>>>>>>>>>>>>>> Minute = "+minute);
		}
		
		currentBatchDate = this.getTimer(dayOfMonth, hour, minute, Integer.parseInt(month));
		timerBatch = this.batchProcess.findBatchById(EWalletConstants.EJB_TIMER_ID);
		LOG.debug(" In MonthlyProcessor >>>>>>>>>>>>> EJB Timer Batch found "+timerBatch);
		
		if(timerBatch == null || timerBatch.getId() == null) {
			// New Run
			LOG.debug(" In MonthlyProcessor >>>>>>>>>>>>> New Run.");
			timerBatch = new Batch();
			timerBatch.setId(EWalletConstants.EJB_TIMER_ID);
			timerBatch.setBatchDate(this.getTimer(dayOfMonth, hour, minute, this.getNextMonth(month))); //Set future run date
			LOG.debug("In  MonthlyProcessor >>>>>>>>>>>>>>>>>>>>> Future Date  = "+DateUtil.convertToDateWithTime(timerBatch.getBatchDate()));
			try {
				timerBatch = this.batchProcess.createBatch(timerBatch);
			} catch (Exception e) {
				
			}
		} else {
			//It's at least day old run
			if(DateUtil.daysBetween(timerBatch.getBatchDate(), new Date(System.currentTimeMillis())) > 0) {
				//Update this batch date in future times on it's first run
				LOG.debug(" In MonthlyProcessor >>>>>>>>>>>>> Updating old Batch.");
				timerBatch.setBatchDate(this.getTimer(dayOfMonth, hour, minute, this.getNextMonth(month))); //Set future run date
				try {
					timerBatch = this.batchProcess.updateBatch(timerBatch);
				} catch (Exception e) {
					
				}
			}
		}
		// schedule timer for month
		//this.scheduleTimer(dayOfMonth, hour, minute, this.getNextMonth());
		//LOG.debug("In  MonthlyProcessor >>>>>>>>>>>>>>>>>>>>> Timer Scheduled.");
		Date lastProcessingDate = null;
		if (strDate != null) {
			try {
				lastProcessingDate = Formats.shortDateFormat.parse(strDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(currentBatchDate != null) {
			thisDate = currentBatchDate;
		}
		LOG.debug("In  MonthlyProcessor >>>>>>>>>>>>>>>>>>>>> Last Processing Date = "+lastProcessingDate);
		if (lastProcessingDate == null) {
			lastProcessingDate = DateUtil.addDays(thisDate, -Integer.parseInt(DateUtil.getLastDayOfMonth(Integer.parseInt(month)))); // There was a -31 initially
		}

		//if(strDate != null) {
			//Process Transaction
			try {
				
				LOG.debug("In  MonthlyProcessor >>>>>>>>>>>>>>>>>>>>> Starting Account  Balance Run at "+DateUtil.convertToDateWithTime(new Date(System.currentTimeMillis())));
				this.processUtil.runEWalletsMonthlyAccountBalance(thisDate);
				LOG.debug("In  MonthlyProcessor >>>>>>>>>>>>>>>>>>>>> Done Running Account Balance process at "+DateUtil.convertToDateWithTime(new Date(System.currentTimeMillis())));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	//	}
		
		config.setConfiguration(SystemConstants.MONTHLY_PROCESSING_LAST_CHARGING_DATE, Formats.shortDateFormat.format(thisDate));*/

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void scheduleTimer(String dayOfMonth, String hour, String minute,int month) {
		LOG.debug("In  MonthlyProcessor >>>>>>>>>>>>>>>>>>>>> Schedule for next date.");
		Calendar calendar = Calendar.getInstance();
		if (SystemConstants.LAST_DAY_OF_MONTH.equals(dayOfMonth)) {
			dayOfMonth = DateUtil.getLastDayOfMonth(month);
			LOG.debug("In  MonthlyProcessor >>>>>>>>>>>>>>>>>>>>> Day of month = "+dayOfMonth);
		}
		try {
			int iDayOfMonth = Integer.parseInt(dayOfMonth);
			LOG.debug("In  MonthlyProcessor >>>>>>>>>>>>>>>>>>>>> Integer day of month = "+iDayOfMonth);
			int hr = Integer.parseInt(hour);
			LOG.debug("In  MonthlyProcessor >>>>>>>>>>>>>>>>>>>>> Hour = "+hr);
			int min = Integer.parseInt(minute);
			LOG.debug("In  MonthlyProcessor >>>>>>>>>>>>>>>>>>>>> Minutes = "+min);
			calendar.set(Calendar.DAY_OF_MONTH, iDayOfMonth);
			calendar.set(Calendar.HOUR_OF_DAY, hr);
			calendar.set(Calendar.MINUTE, min);
			calendar.set(Calendar.MONTH, month);
			
			cancelTimer(MONTHLY_TIMER);
			LOG.debug("In  MonthlyProcessor >>>>>>>>>>>>>>>>>>>>> Done cancelling Monthly Timer.");
			Date timerDate = calendar.getTime();
			timerService.createTimer(timerDate, MONTHLY_TIMER);
			LOG.debug("In  MonthlyProcessor >>>>>>>>>>>>>>>>>>>>> Done Scheduling new timer.");
		} catch (Exception e) {
			// e.printStackTrace();
		}

	}

	private int getNextMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, 1);
		return calendar.get(Calendar.MONTH);
	}
	
	private int getNextMonth(String month) {
		Calendar calendar = Calendar.getInstance();
		Date date = new Date();
		calendar.setTime(date);
		calendar.set(Calendar.MONTH, Integer.parseInt(month));
		calendar.add(Calendar.MONTH, 1);
		return calendar.get(Calendar.MONTH);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void scheduleTimer(String dayOfMonth, String hour, String min,
			boolean isForThisMonth, Date runDate) throws Exception{
		LOG.debug(" In Schedule Method >>>>>>>>>>>>> Day of Moth = "+dayOfMonth+" , Hour = "+hour+" , Minutes = "+min+", Is For this Month = "+isForThisMonth+" Run Date = "+runDate);
		/*if (SystemConstants.LAST_DAY_OF_MONTH.equals(dayOfMonth)) {
			if (isForThisMonth) {
				dayOfMonth = DateUtil.getLastDayOfMonth(new Date());
				LOG.debug(" In Schedule Method >>>>>>>>>>>>> Is for this Moth = true, Day of Month = "+dayOfMonth);
			} else {
				if(runDate != null && DateUtil.isDayBeforeOrEqual(runDate, new Date(System.currentTimeMillis()))) {
					dayOfMonth = DateUtil.getLastDayOfMonth(runDate);
					LOG.debug(" In Schedule Method >>>>>>>>>>>>> Day if before or equal to current.");
				} else {
					dayOfMonth = DateUtil.getLastDayOfNextMonth(new Date());
				}
				LOG.debug(" In Schedule Method >>>>>>>>>>>>> Is for this Moth = false, Day of Month = "+dayOfMonth);
			}
		}*/
		this.cancelTimer(MONTHLY_TIMER);
		/*Date timerDate = null;
		if(runDate == null) {
			timerDate = DateUtil.getDate(dayOfMonth, hour, min);
		} else {
			timerDate = runDate;
		}
		if (timerDate.after(new Date())) {
			LOG.debug(" In Schedule Method >>>>>>>>>>>>> Timer is after this date "+timerDate);
			timerService.createTimer(timerDate, MONTHLY_TIMER);
			LOG.debug(" In Schedule Method >>>>>>>>>>>>> Timer created successfully.");
		} else {
			LOG.debug(" In Schedule Method >>>>>>>>>>>>> Timer is before this date");
			if(runDate != null && runDate.before(new Date(System.currentTimeMillis()))) {
				LOG.debug(" In Schedule Method >>>>>>>>>>>>> Timer is before this date "+timerDate);
				timerService.createTimer(timerDate, MONTHLY_TIMER);
				LOG.debug(" In Schedule Method >>>>>>>>>>>>> Timer created successfully.");
			} else {
				this.scheduleTimer(dayOfMonth, hour, min, this.getNextMonth());
				timerDate = this.getTimer(dayOfMonth, hour, min, this.getNextMonth());
			}
			
		}
		
		try {
			this.config.initialiaseOrUpdateConfigurations(this.populateConfig(timerDate));
		} catch (Exception e) {
			// TODO: handle exception
		}*/

	}
	
	
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void cancelTimer() throws Exception {
		this.cancelTimer(MONTHLY_TIMER);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void cancelTimer(String description) throws Exception{
		LOG.debug(" In Cancel Timer Method >>>>>>>>>>>>> Description = "+description);
		for (Object obj : timerService.getTimers( )) {
	        Timer timer = (Timer)obj;
	        LOG.debug(" In cancelTimer Method >>>>>>>>>>>>> Timer found = "+timer);
	        String scheduled = (String)timer.getInfo();
	        LOG.debug(" In cancelTimer Method >>>>>>>>>>>>> Timer found description : "+scheduled);
	        if (scheduled.equals(description)) {
	            timer.cancel();
	            LOG.debug(" In cancelTimer Method >>>>>>>>>>>>> Timer cancelled : "+scheduled);
	        }
	    }
	}
	public Date getCurrentDispatchTime() {

		for (Object obj : timerService.getTimers()) {
			Timer timer = (Timer) obj;
			String description = (String) timer.getInfo();
			if (description.equalsIgnoreCase(MONTHLY_TIMER)) {
				return timer.getNextTimeout();
			}
		}

		return null;

	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void scheduleBatchTimer(Date timerDate) throws Exception{
		this.cancelTimer(MONTHLY_TIMER);
		LOG.debug(" In Schedule Batch Method >>>>>>>>>>>>> Timer = "+timerDate);
		timerService.createTimer(timerDate, MONTHLY_TIMER);
		LOG.debug(" In Schedule Batch Method >>>>>>>>>>>>> Timer created successfully.");
		

	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void scheduleNextMonthBatchTimer(Date timerDate) throws Exception{
		this.cancelTimer(MONTHLY_TIMER);
		LOG.debug(" In Schedule Batch Method >>>>>>>>>>>>> Timer = "+timerDate);
		timerService.createTimer(timerDate, MONTHLY_TIMER);
		try {
			this.config.initialiaseOrUpdateConfigurations(this.populateConfig(timerDate));
		} catch (Exception e) {
			e.printStackTrace();
		}
		LOG.debug(" In Schedule Batch Method >>>>>>>>>>>>> Timer created successfully.");
		

	}
	
	private Date getTimer(String dayOfMonth, String hour, String minute,int month) {
		LOG.debug("In  MonthlyProcessor >>>>>>>>>>>>>>>>>>>>> In getTimer Date.");
		Calendar calendar = Calendar.getInstance();
		if (SystemConstants.LAST_DAY_OF_MONTH.equals(dayOfMonth)) {
			dayOfMonth = DateUtil.getLastDayOfMonth(month);
			LOG.debug("In  MonthlyProcessor >>>>>>>>>>>>>>>>>>>>> Day of month = "+dayOfMonth);
		}
		try {
			int iDayOfMonth = Integer.parseInt(dayOfMonth);
			LOG.debug("In  MonthlyProcessor >>>>>>>>>>>>>>>>>>>>> Integer day of month = "+iDayOfMonth);
			int hr = Integer.parseInt(hour);
			LOG.debug("In  MonthlyProcessor >>>>>>>>>>>>>>>>>>>>> Hour = "+hr);
			int min = Integer.parseInt(minute);
			LOG.debug("In  MonthlyProcessor >>>>>>>>>>>>>>>>>>>>> Minutes = "+min);
			calendar.set(Calendar.DAY_OF_MONTH, iDayOfMonth);
			calendar.set(Calendar.HOUR_OF_DAY, hr);
			calendar.set(Calendar.MINUTE, min);
			calendar.set(Calendar.MONTH, month);
			LOG.debug("In  MonthlyProcessor >>>>>>>>>>>>>>>>>>>>> Done cancelling Monthly Timer.");
			Date timerDate = calendar.getTime();
			return timerDate;
			
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return null;
	}
	
	public Map<String,String> populateConfig(Date timerDate) {
		Map<String,String> configMap = new java.util.HashMap<String, String>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(timerDate);
		configMap.put(SystemConstants.MONTHLY_PROCESSING_DAY, SystemConstants.LAST_DAY_OF_MONTH);
		LOG.debug("In  Populate Timer Configs >>>>>>>>>>>>>>>>>>>>> Day of Month = "+configMap.get(SystemConstants.MONTHLY_PROCESSING_DAY));
		
		configMap.put(SystemConstants.MONTH_, new Integer(cal.get(Calendar.MONTH)).toString());
		LOG.debug("In  Populate Timer Configs >>>>>>>>>>>>>>>>>>>>> Month = "+configMap.get(SystemConstants.MONTH_));
		
		configMap.put(SystemConstants.MONTHLY_PROCESSING_HOUR, new Integer(cal.get(Calendar.HOUR_OF_DAY)).toString());
		LOG.debug("In  Populate Timer Configs >>>>>>>>>>>>>>>>>>>>> Hour = "+configMap.get(SystemConstants.MONTHLY_PROCESSING_HOUR));
		
		configMap.put(SystemConstants.MONTHLY_PROCESSING_MINUTE, new Integer(cal.get(Calendar.MINUTE)).toString());
		LOG.debug("In  Populate Timer Configs >>>>>>>>>>>>>>>>>>>>> Minute = "+configMap.get(SystemConstants.MONTHLY_PROCESSING_MINUTE));
		
		return configMap;
	}
	

}
