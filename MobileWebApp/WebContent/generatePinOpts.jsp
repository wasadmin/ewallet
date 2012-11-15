
<%@page import="zw.co.esolutions.ussd.web.services.WebSession"%>
<%@page import="zw.co.esolutions.ewallet.util.NumberUtil"%>
<%@page import="zw.co.esolutions.mobile.web.utils.WebConstants"%>
<%@page import="zw.co.esolutions.mobile.web.conf.MobileWebConfiguration"%>
<%@page import="zw.co.esolutions.ewallet.services.proxy.MobileWebServiceProxy"%>
<%@page import="zw.co.esolutions.mobile.transactions.utils.LoginUtils"%>
<%@page import="zw.co.esolutions.ewallet.customerservices.service.GenerateTxnPassCodeResp"%>
<%@page import="zw.co.esolutions.ewallet.services.proxy.CustomerServiceProxy"%>
<%@page import="zw.co.esolutions.ewallet.customerservices.service.GenerateTxnPassCodeResponse"%><%
/*
' generatePinOpts.jsp
' --------
*/

response.setContentType("text/json");
%>
<% 
	String json = "";
	try {
		String mobile = request.getParameter("mobile");
		String bankConf = request.getParameter("bankConf");
		String bankId = MobileWebConfiguration.getInstance().getStringValueOf(bankConf); 
		GenerateTxnPassCodeResp passcodeResp = null;
		try {
			mobile = NumberUtil.formatMobileNumber(mobile);
		} catch(Exception e1) {
		
		}
		if(mobile != null) {
			passcodeResp = CustomerServiceProxy.getInstance().generateTxnPassCode("MOBILE");
		}
		
		WebSession wSession = MobileWebServiceProxy.getInstance().getFailedWebSession(mobile, bankId);
		if(wSession == null) {
			bankId = MobileWebConfiguration.getInstance().getStringValueOf(WebConstants.BANK_ID_2);
			 wSession = MobileWebServiceProxy.getInstance().getFailedWebSession(mobile, bankId);
		}
		
		
		//Already Failed response
		if(wSession != null) {
			passcodeResp.setFirstIndex(wSession.getFirstIndex());
			passcodeResp.setSecondIndex(wSession.getSecondIndex());
		}
		
		json = LoginUtils.getJson(passcodeResp);
		
	} catch(Exception e) {
	 e.printStackTrace();
	}

%>

<%=json%>