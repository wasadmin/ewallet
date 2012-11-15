package zw.co.esolutions.security.permission;

import java.security.*;

public final class URLPermission extends BasicPermission {

	private static final long serialVersionUID = 1L;

	public URLPermission(String name){
		super(name);
    }
	
    public URLPermission(String name, String actions){
    	super(name, actions);
    }
}

