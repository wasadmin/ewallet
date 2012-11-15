/**
 * 
 */
package zw.co.esolutions.ussd.beans;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import pagecode.PageCodeBase;
import zw.co.econet.intergration.messagingSchema.MessageResponse;
import zw.co.econet.ussdgateway.client.EconetXMLUtil;
import zw.co.econet.ussdgateway.client.TestClient;
import zw.co.esolutions.ussd.utils.GenerateKey;
import zw.co.esolutions.ussd.utils.NumberUtil;

/**
 * @author taurai
 *
 */
public class UssdBean extends PageCodeBase{

	private String message;
	private String transactionId;
	private String sourceMobile;
	private String transactionIdLabel;
	private String stageItem;
	private String menu;
	private boolean newSession;
	/**
	 * 
	 */
	public UssdBean() {
		// TODO Auto-generated constructor stub
	}
	
	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public String getTransactionId() {
		return transactionId;
	}


	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}


	public String getSourceMobile() {
		if(this.sourceMobile != null) {
			try {
				this.sourceMobile = NumberUtil.formatMobileNumber(this.sourceMobile);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return sourceMobile;
	}


	public void setSourceMobile(String sourceMobile) {
		this.sourceMobile = sourceMobile;
	}

	public String getStageItem() {
		return stageItem;
	}


	public void setStageItem(String stageItem) {
		this.stageItem = stageItem;
	}


	public void setMenu(String menu) {
		this.menu = menu;
	}


	public String getMenu() {
		return menu;
	}


	public void setNewSession(boolean newSession) {
		this.newSession = newSession;
	}

	public boolean isNewSession() {
		return newSession;
	}

	public void setTransactionIdLabel(String transactionIdLabel) {
		this.transactionIdLabel = transactionIdLabel;
	}

	public String getTransactionIdLabel() {
		return transactionIdLabel;
	}

	public String submitRequest() {
		try {
			if(this.getMessage() == null || this.getMessage().equals("")) {
				super.setErrorMessage("USSD Message Required.");
				return "failure";
			}
			if(this.getSourceMobile() == null || this.getSourceMobile().equals("")) {
				super.setErrorMessage("Source Mobile Required.");
				return "failure";
			}
			
			if(this.isNewSession()) {
				this.setTransactionId(GenerateKey.generateEntityId());
				this.setTransactionIdLabel(this.getTransactionId());
			}
			System.out.println(">>>>>>>>>>>>>>>>>>>>> Is New Session "+isNewSession());
			this.processUSSDRequest();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}
	
	private void processUSSDRequest() {
		 try {
	            HttpClient client = new DefaultHttpClient();
	            HttpPost post = new HttpPost("http://localhost:9080/mCommerce/ussd");

	            HttpEntity requestBody = new StringEntity("<?xml version=\"1.0\"?>"
	                    + "<messageRequest xmlns=\"http://econet.co.zw/intergration/messagingSchema\">"
	                    + "<transactionTime>2009-05-26T17:43:34.000+02:00</transactionTime>"
	                    + "<transactionID>"+getTransactionId()+"</transactionID>"
	                    + "<sourceNumber>"+getSourceMobile()+"</sourceNumber>"
	                    + " <destinationNumber>440</destinationNumber>"
	                    + " <message>"+getMessage()+"</message>"
	                    + " <stage>FIRST</stage>"
	                    + " <channel>USSD</channel>"
	                    + "</messageRequest>", "UTF-8");

	     
	            post.setEntity(requestBody);
	            HttpResponse response = client.execute(post);
	            String xmlResponse = EntityUtils.toString(response.getEntity());
	           // System.out.println(xmlResponse);
	            
	            MessageResponse msgResp= EconetXMLUtil.getMessageResponse(xmlResponse);
	            this.setTransactionId(msgResp.getTransactionID());
	            String msg = msgResp.getMessage();
	            if(msg.length() > 210) {
	            	msg = msg.substring(0, 210);
	            }
	            this.setMenu(msg);
	            this.setStageItem(msgResp.getStage());
	            this.setMessage(null);
	            if("COMPLETE".equals(msgResp.getStage())) {
	            	this.setNewSession(true);
	            	this.setTransactionId(null);
	            	this.setTransactionIdLabel(null);
	            } else {
	            	this.setNewSession(false);
	            	this.setTransactionId(msgResp.getTransactionID());
	            	this.setTransactionIdLabel(msgResp.getTransactionID());
	            	
	            }
	        } catch (ClientProtocolException ex) {
	            Logger.getLogger(TestClient.class.getName()).log(Level.SEVERE, null, ex);
	        } catch (UnsupportedEncodingException ex) {
	            Logger.getLogger(TestClient.class.getName()).log(Level.SEVERE, null, ex);
	        } catch (IOException ex) {
	            Logger.getLogger(TestClient.class.getName()).log(Level.SEVERE, null, ex);
	        }

	}

}
