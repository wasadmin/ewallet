
<%@page import="zw.co.esolutions.mobile.web.utils.WebConstants"%>
<%@page import="zw.co.esolutions.ewallet.util.NumberUtil"%>
<%@page import="zw.co.esolutions.mobile.transactions.utils.LoginUtils"%>
<%@page import="com.ibm.json.java.JSONObject"%>
<%
/*
' Login.jsp
' --------
*/

response.setContentType("text/json");
%>

<% String mobile = request.getParameter("mobileNumber");
	String passwordPrompt = request.getParameter("parts");
	String pin = request.getParameter("pin");
	String bankConf = request.getParameter("bankconf");
	String nationalId = request.getParameter("natId");
	String bankId = null; 
	String json = "";
	
	
	//Json
	JSONObject jb = new JSONObject();
	jb.put("mobile", mobile);
	//jb.put("mobile", SimpleEncryption.encryt(mobile));
	jb.put("parts", passwordPrompt);
	
	
		
		
try {
		try {
			mobile = NumberUtil.formatMobileNumber(mobile);
			jb = LoginUtils.authenticateMobile(jb, bankConf, mobile, nationalId, pin, passwordPrompt);
			
		} catch(Exception e1) {
			jb.put("loginResponse", WebConstants.FAILURE_CODE);
		   jb.put("message", "Login Failed, General Error Occured");
		}
		
		json =  jb.serialize();		
		
	} catch(Exception e) {
		e.printStackTrace();
	}
	
%>

<%= json %>
