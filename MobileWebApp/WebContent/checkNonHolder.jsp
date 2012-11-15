
<%@page import="com.ibm.json.java.JSONObject"%>
<%@page import="zw.co.esolutions.ewallet.customerservices.service.MobileProfile"%>
<%@page import="zw.co.esolutions.ewallet.customerservices.service.MobileProfileStatus"%>
<%@page import="zw.co.esolutions.ewallet.util.NumberUtil"%>
<%@page import="zw.co.esolutions.mobile.web.utils.WebConstants"%>
<%@page import="zw.co.esolutions.mobile.web.conf.MobileWebConfiguration"%>
<%@page import="zw.co.esolutions.ewallet.services.proxy.MobileWebServiceProxy"%>
<%@page import="zw.co.esolutions.mobile.transactions.utils.LoginUtils"%>
<%@page import="zw.co.esolutions.ewallet.customerservices.service.GenerateTxnPassCodeResp"%>
<%@page import="zw.co.esolutions.ewallet.services.proxy.CustomerServiceProxy"%>
<%@page import="zw.co.esolutions.ewallet.customerservices.service.GenerateTxnPassCodeResponse"%><%
/*
' checkNonHolder.jsp
' --------
*/

response.setContentType("text/json");
%>
<% 
	String json = "";
	try {
		String mobile = request.getParameter("targetAccount");
		String bankConf = request.getParameter("bankConf");
		String bankId = MobileWebConfiguration.getInstance().getStringValueOf(bankConf); 
		MobileProfile mobileProfile = null;
		boolean isNonHolder = false; 
		try {
			mobile = NumberUtil.formatMobileNumber(mobile);
		} catch(Exception e1) {
		
		}
		//System.out.println(">>>>>>>>>>>>>>>>>>>>>> Mobile Number : "+mobile);
		if(mobile != null) {
			mobileProfile = CustomerServiceProxy.getInstance().getMobileProfileByBankIdMobileNumberAndStatus(bankId, mobile, MobileProfileStatus.ACTIVE);
		}
		//System.out.println(">>>>>>>>>>>>>>>>>>>>>> 1 Mobile Prof : "+mobileProfile);
		if(mobileProfile == null || mobileProfile.getId() == null) {
			bankId = MobileWebConfiguration.getInstance().getStringValueOf(WebConstants.BANK_ID_2);
			mobileProfile = CustomerServiceProxy.getInstance().getMobileProfileByBankIdMobileNumberAndStatus(bankId, mobile, MobileProfileStatus.ACTIVE);
		}
		//System.out.println(">>>>>>>>>>>>>>>>>>>>>> 2 Mobile  Prof: "+mobileProfile);
		if(mobileProfile == null || mobileProfile.getId() == null) {	
			isNonHolder = true;
		} else {
			isNonHolder = false;
		}
		
		JSONObject js = new JSONObject();
		js.put("isNonHolder", new Boolean(isNonHolder));
		json = js.serialize();
		//System.out.println(">>>>>>>>>>>>>>>>>>>>>> Json Iyo  :::::::::::::::: "+json);
		
	} catch(Exception e) {
	 e.printStackTrace();
	}

%>

<%=json%>