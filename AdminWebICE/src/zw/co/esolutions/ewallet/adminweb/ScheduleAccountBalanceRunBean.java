/**
 * 
 */
package zw.co.esolutions.ewallet.adminweb;

import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.process.ProcessServiceSOAPProxy;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.SystemConstants;

/**
 * @author taurai
 *
 */
public class ScheduleAccountBalanceRunBean extends PageCodeBase{

	private Date date;
	/**
	 * 
	 */
	public ScheduleAccountBalanceRunBean() {
		// TODO Auto-generated constructor stub
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

	public String submit() {
		String message = null;
		boolean isForThisMonth = false;
		ProcessServiceSOAPProxy processService = new ProcessServiceSOAPProxy();
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(this.getDate());
			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(new Date(System.currentTimeMillis()));
			if(cal.get(Calendar.MONTH) == cal1.get(Calendar.MONTH)) {
				isForThisMonth = true;
			}
			XMLGregorianCalendar xmlDate = DateUtil.convertToXMLGregorianCalendar(cal);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>> XML Date = "+xmlDate);
			message = processService.scheduleTimer(SystemConstants.LAST_DAY_OF_MONTH, new Integer(cal.get(Calendar.HOUR_OF_DAY)).toString(), new Integer(cal.get(Calendar.MINUTE)).toString(), isForThisMonth, xmlDate);
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

}
