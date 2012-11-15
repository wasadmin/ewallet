package zw.co.esolutions.security.util;


import java.security.AccessControlException;
import java.security.Permission;
import java.security.Policy;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.Subject;

import zw.co.esolutions.ewallet.profileservices.service.AccessRightStatus;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.profileservices.service.RoleAccessRight;
import zw.co.esolutions.security.loginmodule.EWalletLoginModule;
import zw.co.esolutions.security.principal.CompositePolicy;
import zw.co.esolutions.security.principal.DbPolicy;


public class AuthUtils {
	
	private static List<RoleAccessRight> accessRightList;
    public AuthUtils() {}

    static public boolean permitted(Subject subj, final Permission p,final Object context) {
    	ProfileServiceSOAPProxy pssp = new ProfileServiceSOAPProxy();
    	
 //   	final SecurityManager sm = System.getSecurityManager();
    	boolean permitted = false;
//        System.err.println("trying to auth "+ subj + " with permission "+p);
//        try {
//            Subject.doAsPrivileged(subj, new PrivilegedExceptionAction<Object>(){
//                public Object run(){ 
//                    System.err.println("sm: "+sm);
//                    sm.checkPermission(p, context);
//                    return null;
//                }
//            },null);
//            permitted = true;
//        } catch (AccessControlException ace) {
//            System.err.println("exception caught: "+ace);
//            permitted = false;
//        } catch (PrivilegedActionException pae) {
//            if (pae.getException() instanceof SecurityException) {
//                System.err.println("exception caught: "+pae);
//            } else {
//                System.err.println("what the hell is this: "+pae);
//            }
//            permitted = false;
//        }
    	try {
    		Profile profile = pssp.getProfileByUserName(getUserName(subj));
    		RoleAccessRight accss = pssp.getRoleAccessRightByRoleActionNameAndStatus(profile.getRole().getId(), p.getName().toUpperCase(), AccessRightStatus.ENABLED);
    		if(accss == null) {
    			permitted = false;
    		} else {
    			permitted = true;
    		}
		} catch (Exception e) {
			e.printStackTrace();
			permitted = false;
		}
        return permitted;
    }
    
    public static void setup(Subject subj){
    	ProfileServiceSOAPProxy pssp = new ProfileServiceSOAPProxy();
    	try {
    		
    		Profile p = pssp.getProfileByUserName(EWalletLoginModule.getUserPrincipal().getName());
    		accessRightList = pssp.getRoleAccessRightByRole(p.getRole().getId());
            Subject.doAsPrivileged(subj, new PrivilegedExceptionAction<Object>(){
                public Object run(){ 
                	try {
                		
                		System.setSecurityManager(new SecurityManager()); 
                	      Policy defaultPolicy = Policy.getPolicy();
                	      DbPolicy dbPolicy = new DbPolicy();
                	      List<Policy> policies = new ArrayList<Policy>();
                	      policies.add(dbPolicy);
                	      policies.add(defaultPolicy);
                	      CompositePolicy p = new CompositePolicy(policies);
                	      Policy.setPolicy(p); 
           
                	  } catch(Exception e) {
                		 e.printStackTrace();
                	  }
                    return null;
                }
            },null);
        } catch (AccessControlException ace) {
          //  System.err.println("exception caught: "+ace);
        } catch (PrivilegedActionException pae) {
            if (pae.getException() instanceof SecurityException) {
         //       System.err.println("exception caught: "+pae);
            } else {
         //       System.err.println("what the hell is this: "+pae);
            }
        }
    }

	public static List<RoleAccessRight> getAccessRightList() {
		return accessRightList;
	}

	public static void setAccessRightList(List<RoleAccessRight> accessRightList) {
		AuthUtils.accessRightList = accessRightList;
	}
	
	public static String getUserName(Subject subject) {
		String userName = null;
		try {
			for( Principal p : subject.getPrincipals().toArray( new Principal [1])) {
				userName = p.getName();
			}
			return userName;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return userName;
	}
    
}

