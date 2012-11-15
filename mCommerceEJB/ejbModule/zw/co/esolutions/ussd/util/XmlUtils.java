package zw.co.esolutions.ussd.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import zw.co.econet.ussdgateway.client.ApplicationConstants;
import zw.co.econet.ussdgateway.client.DateUtil;
import zw.co.econet.ussdgateway.client.EconetXMLUtil;

import noNamespace.MethodCallDocument;
import noNamespace.MethodResponseDocument;
import noNamespace.MemberDocument.Member;
import noNamespace.MethodCallDocument.MethodCall;
import noNamespace.MethodResponseDocument.MethodResponse;
import noNamespace.StructDocument.Struct;
import noNamespace.ValueDocument.Value;

public class XmlUtils {
	
	public static Map<String, String> parseXml(String xml){
		Map<String, String> attributesMap = new HashMap<String, String>();
		try{
			/*MethodCallDocument methodCallDocument = MethodCallDocument.Factory.parse(xml);
			MethodCall mc = methodCallDocument.getMethodCall();
			Member[] members = mc.getParams().getParam().getValue().getStruct().getMemberArray();
			for (Member member : members) {
				if(SystemConstants.TRANSACTION_TIME.equals(member.getName())){
					attributesMap.put(member.getName(), member.getValue().getDateTimeIso8601());
				}else{
					attributesMap.put(member.getName(), member.getValue().getString());
				}
			}*/
			attributesMap = EconetXMLUtil.parseESMEXMLRequest(xml);
			attributesMap = convertRequestToOldMap(attributesMap);
		}catch(Exception e){
			e.printStackTrace();
		}
		return attributesMap;
	}
	
	
	public static String composeXml(Map<String, String> attributesMap){
		/*
		MethodResponseDocument mrd = MethodResponseDocument.Factory.newInstance();
		MethodResponse methodResponse = mrd.addNewMethodResponse();
		Struct struc = methodResponse.addNewParams().addNewParam().addNewValue().addNewStruct();
		
		Member transactionID = struc.addNewMember();
		transactionID.setName(SystemConstants.TRANSACTION_ID);
		Value value = transactionID.addNewValue();
		value.setString(attributesMap.get(SystemConstants.TRANSACTION_ID));
		
		Member transactionTime = struc.addNewMember();
		transactionTime.setName(SystemConstants.TRANSACTION_TIME);
		value = transactionTime.addNewValue();
		value.setDateTimeIso8601(DateUtils.formatXmlDate(new Date()));
		
		Member ussdResponseString = struc.addNewMember();
		ussdResponseString.setName(SystemConstants.USSD_RESPONSE_STRING);
		value = ussdResponseString.addNewValue();
		value.setString(attributesMap.get(SystemConstants.USSD_RESPONSE_STRING));
		
		Member action = struc.addNewMember();
		action.setName(SystemConstants.ACTION);
		value = action.addNewValue();
		value.setString(attributesMap.get(SystemConstants.ACTION));
		*/
		return EconetXMLUtil.composeESMEXMLResponse(populateEcoCompliantXMLResponse(attributesMap));
		//return SystemConstants.XML_HEADER + mrd.xmlText();
		
	}
	
	public static Map<String, String> populateEcoCompliantXMLResponse(Map<String, String> attributesMap) {
		 Map<String, String> ussdResponseMap = new java.util.HashMap<String, String>();
         ussdResponseMap.put(ApplicationConstants.SOURCE_NUMBER, attributesMap.get(ApplicationConstants.SOURCE_NUMBER));
         ussdResponseMap.put(ApplicationConstants.MESSAGE_BODY, attributesMap.get(SystemConstants.USSD_RESPONSE_STRING));
         ussdResponseMap.put(ApplicationConstants.TRANSACTION_TIME, DateUtil.generateISO8601Date(new Date()));
         ussdResponseMap.put(ApplicationConstants.TRANSACTION_ID, attributesMap.get(SystemConstants.TRANSACTION_ID));
         ussdResponseMap.put(ApplicationConstants.SESSION_STATE, attributesMap.get(SystemConstants.ACTION));
         ussdResponseMap.put(ApplicationConstants.DESTINATION_NUMBER, attributesMap.get(ApplicationConstants.DESTINATION_NUMBER));
         return ussdResponseMap;
	}
	
	public static Map<String, String> convertRequestToOldMap(Map<String, String> attributesMap) {
		 Map<String, String> ussdRequestMap = new java.util.HashMap<String, String>();
		 ussdRequestMap.put(SystemConstants.TRANSACTION_ID, attributesMap.get(ApplicationConstants.TRANSACTION_ID));
		 ussdRequestMap.put(SystemConstants.MSISDN, attributesMap.get(ApplicationConstants.SOURCE_NUMBER));
		 ussdRequestMap.put(SystemConstants.SERVICE_NAME_AUTHENTICATION, " ");
		 ussdRequestMap.put(SystemConstants.USSD_REQUEST_STRING, attributesMap.get(ApplicationConstants.MESSAGE_BODY));
		 ussdRequestMap.put(SystemConstants.TRANSACTION_TIME, attributesMap.get(ApplicationConstants.TRANSACTION_TIME));
		 return ussdRequestMap;
	}

}
