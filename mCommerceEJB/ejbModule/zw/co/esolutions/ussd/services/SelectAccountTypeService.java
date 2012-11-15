package zw.co.esolutions.ussd.services;

import java.util.Map;

import zw.co.esolutions.ussd.conf.USSDConfiguration;
import zw.co.esolutions.ussd.entities.USSDSession;
import zw.co.esolutions.ussd.util.AccountType;
import zw.co.esolutions.ussd.util.SystemConstants;

public class SelectAccountTypeService extends USSDService{
	
	USSDConfiguration config;

	@Override
	public USSDSession processUSSDRequest(USSDSession session,
			Map<String, String> attributesMap) {
		
		String response = attributesMap.get(SystemConstants.USSD_REQUEST_STRING);
		Map<String,String> optionsMap =  session.getOptionsMenuMap();
		String option = optionsMap.get(response);
		
		boolean isAgent = session.isAgent();
		
		// Initialize Config File
		config = getConfigInstance(session, attributesMap);
		
		if(option != null && !isAgent){
			if(SystemConstants.E_WALLET.equals(option)){
				session.setAccountType(AccountType.E_WALLET);
			}else if(SystemConstants.BANK_ACCOUNT.equals(option)){
				session.setAccountType(AccountType.BANK_ACCOUNT);
			}
			String menu = config.getStringValueOf(SystemConstants.SELECT_SERVICE_TYPE_MSG);
			session.generateOptionsMenu(config.getStringValueOf(SystemConstants.SERVICE_OPTIONS));
			session.setServiceName(SystemConstants.SERVICE_NAME_SELECT_SERVICE);
			session.setUssdResponseString(menu);
			session.generateXml();
			
		} else  if(option != null && isAgent){
			
			if(SystemConstants.E_WALLET.equals(option)){
				session.setAccountType(AccountType.E_WALLET);
			}else if(SystemConstants.BANK_ACCOUNT.equals(option)){
				session.setAccountType(AccountType.BANK_ACCOUNT);
			}
			String menu = config.getStringValueOf(SystemConstants.SELECT_SERVICE_TYPE_AGENT_MSG);
			session.generateOptionsMenu(config.getStringValueOf(SystemConstants.SERVICE_OPTIONS_AGENT));
			session.setServiceName(SystemConstants.SERVICE_NAME_SELECT_SERVICE);
			session.setUssdResponseString(menu);
			session.generateXml();
			
		} else {
			String menu = config.getStringValueOf(SystemConstants.SELECT_ACCOUNT_TYPE_INVALID_OPTIONS_MSG);
			session.generateOptionsMenu(config.getStringValueOf(SystemConstants.ACCOUNT_TYPE_OPTIONS));
			session.setServiceName(SystemConstants.SERVICE_NAME_SELECT_ACCOUNT_TYPE);
			session.setUssdResponseString(menu);
			session.generateXml();
			return session;
		}
		return session;
	}
	

}
