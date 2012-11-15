package zw.co.esolutions.ewallet.adminweb;

import java.util.List;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.profileservices.service.Role;
import zw.co.esolutions.ewallet.profileservices.service.RoleAccessRight;

public class ViewRoleBean extends PageCodeBase{

	private String roleId;
	private Role role;
	private List<RoleAccessRight> rarList;
	
	public String getRoleId() {
		if (roleId == null) {
			roleId = (String) super.getRequestParam().get("roleId");
		}
		if (roleId == null) {
			roleId = (String) super.getRequestScope().get("roleId");
		}
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public Role getRole() {
		try {
			role = super.getProfileService().findRoleByRoleId(getRoleId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public List<RoleAccessRight> getRarList() {
		try {
			rarList = super.getProfileService().getRoleAccessRightByRole(getRoleId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rarList;
	}
	public void setRarList(List<RoleAccessRight> rarList) {
		this.rarList = rarList;
	}
	
	@SuppressWarnings("unchecked")
	public String changeCanDoValue() {
		String id = (String) super.getRequestParam().get("roleAccessRightId");
		try {
			if (id != null) {
				RoleAccessRight rar = super.getProfileService().findRoleAccessRightById(id);
				if (rar.isCanDo()) {
					rar.setCanDo(false);
				} else {
					rar.setCanDo(true);
				}
				rar = super.getProfileService().updateRoleAccessRight(rar, super.getJaasUserName());
				roleId = rar.getRole().getId();
				rar = null;
				this.getRarList();
		
			} else {
				super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
				return "failure";
			}
		} catch (Exception e) {
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.getRequestScope().put("roleId", roleId);
		super.setInformationMessage("Role Access Right updated successfully.");
		
		return "changeCanDoValue";
	}
	
	public String viewAll(){
		super.gotoPage("admin/viewAllRoles.jspx");
		return "viewAll";
	}
	
}
