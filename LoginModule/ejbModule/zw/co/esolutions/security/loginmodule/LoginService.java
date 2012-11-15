package zw.co.esolutions.security.loginmodule;
import javax.ejb.Local;
import javax.security.auth.login.LoginContext;

@Local
public interface LoginService {
	public LoginContext createLoginContext(String module, String username , String password);
}
