package zw.co.esolutions.mcommerce.centralswitch.processes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.centralswitch.config.service.ConfigInfo;
import zw.co.esolutions.centralswitch.config.service.SwitchConfigurationServiceSOAPProxy;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.bankservices.service.BankStatus;
import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.enums.MobileNetworkOperator;
import zw.co.esolutions.ewallet.enums.ResponseType;
import zw.co.esolutions.ewallet.enums.TransactionLocationType;
import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.merchantservices.service.BankMerchant;
import zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchant;
import zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchantStatus;
import zw.co.esolutions.ewallet.merchantservices.service.Merchant;
import zw.co.esolutions.ewallet.merchantservices.service.MerchantServiceSOAPProxy;
import zw.co.esolutions.ewallet.msg.NotificationInfo;
import zw.co.esolutions.ewallet.msg.RequestInfo;
import zw.co.esolutions.ewallet.msg.ResponseInfo;
import zw.co.esolutions.ewallet.services.proxy.MobileCommerceProxy;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.EncryptAndDecrypt;
import zw.co.esolutions.ewallet.util.GenerateKey;
import zw.co.esolutions.ewallet.util.MoneyUtil;
import zw.co.esolutions.ewallet.util.SystemConstants;
import zw.co.esolutions.mcommerce.centralswitch.model.MessageTransaction;
import zw.co.esolutions.mcommerce.centralswitch.model.TransactionState;
import zw.co.esolutions.mobile.web.utils.WebStatus;
import zw.co.esolutions.ussd.web.services.FlowStatus;
import zw.co.esolutions.ussd.web.services.MobileCommerceServiceSOAPProxy;
import zw.co.esolutions.ussd.web.services.UssdTransaction;
import zw.co.esolutions.ussd.web.services.WebSession;

/**
 * Session Bean implementation class ProcessBankResponseImpl
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class ProcessBankResponseImpl implements ProcessBankResponse {

	@PersistenceContext(unitName = "CentralSwitchEJB")
	private EntityManager em;

	/**
	 * Default constructor.
	 */
	public ProcessBankResponseImpl() {
		// TODO Auto-generated constructor stub
	}

	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/switch.log.properties");
			LOG = Logger.getLogger(ProcessBankResponseImpl.class);
		} catch (Exception e) {
		}
	}

	@Override
	public List<NotificationInfo> process(ResponseInfo responseInfo) throws Exception {

		LOG.debug("In Process Message .... responseType : " + responseInfo.getResponseType() + ":::::::: Txn Type" + responseInfo.getRequestInfo().getTransactionType());
		RequestInfo requestInfo = responseInfo.getRequestInfo();

		if (requestInfo == null) {
			throw new Exception("Invalid response info, the bank request is not set.");
		}

		if (requestInfo.getProfileId() != null && ResponseType.REVERSAL_RESPONSE.toString().equalsIgnoreCase(requestInfo.getProfileId())) {
			return this.processTransactionReversalMessage(responseInfo);

		}
		if (TransactionType.ALERT.equals(requestInfo.getTransactionType())) {
			LOG.debug("Processing Transaction Alert...");
			return this.processTransactionAlert(responseInfo);
		}

		String sourceCustomerName = this.getSourceCustomerName(requestInfo);

		if (TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER.equals(requestInfo.getTransactionType())) {
			LOG.debug("Processing BA to BA Transfer Request...");
			return this.processBankAccountToBankAccountAdvice(responseInfo);
		}

		if (TransactionType.EWALLET_TOPUP.equals(requestInfo.getTransactionType()) || TransactionType.TOPUP.equals(requestInfo.getTransactionType())) {
			LOG.debug("Processing Transaction Alert...");
			return this.processTopupAdvice(responseInfo);
		}

		if (TransactionType.BILLPAY.equals(requestInfo.getTransactionType()) || TransactionType.EWALLET_BILLPAY.equals(requestInfo.getTransactionType())) {
			LOG.debug("Processing Transaction Alert...");
			return this.processBillPayAdvice(responseInfo);
		}

		if (TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER.equals(requestInfo.getTransactionType())) {
			return this.processBankAccountToEWalletTransferAdvice(responseInfo, sourceCustomerName);

		} else if (TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER.equals(requestInfo.getTransactionType())) {

			return this.processEwalletToBankAccountTransferAdvice(responseInfo);

		} else if (TransactionType.EWALLET_TO_EWALLET_TRANSFER.equals(requestInfo.getTransactionType())) {

			return this.processEWalletToEWalletTransferAdvice(responseInfo, sourceCustomerName);

		} else if (TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER.equals(requestInfo.getTransactionType())) {

			return this.processBankAccountToNonHolderTransfer(responseInfo, sourceCustomerName);

		} else if (TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER.equals(requestInfo.getTransactionType())) {

			return this.processEwalletToNonHolderTransfer(responseInfo, sourceCustomerName);

		} else if (TransactionType.DEPOSIT.equals(requestInfo.getTransactionType())) {

			return this.processEWalletDepositAdvice(responseInfo);

		} else if (TransactionType.AGENT_CASH_DEPOSIT.equals(requestInfo.getTransactionType())) {

			return this.processEWalletAgentDepositAdvice(responseInfo);

		} else if (TransactionType.WITHDRAWAL.equals(requestInfo.getTransactionType())) {

			return this.processEWalletWithdrawalAdvice(responseInfo);

		} else if (TransactionType.WITHDRAWAL_NONHOLDER.equals(requestInfo.getTransactionType())) {

			return this.processNonHolderWithdrawal(responseInfo);

		} else if (TransactionType.BALANCE.equals(requestInfo.getTransactionType())) {

			return this.processBalanceAdvice(responseInfo);

		} else if (TransactionType.MINI_STATEMENT.equals(requestInfo.getTransactionType())) {

			return this.processMiniStatementAdvice(responseInfo);

		} else if (TransactionType.MERCHANT_REG.equals(requestInfo.getTransactionType())) {

			return this.processMerchantRegistrationAdvice(responseInfo);

		} else if (TransactionType.CUSTOMER_ACTIVATION.equals(requestInfo.getTransactionType())) {

			return this.processCustomerActivationAdvice(responseInfo);

		} else if (TransactionType.CHANGE_PASSCODE.equals(requestInfo.getTransactionType())) {
			LOG.debug("CHANGE PASSCODE ADVICE RECEIVED");
			
			return this.processPinChangeAdvice(responseInfo);
		} else if (TransactionType.DEPOSIT.equals(requestInfo.getTransactionType())) {

			return this.processEWalletDepositAdvice(responseInfo);

		} else if (TransactionType.PASSCODE.equals(requestInfo.getTransactionType())) {

			return this.processMobileProfileLockAdvice(responseInfo);

		} else if (TransactionType.AGENT_CUSTOMER_DEPOSIT.equals(requestInfo.getTransactionType())) {

			return this.processAgentDepositAdvice(responseInfo);

		} else if (TransactionType.AGENT_CUSTOMER_WITHDRAWAL.equals(requestInfo.getTransactionType())) {

			return this.processAgentWithdrawalAdvice(responseInfo);

		} else if (TransactionType.AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL.equals(requestInfo.getTransactionType())) {

			return this.processAgentNonHolderWithdrawalAdvice(responseInfo);

		} else if (TransactionType.AGENT_EWALLET_TO_BANKACCOUNT_TRANSFER.equals(requestInfo.getTransactionType())) {

			return this.processAgentEwalletToBankAccountTransferAdvice(responseInfo);

		} else if (TransactionType.AGENT_SUMMARY.equals(requestInfo.getTransactionType())) {

			return this.processAgentTransactionSummaryAdvice(responseInfo);

		} else if (TransactionType.IB_BULK_TO_EWALLET_TRANSFER.equals(requestInfo.getTransactionType())) {

			return this.processIBBulkToEWalletTransferAdvice(responseInfo);

		} else if (TransactionType.IB_BULK_TO_NON_HOLDER_TRANSFER.equals(requestInfo.getTransactionType())) {

			return this.processIBBulkToNonHolderTransferAdvice(responseInfo);

		} else if (TransactionType.SECRET_CODE_RESET.equals(requestInfo.getTransactionType())) {

			return this.processNonHolderSecretCodeResetAdvice(responseInfo);

		} else if (TransactionType.RTGS.equals(requestInfo.getTransactionType())) {

			return this.processRTGSAdvice(responseInfo);

		}

		return null;
	}

	@Override
	public List<NotificationInfo> processPinChangeAdvice(ResponseInfo responseInfo) throws Exception {
		LOG.debug("PROCESSING PIN CHANGE ADVICE...");
		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();
		RequestInfo requestInfo = responseInfo.getRequestInfo();
		// look for the txn
		MessageTransaction txn = em.find(MessageTransaction.class, requestInfo.getMessageId());
		// update it to the new status
		if (txn == null) {
			throw new Exception("Original Transaction with reference [" + requestInfo.getMessageId() + "] was not found.");
		}
		String message;
		LOG.debug("Found the PIN Change Request Match...");
		// promote the txn state according to response.
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		MobileProfile profile = customerService.getMobileProfileByBankAndMobileNumber(requestInfo.getSourceBankId(), requestInfo.getSourceMobile());
		String encCode = EncryptAndDecrypt.encrypt(requestInfo.getNewPin(), requestInfo.getSourceMobile());
		profile.setSecretCode(encCode);
		
		
		//This is done to clear First Pin Change
		if(profile.getMobileProfileEditBranch() != null && 	EWalletConstants.CHANGE_PIN.equalsIgnoreCase(profile.getMobileProfileEditBranch())) {
			profile.setMobileProfileEditBranch("");
		}
		profile = customerService.updateMobileProfile(profile, EWalletConstants.SYSTEM);
		
		txn = this.promoteTxnState(txn, TransactionStatus.COMPLETED);
		LOG.debug("Promoted.......");
		txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
		message = "Your PIN CHANGE Request was successful. Your new ZB e-Wallet PIN is " + requestInfo.getNewPin() + " . ZB e-Wallet - Powered by e-Solutions";
		if(TransactionLocationType.MOBILE_WEB.equals(txn.getTransactionLocationType())) {
			LOG.debug("It's MOBILE_WEB Txn ......");
			MobileCommerceServiceSOAPProxy mobileService = MobileCommerceProxy.getInstance();
			WebSession session = mobileService.getWebSessionByReferenceId(txn.getUuid());
			session.setMessage(message);
			session.setStatus(WebStatus.COMLETED.toString());
			session = mobileService.updateWebSession(session);
			LOG.debug("Promoted MOBILE WEB.......");
		}
		txn = this.updateProcessTransaction(txn);
		LOG.debug("The message : " + message);
		// notify originator.
		notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, null, EWalletConstants.ECONET_SMS_OUT_QUEUE));
		LOG.debug("Returning count of notifications  : " + notificationInfos.size());
		return notificationInfos;
	}
	
	private List<NotificationInfo> processNonHolderSecretCodeResetAdvice(ResponseInfo responseInfo) {

		LOG.debug("PROCESSING NONHOLDER SECRET CODE RESET ADVICE ...");
		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();

		RequestInfo requestInfo = responseInfo.getRequestInfo();

		LOG.info("Generating response message for nonholder secret code reset...");
		// promote the txn state according to response.
		if (ResponseCode.E000.equals(responseInfo.getResponseCode())) {
			// Notify originating customer.
			String message = "Secret code for transaction " + requestInfo.getOldReference() + " has been changed to " + requestInfo.getSecretCode() + ". ZB e-Wallet - Powered by e-Solutions";
			notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, requestInfo.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
			LOG.debug("Notifying customer on secret code reset.");
		} else {
			LOG.debug("For some reason code reset has failed.");
			String message = responseInfo.getNarrative();
			notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, requestInfo.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));

		}
		LOG.debug("Returning count of notifications for code reset : " + notificationInfos.size());
		return notificationInfos;
	}

	private String getSourceCustomerName(RequestInfo requestInfo) {
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		MobileProfile origin = customerService.findMobileProfileById(requestInfo.getSourceMobileProfileId());
		if (origin == null || origin.getId() == null) {
			return null;
		}
		if (SystemConstants.AUTOREG_NAME.equalsIgnoreCase(origin.getCustomer().getFirstNames()) || SystemConstants.AUTOREG_NAME.equalsIgnoreCase(origin.getCustomer().getLastName())) {
			return null;
		}
		String firstName = origin.getCustomer().getFirstNames();
		if (firstName == null || "".equals(firstName.trim())) {
			firstName = "";
		} else {
			firstName = firstName.substring(0, 1);
		}
		String customerName = firstName + " " + origin.getCustomer().getLastName();
		if ("".equalsIgnoreCase(customerName.trim())) {
			return null;
		}
		return customerName.trim().toUpperCase();
	}

	private List<NotificationInfo> processNonHolderWithdrawal(ResponseInfo responseInfo) {
		LOG.debug("Handling the Non-Holder WDL..." + responseInfo.getNarrative());
		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();
		RequestInfo requestInfo = responseInfo.getRequestInfo();

		String message;
		// promote the txn state according to response.
		if (ResponseCode.E000.equals(responseInfo.getResponseCode())) {
			// notify sender
			LOG.debug("Sender mobile " + requestInfo.getSourceMobile());
			message = "Your transfer of " + MoneyUtil.convertCentsToDollarsPatternNoCurrency(requestInfo.getAmount()) + " to " + requestInfo.getTargetMobile() + " has been collected" + ". Ref : " + requestInfo.getMessageId() + ". ZB e-Wallet - Powered by e-Solutions";
			notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, MobileNetworkOperator.ECONET, EWalletConstants.ECONET_SMS_OUT_QUEUE));
			// notify beneficiary
			LOG.debug("Beneficiary mobile " + requestInfo.getTargetMobile());
			String beneficiaryMessage = "The " + MoneyUtil.convertCentsToDollarsPatternNoCurrency(requestInfo.getAmount()) + " you received from " + requestInfo.getSourceMobile() + " has been collected" + ". Ref : " + requestInfo.getMessageId() + ". ZB e-Wallet - Powered by e-Solutions";
			notificationInfos.add(new NotificationInfo(requestInfo.getTargetMobile(), beneficiaryMessage, MobileNetworkOperator.ECONET, EWalletConstants.ECONET_SMS_OUT_QUEUE));
			LOG.debug("Done creating notifications");
		} else {
			LOG.debug("This status is unbearable");
		}
		// notify beneficiary
		LOG.debug("Zvafaya");
		return notificationInfos;

	}

	private List<NotificationInfo> processMobileProfileLockAdvice(ResponseInfo responseInfo) throws Exception {

		LOG.debug("PROCESSING MOBILE PROFILE LOCK ADVICE ...");
		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();

		RequestInfo requestInfo = responseInfo.getRequestInfo();

		LOG.info("Generating response message for mobile profile lock...");
		// promote the txn state according to response.
		if (ResponseCode.E000.equals(responseInfo.getResponseCode())) {
			// Notify originating customer.
			String message = responseInfo.getNarrative() + ". ZB e-Wallet - Powered by e-Solutions";
			notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, requestInfo.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
			LOG.debug("Notifying customer on locked profile.");
		} else {
			LOG.debug("For some reason profile locking has failed.");
		}
		LOG.debug("Returning count of notifications for ewallet to ewallet : " + notificationInfos.size());
		return notificationInfos;
	}

	private List<NotificationInfo> processBillPayAdvice(ResponseInfo responseInfo) throws Exception {
		LOG.debug("PROCESSING BILLPAY ADVICE ...");
		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();

		RequestInfo requestInfo = responseInfo.getRequestInfo();
		// look for the txn
		MessageTransaction txn = em.find(MessageTransaction.class, requestInfo.getMessageId());
		// update it to the new status
		if (txn == null) {
			throw new Exception("Original Transaction with reference [" + requestInfo.getMessageId() + "] was not found.");
		}
		LOG.debug("Done running finder......" + txn);
		LOG.info("Generating response message for billpay...");
		// promote the txn state according to response.

		if (ResponseCode.E000.equals(responseInfo.getResponseCode())) {
			txn = this.promoteTxnState(txn, TransactionStatus.COMPLETED);
			txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
			txn.setNarrative(responseInfo.getNarrative());
			txn.setCustomerUtilityAccount(requestInfo.getCustomerUtilityAccount());
			// txn = this.updateProcessTransaction(txn);
			String message = "Your " + txn.getUtilityName() + " payment has been accepted.";
			message += "[nl]Amnt:" + MoneyUtil.convertCentsToDollarsPattern(requestInfo.getAmount());
			message += "[nl]Account:" + txn.getCustomerUtilityAccount();
			
			if (EWalletConstants.MERCHANT_NAME_DSTV.equals(txn.getUtilityName())) {
				message += "[nl]Bouquet:" + txn.getBouquetName();
				message += "[nl]Months:" + txn.getNumberOfMonths();
			} else {
				message += "[nl]From:" + txn.getSourceBankAccount();
				message += "[nl]State:SUCCESSFUL";
			}

			message += "[nl]REF:" + txn.getUuid();
			message += "[nl]ZB e-Wallet - Powered by e-Solutions";

			// handle sending messages to ussd and web
			if (this.isSendMessageToNonSmsClient(txn, message)) {
				return null;
			}

			// also notify beneficiary if not the same.
			notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));

		} else {
			txn = this.promoteTxnState(txn, TransactionStatus.FAILED);
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			txn.setNarrative(responseInfo.getNarrative());
			// txn = this.updateProcessTransaction(txn);
			String message = "Your " + txn.getUtilityName() + " bill payment of " + MoneyUtil.convertCentsToDollarsPattern(requestInfo.getAmount()) + " to " + requestInfo.getCustomerUtilityAccount() + " failed. Reason : " + responseInfo.getNarrative() + ". ZB e-Wallet";

			// handle sending messages to ussd and web
			if (this.isSendMessageToNonSmsClient(txn, message)) {
				return null;
			}

			// notify originator.
			notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		}
		LOG.debug("Returning count of notifications for ewallet to ewallet : " + notificationInfos.size());
		return notificationInfos;

	}

	private List<NotificationInfo> processTopupAdvice(ResponseInfo responseInfo) throws Exception {

		LOG.debug("PROCESSING TOPUP ADVICE ...");
		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();

		RequestInfo requestInfo = responseInfo.getRequestInfo();
		// look for the txn
		MessageTransaction txn = em.find(MessageTransaction.class, requestInfo.getMessageId());
		// update it to the new status
		if (txn == null) {
			throw new Exception("Original Transaction with reference [" + requestInfo.getMessageId() + "] was not found.");
		}
		LOG.debug("Done running finder......" + txn);
		// promote the txn state according to response.
		if (ResponseCode.E000.equals(responseInfo.getResponseCode())) {
			txn = this.promoteTxnState(txn, TransactionStatus.COMPLETED);
			txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
			txn.setNarrative(responseInfo.getNarrative());
			txn = this.updateProcessTransaction(txn);

			if (requestInfo.getSourceMobile().equalsIgnoreCase(requestInfo.getTargetMobile())) {
				// Topup Self, notify self
				String message = MoneyUtil.convertCentsToDollarsPattern(requestInfo.getAmount()) + " Recharge Accepted. Your airtime balance is now: " + MoneyUtil.convertCentsToDollarsPattern(responseInfo.getMerchantAccBalance()) + ". Your account balance is now: " + MoneyUtil.convertCentsToDollarsPattern(responseInfo.getAvailableBalance()) + ". ZB e-Wallet - Powered By e-Solutions -";
				// handle sending messages to ussd and web
				if (this.isSendMessageToNonSmsClient(txn, message)) {
					return null;
				}
				// also notify beneficiary if not the same.
				notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
			} else {
				// topup third party
				LOG.info("Generating response message for topup [another phone]...");
				String message = MoneyUtil.convertCentsToDollarsPattern(requestInfo.getAmount()) + " Recharge for " + requestInfo.getTargetMobile() + " Accepted. Your account balance is now: " + MoneyUtil.convertCentsToDollarsPattern(responseInfo.getAvailableBalance()) + ". ZB e-Wallet - Powered By e-Solutions -";
				// handle sending messages to ussd and web

				if (!this.isSendMessageToNonSmsClient(txn, message)) {
					notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
				}
				// notify beneficiary
				String beneficiaryMessage = MoneyUtil.convertCentsToDollarsPattern(requestInfo.getAmount()) + " Recharge from " + requestInfo.getSourceMobile() + " Accepted. Your airtime balance is now " + MoneyUtil.convertCentsToDollarsPattern(responseInfo.getMerchantAccBalance()) + ". ZB e-Wallet - Powered By e-Solutions -";
				notificationInfos.add(new NotificationInfo(requestInfo.getTargetMobile(), beneficiaryMessage, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));

			}
		} else {
			txn = this.promoteTxnState(txn, TransactionStatus.FAILED);
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			txn.setNarrative(responseInfo.getNarrative());

			String message = "Your recharge of " + MoneyUtil.convertCentsToDollarsPattern(requestInfo.getAmount()) + " to " + requestInfo.getTargetMobile() + " failed. Reason : " + responseInfo.getNarrative() + ". ZB e-Wallet";
			// notify originator.
			if (this.isSendMessageToNonSmsClient(txn, message)) {
				return null;
			}
			notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		}
		LOG.debug("Returning count of notifications for ewallet to ewallet : " + notificationInfos.size());
		return notificationInfos;

	}

	private List<NotificationInfo> processTransactionAlert(ResponseInfo responseInfo) {
		LOG.debug("PROCESSING TRANSACTION ALERT ADVICE ...");
		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();
		LOG.debug("getting request info ...");
		RequestInfo requestInfo = responseInfo.getRequestInfo();
		LOG.debug("getting MNO from mobile number  ..." + requestInfo.getSourceMobile());
		MobileNetworkOperator mno = getMNO(requestInfo.getSourceMobile());
		if (mno == null) {
			return null;
		}
		LOG.debug("Done setting MNO ..." + mno.name());
		requestInfo.setMno(mno);
		// notify.
		notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), responseInfo.getNarrative(), requestInfo.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		LOG.debug("Returning count of notifications for transaction alert : " + notificationInfos.size());
		return notificationInfos;
	}

	private MobileNetworkOperator getMNO(String mobileNumber) {
		LOG.debug("In getMNO... mobileNumber = " + mobileNumber);
		if (mobileNumber == null) {
			LOG.debug("mobile number is null.. return");
			return null;
		}
		if (mobileNumber.startsWith("26373")) {
			return MobileNetworkOperator.TELECEL;
		} else if (mobileNumber.startsWith("26371")) {
			return MobileNetworkOperator.NETONE;
		} else if (mobileNumber.startsWith("26377")) {
			return MobileNetworkOperator.ECONET;
		} else {
			return null;
		}
	}

	private List<NotificationInfo> processEWalletWithdrawalAdvice(ResponseInfo responseInfo) {

		LOG.debug("PROCESSING EWALLET WITHDRAWAL ADVICE ...");
		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();

		RequestInfo requestInfo = responseInfo.getRequestInfo();
		// promote the txn state according to response.
		String response = "Your " + MoneyUtil.convertCentsToDollarsPattern(requestInfo.getAmount()) + " withdrawal from ewallet account " + requestInfo.getSourceMobile() + " was successful. " + "Your new EWallet Balance is " + MoneyUtil.convertCentsToDollarsPattern(responseInfo.getAvailableBalance()) + ". ZB e-Wallet - Powered by e-Solutions";

		// notify originator.
		notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), response, requestInfo.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		LOG.debug("Returning count of notifications for ewallet to ewallet : " + notificationInfos.size());
		return notificationInfos;
	}

	private List<NotificationInfo> processCustomerActivationAdvice(ResponseInfo responseInfo) throws Exception {
		LOG.debug("PROCESSING CUSTOMER ACTIVATION ADVICE ...");
		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();
		RequestInfo requestInfo = responseInfo.getRequestInfo();
		String advice = responseInfo.getNarrative() + " ZB e-Wallet - Powered by e-Solutions.";
		notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), advice, requestInfo.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		LOG.debug("Returning count of notifications for ewallet to ewallet : " + notificationInfos.size());
		return notificationInfos;
	}

	private List<NotificationInfo> processMerchantRegistrationAdvice(ResponseInfo responseInfo) throws Exception {
		LOG.debug("PROCESSING CUSTOMER UTILITY ACCOUNT REG ADVICE ...");
		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();

		RequestInfo requestInfo = responseInfo.getRequestInfo();
		// look for the txn
		MessageTransaction txn = em.find(MessageTransaction.class, requestInfo.getMessageId());
		// update it to the new status
		if (txn == null) {
			throw new Exception("Original Transaction with reference [" + requestInfo.getMessageId() + "] was not found.");
		}
		// promote the txn state according to response.
		if (ResponseCode.E000.equals(responseInfo.getResponseCode())) {
			txn = this.promoteTxnState(txn, TransactionStatus.COMPLETED);
			txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
			txn.setNarrative(responseInfo.getNarrative());
			this.processCustomerMerchatRegistration(txn);
			String message = responseInfo.getNarrative();
			// handle sending messages to ussd and web
			if (this.isSendMessageToNonSmsClient(txn, message)) {
				return null;
			}
			// notify originator.
			notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), responseInfo.getNarrative() + ". ZB e-Wallet - Powered by e-Solutions", txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		} else {
			txn = this.promoteTxnState(txn, TransactionStatus.FAILED);
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			txn.setNarrative(responseInfo.getNarrative());
			String message = "Request failed. Reason : " + responseInfo.getNarrative() + ". ZB e-Wallet";

			// handle sending messages to ussd and web
			if (this.isSendMessageToNonSmsClient(txn, message)) {
				return null;
			}
			// notify originator.
			notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		}
		LOG.debug("Returning count of notifications for mini statement : " + notificationInfos.size());
		return notificationInfos;

	}

	private List<NotificationInfo> processMiniStatementAdvice(ResponseInfo responseInfo) throws Exception {

		LOG.debug("PROCESSING MINI STATEMENT ADVICE ...");
		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();

		RequestInfo requestInfo = responseInfo.getRequestInfo();
		// look for the txn
		MessageTransaction txn = em.find(MessageTransaction.class, requestInfo.getMessageId());
		// update it to the new status
		if (txn == null) {
			throw new Exception("Original Transaction with reference [" + requestInfo.getMessageId() + "] was not found.");
		}
		// promote the txn state according to response.
		if (ResponseCode.E000.equals(responseInfo.getResponseCode())) {
			txn = this.promoteTxnState(txn, TransactionStatus.COMPLETED);
			txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
			txn.setNarrative(responseInfo.getNarrative());
			String message = responseInfo.getNarrative();
			// handle sending messages to ussd and web
			if (this.isSendMessageToNonSmsClient(txn, message)) {
				return null;
			}
			// notify originator.
			notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), responseInfo.getNarrative() + ". ZB e-Wallet - Powered by e-Solutions", txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		} else {
			txn = this.promoteTxnState(txn, TransactionStatus.FAILED);
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			txn.setNarrative(responseInfo.getNarrative());
			String message = "Mini Statement Request failed. Reason : " + responseInfo.getNarrative() + ". ZB e-Wallet";
			// handle sending messages to ussd and web
			if (this.isSendMessageToNonSmsClient(txn, message)) {
				return null;
			}
			// notify originator.
			notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		}
		LOG.debug("Returning count of notifications for mini statement : " + notificationInfos.size());
		return notificationInfos;

	}

	private List<NotificationInfo> processBalanceAdvice(ResponseInfo responseInfo) throws Exception {
		LOG.debug("PROCESSING BANK ACCOUNT BALANCE ADVICE ...");
		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();

		RequestInfo requestInfo = responseInfo.getRequestInfo();
		// look for the txn
		MessageTransaction txn = em.find(MessageTransaction.class, requestInfo.getMessageId());
		// update it to the new status
		if (txn == null) {
			throw new Exception("Original Transaction with reference [" + requestInfo.getMessageId() + "] was not found.");
		}
		// promote the txn state according to response.
		if (ResponseCode.E000.equals(responseInfo.getResponseCode())) {
			txn = this.promoteTxnState(txn, TransactionStatus.COMPLETED);
			txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
			txn.setNarrative(responseInfo.getNarrative());
			String response;
			if (txn.getSourceBankAccount().equalsIgnoreCase(txn.getSourceMobileNumber())) {
				response = "Your ZB EWallet Balance is " + MoneyUtil.convertCentsToDollarsPattern(responseInfo.getAvailableBalance()) + ". ZB e-Wallet - Powered by e-Solutions";
			} else {
				response = "Your ZB Bank Account Available Balance is " + MoneyUtil.convertCentsToDollarsPattern(responseInfo.getAvailableBalance()) + " and Ledger Balance is " + MoneyUtil.convertCentsToDollarsPattern(responseInfo.getLedgerBalance()) + ". ZB e-Wallet - Powered by e-Solutions";
			}
			String message = response;
			// handle sending messages to ussd and web
			if (this.isSendMessageToNonSmsClient(txn, message)) {
				return null;
			}

			notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), response, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));

		} else {
			txn = this.promoteTxnState(txn, TransactionStatus.FAILED);
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			txn.setNarrative(responseInfo.getNarrative());
			String message = "Balance request failed. Reason : " + responseInfo.getNarrative() + ". ZB e-Wallet";

			// handle sending messages to ussd and web
			if (this.isSendMessageToNonSmsClient(txn, message)) {
				return null;
			}

			notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));

		}
		LOG.debug("Returning count of notifications for ewallet to ewallet : " + notificationInfos.size());
		return notificationInfos;
	}

	private List<NotificationInfo> processEwalletToBankAccountTransferAdvice(ResponseInfo responseInfo) throws Exception {
		LOG.debug("PROCESSING EWALLET TO BANK ACCOUNT TRANSFER ADVICE ...");
		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();

		RequestInfo requestInfo = responseInfo.getRequestInfo();
		// look for the txn
		MessageTransaction txn = em.find(MessageTransaction.class, requestInfo.getMessageId());
		// update it to the new status
		if (txn == null) {
			throw new Exception("Original Transaction with reference [" + requestInfo.getMessageId() + "] was not found.");
		}
		LOG.debug("Done running finder......" + txn);
		// promote the txn state according to response.
		if (ResponseCode.E000.equals(responseInfo.getResponseCode())) {
			txn = this.promoteTxnState(txn, TransactionStatus.COMPLETED);
			txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
			txn.setNarrative(responseInfo.getNarrative());
			String message = "Your transfer of " + MoneyUtil.convertCentsToDollarsPattern(txn.getAmount()) + " from ewallet account " + requestInfo.getSourceMobile() + " to " + requestInfo.getDestinationAccountNumber() + " was successful. Your new EWallet balance is " + MoneyUtil.convertCentsToDollarsPattern(responseInfo.getAvailableBalance()) + ". " + EWalletConstants.REF_NARRATIVE + responseInfo.getMessageId() + ". ZB e-Wallet - Powered by e-Solutions. ";

			// handle sending messages to ussd and web
			if (this.isSendMessageToNonSmsClient(txn, message)) {
				return null;
			}
			// notify originator.
			notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		} else {
			txn = this.promoteTxnState(txn, TransactionStatus.FAILED);
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			txn.setNarrative(responseInfo.getNarrative());
			String message = "Your transfer to " + requestInfo.getDestinationAccountNumber() + " failed. Reason : " + responseInfo.getNarrative() + ". ZB e-Wallet";

			// handle sending messages to ussd and web
			if (this.isSendMessageToNonSmsClient(txn, message)) {
				return null;
			}
			// notify originator.
			notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		}
		LOG.debug("Returning count of notifications for ewallet to bankaccount : " + notificationInfos.size());
		return notificationInfos;
	}

	private List<NotificationInfo> processBankAccountToEWalletTransferAdvice(ResponseInfo responseInfo, String srcCustomerName) throws Exception {
		LOG.debug("PROCESSING BANK ACCOUNT TO EWALLET TRANSFER ADVICE ...");

		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();

		RequestInfo requestInfo = responseInfo.getRequestInfo();
		// look for the txn
		MessageTransaction txn = em.find(MessageTransaction.class, requestInfo.getMessageId());
		// update it to the new status
		if (txn == null) {
			throw new Exception("Original Transaction with reference [" + requestInfo.getMessageId() + "] was not found.");
		}
		LOG.debug("Done running finder......" + txn);
		// promote the txn state according to response.
		if (ResponseCode.E000.equals(responseInfo.getResponseCode())) {
			txn = this.promoteTxnState(txn, TransactionStatus.COMPLETED);
			txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
			txn.setNarrative(responseInfo.getNarrative());
			String message = "Money SENT[nl] TO: "+ requestInfo.getTargetMobile() + "[nl]FROM: " + requestInfo.getSourceAccountNumber() +" [nl]AMT : " + MoneyUtil.convertCentsToDollarsPattern(txn.getAmount()) + "[nl]Status: Successful[nl]REF: " +responseInfo.getMessageId() + "[nl]Your new balance is " + MoneyUtil.convertCentsToDollarsPattern(responseInfo.getAvailableBalance()) + ". ZB e-Wallet - Powered by e-Solutions";

			String name = srcCustomerName == null ? requestInfo.getSourceMobile() : srcCustomerName;
			LOG.debug("Customer Name : " + name);
			String beneficiaryMessage = "CASH received in e-Wallet[nl]From : " + name + "[nl]" + "AMT : " + MoneyUtil.convertCentsToDollarsPattern(requestInfo.getAmount()) + "[nl]Ref : " + requestInfo.getMessageId() + ". ZB e-Wallet - Powered by e-Solutions";
			LOG.debug("BENEFICIARY MESSAGE : " + beneficiaryMessage);

			// notify originator.
			if (!this.isSendMessageToNonSmsClient(txn, message)) {
				notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
			}

			// also notify beneficiary.
			notificationInfos.add(new NotificationInfo(requestInfo.getTargetMobile(), beneficiaryMessage, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		} else {
			txn = this.promoteTxnState(txn, TransactionStatus.FAILED);
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			txn.setNarrative(responseInfo.getNarrative());
			String message = "Your transfer to " + requestInfo.getTargetMobile() + " failed. Reason : " + responseInfo.getNarrative() + ". ZB e-Wallet";

			// handle sending messages to ussd and web
			if (this.isSendMessageToNonSmsClient(txn, message)) {
				return null;
			}
			// notify originator.
			notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		}
		LOG.debug("Returning count of notifications for ewallet to ewallet : " + notificationInfos.size());
		return notificationInfos;
	}

	private List<NotificationInfo> processEWalletDepositAdvice(ResponseInfo responseInfo) throws Exception {
		LOG.debug("PROCESSING EWALLET DEPOSIT ADVICE ...");
		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();

		RequestInfo requestInfo = responseInfo.getRequestInfo();
		// promote the txn state according to response.
		if (ResponseCode.E000.equals(responseInfo.getResponseCode())) {
			String response = "Your " + MoneyUtil.convertCentsToDollarsPattern(requestInfo.getAmount()) + " deposit to ewallet account " + requestInfo.getSourceMobile() + " was successful. " + "Your new balance is " + MoneyUtil.convertCentsToDollarsPattern(responseInfo.getAvailableBalance()) + ". " + EWalletConstants.REF_NARRATIVE + responseInfo.getMessageId() + ". ZB e-Wallet - Powered by e-Solutions";

			// notify originator.
			notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), response, requestInfo.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		} else {
			String message = "Your deposit to " + requestInfo.getTargetMobile() + " failed. Reason : " + responseInfo.getNarrative() + ". ZB e-Wallet";
			// notify originator.
			notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, requestInfo.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		}
		LOG.debug("Returning count of notifications for ewallet to ewallet : " + notificationInfos.size());
		return notificationInfos;
	}

	private List<NotificationInfo> processBankAccountToNonHolderTransfer(ResponseInfo responseInfo, String srcCustomerName) throws Exception {

		LOG.debug("PROCESSING BANK ACCOUNT TO NON-HOLDER TRANSFER ADVICE ...");
		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>(2);

		RequestInfo requestInfo = responseInfo.getRequestInfo();
		// look for the txn
		MessageTransaction txn = em.find(MessageTransaction.class, requestInfo.getMessageId());
		// update it to the new status
		if (txn == null) {
			throw new Exception("Original Transaction with reference [" + requestInfo.getMessageId() + "] was not found.");
		}
		// promote the txn state according to response.
		if (ResponseCode.E000.equals(responseInfo.getResponseCode())) {
			txn = this.promoteTxnState(txn, TransactionStatus.COMPLETED);
			txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
			txn.setNarrative(responseInfo.getNarrative());
			String message = "Your transfer of " + MoneyUtil.convertCentsToDollarsPattern(txn.getAmount()) + " from " + requestInfo.getSourceAccountNumber() + " to " + requestInfo.getTargetMobile() + " was successful. Your new available balance is " + MoneyUtil.convertCentsToDollarsPattern(responseInfo.getAvailableBalance()) + ". " + EWalletConstants.REF_NARRATIVE + responseInfo.getMessageId() + ". ZB e-Wallet - Powered by e-Solutions";

			// notify originator.
			if (!this.isSendMessageToNonSmsClient(txn, message)) {
				notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
			}
			// also notify beneficiary.
			String name = srcCustomerName == null ? requestInfo.getSourceMobile() : srcCustomerName;
			LOG.debug("Customer Name : " + name);
			String beneficiaryMessage = "CASH received[nl]From : " + name + "[nl]" + "AMT : " + MoneyUtil.convertCentsToDollarsPattern(requestInfo.getAmount()) + "[nl]Ref : " + requestInfo.getMessageId() + ". ZB e-Wallet - Powered by e-Solutions";
			// String beneficiaryMessage = "You have received " +
			// MoneyUtil.convertCentsToDollarsPattern(requestInfo.getAmount()) +
			// " from mobile " + requestInfo.getSourceMobile() +
			// ". The reference code is " + requestInfo.getMessageId() +
			// ". ZB e-Wallet - Powered by e-Solutions";
			// notify originator.
			LOG.debug("BENEFICIARY MESSAGE : " + beneficiaryMessage);

			// String beneficiaryMessage = "You have received CASH[nl]From : " +
			// srcCustomerName == null ? requestInfo.getSourceMobile() :
			// srcCustomerName +"[nl]" + "AMT : " +
			// MoneyUtil.convertCentsToDollarsPattern(requestInfo.getAmount()) +
			// "[nl]Ref : " + requestInfo.getMessageId() +
			// "[nl]Please collect from any ZB Branch or Agent. ZB e-Wallet - Powered by e-Solutions";
			// String beneficiaryMessage = "You have receive CASH[[nl]From : " +
			// srcCustomerName == null ? requestInfo.getSourceMobile() :
			// srcCustomerName +"[nl]" + "AMT : " +
			// MoneyUtil.convertCentsToDollarsPattern(requestInfo.getAmount()) +
			// "[nl]Ref : " + requestInfo.getMessageId() +
			// ". ZB e-Wallet - Powered by e-Solutions";
			notificationInfos.add(new NotificationInfo(requestInfo.getTargetMobile(), beneficiaryMessage, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));

			// Add auto-reg notifications to beneficiary
			if (responseInfo.isAutoregPerformed()) {
				notificationInfos = this.addAutoRegNotifications(notificationInfos, responseInfo);
			}

		} else {
			txn = this.promoteTxnState(txn, TransactionStatus.FAILED);
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			txn.setNarrative(responseInfo.getNarrative());
			String message = "Your transfer to " + requestInfo.getTargetMobile() + " failed. Reason : " + responseInfo.getNarrative() + ". ZB e-Wallet";

			// handle sending messages to ussd and web
			if (this.isSendMessageToNonSmsClient(txn, message)) {
				return null;
			}
			// notify originator.
			notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		}
		LOG.debug("Returning count of notifications for ewallet to ewallet : " + notificationInfos.size());
		return notificationInfos;
	}

	private List<NotificationInfo> processEWalletToEWalletTransferAdvice(ResponseInfo responseInfo, String srcCustomerName) throws Exception {
		LOG.debug("PROCESSING EWALLET TO EWALLET TRANSFER ADVICE ...");
		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();

		RequestInfo requestInfo = responseInfo.getRequestInfo();
		// look for the txn
		MessageTransaction txn = em.find(MessageTransaction.class, requestInfo.getMessageId());
		// update it to the new status
		if (txn == null) {
			throw new Exception("Original Transaction with reference [" + requestInfo.getMessageId() + "] was not found.");
		}
		LOG.debug("Done running finder......" + txn);
		// promote the txn state according to response.
		if (ResponseCode.E000.equals(responseInfo.getResponseCode())) {
			txn = this.promoteTxnState(txn, TransactionStatus.COMPLETED);
			txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
			txn.setNarrative(responseInfo.getNarrative());
			String message = "Your transfer of " + MoneyUtil.convertCentsToDollarsPattern(txn.getAmount()) + " from ewallet account " + requestInfo.getSourceMobile() + " to " + requestInfo.getTargetMobile() + " was successful. Your new balance is " + MoneyUtil.convertCentsToDollarsPattern(responseInfo.getAvailableBalance()) + ". " + EWalletConstants.REF_NARRATIVE + responseInfo.getMessageId() + ". ZB e-Wallet - Powered by e-Solutions";

			String name = srcCustomerName == null ? requestInfo.getSourceMobile() : srcCustomerName;
			LOG.debug("Customer Name : " + name);
			String beneficiaryMessage = "CASH received in your ewallet[nl]From : " + name + "[nl]" + "AMT : " + MoneyUtil.convertCentsToDollarsPattern(requestInfo.getAmount()) + "[nl]Ref : " + requestInfo.getMessageId() + ". ZB e-Wallet - Powered by e-Solutions";
			// String beneficiaryMessage = "You have received " +
			// MoneyUtil.convertCentsToDollarsPattern(requestInfo.getAmount()) +
			// " from mobile " + requestInfo.getSourceMobile() +
			// ". The reference code is " + requestInfo.getMessageId() +
			// ". ZB e-Wallet - Powered by e-Solutions";
			// notify originator.
			LOG.debug("BENEFICIARY MESSAGE : " + beneficiaryMessage);

			if (!this.isSendMessageToNonSmsClient(txn, message)) {
				notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
			}
			// also notify beneficiary.
			notificationInfos.add(new NotificationInfo(requestInfo.getTargetMobile(), beneficiaryMessage, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		} else {
			txn = this.promoteTxnState(txn, TransactionStatus.FAILED);
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			txn.setNarrative(responseInfo.getNarrative());
			String message = "Your transfer to " + requestInfo.getTargetMobile() + " failed. Reason : " + responseInfo.getNarrative() + ". ZB e-Wallet";

			// handle sending messages to ussd and web
			if (this.isSendMessageToNonSmsClient(txn, message)) {
				return null;
			}
			// notify originator.
			notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		}
		LOG.debug("Returning count of notifications for ewallet to ewallet : " + notificationInfos.size());
		return notificationInfos;
	}

	private List<NotificationInfo> processEwalletToNonHolderTransfer(ResponseInfo responseInfo, String srcCustomerName) throws Exception {
		LOG.debug("Handling the eWallet to Non-Holder transfer..." + responseInfo.getNarrative());
		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>(2);
		RequestInfo requestInfo = responseInfo.getRequestInfo();

		// look for the txn
		MessageTransaction txn = em.find(MessageTransaction.class, requestInfo.getMessageId());
		// update it to the new status
		if (txn == null) {
			throw new Exception("Original Transaction with reference [" + requestInfo.getMessageId() + "] was not found.");
		}
		String message;
		// promote the txn state according to response.
		if (ResponseCode.E000.equals(responseInfo.getResponseCode())) {
			txn = this.promoteTxnState(txn, TransactionStatus.COMPLETED);
			txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
			txn.setNarrative(responseInfo.getNarrative());
			message = "Your transfer of " + MoneyUtil.convertCentsToDollarsPatternNoCurrency(requestInfo.getAmount()) + " from " + requestInfo.getSourceMobile() + " to " + requestInfo.getTargetMobile() + " was successful. Your new Ewallet Balance " + MoneyUtil.convertCentsToDollarsPatternNoCurrency(responseInfo.getAvailableBalance()) + ". " + EWalletConstants.REF_NARRATIVE + responseInfo.getMessageId() + ". ZB e-Wallet - Powered by e-Solutions";

			// notify beneficiary
			// String beneficiaryMessage = "You have received CASH[nl]From : " +
			// srcCustomerName == null ? requestInfo.getSourceMobile() :
			// srcCustomerName +"[nl]" + "AMT : " +
			// MoneyUtil.convertCentsToDollarsPattern(requestInfo.getAmount()) +
			// "[nl]Ref : " + requestInfo.getMessageId() +
			// "[nl]Please collect from any ZB Branch or Agent. ZB e-Wallet - Powered by e-Solutions";
			String name = srcCustomerName == null ? requestInfo.getSourceMobile() : srcCustomerName;
			LOG.debug("Customer Name : " + name);
			String beneficiaryMessage = "CASH received[nl]From : " + name + "[nl]" + "AMT : " + MoneyUtil.convertCentsToDollarsPattern(requestInfo.getAmount()) + "[nl]Ref : " + requestInfo.getMessageId() + ". ZB e-Wallet - Powered by e-Solutions";
			// String beneficiaryMessage = "You have received " +
			// MoneyUtil.convertCentsToDollarsPattern(requestInfo.getAmount()) +
			// " from mobile " + requestInfo.getSourceMobile() +
			// ". The reference code is " + requestInfo.getMessageId() +
			// ". ZB e-Wallet - Powered by e-Solutions";
			// notify originator.
			LOG.debug("BENEFICIARY MESSAGE : " + beneficiaryMessage);

			notificationInfos.add(new NotificationInfo(requestInfo.getTargetMobile(), beneficiaryMessage, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));

			// Add auto-reg notifications to beneficiary
			if (responseInfo.isAutoregPerformed()) {

				notificationInfos = this.addAutoRegNotifications(notificationInfos, responseInfo);

			}

		} else {
			txn = this.promoteTxnState(txn, TransactionStatus.FAILED);
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			txn.setNarrative(responseInfo.getNarrative());
			message = "Your transfer request failed. Reason " + responseInfo.getNarrative() + ". ZB e-Wallet";

			if (this.isSendMessageToNonSmsClient(txn, message)) {
				return null;
			}

		}

		// notify originator.
		if (!this.isSendMessageToNonSmsClient(txn, message)) {
			notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		}
		LOG.debug("Zvafaya");
		return notificationInfos;
	}

	List<NotificationInfo> processBankAccountToBankAccountAdvice(ResponseInfo responseInfo) throws Exception {
		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();
		RequestInfo requestInfo = responseInfo.getRequestInfo();
		LOG.debug("In the process code.....for BA to BA.....");
		// look for the txn
		MessageTransaction txn = em.find(MessageTransaction.class, requestInfo.getMessageId());
		// update it to the new status
		if (txn == null) {
			throw new Exception("Original Transaction with reference [" + requestInfo.getMessageId() + "] was not found.");
		}
		String message;
		// promote the txn state according to response.
		if (ResponseCode.E000.equals(responseInfo.getResponseCode())) {
			txn = this.promoteTxnState(txn, TransactionStatus.COMPLETED);
			txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
			txn.setNarrative(responseInfo.getNarrative());
			message = "Your transfer of " + MoneyUtil.convertCentsToDollarsPatternNoCurrency(requestInfo.getAmount()) + " from " + requestInfo.getSourceAccountNumber() + " to " + requestInfo.getDestinationAccountNumber() + " was successful. Your new Available Balance " + MoneyUtil.convertCentsToDollarsPatternNoCurrency(responseInfo.getAvailableBalance()) + ". " + EWalletConstants.REF_NARRATIVE + responseInfo.getMessageId() + ". ZB e-Wallet - Powered by e-Solutions";

			// handle sending messages to ussd and web
			if (this.isSendMessageToNonSmsClient(txn, message)) {
				return null;
			}

		} else {
			txn = this.promoteTxnState(txn, TransactionStatus.FAILED);
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			txn.setNarrative(responseInfo.getNarrative());
			message = "Your transfer request failed. Reason " + responseInfo.getNarrative() + ". ZB e-Wallet";

			// handle sending messages to ussd and web
			if (this.isSendMessageToNonSmsClient(txn, message)) {
				return null;
			}
		}
		// notify originator.
		LOG.debug("Done creating msg : " + message);
		notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		LOG.debug("RESPONSE : " + notificationInfos.size());
		return notificationInfos;
	}

	List<NotificationInfo> processRTGSAdvice(ResponseInfo responseInfo) throws Exception {
		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();
		RequestInfo requestInfo = responseInfo.getRequestInfo();
		LOG.debug("In process RGTS Advice");
		// look for the txn
		MessageTransaction txn = em.find(MessageTransaction.class, requestInfo.getMessageId());
		// update it to the new status
		if (txn == null) {
			throw new Exception("Original Transaction with reference [" + requestInfo.getMessageId() + "] was not found.");
		}
		String message;
		// promote the txn state according to response.
		if (ResponseCode.E000.equals(responseInfo.getResponseCode())) {
			txn = this.promoteTxnState(txn, TransactionStatus.COMPLETED);
			txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
			txn.setNarrative(responseInfo.getNarrative());
			message = "Your RTGS of " + MoneyUtil.convertCentsToDollarsPatternNoCurrency(requestInfo.getAmount()) + " from " + requestInfo.getSourceAccountNumber() + " to " + requestInfo.getDestinationBankName() + " Account " + requestInfo.getDestinationAccountNumber() + " was successful. Your new Available Balance " + MoneyUtil.convertCentsToDollarsPatternNoCurrency(responseInfo.getAvailableBalance()) + ". " + EWalletConstants.REF_NARRATIVE + responseInfo.getMessageId() + ". ZB e-Wallet - Powered by e-Solutions";

			// handle sending messages to ussd and web
			if (this.isSendMessageToNonSmsClient(txn, message)) {
				return null;
			}
		} else {
			txn = this.promoteTxnState(txn, TransactionStatus.FAILED);
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			txn.setNarrative(responseInfo.getNarrative());
			message = "Your RTGS request failed. Reason " + responseInfo.getNarrative() + ". ZB e-Wallet";

			// handle sending messages to ussd and web
			if (this.isSendMessageToNonSmsClient(txn, message)) {
				return null;
			}
		}
		// notify originator.
		LOG.debug("Done creating msg : " + message);
		notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		LOG.debug("RESPONSE : " + notificationInfos.size());
		return notificationInfos;
	}

	private MessageTransaction updateProcessTransaction(MessageTransaction tranx) throws Exception {
		return em.merge(tranx);
	}

	public TransactionState createTransactionState(TransactionState tranxState) throws Exception {
		tranxState.setId(GenerateKey.generateEntityId());
		tranxState.setDateCreated(new Date());
		em.persist(tranxState);
		return tranxState;
	}

	public TransactionState updateTransactionState(TransactionState tranxState) throws Exception {
		return em.merge(tranxState);
	}

	public MessageTransaction promoteTxnState(MessageTransaction txn, TransactionStatus status) throws Exception {
		txn = em.find(MessageTransaction.class, txn.getUuid());
		txn.setStatus(status);
		TransactionState state = new TransactionState();
		state.setStatus(status);
		try {
			txn = this.updateProcessTransaction(txn);
			state.setTransaction(txn);
			this.createTransactionState(state);
		} catch (Exception e) {
			LOG.error(e);
			throw e;
		}
		return txn;
	}

	private String getDefaultHelpMessage(RequestInfo msgTxn) {
		try {
			ConfigInfo configInfo = new SwitchConfigurationServiceSOAPProxy().findConfigInfoByOwnerId(msgTxn.getBankCode());
			if (configInfo != null && configInfo.getConfigId() != null) {
				String help = "For help on a specific txn send : " + configInfo.getSmsCode() + "*help*{TXN_TYPE} to 440. TXN can be TOPUP or TRANSFER or BAL or MINI or BILLPAY or PINCHANGE";
				return help;
			}
		} catch (Exception e) {
			LOG.error("Failed to generate HELP message.");
		}
		return null;
	}

	private List<NotificationInfo> addAutoRegNotifications(List<NotificationInfo> notificationInfos, ResponseInfo responseInfo) throws Exception {

		RequestInfo requestInfo = responseInfo.getRequestInfo();
		String code = EncryptAndDecrypt.decrypt(responseInfo.getAutoregPassword(), requestInfo.getTargetMobile());

		String beneficiaryMessage1 = "Welcome to ZB Mobile Banking.[nl] Your e-Wallet was opened when you received money. [nl] Your password is " + code + ".[nl]Send your requests to 440. ZB e-Wallet - Powered by e-Solutions";
		notificationInfos.add(new NotificationInfo(requestInfo.getTargetMobile(), beneficiaryMessage1, requestInfo.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));

		String beneficiaryMessage2 = this.getDefaultHelpMessage(requestInfo);
		if (beneficiaryMessage2 != null) {
			notificationInfos.add(new NotificationInfo(requestInfo.getTargetMobile(), beneficiaryMessage2, requestInfo.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		}

		return notificationInfos;
	}

	public List<NotificationInfo> processReversal(ResponseInfo responseInfo) throws Exception {

		LOG.debug("In Process Message .... responseType " + responseInfo.getResponseType() + ":::::::: Txn Type" + responseInfo.getRequestInfo().getTransactionType());
		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();
		RequestInfo requestInfo = responseInfo.getRequestInfo();

		MessageTransaction txn = em.find(MessageTransaction.class, requestInfo.getMessageId());
		// update it to the new status
		if (txn == null) {
			throw new Exception("Original Transaction with reference [" + requestInfo.getMessageId() + "] was not found.");
		}
		String message;

		// promote the txn state according to response.
		txn = this.promoteTxnState(txn, TransactionStatus.FAILED);
		txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
		txn.setNarrative(responseInfo.getNarrative());
		message = responseInfo.getNarrative();
		this.updateProcessTransaction(txn);

		// notify originator.
		LOG.debug("Done creating msg : " + message);
		notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		LOG.debug("RESPONSE : " + notificationInfos.size());
		return notificationInfos;
	}

	private List<NotificationInfo> processAgentDepositAdvice(ResponseInfo responseInfo) throws Exception {
		LOG.debug("PROCESSING AGENT DEPOSIT ADVICE ...");
		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();

		RequestInfo requestInfo = responseInfo.getRequestInfo();

		String agentNumber = requestInfo.getAgentNumber();

		// Customer agentCustomer = new CustomerServiceSOAPProxy()
		// .getMobileProfileByBankAndMobileNumber(requestInfo.getSourceBankId(),
		// requestInfo.getSourceMobile())
		// .getCustomer();
		//
		// Agent agent = new
		// AgentServiceSOAPProxy().getAgentByCustomerId(agentCustomer.getId());
		//
		// agentNumber = (agent == null)? "" : agent.getAgentNumber();

		// look for the txn
		MessageTransaction txn = em.find(MessageTransaction.class, requestInfo.getMessageId());
		// update it to the new status
		if (txn == null) {
			throw new Exception("Original Transaction with reference [" + requestInfo.getMessageId() + "] was not found.");
		}
		LOG.debug("Done running finder......" + txn);
		// promote the txn state according to response.
		if (ResponseCode.E000.equals(responseInfo.getResponseCode())) {
			txn = this.promoteTxnState(txn, TransactionStatus.COMPLETED);
			txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
			txn.setNarrative(responseInfo.getNarrative());
			String message = "Deposit of " + MoneyUtil.convertCentsToDollarsPattern(txn.getAmount()) + " into ewallet account " + requestInfo.getTargetMobile() + " at agent " + agentNumber + " was successful. " + EWalletConstants.REF_NARRATIVE + responseInfo.getMessageId() + ". ZB e-Wallet - Powered by e-Solutions";
			txn = this.updateProcessTransaction(txn);
			String beneficiaryMessage = "Deposit of " + MoneyUtil.convertCentsToDollarsPattern(requestInfo.getAmount()) + " into ewallet account " + requestInfo.getTargetMobile() + " at agent " + agentNumber + " was successful. Your new balance is " + MoneyUtil.convertCentsToDollarsPattern(responseInfo.getTargetAccountAvailableBalance()) + ". The reference code is " + requestInfo.getMessageId() + ". ZB e-Wallet - Powered by e-Solutions";
			// notify originator.
			if (!this.isSendMessageToNonSmsClient(txn, message)) {
				notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
			}
			// also notify beneficiary.
			notificationInfos.add(new NotificationInfo(requestInfo.getTargetMobile(), beneficiaryMessage, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		} else {
			txn = this.promoteTxnState(txn, TransactionStatus.FAILED);
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			txn.setNarrative(responseInfo.getNarrative());
			String message = "Your deposit into account " + requestInfo.getTargetMobile() + " failed. Reason : " + responseInfo.getNarrative() + ". ZB e-Wallet";

			if (this.isSendMessageToNonSmsClient(txn, message)) {
				return null;
			}
			// notify originator.
			notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		}
		LOG.debug("Returning count of notifications for agent deposit : " + notificationInfos.size());
		return notificationInfos;
	}

	private List<NotificationInfo> processAgentWithdrawalAdvice(ResponseInfo responseInfo) throws Exception {
		LOG.debug("PROCESSING AGENT WITHDRAWAL ADVICE ...");
		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();

		RequestInfo requestInfo = responseInfo.getRequestInfo();

		String agentNumber = requestInfo.getAgentNumber();

		// Customer agentCustomer = new CustomerServiceSOAPProxy()
		// .getMobileProfileByBankAndMobileNumber(requestInfo.getSourceBankId(),
		// requestInfo.getTargetMobile())
		// .getCustomer();
		//
		// Agent agent = new
		// AgentServiceSOAPProxy().getAgentByCustomerId(agentCustomer.getId());
		//
		// agentNumber = (agent == null)? "" : agent.getAgentNumber();
		//
		// look for the txn
		MessageTransaction txn = em.find(MessageTransaction.class, requestInfo.getMessageId());
		// update it to the new status
		if (txn == null) {
			throw new Exception("Original Transaction with reference [" + requestInfo.getMessageId() + "] was not found.");
		}
		LOG.debug("Done running finder......" + txn);
		// promote the txn state according to response.
		if (ResponseCode.E000.equals(responseInfo.getResponseCode())) {
			txn = this.promoteTxnState(txn, TransactionStatus.COMPLETED);
			txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
			txn.setNarrative(responseInfo.getNarrative());
			String message = "Withdrawal of " + MoneyUtil.convertCentsToDollarsPattern(txn.getAmount()) + " from ewallet account " + requestInfo.getSourceMobile() + " at agent " + agentNumber + " was successful. Your new balance is " + MoneyUtil.convertCentsToDollarsPattern(responseInfo.getAvailableBalance()) + ". " + EWalletConstants.REF_NARRATIVE + responseInfo.getMessageId() + ". ZB e-Wallet - Powered by e-Solutions";

			// handle sending messages to ussd and web

			String beneficiaryMessage = "Withdrawal of " + MoneyUtil.convertCentsToDollarsPattern(requestInfo.getAmount()) + " from ewallet account " + requestInfo.getSourceMobile() + " at agent " + agentNumber + " was successful. The reference code is " + requestInfo.getMessageId() + ". ZB e-Wallet - Powered by e-Solutions";
			// notify originator.
			if (!this.isSendMessageToNonSmsClient(txn, message)) {
				notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
			}
			// also notify beneficiary.
			notificationInfos.add(new NotificationInfo(requestInfo.getTargetMobile(), beneficiaryMessage, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		} else {
			txn = this.promoteTxnState(txn, TransactionStatus.FAILED);
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			txn.setNarrative(responseInfo.getNarrative());
			String message = "Your withdrawal failed. Reason : " + responseInfo.getNarrative() + ". ZB e-Wallet";

			// handle sending messages to ussd and web
			if (this.isSendMessageToNonSmsClient(txn, message)) {
				return null;
			}
			// notify originator.
			notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		}
		LOG.debug("Returning count of notifications for agent withdrawal : " + notificationInfos.size());
		return notificationInfos;
	}

	private List<NotificationInfo> processAgentNonHolderWithdrawalAdvice(ResponseInfo responseInfo) throws Exception {
		LOG.debug("PROCESSING AGENT NONHOLDER WITHDRAWAL ADVICE ...");
		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();

		RequestInfo requestInfo = responseInfo.getRequestInfo();

		String agentNumber = requestInfo.getAgentNumber();

		// Customer agentCustomer = new CustomerServiceSOAPProxy()
		// .getMobileProfileByBankAndMobileNumber(requestInfo.getSourceBankId(),
		// requestInfo.getSourceMobile())
		// .getCustomer();
		//
		// Agent agent = new
		// AgentServiceSOAPProxy().getAgentByCustomerId(agentCustomer.getId());
		//
		// agentNumber = (agent == null)? "" : agent.getAgentNumber();

		// look for the txn
		MessageTransaction txn = em.find(MessageTransaction.class, requestInfo.getMessageId());
		// update it to the new status
		if (txn == null) {
			throw new Exception("Original Transaction with reference [" + requestInfo.getMessageId() + "] was not found.");
		}
		LOG.debug("Done running finder......" + txn);
		// promote the txn state according to response.
		if (ResponseCode.E000.equals(responseInfo.getResponseCode())) {
			txn = this.promoteTxnState(txn, TransactionStatus.COMPLETED);
			txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
			txn.setNarrative(responseInfo.getNarrative());
			String message = "Withdrawal of " + MoneyUtil.convertCentsToDollarsPattern(txn.getAmount()) + " from mobile " + requestInfo.getTargetMobile() + " at agent " + agentNumber + " was successful." + EWalletConstants.REF_NARRATIVE + responseInfo.getMessageId() + ". ZB e-Wallet - Powered by e-Solutions";

			String beneficiaryMessage = "Withdrawal of " + MoneyUtil.convertCentsToDollarsPattern(requestInfo.getAmount()) + " from mobile " + requestInfo.getTargetMobile() + " at agent " + agentNumber + " was successful. The reference code is " + requestInfo.getMessageId() + ". ZB e-Wallet - Powered by e-Solutions";
			// notify originator.
			if (!this.isSendMessageToNonSmsClient(txn, message)) {
				notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
			}
			// also notify beneficiary.
			notificationInfos.add(new NotificationInfo(requestInfo.getTargetMobile(), beneficiaryMessage, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		} else {
			txn = this.promoteTxnState(txn, TransactionStatus.FAILED);
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			txn.setNarrative(responseInfo.getNarrative());
			String message = "Your withdrawal failed. Reason : " + responseInfo.getNarrative() + ". ZB e-Wallet";

			// handle sending messages to ussd and web
			if (this.isSendMessageToNonSmsClient(txn, message)) {
				return null;
			}

			// notify originator.
			notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		}
		LOG.debug("Returning count of notifications for agent nonholder withdrawal : " + notificationInfos.size());
		return notificationInfos;
	}

	private List<NotificationInfo> processAgentEwalletToBankAccountTransferAdvice(ResponseInfo responseInfo) throws Exception {
		LOG.debug("PROCESSING AGENT EWALLET TO BANK ACCOUNT TRANSFER ADVICE ...");
		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();

		RequestInfo requestInfo = responseInfo.getRequestInfo();
		// look for the txn
		MessageTransaction txn = em.find(MessageTransaction.class, requestInfo.getMessageId());
		// update it to the new status
		if (txn == null) {
			throw new Exception("Original Transaction with reference [" + requestInfo.getMessageId() + "] was not found.");
		}
		LOG.debug("Done running finder......" + txn);
		// promote the txn state according to response.
		if (ResponseCode.E000.equals(responseInfo.getResponseCode())) {
			txn = this.promoteTxnState(txn, TransactionStatus.COMPLETED);
			txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
			txn.setNarrative(responseInfo.getNarrative());
			String message = "Your transfer of " + MoneyUtil.convertCentsToDollarsPattern(txn.getAmount()) + " from eWallet account " + requestInfo.getSourceMobile() + " to " + requestInfo.getDestinationAccountNumber() + " was successful. Your new eWallet balance is " + MoneyUtil.convertCentsToDollarsPattern(responseInfo.getAvailableBalance()) + ". " + EWalletConstants.REF_NARRATIVE + responseInfo.getMessageId() + ". ZB e-Wallet - Powered by e-Solutions. ";

			// txn = this.updateProcessTransaction(txn);
			// handle sending messages to ussd and web
			if (this.isSendMessageToNonSmsClient(txn, message)) {
				return null;
			}

			// notify originator.
			notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		} else {
			txn = this.promoteTxnState(txn, TransactionStatus.FAILED);
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			txn.setNarrative(responseInfo.getNarrative());
			String message = "Your transfer to " + requestInfo.getDestinationAccountNumber() + " failed. Reason : " + responseInfo.getNarrative() + ". ZB e-Wallet";

			// handle sending messages to ussd and web
			if (this.isSendMessageToNonSmsClient(txn, message)) {
				return null;
			}

			// notify originator.
			notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		}
		LOG.debug("Returning count of notifications for agent ewallet to bankaccount txf : " + notificationInfos.size());
		return notificationInfos;
	}

	private List<NotificationInfo> processAgentTransactionSummaryAdvice(ResponseInfo responseInfo) throws Exception {

		LOG.debug("PROCESSING AGENT SUMMARY ADVICE ...");
		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();

		RequestInfo requestInfo = responseInfo.getRequestInfo();
		// look for the txn
		MessageTransaction txn = em.find(MessageTransaction.class, requestInfo.getMessageId());
		// update it to the new status
		if (txn == null) {
			throw new Exception("Original Transaction with reference [" + requestInfo.getMessageId() + "] was not found.");
		}
		// promote the txn state according to response.
		if (ResponseCode.E000.equals(responseInfo.getResponseCode())) {
			txn = this.promoteTxnState(txn, TransactionStatus.COMPLETED);
			txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
			txn.setNarrative(responseInfo.getNarrative());

			String message = responseInfo.getNarrative();

			// handle sending messages to ussd and web
			if (this.isSendMessageToNonSmsClient(txn, message)) {
				return null;
			}

			// notify originator.
			notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), responseInfo.getNarrative() + ". ZB e-Wallet - Powered by e-Solutions", txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		} else {
			txn = this.promoteTxnState(txn, TransactionStatus.FAILED);
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			txn.setNarrative(responseInfo.getNarrative());
			String message = "Agent Summary Request failed. Reason : " + responseInfo.getNarrative() + ". ZB e-Wallet";

			// handle sending messages to ussd and web
			if (this.isSendMessageToNonSmsClient(txn, message)) {
				return null;
			}

			// notify originator.
			notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, txn.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		}
		LOG.debug("Returning count of notifications for agent summary : " + notificationInfos.size());
		return notificationInfos;

	}

	List<NotificationInfo> processTransactionReversalMessage(ResponseInfo responseInfo) throws Exception {
		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();
		RequestInfo requestInfo = responseInfo.getRequestInfo();
		LOG.debug("In the process code....Transaction Reversal Message.....");
		String message = responseInfo.getNarrative();
		message = message + ". Ref : " + requestInfo.getMessageId() + ". ZB e-Wallet - Powered by e-Solutions";
		// notify originator.
		LOG.debug("Done creating msg : " + message);
		notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, this.getMNO(requestInfo.getSourceMobile()), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		LOG.debug("RESPONSE : " + notificationInfos.size());
		return notificationInfos;
	}

	private List<NotificationInfo> processEWalletAgentDepositAdvice(ResponseInfo responseInfo) throws Exception {
		LOG.debug("PROCESSING EWALLET AGENT DEPOSIT ADVICE ...");
		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();

		RequestInfo requestInfo = responseInfo.getRequestInfo();
		// promote the txn state according to response.
		if (ResponseCode.E000.equals(responseInfo.getResponseCode())) {
			String response = "Your " + MoneyUtil.convertCentsToDollarsPattern(requestInfo.getAmount()) + " deposit to your agent ewallet account " + requestInfo.getSourceMobile() + " was successful. " + "Your new balance is " + MoneyUtil.convertCentsToDollarsPattern(responseInfo.getAvailableBalance()) + ". " + EWalletConstants.REF_NARRATIVE + responseInfo.getMessageId() + ". ZB e-Wallet - Powered by e-Solutions";

			// notify originator.
			notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), response, requestInfo.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		} else {
			String message = "Your deposit to " + requestInfo.getTargetMobile() + " failed. Reason : " + responseInfo.getNarrative() + ". ZB e-Wallet";
			// notify originator.
			notificationInfos.add(new NotificationInfo(requestInfo.getSourceMobile(), message, requestInfo.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));
		}
		LOG.debug("Returning count of notifications for ewallet to ewallet : " + notificationInfos.size());
		return notificationInfos;
	}

	private List<NotificationInfo> processIBBulkToEWalletTransferAdvice(ResponseInfo responseInfo) throws Exception {
		LOG.debug("PROCESSING IB BULK TO EWALLET TRANSFER ADVICE ...");

		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();

		RequestInfo requestInfo = responseInfo.getRequestInfo();

		if (ResponseCode.E000.equals(responseInfo.getResponseCode())) {

			String name = requestInfo.getAccountName() == null ? requestInfo.getSourceAccountNumber() : requestInfo.getAccountName();
			LOG.debug("Customer Name : " + name);
			String beneficiaryMessage = "CASH received[nl]From : " + name + "[nl]" + "AMT : " + MoneyUtil.convertCentsToDollarsPattern(requestInfo.getAmount()) + "[nl]Ref : " + requestInfo.getMessageId() + ". ZB e-Wallet - Powered by e-Solutions";

			LOG.debug("BENEFICIARY MESSAGE : " + beneficiaryMessage);

			// also notify beneficiary.
			notificationInfos.add(new NotificationInfo(requestInfo.getTargetMobile(), beneficiaryMessage, requestInfo.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));

			// Add auto-reg notifications to beneficiary
			if (responseInfo.isAutoregPerformed()) {

				notificationInfos = this.addAutoRegNotifications(notificationInfos, responseInfo);

			}

		} else {

			LOG.debug("IB Bulk ResponseCode not E000.. Ignore message..");

		}
		LOG.debug("Returning count of notifications for IB BULK to ewallet : " + notificationInfos.size());
		return notificationInfos;
	}

	private List<NotificationInfo> processIBBulkToNonHolderTransferAdvice(ResponseInfo responseInfo) throws Exception {
		LOG.debug("PROCESSING IB BULK TO NONHOLDER TRANSFER ADVICE ...");

		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();

		RequestInfo requestInfo = responseInfo.getRequestInfo();

		if (ResponseCode.E000.equals(responseInfo.getResponseCode())) {

			String name = requestInfo.getAccountName() == null ? requestInfo.getSourceAccountNumber() : requestInfo.getAccountName();
			LOG.debug("Customer Name : " + name);
			String beneficiaryMessage = "CASH received[nl]From : " + name + "[nl]" + "AMT : " + MoneyUtil.convertCentsToDollarsPattern(requestInfo.getAmount()) + "[nl]Ref : " + requestInfo.getMessageId() + ". ZB e-Wallet - Powered by e-Solutions";

			LOG.debug("BENEFICIARY MESSAGE : " + beneficiaryMessage);

			// also notify beneficiary.
			notificationInfos.add(new NotificationInfo(requestInfo.getTargetMobile(), beneficiaryMessage, requestInfo.getMno(), EWalletConstants.ECONET_SMS_OUT_QUEUE));

		} else {

			LOG.debug("IB Bulk ResponseCode not E000.. Ignore message..");

		}
		LOG.debug("Returning count of notifications for IB BULK to nonholder : " + notificationInfos.size());
		return notificationInfos;
	}

	private void completeUSSDTxn(UssdTransaction ussd, String message) throws Exception {
		// update USSD Transaction
		MobileCommerceServiceSOAPProxy ussdService = MobileCommerceProxy.getInstance();
		ussd.setMessage(message);
		ussd.setFlowStatus(FlowStatus.COMPLETED);
		ussdService.updateTransaction(ussd);
	}

	private void completeWebSession(WebSession webSession, String message) throws Exception {
		// update USSD Transaction
		MobileCommerceServiceSOAPProxy mobileService = MobileCommerceProxy.getInstance();
		webSession.setMessage(message);
		webSession.setStatus(WebStatus.COMLETED.toString());
		mobileService.updateWebSession(webSession);
	}

	public boolean isSendMessageToNonSmsClient(MessageTransaction txn, String response) {
		boolean canSend = false;
		// Check if ussd txn
		try {
			// Necessary Try, if it throws, it mustn't affect the original flow
			// of txns
			if (TransactionLocationType.USSD.equals(txn.getTransactionLocationType())) {
				
				MobileCommerceServiceSOAPProxy ussdService = MobileCommerceProxy.getInstance();
				UssdTransaction ussd = ussdService.findTransaction(txn.getUuid());
				LOG.debug("USSD Transaction is : "+ussd);
				if(ussd == null || ussd.getUuid() == null) {
					
					txn = this.updateProcessTransaction(txn);
					
				} else {
					LOG.debug("USSD Transaction UUID : "+ussd.getUuid()+", FLOW STATUS : "+ussd.getFlowStatus());
					if (!ussd.isSendSms()) {
	
						// Complete USSD Transaction
						this.completeUSSDTxn(ussd, response);
	
						// Update Message Transaction
						txn = this.updateProcessTransaction(txn);
	
						// Don't Notify Via SMS
						canSend = true;
	
					} else {
	
						ussdService.deleteTransaction(ussd.getUuid());
						txn = this.updateProcessTransaction(txn);
	
					}
				}
			} else if (TransactionLocationType.MOBILE_WEB.equals(txn.getTransactionLocationType())) {

				MobileCommerceServiceSOAPProxy mobileService = MobileCommerceProxy.getInstance();
				WebSession session = mobileService.getWebSessionByReferenceId(txn.getUuid());
				
				if(session == null || session.getId() == null) {
					
					txn = this.updateProcessTransaction(txn);
					
				} else {
					if (!session.isSendSms()) {
	
						// Complete USSD Transaction
						this.completeWebSession(session, response);
	
						// Update Message Transaction
						txn = this.updateProcessTransaction(txn);
	
						// Don't Notify Via SMS
						canSend = true;
	
					} else {
	
						mobileService.deleteWebSession(session.getId());
						txn = this.updateProcessTransaction(txn);
	
					}
				}
			} else {
				txn = this.updateProcessTransaction(txn);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return canSend;
	}

	public boolean processCustomerMerchatRegistration(MessageTransaction txn) throws Exception {
		MerchantServiceSOAPProxy merchantService = new MerchantServiceSOAPProxy();
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		List<Bank> banks = bankService.getBankByStatus(BankStatus.ACTIVE);

		Merchant merchant = merchantService.getMerchantByShortName(txn.getUtilityName());
		if (merchant == null || merchant.getId() == null) {
			LOG.debug("Merchant with short name " + txn.getUtilityName() + " could not be found. FAIL the operation");
			return false;
		} else {
			BankMerchant bankMerchant = null;
			for (Bank bank : banks) {
				bankMerchant = merchantService.getBankMerchantByBankIdAndMerchantId(bank.getId(), merchant.getId());
				if (bankMerchant != null && bankMerchant.getId() != null) {
					break;
				}
			}

			MobileProfile mobileProfile = customerService.findMobileProfileById(txn.getSourceMobileId());

			if (bankMerchant == null || bankMerchant.getId() == null) {
				LOG.debug("Merchant with short name " + merchant.getShortName() + " could is not enabled for this bank. FAIL the operation");
				return false;
			} else if (mobileProfile == null || mobileProfile.getId() == null) {
				LOG.debug("Source Mobile Number is not valid at the bank... something messy here. FAIL the operation");
				return false;
			}

			else {
				CustomerMerchant existing = merchantService.getCustomerMerchantByCustomerIdAndBankMerchantIdAndStatus(mobileProfile.getCustomer().getId(), bankMerchant.getId(), CustomerMerchantStatus.ACTIVE);
				if (existing == null || existing.getId() == null) {
					CustomerMerchant customerMerchant = new CustomerMerchant();
					customerMerchant.setBankId(txn.getSourceBankId());
					customerMerchant.setBankMerchant(bankMerchant);
					customerMerchant.setCustomerAccountNumber(txn.getCustomerUtilityAccount());
					customerMerchant.setCustomerId(mobileProfile.getCustomer().getId());
					customerMerchant.setStatus(CustomerMerchantStatus.ACTIVE);
					customerMerchant = merchantService.createCustomerMerchant(customerMerchant, EWalletConstants.SYSTEM);
					return true;
				} else {
					LOG.debug("The customer already has a registered account with " + txn.getUtilityName());
					return true;
				}
			}
		}
	}

}
