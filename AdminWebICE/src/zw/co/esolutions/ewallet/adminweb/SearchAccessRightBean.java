package zw.co.esolutions.ewallet.adminweb;

import java.util.List;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.profileservices.service.AccessRight;
import zw.co.esolutions.ewallet.profileservices.service.AccessRightStatus;

public class SearchAccessRightBean extends PageCodeBase {
	
	private List<AccessRight> accessRightList;
	
	public void setAccessRightList(List<AccessRight> accessRightList) {
		this.accessRightList = accessRightList;
	}
	public List<AccessRight> getAccessRightList() {
		if (accessRightList == null) {
			try {
				accessRightList = super.getProfileService().getAccessRight();
			} catch(Exception e) {
				
			}
		}
		return accessRightList;
	}
	
	public String edit() {
		return "edit";
	}
	
	public String delete() {
		return "delete";
	}
	
	@SuppressWarnings("unchecked")
	public String changeStatusValue() {
		String id = (String) super.getRequestParam().get("accessRightId");
		try {
			if (id != null) {
				AccessRight ar = super.getProfileService().findAccessRightById(id);
				if (AccessRightStatus.ENABLED.name().equals(ar.getStatus().name())) {
					ar.setStatus(AccessRightStatus.DISABLED);
				} else {
					ar.setStatus(AccessRightStatus.ENABLED);
				}
				ar = super.getProfileService().editAccessRight(ar, super.getJaasUserName());
				
			} else {
				super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
				return "failure";
			}
		} catch (Exception e) {
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage("Access Right updated successfully.");
		return "changeStatusValue";
	}
}
