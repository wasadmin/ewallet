/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.co.econet.ussdgateway.client;

/**
 *
 * @author oswin
 */
public class ApplicationConstants {
    // message elements from econet ussd gateway

    public static final String TRANSACTION_ID = "TransactionId";
    public static final String TRANSACTION_TIME = "TransactionTime";
    public static final String MSISDN = "MSISDN";
    public static final String USSD_SERVICE_CODE = "USSDServiceCode";
    public static final String USSD_REQUEST_STRING = "USSDRequestString";
    public static final String USSD_RESPONSE = "response";
    public static final String XML_HEADER = "<?xml version=\"1.0\"?>";
    public static final String USSD_RESPONSE_STRING = "USSDResponseString";
    public static final String ACTION = "action";
    public static final String ACTION_REQUEST = "request";
    public static final String ACTION_END = "end";
    // Message elemental parts
    public static final String SOURCE_NUMBER = "source_number";
    public static final String DESTINATION_NUMBER = "destination_number";
    public static final String MESSAGE_BODY = "message_body";
    public static final String SESSION_TRANSACTION_TIME = "transaction_time";
    public static final String MESSAGE_CHANNEL = "message_channel";
    public static final String TRANSACTION_UUID = "transaction_uuid";
    public static final String SYSTEM_ID = "system_id";
    public static final String TRAFFIC_FLOW = "traffic_flow";
    public static final String USSD_SESSION_ID = "ussd_session_id";
    public static final String SUBMIT_ID = "submit_id";
    public static final String SESSION_STATE = "session_state";
    public static final String USSD_XML_MESSAGE = "ussd_xml_message";
    // USSD session states
    public static final String SESSION_CLOSED = "session_closed";
    public static final String SESSION_ACTIVE = "session_active";
    // message flow elements
    public static final String MESSAGE_INCOMING = "INCOMING";
    public static final String MESSAGE_OUTGOING = "OUTGOING";
    // message channels
    public static final String SMS_CHANNEL = "SMS";
    public static final String USSD_CHANNEL = "USSD";
    public static final String JMS_CHANNEL = "JMS";
    public static final String ONEAPI = "ONEAPI";
    // authentication elements
    public static final String SYSTEM_USER = "username";
    public static final String SYSTEM_PASSWORD = "password";
    // Service responses
    public static final String SERVICE_NOT_AVAILABLE = "The service requested is currently not available";
}
