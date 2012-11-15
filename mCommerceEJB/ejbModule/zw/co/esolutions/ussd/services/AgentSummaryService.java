package zw.co.esolutions.ussd.services;

import java.text.ParseException;
import java.util.Map;

import org.apache.log4j.Logger;

import zw.co.esolutions.ewallet.enums.MobileNetworkOperator;
import zw.co.esolutions.ewallet.enums.USSDTransactionType;
import zw.co.esolutions.ewallet.msg.USSDRequestMessage;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.Formats;
import zw.co.esolutions.mobile.banking.services.TransactionResponse;
import zw.co.esolutions.ussd.conf.USSDConfiguration;
import zw.co.esolutions.ussd.entities.USSDSession;
import zw.co.esolutions.ussd.util.AgentAuth;
import zw.co.esolutions.ussd.util.LoggerFactory;
import zw.co.esolutions.ussd.util.SystemConstants;

public class AgentSummaryService extends USSDService {
	
	static Logger logger = LoggerFactory.getLogger(BalanceEnquiryService.class);
	static USSDConfiguration config;

	@Override
	public USSDSession processUSSDRequest(USSDSession session,
			Map<String, String> attributesMap) {
		
		String response = attributesMap.get(SystemConstants.USSD_REQUEST_STRING);
		Map<String,String> optionsMap =  session.getOptionsMenuMap();
		String option = optionsMap.get(response);
		//if user was selecting account
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
			
			if(session.getLevel() == 1){
				
				if(SystemConstants.EXIT.equals(option)){
					session = super.terminateSession(session, null);
					return session;
				}else if(SystemConstants.BACK.equals(option)){
					session = SelectService.generateAgentDefaultView(session);
					return session;
				} else if(option != null){
					session.addUserInput(SystemConstants.SOURCE_ACCOUNT, option);
					session = gotoLevelTwo(session, null);
					return session;
				}else{
					session = gotoLevelOne(session, config.getStringValueOf(SystemConstants.SELECT_ACCOUNT_AGENT_SUMMARY_INVALID_OPTION_MSG));
					return session;
				}
				
			
			}else if(session.getLevel() == 2){
				//target mobile
				if(SystemConstants.EXIT.equals(option)){
					session = super.terminateSession(session, null);
					return session;
				}else if(SystemConstants.BACK.equals(option)){
					//session = gotoLevelOne(session, null);
					session = SelectService.generateAgentDefaultView(session);
					return session;
				} else if(response != null && !response.trim().equals("")){
					String authResp = AgentAuth.validateAgentSummary(response.trim());
					
					if(ResponseCode.E000.name().equalsIgnoreCase(authResp)) {
						//gotoConfirm
						String agentNumber = AgentAuth.getAgentByMobileNumberAndBankId(session.getMobileNumber(), session.getBankCode());
						if (agentNumber != null) {
							session.addUserInput(SystemConstants.AGENT_NUMBER, agentNumber);
						}
						session.addUserInput(SystemConstants.SUMMARY_DATE, response.trim());
						session = gotoLevelThree(session, null);
					}else{
						session = gotoLevelTwo(session, authResp);
					}
					return session;
				}else{
					session = gotoLevelTwo(session, config.getStringValueOf(SystemConstants.ENTER_SUMMARY_DATE_INVALID_OPTION_MSG));
					return session;
				}
			}else if(session.getLevel() == 3){
				if(SystemConstants.EXIT.equals(option) || SystemConstants.CANCEL.equals(option)){
					session = super.terminateSession(session, null);
					return session;
				}else if(SystemConstants.BACK.equals(option)){
					session = gotoLevelTwo(session, null);
					return session;
				} else if(SystemConstants.CONFIRM.equals(option)){
					session = processTransaction(session);
					return session;
				}else {
					session = gotoLevelThree(session, config.getStringValueOf(SystemConstants.INVALID_OPTION_DEFAULT));
					return session;
				}
			}
		} catch (Exception e) {
			logger.debug(" Message : "+e.getMessage());
			return super.terminateSession(session, config.getStringValueOf(SystemConstants.SERVICE_ERROR));
		}
		return null;
	}
	public static USSDSession gotoLevelOne(USSDSession session,String menu){
		// Initialize Config File
		config = getConfigInstance(session);
		
		if(menu == null){
			menu = config.getStringValueOf(SystemConstants.SELECT_ACCOUNT_AGENT_SUMMARY_MSG);
		}
		session.generateAccountsOptionsMenu();
	    menu += session.getAccountsMenu();
	    menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
	    session.setLevel(1);
	    session.addBackAndExitToOptionsMenu();
		session.setServiceName(SystemConstants.SERVICE_NAME_AGENT_SUMMARY);
		session.setUssdResponseString(menu);
		session.generateXml();
		return session;
	}
	
	public static USSDSession gotoLevelTwo(USSDSession session,String menu){
		// Initialize Config File
		config = getConfigInstance(session);
		
		if(menu == null){
			menu = config.getStringValueOf(SystemConstants.AGENT_SUMMARY_ENTER_SUMMARY_DATE_MSG);
		} 
		menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
		session.addBackAndExitToOptionsMenu();
		session.setLevel(2);
		session.setServiceName(SystemConstants.SERVICE_NAME_AGENT_SUMMARY);
		session.setUssdResponseString(menu);
		session.generateXml();
        return session;				
	}
	
	public static USSDSession gotoLevelThree(USSDSession session,String menu){
		// Initialize Config File
		config = getConfigInstance(session);
		
		if(menu == null){
			menu = config.getStringValueOf(SystemConstants.CONFIRM_AGENT_SUMMARY_DATE);
		} 
		
		try {
			menu += "\nAgent Summary Date is " + DateUtil.convertToShortUploadDateFormatNumbersOnly( 
					Formats.short2DigitYearPlainDateFormat.parse(session.getInput(SystemConstants.SUMMARY_DATE)));
		} catch (ParseException e) {
			
		}
		menu += config.getStringValueOf(SystemConstants.CONFIRM_OPTIONS);
		menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
		session.addConfirmAndCancelToOptionsMenu();
		session.addBackAndExitToOptionsMenu();
		session.setLevel(3);
		session.setServiceName(SystemConstants.SERVICE_NAME_AGENT_SUMMARY);
		session.setUssdResponseString(menu);
		session.generateXml();
        return session;				
	}
	
	public static USSDSession processTransaction(USSDSession session){
		// Initialize Config File
		config = getConfigInstance(session);
		
		USSDRequestMessage msg = new USSDRequestMessage();
		msg.setMno(MobileNetworkOperator.ECONET);
		msg.setSourceMobileNumber(session.getMobileNumber());
		msg.setSourceBankAccount(session.getInput(SystemConstants.SOURCE_ACCOUNT));
		msg.setTargetMobileNumber(session.getMobileNumber());
		msg.setDdmmyyDate(session.getInput(SystemConstants.SUMMARY_DATE));
		msg.setTransactionType(USSDTransactionType.AGENT_SUMMARY);
		msg.setAgentNumber(session.getInput(SystemConstants.AGENT_NUMBER));
		msg.setSourceBankId(session.getBankCode());
		msg.setUuid(session.getSessionId());
		
		TransactionResponse transactionResponse = sendMessage(msg);
		return showFinalMenu(session, transactionResponse);
	}


}
