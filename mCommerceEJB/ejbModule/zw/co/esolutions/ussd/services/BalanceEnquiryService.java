package zw.co.esolutions.ussd.services;

import java.util.Map;

import org.apache.log4j.Logger;

import zw.co.esolutions.ewallet.enums.MobileNetworkOperator;
import zw.co.esolutions.ewallet.enums.USSDTransactionType;
import zw.co.esolutions.ewallet.msg.USSDRequestMessage;
import zw.co.esolutions.mobile.banking.services.TransactionResponse;
import zw.co.esolutions.ussd.conf.USSDConfiguration;
import zw.co.esolutions.ussd.entities.USSDSession;
import zw.co.esolutions.ussd.util.LoggerFactory;
import zw.co.esolutions.ussd.util.SystemConstants;

public class BalanceEnquiryService extends USSDService {
	
	static USSDConfiguration config;
	static Logger logger = LoggerFactory.getLogger(BalanceEnquiryService.class);

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
			if(option != null){
				if(SystemConstants.EXIT.equals(option)){
					session = super.terminateSession(session, null);
					return session;
				}else if(SystemConstants.BACK.equals(option)){
					
					if(session.isAgent()) {
						session = SelectService.generateAgentDefaultView(session);
					} else {
						session = SelectService.generateDefaultView(session);
					}
					return session;
				}else{ 
					session.addUserInput(SystemConstants.SOURCE_ACCOUNT, option);
					session = processTransaction(session);
					return session;
				}
			}else{
				String menu = config.getStringValueOf(SystemConstants.SELECT_ACCOUNT_BALANCE_MSG_INVALID_OPTION);
				session.generateAccountsOptionsMenu();
			    menu += session.getAccountsMenu();
			    menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
				session.addBackAndExitToOptionsMenu();
				session.setServiceName(SystemConstants.SERVICE_NAME_BALANCE_ENQUIRY);
				session.setUssdResponseString(menu);
				session.generateXml();
				return session;
			}
		} catch (Exception e) {
			logger.debug(" Message : "+e.getMessage());
			return super.terminateSession(session, config.getStringValueOf(SystemConstants.SERVICE_ERROR));
		}
	}
	
	public static USSDSession processTransaction(USSDSession session){
		// Initialize Config File
		config = getConfigInstance(session);
		session.setServiceName(SystemConstants.SERVICE_NAME_BALANCE_ENQUIRY);
		USSDRequestMessage msg = new USSDRequestMessage();
		//List<UserInput> input = session.getUserInput();
		msg.setMno(MobileNetworkOperator.ECONET);
		msg.setSourceMobileNumber(session.getMobileNumber());
		msg.setSourceBankAccount(session.getInput(SystemConstants.SOURCE_ACCOUNT));
		msg.setTransactionType(USSDTransactionType.BALANCE);
		msg.setSourceBankId(session.getBankCode());
		msg.setUuid(session.getSessionId());
		logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>> Bank Id : "+msg.getSourceBankId());
		TransactionResponse transactionResponse = sendMessage(msg);
		return showFinalMenu(session, transactionResponse);
	}

}
