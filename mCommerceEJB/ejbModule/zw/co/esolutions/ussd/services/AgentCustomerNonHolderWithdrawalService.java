package zw.co.esolutions.ussd.services;

import java.util.Map;

import org.apache.log4j.Logger;

import zw.co.esolutions.ewallet.enums.MobileNetworkOperator;
import zw.co.esolutions.ewallet.enums.USSDTransactionType;
import zw.co.esolutions.ewallet.msg.USSDRequestMessage;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.mobile.banking.services.TransactionResponse;
import zw.co.esolutions.ussd.conf.USSDConfiguration;
import zw.co.esolutions.ussd.entities.USSDSession;
import zw.co.esolutions.ussd.util.AgentAuth;
import zw.co.esolutions.ussd.util.LoggerFactory;
import zw.co.esolutions.ussd.util.NumberUtils;
import zw.co.esolutions.ussd.util.SystemConstants;

public class AgentCustomerNonHolderWithdrawalService extends USSDService {
	
	static Logger logger = LoggerFactory.getLogger(BalanceEnquiryService.class);
	static USSDConfiguration config;

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
					session = gotoLevelOne(session, config.getStringValueOf(SystemConstants.SELECT_ACCOUNT_AGENT_NON_HOLDER_INVALID_OPTION_MSG));
					return session;
				}
				
			
			}else if(session.getLevel() == 2){
				//target account
				if(SystemConstants.EXIT.equals(option)){
					session = super.terminateSession(session, null);
					return session;
				}else if(SystemConstants.BACK.equals(option)){
					//if(session.numberOfAccounts() == 1){
						session = SelectService.generateAgentDefaultView(session);
					/*}else if(session.numberOfAccounts() > 1){
						session = gotoLevelOne(session, null);
					}*/
					return session;
				} else if(response != null && !response.trim().equals("")){
					response = response.toUpperCase();
					String authResp = AgentAuth.validateOldRef(response);
					
					if(ResponseCode.E000.name().equalsIgnoreCase(authResp)) {
						session.addUserInput(SystemConstants.TRANSACTION_REF, response);
						session = gotoLevelThree(session, null);
					} else {
						session = gotoLevelFour(session, authResp);
					}
					return session;
				}else{
					session = gotoLevelTwo(session, config.getStringValueOf(SystemConstants.ENTER_TRANSACTION_REF_INVALID_OPTION_MSG));
					return session;
				}
			}else if(session.getLevel() == 3){
				if(SystemConstants.EXIT.equals(option)){
					session = super.terminateSession(session, null);
					return session;
				}else if(SystemConstants.BACK.equals(option)){
					session = gotoLevelTwo(session, null);
					return session;
				} else if(response != null && !response.trim().equals("")){
					boolean valid = NumberUtils.validateAmount(response.trim());
					if(valid){
						//gotoConfirm
						String agentNumber = AgentAuth.getAgentByMobileNumberAndBankId(session.getMobileNumber(), session.getBankCode());
						if (agentNumber != null) {
							session.addUserInput(SystemConstants.AGENT_NUMBER, agentNumber);
						}
						session.addUserInput(SystemConstants.AMOUNT, response.trim());
						session = gotoLevelFour(session, null);
					}else{
						session = gotoLevelThree(session, config.getStringValueOf(SystemConstants.ENTER_AMOUNT_INVALID_OPTION_MSG));
					}
					return session;
				}else{
					session = gotoLevelThree(session, config.getStringValueOf(SystemConstants.ENTER_AMOUNT_INVALID_OPTION_MSG));
					return session;
				}
			}else if(session.getLevel() == 4){
				if(SystemConstants.EXIT.equals(option) || SystemConstants.CANCEL.equals(option)){
					session = super.terminateSession(session, null);
					return session;
				}else if(SystemConstants.BACK.equals(option)){
					session = gotoLevelThree(session, null);
					return session;
				}  else if(response != null && !response.trim().equals("")){
					
					if(SystemConstants.MAX_SECRET_CODE_LENGTH < response.length()) {
						session = gotoLevelFour(session, config.getStringValueOf(SystemConstants.INVALID_NON_HOLDER_CODE));
					} else {
					response = response.toUpperCase();
					
					
					session.addUserInput(SystemConstants.NON_HOLDER_CODE, response);
					session = gotoConfirmationLevel(session, null);
					}
					return session;
				}else {
					session = gotoLevelFour(session, config.getStringValueOf(SystemConstants.INVALID_OPTION_DEFAULT));
					return session;
				}
			}else if(session.getLevel() == 5){
				if(SystemConstants.EXIT.equals(option) || SystemConstants.CANCEL.equals(option)){
					session = super.terminateSession(session, null);
					return session;
				}else if(SystemConstants.BACK.equals(option)){
					session = gotoLevelFour(session, null);
					return session;
				} else if(SystemConstants.CONFIRM.equals(option)){
					session = processTransaction(session);
					return session;
				}else {
					session = gotoLevelFour(session, config.getStringValueOf(SystemConstants.INVALID_OPTION_DEFAULT));
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
			menu = config.getStringValueOf(SystemConstants.SELECT_ACCOUNT_AGENT_WITH_NON_MSG);
		}
		session.generateAccountsOptionsMenu();
	    menu += session.getAccountsMenu();
	    menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
	    session.setLevel(1);
	    session.addBackAndExitToOptionsMenu();
		session.setServiceName(SystemConstants.SERVICE_NAME_AGENT_NON_WITH);
		session.setUssdResponseString(menu);
		session.generateXml();
		return session;
	}
	
	public static USSDSession gotoLevelTwo(USSDSession session,String menu){
		// Initialize Config File
		config = getConfigInstance(session);
		
		if(menu == null){
			menu = config.getStringValueOf(SystemConstants.ENTER_TRANSACTION_REF_MSG);
		} 
		menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
		session.addBackAndExitToOptionsMenu();
		session.setLevel(2);
		session.setServiceName(SystemConstants.SERVICE_NAME_AGENT_NON_WITH);
		session.setUssdResponseString(menu);
		session.generateXml();
        return session;					
	}
	
	public static USSDSession gotoLevelThree(USSDSession session,String menu){
		// Initialize Config File
		config = getConfigInstance(session);
		
		if(menu == null){
			menu = config.getStringValueOf(SystemConstants.AGENT_NON_WITH_ENTER_AMOUNT_MSG);
		} 
		menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
		session.addBackAndExitToOptionsMenu();
		session.setLevel(3);
		session.setServiceName(SystemConstants.SERVICE_NAME_AGENT_NON_WITH);
		session.setUssdResponseString(menu);
		session.generateXml();
        return session;					
	}
	public static USSDSession gotoLevelFour(USSDSession session,String menu){
		// Initialize Config File
		config = getConfigInstance(session);
		
		if(menu == null){
			menu = config.getStringValueOf(SystemConstants.AGENT_ENTER_NON_HOLDER_CODE);
		} 
		menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
		session.addBackAndExitToOptionsMenu();
		session.setLevel(4);
		session.setServiceName(SystemConstants.SERVICE_NAME_AGENT_NON_WITH);
		session.setUssdResponseString(menu);
		session.generateXml();
        return session;					
	}
	public static USSDSession gotoConfirmationLevel(USSDSession session,String menu){
		// Initialize Config File
		config = getConfigInstance(session);
		
		if(menu == null){
			menu = config.getStringValueOf(SystemConstants.CONFIRM_NON_HOLDER_WITHDRAWAL);
		} 
		menu += "\n Non Holder Withdrawal of " + NumberUtils.getFormattedAmount(session.getInput(SystemConstants.AMOUNT)) + 
		" Ref : " + session.getInput(SystemConstants.TRANSACTION_REF)+" \n Code :" +session.getInput(SystemConstants.NON_HOLDER_CODE)+  " Agent : " + session.getInput(SystemConstants.AGENT_NUMBER);
		
		menu += config.getStringValueOf(SystemConstants.CONFIRM_OPTIONS);
		menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
		session.addConfirmAndCancelToOptionsMenu();
		session.addBackAndExitToOptionsMenu();
		session.setLevel(5);
		session.setServiceName(SystemConstants.SERVICE_NAME_AGENT_NON_WITH);
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
		msg.setRefCode(session.getInput(SystemConstants.TRANSACTION_REF));
		msg.setAmount(NumberUtils.getAmountInCents(session.getInput(SystemConstants.AMOUNT)));
		msg.setTransactionType(USSDTransactionType.AGENT_CUSTOMER_NON_HOLDER_WITHDRAWAL);
		msg.setSourceBankId(session.getBankCode());
		msg.setAgentNumber(session.getInput(SystemConstants.AGENT_NUMBER));
		msg.setSecretCode(session.getInput(SystemConstants.NON_HOLDER_CODE));
		msg.setUuid(session.getSessionId());
		
		TransactionResponse transactionResponse = sendMessage(msg);
		return showFinalMenu(session, transactionResponse);
	}
	
	
}
