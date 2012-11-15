package zw.co.esolutions.ussd;

import static zw.co.econet.ussdgateway.client.ApplicationConstants.DESTINATION_NUMBER;
import static zw.co.econet.ussdgateway.client.ApplicationConstants.MESSAGE_BODY;
import static zw.co.econet.ussdgateway.client.ApplicationConstants.MESSAGE_CHANNEL;
import static zw.co.econet.ussdgateway.client.ApplicationConstants.SESSION_STATE;
import static zw.co.econet.ussdgateway.client.ApplicationConstants.SOURCE_NUMBER;
import static zw.co.econet.ussdgateway.client.ApplicationConstants.TRANSACTION_ID;
import static zw.co.econet.ussdgateway.client.ApplicationConstants.USSD_CHANNEL;
import static zw.co.econet.ussdgateway.client.ApplicationConstants.USSD_RESPONSE;
import static zw.co.econet.ussdgateway.client.ApplicationConstants.TRANSACTION_TIME;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;

import org.apache.log4j.Logger;

import zw.co.econet.ussdgateway.client.DateUtil;
import zw.co.econet.ussdgateway.client.EconetXMLUtil;
import zw.co.esolutions.ussd.ejb.USSDSessionProcessor;
import zw.co.esolutions.ussd.util.LoggerFactory;
import zw.co.esolutions.ussd.util.SystemConstants;

public class USSDManagedBean {
	
	private String transactionId;
	private String sourceNumber;
	private String USSDServiceCode;
	private String message;
	private String stage;
	private String menu;
	private Date transactionDate;
	
	Logger logger = LoggerFactory.getLogger(USSDManagedBean.class);
	
	@EJB
	private USSDSessionProcessor ussdProcessor;
	
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getSourceNumber() {
		return sourceNumber;
	}
	public void setSourceNumber(String sourceNumber) {
		this.sourceNumber = sourceNumber;
	}
	public String getUSSDServiceCode() {
		return USSDServiceCode;
	}
	public void setUSSDServiceCode(String serviceCode) {
		USSDServiceCode = serviceCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStage() {
		return stage;
	}
	public void setStage(String stage) {
		this.stage = stage;
	}
	public String getMenu() {
		return menu;
	}
	public void setMenu(String menu) {
		this.menu = menu;
	}
	
	public String submitRequest(){
		System.out.println(">>>>>>>>>>>> In Request ");
		 try {
			Map<String, String> requestMap = new HashMap<String, String>();
			 Map<String, String> responseMap;
			 String txnDate = DateUtil.generateISO8601Date(new Date());
			    try {
			        requestMap.put(SOURCE_NUMBER, this.getSourceNumber());
			        requestMap.put(DESTINATION_NUMBER, "150");
			        requestMap.put(MESSAGE_CHANNEL, USSD_CHANNEL);
			        requestMap.put(MESSAGE_BODY, this.getMessage());
			        if(!(SystemConstants.ACTION_REQUEST.equalsIgnoreCase(this.getStage()) || 
			        		SystemConstants.ACTION_END.equalsIgnoreCase(this.getStage()) ) ) {
			        	requestMap.put(TRANSACTION_TIME, txnDate);
			        	requestMap.put(USSD_RESPONSE, "false");
			        } else {
			        	requestMap.put(TRANSACTION_TIME, DateUtil.generateISO8601Date(this.getTransactionDate()));
			        }
			        requestMap.put(SESSION_STATE, this.getStage());
			        requestMap.put(TRANSACTION_ID, this.getTransactionId());
			        
			    } catch (Exception e) {
			        e.printStackTrace();
			    }
			String xml = EconetXMLUtil.composeESMEXML(requestMap);
			String xmlResponse = ussdProcessor.processUSSDRequest(xml);
			responseMap = EconetXMLUtil.parseESMEXML(xmlResponse);
			this.setStage(responseMap.get(SESSION_STATE));
			this.setMenu(responseMap.get(MESSAGE_BODY));
			this.setTransactionDate(DateUtil.getDateFromISO8601(responseMap.get(TRANSACTION_TIME)));
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		return "";
	}
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}
	public Date getTransactionDate() {
		return transactionDate;
	}
	
	
	
	

}
