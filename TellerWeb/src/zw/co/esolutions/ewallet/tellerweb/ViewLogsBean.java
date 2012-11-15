package zw.co.esolutions.ewallet.tellerweb;

import java.util.ArrayList;
import java.util.List;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.audittrailservices.service.AuditTrail;
import zw.co.esolutions.ewallet.audittrailservices.service.AuditTrailServiceSOAPProxy;
import zw.co.esolutions.ewallet.audittrailservices.service.Exception_Exception;

public class ViewLogsBean extends PageCodeBase {
	
	private String entityId;
	private String entityType;
	private List<AuditTrail> logList;
	
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	public String getEntityId() {
		if (entityId == null) {
			entityId = (String) super.getRequestParam().get("entityId");
		}
		return entityId;
	}
	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
	public String getEntityType() {
		if (entityType == null) {
			entityType = (String) super.getRequestParam().get("entityType");
		}
		return entityType;
	}
	public void setLogList(List<AuditTrail> logList) {
		this.logList = logList;
	}
	public List<AuditTrail> getLogList() {
		AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy(); 
		if (logList == null) {
			try {
				logList = auditService.getByEntityNameAndEntityId(this.getEntityType().toString(), this.getEntityId());
			} catch (Exception_Exception e) {
				e.printStackTrace();
			}
			if (logList == null || logList.isEmpty()) {
				super.setInformationMessage("No logs found.");
				logList = new ArrayList<AuditTrail>();
			} else {
				super.setInformationMessage(logList.size() + " logs found.");
			}
		}
		return logList;
	}
}
