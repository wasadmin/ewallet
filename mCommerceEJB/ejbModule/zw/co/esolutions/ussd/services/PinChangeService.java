package zw.co.esolutions.ussd.services;

import java.util.Map;

import org.apache.log4j.Logger;

import zw.co.esolutions.ewallet.enums.MobileNetworkOperator;
import zw.co.esolutions.ewallet.enums.USSDTransactionType;
import zw.co.esolutions.ewallet.msg.USSDRequestMessage;
import zw.co.esolutions.mobile.banking.services.Pares;
import zw.co.esolutions.mobile.banking.services.TransactionResponse;
import zw.co.esolutions.ussd.conf.USSDConfiguration;
import zw.co.esolutions.ussd.encry.SimpleEncryption;
import zw.co.esolutions.ussd.entities.USSDSession;
import zw.co.esolutions.ussd.util.LoggerFactory;
import zw.co.esolutions.ussd.util.NumberUtils;
import zw.co.esolutions.ussd.util.SystemConstants;

public class PinChangeService extends USSDService {

	static USSDConfiguration config;
	static Logger logger = LoggerFactory.getLogger(BalanceEnquiryService.class);

	@Override
	public USSDSession processUSSDRequest(USSDSession session,
			Map<String, String> attributesMap) {
		String response = attributesMap
				.get(SystemConstants.USSD_REQUEST_STRING);
		Map<String, String> optionsMap = session.getOptionsMenuMap();
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
				
				if(session.getLevel() == 1) {
					if(SystemConstants.EXIT.equals(option)){
						session = super.terminateSession(session, null);
						return session;
					}else if(SystemConstants.BACK.equals(option)){

						if (session.isAgent()) {
							session = SelectService
									.generateAgentDefaultView(session);
						} else {
							session = SelectService
									.generateDefaultView(session);
						}
						return session;
					} else {
						
						String pinMessage = AuthenticationService.validatePin(response.trim());
						if(pinMessage != null) {
							session.setUssdResponseString(pinMessage);
							session.generateXml();
							return session;
						}
						Pares pares = AuthenticationService.authenticateSession(session, response.trim());
						
						if(pares.isVerificationResult()) {
							session.addUserInput(SystemConstants.PIN, SimpleEncryption.encryt(response.trim()));
							session = getNewPinMenu1(session);
							return session;
						} else {
							//invalid pin
							String menu = config.getStringValueOf(SystemConstants.CHANGE_PIN_INVALID_PIN_MSG);
							menu += config.getStringValueOf(
									SystemConstants.BACK_EXIT_MSG);
							session.setUssdResponseString(menu);
							session.generateXml();
							return session;
						}
					}
				} 
				
				else if (session.getLevel() == 2 ) {
					if (option != null) {
						if (SystemConstants.EXIT.equals(option)) {
							session = super.terminateSession(session, null);
							return session;
						} else if (SystemConstants.BACK.equals(option)) {
							if(session.isAutoPinChange()) {
								session = super.terminateSession(session, null);
								return session;
							}
							session = goToLevelOne(session);
							return session;
						}
					} else if (response != null && !response.trim().equals("")) {

						String validPin = this.validateNewPin2(response.trim());
						if (zw.co.esolutions.ewallet.sms.ResponseCode.E000.name().equalsIgnoreCase(validPin)) {
							
							if(session.getInput(SystemConstants.PIN).equalsIgnoreCase(SimpleEncryption.encryt(response.trim()))) {
								String menu = SystemConstants.PIN_CHANGE+" \nNew Pin matches Old Pin, try a different pin. \nEnter New Pin";
								menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
								session.addBackAndExitToOptionsMenu();
								session.setServiceName(SystemConstants.SERVICE_NAME_PINCHANGE);
								session.setUssdResponseString(menu);
								session.generateXml();
								return session;
							} else {
								session.addUserInput(SystemConstants.NEW_PIN_1, SimpleEncryption.encryt(response.trim()));
								session = getNewPinMenu2(session);
								return session;
							}
						} else {
							// return session;

							String menu = validPin;
							menu += config
									.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
							session.addBackAndExitToOptionsMenu();
							session
									.setServiceName(SystemConstants.SERVICE_NAME_PINCHANGE);
							session.setUssdResponseString(menu);
							session.generateXml();
							return session;
						}
					}
					String menu = config
							.getStringValueOf(SystemConstants.ENTER_NEW_PIN_INVALID_PIN_MSG);
					menu += config.getStringValueOf(
							SystemConstants.BACK_EXIT_MSG);
					session.addBackAndExitToOptionsMenu();
					session.setServiceName(SystemConstants.SERVICE_NAME_PINCHANGE);
					session.setUssdResponseString(menu);
					session.generateXml();
				} 
				
				else if (session.getLevel() == 3 ) {
					if (option != null) {
						if (SystemConstants.EXIT.equals(option)) {
							session = super.terminateSession(session, null);
							return session;
						} else if (SystemConstants.BACK.equals(option)) {
							session = getNewPinMenu1(session);
							return session;
						}
					} else if (response != null && !response.trim().equals("")) {

						String validPin = this.validateNewPin2(response.trim());
						if (zw.co.esolutions.ewallet.sms.ResponseCode.E000.name().equalsIgnoreCase(validPin)) {
							if(session.getInput(SystemConstants.NEW_PIN_1).equalsIgnoreCase(SimpleEncryption.encryt(response.trim()))) {
								session = processTransaction(session);
								return session;
							} else {
								String menu = SystemConstants.PIN_CHANGE+" \nNew Pins not matching. \nVerify New Pin";
								menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
								session.addBackAndExitToOptionsMenu();
								session.setServiceName(SystemConstants.SERVICE_NAME_PINCHANGE);
								session.setUssdResponseString(menu);
								session.generateXml();
								return session;
							}
						} else {
							// return session;

							String menu = validPin;
							menu += config
									.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
							session.addBackAndExitToOptionsMenu();
							session
									.setServiceName(SystemConstants.SERVICE_NAME_PINCHANGE);
							session.setUssdResponseString(menu);
							session.generateXml();
							return session;
						}
					}
					String menu = config
							.getStringValueOf(SystemConstants.ENTER_NEW_PIN_INVALID_PIN_MSG);
					menu += config.getStringValueOf(
							SystemConstants.BACK_EXIT_MSG);
					session.addBackAndExitToOptionsMenu();
					session.setServiceName(SystemConstants.SERVICE_NAME_PINCHANGE);
					session.setUssdResponseString(menu);
					session.generateXml();
				}
				
				else if(session.getLevel() == SystemConstants.CRITICAL_LEVEL) {
					if(SystemConstants.EXIT.equals(option)){
						session = super.terminateSession(session, null);
						return session;
					}else if(SystemConstants.BACK.equals(option)){
						session = getNewPinMenu1(session);
						return session;
					} else {
						
						Pares pares = AuthenticationService.authenticateSession(session, response.trim());
						if(pares.isVerificationResult()) {
							
							session = processTransaction(session);
						} else {
							//invalid pin
							String menu = config.getStringValueOf(SystemConstants.INVALID_PIN_MSG);
							menu += config.getStringValueOf(
									SystemConstants.BACK_EXIT_MSG);
							session.setUssdResponseString(menu);
							session.generateXml();
							return session;
						}
					}
				}
			} catch (Exception e) {
				logger.debug(" Message : "+e.getMessage());
				return super.terminateSession(session, config.getStringValueOf(SystemConstants.SERVICE_ERROR));
			}
		return session;

	}

	public static USSDSession processTransaction(USSDSession session) {
		// Initialize Config File
		config = getConfigInstance(session);
		
		USSDRequestMessage msg = new USSDRequestMessage();
		
		msg.setMno(MobileNetworkOperator.ECONET);
		msg.setSourceBankId(session.getBankCode());
		msg.setSourceMobileNumber(session.getMobileNumber());
        msg.setNewPin(SimpleEncryption.decrypt(session.getInput(SystemConstants.NEW_PIN_1)));
		msg.setTransactionType(USSDTransactionType.CHANGE_PASSCODE);
		msg.setUuid(session.getSessionId());
		
		TransactionResponse transactionResponse = sendMessage(msg);
		return showFinalMenu(session, transactionResponse);
	}
	
	public static USSDSession getNewPinMenu1(USSDSession session) {
		// Initialize Config File
		config = getConfigInstance(session);
		
		String menu = config.getStringValueOf(SystemConstants.ENTER_NEW_PIN_VALID_MSG);
		if(session.isAutoPinChange()) {
			menu += config.getStringValueOf(SystemConstants.EXIT_MSG);
		} else {
			menu += config.getStringValueOf(
			SystemConstants.BACK_EXIT_MSG);
			
		}
		session.addBackAndExitToOptionsMenu();
		session.setServiceName(SystemConstants.SERVICE_NAME_PINCHANGE);
		session.setUssdResponseString(menu);
		session.setLevel(2);
		session.generateXml();
		return session;
	}
	
	public static USSDSession getNewPinMenu2(USSDSession session) {
		// Initialize Config File
		config = getConfigInstance(session);
		
		String menu = config.getStringValueOf(SystemConstants.RE_ENTER_NEW_PIN_VALID_MSG);
		menu += config.getStringValueOf(
				SystemConstants.BACK_EXIT_MSG);
		session.addBackAndExitToOptionsMenu();
		session.setServiceName(SystemConstants.SERVICE_NAME_PINCHANGE);
		session.setUssdResponseString(menu);
		session.setLevel(3);
		session.generateXml();
		return session;
	}
	
	public static USSDSession goToLevelOne(USSDSession session) {
		// Initialize Config File
			config = getConfigInstance(session);
			
			String menu = config.getStringValueOf(SystemConstants.ENTER_OLD_PIN_CHANGE_PIN_MSG);
			menu += config.getStringValueOf(
			SystemConstants.BACK_EXIT_MSG);
			session.addBackAndExitToOptionsMenu();
			session.setLevel(SystemConstants.CRITICAL_LEVEL);
			session.setServiceName(SystemConstants.SERVICE_NAME_PINCHANGE);
			session.setUssdResponseString(menu);
			session.setLevel(1);
			session.generateXml();
			return session;		
			
		}
	
	public String validateNewPin1(String pinStr){
		String str = zw.co.esolutions.ewallet.sms.ResponseCode.E000.name();
		if(!NumberUtils.validatePin(pinStr)) {
			return SystemConstants.PIN_CHANGE+" \nNew Pin " + pinStr + " is not a number \nEnter New Pin";
		}
		if(pinStr.length() > 5) {
			return SystemConstants.PIN_CHANGE+" \nNew Pin " + pinStr + " too long. Required is 5 digits only \nEnter New Pin";
		} else if(pinStr.length() < 5) {
			return SystemConstants.PIN_CHANGE+" \nNew Pin " + pinStr + " too short. Required is 5 digits only \nEnter New Pin";
		}
		return str;
	}
	
	public String validateNewPin2(String pinStr){
		String str = zw.co.esolutions.ewallet.sms.ResponseCode.E000.name();
		if(!NumberUtils.validatePin(pinStr)) {
			return SystemConstants.PIN_CHANGE+" \nNew Pin " + pinStr + " is not a number \nVerify New Pin";
		}
		if(pinStr.length() > 5) {
			return SystemConstants.PIN_CHANGE+" \nNew Pin " + pinStr + " too long. Required is 5 digits only \nVerify New Pin";
		} else if(pinStr.length() < 5) {
			return SystemConstants.PIN_CHANGE+" \nNew Pin " + pinStr + " too short. Required is 5 digits only \nVerify New Pin";
		}
		return str;
	}

}
