package zw.co.esolutions.ewallet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;


/**
 * Servlet Filter implementation class LoginFilter
 */
public class LoginFilter implements Filter {

   
    public LoginFilter() {
    	
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
		String loginPage = "/EWalletReportsWebICE/login/login.jspx";
		String errorPage = "/EWalletReportsWebICE/login/errorPage.jspx";
	    System.err.println("loginPage: " + loginPage);
	    String pageReq = httpRequest.getRequestURI();
	    //System.out.println(">>>>>>>>>>>>>>>>>>> Filter Zvirimo : "+pageReq);
		    if(pageReq != null) {
					if(pageReq.contains(".html")) {
						return;
					}
				}
				
				//System.out.println("requestedPage: " + pageReq);
				
				String actualPage = trim(pageReq.toUpperCase());
				String userName = ((String)(httpRequest.getSession().getAttribute("CurrentUser")));
	
				ProfileServiceSOAPProxy ps = new ProfileServiceSOAPProxy();
				//System.out.println(">>>>>>>>>>>>>>>>>> User Name = "+userName);
				if(userName == null || "".equalsIgnoreCase(userName)) {
					httpResponse.sendRedirect(loginPage);
				} else {
					//if(ps.canDo(userName, actualPage)) {
						chain.doFilter(httpRequest,httpResponse);
					//} else {
						//httpResponse.sendRedirect(errorPage);
					//}
				}
				
				//chain.doFilter(request, response);
		
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
            ioe.printStackTrace();
        }
    }
	
	private String trim(String url){
		String[] tokens = url.split("/");
		String pageName = tokens[tokens.length - 1];
		String[] pageTokens = pageName.split("\\.");
		
		return pageTokens[0];
		
	}
	
}
