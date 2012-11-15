package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.audittrailservices.service.Activity;
import zw.co.esolutions.ewallet.audittrailservices.service.AuditTrail;
import zw.co.esolutions.ewallet.audittrailservices.service.Exception_Exception;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.SystemConstants;

public class SearchLogsBean extends PageCodeBase{

	private String selectedCriteria;
	private List<SelectItem> criteriaList;
	private Date startTime;
	private Date endTime;
	private String username;
	private String activityName;
	private List<SelectItem> activityList;
	private String selectedActivity;
	private boolean showUsername;
	private boolean showActivityName;
	private List<AuditTrail> auditTrailList;
	private Properties config = SystemConstants.configParams;
	
	public SearchLogsBean(){
		startTime = new Date();
		endTime = new Date();
	}
	
	public String getSelectedCriteria() {
		return selectedCriteria;
	}
	public void setSelectedCriteria(String selectedCriteria) {
		this.selectedCriteria = selectedCriteria;
	}
	public List<SelectItem> getCriteriaList() {
		if(criteriaList==null){
			criteriaList = new ArrayList<SelectItem>();
			criteriaList.add(new SelectItem("Period","Period"));
			criteriaList.add(new SelectItem("Username","Username"));
			criteriaList.add(new SelectItem("Activity","Activity"));
		}
		return criteriaList;
	}
	public void setCriteriaList(List<SelectItem> criteriaList) {
		this.criteriaList = criteriaList;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getActivityName() {
		return activityName;
	}
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	public List<SelectItem> getActivityList() {
		activityList = new ArrayList<SelectItem>();
		activityList.add(new SelectItem("","------"));
		try {
			List<Activity> activities = super.getAuditService().getAllActivities();
			for(Activity activity:activities){
				activityList.add(new SelectItem(activity.getId(),activity.getName()));
			}
		} catch (Exception e) {
			
		}
		return activityList;
	}
	public void setActivityList(List<SelectItem> activityList) {
		this.activityList = activityList;
	}
	public boolean isShowUsername() {
		return showUsername;
	}
	public void setShowUsername(boolean showUsername) {
		this.showUsername = showUsername;
	}
	public boolean isShowActivityName() {
		return showActivityName;
	}
	public void setShowActivityName(boolean showActivityName) {
		this.showActivityName = showActivityName;
	}
	public List<AuditTrail> getAuditTrailList() {
		if(auditTrailList==null){
			auditTrailList = new ArrayList<AuditTrail>();
		}
		return auditTrailList;
	}
	public String getSelectedActivity() {
		return selectedActivity;
	}
	public void setSelectedActivity(String selectedActivity) {
		this.selectedActivity = selectedActivity;
	}
	public void setAuditTrailList(List<AuditTrail> auditTrailList) {
		this.auditTrailList = auditTrailList;
	}
	
	public void criteriaAction(ValueChangeEvent event){
		String s = (String)event.getNewValue();
		if(s.equals("Activity")){
			showUsername = false;
			showActivityName = true;
		}else if(s.equals("Username")){
			showUsername = true;
			showActivityName = false;
		}else{
			showUsername = false;
			showActivityName = false;
		}
	}
	
	public String submit(){
		try {
			
			int maxDateRange = Integer.parseInt(config.getProperty("SYSTEM_MAX_DATE_RANGE"));
			int dateRange = this.getDateDiff(startTime, endTime);
			if(dateRange>maxDateRange){
				super.setErrorMessage("Date range above the required "+maxDateRange+" days");
				auditTrailList = new ArrayList<AuditTrail>();
				return "failure";
			}
			if(selectedCriteria.equals("Period")){
				auditTrailList = super.getAuditService().getByTimePeriod(DateUtil.convertToXMLGregorianCalendar(startTime),DateUtil.convertToXMLGregorianCalendar(endTime));
			}else if(selectedCriteria.equals("Username")){
				auditTrailList = super.getAuditService().getByUsernameAndTimePeriod(username, DateUtil.convertToXMLGregorianCalendar(startTime),DateUtil.convertToXMLGregorianCalendar(endTime));
			}else if(selectedCriteria.equals("Activity")){
				auditTrailList = super.getAuditService().getByActivityAndTimePeriod(selectedActivity, DateUtil.convertToXMLGregorianCalendar(startTime),DateUtil.convertToXMLGregorianCalendar(endTime));
			}
		} catch (Exception_Exception e) {
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			e.printStackTrace();
		}
		return "search";
	}
	
	private int getDateDiff(Date date1,Date date2){
		return Math.abs((int)( (date1.getTime() - date2.getTime()) / (1000 * 60 * 60 * 24)));
	}
}
