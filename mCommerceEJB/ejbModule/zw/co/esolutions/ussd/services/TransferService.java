package zw.co.esolutions.ussd.services;

import java.util.Map;

import org.apache.log4j.Logger;

import zw.co.esolutions.ewallet.enums.MobileNetworkOperator;
import zw.co.esolutions.ewallet.enums.USSDTransactionType;
import zw.co.esolutions.ewallet.msg.USSDRequestMessage;
import zw.co.esolutions.ewallet.util.NumberUtil;
import zw.co.esolutions.mobile.banking.services.TransactionResponse;
import zw.co.esolutions.ussd.conf.USSDConfiguration;
import zw.co.esolutions.ussd.entities.USSDSession;
import zw.co.esolutions.ussd.util.LoggerFactory;
import zw.co.esolutions.ussd.util.NumberUtils;
import zw.co.esolutions.ussd.util.SystemConstants;
import zw.co.esolutions.ussd.web.services.proxy.UssdServiceProxy;

public class TransferService extends USSDService {

	static Logger logger = LoggerFactory.getLogger(TransferService.class);
	static USSDConfiguration config;
	
	@Override
	public USSDSession processUSSDRequest(USSDSession session,
			Map<String, String> attributesMap) {

		String response = attributesMap
				.get(SystemConstants.USSD_REQUEST_STRING);
		Map<String, String> optionsMap = session.getOptionsMenuMap();
		String option = optionsMap.get(response);
		// if user was selecting account
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
			
			if (session.getLevel() == 1) {

				if (SystemConstants.EXIT.equals(option)) {
					session = super.terminateSession(session, null);
					return session;
				} else if (SystemConstants.BACK.equals(option)) {
					session = SelectService.generateDefaultView(session);
					return session;
				} else if (option != null) {
					session.addUserInput(SystemConstants.SOURCE_ACCOUNT, option);
					session = gotoLevelTwo(session, null);
					return session;
				} else {
					session = gotoLevelOne(
							session,
							config
									.getStringValueOf(SystemConstants.SELECT_ACCOUNT_TRANSFER_INVALID_OPTION_MSG));
					return session;
				}

			} else if (session.getLevel() == 2) {
				// target Account
				if (SystemConstants.EXIT.equals(option)) {
					session = super.terminateSession(session, null);
					return session;
				} else if (SystemConstants.BACK.equals(option)) {
					session = SelectService.generateDefaultView(session);
					return session;
				} else if (response != null && !response.trim().equals("")) {

					// Format Target if mobile
					try {
						response = NumberUtil.formatMobileNumber(response);
					} catch (Exception e) {

					}
					
					if(session.getInput(SystemConstants.SOURCE_ACCOUNT).equalsIgnoreCase(response)) {
						session = gotoLevelTwo(session, config
								.getStringValueOf(SystemConstants.TRANSFER_INVALID_ACCOUNTS_ACCOUNT));
								return session;
					} else {
						session.addUserInput(SystemConstants.TARGET_ACCOUNT,
								response);
						session = gotoLevelThree(session, null);
						return session;
					}
				} else {
					session = gotoLevelTwo(
							session,
							config
									.getStringValueOf(SystemConstants.ENTER_TARGET_ACCOUNT_INVALID_OPTION_MSG));
					return session;
				}
			} else if (session.getLevel() == 3) {
				if (SystemConstants.EXIT.equals(option)) {
					session = super.terminateSession(session, null);
					return session;
				} else if (SystemConstants.BACK.equals(option)) {
					session = gotoLevelTwo(session, null);
					return session;
				} else if (response != null && !response.trim().equals("")) {
					boolean valid = NumberUtils.validateAmount(response.trim());
					if (valid) {
						// gotoConfirm
						session.addUserInput(SystemConstants.AMOUNT, response
								.trim());

						if (this.isNonHolderTxf(session)) {
							session = gotoLevelFourCode(session, null);
						} else {
							session = gotoConfirmationLevel(session, null);
						}
					} else {
						session = gotoLevelThree(
								session,
								config
										.getStringValueOf(SystemConstants.ENTER_AMOUNT_INVALID_OPTION_MSG));
					}
					return session;
				} else {
					session = gotoLevelThree(
							session,
							config
									.getStringValueOf(SystemConstants.ENTER_AMOUNT_INVALID_OPTION_MSG));
					return session;
				}
			} else if (session.getLevel() == 4) {
				if (SystemConstants.EXIT.equals(option)
						|| SystemConstants.CANCEL.equals(option)) {
					session = super.terminateSession(session, null);
					return session;
				} else if (SystemConstants.BACK.equals(option)) {
					session = gotoLevelThree(session, null);
					return session;
				} else if (response != null && !response.trim().equals("")) {

					if (SystemConstants.MAX_SECRET_CODE_LENGTH < response
							.length()) {
						session = gotoLevelFourCode(
								session,
								config
										.getStringValueOf(SystemConstants.INVALID_NON_HOLDER_CODE));
					} else {
						response = response.toUpperCase();
						session.addUserInput(SystemConstants.NON_HOLDER_CODE,
								response);
						session = gotoConfirmationLevel(session, null);
					}
					return session;
				} else {
					session = gotoLevelFourCode(
							session,
							config
									.getStringValueOf(SystemConstants.INVALID_OPTION_DEFAULT));
					return session;
				}
			} else if (session.getLevel() == 5) {
				if (SystemConstants.EXIT.equals(option)
						|| SystemConstants.CANCEL.equals(option)) {
					session = super.terminateSession(session, null);
					return session;
				} else if (SystemConstants.BACK.equals(option)) {

					if (!this.isNonHolderTxf(session)) {
						session = gotoLevelThree(session, null);
					} else {
						session = gotoLevelFourCode(session, null);
					}

					return session;
				} else if (SystemConstants.CONFIRM.equals(option)) {
					session = processTransaction(session);
					return session;
				} else {
					session = gotoConfirmationLevel(
							session,
							config
									.getStringValueOf(SystemConstants.INVALID_OPTION_DEFAULT));
					return session;
				}
			}
		} catch (Exception e) {
			logger.debug(" Message : " + e.getMessage());
			return super.terminateSession(session, config
					.getStringValueOf(SystemConstants.SERVICE_ERROR));
		}
		return null;
	}

	public static USSDSession gotoLevelOne(USSDSession session, String menu) {
		// Initialize Config File
		config = getConfigInstance(session);

		if (menu == null) {
			menu = config
					.getStringValueOf(SystemConstants.SELECT_ACCOUNT_TRANSFER_MSG);
		}
		session.generateAccountsOptionsMenu();
		menu += session.getAccountsMenu();
		menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
		session.setLevel(1);
		session.addBackAndExitToOptionsMenu();
		session.setServiceName(SystemConstants.SERVICE_NAME_TRANSFER);
		session.setUssdResponseString(menu);
		session.generateXml();
		return session;
	}

	public static USSDSession gotoLevelTwo(USSDSession session, String menu) {
		// Initialize Config File
		config = getConfigInstance(session);
		session.removeUserInput(SystemConstants.TARGET_ACCOUNT);
		if (menu == null) {
			menu = config
					.getStringValueOf(SystemConstants.ENTER_TARGET_ACCOUNT_MSG);
		}
		menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
		session.addBackAndExitToOptionsMenu();
		session.setLevel(2);
		session.setServiceName(SystemConstants.SERVICE_NAME_TRANSFER);
		session.setUssdResponseString(menu);
		session.generateXml();
		return session;
	}

	public static USSDSession gotoLevelThree(USSDSession session, String menu) {
		// Initialize Config File
		config = getConfigInstance(session);
		session.removeUserInput(SystemConstants.AMOUNT);
		session.removeUserInput(SystemConstants.NON_HOLDER_CODE);
		if (menu == null) {
			menu = config
					.getStringValueOf(SystemConstants.TRANSFER_ENTER_AMOUNT_MSG);
		}
		menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
		session.addBackAndExitToOptionsMenu();
		session.setLevel(3);
		session.setServiceName(SystemConstants.SERVICE_NAME_TRANSFER);
		session.setUssdResponseString(menu);
		session.generateXml();
		return session;
	}

	public static USSDSession gotoLevelFourCode(USSDSession session, String menu) {
		// Initialize Config File
		config = getConfigInstance(session);
		session.removeUserInput(SystemConstants.NON_HOLDER_CODE);
		if (menu == null) {
			menu = config
					.getStringValueOf(SystemConstants.TRANSFER_ENTER_NON_HOLDER_CODE);
		}
		menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
		session.addBackAndExitToOptionsMenu();
		session.setLevel(4);
		session.setServiceName(SystemConstants.SERVICE_NAME_TRANSFER);
		session.setUssdResponseString(menu);
		session.generateXml();
		return session;
	}

	public static USSDSession gotoConfirmationLevel(USSDSession session,
			String menu) {
		// Initialize Config File
		config = getConfigInstance(session);

		if (menu == null) {
			menu = config.getStringValueOf(SystemConstants.CONFIRM_TRANSFER);
		}

		menu += "\n Tranfer "
				+ NumberUtils.getFormattedAmount(session
						.getInput(SystemConstants.AMOUNT)) + " to "
				+ session.getInput(SystemConstants.TARGET_ACCOUNT) + " from "
				+ session.getInput(SystemConstants.SOURCE_ACCOUNT);
		String code = session.getInput(SystemConstants.NON_HOLDER_CODE);
		if (code != null) {
			menu += "\nCode : " + code + " ";
		}
		menu += config.getStringValueOf(SystemConstants.CONFIRM_OPTIONS);
		menu += config.getStringValueOf(SystemConstants.BACK_EXIT_MSG);
		session.addConfirmAndCancelToOptionsMenu();
		session.addBackAndExitToOptionsMenu();
		session.setLevel(5);
		session.setServiceName(SystemConstants.SERVICE_NAME_TRANSFER);
		session.setUssdResponseString(menu);
		session.generateXml();
		return session;
	}

	public static USSDSession processTransaction(USSDSession session) {
		// Initialize Config File
		config = getConfigInstance(session);

		USSDRequestMessage msg = new USSDRequestMessage();
		msg.setMno(MobileNetworkOperator.ECONET);
		msg.setSourceMobileNumber(session.getMobileNumber());
		msg.setSourceBankAccount(session
				.getInput(SystemConstants.SOURCE_ACCOUNT));
		msg.setTargetBankAccount(session
				.getInput(SystemConstants.TARGET_ACCOUNT));
		msg.setAmount(NumberUtils.getAmountInCents(session
				.getInput(SystemConstants.AMOUNT)));
		msg.setTransactionType(USSDTransactionType.TRANSFER);
		msg.setSecretCode(session.getInput(SystemConstants.NON_HOLDER_CODE));
		msg.setSourceBankId(session.getBankCode());
		msg.setTargetBankId(getTargetBankCodeForTargetAccount(session));
		logger.debug(">>>>>>>>>>>>>>>>>>> Target Bank Code = "+msg.getTargetBankId());
		msg.setUuid(session.getSessionId());
		
		TransactionResponse transactionResponse = sendMessage(msg);
		return showFinalMenu(session, transactionResponse);
	}

	private boolean isNonHolderTxf(USSDSession session) {
		boolean nonHolder = false;
		String targetAccount = session.getInput(SystemConstants.TARGET_ACCOUNT);
		logger.debug(">>>>>>>>>>>>>> Target Bank Acc = " + targetAccount);
		try {
			targetAccount = NumberUtil.formatMobileNumber(targetAccount);
			nonHolder =  UssdServiceProxy.getInstance().isNonHolderAccount(targetAccount, session.getBankCode());
		} catch (Exception e) {
			nonHolder = false;
		}
		
		return nonHolder;
	}
	
	

}
