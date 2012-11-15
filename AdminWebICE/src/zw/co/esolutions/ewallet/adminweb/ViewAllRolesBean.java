package zw.co.esolutions.ewallet.adminweb;

import java.util.List;

import pagecode.PageCodeBase;

import zw.co.esolutions.ewallet.profileservices.service.Role;

public class ViewAllRolesBean extends PageCodeBase{

	private List<Role> roleList;

	public List<Role> getRoleList() {
		try{
			roleList = super.getProfileService().getActiveRoles();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return roleList;
	}

	public void setRoleList(List<Role> roleList) {
		this.roleList = roleList;
	}
	
	public String view(){
		super.gotoPage("admin/viewRole.jspx");
		return "view";
	}
	
}
