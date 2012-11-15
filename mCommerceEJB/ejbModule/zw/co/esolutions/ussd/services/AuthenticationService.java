package zw.co.esolutions.ussd.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.NumberUtil;
import zw.co.esolutions.mobile.banking.services.MobileNetworkOperator;
import zw.co.esolutions.mobile.banking.services.Pareq;
import zw.co.esolutions.mobile.banking.services.Pares;
import zw.co.esolutions.mobile.banking.services.Vereq;
import zw.co.esolutions.mobile.banking.services.Veres;
import zw.co.esolutions.mobile.banking.services.proxy.MobileBankingServiceProxy;
import zw.co.esolutions.ussd.conf.USSDConfiguration;
import zw.co.esolutions.ussd.encry.SimpleEncryption;
import zw.co.esolutions.ussd.entities.USSDSession;
import zw.co.esolutions.ussd.util.LoggerFactory;
import zw.co.esolutions.ussd.util.SystemConstants;

public class AuthenticationService extends USSDService {
	
	USSDConfiguration config;
	Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public USSDSession processUSSDRequest(USSDSession session, Map<String,String> attributesMap) {
		logger.debug(">>>>>>>>>>>> Auth Service...");
		//FIXME get bank by request code
		String bankCode;
		try {
			// Initialize Config File
			config = getConfigInstance(session, attributesMap);
			
			if(config == null) {
				String menu = "Invalid Request \nRequest "+session.getUssdRequestCode()+" is incorrect.";
				String code = USSDConfiguration.getInstance(SystemConstants.USSD_FILE_SERVICE_CODE_CASE).getStringValueOf(SystemConstants.SHORT_CODE);
				if(code.equalsIgnoreCase(attributesMap.get(SystemConstants.USSD_REQUEST_STRING))) {
					menu = "Welcome to e-Solutions Mobile Banking Platform \nPlease specify appropriate option for this request "+session.getUssdRequestCode()+", as *"+code.substring(1, code.length()-1)+"*OPT#, to access the services. E.g *"+code.substring(1, code.length()-1)+"*600#";
				}
				if(attributesMap.get(SystemConstants.USSD_REQUEST_STRING).startsWith(code.substring(0, code.length()-1)) && 
						attributesMap.get(SystemConstants.USSD_REQUEST_STRING).length() > code.length()) {
					menu = "Invalid Request \nInvalid option supplied "+attributesMap.get(SystemConstants.USSD_REQUEST_STRING).substring(code.length(), 
							attributesMap.get(SystemConstants.USSD_REQUEST_STRING).length() -1)+" for code "+code.substring(1, code.length()-1)+" in request "+
							attributesMap.get(SystemConstants.USSD_REQUEST_STRING)+".";
				}
				session.endSession();
				session.setUssdResponseString(menu);
				session.generateXml();
				session.setStatus(SystemConstants.STATUS_NEW);
				return session;
			}
			
			if(!Boolean.parseBoolean(config.getStringValueOf(SystemConstants.IS_BANK_SUPPORTED_ON_USSD))) {
				String menu = config.getStringValueOf(SystemConstants.BANK_NOT_SUPPORTED_MSG);
				session.endSession();
				session.setUssdResponseString(menu);
				session.generateXml();
				session.setStatus(SystemConstants.STATUS_NEW);
				return session;
			}
			
			//Initialize bank ID
			bankCode = config.getStringValueOf(SystemConstants.BANK_CODE_MAIN);
			
			if(SystemConstants.STATUS_NEW.equals(session.getStatus())){
				logger.debug(">>>>>>>>>>>>>>>>>>>>>>>> New Status.......");
				Veres veres;
				session.setUssdRequestCode(attributesMap.get(SystemConstants.USSD_REQUEST_STRING));
				
				String mobileNumber = session.getMobileNumber();
				session.setBankCode(bankCode);
				
				Vereq vereq = new Vereq();
				vereq.setAcquirerId(zw.co.esolutions.ewallet.util.SystemConstants.ECONET_ACQUIRER_ID);
				vereq.setMobileNumber(mobileNumber);
				vereq.setUssdSessionId(SystemConstants.ECONET_SESSION_ID_PREFIX + session.getSessionId());
				vereq.setBankCode(bankCode);
				vereq.setSourceMobileNetworkOperator(MobileNetworkOperator.ECONET);
				
				vereq.setTransactionTimestamp(DateUtil.convertToXMLGregorianCalendar(session.getTransactionTime()));
				
				veres = MobileBankingServiceProxy.getInstance().verifyEnrolment(vereq);
				
				if(!veres.isEnrolmentResult()) {
					
					bankCode = config.getStringValueOf(SystemConstants.BANK_CODE_SUB);
					vereq.setBankCode(bankCode);
					
					veres = MobileBankingServiceProxy.getInstance().verifyEnrolment(vereq);
				}
				
				
				if(veres.isEnrolmentResult()){
					session.setBankCode(bankCode);
					String menu;
					menu = config.getStringValueOf(SystemConstants.AUTHENTICATE_SCREEN);
					
					//Session ID is set the value of customrId from a webservice call;
					String customerId = veres.getUssdSessionId();
					session.setCustomerId(customerId);
					session.setUssdResponseString(menu);
					session.generateXml();
					
				}else{
					String menu = config.getStringValueOf(SystemConstants.NO_MOBILE_PROFILE_MSG);
					menu +=  " "+config.getStringValueOf(SystemConstants.PRODUCT_NAME);
					session.endSession();
					session.setUssdResponseString(menu);
					session.generateXml();
					session.setStatus(SystemConstants.STATUS_NEW);
					
				}
				return session;
				
			}else if(SystemConstants.STATUS_ACTIVE.equals(session.getStatus())){
				
				boolean isAgent = false;
				String pin = attributesMap.get(SystemConstants.USSD_REQUEST_STRING);
				
				String pinMessage = validatePin(pin);
				if(pinMessage != null) {
					session.setUssdResponseString(pinMessage);
					session.generateXml();
					return session;
				}
				Pares pares = authenticateSession(session, pin);
				
				if(pares.isVerificationResult()) {
					List<String> accounts = pares.getBankAccounts();
					
					List<String> eWalletAccounts = new ArrayList<String>();
					List<String> bankAccounts = new ArrayList<String>();
					
					if(pares.isAgent()) {
						logger.debug(">>>>>>>>>>>>>>>>>>>>>>>> Is agent.......");
						 isAgent = true;
												
						
					} else {
						logger.debug(">>>>>>>>>>>>>>>>>>>>>> Non Agent ");
						
					}
					 
					for(String acc : accounts) {
						 try {
							String ac = NumberUtil.formatMobileNumber(acc);
							eWalletAccounts.add(ac);
						} catch (Exception e) {
							bankAccounts.add(acc);
						}
					 }
					 if(eWalletAccounts.isEmpty()) {
						 eWalletAccounts = null;
					 }
					 if(bankAccounts.isEmpty()) {
						 bankAccounts = null;
					 }
					 logger.debug(">>>>>>>>>>>>>>>>>>>>>>> Ewallets = "+eWalletAccounts+", Bank Accs = "+bankAccounts);	
					 
					boolean hasEwalletAccounts = eWalletAccounts != null ? eWalletAccounts.size()> 0 : false; 
					boolean hasBankAccounts = bankAccounts != null ? bankAccounts.size()> 0 : false; 
					
					if(hasEwalletAccounts){
						session.setHasEwalletAccounts(true);
						for(String account : eWalletAccounts){
							session.addUserAccount(SystemConstants.ACCOUNT_TYPE_EWALLET, account);
						}
					}
					
					if(hasBankAccounts){
						session.setHasBankAccounts(true);
						for(String account : bankAccounts){
							session.addUserAccount(SystemConstants.ACCOUNT_TYPE_BANK_ACCOUNT, account);
						}
					}
					
					if(isAgent) {
						session.addUserInput(SystemConstants.AGENT_INITIATOR, SystemConstants.IS_AGENT);
					}
					
					if(pares.isAutoPinChange()) {
						session.addUserInput(SystemConstants.AUTO_PIN_CHANGE, SystemConstants.IS_AUTO_PIN_CHANGE);
					}
					
					if(session.isAutoPinChange()) {
						session.addUserInput(SystemConstants.PIN, SimpleEncryption.encryt(pin.trim()));
						session = PinChangeService.getNewPinMenu1(session);
					} else if(isAgent) {
						session = SelectService.generateAgentDefaultView(session);
					} else {
						session = SelectService.generateDefaultView(session);
					}
					
					return session;
				   				
				
				} else {
					
					String menu = pares.getNarrative();
					//Password Retry option
					if(config.getStringValueOf(SystemConstants.INVALID_PIN_MSG).equalsIgnoreCase(menu)) {
						session.setUssdResponseString(menu);
						session.generateXml();
						return session;
						
					} else {
						session.endSession();
						session.setUssdResponseString(menu);
						session.generateXml();
						return session;
						
					}
				}		
				
			}
			
		} catch (Exception e) {
			logger.debug(" Message : "+e.getMessage());
			session = super.terminateSession(session, config.getStringValueOf(SystemConstants.SERVICE_ERROR));
			
		}
		
		return session;
	}
	
	public final static Pares authenticateSession(USSDSession session, String pin) {
		Pareq pareq = new Pareq();
		pareq.setPassword(pin);
		pareq.setTransactionTimestamp(DateUtil.convertToXMLGregorianCalendar(new Date(System.currentTimeMillis())));
		pareq.setUssdSessionId(SystemConstants.ECONET_SESSION_ID_PREFIX + session.getSessionId());
		Pares pares = MobileBankingServiceProxy.getInstance().verifyPassword(pareq);
		return pares;
	
	}
	
	public static String validatePin(String pin) {
		String message = null;
		try {
			long digits = Long.parseLong(pin);
			digits = digits + 0;
			if(pin.length() == 5) {
				//Do nothing
			} else {
				message = "Password must be a 5 digit number. Try again : ";
			}
		} catch (Exception e) {
			message = "Password must be a 5 digit number. Try again : ";
		}
		return message;
	}
	
}
