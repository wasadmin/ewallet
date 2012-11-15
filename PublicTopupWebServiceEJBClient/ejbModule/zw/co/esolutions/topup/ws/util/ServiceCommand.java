/**
 * 
 */
package zw.co.esolutions.topup.ws.util;

import zw.co.esolutions.topup.mno.MNOInterface;
import zw.co.esolutions.topup.mno.MNOInterfaceFactory;

/**
 * @author blessing
 *
 */
public enum ServiceCommand{
	BALANCE(){
		@Override
		public WSResponse execute(WSRequest request) throws Exception {
			MNOInterface mnoInterface = MNOInterfaceFactory.getMNOInstance(request.getMnoName());
			System.out.println("Got MNO Interface " + mnoInterface.getMNOName() + " for MNO  " + request.getMnoName());
			return mnoInterface.processRequest(request);		
		}
	},
	BILLPAY(){
		@Override
		public WSResponse execute(WSRequest request) throws Exception {
			//get the configuration info
			TopupWebServiceConfig conf = TopupWebServiceConfig.getInstance();
			
			//initialise the response that we will return
			WSResponse response = new WSResponse();
			response.setRequest(request);
			
			//check limits and respond accourdingly
//			double min = conf.getDoubleValueOf("billpay.minimum", 0);			
//			if (request.getAmount() < min ) {
//				response.setNarrative("Bill Payment amount is below minimum limit of " + Formats.moneyFormat.format(min));
//				response.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
//				return response;
//			}
//			double max = conf.getDoubleValueOf("billpay.maximum", 50);
//			if(request.getAmount() > max){
//				response.setNarrative("Bill Payment is above maximum limit of " + Formats.moneyFormat.format(min));
//				response.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
//				return response;
//			}
			MNOInterface mnoInterface = MNOInterfaceFactory.getMNOInstance(request.getMnoName());
			return mnoInterface.processRequest(request);
		 
		}
	},
	TOPUP(){
		@Override
		public WSResponse execute(WSRequest request) throws Exception {
			//get the configuration info
//			TopupWebServiceConfig conf = TopupWebServiceConfig.getInstance();
			
			//initialise the response that we will return
			WSResponse response = new WSResponse();
			response.setRequest(request);
			
//			//check limits and respond accourdingly
//			double min = conf.getDoubleValueOf(request.getBankId() + ".topup.minimum", 0);			
//			if (request.getAmount() < min ) {
//				response.setNarrative("Topup amount is below minimum limit of " + Formats.moneyFormat.format(min));
//				response.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
//				return response;
//			}
//			double max = conf.getDoubleValueOf(request.getBankId() + ".topup.maximum", 50);
//			if(request.getAmount() > max){
//				response.setNarrative("Topup amount is above maximum limit of " + Formats.moneyFormat.format(min));
//				response.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
//				return response;
//			}
			//well within LIMITS, lets get the next we get the MNO Instance and delegate, I LOVE JAVA
			
			MNOInterface mnoInterface = MNOInterfaceFactory.getMNOInstance(request.getMnoName());
			return mnoInterface.processRequest(request);
			
		}
	},
	
	BILLPAY_REVERSAL(){
		@Override
		public WSResponse execute(WSRequest request) throws Exception {
			//since this is a reversal, we need no more validation that we have done already
			//get the MNOInterface and we go
			MNOInterface mnoInterface = MNOInterfaceFactory.getMNOInstance(request.getMnoName());
			return mnoInterface.processReversal(request);

		}
	},
	TOPUP_REVERSAL(){
		@Override
		public WSResponse execute(WSRequest request) throws Exception {
			//since this is a reversal, we need no more validation that we have done already
			//get the MNOInterface and we go
			MNOInterface mnoInterface = MNOInterfaceFactory.getMNOInstance(request.getMnoName());
			return mnoInterface.processReversal(request);		
		}
	},
	
	TEXT_TOPUP(){
		
	}
	;
	
	
	
	public WSResponse execute(WSRequest request) throws Exception{
		WSResponse response = new WSResponse();
		response.setNarrative("Unsupported request type [" + request.getServiceCommand().name() +"]");
		response.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
		return response;		
	}
	
}
