package zw.co.esolutions.ewallet.adminweb;

import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.process.ProcessServiceSOAPProxy;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.DateUtil;

public class ScheduleExpiredTxnReversalBean extends PageCodeBase{
	
	private Date date;
	private String runningTime;
	private String expPeriod;
	
	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		if(this.date == null) {
			this.date = new Date(System.currentTimeMillis());
		}
		return date;
	}
	
	public String getRunningTime() {
		return runningTime;
	}

	public void setRunningTime(String runningTime) {
		this.runningTime = runningTime;
	}

	public String getExpPeriod() {		
		return expPeriod;
	}

	public void setExpPeriod(String expPeriod) {
		this.expPeriod = expPeriod;
	}

	public String submit() {
		String message = null;
		ProcessServiceSOAPProxy processService = new ProcessServiceSOAPProxy();
		try{
			Integer.parseInt(getExpPeriod());
			Integer.parseInt(getRunningTime());
		}catch (Exception e) {
			setErrorMessage("Enter valid number of hours !");
			return "failure";
		}
		try {
			Calendar cal = Calendar.getInstance();
//			cal.setTime(getDate());
//			cal.add(Calendar.MINUTE, 2);
			cal.setTime(DateUtil.getBeginningOfDay(getDate()));
			XMLGregorianCalendar xmlDate = DateUtil.convertToXMLGregorianCalendar(cal);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>> XML Date = "+xmlDate);
			
			message = processService.saveTxnReversalConfigData(runningTime ,expPeriod);
			message = processService.scheduleTxnReversalTimer(xmlDate);
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		if(message != null && ResponseCode.E000.name().equalsIgnoreCase(message)) {
			super.setInformationMessage("Schedule process completed successfully.");
		} else {
		super.setErrorMessage(message);
		
		}
		return "success";
	}
	
	public String cancel () {
		super.gotoPage("/admin/adminHome.jspx");
		return "success";
	}
	
	public String cancelTimer(){
		ProcessServiceSOAPProxy processService = new ProcessServiceSOAPProxy();
		String message = null;
		try{
			message = processService.cancellTxnReversalTimer();
			super.setInformationMessage("Cancel  process completed successfully.");
		}catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(message);
			
		}
		return "success";
	}
}
