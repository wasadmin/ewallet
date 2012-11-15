package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.profileservices.service.AccessRight;
import zw.co.esolutions.ewallet.profileservices.service.AccessRightStatus;

public class EditAccessRightBean extends PageCodeBase {
	
	private String accessRightId;
	private AccessRight accessRight;
	private List<SelectItem> statusList;
	private String selectedStatus;
	
	public void setAccessRightId(String accessRightId) {
		this.accessRightId = accessRightId;
	}
	public String getAccessRightId() {
		if (accessRightId == null) {
			accessRightId = (String) super.getRequestParam().get("accessRightId");
		}
		return accessRightId;
	}
	public AccessRight getAccessRight() {
		if (accessRight == null || accessRight.getId() == null) {
			if (getAccessRightId() != null) {
				accessRight = super.getProfileService().findAccessRightById(accessRightId);
				setSelectedStatus(accessRight.getStatus().name());
			} else {
				accessRight = new AccessRight();
			}
		}
		return accessRight;
	}
	public void setAccessRight(AccessRight accessRight) {
		this.accessRight = accessRight;
	}
	public List<SelectItem> getStatusList() {
		if (statusList == null) {
			statusList = new ArrayList<SelectItem>();
			for (AccessRightStatus status: AccessRightStatus.values()) {
				statusList.add(new SelectItem(status.name(), status.name()));
			}
		}
		return statusList;
	}
	public void setStatusList(List<SelectItem> statusList) {
		this.statusList = statusList;
	}
	public String getSelectedStatus() {
		return selectedStatus;
	}
	public void setSelectedStatus(String selectedStatus) {
		this.selectedStatus = selectedStatus;
	}
	
	@SuppressWarnings("unchecked")
	public String submit() {
		try {
			getAccessRight().setStatus(AccessRightStatus.valueOf(selectedStatus));
			accessRight = super.getProfileService().editAccessRight(getAccessRight(), super.getJaasUserName());
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage("The access right has been updated successfully.");
		super.getRequestScope().put("accessRightId", accessRight.getId());
		return "submit";
	}
	
	public String cancel() {
		super.gotoPage("/adminHome.jspx");
		return "cancel";
	}
}
