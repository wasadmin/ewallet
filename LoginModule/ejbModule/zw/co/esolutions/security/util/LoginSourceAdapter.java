package zw.co.esolutions.security.util;

import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.util.SystemConstants;


public class LoginSourceAdapter {
	ProfileServiceSOAPProxy pssp;
	
	public boolean authenticate(String name ,char[] password ){
		
		pssp = new ProfileServiceSOAPProxy();
		String result= pssp.authenticateUser(name, new String(password));
		if(result.equalsIgnoreCase(SystemConstants.AUTH_STATUS_AUTHENTICATED)){
			return true;
		}else{
			return false;
		}
	}
}
