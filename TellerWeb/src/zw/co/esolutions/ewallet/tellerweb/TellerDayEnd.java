package zw.co.esolutions.ewallet.tellerweb;

import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.enums.DayEndStatus;
import zw.co.esolutions.ewallet.process.DayEnd;
import zw.co.esolutions.ewallet.process.DayEndResponse;
import zw.co.esolutions.ewallet.process.ProcessServiceSOAPProxy;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.JsfUtil;
import zw.co.esolutions.ewallet.util.MoneyUtil;

public class TellerDayEnd extends PageCodeBase {
	private double cashTendered;
	private Date dayEndDate;
	private List<DayEnd> dayEnds;
	private List<SelectItem> dayStatus;
	private String selectedStatus;
	private Date startDate;
	private Date endDate;
	
	
private static Logger LOG ;
	
	static{
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(TellerDayEnd.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + TellerDayEnd.class);
		}
	}
	
	
	public TellerDayEnd() {
		super();
		// TODO Auto-generated constructor stub
		//searchDayEnds();
	}
	public String getSelectedStatus() {
		return selectedStatus;
	}
	public void setSelectedStatus(String selectedStatus) {
		this.selectedStatus = selectedStatus;
	}
	public double getCashTendered() {
		return cashTendered;
	}
	public void setCashTendered(double cashTendered) {
		this.cashTendered = cashTendered;
	}
	public Date getDayEndDate() {
		return dayEndDate;
	}
	public void setDayEndDate(Date dayEndDate) {
		this.dayEndDate = dayEndDate;
	}
	
	public String runDayEnd(){
		
		try {
			if (checkValues()) {
				DayEnd dayEnd = new DayEnd();
				dayEnd.setDayEndDate(DateUtil
						.convertToXMLGregorianCalendar(dayEndDate));
				dayEnd.setCashTendered((MoneyUtil.convertToCents (cashTendered)));
				LOG.debug("Converted to cents  "+MoneyUtil.convertToCents (cashTendered));
				dayEnd.setStatus(zw.co.esolutions.ewallet.process.DayEndStatus.AWAITING_APPROVAL);
			ProcessServiceSOAPProxy proxy= new ProcessServiceSOAPProxy();
		DayEndResponse response=	proxy.isPreviousDayEndRun(getJaasUserName(), DateUtil.convertToXMLGregorianCalendar(dayEndDate));
		LOG.debug(" day "+response.getDayEndDate());
		LOG.debug("status "+response.isStatus());
		boolean ispreviousRun=response.isStatus();
		if(ispreviousRun){
			//need to run past dayEnds
			super.setInformationMessage("Day end has not  been run for  "+DateUtil.convertDateToString(DateUtil.convertToDate(response.getDayEndDate())));
		}else{
		DayEndResponse runResponse=proxy.checkIfThereAreTrxnTomark(getJaasUserName(),  DateUtil.convertToXMLGregorianCalendar(dayEndDate));
		boolean runResult=runResponse.isStatus();
		LOG.debug("Run status :::::"+runResult);
		
			LOG.debug("1 Day end id :::"+dayEnd.getDayEndId());
			if(runResult){
				ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
				Profile profile=profileService.getProfileByUserName(getJaasUserName());
				boolean floatResult=proxy.checkIfAnyFloatsPendingApproval(profile.getId());
				if(!floatResult){
				dayEnd=	proxy.runTellerDayEnd(dayEnd, getJaasUserName());
				String dayId=dayEnd.getDayEndId();
				if(dayId!=null){
					super.setInformationMessage("The day end has been successfully run for "+DateUtil.convertDateToString(dayEndDate));
					LOG.debug("2 day end is   id is "+dayEnd.getDayEndId());
					super.getRequestScope().put("dayEndId", dayEnd.getDayEndId());
					gotoPage("/teller/tellerdayEndSummary.jspx");
					
						}		
				
				
						else{
							super.setErrorMessage("Day end creation failed.");
						}
				
				} 
				
				       else if(floatResult){
			super.setErrorMessage("You have floats that have not been approved. Please contact your supervisor.");
				       }
				
			}else{
				super.setInformationMessage("There are no transactions for the day end.");
			}
		}
		
		
		
			} 
		} catch (Exception e) {
			super.setErrorMessage("Day end creation failed.");
			e.printStackTrace();
		}
		return "";
	}
	private boolean checkValues() {
		if(dayEndDate==null){
			super.setInformationMessage("Please enter a date");
			return false;
		}
		
		return true;
	}
	public String cancel() {
		super.cleanUpSessionScope();
		gotoPage("/teller/tellerHome.jspx");
		return "cancel";
	}
	public List<DayEnd> getDayEnds() {
		ProcessServiceSOAPProxy proxy= new ProcessServiceSOAPProxy();
		this.dayEnds=proxy.getDayEndByDayEndStatusAndBranch(zw.co.esolutions.ewallet.process.DayEndStatus.AWAITING_APPROVAL,super.getJaasUserName());
		
		return dayEnds;
	}
	
	public String searchDayEnds(){
		ProcessServiceSOAPProxy proxy= new ProcessServiceSOAPProxy();
		this.dayEnds=proxy.getDayEndByDayEndStatusAndBranch(zw.co.esolutions.ewallet.process.DayEndStatus.AWAITING_APPROVAL,super.getJaasUserName());
		
		return "";
	}
	
	public void setDayEnds(List<DayEnd> dayEnds) {
		this.dayEnds = dayEnds;
	}
	public List<SelectItem> getDayStatus() {
		dayStatus= JsfUtil.getSelectItemsAsList(DayEndStatus.values(), true);
		return dayStatus;
	}
	public void setDayStatus(List<SelectItem> dayStatus) {
		this.dayStatus = dayStatus;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	@SuppressWarnings("unchecked")
	public String viewDayEnd() {
		LOG.debug("Day end id value   "+super.getRequestParam().get("dayEndId"));
		super.getRequestScope().put("dayEndId", super.getRequestParam().get("dayEndId"));
		
		super.gotoPage("/teller/dayEndSummary.jspx");
		return "viewDayEndSummary";
	}
	
	
	
}
