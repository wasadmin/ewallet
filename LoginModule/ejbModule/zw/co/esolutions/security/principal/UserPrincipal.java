package zw.co.esolutions.security.principal;

import java.io.Serializable;
import java.security.Principal;

public class UserPrincipal implements Principal, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	
	public UserPrincipal(String name){
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof UserPrincipal)) {
			return false;
	    }
	    UserPrincipal other = (UserPrincipal) obj;
	    return getName().equals(other.getName());
	}

	public int hashCode() {
		return getName().hashCode();
	}

	public String toString() {
		return getName();
	}
}
