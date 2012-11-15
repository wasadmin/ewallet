/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.econet.ussdgateway.client;

import static zw.co.econet.ussdgateway.client.ApplicationConstants.DESTINATION_NUMBER;
import static zw.co.econet.ussdgateway.client.ApplicationConstants.MESSAGE_BODY;
import static zw.co.econet.ussdgateway.client.ApplicationConstants.MESSAGE_CHANNEL;
import static zw.co.econet.ussdgateway.client.ApplicationConstants.MESSAGE_OUTGOING;
import static zw.co.econet.ussdgateway.client.ApplicationConstants.MSISDN;
import static zw.co.econet.ussdgateway.client.ApplicationConstants.SESSION_STATE;
import static zw.co.econet.ussdgateway.client.ApplicationConstants.SOURCE_NUMBER;
import static zw.co.econet.ussdgateway.client.ApplicationConstants.SYSTEM_PASSWORD;
import static zw.co.econet.ussdgateway.client.ApplicationConstants.SYSTEM_USER;
import static zw.co.econet.ussdgateway.client.ApplicationConstants.TRAFFIC_FLOW;
import static zw.co.econet.ussdgateway.client.ApplicationConstants.TRANSACTION_ID;
import static zw.co.econet.ussdgateway.client.ApplicationConstants.TRANSACTION_TIME;
import static zw.co.econet.ussdgateway.client.ApplicationConstants.USSD_CHANNEL;
import static zw.co.econet.ussdgateway.client.ApplicationConstants.USSD_REQUEST_STRING;
import static zw.co.econet.ussdgateway.client.ApplicationConstants.USSD_RESPONSE;
import static zw.co.econet.ussdgateway.client.ApplicationConstants.USSD_SERVICE_CODE;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.xmlbeans.XmlException;
import org.joda.time.DateTime;

import zw.co.econet.intergration.messagingSchema.MessageRequest;
import zw.co.econet.intergration.messagingSchema.MessageRequestDocument;
import zw.co.econet.intergration.messagingSchema.MessageResponse;
import zw.co.econet.intergration.messagingSchema.MessageResponseDocument;

/**
 *
 * @author oswin
 */
public class EconetXMLUtil {

    /**
     *
     * @param xml
     * @return
     */
    public static Map<String, String> parseESMEXML(String xml) {
        Map<String, String> responseMap = new HashMap<String, String>();
        try {
            MessageResponse messageResponse = MessageResponseDocument.Factory.parse(xml).getMessageResponse();
            responseMap.put(SOURCE_NUMBER, messageResponse.getSourceNumber());
            responseMap.put(DESTINATION_NUMBER, messageResponse.getDestinationNumber());
            responseMap.put(MESSAGE_CHANNEL, USSD_CHANNEL);
            responseMap.put(MESSAGE_BODY, messageResponse.getMessage());
            responseMap.put(TRAFFIC_FLOW, MESSAGE_OUTGOING);
            responseMap.put(TRANSACTION_TIME, DateUtil.generateISO8601Date(messageResponse.getTransactionTime().getTime()));
            responseMap.put(SESSION_STATE, messageResponse.getStage());
            responseMap.put(SYSTEM_USER, messageResponse.getApplicationID());
            responseMap.put(SYSTEM_PASSWORD, messageResponse.getApplicationPassword());
        } catch (XmlException e) {
            e.printStackTrace();
        }
        return responseMap;
    }
    
    
    public static MessageResponse getMessageResponse(String xml) {
        try {
            MessageResponse messageResponse = MessageResponseDocument.Factory.parse(xml).getMessageResponse();
            return messageResponse;
        } catch (XmlException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param ussdRequest
     * @return
     */
    public static String composeESMEXML(Map<String, String> ussdRequest) {
        MessageRequestDocument messageRequestDocument = MessageRequestDocument.Factory.newInstance();
        MessageRequest messageRequest = messageRequestDocument.addNewMessageRequest();
        messageRequest.setChannel(USSD_CHANNEL);
        messageRequest.setDestinationNumber(ussdRequest.get(USSD_SERVICE_CODE));
        messageRequest.setMessage(ussdRequest.get(USSD_REQUEST_STRING));
        messageRequest.setTransactionID(ussdRequest.get(TRANSACTION_ID));
        messageRequest.setSourceNumber(ussdRequest.get(MSISDN));
        if (ussdRequest.get(USSD_RESPONSE).trim().equalsIgnoreCase("false")) {
            messageRequest.setStage("FIRST");
        } else {
            messageRequest.setStage("PENDING");
        }
        Calendar transactionTime = Calendar.getInstance();
        transactionTime.setTime(DateUtil.parseXmlDate(ussdRequest.get(TRANSACTION_TIME)));
        messageRequest.setTransactionTime(transactionTime);
        return ApplicationConstants.XML_HEADER + "\n" + messageRequestDocument.toString();
    }

  
    public static Map<String, String> parseESMEXMLRequest(String xml) {
        Map<String, String> requestMap = new HashMap<String, String>();
        try {
            MessageRequest messageRequest = MessageRequestDocument.Factory.parse(xml).getMessageRequest();
            requestMap.put(SOURCE_NUMBER, messageRequest.getSourceNumber());
            requestMap.put(DESTINATION_NUMBER, messageRequest.getDestinationNumber());
            requestMap.put(MESSAGE_CHANNEL, USSD_CHANNEL);
            requestMap.put(MESSAGE_BODY, messageRequest.getMessage());
            requestMap.put(TRANSACTION_TIME, DateUtil.generateISO8601Date(messageRequest.getTransactionTime().getTime()));
            requestMap.put(SESSION_STATE, messageRequest.getStage());
            requestMap.put(TRANSACTION_ID, messageRequest.getTransactionID());
        } catch (XmlException e) {
            e.printStackTrace();
        }
        return requestMap;
    }
    
    
    public static String composeESMEXMLResponse(Map<String, String> responseMap) {
        MessageResponseDocument messageResponseDocument = MessageResponseDocument.Factory.newInstance();
        MessageResponse messageResponse = messageResponseDocument.addNewMessageResponse();
        messageResponse.setChannel(ApplicationConstants.USSD_CHANNEL);
        messageResponse.setSourceNumber(responseMap.get(SOURCE_NUMBER));
        messageResponse.setMessage(responseMap.get(MESSAGE_BODY));
        messageResponse.setTransactionTime(new DateTime(DateUtil.getDateFromISO8601(responseMap.get(TRANSACTION_TIME))).toCalendar(Locale.ENGLISH));
        messageResponse.setTransactionID(responseMap.get(TRANSACTION_ID));
        messageResponse.setStage(responseMap.get(SESSION_STATE));
        messageResponse.setDestinationNumber(responseMap.get(DESTINATION_NUMBER));
        return ApplicationConstants.XML_HEADER + "\n" + messageResponseDocument.toString();
    }
  

       
  

}
