package zw.co.esolutions.security.util;

import java.security.Permissions;
import java.util.Iterator;
import java.util.List;

import zw.co.esolutions.ewallet.profileservices.service.RoleAccessRight;
import zw.co.esolutions.security.permission.URLPermission;

public class PermissionService {
	
	private static boolean isOnce;

	public static void getProfilePermissions(Permissions permissions){
		if (!isOnce) {
			List<RoleAccessRight> parList = AuthUtils.getAccessRightList();
			Iterator<RoleAccessRight> it = parList.iterator();
			while(it.hasNext()){
				RoleAccessRight par = it.next();
				if(par.isCanDo()){
					permissions.add(createPermission(par.getAccessRight().getActionName()));
				}
			}
			isOnce = true;
		} else {
		}
	}
	
	private static URLPermission createPermission(String name){
		return new URLPermission(name);
	}
}
