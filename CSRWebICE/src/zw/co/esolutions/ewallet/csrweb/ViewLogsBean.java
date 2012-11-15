package zw.co.esolutions.ewallet.csrweb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.audittrailservices.service.AuditTrail;

public class ViewLogsBean extends PageCodeBase{

	private String entityName;
	private String entityId;
	private List<AuditTrail> auditTrailList;
	private String previous;
	
	public void viewLogs(ActionEvent event){
		this.entityName = (String)event.getComponent().getAttributes().get("entityName");
		this.entityId = (String)event.getComponent().getAttributes().get("entityId");
		this.previous = (String)event.getComponent().getAttributes().get("previous");
	}
	public String getEntityName() {
		return entityName;
	}
	
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	
	public String getEntityId() {
		return entityId;
	}
	
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	
	public String getPrevious() {
		return previous;
	}
	public void setPrevious(String previous) {
		this.previous = previous;
	}
	
	public List<AuditTrail> getAuditTrailList() {
		try {
			auditTrailList = super.getAuditService().getByEntityNameAndEntityId(getEntityName(), getEntityId());
			if(auditTrailList==null || auditTrailList.isEmpty()){
				auditTrailList= new ArrayList<AuditTrail>();
				super.setInformationMessage("No logs where found.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			auditTrailList= new ArrayList<AuditTrail>();
		}		
		
		return auditTrailList;
	}
	public void setAuditTrailList(List<AuditTrail> auditTrailList) {
		this.auditTrailList = auditTrailList;
	}
	
	
	
	public String back(){
		System.out.println(this.getPrevious());
		super.gotoPage(this.getPrevious());
		return "back";
	}
	
}
