package zw.co.esolutions.ussd.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import zw.co.esolutions.ewallet.enums.USSDTransactionType;
import zw.co.esolutions.ewallet.msg.USSDRequestMessage;
import zw.co.esolutions.mobile.banking.services.MerchantInfo;
import zw.co.esolutions.mobile.banking.services.MerchantRequest;
import zw.co.esolutions.mobile.banking.services.MerchantResponse;
import zw.co.esolutions.mobile.banking.services.TransactionRequest;
import zw.co.esolutions.mobile.banking.services.TransactionResponse;
import zw.co.esolutions.mobile.banking.services.TransactionType;
import zw.co.esolutions.mobile.banking.services.proxy.MobileBankingServiceProxy;
import zw.co.esolutions.ussd.conf.USSDConfiguration;
import zw.co.esolutions.ussd.entities.USSDSession;
import zw.co.esolutions.ussd.util.LoggerFactory;
import zw.co.esolutions.ussd.util.SystemConstants;
import zw.co.esolutions.ussd.web.services.proxy.UssdServiceProxy;

public abstract class USSDService {
	
	static USSDConfiguration config;
	static Logger logger = LoggerFactory.getLogger(USSDService.class);
	
	public static String[] airtimeTypes = {"Voice Airtime", "TXT"};
	public static String TXT = "TXT";
	public static USSDConfiguration getConfigInstance(USSDSession session, Map<String,String> attributesMap) {
		if(SystemConstants.STATUS_NEW.equals(session.getStatus())) {
			config = USSDConfiguration.getInstance(attributesMap.get(SystemConstants.USSD_REQUEST_STRING));
		} else {
			config = USSDConfiguration.getInstance(session.getUssdRequestCode());
		}
		return config;
	}
	
	public static List<MerchantInfo> getAllActiveMerchantsByBankId(USSDSession session) {
		try {
			MerchantRequest merchantRequest = new MerchantRequest();
			merchantRequest.setThirdParty(true);
			merchantRequest.setUssdSessionId(SystemConstants.ECONET_SESSION_ID_PREFIX + session.getSessionId());
			MerchantResponse merchantResponse = MobileBankingServiceProxy.getInstance().getMerchants(merchantRequest);
			return merchantResponse.getMerchants();
		} catch (Exception e) {
			return null;
		}
		
	}
	
	public static List<MerchantInfo> getAllMerchants(USSDSession session) {
		try {
			MerchantRequest merchantRequest = new MerchantRequest();
			merchantRequest.setThirdParty(true);
			merchantRequest.setUssdSessionId(SystemConstants.ECONET_SESSION_ID_PREFIX + session.getSessionId());
			MerchantResponse merchantResponse = MobileBankingServiceProxy.getInstance().getMerchants(merchantRequest);
			return merchantResponse.getMerchants();
		} catch (Exception e) {
			return null;
		}
	
	}
	
	public static List<MerchantInfo> getRegisteredMerchant(USSDSession session) {
		try {

			MerchantRequest merchantRequest = new MerchantRequest();
			merchantRequest.setThirdParty(false);
			merchantRequest.setUssdSessionId(SystemConstants.ECONET_SESSION_ID_PREFIX + session.getSessionId());
			MerchantResponse merchantResponse = MobileBankingServiceProxy.getInstance().getMerchants(merchantRequest);
			
			List<MerchantInfo> cmerchants = merchantResponse.getMerchants();
			
			return cmerchants;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static List<MerchantInfo> getOtherMerchants(USSDSession session) {
		try {
			List<MerchantInfo> merchants = new ArrayList<MerchantInfo>();
			MerchantRequest merchantRequest = new MerchantRequest();
			merchantRequest.setThirdParty(false);
			merchantRequest.setUssdSessionId(SystemConstants.ECONET_SESSION_ID_PREFIX + session.getSessionId());
			MerchantResponse merchantResponse = MobileBankingServiceProxy.getInstance().getMerchants(merchantRequest);
			
			List<MerchantInfo> custoMerchants = merchantResponse.getMerchants();
			
			List<MerchantInfo> allMerchants = getAllMerchants(session);
			
			for(MerchantInfo m : allMerchants) {
				if(custoMerchants == null || custoMerchants.isEmpty()) {
					return allMerchants;
				} else  {
					customers : for(MerchantInfo c : custoMerchants) {
						if(c.getMerchantName().equals(m.getMerchantName())) {
							//Dont Add M to list
							continue customers;
						} else {
							merchants.add(m);
						}
					}
				}
			}
			if(merchants.isEmpty()) {
				merchants = null;
			}
			return merchants;
		} catch (Exception e) {
			return null;
		}
	
	}
	
	public static USSDConfiguration getConfigUSSDInstance() {
		return USSDConfiguration.getInstance(SystemConstants.USSD_FILE_SERVICE_CODE_CASE);
		
	}
	
	public static USSDConfiguration getConfigInstance(USSDSession session) {
			config = USSDConfiguration.getInstance(session.getUssdRequestCode());
			return config;
	}
	public abstract USSDSession processUSSDRequest(USSDSession session,Map<String,String> attributesMap);
	
	public static USSDSession terminateSession(USSDSession session,String menuMsg){
		// Initialize Config File
		config = getConfigInstance(session);
		
		if(menuMsg == null){
			menuMsg = config.getStringValueOf(SystemConstants.DEFAULT_EXIT_MSG);
		}
		session.endSession();
		session.setUssdResponseString(menuMsg);
		session.generateXml();
		return session;
	}
	
	public static TransactionResponse sendMessage(USSDRequestMessage ussdMsg){
		logger.debug(">>>>>>>>>>> In Call Send Message");
		TransactionResponse transactionResponse = null;
		TransactionRequest transactionRequest = new TransactionRequest();
		
		transactionRequest.setAgentNumber(ussdMsg.getAgentNumber());
		transactionRequest.setNewPassword(ussdMsg.getNewPin());
		transactionRequest.setPaymentReference(ussdMsg.getPaymentRef());
		transactionRequest.setSourceAccountNumber(ussdMsg.getSourceBankAccount());
		transactionRequest.setTargetAccountNumber(ussdMsg.getTargetBankAccount());
		transactionRequest.setTargetBankCode(ussdMsg.getTargetBankId());
		transactionRequest.setTargetCustomerMerchantAccount(ussdMsg.getCustomerUtilityAccount());
		transactionRequest.setTargetMerchant(ussdMsg.getUtilityName());
		transactionRequest.setTargetMobileNumber(ussdMsg.getTargetMobileNumber());
		transactionRequest.setTransactionAmount(ussdMsg.getAmount());
		transactionRequest.setUssdSessionId(SystemConstants.ECONET_SESSION_ID_PREFIX + ussdMsg.getUuid());
		transactionRequest.setSecretCode(ussdMsg.getSecretCode());
		//Beneficiary
		if(USSDTransactionType.RTGS.equals(ussdMsg.getTransactionType())) {
			transactionRequest.setTargetBankCode(ussdMsg.getDestinationBankName());
			transactionRequest.setBeneficiaryName(ussdMsg.getBeneficiaryName());
		}
		if(USSDTransactionType.CHANGE_PASSCODE.equals(ussdMsg.getTransactionType())) {
			transactionRequest.setTransactionType(TransactionType.PASSWORD_CHANGE);
		} else if(USSDTransactionType.AGENT_CUSTOMER_DEPOSIT.equals(ussdMsg.getTransactionType())) {
			transactionRequest.setTransactionType(TransactionType.DEPOSIT);
		} else if(USSDTransactionType.AGENT_CUSTOMER_NON_HOLDER_WITHDRAWAL.equals(ussdMsg.getTransactionType())) {
			transactionRequest.setTransactionType(TransactionType.CASHOUT);
		} else if(USSDTransactionType.AGENT_CUSTOMER_WITHDRAWAL.equals(ussdMsg.getTransactionType())) {
			transactionRequest.setTransactionType(TransactionType.CASH_WITHDRAWAL);
		} else if(USSDTransactionType.REGISTER_MERCHANT.equals(ussdMsg.getTransactionType())) {
			transactionRequest.setTransactionType(TransactionType.MERCHANT_REG);
		} else if(USSDTransactionType.MINI_STATEMENT.equals(ussdMsg.getTransactionType())) {
			transactionRequest.setTransactionType(TransactionType.MINISTATEMENT);
		}else {
			transactionRequest.setTransactionType(TransactionType.valueOf(ussdMsg.getTransactionType().toString()));
		}
		
		
		try{
			transactionResponse = MobileBankingServiceProxy.getInstance().submitTransaction(transactionRequest);
		}catch(Exception e){
			return transactionResponse;
		}
		logger.debug(">>>>>>>>>>> Finished Send Message");
		return transactionResponse;
	}

	public final static String getTargetBankCodeForTargetAccount(USSDSession session) {
		String targetAccount = session.getInput(SystemConstants.TARGET_ACCOUNT);
		String targetMobile = session.getInput(SystemConstants.TARGET_MOBILE);
		logger.debug(">>>>>>>>>>>>>>>>>>>>>> Target Account = "+targetAccount+" Target Mobile = "+targetMobile);
		return UssdServiceProxy.getInstance().getTargetBankCodeForTargetAccount(SystemConstants.ECONET_SESSION_ID_PREFIX + session.getSessionId(), targetAccount, targetMobile);
		
	
	}
	
	public final static USSDSession showFinalMenu(USSDSession session, TransactionResponse transactionResponse) {
		try {
			String menu = transactionResponse.getNarrative();
			logger.debug(">>>>>>>>>>>>>>>>>>>> Menu = "+menu);
			Map<String, String> map = new HashMap<String, String>();
			menu += SystemConstants.MENU_LABEL;
			map.put(SystemConstants.MENU_VALUE, SystemConstants.MENU_LABEL);
			session.setLevel(SystemConstants.MENU_LEVEL);
			session.createOptionMenuFromMap(map);
			menu += config.getStringValueOf(SystemConstants.EXIT_MSG);
			session.addBackAndExitToOptionsMenu();
			session.setUssdResponseString(menu);
			session.generateXml();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return session;
	}

}
