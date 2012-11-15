package zw.co.esolutions.ussd.services;

import java.util.ArrayList;
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
import zw.co.esolutions.ussd.entities.UserAccount;
import zw.co.esolutions.ussd.util.LoggerFactory;
import zw.co.esolutions.ussd.util.NumberUtils;
import zw.co.esolutions.ussd.util.SystemConstants;

public class BillPaymentService extends USSDService {

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
			
			if(session.getLevel() == 1){
				
				if(SystemConstants.EXIT.equals(option)){
					session = super.terminateSession(session, null);
					return session;
				}else if(SystemConstants.BACK.equals(option)){
					session = SelectService.generateDefaultView(session);
					return session;
				} else if(option != null){
					session.addUserInput(SystemConstants.SOURCE_ACCOUNT, option);
					session = gotoViewAppropriateMerchants(session);
					return session;
				}else{
					session = gotoSelectAccount(session, config.getStringValueOf(SystemConstants.SELECT_ACCOUNT_BILLPAYMENT_INVALID_OPTION_MSG));
					return session;
				}
				
			
			}else if(session.getLevel() == 2){
				if(option != null){
					
					if(SystemConstants.EXIT.equals(option)){
						session = super.terminateSession(session, null);
						return session;
					}else if(SystemConstants.BACK.equals(option)){
						if(session.numberOfAccounts() == 1) {
							session = SelectService.generateDefaultView(session);
						} else {
							gotoSelectAccount(session, null);
						}
						return session;
					}else if(SystemConstants.OTHER.equals(option)){ 
						session = gotoViewOtherMerchants(session);
						return session;
					}else{
						
						session.addUserInput(SystemConstants.MERCHANT, option);
						
						session = gotoMerchantAccountsChoices(session);
						
						return session;
					}
					
				}else{
					session = gotoViewAppropriateMerchants(session);
					return session;
				}
			}else if(session.getLevel() == SystemConstants.MERCHANT_ACCOUNT_CHOICE_LEVEL){
				
				if(option != null){
					if(SystemConstants.EXIT.equals(option)){
						session = super.terminateSession(session, null);
						return session;
					}else if(SystemConstants.BACK.equals(option)){
						session = gotoViewAppropriateMerchants(session);
						return session;
					} else if(SystemConstants.BACK.equals(option)){
						session = gotoViewAppropriateMerchants(session);
						return session;
					} else if(SystemConstants.ENTER_ACCOUNT_BILLPAYMENT_OPTION.equals(option)) {
						
						// Enter To Go To Enter Account
						session = gotoEnterMerchantAccount(session, null);
						return session;
					} else if(session.isMerchantAccount(option)) {
						// Go to enter Amount
						session.addUserInput(SystemConstants.MERCHANT_ACCCOUNT, option.trim());
						session.addUserInput(SystemConstants.MERCHANT_OWN_ACCOUNT, SystemConstants.MERCHANT_OWN_ACCOUNT);
						session = gotoEnterAmount(session, null);
						return session;
					} else {
						return gotoMerchantAccountsChoices(session);
					}
				}
				
			}else if(session.getLevel() == 3){
				
				if(option != null){
					if(SystemConstants.EXIT.equals(option)){
						session = super.terminateSession(session, null);
						return session;
					}else if(SystemConstants.BACK.equals(option)){
						session = gotoViewAppropriateMerchants(session);
						return session;
					}
				}
				
				if(response != null && !"".equals(response.trim())){
					session.addUserInput(SystemConstants.MERCHANT_ACCCOUNT, response);
					String menu = config.getStringValueOf(SystemConstants.ENTER_AMOUNT_BILLPAYMENT_MSG);
					session = gotoEnterAmount(session, menu);
					return session;
				}else{
					String menu = config.getStringValueOf(SystemConstants.ENTER_AMOUNT_BILLPAYMENT_INVALID_OPTION_MSG);
					session = gotoEnterMerchantAccount(session, menu);
					return session;
				}
			}else if(session.getLevel() == 4){
				
				if(option != null){
					if(SystemConstants.EXIT.equals(option)){
						session = super.terminateSession(session, null);
						return session;
					}else if(SystemConstants.BACK.equals(option)){
						if(session.isMerchactAccountSelected()) {
							session = gotoMerchantAccountsChoices(session);
						} else {
							session = gotoEnterMerchantAccount(session, null);
						}
						
						return session;
					}
				}
				
				if(response != null && !"".equals(response.trim())){
					session.addUserInput(SystemConstants.AMOUNT, response);
					session = gotoConfirm(session, null);
					return session;
				}else{
					String menu = config.getStringValueOf(SystemConstants.ENTER_ACCOUNT_BILLPAYMENT_INVALID_OPTION_MSG);
					session = gotoEnterMerchantAccount(session, menu);
					return session;
				}
			}else if(session.getLevel() == 5){
				if(SystemConstants.EXIT.equals(option) || SystemConstants.CANCEL.equals(option)){
					session = super.terminateSession(session, null);
					return session;
				}else if(SystemConstants.BACK.equals(option)){
					session = gotoEnterAmount(session, null);
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
	
	
	public static USSDSession gotoViewOtherMerchants(USSDSession session){
		try {
			// Initialize Config File
			config = getConfigInstance(session);
			session.removeUserInput(SystemConstants.MERCHANT);
			List<MerchantInfo> merchants = USSDService.getOtherMerchants(session);
			int count = 0;
			String menu = config.getStringValueOf(SystemConstants.SELECT_MERCHANT_UNREGISTERED_MSG);
			//menu += "(Other Unregistered)";
			Map<String, String> map = new HashMap<String, String>();
			for(MerchantInfo merchant : merchants){
				menu += "\n " +  ++count + ". " + merchant.getMerchantName();
				map.put(""+count, merchant.getMerchantName());
			}
			session.setLevel(2);
			session.createOptionMenuFromMap(map);
			menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
			session.addBackAndExitToOptionsMenu();
			session.setServiceName(SystemConstants.SERVICE_NAME_BILL_PAYMENT);
			session.setUssdResponseString(menu);
			session.generateXml();
		} catch (Exception e) {
			logger.error(" Error occured getting all merchants , exception = " + e.getMessage());
		}
		return session;
	}
	
	public static USSDSession gotoSelectAccount(USSDSession session,String menu){
		// Initialize Config File
		config = getConfigInstance(session);
		
		if(menu == null){
			menu = config.getStringValueOf(SystemConstants.SELECT_ACCOUNT_BILL_PAYEMENT_MSG);
		}
		session.generateAccountsOptionsMenu();
	    menu += session.getAccountsMenu();
	    menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
	    session.setLevel(1);
	    session.addBackAndExitToOptionsMenu();
		session.setServiceName(SystemConstants.SERVICE_NAME_BILL_PAYMENT);
		session.setUssdResponseString(menu);
		session.generateXml();
		return session;
	}
	
	public static USSDSession gotoViewAppropriateMerchants(USSDSession session){
		    
		    try {
			    	// Initialize Config File
					config = getConfigInstance(session);
					session.removeUserInput(SystemConstants.MERCHANT);
					List<MerchantInfo> cmerchants = getRegisteredMerchant(session);
					
					logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>> C Merchants = "+cmerchants);
					if(cmerchants == null || cmerchants.size() == 0){
						logger.debug(">>>>>>>>>>>>>>>> Merchants 0");
						String menu = config.getStringValueOf(SystemConstants.SELECT_MERCHANT_UNREGISTERED_MSG);
						logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>> Menu = "+menu);
						
						session.setLevel(2);
						int count = 0;
						List<MerchantInfo> merchants = USSDService.getAllMerchants(session);
						Map<String, String> map = new HashMap<String, String>();
						for(MerchantInfo merchant : merchants){
							menu += "\n " +  ++count + ". " + merchant.getMerchantName();
							map.put(""+count, merchant.getMerchantName());
							
						}
						
						session.createOptionMenuFromMap(map);
						menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
						session.addBackAndExitToOptionsMenu();
						session.setServiceName(SystemConstants.SERVICE_NAME_BILL_PAYMENT);
						session.setUssdResponseString(menu);
						session.generateXml();
						return session;
					}else{
						int count = 0;
						String menu = config.getStringValueOf(SystemConstants.SELECT_MERCHANT_REGISTERED_MSG);
						
						Map<String, String> map = new HashMap<String, String>();
						for(MerchantInfo cmerchant : cmerchants){
							menu += "\n " +  ++count + ". " + cmerchant.getMerchantName();
							map.put(""+count, cmerchant.getMerchantName());
							session.addUserMerchantAccount(cmerchant.getMerchantName(), cmerchant.getUtilityAccount());
							
						}
						menu += "\n " +  ++count + ". " + SystemConstants.OTHER;
						map.put(""+count, SystemConstants.OTHER);
						session.setLevel(2);
						session.createOptionMenuFromMap(map);
						menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
						session.addBackAndExitToOptionsMenu();
						session.setServiceName(SystemConstants.SERVICE_NAME_BILL_PAYMENT);
						session.setUssdResponseString(menu);
						session.generateXml();
						return session;
					}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return session;
	}
	
	public static USSDSession gotoMerchantAccountsChoices(USSDSession session){
	    
	    try {
		    	// Initialize Config File
				config = getConfigInstance(session);
				session.removeUserInput(SystemConstants.MERCHANT_OWN_ACCOUNT);
				List<MerchantInfo> cmerchants = new ArrayList<MerchantInfo>();
				
				String merchantName = session.getInput(SystemConstants.MERCHANT);
				UserAccount userAccount = session.getMerchantAccount(merchantName);
				
				if(userAccount != null && userAccount.getAccountNumber() != null) {
					MerchantInfo info = new MerchantInfo();
					info.setMerchantName(merchantName);
					info.setUtilityAccount(userAccount.getAccountNumber());
					cmerchants.add(info);
				}
				logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>> C Merchants = "+cmerchants);
				
				if(cmerchants == null || cmerchants.size() == 0){
					return gotoEnterMerchantAccount(session, null);
				}else{
					int count = 0;
					String menu = config.getStringValueOf(SystemConstants.SELECT_MERCHANT_ACCOUNT_CHOICE__MSG);
					menu += "( "+cmerchants.get(0).getMerchantName()+" )";
					Map<String, String> map = new HashMap<String, String>();
					for(MerchantInfo cmerchant : cmerchants){
						menu += "\n " +  ++count + ". " + cmerchant.getUtilityAccount();
						map.put(""+count, cmerchant.getUtilityAccount());
						
					}
					menu += "\n " +  ++count + ". " + SystemConstants.ENTER_ACCOUNT_BILLPAYMENT_OPTION;
					map.put(""+count, SystemConstants.ENTER_ACCOUNT_BILLPAYMENT_OPTION);
					session.setLevel(SystemConstants.MERCHANT_ACCOUNT_CHOICE_LEVEL);
					session.createOptionMenuFromMap(map);
					menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
					session.addBackAndExitToOptionsMenu();
					session.setServiceName(SystemConstants.SERVICE_NAME_BILL_PAYMENT);
					session.setUssdResponseString(menu);
					session.generateXml();
					return session;
				}
		} catch (Exception e) {
			e.printStackTrace();
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
		msg.setAmount(NumberUtils.getAmountInCents(session.getInput(SystemConstants.AMOUNT)));
		msg.setTransactionType(USSDTransactionType.BILLPAY);
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
			menu = config.getStringValueOf(SystemConstants.ENTER_ACCOUNT_BILLPAYMENT_MSG);
		} 
		menu += " " + session.getInput(SystemConstants.MERCHANT)  + " account number \n";
		menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
		session.addBackAndExitToOptionsMenu();
		session.setLevel(3);
		session.setServiceName(SystemConstants.SERVICE_NAME_BILL_PAYMENT);
		session.setUssdResponseString(menu);
		session.generateXml();
        return session;					
	}
	
	public static USSDSession gotoEnterAmount(USSDSession session,String menu){
		// Initialize Config File
		config = getConfigInstance(session);
		session.removeUserInput(SystemConstants.AMOUNT);
		if(menu == null){
			menu = config.getStringValueOf(SystemConstants.ENTER_AMOUNT_BILLPAYMENT_MSG);
		}
		menu += " " + session.getInput(SystemConstants.MERCHANT);
		menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
		session.addBackAndExitToOptionsMenu();
		session.setLevel(4);
		session.setServiceName(SystemConstants.SERVICE_NAME_BILL_PAYMENT);
		session.setUssdResponseString(menu);
		session.generateXml();
        return session;					
	}
	
	public static USSDSession gotoConfirm(USSDSession session,String menu){
		// Initialize Config File
		config = getConfigInstance(session);
		
		if(menu == null){
			menu = config.getStringValueOf(SystemConstants.CONFIRM_BILLPAYMENT);
		} 
		
		menu += "\n Pay " + NumberUtils.getFormattedAmount(session.getInput(SystemConstants.AMOUNT)) + 
				" to " + session.getInput(SystemConstants.MERCHANT) + " ( Account " +session.getInput(SystemConstants.MERCHANT_ACCCOUNT) +  " ) " +  " from " + session.getInput(SystemConstants.SOURCE_ACCOUNT);
		menu += config.getStringValueOf(SystemConstants.CONFIRM_OPTIONS);
		menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
		session.addConfirmAndCancelToOptionsMenu();
		session.addBackAndExitToOptionsMenu();
		session.setLevel(5);
		session.setServiceName(SystemConstants.SERVICE_NAME_BILL_PAYMENT);
		session.setUssdResponseString(menu);
		session.generateXml();
        return session;					
	}


}
