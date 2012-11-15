package zw.co.esolutions.ussd.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import zw.co.esolutions.ewallet.enums.MobileNetworkOperator;
import zw.co.esolutions.ewallet.enums.USSDTransactionType;
import zw.co.esolutions.ewallet.msg.USSDRequestMessage;
import zw.co.esolutions.mobile.banking.services.MerchantInfo;
import zw.co.esolutions.mobile.banking.services.TransactionResponse;
import zw.co.esolutions.ussd.conf.USSDConfiguration;
import zw.co.esolutions.ussd.entities.USSDSession;
import zw.co.esolutions.ussd.util.LoggerFactory;
import zw.co.esolutions.ussd.util.SystemConstants;

public class RegisterMerchantService extends USSDService {

	static USSDConfiguration config;
	static Logger logger = LoggerFactory.getLogger(RegisterMerchantService.class);

	@Override
	public USSDSession processUSSDRequest(USSDSession session,
			Map<String, String> attributesMap) {
		String response = attributesMap.get(SystemConstants.USSD_REQUEST_STRING);
		Map<String,String> optionsMap =  session.getOptionsMenuMap();
		String option = optionsMap.get(response);
		
		try {
			// Initialize Config File
			config = getConfigInstance(session, attributesMap);
			
			if(session.getLevel() == SystemConstants.MENU_LEVEL){
				if(SystemConstants.EXIT.equals(option) || SystemConstants.CANCEL.equals(option)){
					session = super.terminateSession(session, null);
					return session;
				} else {
					logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Yanetsa level iyi = "+session.getLevel());
					if(session.isAgent()) {
						session = SelectService.generateAgentDefaultView(session);
					} else {
						session = SelectService.generateDefaultView(session);
					}
					return session;
				}
			}
			
			 if(session.getLevel() == 2){
				if(option != null){
					
					if(SystemConstants.EXIT.equals(option)){
						session = super.terminateSession(session, null);
						return session;
					}else if(SystemConstants.BACK.equals(option)){
						session = SelectService.generateDefaultView(session);
						return session;
					}else if(SystemConstants.OTHER.equals(option)){ 
						//session.addUserInput(SystemConstants.SOURCE_ACCOUNT, option);
						session = gotoViewAllMerchants(session);
						return session;
					}else{
						
						session.addUserInput(SystemConstants.MERCHANT, option);
						session = gotoEnterMerchantAccount(session, null);
						return session;
					}
					
				}else{
					session = gotoViewAllMerchants(session);
					return session;
				}
			}else if(session.getLevel() == 3){
				
				if(option != null){
					if(SystemConstants.EXIT.equals(option)){
						session = super.terminateSession(session, null);
						return session;
					}else if(SystemConstants.BACK.equals(option)){
						session = gotoViewAllMerchants(session);
						return session;
					}
				}
				
				if(response != null && !"".equals(response.trim())){
					session.addUserInput(SystemConstants.MERCHANT_ACCCOUNT, response);
					session = gotoConfirm(session, null);
					return session;
				}else{
					String menu = config.getStringValueOf(SystemConstants.ENTER_AMOUNT_REG_MERCHANT_INVALID_OPTION_MSG) + 
									" " + session.getInput(SystemConstants.MERCHANT)  + " account number \n";;
					session = gotoEnterMerchantAccount(session, menu);
					return session;
				}
			}else if(session.getLevel() == 4){
				if(SystemConstants.EXIT.equals(option) || SystemConstants.CANCEL.equals(option)){
					session = super.terminateSession(session, null);
					return session;
				}else if(SystemConstants.BACK.equals(option)){
					session = gotoEnterMerchantAccount(session, null);
					return session;
				} else if(SystemConstants.CONFIRM.equals(option)){
					session = processTransaction(session);
					return session;
				}else {
					session = gotoConfirm(session, config.getStringValueOf(SystemConstants.INVALID_OPTION_DEFAULT));
					return session;
				}
			}
		} catch (Exception e) {
			logger.debug(" Message : "+e.getMessage());
			return super.terminateSession(session, config.getStringValueOf(SystemConstants.SERVICE_ERROR));
		}
		return session;
	}
	
	
	public static USSDSession gotoViewAllMerchants(USSDSession session){
		try {
			// Initialize Config File
			config = getConfigInstance(session);
			session.removeUserInput(SystemConstants.MERCHANT);
			//List<Merchant> merchants = getAllActiveMerchantsByBankId(session.getBankId());
			List<MerchantInfo> merchants = USSDService.getOtherMerchants(session);
			String menu = "";
			int count = 0;
			if(merchants == null || merchants.isEmpty()) {
				
				menu = config.getStringValueOf(SystemConstants.NO_ACTIVE_MERCHANT_MSG);
				session.setLevel(2);
					
				
			} else {
				menu = config.getStringValueOf(SystemConstants.SELECT_REGISTER_MERCHANT_MSG);
				Map<String, String> map = new HashMap<String, String>();
				for(MerchantInfo merchant : merchants){
					menu += "\n " +  ++count + ". " + merchant.getMerchantName();
					map.put(""+count, merchant.getMerchantName());
				}
				session.setLevel(2);
				session.createOptionMenuFromMap(map);
				
			}
			menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
			session.addBackAndExitToOptionsMenu();
			session.setServiceName(SystemConstants.SERVICE_NAME_REGISTER_MERCHANT);
			session.setUssdResponseString(menu);
			session.generateXml();
		} catch (Exception e) {
			logger.error(" Error occured getting all merchants , exception = " + e.getMessage());
		}
		return session;
	}
	
	public static USSDSession processTransaction(USSDSession session){
		// Initialize Config File
		config = getConfigInstance(session);
		
		USSDRequestMessage msg = new USSDRequestMessage();
		msg.setMno(MobileNetworkOperator.ECONET);
		msg.setSourceMobileNumber(session.getMobileNumber());
		msg.setSourceBankAccount(session.getInput(SystemConstants.SOURCE_ACCOUNT));
		msg.setUtilityName(session.getInput(SystemConstants.MERCHANT));
		msg.setCustomerUtilityAccount(session.getInput(SystemConstants.MERCHANT_ACCCOUNT));
		msg.setTransactionType(USSDTransactionType.REGISTER_MERCHANT);
		msg.setSourceBankId(session.getBankCode());
		msg.setUuid(session.getSessionId());
		
		TransactionResponse transactionResponse = sendMessage(msg);
		return showFinalMenu(session, transactionResponse);
	}
	
	public static USSDSession gotoEnterMerchantAccount(USSDSession session,String menu){
		// Initialize Config File
		config = getConfigInstance(session);
		session.removeUserInput(SystemConstants.MERCHANT_ACCCOUNT);
		if(menu == null){
			menu = config.getStringValueOf(SystemConstants.ENTER_ACCOUNT_REGISTER_ACCOUNT_MSG);
		} 
		menu += " " + session.getInput(SystemConstants.MERCHANT)  + " account number ";
		menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
		session.addBackAndExitToOptionsMenu();
		session.setLevel(3);
		session.setServiceName(SystemConstants.SERVICE_NAME_REGISTER_MERCHANT);
		session.setUssdResponseString(menu);
		session.generateXml();
        return session;					
	}
	
	public static USSDSession gotoConfirm(USSDSession session,String menu){
		// Initialize Config File
		config = getConfigInstance(session);
		
		if(menu == null){
			menu = config.getStringValueOf(SystemConstants.CONFIRM_MERCHANT_REG);
		} 
		
		menu += "\nDETAILS " + 
				"\nMerchant : " + session.getInput(SystemConstants.MERCHANT) + " " +
				"\nAccount :  " +session.getInput(SystemConstants.MERCHANT_ACCCOUNT);
		menu += config.getStringValueOf(SystemConstants.CONFIRM_OPTIONS);
		menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
		session.addConfirmAndCancelToOptionsMenu();
		session.addBackAndExitToOptionsMenu();
		session.setLevel(4);
		session.setServiceName(SystemConstants.SERVICE_NAME_REGISTER_MERCHANT);
		session.setUssdResponseString(menu);
		session.generateXml();
        return session;					
	}


}
