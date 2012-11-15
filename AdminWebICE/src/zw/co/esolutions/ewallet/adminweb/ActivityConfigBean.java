package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.List;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.audittrailservices.service.Activity;

public class ActivityConfigBean extends PageCodeBase{

	private List<Activity> activityList;

	public List<Activity> getActivityList() {
		try {
			activityList = super.getAuditService().getAllActivities();
			if(activityList==null || activityList.isEmpty()){
				super.setInformationMessage("No activities where found");
				activityList = new ArrayList<Activity>();
			}
		} catch (Exception e) {
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			activityList = new ArrayList<Activity>();
		}
		return activityList;
	}

	public void setActivityList(List<Activity> activityList) {
		this.activityList = activityList;
	}
	
	public String updateActivity(){
		String activityId = (String)super.getRequestParam().get("activityId");
		try {
			if(activityId !=null){
				Activity activity = super.getAuditService().findActivityById(activityId);
				if(activity.isLogged()){
					activity.setLogged(false);
				}else{
					activity.setLogged(true);
				}
				activity = super.getAuditService().editActivity(activity, super.getJaasUserName());
				this.getActivityList();
				super.setInformationMessage("Activity has been updated.");
			}else{
				super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
		}
		return "updateActivity";
	}
	
}
