package zw.co.esolutions.security.principal;

import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import zw.co.esolutions.security.permission.URLPermission;

@SuppressWarnings("unchecked")
public class CompositePolicy extends Policy {

	private List<Policy> policies = Collections.EMPTY_LIST;
	private Policy policy;

	public CompositePolicy(List<Policy> policies) {
		this.policies = new ArrayList<Policy>(policies);
	}
	
	public CompositePolicy(Policy policy) {
		this.policy = policy;
	}

	public PermissionCollection getPermissions(ProtectionDomain domain) {
		Permissions perms = new Permissions();
		for (Iterator<Policy> itr = policies.iterator(); itr.hasNext();) {
			Policy p = itr.next();
			PermissionCollection permCol = p.getPermissions(domain);
			for (Enumeration<Permission> en = permCol.elements(); en.hasMoreElements();) {
				Permission p1 = en.nextElement();
				perms.add(p1);
			}
		}
		return perms;
	}

	public boolean implies(final ProtectionDomain domain,final Permission permission) {
		boolean implies = false;
		for (Iterator<Policy> itr = policies.iterator(); itr.hasNext();) {
			Policy p =itr.next();
			if(permission instanceof URLPermission) {
	
				if (p instanceof DbPolicy) {
					implies = p.implies(domain, permission);
				} else {
					
				}
			} else {
				implies = p.implies(domain, permission);
				
			} 

		}

		if (implies) {
			return true;
		}else{
			return false;
		}
	}

	public PermissionCollection getPermissions(CodeSource codesource) {
		Permissions perms = new Permissions();
		for (Iterator<Policy> itr = policies.iterator(); itr.hasNext();) {
			Policy p = itr.next();
			PermissionCollection permCol = p.getPermissions(codesource);
			for (Enumeration<Permission> en = permCol.elements(); en.hasMoreElements();) {
				Permission p1 = en.nextElement();
				perms.add(p1);
			}
		}

		return perms;
	}

	public void refresh() {
		for (Iterator<Policy> itr = policies.iterator(); itr.hasNext();) {
			Policy p = itr.next();
			p.refresh();
		}
	}
	
}