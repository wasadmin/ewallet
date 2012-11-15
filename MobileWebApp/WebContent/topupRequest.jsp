
<%@page import="zw.co.esolutions.ussd.web.services.MobileWebTransactionType"%>
<%@page import="zw.co.esolutions.ewallet.customerservices.service.MobileProfileStatus"%>
<%@page import="zw.co.esolutions.mobile.transactions.utils.LoginUtils"%>
<%@page import="zw.co.esolutions.ewallet.util.GenerateKey"%>
<%@page import="zw.co.esolutions.ewallet.services.proxy.CustomerServiceProxy"%>
<%@page import="zw.co.esolutions.mobile.web.utils.WebConstants"%>
<%@page import="zw.co.esolutions.mobile.transactions.utils.TransactionUtil"%>
<%@page import="zw.co.esolutions.ussd.web.services.MobileWebRequestMessage"%>
<%@page import="zw.co.esolutions.ewallet.util.NumberUtil"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.ibm.json.java.JSONArray"%>
<%@page import="java.util.List"%><%@page import="com.ibm.json.java.JSONObject"%><%
/*
' Login.jsp
' --------
*/

response.setContentType("text/json");
%>

<% String mobile = request.getParameter("mobileNumber");
	String bankId = request.getParameter("bankId");
	String srcAccount = request.getParameter("srcAccount");
	String amount = request.getParameter("amount");
	
	long longAmnt = 0l;
	String message = "";
	
	String json = "";
	JSONObject jb = new JSONObject();
	jb.put("mobile", mobile);
	jb.put("bankId", bankId);
	
	try {
	
		mobile = NumberUtil.formatMobileNumber(mobile);
		longAmnt = TransactionUtil.isValidAmount(amount);
		
		if( longAmnt > 0L) { 		
			MobileWebRequestMessage req = TransactionUtil.populateBasicMobileWebRequestMessage(GenerateKey.generateEntityId(), bankId, mobile, longAmnt, srcAccount, MobileWebTransactionType.TOPUP);
			req.setTargetMobileNumber(mobile);
			
			message = TransactionUtil.processTransaction(req);
		} else {
			message = "Top Up failed,  invalid amount entered : "+amount;
		}
		
		//Dependant On Server
		jb.put("responseCode", WebConstants.SUCCESS_CODE);
		jb.put("message", message);
		
		jb = LoginUtils.populateInitialLoginJson(jb, CustomerServiceProxy.getInstance().getMobileProfileByBankIdMobileNumberAndStatus(bankId, mobile, MobileProfileStatus.ACTIVE));
		
		json =  jb.serialize();
		
	} catch(Exception e) {
		e.printStackTrace();
	}
	
%>

<%= json %>
