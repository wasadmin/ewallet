package zw.co.esolutions.ewallet.process.mdb;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import zw.co.esolutions.ewallet.bankservices.service.EWalletPostingResponse;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.bankservices.service.TransactionCategory;
import zw.co.esolutions.ewallet.bankservices.service.TransactionPostingInfo;
import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.enums.DayEndStatus;
import zw.co.esolutions.ewallet.enums.ResponseType;
import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.msg.AlertInfo;
import zw.co.esolutions.ewallet.msg.BankRequest;
import zw.co.esolutions.ewallet.msg.BankResponse;
import zw.co.esolutions.ewallet.msg.RequestInfo;
import zw.co.esolutions.ewallet.msg.ResponseInfo;
import zw.co.esolutions.ewallet.process.AutoRegService;
import zw.co.esolutions.ewallet.process.ConfigurationsLocal;
import zw.co.esolutions.ewallet.process.DayEndBeanLocal;
import zw.co.esolutions.ewallet.process.ProcessUtil;
import zw.co.esolutions.ewallet.process.model.DayEnd;
import zw.co.esolutions.ewallet.process.model.ProcessTransaction;
import zw.co.esolutions.ewallet.process.model.TransactionCharge;
import zw.co.esolutions.ewallet.process.pojo.PostingsRequestWrapper;
import zw.co.esolutions.ewallet.sms.MessageSender;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.GenerateKey;
import zw.co.esolutions.ewallet.util.MoneyUtil;
import zw.co.esolutions.ewallet.util.SystemConstants;

/**
 * Message-Driven Bean implementation class for: HandleTransactionResponse
 * 
 */
@MessageDriven(activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") }, mappedName = EWalletConstants.FROM_BANKMED_TO_EWALLET_QUEUE)
public class HandleTransactionResponse implements MessageListener {
	
	@EJB
	private ProcessUtil processUtil;
	@EJB
	private DayEndBeanLocal dayEndBean;
	
	@EJB private AutoRegService autoRegService;
	
	@EJB
	private ConfigurationsLocal config;
	
	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(HandleTransactionResponse.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + HandleTransactionResponse.class);
		}
	}

	public HandleTransactionResponse() {
	
	}

	/**
	 * @see MessageListener#onMessage(Message)
	 */
	public void onMessage(Message message) {
		try {
			ObjectMessage msg = (ObjectMessage) message;
			BankResponse bankResp = (BankResponse) msg.getObject();
			
			if (bankResp != null) {
				this.handleTransactionResponse(bankResp);
			} else {
				LOG.error("BankRequest is NULL");
			}
		} catch (JMSException e) {
			LOG.fatal("JMS Exception Thrown : Message " + e.getMessage() + " : ", e);
		}
	}

	public void handleTransactionResponse(BankResponse bankResponse) {
		
		BankRequest request = bankResponse.getBankRequest();
		
		if (request == null) {
			LOG.error("BankRequest is NULL.. return");
			return;
		}
		
		//Handle the timeout response here :
		
		if (!TransactionType.ALERT.equals(request.getTransactionType())) {
			
			ProcessTransaction txn = processUtil.findProcessTransactionById(request.getReference());
			
			if(txn != null){
				if(! TransactionType.REVERSAL.equals(request.getTransactionType())){
					if(txn.isTimedOut() && TransactionStatus.TIMEOUT.equals(txn.getStatus())){
						this.handleBankResponseForTimedOutTransaction(txn, bankResponse);
						return ;
					}
				}
			}
			
		}
		
		
		if (zw.co.esolutions.ewallet.enums.TransactionType.BALANCE.equals(request.getTransactionType())) {
			LOG.debug("Processing BALANCE response from EQ3");
			this.handleBalanceResponse(bankResponse);
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER.equals(request.getTransactionType())) {
			LOG.debug("Processing BA to BA TXF response from EQ3");
			this.handleBankAccountToBankAccountTransferResponse(bankResponse);
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER.equals(request.getTransactionType())) {
			LOG.debug("Processing BA to EWT TXF response from EQ3");
			this.handleBankAccountToEWalletTransferResponse(bankResponse);
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER.equals(request.getTransactionType())) {
			LOG.debug("Processing BA to NHL TXF response from EQ3");
			this.handleBankAccountToNonHolderTransferResponse(bankResponse);
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.BILLPAY.equals(request.getTransactionType())) {
			LOG.debug("Processing BILLPAY response from EQ3");
			this.handleBillPayResponse(bankResponse);
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.DAYEND_CASH_TENDERED.equals(request.getTransactionType())) {
			LOG.debug("Processing DAYEND_CASH_TENDERED response from EQ3");
			this.handleDayEndCashTenderedResponse(bankResponse);
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.DAYEND_FLOATS.equals(request.getTransactionType())) {
			LOG.debug("Processing DAYEND_CASH_TENDERED response from EQ3");
			this.handleDayEndFloatsResponse(bankResponse);
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.DAYEND_OVERPOST.equals(request.getTransactionType())) {
			LOG.debug("Processing DAYEND_OVERPOST response from EQ3");
			this.handleDayEndOverPostResp(bankResponse);
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.DAYEND_UNDERPOST.equals(request.getTransactionType())) {
			LOG.debug("Processing DAYEND_UNDERPOST reponse from EQ3");
			this.handleDayEndUnderPostsResp(bankResponse);
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_BILLPAY.equals(request.getTransactionType())) {
			LOG.debug("Processing EWALLET BILLPAY reponse from EQ3");
			this.handleEwalletBillPayResponse(bankResponse);
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.DEPOSIT.equals(request.getTransactionType())) {
			LOG.debug("Processing EWALLET DEPOSIT reponse from EQ3");
			this.handleEWalletDepositTxnResp(bankResponse);
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.WITHDRAWAL.equals(request.getTransactionType())) {
			LOG.debug("Processing EWALLET WITHDRAWAL reponse from EQ3");
			this.handleEWalletHolderWithdrawalResp(bankResponse);
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.MINI_STATEMENT.equals(request.getTransactionType())) {
			LOG.debug("Processing MINI STATEMENT reponse from EQ3");
			this.handleEWalletMiniStatementResp(bankResponse);
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER.equals(request.getTransactionType())) {
			LOG.debug("Processing EWT TO BA TRANSFER reponse from EQ3");
			this.handleEWalletToBankAccountTransferResp(bankResponse);
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TO_EWALLET_TRANSFER.equals(request.getTransactionType())) {
			LOG.debug("Processing EWT TO EWT TRANSFER reponse from EQ3");
			this.handleEWalletToEWalletTransferResp(bankResponse);
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER.equals(request.getTransactionType())) {
			LOG.debug("Processing EWT TO NON HOLDER TRANSFER reponse from EQ3");
			this.handleEWalletToNonHolderTransferResp(bankResponse);
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TOPUP.equals(request.getTransactionType())) {
			LOG.debug("Processing EWT TOPUP reponse from EQ3");
			this.handleEwalletTopupResponse(bankResponse);
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.WITHDRAWAL_NONHOLDER.equals(request.getTransactionType())) {
			LOG.debug("Processing NON HOLDER WDL reponse from EQ3");
			this.handleNonHolderResponse(bankResponse);
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.REVERSAL.equals(request.getTransactionType())) {
			LOG.debug("Processing REVERSAL reponse from EQ3");
//			ProcessTransaction txn = processUtil.findProcessTransactionById(request.getReference());
//			if(TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER.equals(txn.getTransactionType())){
//				this.handleNonHolderTxfReversalResponse(bankResponse);
//			}else{
//				this.handleReversalBankResp(bankResponse);
//			}
			this.handleAllTxnReversal(bankResponse);
			
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.START_OF_DAY_FLOAT_IN.equals(request.getTransactionType())) {
			LOG.debug("Processing START OF DAY FLOAT IN reponse from EQ3");
			this.handleStartDayFloatInResponse(bankResponse);
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.TARIFF.equals(request.getTransactionType())) {
			LOG.debug("Processing TARIFF reponse from EQ3");
			this.handleTarrifResponse(bankResponse);
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.TOPUP.equals(request.getTransactionType())) {
			LOG.debug("Processing TARIFF reponse from EQ3");
			this.handleTopupResponse(bankResponse);
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.ALERT.equals(request.getTransactionType())) {
			LOG.debug("Processing ALERT reponse from EQ3");
			this.handleTransactionAlert(bankResponse);
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.ADJUSTMENT.equals(request.getTransactionType())) {
			LOG.debug("Processing ADJUSTMENT reponse from EQ3");
			this.handleAdjustmentResponse(bankResponse);
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.AGENT_CUSTOMER_DEPOSIT.equals(request.getTransactionType())) {
			LOG.debug("Processing AGENT_CUSTOMER_DEPOSIT reponse from EQ3");
			this.handleAgentCustomerDepositResponse(bankResponse);
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.AGENT_CUSTOMER_WITHDRAWAL.equals(request.getTransactionType())) {
			LOG.debug("Processing AGENT_CUSTOMER_WITHDRAWAL reponse from EQ3");
			this.handleAgentCustomerWithdrawalResponse(bankResponse);
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL.equals(request.getTransactionType())) {
			LOG.debug("Processing AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL reponse from EQ3");
			this.handleAgentCustomerNonHolderWithdrawalResponse(bankResponse);
		} else if(zw.co.esolutions.ewallet.enums.TransactionType.AGENT_CASH_DEPOSIT.equals(request.getTransactionType())){
			LOG.debug("Processing AGENT_CASH_DEPOSIT reponse from EQ3");
			this.handleEWalletAgentDepositTxnResp(bankResponse);
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.AGENT_EWALLET_TO_BANKACCOUNT_TRANSFER.equals(request.getTransactionType())) {
			LOG.debug("Processing AGENT EWT TO BA TRANSFER reponse from EQ3");
			this.handleAgentEWalletToBankAccountTransferResp(bankResponse);
		} else if(zw.co.esolutions.ewallet.enums.TransactionType.COMMISSION_SWEEPING.equals(request.getTransactionType())){
			LOG.debug("Processing AGENT COMMISSION SWEEP reponse from EQ3");
			this.handleAgentCommissionSweepResp(bankResponse);
		}  else if(zw.co.esolutions.ewallet.enums.TransactionType.ALERT_REG.equals(request.getTransactionType())){
			LOG.debug("Processing ALERT REG reponse from EQ3");
			this.handleAlertRegistrationResponse(bankResponse);
		} else if(zw.co.esolutions.ewallet.enums.TransactionType.ALERT_DEREG.equals(request.getTransactionType())){
			LOG.debug("Processing ALERT DEREG reponse from EQ3");
			this.handleAlertRegistrationResponse(bankResponse);
		}
	}

	private ProcessTransaction handleBankResponseForTimedOutTransaction(ProcessTransaction txn, BankResponse bankResponse) {
		try {
			LOG.debug("Processing TIMEOUT for TXN : [" + txn.getMessageId()  +" : " + txn.getTransactionType() +"]");
			txn = processUtil.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, bankResponse.getNarrative());
			if (ResponseCode.E000.equals(bankResponse.getResponseCode())) {
				LOG.debug("The 0210 was a success despite this txn being timed out");
				BankRequest bankRequest = processUtil.populateBankRequest(txn);
				LOG.debug("Submitting reversal for TXN : [" + txn.getMessageId()  +" : " + txn.getTransactionType() +"]");
				boolean sent = processUtil.submitRequest(bankRequest, true);
				if(sent){
					LOG.debug("Reversal Request has been sent");
					txn = processUtil.promoteTxnState(txn, TransactionStatus.REVERSAL_REQUEST, "Transaction timed out");
				}else{
					LOG.fatal("FAILED to submit LEGITIMATE Reversal : ");
					txn = processUtil.promoteTxnState(txn, TransactionStatus.MANUAL_RESOLVE, "Failed to submit reversal.");
				}
			}else{
				LOG.debug("The 0210 was a failure despite the txn being timed out");
				txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, "Transaction timed out");
			}
		} catch (Exception e) {
			LOG.fatal("Exception thrown while processing a 0210 for a TIMED out TXN", e);
			e.printStackTrace(System.err);
		}
		return txn;
		
	}

	private void handleTransactionAlert(BankResponse bankResp) {
		try {
			BankRequest request = bankResp.getBankRequest();
			LOG.debug(bankResp);
			LOG.debug("Handle TRANSACTION ALERT has consumed : TXN TYPE " + request.getTransactionType());
			// retrieve txn and update
			RequestInfo requestInfo = new RequestInfo();
			// set source mobile and destination mobile
			String accountNumber = request.getSourceAccountNumber();
			LOG.debug("In ewt process module ACC NUM is :::::::::" + accountNumber);
			String primaryMobile = null;
			if (accountNumber == null || "".equalsIgnoreCase(accountNumber)) {
				LOG.debug("ALERT IGNORED NO ACCOUNT NUMBER SET CANNOT RETRIEVE BANKACCOUNT");
				return;
			} else {
				BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
				BankAccount acc = bankService.getUniqueBankAccountByAccountNumber(accountNumber);
				if (acc != null && acc.getId() != null) {
					String customerId = acc.getAccountHolderId();
					CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
					List<MobileProfile> mobileProfiles = customerService.getMobileProfileByCustomer(customerId);
					for (MobileProfile mobile : mobileProfiles) {
						if (mobile.isPrimary()) {
							primaryMobile = mobile.getMobileNumber();
							break;
						}
					}
					if (primaryMobile == null) {
						LOG.debug("Could not get primary mobile for alert msg");
						return;
					}
				}
			}
			LOG.debug("Done processing the alert, now sending it to the bank.");
			request.setTransactionType(TransactionType.ALERT);
			requestInfo.setMessageId(bankResp.getBankReference());
			AlertInfo alertInfo = this.toAlert(bankResp);
			ResponseInfo responseInfo = new ResponseInfo(alertInfo.toString(), bankResp.getResponseCode(), requestInfo, ResponseType.NOTIFICATION, bankResp.getLedgerBalance(), bankResp.getAvailableBalance(), 0L, requestInfo.getMessageId());
			requestInfo.setSourceMobile(primaryMobile);
			requestInfo.setTargetMobile(primaryMobile);
			requestInfo.setTransactionType(TransactionType.ALERT);
			LOG.debug("Sending ALERT NOTIFICATION (to SWITCH) for transaction...." + bankResp.getBankReference());
			processUtil.submitResponse(responseInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public AlertInfo toAlert(BankResponse bankResponse) {
		try {
			AlertInfo alert = new AlertInfo();
			String[] data = bankResponse.getNarrative().replace("[nl]", "^").replace("[NL]", "^").split("\\^");
			int count = 0;
			for (String string : data) {
				LOG.debug(count++ + " :  " + string);
			}
			String ref = data[0].trim();
			if ("".equalsIgnoreCase(ref)) {
				ref = bankResponse.getBankRequest().getReference();
			}
			alert.setCustomerReference(ref);
			alert.setBankAccountNumber(bankResponse.getBankRequest().getSourceAccountNumber());
			alert.setAmount(bankResponse.getBankRequest().getAmount());
			alert.setAccountBalance(bankResponse.getAvailableBalance());
			alert.setTransactionSource(data[1].trim());
			alert.setTransactionType(data[2].trim());
			alert.setTransactionTypeDetails(data[3].trim());
			DateFormat df = new SimpleDateFormat("yyMMdd");
			Date tmp;
			try {
				tmp = df.parse(data[4].trim());
			} catch (Exception e) {
				tmp = new Date(System.currentTimeMillis());
			}
			df = new SimpleDateFormat("dd/MM/yyyy");
			alert.setTransactionDate(df.format(tmp));
			alert.setBankReference(bankResponse.getBankReference());
			LOG.debug(alert);
			return alert;
		} catch (IndexOutOfBoundsException e) {
			LOG.error("Additional data field seems wrong [5]: " + bankResponse.getNarrative());
			return null;
		} catch (Exception e) {
			LOG.error(e, e);
			return null;
		}
	}

	private void handleTopupResponse(BankResponse bankResp) {
		LOG.debug(">>>>>>>>>>>>>>>>>>>>HandleTopupResponse MDB Has consumed!!!!!!");
		ProcessTransaction txn = null;
		try {
			BankRequest request = bankResp.getBankRequest();
			// retrieve txn and update
			txn = processUtil.findProcessTransactionById(request.getReference());
			if(txn != null && TransactionStatus.FAILED.equals(txn.getStatus())) {
				// Edit Bank Response Here
				bankResp.setNarrative(ResponseCode.E505.getDescription());
				bankResp.setResponseCode(ResponseCode.E505);
				LOG.debug("Failed Transaction.......... IGNORE Ref = "+txn.getId());
				return;
				
			} else if(txn != null && TransactionStatus.REVERSAL_REQUEST.equals(txn.getStatus())) {
				LOG.debug(">>>>> Ingore this transaction. ID "+txn.getId()+" , Status = "+txn.getStatus());
				return;
			}
			if (txn != null) {
				LOG.debug("Found a matching transaction !!!!!!");
				txn = processUtil.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, bankResp.getNarrative());
				LOG.debug("Done updating txn to BANK RESPONSE");
				txn = processUtil.updateProcessTxnWithBankRespInfo(txn, bankResp);
				LOG.debug("Done updating the txn using bank response.");
				String narrative = null;
				ResponseInfo responseInfo = null;
				if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
					LOG.debug("Transaction was successful.");
					narrative = bankResp.getNarrative();
					txn = processUtil.promoteTxnState(txn, TransactionStatus.CREDIT_REQUEST, bankResp.getNarrative());
					LOG.debug("Done promoting the transaction to CREDIT_REQUEST status.");
					RequestInfo requestInfo = processUtil.populateRequestInfo(txn);
					LOG.debug("Done creating a request message.");
					responseInfo = new ResponseInfo(narrative, bankResp.getResponseCode(), requestInfo, ResponseType.TOPUP_REQUEST, bankResp.getLedgerBalance(), bankResp.getAvailableBalance(), 0L, txn.getMessageId());
					LOG.debug("Done creating the response in forquest message.");
				} else {
					LOG.debug("Transaction failed at the bank.");
					narrative = bankResp.getNarrative();
					LOG.debug("Done picking narrative.");
					txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, narrative);
					LOG.debug("Done promoting transaction to failed.");
					RequestInfo requestInfo = processUtil.populateRequestInfo(txn);
					LOG.debug("Done populating the request info.");
					responseInfo = new ResponseInfo(narrative, bankResp.getResponseCode(), requestInfo, ResponseType.NOTIFICATION, bankResp.getLedgerBalance(), bankResp.getAvailableBalance(), 0L, txn.getMessageId());
					LOG.debug("Done creating response info.");
				}
				GenerateKey.throwsBankResponseException();
				processUtil.submitResponse(responseInfo);
				LOG.debug("Done putting message into FROM_EWALLET_TO_SWITCH_QUEUE queue.");
			} else {
				LOG.debug("Could not find a matching transaction " + request.getReference());
			}
		} catch (Exception e) {
			LOG.error("Exception thrown : ", e);
			this.reversePostingsAndNotify(bankResp, txn, false, false, null);
		}
	}

	private void handleTarrifResponse(BankResponse bankResp) {
		try {
			BankRequest request = bankResp.getBankRequest();
			// retrieve Original Tariff Message
			LOG.debug("Trying to find the original transaction charge...");
			TransactionCharge txnCharge = processUtil.findtTransactionChargeById(request.getReference());
			// if txn is not null && status is expecting a bank response;
			if (txnCharge == null) {
				LOG.debug("Unknown transaction charge here, ignore it.");
			} else if (TransactionStatus.BANK_REQUEST.equals(txnCharge.getStatus())) {
				LOG.debug("Bank Response status for tariff : " + bankResp.getResponseCode());
				if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
					LOG.debug("Response a success in EQ3 ");
					txnCharge.setStatus(TransactionStatus.COMPLETED);
					txnCharge = processUtil.updateTransactionCharge(txnCharge);
					LOG.debug("Done updating txnCharge ");
				} else {
					LOG.debug("Response failed in EQ3 ");
					txnCharge.setStatus(TransactionStatus.MANUAL_RESOLVE);
					txnCharge = processUtil.updateTransactionCharge(txnCharge);
					LOG.debug("Done updating txnCharge ");
				}
				LOG.debug("Done processing trasaction charge");

			} else {
				// invalid state for bank response
				LOG.debug("Invalid transaction state for the bank response.. ignore i guess... chances of reprocessing");
			}
		} catch (Exception e) {
			LOG.debug("Exception Thrown : Message " + e.getMessage() + " : ", e);
		}
	}

	private void handleStartDayFloatInResponse(BankResponse bankResp) {
		LOG.debug("-----------------------------START OF DAY FLOAT_______-------------------");
		try {
			BankRequest request = bankResp.getBankRequest();
			LOG.debug("STARY FO DAY FLOAT Looking for original transaction with reference : " + request.getReference());
			ProcessTransaction txn = processUtil.getProcessTransactionByMessageId(request.getReference());
			
			if(txn != null && TransactionStatus.REVERSAL_REQUEST.equals(txn.getStatus())) {
				LOG.debug(">>>>> Ingore this transaction. ID "+txn.getId()+" , Status = "+txn.getStatus());
				return;
			}
			
			txn = processUtil.updateProcessTxnWithBankRespInfo(txn, bankResp);
			txn.setNarrative(bankResp.getNarrative());
			if (TransactionStatus.BANK_REQUEST.equals(txn.getStatus())) {
				LOG.debug("Processing START_OF_DAY_FLOAT_IN response SUCESS OR FAIL      " + bankResp.getResponseCode().name());
				if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
					txn = processUtil.updateProcessTransactionWith(txn.getId(), txn.getResponseCode(), txn.getValueDate(), txn.getNarrative());
					txn = processUtil.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, bankResp.getNarrative());
					LOG.debug("Done promoting to bank response.");
				} else {
					txn = processUtil.updateProcessTransactionWith(txn.getId(), txn.getResponseCode(), txn.getValueDate(), txn.getNarrative());
					txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResp.getNarrative());
					LOG.debug("START_OF_DAY_FLOAT_IN FAILED");
				}
			} else {
				LOG.debug("Response already committed : " + txn.getStatus());
			}
		} catch (Exception e) {
			LOG.debug("Exception Thrown : Message " + e.getMessage() + " : ", e);
		}
	}

	private void handleReversalBankResp(BankResponse bankResp) {
		LOG.debug("HandleReversalBankResp MDB Has consumed!!!!!!");
		try {
			BankRequest request = bankResp.getBankRequest();
			LOG.debug("REVERSAL RESPONSE FROM EQ3...");
			if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
				LOG.debug("In Handle Reversal Response >>>>>>>>>>>Reversal Was Successful... IGNORE");
				return;
			} else {
				LOG.debug("Reversal Was not successful. Code = " + bankResp.getResponseCode());
				ProcessTransaction txn = processUtil.getProcessTransactionByMessageId(request.getReference());
				if (txn != null) {
					LOG.debug("REVERSAL FAILED... MARK AS MANUAL_RESOLVE");
					// mark txn as MANUAL_RESOLVE
					processUtil.updateProcessTransactionWith(txn.getId(), bankResp.getResponseCode().name(), bankResp.getValueDate(), bankResp.getNarrative());
					txn = processUtil.promoteTxnState(txn, TransactionStatus.MANUAL_RESOLVE, "Reversal of txn failed in EQ3");
					LOG.debug("MARKED AS MANUAL_RESOLVE");
				} else {
					LOG.debug("Charge Reversal Failed. Reference = " + request.getReference());
				}
			}
		} catch (Exception e) {
			LOG.debug("Exception Thrown : Message " + e.getMessage() + " : ", e);
		}

	}

	private void handleNonHolderResponse(BankResponse bankResp) {
		LOG.debug("HandleNonHolderResponse Has consumed!!!!!!");
		try {
			BankRequest request = bankResp.getBankRequest();
			// retrieve txn and update
			ProcessTransaction txn = processUtil.getProcessTransactionByMessageId(request.getReference());
			if(txn != null && TransactionStatus.FAILED.equals(txn.getStatus())) {
				// Edit Bank Response Here
				bankResp.setNarrative(ResponseCode.E505.getDescription());
				bankResp.setResponseCode(ResponseCode.E505);
				LOG.debug("Failed Transaction.......... IGNORE Ref = "+txn.getId());
				return;
			} else if(txn != null && TransactionStatus.REVERSAL_REQUEST.equals(txn.getStatus())) {
				LOG.debug(">>>>> Ingore this transaction. ID "+txn.getId()+" , Status = "+txn.getStatus());
				return;
			}
			txn = processUtil.updateProcessTxnWithBankRespInfo(txn, bankResp);
			txn.setNarrative(bankResp.getNarrative());
			LOG.debug("Found OG TXN " + txn);
			if (TransactionStatus.FAILED.equals(txn.getStatus())) { // txn timed
				LOG.debug("NON HOLDER WITHDRAWAL TXN TIMED OUT... return");
				return;
			}
			if (TransactionStatus.BANK_REQUEST.equals(txn.getStatus())) {
				LOG.debug("Processing NHL WDL txn response ");
				if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
					txn = processUtil.updateProcessTransactionWith(txn.getId(), txn.getResponseCode(), txn.getValueDate(), txn.getNarrative());
					txn = processUtil.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, bankResp.getNarrative());
					LOG.debug("Done promoting to bank response.");
				} else {
					txn = processUtil.updateProcessTransactionWith(txn.getId(), txn.getResponseCode(), txn.getValueDate(), txn.getNarrative());
					txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResp.getNarrative());
					LOG.debug("WITHDRWAL FAILED");
				}
			} else {
				LOG.debug("Response already committed for transaction : " + request.getReference());
			}
		} catch (Exception e) {
			LOG.debug("Exception Thrown : Message " + e.getMessage() + " : ", e);
		}
	}

	private void handleEwalletTopupResponse(BankResponse bankResp) {
		LOG.debug("HandleTopupResponse MDB Has consumed!!!!!!");
		ProcessTransaction txn = null;
		try {
			BankRequest request = bankResp.getBankRequest();
			// retrieve txn and update
			txn = processUtil.findProcessTransactionById(request.getReference());
			if(txn != null && TransactionStatus.FAILED.equals(txn.getStatus())) {
				// Edit Bank Response Here
				bankResp.setNarrative(ResponseCode.E505.getDescription());
				bankResp.setResponseCode(ResponseCode.E505);
				LOG.debug("Failed Transaction.......... IGNORE Ref = "+txn.getId());
				return;
			} else if(txn != null && TransactionStatus.REVERSAL_REQUEST.equals(txn.getStatus())) {
				LOG.debug(">>>>> Ingore this transaction. ID "+txn.getId()+" , Status = "+txn.getStatus());
				return;
			}
			if (txn != null) {
				LOG.debug("Found a matching transaction !!!!!!");
				txn = processUtil.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, bankResp.getNarrative());
				LOG.debug("Done updating txn to BANK RESPONSE");
				txn = processUtil.updateProcessTxnWithBankRespInfo(txn, bankResp);
				LOG.debug("Done updating the txn using bank response.");
				String narrative = null;
				ResponseInfo responseInfo = null;
				if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
					LOG.debug("Transaction was successful, doing book entry postings.");
					LOG.debug("1.    Topup balance after problem>>>>>>>>>>>>>>>>>>>>>");
					ResponseCode responseCode = processUtil.postEWalletTopupRequest(txn);
					
					GenerateKey.throwsBankResponseException();
					
					if (ResponseCode.E000.name().equals(responseCode.name())) {
						LOG.debug("Done posting to book entry....." + responseCode);
						narrative = bankResp.getNarrative();

						txn = this.processUtil.promoteTxnState(txn, TransactionStatus.CREDIT_REQUEST, bankResp.getNarrative());
						LOG.debug("Done promoting the transaction to CREDIT_REQUEST status.");
						bankResp.setAvailableBalance(this.processUtil.getRunningBalance(request.getSourceMobileNumber(), BankAccountType.E_WALLET));
						RequestInfo requestInfo = processUtil.populateRequestInfo(txn);
						LOG.debug("Done creating a request message.");
						responseInfo = new ResponseInfo(narrative, bankResp.getResponseCode(), requestInfo, ResponseType.TOPUP_REQUEST, bankResp.getLedgerBalance(), bankResp.getAvailableBalance(), 0L, txn.getMessageId());
						LOG.debug("Done creating the response in forquest message.");
					} else {
						bankResp.setNarrative(ResponseCode.E505.getDescription());
						bankResp.setResponseCode(ResponseCode.valueOf(responseCode.name()));
						RequestInfo requestInfo = processUtil.populateRequestInfo(txn);
						LOG.debug("Done creating a request message for failed postings.");
						narrative = bankResp.getNarrative();
						responseInfo = new ResponseInfo(narrative, bankResp.getResponseCode(), requestInfo, ResponseType.NOTIFICATION, bankResp.getLedgerBalance(), bankResp.getLedgerBalance(), 0L, txn.getMessageId());
						LOG.debug("Done creating the response in forquest message for failed testing.");
					}

				} else {
					LOG.debug("Transaction failed at the bank.");
					narrative = bankResp.getNarrative();
					LOG.debug("Done picking narrative.");
					txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, narrative);
					LOG.debug("Done promoting transaction to failed.");
					RequestInfo requestInfo = processUtil.populateRequestInfo(txn);
					LOG.debug("Done populating the request info.");
					responseInfo = new ResponseInfo(narrative, bankResp.getResponseCode(), requestInfo, ResponseType.NOTIFICATION, bankResp.getLedgerBalance(), bankResp.getAvailableBalance(), 0L, txn.getMessageId());
					LOG.debug("Done creating response info.");
				}
				processUtil.submitResponse(responseInfo);
				LOG.debug("Done putting message into FROM_EWALLET_TO_SWITCH_QUEUE queue.");
			} else {
				LOG.debug("Could not find a matching transaction " + request.getReference());
			}
		} catch (Exception e) {
			LOG.debug("Exception Thrown : Message " + e.getMessage() + " : ", e);
			this.reversePostingsAndNotify(bankResp, txn, true, false, null);
		}

	}

	private void handleEWalletToNonHolderTransferResp(BankResponse bankResp) {
		LOG.debug(">>>>>>>>>>>>>>>>>>>>HandleEWalletToNonHolderTransferResp Has consumed!!!!!!");
		ProcessTransaction txn = null;
		try {
			String response;
			BankRequest request = bankResp.getBankRequest();
			LOG.debug("Process non holder response....");
			// retrieve txn and update
			txn = processUtil.getProcessTransactionByMessageId(request.getReference());
			LOG.debug("Found the txn : " + txn);
			
			if(txn != null && TransactionStatus.FAILED.equals(txn.getStatus())) {
				// Edit Bank Response Here
				bankResp.setNarrative(ResponseCode.E505.getDescription());
				bankResp.setResponseCode(ResponseCode.E505);
				LOG.debug("Transaction timed out.. Ignore response");
				return;
			} else if(txn != null && TransactionStatus.REVERSAL_REQUEST.equals(txn.getStatus())) {
				LOG.debug(">>>>> Ingore this transaction. ID "+txn.getId()+" , Status = "+txn.getStatus());
				return;
			}
			//process txn
			txn = processUtil.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, bankResp.getNarrative());
			LOG.debug("Done promote txn to bank response");
			txn = processUtil.updateProcessTxnWithBankRespInfo(txn, bankResp);
			LOG.debug("Done updating txn with bank respo info..." + txn.getResponseCode());
			
			boolean autoregPerformed = false;
			boolean postingsSuccessful = false;
			
			if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
				//TRY REGISTERING RECEIPIENT BY AUTO-REG
				ResponseCode autoRegResponse = ResponseCode.E839; 
					
				//	autoRegService.processNonHolderAutoRegistration(processUtil.populateRequestInfo(txn));
				
				if (ResponseCode.E000.equals(autoRegResponse) || ResponseCode.E840.equals(autoRegResponse)) {
					
					if (ResponseCode.E000.equals(autoRegResponse)) {
						//Account was already registered 
						autoregPerformed = true;
						
						//update txn with target info
						LOG.debug("Start Updating txn info with target mobile details");
						MobileProfile targetMobileProfile = processUtil.getMobileProfile(txn.getTargetMobile());
						txn.setTargetMobileId(targetMobileProfile.getId());
						txn.setToBankId(txn.getFromBankId());
						txn.setDestinationAccountNumber(txn.getTargetMobile());
						
						txn = processUtil.updateProcessTransaction(txn);
						
						LOG.debug("Finished Updating txn info with target mobile details");
						
					}
					
					//Target Account successfully registered, post as EWALLET TO EWALLET TXF
					ResponseCode postingResponse = processUtil.postEWalletToEWalletTransfer(txn);
					
					GenerateKey.throwsBankResponseException();
					
					if (ResponseCode.E000.equals(postingResponse)) {
						postingsSuccessful = true;
						txn = this.processUtil.promoteTxnState(txn, TransactionStatus.COMPLETED, bankResp.getNarrative());
					} else {
						bankResp.setResponseCode(postingResponse);
						bankResp.setNarrative(ResponseCode.E505.getDescription());
					}
					
				} else if (ResponseCode.E839.equals(autoRegResponse)) {
				
					//Target mobile is a non-Econet number, do normal postings
					ResponseCode postingResponse = processUtil.postEWalletToNonHolderTransfer(txn);
					
					GenerateKey.throwsBankResponseException();
					
					if (ResponseCode.E000.equals(postingResponse)) {
						postingsSuccessful = true;
						txn = this.processUtil.promoteTxnState(txn, TransactionStatus.AWAITING_COLLECTION, bankResp.getNarrative());
					} else {
						bankResp.setResponseCode(postingResponse);
						bankResp.setNarrative(ResponseCode.E505.getDescription());
					}
					
				} else {
					
					//An error has occured
					bankResp.setNarrative(ResponseCode.E505.getDescription());
					bankResp.setResponseCode(autoRegResponse);
					txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResp.getNarrative());
					
					//SEND REVERSALS TO EQ3
					processUtil.rollbackEQ3Postings(txn);
					LOG.debug("NONHOLDER TXF HAS FAILED... ");
					
				}
	
			} else {
				txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResp.getNarrative());
			}
			
			//update process txn
			txn = processUtil.updateProcessTransactionWith(txn.getId(), bankResp.getResponseCode().name(), txn.getValueDate(), bankResp.getNarrative());
			
			//set response params
			if (postingsSuccessful) {
				bankResp.setAvailableBalance(this.processUtil.getRunningBalance(request.getSourceMobileNumber(), BankAccountType.E_WALLET));
				response = "Transfer to mobile " + request.getTargetMobileNumber() + " of " + MoneyUtil.convertCentsToDollarsPattern(txn.getAmount()) + " was successful. The reference code is " + request.getReference() + ". Your new balance is " + MoneyUtil.convertCentsToDollarsPattern(bankResp.getAvailableBalance()) + ". ZB e-Wallet";	
			} else {
				bankResp.setResponseCode(ResponseCode.E836);
				response = bankResp.getNarrative();
				txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, response);

			}
			
			LOG.debug("Response to the client has the reponse code " + txn.getResponseCode());
			RequestInfo requestInfo = processUtil.populateRequestInfo(txn);

			LOG.debug("Done populating...." + requestInfo.getTransactionType());
			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
			BankAccount bankAccount = bankService.getUniqueBankAccountByAccountNumber(txn.getSourceAccountNumber());
			ResponseInfo responseInfo = new ResponseInfo(response, ResponseCode.valueOf(txn.getResponseCode()), requestInfo, ResponseType.NOTIFICATION, bankAccount.getRunningBalance(), bankAccount.getRunningBalance(), 0L, txn.getMessageId());

			if (autoregPerformed) {
				responseInfo.setAutoregPerformed(autoregPerformed);
				responseInfo.setAutoregPassword(processUtil.getMobileProfile(requestInfo.getTargetMobile()).getSecretCode());
			}
			
			LOG.debug("Response info : " + responseInfo);

			LOG.debug("Sending EWT TO NH TXF REPLY (to SWITCH) for transaction...." + txn.getMessageId());
			processUtil.submitResponse(responseInfo);
			LOG.debug("Done.........");
		} catch (Exception e) {
			LOG.debug("Exception Thrown : Message " + e.getMessage() + " : ", e);
			this.reversePostingsAndNotify(bankResp, txn, true, false, null);
		}

	}

	private void handleEWalletToEWalletTransferResp(BankResponse bankResp) {
		LOG.debug("HandleTransferToHolderResp Has consumed!!!!!!");
		ProcessTransaction txn = null;
		try {
			BankRequest request = bankResp.getBankRequest();
			// retrieve txn and update
			txn = this.processUtil.getProcessTransactionByMessageId(request.getReference());
			if(txn != null && TransactionStatus.FAILED.equals(txn.getStatus())) {
				// Edit Bank Response Here
				bankResp.setNarrative(ResponseCode.E505.getDescription());
				bankResp.setResponseCode(ResponseCode.E505);
				LOG.debug("Failed Transaction.......... IGNORE Ref = "+txn.getId());
				return;
			} else if(txn != null && TransactionStatus.REVERSAL_REQUEST.equals(txn.getStatus())) {
				LOG.debug(">>>>> Ingore this transaction. ID "+txn.getId()+" , Status = "+txn.getStatus());
				return;
			}
			txn = this.processUtil.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, bankResp.getNarrative());
			txn = processUtil.updateProcessTxnWithBankRespInfo(txn, bankResp);
			if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
				// post to e-wallet accounts
				ResponseCode responseCode = this.processUtil.postEWalletToEWalletTransfer(txn);
				GenerateKey.throwsBankResponseException();
				if (ResponseCode.E000.equals(responseCode)) {
					txn = this.processUtil.promoteTxnState(txn, TransactionStatus.COMPLETED, bankResp.getNarrative());
					bankResp.setAvailableBalance(this.processUtil.getRunningBalance(request.getSourceMobileNumber(), BankAccountType.E_WALLET));
				} else {
					bankResp.setNarrative(ResponseCode.E505.getDescription());
					bankResp.setResponseCode(responseCode);
					txn = this.processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResp.getNarrative());
				}

			} else {
				txn = this.processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResp.getNarrative());
				bankResp.setAvailableBalance(this.processUtil.getRunningBalance(request.getSourceMobileNumber(), BankAccountType.E_WALLET));
			}

			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
			CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();

			RequestInfo requestInfo = processUtil.populateRequestInfo(txn);
			BankAccount bankAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getCustomerId(), BankAccountType.E_WALLET, OwnerType.CUSTOMER, txn.getSourceMobile());
			MobileProfile targetMobile = customerService.findMobileProfileById(txn.getTargetMobileId());

			BankAccount target = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(targetMobile.getCustomer().getId(), BankAccountType.E_WALLET, OwnerType.CUSTOMER, targetMobile.getMobileNumber());
			ResponseInfo responseInfo = new ResponseInfo(bankResp.getNarrative(), bankResp.getResponseCode(), requestInfo, ResponseType.NOTIFICATION, bankAccount.getRunningBalance(), bankAccount.getRunningBalance(), target.getRunningBalance(), txn.getMessageId());
			LOG.debug("Sending EWALLET TO EWALLET REPLY (to SWITCH) for transaction...." + txn.getMessageId());
			processUtil.submitResponse(responseInfo);

		} catch (Exception e) {
			LOG.debug("Exception Thrown : Message " + e.getMessage() + " : ", e);
			this.reversePostingsAndNotify(bankResp, txn, true, false, null);
		}

	}

	private void handleEWalletToBankAccountTransferResp(BankResponse bankResp) {
		ProcessTransaction txn = null;
		try {
			BankRequest request = bankResp.getBankRequest();

			txn = this.processUtil.getProcessTransactionByMessageId(request.getReference());
			if(txn != null && TransactionStatus.FAILED.equals(txn.getStatus())) {
				// Edit Bank Response Here
				bankResp.setNarrative(ResponseCode.E505.getDescription());
				bankResp.setResponseCode(ResponseCode.E505);
				LOG.debug("Failed Transaction.......... IGNORE Ref = "+txn.getId());
				return;
			} else if(txn != null && TransactionStatus.REVERSAL_REQUEST.equals(txn.getStatus())) {
				LOG.debug(">>>>> Ingore this transaction. ID "+txn.getId()+" , Status = "+txn.getStatus());
				return;
			}
			txn = this.processUtil.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, bankResp.getNarrative());
			txn = processUtil.updateProcessTxnWithBankRespInfo(txn, bankResp);
			if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
				ResponseCode responseCode = this.processUtil.postEWalletAccountToBankAccountTransfer(txn);
				//GenerateKey.throwsBankResponseException();
				if (ResponseCode.E000.equals(responseCode)) {
					txn = this.processUtil.promoteTxnState(txn, TransactionStatus.COMPLETED, bankResp.getNarrative());
					bankResp.setAvailableBalance(this.processUtil.getRunningBalance(request.getSourceMobileNumber(), BankAccountType.E_WALLET));
				} else {
					bankResp.setNarrative(ResponseCode.E505.getDescription());
					bankResp.setResponseCode(responseCode);
				}
			} else {
				if(!TransactionStatus.FAILED.equals(txn.getStatus())) {
					txn = this.processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResp.getNarrative());
				}
			}
			
			RequestInfo requestInfo = processUtil.populateRequestInfo(txn);
			BankAccount bankAccount = new BankServiceSOAPProxy().getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getCustomerId(), BankAccountType.E_WALLET, OwnerType.CUSTOMER, txn.getSourceMobile());
			ResponseInfo responseInfo = new ResponseInfo(bankResp.getNarrative(), bankResp.getResponseCode(), requestInfo, ResponseType.NOTIFICATION, bankAccount.getRunningBalance(), bankAccount.getRunningBalance(), bankResp.getLedgerBalance(), txn.getMessageId());
			LOG.debug("Sending EWALLET TO BANK ACCOUNT REPLY (to SWITCH) for transaction...." + txn.getMessageId());
			processUtil.submitResponse(responseInfo);
		} catch (Exception e) {
			LOG.debug("Exception Thrown : Message " + e.getMessage() + " : ", e);
			this.reversePostingsAndNotify(bankResp, txn, true, false, null);
		}
	}

	private void handleEWalletMiniStatementResp(BankResponse bankResp) {
		LOG.debug("Handle MINI StatementResp Has consumed!!!!!!");
		try {
			BankRequest request = bankResp.getBankRequest();
			String response;
			// retrieve txn and update
			ProcessTransaction txn = processUtil.getProcessTransactionByMessageId(request.getReference());
			if(txn != null && TransactionStatus.FAILED.equals(txn.getStatus())) {
				// Edit Bank Response Here
				bankResp.setNarrative(ResponseCode.E505.getDescription());
				bankResp.setResponseCode(ResponseCode.E505);
				LOG.debug("Failed Transaction.......... IGNORE Ref = "+txn.getId());
				return;
			} 
			txn = processUtil.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, bankResp.getNarrative());
			txn = processUtil.updateProcessTxnWithBankRespInfo(txn, bankResp);
			
			if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
				txn = processUtil.promoteTxnState(txn, TransactionStatus.COMPLETED, bankResp.getNarrative());
				response = "ZB Account STMT [nl]" + bankResp.getAdditionalData() + ". Available Balance " + MoneyUtil.convertCentsToDollarsPatternNoCurrency(bankResp.getAvailableBalance());
			} else {
				txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResp.getNarrative());
				response = bankResp.getNarrative();
			}
			RequestInfo requestInfo = processUtil.populateRequestInfo(txn);
			ResponseInfo responseInfo = new ResponseInfo(response, bankResp.getResponseCode(), requestInfo, ResponseType.NOTIFICATION, bankResp.getAvailableBalance(), bankResp.getLedgerBalance(), 0L, txn.getMessageId());
			LOG.debug("Sending BANK ACCOUNT STMT REPLY (to SWITCH) for transaction...." + txn.getMessageId());
			processUtil.submitResponse(responseInfo);
		} catch (Exception e) {
			LOG.debug("Exception Thrown : Message " + e.getMessage() + " : ", e);
		}

	}

	private void handleEWalletHolderWithdrawalResp(BankResponse bankResp) {
		LOG.debug(">>>>>>>>>>>>>>>>>>>>HandleHolderWithdrwalResp Has consumed!!!!!!");
		try {
			BankRequest request = bankResp.getBankRequest();
			// retrieve txn and update
			ProcessTransaction txn = this.processUtil.getProcessTransactionByMessageId(request.getReference());
			
			if(txn != null && TransactionStatus.FAILED.equals(txn.getStatus())) {
				// Edit Bank Response Here
				bankResp.setNarrative(ResponseCode.E505.getDescription());
				bankResp.setResponseCode(ResponseCode.E505);
				LOG.debug("Failed Transaction.......... IGNORE Ref = "+txn.getId());
				return;
			} else if(txn != null && TransactionStatus.REVERSAL_REQUEST.equals(txn.getStatus())) {
				LOG.debug(">>>>> Ingore this transaction. ID "+txn.getId()+" , Status = "+txn.getStatus());
				return;
			}
			this.processUtil.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, bankResp.getNarrative());
			txn = processUtil.updateProcessTxnWithBankRespInfo(txn, bankResp);
			if (TransactionStatus.FAILED.equals(txn.getStatus())) { // txn timed
																	// out
				LOG.debug("&&&&&&&&     WITHDRAWAL TXN TIMED OUT... return");
				return;
			}
			if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
				txn = processUtil.updateProcessTransactionWith(txn.getId(), txn.getResponseCode(), txn.getValueDate(), txn.getNarrative());
				this.processUtil.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, bankResp.getNarrative());
			} else {
				LOG.debug("EWallet Withdrawal " + request.getReference() + " Failed with RC : " + bankResp.getResponseCode() + " AND narrative " + bankResp.getNarrative());
				txn = processUtil.updateProcessTransactionWith(txn.getId(), txn.getResponseCode(), txn.getValueDate(), txn.getNarrative());
				this.processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResp.getNarrative());
			}
		} catch (Exception e) {
			LOG.debug("Exception Thrown : Message " + e.getMessage() + " : ", e);
		}
	}

	private void handleEWalletDepositTxnResp(BankResponse bankResp) {
		ProcessTransaction txn = null;
		try {
			BankRequest bankRequest = bankResp.getBankRequest();
			LOG.debug("Getting the original txn.......");
			txn = processUtil.findProcessTransactionById(bankRequest.getReference());
			
			if(txn != null && TransactionStatus.FAILED.equals(txn.getStatus())) {
				// Edit Bank Response Here
				bankResp.setNarrative(ResponseCode.E505.getDescription());
				bankResp.setResponseCode(ResponseCode.E505);
				LOG.debug("Failed Transaction.......... IGNORE Ref = "+txn.getId());
				return ;
			} else if(txn != null && TransactionStatus.REVERSAL_REQUEST.equals(txn.getStatus())) {
				LOG.debug(">>>>> Ingore this transaction. ID "+txn.getId()+" , Status = "+txn.getStatus());
				return;
			}
			LOG.debug("Done finding OG Txn " + txn + " status " + txn.getStatus());
			if (!TransactionStatus.COMPLETED.equals(txn.getStatus()) && !TransactionStatus.MANUAL_RESOLVE.equals(txn.getStatus())) {
				// this txn is expecting a result from the bank, so we proceed
				LOG.debug("Processing ewallet Deposit response for " + txn.getMessageId() + " and response code " + bankResp.getResponseCode());
				// now check the response code
				txn.setResponseCode(bankResp.getResponseCode().name());
				txn.setNarrative(bankResp.getNarrative());

				if (TransactionStatus.FAILED.equals(txn.getStatus())) { // txn
																		// timed
																		// out
					LOG.debug("&&&&&&&&     DEPOSIT TXN TIMED OUT... return");
					return;
				}
				// set value dat
				if (bankResp.getValueDate() != null) {
					txn.setValueDate(bankResp.getValueDate());
				} else {
					txn.setValueDate(new Date(System.currentTimeMillis()));
				}
				if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
					// set status to completed
					txn = processUtil.updateProcessTransactionWith(txn.getId(), txn.getResponseCode(), txn.getValueDate(), txn.getNarrative());
					txn = processUtil.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, txn.getNarrative());
					LOG.debug(">>>>>>>> Finished updating Txn " + txn.getStatus().toString());
				} else {
					txn = processUtil.updateProcessTransactionWith(txn.getId(), txn.getResponseCode(), txn.getValueDate(), txn.getNarrative());
					txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, txn.getNarrative());
					LOG.debug(">>>>>>>> Finished updating FAILED Txn " + txn.getStatus().toString());
				}

			} else {
				// txn has already committed
				LOG.debug("Duplicate response for transaction " + txn.getMessageId());
			}

		} catch (Exception e) {
			LOG.debug("Exception Thrown : Message " + e.getMessage() + " : ", e);
			//No Need for deposit Txn
			//this.reversePostingsAndNotify(bankResp, txn);
		}

	}

	private void handleEwalletBillPayResponse(BankResponse bankResp) {
		LOG.debug("HandleEwalletBillPayResponse MDB Has consumed!!!!!!");
		ProcessTransaction txn = null;
		try {
			BankRequest request = bankResp.getBankRequest();
			// retrieve txn and update
			txn = processUtil.findProcessTransactionById(request.getReference());
			
			if(txn != null && TransactionStatus.FAILED.equals(txn.getStatus())) {
				// Edit Bank Response Here
				bankResp.setNarrative(ResponseCode.E505.getDescription());
				bankResp.setResponseCode(ResponseCode.E505);
				LOG.debug("Failed Transaction.......... IGNORE Ref = "+txn.getId());
				return;
			} else if(txn != null && TransactionStatus.REVERSAL_REQUEST.equals(txn.getStatus())) {
				LOG.debug(">>>>> Ingore this transaction. ID "+txn.getId()+" , Status = "+txn.getStatus());
				return;
			}
			if (txn != null) {
				LOG.debug("Found a matching transaction !!!!!!");
				txn = processUtil.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, bankResp.getNarrative());
				LOG.debug("Done updating txn to BANK RESPONSE");
				txn = processUtil.updateProcessTxnWithBankRespInfo(txn, bankResp);
				LOG.debug("Done updating the txn using bank response.");
				ResponseInfo responseInfo;
				if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
					LOG.debug("Transaction was successful, doing book entry postings.");
					ResponseCode responseCode = processUtil.postEwalletBillPayRequest(txn);
					LOG.debug("Done posting to book entry....." + responseCode);
					GenerateKey.throwsBankResponseException();
					if (ResponseCode.E000.name().equals(responseCode.name())) {
						txn = this.processUtil.promoteTxnState(txn, TransactionStatus.CREDIT_REQUEST, bankResp.getNarrative());
						bankResp.setAvailableBalance(this.processUtil.getRunningBalance(request.getSourceMobileNumber(), BankAccountType.E_WALLET));
						LOG.debug("Done promoting the transaction to CREDIT_REQUEST status.");
						RequestInfo requestInfo = processUtil.populateRequestInfo(txn);
						LOG.debug("Done creating a request message.");
						responseInfo = new ResponseInfo(bankResp.getNarrative(), bankResp.getResponseCode(), requestInfo, ResponseType.BILLPAY_REQUEST, txn.getBalance(), txn.getBalance(), 0L, txn.getMessageId());
						LOG.debug("Done creating the response in forquest message.");
					} else {
						LOG.debug("E-Wallet Postings were not successful.");
						RequestInfo requestInfo = processUtil.populateRequestInfo(txn);
						bankResp.setNarrative(ResponseCode.E505.getDescription());
						bankResp.setResponseCode(ResponseCode.valueOf(responseCode.name()));
						responseInfo = new ResponseInfo(bankResp.getNarrative(), bankResp.getResponseCode(), requestInfo, ResponseType.NOTIFICATION, bankResp.getLedgerBalance(), bankResp.getAvailableBalance(), 0L, txn.getMessageId());
					}

				} else {
					LOG.debug("Transaction failed at the bank.");
					LOG.debug("Done picking narrative.");
					txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResp.getNarrative());
					LOG.debug("Done promoting transaction to failed.");
					RequestInfo requestInfo = processUtil.populateRequestInfo(txn);
					LOG.debug("Done populating the request info.");
					responseInfo = new ResponseInfo(bankResp.getNarrative(), bankResp.getResponseCode(), requestInfo, ResponseType.NOTIFICATION, bankResp.getLedgerBalance(), bankResp.getAvailableBalance(), 0L, txn.getMessageId());
					LOG.debug("Done creating response info.");
				}
				processUtil.submitResponse(responseInfo);
				LOG.debug("Done putting message into FROM_EWALLET_TO_SWITCH_QUEUE queue.");
			} else {
				LOG.debug("Could not find a matching transaction " + request.getReference());
			}
		} catch (JMSException e) {
			LOG.debug("JMS Exception Thrown : Message " + e.getMessage() + " : ", e);
		} catch (Exception e) {
			LOG.debug("Exception Thrown : Message " + e.getMessage() + " : ", e);
			this.reversePostingsAndNotify(bankResp, txn, true, false, null);
		}

	}

	private void handleDayEndUnderPostsResp(BankResponse bankResp) {

		/*
		 * check if processtxn is in status SENT TO HOST (BANK_REQUEST) if not
		 * set error cod on ProcessTrxn update ProcessTxn as successful and set
		 * response code and status and where other trxn are successful and
		 * DayEnd .
		 */

		try {
			BankRequest request = bankResp.getBankRequest();

			// retrieve txn and update
			ProcessTransaction txn = processUtil.getProcessTransactionByMessageId(request.getReference());
			
			if(txn != null && TransactionStatus.REVERSAL_REQUEST.equals(txn.getStatus())) {
				LOG.debug(">>>>> Ingore this transaction. ID "+txn.getId()+" , Status = "+txn.getStatus());
				return;
			}
			txn = processUtil.updateProcessTxnWithBankRespInfo(txn, bankResp);
			txn.setNarrative(bankResp.getNarrative());

			if (txn != null) {
				if (TransactionStatus.BANK_REQUEST.equals(txn.getStatus())) {
					if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
						LOG.debug("Posting for UNDERPOST >>>>>>>SUCCESS>>>>>>>>>" + txn.getNarrative());
						txn = processUtil.updateProcessTransactionWith(txn.getId(), txn.getResponseCode(), txn.getValueDate(), txn.getNarrative());
						txn = processUtil.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, bankResp.getNarrative());

					} else {
						LOG.debug(">>>>>>>>>>>>>>>>>>>>FAILURE UNDERPOST>>>>>>>>>>>>>>>txn narrative>>>>>>" + txn.getNarrative());
						txn = processUtil.updateProcessTransactionWith(txn.getId(), txn.getResponseCode(), txn.getValueDate(), txn.getNarrative());
						txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResp.getNarrative());
					}
				}
			}
		} catch (Exception e) {
			LOG.debug("Exception Thrown : Message " + e.getMessage() + " : ", e);
		}

	}

	private void handleDayEndOverPostResp(BankResponse bankResp) {
		try {
			BankRequest request = bankResp.getBankRequest();

			// retrieve txn and update
			ProcessTransaction txn = processUtil.getProcessTransactionByMessageId(request.getReference());
			
			if(txn != null && TransactionStatus.REVERSAL_REQUEST.equals(txn.getStatus())) {
				LOG.debug(">>>>> Ingore this transaction. ID "+txn.getId()+" , Status = "+txn.getStatus());
				return;
			}
			txn = processUtil.updateProcessTxnWithBankRespInfo(txn, bankResp);
			txn.setNarrative(bankResp.getNarrative());

			if (txn != null) {
				if (TransactionStatus.BANK_REQUEST.equals(txn.getStatus())) {
					if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
						LOG.debug("Posting for cash tendered >>>>>>>SUCCESS>>>>>>>>>" + txn.getNarrative());
						txn = processUtil.updateProcessTransactionWith(txn.getId(), txn.getResponseCode(), txn.getValueDate(), txn.getNarrative());
						txn = processUtil.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, bankResp.getNarrative());
					} else {
						LOG.debug(">>>>>>>>>>>>>>>>>>>>FAILURE OVERPOST>>>>>>>>>>>>>>>txn narrative>>>>>>" + txn.getNarrative());
						txn = processUtil.updateProcessTransactionWith(txn.getId(), txn.getResponseCode(), txn.getValueDate(), txn.getNarrative());
						txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResp.getNarrative());
					}
				}
			}
		} catch (Exception e) {
			LOG.debug("Exception Thrown : Message " + e.getMessage() + " : ", e);
		}
	}

	private void handleDayEndFloatsResponse(BankResponse bankResp) {

		/*
		 * check if processtxn is in status SENT TO HOST (BANK_REQUEST) if not
		 * set error cod on ProcessTrxn update ProcessTxn as successful and set
		 * response code and status and where other trxn are successful and
		 * DayEnd .
		 */
		try {

			BankRequest request = bankResp.getBankRequest();
			// retrieve txn and update

			ProcessTransaction txn = processUtil.getProcessTransactionByMessageId(request.getReference());
			
			if(txn != null && TransactionStatus.REVERSAL_REQUEST.equals(txn.getStatus())) {
				LOG.debug(">>>>> Ingore this transaction. ID "+txn.getId()+" , Status = "+txn.getStatus());
				return;
			}
			String dayEndId = txn.getDayEndId();
			LOG.debug("Floats dayend id    " + dayEndId);
			LOG.debug("floats mdb                  txn status    " + txn.getStatus());
			DayEnd dayEnd = dayEndBean.findDayEndById(dayEndId, null);
			if (TransactionStatus.BANK_REQUEST.equals(txn.getStatus())) {
				txn = processUtil.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, bankResp.getNarrative());
				txn = processUtil.updateProcessTxnWithBankRespInfo(txn, bankResp);
				if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
					txn.setStatus(TransactionStatus.COMPLETED);
					txn = processUtil.updateProcessTransaction(txn);
					txn = processUtil.promoteTxnState(txn, TransactionStatus.COMPLETED, bankResp.getNarrative());
				} else {
					txn.setStatus(TransactionStatus.FAILED);
					txn = processUtil.updateProcessTransaction(txn);
					txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResp.getNarrative());
					dayEnd.setStatus(DayEndStatus.MANUALLY_RESOLVE);
				}
				if (DayEndStatus.APPROVED.equals(dayEnd.getStatus()) && processUtil.checkIfProcessTransactionsSuccessful(dayEnd)) {
					dayEnd.setStatus(DayEndStatus.COMPLETED);
				}
				dayEndBean.editDayEnd(dayEnd, EWalletConstants.SYSTEM);
			}
		} catch (Exception e) {
			LOG.debug("Exception Thrown : Message " + e.getMessage() + " : ", e);
		}
	}

	private void handleDayEndCashTenderedResponse(BankResponse bankResp) {

		/*
		 * check if processtxn is in status SENT TO HOST (BANK_REQUEST) if not
		 * set error cod on ProcessTrxn update ProcessTxn as successful and set
		 * response code and status and where other trxn are successful and
		 * DayEnd .
		 */

		try {
			BankRequest request = bankResp.getBankRequest();

			ProcessTransaction txn = processUtil.getProcessTransactionByMessageId(request.getReference());
			if(txn != null && TransactionStatus.REVERSAL_REQUEST.equals(txn.getStatus())) {
				LOG.debug(">>>>> Ingore this transaction. ID "+txn.getId()+" , Status = "+txn.getStatus());
				return;
			}
			txn = processUtil.updateProcessTxnWithBankRespInfo(txn, bankResp);
			txn.setNarrative(bankResp.getNarrative());
			if (txn != null) {
				if (TransactionStatus.BANK_REQUEST.equals(txn.getStatus())) {
					if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
						LOG.debug("Posting for cash tendered >>>>>>>SUCCESS>>>>>>>>>" + txn.getNarrative());
						txn = processUtil.updateProcessTransactionWith(txn.getId(), txn.getResponseCode(), txn.getValueDate(), txn.getNarrative());
						txn = processUtil.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, bankResp.getNarrative());
					} else {
						LOG.debug(">>>>>>>>>>>>>>>>>>>>FAILURE CASH TENDERED>>>>>>>>>>>>>>>txn narrative>>>>>>" + txn.getNarrative());
						txn = processUtil.updateProcessTransactionWith(txn.getId(), txn.getResponseCode(), txn.getValueDate(), txn.getNarrative());
						txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResp.getNarrative());
					}
				}
			}

		} catch (Exception e) {
			LOG.debug("Exception Thrown : Message " + e.getMessage() + " : ", e);
		}

	}

	private void handleBillPayResponse(BankResponse bankResp) {
		LOG.debug("HandleBillPayResponse MDB Has consumed!!!!!!");
		ProcessTransaction txn = null;
		try {
			BankRequest request = bankResp.getBankRequest();
			// retrieve txn and update
			txn = processUtil.findProcessTransactionById(request.getReference());
			
			if(txn != null && TransactionStatus.FAILED.equals(txn.getStatus())) {
				// Edit Bank Response Here
				bankResp.setNarrative(ResponseCode.E505.getDescription());
				bankResp.setResponseCode(ResponseCode.E505);
				LOG.debug("Failed Transaction.......... IGNORE Ref = "+txn.getId());
				return;
			} else if(txn != null && TransactionStatus.REVERSAL_REQUEST.equals(txn.getStatus())) {
				LOG.debug(">>>>> Ingore this transaction. ID "+txn.getId()+" , Status = "+txn.getStatus());
				return;
			}
			if (txn != null) {
				LOG.debug("Found a matching transaction !!!!!!");
				txn = processUtil.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, bankResp.getNarrative());
				LOG.debug("Done updating txn to BANK RESPONSE");
				txn = processUtil.updateProcessTxnWithBankRespInfo(txn, bankResp);
				LOG.debug("Done updating the txn using bank response.");
				ResponseInfo responseInfo;
				if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
					LOG.debug("Transaction was successful.");
					txn = processUtil.promoteTxnState(txn, TransactionStatus.CREDIT_REQUEST, bankResp.getNarrative());
					LOG.debug("Done promoting the transaction to CREDIT_REQUEST status.");
					RequestInfo requestInfo = processUtil.populateRequestInfo(txn);
					LOG.debug("Done creating a request message.");
					responseInfo = new ResponseInfo(bankResp.getNarrative(), bankResp.getResponseCode(), requestInfo, ResponseType.BILLPAY_REQUEST, bankResp.getLedgerBalance(), bankResp.getAvailableBalance(), 0L, txn.getMessageId());
					LOG.debug("Done creating the response in forquest message.");
				} else {
					LOG.debug("Transaction failed at the bank.");
					txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResp.getNarrative());
					LOG.debug("Done promoting transaction to failed.");
					RequestInfo requestInfo = processUtil.populateRequestInfo(txn);
					LOG.debug("Done populating the request info.");
					responseInfo = new ResponseInfo(bankResp.getNarrative(), bankResp.getResponseCode(), requestInfo, ResponseType.NOTIFICATION, bankResp.getLedgerBalance(), bankResp.getAvailableBalance(), 0L, txn.getMessageId());
					LOG.debug("Done creating response info.");
				}
				GenerateKey.throwsBankResponseException();
				
				processUtil.submitResponse(responseInfo);
				LOG.debug("Done putting message into FROM_EWALLET_TO_SWITCH_QUEUE queue.");
			} else {
				LOG.debug("Could not find a matching transaction " + request.getReference());
			}
			
		} catch (Exception e) {
			LOG.debug("Exception Thrown : Message " + e.getMessage() + " : ", e);
			this.reversePostingsAndNotify(bankResp, txn, false, false, null);
		}
	}

	private void handleBankAccountToEWalletTransferResponse(BankResponse bankResp) {
		LOG.debug("Handle BankAccountToEWalletTransferResp Has consumed....");
		ProcessTransaction txn = null;
		try {
			BankRequest request = bankResp.getBankRequest();
			txn = processUtil.getProcessTransactionByMessageId(request.getReference());
			
			if(txn != null && TransactionStatus.FAILED.equals(txn.getStatus())) {
				// Edit Bank Response Here
				bankResp.setNarrative(ResponseCode.E505.getDescription());
				bankResp.setResponseCode(ResponseCode.E505);
				LOG.debug("Failed Transaction.......... IGNORE Ref = "+txn.getId());
				return;
			} else if(txn != null && TransactionStatus.REVERSAL_REQUEST.equals(txn.getStatus())) {
				LOG.debug(">>>>> Ingore this transaction. ID "+txn.getId()+" , Status = "+txn.getStatus());
				return;
			}
			LOG.debug("Found original txn ..." + txn);
			txn = processUtil.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, bankResp.getNarrative());
			LOG.debug("Done promoting to status " + txn.getStatus());
			txn = processUtil.updateProcessTxnWithBankRespInfo(txn, bankResp);
			LOG.debug("Done updating txn with bank response...");
			LOG.debug("Bank response code is " + bankResp.getResponseCode() + " txn response code " + txn.getResponseCode());
			if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
				// post to ewallet account
				LOG.debug("TXN from bank was successful, posting to book entry.");
				// ResponseCode responseCode =
				// processUtil.postBankAccountToNonHolderTransfers(txn);
				ResponseCode responseCode = processUtil.postBankAccountToEWalletTransfer(txn);
				if (ResponseCode.E000.name().equals(responseCode.toString())) {
					txn = this.processUtil.promoteTxnState(txn, TransactionStatus.COMPLETED, bankResp.getNarrative());

				} else {
					bankResp.setNarrative(ResponseCode.E505.getDescription());
					bankResp.setResponseCode(ResponseCode.valueOf(responseCode.toString()));
					txn = processUtil.updateProcessTransactionWith(txn.getId(), bankResp.getResponseCode().name(), bankResp.getValueDate(), bankResp.getNarrative());
					txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResp.getNarrative());
				}
				LOG.debug("Done book entry postings... promoting to completed");

			} else {
				LOG.debug("TXN from bank was failed.. promoting to failed.");
				txn.setStatus(TransactionStatus.FAILED);
				txn.setNarrative(bankResp.getNarrative());
				txn = processUtil.updateProcessTransaction(txn);
				txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResp.getNarrative());
				LOG.debug("Done promoting to failed... ");
			}
			GenerateKey.throwsBankResponseException();
			LOG.debug("Notifying customer on txn state " + txn.getStatus());
			RequestInfo requestInfo = processUtil.populateRequestInfo(txn);
			ResponseInfo responseInfo = new ResponseInfo(bankResp.getNarrative(), bankResp.getResponseCode(), requestInfo, ResponseType.NOTIFICATION, bankResp.getLedgerBalance(), bankResp.getAvailableBalance(), 0L, txn.getMessageId());
			LOG.debug("Sending BANK ACCOUNT TO EWALLET REPLY (to SWITCH) for transaction...." + txn.getMessageId());
			processUtil.submitResponse(responseInfo);

		} catch (Exception e) {
			LOG.debug("Exception Thrown : Message " + e.getMessage() + " : ", e);
			this.reversePostingsAndNotify(bankResp, txn, true, false, null);
		}
	}

	private void handleBankAccountToNonHolderTransferResponse(BankResponse bankResp) {
		LOG.debug("HandleBankAccountToNonHolderTransferResp Has consumed........");
		ProcessTransaction txn = null;
		try {
			BankRequest request = bankResp.getBankRequest();
			// retrieve txn and update
			txn = processUtil.getProcessTransactionByMessageId(request.getReference());
			
			if(txn != null && TransactionStatus.FAILED.equals(txn.getStatus())) {
				// Edit Bank Response Here
				bankResp.setNarrative(ResponseCode.E505.getDescription());
				bankResp.setResponseCode(ResponseCode.E505);
				LOG.debug("Failed Transaction.......... IGNORE Ref = "+txn.getId());
				return;
			} else if(txn != null && TransactionStatus.REVERSAL_REQUEST.equals(txn.getStatus())) {
				LOG.debug(">>>>> Ingore this transaction. ID "+txn.getId()+" , Status = "+txn.getStatus());
				return;
			}
			
			//process txn
			txn = processUtil.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, bankResp.getNarrative());
			txn = processUtil.updateProcessTxnWithBankRespInfo(txn, bankResp);

			boolean autoregPerformed = false;
			boolean postingsSuccessful = false;

			if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
				
				//TRY REGISTERING RECEIPIENT BY AUTO-REG
				ResponseCode autoRegResponse = ResponseCode.E839;
					
				//	autoRegService.processNonHolderAutoRegistration(processUtil.populateRequestInfo(txn));
				
				if (ResponseCode.E000.equals(autoRegResponse) || ResponseCode.E840.equals(autoRegResponse)) {
					
					if (ResponseCode.E000.equals(autoRegResponse)) {
						//Account was auto-registered 
						autoregPerformed = true;
						
						//update txn with target info
						LOG.debug("Start Updating txn info with target mobile details");
						MobileProfile targetMobileProfile = processUtil.getMobileProfile(txn.getTargetMobile());
						txn.setTargetMobileId(targetMobileProfile.getId());
						txn.setToBankId(txn.getFromBankId());
						txn.setDestinationAccountNumber(txn.getTargetMobile());
						
						txn = processUtil.updateProcessTransaction(txn);
						
						LOG.debug("Finished Updating txn info with target mobile details");
						
					}
					
					//Target Account successfully registered, post as BANKACC TO EWALLET TXF
					ResponseCode postingResponse = processUtil.postBankAccountToEWalletTransfer(txn);
					if (ResponseCode.E000.equals(postingResponse)) {
						LOG.debug("---- Postings successful");
						postingsSuccessful = true;
						txn = this.processUtil.promoteTxnState(txn, TransactionStatus.COMPLETED, bankResp.getNarrative());
					} else {
						LOG.debug("---- Postings failed");
						bankResp.setResponseCode(postingResponse);
						bankResp.setNarrative(ResponseCode.E505.getDescription());
						txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResp.getNarrative());
						LOG.debug("---- Txn marked as : " + txn.getStatus());

					}
				} else if (ResponseCode.E839.equals(autoRegResponse)) {
					//Target mobile is a non-Econet number, do normal postings
					ResponseCode postingResponse = processUtil.postBankAccountToNonHolderTransfer(txn);
					if (ResponseCode.E000.equals(postingResponse)) {
						postingsSuccessful = true;
						txn = this.processUtil.promoteTxnState(txn, TransactionStatus.AWAITING_COLLECTION, bankResp.getNarrative());
					} else {
						bankResp.setResponseCode(postingResponse);
						bankResp.setNarrative(ResponseCode.E505.getDescription());
						txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResp.getNarrative());
					}
				} else {
					bankResp.setNarrative(ResponseCode.E505.getDescription());
					bankResp.setResponseCode(autoRegResponse);
					txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResp.getNarrative());
					
					//SEND REVERSALS TO EQ3
					processUtil.rollbackEQ3Postings(txn);
					LOG.debug("NONHOLDER TXF HAS FAILED... ");
				}

			} else {
				bankResp.setNarrative(bankResp.getNarrative());
				txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResp.getNarrative());
			}
			
			//update process txn
			txn = processUtil.updateProcessTransactionWith(txn.getId(), bankResp.getResponseCode().name(), txn.getValueDate(), bankResp.getNarrative());
			
			if (postingsSuccessful) {
				LOG.debug("Postings successful");
			} else {
				bankResp.setResponseCode(ResponseCode.E836);
				bankResp.setNarrative(bankResp.getNarrative());
				txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResp.getNarrative());
			}
			// send mobile response
			RequestInfo requestInfo = processUtil.populateRequestInfo(txn);
			ResponseInfo responseInfo = new ResponseInfo(bankResp.getNarrative(), bankResp.getResponseCode(), requestInfo, ResponseType.NOTIFICATION, bankResp.getLedgerBalance(), bankResp.getAvailableBalance(), 0L, txn.getMessageId());
			
			if (autoregPerformed) {
				responseInfo.setAutoregPerformed(autoregPerformed);
				responseInfo.setAutoregPassword(processUtil.getMobileProfile(requestInfo.getTargetMobile()).getSecretCode());
			}
			
			GenerateKey.throwsBankResponseException();
			
			LOG.debug("Sending BANK ACCOUNT TO NON HOLDER REPLY (to SWITCH) for transaction...." + txn.getMessageId());
			processUtil.submitResponse(responseInfo);
		} catch (JMSException e) {
			LOG.debug("JMS Exception Thrown : Message " + e.getMessage() + " : ", e);
		} catch (Exception e) {
			LOG.debug("Exception Thrown : Message " + e.getMessage() + " : ", e);
			this.reversePostingsAndNotify(bankResp, txn, true, false, null);
		}

	}

	private void handleBankAccountToBankAccountTransferResponse(BankResponse bankResponse) {
		ProcessTransaction txn = null;
		try {
			BankRequest request = bankResponse.getBankRequest();
			// retrieve txn and update
			txn = processUtil.getProcessTransactionByMessageId(request.getReference());
			
			if(txn != null && TransactionStatus.FAILED.equals(txn.getStatus())) {
				// Edit Bank Response Here
				bankResponse.setNarrative(ResponseCode.E505.getDescription());
				bankResponse.setResponseCode(ResponseCode.E505);
				LOG.debug("Failed Transaction.......... IGNORE Ref = "+txn.getId());
				return;
			} else if(txn != null && TransactionStatus.REVERSAL_REQUEST.equals(txn.getStatus())) {
				LOG.debug(">>>>> Ingore this transaction. ID "+txn.getId()+" , Status = "+txn.getStatus());
				return;
			}
			txn = processUtil.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, bankResponse.getNarrative());
			txn = processUtil.updateProcessTxnWithBankRespInfo(txn, bankResponse);

			if (ResponseCode.E000.equals(bankResponse.getResponseCode())) {
				bankResponse.setNarrative("Transfer transaction successful.");
				txn = processUtil.promoteTxnState(txn, TransactionStatus.COMPLETED, bankResponse.getNarrative());
			} else {
				txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResponse.getNarrative());
			}
			GenerateKey.throwsBankResponseException();
			RequestInfo requestInfo = processUtil.populateRequestInfo(txn);
			ResponseInfo responseInfo = new ResponseInfo(bankResponse.getNarrative(), bankResponse.getResponseCode(), requestInfo, ResponseType.NOTIFICATION, bankResponse.getLedgerBalance(), bankResponse.getAvailableBalance(), 0L, txn.getMessageId());
			LOG.debug("Sending REPLY (to SWITCH) for transaction...." + txn.getMessageId());
			processUtil.submitResponse(responseInfo);
		} catch (Exception e) {
			LOG.error("Exception Thrown : Message " + e.getMessage() + " : ", e);
			this.reversePostingsAndNotify(bankResponse, txn, false, false, null);
		}
	}

	private void handleBalanceResponse(BankResponse bankResponse) {
		ProcessTransaction txn = null;
		try {
			BankRequest request = bankResponse.getBankRequest();
			// retrieve txn and update
			txn = processUtil.getProcessTransactionByMessageId(request.getReference());
			// if txn is not null && status is expecting a bank response;
			if (txn == null) {
				LOG.debug("Unknown transaction here, ignore it.");
			} else if (TransactionStatus.BANK_REQUEST.equals(txn.getStatus())) {
				processUtil.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, bankResponse.getNarrative());
				txn = processUtil.updateProcessTxnWithBankRespInfo(txn, bankResponse);
				if (ResponseCode.E000.equals(bankResponse.getResponseCode())) {
					txn.setStatus(TransactionStatus.COMPLETED);
					txn = processUtil.updateProcessTransaction(txn);
					txn = processUtil.promoteTxnState(txn, TransactionStatus.COMPLETED, bankResponse.getNarrative());
				} else {
					txn.setStatus(TransactionStatus.FAILED);
					txn = processUtil.updateProcessTransaction(txn);
					txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResponse.getNarrative());
				}
				//GenerateKey.throwsBankResponseException();
				RequestInfo requestInfo = processUtil.populateRequestInfo(txn);
				ResponseInfo responseInfo = new ResponseInfo(bankResponse.getNarrative(), bankResponse.getResponseCode(), requestInfo, ResponseType.NOTIFICATION, bankResponse.getLedgerBalance(), bankResponse.getAvailableBalance(), 0L, txn.getMessageId());
				LOG.debug("Sending BANK ACCOUNT BALANCE REPLY (to SWITCH) for transaction...." + txn.getMessageId());
				processUtil.submitResponse(responseInfo);
			} else {
				// invalid state for bank response
				LOG.warn("Invalid transaction state for the bank response : REF : " + request.getReference() + " : STATUS  : " + txn.getStatus());
			}

		} catch (Exception e) {
			LOG.error("Exception Thrown : Message " + e.getMessage() + " : ", e);
			this.reversePostingsAndNotify(bankResponse, txn, false, true, null);
		}
	}

	private void handleAdjustmentResponse(BankResponse bankResp) {
		try {
			BankRequest bankRequest = bankResp.getBankRequest();
			LOG.debug("Getting the original txn.......");
			ProcessTransaction txn = processUtil.findProcessTransactionById(bankRequest.getReference());
			LOG.debug("Done finding OG Txn " + txn + " status " + txn.getStatus());
			
			if(txn != null && TransactionStatus.REVERSAL_REQUEST.equals(txn.getStatus())) {
				LOG.debug(">>>>> Ingore this transaction. ID "+txn.getId()+" , Status = "+txn.getStatus());
				return;
			}
			if (!TransactionStatus.COMPLETED.equals(txn.getStatus()) && !TransactionStatus.MANUAL_RESOLVE.equals(txn.getStatus())) {
				// this txn is expecting a result from the bank, so we proceed
				LOG.debug("Processing ewallet Adjustment response for " + txn.getMessageId() + " and response code " + bankResp.getResponseCode());
				// now check the response code
				txn.setResponseCode(bankResp.getResponseCode().name());

				if (TransactionStatus.FAILED.equals(txn.getStatus())) { // txn
																		// timed
																		// out
					LOG.debug("&&&&&&&&     DEPOSIT TXN TIMED OUT... return");
					return;
				}
				// set value dat
				if (bankResp.getValueDate() != null) {
					txn.setValueDate(bankResp.getValueDate());
				} else {
					txn.setValueDate(new Date(System.currentTimeMillis()));
				}
				if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
					// set status to completed
					txn = processUtil.updateProcessTransactionWith(txn.getId(), txn.getResponseCode(), txn.getValueDate(), txn.getNarrative());
					txn = processUtil.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, txn.getNarrative());
					LOG.debug(">>>>>>>> Finished updating Txn " + txn.getStatus().toString());
				} else {
					txn = processUtil.updateProcessTransactionWith(txn.getId(), txn.getResponseCode(), txn.getValueDate(), txn.getNarrative());
					txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, txn.getNarrative());
					LOG.debug(">>>>>>>> Finished updating FAILED Txn " + txn.getStatus().toString());
				}

			} else {
				// txn has already committed
				LOG.debug("Duplicate response for transaction " + txn.getMessageId());
			}

		} catch (Exception e) {
			
			LOG.debug("Exception Thrown : Message " + e.getMessage() + " : ", e);
		}
	}
	
	private void handleAllTxnReversal(BankResponse bankResp){
		
		
		BankRequest bankRequest = bankResp.getBankRequest();
		LOG.info("Handling reversal for : [" + bankRequest.getReference() + "]");
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		EWalletPostingResponse ewtResponse = null;
		
		try {
			LOG.debug("REVERSAL RESPONSE FROM EQ3...");
			
			ProcessTransaction txn = this.processUtil.getProcessTransactionByMessageId(bankRequest.getReference());
			
			if (txn == null) {
				LOG.warn("OG TXN for reversal response not fould : " + bankRequest.getReference() );
				return;
			}
			
			if (TransactionStatus.REVERSAL_REQUEST.equals(txn.getStatus())) {
				LOG.debug("Original Txn is in status REVERSAL_REQUEST.. promote to REVERSAL_RESPONSE");
				txn = processUtil.promoteTxnState(txn, TransactionStatus.REVERSAL_RESPONSE, "Reversal response from EQ");
				LOG.debug("MARKED AS REVERSAL_RESPONSE");
				//proceed
			} else {
				LOG.warn("Original Txn is in not expecting a reversal : Possible DUBLICATE REVERSAL RESPONSE Ignored: " + txn.getMessageId() + " " + txn.getStatus());
				return;
			}
			
			TransactionStatus nextTxnStatus;
			
			if(ResponseCode.E000.equals(bankResp.getResponseCode())){
				LOG.debug("Reversal at EQ was success");
				ewtResponse = bankService.reverseEWalletEntries(this.processUtil.populateTransactionPostingInfoReversal(txn), null);
				LOG.debug("Attempted to reverse EWT Postings");
				if (zw.co.esolutions.ewallet.bankservices.service.ResponseCode.E_600.equals(ewtResponse.getResponseCode()) || zw.co.esolutions.ewallet.bankservices.service.ResponseCode.E_000.equals(ewtResponse.getResponseCode())){
					LOG.debug("Reversals to EWT were successful");
					nextTxnStatus = TransactionStatus.FAILED;
				}else{
					LOG.debug("EWT Reversals Failed : MARK AS MANUAL RESOLVE");
					nextTxnStatus = TransactionStatus.MANUAL_RESOLVE;
				}
				
			}else{
				LOG.debug("EQ Reversals Failed : MARK AS MANUAL RESOLVE");
				txn = processUtil.updateProcessTransactionWith(txn.getId(), bankResp.getResponseCode().name(), bankResp.getValueDate(), bankResp.getNarrative());
				nextTxnStatus = TransactionStatus.MANUAL_RESOLVE;
			}
			
			if(txn.isCollectionTimeOut()){
				//do special notify
				this.sendCustomerResponse(txn,ewtResponse,bankResp);
			}else if(txn.isTimedOut()){
				// do nothing, notify is already done
			}else{
				RequestInfo requestInfo = processUtil.populateRequestInfo(txn);
				ResponseInfo responseInfo = new ResponseInfo(txn.getNarrative(), ResponseCode.E505, requestInfo, ResponseType.NOTIFICATION, bankResp.getAvailableBalance(), bankResp.getLedgerBalance(), 0L, txn.getMessageId());
				processUtil.submitResponse(responseInfo);
			}
			txn = processUtil.promoteTxnState(txn, nextTxnStatus, "Transaction Failed");
		} catch (Exception e) {
			LOG.debug("Exception Thrown : Message " + e.getMessage() + " : ", e);
		}

	}
	
	private void sendCustomerResponse(ProcessTransaction txn , EWalletPostingResponse ewtResponse ,BankResponse bankResp){
		
		String response = null;
		try {	
			RequestInfo requestInfo = processUtil.populateRequestInfo(txn);
			requestInfo.setProfileId(ResponseType.REVERSAL_RESPONSE.toString());
			if(zw.co.esolutions.ewallet.bankservices.service.ResponseCode.E_000.equals(ewtResponse.getResponseCode())){
				LOG.debug("In sendCustomerResponse >>>>>>>> Sending collection time out message for NHW ");
				response = "ZB Transaction Status [nl] Transfer of " + MoneyUtil.convertCentsToDollarsPatternNoCurrency(txn.getAmount()) + " to "+txn.getTargetMobile() + " was reversed due to late collection";
			}else if(ewtResponse.getResponseCode().equals(zw.co.esolutions.ewallet.bankservices.service.ResponseCode.E_600)){
				LOG.debug("In sendCustomerResponse >>>>>>>> Sending txn time out message for all other txn ");
				response = "ZB Transaction Status [nl] Transaction : " + getTransactionTypeNarrative(txn.getTransactionType()).replace("_", " ") + " timed out, amount : "+MoneyUtil.convertCentsToDollarsPatternNoCurrency(txn.getAmount())+" ";
			}
		
			LOG.debug(">>>>>>>>>>>>>>>>>>>>> Reversal Response sent to SWITCH");
		} catch (Exception e) {
			LOG.debug(">>>>>>>>> Exception In Send Customer = "+e.getMessage());
			e.printStackTrace(System.err);
		}
	}
	
	private void reversePostingsAndNotify(BankResponse bankResp, ProcessTransaction txn, boolean hasEWalletPostings, boolean notifyOnly, String customNarrative) {
		LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>> In Reverse Postings and Notify >>>>>>>>>>>>> ");
		if((bankResp != null && txn != null) && ResponseCode.E000.equals(bankResp.getResponseCode()))  {
			try {
				bankResp.setResponseCode(ResponseCode.E505);
				if (customNarrative != null) {
					bankResp.setNarrative(customNarrative);
				} else {
					bankResp.setNarrative(ResponseCode.E505.getDescription());
				}
				
				if(hasEWalletPostings) {
					BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
					LOG.debug("In Reverse Postings and Notify >>>>>>>>>>> Now populating ewallet reversal ");
					PostingsRequestWrapper postings = this.processUtil.populateReversalPostingsResponse(txn);
					LOG.debug(">>>>>>>>>> Main Transaction = "+postings.getTransactionPostingInfos());
					LOG.debug(">>>>>>>>>> Charge Transaction = "+postings.getChargePostingsInfos());
					EWalletPostingResponse ewtResponse = bankService.reverseEWalletEntries(postings.getTransactionPostingInfos(), postings.getChargePostingsInfos());
					LOG.debug(">>>>>>>>>> Postings Response = "+ewtResponse.getResponseCode());
					if(zw.co.esolutions.ewallet.bankservices.service.ResponseCode.E_600.equals(ewtResponse.getResponseCode()) || 
							zw.co.esolutions.ewallet.bankservices.service.ResponseCode.E_000.equals(ewtResponse.getResponseCode())){
						LOG.debug("Reversal successful markin original txn as failed  >>>>>>>>>>>>>>>>.");
						txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, "Transaction Failed.");
						LOG.debug("MARKED AS FAILED");
						this.processUtil.rollbackEQ3Postings(txn);
						txn.setNarrative("Transaction Failed. Reversed.");
						txn = processUtil.updateProcessTransaction(txn);
						
						this.notifyCustomerWithFailure(txn, bankResp);
						
					}else {
						LOG.debug("Reversal failed marking as Manual Resolve >>>>>>>>>>>>>>>>.");
						txn = processUtil.promoteTxnState(txn, TransactionStatus.MANUAL_RESOLVE, "Reversal of txn failed in EWT");
						LOG.debug("MARKED AS MANUAL_RESOLVE");
						
						this.notifyCustomerWithFailure(txn, bankResp);
					}
				} else {
					if(notifyOnly) {
						LOG.debug("Notify Only  >>>>>>>>>>>>>>>>>>  "+txn.getTransactionType()+" ID = "+txn.getId());
						txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, "Transaction failed. ");
						LOG.debug("MARKED AS FAILED");
						
						this.notifyCustomerWithFailure(txn, bankResp);
					} else {
						LOG.debug("No ewallet postings here : "+txn.getTransactionType());
						txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, "Transaction Failed.");
						this.processUtil.rollbackEQ3Postings(txn);
						txn.setNarrative("Transaction Failed. Reversed.");
						txn = processUtil.updateProcessTransaction(txn);
						
						this.notifyCustomerWithFailure(txn, bankResp);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			LOG.debug(">>>>>>Failed Transaction.");
			this.notifyCustomerWithFailure(txn, bankResp);
		}
		
	}
	
	private void notifyCustomerWithFailure(ProcessTransaction txn, BankResponse bankResp){
		LOG.debug("In notify customer method.");
		if(txn == null && bankResp == null) {
			LOG.debug(" Bank Response and Process Txn null.");
			return;
		}
		String response = bankResp.getNarrative();
		RequestInfo requestInfo = processUtil.populateRequestInfo(txn);
		
		ResponseInfo responseInfo = new ResponseInfo(response, bankResp.getResponseCode(), requestInfo, ResponseType.NOTIFICATION, bankResp.getAvailableBalance(), bankResp.getLedgerBalance(), 0L, txn.getMessageId());
		
		try {
			processUtil.submitResponse(responseInfo);
			LOG.debug("Notification message sent to SWITCH");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void handleAgentCustomerDepositResponse(BankResponse bankResp) {
		//INITIATOR: AGENT
		LOG.debug("HandleAgentCustomerDepositResponse Has consumed!!!!!!");
		ProcessTransaction txn = null;
		try {
			BankRequest request = bankResp.getBankRequest();
			// retrieve txn and update
			txn = this.processUtil.getProcessTransactionByMessageId(request.getReference());
			if(txn != null && TransactionStatus.FAILED.equals(txn.getStatus())) {
				// Edit Bank Response Here
				bankResp.setNarrative(ResponseCode.E505.getDescription());
				bankResp.setResponseCode(ResponseCode.E505);
				LOG.debug("Txn has already FAILED..");
				if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
					LOG.debug("Reverse successful EQ3 postings..");
					processUtil.rollbackEQ3Postings(txn);
				}
				
			} else {
				txn = this.processUtil.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, bankResp.getNarrative());
				txn = processUtil.updateProcessTxnWithBankRespInfo(txn, bankResp);
				if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
					// post to e-wallet accounts
					ResponseCode responseCode = this.processUtil.postAgentCustomerDeposit(txn);
					GenerateKey.throwsBankResponseException();
					if (ResponseCode.E000.equals(responseCode)) {
						txn = this.processUtil.promoteTxnState(txn, TransactionStatus.COMPLETED, bankResp.getNarrative());
						bankResp.setAvailableBalance(this.processUtil.getRunningBalance(request.getSourceMobileNumber(), BankAccountType.AGENT_EWALLET));
					} else {
						bankResp.setNarrative(ResponseCode.E505.getDescription());
						bankResp.setResponseCode(responseCode);
						txn = this.processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResp.getNarrative());
					}
	
				} else {
					txn = this.processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResp.getNarrative());
					bankResp.setAvailableBalance(this.processUtil.getRunningBalance(request.getSourceMobileNumber(), BankAccountType.AGENT_EWALLET));
				}
			}
			
			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
			CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();

			RequestInfo requestInfo = processUtil.populateRequestInfo(txn);
			BankAccount sourceBankAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getCustomerId(), BankAccountType.AGENT_EWALLET, OwnerType.AGENT, txn.getSourceMobile());
			MobileProfile targetMobile = customerService.findMobileProfileById(txn.getTargetMobileId());

			BankAccount targetBankAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(targetMobile.getCustomer().getId(), BankAccountType.E_WALLET, OwnerType.CUSTOMER, targetMobile.getMobileNumber());
			ResponseInfo responseInfo = new ResponseInfo(bankResp.getNarrative(), bankResp.getResponseCode(), requestInfo, ResponseType.NOTIFICATION, sourceBankAccount.getRunningBalance(), sourceBankAccount.getRunningBalance(), targetBankAccount.getRunningBalance(), txn.getMessageId());
			LOG.debug("Sending AGENT CUSTOMER DEPOSIT REPLY (to SWITCH) for transaction...." + txn.getMessageId());
			processUtil.submitResponse(responseInfo);

		} catch (Exception e) {
			LOG.debug("Exception Thrown : Message " + e.getMessage() + " : ", e);
			this.reversePostingsAndNotify(bankResp, txn, true, false, null);
		}

	}
	
	private void handleAgentCustomerWithdrawalResponse(BankResponse bankResp) {
		//INITIATOR: CUSTOMER
		LOG.debug("HandleAgentCustomerWithdrawalResponse Has consumed!!!!!!");
		ProcessTransaction txn = null;
		try {
			BankRequest request = bankResp.getBankRequest();
			// retrieve txn and update
			txn = this.processUtil.getProcessTransactionByMessageId(request.getReference());
			if(txn != null && TransactionStatus.FAILED.equals(txn.getStatus())) {
				// Edit Bank Response Here
				bankResp.setNarrative(ResponseCode.E505.getDescription());
				bankResp.setResponseCode(ResponseCode.E505);
				LOG.debug("Txn has already FAILED..");
				if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
					LOG.debug("Reverse successful EQ3 postings..");
					processUtil.rollbackEQ3Postings(txn);
				}
				
			} else {
				txn = this.processUtil.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, bankResp.getNarrative());
				txn = processUtil.updateProcessTxnWithBankRespInfo(txn, bankResp);
				if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
					// post to e-wallet accounts
					ResponseCode responseCode = this.processUtil.postAgentCustomerWithdrawal(txn);
					GenerateKey.throwsBankResponseException();
					if (ResponseCode.E000.equals(responseCode)) {
						txn = this.processUtil.promoteTxnState(txn, TransactionStatus.COMPLETED, bankResp.getNarrative());
						bankResp.setAvailableBalance(this.processUtil.getRunningBalance(request.getSourceMobileNumber(), BankAccountType.E_WALLET));
					} else {
						bankResp.setNarrative(ResponseCode.E505.getDescription());
						bankResp.setResponseCode(responseCode);
						txn = this.processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResp.getNarrative());
					}
	
				} else {
					txn = this.processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResp.getNarrative());
					bankResp.setAvailableBalance(this.processUtil.getRunningBalance(request.getSourceMobileNumber(), BankAccountType.E_WALLET));
				}
			}
			
			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
			CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();

			RequestInfo requestInfo = processUtil.populateRequestInfo(txn);
			BankAccount bankAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getCustomerId(), BankAccountType.E_WALLET, OwnerType.CUSTOMER, txn.getSourceMobile());
			MobileProfile targetMobile = customerService.findMobileProfileById(txn.getTargetMobileId());

			BankAccount target = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(targetMobile.getCustomer().getId(), BankAccountType.AGENT_EWALLET, OwnerType.AGENT, targetMobile.getMobileNumber());
			ResponseInfo responseInfo = new ResponseInfo(bankResp.getNarrative(), bankResp.getResponseCode(), requestInfo, ResponseType.NOTIFICATION, bankAccount.getRunningBalance(), bankAccount.getRunningBalance(), target.getRunningBalance(), txn.getMessageId());
			LOG.debug("Sending AGENT CUSTOMER WITHDRAWAL REPLY (to SWITCH) for transaction...." + txn.getMessageId());
			processUtil.submitResponse(responseInfo);

		} catch (Exception e) {
			LOG.debug("Exception Thrown : Message " + e.getMessage() + " : ", e);
			this.reversePostingsAndNotify(bankResp, txn, true, false, null);
		}

	}
	
	private void handleAgentCustomerNonHolderWithdrawalResponse(BankResponse bankResp) {
		//INITIATOR: CUSTOMER
		LOG.debug("HandleAgentCustomerNonHolderWithdrawalResponse Has consumed!!!!!!");
		ProcessTransaction txn = null;
		try {
			BankRequest request = bankResp.getBankRequest();
			// retrieve txn and update
			txn = this.processUtil.getProcessTransactionByMessageId(request.getReference());
			if(txn != null && TransactionStatus.FAILED.equals(txn.getStatus())) {
				// Edit Bank Response Here
				bankResp.setNarrative(ResponseCode.E505.getDescription());
				bankResp.setResponseCode(ResponseCode.E505);
				LOG.debug("Txn has already FAILED..");
				if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
					LOG.debug("Reverse successful EQ3 postings..");
					processUtil.rollbackEQ3Postings(txn);
				}
				
			} else {
				LOG.debug("Tranx ok.. promote it to BANK_RESPONSE");
				txn = this.processUtil.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, bankResp.getNarrative());
				txn = processUtil.updateProcessTxnWithBankRespInfo(txn, bankResp);
				if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
					//check transfer status
					LOG.debug("BankResponse is E000...continue..");
					ProcessTransaction originalTransfer = processUtil.getProcessTransactionByMessageId(txn.getOldMessageId());
					LOG.debug("NonHolder transfer is in state.. " + originalTransfer.getStatus());
					if (!TransactionStatus.AWAITING_COLLECTION.equals(originalTransfer.getStatus())) {
						bankResp.setResponseCode(ResponseCode.E505);
						bankResp.setNarrative("Invalid nonholder transfer state. Cannot be withdrawn");
						this.reversePostingsAndNotify(bankResp, txn, false, false, bankResp.getNarrative());
						return;
					}
					// post to e-wallet accounts
					LOG.debug("Performing postings...");
					ResponseCode responseCode = this.processUtil.postAgentCustomerNonHolderWithdrawal(txn);
					GenerateKey.throwsBankResponseException();
					
					if (ResponseCode.E000.equals(responseCode)) {
						//promote withdrawal txn to completed
						LOG.debug("Postings Successful.. promote both txns to COMPLETED");
						txn = this.processUtil.promoteTxnState(txn, TransactionStatus.COMPLETED, bankResp.getNarrative());
						//promote original txn to completed
						originalTransfer = processUtil.promoteTxnState(originalTransfer, TransactionStatus.COMPLETED, bankResp.getNarrative());
						
						bankResp.setAvailableBalance(this.processUtil.getRunningBalance(request.getSourceMobileNumber(), BankAccountType.AGENT_EWALLET));
						
					} else {
						LOG.debug("Postings failed.. ");
						bankResp.setNarrative(ResponseCode.E505.getDescription());
						bankResp.setResponseCode(responseCode);
						txn = this.processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResp.getNarrative());
					}
	
				} else {
					LOG.debug("Postings failed.. ");
					txn = this.processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResp.getNarrative());
					bankResp.setAvailableBalance(this.processUtil.getRunningBalance(request.getSourceMobileNumber(), BankAccountType.AGENT_EWALLET));
				}
			}
			
			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();

			RequestInfo requestInfo = processUtil.populateRequestInfo(txn);
			BankAccount bankAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getCustomerId(), BankAccountType.AGENT_EWALLET, OwnerType.AGENT, txn.getSourceMobile());

			ResponseInfo responseInfo = new ResponseInfo(bankResp.getNarrative(), bankResp.getResponseCode(), requestInfo, ResponseType.NOTIFICATION, bankAccount.getRunningBalance(), bankAccount.getRunningBalance(), 0L, txn.getMessageId());
			LOG.debug("Sending AGENT CUSTOMER NONHOLDER WITHDRAWAL REPLY (to SWITCH) for transaction...." + txn.getMessageId());
			processUtil.submitResponse(responseInfo);

		} catch (Exception e) {
			LOG.debug("Exception Thrown : Message " + e.getMessage() + " : ", e);
			this.reversePostingsAndNotify(bankResp, txn, true, false, null);
		}

	}
	
	private void handleEWalletAgentDepositTxnResp(BankResponse bankResp) {
		ProcessTransaction txn = null;
		try {
			BankRequest bankRequest = bankResp.getBankRequest();
			LOG.debug("Getting the original txn.......");
			txn = processUtil.findProcessTransactionById(bankRequest.getReference());
			
			if(txn != null && TransactionStatus.FAILED.equals(txn.getStatus())) {
				// Edit Bank Response Here
				bankResp.setNarrative(ResponseCode.E505.getDescription());
				bankResp.setResponseCode(ResponseCode.E505);
				LOG.debug("Failed Transaction.......... IGNORE Ref = "+txn.getId());
				return ;
			} else if(txn != null && TransactionStatus.REVERSAL_REQUEST.equals(txn.getStatus())) {
				LOG.debug(">>>>> Ingore this transaction. ID "+txn.getId()+" , Status = "+txn.getStatus());
				return;
			}
			LOG.debug("Done finding OG Txn " + txn + " status " + txn.getStatus());
			if (!TransactionStatus.COMPLETED.equals(txn.getStatus()) && !TransactionStatus.MANUAL_RESOLVE.equals(txn.getStatus())) {
				// this txn is expecting a result from the bank, so we proceed
				LOG.debug("Processing ewallet Deposit response for " + txn.getMessageId() + " and response code " + bankResp.getResponseCode());
				// now check the response code
				txn.setResponseCode(bankResp.getResponseCode().name());
				txn.setNarrative(bankResp.getNarrative());

				if (TransactionStatus.FAILED.equals(txn.getStatus())) { // txn
																		// timed
																		// out
					LOG.debug("&&&&&&&&  AGENT   DEPOSIT TXN TIMED OUT... return");
					return;
				}
				// set value dat
				if (bankResp.getValueDate() != null) {
					txn.setValueDate(bankResp.getValueDate());
				} else {
					txn.setValueDate(new Date(System.currentTimeMillis()));
				}
				if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
					// set status to completed
					txn = processUtil.updateProcessTransactionWith(txn.getId(), txn.getResponseCode(), txn.getValueDate(), txn.getNarrative());
					txn = processUtil.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, txn.getNarrative());
					LOG.debug(">>>>>>>> Finished updating Txn " + txn.getStatus().toString());
				} else {
					txn = processUtil.updateProcessTransactionWith(txn.getId(), txn.getResponseCode(), txn.getValueDate(), txn.getNarrative());
					txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, txn.getNarrative());
					LOG.debug(">>>>>>>> Finished updating FAILED Txn " + txn.getStatus().toString());
				}

			} else {
				// txn has already committed
				LOG.debug("Duplicate response for transaction " + txn.getMessageId());
			}

		} catch (Exception e) {
			LOG.debug("Exception Thrown : Message " + e.getMessage() + " : ", e);
			//No Need for deposit Txn
			//this.reversePostingsAndNotify(bankResp, txn);
		}

	}
	
	private void handleAgentEWalletToBankAccountTransferResp(BankResponse bankResp) {
		ProcessTransaction txn = null;
		try {
			BankRequest request = bankResp.getBankRequest();

			txn = this.processUtil.getProcessTransactionByMessageId(request.getReference());
			if(txn != null && TransactionStatus.FAILED.equals(txn.getStatus())) {
				// Edit Bank Response Here
				bankResp.setNarrative(ResponseCode.E505.getDescription());
				bankResp.setResponseCode(ResponseCode.E505);
				LOG.debug("Failed Transaction.......... IGNORE Ref = "+txn.getId());
				return;
			} else if(txn != null && TransactionStatus.REVERSAL_REQUEST.equals(txn.getStatus())) {
				LOG.debug(">>>>> Ingore this transaction. ID "+txn.getId()+" , Status = "+txn.getStatus());
				return;
			}
			txn = this.processUtil.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, bankResp.getNarrative());
			txn = processUtil.updateProcessTxnWithBankRespInfo(txn, bankResp);
			if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
				ResponseCode responseCode = this.processUtil.postAgentEWalletAccountToBankAccountTransfer(txn);
				//GenerateKey.throwsBankResponseException();
				if (ResponseCode.E000.equals(responseCode)) {
					txn = this.processUtil.promoteTxnState(txn, TransactionStatus.COMPLETED, bankResp.getNarrative());
					LOG.debug("Finished promoting transaction status : "+txn.getStatus());
					bankResp.setAvailableBalance(this.processUtil.getRunningBalance(request.getSourceMobileNumber(), BankAccountType.E_WALLET));
				} else {
					bankResp.setNarrative(ResponseCode.E505.getDescription());
					bankResp.setResponseCode(responseCode);
				}
			} else {
				if(!TransactionStatus.FAILED.equals(txn.getStatus())) {
					txn = this.processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResp.getNarrative());
				}
			}
			
			RequestInfo requestInfo = processUtil.populateRequestInfo(txn);
			BankAccount bankAccount = new BankServiceSOAPProxy().getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getCustomerId(), BankAccountType.AGENT_EWALLET, OwnerType.AGENT, txn.getSourceMobile());
			ResponseInfo responseInfo = new ResponseInfo(bankResp.getNarrative(), bankResp.getResponseCode(), requestInfo, ResponseType.NOTIFICATION, bankAccount.getRunningBalance(), bankAccount.getRunningBalance(), bankResp.getLedgerBalance(), txn.getMessageId());
			LOG.debug("Sending AGENT EWALLET TO BANK ACCOUNT REPLY (to SWITCH) for transaction...." + txn.getMessageId());
			processUtil.submitResponse(responseInfo);
		} catch (Exception e) {
			LOG.debug("Exception Thrown : Message " + e.getMessage() + " : ", e);
			this.reversePostingsAndNotify(bankResp, txn, true, false, null);
		}
	}
	
	private String getTransactionTypeNarrative(TransactionType type) {
		String str = "";
		if(TransactionType.ADJUSTMENT.equals(type)) {
			
			str = TransactionType.ADJUSTMENT.toString();
			
		} else if(TransactionType.AGENT_ACCOUNT_BALANCE.equals(type) || 
				TransactionType.BALANCE.equals(type) || TransactionType.BALANCE_REQUEST.equals(type)) {
			
			str = TransactionType.BALANCE_REQUEST.toString();
			
		} else if(TransactionType.AGENT_CASH_DEPOSIT.equals(type) || 
				TransactionType.DEPOSIT.equals(type) ||
				TransactionType.AGENT_CUSTOMER_DEPOSIT.equals(type)) {
			
			str = TransactionType.DEPOSIT.toString();
			
		} else if(TransactionType.WITHDRAWAL.equals(type) || 
				TransactionType.AGENT_CUSTOMER_WITHDRAWAL.equals(type)) {
			
			str = TransactionType.WITHDRAWAL.toString();
			
		} else if(TransactionType.WITHDRAWAL_NONHOLDER.equals(type) ||
				TransactionType.AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL.equals(type)) {
			
			str = "NON-HOLDER WITHDRAWAL";
			
		} else if(TransactionType.TOPUP.equals(type) ||
				TransactionType.EWALLET_TOPUP.equals(type)) {
			
			str = TransactionType.TOPUP.toString();
			
		} else if(TransactionType.BILLPAY.equals(type) || 
				TransactionType.EWALLET_BILLPAY.equals(type)) {
			
			str = TransactionType.BILLPAY.toString();
			
		} else if(TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER.equals(type) || 
				TransactionType.EWALLET_TO_EWALLET_TRANSFER.equals(type) || 
				TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER.equals(type)) {
			
			str = "TRANSFER";
			
		} else if(TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER.equals(type) ||
				TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER.equals(type) || 
				TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER.equals(type)) {
			
			str = "TRANSFER";
			
		} else if(TransactionType.AGENT_EMONEY_TRANSFER.equals(type) ||
				TransactionType.AGENT_EWALLET_TO_BANKACCOUNT_TRANSFER.equals(type)) {
			
			str = "TRANSFER";
			
		} else if(TransactionType.AGENT_SUMMARY.equals(type)) {
			
			str = TransactionType.AGENT_SUMMARY.toString();
			
		} else if(TransactionType.MINI_STATEMENT.equals(type)) {
			
			str = TransactionType.MINI_STATEMENT.toString();
			
		}
			
		return str;
	}
	
	private void handleAgentCommissionSweepResp(BankResponse bankResp){
		ProcessTransaction txn = null;
		try {
			BankRequest request = bankResp.getBankRequest();

			txn = this.processUtil.getProcessTransactionByMessageId(request.getReference());
			if(txn != null && TransactionStatus.FAILED.equals(txn.getStatus())) {
				// Edit Bank Response Here
				bankResp.setNarrative(ResponseCode.E505.getDescription());
				bankResp.setResponseCode(ResponseCode.E505);
				LOG.debug("Failed Transaction.......... IGNORE Ref = "+txn.getId());
				return;
			} else if(txn != null && TransactionStatus.REVERSAL_REQUEST.equals(txn.getStatus())) {
				LOG.debug(">>>>> Ingore this transaction. ID "+txn.getId()+" , Status = "+txn.getStatus());
				return;
			}
			txn = this.processUtil.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, bankResp.getNarrative());
			txn = processUtil.updateProcessTxnWithBankRespInfo(txn, bankResp);
			if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
				ResponseCode responseCode = this.processUtil.postAgentCommissionSweep(txn);
				//GenerateKey.throwsBankResponseException();
				if (ResponseCode.E000.equals(responseCode)) {
					txn = this.processUtil.promoteTxnState(txn, TransactionStatus.COMPLETED, bankResp.getNarrative());
					LOG.debug("Finished promoting transaction status : "+txn.getStatus());
					bankResp.setAvailableBalance(this.processUtil.getRunningBalance(request.getSourceMobileNumber(), BankAccountType.AGENT_EWALLET));
				} else {
					bankResp.setNarrative(ResponseCode.E505.getDescription());
					bankResp.setResponseCode(responseCode);
				}
			} else {
				if(!TransactionStatus.FAILED.equals(txn.getStatus())) {
					txn = this.processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResp.getNarrative());
				}
			}
		} catch (Exception e) {
			LOG.debug("Exception Thrown : Message " + e.getMessage() + " : ", e);
			Map<String, String> configMap = config.getConfigMap();
			double amnt = (txn.getAmount())/100;
			String recipient = configMap.get(SystemConstants.COMMISSION_SWEEPING_EMAIL);
			String subject = "Failed Commission Sweep "+txn.getMessageId();
			String title = "Failed Agent Commission Sweep";
			String heading = " Reference    , Mobile Number , Agent Number , Amount($)";
			String txnInfo = " "+txn.getMessageId()+" , "+txn.getSourceMobile()+"  , "+txn.getAgentNumber()+"       , "+amnt;
			processUtil.sendReport(recipient, subject, title, txnInfo , heading);
			this.reversePostingsAndNotify(bankResp, txn, true, false, null);
		}
	}
	
	private void handleAlertRegistrationResponse(BankResponse bankResp) {
		ProcessTransaction txn = null;
		try {
			BankRequest bankRequest = bankResp.getBankRequest();
			LOG.debug("Getting the original txn.......");
			txn = processUtil.findProcessTransactionById(bankRequest.getReference());
			
			if(txn != null && TransactionStatus.FAILED.equals(txn.getStatus())) {
				// Edit Bank Response Here
				bankResp.setNarrative(ResponseCode.E505.getDescription());
				bankResp.setResponseCode(ResponseCode.E505);
				LOG.debug("Failed Transaction.. IGNORE Ref = "+txn.getId());
				return ;
			} 
			
			if (ResponseCode.E000.equals(bankResp.getResponseCode())) {
				// set status to completed
				txn = processUtil.updateProcessTransactionWith(txn.getId(), bankResp.getResponseCode().name(), txn.getValueDate(), txn.getNarrative());
				txn = processUtil.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, txn.getNarrative());
				LOG.debug("@@ Finished updating Txn " + txn.getStatus().toString());
			} else {
				txn = processUtil.updateProcessTransactionWith(txn.getId(), bankResp.getResponseCode().name(), txn.getValueDate(), txn.getNarrative());
				txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, txn.getNarrative());
				LOG.debug("@@ Finished updating FAILED Txn " + txn.getStatus().toString());
			}

		} catch (Exception e) {
			LOG.debug("Exception Thrown : Message " + e.getMessage() + " : ", e);
		}

	}
}