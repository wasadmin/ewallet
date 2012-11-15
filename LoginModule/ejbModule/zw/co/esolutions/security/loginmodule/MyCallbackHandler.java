package zw.co.esolutions.security.loginmodule;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class MyCallbackHandler implements CallbackHandler{
	
	private String username;
    private String password;
    
    
    public MyCallbackHandler(String username, String password) {
        this.username=username;
        this.password=password;        
    }
     
    
	@Override
	public void handle(Callback[] callbacks) throws IOException,
			UnsupportedCallbackException {
		for (int i = 0; i< callbacks.length; i++) {
            if (callbacks[i] instanceof NameCallback) {
                NameCallback nc = (NameCallback)callbacks[i];
                nc.setName(username);
            } else if (callbacks[i] instanceof PasswordCallback) {
                PasswordCallback pc = (PasswordCallback)callbacks[i];
                pc.setPassword(password.toCharArray());
            }
        }		
	}
}
