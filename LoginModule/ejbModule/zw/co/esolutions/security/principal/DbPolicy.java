package zw.co.esolutions.security.principal;

import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.Principal;
import java.security.ProtectionDomain;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import zw.co.esolutions.ewallet.profileservices.service.RoleAccessRight;
import zw.co.esolutions.security.permission.URLPermission;
import zw.co.esolutions.security.util.AuthUtils;


public class DbPolicy extends Policy {

	private static final String LOG_TOPIC = DbPolicy.class.getName();
	private static final Logger LOGGER = Logger.getLogger(LOG_TOPIC);
	
		
	public PermissionCollection getPermissions(CodeSource codesource) {
		final Permissions perms = new Permissions();
		return perms;
	} 

	public PermissionCollection getPermissions(final ProtectionDomain domain) {
		final Permissions permissions = new Permissions();

		List<RoleAccessRight> parList = AuthUtils.getAccessRightList();
		Iterator<RoleAccessRight> it = parList.iterator();
		while(it.hasNext()){
			RoleAccessRight par = it.next();
			if(par.isCanDo()){
				permissions.add(new URLPermission(par.getAccessRight().getActionName()));
			}
		}

		return permissions;
	}

	public boolean implies(final ProtectionDomain domain,final Permission permission) {
		PermissionCollection perms = getPermissions(domain);
		boolean implies = perms.implies(permission);

		if (!implies) {
			LOGGER.logp(Level.FINEST, LOG_TOPIC, "implies()","Permission {0} denied for ProtectionDomain {1}/{2}",
          new Object[] { permission, domain.getCodeSource(),toString(domain.getPrincipals()) });
		}
		return implies;
	}

	public void refresh() {
		// does nothing for DB.
	}

	private String toString(Principal[] principals) {
		if (principals == null || principals.length == 0) {
			return "<empty principals>";
		}
		StringBuffer buf = new StringBuffer();

		buf.append("<");
		for (int i = 0; i < principals.length; i++) {
			Principal p = principals[i];
			buf.append("(class=");
			buf.append(p.getClass().getName());
			buf.append(", name=");
			buf.append(p.getName());
			buf.append(")");
			if (i < principals.length - 1) {
				buf.append(", ");
			}
		}
		buf.append(">");

		return buf.toString();
	}
}