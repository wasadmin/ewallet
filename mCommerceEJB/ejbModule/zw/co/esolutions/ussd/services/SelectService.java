package zw.co.esolutions.ussd.services;

import java.util.Map;

import org.apache.log4j.Logger;

import zw.co.esolutions.ussd.conf.USSDConfiguration;
import zw.co.esolutions.ussd.entities.USSDSession;
import zw.co.esolutions.ussd.entities.UserAccount;
import zw.co.esolutions.ussd.util.AccountType;
import zw.co.esolutions.ussd.util.LoggerFactory;
import zw.co.esolutions.ussd.util.SystemConstants;

public class SelectService extends USSDService{
	
	static USSDConfiguration config;
	static Logger logger = LoggerFactory.getLogger(BalanceEnquiryService.class);


	@Override
	public USSDSession processUSSDRequest(USSDSession session,
			Map<String, String> attributesMap) {
		// Initialize Config File
		config = getConfigInstance(session, attributesMap);
		
		String response = attributesMap.get(SystemConstants.USSD_REQUEST_STRING);
		Map<String,String> optionsMap =  session.getOptionsMenuMap();
		String option = optionsMap.get(response);
		
		boolean isAgent = session.isAgent();
	    
		logger.debug(">>>>>>>>>>>>>>>>>>>>. Option : "+option+" , Is agent "+isAgent);
		
		try {
			if(option != null){
				
				if(SystemConstants.SERVICE_OPTION_BALANCE.equals(option)){
					
					if(session.numberOfAccounts() == 1){
						UserAccount account = session.getAppropriateUserAccounts().get(0);
						session.addUserInput(SystemConstants.SOURCE_ACCOUNT, account.getAccountNumber());
						session = BalanceEnquiryService.processTransaction(session);
			            return session;					
					}else{
						
						String menu = config.getStringValueOf(SystemConstants.SELECT_ACCOUNT_BALANCE_MSG);
						session.generateAccountsOptionsMenu();
					    menu += session.getAccountsMenu();
					    menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
						session.addBackAndExitToOptionsMenu();
						session.setServiceName(SystemConstants.SERVICE_NAME_BALANCE_ENQUIRY);
						session.setUssdResponseString(menu);
						session.generateXml();
						return session;
						
					}
					
				}else if(SystemConstants.SERVICE_OPTION_BILL_PAYMENT.equals(option)){
					
					if(session.numberOfAccounts() == 1){
						UserAccount account = session.getAppropriateUserAccounts().get(0);
						session.addUserInput(SystemConstants.SOURCE_ACCOUNT, account.getAccountNumber());
						session = BillPaymentService.gotoViewAppropriateMerchants(session);
						return session;
					}else {
						session = BillPaymentService.gotoSelectAccount(session, null);
						return session;
						
					}
					
					
					
					
				}else if(SystemConstants.SERVICE_OPTION_MINI_STATEMENT.equals(option)){
					
					if(session.numberOfAccounts() == 1){
						UserAccount account = session.getAppropriateUserAccounts().get(0);
						session.addUserInput(SystemConstants.SOURCE_ACCOUNT, account.getAccountNumber());
						session = MiniStatementService.processTransaction(session);
			            return session;					
					}else{
						
						String menu = config.getStringValueOf(SystemConstants.SELECT_ACCOUNT_MINISTATEMENT_MSG);
						session.generateAccountsOptionsMenu();
					    menu += session.getAccountsMenu();
					    menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
						session.addBackAndExitToOptionsMenu();
						session.setLevel(1);
						session.setServiceName(SystemConstants.SERVICE_NAME_MINI_STATEMENT);
						session.setUssdResponseString(menu);
						session.generateXml();
						return session;
						
					}
					
				}else if(SystemConstants.SERVICE_OPTION_PIN_CHANGE.equals(option)){
					/*String menu = config.getStringValueOf(SystemConstants.ENTER_NEW_PIN_VALID_MSG);
					menu += config.getStringValueOf(
					SystemConstants.BACK_EXIT_MSG);
					session.addBackAndExitToOptionsMenu();
					session.setServiceName(SystemConstants.SERVICE_NAME_PINCHANGE);
					session.setUssdResponseString(menu);
					session.setLevel(2);
					session.generateXml();*/
					session = PinChangeService.goToLevelOne(session);
					return session;
					
				}else if(SystemConstants.SERVICE_OPTION_TOPUP_MY_PHONE.equals(option)){
					
					if(session.numberOfAccounts() == 1){
						UserAccount account = session.getAppropriateUserAccounts().get(0);
						session.addUserInput(SystemConstants.SOURCE_ACCOUNT, account.getAccountNumber());
						session = SelfTopupService.gotoLevelTwo(session,null);
						return session;
					}else{
						session = SelfTopupService.gotoLevelOne(session, null);
						return session;
						
					}
					
				}else if(SystemConstants.SERVICE_OPTION_TOPUP_OTHER.equals(option)){
					
					if(session.numberOfAccounts() == 1){
						UserAccount account = session.getAppropriateUserAccounts().get(0);
						session.addUserInput(SystemConstants.SOURCE_ACCOUNT, account.getAccountNumber());
						session = TopupService.gotoLevelTwo(session,null);
						return session;
					}else{
						session = TopupService.gotoLevelOne(session, null);
						return session;
						
					}
					
				}else if(SystemConstants.SERVICE_OPTION_TRANSFER.equals(option)){
					
					if(session.numberOfAccounts() == 1){
						UserAccount account = session.getAppropriateUserAccounts().get(0);
						session.addUserInput(SystemConstants.SOURCE_ACCOUNT, account.getAccountNumber());
						session = TransferService.gotoLevelTwo(session,null);
						return session;
					}else{
						session = TransferService.gotoLevelOne(session, null);
						return session;
						
					}
				}else if(SystemConstants.SERVICE_OPTION_DEPOSIT.equals(option)){
					
				  //if(session.numberOfAccounts() == 1){
						UserAccount account = session.getEWalletUserAccount();
						session.addUserInput(SystemConstants.SOURCE_ACCOUNT, account.getAccountNumber());
						session = AgentCustomerDepositService.gotoLevelTwo(session,null);
						return session;
					/*}else{
						session = AgentCustomerDepositService.gotoLevelOne(session, null);
						return session;
						
					}*/
					
				}else if(SystemConstants.SERVICE_OPTION_NON_HOLDER_WITHDRAWAL.equals(option)){
					
					//if(session.numberOfAccounts() == 1){
						UserAccount account = session.getEWalletUserAccount();
						session.addUserInput(SystemConstants.SOURCE_ACCOUNT, account.getAccountNumber());
						session = AgentCustomerNonHolderWithdrawalService.gotoLevelTwo(session,null);
						return session;
					/*}else{
						session = AgentCustomerNonHolderWithdrawalService.gotoLevelOne(session, null);
						return session;
						
					}*/
					
				}else if(SystemConstants.SERVICE_OPTION_AGENT_HOLDER_WITHDRAWAL.equals(option)){
					
					//if(session.numberOfAccounts() == 1){
						UserAccount account = session.getEWalletUserAccount();
						session.addUserInput(SystemConstants.SOURCE_ACCOUNT, account.getAccountNumber());
						session = AgentCustomerHolderWithdrwalService.gotoLevelTwo(session,null);
						return session;
					/*}else{
						session = AgentCustomerHolderWithdrwalService.gotoLevelOne(session, null);
						return session;
						
					}*/
					
				}else if(SystemConstants.SERVICE_OPTION_AGENT_TRANSFER.equals(option)){
					
					//if(session.numberOfAccounts() == 1){
						UserAccount account = session.getEWalletUserAccount();
						session.addUserInput(SystemConstants.SOURCE_ACCOUNT, account.getAccountNumber());
						session = AgentTransferService.gotoLevelTwo(session,null);
						return session;
					/*}else{
						session = AgentTransferService.gotoLevelOne(session, null);
						return session;
						
					}*/
					
				}else if(SystemConstants.SERVICE_OPTION_AGENT_SUMMARY.equals(option)){
					
					//if(session.numberOfAccounts() == 1){
						UserAccount account = session.getEWalletUserAccount();
						session.addUserInput(SystemConstants.SOURCE_ACCOUNT, account.getAccountNumber());
						session = AgentSummaryService.gotoLevelTwo(session,null);
						return session;
					/*}else{
						session = AgentTransferService.gotoLevelOne(session, null);
						return session;
						
					}*/
					
				}  else if(SystemConstants.SERVICE_OPTION_REGISTER_MERCHANT.equals(option)){
					logger.debug(">>>>>>>>>>>>>>>>>>> In Register merchant");
					session = RegisterMerchantService.gotoViewAllMerchants(session);
					return session;
					
				}	else if(SystemConstants.SERVICE_OPTION_RTGS.equals(option)){
					
					if(session.numberOfAccounts() == 1){
						UserAccount account = session.getAppropriateUserAccounts().get(0);
						session.addUserInput(SystemConstants.SOURCE_ACCOUNT, account.getAccountNumber());
						session = RtgsService.gotoSelectBank(session);
						return session;
					}else{
						session = RtgsService.gotoLevelOne(session, null);
						return session;
						
					}
					
				}
				if(SystemConstants.BACK.equals(option)){
					String menu;
					if(isAgent) {
						menu = config.getStringValueOf(SystemConstants.SELECT_SERVICE_INVALID_AGENTS_OPTIONS_MSG);
						menu += config.getStringValueOf(SystemConstants.EXIT_MSG);
						session.generateOptionsMenu(config.getStringValueOf(SystemConstants.SERVICE_OPTIONS_AGENT));
						session.addExitToOptionsMenu();
						session.setServiceName(SystemConstants.SERVICE_NAME_SELECT_SERVICE);
					} else {
						menu = config.getStringValueOf(SystemConstants.SELECT_SERVICE_INVALID_OPTIONS_MSG);
						menu += config.getStringValueOf(SystemConstants.EXIT_MSG);
						session.generateOptionsMenu(config.getStringValueOf(SystemConstants.SERVICE_OPTIONS));
						session.addExitToOptionsMenu();
						session.setServiceName(SystemConstants.SERVICE_NAME_SELECT_SERVICE);
					}
					session.setUssdResponseString(menu);
					session.generateXml();
					return session;
				}
				if(SystemConstants.EXIT.equals(option)){
					session = super.terminateSession(session, null);
					return session;
				}
				
			}else{
				String menu;
				if(isAgent) {
					menu = config.getStringValueOf(SystemConstants.SELECT_SERVICE_INVALID_AGENTS_OPTIONS_MSG);
					menu += config.getStringValueOf(SystemConstants.EXIT_MSG);
					session.generateOptionsMenu(config.getStringValueOf(SystemConstants.SERVICE_OPTIONS_AGENT));
					session.addExitToOptionsMenu();
					session.setServiceName(SystemConstants.SERVICE_NAME_SELECT_SERVICE);
				} else {
					menu = config.getStringValueOf(SystemConstants.SELECT_SERVICE_INVALID_OPTIONS_MSG);
					menu += config.getStringValueOf(SystemConstants.EXIT_MSG);
					session.generateOptionsMenu(config.getStringValueOf(SystemConstants.SERVICE_OPTIONS));
					session.addExitToOptionsMenu();
					session.setServiceName(SystemConstants.SERVICE_NAME_SELECT_SERVICE);
				}
				session.setUssdResponseString(menu);
				session.generateXml();
				return session;
			}
		} catch (Exception e) {
			logger.debug(" Message : "+e.getMessage());
			return super.terminateSession(session, config.getStringValueOf(SystemConstants.SERVICE_ERROR));
		}
		
		return session;
	}
	
	
	
	public static USSDSession generateDefaultView(USSDSession session){
		// Initialize Config File
		config = getConfigInstance(session);
		
		String menu = config.getStringValueOf(SystemConstants.SELECT_SERVICE_TYPE_MSG);
	
		session.generateOptionsMenu(config.getStringValueOf(SystemConstants.SERVICE_OPTIONS));
		
		session.setServiceName(SystemConstants.SERVICE_NAME_SELECT_SERVICE);
		session.setLevel(0);
		if(session.hasEwalletAccounts() && session.hasBankAccounts()){
			//menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
			menu += config.getStringValueOf(SystemConstants.EXIT_MSG);
			session.addBackAndExitToOptionsMenu();
		}else{
			menu += config.getStringValueOf(SystemConstants.EXIT_MSG);
			session.addExitToOptionsMenu();
			if(session.hasEwalletAccounts()){
				session.setAccountType(AccountType.E_WALLET);
			}else if(session.hasBankAccounts()){
				session.setAccountType(AccountType.BANK_ACCOUNT);
			}
		}
		
		session.setUssdResponseString(menu);
		session.generateXml();
		return session;
	}
	
	public static USSDSession generateAgentDefaultView(USSDSession session){
		// Initialize Config File
		config = getConfigInstance(session);
		
		String menu = config.getStringValueOf(SystemConstants.SELECT_SERVICE_TYPE_AGENT_MSG);
	
		session.generateOptionsMenu(config.getStringValueOf(SystemConstants.SERVICE_OPTIONS_AGENT));
		
		session.setServiceName(SystemConstants.SERVICE_NAME_SELECT_SERVICE);
		session.setLevel(0);
		if(session.hasEwalletAccounts() && session.hasBankAccounts()){
			//menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
			menu += config.getStringValueOf(SystemConstants.EXIT_MSG);
			session.addBackAndExitToOptionsMenu();
		}else{
			menu += config.getStringValueOf(SystemConstants.EXIT_MSG);
			session.addExitToOptionsMenu();
			if(session.hasEwalletAccounts()){
				session.setAccountType(AccountType.E_WALLET);
			}else if(session.hasBankAccounts()){
				session.setAccountType(AccountType.BANK_ACCOUNT);
			}
		}
		
		session.setUssdResponseString(menu);
		session.generateXml();
		return session;
	}
	
	public static USSDSession confirmCriticalTxn(USSDSession session) {
		// Initialize Config File
		config = getConfigInstance(session);
		
		String menu = config.getStringValueOf(SystemConstants.ENTER_CONFIRMATION_PIN_MSG);
		menu += config.getStringValueOf(
		SystemConstants.BACK_EXIT_MSG);
		session.addBackAndExitToOptionsMenu();
		session.setLevel(SystemConstants.CRITICAL_LEVEL);
		session.setServiceName(session.getServiceName());
		session.setUssdResponseString(menu);
		session.generateXml();
		return session;		
		
	}
	
   /*public static USSDSession confirmPinChangeOldPin(USSDSession session) {
	// Initialize Config File
		config = getConfigInstance(session);
		
		String menu = config.getStringValueOf(SystemConstants.ENTER_OLD_PIN_CHANGE_PIN_MSG);
		menu += config.getStringValueOf(
		SystemConstants.BACK_EXIT_MSG);
		session.addBackAndExitToOptionsMenu();
		session.setLevel(SystemConstants.CRITICAL_LEVEL);
		session.setServiceName(session.getServiceName());
		session.setUssdResponseString(menu);
		session.generateXml();
		return session;		
		
	}*/

}
