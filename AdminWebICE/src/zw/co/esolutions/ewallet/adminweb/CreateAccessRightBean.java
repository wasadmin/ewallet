package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.profileservices.service.AccessRight;
import zw.co.esolutions.ewallet.profileservices.service.AccessRightStatus;

public class CreateAccessRightBean extends PageCodeBase {
	
	private AccessRight accessRight = new AccessRight();
	private List<SelectItem> statusList;
	private String selectedStatus;
	
	public AccessRight getAccessRight() {
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
			super.getProfileService().createAccessRight(getAccessRight(), super.getJaasUserName());
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage("The access right " + accessRight.getActionName().toUpperCase() + " has been created successfully.");
		accessRight.setActionName("");	//clear the field
		super.getRequestScope().put("accessRightId", accessRight.getId());
		return "submit";
	}
	
	public String cancel() {
		super.gotoPage("/admin/adminHome.jspx");
		return "cancel";
	}
}
