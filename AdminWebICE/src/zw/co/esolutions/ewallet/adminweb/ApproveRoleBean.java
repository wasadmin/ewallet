package zw.co.esolutions.ewallet.adminweb;

import java.util.List;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.profileservices.service.Role;
import zw.co.esolutions.ewallet.profileservices.service.RoleStatus;

public class ApproveRoleBean extends PageCodeBase{

	private List<Role> roleList;

	public List<Role> getRoleList() {
		if(roleList==null){
			try{
				roleList = super.getProfileService().getRoleByStatus(RoleStatus.AWAITING_APPROVAL);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return roleList;
	}

	public void setRoleList(List<Role> roleList) {
		this.roleList = roleList;
	}
	
	public String approve(){
		Role role=null;
		try {
			String roleId = (String)super.getRequestParam().get("roleId");
			
			role = super.getProfileService().findRoleByRoleId(roleId);
			role=super.getProfileService().approveRole(role, super.getJaasUserName());
			super.setInformationMessage("Role "+role.getRoleName()+" approves successfully");
			//Refresh List
			roleList = null;
			this.getRoleList();
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(e.getMessage());
		}
		return "approve";
	}
	
	public String reject(){
		Role role=null;
		try {
			String roleId = (String)super.getRequestParam().get("roleId");
			
			role = super.getProfileService().findRoleByRoleId(roleId);
			role=super.getProfileService().rejectRole(role, super.getJaasUserName());
			super.setInformationMessage("Role "+role.getRoleName()+" approves successfully");
			//Refresh List
			roleList = null;
			this.getRoleList();
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(e.getMessage());
		}
		return "approve";
	}
	
}
