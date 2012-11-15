 package zw.co.esolutions.security.loginmodule;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.util.SystemConstants;
import zw.co.esolutions.security.principal.UserPrincipal;
import zw.co.esolutions.security.util.LoginSourceAdapter;

public class EWalletLoginModule implements LoginModule{
	
	//Initial state 
	private static Subject subject;
    private CallbackHandler callbackHandler;
    private Map<String, ?> sharedState;
    private Map<String, ?> options;
    
    //the authentication status
	private boolean succeeded = false;
    private boolean commitSucceeded = false;   
    private String username;
    private char[] password;
    
    private static UserPrincipal userPrincipal;
    private ProfileServiceSOAPProxy profileService;
    private static boolean once = true;
    
	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map<String, ?> sharedState, Map<String, ?> options) {
		this.subject = subject;
		this.callbackHandler = callbackHandler;
		this.sharedState = sharedState;
		this.options = options;
	}

	@Override
	public boolean login() throws LoginException {
		profileService = new ProfileServiceSOAPProxy();
		if (callbackHandler == null)
		    throw new LoginException("Error: no CallbackHandler available ");

		Callback[] callbacks = new Callback[2];
		callbacks[0] = new NameCallback("username: ");
		callbacks[1] = new PasswordCallback("password: ", false);
 
		try {
			callbackHandler.handle(callbacks);
		    username = ((NameCallback)callbacks[0]).getName();
		    char[] tmpPassword = ((PasswordCallback)callbacks[1]).getPassword();
		    if (tmpPassword == null) {
		    	tmpPassword = new char[0];
		    }
		    password = new char[tmpPassword.length];
		    System.arraycopy(tmpPassword, 0,
				password, 0, tmpPassword.length);
		    ((PasswordCallback)callbacks[1]).clearPassword();
	 
		} catch (java.io.IOException ioe) {
		    throw new LoginException(ioe.toString());
		} catch (UnsupportedCallbackException uce) {
		    throw new LoginException("Error: " + uce.getCallback().toString() +
			" not available to garner authentication information " +
			"from the user");
		}

		boolean usernameCorrect = false;
		boolean passwordCorrect = false;
		String result = profileService.authenticateUser(username, new String(password));
		if (result.equals(SystemConstants.AUTH_STATUS_AUTHENTICATED)) {
//			System.out.println("Authentication succeded");
		    passwordCorrect = true;
		    usernameCorrect = true;
		    succeeded = true;
		    return passwordCorrect;
		} else {
		    succeeded = false;
		    username = null;
		    for (int i = 0; i < password.length; i++)
		    	password[i] = ' ';
		    password = null;
		    throw new FailedLoginException(result);
		    /*if (!usernameCorrect) {
		    	throw new FailedLoginException("User Name Incorrect");
		    } else {
		    	throw new FailedLoginException("Password Incorrect");
		    }*/
		}
	}

	@Override
	public boolean commit() throws LoginException {
		userPrincipal = new UserPrincipal(username);
		if (succeeded == false) {
		    return false;
		} else {
			this.populateSubject(subject, userPrincipal);
	    	return true;
		}
	}
	
	@Override
	public boolean logout() throws LoginException {
		if (succeeded == false) {
		    return false;
		} else if (succeeded == true && commitSucceeded == false) {
		    succeeded = false;
		    username = null;
		    if (password != null) {
				for (int i = 0; i < password.length; i++)
				    password[i] = ' ';
				password = null;
		    }
		    userPrincipal = null;
		} else {
		    logout();
		}
		return true;
	}

	@Override
	public boolean abort() throws LoginException {
		subject.getPrincipals().remove(userPrincipal);
		succeeded = false;
		succeeded = commitSucceeded;
		username = null;
		if (password != null) {
		    for (int i = 0; i < password.length; i++)
			password[i] = ' ';
		    password = null;
		}
		userPrincipal = null;
		return true;
	}

	public static Subject getSubject() {
		return subject;
	}

	public static UserPrincipal getUserPrincipal() {
		return userPrincipal;
	}

	public static void setUserPrincipal(UserPrincipal userPrincipal) {
		EWalletLoginModule.userPrincipal = userPrincipal;
	}

	public static boolean isOnce() {
		return once;
	}

	public static void setOnce(boolean once) {
		EWalletLoginModule.once = once;
	}
	
	public void populateSubject(Subject subject , UserPrincipal userPrincipal){
		subject.getPrincipals().add(userPrincipal);
	}
	
}
