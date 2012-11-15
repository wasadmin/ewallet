package zw.co.esolutions.ewallet.adminweb;

import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.process.ProcessServiceSOAPProxy;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.DateUtil;

public class ScheduleNHReversalRunBean extends PageCodeBase{
	private Date date;
	private String runningTime;
	private String email;
	private String expPeriod;
	/**
	 * 
	 */
	public ScheduleNHReversalRunBean() {
		runningTime = getDefaultTime();
	}
	
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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
		
		try {
			
			int i= Integer.parseInt(expPeriod);
			Calendar cal = Calendar.getInstance();
			cal.setTime(this.getSetTime(getRunningTime()));
//			cal.setTime(DateUtil.getBusinessDayEndOfDay(new Date()));
//			cal.add(Calendar.DATE, 1);
			XMLGregorianCalendar xmlDate = DateUtil.convertToXMLGregorianCalendar(cal);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>> XML Date = "+xmlDate);
			
			message = processService.saveCollectionConfigurationData(email ,runningTime ,expPeriod);
			message = processService.scheduleCollectionTimer(xmlDate);
		}catch(NumberFormatException nfe){
			super.setErrorMessage("Enter valid Expiration period.");
			return "failure";
		} catch (Exception e) {
			e.printStackTrace(System.out);
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
			message = processService.cancellCollectionTimer();
			super.setInformationMessage("Cancel  process completed successfully.");
		}catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(message);
			
		}
		return "success";
	}
	
	private Date getSetTime(String time){
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		String [] t = time.split(":");
		c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(t[0]));
		c.set(Calendar.MINUTE, Integer.parseInt(t[1]));
		return c.getTime();
	}
	
	private String getDefaultTime(){
		Date date = DateUtil.getBusinessDayEndOfDay(new Date());
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		String minute = null;
		int i = c.get(Calendar.MINUTE);
		if(i < 10){
			minute = "0"+i;
		}else{
			minute = i+"";
		}
		return runningTime = c.get(Calendar.HOUR_OF_DAY)+":"+minute;
	}
//	public static void main(String []args){
//		String timeToRun = "19:40";
//		DateFormat df = new SimpleDateFormat("HH:mm");
//		try {
//			Date d = df.parse(timeToRun);
//			System.out.println("XXXXXXXX"+d);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
