package zw.co.esolutions.ewallet.process.mdb;

import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountType;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.bankservices.service.TransactionSummary;
import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfileStatus;
import zw.co.esolutions.ewallet.enums.ResponseType;
import zw.co.esolutions.ewallet.enums.TransactionLocationType;
import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.msg.BankRequest;
import zw.co.esolutions.ewallet.msg.MerchantResponse;
import zw.co.esolutions.ewallet.msg.ProcessResponse;
import zw.co.esolutions.ewallet.msg.RequestInfo;
import zw.co.esolutions.ewallet.msg.ResponseInfo;
import zw.co.esolutions.ewallet.process.AutoRegService;
import zw.co.esolutions.ewallet.process.ProcessUtil;
import zw.co.esolutions.ewallet.process.model.ProcessTransaction;
import zw.co.esolutions.ewallet.process.util.CustomerRegResponse;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.SystemConstants;

/**
 * Message-Driven Bean implementation class for: HandleTransactionRequest
 * 
 */
@MessageDriven(activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") }, mappedName = EWalletConstants.FROM_SWITCH_TO_ZB_QUEUE)
public class HandleTransactionRequest implements MessageListener {
	
	@EJB
	private ProcessUtil processUtil;
	
	@EJB 
	private AutoRegService autoRegService;
	
	public HandleTransactionRequest() {

	}

	private static Logger LOG;
	
	private static Properties config = SystemConstants.configParams;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(HandleTransactionRequest.class);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.err.println("Failed to initilise logger for " + HandleTransactionRequest.class);
		}
	}

	public void onMessage(Message message) {
		try {
			LOG.info("Request Received : Handle Transaction Request >>");
			ObjectMessage msg = (ObjectMessage) message;
			Object obj = msg.getObject();
			if (obj instanceof RequestInfo) {
				RequestInfo requestInfo = (RequestInfo) obj;
				this.processTransactionRequest(requestInfo);
			} else if (obj instanceof MerchantResponse) {
				MerchantResponse merchantResponse = (MerchantResponse) obj;
				this.processMerchantResponse(merchantResponse);
			} else {
				LOG.warn("Unknown REQUEST message picked up from SWITCH Cannonical Class : " + obj.getClass().getCanonicalName() + " Simple Name : " + obj.getClass().getSimpleName());
			}
		} catch (JMSException e) {
			LOG.fatal("Failed to HANDLE INCOMING REQUEST >> JMS Exception Thrown : Message " + e.getMessage() + " : ", e);
			e.printStackTrace(System.err);
		}
	}

	private void processMerchantResponse(MerchantResponse merchantResponse) {
		LOG.info("Processing Merchant Response : RESP INFO >> [" + merchantResponse.getMerchantRequest().getReference() + " : " + merchantResponse.getMerchantResponseCode() + " : " + merchantResponse.getNarrative() +"]");
		try {
			// retrieve txn and update
			ProcessTransaction txn = processUtil.findProcessTransactionById(merchantResponse.getMerchantRequest().getReference());
			if (txn == null) {
				LOG.warn("NO MATCHING TXN for " + merchantResponse.getMerchantRequest().getReference() + " : IGNORE ? ");
				return;
			}
			LOG.debug("Found a matching transaction : TXN INFO >> [" + txn.getStatus() + " : " + txn.getMessageId() +"]");
			ResponseInfo responseInfo = merchantResponse.getResponseInfo();
			if(responseInfo == null){
				responseInfo = new ResponseInfo(txn.getNarrative(), merchantResponse.getResponseCode(), processUtil.populateRequestInfo(txn), merchantResponse.getResponseType(), 0L, 0L, 0L, txn.getId());
			}
			TransactionStatus nextTxnStatus ;
			if(TransactionStatus.COMPLETED.equals(txn.getStatus()) || TransactionStatus.FAILED.equals(txn.getStatus())){
				LOG.warn("TXN " + txn.getMessageId() + " IS already commited to STATUS " + txn.getStatus() + " BUT received a merchant response >> Do nothing DUPLICATE ? ");
				return;
			}
			txn = processUtil.promoteTxnState(txn, TransactionStatus.CREDIT_RESPONSE, "Credit Response");
			txn.setNarrative(merchantResponse.getNarrative());	
			txn.setMerchantRef(merchantResponse.getMerchantRequest().getMerchantRef());
			txn = processUtil.updateProcessTransaction(txn);
			if (ResponseCode.E000.equals(merchantResponse.getResponseCode())) {
				LOG.debug("Done updating txn to COMPLETED");
				responseInfo.setNarrative(merchantResponse.getNarrative());
				responseInfo.setResponseCode(merchantResponse.getResponseCode());
				nextTxnStatus = TransactionStatus.COMPLETED;
				// Send notifcation
				responseInfo.setResponseType(ResponseType.NOTIFICATION);
				LOG.debug("Done setting response info parameters.");
				boolean replied = processUtil.submitResponse(responseInfo);
				LOG.debug("EWT has replied to SWITCH : " + replied + " : FOR TXN : " + txn.getMessageId());
				
				txn = processUtil.promoteTxnState(txn, nextTxnStatus, merchantResponse.getNarrative());
				LOG.debug("Done PROCESSING MERCHANT RESPONSE : REPLY SENT.");
			} else {
				LOG.debug("TXN FAILED : Post Reversals");
				boolean sent;
				String narrative;
				try {
					BankRequest bankRequest = processUtil.populateBankRequest(txn);				
					LOG.debug("Reversals sent..");
					sent = processUtil.submitRequest(bankRequest, true);
					if(sent){
						narrative = "Credit Transaction Failed.";
						nextTxnStatus = TransactionStatus.REVERSAL_REQUEST;
					}else{
						narrative = "Failed to submit reversal";
						nextTxnStatus = TransactionStatus.MANUAL_RESOLVE;						
					}
				} catch (Exception e) {
					LOG.warn("FAILED to send reversals for : " + txn.getMessageId() + " : MARK TXN FOR MANUAL RESOLUTION", e);
					nextTxnStatus = TransactionStatus.MANUAL_RESOLVE;
					narrative = "Failed to submit reversal";
				}
				txn = processUtil.promoteTxnState(txn, nextTxnStatus, narrative);				
			}
			
		} catch (Exception e) {
			LOG.fatal("FAILED to process MERCHANT Response : " + e.getMessage(), e);
			e.printStackTrace(System.err);
		}
	}

	private void processTransactionRequest(RequestInfo requestInfo) {
		LOG.debug("Check if transaction already exists");
		ProcessTransaction txn = processUtil.findProcessTransactionById(requestInfo.getMessageId());
		if(txn != null){
			LOG.warn("Suspected Duplicate transaction [" + txn.getMessageId() + "|" + txn.getTransactionType() +"|"+txn.getAmount()+"]") ;
			return;
		}
		//check if request timed out
		int SWITCH_REQUEST_TIMEOUT_MINUTES;
		try {
			SWITCH_REQUEST_TIMEOUT_MINUTES = Integer.parseInt(config.getProperty("SWITCH_REQUEST_TIMEOUT_MINUTES"));
		} catch (NumberFormatException e1) {
			LOG.warn("SWITCH_REQUEST_TIMEOUT_MINUTES is probably not set : USE DEFAULT 2HRS");
			SWITCH_REQUEST_TIMEOUT_MINUTES = 60;
		}
		
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		
		Calendar switchTime = Calendar.getInstance();
		if(requestInfo.getDateCreated() == null){
			requestInfo.setDateCreated(new Date(System.currentTimeMillis()));
		}
		
		switchTime.setTime(requestInfo.getDateCreated());
		switchTime.add(Calendar.MINUTE, SWITCH_REQUEST_TIMEOUT_MINUTES);
		
		if (switchTime.before(now)) {
			LOG.debug("^^^^^^^^^^^   Switch Request Timed Out.. send failure resp for txnType " + requestInfo.getTransactionType());
			String response = "The request took too long to be processed";
			try {
				txn = processUtil.populateProcessTransaction(requestInfo);
				txn = processUtil.createProcessTransaction(txn);
				txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, response);
				
				ResponseInfo responseInfo = new ResponseInfo(response, ResponseCode.E505, requestInfo, ResponseType.NOTIFICATION, 0L, 0L, 0L, requestInfo.getMessageId());
				
				boolean replied  = processUtil.submitResponse(responseInfo);
				if(replied){
					LOG.debug("TIMEOUT REPLY SENT(to SWITCH) " + requestInfo.getMessageId());
				}else{
					LOG.fatal("FAILED to SEND TIMEOUT REPLY (to SWITCH) " + requestInfo.getMessageId() + ": Customer WILL wait forever, but txn never posted.");
				}
			} catch (Exception e) {
				LOG.warn("TIMEOUT Transaction failed to persist : IGNORE IT ");
			}
			return;
		}

		
		
		LOG.info("RQST Received : [" + requestInfo.getMessageId() + " | " + requestInfo.getTransactionType() + " | "+requestInfo.getSourceMobile() +"]");
		
		if (TransactionType.BALANCE.equals(requestInfo.getTransactionType()) || TransactionType.BALANCE_REQUEST.equals(requestInfo.getTransactionType())) {
			this.handleBalanceRequest(requestInfo);
			return;
		}
		if (TransactionType.MINI_STATEMENT.equals(requestInfo.getTransactionType())) {
			this.handleMiniStatementRequest(requestInfo);
			return;
		}
		if (TransactionType.PASSCODE.equals(requestInfo.getTransactionType())) {
			this.handlePhoneLockRequest(requestInfo);
			return;
		}
		if (TransactionType.CHANGE_PASSCODE.equals(requestInfo.getTransactionType())) {
			this.handlePinChangeRequest(requestInfo);
			return;
		}

		if (TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER.equals(requestInfo.getTransactionType())) {
			this.handleEWalletAccountToBankAccountTransfer(requestInfo);
			return;
		}
		if (TransactionType.EWALLET_TO_EWALLET_TRANSFER.equals(requestInfo.getTransactionType())) {
			this.handleEWalletAccountToEWalletAccountTransfer(requestInfo);
			return;
		}
		if (TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER.equals(requestInfo.getTransactionType())) {
			this.handleEWalletAccountToNonHolderTransfer(requestInfo);
			return;
		}

		if (TransactionType.BILLPAY.equals(requestInfo.getTransactionType())) {
			this.handleBillPayRequest(requestInfo);
			return;
		}
		if (TransactionType.MERCHANT_REG.equals(requestInfo.getTransactionType())) {
			this.handleMerchantRegistrationRequest(requestInfo);
			return;
		}
		if (TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER.equals(requestInfo.getTransactionType())) {
			this.handleEWalletAccountToBankAccountTransfer(requestInfo);
			return;
		}
		if (TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER.equals(requestInfo.getTransactionType())) {
			this.handleBankAccountToBankAccountTransfer(requestInfo);
			return;
		}
		if (TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER.equals(requestInfo.getTransactionType())) {
			this.handleBankAccountToEWalletTransfer(requestInfo);
			return;
		}
		if (TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER.equals(requestInfo.getTransactionType())) {
			this.handleBankAccountToNonHolderTransfer(requestInfo);
			return;
		}

		if (TransactionType.TOPUP.equals(requestInfo.getTransactionType())) {
			this.handleTopupRequest(requestInfo);
			return;
		}
		
		if (TransactionType.AGENT_CUSTOMER_DEPOSIT.equals(requestInfo.getTransactionType())) {
			this.handleAgentCustomerDepositRequest(requestInfo);
			return;
		}
		
		if (TransactionType.AGENT_CUSTOMER_WITHDRAWAL.equals(requestInfo.getTransactionType())) {
			this.handleAgentCustomerWithdrawalRequest(requestInfo);
			return;
		}
		
		if (TransactionType.AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL.equals(requestInfo.getTransactionType())) {
			this.handleAgentCustomerNonHolderWithdrawalRequest(requestInfo);
			return;
		}
		if (TransactionType.AGENT_EWALLET_TO_BANKACCOUNT_TRANSFER.equals(requestInfo.getTransactionType())) {
			this.handleAgentEWalletAccountToBankAccountTransfer(requestInfo);
			return;
		}
		if(TransactionType.AGENT_SUMMARY.equals(requestInfo.getTransactionType())){
			this.handleAgentTrannsactionSummaryRequest(requestInfo);
			return;
		}
		
		else{
			LOG.debug("Unknown request " + requestInfo.getTransactionType());
		}
 	}

	
	private void handleMerchantRegistrationRequest(RequestInfo requestInfo) {
		try {
			// create process tranx
			ProcessTransaction txn = processUtil.populateProcessTransaction(requestInfo);
			LOG.debug("Done populating TXN ");
			txn.setNarrative("Customer Merchant Account Reg");
			txn = processUtil.createProcessTransaction(txn);
			txn = processUtil.promoteTxnState(txn, TransactionStatus.DRAFT, txn.getNarrative());
			LOG.debug("Done creating txn");
			boolean sendToBank = false;
			ResponseInfo responseInfo = null;
			TransactionStatus nextTxnStatus;
			String narrative;
			CustomerRegResponse response = processUtil.processCustomerMerchatRegistration(txn);
			LOG.debug("Done processing reg : RESP CODE : " + response.getResponseCode() + " NARRATIVE : " + response.getNarrative());
			if (ResponseCode.E000.equals(response.getResponseCode())) {
				nextTxnStatus = TransactionStatus.COMPLETED;
				narrative = "Transaction Successful";
			} else {
				nextTxnStatus = TransactionStatus.FAILED;
				narrative = response.getNarrative();
			}
			LOG.debug("DONE updaing txn status to " + txn.getStatus());
			responseInfo = new ResponseInfo(response.getNarrative(), response.getResponseCode(), requestInfo, ResponseType.NOTIFICATION, 0L, 0L, 0L, txn.getMessageId());
			LOG.debug("Sending CUSTOMER MERCHANT REG REPLY (to SWITCH) for transaction...." + txn.getMessageId());
			
			//if you get here then we must be ready to reply to the SWITCH / To forward to Bank
			boolean sent ;
			String stmt;
			//send reply to SWITCH
			sent = processUtil.submitResponse(responseInfo);
			stmt = " REPLY has been sent (to SWITCH)";
			
			if(sent){
				txn = processUtil.promoteTxnState(txn, nextTxnStatus, narrative);
				LOG.info(txn.getTransactionType() + stmt);
			}else{
				LOG.fatal("FAILED to submit " + txn.getTransactionType() + (sendToBank ? " REQUEST (to BANK) " : " REPLY (to SWITCH)"));
			}
		} catch (Exception e) {
			LOG.fatal("FAILED to process " + requestInfo.getTransactionType() + " REQUEST : " + requestInfo.getMessageId() + " : " + e.getMessage(), e);
			e.printStackTrace(System.err);			
		}
	}

	private void handleBillPayRequest(RequestInfo requestInfo) {
		ProcessTransaction txn = new ProcessTransaction();
		try {
			try {
				requestInfo = processUtil.populateRequestWithMerchantInfo(requestInfo);
			} catch (Exception e) {
				ResponseInfo responseInfo = new ResponseInfo(e.getMessage(), ResponseCode.E903, requestInfo, ResponseType.NOTIFICATION, 0L, 0L, 0L, txn.getMessageId());
				//if you get here then we must be ready to reply to the SWITCH / To forward to Bank
				boolean sent = processUtil.submitResponse(responseInfo);
				String stmt = " REPLY has been sent (to SWITCH)";
				//send reply to SWITCH
				if(sent){
					LOG.info(txn.getTransactionType() + stmt);
				}else{
					LOG.fatal("FAILED to submit " + txn.getTransactionType() + " REPLY (to SWITCH)");
				}
				return;
			}
			// create process tranx
			txn = processUtil.populateProcessTransaction(requestInfo);
			LOG.debug("Done Populating the process transaction.");
			txn.setNarrative("BILLPAY REQUEST.");
			txn = processUtil.createProcessTransaction(txn);
			txn = processUtil.promoteTxnState(txn, TransactionStatus.DRAFT, TransactionStatus.DRAFT.name());
			LOG.debug("Done promoting state.");
			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
			BankAccount bankAccount = bankService.getBankAccountByAccountNumberAndOwnerType(txn.getSourceAccountNumber(), OwnerType.CUSTOMER);
			
			txn = processUtil.validateBillPayRequest(txn, bankAccount);
			LOG.debug("Done validating go for the postings to EQ3 with response code " + txn.getResponseCode());
			
			txn = processUtil.updateProcessTransactionWith(txn.getId(), txn.getResponseCode(), null, txn.getNarrative());
			LOG.debug("updated validation result..");
			
			boolean sendToBank = false;
			ResponseInfo responseInfo = null;
			BankRequest bankRequest = null;
			TransactionStatus nextTxnStatus;
			String narrative;
			
			if (ResponseCode.E000.name().equalsIgnoreCase(txn.getResponseCode())) {
				if (BankAccountType.E_WALLET.equals(bankAccount.getType())) {
					LOG.debug("Found bank account of type EWALLET " + bankAccount);
					txn.setTransactionType(TransactionType.EWALLET_BILLPAY);
					txn = this.processUtil.updateProcessTransaction(txn);
				} else {
					LOG.debug("Found bank account of type EQ3 " + bankAccount);
				}
				bankRequest = processUtil.populateBankRequest(txn);
				nextTxnStatus = TransactionStatus.BANK_REQUEST;
				narrative = TransactionStatus.BANK_REQUEST.name();
				sendToBank = true;
			} else {
				LOG.debug("Txn has failed somewhere in validations, return to sender : " + txn.getMessageId());
				nextTxnStatus = TransactionStatus.FAILED;
				narrative = txn.getNarrative();
				responseInfo = new ResponseInfo(txn.getNarrative(), ResponseCode.valueOf(txn.getResponseCode()), requestInfo, ResponseType.NOTIFICATION, 0L, 0L, 0L, txn.getMessageId());
			}
			//if you get here then we must be ready to reply to the SWITCH / To forward to Bank
			boolean sent ;
			String stmt;
			if(sendToBank){
				//send request to bank
				sent = processUtil.submitRequest(bankRequest, false);
				stmt = " REQUEST has been sent (to BANK)";
			}else{
				//send reply to SWITCH
				sent = processUtil.submitResponse(responseInfo);
				stmt = " REPLY has been sent (to SWITCH)";
			}
			if(sent){
				txn = processUtil.promoteTxnState(txn, nextTxnStatus, narrative);
				LOG.info(txn.getTransactionType() + stmt);
			}else{
				LOG.fatal("FAILED to submit " + txn.getTransactionType() + (sendToBank ? " REQUEST (to BANK) " : " REPLY (to SWITCH)"));
			}
		} catch (Exception e) {
			LOG.fatal("FAILED to process " + requestInfo.getTransactionType() + " REQUEST : " + requestInfo.getMessageId() + " : " + e.getMessage(), e);
			e.printStackTrace(System.err);
			txn = this.failTransaction(txn, requestInfo);
		}
	}

	private void handleEWalletAccountToNonHolderTransfer(RequestInfo requestInfo) {
		LOG.debug("Handle EWallet To Non Holder Transfer Request....");
		ProcessTransaction txn = null;
		try {
			// create process tranx
			LOG.debug("Calling the populate method.");
			txn = processUtil.populateProcessTransaction(requestInfo);
			LOG.debug("Done populating");
			txn.setNarrative("Transfer from ewallet to non-holder");
			// we put the mobile number here coz this target customer is not
			// registered.
			txn.setTargetMobileId(requestInfo.getTargetMobile());
			txn.setSecretCode(requestInfo.getSecretCode());
			LOG.debug("Calling the create method");
			txn = processUtil.createProcessTransaction(txn);
			LOG.debug("Create method has returned successfully " + txn + " going to promote txn state");
			txn = processUtil.promoteTxnState(txn, TransactionStatus.DRAFT, TransactionStatus.DRAFT.name());
			LOG.debug("Done promoting state to draft : returning " + txn + " going for the process...");
			
			txn = processUtil.validateEWalletToNonHolderTransferReq(txn);
			LOG.debug("Done validating go for the postings to EQ3 with response code " + txn.getResponseCode());
			
			txn = processUtil.updateProcessTransactionWith(txn.getId(), txn.getResponseCode(), null, txn.getNarrative());
			LOG.debug("updated validation result..");
			
			boolean sendToBank = false;
			ResponseInfo responseInfo = null;
			BankRequest bankRequest = null;
			TransactionStatus nextTxnStatus;
			String narrative;
			
			LOG.debug("Done processing going for EQ3 in populate.");
			if (ResponseCode.E000.name().equalsIgnoreCase(txn.getResponseCode())) {
				bankRequest = processUtil.populateBankRequest(txn);
				nextTxnStatus = TransactionStatus.BANK_REQUEST;
				narrative = TransactionStatus.BANK_REQUEST.name();
				sendToBank = true;
			} else {
				LOG.debug("Txn has failed somewhere in validations, return to sender : " + txn.getMessageId());
				nextTxnStatus = TransactionStatus.FAILED;
				narrative = txn.getNarrative();
				responseInfo = new ResponseInfo(txn.getNarrative(), ResponseCode.valueOf(txn.getResponseCode()), requestInfo, ResponseType.NOTIFICATION, 0L, 0L, 0L, txn.getMessageId());
			}
			//if you get here then we must be ready to reply to the SWITCH / To forward to Bank
			boolean sent ;
			String stmt;
			if(sendToBank){
				//send request to bank
				sent = processUtil.submitRequest(bankRequest, false);
				stmt = " REQUEST has been sent (to BANK)";
			}else{
				//send reply to SWITCH
				sent = processUtil.submitResponse(responseInfo);
				stmt = " REPLY has been sent (to SWITCH)";
			}
			if(sent){
				txn = processUtil.promoteTxnState(txn, nextTxnStatus, narrative);
				LOG.info(txn.getTransactionType() + stmt);
			}else{
				LOG.fatal("FAILED to submit " + txn.getTransactionType() + (sendToBank ? " REQUEST (to BANK) " : " REPLY (to SWITCH)"));
			}
		} catch (Exception e) {
			LOG.fatal("FAILED to process " + requestInfo.getTransactionType() + " REQUEST : " + requestInfo.getMessageId() + " : " + e.getMessage(), e);
			e.printStackTrace(System.err);
			txn = this.failTransaction(txn, requestInfo);
		}

	}
	
//	private boolean processAutoRegRequest(RequestInfo requestInfo){
//		//do the auto reg process
//		LOG.debug("Handling auto reg request.....");
//		LOG.debug("Handling customer reg...");
//		Customer customer = autoRegService.processCustomerAutoReg(requestInfo);
//		BankAccount bankAccount = null;
//		MobileProfile mobileProfile = null;
//		boolean result;
//		if(customer == null || customer.getId() == null){
//			LOG.debug("Failed to create customer... fail the auto reg part and post txf to NHL");
//			result = false;
//		}else{
//			LOG.debug("Customer has been created successfully... " + customer.getId());
//			mobileProfile = autoRegService.processMobileProfileAutoReg(customer, requestInfo);
//			if(mobileProfile == null || mobileProfile.getId() == null){
//				LOG.debug("Failed to create MOBILE PROFILE, abort an proceed to post TXF to NHL");
//				result = false;
//			}else{
//				LOG.debug("Created the customer's MOBILE PROFILE " + mobileProfile.getId());
//				bankAccount = autoRegService.processBankAccountAutoReg(customer, requestInfo, mobileProfile);
//				if(bankAccount == null || bankAccount.getId() == null){
//					LOG.debug("Failed to create bank account.... deregister customer and mobile profile, proceed to post TXF to NHL");
//					autoRegService.deleteCustomer(customer);
//					autoRegService.deleteMobileProfile(mobileProfile);
//					result = false;
//				}else{
//					LOG.debug("Done registering the auto reg customer's bank account " + bankAccount.getId());
//					result = true;
//				}
//			}
//		}
//		if(result){
//			//synch the stuff
//			autoRegService.synchAutoRegDetails(customer, mobileProfile, bankAccount);
//			
//		}
//		return result;
//	}

	

	private void handleBankAccountToNonHolderTransfer(RequestInfo requestInfo) {
		LOG.debug("Handle EWallet To Non Holder Transfer Request Has consumed...");
		// create process tranx
		ProcessTransaction txn = null;
		try {
			LOG.debug("target Bank I d " + requestInfo.getTargetBankId() + " SRC BANK " + requestInfo.getSourceBankId());
			txn = processUtil.populateProcessTransaction(requestInfo);
			txn.setNarrative("Transfer from bank account to non-holder");
			// we put the mobile number here coz this target customer is not
			// registered.
			txn.setTargetMobileId(requestInfo.getTargetMobile());
			txn.setSecretCode(requestInfo.getSecretCode());
			LOG.debug("IN TXN : target Bank I d " + txn.getToBankId() + " SRC BANK " + txn.getFromBankId());
			txn = processUtil.createProcessTransaction(txn);
			txn = processUtil.promoteTxnState(txn, TransactionStatus.DRAFT, TransactionStatus.DRAFT.name());
			
			txn = processUtil.validateBankAccountToNonHolderTransferReq(txn);
			LOG.debug("Done validating go for the postings to EQ3 with response code " + txn.getResponseCode());
			
			txn = processUtil.updateProcessTransactionWith(txn.getId(), txn.getResponseCode(), null, txn.getNarrative());
			LOG.debug("updated validation result..");
			
			boolean sendToBank = false;
			ResponseInfo responseInfo = null;
			BankRequest bankRequest = null;
			TransactionStatus nextTxnStatus;
			String narrative;
			
			if (ResponseCode.E000.name().equalsIgnoreCase(txn.getResponseCode())) {
				bankRequest = processUtil.populateBankRequest(txn);
				LOG.debug("Back in the MDB Target Acc : " + bankRequest.getTargetAccountNumber() + " and SRC : " + bankRequest.getSourceAccountNumber());
				// forward to bank queue
				nextTxnStatus = TransactionStatus.BANK_REQUEST;
				narrative = TransactionStatus.BANK_REQUEST.name();
				sendToBank = true;
			} else {
				nextTxnStatus = TransactionStatus.FAILED;
				narrative = txn.getNarrative();
				responseInfo = new ResponseInfo(txn.getNarrative(), ResponseCode.valueOf(txn.getResponseCode()), requestInfo, ResponseType.NOTIFICATION, 0L, 0L, 0L, txn.getMessageId());
				LOG.debug("Sending BANK ACCOUNT TO NON HOLDER FAILED (to SWITCH) for transaction...." + txn.getMessageId());
			}
			//if you get here then we must be ready to reply to the SWITCH / To forward to Bank
			boolean sent ;
			String stmt;
			if(sendToBank){
				//send request to bank
				sent = processUtil.submitRequest(bankRequest, false);
				stmt = " REQUEST has been sent (to BANK)";
			}else{
				//send reply to SWITCH
				sent = processUtil.submitResponse(responseInfo);
				stmt = " REPLY has been sent (to SWITCH)";
			}
			if(sent){
				txn = processUtil.promoteTxnState(txn, nextTxnStatus, narrative);
				LOG.info(txn.getTransactionType() + stmt);
			}else{
				LOG.fatal("FAILED to submit " + txn.getTransactionType() + (sendToBank ? " REQUEST (to BANK) " : " REPLY (to SWITCH)"));
			}
		} catch (Exception e) {
			LOG.fatal("FAILED to process " + requestInfo.getTransactionType() + " REQUEST : " + requestInfo.getMessageId() + " : " + e.getMessage(), e);
			e.printStackTrace(System.err);
			txn = this.failTransaction(txn, requestInfo);
		}

	}

	private void handleBankAccountToEWalletTransfer(RequestInfo requestInfo) {

		LOG.debug("Handle Bank Acc To Ewallet Transfer Request MDB Has consumed!!!!!!");
		// create process tranx
		ProcessTransaction txn = null;
		try {
			txn =  processUtil.populateProcessTransaction(requestInfo);
			txn.setNarrative("Transfer from regular bank account to e-wallet");

			txn = processUtil.createProcessTransaction(txn);
			txn = processUtil.promoteTxnState(txn, TransactionStatus.DRAFT, TransactionStatus.DRAFT.name());

			txn = processUtil.validateBankAccountToEWalletTransferReq(txn);
			LOG.debug("Done validating go for the postings to EQ3 with response code " + txn.getResponseCode());
			
			txn = processUtil.updateProcessTransactionWith(txn.getId(), txn.getResponseCode(), null, txn.getNarrative());
			LOG.debug("updated validation result..");
			
			boolean sendToBank = false;
			ResponseInfo responseInfo = null;
			BankRequest bankRequest = null;
			TransactionStatus nextTxnStatus;
			String narrative;
			
			if (ResponseCode.E000.name().equalsIgnoreCase(txn.getResponseCode())) {
				bankRequest = processUtil.populateBankRequest(txn);
				LOG.debug("Back in the MDB Target Acc : " + bankRequest.getTargetAccountNumber() + " and SRC : " + bankRequest.getSourceAccountNumber());
				// forward to bank queue
				nextTxnStatus = TransactionStatus.BANK_REQUEST;
				narrative = TransactionStatus.BANK_REQUEST.name();
				sendToBank = true;
			} else {
				nextTxnStatus = TransactionStatus.FAILED;
				narrative = txn.getNarrative();
				responseInfo = new ResponseInfo(txn.getNarrative(), ResponseCode.valueOf(txn.getResponseCode()), requestInfo, ResponseType.NOTIFICATION, 0L, 0L, 0L, txn.getMessageId());
			}
			//if you get here then we must be ready to reply to the SWITCH / To forward to Bank
			boolean sent ;
			String stmt;
			if(sendToBank){
				//send request to bank
				sent = processUtil.submitRequest(bankRequest, false);
				stmt = " REQUEST has been sent (to BANK)";
			}else{
				//send reply to SWITCH
				sent = processUtil.submitResponse(responseInfo);
				stmt = " REPLY has been sent (to SWITCH)";
			}
			if(sent){
				txn = processUtil.promoteTxnState(txn, nextTxnStatus, narrative);
				LOG.info(txn.getTransactionType() + stmt);
			}else{
				LOG.fatal("FAILED to submit " + txn.getTransactionType() + (sendToBank ? " REQUEST (to BANK) " : " REPLY (to SWITCH)"));
			}
		} catch (Exception e) {
			LOG.fatal("FAILED to process " + requestInfo.getTransactionType() + " REQUEST : " + requestInfo.getMessageId() + " : " + e.getMessage(), e);
			e.printStackTrace(System.err);
			txn = this.failTransaction(txn, requestInfo);
		}
	}

	private void handleBankAccountToBankAccountTransfer(RequestInfo requestInfo) {

		LOG.debug("HandleBankAccountToBankAccountTransferReq MDB Has consumed!!!!!!");
		// create process tranx
		ProcessTransaction txn = null;
		try {
			txn = processUtil.populateProcessTransaction(requestInfo);

			txn.setNarrative("Transfer from regular bank account to bank account");

			txn = processUtil.createProcessTransaction(txn);
			txn = processUtil.promoteTxnState(txn, TransactionStatus.DRAFT, TransactionStatus.DRAFT.name());

			txn = processUtil.validateBankAccountToBankAccountTransferReq(txn);
			LOG.debug("Done validating go for the postings to EQ3 with response code " + txn.getResponseCode());
			
			txn = processUtil.updateProcessTransactionWith(txn.getId(), txn.getResponseCode(), null, txn.getNarrative());
			LOG.debug("updated validation result..");

			boolean sendToBank = false;
			ResponseInfo responseInfo = null;
			BankRequest bankRequest = null;
			TransactionStatus nextTxnStatus;
			String narrative;
			
			if (ResponseCode.E000.name().equalsIgnoreCase(txn.getResponseCode())) {
				bankRequest = processUtil.populateBankRequest(txn);
				LOG.debug("Back in the MDB Target Acc : " + bankRequest.getTargetAccountNumber() + " and SRC : " + bankRequest.getSourceAccountNumber());
				// forward to bank queue
				nextTxnStatus = TransactionStatus.BANK_REQUEST;
				narrative = TransactionStatus.BANK_REQUEST.name();
				sendToBank = true;
			} else {
				nextTxnStatus = TransactionStatus.FAILED;
				narrative = txn.getNarrative();
				responseInfo = new ResponseInfo(txn.getNarrative(), ResponseCode.valueOf(txn.getResponseCode()), requestInfo, ResponseType.NOTIFICATION, 0L, 0L, 0L, txn.getMessageId());
			}
			//if you get here then we must be ready to reply to the SWITCH / To forward to Bank
			boolean sent ;
			String stmt;
			if(sendToBank){
				//send request to bank
				sent = processUtil.submitRequest(bankRequest, false);
				stmt = " REQUEST has been sent (to BANK)";
			}else{
				//send reply to SWITCH
				sent = processUtil.submitResponse(responseInfo);
				stmt = " REPLY has been sent (to SWITCH)";
			}
			if(sent){
				txn = processUtil.promoteTxnState(txn, nextTxnStatus, narrative);
				LOG.info(txn.getTransactionType() + stmt);
			}else{
				LOG.fatal("FAILED to submit " + txn.getTransactionType() + (sendToBank ? " REQUEST (to BANK) " : " REPLY (to SWITCH)"));
			}
		} catch (Exception e) {
			LOG.fatal("FAILED to process " + requestInfo.getTransactionType() + " REQUEST : " + requestInfo.getMessageId() + " : " + e.getMessage(), e);
			e.printStackTrace(System.err);
			txn = this.failTransaction(txn, requestInfo);
		}

	}

	

	private void handleBalanceRequest(RequestInfo requestInfo) {
		ProcessTransaction txn = null;
		try {
			// create process tranx
			txn = processUtil.populateProcessTransaction(requestInfo);
			txn.setNarrative("Balance Request.");
			txn = processUtil.createProcessTransaction(txn);
			txn = processUtil.promoteTxnState(txn, TransactionStatus.DRAFT, TransactionStatus.DRAFT.name());
			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
			BankAccount bankAccount = bankService.getUniqueBankAccountByAccountNumber(txn.getSourceAccountNumber());
			
			boolean sendToBank = false;
			ResponseInfo responseInfo = null;
			BankRequest bankRequest = null;
			TransactionStatus nextTxnStatus;
			String narrative;
			
			if (bankAccount == null || bankAccount.getAccountNumber() == null) {
				nextTxnStatus = TransactionStatus.FAILED;
				narrative = "No account held.";
				responseInfo = new ResponseInfo("Account " + txn.getSourceAccountNumber() + " is not registered on ZB e-Wallet", ResponseCode.E900, requestInfo, ResponseType.NOTIFICATION, 0L, 0L, 0L, txn.getMessageId());
			} else if (BankAccountType.E_WALLET.equals(bankAccount.getType()) || BankAccountType.AGENT_EWALLET.equals(bankAccount.getType())) {
				long bal = bankAccount.getRunningBalance();
				nextTxnStatus = TransactionStatus.COMPLETED;
				narrative = "Transaction Successful.";
				responseInfo = new ResponseInfo("Balance Enquiry Successful.", ResponseCode.E000, requestInfo, ResponseType.NOTIFICATION, bal, bal, 0L, txn.getMessageId());				
			} else {
				bankRequest = processUtil.processBankAccountBalanceRequest(txn);
				nextTxnStatus = TransactionStatus.BANK_REQUEST;
				narrative = TransactionStatus.BANK_REQUEST.name();
				sendToBank = true;				
			}
			
			//if you get here then we must be ready to reply to the SWITCH / To forward to Bank
			boolean sent ;
			String stmt;
			if(sendToBank){
				//send request to bank
				sent = processUtil.submitRequest(bankRequest, false);
				stmt = " REQUEST has been sent (to BANK)";
			}else{
				//send reply to SWITCH
				sent = processUtil.submitResponse(responseInfo);
				stmt = " REPLY has been sent (to SWITCH)";
			}
			if(sent){
				txn = processUtil.promoteTxnState(txn, nextTxnStatus, narrative);
				LOG.info(txn.getTransactionType() + stmt);
			}else{
				LOG.fatal("FAILED to submit " + txn.getTransactionType() + (sendToBank ? " REQUEST (to BANK) " : " REPLY (to SWITCH)"));
			}
		} catch (Exception e) {
			LOG.fatal("FAILED to process " + requestInfo.getTransactionType() + " REQUEST : " + requestInfo.getMessageId() + " : " + e.getMessage(), e);
			e.printStackTrace(System.err);
			txn = this.failTransaction(txn, requestInfo);
		}
	}
	
	private void handleMiniStatementRequest(RequestInfo requestInfo) {
		// create process tranx
		ProcessTransaction txn = null;
		try {
			txn = processUtil.populateProcessTransaction(requestInfo);
			txn.setNarrative("Mini Statement Request.");
			txn = processUtil.createProcessTransaction(txn);
			txn = processUtil.promoteTxnState(txn, TransactionStatus.DRAFT, TransactionStatus.DRAFT.name());
			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
			BankAccount bankAccount = bankService.getUniqueBankAccountByAccountNumber(txn.getSourceAccountNumber());
			boolean sendToBank = false;
			ResponseInfo responseInfo = null;
			BankRequest bankRequest = null;
			TransactionStatus nextTxnStatus;
			String narrative;
			if (bankAccount == null || bankAccount.getAccountNumber() == null) {
				nextTxnStatus = TransactionStatus.FAILED;
				narrative = "No account held.";
				responseInfo = new ResponseInfo("Account " + txn.getSourceAccountNumber() + " is not registered on ZB e-Wallet", ResponseCode.E900, requestInfo, ResponseType.NOTIFICATION, 0L, 0L, 0L, txn.getMessageId());
			}else if (BankAccountType.E_WALLET.equals(bankAccount.getType())) {
				long balance = bankAccount.getRunningBalance();
				nextTxnStatus = TransactionStatus.COMPLETED;
				narrative = "Transaction Successful.";
				responseInfo = new ResponseInfo(processUtil.processMiniStatementRequest(txn), ResponseCode.E000, requestInfo, ResponseType.NOTIFICATION, balance, balance, 0L, txn.getMessageId());
				
			} else {
				bankRequest = processUtil.populateBankRequest(txn);
				sendToBank = true;
				nextTxnStatus = TransactionStatus.BANK_REQUEST;
				narrative = TransactionStatus.BANK_REQUEST.name();
				
				LOG.debug("Bank MINI STMT has been sent to BANK..");
			}
			//if you get here then we must be ready to reply to the SWITCH / To forward to Bank
			boolean sent ;
			String stmt;
			if(sendToBank){
				//send request to bank
				sent = processUtil.submitRequest(bankRequest, false);
				stmt = " REQUEST has been sent (to BANK)";
			}else{
				//send reply to SWITCH
				sent = processUtil.submitResponse(responseInfo);
				stmt = " REPLY has been sent (to SWITCH)";
			}
			if(sent){
				txn = processUtil.promoteTxnState(txn, nextTxnStatus, narrative);
				LOG.info(txn.getTransactionType() + stmt);
			}else{
				LOG.fatal("FAILED to submit " + txn.getTransactionType() + (sendToBank ? " REQUEST (to BANK) " : " REPLY (to SWITCH)"));
			}
		} catch (Exception e) {
			LOG.fatal("FAILED to process " + requestInfo.getTransactionType() + " REQUEST : " + requestInfo.getMessageId() + " : " + e.getMessage(), e);
			e.printStackTrace(System.err);
			txn = this.failTransaction(txn, requestInfo);
		}
	}
	

	private void handlePhoneLockRequest(RequestInfo requestInfo) {
		try {
			LOG.debug("Got a mobile profile lock request");
			CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
			MobileProfile mobileProfile = customerService.getMobileProfileByBankAndMobileNumber(requestInfo.getSourceBankId(), requestInfo.getSourceMobile());
			LOG.debug("Found mobile profile to lock...");
			mobileProfile.setStatus(MobileProfileStatus.LOCKED);
			customerService.updateMobileProfile(mobileProfile, EWalletConstants.SYSTEM);
			LOG.debug("Done locking mobile profile.");
			ResponseInfo responseInfo = new ResponseInfo("Your account has been locked. Please contact your nearest branch.", ResponseCode.E000, requestInfo, ResponseType.NOTIFICATION, 0L, 0L, 0L, requestInfo.getMessageId());
			LOG.info("Sending PHONE LOCK REPLY (to SWITCH) for txn ...." + requestInfo.getMessageId() + " and Mobile " + requestInfo.getSourceMobile());
			processUtil.submitResponse(responseInfo);
		} catch (Exception e) {
			LOG.fatal("FAILED to process " + requestInfo.getTransactionType() + " REQUEST : " + requestInfo.getMessageId() + " : " + e.getMessage(), e);
			e.printStackTrace(System.err);
		}
	}

	private void handlePinChangeRequest(RequestInfo requestInfo) {
		try {
			// create process tranx
			ProcessTransaction txn = processUtil.populateProcessTransaction(requestInfo);
			txn.setNarrative("Change Passcode.");
			txn.setSecretCode(requestInfo.getSecretCode()); // new passcode

			txn = processUtil.createProcessTransaction(txn);
			// password prompt
			String response = processUtil.processPinChangeRequest(txn, requestInfo.getOldPin(), requestInfo.getNewPin());
			LOG.debug("he response from pin change : " + response);
			ResponseCode responseCode;
			TransactionStatus nextTxnStatus ;
			if (ResponseCode.E000.name().equalsIgnoreCase(response)) {
				nextTxnStatus = TransactionStatus.COMPLETED;
				responseCode = ResponseCode.E000;
			} else {
				nextTxnStatus = TransactionStatus.FAILED;
				responseCode = ResponseCode.E819;
			}
			ResponseInfo responseInfo = new ResponseInfo(response, responseCode, requestInfo, ResponseType.NOTIFICATION, 0L, 0L, 0L, txn.getMessageId());
			
			boolean sent = processUtil.submitResponse(responseInfo);
			if(sent){
				txn = processUtil.promoteTxnState(txn, nextTxnStatus, response);
				LOG.info("PIN CHANGE REPLY SENT (to SWITCH) for transaction...." + txn.getMessageId());
			}else{
				LOG.fatal("FAILED to submit " + txn.getTransactionType() + " REPLY (to SWITCH)");
			}
			
		} catch (Exception e) {
			LOG.fatal("FAILED to process " + requestInfo.getTransactionType() + " REQUEST : " + requestInfo.getMessageId() + " : " + e.getMessage(), e);
			e.printStackTrace(System.err);
		}
	}

	private void handleEWalletAccountToBankAccountTransfer(RequestInfo requestInfo) {

		// create process tranx
		ProcessTransaction txn = null;
		
		try {
			txn = processUtil.populateProcessTransaction(requestInfo);
			txn.setNarrative("Transfer from ewallet account to  bank account");

			txn = processUtil.createProcessTransaction(txn);
			txn = processUtil.promoteTxnState(txn, TransactionStatus.DRAFT, TransactionStatus.DRAFT.name());
			
			txn = processUtil.validateEwalletAccountToBankAccountTransferReq(txn);
			LOG.debug("Done validating go for the postings to EQ3 with response code " + txn.getResponseCode());
			
			txn = processUtil.updateProcessTransactionWith(txn.getId(), txn.getResponseCode(), null, txn.getNarrative());
			LOG.debug("updated validation result..");
			
			LOG.debug("Response code after validate is " + txn.getResponseCode() + " and narrative " + txn.getNarrative());
			
			boolean sendToBank = false;
			ResponseInfo responseInfo = null;
			BankRequest bankRequest = null;
			TransactionStatus nextTxnStatus;
			String narrative;
			
			if (ResponseCode.E000.name().equalsIgnoreCase(txn.getResponseCode())) {

				bankRequest = processUtil.populateBankRequest(txn);
				// forward to bank queue
				nextTxnStatus = TransactionStatus.BANK_REQUEST;
				narrative = TransactionStatus.BANK_REQUEST.name();
				sendToBank = true;
				
			} else {
				nextTxnStatus =  TransactionStatus.FAILED;
				narrative = txn.getNarrative();
				responseInfo = new ResponseInfo(txn.getNarrative(), ResponseCode.valueOf(txn.getResponseCode()), requestInfo, ResponseType.NOTIFICATION, 0L, 0L, 0L, txn.getMessageId());
				LOG.debug("Txn has failed somewhere in validations, return to sender : " + txn.getMessageId());
				
			}
			
			//if you get here then we must be ready to reply to the SWITCH / To forward to Bank
			boolean sent ;
			String stmt;
			if(sendToBank){
				//send request to bank
				sent = processUtil.submitRequest(bankRequest, false);
				stmt = " REQUEST has been sent (to BANK)";
			}else{
				//send reply to SWITCH
				sent = processUtil.submitResponse(responseInfo);
				stmt = " REPLY has been sent (to SWITCH)";
			}
			if(sent){
				txn = processUtil.promoteTxnState(txn, nextTxnStatus, narrative);
				LOG.info(txn.getTransactionType() + stmt);
			}else{
				LOG.fatal("FAILED to submit " + txn.getTransactionType() + (sendToBank ? " REQUEST (to BANK) " : " REPLY (to SWITCH)"));
			}
		} catch (Exception e) {
			LOG.fatal("FAILED to process " + requestInfo.getTransactionType() + " REQUEST : " + requestInfo.getMessageId() + " : " + e.getMessage(), e);
			e.printStackTrace(System.err);
			txn = this.failTransaction(txn, requestInfo);
		}
	}
	
	private void handleEWalletAccountToEWalletAccountTransfer(RequestInfo requestInfo) {
		ProcessTransaction txn = null;
		try {
			LOG.debug("Going to populate the txn......");
			txn = processUtil.handleEwalletToEwalletTransfer(requestInfo);
			// Rechecking validations
			LOG.debug("Done populating, going for the validation request");
			
			txn = this.processUtil.validateEWalletToEWalletTransfer(txn);
			LOG.debug("Done validating go for the postings to EQ3 with response code " + txn.getResponseCode());
			
			txn = processUtil.updateProcessTransactionWith(txn.getId(), txn.getResponseCode(), null, txn.getNarrative());
			LOG.debug("updated validation result..");
			
			boolean sendToBank = false;
			ResponseInfo responseInfo = null;
			BankRequest bankRequest = null;
			TransactionStatus nextTxnStatus;
			String narrative;
			
			if (ResponseCode.E000.name().equalsIgnoreCase(txn.getResponseCode())) {
				nextTxnStatus = TransactionStatus.BANK_REQUEST;
				narrative = TransactionStatus.BANK_REQUEST.name();
				bankRequest = processUtil.populateBankRequest(txn);
				sendToBank = true;
			} else {
				nextTxnStatus = TransactionStatus.FAILED;
				narrative = txn.getNarrative();
				responseInfo = new ResponseInfo(txn.getNarrative(), ResponseCode.valueOf(txn.getResponseCode()), requestInfo, ResponseType.NOTIFICATION, 0L, 0L, 0L, txn.getMessageId());
				LOG.debug("Txn has failed somewhere in validations, return to sender : " + txn.getMessageId());				
			}
			//if you get here then we must be ready to reply to the SWITCH / To forward to Bank
			boolean sent ;
			String stmt;
			if(sendToBank){
				//send request to bank
				sent = processUtil.submitRequest(bankRequest, false);
				stmt = " REQUEST has been sent (to BANK)";
			}else{
				//send reply to SWITCH
				sent = processUtil.submitResponse(responseInfo);
				stmt = " REPLY has been sent (to SWITCH)";
			}
			if(sent){
				txn = processUtil.promoteTxnState(txn, nextTxnStatus, narrative);
				LOG.info(txn.getTransactionType() + stmt);
			}else{
				LOG.fatal("FAILED to submit " + txn.getTransactionType() + (sendToBank ? " REQUEST (to BANK) " : " REPLY (to SWITCH)"));
			}
		} catch (Exception e) {
			LOG.fatal("FAILED to process " + requestInfo.getTransactionType() + " REQUEST : " + requestInfo.getMessageId() + " : " + e.getMessage(), e);
			e.printStackTrace(System.err);
			txn = this.failTransaction(txn, requestInfo);
		}
	}
	
	private void handleTopupRequest(RequestInfo requestInfo) {
		LOG.debug(">>>>>>>>>>>>>>>>>>>>HandleTopupRequest MDB Has consumed!!!!!!");
		// create process tranx
		ProcessTransaction txn = null;
		try {
			txn = processUtil.populateProcessTransaction(requestInfo);
			LOG.debug("Done Populating the process transaction.");
			txn.setNarrative("Topup Request.");
			txn = processUtil.createProcessTransaction(txn);
			txn = processUtil.promoteTxnState(txn, TransactionStatus.DRAFT, TransactionStatus.DRAFT.name());
			LOG.debug("Done promoting state.");

			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
			BankAccount bankAccount = bankService.getUniqueBankAccountByAccountNumber(txn.getSourceAccountNumber());

			txn = processUtil.validateTopupRequest(txn, bankAccount);
			LOG.debug("Done validating go for the postings to EQ3 with response code " + txn.getResponseCode());
			
			txn = processUtil.updateProcessTransactionWith(txn.getId(), txn.getResponseCode(), null, txn.getNarrative());
			LOG.debug("updated validation result..");
			
			boolean sendToBank = false;
			ResponseInfo responseInfo = null;
			BankRequest bankRequest = null;
			TransactionStatus nextTxnStatus;
			String narrative;
			
			if (ResponseCode.E000.name().equalsIgnoreCase(txn.getResponseCode())) {
				if (BankAccountType.E_WALLET.equals(bankAccount.getType())) {
					LOG.debug("Found bank account of type EWALLET " + bankAccount);
					txn.setTransactionType(TransactionType.EWALLET_TOPUP);
					txn = this.processUtil.updateProcessTransaction(txn);
				} else {
					LOG.debug("Found bank account of type EQ3 " + bankAccount);
				}

				bankRequest = processUtil.populateBankRequest(txn);
								
				nextTxnStatus = TransactionStatus.BANK_REQUEST;
				narrative = TransactionStatus.BANK_REQUEST.name();
				sendToBank = true;
			} else {
				LOG.debug("Txn has failed somewhere in validations, return to sender : " + txn.getMessageId());
				nextTxnStatus = TransactionStatus.FAILED;
				narrative = txn.getNarrative();
				responseInfo = new ResponseInfo(txn.getNarrative(), ResponseCode.valueOf(txn.getResponseCode()), requestInfo, ResponseType.NOTIFICATION, 0L, 0L, 0L, txn.getMessageId());
				sendToBank = false;
			}
			LOG.debug("Done with handle TOPUP REQ...");
			//if you get here then we must be ready to reply to the SWITCH / To forward to Bank
			boolean sent ;
			String stmt;
			if(sendToBank){
				//send request to bank
				sent = processUtil.submitRequest(bankRequest, false);
				stmt = " REQUEST has been sent (to BANK)";
			}else{
				//send reply to SWITCH
				sent = processUtil.submitResponse(responseInfo);
				stmt = " REPLY has been sent (to SWITCH)";
			}
			if(sent){
				txn = processUtil.promoteTxnState(txn, nextTxnStatus, narrative);
				LOG.info(txn.getTransactionType() + stmt);
			}else{
				LOG.fatal("FAILED to submit " + txn.getTransactionType() + (sendToBank ? " REQUEST (to BANK) " : " REPLY (to SWITCH)"));
			}
		} catch (Exception e) {
			LOG.fatal("FAILED to process " + requestInfo.getTransactionType() + " REQUEST : " + requestInfo.getMessageId() + " : " + e.getMessage(), e);
			e.printStackTrace(System.err);
			txn = this.failTransaction(txn, requestInfo);
		}
	}
	
	
	private ProcessTransaction failTransaction(ProcessTransaction txn, RequestInfo requestInfo) {
		try {
			if(txn != null && requestInfo != null) {
				ResponseInfo responseInfo = new ResponseInfo(ResponseCode.E505.getDescription(), ResponseCode.E505, requestInfo, ResponseType.NOTIFICATION, 0L, 0L, 0L, txn.getMessageId());
				txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, TransactionStatus.FAILED.name());
				boolean returnToSender = processUtil.submitResponse(responseInfo);
				LOG.error("Txn has failed somewhere in validations : " + txn.getMessageId() + " Return To Sender : " + returnToSender);
				
			}
		} catch (Exception e) {
			LOG.fatal("FAILED to FAIL TXN : [" + txn.getTransactionType() + " | " + txn.getMessageId() + " | " + txn.getSourceMobile() + "]");
		}
		return txn;
	}
	
	private void handleAgentCustomerDepositRequest(RequestInfo requestInfo) {
		LOG.debug("Handle AGENT CUSTOMER DEPOSIT Request Has Consumed...");
		ProcessTransaction txn = null;
		try {
			LOG.debug("Going to populate the txn......");
			txn = processUtil.handleAgentCustomerDepositRequest(requestInfo);
			// Rechecking validations
			LOG.debug("Done populating, going for the validation request");
			txn = this.processUtil.validateAgentCustomerDepositRequest(txn);
			LOG.debug("Done validating go for the postings to EQ3 with response code " + txn.getResponseCode());
			txn = processUtil.updateProcessTransactionWith(txn.getId(), txn.getResponseCode(), null, txn.getNarrative());
			LOG.debug("Transaction validation response updated..");
		
			boolean sendToBank = false;
			ResponseInfo responseInfo = null;
			BankRequest bankRequest = null;
			TransactionStatus nextTxnStatus;
			String narrative;
			
			if (ResponseCode.E000.name().equalsIgnoreCase(txn.getResponseCode())) {
				bankRequest = processUtil.populateBankRequest(txn);
				LOG.debug("Back in the MDB Target Acc : " + bankRequest.getTargetAccountNumber() + " and SRC : " + bankRequest.getSourceAccountNumber());
				// forward to bank queue
				nextTxnStatus = TransactionStatus.BANK_REQUEST;
				narrative = TransactionStatus.BANK_REQUEST.name();
				sendToBank = true;
			} else {
				nextTxnStatus = TransactionStatus.FAILED;
				narrative = txn.getNarrative();
				responseInfo = new ResponseInfo(txn.getNarrative(), ResponseCode.valueOf(txn.getResponseCode()), requestInfo, ResponseType.NOTIFICATION, 0L, 0L, 0L, txn.getMessageId());
			}
			//if you get here then we must be ready to reply to the SWITCH / To forward to Bank
			boolean sent ;
			String stmt;
			if(sendToBank){
				//send request to bank
				sent = processUtil.submitRequest(bankRequest, false);
				stmt = " REQUEST has been sent (to BANK)";
			}else{
				//send reply to SWITCH
				sent = processUtil.submitResponse(responseInfo);
				stmt = " REPLY has been sent (to SWITCH)";
			}
			if(sent){
				txn = processUtil.promoteTxnState(txn, nextTxnStatus, narrative);
				LOG.info(txn.getTransactionType() + stmt);
			}else{
				LOG.fatal("FAILED to submit " + txn.getTransactionType() + (sendToBank ? " REQUEST (to BANK) " : " REPLY (to SWITCH)"));
			}
		} catch (Exception e) {
			LOG.fatal("FAILED to process " + requestInfo.getTransactionType() + " REQUEST : " + requestInfo.getMessageId() + " : " + e.getMessage(), e);
			e.printStackTrace(System.err);
			txn = this.failTransaction(txn, requestInfo);
		}
	}
	
	private void handleAgentCustomerWithdrawalRequest(RequestInfo requestInfo) {
		LOG.debug("Handle AGENT CUSTOMER WITHDRAWAL Request Has Consumed...");
		ProcessTransaction txn = null;
		try {
			LOG.debug("Going to populate the txn......");
			txn = processUtil.handleAgentCustomerWithdrawalRequest(requestInfo);
			// Re-checking validations
			LOG.debug("Done populating, going for the validation request");
			txn = this.processUtil.validateAgentCustomerWithdrawalRequest(txn);
			LOG.debug("Done validating go for the postings to EQ3 with response code " + txn.getResponseCode());
			txn = processUtil.updateProcessTransactionWith(txn.getId(), txn.getResponseCode(), null, txn.getNarrative());
			LOG.debug("Transaction validation response updated..");
		
			boolean sendToBank = false;
			ResponseInfo responseInfo = null;
			BankRequest bankRequest = null;
			TransactionStatus nextTxnStatus;
			String narrative;
			
			if (ResponseCode.E000.name().equalsIgnoreCase(txn.getResponseCode())) {
				bankRequest = processUtil.populateBankRequest(txn);
				LOG.debug("Back in the MDB Target Acc : " + bankRequest.getTargetAccountNumber() + " and SRC : " + bankRequest.getSourceAccountNumber());
				// forward to bank queue
				nextTxnStatus = TransactionStatus.BANK_REQUEST;
				narrative = TransactionStatus.BANK_REQUEST.name();
				sendToBank = true;
			} else {
				nextTxnStatus = TransactionStatus.FAILED;
				narrative = txn.getNarrative();
				responseInfo = new ResponseInfo(txn.getNarrative(), ResponseCode.valueOf(txn.getResponseCode()), requestInfo, ResponseType.NOTIFICATION, 0L, 0L, 0L, txn.getMessageId());
			}
			//if you get here then we must be ready to reply to the SWITCH / To forward to Bank
			boolean sent ;
			String stmt;
			if(sendToBank){
				//send request to bank
				sent = processUtil.submitRequest(bankRequest, false);
				stmt = " REQUEST has been sent (to BANK)";
			}else{
				//send reply to SWITCH
				sent = processUtil.submitResponse(responseInfo);
				stmt = " REPLY has been sent (to SWITCH)";
			}
			if(sent){
				txn = processUtil.promoteTxnState(txn, nextTxnStatus, narrative);
				LOG.info(txn.getTransactionType() + stmt);
			}else{
				LOG.fatal("FAILED to submit " + txn.getTransactionType() + (sendToBank ? " REQUEST (to BANK) " : " REPLY (to SWITCH)"));
			}
		} catch (Exception e) {
			LOG.fatal("FAILED to process " + requestInfo.getTransactionType() + " REQUEST : " + requestInfo.getMessageId() + " : " + e.getMessage(), e);
			e.printStackTrace(System.err);
			txn = this.failTransaction(txn, requestInfo);
		}
	}
	
	private void handleAgentCustomerNonHolderWithdrawalRequest(RequestInfo requestInfo) {
		LOG.debug("Handle AGENT CUSTOMER NONHOLDER WITHDRAWAL Request Has Consumed...");
		ProcessTransaction txn = null;
		try {
			
			LOG.debug("Going to populate the txn......");
			txn = processUtil.populateProcessTransaction(requestInfo);
			txn.setOldMessageId(requestInfo.getOldReference());
			txn.setSecretCode(requestInfo.getSecretCode());
			LOG.debug("OldMessageId and SecretCode updated..");
			txn = processUtil.createProcessTransaction(txn);
			
			LOG.debug("Validate original nonholder transfer..");
			ProcessResponse processResponse = processUtil.validateOriginalNonHolderTransfer(requestInfo);
			
			if (!ResponseCode.E000.name().equals(processResponse.getResponseCode())) {
			
				LOG.debug("NonHolder transfer validation FAILED..");
				TransactionStatus nextTxnStatus = TransactionStatus.FAILED;
				ResponseInfo responseInfo = new ResponseInfo(processResponse.getNarrative(), ResponseCode.valueOf(processResponse.getResponseCode()), requestInfo, ResponseType.NOTIFICATION, 0L, 0L, 0L, txn.getMessageId());
				LOG.debug("Txn has failed somewhere in validations, return to sender : " + txn.getMessageId());
				//send reply to SWITCH
				boolean sent = processUtil.submitResponse(responseInfo);
				String stmt = " REPLY has been sent (to SWITCH)";
				if(sent){
					txn = processUtil.promoteTxnState(txn, nextTxnStatus, txn.getNarrative());
					LOG.info(txn.getTransactionType() + stmt);
				}else{
					LOG.fatal("FAILED to submit " + txn.getTransactionType() + " REPLY (to SWITCH)");
				}				
				return;
			}
			
			LOG.debug("NonHolder transfer Validation Success......PROCEED");
			txn = processUtil.handleAgentCustomerNonHolderWithdrawalRequest(requestInfo, txn);
			// Rechecking validations
			LOG.debug("Done populating, going for the validation request");
			txn = this.processUtil.validateAgentCustomerNonHolderWithdrawalRequest(txn);
			LOG.debug("Done validating go for the postings to EQ3 with response code " + txn.getResponseCode());
			txn = processUtil.updateProcessTransactionWith(txn.getId(), txn.getResponseCode(), null, txn.getNarrative());

			boolean sendToBank = false;
			ResponseInfo responseInfo = null;
			BankRequest bankRequest = null;
			TransactionStatus nextTxnStatus;
			String narrative;
			
			if (ResponseCode.E000.name().equalsIgnoreCase(txn.getResponseCode())) {
				bankRequest = processUtil.populateBankRequest(txn);
				LOG.debug("Back in the MDB Target Acc : " + bankRequest.getTargetAccountNumber() + " and SRC : " + bankRequest.getSourceAccountNumber());
				// forward to bank queue
				nextTxnStatus = TransactionStatus.BANK_REQUEST;
				narrative = TransactionStatus.BANK_REQUEST.name();
				sendToBank = true;
			} else {
				nextTxnStatus = TransactionStatus.FAILED;
				narrative = txn.getNarrative();
				responseInfo = new ResponseInfo(txn.getNarrative(), ResponseCode.valueOf(txn.getResponseCode()), requestInfo, ResponseType.NOTIFICATION, 0L, 0L, 0L, txn.getMessageId());
			}
			//if you get here then we must be ready to reply to the SWITCH / To forward to Bank
			boolean sent ;
			String stmt;
			if(sendToBank){
				//send request to bank
				sent = processUtil.submitRequest(bankRequest, false);
				stmt = " REQUEST has been sent (to BANK)";
			}else{
				//send reply to SWITCH
				sent = processUtil.submitResponse(responseInfo);
				stmt = " REPLY has been sent (to SWITCH)";
			}
			if(sent){
				txn = processUtil.promoteTxnState(txn, nextTxnStatus, narrative);
				LOG.info(txn.getTransactionType() + stmt);
			}else{
				LOG.fatal("FAILED to submit " + txn.getTransactionType() + (sendToBank ? " REQUEST (to BANK) " : " REPLY (to SWITCH)"));
			}
		} catch (Exception e) {
			LOG.fatal("FAILED to process " + requestInfo.getTransactionType() + " REQUEST : " + requestInfo.getMessageId() + " : " + e.getMessage(), e);
			e.printStackTrace(System.err);
			txn = this.failTransaction(txn, requestInfo);
		}
	}
	
	
	private void handleAgentEWalletAccountToBankAccountTransfer(RequestInfo requestInfo) {

		LOG.debug("Handle Agent EWallet To Bank Account Transfer Request.");
		// create process tranx
		ProcessTransaction txn = null;
		
		try {
			requestInfo.setLocationType(TransactionLocationType.AGENT);
			txn = processUtil.populateProcessTransaction(requestInfo);
			txn.setNarrative("Transfer from agent ewallet account to  bank account");

			txn = processUtil.createProcessTransaction(txn);
			txn = processUtil.promoteTxnState(txn, TransactionStatus.DRAFT, TransactionStatus.DRAFT.name());
			
			txn = processUtil.validateAgentEwalletAccountToBankAccountTransferReq(txn);
			LOG.debug("Done validating go for the postings to EQ3 with response code " + txn.getResponseCode());
			
			txn = processUtil.updateProcessTransactionWith(txn.getId(), txn.getResponseCode(), null, txn.getNarrative());
			LOG.debug("updated validation result..");
			

			boolean sendToBank = false;
			ResponseInfo responseInfo = null;
			BankRequest bankRequest = null;
			TransactionStatus nextTxnStatus;
			String narrative;
			
			if (ResponseCode.E000.name().equalsIgnoreCase(txn.getResponseCode())) {
				bankRequest = processUtil.populateBankRequest(txn);
				LOG.debug("Back in the MDB Target Acc : " + bankRequest.getTargetAccountNumber() + " and SRC : " + bankRequest.getSourceAccountNumber());
				// forward to bank queue
				nextTxnStatus = TransactionStatus.BANK_REQUEST;
				narrative = TransactionStatus.BANK_REQUEST.name();
				sendToBank = true;
			} else {
				nextTxnStatus = TransactionStatus.FAILED;
				narrative = txn.getNarrative();
				responseInfo = new ResponseInfo(txn.getNarrative(), ResponseCode.valueOf(txn.getResponseCode()), requestInfo, ResponseType.NOTIFICATION, 0L, 0L, 0L, txn.getMessageId());
			}
			//if you get here then we must be ready to reply to the SWITCH / To forward to Bank
			boolean sent ;
			String stmt;
			if(sendToBank){
				//send request to bank
				sent = processUtil.submitRequest(bankRequest, false);
				stmt = " REQUEST has been sent (to BANK)";
			}else{
				//send reply to SWITCH
				sent = processUtil.submitResponse(responseInfo);
				stmt = " REPLY has been sent (to SWITCH)";
			}
			if(sent){
				txn = processUtil.promoteTxnState(txn, nextTxnStatus, narrative);
				LOG.info(txn.getTransactionType() + stmt);
			}else{
				LOG.fatal("FAILED to submit " + txn.getTransactionType() + (sendToBank ? " REQUEST (to BANK) " : " REPLY (to SWITCH)"));
			}
		} catch (Exception e) {
			LOG.fatal("FAILED to process " + requestInfo.getTransactionType() + " REQUEST : " + requestInfo.getMessageId() + " : " + e.getMessage(), e);
			e.printStackTrace(System.err);
			txn = this.failTransaction(txn, requestInfo);
		}
	}
	
	private void handleAgentTrannsactionSummaryRequest(RequestInfo requestInfo) {
		LOG.debug(">>>>>>>>>>>>>>>>>>>>AGENT SUMMARY REQUEST MDB Has consumed!!!!!!");
		ProcessTransaction txn = null;
		try {
			if(requestInfo.getSummaryDate() == null){
				requestInfo.setSummaryDate(new Date(System.currentTimeMillis()));
			}
			txn = processUtil.populateProcessTransaction(requestInfo);
			txn.setNarrative("AGENT SUMMARY");
			txn.setValueDate(requestInfo.getSummaryDate());
			txn = processUtil.createProcessTransaction(txn);
			txn = processUtil.promoteTxnState(txn, TransactionStatus.DRAFT, TransactionStatus.DRAFT.name());
			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
			BankAccount bankAccount = bankService.getUniqueBankAccountByAccountNumber(txn.getSourceAccountNumber());
			
			boolean sendToBank = false;
			ResponseInfo responseInfo = null;
			TransactionStatus nextTxnStatus;
			String narrative;
			if(bankAccount == null || bankAccount.getId() == null){
				responseInfo = new ResponseInfo("No account held.", ResponseCode.E832, requestInfo, ResponseType.NOTIFICATION, 0L, 0L, 0L, txn.getMessageId());
				LOG.debug("Sending AGENT SUMMARY REPLY (to SWITCH) for transaction...." + txn.getMessageId());
				//if you get here then we must be ready to reply to the SWITCH / To forward to Bank
				boolean sent = processUtil.submitResponse(responseInfo);
				String stmt = " REPLY has been sent (to SWITCH)";
				
				if(sent){
					txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, responseInfo.getNarrative());
					LOG.info(txn.getTransactionType() + stmt);
				}else{
					LOG.fatal("FAILED to submit " + txn.getTransactionType() + (sendToBank ? " REQUEST (to BANK) " : " REPLY (to SWITCH)"));
				}
				return;
			}else {
				Calendar cal = Calendar.getInstance();
				cal.setTime(requestInfo.getSummaryDate());
				TransactionSummary summary = bankService.getTransactionSummary(bankAccount.getId(), DateUtil.convertToXMLGregorianCalendar(cal));
				if(summary == null || summary.getHeader() == null){
					LOG.debug("Summary Not found");
					nextTxnStatus = TransactionStatus.FAILED;
					narrative =  ResponseCode.E507.getDescription();
					txn.setResponseCode(ResponseCode.E507.name());
				}else{
					narrative = summary.getDescription();
					nextTxnStatus = TransactionStatus.COMPLETED;
					txn.setResponseCode(ResponseCode.E000.name());
				}
				
				responseInfo = new ResponseInfo(narrative, ResponseCode.valueOf(txn.getResponseCode()), requestInfo, ResponseType.NOTIFICATION, 0L, 0L, 0L, txn.getMessageId());
				LOG.debug("Sending AGENT SUMMARY REPLY (to SWITCH) for transaction...." + txn.getMessageId());
				//if you get here then we must be ready to reply to the SWITCH / To forward to Bank
				boolean sent = processUtil.submitResponse(responseInfo);
				String stmt = " REPLY has been sent (to SWITCH)";
				
				if(sent){
					txn = processUtil.promoteTxnState(txn, nextTxnStatus, narrative);
					LOG.info(txn.getTransactionType() + stmt);
				}else{
					LOG.fatal("FAILED to submit " + txn.getTransactionType() + (sendToBank ? " REQUEST (to BANK) " : " REPLY (to SWITCH)"));
				}
			}
			
		} catch (Exception e) {
			LOG.fatal("FAILED to process " + requestInfo.getTransactionType() + " REQUEST : " + requestInfo.getMessageId() + " : " + e.getMessage(), e);
			e.printStackTrace(System.err);			
		}
	}
	
}
