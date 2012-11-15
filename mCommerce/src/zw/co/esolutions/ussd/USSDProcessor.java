package zw.co.esolutions.ussd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import zw.co.econet.ussdgateway.client.ApplicationConstants;
import zw.co.econet.ussdgateway.client.EconetXMLUtil;
import zw.co.esolutions.ussd.ejb.USSDSessionProcessor;
import zw.co.esolutions.ussd.util.LoggerFactory;
import zw.co.esolutions.ussd.util.SystemConstants;

/**
 * Servlet implementation class USSDProcessor
 */
public class USSDProcessor extends HttpServlet {
	
	//USSDConfiguration config;
	Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private USSDSessionProcessor ussdSessionProcessor;
	
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public USSDProcessor() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}
	
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		BufferedReader reader =  request.getReader();
		
		 String line = null;
         StringBuffer buffer = new StringBuffer();
         while ((line = reader.readLine()) != null) {
             buffer.append(line);
         }
         logger.debug("Seen >>>> ##############################################################3 :: "+buffer.toString());
         Map<String, String> ussdRequestMap = EconetXMLUtil.parseESMEXMLRequest(buffer.toString());
         for (Map.Entry<String, String> entry : ussdRequestMap.entrySet()) {
             logger.debug(entry.getKey() + " = " + entry.getValue());
         }
         String xmlResponse;
         
         xmlResponse = ussdSessionProcessor.processUSSDRequest(buffer.toString());
         //xmlResponse = getDummyResponse(ussdRequestMap);
         
         logger.debug(">>>>>>>> Response $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$4 "+xmlResponse);
         PrintWriter out = response.getWriter();
         out.print(xmlResponse);
         
		
	}
	
	public static String getDummyResponse(Map<String, String> ussdRequestMap) {
		  Map<String, String> ussdResponseMap = new java.util.HashMap<String, String>();
	         ussdResponseMap.put(ApplicationConstants.DESTINATION_NUMBER, ussdRequestMap.get(ApplicationConstants.SOURCE_NUMBER));
	         ussdResponseMap.put(ApplicationConstants.MESSAGE_BODY, "e-Solutions online");
	         ussdResponseMap.put(ApplicationConstants.TRANSACTION_TIME, ussdRequestMap.get(ApplicationConstants.TRANSACTION_TIME));
	         ussdResponseMap.put(ApplicationConstants.TRANSACTION_ID, ussdRequestMap.get(ApplicationConstants.TRANSACTION_ID));
	         ussdResponseMap.put(ApplicationConstants.SESSION_STATE, SystemConstants.ACTION_END);
	         ussdResponseMap.put(ApplicationConstants.SOURCE_NUMBER, ussdRequestMap.get(ApplicationConstants.DESTINATION_NUMBER));
	         
	         System.out.println(">>>>>>>>>>>>>>>>>>> Response ::: \n");
	         for (Map.Entry<String, String> entry : ussdResponseMap.entrySet()) {
	             System.out.println(entry.getKey() + " = " + entry.getValue());
	         }
	         
	         String xmlResponse = EconetXMLUtil.composeESMEXMLResponse(ussdResponseMap);
	         return xmlResponse;
	}

}
