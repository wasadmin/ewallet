package zw.co.esolutions.ewallet.process.timers;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.ewallet.process.ConfigurationsLocal;
import zw.co.esolutions.ewallet.process.ProcessUtil;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.SystemConstants;

/**
 * Session Bean implementation class AgentCommissionProcessorImpl
 */
@Stateless
public class AgentCommissionProcessorImpl implements AgentCommissionProcessor{
	
	@Resource
	private TimerService timerService;
	@EJB
	private ConfigurationsLocal config;
	@EJB
	private ProcessUtil processUtil;
	private static Logger LOG;
	private final static String COMMISSION_SWEEPING_TIMER = "Commission_Sweeping_Timer";
 

    /**
     * Default constructor. 
     */
    public AgentCommissionProcessorImpl() {
        // TODO Auto-generated constructor stub
    }
    
    static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(MonthlyProcessor.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + AgentCommissionProcessorImpl.class);
		}
	}
    
    @Timeout
    public void processAgentCommissionSweep(Timer timer){
		LOG.debug("***************** running agent sweeping processes *******************");
		
		Map<String, String> configMap = config.getConfigMap();
		
//		String hour = null,period = null , minute= null,
		String recipient = null;

		if (configMap != null) {
//			
//			hour = configMap.get(SystemConstants.COMMISSION_SWEEPING_HOUR);
//			LOG.debug("In  Agent Sweeping processes >>>>>>>>>>>>>>>>>>>>> Hour = "+hour);
//			minute = configMap.get(SystemConstants.COMMISSION_SWEEPING_MINUTE);
//			LOG.debug("In  NHW reversal processes >>>>>>>>>>>>>>>>>>>>> Minute = "+minute);
//			period = configMap.get(SystemConstants.COMMISSION_SWEEPING_PERIOD);
//			LOG.debug("In  Agent Sweeping processes >>>>>>>>>>>>>>>>>>>>> Revesal period = "+period);
			recipient = configMap.get(SystemConstants.COMMISSION_SWEEPING_EMAIL);
			LOG.debug("In  Agent Sweeping processes >>>>>>>>>>>>>>>>>>>>> Recipient = "+recipient);
		}
		
		try {
			LOG.debug("In  Agent Sweeping processes >>>>>>>>>>>>>>>>>>>>> Starting Sweep at "+DateUtil.convertToDateWithTime(new Date(System.currentTimeMillis())));
			processCommissionSweep(recipient);
			LOG.debug("In  Agent Sweeping processes >>>>>>>>>>>>>>>>>>>>> Done Running Commission sweeping process at "+DateUtil.convertToDateWithTime(new Date(System.currentTimeMillis())));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		config.setConfiguration(SystemConstants.NHW_LAST_PROCCESSING_DATE, Formats.shortDateFormat.format(lastProcessingDate));
	}

	@Override
	public void cancelTimer() throws Exception {
		this.cancelTimer(COMMISSION_SWEEPING_TIMER);
		
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void processCommissionSweep(String recipient) throws Exception{
		this.processUtil.runAgentCommsionSweep(recipient);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void scheduleTimer(Date date) {
		LOG.debug("In  Agent Sweeping processes >>>>>>>>>>>>>>>>>>>>> Schedule for next date.");
		Calendar calendar = Calendar.getInstance();
		Map<String, String> configMap = config.getConfigMap();
		String nextRun = null;
		try {	
			
			if (configMap != null) {
				
				nextRun = configMap.get(SystemConstants.COMMISSION_SWEEPING_PERIOD);
				LOG.debug("In   Expired Txn reversal processes >>>>>>>>>>>>>>>>>>>>> Revesal period = "+nextRun);
//				strDate = configMap.get(SystemConstants.TXN_LAST_PROCCESSING_TIME);
//				LOG.debug("In   Expired Txn reversal processes >>>>>>>>>>>>>>>>>>>>>lastProcessingDate = "+strDate);
			}
			int days = Integer.parseInt(nextRun);
			calendar.setTime(date);
			cancelTimer(COMMISSION_SWEEPING_TIMER);
			LOG.debug("In  Agent Sweeping processes >>>>>>>>>>>>>>>>>>>>> Done cancelling DAILY Timer."+date.toString());
			long time = 1000*60*60*24*days;
			LOG.debug("The number of days is "+days);
			timerService.createTimer(date, time,COMMISSION_SWEEPING_TIMER);
			LOG.debug("In  Agent Sweeping processes >>>>>>>>>>>>>>>>>>>>> Done Scheduling new timer.");
		} catch (Exception e) {
			e.printStackTrace();
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
	
//	private Date getNextRun(int days ,int hour , int minute) {
//		Calendar calendar = Calendar.getInstance();
//		Date date = new Date();
//		calendar.setTime(date);
//		calendar.set(Calendar.HOUR_OF_DAY, hour);
//		calendar.set(Calendar.MINUTE, minute);
//		calendar.add(Calendar.DATE,days);
//		System.out.println(">>>>>>>>>>>>>>>>>>>>>> Date after adding "+hour+" min = "+calendar.getTime());
//		return calendar.getTime();
//	}

}
