package zw.co.esolutions.ewallet.tellerweb;

import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.process.Exception_Exception;
import zw.co.esolutions.ewallet.process.ProcessServiceSOAPProxy;
import zw.co.esolutions.ewallet.process.ProcessTransaction;
import zw.co.esolutions.ewallet.util.DateUtil;

public class TellerStartDaySummary extends PageCodeBase {
	
	
	
	private static Logger LOG ;
	
	static{
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(TellerStartDaySummary.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + TellerStartDaySummary.class);
		}
	}
	private ProcessTransaction startDayTxn;
	private String processTxnId;
	private long netCashHeld;
	public ProcessTransaction getStartDayTxn() {
		ProcessServiceSOAPProxy service= new ProcessServiceSOAPProxy();
		/*
		 * get process trx by id
		 */
		try {
			this.startDayTxn=service.findProcessTransactionById(getProcessTxnId());
			LOG.debug(" tellerstartdaysummary found startDayTxn ------------------ id    "+startDayTxn.getId());
			LOG.debug("tellerstartdaysummary------------------------------------------"+startDayTxn.getAmount());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return startDayTxn;
	}
	public void setStartDayTxn(ProcessTransaction startDayTxn) {
		this.startDayTxn = startDayTxn;
	}
	public long getNetCashHeld() {
		ProcessServiceSOAPProxy service= new ProcessServiceSOAPProxy();
		String profileId=getStartDayTxn().getProfileId();
		try {
			LOG.debug("---tellerstartdaysummary-------------profile ------------------"+profileId);
			LOG.debug("------------------------");
			this.netCashHeld=service.getTellerNetCashPosition(profileId, DateUtil.convertToXMLGregorianCalendar(new Date()));
		LOG.debug("-------tellerstartdaysummary-----------------------netcashHeld>>>>>>>>>>>>>>>>>>>>"+netCashHeld);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * get netcash held
		 * 
		 */
		
		return netCashHeld;
	}
	public void setNetCashHeld(long netCashHeld) {
		this.netCashHeld = netCashHeld;
	}
	public String getProcessTxnId() {
	
		this.processTxnId=(String) getRequestScope().get("processTxnId");
		LOG.debug("prtxnid>>>>>>>tellerstartdaysummary>>>>>>>>>>>>>>>>>"+processTxnId);
		return processTxnId;
	}
	public void setProcessTxnId(String processTxnId) {
		this.processTxnId = processTxnId;
	}
	
	
	

}
