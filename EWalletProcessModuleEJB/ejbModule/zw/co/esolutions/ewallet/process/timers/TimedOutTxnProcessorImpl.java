package zw.co.esolutions.ewallet.process.timers;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

import zw.co.esolutions.ewallet.enums.ResponseType;
import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.msg.RequestInfo;
import zw.co.esolutions.ewallet.msg.ResponseInfo;
import zw.co.esolutions.ewallet.process.ConfigurationsLocal;
import zw.co.esolutions.ewallet.process.ProcessUtil;
import zw.co.esolutions.ewallet.process.model.ProcessTransaction;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.SystemConstants;

/**
 * Session Bean implementation class TimedOutTxnProcessorImpl
 */
@Stateless
public class TimedOutTxnProcessorImpl implements TimedOutTxnProcessor{

    /**
     * Default constructor. 
     */
    public TimedOutTxnProcessorImpl() {
        // TODO Auto-generated constructor stub
    }
    
    @Resource
	private TimerService timerService;
	@EJB
	private ConfigurationsLocal config;
	@EJB
	private ProcessUtil processUtil;
	private static Logger LOG;
	private final static String TXN_REVERSAL_TIMER = "Txn_Reversal_Timer";
 
    static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(MonthlyProcessor.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + TimedOutTxnProcessorImpl.class);
		}
	}
     
	@Timeout
	public void processAgentStatements(Timer timer){
		LOG.debug("***************** running timed out reversal processes *******************");		
		Map<String, String> configMap = config.getConfigMap();
		
		String expPeriod = null;

		if (configMap != null) {
			
			expPeriod = configMap.get(SystemConstants.TXN_REVERSAL_EXPIRATION_PERIOD);
			LOG.debug("In   Expired Txn reversal processes >>>>>>>>>>>>>>>>>>>>> Revesal period = "+expPeriod);
//			strDate = configMap.get(SystemConstants.TXN_LAST_PROCCESSING_TIME);
//			LOG.debug("In   Expired Txn reversal processes >>>>>>>>>>>>>>>>>>>>>lastProcessingDate = "+strDate);
		}
				
		try {
			//Setting the cutoff date for Txfs to be reversed 
			Date expirationDate = new Date(System.currentTimeMillis());
			int mins = Integer.parseInt(expPeriod);
			Calendar c = Calendar.getInstance();
			c.setTime(expirationDate);
			c.add(Calendar.MINUTE, -mins);
			expirationDate = c.getTime();
			LOG.debug("The experiation date is >>>>>>>>>>>>>>>>>>>>>>>>>>> "+expirationDate);	
				
			LOG.debug("In  MonthlyProcessor >>>>>>>>>>>>>>>>>>>>> Starting Reversal Run at "+DateUtil.convertToDateWithTime(new Date(System.currentTimeMillis())));
			this.runExpiredTxnReversal(expirationDate);
			LOG.debug("In  MonthlyProcessor >>>>>>>>>>>>>>>>>>>>> Done Running Expired Txn reversal process at "+DateUtil.convertToDateWithTime(new Date(System.currentTimeMillis())));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		config.setConfiguration(SystemConstants.NHW_LAST_PROCCESSING_DATE, Formats.shortDateFormat.format(lastProcessingDate));
	}

	
	public void runExpiredTxnReversal(Date date) {
		LOG.debug("************************************************Running HOST RESPONSE TIMEOUT  TIMER for Date : ["+date + "]**************************************************");
		 
		List<ProcessTransaction> processTxns = processUtil.getTimedOutTxns(date);
		if(processTxns != null){
			LOG.debug("Got [" + processTxns.size()+"] TXNS to TIMEOUT");
			for(ProcessTransaction txn : processTxns){
				if(TransactionType.BALANCE_REQUEST.toString().equals(txn.getTransactionType().toString()) || TransactionType.BALANCE.toString().equals(txn.getTransactionType().toString()) ||
						TransactionType.MINI_STATEMENT.toString().equals(txn.getTransactionType().toString())){
					try {
						processUtil.promoteTxnState(txn, TransactionStatus.FAILED, "Host Not Available");
						LOG.debug("MARKED TXN AS FAILED due to HOST RESPONSE TIMEOUT : [" + txn.getMessageId() + " |" + txn.getTransactionType() +"]");
					} catch (Exception e) {
						LOG.debug("FAILED TO MARK TXN AS FAILED due to HOST RESPONSE TIMEOUT : [" + txn.getMessageId() + " |" + txn.getTransactionType() +"]");
						e.printStackTrace(System.err);
					}
				}else{
					try {
						//Mark original txn to TIMEOUT
						txn.setTimedOut(true);
						txn = processUtil.updateProcessTransaction(txn);
						String response = "Your transaction timed out. Reason : Host Not Available";
						RequestInfo requestInfo = processUtil.populateRequestInfo(txn);
						LOG.debug("Sending txn time out message for timed out txn ");
						ResponseInfo responseInfo = new ResponseInfo(response, ResponseCode.E830, requestInfo, ResponseType.NOTIFICATION, 0L, 0L, 0L, txn.getMessageId());
						processUtil.submitResponse(responseInfo);
						txn = processUtil.promoteTxnState(txn, TransactionStatus.TIMEOUT,"Transaction Timed out:Host not available ");
						LOG.debug("MARKED TXN AS TIMEOUT due to HOST RESPONSE TIMEOUT : [" + txn.getMessageId() + " |" + txn.getTransactionType() +"]");
						LOG.debug("Sending notification to switch ");
					} catch (Exception e) {
						LOG.debug("FAILED TO MARK TXN AS TIMEOUT due to HOST RESPONSE TIMEOUT : [" + txn.getMessageId() + " |" + txn.getTransactionType() +"]");
						e.printStackTrace(System.err);
					}
				}				
			}		
		}
		LOG.debug("HOST Response TIMEOUT TIMER COMPLETED Successfully*************************************************************");
	}
	
    
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void scheduleTimer(Date date) {
		LOG.debug("In  Txn Reversal processes >>>>>>>>>>>>>>>>>>>>> Schedule for next date.");
		Calendar calendar = Calendar.getInstance();
		String hour = null;
		
		try {
			Map<String, String> configMap = config.getConfigMap();
			hour = configMap.get(SystemConstants.TXN_REVERSAL_PROCESSING_HOUR);
			int min = Integer.parseInt(hour);
			
			calendar.setTime(date);
			cancelTimer(TXN_REVERSAL_TIMER);
			long time = 1000*60*min;
			LOG.debug("In   Expired Txn reversal processes >>>>>>>>>>>>>>>>>>>>> Done cancelling DAILY Timer."+date.toString());
			timerService.createTimer(this.getRunTime(min), time, TXN_REVERSAL_TIMER);
			LOG.debug("In   Expired Txnreversal processes >>>>>>>>>>>>>>>>>>>>> Done Scheduling new timer.");
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
	
	private Date getRunTime(int min) {
		Calendar calendar = Calendar.getInstance();
		Date date = new Date();
		calendar.setTime(date);
		System.out.println(">>>>>>>>>>>>>>>>>>>>>> Date initial value = "+calendar.getTime());
		calendar.add(Calendar.MINUTE, min);
		System.out.println(">>>>>>>>>>>>>>>>>>>>>> Date after adding "+min+" min = "+calendar.getTime());
		return calendar.getTime();
	}
}
