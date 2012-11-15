/**
 * 
 */
package zw.co.esolutions.topup.mno;

import zw.co.econet.econetvas.BalanceRequest;
import zw.co.econet.econetvas.BalanceResponse;
import zw.co.econet.econetvas.BillPaymentRequest;
import zw.co.econet.econetvas.BillPaymentResponse;
import zw.co.econet.econetvas.CreditRequest;
import zw.co.econet.econetvas.CreditResponse;
import zw.co.econet.econetvas.EconetvasSOAPProxy;
import zw.co.esolutions.topup.ws.util.MNOName;
import zw.co.esolutions.topup.ws.util.ServiceCommand;
import zw.co.esolutions.topup.ws.util.SystemConstants;
import zw.co.esolutions.topup.ws.util.WSRequest;
import zw.co.esolutions.topup.ws.util.WSResponse;

/**
 * @author blessing
 *
 */
public class EconetMNOPlatform implements MNOInterface {
	private static EconetMNOPlatform mnoPlatform;
	private EconetvasSOAPProxy proxy;
	/**
	 * 
	 */
	private EconetMNOPlatform() {
		proxy = new EconetvasSOAPProxy();
	}
	
	public static EconetMNOPlatform getMnoPlatform() {
		if (mnoPlatform == null) {
			mnoPlatform = new EconetMNOPlatform();		
		}
		return mnoPlatform;
	}

	/* (non-Javadoc)
	 * @see zw.co.esolutions.topup.mno.MNOInterface#getMNOName()
	 */
	@Override
	public MNOName getMNOName() {
		return MNOName.ECONET;
	}

	/* (non-Javadoc)
	 * @see zw.co.esolutions.topup.mno.MNOInterface#processRequest(zw.co.esolutions.topup.ws.dto.Request)
	 */
	@Override
	public WSResponse processRequest(WSRequest request) {
		ServiceCommand requestCommand = request.getServiceCommand(); 
		if(ServiceCommand.TOPUP.equals(requestCommand)){
			return this.doTopup(request);
		}else if(ServiceCommand.BILLPAY.equals(requestCommand)){
			return this.doBillPayment(request);
		}else if(ServiceCommand.BALANCE.equals(requestCommand)){
			return this.doBalanceEnquiry(request);
		}else{
			//this is a dead letter
			WSResponse response = new WSResponse();
			response.setNarrative("Unknown Service ServiceCommand [" + requestCommand.name() +"]");
			response.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			return response;
		}
	}

	private WSResponse doTopup(WSRequest request){
		WSResponse response = new WSResponse();
		response.setRequest(request);
		if(this.validateMobile(request.getTargetMobileNumber())){
			//create the VAS REQUEST
			CreditRequest vasRequest = new CreditRequest();
			vasRequest.setAmount(request.getAmount() / 100.0);
			vasRequest.setNumberOfDays(0);
			vasRequest.setReference(request.getUuid());
			vasRequest.setServiceId(request.getServiceId());
			vasRequest.setServiceProviderId(request.getServiceProviderId());
			vasRequest.setSourceMobileNumber(request.getSourceMobileNumber());
			vasRequest.setTargetMobileNumber(request.getTargetMobileNumber());
			//post to the VAS WS		
			CreditResponse vasResponse = proxy.creditSubscriber(vasRequest);
			//Create a response object.
			
			
			if (vasResponse == null) {
				response.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
				response.setNarrative("Credit Transaction Failed.");
				return response;
			} else if(SystemConstants.RC_OK.equalsIgnoreCase(vasResponse.getResponseCode())){
				response.setAirtimeBalance(vasResponse.getFinalBalance());
				response.setInitialBalance(vasResponse.getInitialBalance());
				response.setNarrative(vasResponse.getNarrative());
				response.setResponseCode(SystemConstants.RC_OK);
				return response;
			}else{
				response.setAirtimeBalance(vasResponse.getFinalBalance());
				response.setInitialBalance(vasResponse.getInitialBalance());
				response.setNarrative(vasResponse.getNarrative());
				response.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
				return response;
			}
		}else{
			response.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			response.setNarrative("Invalid mobile number [" + request.getTargetMobileNumber() +"] for Network Provider ["+ request.getMnoName()+"]");
			return response;
		}
		
		
	}
	
	private boolean validateMobile(String mobileNumber) {
		//check for null and empties.
		if(null == mobileNumber || "".equalsIgnoreCase(mobileNumber.trim())){
			return false;
		}

		//check the lengths
		int length = mobileNumber.length();		
		if(!(14==length || 12 == length || 10 == length || 9 == length)){
			return false;
		}

		//valid length, now lets check prefixes
		boolean potentialValid = false;
		if(length == 14){
			potentialValid = mobileNumber.startsWith("0026377");
		}else if(length == 12){
			potentialValid = mobileNumber.startsWith("26377");
		}else if(length == 10){
			potentialValid = mobileNumber.startsWith("077");
		}else if(length == 9){
			potentialValid = mobileNumber.startsWith("77");
		}else{
			return false;
		}

		if (potentialValid) {
			//if we get here then we need to check if the this is a number for sure
			try {
				Long.parseLong(mobileNumber);
				return true;
			} catch (NumberFormatException e) {
				//this this is not a number at all
				return false;
			}
		}
		return false;
	}

	private WSResponse doBillPayment(WSRequest request) {
//		TopupWebServiceConfig conf = TopupWebServiceConfig.getInstance();		
		BillPaymentRequest billPay = new BillPaymentRequest();
		billPay.setAmount(request.getAmount());
		billPay.setReference(request.getUuid());
		billPay.setServiceId(request.getServiceId());
		billPay.setServiceProviderId(request.getServiceProviderId());
		billPay.setSourceMobileNumber(request.getSourceMobileNumber());
		billPay.setTargetMobileNumber(request.getTargetMobileNumber());
		
		BillPaymentResponse vasResponse = proxy.billPay(billPay);
		//Create a response object.
		WSResponse response = new WSResponse();
		response.setRequest(request);
		
		if (vasResponse == null) {
			response.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			response.setNarrative("Bill Payment Failed.");
			return response;
		} else if(SystemConstants.RC_OK.equalsIgnoreCase(vasResponse.getResponseCode())){
			response.setNarrative(vasResponse.getNarrative());
			response.setResponseCode(SystemConstants.RC_OK);
			return response;
		}else{
			response.setNarrative(vasResponse.getNarrative());
			response.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			return response;
		}
	}

	private WSResponse doBalanceEnquiry(WSRequest request) {
		//Create a response object.
		WSResponse response = new WSResponse();
		response.setRequest(request);
		if(this.validateMobile(request.getTargetMobileNumber())){			
			
			BalanceRequest balanceRequest = new BalanceRequest();
			balanceRequest.setMobileNumber(request.getTargetMobileNumber());
			balanceRequest.setReference(request.getUuid());
			balanceRequest.setServiceId(request.getServiceId());
			balanceRequest.setServiceProviderId(request.getServiceProviderId());
//			
			BalanceResponse vasResponse = proxy.balanceEnquiry(balanceRequest);		
			if (vasResponse == null) {
				response.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
				response.setNarrative("Balance Enquiry Failed.");
				return response;
			} else if(SystemConstants.RC_OK.equalsIgnoreCase(vasResponse.getResponseCode())){
				response.setNarrative(vasResponse.getNarrative());
				response.setInitialBalance(vasResponse.getCurrentBalance());
				response.setAirtimeBalance(vasResponse.getCurrentBalance());
				response.setResponseCode(SystemConstants.RC_OK);
				return response;
			}else{
				response.setNarrative(vasResponse.getNarrative());
				response.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
				return response;
			}
		}else{
			response.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			response.setNarrative("Invalid mobile number [" + request.getTargetMobileNumber() +"] for Network Provider ["+ request.getMnoName()+"]");
			return response;
		}
	}

	@Override
	public WSResponse processReversal(WSRequest request) {
		// TODO Auto-generated method stub
		return null;
	}
}
