package zw.co.esolutions.mcommerce.centralswitch.processes;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.xmlbeans.XmlException;

import zw.co.esolutions.config.bank.BankInfo;
import zw.co.esolutions.config.merchant.MerchantInfo;
import zw.co.esolutions.ewallet.agentservice.service.Agent;
import zw.co.esolutions.ewallet.agentservice.service.AgentServiceSOAPProxy;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountType;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.CustomerClass;
import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.CustomerStatus;
import zw.co.esolutions.ewallet.customerservices.service.Exception_Exception;
import zw.co.esolutions.ewallet.customerservices.service.GenerateTxnPassCodeResp;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfileStatus;
import zw.co.esolutions.ewallet.customerservices.service.ValidateTxnPassCodeReq;
import zw.co.esolutions.ewallet.enums.MerchantRequestType;
import zw.co.esolutions.ewallet.enums.MobileNetworkOperator;
import zw.co.esolutions.ewallet.enums.TransactionLocationType;
import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.enums.TransferType;
import zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchant;
import zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchantStatus;
import zw.co.esolutions.ewallet.merchantservices.service.MerchantServiceSOAPProxy;
import zw.co.esolutions.ewallet.msg.MerchantRequest;
import zw.co.esolutions.ewallet.msg.MerchantResponse;
import zw.co.esolutions.ewallet.msg.RequestInfo;
import zw.co.esolutions.ewallet.msg.ResponseInfo;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.EncryptAndDecrypt;
import zw.co.esolutions.ewallet.util.Formats;
import zw.co.esolutions.ewallet.util.GenerateKey;
import zw.co.esolutions.ewallet.util.KeyWordUtil;
import zw.co.esolutions.ewallet.util.MoneyUtil;
import zw.co.esolutions.ewallet.util.NumberUtil;
import zw.co.esolutions.ewallet.util.SystemConstants;
import zw.co.esolutions.mcommerce.centralswitch.model.Bouquet;
import zw.co.esolutions.mcommerce.centralswitch.model.MessageTransaction;
import zw.co.esolutions.mcommerce.centralswitch.model.Tap;
import zw.co.esolutions.mcommerce.centralswitch.model.TransactionRoutingInfo;
import zw.co.esolutions.mcommerce.centralswitch.model.TransactionState;
import zw.co.esolutions.mcommerce.centralswitch.util.BouquetType;
import zw.co.esolutions.mcommerce.centralswitch.util.SMPPParser;
import zw.co.esolutions.mcommerce.msg.Message;
import zw.co.esolutions.mcommerce.msg.MessageDocument;
import zw.co.esolutions.mcommerce.msg.MobileOriginatedShortMessage;
import zw.co.esolutions.mcommerce.refgen.service.ReferenceGeneratorServiceSOAPProxy;
import zw.co.esolutions.mcommerce.xml.ISOMarshaller;
import zw.co.esolutions.topup.ws.impl.WsResponse;

/**
 * Session Bean implementation class ProcessSMSRequestImpl
 */
@Stateless
public class ProcessSMSRequestImpl implements ProcessSMSRequest {

	@PersistenceContext(unitName = "CentralSwitchEJB")
	private EntityManager em;

	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/switch.log.properties");
			LOG = Logger.getLogger(ProcessSMSRequestImpl.class);
		} catch (Exception e) {
		}
	}

	public ProcessSMSRequestImpl() {
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	void initialise() {
		
	}

	public MessageTransaction parseSMSRequestMessage(String smsRequestText) {

		MessageDocument doc;
		try {
			doc = MessageDocument.Factory.parse(smsRequestText);
		} catch (XmlException e) {
			LOG.debug("FAILED to parse TEXT to MOSM : ");
			e.printStackTrace(System.err);
			return null;
		}
		Message msg = doc.getMessage();
		MobileOriginatedShortMessage mosm = msg.getMobileOriginatedShortMessage();
		return this.parse(mosm);

	}

	private MessageTransaction parse(MobileOriginatedShortMessage msg) {
		if (msg == null) {
			return null;
		}

		MessageTransaction txn = new MessageTransaction();
		txn.setTransactionLocationType(TransactionLocationType.SMS);
		try {
			msg.setSubscriberId(NumberUtil.formatMobileNumber(msg.getSubscriberId()));
		} catch (Exception e1) {
			LOG.debug("FAILED to parse SOURCE subscriber ID : ");
			e1.printStackTrace(System.err);
			return null;
		}
		txn.setSourceMobileNumber(msg.getSubscriberId());
		LOG.debug("Set the source mobile number : " + txn.getSourceMobileNumber());
		txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
		LOG.debug("Assume this TXN is going to fail");
		String sms = msg.getShortMessage();
		// strip all non-ascii characters.
		sms = SMPPParser.clean(sms);
		String[] tokens = sms.split("\\*");
		LOG.info("RQST RECEIVED : " + txn.getSourceMobileNumber() +"|" + sms );
		String request;
		try {
			txn.setSourceBankPrefix(tokens[0]);
			request = tokens[1];
		} catch (Exception e) {
			e.printStackTrace();
			txn.setNarrative("Invalid Request. Please submit " + tokens[0] + "*HELP to 440");
			return txn;
		}

		if (SystemConstants.SMS_REQUEST_TYPE_TAP.equalsIgnoreCase(request)) {
			txn.setNarrative("Parsing tap request successful");
			txn.setTransactionType(TransactionType.ADMIN_TAP);
			txn.setTapCommand(tokens[2]);
			txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
			return txn;
		}
		if (SystemConstants.SMS_REQUEST_TYPE_HELP.equalsIgnoreCase(request)) {
			LOG.debug("Found a help request [" + sms + "]");
			txn.setNarrative(this.parseHelpMessage(txn, tokens));
			return txn;
		}

		// type of the request.
		if (SystemConstants.SMS_REQUEST_TYPE_BALANCE_ENQUIRY.equalsIgnoreCase(request) || SystemConstants.SMS_REQUEST_TYPE_BALANCE_ENQUIRY_LONG.equalsIgnoreCase(request)) {
			LOG.debug("Found a balance request");
			LOG.info("Verifying balance request for " + txn.getSourceMobileNumber());
			txn = this.parseBalanceRequest(txn, tokens);
			txn.setAgentTransaction(false);
		}

		else if (SystemConstants.SMS_REQUEST_TYPE_TRANSFER.equalsIgnoreCase(request) || SystemConstants.SMS_REQUEST_TYPE_TRANSFER_SHORT.equalsIgnoreCase(request) || SystemConstants.SMS_REQUEST_TYPE_TRANSFER_TRF.equalsIgnoreCase(request)) {
			LOG.debug("\n Found a transfer request.");
			txn = this.parseTransferRequest(txn, tokens);
			txn.setAgentTransaction(false);
		}

		else if (SystemConstants.SMS_REQUEST_TYPE_TOPUP.equalsIgnoreCase(request)) {
			LOG.debug("\n Found a topup request.");
			txn = this.parseTopupRequest(txn, tokens);
			txn.setAgentTransaction(false);
		}

		else if (SystemConstants.SMS_REQUEST_TYPE_MINI_STATEMENT.equalsIgnoreCase(request)) {
			LOG.debug("\n Found a mini statement request.");
			txn = this.parseMiniRequest(txn, tokens);
			txn.setAgentTransaction(false);
		}

		else if (SystemConstants.SMS_REQUEST_TYPE_PINCHANGE.equalsIgnoreCase(request) || SystemConstants.SMS_REQUEST_TYPE_PINCHANGE_LONG.equalsIgnoreCase(request)) {
			LOG.debug("\n Found a PIN CHANGE request.");
			txn = this.parsePINChangeRequest(txn, tokens);
			txn.setAgentTransaction(false);
		}

		else if (SystemConstants.SMS_REQUEST_TYPE_BILLPAYMENT.equalsIgnoreCase(request)) {
			LOG.debug("\n Found a bill pay request.");
			txn = this.parseBillPayRequest(txn, tokens);
			txn.setAgentTransaction(false);
		}

		else if (SystemConstants.SMS_REQUEST_TYPE_REGISTRATION.equalsIgnoreCase(request)) {
			LOG.debug("\n Found a MERCHANT REG request.");
			txn = this.parseMerchantRegistrationRequest(txn, tokens);
			txn.setAgentTransaction(false);
		}

		else if (SystemConstants.SMS_REQUEST_TYPE_SEND_CASH.equalsIgnoreCase(request)) {
			LOG.debug("\n Found a SEND MONEY request.");
			txn = this.parseSendCashRequest(txn, tokens);
			txn.setAgentTransaction(false);
		}

		else if (SystemConstants.SMS_REQUEST_TYPE_AGENT_DEPOSIT.equalsIgnoreCase(request) || SystemConstants.SMS_REQUEST_TYPE_AGENT_DEPOSIT_SHORT.equalsIgnoreCase(request)) {
			LOG.debug("\n Found an agent deposit request.");
			txn = this.parseAgentDepositRequest(txn, tokens);
			txn.setAgentTransaction(true);
		}

		else if (SystemConstants.SMS_REQUEST_TYPE_AGENT_WITHDRAWAL.equalsIgnoreCase(request) || SystemConstants.SMS_REQUEST_TYPE_AGENT_WITHDRAWAL_SHORT.equalsIgnoreCase(request)) {
			LOG.debug("\n Found an agent withdrawal request.");
			txn = this.parseAgentWithdrawalRequest(txn, tokens);
			txn.setValidateAgentNumber(true);
		}

		else if (SystemConstants.SMS_REQUEST_TYPE_AGENT_NONHOLDER_WITHDRAWAL.equalsIgnoreCase(request) || SystemConstants.SMS_REQUEST_TYPE_AGENT_NONHOLDER_WITHDRAWAL_SHORT.equalsIgnoreCase(request)) {
			LOG.debug("\n Found an agent non-holder withdrawal request.");
			txn = this.parseAgentNonHolderWithdrawalRequest(txn, tokens);
			txn.setAgentTransaction(true);
		}

		else if (SystemConstants.SMS_REQUEST_TYPE_AGENT_SUMMARY.equalsIgnoreCase(request) || SystemConstants.SMS_REQUEST_TYPE_AGENT_SUMMARY_SHORT.equalsIgnoreCase(request) || SystemConstants.SMS_REQUEST_TYPE_AGENT_SUMMARY_MEDIUM.equalsIgnoreCase(request)) {
			LOG.debug("\n Found an agent transaction summary request.");
			txn = this.parseAgentTransactionSummaryRequest(txn, tokens);
			txn.setAgentTransaction(true);
		}

		else if (SystemConstants.SMS_REQUEST_TYPE_SECRET_CODE_RESET.equalsIgnoreCase(request) || SystemConstants.SMS_REQUEST_TYPE_SECRET_CODE_RESET_SHORT.equalsIgnoreCase(request) || SystemConstants.SMS_REQUEST_TYPE_SECRET_CODE_RESET_MEDIUM.equalsIgnoreCase(request)) {
			LOG.debug("\n Found a nonholder secret code reset request.");
			txn = this.parseNonHolderSecretCodeResetRequest(txn, tokens);
		} else if (SystemConstants.SMS_REQUEST_TYPE_DSTV_BILLPAY.equalsIgnoreCase(request)) {
			
			LOG.debug("\n Found a DStv Billpay request.");
			
			try {
				
				String[] newTokens = new String[tokens.length + 1];
				
				newTokens[0] = tokens[0];
				newTokens[1] = SystemConstants.SMS_REQUEST_TYPE_BILLPAYMENT;
				
				for (int i = 1; i < tokens.length; i++) {
					LOG.debug("Token [" + i + "] is : " + tokens[i]);
					newTokens[i+1] = tokens[i];
				}
				
				txn = this.parseBillPayRequest(txn, newTokens);
				
			} catch (Exception e) {
				LOG.fatal("Failed to translate DSTV request");
				LOG.debug(e.toString());
				
				return null;
			}
		} 
		
		else if (tokens.length == 2) { // might be the pin
			LOG.debug("Got a PAREQ...[" + sms + "]");
			return this.parsePAREQ(tokens, txn);
		} else {
			txn.setNarrative(this.getDefaultHelpMessage(txn));
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			return txn;
		}
		MobileNetworkOperator mno = MobileNetworkOperator.valueOf(msg.getMobileNetworkOperator().toString());
		if (mno == null) {
			mno = MobileNetworkOperator.ECONET;
		}
		txn.setMno(mno);
		return txn;
	}

	private MessageTransaction parsePAREQ(String[] tokens, MessageTransaction txn) {
		String secondPart = tokens[1];
		LOG.debug("Validating PAREQ..");
		try {
			if (secondPart.length() != 2) {
				LOG.debug("PAREQ is not of length 2... REJECT");
				txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
				txn.setNarrative("Your password response " + tokens[0] + "*" + secondPart + " has been rejected. Password must be two digits only. e.g. " + tokens[0] + "*89");
				return txn;
			}
			int number = Integer.parseInt(secondPart);
			LOG.debug("PAREQ is: " + number);
		} catch (NumberFormatException ne) {
			LOG.debug("Failed to parseInt.. REJECT");
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			txn.setNarrative("Your password response " + tokens[0] + "*" + secondPart + " has been rejected. Password must be digits only. e.g. " + tokens[0] + "*89");
			return txn;
		}
		LOG.debug("PAREQ Validation successfull.. PROCEED");
		txn.setTransactionType(TransactionType.PASSCODE);
		txn.setStatus(TransactionStatus.PAREQ);
		txn.setPasswordParts(secondPart);
		txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
		return txn;

	}

	private MessageTransaction parseNonHolderSecretCodeResetRequest(MessageTransaction txn, String[] tokens) {
		// 6*code*{ref}*{newCode}
		if (tokens == null || tokens.length < 4) {
			// a secret code change request must have at least 4 parameters
			LOG.debug("Too few parameters for SECRET CODE RESET ");
			txn.setNarrative("Request Rejected. Invalid CODE RESET request. Please send " + tokens[0] + "*code*{ref}*{newCode}.");
			return txn;
		}
		txn.setStatus(TransactionStatus.VEREQ);
		txn.setTransactionType(TransactionType.SECRET_CODE_RESET);

		txn.setOldMessageId(tokens[2]);

		txn.setSecretCode(tokens[3]);

		LOG.debug("Done Parsing PIN CHANGE request .... returning success.");

		txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
		txn.setNarrative("PARSING SECRET CODE CHANGE Successful");
		return txn;
	}

	private MessageTransaction parseAgentTransactionSummaryRequest(MessageTransaction txn, String[] tokens) {
		txn.setStatus(TransactionStatus.VEREQ);
		txn.setTransactionType(TransactionType.AGENT_SUMMARY);
		// reject if not agent
		LOG.debug("Setting agent number..");
		// 6*summary*{ddmmyy}
		LOG.debug("Parsing an AGENT SUMMARY Request");
		if (tokens == null || tokens.length < 2) {
			// a MINI REQUEST must have at least 2 parameters
			LOG.debug("Too few parameters for SUMMARY REQUEST");
			txn.setNarrative("Request Rejected. Invalid SUMMARY request. Please send " + tokens[0] + "*SUM for today's summary or " + tokens[0] + "*SUM*{ddMMyy} for a specific date.");
			return txn;
		}

		LOG.debug("Parse date if any...");
		Date fullDate;
		try {
			String ddmmyyDate = tokens[2];
			LOG.debug("Date supplied.. parse it: " + ddmmyyDate);
			if (ddmmyyDate.trim().length() != 6) {
				LOG.debug("Date is not of length 6.. REJECT");
				txn.setNarrative("Request Rejected. Date must be in the ddMMyy format including leading zeros e.g. " + tokens[0] + "*SUM*020512 stands for 2 May 2012.");
				return txn;
			}
			try {
				fullDate = Formats.short2DigitYearPlainDateFormat.parse(ddmmyyDate);
				LOG.debug("Date parsed successfully.. proceed");
			} catch (ParseException pe) {
				LOG.debug("Failed to parse date.. REJECT");
				txn.setNarrative("Request Rejected. Date must be in the ddMMyy format e.g. " + tokens[0] + "*SUM*020512 stands for 2 May 2012.");
				return txn;
			}

		} catch (Exception e) {
			LOG.debug("Date not supplied.. summary is for today..");
			fullDate = new Date();
		}
		LOG.debug("Setting this date to txn.." + fullDate);
		txn.setSummaryDate(fullDate);
		txn.setSourceBankAccount(txn.getSourceMobileNumber());
		txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
		LOG.debug("Done Parsing agent summary request.... returning SUCCESS ..");
		return txn;
	}

	private MessageTransaction parseAgentNonHolderWithdrawalRequest(MessageTransaction txn, String[] tokens) {
		LOG.debug("Processing an AGENT NONHOLDER WITHDRAWAL REQ");
		// 6*cash*{amount}*{reference}*{secretCode}
		if (tokens == null || tokens.length < 5) {
			// a cash out message must have at least 5 parameters
			LOG.debug("Too few parameters for cash out money");
			txn.setNarrative("Request Rejected. Invalid cash out request. To send money to a mobile " + tokens[0] + "*CASH*{amount}*{reference}*{secretCode}.");
			return txn;
		}
		txn.setStatus(TransactionStatus.VEREQ);
		txn.setTransactionType(TransactionType.AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL);
		LOG.debug("Setting the Agent account as Destination Account...");
		txn.setDestinationBankAccount(txn.getSourceMobileNumber());

		try {
			txn.setAmount(MoneyUtil.convertToCents(Double.parseDouble(tokens[2])));
		} catch (NumberFormatException e1) {
			LOG.debug("\n Invalid amount [" + tokens[2] + "]");
			e1.printStackTrace();
			txn.setNarrative("Request Rejected. " + tokens[2] + " is not a valid amount.");
			return txn;
		}

		LOG.debug("Validating additional parameters");
		txn.setOldMessageId(tokens[3]);
		txn.setSecretCode(tokens[4]);
		LOG.debug("Old Reference: " + txn.getOldMessageId() + " ....and Secret Code set: " + txn.getSecretCode());
		LOG.debug("Returning parse SUCCESS");
		txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
		txn.setNarrative("PARSING AGENT NON-HOLDER WDL Successful.");
		return txn;
	}

	private MessageTransaction parseAgentWithdrawalRequest(MessageTransaction txn, String[] tokens) {

		LOG.debug("Processing an AGENT WITHDRAWAL REQ");
		// 6*withdraw*{amount}*{agentNumber}
		if (tokens == null || tokens.length < 4) {
			// a cash WDL message must have at least 5 parameters
			LOG.debug("Too few parameters for cash out money");
			txn.setNarrative("Request Rejected. Invalid cash withdrawal request. To send money to a mobile " + tokens[0] + "*WITHDRAW*{amount}*{agentNumber}.");
			return txn;
		}
		txn.setStatus(TransactionStatus.VEREQ);
		txn.setTransactionType(TransactionType.AGENT_CUSTOMER_WITHDRAWAL);
		LOG.debug("Setting the source account...");
		txn.setSourceBankAccount(txn.getSourceMobileNumber());

		try {
			txn.setAmount(MoneyUtil.convertToCents(Double.parseDouble(tokens[2])));
		} catch (NumberFormatException e1) {
			LOG.debug("\n Invalid amount [" + tokens[2] + "]");
			e1.printStackTrace();
			txn.setNarrative("Request Rejected. " + tokens[2] + " is not a valid amount.");
			return txn;
		}

		LOG.debug("Validating agent registration");
		String agentNumber = tokens[3];
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		Agent agent = agentService.getAgentByAgentNumber(agentNumber);
		if (agent == null || agent.getId() == null) {
			txn.setNarrative("Request Rejected. Agent " + agentNumber + " is not a registered agent.");
			return txn;
		}
		LOG.debug("Setting agent number..");
		txn.setAgentNumber(agentNumber);
		LOG.debug("Agent number set to: " + txn.getAgentNumber());

		LOG.debug("Getting agent mobileProfile..");
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		List<MobileProfile> agentProfiles = customerService.getMobileProfileByCustomer(agent.getCustomerId());

		if (agentProfiles == null || agentProfiles.isEmpty()) {
			LOG.debug("No mobileProfiles found.. REJECT");
			txn.setNarrative("Request Rejected. Mobile phone for agent " + agentNumber + " is not registered.");
			return txn;
		}

		MobileProfile agentProfile = agentProfiles.get(0);

		LOG.debug("Checking if src is not target");
		if (txn.getSourceBankAccount().equals(agentProfile.getMobileNumber())) {
			LOG.debug("Src and Dest accounts are the same... REJECT");
			txn.setNarrative("Request Rejected. You cannot withdraw from your agent account.");
		}

		LOG.debug("Setting target account, target mobile..");
		txn.setDestinationBankAccount(agentProfile.getMobileNumber());
		txn.setTargetMobileNumber(agentProfile.getMobileNumber());
		txn.setTargetBankId(agentProfile.getBankId());

		LOG.debug("Returning parse SUCCESS");
		txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
		txn.setNarrative("PARSING AGENT WDL RQST Successful");
		return txn;
	}

	private MessageTransaction parseAgentDepositRequest(MessageTransaction txn, String[] tokens) {
		LOG.debug("Processing an AGENT DEPOSIT REQ");
		// 6*deposit*{amount}*{targetMobile}
		if (tokens == null || tokens.length < 4) {
			// a DEPOSIT message must have at least 5 parameters
			LOG.debug("Too few parameters for cash out money");
			txn.setNarrative("Request Rejected. Invalid deposit request. To send money to a mobile " + tokens[0] + "*WITHDRAW*{amount}*{agentNumber}.");
			return txn;
		}
		txn.setStatus(TransactionStatus.VEREQ);
		txn.setTransactionType(TransactionType.AGENT_CUSTOMER_DEPOSIT);
		LOG.debug("Setting the source account...");
		txn.setSourceBankAccount(txn.getSourceMobileNumber());
		LOG.debug("Validating agent registration");

		String targetMobile;
		try {
			targetMobile = NumberUtil.formatMobile(tokens[3]);
			LOG.debug("Setting target account, target Mobile");
			txn.setDestinationBankAccount(targetMobile);
			txn.setTargetMobileNumber(targetMobile);
		} catch (Exception e) {
			LOG.debug("Invalid target mobile");
			txn.setNarrative("Request Rejected. Target account is not an eWallet account.");
			return txn;
		}
		try {
			txn.setAmount(MoneyUtil.convertToCents(Double.parseDouble(tokens[2])));
		} catch (NumberFormatException e1) {
			LOG.debug("\n Invalid amount [" + tokens[2] + "]");
			e1.printStackTrace();
			txn.setNarrative("Request Rejected. " + tokens[2] + " is not a valid amount.");
			return txn;
		}

		LOG.debug("Returning parse SUCCESS");
		txn.setNarrative("PARSING AGENT DEPOSIT Request Successful.");
		txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
		return txn;
	}

	private Agent getAgentByMobileNumberAndBankId(String mobileNumber, String bankId) {
		try {
			CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
			AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
			MobileProfile mobileProfile = customerService.getMobileProfileByBankAndMobileNumber(bankId, mobileNumber);
			Agent agent = agentService.getAgentByCustomerId(mobileProfile.getCustomer().getId());
			return agent;
		} catch (Exception e) {
			return null;
		}
	}

	private MessageTransaction parseSendCashRequest(MessageTransaction txn, String[] tokens) {
		// 6*send*{amount}*[{targetMobile}*{secret code}]
		LOG.debug("Parsing a SEND Money Request ");
		if (tokens == null || tokens.length < 5) {
			// a send cash message must have at least 5 parameters
			LOG.debug("Too few parameters for send money");
			txn.setNarrative("Request Rejected. Invalid send money request. To send money to a mobile " + tokens[0] + "*SEND*{amount}*{targetmobile}*{secretCode}.");
			return txn;
		}
		txn.setStatus(TransactionStatus.VEREQ);
		txn.setTransactionType(TransactionType.TRANSFER);
		try {
			txn.setAmount(MoneyUtil.convertToCents(Double.parseDouble(tokens[2])));
		} catch (NumberFormatException e) {
			LOG.debug("Invalid amount " + tokens[2]);
			txn.setNarrative("Request Rejected. " + tokens[2] + " is not a valid amount.");
			return txn;
		}
		String targetMobile = tokens[3];
		try {
			targetMobile = NumberUtil.formatMobileNumber(targetMobile);
			LOG.debug("Target Mobile is there and is in the correct format... set it now " + targetMobile);
			txn.setTargetMobileNumber(targetMobile);
			txn.setDestinationBankAccount(targetMobile);
		} catch (Exception e) {
			LOG.debug("Invalid Target Mobile......" + targetMobile);
			txn.setNarrative("Request Rejected. " + targetMobile + " is not a target mobile number.");
			return txn;
		}
		txn.setSecretCode(tokens[4]);
		LOG.debug("Done Parsing SEND CASH request returning success...");
		txn.setNarrative("PARSING SEND MONEY Request Successful.");
		txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
		return txn;
	}

	private MessageTransaction parseMerchantRegistrationRequest(MessageTransaction txn, String[] tokens) {
		// 6*reg*{merchant}*{customerMerchantAccount}
		if (tokens == null || tokens.length < 4) {
			// a topup message must have at least 3 parameters
			LOG.debug("Too few parameters merchant registration");
			txn.setNarrative("Request Rejected. Invalid merchant registration request. To register send " + tokens[0] + "*reg*{merchant}*{merchantAccount}.");
			return txn;
		}
		//Checking merchant availability
		MerchantInfo mi = this.getMerchantConfigInfo(tokens[2]);
		if(mi != null){
			if(!mi.isOnline()){
				LOG.debug(tokens[2]+" Is not online ");
				txn.setNarrative("Request Rejected. " + tokens[2] + " is currently not available on ZB e-Wallet ");
				return txn;
			}
		}
		txn.setTransactionType(TransactionType.MERCHANT_REG);
		txn.setStatus(TransactionStatus.VEREQ);
		txn.setUtilityName(tokens[2]);
		txn.setCustomerUtilityAccount(tokens[3]);
		txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
		txn.setNarrative("PARSING MERCHANT REG Request Successful.");
		return txn;
	}

	private MessageTransaction parseBillPayRequest(MessageTransaction txn, String[] tokens) {
		// 6*pay*{merchant}*{amount}*[{customerMerchantAcount}]
		if (tokens == null || tokens.length < 4) {
			// a bill pay message must have at least 4 parameters
			LOG.debug("Too few parameters fot BILL PAY ");
			txn.setNarrative("Request Rejected. Invalid BILLPAY request. Please send " + tokens[0] + "*pay*{merchant}*{amount}*{merchantAccount}.");
			return txn;
		}
		txn.setTransactionType(TransactionType.BILLPAY);
		txn.setStatus(TransactionStatus.VEREQ);
		txn.setUtilityName(tokens[2]);
		try {
			txn.setAmount(MoneyUtil.convertToCents(Double.parseDouble(tokens[3])));
		} catch (NumberFormatException e) {
			LOG.debug("Invalid amount " + tokens[3]);
			txn.setNarrative("Request Rejected. " + tokens[3] + " is not a valid amount. Please send " + tokens[0] + "*pay*{merchant}*{amount}*{merchantAccount}.");
			return txn;
		}
		
		//Check DSTV Request
		if (EWalletConstants.MERCHANT_NAME_DSTV.equalsIgnoreCase(tokens[2])) {
			
			LOG.debug("Billpay is for DSTV..");

			LOG.debug("DStv tokens are: " + tokens.length);

			if (tokens.length == 6) {	
				LOG.debug("Processing own payment..");
				txn.setBouquetName(tokens[4]);
				try {
					txn.setNumberOfMonths(Integer.parseInt(tokens[5]));
				} catch (Exception e) {
					txn.setNarrative("Request Rejected. " + tokens[5] + " is not a valid no. of months. " +"Please send " + tokens[0] + "*pay*dstv*{amount}*{dstvAccount}*{bouquet}*{months}.");
					return txn;
				}
			} else if (tokens.length == 7) {
				LOG.debug("Processing a friend's payment..");
				txn.setCustomerUtilityAccount(tokens[4]);
				LOG.debug("Cutsomer utility account is:" + txn.getCustomerUtilityAccount());
				txn.setBouquetName(tokens[5]);
				try {
					txn.setNumberOfMonths(Integer.parseInt(tokens[6]));
				} catch (Exception e) {
					txn.setNarrative("Request Rejected. " + tokens[6] + " is not a valid no. of months. " +"Please send " + tokens[0] + "*pay*dstv*{amount}*{dstvAccount}*{bouquet}*{months}.");
					return txn;
				}			
			} else {
				LOG.debug("Too few parameters for DSTV Bill payment ");
				txn.setNarrative("Request Rejected. Invalid DStv BILLPAY request. Please send " + tokens[0] + "*pay*dstv*{amount}*{dstvAccount}*{bouquet}*{months}.");
				return txn;
			}

			LOG.debug("BOUQUET NAME is: " + txn.getBouquetName());
			
			String[] bouquetTokens = txn.getBouquetName().split("\\.");
			
			LOG.debug("Number of tokens in BOUQUET Name is:  " + bouquetTokens.length);

			Bouquet bouquet = this.getBouquetByNameAndType(bouquetTokens[0], BouquetType.BOUQUET.name());
			
			if (bouquet == null) {
				LOG.debug("BOUQUET not found..");
				txn.setNarrative("Request Rejected. Invalid DStv BILLPAY request. Reason: Bouquet " + bouquetTokens[0] + " not recognised.");
				return txn;
			}
			
			String bouquetCode = bouquet.getCode();
			
			if (bouquetTokens.length > 1) {
				
				LOG.debug("Bouquet HAS ADD ONs...");
				
				for (int i = 1; i < bouquetTokens.length; i++) {
				
					String addOnName = bouquetTokens[i];
					
					LOG.debug("ADD ON is :" + addOnName);
					
					Bouquet addOn = this.getBouquetByNameAndType(addOnName, BouquetType.ADD_ON.name());
					
					if (addOn == null) {
						LOG.debug("ADD ON not found..");
						txn.setNarrative("Request Rejected. Invalid DStv BILLPAY request. Reason: Add On " + addOnName + " not recognised.");
						return txn;
					}
					
					bouquetCode += "+" + addOn.getCode();
					
				}
			}
				
			txn.setBouquetCode(bouquetCode);
			
			LOG.debug("BOUQUET CODE: " + txn.getBouquetCode());
			LOG.debug("AMOUNT is: " + txn.getAmount());
			LOG.debug("MONTHS is: " + txn.getNumberOfMonths());

		} else {
			
			LOG.debug("NOT DStv request.. nw adding customer utility account..");
			
			try {
				txn.setCustomerUtilityAccount(tokens[4]);
				LOG.debug("Target Customer Utility Account Specified : " + txn.getCustomerUtilityAccount());
			} catch (Exception e) {
				// not specified, will go get the registered utility account for
				// this customer on the specified merchant
				LOG.debug("No merchant account specified, will get the one registered for the customer.");
			}
			
		}
		
		//Checking merchant availability
		MerchantInfo mi = this.getMerchantConfigInfo(tokens[2]);
		if(mi != null){
			if(!mi.isOnline()){
				LOG.debug(tokens[2]+" Is not online ");
				txn.setNarrative("Request Rejected. " + tokens[2] + " is currently not available on ZB e-Wallet ");
				return txn;
			}
		}
		
		LOG.debug("Done parsing Utility Bill Payment...");
		txn.setNarrative("PARSING BILLPAY Request successful");
		txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
		return txn;
	}

	private MessageTransaction parsePINChangeRequest(MessageTransaction txn, String[] tokens) {
		// 6*pin*{oldPin}*{newPin}
		if (tokens == null || tokens.length < 4) {
			// a pin change request must have at least 4 parameters
			LOG.debug("Too few parameters for PIN CHANGE ");
			txn.setNarrative("Request Rejected. Invalid PIN CHANGE request. Please send " + tokens[0] + "*PIN*{oldPin}*{newPin}.");
			return txn;
		}
		txn.setStatus(TransactionStatus.VEREQ);
		txn.setTransactionType(TransactionType.CHANGE_PASSCODE);
		String oldPin = tokens[2];

		LOG.debug("Mobile Number : " + txn.getSourceMobileNumber() + " and BANK " + txn.getSourceBankId());
		try {
			if (this.isPinValid(oldPin)) {
				txn.setOldPin(oldPin);
			} else {
				LOG.debug("Invalid old pin : " + tokens[2]);
				txn.setNarrative("Request Rejected. Invalid old pin, " + tokens[2]);
				return txn;
			}
		} catch (Exception e1) {
			LOG.debug("Invalid old pin : " + e1.getMessage());
			txn.setNarrative("Request Rejected. Invalid old pin, " + e1.getMessage());
			return txn;

		}
		String newPin = tokens[3];
		try {
			if (this.isPinValid(newPin)) {
				txn.setNewPin(newPin);
			} else {
				LOG.debug("Invalid new pin : " + tokens[3]);
				txn.setNarrative("Request Rejected. Invalid new pin, " + tokens[2]);
				return txn;
			}
		} catch (Exception e1) {
			LOG.debug("Invalid new pin : " + e1.getMessage());
			txn.setNarrative("Request Rejected. Invalid new pin, " + e1.getMessage());
			return txn;
		}
		if (txn.getOldPin().equalsIgnoreCase(txn.getNewPin())) {
			LOG.debug("Old pin and new pin are similar");
			txn.setNarrative("Request Rejected. Invalid PIN Change request, old pin and new pin must not be the same.");
			return txn;
		}
		LOG.debug("Done Parsing PIN CHANGE request .... returning success.");
		txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
		txn.setNarrative("PARSING PINCHANGE Request Successful.");
		return txn;
	}

	private boolean isPinValid(String pin) throws Exception {
		// check if its numeric
		try {
			Long.parseLong(pin);
		} catch (Exception e) {
			throw new Exception("PIN must be numeric.");
		}
		if (pin.length() != 5) {
			// invalid pin length
			throw new Exception("PIN must be a 5 digit number.");
		}
		return true;

	}

	private MessageTransaction parseMiniRequest(MessageTransaction txn, String[] tokens) {
		// 6*mini*{accountNumber}
		LOG.debug("Parsing a MINI STATEMENT Request");
		if (tokens == null || tokens.length < 2) {
			// a MINI REQUEST must have at least 2 parameters
			LOG.debug("Too few parameters for MINI REQUEST");
			txn.setNarrative("Request Rejected. Invalid MINI request. Please send " + tokens[0] + "*MINI*{accountNumber}.");
			return txn;
		}
		txn.setStatus(TransactionStatus.VEREQ);
		txn.setTransactionType(TransactionType.MINI_STATEMENT);
		try {
			String srcBankAccountNumber = tokens[2];
			try {
				srcBankAccountNumber = NumberUtil.formatMobileNumber(tokens[2]);
				LOG.debug("DEST is there and is in the correct format... set it now " + srcBankAccountNumber);
				txn.setSourceBankAccount(srcBankAccountNumber);
			} catch (Exception e) {
				LOG.debug("DEST is not an eWallet Account Invalid Target Mobile......" + tokens[2]);
				txn.setSourceBankAccount(tokens[2]);
			}
		} catch (Exception e) {
			LOG.debug("Account Number is not set, will go and get primary");
		}
		LOG.debug("Done Parsing mini statement request.... returning success ..");
		txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
		txn.setNarrative("PARSING MINI STATEMENT request successful.");

		return txn;
	}

	private MessageTransaction parseTopupRequest(MessageTransaction txn, String[] tokens) {
		// 6*topup*{amount}*[{targetMobile}]
		LOG.debug("Parsing a TOPUP Request ");
		if (tokens == null || tokens.length < 3) {
			// a topup message must have at least 3 parameters
			LOG.debug("Too few parameters for topup");
			txn.setNarrative("Request Rejected. Invalid topup request. To topup send " + tokens[0] + "*TOPUP*{amount}*{targetmobile}.");
			return txn;
		}
		txn.setStatus(TransactionStatus.VEREQ);
		txn.setTransactionType(TransactionType.TOPUP);
		try {
			txn.setAmount(MoneyUtil.convertToCents(Double.parseDouble(tokens[2])));
		} catch (NumberFormatException e) {
			LOG.debug("Invalid amount " + tokens[2]);
			txn.setNarrative("Request Rejected. " + tokens[2] + " is not a valid amount.");
			return txn;
		}
		String targetMobile;
		try {
			targetMobile = tokens[3];
			try {
				targetMobile = NumberUtil.formatMobileNumber(targetMobile);
				LOG.debug("Target Mobile is there and is in the correct format... set it now " + targetMobile);
				txn.setTargetMobileNumber(targetMobile);
			} catch (Exception e) {
				LOG.debug("Invalid Target Mobile......" + targetMobile);
				LOG.debug("Invalid amount " + tokens[2]);
				txn.setNarrative("Request Rejected. " + targetMobile + " is not a target mobile number.");
			}
		} catch (Exception e) {
			LOG.debug("Target mobile is empty, we go get the source and set it to target, customer is toping up SELF");
			txn.setTargetMobileNumber(txn.getSourceMobileNumber());
		}
		LOG.debug("Done Parseing topup request returning success...");
		txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
		txn.setNarrative("PARSING TOPUP request successful.");
		return txn;
	}

	private MessageTransaction parseTransferRequest(MessageTransaction txn, String[] tokens) {
		// 6*txf*amt*src*dest{*sec_code}
		if (tokens.length < 5) {
			LOG.debug("Too FEW parameters for transfer request");
			txn.setNarrative("Request Rejected: Invalid transfer request. Please submit " + txn.getSourceBankPrefix() + "*HELP*TRANSFER to 440");
			return txn;
		}
		if (tokens.length > 6) {
			LOG.debug("Too MANY parameters for transfer request");
			txn.setNarrative("Request Rejected: Invalid transfer request. Please submit " + txn.getSourceBankPrefix() + "*HELP*TRANSFER to 440");
			return txn;
		}
		txn.setStatus(TransactionStatus.VEREQ);
		txn.setTransactionType(TransactionType.TRANSFER);
		try {
			txn.setAmount(MoneyUtil.convertToCents(Double.parseDouble(tokens[2])));
		} catch (NumberFormatException e1) {
			LOG.debug("\n Invalid amount [" + tokens[2] + "]");
			e1.printStackTrace();
			txn.setNarrative("Request Rejected. " + tokens[2] + " is not a valid amount.");
			return txn;
		}

		if (KeyWordUtil.isEwallet(tokens[3])) {
			txn.setSourceBankAccount(txn.getSourceMobileNumber());
		} else {
			try {
				String srcBankAccountNumber = NumberUtil.formatMobileNumber(tokens[3]);
				LOG.debug("SRC is there and is in the correct format... set it now " + srcBankAccountNumber);
				txn.setSourceBankAccount(srcBankAccountNumber);
			} catch (Exception e) {
				LOG.debug("SRC is not an eWallet Account Invalid Target Mobile......" + tokens[3]);
				txn.setSourceBankAccount(tokens[3]);
			}
			
		}

		if (KeyWordUtil.isEwallet(tokens[4])) {
			txn.setDestinationBankAccount(txn.getSourceMobileNumber());
		} else {
			try {
				String destBankAccountNumber = NumberUtil.formatMobileNumber(tokens[4]);
				LOG.debug("DEST is there and is in the correct format... set it now " + destBankAccountNumber);
				txn.setDestinationBankAccount(destBankAccountNumber);
				txn.setTargetMobileNumber(destBankAccountNumber);
			} catch (Exception e) {
				LOG.debug("DEST is not an eWallet Account Invalid Target Mobile......" + tokens[4]);
				txn.setDestinationBankAccount(tokens[4]);
			}
			
		}

		LOG.debug("SRC ACC NUMBER : [" + txn.getSourceBankAccount() + "] DEST ACC NUMBER : [" + txn.getDestinationBankAccount() + "]");

		if (txn.getSourceBankAccount().equalsIgnoreCase(txn.getDestinationBankAccount())) {
			LOG.debug("Source and dest account is the same.[" + txn.getSourceBankAccount() + "|" + txn.getDestinationBankAccount() + "]");
			txn.setNarrative("Request Rejected. Source and destination accounts cannot be the same.");
			return txn;
		}

		try {
			txn.setSecretCode(tokens[5]);
			LOG.debug("Transfer with secret code [" + tokens[5] + "]");
		} catch (Exception e) {
			LOG.debug("Transfer HAS NO Secret Code : Assume its valid, will catch it when we validate REG ");
		}
		LOG.debug("Parsing response....  SUCCESS");
		txn.setNarrative("PARSING TRANSFER Successful.");
		txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
		return txn;

	}

	private MessageTransaction parseBalanceRequest(MessageTransaction txn, String[] tokens) {
		txn.setTransactionType(TransactionType.BALANCE);
		txn.setStatus(TransactionStatus.VEREQ);
		LOG.debug("Found a balance enquiry");
		try {
			if (KeyWordUtil.isEwallet(tokens[2])) {
				txn.setTargetMobileNumber(txn.getSourceMobileNumber());
				txn.setSourceBankAccount(txn.getSourceMobileNumber());
			} else {
				try {
					String srcBankAccountNumber = NumberUtil.formatMobileNumber(tokens[2]);
					LOG.debug("DEST is there and is in the correct format... set it now " + srcBankAccountNumber);
					txn.setSourceBankAccount(srcBankAccountNumber);
				} catch (Exception e) {
					LOG.debug("DEST is not an eWallet Account Invalid Target Mobile......" + tokens[2]);
					txn.setSourceBankAccount(tokens[2]);
				}				
			}
			LOG.debug("The bank account is : [" + txn.getTargetMobileNumber() + "]");
		} catch (Exception e) {
			LOG.debug("The bank account is not set, go for the primary after parse");
		}
		txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
		txn.setNarrative("Parsing BALANCE RQST successful.");
		return txn;
	}

	private String parseHelpMessage(MessageTransaction msgTxn, String tokens[]) {
		// 6*HELP
		if (tokens.length <= 2) {
			return this.getDefaultHelpMessage(msgTxn);
		}
		String txnType = tokens[2];
		if (SystemConstants.SMS_REQUEST_TYPE_BALANCE_ENQUIRY.equalsIgnoreCase(txnType) || SystemConstants.SMS_REQUEST_TYPE_BALANCE_ENQUIRY_LONG.equalsIgnoreCase(txnType)) {
			String help = "BALANCE HELP[nl]";
			help += "To check your bank account balance: ";
			help += msgTxn.getSourceBankPrefix() + "*bal*{accountNumber}[nl]";
			help += "To check your e-Wallet balance: ";
			help += msgTxn.getSourceBankPrefix() + "*bal*ewallet[nl]";
			return help;
		}
		if (SystemConstants.SMS_REQUEST_TYPE_BILLPAYMENT.equalsIgnoreCase(txnType) || "BILLPAY".equalsIgnoreCase(txnType)) {
			String help = "BILLPAY HELP[nl]: ";
			help += "To pay your bill: ";
			help += msgTxn.getSourceBankPrefix() + "*pay*{utilityName}*{amount}[nl]";
			help += "To pay someone's bill: ";
			help += msgTxn.getSourceBankPrefix() + "*pay*{utilityName}*{amount}*{utilityAccount}[nl]";
			return help;
		}
		if (SystemConstants.SMS_REQUEST_TYPE_TOPUP.equalsIgnoreCase(txnType)) {
			String help = "TOPUP HELP[nl]: ";
			help += "To topup yourself: ";
			help += msgTxn.getSourceBankPrefix() + "*topup*{amount}[nl]";
			help += "To topup someone else: ";
			help += msgTxn.getSourceBankPrefix() + "*topup*{amount}*{number to topup}[nl]";
			return help;
		}
		if (SystemConstants.SMS_REQUEST_TYPE_TRANSFER.equalsIgnoreCase(txnType) || SystemConstants.SMS_REQUEST_TYPE_TRANSFER_SHORT.equalsIgnoreCase(txnType) || SystemConstants.SMS_REQUEST_TYPE_TRANSFER_TRF.equalsIgnoreCase(txnType)) {
			String help = "TRF HELP[nl]: ";
			help += "To transfer : ";
			help += msgTxn.getSourceBankPrefix() + "*transfer*{amount}*{sourceAccount}*{beneficiaryAccount}[nl]";
			help += "To send money to a mobile: ";
			help += msgTxn.getSourceBankPrefix() + "*transfer*{amount}*{sourceAccount}*{beneficiaryMobile}*{secretCode}[nl]";
			return help;
		}
		if (SystemConstants.SMS_REQUEST_TYPE_REGISTRATION.equalsIgnoreCase(txnType)) {
			// 6*reg*{merchant}*{customerMerchantAccount}
			String help = "UTILITY REG HELP[nl]: ";
			help += "To register your utility account: ";
			help += msgTxn.getSourceBankPrefix() + "*REG*{UTILITY NAME}*{ACCOUNT_NUMBER}[nl]";
			return help;
		}
		if (SystemConstants.SMS_REQUEST_TYPE_DSTV_BILLPAY.equalsIgnoreCase(txnType)) {
			String help = "DSTV HELP[nl]: ";
			help += "To subscribe: ";
			help += msgTxn.getSourceBankPrefix() + "*pay*dstv*{amount}*{bouquet}*{months}[nl]";
			help += "To pay for someone: ";
			help += msgTxn.getSourceBankPrefix() + "*pay*dstv*{amount}*{dstvAccount}*{bouquet}*{months}[nl]";
			return help;
		}
		return this.getDefaultHelpMessage(msgTxn);
	}

	private String getDefaultHelpMessage(MessageTransaction msgTxn) {
		String help = "For help on a specific txn send : " + msgTxn.getSourceBankPrefix() + "*help*{TXN_TYPE} to 440. TXN can be TOPUP or TRANSFER or BAL or MINI or BILLPAY or PINCHANGE or DSTV";
		return help;
	}

	@Override
	public MessageTransaction populatePAREQInfo(MessageTransaction txn){

		if (txn.getSourceMobileNumber() == null || "".equalsIgnoreCase(txn.getSourceMobileNumber().trim())) {
			LOG.debug("Invalid source account");
			txn.setNarrative("Request Rejected : Invalid source mobile.");
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			return txn;
		}

		LOG.debug("Trying to get the source bank id for bank : " + txn.getSourceBankPrefix());

		BankInfo bankInfo = this.getBankConfigInfo(txn.getSourceBankPrefix());

		if (bankInfo == null) {
			LOG.fatal("Invalid Bank Config for BANK PREFIX [" + txn.getSourceBankPrefix() + "]");
			txn.setNarrative("Request Rejected : Bank " + txn.getSourceBankPrefix() + " is not properly configured for SMS");
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			return txn;
		}

		LOG.debug("Get profile for MOBILE : " + txn.getSourceMobileNumber() + ", bank id:" + bankInfo.getBankId());

		txn = this.checkSourceMobileProfileRegistration(txn, bankInfo);
		if (!SystemConstants.RESPONSE_CODE_OK_SHORT.equalsIgnoreCase(txn.getResponseCode())) {
			LOG.debug("Mobile Profile REG Verification Failed : Return to sender");
			return txn;
		}
		return txn;
	}
	
	@Override
	public MessageTransaction checkRegistration(MessageTransaction txn) {

		if (txn.getSourceMobileNumber() == null || "".equalsIgnoreCase(txn.getSourceMobileNumber().trim())) {
			LOG.debug("Invalid source account");
			txn.setNarrative("Request Rejected : Invalid source mobile.");
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			return txn;
		}

		LOG.debug("Trying to get the source bank id for bank : " + txn.getSourceBankPrefix());

		BankInfo bankInfo = this.getBankConfigInfo(txn.getSourceBankPrefix());

		if (bankInfo == null) {
			LOG.fatal("Invalid Bank Config for BANK PREFIX [" + txn.getSourceBankPrefix() + "]");
			txn.setNarrative("Request Rejected : Bank " + txn.getSourceBankPrefix() + " is not properly configured for SMS");
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			return txn;
		}

		LOG.debug("Get profile for MOBILE : " + txn.getSourceMobileNumber() + ", bank id:" + bankInfo.getBankId());

		txn = this.checkSourceMobileProfileRegistration(txn, bankInfo);
		if (!SystemConstants.RESPONSE_CODE_OK_SHORT.equalsIgnoreCase(txn.getResponseCode())) {
			LOG.debug("Mobile Profile REG Verification Failed : Return to sender");
			return txn;
		}

		if (TransactionType.CHANGE_PASSCODE.equals(txn.getTransactionType())) {
			LOG.debug("TXN is PIN Change, no need to validate bank accounts");
		} else if (TransactionType.PASSCODE.equals(txn.getTransactionType())) {
			LOG.debug("TXN is PIN Change, no need to validate bank accounts");
		} else {
			txn = this.checkBankAccountsRegistration(txn, bankInfo);
			if (!SystemConstants.RESPONSE_CODE_OK_SHORT.equalsIgnoreCase(txn.getResponseCode())) {
				LOG.debug("BANK Accounts REG Verification Failed : Return to sender");
				return txn;
			}
		}
		ReferenceGeneratorServiceSOAPProxy proxy = new ReferenceGeneratorServiceSOAPProxy();
		String date = Formats.yearFormatTwoDigit.format(new Date(System.currentTimeMillis()));
		MobileNetworkOperator mno = txn.getMno();
		txn.setUuid(proxy.generateUUID(mno.name(), mno.name().substring(0, 1), date, 0L, 1000000000L - 1L));

		txn = this.populateRoutingInfo(txn, bankInfo);
		LOG.debug("Done populating router info for txn : " + txn.getUuid() +"|" + txn.getTransactionType() +"|" );
		return txn;
	}

	private MessageTransaction populateRoutingInfo(MessageTransaction txn, BankInfo bankInfo) {
		TransactionRoutingInfo txnRoutingInfo = txn.getTransactionRoutingInfo();
		if (txnRoutingInfo == null) {
			txnRoutingInfo = new TransactionRoutingInfo();
		}
		txnRoutingInfo.setApplyVendorSignature(bankInfo.isApplySignature());
		txnRoutingInfo.setBankName(bankInfo.getBankName());
		txnRoutingInfo.setBankReplyQueueName(bankInfo.getReplyQueueName());
		txnRoutingInfo.setBankRequestQueueName(bankInfo.getRequestQueueName());
		txnRoutingInfo.setZesaPayBranch(bankInfo.getZesaPayBranch());
		txnRoutingInfo.setZesaPayCode(bankInfo.getZesaPayCode());
		txnRoutingInfo.setZswCode(bankInfo.getZswCode());
		txn.setTransactionRoutingInfo(txnRoutingInfo);
		TransactionType txnType = txn.getTransactionType();
		if (TransactionType.BILLPAY.equals(txnType) || TransactionType.MERCHANT_REG.equals(txnType)) {
			MerchantInfo merchantInfo = this.getMerchantConfigInfo(txn.getUtilityName());
			if (merchantInfo != null) {
				txnRoutingInfo.setAccountValidationEnabled(merchantInfo.isEnableAccountValidation());
				txnRoutingInfo.setNotificationEnabled(merchantInfo.isEnableNotification());
				txnRoutingInfo.setStraightThroughEnabled(merchantInfo.isEnableStraightThrough());
				txnRoutingInfo.setMerchantReplyQueueName(merchantInfo.getReplyQueueName());
				txnRoutingInfo.setMerchantRequestQueueName(merchantInfo.getRequestQueueName());
			}
		}
		return txn;
	}

	private MessageTransaction checkBankAccountsRegistration(MessageTransaction txn, BankInfo bankInfo) {
		// check bank accounts here
		if (txn.getSourceBankAccount() == null || "PRI".equalsIgnoreCase(txn.getSourceBankAccount())) {
			LOG.debug("SRC ACC is not specified : GO FOR THE PRIMARY");
			OwnerType ownerType = OwnerType.CUSTOMER;
			CustomerServiceSOAPProxy  customerService = new CustomerServiceSOAPProxy();
			Customer customer = customerService.findCustomerById(txn.getCustomerId());
			if (CustomerClass.AGENT.equals(customer.getCustomerClass())) {
				LOG.debug("Customer is an AGENT.. setting ownerType to AGENT");
				ownerType = OwnerType.AGENT;
			}
			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
			BankAccount priBankAccount = bankService.getPrimaryAccountByAccountHolderIdAndOwnerType(true, customer.getId(), ownerType);
			if (priBankAccount != null && priBankAccount.getId() != null) {
				txn.setSourceBankAccount(priBankAccount.getAccountNumber());
			} else {
				LOG.debug("No primary bank account defined.");
				txn.setNarrative("Request Rejected : No primary account registered. Please contact your nearest branch.");
				txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
				return txn;
			}
		}

		TransactionType txnType = txn.getTransactionType();
		if (TransactionType.AGENT_CUSTOMER_DEPOSIT.equals(txnType)) {
			txn = this.validateAgentCustomerDeposit(txn, bankInfo);
			return txn;
		}
		
		if(TransactionType.AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL.equals(txnType)){
			txn = this.validateAgentNonHolderWithdrawal(txn, bankInfo);
			return txn;
		}

		if (TransactionType.AGENT_CUSTOMER_WITHDRAWAL.equals(txnType)) {
			txn = this.validateAgentCustomerWithdrawal(txn);
			return txn;
		}

		if (TransactionType.TRANSFER.equals(txnType)) {
			txn = this.validateTransfer(txn);
			return txn;
		}
		if (TransactionType.BILLPAY.equals(txnType)) {
			txn = this.validateBillPay(txn);
			return txn;
		}
		if (TransactionType.MERCHANT_REG.equals(txnType)) {
			txn = this.validateMerchantReg(txn);
			return txn;
		}
		if (TransactionType.AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL.equals(txnType)) {
			LOG.debug("This is an AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL txn... skip verify sourceAccount");
			return txn;
		}
		if (TransactionType.BALANCE.equals(txnType) || TransactionType.MINI_STATEMENT.equals(txnType)) {
			return txn;
		}
		return txn;

	}

	private MessageTransaction validateAgentCustomerWithdrawal(MessageTransaction txn) {
		return txn;
	}

	private MessageTransaction validateAgentCustomerDeposit(MessageTransaction txn, BankInfo bankInfo) {
		LOG.debug("Validate AGENT CUSTOMER DEPOSIT : ");
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		BankAccount targetBankAccount = bankService.getUniqueBankAccountByAccountNumberAndBankId(txn.getTargetMobileNumber(), bankInfo.getBankId());
		String targetFIId;
		if(targetBankAccount == null || targetBankAccount.getId() == null){
			targetBankAccount = bankService.getUniqueBankAccountByAccountNumberAndBankId(txn.getTargetMobileNumber(), bankInfo.getAlternateBankId());
			if(targetBankAccount == null || targetBankAccount.getId() == null){
				txn.setNarrative("Target account not registered");
				txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
				return txn;
			}
			LOG.debug("TARGET FI is alternate:");
			targetFIId = bankInfo.getAlternateBankId();
		}else{
			LOG.debug("TARGET FI is MAIN:");
			targetFIId = bankInfo.getBankId();
		}
		
		txn.setTargetBankId(targetFIId);
		txn.setDestinationBankAccount(txn.getTargetMobileNumber());
		LOG.debug("Target FI ID : " + txn.getTargetBankId() + " | Target BA : " + txn.getDestinationBankAccount());
		return txn;
	}

	private MessageTransaction validateAgentNonHolderWithdrawal(MessageTransaction txn, BankInfo bankInfo){
		LOG.debug("Setting agent number..");
	
		Agent agent = this.getAgentByMobileNumberAndBankId(txn.getSourceMobileNumber(), txn.getSourceBankId());
		
		if (agent != null) {
			txn.setAgentNumber(agent.getAgentNumber());
		} else {
			txn.setNarrative("Request Rejected. Agent is not registered properly. Contact your nearest branch for help.");
			return txn;
		}
		LOG.debug("Agent number set to: " + txn.getAgentNumber());

		return txn;
	}
	
	private MessageTransaction validateMerchantReg(MessageTransaction txn) {
		String customerUtilityAccount = txn.getCustomerUtilityAccount();
		if (customerUtilityAccount == null || "".equalsIgnoreCase(customerUtilityAccount)) {
			LOG.debug("NO MERCHANT merchant account fail to the merchant reg [" + txn.getCustomerUtilityAccount() +"]");
			txn.setNarrative("Request Rejected : You have not registered an account with " + txn.getUtilityName() + " on your ZB e-Wallet.");
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			return txn;
		} else {
			CustomerMerchant customerMerchant;
			try {
				MerchantServiceSOAPProxy merchantService = new MerchantServiceSOAPProxy();
				customerMerchant = merchantService.getCustomerMerchantByCustomerIdAndMerchantShortNameAndStatus(txn.getCustomerId(), txn.getUtilityName(), CustomerMerchantStatus.ACTIVE);
				if (customerMerchant == null || customerMerchant.getId() == null) {
					LOG.debug("Proceed With Reg");
				} else {
					txn.setNarrative("Request Rejected : You already have an account registered for " + txn.getUtilityName() + " on your ZB e-Wallet.");
					txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
					return txn;
				}
			} catch (zw.co.esolutions.ewallet.merchantservices.service.Exception_Exception e) {
				LOG.debug("NO MERCHANT merchant account " + txn.getCustomerUtilityAccount());
			}
		}
		return txn;
	}

	private MessageTransaction validateBillPay(MessageTransaction txn) {
		String customerUtilityAccount = txn.getCustomerUtilityAccount();
		if (customerUtilityAccount == null || "".equalsIgnoreCase(customerUtilityAccount)) {
			LOG.debug("No customer utility acccount specified. Look for one registered");
			CustomerMerchant customerMerchant;
			try {
				MerchantServiceSOAPProxy merchantService = new MerchantServiceSOAPProxy();
				customerMerchant = merchantService.getCustomerMerchantByCustomerIdAndMerchantShortNameAndStatus(txn.getCustomerId(), txn.getUtilityName(), CustomerMerchantStatus.ACTIVE);
			} catch (zw.co.esolutions.ewallet.merchantservices.service.Exception_Exception e) {
				LOG.debug("NO MERCHANT merchant account " + txn.getCustomerUtilityAccount());
				txn.setNarrative("Request Rejected : You have not registered an account with " + txn.getUtilityName() + " on your ZB e-Wallet.");
				txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
				return txn;
			}
			if (customerMerchant != null && customerMerchant.getId() != null) {
				txn.setCustomerUtilityAccount(customerMerchant.getCustomerAccountNumber());
				LOG.debug("Found a registered customer merchant account " + txn.getCustomerUtilityAccount());
				return txn;
			} else {
				LOG.debug("NO MERCHANT merchant account " + txn.getCustomerUtilityAccount());
				txn.setNarrative("Request Rejected : You have not registered an account with " + txn.getUtilityName() + " on your ZB e-Wallet.");
				txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
				return txn;
			}
		} else {
			LOG.debug("MERCHANT ACC was specified in txn request " + txn.getCustomerUtilityAccount());
			return txn;
		}
	}

	private MessageTransaction validateTransfer(MessageTransaction txn) {
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		BankAccount sourceBankAcc = bankService.getUniqueBankAccountByAccountNumberAndBankId(txn.getSourceBankAccount(), txn.getSourceBankId());
		if (sourceBankAcc == null || sourceBankAcc.getId() == null) {
			LOG.debug("Source Bank Account was not found");
			txn.setNarrative("Request rejected : Bank Account " + txn.getSourceBankAccount() + " is not registered.");
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			return txn;
		}
		
//		MobileProfile cp = new CustomerServiceSOAPProxy().findMobileProfileById();
		if(!sourceBankAcc.getAccountHolderId().equals(txn.getCustomerId())){
			LOG.debug("Bank Account does not belong to Customer");
			txn.setNarrative("Request Rejected : Source Account specified is not registered " + txn.getSourceBankAccount() + " on your ZB e-Wallet.");
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			return txn;
		}
		
		boolean isSourceEwallet = false;
		if(BankAccountType.E_WALLET.equals(sourceBankAcc.getType()) || BankAccountType.AGENT_EWALLET.equals(sourceBankAcc.getType())){
			isSourceEwallet = true;
		}
		LOG.debug("IS SRC EWT : " + isSourceEwallet);

		boolean isTargetEwallet;
		try {
			txn.setDestinationBankAccount(NumberUtil.formatMobileNumber(txn.getDestinationBankAccount()));
			isTargetEwallet = true;
		} catch (Exception e) {
			isTargetEwallet = false;
		}

		boolean isTargetRegistered;
		boolean requiresSecretCode;
		BankAccount destBankAcc = bankService.getUniqueBankAccountByAccountNumber(txn.getDestinationBankAccount());
		if (destBankAcc == null || destBankAcc.getId() == null) {
			isTargetRegistered = false;
			requiresSecretCode = true;
		} else {
			isTargetRegistered = true;
			isTargetEwallet = BankAccountType.E_WALLET.equals(destBankAcc.getType());
		}
		txn.setTransferType(TransferType.INTRABANK);
		if (isSourceEwallet) {
			if (isTargetEwallet) {
				if (isTargetRegistered) {
					txn.setTargetBankId(destBankAcc.getBranch().getBank().getId());
					txn.setTransactionType(TransactionType.EWALLET_TO_EWALLET_TRANSFER);
					txn.setTargetMobileNumber(txn.getDestinationBankAccount());
					requiresSecretCode = false;
				} else {
					txn.setTargetBankId(txn.getSourceBankId());
					txn.setTransactionType(TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER);
					requiresSecretCode = true;
				}
			} else {
				txn.setTargetBankId(txn.getSourceBankId());
				txn.setTransactionType(TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER);
				requiresSecretCode = false;
			}
		} else {
			if (isTargetEwallet) {
				if (isTargetRegistered) {
					txn.setTargetBankId(destBankAcc.getBranch().getBank().getId());
					txn.setTransactionType(TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER);
					txn.setTargetMobileNumber(txn.getDestinationBankAccount());
					requiresSecretCode = false;
				} else {
					txn.setTargetBankId(txn.getSourceBankId());
					txn.setTransactionType(TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER);
					requiresSecretCode = true;
				}
			} else {
				txn.setTargetBankId(txn.getSourceBankId());
				txn.setTransactionType(TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER);
				requiresSecretCode = false;
			}

		}
		if (requiresSecretCode) {
			if (txn.getSecretCode() == null || "".equalsIgnoreCase(txn.getSecretCode())) {
				LOG.debug("TXN Requires secret code but it is not specified : FAIL THE TXN");
				txn.setResponseCode(SystemConstants.RESPONSE_CODE_GENERAL_FAILURE);
				txn.setNarrative("Request rejected : Transfer to non holder requires secret code.");
				return txn;
			} else {
				LOG.debug("Secret code is required and is specified");
			}
		}
		return txn;
	}

	private MessageTransaction checkSourceMobileProfileRegistration(MessageTransaction txn, BankInfo bankInfo) {
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		MobileProfile profile = customerService.getMobileProfileByBankAndMobileNumber(bankInfo.getBankId(), txn.getSourceMobileNumber());
		boolean isMobileRegistered = false;

		if (profile == null || profile.getId() == null) {
			LOG.debug("Mobile Profile not registered on primary FI : try alternate");
			if (bankInfo.getAlternateBankId() != null) {
				profile = customerService.getMobileProfileByBankAndMobileNumber(bankInfo.getAlternateBankId(), txn.getSourceMobileNumber());
				if (profile == null || profile.getId() == null) {
					isMobileRegistered = false;
				} else {
					isMobileRegistered = true;
				}
			} else {
				isMobileRegistered = false;
			}

		} else {
			isMobileRegistered = true;
		}
		if (!isMobileRegistered) {
			LOG.debug("MOBILE PROFILE is not registered : " + txn.getSourceMobileNumber());
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			txn.setNarrative("Request Rejected: Your phone is not registered. Please contact your nearest branch.");
			return txn;
		}
		LOG.debug("Status : " + profile.getStatus() + " MOB NO :" + profile.getMobileNumber() + " bankId " + profile.getBankId());
		LOG.debug("Mobile Profile found = " + profile);
		// process locked first.
		if (MobileProfileStatus.LOCKED.equals(profile.getStatus())) {
			LOG.debug("Mobile Profile Locked...");
			boolean after;
			try {
				after = new Date().after(DateUtil.convertToDate(profile.getTimeout()));
			} catch (Exception e1) {
				//This is a deliberate bug.. Pliz don't remove this bug
				after = true;
			} 
			if (after) {
				profile.setStatus(MobileProfileStatus.ACTIVE);
				profile.setPasswordRetryCount(0);
				try {
					customerService.updateMobileProfile(profile, EWalletConstants.SYSTEM);
				} catch (Exception_Exception e) {
					LOG.debug("MOBILE PROFILE is LOCKED, LOCK TIMEOUT has yet expired but FAILED to auto activate : " + txn.getSourceMobileNumber());
					txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
					txn.setNarrative("Request Rejected: Your phone is currently locked. Please contact your nearest branch.");
					return txn;
				}
				LOG.debug("Mobile Profile unloked on timeout.");
			} else {
				LOG.debug("MOBILE PROFILE is LOCKED and LOCK TIMEOUT has not yet expired : " + txn.getSourceMobileNumber());
				txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
				txn.setNarrative("Request Rejected: Your phone is currently locked. Please contact your nearest branch.");
				return txn;
			}
		}

		// set source mobile profile
		txn.setSourceMobileId(profile.getId());
		txn.setSourceBankId(profile.getBankId());

		// if we get here the customer is certainly not locked, try not active
		if (!MobileProfileStatus.ACTIVE.equals(profile.getStatus())) {
			LOG.debug("MOBILE PROFILE is NOT ACTIVE : " + profile.getStatus());
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			txn.setNarrative("Request Rejected: Your phone has not been activated. Please contact your nearest branch.");
			return txn;
		}

		// we get here our profile is active.

		// now for the customer
		Customer customer = profile.getCustomer();
		if (!CustomerStatus.ACTIVE.equals(customer.getStatus())) {
			// customer not active.
			LOG.debug("MOBILE PROFILE is LOCKED and LOCK TIMEOUT has not yet expired : " + txn.getSourceMobileNumber());
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			txn.setNarrative("Request Rejected: Your account is not activated. Please contact your nearest branch.");
			return txn;
		}
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		if (CustomerClass.AGENT.equals(customer.getCustomerClass())) {
			LOG.debug("TXN was initiated BY AGENT : ");
			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
			if (!SystemConstants.canDo(zw.co.esolutions.ewallet.enums.CustomerClass.AGENT, txn.getTransactionType())) {
				txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
				txn.setNarrative("Request Rejected: Agents are not authorised to do " + txn.getTransactionType() + " transactions");
			
				return txn;
			} else {
				LOG.debug("Agent is allowed !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				try {
					LOG.debug("Validating if its agent account");
					if(TransactionType.TRANSFER.toString().equals(txn.getTransactionType().toString())) {
						BankAccount account = bankService.getUniqueBankAccountByAccountNumber(txn.getDestinationBankAccount());
						LOG.debug("Validating if its agent account"+account);
						if(account!= null && account.getId()!=null){
							LOG.debug("Validating if its agent account"+account.getAccountHolderId()+"XXXXX"+customer.getId());
							if(!account.getAccountHolderId().equals(customer.getId())){
								txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
								txn.setNarrative("Request Rejected: Destination bank account specified ,"+txn.getDestinationBankAccount()+" does not belong to Agent");
								return txn;
							}
						}else{
							txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
							txn.setNarrative("Request Rejected: Destination bank account specified ,"+txn.getDestinationBankAccount()+" does not belong to Agent");
							return txn;
						}
					}
					LOG.debug("In Agent Try block : ");
					Agent agent = agentService.getAgentByCustomerId(customer.getId());
					txn.setAgentNumber(agent.getAgentNumber());
					LOG.debug("Agent Number has been set to: " + agent.getAgentNumber());
				} catch (zw.co.esolutions.ewallet.agentservice.service.Exception_Exception e) {
					txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
					txn.setNarrative("Request Rejected: Invalid agent number specied.");
					LOG.debug("Invalid agent ");
					return txn;
				}
			}
		}

		if (txn.isValidateAgentNumber()) {
			Agent agent = agentService.getAgentByAgentNumber(txn.getAgentNumber());
			if (agent == null || agent.getId() == null) {
				LOG.debug("Txn requires agent number validation : but it failed");
				txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
				txn.setNarrative("Request Rejected: Invalid agent number specied.");
				return txn;
			}else{
				LOG.debug("Txn requires agent number validation : SUCCESS");
				txn.setAgentNumber(agent.getAgentNumber());
			}
		}

		txn.setCustomerId(customer.getId());
		txn.setSourceBankCode(bankInfo.getZswCode());
		txn.setCustomerName(this.getSourceCustomerName(txn));
		LOG.debug("Done validating registration : " + txn.getResponseCode() +" | " + txn.getUuid());
		return txn;
	}

	private String getSourceCustomerName(MessageTransaction txn) {
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		MobileProfile origin = customerService.findMobileProfileById(txn.getSourceMobileId());
		if (origin == null || origin.getId() == null) {
			return txn.getSourceBankAccount();
		}
		if (SystemConstants.AUTOREG_NAME.equalsIgnoreCase(origin.getCustomer().getFirstNames()) || SystemConstants.AUTOREG_NAME.equalsIgnoreCase(origin.getCustomer().getLastName())) {
			return txn.getSourceBankAccount();
		}
		String firstName = origin.getCustomer().getFirstNames();
		if (firstName == null || "".equals(firstName.trim())) {
			firstName = "";
		} else {
			firstName = firstName.substring(0, 1);
		}
		String customerName = firstName + " " + origin.getCustomer().getLastName();
		if ("".equalsIgnoreCase(customerName.trim())) {
			return txn.getSourceBankAccount();
		}
		return customerName.trim().toUpperCase();
	}

	private BankInfo getBankConfigInfo(String bankPrefix) {
		String path = "/opt/eSolutions/conf/bank." + bankPrefix + ".config.xml";
//		System.out.println("PATH TO CONFIG : " + path);
		zw.co.esolutions.config.bank.BankInfo bankInfo = ISOMarshaller.unmarshalBankInfo(path);
		if (bankInfo == null) {
			LOG.fatal("Bank Config for SMS CODE : " + bankPrefix + " could not be loaded");
			return null;
		}
		return bankInfo;
	}

	private MerchantInfo getMerchantConfigInfo(String utilityName) {
		String path = "/opt/eSolutions/conf/merchant." + utilityName.toLowerCase() + ".config.xml";
		LOG.debug("PATH TO CONFIG: " + path);
		MerchantInfo merchantInfo = ISOMarshaller.unmarshalMerchantInfo(path);
		if (merchantInfo == null) {
			LOG.fatal("MERCHANT Config for MERCHANT : " + utilityName + " could not be loaded");
			return null;
		}
		
		LOG.debug("MERCHANT INFO: " + merchantInfo);
		
		return merchantInfo;
	}

	private String formatPasswordPart(int number) {
		if (number == 1) {
			return "1st";
		} else if (number == 2) {
			return "2nd";
		} else if (number == 3) {
			return "3rd";
		} else if (number == 5) {
			return "last";
		}
		return number + "th";
	}

	@Override
	public MessageTransaction generatePasswordParts(MessageTransaction txn) {
		String narrative;
		TransactionStatus nextStatus;
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		GenerateTxnPassCodeResp passcodeResp = customerService.generateTxnPassCode(txn.getSourceMobileId());
		if (passcodeResp != null) {
			txn.setPasscodePrompt(String.valueOf(passcodeResp.getFirstIndex()) + String.valueOf(passcodeResp.getSecondIndex()));
			txn.setTimeout(DateUtil.getNextTimeout(new Timestamp(System.currentTimeMillis()), 3));
			nextStatus = TransactionStatus.VERES;
			
			if (EWalletConstants.MERCHANT_NAME_DSTV.equals(txn.getUtilityName()) 
					&& !TransactionType.MERCHANT_REG.equals(txn.getTransactionType())) {
				
				narrative = "Please submit " + this.formatPasswordPart(passcodeResp.getFirstIndex()) + " and " + this.formatPasswordPart(passcodeResp.getSecondIndex()) 
					+ " parts of your password to pay DStv " + txn.getBouquetName() + " for " + txn.getNumberOfMonths() + " months at value "  
					+ MoneyUtil.convertCentsToDollarsPattern(txn.getAmount()) + ". ZB e-Wallet - Powered by e-Solutions";
				
			} else {
				narrative = "Please submit " + this.formatPasswordPart(passcodeResp.getFirstIndex()) + " and " + this.formatPasswordPart(passcodeResp.getSecondIndex()) + " parts of your password eg 6*73. ZB e-Wallet - Powered by e-Solutions";
			}
			txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
		} else {
			LOG.fatal("FAILED to generate PASSCODE RQST");
			narrative = "Request failed in generating passcode request. ZB e-Wallet.";
			nextStatus = TransactionStatus.FAILED;
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
		}
		txn.setNarrative(narrative);
		txn.setStatus(nextStatus);
		return txn;
	}

	@Override
	public RequestInfo populateRequestInfo(MessageTransaction txn) {
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setAmount(txn.getAmount());
		requestInfo.setBankCode(txn.getBankReference());
		requestInfo.setLocationType(txn.getTransactionLocationType());
		requestInfo.setMessageId(txn.getUuid());
		requestInfo.setTransactionType(txn.getTransactionType());
		requestInfo.setPasswordParts(txn.getPasscodePrompt());
		requestInfo.setSourceMobileProfileId(txn.getSourceMobileId());
		requestInfo.setSecretCode(txn.getSecretCode());
		requestInfo.setSourceMobile(txn.getSourceMobileNumber());
		requestInfo.setTargetMobile(txn.getTargetMobileNumber());
		requestInfo.setCustomerUtilityAccount(txn.getCustomerUtilityAccount());
		requestInfo.setUtilityName(txn.getUtilityName());
		requestInfo.setSourceAccountNumber(txn.getSourceBankAccount());
		requestInfo.setDestinationAccountNumber(txn.getDestinationBankAccount());
		requestInfo.setSourceBankId(txn.getSourceBankId());
		requestInfo.setTargetBankId(txn.getTargetBankId());
		requestInfo.setOldReference(txn.getOldMessageId());
		requestInfo.setSummaryDate(txn.getSummaryDate());
		requestInfo.setAgentNumber(txn.getAgentNumber());
		requestInfo.setDateCreated(new Date(System.currentTimeMillis()));
		requestInfo.setOldPin(txn.getOldPin());
		requestInfo.setNewPin(txn.getNewPin());
//		requestInfo.setOnline(txn.isOnline());
		requestInfo.setBouquetCode(txn.getBouquetCode());
		requestInfo.setBouquetName(txn.getBouquetName());
		requestInfo.setNumberOfMonths(txn.getNumberOfMonths());
		requestInfo.setCommission(txn.getCommission());
		requestInfo.setBeneficiaryName(txn.getToCustomerName());
		
		return requestInfo;
	}

	public MessageTransaction handleTapRequest(MessageTransaction txn) {
		String ADMIN_MOBILE_NUMBERS = SystemConstants.configParams.getProperty("ADMIN_MOBILE_NUMBERS");

		if (ADMIN_MOBILE_NUMBERS == null) {
			txn.setNarrative("Request rejected. No admin mobiles have been defined.");
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			return txn;
		}
		List<String> mobileNumbers = Arrays.asList(ADMIN_MOBILE_NUMBERS.replace(" ", "").split(","));
		if (!mobileNumbers.contains(txn.getSourceMobileNumber())) {
			txn.setNarrative("Request rejected. This mobile is not defined as admin.");
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			return txn;
		}
		Tap tap = this.getTapById(SystemConstants.SMPP_IN_TAP);
		if (tap == null || tap.getId() == null) {
			txn.setNarrative("Request rejected. Tap is not configured.");
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			return txn;
		}
		if (SystemConstants.TAP_STATUS_ON.equalsIgnoreCase(txn.getTapCommand()) || SystemConstants.TAP_STATUS_OFF.equalsIgnoreCase(txn.getTapCommand())) {
			tap.setStatus(txn.getTapCommand());
			tap = this.updateTap(tap);
			if (tap == null || tap.getId() == null) {
				txn.setNarrative("Request rejected. Failed to update tap status.");
				txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
				return txn;
			} else {
				txn.setNarrative("Tap status updated to " + txn.getTapCommand() + ".");
				txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
				return txn;
			}
		} else {
			txn.setNarrative("Request rejected. Invalid tap request.");
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			return txn;
		}
	}

	@Override
	public MessageTransaction validatePinChangeRequest(MessageTransaction txn) {
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		MobileProfile mobileProfile = customerService.findMobileProfileById(txn.getSourceMobileId());
		String secretCode;
		
		//This Applies currently for USSD and Mobile Web Initiated Transactions
		boolean isPasswordChangedAlready = (txn.getTransactionLocationType() != null 
												&& (TransactionLocationType.USSD.equals(txn.getTransactionLocationType())
															|| TransactionLocationType.MOBILE_WEB.equals(txn.getTransactionLocationType())));
			
		try {
			secretCode = EncryptAndDecrypt.decrypt(mobileProfile.getSecretCode(), mobileProfile.getMobileNumber());
		} catch (Exception e) {
			txn.setStatus(TransactionStatus.FAILED);
			txn.setNarrative("Request Rejected. Incorrect old pin supplied.");
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			return txn;
		}
		
		if (txn.getOldPin().equals(secretCode) || isPasswordChangedAlready) {
			txn.setStatus(TransactionStatus.BANK_REQUEST);
			txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
			txn.setNarrative("Pin Change Request Validation Successful");
			return txn;
		} else {
			txn.setStatus(TransactionStatus.FAILED);
			txn.setNarrative("Request Rejected. Incorrect old pin supplied.");
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			return txn;
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public MessageTransaction createTransaction(MessageTransaction txn) {
		txn.setDateCreated(new Timestamp(System.currentTimeMillis()));
		LOG.debug("PERSIST THE TXN : "+txn.getUuid() + "|" + txn.getAgentNumber() + "|" + txn.getAmount() + "|" + txn.getBankReference() + "|" + txn.getCustomerId() + "|" + txn.getCustomerName() + "|" + txn.getCustomerUtilityAccount() + "|" + txn.getDestinationBankAccount() + "|" + txn.getMno() + "|" + txn.getNarrative() + "|" + txn.getOldMessageId() + "|" + txn.getPasscodePrompt() + "|" + txn.getPasswordRetryCount() + "|" + txn.getResponseCode() + "|" + txn.getSecretCode() + "|" + txn.getSourceBankAccount() + "|" + txn.getSourceBankCode() + "|" + txn.getSourceBankId() + "|" + txn.getSourceBankPrefix() + "|" + txn.getSourceMobileId() + "|" + txn.getSourceMobileNumber() + "|" + txn.getStatus() + "|" + txn.getSummaryDate() + "|" + txn.getTargetBankCode() + "|" + txn.getTargetBankId() + "|" + txn.getTargetMobileId() + "|" + txn.getTargetMobileNumber() + "|" + txn.getToCustomerName() + "|" + txn.getTransactionLocationId() + "|" + txn.getTransactionLocationType() + "|" + txn.getTransactionType() + "|" + txn.getTransferType() + "|" + txn.getUtilityName() + "|" + txn.getTransactionRoutingInfo());
		em.persist(txn);
		return txn;
	}

	@Override
	public MessageTransaction updateTransaction(MessageTransaction txn) {
		LOG.debug(txn.getUuid() + "|" + txn.getAgentNumber() + "|" + txn.getAmount() + "|" + txn.getBankReference() + "|" + txn.getCustomerId() + "|" + txn.getCustomerName() + "|" + txn.getCustomerUtilityAccount() + "|" + txn.getDestinationBankAccount() + "|" + txn.getMno() + "|" + txn.getNarrative() + "|" + txn.getOldMessageId() + "|" + txn.getPasscodePrompt() + "|" + txn.getPasswordRetryCount() + "|" + txn.getResponseCode() + "|" + txn.getSecretCode() + "|" + txn.getSourceBankAccount() + "|" + txn.getSourceBankCode() + "|" + txn.getSourceBankId() + "|" + txn.getSourceBankPrefix() + "|" + txn.getSourceMobileId() + "|" + txn.getSourceMobileNumber() + "|" + txn.getStatus() + "|" + txn.getSummaryDate() + "|" + txn.getTargetBankCode() + "|" + txn.getTargetBankId() + "|" + txn.getTargetMobileId() + "|" + txn.getTargetMobileNumber() + "|" + txn.getToCustomerName() + "|" + txn.getTransactionLocationId() + "|" + txn.getTransactionLocationType() + "|" + txn.getTransactionType() + "|" + txn.getTransferType() + "|" + txn.getUtilityName() + "|" + txn.getTransactionRoutingInfo());
		return em.merge(txn);
	}

	@Override
	public MessageTransaction promoteTransactionStatus(MessageTransaction txn, TransactionStatus nextTxnStatus, String narrative) {
		TransactionState txnState = new TransactionState();
		txnState.setStatus(nextTxnStatus);
		txnState.setNarrative(narrative);
		txnState.setId(GenerateKey.generateEntityId());
		txnState.setDateCreated(new Date());
		txnState.setTransaction(txn);
		em.persist(txnState);
		return txn;
	}

	@Override
	public MessageTransaction findMessageTransactionByUUID(String uuid) {
		return em.find(MessageTransaction.class, uuid);
	}

	public Tap getTapById(String id) {
		Tap tap;
		try {
			tap = em.find(Tap.class, id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return tap;
	}

	public Tap updateTap(Tap tap) {
		try {
			tap = em.merge(tap);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return tap;
	}

	@SuppressWarnings("unchecked")
	private MessageTransaction getLatestTransactionPendingAuthorisation(String sourceMobileId) {
		List<MessageTransaction> results = null;
		try {
			Query query = em.createNamedQuery("getLatestTransactionPendingAuthorisation");
			query.setParameter("sourceMobileId", sourceMobileId);
			query.setParameter("status", TransactionStatus.VERES);
			results = (List<MessageTransaction>) query.getResultList();
			if (results == null || results.isEmpty()) {
				return null;
			} else {
				return results.get(0);
			}
		} catch (NoResultException e) {
			e.printStackTrace();

		} catch (Exception e) {
			LOG.error("Exception thrown trying to find txn to authorise");
		}
		return null;
	}

	public MessageTransaction handlePasscodeResponse(MessageTransaction pareq) {

		LOG.debug("Processing PAREQ for : [" + pareq.getSourceMobileNumber() + " | " + pareq.getSourceMobileNumber() + "|" + pareq.getSourceBankId() + "| " + pareq.getSourceBankCode() + "]");
		MessageTransaction txn = this.getLatestTransactionPendingAuthorisation(pareq.getSourceMobileId());
		LOG.debug("\nFound existing transaction : " + txn);
		if (txn == null) {
			LOG.info("No Requests pending authorisation for " + pareq.getSourceMobileNumber());
			// no requests pending authorisation
			pareq.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			pareq.setNarrative("Request Rejected: You have no request pending authorisation. ZB e-Wallet - Powered by e-Solutions.");
			return pareq;
		}
		// validate txn passcode
		txn = this.processPasswordRetry(pareq, txn);
		LOG.debug("Done handling password retry");
		if (SystemConstants.RESPONSE_CODE_OK_SHORT.equalsIgnoreCase(txn.getResponseCode())) {
			LOG.debug("Transaction Autho successful");
			return txn;
		} else {
			return txn;
		}
	}

	private MessageTransaction processPasswordRetry(MessageTransaction pareq, MessageTransaction txn) {
		LOG.debug("Authorising transaction...");
		if (this.passcodeIsValid(pareq, txn)) {
			LOG.debug("Password is CORRECT!");
			txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
			txn.setNarrative("Payment Authorisation successful");
			return txn;
		}
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		// wrong password, increment retry counter.
		LOG.debug("Password is WRONG!");
		txn.setPasswordRetryCount(txn.getPasswordRetryCount() + 1);
		if (txn.getPasswordRetryCount() < 3) {
			txn.setTimeout(DateUtil.getNextTimeout(new Timestamp(System.currentTimeMillis()), 3));
			String message = "Wrong password parts. Please submit " + this.formatPasswordPart(Integer.parseInt(txn.getPasscodePrompt().substring(0, 1))) + " and " + this.formatPasswordPart(Integer.parseInt(txn.getPasscodePrompt().substring(1))) + " parts of your password. ZB e-Wallet - Powered by e-Solutions";
			LOG.debug("Request password again..");
			txn.setNarrative(message);
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
		} else {
			// passcode entered incorrectly 3 times, lock the profile
			MobileProfile mobileProfile = customerService.findMobileProfileById(txn.getSourceMobileId());
			mobileProfile.setPasswordRetryCount(3);
			mobileProfile.setStatus(MobileProfileStatus.LOCKED);
			mobileProfile.setTimeout(DateUtil.convertToXMLGregorianCalendar(DateUtil.addHours(new Date(), 24)));
			try {
				customerService.updateMobileProfile(mobileProfile, EWalletConstants.SYSTEM);
			} catch (Exception_Exception e) {
				LOG.fatal("Failed to lock mobile profile : Exception thrown");
			}
			LOG.debug("Lock the customer..");
			txn.setNarrative("Your phone is currently locked. Please retry after a few hours.");
			txn.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
		}
		return txn;
	}

	private boolean passcodeIsValid(MessageTransaction pareq, MessageTransaction txn) {
		try {
			CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
			String passcodeParts = pareq.getPasswordParts();
			ValidateTxnPassCodeReq req = new ValidateTxnPassCodeReq();
			req.setMobileNumber(pareq.getSourceMobileNumber());
			req.setFirstIndex(Integer.parseInt(txn.getPasscodePrompt().substring(0, 1)));
			req.setSecondIndex(Integer.parseInt(txn.getPasscodePrompt().substring(1)));
			req.setFirstValue(Integer.parseInt(passcodeParts.substring(0, 1)));
			req.setSecondValue(Integer.parseInt(passcodeParts.substring(1)));
			boolean isValid = customerService.txnPassCodeIsValid(req);
			return isValid;
		} catch (Exception e) {
			LOG.debug("Got invalid passcode.", e);
			return false;
		}
	}

	public MerchantResponse populateTopupMerchantResponse(ResponseInfo responseInfo, WsResponse response) {
		MerchantResponse merchantResponse = new MerchantResponse();
		MerchantRequest merchantRequest = new MerchantRequest();
		merchantRequest.setAmount(responseInfo.getRequestInfo().getAmount());
		merchantRequest.setMerchantId(null);
		merchantRequest.setCustomerUtilityAccount(responseInfo.getRequestInfo().getTargetMobile());
		merchantRequest.setMerchantName(null);
		merchantRequest.setMerchantRequestType(MerchantRequestType.MERCHANT_RESPONSE);
		merchantRequest.setReference(responseInfo.getRequestInfo().getMessageId());
		merchantRequest.setSourceMobileNumber(responseInfo.getRequestInfo().getSourceMobile());
		merchantRequest.setTargetMobileNumber(responseInfo.getRequestInfo().getTargetMobile());

		merchantResponse.setMerchantRequest(merchantRequest);
		merchantResponse.setMerchantResponseCode(responseInfo.getResponseCode().name());
		merchantResponse.setNarrative(responseInfo.getNarrative());
		merchantResponse.setResponseCode(responseInfo.getResponseCode());
		if (response != null) {
			merchantResponse.setPreBalance(MoneyUtil.convertToCents(response.getInitialBalance()));
			merchantResponse.setPostBalance(MoneyUtil.convertToCents(response.getAirtimeBalance()));
			responseInfo.setMerchantAccBalance(MoneyUtil.convertToCents(response.getAirtimeBalance()));
		}

		merchantResponse.setResponseInfo(responseInfo);
		merchantResponse.setResponseType(responseInfo.getResponseType());

		return merchantResponse;
	}

	public MerchantResponse populateMerchantResponse(MessageTransaction txn) {
		MerchantResponse merchantResponse = new MerchantResponse();
		MerchantRequest merchantRequest = new MerchantRequest();
		merchantRequest.setAmount(txn.getAmount());
		merchantRequest.setCustomerUtilityAccount(txn.getCustomerUtilityAccount());
		merchantRequest.setMerchantName(txn.getUtilityName());
		merchantRequest.setMerchantRequestType(MerchantRequestType.MERCHANT_RESPONSE);
		merchantRequest.setReference(txn.getUuid());
		merchantRequest.setSourceMobileNumber(txn.getSourceMobileNumber());
		merchantRequest.setMerchantRef(txn.getMerchantRef());
		
		merchantResponse.setMerchantRequest(merchantRequest);

		// merchantResponse.setResponseInfo(responseInfo);
		// merchantResponse.setResponseType(responseInfo.getResponseType());

		return merchantResponse;
	}
	
	public Bouquet getBouquetByName(String name) {
		Bouquet bouquet = null;
		try {
			Query query = em.createNamedQuery("getBouquetByName");
			query.setParameter("name", name);
			bouquet = (Bouquet) query.getSingleResult();
		} catch (NoResultException e) {
			LOG.debug("No bouquet found");
		} catch (Exception e) {
			LOG.fatal("Exception finding bouquet: " + e.getCause());
			e.printStackTrace(System.err);
		}
		return bouquet;
	}

	public Bouquet getBouquetByCode(String code) {
		Bouquet bouquet = null;
		try {
			Query query = em.createNamedQuery("getBouquetByCode");
			query.setParameter("code", code);
			bouquet = (Bouquet) query.getSingleResult();
		} catch (NoResultException e) {
			LOG.debug("No bouquet found");
		} catch (Exception e) {
			LOG.fatal("Exception finding bouquet: " + e.getCause());
			e.printStackTrace(System.err);
		}
		return bouquet;
	}
	
	public Bouquet getBouquetByNameAndType(String name, String type) {
		Bouquet bouquet = null;
		try {
			Query query = em.createNamedQuery("getBouquetByNameAndType");
			query.setParameter("name", name);
			query.setParameter("type", type);
			bouquet = (Bouquet) query.getSingleResult();
		} catch (NoResultException e) {
			LOG.debug("No bouquet found");
		} catch (Exception e) {
			LOG.fatal("Exception finding bouquet: " + e.getCause());
			e.printStackTrace(System.err);
		}
		return bouquet;
	}

	public Bouquet getBouquetByCodeAndType(String code, String type) {
		Bouquet bouquet = null;
		try {
			Query query = em.createNamedQuery("getBouquetByCodeAndType");
			query.setParameter("code", code);
			query.setParameter("type", type);
			bouquet = (Bouquet) query.getSingleResult();
		} catch (NoResultException e) {
			LOG.debug("No bouquet found");
		} catch (Exception e) {
			LOG.fatal("Exception finding bouquet: " + e.getCause());
			e.printStackTrace(System.err);
		}
		return bouquet;
	}
	
	public static void main(String ... args) {
		String str = "premium";
		String[] array = str.split("\\.");
		System.out.println("Length = " + array.length);
		System.out.println("array[0] =" + array[0]);
	}
	
}
