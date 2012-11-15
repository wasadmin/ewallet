package zw.co.esolutions.ewallet.filter;

import java.io.IOException;


import javax.security.auth.Subject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import zw.co.esolutions.security.permission.PermissionFactory;
import zw.co.esolutions.security.permission.URLPermission;
import zw.co.esolutions.security.util.AuthUtils;


/**
 * Servlet Filter implementation class LoginFilter
 */
public class LoginFilter implements Filter {

    private URLPermission perm;
    private static boolean once;
    public static boolean isOnce() {
		return once;
	}

	public static void setOnce(boolean once) {
		LoginFilter.once = once;
	}

	private Object context;
    public LoginFilter() {
    	once = true;
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		String loginPage = "/TellerWeb/login/login.jspx";
		String errorPage = "/TellerWeb/login/errorPage.jspx";
	    System.err.println("loginPage: " + loginPage);
	    String pageReq = httpRequest.getRequestURI();
	    
	    System.out.println("requestedPage: " + pageReq);
	    perm = (URLPermission) PermissionFactory.getInstance().getPermission(trim(pageReq.toUpperCase()));
	    Subject subject = ((Subject)(httpRequest.getSession().getAttribute("CurrentUser")));

	    if(once && subject!=null){
	    	System.out.println("Setting up the application can u dig it :::::::::::::::::::"); 
	    	AuthUtils.setup(subject);
	    	once = false;
		}
	    
		if(subject == null && (!httpRequest.getRequestURI().equals(loginPage))) {
			httpResponse.sendRedirect(loginPage);
	    } else if (subject == null &&  httpRequest.getRequestURI().equals(loginPage)) {
	          // login page is always permitted
	           chain.doFilter(httpRequest,httpResponse);

	    } else{
	    	//System.out.println("The subject is "+subject+":"+subject.getPublicCredentials().size()+" : Result of auth :"+AuthUtils.permitted(subject, perm));
	    	//boolean b = false;
	    	context = System.getSecurityManager().getSecurityContext();
	    	if(AuthUtils.permitted(subject, perm , context)){
	//    		chain.doFilter(httpRequest,httpResponse);
		    }else {
		    	   // subject is not permitted; redirect to error page
		    	httpResponse.sendRedirect(errorPage);
	        }
	    }
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}
	
	protected void loginRedirect(HttpServletResponse res, String url) {
        try {
            res.sendRedirect(url);
        } catch (java.io.IOException ioe) {
            //ignore
        }
    }
	
	private String trim(String url){
		String[] tokens = url.split("/");
		String pageName = tokens[tokens.length - 1];
		String[] pageTokens = pageName.split("\\.");
		
		return pageTokens[0];
		
	}
	
}
