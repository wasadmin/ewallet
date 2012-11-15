package zw.co.esolutions.ewallet.adminweb;

import javax.persistence.EntityExistsException;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.profileservices.service.Role;

public class CreateRoleBean extends PageCodeBase{

	private Role role = new Role();

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
	
	@SuppressWarnings("unchecked")
	public String submit(){
		if(role.getRoleName()==null || role.getRoleName().equals("")){
			super.setErrorMessage("Role Name is required");
			return "failure";
		}
		try{
			
			Role r = super.getProfileService().getRoleByRoleName(role.getRoleName());
			
			if(!(r == null || r.getId()==null)){
				super.setErrorMessage("The role "+role.getRoleName()+" already exists");
				return "failure";
			}
			
			role = super.getProfileService().createRole(role, super.getJaasUserName());
			super.setInformationMessage("The role "+role.getRoleName()+" has been successfully created.");
			super.getRequestScope().put("roleId", role.getId());
			super.gotoPage("admin/viewRole.jspx");
			return "success";
		}catch(Exception e){
			super.setErrorMessage(e.getMessage());
			return "failure";
		}
	}
	
	public String cancel() {
		super.gotoPage("/admin/adminHome.jspx");
		return "cancel";
	}
}
