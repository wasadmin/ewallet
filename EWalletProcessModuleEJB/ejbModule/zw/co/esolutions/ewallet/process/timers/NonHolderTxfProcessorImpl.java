package zw.co.esolutions.ewallet.process.timers;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.ewallet.process.ConfigurationsLocal;
import zw.co.esolutions.ewallet.process.ProcessUtil;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.Formats;
import zw.co.esolutions.ewallet.util.SystemConstants;

/**
 * Session Bean implementation class NonHolderTxfProcessorImpl
 */
@Stateless
public class NonHolderTxfProcessorImpl implements NonHolderTxfProcessor , TimedObject {
	
	@Resource
	private TimerService timerService;
	@EJB
	private ConfigurationsLocal config;
	@EJB
	private ProcessUtil processUtil;
	private static Logger LOG;
	private final static String DAILY_TIMER = "Daily_Timer";
 
    static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(MonthlyProcessor.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + NonHolderTxfProcessorImpl.class);
		}
	}
    
    public NonHolderTxfProcessorImpl() {
        // TODO Auto-generated constructor stub
    }
    
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void ejbTimeout(Timer timer) {
		LOG.debug("***************** running daily NHW reversal processes *******************");
		Map<String, String> configMap = config.getConfigMap();
		
		String strDate = null,hour = null, minute = null, expPeriod = null , recipient = null;

		if (configMap != null) {
			strDate = configMap.get(SystemConstants.NHW_LAST_PROCCESSING_DATE);
			LOG.debug("In NHW reversal processes >>>>>>>>>>>>>>>>>>>>> Start date = "+strDate);
			hour = configMap.get(SystemConstants.NHW_PROCESSING_HOUR);
			LOG.debug("In  NHW reversal processes >>>>>>>>>>>>>>>>>>>>> Hour = "+hour);
			minute = configMap.get(SystemConstants.NHW_PROCESSING_MINUTE);
			LOG.debug("In  NHW reversal processes >>>>>>>>>>>>>>>>>>>>> Minute = "+minute);
			expPeriod = configMap.get(SystemConstants.NHW_REVERSAL_EXPIRATION_PERIOD);
			LOG.debug("In  NHW reversal processes >>>>>>>>>>>>>>>>>>>>> Revesal period = "+expPeriod);
			recipient = configMap.get(SystemConstants.NHW_EMAIL);
			LOG.debug("In  NHW reversal processes >>>>>>>>>>>>>>>>>>>>> Recipient = "+recipient);
		}
		
		
		// schedule timer for next day 
		int hr = Integer.parseInt(hour);
		int min = Integer.parseInt(minute);
		this.scheduleTimer(this.getNextDay(hr,min));

		LOG.debug("In  NHW reversal processes >>>>>>>>>>>>>>>>>>>>> Timer Scheduled.");
		Date lastProcessingDate = null;
		
		if (strDate != null) {
			try {
				lastProcessingDate = Formats.shortDateFormat.parse(strDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		LOG.debug("In  NHW reversal processes >>>>>>>>>>>>>>>>>>>>> Last Processing Date = "+lastProcessingDate);
//		if (lastProcessingDate == null || ) {
//			LOG.debug("In  NHW reversal processes >>>>>>>>>>>>>>>>>>>>>  Not running day");
//		}else{
			try {
				//Setting the cutoff date for Txfs to be reversed 
				Date expirationDate = new Date();
				int days = Integer.parseInt(expPeriod);
				Calendar c = Calendar.getInstance();
				c.setTime(expirationDate);
				c.add(Calendar.DATE, -days);
				expirationDate = c.getTime();
				
				
				LOG.debug("In  MonthlyProcessor >>>>>>>>>>>>>>>>>>>>> Starting Reversal Run at "+DateUtil.convertToDateWithTime(new Date(System.currentTimeMillis())));
				this.processUtil.runDailyNHCollectionReversal(expirationDate, recipient);
				LOG.debug("In  MonthlyProcessor >>>>>>>>>>>>>>>>>>>>> Done Running N H collection reversal process at "+DateUtil.convertToDateWithTime(new Date(System.currentTimeMillis())));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//		}
		
//		config.setConfiguration(SystemConstants.NHW_LAST_PROCCESSING_DATE, Formats.shortDateFormat.format(thisDate));
	}

	@Override
	public void scheduleTimer(String dayOfMonth, String hour, String min,
			boolean isForThisMonth, Date runDate) throws Exception {
		this.scheduleTimer(runDate);
		
	}

	@Override
	public void cancelTimer() throws Exception {
		cancelTimer(DAILY_TIMER);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void scheduleTimer(Date date) {
		LOG.debug("In  NHW reversal processes >>>>>>>>>>>>>>>>>>>>> Schedule for next date.");
		Calendar calendar = Calendar.getInstance();
		
		try {
			
			calendar.setTime(date);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>> Date initial value = "+calendar.getTime());
			calendar.add(Calendar.MINUTE, 2);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>> Date after adding 5 min  = "+calendar.getTime());
			cancelTimer(DAILY_TIMER);
			LOG.debug("In  NHW reversal processes >>>>>>>>>>>>>>>>>>>>> Done cancelling DAILY Timer."+date.toString());

			timerService.createTimer(calendar.getTime(), DAILY_TIMER);

			LOG.debug("In  NHW reversal processes >>>>>>>>>>>>>>>>>>>>> Done Scheduling new timer.");
		} catch (Exception e) {
			// e.printStackTrace();
		}

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
	
	private Date getNextDay(int hour , int min) {
		Calendar calendar = Calendar.getInstance();
//		Date date = DateUtil.getBusinessDayEndOfDay(new Date());
		Date date = new Date();
		
		calendar.setTime(date);
		calendar.add(Calendar.DATE, 1);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, min);
		
		return calendar.getTime();
	}
}
