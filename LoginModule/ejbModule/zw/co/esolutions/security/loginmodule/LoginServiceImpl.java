package zw.co.esolutions.security.loginmodule;

import javax.ejb.Stateless;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

/**
 * Session Bean implementation class LoginServiceImpl
 */
@Stateless
public class LoginServiceImpl implements LoginService {

    /**
     * Default constructor. 
     */
    public LoginServiceImpl() {
        // TODO Auto-generated constructor stub
    }
    
    public LoginContext createLoginContext(String module, String username , String password){
		LoginContext lc = null;
		try {
		    lc = new LoginContext(module,new MyCallbackHandler(username, password));
		} catch (LoginException le) {
		   // System.err.println("Cannot create LoginContext. "+ le.getMessage()+" LoginException");
		} catch (SecurityException se) {
		   // System.err.println("Cannot create LoginContext. "+ se.getMessage()+ " SecurityException");
		} 
		return lc;
	}

}
