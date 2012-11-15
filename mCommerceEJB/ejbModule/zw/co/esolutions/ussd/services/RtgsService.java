package zw.co.esolutions.ussd.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import zw.co.esolutions.ewallet.enums.MobileNetworkOperator;
import zw.co.esolutions.ewallet.enums.USSDTransactionType;
import zw.co.esolutions.ewallet.msg.USSDRequestMessage;
import zw.co.esolutions.ewallet.util.NumberUtil;
import zw.co.esolutions.mobile.banking.services.BankInfo;
import zw.co.esolutions.mobile.banking.services.BankRequest;
import zw.co.esolutions.mobile.banking.services.BankResponse;
import zw.co.esolutions.mobile.banking.services.TransactionResponse;
import zw.co.esolutions.mobile.banking.services.proxy.MobileBankingServiceProxy;
import zw.co.esolutions.ussd.conf.USSDConfiguration;
import zw.co.esolutions.ussd.entities.USSDSession;
import zw.co.esolutions.ussd.util.LoggerFactory;
import zw.co.esolutions.ussd.util.NumberUtils;
import zw.co.esolutions.ussd.util.SystemConstants;

public class RtgsService extends USSDService {
	
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
						session = SelectService.generateDefaultView(session);
						return session;
					} else if(option != null){
						session.addUserInput(SystemConstants.SOURCE_ACCOUNT, option);
						session = gotoSelectBank(session);
						return session;
					}else{
						session = gotoLevelOne(session, config.getStringValueOf(SystemConstants.SELECT_ACCOUNT_SOURCE_INVALID_OPTION_MSG));
						return session;
					}
				
			
			}	else if(session.getLevel() == 2){
					if(option != null){
						
						if(SystemConstants.EXIT.equals(option)){
							session = super.terminateSession(session, null);
							return session;
						}else if(SystemConstants.BACK.equals(option)){
							session = SelectService.generateDefaultView(session);
							return session;
						} else{
							String [] tokens = option.split("\\*");
							session.addUserInput(SystemConstants.SELECTED_BANK, tokens[0]);
							session.addUserInput(SystemConstants.SELECTED_BANK_CODE, tokens[1]);
							session = gotoEnterDestinationAccountLevel(session,null);
							return session;
						}
						
					}else{
						session = gotoSelectBank(session);
						return session;
					}
			}	else if(session.getLevel() == 3){
					//destination account mobile
					if(SystemConstants.EXIT.equals(option)){
						session = super.terminateSession(session, null);
						return session;
					}else if(SystemConstants.BACK.equals(option)){
						
						session = gotoSelectBank(session);
						
						return session;
					} else if(response != null && !response.trim().equals("")){
						response = response.trim();
						// Format Target if mobile
						try {
							response = NumberUtil.formatMobileNumber(response);
						} catch (Exception e) {
	
						}
						
						if(session.getInput(SystemConstants.SOURCE_ACCOUNT).equalsIgnoreCase(response)) {
							session = gotoEnterDestinationAccountLevel(session, SystemConstants.SERVICE_NAME_RTGS_PAYMENT +" \n"+ config
									.getStringValueOf(SystemConstants.TRANSFER_GENERIC_INVALID_ACCOUNTS_ACCOUNT));
									return session;
						} else {
							session.addUserInput(SystemConstants.TARGET_ACCOUNT,
									response);
							session = gotoEnterBeneficiaryNameLevel(session, null);
							return session;
						}
					} else {
						session = gotoEnterDestinationAccountLevel(
								session, SystemConstants.SERVICE_NAME_RTGS_PAYMENT +" \n"+ config
								.getStringValueOf(SystemConstants.ENTER_GENERIC_TARGET_ACCOUNT_INVALID_OPTION_MSG));
						return session;
					}
				
			} else if(session.getLevel() == 4){
					if(SystemConstants.EXIT.equals(option)){
						session = super.terminateSession(session, null);
						return session;
					}else if(SystemConstants.BACK.equals(option)){
						session = gotoEnterDestinationAccountLevel(session, null);
						return session;
					} else if(response != null && !response.trim().equals("")){
						
						session.addUserInput(SystemConstants.BENEFICIARY_NAME, response.trim());
						session = gotoEnterPaymentRefLevel(session, null);
						
						return session;
					}else{
						session = gotoEnterBeneficiaryNameLevel(session, config.getStringValueOf(SystemConstants.ENTER_BENEFICIARY_NAME_INVALID_MSG));
						return session;
					}
				} else if(session.getLevel() == 5){
					if(SystemConstants.EXIT.equals(option)){
						session = super.terminateSession(session, null);
						return session;
					}else if(SystemConstants.BACK.equals(option)){
						session = gotoEnterBeneficiaryNameLevel(session, null);
						return session;
					} else if(response != null && !response.trim().equals("")){
						
						session.addUserInput(SystemConstants.PAYMENT_REF, response.trim());
						session = gotoEnterAmountLevel(session, null);
						
						return session;
					}else{
						session = gotoEnterPaymentRefLevel(session, config.getStringValueOf(SystemConstants.ENTER_PAYMENT_REF_INVALID_MSG));
						return session;
					}
			} else if(session.getLevel() == 6){
					if(SystemConstants.EXIT.equals(option)){
						session = super.terminateSession(session, null);
						return session;
					}else if(SystemConstants.BACK.equals(option)){
						session = gotoEnterPaymentRefLevel(session, null);
						return session;
					} else if(response != null && !response.trim().equals("")){
						boolean valid = NumberUtils.validateAmount(response.trim());
						if(valid){
							//gotoConfirm
							session.addUserInput(SystemConstants.AMOUNT, response.trim());
							session = gotoConfirmationLevel(session, null);
						}else{
							session = gotoEnterAmountLevel(session, config.getStringValueOf(SystemConstants.ENTER_RTGS_AMOUNT_INVALID_MSG));
						}
						return session;
					}else{
						session = gotoEnterAmountLevel(session, config.getStringValueOf(SystemConstants.ENTER_RTGS_AMOUNT_INVALID_MSG));
						return session;
					}
			}	else if(session.getLevel() == 7){
					if(SystemConstants.EXIT.equals(option) || SystemConstants.CANCEL.equals(option)){
						session = super.terminateSession(session, null);
						return session;
					}else if(SystemConstants.BACK.equals(option)){
						session = gotoEnterAmountLevel(session, null);
						return session;
					} else if(SystemConstants.CONFIRM.equals(option)){
						session = processTransaction(session);
						return session;
					}else {
						session = gotoConfirmationLevel(session, config.getStringValueOf(SystemConstants.INVALID_OPTION_DEFAULT));
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
			menu = config.getStringValueOf(SystemConstants.SELECT_ACCOUNT_RTGS_MSG);
		}
		session.generateAccountsOptionsMenu();
	    menu += session.getAccountsMenu();
	    menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
	    session.setLevel(1);
	    session.addBackAndExitToOptionsMenu();
		session.setServiceName(SystemConstants.SERVICE_NAME_RTGS_PAYMENT);
		session.setUssdResponseString(menu);
		session.generateXml();
		return session;
	}
	
	public static USSDSession gotoSelectBank(USSDSession session){
		try {
			// Initialize Config File
			config = getConfigInstance(session);
			
			
			//List<String> banks = UssdServiceProxy.getInstance().getActiveBankNames();
			BankRequest bankRequest = new BankRequest();
			BankResponse bankResponse = MobileBankingServiceProxy.getInstance().getBanks(bankRequest);
			List<BankInfo> banks = bankResponse.getBanks();
			int count = 0;
			String menu = config.getStringValueOf(SystemConstants.SELECT_BANK_ACTIVE_MSG);
			Map<String, String> map = new HashMap<String, String>();
			for(BankInfo bank : banks){
				menu += "\n " +  ++count + ". " + bank.getBankName();
				map.put(""+count, bank.getBankName()+"*"+bank.getBankCode());
			}
			session.setLevel(2);
			session.createOptionMenuFromMap(map);
			menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
			session.addBackAndExitToOptionsMenu();
			session.setServiceName(SystemConstants.SERVICE_NAME_RTGS_PAYMENT);
			session.setUssdResponseString(menu);
			session.generateXml();
		} catch (Exception e) {
			logger.error(" Error occured getting all banks , exception = " + e.getMessage());
		}
		return session;
	}
	
	public static USSDSession gotoEnterDestinationAccountLevel(USSDSession session,String menu){
		logger.debug(">>>>>>>>>>>>>>>>>> In gotoEnterDestinationAccountLevel");
		// Initialize Config File
		config = getConfigInstance(session);
		logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Got config "+config);
		if(menu == null){
			menu = SystemConstants.SERVICE_NAME_RTGS_PAYMENT + " \n" + config.getStringValueOf(SystemConstants.ENTER_GENERIC_TARGET_ACCOUNT_MSG);
		} 
		logger.debug(">>>>>>>>>>>>>>>>>>>>>>>> Menu = "+menu);
		menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
		logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>> Done creating menu = "+menu);
		session.addBackAndExitToOptionsMenu();
		session.setLevel(3);
		session.setServiceName(SystemConstants.SERVICE_NAME_RTGS_PAYMENT);
		session.setUssdResponseString(menu);
		session.generateXml();
        return session;					
	}
	
	public static USSDSession gotoEnterBeneficiaryNameLevel(USSDSession session,String menu){
		// Initialize Config File
		config = getConfigInstance(session);
		
		if(menu == null){
			menu = config.getStringValueOf(SystemConstants.ENTER_BENEFICIARY_NAME_MSG);
		} 
		menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
		session.addBackAndExitToOptionsMenu();
		session.setLevel(4);
		session.setServiceName(SystemConstants.SERVICE_NAME_RTGS_PAYMENT);
		session.setUssdResponseString(menu);
		session.generateXml();
        return session;					
	}
	
	public static USSDSession gotoEnterPaymentRefLevel(USSDSession session,String menu){
		// Initialize Config File
		config = getConfigInstance(session);
		
		if(menu == null){
			menu = config.getStringValueOf(SystemConstants.ENTER_PAYMENT_REF_MSG);
		} 
		menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
		session.addBackAndExitToOptionsMenu();
		session.setLevel(5);
		session.setServiceName(SystemConstants.SERVICE_NAME_RTGS_PAYMENT);
		session.setUssdResponseString(menu);
		session.generateXml();
        return session;					
	}
	
	public static USSDSession gotoEnterAmountLevel(USSDSession session,String menu){
		// Initialize Config File
		config = getConfigInstance(session);
		
		if(menu == null){
			menu = config.getStringValueOf(SystemConstants.ENTER_RTGS_AMOUNT_MSG);
		} 
		menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
		session.addBackAndExitToOptionsMenu();
		session.setLevel(6);
		session.setServiceName(SystemConstants.SERVICE_NAME_RTGS_PAYMENT);
		session.setUssdResponseString(menu);
		session.generateXml();
        return session;					
	}
	
	public static USSDSession gotoConfirmationLevel(USSDSession session,String menu){
		// Initialize Config File
		config = getConfigInstance(session);
		
		if(menu == null){
			menu = config.getStringValueOf(SystemConstants.CONFIRM_RTGS);
		} 
		
		menu += "DETAILS " +
				"\nSource Acc: " + session.getInput(SystemConstants.SOURCE_ACCOUNT)  +  " " +
				"\nDestination Bank: " + session.getInput(SystemConstants.SELECTED_BANK) +" "+ 
				"\nDestination Account: " + session.getInput(SystemConstants.TARGET_ACCOUNT) +" "+
				"\nBeneficiary: " + session.getInput(SystemConstants.BENEFICIARY_NAME) +" "+
				"\nPayment Ref: " + session.getInput(SystemConstants.PAYMENT_REF)+" "+
				"\nAmount: " + NumberUtils.getFormattedAmount(session.getInput(SystemConstants.AMOUNT));
		
		menu += config.getStringValueOf(SystemConstants.CONFIRM_OPTIONS);
		menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
		session.addConfirmAndCancelToOptionsMenu();
		session.addBackAndExitToOptionsMenu();
		session.setLevel(7);
		session.setServiceName(SystemConstants.SERVICE_NAME_RTGS_PAYMENT);
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
		msg.setDestinationBankName(session.getInput(SystemConstants.TARGET_ACCOUNT));
		msg.setAmount(NumberUtils.getAmountInCents(session.getInput(SystemConstants.AMOUNT)));
		msg.setTransactionType(USSDTransactionType.RTGS);
		msg.setSourceBankId(session.getBankCode());
		msg.setDestinationBankName(session.getInput(SystemConstants.SELECTED_BANK));
		msg.setTargetBankId(session.getInput(SystemConstants.SELECTED_BANK_CODE));
		msg.setUuid(session.getSessionId());
		msg.setPaymentRef(session.getInput(SystemConstants.PAYMENT_REF));
		msg.setBeneficiaryName(session.getInput(SystemConstants.BENEFICIARY_NAME));
		
		TransactionResponse transactionResponse = sendMessage(msg);
		return showFinalMenu(session, transactionResponse);
	}


}
