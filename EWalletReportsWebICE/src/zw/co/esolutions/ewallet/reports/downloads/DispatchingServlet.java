package zw.co.esolutions.ewallet.reports.downloads;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import com.ibm.ws.management.wsdm.resource.Servlet;

/**
 * Servlet implementation class DispatchingServlet
 */
public class DispatchingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DispatchingServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init() throws ServletException {
		System.out.println(">>>>>>>>>>>>>>>>>>>> INIT  ....");
	}
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pageName = request.getParameter("pageName");
		System.out.println(">>>>>>>>>>. Page "+pageName);
		try {
			response.sendRedirect("/EWalletReportsWebICE/reportsweb/"+pageName);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	

}
