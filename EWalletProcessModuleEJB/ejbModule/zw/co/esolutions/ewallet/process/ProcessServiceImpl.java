package zw.co.esolutions.ewallet.process;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.ewallet.agentservice.service.Agent;
import zw.co.esolutions.ewallet.agentservice.service.AgentServiceSOAPProxy;
import zw.co.esolutions.ewallet.alertsservices.service.AlertOption;
import zw.co.esolutions.ewallet.alertsservices.service.AlertOptionStatus;
import zw.co.esolutions.ewallet.alertsservices.service.AlertsServiceSOAPProxy;
import zw.co.esolutions.ewallet.audittrailservices.service.AuditTrailServiceSOAPProxy;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountType;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.CustomerAutoRegStatus;
import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.enums.DayEndStatus;
import zw.co.esolutions.ewallet.enums.MobileNetworkOperator;
import zw.co.esolutions.ewallet.enums.ResponseType;
import zw.co.esolutions.ewallet.enums.TransactionClass;
import zw.co.esolutions.ewallet.enums.TransactionLocationType;
import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.enums.TxnFamily;
import zw.co.esolutions.ewallet.limitservices.service.BankAccountClass;
import zw.co.esolutions.ewallet.limitservices.service.Limit;
import zw.co.esolutions.ewallet.limitservices.service.LimitPeriodType;
import zw.co.esolutions.ewallet.limitservices.service.LimitServiceSOAPProxy;
import zw.co.esolutions.ewallet.limitservices.service.TransactionType;
import zw.co.esolutions.ewallet.msg.BankRequest;
import zw.co.esolutions.ewallet.msg.BankResponse;
import zw.co.esolutions.ewallet.msg.DayEndApprovalResponse;
import zw.co.esolutions.ewallet.msg.ProcessResponse;
import zw.co.esolutions.ewallet.msg.RequestInfo;
import zw.co.esolutions.ewallet.msg.ResponseInfo;
import zw.co.esolutions.ewallet.process.model.DayEnd;
import zw.co.esolutions.ewallet.process.model.DayEndResponse;
import zw.co.esolutions.ewallet.process.model.DayEndSummary;
import zw.co.esolutions.ewallet.process.model.ProcessTransaction;
import zw.co.esolutions.ewallet.process.model.TransactionCharge;
import zw.co.esolutions.ewallet.process.pojo.ManualPojo;
import zw.co.esolutions.ewallet.process.pojo.ManualReturn;
import zw.co.esolutions.ewallet.process.pojo.UniversalProcessSearch;
import zw.co.esolutions.ewallet.process.timers.AgentCommissionProcessor;
import zw.co.esolutions.ewallet.process.timers.MonthlyProcessorLocal;
import zw.co.esolutions.ewallet.process.timers.NonHolderTxfProcessor;
import zw.co.esolutions.ewallet.process.timers.TimedOutTxnProcessor;
import zw.co.esolutions.ewallet.process.util.DepositInfo;
import zw.co.esolutions.ewallet.process.util.NonHolderWithdrawalInfo;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.sms.MessageSender;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.tariffservices.service.AgentType;
import zw.co.esolutions.ewallet.tariffservices.service.CustomerClass;
import zw.co.esolutions.ewallet.tariffservices.service.Tariff;
import zw.co.esolutions.ewallet.tariffservices.service.TariffServiceSOAPProxy;
import zw.co.esolutions.ewallet.tariffservices.service.TariffType;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.Formats;
import zw.co.esolutions.ewallet.util.GenerateKey;
import zw.co.esolutions.ewallet.util.MoneyUtil;
import zw.co.esolutions.ewallet.util.NumberUtil;
import zw.co.esolutions.ewallet.util.SystemConstants;
import zw.co.esolutions.mcommerce.refgen.service.ReferenceGeneratorServiceSOAPProxy;

@Stateless
@SuppressWarnings("unused")
@WebService(serviceName = "ProcessService", endpointInterface = "zw.co.esolutions.ewallet.process.ProcessService", portName = "ProcessServiceSOAP")
public class ProcessServiceImpl implements ProcessService {

	@PersistenceContext
	private EntityManager em;

	@EJB
	private ProcessUtil processUtil;
	@EJB
	private DayEndBeanLocal dayEndBean;

	@EJB
	private MonthlyProcessorLocal monthlyProcessor;
	@EJB
	private ConfigurationsLocal config;
	@EJB
	private NonHolderTxfProcessor nonHolderTxfProcessor;
	@EJB
	private TimedOutTxnProcessor timedOutTxnProcessor;
	@EJB 
	private AgentCommissionProcessor agentCommissionProcessor;

	private final static String TXN_REVERSAL_TIMER = "Txn_Reversal_Timer";
	
	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(ProcessServiceImpl.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + ProcessServiceImpl.class);
		}
	}

	/**
	 * Default constructor.
	 */
	public ProcessServiceImpl() {
		super();
	}

	@Override
	public ProcessResponse depositCash(DepositInfo depositInfo) {
		String messageId;
		// Populating Txn Process
		ProcessTransaction processTxn = null;
		ProcessResponse res = new ProcessResponse();
		if (depositInfo == null) {
			res.setResponseCode(ResponseCode.E777.name());
			return res;
		}
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		ReferenceGeneratorServiceSOAPProxy refService = new ReferenceGeneratorServiceSOAPProxy();
		try {
			LOG.debug("Deposit Info mobile number : " + depositInfo.getMobileNumber() + " and profile id " + depositInfo.getProfileId());

			MobileProfile mobileProfile = customerService.getMobileProfileByMobileNumber(depositInfo.getMobileNumber());
			LOG.debug("Found mobile profile ..." + mobileProfile);
			Profile profile = profileService.findProfileById(depositInfo.getProfileId());
			LOG.debug("Found a profile " + profile);
			
			processTxn = this.createDepositProcessTransaction(depositInfo, mobileProfile, profile);

			LOG.debug("Done populating the txn........");
			// Create Txn Process
			processTxn = this.processUtil.createProcessTransaction(processTxn);
			LOG.debug("Done createProcess txn..");
			this.processUtil.promoteTxnState(processTxn, TransactionStatus.DRAFT, TransactionStatus.DRAFT.name());
			LOG.debug("Done promoting txn state...");

			try {
				processTxn = this.processUtil.findProcessTransactionById(processTxn.getId());
				processTxn = this.processUtil.addApplicableTariffToProcessTxn(processTxn);

				// Populating Bank Req
				BankRequest bankReq = this.processUtil.populateBankRequest(processTxn);
				bankReq.setAmount(depositInfo.getAmount());
				bankReq.setSourceBankCode(depositInfo.getBankCode());
				bankReq.setSourceMobileNumber(depositInfo.getMobileNumber());
				bankReq.setTransactionType(zw.co.esolutions.ewallet.enums.TransactionType.DEPOSIT);
				bankReq.setCurrencyISOCode("USD");

				GenerateKey.throwsException();
				
				this.sendBankRequestToBank(bankReq);

				this.processUtil.promoteTxnState(processTxn, TransactionStatus.BANK_REQUEST, TransactionStatus.BANK_REQUEST.name());

				// wait for response

				BankResponse bankResponse = processUtil.waitForBankResponse(bankReq);

				if (ResponseCode.E000.equals(bankResponse.getResponseCode())) {
					// post to ewallet
					ResponseCode resp = this.processUtil.postEWalletDepositTxn(processTxn);
					if (!ResponseCode.E000.equals(resp)) {
						res.setResponseCode(resp.name());
						return res;
					}
					// success in ewallet
					res.setResponseCode(resp.name());
					res.setAmount(processTxn.getAmount());
					res.setBalance(processTxn.getBalance());
					res.setMessageId(processTxn.getMessageId());

					LOG.debug(">>>>>>>> Done Deposit Book entry posting");

					// send notif
					if (ResponseCode.E000.equals(resp)) {
						processTxn = processUtil.promoteTxnState(processTxn, TransactionStatus.COMPLETED, bankResponse.getNarrative());
						this.notifyCustomerOfDepositSuccess(processTxn, bankResponse);
					}

				} else {

						if (ResponseCode.E830.equals(bankResponse.getResponseCode())) { // txn
																					// has
																					// timed
						LOG.debug("timing out >>>>>>>>>>>>>>>>Deposit cash time out txn  "+processTxn.getId());															// out
						//processUtil.submitRequest(bankReq, true);
						processTxn=markTxnAsTimedOut(processTxn, bankResponse);

					}
					else{
						processTxn = processUtil.promoteTxnState(processTxn, TransactionStatus.FAILED, bankResponse.getNarrative());

						
					}
					res.setResponseCode(bankResponse.getResponseCode().name());
					LOG.debug("DEPOSIT FAILED : ResponseCode > " + bankResponse.getResponseCode().name() + " : " + bankResponse.getNarrative());

				}
			} catch (Exception e) {
				e.printStackTrace();
				res.setResponseCode(ResponseCode.E818.name());
				if(processTxn != null) {
					processTxn = processUtil.promoteTxnState(processTxn, TransactionStatus.FAILED, ResponseCode.E818.getDescription());
				}
				return res;
			}

		} catch (Exception e) {
			e.printStackTrace();
			res.setResponseCode(ResponseCode.E818.name());
			if (processTxn != null) {
				try { processTxn = processUtil.promoteTxnState(processTxn,
						TransactionStatus.FAILED, ResponseCode.E818
								.getDescription()); 
				} catch (Exception e1) {
					// TODO: handle exception
				}
			}
			return res;
		}

		return res;
	}

	public ProcessTransaction markTxnAsTimedOut(ProcessTransaction processTxn, BankResponse response) throws Exception{
		
		processTxn = processUtil.promoteTxnState(processTxn, TransactionStatus.TIMEOUT, response.getNarrative());
		LOG.debug("TXN timed out >>>>"+processTxn.getId()+">>>>>>>>>>>>>>>>>>"+processTxn.getTransactionType());
		processTxn.setTimedOut(true);
		processTxn= processUtil.updateProcessTransaction(processTxn);
		LOG.debug("TXN timed out END>>>>"+processTxn.getId()+">>>>>>>>>>>>>>>>>>"+processTxn.getTransactionType());
		
		return processTxn;
	}
	
	private void notifyCustomerOfDepositSuccess(ProcessTransaction txn, BankResponse bankResp) {
		try {
			RequestInfo requestInfo = processUtil.populateRequestInfo(txn);
			requestInfo.setTargetMobile(requestInfo.getSourceMobile());
			BankAccount bankAccount = new BankServiceSOAPProxy().getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getCustomerId(), BankAccountType.E_WALLET, OwnerType.CUSTOMER, txn.getSourceMobile());

			ResponseInfo responseInfo = new ResponseInfo(bankResp.getNarrative(), ResponseCode.E000, requestInfo, ResponseType.NOTIFICATION, bankAccount.getRunningBalance(), bankAccount.getRunningBalance(), 0L, txn.getMessageId());
			LOG.debug("Sending DEPOSIT REPLY (to SWITCH) for transaction...." + txn.getMessageId());
			processUtil.submitResponse(responseInfo);
			LOG.debug("Done.........");
		} catch (Exception e) {
			LOG.debug("FAILED TO NOTIFY CUSTOMER");
		}

	}

	private ProcessTransaction createDepositProcessTransaction(DepositInfo depositInfo, MobileProfile mobileProfile, Profile profile) throws Exception {

		ReferenceGeneratorServiceSOAPProxy refProxy = new ReferenceGeneratorServiceSOAPProxy();

		String messageId = refProxy.generateUUID(zw.co.esolutions.ewallet.enums.TransactionType.DEPOSIT.name(), "D", Formats.yearFormatTwoDigit.format(new Date(System.currentTimeMillis())), 0, 1000000000L - 1L);
		ProcessTransaction processTxn = new ProcessTransaction();
		processTxn.setMessageId(messageId);
		processTxn.setId(messageId);
		processTxn.setAmount(depositInfo.getAmount());
		processTxn.setBalance(depositInfo.getRunningBalance());
		processTxn.setNarrative("CASH Deposit");
		processTxn.setBankReference(depositInfo.getBankCode());
		processTxn.setTransactionType(zw.co.esolutions.ewallet.enums.TransactionType.DEPOSIT);
		processTxn.setSourceMobileId(mobileProfile.getId());
		processTxn.setProfileId(depositInfo.getProfileId());
		processTxn.setFromBankId(mobileProfile.getBankId());
		processTxn.setToBankId(mobileProfile.getBankId());
		// we all know that for a deposit the banks are indeer the same
		processTxn.setSourceAccountNumber(mobileProfile.getMobileNumber());
		processTxn.setDestinationAccountNumber(mobileProfile.getMobileNumber());
		processTxn.setSourceEQ3AccountNumber(mobileProfile.getMobileNumber());
		processTxn.setDestinationEQ3AccountNumber(mobileProfile.getMobileNumber());
		processTxn.setCustomerId(mobileProfile.getCustomer().getId());
		// branch Id MUST be set to the customer's branch
		processTxn.setBranchId(mobileProfile.getCustomer().getBranchId());
		// we postto the teller's branch
		processTxn.setTransactionLocationType(TransactionLocationType.BANK_BRANCH);
		processTxn.setTransactionLocationId(profile.getBranchId());
		processTxn.setSourceMobile(depositInfo.getMobileNumber());
		processTxn.setTargetMobileId(processTxn.getSourceMobileId());
		processTxn.setTargetMobile(processTxn.getSourceMobile());
		processTxn.setCustomerName(mobileProfile.getCustomer().getLastName() + " " + mobileProfile.getCustomer().getFirstNames());
		// required to set transaction location id and type
		processTxn = this.processUtil.populateEquationAccoountsByTransactionType(processTxn);

		return processTxn;
	}

	public String validateAccountDeposit(String customerId, long value, String mobileNumber) throws Exception {
		Limit balanceLimit = null;
		BankAccount bankAccount = null;
		Date depositDate = new Date();
		String bankId = null;

		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		LimitServiceSOAPProxy limitService = new LimitServiceSOAPProxy();
		bankAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(customerId, BankAccountType.E_WALLET, OwnerType.CUSTOMER, mobileNumber);
		bankId = bankAccount.getBranch().getBank().getId();
		
		//Setting Bank Account Class to NONE
		bankAccount.setAccountClass(zw.co.esolutions.ewallet.bankservices.service.BankAccountClass.NONE);
		
		balanceLimit = limitService.getValidLimitOnDateByBankId(TransactionType.BALANCE, BankAccountClass.valueOf(bankAccount.getAccountClass().toString()), DateUtil.convertToXMLGregorianCalendar(depositDate), LimitPeriodType.TRANSACTION, bankId);

		if (balanceLimit.getMaxValue() < (value + bankAccount.getRunningBalance())) {
			return ResponseCode.E803.name(); // Deposit amount will exceed max
			// balance

		}
		return ResponseCode.E000.name();
	}

	public ProcessTransaction getProcessTransaction(NonHolderWithdrawalInfo info) throws Exception {
		ProcessTransaction txn;
		try {
			Query query = em.createNamedQuery("getProcessTransactionByTargetMobileAndAmount");
			// query.setParameter("status", info.getStatus());
			query.setParameter("targetMobileId", info.getTargetMobile());
			query.setParameter("amount", (long) info.getAmount());
			query.setParameter("secretCode", info.getSecretCode());
			query.setParameter("reference", info.getReference());
			txn = (ProcessTransaction) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
		return txn;
	}

	public ProcessResponse confirmNonHolderWithdrawal(String txnRefId, String profileId) {
		ProcessResponse res = new ProcessResponse();
		ProcessTransaction withdrawalTxn = null;
		try {
			ProcessTransaction txn = processUtil.getProcessTransactionByMessageId(txnRefId);
			CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
			MobileProfile sourceProfile = customerService.findMobileProfileById(txn.getSourceMobileId());

			LOG.debug("Creating the non holder txn for " + txn.getMessageId());
			 withdrawalTxn = this.createNonHolderWithdrawal(profileId, txn);
			LOG.debug("Non holder withdrawal " + withdrawalTxn.getMessageId() + " has been created for " + txn.getMessageId());

			BankRequest bankRequest = processUtil.populateBankRequest(withdrawalTxn);
			LOG.debug("Created bank request for non holder withdrawal.");
			


			this.sendBankRequestToBank(bankRequest);

			withdrawalTxn = processUtil.promoteTxnState(withdrawalTxn, TransactionStatus.BANK_REQUEST, withdrawalTxn.getNarrative());

			LOG.debug("Non holder withdrawal has been sent to bank");

			BankResponse bankResponse = processUtil.waitForBankResponse(bankRequest);

			ResponseCode response;

			if (ResponseCode.E000.equals(bankResponse.getResponseCode())) {
				LOG.debug("Post to book entry");
				response = processUtil.postNonHolderWithdrawal(withdrawalTxn); 
				// Process WIthdrawal Txn
				LOG.debug("Back from book entry");
				// notify customer if E000
				if (ResponseCode.E000.equals(response)) {
					this.notifyCustomerOfNonHolderWithdrawalSuccess(withdrawalTxn, bankResponse);
					withdrawalTxn = processUtil.promoteTxnState(withdrawalTxn, TransactionStatus.COMPLETED, bankResponse.getNarrative());
					txn = processUtil.promoteTxnState(txn, TransactionStatus.COMPLETED, "Cash collected. Withdrawal Reference " + withdrawalTxn.getMessageId());
				} else {
					processUtil.submitRequest(bankRequest, true);
					withdrawalTxn = processUtil.promoteTxnState(withdrawalTxn, TransactionStatus.REVERSAL_REQUEST, "Withdrawal failed.");
				}
				

			} else {
				//
				if (ResponseCode.E830.equals(bankResponse.getResponseCode())) { 
					// txn has timed out
					//processUtil.submitRequest(bankRequest, true);
					LOG.debug(">>>>>>>>>>>>>>>>>>>>>>withdrawal timeout >>>>>"+withdrawalTxn.getId());
					withdrawalTxn=this.markTxnAsTimedOut(withdrawalTxn, bankResponse);
				}else{
					withdrawalTxn = processUtil.promoteTxnState(withdrawalTxn, TransactionStatus.FAILED, withdrawalTxn.getNarrative());
				}
				response = bankResponse.getResponseCode();
				LOG.debug("NON HLD WDL Failed : " + response);
			}

			res.setAmount(withdrawalTxn.getAmount());
			res.setResponseCode(response.name());
			res.setMessageId(withdrawalTxn.getMessageId());
			LOG.debug("Return to teller");

			

		} catch (Exception e) {
			LOG.error("Exception thrown ", e);
			res.setResponseCode(ResponseCode.E842.name());
			if(withdrawalTxn != null) {
				try {
					withdrawalTxn = processUtil.promoteTxnState(withdrawalTxn, TransactionStatus.FAILED, ResponseCode.E842.getDescription());
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
			return res;
		}

		return res;

	}

	private void notifyCustomerOfNonHolderWithdrawalSuccess(ProcessTransaction txn, BankResponse bankResponse) {
		try {
			// send mobile response
			LOG.debug("Communicate to customers");

			RequestInfo requestInfo = processUtil.populateRequestInfo(txn);
			LOG.debug("Request info created " + requestInfo);
			ResponseInfo responseInfo = new ResponseInfo(bankResponse.getNarrative(), ResponseCode.E000, requestInfo, ResponseType.NOTIFICATION, 0L, 0L, 0L, txn.getMessageId());
			LOG.debug("Response info created " + responseInfo);
			LOG.debug("Sending NON HOLDER WITHDRWAWAL REPLY (to SWITCH) for transaction...." + txn.getMessageId());
			processUtil.submitResponse(responseInfo);
			LOG.debug("Done sending to switch");
		} catch (Exception e) {
			LOG.error(e.getMessage());
			LOG.debug("NOTIFY CUSTOMERS FAILED");
		}
	}

	private ProcessTransaction createNonHolderWithdrawal(String tellerId, ProcessTransaction txn) throws Exception {
		LOG.debug("in method create non holder withdrawal:::::::::::::::::::::::");
		ProcessTransaction withdrawalTxn = new ProcessTransaction();
		withdrawalTxn.setTransactionType(zw.co.esolutions.ewallet.enums.TransactionType.WITHDRAWAL_NONHOLDER);
		withdrawalTxn.setAmount(txn.getAmount());
		withdrawalTxn.setBankReference(txn.getBankReference());
		LOG.debug("Done setting bank ref, amt and txn type");
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		Profile profile = profileService.findProfileById(tellerId);
		if (profile == null || profile.getUserName() == null) {
			LOG.debug("Profile is null");
			throw new Exception("Invalid profile id " + tellerId);
		}

		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		BankBranch bankBranch = bankService.findBankBranchById(profile.getBranchId());

		if (bankBranch == null || bankBranch.getId() == null) {
			LOG.debug("Branch is null");
			throw new Exception("Invalid bank branch for teller " + profile.getUserName());
		}

		withdrawalTxn.setOldMessageId(txn.getMessageId());
		withdrawalTxn.setBranchId(profile.getBranchId());
		withdrawalTxn.setProfileId(tellerId);
		withdrawalTxn.setCustomerId(txn.getCustomerId());
		withdrawalTxn.setCustomerName(txn.getCustomerName());
		withdrawalTxn.setDateCreated(new Date(System.currentTimeMillis()));
		withdrawalTxn.setDestinationAccountNumber(txn.getDestinationAccountNumber());
		withdrawalTxn.setFromBankId(txn.getFromBankId());
		LOG.debug("Done setting customer info and bank info");

		ReferenceGeneratorServiceSOAPProxy referenceGeneratorService = new ReferenceGeneratorServiceSOAPProxy();
		String year = Formats.yearFormatTwoDigit.format(new Date(System.currentTimeMillis()));
		String id = referenceGeneratorService.generateUUID(EWalletConstants.SEQUENCE_NAME_WITHDRAWAL, EWalletConstants.SEQUENCE_PREFIX_WITHDRAWAL, year, 0, 10000000000L - 1L);
		LOG.debug(">>>>>>>>>>>>>>>>>>generated>>id>>>>>>>>" + id);
		withdrawalTxn.setId(id);
		withdrawalTxn.setMessageId(id);
		LOG.debug(">>>>>>>>>>>>>>>>>>generated>>id>>>>>>>>" + withdrawalTxn.getId());

		LOG.debug("Done setting ref ID " + withdrawalTxn.getMessageId());

		withdrawalTxn.setOldMessageId(txn.getId());
		withdrawalTxn.setNarrative("Withdrawal Non Holder");
		withdrawalTxn.setSourceAccountNumber(txn.getSourceAccountNumber());
		
		withdrawalTxn.setSourceMobile(txn.getSourceMobile());
		withdrawalTxn.setSourceMobileId(txn.getSourceMobileId());
		withdrawalTxn.setStatus(TransactionStatus.DRAFT);
		withdrawalTxn.setTargetMobile(txn.getTargetMobile());
		withdrawalTxn.setToBankId(bankBranch.getBank().getId());
		withdrawalTxn.setTransactionLocationId(profile.getBranchId());
		withdrawalTxn.setTransactionLocationType(TransactionLocationType.BANK_BRANCH);
		LOG.debug("Done setting additional info");
		withdrawalTxn = this.processUtil.populateEquationAccoountsByTransactionType(withdrawalTxn);
		
		withdrawalTxn = processUtil.createProcessTransaction(withdrawalTxn);
		LOG.debug("Done creating in the db");
		withdrawalTxn = processUtil.promoteTxnState(withdrawalTxn, TransactionStatus.DRAFT, "WDL Non Holder");
		LOG.debug("DONE creating the WDL NHL txn");
		return withdrawalTxn;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProcessTransaction> getProcessTransactionsWithinDateRange(Date fromDate, Date toDate, TransactionStatus status) {
		fromDate = DateUtil.getBeginningOfDay(fromDate);
		toDate = DateUtil.getEndOfDay(toDate);
		List<ProcessTransaction> results = null;
		try {
			Query query = em.createNamedQuery("getBankProcessTransactionsWithinDateRange");
			query.setParameter("fromDate", fromDate);
			query.setParameter("toDate", toDate);
			// query.setParameter("status", status);
			results = (List<ProcessTransaction>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (results != null) {
			if (results.isEmpty()) {
				results = null;
			}
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProcessTransaction> getProcessTransactionsWithinDateRangeByBankId(Date fromDate, Date toDate, String bankId, TransactionStatus status) {
		fromDate = DateUtil.getBeginningOfDay(fromDate);
		toDate = DateUtil.getEndOfDay(toDate);
		List<ProcessTransaction> results = null;
		try {
			Query query = em.createNamedQuery("getBankProcessTransactionsWithinDateRangeByBankId");
			query.setParameter("fromDate", fromDate);
			query.setParameter("toDate", toDate);
			// query.setParameter("status", status);
			query.setParameter("bankId", bankId);
			results = (List<ProcessTransaction>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (results != null) {
			if (results.isEmpty()) {
				results = null;
			}
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProcessTransaction> getProcessTransactionsWithinDateRangeByBankIdAndBranchId(Date fromDate, Date toDate, String bankId, String branchId, TransactionStatus status) {
		fromDate = DateUtil.getBeginningOfDay(fromDate);
		toDate = DateUtil.getEndOfDay(toDate);
		List<ProcessTransaction> results = null;
		try {
			Query query = em.createNamedQuery("getBankProcessTransactionsWithinDateRangeByBankIdAndBranchId");
			query.setParameter("fromDate", fromDate);
			query.setParameter("toDate", toDate);
			// query.setParameter("status", status);
			query.setParameter("bankId", bankId);
			query.setParameter("branchId", branchId);
			results = (List<ProcessTransaction>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (results != null) {
			if (results.isEmpty()) {
				results = null;
			}
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProcessTransaction> getProcessTransactionsWithinDateRangeByMsgType(Date fromDate, Date toDate, zw.co.esolutions.ewallet.enums.TransactionType msgType, TransactionStatus status) {
		fromDate = DateUtil.getBeginningOfDay(fromDate);
		toDate = DateUtil.getEndOfDay(toDate);
		List<ProcessTransaction> results = null;
		try {
			Query query = em.createNamedQuery("getBankProcessTransactionsWithinDateRangeByMsgType");
			query.setParameter("fromDate", fromDate);
			query.setParameter("toDate", toDate);
			// query.setParameter("status", status);
			query.setParameter("transactionType", msgType);
			results = (List<ProcessTransaction>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (results != null) {
			if (results.isEmpty()) {
				results = null;
			}
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProcessTransaction> getProcessTransactionsWithinDateRangeByMsgTypeByBankId(Date fromDate, Date toDate, zw.co.esolutions.ewallet.enums.TransactionType msgType, String bankId, TransactionStatus status) {
		fromDate = DateUtil.getBeginningOfDay(fromDate);
		toDate = DateUtil.getEndOfDay(toDate);
		List<ProcessTransaction> results = null;
		try {
			Query query = em.createNamedQuery("getBankProcessTransactionsWithinDateRangeByMsgTypeByBankId");
			query.setParameter("fromDate", fromDate);
			query.setParameter("toDate", toDate);
			// query.setParameter("status", status);
			query.setParameter("transactionType", msgType);
			query.setParameter("bankId", bankId);
			results = (List<ProcessTransaction>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (results != null) {
			if (results.isEmpty()) {
				results = null;
			}
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProcessTransaction> getProcessTransactionsWithinDateRangeByMsgTypeByBankIdAndBranchId(Date fromDate, Date toDate, zw.co.esolutions.ewallet.enums.TransactionType msgType, String bankId, String branchId, TransactionStatus status) {
		fromDate = DateUtil.getBeginningOfDay(fromDate);
		toDate = DateUtil.getEndOfDay(toDate);
		List<ProcessTransaction> results = null;
		try {
			// getBankProcessTransactionsWithinDateRangeByMsgTypeByBankIdAndBranchId
			Query query = em.createNamedQuery("getBankProcessTransactionsWithinDateRangeByMsgTypeByBankIdAndBranchId");
			query.setParameter("fromDate", fromDate);
			query.setParameter("toDate", toDate);
			// query.setParameter("status", status);
			query.setParameter("transactionType", msgType);
			query.setParameter("bankId", bankId);
			query.setParameter("branchId", branchId);
			results = (List<ProcessTransaction>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (results != null) {
			if (results.isEmpty()) {
				results = null;
			}
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProcessTransaction> getTellerDayEnd(String bankName, String profileId, Date txnDate, zw.co.esolutions.ewallet.enums.TransactionType msgType) {
		List<ProcessTransaction> results = null;
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		try {
			Bank bank = bankService.findBankBranchById(profileService.findProfileById(profileId).getBranchId()).getBank();
			Query query = em.createNamedQuery("getTellerDailyTransactions");
			query.setParameter("profileId", profileId);
			query.setParameter("fromDate", DateUtil.getBeginningOfDay(txnDate));
			query.setParameter("toDate", DateUtil.getEndOfDay(txnDate));
			query.setParameter("transactionType", msgType);
			results = (List<ProcessTransaction>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (results == null || results.isEmpty()) {
			results = null;
		}
		return results;
	}

	@Override
	public long getTellerDayEndTotal(String bankName, String profileId, Date txnDate, zw.co.esolutions.ewallet.enums.TransactionType msgType) {
		long total = 0L;
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();

		try {

			Bank bank = bankService.findBankBranchById(profileService.findProfileById(profileId).getBranchId()).getBank();
			Query query = em.createNamedQuery("getTellerTransactionTotal");
			query.setParameter("profileId", profileId);
			// query.setParameter("bankId", bank.getId());
			query.setParameter("fromDate", DateUtil.getBeginningOfDay(txnDate));
			query.setParameter("toDate", DateUtil.getEndOfDay(txnDate));
			query.setParameter("transactionType", msgType);
			// query.setParameter("status", TransactionStatus.COMPLETED);
			Long value = (Long) query.getSingleResult();
			if (value != null) {
				total = value.longValue();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}

		return total;
	}

	public DayEndResponse isPreviousDayEndRun(String profileId, Date dayEndDate) {
		DayEndResponse response = new DayEndResponse();
		LOG.debug("checking if any txn nt run");
		List<ProcessTransaction> transactions = getTransactionByTellerIdAndNullDayEndSummary(profileId, dayEndDate);
		LOG.debug(" returned object value  " + transactions);
		if (transactions != null) {
			LOG.debug(";;;;;;" + transactions.size());
		}
		if (transactions != null && transactions.size() > 0) {
			// There are transactions that have not had a day endRun
			ProcessTransaction txn = transactions.get(0);
			response.setDayEndDate(txn.getDateCreated());
			response.setStatus(true);
		}
		LOG.debug("day end date    " + response.getDayEndDate());
		return response;
	}

	public DayEndResponse checkIfThereAreTrxnTomark(String profileId, Date dayEndDate) throws Exception {
		DayEndResponse response = new DayEndResponse();
		LOG.debug("checking if any txn nt run");
		List<ProcessTransaction> transactions = getTransactionByTellerIdAndNullDayEndSummaryDayEndDate(profileId, dayEndDate);
		LOG.debug(" returned object value  " + transactions);
		if (transactions != null) {
			LOG.debug(";;;;;;" + transactions.size());
			for (ProcessTransaction result : transactions) {
				LOG.debug(" Amount  " + result.getAmount() + "   date created " + result.getDateCreated() + " Txn Type " + result.getTransactionType());

			}
		}
		if (transactions != null && transactions.size() > 0) {
			// There are transactions that have not had a day endRun
			ProcessTransaction txn = transactions.get(0);
			response.setDayEndDate(txn.getDateCreated());
			response.setStatus(true);
		}
		LOG.debug("day end date    " + response.getDayEndDate());
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<ProcessTransaction> getTransactionByTellerIdAndNullDayEndSummaryDayEndDate(String tellerId, Date dayEndDate) throws Exception {
		ProfileServiceSOAPProxy profilProxy = new ProfileServiceSOAPProxy();
		Profile profile = profilProxy.getProfileByUserName(tellerId);
		tellerId = profile.getId();
		List<ProcessTransaction> results = null;
		try {
			LOG.debug("Teller id       " + tellerId);
			LOG.debug(" dayend date  ::::::" + dayEndDate);
			Query query = em.createNamedQuery("getTransactionByTellerIdAndNullDayEndSummaryDayEndDate");
			LOG.debug("query string   " + query.toString());

			query.setParameter("tellerId", tellerId);
			query.setParameter("fromDate", DateUtil.getBeginningOfDay(dayEndDate));
			query.setParameter("toDate", DateUtil.getEndOfDay(dayEndDate));
			query.setParameter("statusBankReq", TransactionStatus.BANK_REQUEST);
			query.setParameter("statusManResolve", TransactionStatus.MANUAL_RESOLVE);
			query.setParameter("statusCompl", TransactionStatus.COMPLETED);
			// query.setParameter("statusAWT",
			// TransactionStatus.AWAITING_APPROVAL);

			results = (List<ProcessTransaction>) query.getResultList();
			LOG.debug("eerrr  logicresults value   " + results);
			if (results != null) {
				LOG.debug("   number of txnlllll" + results.size());
				for (ProcessTransaction result : results) {
					LOG.debug(" Amount  " + result.getAmount() + "   date created " + result.getDateCreated() + " Txn Type " + result.getTransactionType());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (results == null || results.isEmpty()) {
			results = null;
		}
		return results;
	}

	public ProcessResponse processEwalletWithdrawal(RequestInfo info) {
		Tariff tariff;
		ProcessResponse res = new ProcessResponse();
		ProcessTransaction txn = null;
		MobileProfile mobileProfile;
		ReferenceGeneratorServiceSOAPProxy refProxy = new ReferenceGeneratorServiceSOAPProxy();
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		try {
			LOG.debug(">>>>>>>>>>>>>>> Now in in initialization Withdraws");
			String year = Formats.yearFormatTwoDigit.format(new Date(System.currentTimeMillis()));
			info.setMessageId(refProxy.generateUUID(info.getTransactionType().name(), "W", year, 0, 1000000000L - 1L));
			txn = this.processUtil.populateProcessTransaction(info);
			txn.setStatus(TransactionStatus.DRAFT);
			txn.setNarrative("Cash Withrawal.");
			// Mobile Profile
			mobileProfile = customerService.findMobileProfileById(txn.getSourceMobileId());

			Profile profile = profileService.findProfileById(info.getProfileId());
			txn.setBranchId(profile.getBranchId());

			txn.setSourceAccountNumber(mobileProfile.getMobileNumber());
			txn.setDestinationAccountNumber(mobileProfile.getMobileNumber());

			txn = this.processUtil.createProcessTransaction(txn);

			boolean chargeCustomer = true;
			// check first auto-reg withdrawal
			if (CustomerAutoRegStatus.YES.equals(mobileProfile.getCustomer().getCustomerAutoRegStatus())) {
				
				LOG.debug("Customer is in AuroRegStatus.YES... dont charge him.");
				chargeCustomer = false;
				
			} else if (CustomerAutoRegStatus.NO.equals(mobileProfile.getCustomer().getCustomerAutoRegStatus())) {
			
				LOG.debug("Customer is in AuroRegStatus.NO ... check 1st withdrawal..");
				UniversalProcessSearch pojo = new UniversalProcessSearch();
				pojo.setCustomerId(mobileProfile.getCustomer().getId());
				pojo.setTxnType(zw.co.esolutions.ewallet.enums.TransactionType.WITHDRAWAL);
				pojo.setStatus(TransactionStatus.COMPLETED);

				List<ProcessTransaction> withdrawalTxns = this.getProcessTransactionsByAllAttributes(pojo);

				if (withdrawalTxns == null || withdrawalTxns.isEmpty()) {
					chargeCustomer = false;
					// There are no withdrawals done, don't charge customer
					LOG.debug("#####            Customer was auto-reg, no withdrawals found, therefore no charge");

				} else {
					chargeCustomer = true;
					// charge customer
					LOG.debug("#####            Customer was auto-reg, withdrawals were found, charge this customer");

				}

			}

			if(chargeCustomer) {
				txn = this.processUtil.addApplicableTariffToProcessTxn(txn);
				LOG.debug("Done adding trariff as transaction charge : ");
				List<TransactionCharge> charges = txn.getTransactionCharges();
				if (charges != null && charges.size() > 0) {
					for (TransactionCharge charge : charges) {
						LOG.debug("tarrif amount>>>>>>>>>>>>" + charge.getTariffAmount());
						LOG.debug("tarrif class>>>>>>>>>>>>" + charge.getChargeTransactionClass());

					}
				}
			}
			txn = this.processUtil.promoteTxnState(txn, TransactionStatus.DRAFT, TransactionStatus.DRAFT.name());

			// Populating Bank Req
			BankRequest bankReq = this.processUtil.populateBankRequest(txn);

		
			
			this.sendBankRequestToBank(bankReq);

			txn = this.processUtil.promoteTxnState(txn, TransactionStatus.BANK_REQUEST, TransactionStatus.BANK_REQUEST.name());

			BankResponse bankResponse = processUtil.waitForBankResponse(bankReq);

			ResponseCode response;

			if (ResponseCode.E000.equals(bankResponse.getResponseCode())) {

				// post to e-wallet accounts
				response = this.processUtil.postEWalletWithdrawal(txn);

			} else {
				if (ResponseCode.E830.equals(bankResponse.getResponseCode())) { 
					// txn has timed out
					//processUtil.submitRequest(bankReq, true);
					LOG.debug(">>>>>>>>>>>>>>>>ewallet withdrawal>>>>>>>>>>>"+txn.getId());
					txn = this.markTxnAsTimedOut(txn, bankResponse);

				}

				else{
				txn = this.processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResponse.getNarrative());

				}
				response = bankResponse.getResponseCode();

			}

			res.setResponseCode(response.name());
			res.setAmount(txn.getAmount());
			res.setBalance(txn.getBalance());
			res.setMessageId(txn.getMessageId());

			// notify customer if E000
			if (ResponseCode.E000.equals(response)) {
				txn = processUtil.promoteTxnState(txn, TransactionStatus.COMPLETED, bankResponse.getNarrative());
				this.notifyCustomerOfWithdrawalSuccess(txn, bankResponse);
			}

		} catch (Exception e) {
			e.printStackTrace(System.err);
			res.setResponseCode(ResponseCode.E841.name());
			try {
				if(txn != null) {
					txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, ResponseCode.E841.getDescription());
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
			return res;
		}

		return res;
	}

	private void notifyCustomerOfWithdrawalSuccess(ProcessTransaction txn, BankResponse bankResp) {
		try {
			RequestInfo requestInfo = processUtil.populateRequestInfo(txn);
			requestInfo.setTargetMobile(requestInfo.getSourceMobile());
			BankAccount bankAccount = new BankServiceSOAPProxy().getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getCustomerId(), BankAccountType.E_WALLET, OwnerType.CUSTOMER, txn.getSourceMobile());
			ResponseInfo responseInfo = new ResponseInfo(bankResp.getNarrative(), ResponseCode.E000, requestInfo, ResponseType.NOTIFICATION, bankAccount.getRunningBalance(), bankAccount.getRunningBalance(), 0L, txn.getMessageId());
			LOG.debug("Sending WITHDRAWAL REPLY (to SWITCH) for transaction...." + txn.getMessageId());
			processUtil.submitResponse(responseInfo);
		} catch (Exception e) {
			LOG.debug("NOTIFY CUSTOMER FAILED");
		}
	}

	public String validateEwalletHolderWithdrawal(String sourceMobileId, long amount, String bankId, TransactionLocationType locationType) throws Exception {

		// Local variables to be used
		Date txnDate = new Date();
		MobileProfile sourceProfile;
		MobileProfile destnProfile;
		Limit limit;
		Limit balanceLimit;
		BankAccount bankAccount;
		long tariffAmount;
		Tariff tariff;

		try {
			CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
			ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
			LimitServiceSOAPProxy limitService = new LimitServiceSOAPProxy();

			// Retrieve Approppriate Profiles
			sourceProfile = customerService.findMobileProfileById(sourceMobileId);

			// Retrieve Source & Destination Accounts Details
			bankAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(sourceProfile.getCustomer().getId(), BankAccountType.E_WALLET, OwnerType.CUSTOMER, sourceProfile.getMobileNumber());

			//Setting Bank Account Class to NONE
			bankAccount.setAccountClass(zw.co.esolutions.ewallet.bankservices.service.BankAccountClass.NONE);
			
			// Retrieve Source & Destination Limits
			limit = limitService.getValidLimitOnDateByBankId(TransactionType.WITHDRAWAL, BankAccountClass.valueOf(bankAccount.getAccountClass().toString()), DateUtil.convertToXMLGregorianCalendar(txnDate), LimitPeriodType.TRANSACTION, bankId);

			balanceLimit = limitService.getValidLimitOnDateByBankId(TransactionType.BALANCE, BankAccountClass.valueOf(bankAccount.getAccountClass().toString()), DateUtil.convertToXMLGregorianCalendar(txnDate), LimitPeriodType.TRANSACTION, bankId);

			if (balanceLimit == null || balanceLimit.getId() == null) {
				balanceLimit = limitService.getValidLimitOnDateByBankId(TransactionType.BALANCE, BankAccountClass.valueOf(bankAccount.getAccountClass().toString()), DateUtil.convertToXMLGregorianCalendar(txnDate), LimitPeriodType.DAILY, bankId);
			}
			
			if ((limit == null || limit.getId() == null) || (balanceLimit == null || balanceLimit.getId() == null)) {
				return ResponseCode.E823.name();
			}

			// Check for withdrawal eligibility
			if (limit.getMinValue() > amount) {
				return ResponseCode.E804.name(); // Withdrawal amount is below
													// min
				// withdrawal

			}
			LOG.debug(">>>>>>>>>>>>>>>>>>>>. Max = " + limit.getMaxValue() + " >>>>>>>>>>>>>>>>> Value = " + amount);
			if (limit.getMaxValue() < amount) {
				return ResponseCode.E805.name(); // Withdrawal amount is above
													// max
				// withdrawal

			}

			// Applying Tariffs Now
			// Limits are Ok then look for a tariff
			tariff = this.getApplicableTariff(bankId, CustomerClass.valueOf(sourceProfile.getCustomer().getCustomerClass().toString()), zw.co.esolutions.ewallet.tariffservices.service.TransactionType.WITHDRAWAL, locationType, amount);

			if (tariff == null || tariff.getId() == null) {
				return ResponseCode.E822.name();
			}
			// Retrieve Tariff value
			tariffAmount = this.processUtil.getTariffAmount(tariff, amount);

			if (0 > (bankAccount.getRunningBalance() - (amount + tariffAmount))) {
				return ResponseCode.E808.name(); // No funds in account.
			}
			if ((bankAccount.getRunningBalance() - (amount + tariffAmount)) < balanceLimit.getMinValue()) {

				if ((bankAccount.getRunningBalance() - balanceLimit.getMinValue()) >= balanceLimit.getMinValue() && (bankAccount.getRunningBalance() - balanceLimit.getMinValue()) >= limit.getMinValue()) {
					return ResponseCode.E810.name(); // Withdrawal amount is
														// higher
					// than the expected one
				} else {
					return ResponseCode.E801.name();// Insufficient funds to
					// withdrawal
				}

			}
		} catch (Exception e) {
			LOG.debug(">>>>>>>>>>>>>>>>>>>>>>> In Process Service ::: ");
			e.printStackTrace();
			throw e;
		}

		return ResponseCode.E000.name();

	}

	public String validateDeposit(String sourceMobileId, long amount, String bankId, TransactionLocationType locationType) throws Exception {

		// Local variables to be used
		Date txnDate = new Date();
		MobileProfile sourceProfile;
		MobileProfile destnProfile;
		Limit limit;
		Limit dailyLimit;
		Limit balanceLimit;
		BankAccount bankAccount;
		long tariffAmount;
		Tariff tariff;
		long dailyAmount;

		try {
			CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
			ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
			LimitServiceSOAPProxy limitService = new LimitServiceSOAPProxy();

			// Retrieve Approppriate Profiles
			sourceProfile = customerService.findMobileProfileById(sourceMobileId);

			dailyAmount = this.getDailyAmountByCustomerIdAndTxnType(sourceProfile.getCustomer().getId(), zw.co.esolutions.ewallet.enums.TransactionType.DEPOSIT);

			// Retrieve Source & Destination Accounts Details
			bankAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(sourceProfile.getCustomer().getId(), BankAccountType.E_WALLET, OwnerType.CUSTOMER, sourceProfile.getMobileNumber());

			//Setting Bank Account Class to NONE
			bankAccount.setAccountClass(zw.co.esolutions.ewallet.bankservices.service.BankAccountClass.NONE);
			
			// Retrieve Source & Destination Limits
			limit = limitService.getValidLimitOnDateByBankId(TransactionType.DEPOSIT, BankAccountClass.valueOf(bankAccount.getAccountClass().toString()), DateUtil.convertToXMLGregorianCalendar(txnDate), LimitPeriodType.TRANSACTION, bankId);

			if ((limit == null) || (limit.getId() == null)) {
				return ResponseCode.E823.getDescription();
			}

			/*
			 * // Retrieve Source & Destination Limits dailyLimit =
			 * limitService.getValidLimitOnDateByBankId(TransactionType.DEPOSIT,
			 * BankAccountClass
			 * .valueOf(bankAccount.getAccountClass().toString()),
			 * DateUtil.convertToXMLGregorianCalendar(txnDate),
			 * LimitPeriodType.DAILY, bankId);
			 * 
			 * if(dailyLimit == null) { return
			 * ResponseCode.E826.getDescription(); // No daily Limit }
			 */

			balanceLimit = limitService.getValidLimitOnDateByBankId(TransactionType.BALANCE, BankAccountClass.valueOf(bankAccount.getAccountClass().toString()), DateUtil.convertToXMLGregorianCalendar(txnDate), LimitPeriodType.TRANSACTION, bankId);

			if (balanceLimit == null || balanceLimit.getId() == null) {
				balanceLimit = limitService.getValidLimitOnDateByBankId(TransactionType.BALANCE, BankAccountClass.valueOf(bankAccount.getAccountClass().toString()), DateUtil.convertToXMLGregorianCalendar(txnDate), LimitPeriodType.DAILY, bankId);
			}
			
			if ((limit == null || limit.getId() == null) || (balanceLimit == null || balanceLimit.getId() == null)) {
				return ResponseCode.E823.getDescription();
			}

			/*
			 * if(dailyLimit.getMaxValue() < dailyAmount + amount) { return
			 * ResponseCode
			 * .E829.getDescription()+" Maximum daily limit is "+MoneyUtil
			 * .convertCentsToDollarsPatternNoCurrency
			 * (dailyLimit.getMaxValue())+ " and you transacted " +
			 * ""+MoneyUtil.
			 * convertCentsToDollarsPatternNoCurrency(dailyAmount)+"."; }
			 */

			// Check for Deposit eligibility
			if (limit.getMinValue() > amount) {
				return ResponseCode.E824.getDescription() + " Minimum deposit is " + MoneyUtil.convertCentsToDollarsPatternNoCurrency(limit.getMinValue()) + "."; // Deposit
																																									// amount
																																									// is
																																									// below
				// min
				// Deposit

			}
			LOG.debug(">>>>>>>>>>>>>>>>>>>>. Max = " + limit.getMaxValue() + " >>>>>>>>>>>>>>>>> Value = " + amount);
			if (limit.getMaxValue() < amount) {
				return ResponseCode.E805.getDescription() + " Maximum deposit allowed is " + MoneyUtil.convertCentsToDollarsPatternNoCurrency(limit.getMaxValue()) + "."; // Deposit
																																											// amount
																																											// is
																																											// above
				// max
				// Deposit

			}

			// Applying Tariffs Now
			// Limits are Ok then look for a tariff
			tariff = this.getApplicableTariff(bankId, CustomerClass.valueOf(sourceProfile.getCustomer().getCustomerClass().toString()), zw.co.esolutions.ewallet.tariffservices.service.TransactionType.DEPOSIT, locationType, amount);

			if ((tariff == null) || (tariff.getId() == null)) {
				return ResponseCode.E822.getDescription();
			}

		} catch (Exception e) {
			LOG.debug(">>>>>>>>>>>>>>>>>>>>>>> In Process Service ::: ");
			e.printStackTrace();
			throw e;
		}

		return ResponseCode.E000.name();

	}

	public Tariff getApplicableTariff(String bankId, CustomerClass customerClass, zw.co.esolutions.ewallet.tariffservices.service.TransactionType transactionType, TransactionLocationType locationType, long amount) throws Exception {
		TariffServiceSOAPProxy tariffService = new TariffServiceSOAPProxy();
		Tariff tariff;
		if (TransactionLocationType.ATM.equals(locationType) || TransactionLocationType.SMS.equals(locationType)) {
			locationType = TransactionLocationType.BANK_BRANCH;
		}
		LOG.debug("Looking for tariff>>>>>>>>>>>>>>>>>>>>");
		LOG.debug("Custo Class : " + customerClass + " BankId : " + bankId + " TXN TYPE " + transactionType + " AGNT TYPE : " + locationType + " Amt : " + amount);
		tariff = tariffService.retrieveAppropriateTariff(customerClass, transactionType, AgentType.valueOf(locationType.toString()), amount, bankId);

		LOG.debug("Tariff FOUND : " + tariff);

		return tariff;
	}

	public long getTariffAmount(String bankId, CustomerClass customerClass, zw.co.esolutions.ewallet.tariffservices.service.TransactionType transactionType, TransactionLocationType locationType, long amount) throws Exception {
		TariffServiceSOAPProxy tariffService = new TariffServiceSOAPProxy();
		Tariff tariff;
		long value;
		if (TransactionLocationType.ATM.equals(locationType) || TransactionLocationType.SMS.equals(locationType)) {
			locationType = TransactionLocationType.BANK_BRANCH;
		}
		LOG.debug("Looking for tariff>>>>>>>>>>>>>>>>>>>>");
		tariff = tariffService.retrieveAppropriateTariff(customerClass, transactionType, AgentType.valueOf(locationType.toString()), amount, bankId);

		LOG.debug("Tariff FOUND : " + tariff);

		value = this.processUtil.getTariffAmount(tariff, amount);

		return value;
	}

	

	public ProcessTransaction getProcessTransactionByMessageId(String messageId) throws Exception {
		return this.processUtil.getProcessTransactionByMessageId(messageId);
	}

	public DayEnd createDayEnd(DayEnd dayEnd, String userName) throws Exception {
		return dayEndBean.createDayEnd(dayEnd, userName);

	}

	public DayEnd findDayEndById(String dayEndId, String userName) {
		return dayEndBean.findDayEndById(dayEndId, userName);

	}

	public List<ProcessTransaction> getProcessTransactionsByBranch(String branchId) {
		List<ProcessTransaction> results = null;

		try {

			Query query = em.createNamedQuery("getProcessTransactionsByBranch");
			query.setParameter("branchId", branchId);
			results = (List<ProcessTransaction>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (results == null || results.isEmpty()) {
			results = null;
		}
		return results;

	}

	public List<ProcessTransaction> getProcessTransactionsByTellerIdAndDayEndSummary(String tellerId, String dayEndSummaryId) {
		List<ProcessTransaction> results = null;
		try {
			Query query = em.createNamedQuery("getProcessTransactionsByTellerIdAndDayEndSummary");
			query.setParameter("tellerId", tellerId);
			query.setParameter("dayEndSummaryId", dayEndSummaryId);
			results = (List<ProcessTransaction>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (results == null || results.isEmpty()) {
			results = null;
		}
		return results;

	}

	public List<ProcessTransaction> getProcessTransactionByTellerIdAndDateRangeAndStatus(Date fromDate, Date toDate, String tellerId, TransactionStatus tranStatus) {
		List<ProcessTransaction> results = null;

		try {

			Query query = em.createNamedQuery("getTransactionsByTellerIdDateRangeAndTransactionType");
			query.setParameter("tellerId", tellerId);
			query.setParameter("fromDate", DateUtil.getBeginningOfDay(fromDate));
			query.setParameter("toDate", DateUtil.getEndOfDay(toDate));
			query.setParameter("status", tranStatus);
			results = (List<ProcessTransaction>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (results == null || results.isEmpty()) {
			results = null;
		}
		return results;

	}

	public List<DayEnd> getDayEndByTellerIdAndDateCreatedAndStatus(String tellerId, Date dateCreated, TransactionStatus tranStatus) {
		List<DayEnd> results = null;
		try {
			Query query = em.createNamedQuery("getDayEndByTellerIdAndDateCreatedAndStatus");
			query.setParameter("tellerId", tellerId);
			query.setParameter("dateCreated", dateCreated);
			query.setParameter("status", tranStatus);
			results = (List<DayEnd>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (results == null || results.isEmpty()) {
			results = null;
		}
		return results;

	}

	public List<ProcessTransaction> getPocessTransactionsByDateRangeAndStatus(Date fromDate, Date toDate, TransactionStatus tranStatus) {
		List<ProcessTransaction> results = null;
		try {
			Query query = em.createNamedQuery("getPocessTransactionsByDateRangeAndStatus");
			query.setParameter("fromDate", DateUtil.getBeginningOfDay(fromDate));
			query.setParameter("toDate", DateUtil.getEndOfDay(toDate));
			query.setParameter("status", tranStatus);
			results = (List<ProcessTransaction>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (results == null || results.isEmpty()) {
			results = null;
		}
		return results;

	}

	public List<ProcessTransaction> getProcessTransactionsByTellerIdAndDateRange(Date fromDate, Date toDate, String tellerId) {
		List<ProcessTransaction> results = null;
		try {
			Query query = em.createNamedQuery("getProcessTransactionsByTellerIdAndDateRange");
			query.setParameter("fromDate", DateUtil.getBeginningOfDay(fromDate));
			query.setParameter("toDate", DateUtil.getEndOfDay(toDate));
			query.setParameter("tellerId", tellerId);
			results = (List<ProcessTransaction>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (results == null || results.isEmpty()) {
			results = null;
		}
		return results;

	}

	public DayEndSummary findDayEndSummary(String id, String userName) {
		return dayEndBean.findDayEndSummary(id, userName);

	}

	public List<ProcessTransaction> getProcessTransactionsByBranchAndDateRangeAndStatus(Date fromDate, Date toDate, String branchId, TransactionStatus tranStatus) {
		List<ProcessTransaction> results = null;
		try {
			Query query = em.createNamedQuery("getProcessTransactionsByBranchAndDateRangeAndStatus");
			query.setParameter("fromDate", DateUtil.getBeginningOfDay(fromDate));
			query.setParameter("toDate", DateUtil.getEndOfDay(toDate));
			query.setParameter("branchId", branchId);
			query.setParameter("status", tranStatus);
			results = (List<ProcessTransaction>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (results == null || results.isEmpty()) {
			results = null;
		}
		return results;

	}

	public DayEndSummary createDayEndSummary(DayEndSummary summary, String userName) throws Exception {
		return dayEndBean.createDayEndSummary(summary, userName);

	}

	public List<ProcessTransaction> getProcessTransactionsByTellerID(String tellerId) {
		List<ProcessTransaction> results = null;
		try {
			Query query = em.createNamedQuery("getProcessTransactionsByTellerID");

			query.setParameter("tellerId", tellerId);

			results = (List<ProcessTransaction>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (results == null || results.isEmpty()) {
			results = null;
		}
		return results;

	}

	public DayEnd editDayEnd(DayEnd dayEnd, String userName) throws Exception {
		return dayEndBean.editDayEnd(dayEnd, userName);

	}

	public List<ProcessTransaction> getProcessTransactionsByTransactionStatus(TransactionStatus tranStatus) {

		List<ProcessTransaction> results = null;
		try {
			Query query = em.createNamedQuery("getProcessTransactionsByTransactionStatus");

			query.setParameter("status", tranStatus);

			results = (List<ProcessTransaction>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (results == null || results.isEmpty()) {
			results = null;
		}
		return results;

	}

	public DayEndSummary editDayEndSummary(DayEndSummary summary, String userName) throws Exception {
		return dayEndBean.editDayEndSummary(summary, userName);

	}

	@Override
	public DayEnd runTellerDayEnd(DayEnd dayEnd, String userName) throws Exception {
		List<zw.co.esolutions.ewallet.enums.TransactionType> endDayTransList = getDayEndList();
		List<DayEndSummary> summaryList = getSummariesList(endDayTransList);

		LOG.debug("starting day end");

		if (true) {
			LOG.debug("nw running");
			// Proceed to create DayEnd Object
			dayEnd.setStatus(DayEndStatus.AWAITING_APPROVAL);
			// /ProfileServiceSOAPProxy proxy=new ProfileServiceSOAPProxy();
			// Profile tellerProfile=proxy.findProfileById(tellerId);
			// String branchId=tellerProfile.getBranchId();
			// dayEnd.setBranchId(branchId);
			dayEnd = dayEndBean.createDayEnd(dayEnd, userName);
			LOG.debug("day end created");
			/*
			 * 
			 * Creating DayEnd summaries
			 */
			summaryList = persistSummaries(summaryList, dayEnd, userName);
			LOG.debug("Summaries persited " + summaryList.size());
			// generate DayEnd Summaries
			/*
			 * Marking processtransactions by type and calculating totals
			 */
			setSummariesOnTransactions(summaryList, userName);
			LOG.debug("transactions marked");

		}
		LOG.debug("Operation complete " + dayEnd.getDayEndId());
		return dayEnd;
	}

	private List<DayEndSummary> persistSummaries(List<DayEndSummary> summaryList, DayEnd dayEnd, String userName) throws Exception {
		List<DayEndSummary> summaries = new ArrayList<DayEndSummary>();
		int count = 0;
		for (DayEndSummary summary : summaryList) {
			++count;
			summary.setDayEnd(dayEnd);
			summary = createDayEndSummary(summary, userName);
			summaries.add(summary);
			LOG.debug("Count of summaries   " + count + "    Summary Type " + summary.getTransactionType());
		}
		LOG.debug("Count of summaries   " + count);
		return summaries;
	}

	private void setSummariesOnTransactions(List<DayEndSummary> summaryList, String userName) {
		long trxnNumber = 0;
		long txnValue = 0;
		try {
			for (DayEndSummary summary : summaryList) {
				zw.co.esolutions.ewallet.enums.TransactionType txnType = summary.getTransactionType();
				// List<ProcessTransaction>
				// trxnList=getPocessTransactionsByDateRangeAndStatus(new
				// Date(), new Date(), txnType);
				DayEnd dayEnd = summary.getDayEnd();
				Date dayEndDate = dayEnd.getDayEndDate();
				String tellerId = dayEnd.getTellerId();
				trxnNumber = updateTransactions(dayEndDate, txnType, summary, tellerId);
				txnValue = calculateValueOfTransactions(txnType, summary.getId());
				LOG.debug("TxnType       " + txnType.name());
				LOG.debug("Number of transactions     " + trxnNumber);
				LOG.debug("Value of transactions     " + txnValue);
				summary.setNumberOfTxn(trxnNumber);
				summary.setValueOfTxns(txnValue);
				dayEndBean.editDayEndSummary(summary, userName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private long calculateValueOfTransactions(zw.co.esolutions.ewallet.enums.TransactionType txnType, String id) throws Exception {
		String queryString = " SELECT SUM (p.amount) FROM ProcessTransaction p WHERE p.dayEndSummary.id= :id AND p.transactionType= :txnType AND p.status =: compStatus";
		Query query = em.createQuery(queryString);
		query.setParameter("id", id);
		query.setParameter("txnType", txnType);
		query.setParameter("compStatus", TransactionStatus.COMPLETED);
		// query.setParameter("bankRequestStatus",
		// TransactionStatus.BANK_REQUEST);

		// query.setHint("bankRequestStatus", TransactionStatus.BANK_RESPONSE);

		// start here

		Number valueOfTxn = (Number) query.getSingleResult();
		if (valueOfTxn != null) {
			LOG.debug("value of transactions   :::::" + valueOfTxn.longValue());
			return valueOfTxn.longValue();

		}
		return 0;

	}

	private long updateTransactions(Date startDate, zw.co.esolutions.ewallet.enums.TransactionType txnType, DayEndSummary dayEndSummary, String tellerId) throws Exception {
		long count = 0;

		List<ProcessTransaction> transactions = getTransactionsByTellerIdDateRangeAndTransactionType(tellerId, startDate, startDate, txnType);
		LOG.debug("transactions value <" + transactions);
		if (transactions != null && transactions.size() > 0) {
			for (ProcessTransaction txn : transactions) {
				txn.setDayEndSummary(dayEndSummary);
				LOG.debug("  llll" + txn.getAmount() + "    txn type " + txn.getTransactionType());
				count++;
				// processUtil.updateProcessTransaction(txn);
				LOG.debug("Updating   " + txn.getBankReference());
			}
		}
		LOG.debug(txnType + "::::" + count);
		return count;

	}

	private long markTransNull(zw.co.esolutions.ewallet.enums.TransactionType txnType, DayEndSummary dayEndSummary, String tellerId) throws Exception {
		long count = 0;

		List<ProcessTransaction> transactions = getTransactionsByTellerIdAndDayEndSummary(tellerId, dayEndSummary.getId());
		LOG.debug("NEW >>>>>>>>>>>>>>>>transactions value <" + transactions);
		
		if (transactions != null && transactions.size() > 0) {
			LOG.debug("NEW>>>>>>>>>>>>>>>number of transactions to be marked"+transactions.size());
			for (ProcessTransaction txn : transactions) {
				LOG.debug("NEW >>>>>>>>>>>>>txn b4 update>>>>>>>>>>>"+txn.getDayEndSummary());
				txn.setDayEndSummary(null);
				LOG.debug("NEW  llll" + txn.getAmount() + "    txn type " + txn.getTransactionType());
				count++;
			
				txn=updateProcessTxn(txn, tellerId);
				
				LOG.debug("NEW Updating   " + txn.getDayEndSummary());
			}
		}
		//dayEndBean.editDayEndSummary(dayEndSummary, tellerId);
		LOG.debug(txnType + "::::" + count);
		return count;

	}

	private List<ProcessTransaction> getStartOfDayTransactionsByTellerIdDateRangeAndTransactionType(String tellerId, Date startDate, Date endDate, zw.co.esolutions.ewallet.enums.TransactionType txnType) {

		List<ProcessTransaction> results = null;
		try {
			Query query = em.createNamedQuery("getStartOfDayTransactionsByTellerIdDateRangeAndTransactionType");
			LOG.debug("query string   " + query.toString());

			query.setParameter("transactionType", txnType);
			query.setParameter("tellerId", tellerId);
			query.setParameter("fromDate", DateUtil.getBeginningOfDay(startDate));
			query.setParameter("toDate", DateUtil.getEndOfDay(endDate));
			query.setParameter("statusBankReq", TransactionStatus.BANK_REQUEST);
			query.setParameter("statusManResolve", TransactionStatus.MANUAL_RESOLVE);
			query.setParameter("statusCompl", TransactionStatus.COMPLETED);

			results = (List<ProcessTransaction>) query.getResultList();
			LOG.debug("results value   " + results);
			if (results != null) {
				LOG.debug("   number of txnlllll" + results.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (results == null || results.isEmpty()) {
			results = null;
		}
		return results;

	}

	private List<ProcessTransaction> getTransactionsByTellerIdAndDayEndSummary(String tellerId, String dayEndSummary) {

		List<ProcessTransaction> results = null;
		try {
			Query query = em.createNamedQuery("getTransactionByTellerIdAndDayEndSummary");
			LOG.debug("query string   " + query.toString());

			query.setParameter("dayEndSummary", dayEndSummary);
			query.setParameter("tellerId", tellerId);

			results = (List<ProcessTransaction>) query.getResultList();
			LOG.debug("results value   " + results);
			if (results != null) {
				LOG.debug("   number of txnlllll" + results.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (results == null || results.isEmpty()) {
			results = null;
		}
		return results;

	}

	private List<ProcessTransaction> getTransactionsByTellerIdDateRangeAndTransactionType(String tellerId, Date startDate, Date endDate, zw.co.esolutions.ewallet.enums.TransactionType txnType) {

		List<ProcessTransaction> results = null;
		try {
			Query query = em.createNamedQuery("getTransactionsByTellerIdDateRangeAndTransactionType");
			LOG.debug("query string   " + query.toString());

			query.setParameter("transactionType", txnType);
			query.setParameter("tellerId", tellerId);
			query.setParameter("fromDate", DateUtil.getBeginningOfDay(startDate));
			query.setParameter("toDate", DateUtil.getEndOfDay(endDate));
			// query.setParameter("succStatus", TransactionStatus.COMPLETED);
			// query.setParameter("bankRequestStatus",
			// TransactionStatus.BANK_REQUEST);

			results = (List<ProcessTransaction>) query.getResultList();
			LOG.debug("results value   " + results);
			if (results != null) {
				LOG.debug("   number of txnlllll" + results.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (results == null || results.isEmpty()) {
			results = null;
		}
		return results;

	}

	private List<ProcessTransaction> getTransactionByTellerIdAndNullDayEndSummary(String tellerId, Date dayEndDate) {

		ProfileServiceSOAPProxy profilProxy = new ProfileServiceSOAPProxy();
		Profile profile = profilProxy.getProfileByUserName(tellerId);
		tellerId = profile.getId();
		List<ProcessTransaction> results = null;
		try {
			LOG.debug("Teller id       " + tellerId);
			LOG.debug(" dayend date  ::::::" + dayEndDate);
			Query query = em.createNamedQuery("getTransactionByTellerIdAndNullDayEndSummary");
			LOG.debug("query string   " + query.toString());

			query.setParameter("tellerId", tellerId);
			query.setParameter("dayEndDate", DateUtil.getBeginningOfDay(dayEndDate));
			query.setParameter("statusCompl", TransactionStatus.COMPLETED);
			results = (List<ProcessTransaction>) query.getResultList();
			LOG.debug("results value   " + results);
			if (results != null) {
				LOG.debug("   number of txnlllll" + results.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (results == null || results.isEmpty()) {
			results = null;
		}
		return results;
	}

	private List<DayEndSummary> getSummariesList(List<zw.co.esolutions.ewallet.enums.TransactionType> endDayTransList) {
		List<DayEndSummary> summaryList = new ArrayList<DayEndSummary>();
		for (zw.co.esolutions.ewallet.enums.TransactionType txnType : endDayTransList) {
			DayEndSummary summary = new DayEndSummary();
			summary.setId(GenerateKey.generateEntityId());
			summary.setTransactionType(txnType);
			summaryList.add(summary);
		}
		return summaryList;
		
	}

	public List<zw.co.esolutions.ewallet.enums.TransactionType> getDayEndList() {
		List<zw.co.esolutions.ewallet.enums.TransactionType> endDayTransList = new ArrayList<zw.co.esolutions.ewallet.enums.TransactionType>();
		endDayTransList.add(zw.co.esolutions.ewallet.enums.TransactionType.WITHDRAWAL);
		endDayTransList.add(zw.co.esolutions.ewallet.enums.TransactionType.WITHDRAWAL_NONHOLDER);
		endDayTransList.add(zw.co.esolutions.ewallet.enums.TransactionType.DEPOSIT);
		endDayTransList.add(zw.co.esolutions.ewallet.enums.TransactionType.START_OF_DAY_FLOAT_IN);
		endDayTransList.add(zw.co.esolutions.ewallet.enums.TransactionType.AGENT_CASH_DEPOSIT);
		return endDayTransList;
	}

	/*
	 * 849-9+ +++++ an(non-Javadoc)
	 * 
	 * @see
	 * zw.co.esolutions.ewallet.process.ProcessService#isTodaysDayEndRun(java
	 * .lang.String, java.util.Date)
	 */
	public boolean isTodaysDayEndRun(String tellerId, Date dayEndDate) {
		ProfileServiceSOAPProxy proxy = new ProfileServiceSOAPProxy();
		Profile profile = null;
		LOG.debug("checking if dayend exists");
		if (tellerId != null) {
			profile = proxy.getProfileByUserName(tellerId);
			tellerId = profile.getId();
		}
		LOG.debug("Teller id  " + tellerId);
		LOG.debug("Day end date :::" + dayEndDate);

		List<DayEnd> dayEnds = getDayEndByTellerIdAndDayEndDate(tellerId, dayEndDate);
		LOG.debug("DayEbd :::" + dayEnds);
		if (dayEnds != null && dayEnds.size() == 0) {
			return false;
		} else
			return true;
	}

	private List<DayEnd> getDayEndByTellerIdAndDayEndDate(String tellerId, Date dayEndDate) {
		LOG.debug("in method");
		return dayEndBean.getDayEndByTellerIdAndDayEndDate(tellerId, dayEndDate);

	}

	// @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public DayEnd approveDayEnd(DayEnd dayEnd, String userName) throws Exception {
		LOG.debug("starting approval");
		try {
			return dayEndBean.approveDayEnd(dayEnd, userName);

		}

		catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);

		}

	}

	public List<DayEnd> getDayEndByDayEndStatus(DayEndStatus dayEndStatus, String userName) {
		return dayEndBean.getDayEndByDayEndStatus(dayEndStatus, userName);

	}

	public List<DayEnd> getDayEndsByDayEndStatusAndDateRange(DayEndStatus dayEndStatus, Date fromDate, Date toDate, String userName) {
		return dayEndBean.getDayEndByDayEndStatusAndDateRange(dayEndStatus, fromDate, toDate, userName);

	}

	public List<DayEndSummary> getDayEndSummaryByDayEndId(String dayEndId, String userName) throws Exception {

		return dayEndBean.getDayEndSummaryByDayEndId(dayEndId, userName);

	}

	public List<DayEnd> getDayEndByDayEndStatusAndBranch(DayEndStatus dayEndStatus, String userName) {
		Profile profile = new ProfileServiceSOAPProxy().getProfileByUserName(userName);
		String branchId = null;
		if (profile != null && profile.getId() != null) {
			branchId = profile.getBranchId();
		} else {
			branchId = userName;
		}

		return dayEndBean.getDayEndByDayEndStatusAndBranch(dayEndStatus, branchId);

	}

	public List<DayEnd> getDayEndByDayEndStatusAndDateRangeAndBranch(DayEndStatus dayEndStatus, String userName) {
		return null;

	}

	public DayEnd disapproveDayEnd(DayEnd dayEnd, String userName) throws Exception {

		try {
			LOG.debug("disapprove dayend ");
			List<DayEndSummary> summaries=getDayEndSummaryByDayEndId(dayEnd.getDayEndId(), userName);
			markSummariesAsNull(summaries,dayEnd,userName);
			
			return dayEnd;
		} catch (Exception e) {
			throw new Exception(e);

		}

	}

	private void markSummariesAsNull(List<DayEndSummary> summaries, DayEnd dayEnd, String userName) throws Exception {
		LOG.debug("disapprove dayend marking trxn ");
		for (DayEndSummary summary : summaries) {
			zw.co.esolutions.ewallet.enums.TransactionType txnType = summary.getTransactionType();
			markTransNull(txnType, summary, dayEnd.getTellerId());
		}

	}

	public List<DayEndSummary> getDayEndSummariesByDayEnd(String dayEndId) throws Exception {
		// TODO Auto-generated method stub
		return dayEndBean.getDayEndSummariesByDayEnd(dayEndId);

	}

	private String getAccountByAccountType(DayEnd endDay, zw.co.esolutions.ewallet.enums.BankAccountType sourceAccount) throws Exception {
		BankServiceSOAPProxy proxy = new BankServiceSOAPProxy();
		String sourceAcc = sourceAccount.toString();
		LOG.debug("Source account type defined   " + sourceAcc);
		zw.co.esolutions.ewallet.bankservices.service.OwnerType ownerType1 = null;
		String ownerId = null;
		String accountNumber = null;
		BankAccountType accountType = null;

		for (BankAccountType accountT : BankAccountType.values()) {
			if (accountT.toString().equalsIgnoreCase(sourceAcc)) {
				accountType = accountT;
				break;
			}
		}
		if (zw.co.esolutions.ewallet.enums.BankAccountType.EWALLET_BALANCING_SUSPENSE_ACCOUNT.equals(sourceAccount)) {
			ownerType1 = zw.co.esolutions.ewallet.bankservices.service.OwnerType.BANK_BRANCH;
			ownerId = endDay.getBranchId();
			LOG.debug("owner id is   " + ownerId);
		} else {
			ownerType1 = zw.co.esolutions.ewallet.bankservices.service.OwnerType.BANK_BRANCH;
			ownerId = endDay.getBranchId();
		}
		LOG.debug("ownertype1     " + ownerType1);
		LOG.debug("ownerid          " + ownerId);
		BankAccount bankAccount = proxy.getBankAccountByAccountHolderAndTypeAndOwnerType(ownerId, accountType, ownerType1, null);
		if (bankAccount == null || bankAccount.getId() == null) {
			throw new Exception("Bank account of type " + sourceAccount.name() + " does not exist.");
		}
		accountNumber = bankAccount.getAccountNumber();
		return accountNumber;
	}

	public ProcessTransaction processEquationPosting(DayEnd dayEnd, ProcessTransaction processTxn, String userName) {
		LOG.debug("Process equation posting");
		long amount = processTxn.getAmount();
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		Profile profile = profileService.findProfileById(dayEnd.getTellerId());
		String tellerUserName = profile.getUserName();

		String sourceAccountNumber = processTxn.getSourceAccountNumber();

		String targetAccountNumber = processTxn.getDestinationAccountNumber();
		zw.co.esolutions.ewallet.enums.TransactionType txnType = processTxn.getTransactionType();
		LOG.debug("    --------------------------------------------------------txn type  " + txnType.name());

		try {
			switch (txnType) {

			case DAYEND_CASH_TENDERED:
				// LOG.debug("  getting accounts  --------------------------------------------------------txn type  "
				// + txnType.name());
				sourceAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.BRANCH_CASH_ACCOUNT);
				targetAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT);

				break;

			case DAYEND_RECEIPTS:
				LOG.debug("  getting accounts  --------------------------------------------------------txn type  " + txnType.name());
				sourceAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.BRANCH_CASH_ACCOUNT);
				targetAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT);

				break;
			case DAYEND_PAYOUTS:
				sourceAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT);
				targetAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.BRANCH_CASH_ACCOUNT);
				LOG.debug(" payout accounting entries    source account     " + sourceAccountNumber);
				LOG.debug(" payout accounting entries    target account    " + targetAccountNumber);
				break;

			case DAYEND_UNDERPOST:
				targetAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT);
				sourceAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.EWALLET_BALANCING_SUSPENSE_ACCOUNT);

				break;

			case DAYEND_FLOATS:
				LOG.debug(">>>>>>>>>>floats>>>>>>>>>>>>>>>");
				sourceAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT);
				targetAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.BRANCH_CASH_ACCOUNT);

				break;

			case DAYEND_OVERPOST:
				targetAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.EWALLET_BALANCING_SUSPENSE_ACCOUNT);
				sourceAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT);

				break;
			default:
				break;
			}

		} catch (Exception e1) {
			LOG.debug("Exception in sending process txn for " + txnType);
			processTxn.setStatus(TransactionStatus.MANUAL_RESOLVE);
			e1.printStackTrace();
			return processTxn;

		}

		if (amount != 0) {

			LOG.debug("requeest set fot    " + txnType);
			try {

				String reference = processTxn.getMessageId();
				BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
				BankBranch bankBranch = bankService.findBankBranchById(processTxn.getBranchId());

				String branchName = bankBranch.getName();
				if (bankBranch == null || bankBranch.getId() == null) {
					LOG.debug("STEP : Found a null bank branch, cant proceed " + bankBranch);
					throw new Exception("Bank Branch is could not be found for this teller float.");
				}

				// String narrative = tellerUserName+ "/" + ;
				String narrative = tellerUserName + "/" + userName + "/" + branchName;
				processTxn.setNarrative(narrative);

				LOG.debug("  sourceaccount number  " + sourceAccountNumber);
				LOG.debug("  target account number  " + targetAccountNumber);
				BankRequest postRequest = new BankRequest();
				postRequest.setAmount(amount);
				postRequest.setSourceAccountNumber(sourceAccountNumber);
				postRequest.setTargetAccountNumber(targetAccountNumber);
				postRequest.setReference(reference);
				postRequest.setTransactionType(txnType);
				LOG.debug("  target account number  " + postRequest.getReference());
				LOG.debug("  target account number  " + postRequest.getTransactionType().name());
				LOG.debug("  target account number  " + postRequest.getAmount());
				LOG.debug("  target account number  " + postRequest.getSourceAccountNumber());
				LOG.debug("  target account number  " + postRequest.getTargetAccountNumber());

				postRequest.setNarrative(narrative);
				LOG.debug("  narrative----------------------------------------for posting  " + postRequest.getNarrative());
				processUtil.submitRequest(postRequest, false);
				LOG.debug("request sent");
				// set process txn status and update processTxn
				processTxn.setStatus(TransactionStatus.BANK_REQUEST);
				// updateProcessTxn(processTxn,userName);
				LOG.debug("requeest set fot    " + txnType);
				// throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
				LOG.debug("Exception in sending process txn for " + txnType);
				processTxn.setStatus(TransactionStatus.MANUAL_RESOLVE);
				// updateProcessTxn(processTxn,userName);
				// throw new Exception(e.getMessage());
				return processTxn;
			}
		} else if (amount == 0) {
			// posting not done

		}
		return processTxn;

	}

	public ProcessTransaction updateProcessTxn(ProcessTransaction processTxn, String userName) throws Exception {
		LOG.debug(processTxn.getTransactionType()+"---------updating process txn wahts my status---------"+processTxn.getStatus());
		if (processTxn != null) {
			processTxn.setNonTellerId(new ProfileServiceSOAPProxy().getProfileByUserName(userName).getId());
			processTxn = em.merge(processTxn);
			LOG.debug(" done udating     u knw who   ____-----" + processTxn.getTransactionType());
			return processTxn;
		}
		return processTxn;
	}

	public void deleteProcessTransactionsByDayEnd(DayEnd dayEnd, String userName) throws Exception {
		List<ProcessTransaction> trxns = getProcessTransactionsByDayEndId(dayEnd);
		for (ProcessTransaction processTxn : trxns) {
			if (processTxn != null) {
				em.remove(processTxn);
			}

		}
	}
	
	public void intThrows() throws Exception{
		 throw new Exception();
	}

	public DayEnd processDayEndBookEntries(DayEnd dayEnd, String userName) throws Exception {
		// Simulating exception
		
		//LOG.debug("Processing day end book enties");
		int count = 0;
		List<ProcessTransaction> trxns = getProcessTransactionsByDayEndId(dayEnd);
		ProcessTransaction trans=null;
		try {
			GenerateKey.throwsDayEndEWalletPostingsException();
			for (ProcessTransaction trxn : trxns) {
				++count;

				zw.co.esolutions.ewallet.enums.TransactionType txnType = trxn.getTransactionType();
				LOG.debug(" Process trxn     " + txnType + "    " + trxn.getAmount() + "      count  " + count);
				switch (txnType) {

				case DAYEND_CASH_TENDERED:

					LOG.debug(" Processing dayend_cash tendered book entries Process trxn     " + txnType + "    " + trxn.getAmount());
					trans=trxn;
					processUtil.postDayEndCashTendered(trxn);
					
					break;

				case DAYEND_UNDERPOST:
					LOG.debug("processing under post");
					trans=trxn;
					processUtil.postDayEndUnderPost(trxn);
					break;

				case DAYEND_OVERPOST:
					LOG.debug(" processing over post");
					processUtil.postDayEndOverPost(trxn);
					
					break;

				default:
					break;
				}

			}
		} catch (Exception e) {
			LOG.debug("  message exception      " + e.getMessage());
			setNarrativeOnDayEndPostings(dayEnd);
			dayEnd.setStatus(DayEndStatus.MANUALLY_RESOLVE);
			//dayEnd=dayEndBean.updateDayEnd(dayEnd, userName);
			return dayEnd;
			
		}
		LOG.debug("Done processing bo-ok entires");
		return dayEnd;

	}

	private void setNarrativeOnDayEndPostings(DayEnd dayEnd) {
		long count = 0;
		List<ProcessTransaction> trxns = getProcessTransactionsByDayEndId(dayEnd);
		try {
			for (ProcessTransaction trxn : trxns) {
				++count;

				zw.co.esolutions.ewallet.enums.TransactionType txnType = trxn.getTransactionType();
				LOG.debug(" Process trxn     " + txnType + "    " + trxn.getAmount() + "      count  " + count);
				switch (txnType) {

				case DAYEND_CASH_TENDERED:

					LOG.debug(" Processing dayend_cash tendered book entries Process trxn     " + txnType + "    " + trxn.getAmount());
					processUtil.updateDayEndTxnState(trxn, trxn.getStatus(), EWalletConstants.EWALLET_BOOK_ENTRY_FAILURE_NARRATIVE);
					
					break;

				case DAYEND_UNDERPOST:
					LOG.debug("processing under post");
					processUtil.updateDayEndTxnState(trxn, trxn.getStatus(), EWalletConstants.EWALLET_BOOK_ENTRY_FAILURE_NARRATIVE);
					
					break;

				case DAYEND_OVERPOST:
					LOG.debug(" processing over post");
					processUtil.updateDayEndTxnState(trxn, trxn.getStatus(), EWalletConstants.EWALLET_BOOK_ENTRY_FAILURE_NARRATIVE);
					
					
					break;

				default:
					break;
				}

			}
		} catch (Exception e) {
		}
	}

	public List<ProcessTransaction> getProcessTransactionsByDayEndId(DayEnd dayEnd) {
		return this.processUtil.getProcessTransactionsByDayEndId(dayEnd);		
	}

	public DayEnd deleteDayEnd(DayEnd dayEnd, String userName) throws Exception {

		return dayEndBean.deleteDayEnd(dayEnd, userName);

	}

	public ProcessTransaction createProcessTransaction(ProcessTransaction tranx, String userName) throws Exception {
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		String bankId = bankService.findBankBranchById(tranx.getBranchId()).getBank().getId();
		tranx.setFromBankId(bankId);
		return this.processUtil.createProcessTransaction(tranx); 
	}

	public List<DayEnd> getDayEndsByDayEndStatusAndDateRangeAndTeller(Date fromDate, Date toDate, DayEndStatus dayEndStatus, String tellerId) throws Exception {
		// TODO Auto-generated method stub
		return dayEndBean.getDayEndsByDayEndStatusAndDateRangeAndTeller(dayEndStatus, fromDate, toDate, tellerId);

	}

	public List<DayEnd> getDayEndsByDayEndStatusAndDateRangeAndBranch(Date fromDate, Date toDate, DayEndStatus dayEndStatus, String branch) throws Exception {
		// TODO Auto-generated method stub
		return dayEndBean.getDayEndsByDayEndStatusAndDateRangeAndBranch(dayEndStatus, fromDate, toDate, branch);

	}

	public long getDailyAmountByCustomerIdAndTxnType(String customerId, zw.co.esolutions.ewallet.enums.TransactionType txnType) {
		return processUtil.getDailyAmountByCustomerIdAndTxnType(customerId, txnType);
	}

	@SuppressWarnings("unchecked")
	public List<ProcessTransaction> getProcessTransactionsByApplicableParameters(Date fromDate, Date toDate, TransactionStatus status, zw.co.esolutions.ewallet.enums.TransactionType txnType, TxnFamily txnFamily, String tellerId, String bankId, String branchId) {
		return processUtil.getProcessTransactionsByApplicableParameters(fromDate, toDate, status, txnType, txnFamily, tellerId, bankId, branchId) ;
	}

	public long getTotalAmountByApplicableParameters(Date fromDate, Date toDate, TransactionStatus status, zw.co.esolutions.ewallet.enums.TransactionType txnType, TxnFamily txnFamily, String tellerId, String bankId, String branchId) {
		return processUtil.getTotalAmountByApplicableParameters(fromDate, toDate, status, txnType, txnFamily, tellerId, bankId, branchId);
	}


	public ProcessResponse processRegisterCustomerByAgent(RequestInfo requestInfo) throws Exception {
		// AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();

		ProcessTransaction txn = processUtil.populateProcessTransaction(requestInfo);
		processUtil.promoteTxnState(txn, TransactionStatus.DRAFT, "Posting agent registers customer commission.");
		// Agent agent = agentService.findAgentById(agentId);
		// BankAccount account =
		// bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(agentId,
		// BankAccountType.AGENT_COMMISSION_ACCOUNT, OwnerType.AGENT);
		//
		ResponseCode response = processUtil.postAgentRegistersCustomerTxns(txn);

		ProcessResponse processResponse = new ProcessResponse();
		processResponse.setResponseCode(response.name());

		return processResponse;
	}

	public ProcessTransaction approveTellerDailyFloat(String transactionId, String username) throws Exception {
		LOG.debug("BEGIN : Approve teller daily float ");
		ProcessTransaction processTransaction = this.findProcessTransactionById(transactionId);
		LOG.debug("STEP : Done finding the original txn " + processTransaction);
		// did we find it
		if (processTransaction == null) {
			LOG.debug("STEP : Failed to find the original txn " + processTransaction);
			return null;
		}
		LOG.debug("STEP : Found the original txn " + processTransaction);
		// approve only when its approvable.
		if (!TransactionStatus.AWAITING_APPROVAL.equals(processTransaction.getStatus())) {
			throw new Exception("Can not approve " + processTransaction.getTransactionType() + " in status " + processTransaction.getStatus());
		}

		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		Profile teller = profileService.findProfileById(processTransaction.getProfileId());

		if (teller == null || teller.getId() == null) {
			LOG.debug("STEP : Found a null teller, cant proceed " + teller);
			throw new Exception("Cannot find teller with id  " + processTransaction.getProfileId());
		}

		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		BankBranch bankBranch = bankService.findBankBranchById(processTransaction.getBranchId());
		if (bankBranch == null || bankBranch.getId() == null) {
			LOG.debug("STEP : Found a null bank branch, cant proceed " + bankBranch);
			throw new Exception("Bank Branch is could not be found for this teller float.");
		}

		BankAccount sourceBankAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(bankBranch.getId(), BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT, OwnerType.BANK_BRANCH, null);
		if (bankBranch == null || bankBranch.getId() == null) {
			LOG.debug("STEP : Could not find an EWALLET BRANCH CASH ACCOUNT for BANK BRANCH " + bankBranch.getName());
			throw new Exception("Could not find an EWALLET BRANCH CASH ACCOUNT for BANK BRANCH " + bankBranch.getName());
		}

		BankAccount targetBankAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(bankBranch.getId(), BankAccountType.BRANCH_CASH_ACCOUNT, OwnerType.BANK_BRANCH, null);
		if (bankBranch == null || bankBranch.getId() == null) {
			LOG.debug("STEP : Could not find an BRANCH CASH ACCOUNT for BANK BRANCH " + bankBranch.getName());
			throw new Exception("Could not find an BRANCH CASH ACCOUNT for BANK BRANCH " + bankBranch.getName());
		}
		// update the process trsansaction's bank account info
		processTransaction.setSourceAccountNumber(sourceBankAccount.getAccountNumber());
		processTransaction.setDestinationAccountNumber(targetBankAccount.getAccountNumber());
		processTransaction.setSourceEQ3AccountNumber(sourceBankAccount.getAccountNumber());
		processTransaction.setDestinationEQ3AccountNumber(targetBankAccount.getAccountNumber());

		processTransaction = processUtil.updateProcessTransaction(processTransaction);

		// do EQ3 Posting
		LOG.debug("STEP : Doing EQ3 Postings for transaction.");

		BankRequest bankRequest = this.populateBankRequest(processTransaction, teller.getUserName(), username, bankBranch.getName());
		sendBankRequestToBank(bankRequest);
		this.processUtil.promoteTxnState(processTransaction, TransactionStatus.BANK_REQUEST, TransactionStatus.BANK_REQUEST.name());
		BankResponse response = this.processUtil.waitForBankResponse(bankRequest);

		LOG.debug(">>>>>>>> PROMOTTING FAILED>>>>>>>>>>>>APPROVAL START OF DAY>> banl response:::::   " + response.getResponseCode());

		if (ResponseCode.E000.equals(response.getResponseCode())) {
			// post to ewallet

			LOG.debug("STEP : Doing BOOK Entry postings for transaction.");
			// do book entry posting
			ResponseCode resp = this.processUtil.postTellerFloatToEwallet(processTransaction, sourceBankAccount, targetBankAccount);
			LOG.debug("STEP : DONE BOOK Entry postings for transaction.");
		
			// send notif
			if (ResponseCode.E000.equals(resp)) {
				LOG.debug(">>>>>>>> PROMOTTING COMPLETED>>>>>>>>>>>>APPROVAL START OF DAY " + response.getResponseCode().getDescription());
				processTransaction = processUtil.promoteTxnState(processTransaction, TransactionStatus.COMPLETED, response.getNarrative());
			}else{
				LOG.debug(">>>>>>>> PROMOTTING COMPLETED>>>>>>>>>>>>APPROVAL START OF DAY " + response.getResponseCode().getDescription());
				processUtil.submitRequest(bankRequest, true);
				processTransaction.setNarrative(resp.getDescription());
				processTransaction = processUtil.promoteTxnState(processTransaction, TransactionStatus.FAILED, resp.getDescription());
				
			}

		} else {

			
			if (ResponseCode.E830.equals(response.getResponseCode())) { // txn has timed out
				//processUtil.submitRequest(bankRequest, true);
				processTransaction=markTxnAsTimedOut(processTransaction, response);

			}else{
				LOG.debug(">>>>>>>> PROMOTTING FAILED>>>>>>>>>>>>APPROVAL START OF DAY   " + response.getResponseCode().getDescription());
				processTransaction.setNarrative(response.getResponseCode().getDescription());
				processTransaction = processUtil.promoteTxnState(processTransaction, TransactionStatus.FAILED, response.getNarrative());
			

			}
		}

		try {
			AuditTrailServiceSOAPProxy auditTrailServiceSOAPProxy = new AuditTrailServiceSOAPProxy();
			auditTrailServiceSOAPProxy.logActivityWithNarrative(username, "APPROVE TELLER FLOAT", username + " created " + processTransaction.getTransactionType() + " with ref : " + processTransaction.getMessageId());
		} catch (Exception e) {
			// forget abt this Exception harina basa..
		}
		LOG.debug("END : Finished doing postings, Returning updated txn.");
		return processTransaction;
	}

	private BankRequest populateBankRequest(ProcessTransaction processTransaction, String tellerUsername, String approverUsername, String branchName) throws Exception {
		LOG.debug("BEGIN : Doing EQ3 postings for transaction " + processTransaction.getMessageId() + " " + processTransaction.getTransactionType());
		BankRequest bankRequest = processUtil.populateBankRequest(processTransaction);
		LOG.debug("STEP : Done populating request object ");
		bankRequest.setNarrative(tellerUsername.toUpperCase() + "/" + approverUsername.toUpperCase() + "/" + branchName.toUpperCase());
		LOG.debug("STEP : Done populating the txn narrative.");
		return bankRequest;
	}

	public List<ProcessTransaction> getStartOfDayTransactionByTransactionTypeAndBranchAndStatus(zw.co.esolutions.ewallet.enums.TransactionType transactionType, String branchId, TransactionStatus txnStatus) throws Exception {
		return this.processUtil.getStartOfDayTransactionByTransactionTypeAndBranchAndStatus(transactionType, branchId, txnStatus);
	}

	public List<ProcessTransaction> getStartOfDayTransactionByTransactionTypeAndTellerAndDateRange(zw.co.esolutions.ewallet.enums.TransactionType transactionType, String tellerId, Date fromDate, Date toDate, TransactionStatus txnStatus) throws Exception {
		return this.processUtil.getStartOfDayTransactionByTransactionTypeAndTellerAndDateRange(transactionType, tellerId, fromDate, toDate, txnStatus);
	}

	public ProcessTransaction disapproveTellerDailyFloat(String transactionId, String username) throws Exception {
		LOG.debug("BEGIN : Disapprove teller daily float.");
		ProcessTransaction processTransaction = this.findProcessTransactionById(transactionId);
		LOG.debug("STEP : Done finding the original txn " + processTransaction);
		// did we find it
		if (processTransaction == null) {
			LOG.debug("STEP : Failed to find the original txn " + processTransaction);
			return null;
		}
		LOG.debug("STEP : Found the original txn " + processTransaction);
		// approve only when its approvable.
		if (!TransactionStatus.AWAITING_APPROVAL.equals(processTransaction.getStatus())) {
			throw new Exception("Can not disapprove " + processTransaction.getTransactionType() + " in status " + processTransaction.getStatus());
		}
		// update the txn
		LOG.debug("STEP : Transaction is disapprovable, now promote status to disapproved.");
		processTransaction = processUtil.promoteTxnState(processTransaction, TransactionStatus.DISAPPROVED, "Disapproved");
		AuditTrailServiceSOAPProxy auditTrailServiceSOAPProxy = new AuditTrailServiceSOAPProxy();
		LOG.debug("STEP : Done promoting status to " + processTransaction.getStatus() + " : Log the disapprove event.");
		auditTrailServiceSOAPProxy.logActivityWithNarrative(username, username, username + " disapproved " + processTransaction.getTransactionType() + " with ref : " + processTransaction.getMessageId());
		LOG.debug("END : Return the updated process txn in status " + processTransaction.getStatus());
		return processTransaction;
	}

	public List<ProcessTransaction> getStartDayTxnsByTellerAndDateRangeAndStatus(String tellerId, Date fromDate, Date toDate) throws Exception {
		return this.processUtil.getStartDayTxnsByTellerAndDateRangeAndStatus(tellerId, fromDate, toDate);
	}

	public long getTellerNetCashPosition(String profileId, Date date) throws Exception {
		Date fromDate = DateUtil.getBeginningOfDay(date);
		Date toDate = DateUtil.getEndOfDay(date);

		LOG.debug("BEGIN : Calculation of teller float.");
		long totalFloats = this.processUtil.getTellerSubTotalByTransactionTypeAndDateRangeAndStatus(profileId, fromDate, toDate, zw.co.esolutions.ewallet.enums.TransactionType.START_OF_DAY_FLOAT_IN);
		LOG.debug("STEP : Got a result for total floats " + totalFloats);

		LOG.debug("STEP : Calculating total CASH INS ");
		long totalCashIns = this.processUtil.getTellerSubTotalByTransactionTypeAndDateRangeAndStatus(profileId, fromDate, toDate, zw.co.esolutions.ewallet.enums.TransactionType.DEPOSIT);
		
		LOG.debug("STEP : Got a result for total CASH INS " + totalCashIns);

		LOG.debug("STEP : Calculating total CASH AGENT INS ");
		long totalAgentCashIns = this.processUtil.getTellerSubTotalByTransactionTypeAndDateRangeAndStatus(profileId, fromDate, toDate, zw.co.esolutions.ewallet.enums.TransactionType.AGENT_CASH_DEPOSIT);
		LOG.debug("STEP : Got a result for total CASH AGENT INS " + totalAgentCashIns);
		
		LOG.debug("STEP : Calculating patial cash outs only ");
		long totalCashOuts = this.processUtil.getTellerSubTotalByTransactionTypeAndDateRangeAndStatus(profileId, fromDate, toDate, zw.co.esolutions.ewallet.enums.TransactionType.WITHDRAWAL);
		LOG.debug("STEP : Got a result for PARTIAL CASH OUTS (WITHDRAWAL ONLY)  " + totalCashOuts);

		LOG.debug("STEP : Calculating totals non holder cash outs.");
		long totalNonHolderCashOuts = this.processUtil.getTellerSubTotalByTransactionTypeAndDateRangeAndStatus(profileId, fromDate, toDate, zw.co.esolutions.ewallet.enums.TransactionType.WITHDRAWAL_NONHOLDER);
		LOG.debug("STEP : Done calculation of total PAY OUTS " + totalCashOuts);

		long result = totalFloats + totalCashIns + totalAgentCashIns - totalCashOuts - totalNonHolderCashOuts;
		LOG.debug("END : Returning a result of NET TELLER POSITION " + result);
		return result;

	}

	public ProcessTransaction findProcessTransactionById(String transactionId) throws Exception {
		return processUtil.findProcessTransactionById(transactionId);
	}

	public ProcessTransaction createStartOfDayTransaction(ProcessTransaction processTransaction, String username) throws Exception {
		LOG.debug("BEGIN : Create Start Of Day Transaction for teller daily float " + username);
		processTransaction.setDateCreated(new Date());
		// make sure the reference is set.
		LOG.debug("STEP : Set date created to now. ");

		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		Profile teller = profileService.getProfileByUserName(username);
		processTransaction.setBranchId(teller.getBranchId());

		if (processTransaction.getId() == null) {
			LOG.debug("STEP : TXN reference is null");
			ReferenceGeneratorServiceSOAPProxy refProxy = new ReferenceGeneratorServiceSOAPProxy();
			String year = Formats.yearFormatTwoDigit.format(new Date(System.currentTimeMillis()));
			processTransaction.setMessageId(refProxy.generateUUID(EWalletConstants.SEQUENCE_NAME_DAY_ENDS, EWalletConstants.SEQUENCE_PREFIX_DAY_ENDS, year, 0, 1000000000L - 1L));
			processTransaction.setId(processTransaction.getMessageId());
			LOG.debug("STEP : Set the reference..");
		}
		LOG.debug("STEP : Make sure the branchId and the bank ID are set, else??? ");

		BankBranch bankBranch = bankService.findBankBranchById(teller.getBranchId());
		processTransaction.setTransactionLocationType(TransactionLocationType.BANK_BRANCH);
		processTransaction.setFromBankId(bankBranch.getBank().getId());
		// pupulate accounts to send to
		LOG.debug("STEP : Execute the create method.");
		
		processTransaction = processUtil.createProcessTransaction(processTransaction);
		processUtil.promoteTxnState(processTransaction, TransactionStatus.AWAITING_APPROVAL, "Start of day awaiting approval");
		LOG.debug("STEP : Done running the create " + processTransaction + " now log the event ");
		AuditTrailServiceSOAPProxy auditTrailServiceSOAPProxy = new AuditTrailServiceSOAPProxy();
		auditTrailServiceSOAPProxy.logActivityWithNarrative(username, username, username + " created " + processTransaction.getTransactionType() + " with ref : " + processTransaction.getMessageId());
		LOG.debug("END : Done running the create " + processTransaction);
		return processTransaction;

	}

	public boolean checkIfAnyFloatsPendingApproval(String profileId) {
		boolean result = false;
		List<ProcessTransaction> floatsPending = getStartOfDayTxnByProfileIdAndDayEndSummaryAndStatus(profileId, TransactionStatus.AWAITING_APPROVAL);
		// LOG.debug("floats pending "+floatsPending);
		if (floatsPending != null && floatsPending.size() > 0) {
			// LOG.debug(" float size   "+floatsPending.size());
			return true;
		}
		return false;
	}

	public List<ProcessTransaction> getStartOfDayTxnByProfileIdAndDayEndSummaryAndStatus(String profileId, TransactionStatus awaiting_approval) {
		return this.processUtil.getStartOfDayTxnByProfileIdAndDayEndSummaryAndStatus(profileId, awaiting_approval);
	}

	public boolean checkTellerDayEndsPendingApproval(String tellerId) {
		return this.processUtil.checkTellerDayEndsPendingApproval(tellerId);
	}

	@Override
	public String canTellerMakeTransact(String profileId, Date toDay, zw.co.esolutions.ewallet.enums.TransactionType txnType) {
		DayEndResponse dayEndResponse = null;
		boolean startOfDayAvailable = false;
		boolean floatAvailable = false;
		boolean dayEndsPendingApproval = false;
		boolean floatsPendingApproval = false;
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		Profile profile = profileService.findProfileById(profileId);
		String userName = profile.getUserName();
		// LOG.debug("check day ends pending approval");
		dayEndResponse = isPreviousDayEndRun(userName, toDay);
		LOG.debug("Previous day end status >>>>>>" + dayEndResponse.isStatus());
		dayEndsPendingApproval = checkTellerDayEndsPendingApproval(profileId);
		LOG.debug("2-------day end approval check ::::::::" + dayEndsPendingApproval);
		if (dayEndResponse.isStatus()) {
			return "Day end has not been run for  " + DateUtil.convertDateToString(dayEndResponse.getDayEndDate());
		} else if (dayEndsPendingApproval) {
			return "You have day ends that have not been approved. Please contact your supervisor.";
		}
		floatsPendingApproval = checkIfAnyFloatsPendingApproval(profileId);
		LOG.debug("are there any floats pending approval >>>>>>>>" + floatsPendingApproval);
		if (floatsPendingApproval) {
			return "You have floats that have not been approved. Please contact your supervisor";
		}
		startOfDayAvailable = this.processUtil.checkIfStartofDayExistsAndApproved(profileId, toDay);
		LOG.debug("start of day txt     :::::::::::" + startOfDayAvailable);
		if (startOfDayAvailable) {
			return ResponseCode.E000.toString();
		} else {
			return "Please run the start of day procedure and have it approved by your supervisor.";
		}

	}

	public List<ProcessTransaction> getProcessTransactionsByAllAttributes(UniversalProcessSearch universal){
		return this.processUtil.getProcessTransactionsByAllAttributes(universal);
	} 


	/**
	 * Method for reversing Transaction
	 * 
	 * @param manual
	 *            {set messageId, oldMessage}
	 * @return Notification message
	 * @throws Exception
	 */
	public String reverseTransaction(ManualPojo manual) throws Exception {
		return ResponseCode.E000.toString();
	}

	/**
	 * Method to mark transactioion as completed
	 * 
	 * @param manual
	 *            {set messageId}
	 * @return Notification message
	 * @throws Exception
	 */
	public String completeTransaction(ManualPojo manual) throws Exception {
		try {
			LOG.debug(">>>>>>>>>>In completeTransaction method Message Id = " + manual.getMessageId() + "   Status = " + manual.getStatus());
			ProcessTransaction txn = this.getProcessTransactionByMessageId(manual.getMessageId());
			txn.setStatus(manual.getStatus());
			this.processUtil.promoteTxnState(txn, manual.getStatus(), "Transaction Awaiting Completion Approval.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseCode.E505.getDescription();
		}
		return ResponseCode.E000.toString();
	}

	/**
	 * Method for resolving transactions manually
	 * 
	 * @param manual
	 *            {set almost everything}
	 * @return notification message
	 * @throws Exception
	 */
	public ManualReturn manualResolve(ManualPojo manual) throws Exception {

ManualReturn manualReturn = new ManualReturn();
		
		try {
			LOG.debug(">>>>>>>>>>>>> Now in Manual resolve.");
			ProcessTransaction processTxn = this.processUtil.populateProcessTransactionWithManualInfor(manual);
			processTxn = this.processUtil.createProcessTransaction(processTxn);
			LOG.debug(">>>>>>>>>>>>> Done creating Process Transaction "+processTxn.getMessageId());
			
			
			//Creating TxnCharge
			if (manual.getFromEwalletChargeAccount() != null || manual.getFromEQ3ChargeAccount() != null) {
				if (manual.getToEwalletChargeAccount() != null || manual.getToEQ3ChargeAccount() != null) {

					if(manual.getChargeTransactionClass() != null && manual.getChargeAmount() > 0) {
						LOG.debug(">>>>>>>>>>>>> There is a charge");
						TransactionCharge txnCharge = this.processUtil.populateTransactionCharge(processTxn, manual);
						LOG.debug(">>>>>>>>>>>>> Setting ProcessTransaction on TxnCharge msgid "+processTxn.getMessageId());
						LOG.debug(">>>>>>>>>>>>> Setting ProcessTransaction on TxnCharge id "+processTxn.getId());
		//				processTxn = this.getProcessTransactionByMessageId(processTxn.getMessageId());
						processTxn=this.findProcessTransactionById(processTxn.getId());
						LOG.debug(">>>>>>>>>>>>> Setting ProcessTransaction on TxnCharge "+processTxn);
						txnCharge.setProcessTransaction(processTxn);
						txnCharge = this.processUtil.createTransactionCharge(txnCharge);
						LOG.debug(">>>>>>>>>>>>> Done Creating Transaction charge");
					}
				}
				
			}
			
			//Promote Txn
			processTxn = this.getProcessTransactionByMessageId(processTxn.getMessageId());
			processTxn.setStatus(TransactionStatus.AWAITING_APPROVAL);
			LOG.debug(">>>>>>>>>>>>> Process Transaction Set to "+processTxn.getStatus());
			processTxn = this.processUtil.promoteTxnState(processTxn, TransactionStatus.AWAITING_APPROVAL, null);
			LOG.debug(">>>>>>>>>>>>> Done Promoting Process Transaction = "+processTxn.getStatus().toString());
			
			
			//Check Destination Balance if customer Ewallet
			
			//Check Overdraw Source Balance Ewallet
			manualReturn.setTxn(processTxn);
						
			
		} catch (Exception e) {
			LOG.debug(">>>>>>>>>>>>> Manual Resolve. Exception thrown.");
			e.printStackTrace();
			manualReturn.setResponse(ResponseCode.E505.getDescription());
			return manualReturn;
		}
		manualReturn.setResponse(ResponseCode.E000.getDescription());
		LOG.debug(">>>>>>>>>>>>> Manual Resolve Completed Successfully.");
		return manualReturn;
	}

	/**
	 * 
	 * Method for approval or disapproval of Transactions
	 * 
	 * @param manual
	 *            {set: messageId, oldMessageId, status}
	 * @return message
	 * @throws Exception
	 */

	public String confirmManualResolve(ManualPojo manual) throws Exception {
		try {
			ProcessTransaction txn = this.getProcessTransactionByMessageId(manual.getMessageId());
			LOG.debug(">>>>>>>>>>>>> Now in Confirm Manual resolve.");
			
			//txn = this.updateProcessTxn(txn, manual.getUserName());
			ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
			Profile profile= profileService.getProfileByUserName(manual.getUserName());
			txn.setNonTellerId(profile.getId());
			
			LOG.debug("Approver has been set to proccessTxn");
			
			//Check for disapproval
			if(TransactionStatus.DISAPPROVED.equals(manual.getStatus())) {
				LOG.debug(">>>>>>>>>>>>> In Disapprove");
				txn.setStatus(manual.getStatus());
				txn = this.updateProcessTxn(txn, manual.getUserName());
				if(txn != null) {
					return ResponseCode.E000.getDescription();
				}else {
					return ResponseCode.E505.getDescription();
				}
			}
			
			ResponseCode responseCode;
			
			//Call your posting methods here for ewallet otherwise delagate to EQ3
			// Post Entries
			if(TransactionClass.EWALLET.equals(TransactionClass.valueOf(txn.getTransactionClass()))) {
				
				try {
					
					responseCode = this.processUtil.postAdjustmentTxnsToEwallet(manual, txn);
					} catch (Exception e) {
					LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>exception thrown >>>>>>>>>>>"+e.getMessage());
					LOG.debug("##########      POST To Ewallet THROWN EXCEPTION... INITIATING ROLLBACK");

			//	new BankServiceSOAPProxy().reverseEWalletEntries(transactionPostingInfos, chargePostingInfos);
					
					LOG.debug("##########      ROLLBACK DONE");
					
					return ResponseCode.E505.getDescription();   

				}
				
				return responseCode.getDescription();
				
			} else if (TransactionClass.EQUATION.equals(TransactionClass.valueOf(txn.getTransactionClass()))) {
				
				//Do Equation Postings Here
				LOG.debug(">>>>>>>>>>>>> EQuation Type Txn.");
				
				responseCode = this.postAdjustmentTxnsToEQ3(manual, txn);
				
				if (ResponseCode.E000.equals(responseCode)) {
					processUtil.promoteTxnState(txn, TransactionStatus.COMPLETED, txn.getTransactionType().name() + " completed successfully.");
				} else {
					LOG.debug("ADJUSTMENT FAILED");
				//	this.reinstateAdjustmentTxnToAwaitingApproval(manual);
					
				}
				
				return responseCode.getDescription();
				
			} else if (TransactionClass.BOTH.equals(TransactionClass.valueOf(txn.getTransactionClass()))) {
				//Do Both Postings Here
				LOG.debug(">>>>>>>>>>>>> BOTH Type Txn.");
				
				responseCode = this.postAdjustmentTxnsToEQ3(manual, txn);
				
				if (ResponseCode.E000.equals(responseCode)) {
					
					try {
						
						responseCode = this.processUtil.postAdjustmentTxnsToEwallet(manual, txn);
						if (!ResponseCode.E000.equals(responseCode)){
							LOG.debug("##########      ROLLBACK DONE");
							
							LOG.debug("********   PROCESS EQ3 REVERSAL txn status b4   "+txn.getStatus().toString());
							txn.setStatus(TransactionStatus.FAILED);
							txn=processUtil.promoteTxnState(txn, TransactionStatus.FAILED, txn.getTransactionType().name() + " completed successfully.");
							
							BankRequest bankRequest = processUtil.populateAdjustmentBankRequest(txn, true);
							
							processUtil.submitRequest(bankRequest, true);
							
							//reinstate txn
						//	this.reinstateAdjustmentTxnToAwaitingApproval(manual);
							LOG.debug("ADJUSTMENT FAILED");

							return ResponseCode.E836.getDescription(); 
						}
						
					} catch (Exception e) {
						
						LOG.debug(e.getMessage());
						
						LOG.debug("##########      POST To Ewallet THROWN EXCEPTION... INITIATING ROLLBACK");

			//			processUtil.rollBackEWalletBookEntry(txn.getMessageId());
						
						LOG.debug("##########      ROLLBACK DONE");
						
						LOG.debug("********   PROCESS EQ3 REVERSAL");
						
						BankRequest bankRequest = processUtil.populateAdjustmentBankRequest(txn, true);
						
						processUtil.submitRequest(bankRequest, true);
						//reinstate txn
					//	this.reinstateAdjustmentTxnToAwaitingApproval(manual);
						LOG.debug("ADJUSTMENT FAILED");

						return ResponseCode.E505.getDescription();   

					}
					
					
				} else {
					LOG.debug("ADJUSTMENT FAILED");

				//	this.reinstateAdjustmentTxnToAwaitingApproval(manual);
					
				}
				
				return responseCode.getDescription();
				
			} else {
				//transactionClass not configured				
				return ResponseCode.E505.getDescription();

			}
			
		} catch (Exception e) {
			LOG.debug(">>>>>>>>>>>>> Exception Thrown.");
			e.printStackTrace();
			return ResponseCode.E505.getDescription();
		}

	}

	public ProcessTransaction reinstateAdjustmentTxnToAwaitingApproval(ManualPojo manualPojo) throws Exception {
		// creates a duplicate adjustment txn with a new reference, returns the
		// new transaction
		// manualPojo.setMessageId(processUtil.generateReference(zw.co.esolutions.ewallet.enums.TransactionType.ADJUSTMENT));
		ManualReturn manualReturn = this.manualResolve(manualPojo);

		return manualReturn.getTxn();
	}

	private ResponseCode postAdjustmentTxnsToEQ3(ManualPojo manual, ProcessTransaction txn) throws Exception {
		BankRequest bankRequest = processUtil.populateAdjustmentBankRequest(txn, false);
		this.sendBankRequestToBank(bankRequest);
		txn = processUtil.promoteTxnState(txn, TransactionStatus.BANK_REQUEST, txn.getNarrative());
		BankResponse bankResponse = processUtil.waitForBankResponse(bankRequest);
		if (bankResponse != null) {
			// check timeout and reverse txn
			if (ResponseCode.E830.equals(bankResponse.getResponseCode())) {
				//processUtil.submitRequest(bankRequest, true);
				LOG.debug("EQ4 Manual resolve posting failed mark as timed out "+txn.getId());
				txn=this.markTxnAsTimedOut(txn, bankResponse);
				return ResponseCode.E830;
			} else {
				return bankResponse.getResponseCode();
			}
		}
		return ResponseCode.E505;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void sendBankRequestToBank(BankRequest bankRequest) throws Exception {
		LOG.debug("%%%%%%%%%%% SENDING REQUEST TO BANK");
		processUtil.submitRequest(bankRequest, false);
		LOG.debug("%%%%%%%%%%%  REQUEST SENT TO BANK");

	}

//	public void sendEQ3Reversal(BankRequest bankRequest) {
//		if (bankRequest == null) {
//			return;
//		}
//		bankRequest.setReversal(true);
//		try {
//			LOG.debug("@@@@@@@      SENDING REVERSAL");
//			MessageSender.send(EWalletConstants.FROM_EWALLET_TO_BANKMED_QUEUE, bankRequest); // send reversal and forget
//			LOG.debug("@@@@@@@      REVERSAL SENT");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TransactionCharge> getTransactionChargeByProcessTransactionId(String processTxnId) {
		return this.processUtil.getTransactionChargeByProcessTransactionId(processTxnId);
	}

	@Override
	public String scheduleTimer(String dayOfMonth, String hour, String min, boolean isForThisMonth, Date runDate) {
		LOG.debug("Schedule called.");
		try {
			this.monthlyProcessor.scheduleTimer(dayOfMonth, hour, min, isForThisMonth, runDate);
		} catch (Exception e) {
			e.printStackTrace();
			return "Schedulling failed, an error occurred.";
		}
		return ResponseCode.E000.name();
	}

	public DayEndApprovalResponse processDayEndApproval(DayEnd dayEnd, String userName) throws Exception {
		DayEndApprovalResponse response = new DayEndApprovalResponse();
		LOG.debug("starting dayEnd Approval");

		ReferenceGeneratorServiceSOAPProxy referenceProxy = new ReferenceGeneratorServiceSOAPProxy();
		// Mark dayend as approved

		ProcessTransaction imbalancePostTxn = null;
		ProcessTransaction cashTenderedTxn = null;
		boolean imbalancePresent = false;
		BankRequest imbalanceRequest = null;
		// create ProcessTransactions
		DayEndStatus dayEndStaus = null;

		// Done creation

		List<DayEndSummary> summaries = new ArrayList<DayEndSummary>();

		String year = Formats.yearFormatTwoDigit.format(new Date());
		String sequenceName = "Day Ends";
		String prefix = "A";
		try {
			summaries = getDayEndSummariesByDayEnd(dayEnd.getDayEndId());
			System.out.println(">>>>>>>>>>>summary size>>>" + summaries.size());
			for (DayEndSummary summary : summaries) {
				System.out.println(".....Summary Type " + summary.getTransactionType().name() + ">>>>>>Amount>>>" + summary.getValueOfTxns());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		long tellerFlaots = getSumValue(zw.co.esolutions.ewallet.enums.TransactionType.START_OF_DAY_FLOAT_IN, summaries);
		long withDrawalSum = getSumValue(zw.co.esolutions.ewallet.enums.TransactionType.WITHDRAWAL, summaries);
		long nonHolderWithDrawalSum = getSumValue(zw.co.esolutions.ewallet.enums.TransactionType.WITHDRAWAL_NONHOLDER, summaries);
		long totalPayOuts1 = withDrawalSum + nonHolderWithDrawalSum;
		long depositSum = getSumValue(zw.co.esolutions.ewallet.enums.TransactionType.DEPOSIT, summaries);
		String responseNarrative = "Day end has been approved successfully.";
		response.setDayEndReponse(responseNarrative);
		// Begin the processing

		long cashTendered = dayEnd.getCashTendered();
		long imbalance = 0;
		long balance = tellerFlaots + depositSum - totalPayOuts1;
		LOG.debug("Nw getting references");
		String cashTenderedRef = referenceProxy.generateUUID(sequenceName, prefix, year, 0, 999999999);
		LOG.debug(">>>>>>>>>>>>>>>>>>>>>>balance:::::::::::::::" + balance);
		LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>deposit sum::::::::::::::" + depositSum);
		LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>teller float::::::::::::::" + tellerFlaots);
		LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>payouts sum:::::::::::::::::" + totalPayOuts1);
		LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>cashtendered :::::::::::::::::" + cashTendered);
		LOG.debug(" receipts    " + (depositSum > 0));
		LOG.debug(" payouts    " + (totalPayOuts1 > 0));
		LOG.debug(" is it an over post overpost    " + (cashTendered > balance));
		LOG.debug(" is it an underpost   " + (cashTendered < balance));
		LOG.debug("balance      " + balance);

		LOG.debug("STEP A CREATING TXNS FOR DAY END>>>>>>>>>>>>>>>>>>>>>>>>>>>>>cashtendered :::::::::::::::::" + cashTendered);
		if (cashTendered > 0) {
			cashTenderedTxn = new ProcessTransaction();
			// cashTenderedTxn=populateAndCreateDayEndTransaction(dayEnd,cashTenderedRef,zw.co.esolutions.ewallet.enums.TransactionType.DAYEND_CASH_TENDERED,cashTendered);
			cashTenderedTxn = populateAndCreateDayEndTransaction(dayEnd, cashTenderedRef, zw.co.esolutions.ewallet.enums.TransactionType.DAYEND_CASH_TENDERED, userName, cashTendered);
			LOG.debug("cashTenderedTxn    result   " + cashTenderedTxn + "----------cashTenderedTxn id is  " + cashTenderedTxn.getId());

		}

		if (cashTendered > balance) {
			imbalancePresent = true;
			imbalance = cashTendered - balance;
			LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>surplus :::::::::::::::::" + imbalance);

			String overpostRef = referenceProxy.generateUUID(sequenceName, prefix, year, 0, 999999999);

			imbalancePostTxn = populateAndCreateDayEndTransaction(dayEnd, overpostRef, zw.co.esolutions.ewallet.enums.TransactionType.DAYEND_OVERPOST, userName, imbalance);
		}

		else if (balance > cashTendered) {
			imbalancePresent = true;
			imbalance = balance - cashTendered;
			LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>shoratge :::::::::::::::::" + imbalance);

			String underPostRef = referenceProxy.generateUUID(sequenceName, prefix, year, 0, 999999999);
			imbalancePostTxn = populateAndCreateDayEndTransaction(dayEnd, underPostRef, zw.co.esolutions.ewallet.enums.TransactionType.DAYEND_UNDERPOST, userName, imbalance);

		}

		// Perform DayEnd Postings

		LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>STEP B POSTING TO EQUATION ::::::::::::::::: IS IMBALANCE PRESENT>>>>>>>" + imbalancePresent);
		if (cashTenderedTxn != null && cashTenderedTxn.getId() != null && cashTendered > 0) {
			BankRequest cashTenderedRequest = this.populateDayEndBankRequest(dayEnd, cashTenderedTxn, userName);
			sendBankRequestToBank(cashTenderedRequest);

			this.processUtil.promoteTxnState(cashTenderedTxn, TransactionStatus.BANK_REQUEST, TransactionStatus.BANK_REQUEST.name());
			BankResponse postingResponse = this.processUtil.waitForBankResponse(cashTenderedRequest);

			LOG.debug(">>>>>>>>STEP B Response code  CASH TENDERED POSTING:::::   " + postingResponse.getResponseCode());
			// Dummy time out
			// cash tendered

			if (!ResponseCode.E000.equals(postingResponse.getResponseCode())) {
				if (ResponseCode.E830.equals(postingResponse.getResponseCode())) { // txn
					// has
					// timed
					// out
					LOG.debug("sending reversal for cash tendered>>>>>>>>>>");
					//processUtil.submitRequest(cashTenderedRequest, true);
					LOG.debug("Timing out cash tendered>>>>>>>>>>>>>>>"+cashTenderedTxn.getId());
					cashTenderedTxn=this.markTxnAsTimedOut(cashTenderedTxn, postingResponse);
						LOG.debug("sending reversal for cash tendered sent and returned >>>>>>>>>>");

				}else{
					LOG.debug(">>>>>>>> PROMOTING CASH-TENDER  >>>>> " + postingResponse.getNarrative());
					cashTenderedTxn = processUtil.promoteTxnState(cashTenderedTxn, TransactionStatus.FAILED, postingResponse.getNarrative());
				}

				
				cashTenderedTxn.setNarrative(postingResponse.getResponseCode().getDescription());

				
				// at this point mark the dayEnd Posting as failed or manual
				// resolve what is the correct path
				// update transactions and then update dayend and send response
				// to user
				LOG.debug(" 1  sending reversal for cash tendered sent and returned >>>>>>>>>>");
				dayEndStaus = DayEndStatus.MANUALLY_RESOLVE;
				// LOG.debug("2 sending reversal for cash tendered sent and returned >>>>>>>>>>");
				dayEnd.setStatus(DayEndStatus.MANUALLY_RESOLVE);
				// LOG.debug("3 sending reversal for cash tendered sent and returned >>>>>>>>>>"+cashTenderedTxn.getTransferType().name());
				// LOG.debug("3 sending reversal for cash tendered sent and returned >>>>>>>>>>"+response);

				// response.setDayEndReponse(responseNarrative+". Posting for cash tendered failed. Day end marked as manual resolve");
				response.setDayEndReponse("Day end approval has failed.");
				// LOG.debug("4 sending reversal for cash tendered sent and returned >>>>>>>>>>");
				response.setDayEndId(dayEnd.getDayEndId());
				// LOG.debug("5 sending reversal for cash tendered sent and returned >>>>>>>>>>");
				response.setTeller(userName);
				// LOG.debug("6 sending reversal for cash tendered sent and returned >>>>>>>>>>");
				response.setDetailsCashTenndered("Posting for " + cashTenderedTxn.getTransactionType().name() + " failed. Reason: " + cashTenderedTxn.getNarrative());
				// LOG.debug("7 sending reversal for cash tendered sent and returned >>>>>>>>>>");
				response.setStatus(ResponseCode.E777.name());
				// LOG.debug("8 >>>>>>>>FAILED TRANSACTION TXN unsuccessful >>>>> ");

			} else if (ResponseCode.E000.equals(postingResponse.getResponseCode())) {
				LOG.debug(">>>>>>>>Cash tendered successful IN ELSE state of transaction shld be changed to COMPLETED>>>>> " + postingResponse.getNarrative());
				cashTenderedTxn = processUtil.promoteTxnState(cashTenderedTxn, TransactionStatus.COMPLETED, postingResponse.getNarrative());
				LOG.debug("cash tendred status>>>>>>>>>>>>>>>>>>>>>" + cashTenderedTxn.getStatus().name());
				response.setDayEndId(dayEnd.getDayEndId());
				response.setTeller(userName);

				response.setDetailsCashTenndered("Posting for " + cashTenderedTxn.getTransactionType().name() + " successful. Narration: " + cashTenderedTxn.getNarrative());

			}

		}
		// /Imbalance processing
		LOG.debug("9 b4 imbalance >>>>>>>>>>");
		if (imbalancePresent) {
			System.out.println("IMBALANCE PRESENT>>>>>>>>>>>>>>>>>>>>POSTING BEGINS");
			imbalanceRequest = populateDayEndBankRequest(dayEnd, imbalancePostTxn, userName);
			sendBankRequestToBank(imbalanceRequest);

			this.processUtil.promoteTxnState(imbalancePostTxn, TransactionStatus.BANK_REQUEST, TransactionStatus.BANK_REQUEST.name());
			BankResponse imbalanceResponse = this.processUtil.waitForBankResponse(imbalanceRequest);

			LOG.debug(">>>>>>>> PROMOTING IMBALANCEW >>>>> " + imbalanceResponse.getNarrative());
			if (!ResponseCode.E000.equals(imbalanceResponse.getResponseCode())) {

				if (ResponseCode.E830.equals(imbalanceResponse.getResponseCode())) { // txn
					// has
					// timed
					// out
					LOG.debug("Imbalance posting timed out sending reversal>>>>>>>>>>>>>> "+imbalancePostTxn.getId());
					//processUtil.submitRequest(imbalanceRequest, true);
					imbalancePostTxn=this.markTxnAsTimedOut(imbalancePostTxn, imbalanceResponse);
					LOG.debug(">>>>>>.reversal sent>>>>>>>>>>>>>");
				}else{
				
				
				LOG.debug("1>>>>>>>> IMBALANCE   " + imbalanceResponse.getResponseCode().getDescription());
				LOG.debug("2>>>>>>>> IMBALANCE   " + imbalanceResponse.getNarrative());
				imbalancePostTxn = processUtil.promoteTxnState(imbalancePostTxn, TransactionStatus.FAILED, imbalanceResponse.getNarrative());
				imbalancePostTxn.setNarrative(imbalanceResponse.getResponseCode().getDescription());
				}
				

				// at this point mark the dayEnd Posting as failed or manual
				// resolve what is the correct path
				// update transactions and then update dayend and send response
				// to user
				LOG.debug("2 1>>>>>>>> IMBALANCE   ");
				response.setDayEndReponse("Day end approval has failed.");
				LOG.debug("2 2>>>>>>>> IMBALANCE   ");
				response.setDayEndId(dayEnd.getDayEndId());
				LOG.debug("2 3>>>>>>>> IMBALANCE   ");
				response.setTeller(userName);
				response.setDetailsImbalance("Posting for imbalance failed. Reason: " + imbalancePostTxn.getNarrative());
				LOG.debug("2 4>>>>>>> IMBALANCE   ");
				response.setStatus(ResponseCode.E777.name());
				dayEndStaus = DayEndStatus.MANUALLY_RESOLVE;
				dayEnd.setStatus(DayEndStatus.MANUALLY_RESOLVE);
				LOG.debug("2 5>>>>>>>> IMBALANCE   ");
				// return response;
			} else if (ResponseCode.E000.equals(imbalanceResponse.getResponseCode())) {
				LOG.debug("Imbalance posting a success>>>>>>>>>>>>>>");
				imbalancePostTxn = processUtil.promoteTxnState(imbalancePostTxn, TransactionStatus.COMPLETED, imbalanceResponse.getNarrative());
				System.out.println(">>>>>>>>>>imbalanceTxn status>>>>>>" + imbalancePostTxn.getStatus().name());
				response.setDetailsImbalance("Posting for " + imbalancePostTxn.getTransactionType().name() + " successful. Narrative: " + imbalancePostTxn.getNarrative());

				response.setDayEndId(dayEnd.getDayEndId());
				response.setTeller(userName);

			}
		}
		// this.processUtil.promoteTxnState(processTransaction,
		// TransactionStatus.BANK_REQUEST,
		// TransactionStatus.BANK_REQUEST.name());
		// BankResponse
		// response=this.processUtil.waitForBankResponse(bankRequest);
		LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>STEP C BOOK ENTRY :::::::::::::::::");

		boolean postingStatus = checkIfEquationPostingComplete(cashTenderedTxn, imbalancePostTxn, imbalancePresent);
		LOG.debug("posting status >>>>>>>>>>>>>>" + postingStatus);
		if (postingStatus && dayEnd != null) {
			LOG.debug("Postings all successfull");
			dayEndStaus = DayEndStatus.COMPLETED;
			dayEnd.setStatus(DayEndStatus.COMPLETED);
			response.setStatus(ResponseCode.E000.name());
		}

		LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>final response msg>>>>>>>>>>>" + response.getDayEndReponse());
		LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>STEP D Updating dayend:::::::::::::::::");
		// dayEnd=processUtil.editTellerDayEnd(dayEnd, userName);
		response.setDayEndStatus(dayEndStaus);
		LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>Final Day End status :::::::::::::::::" + dayEnd.getStatus());

		return response;
	}

	private boolean checkIfEquationPostingComplete(ProcessTransaction cashTenderedTxn, ProcessTransaction imbalancePostTxn, boolean imbalancePresent) {
		boolean cashTendered = true;
		boolean imbalanceStatus = true;
		if (cashTenderedTxn != null) {
			cashTendered = TransactionStatus.COMPLETED.equals(cashTenderedTxn.getStatus());
		}

		if (imbalancePresent) {
			System.out.println(">>>>>>>>>>>>>>imbalance status>>>>>>>" + imbalancePostTxn.getStatus().name());
			imbalanceStatus = TransactionStatus.COMPLETED.equals(imbalancePostTxn.getStatus());
		}
		System.out.println("CHECKS CAHSTENDERE>>>>>>>>>>>>>>>>>>>" + cashTendered);
		System.out.println("IS IT PRESENT" + imbalancePresent + "IMBALANCE>>>>>>>>>>>>>>>>>>>" + cashTendered);
		if (cashTendered && imbalanceStatus) {
			return true;
		}
		return false;
	}

	private ProcessTransaction populateAndCreateDayEndTransaction(DayEnd dayEnd, String ref, zw.co.esolutions.ewallet.enums.TransactionType transactionType, String userName, long amount) {
		ProcessTransaction processTxn = new ProcessTransaction();
		processTxn.setAmount(amount);
		processTxn.setMessageId(ref);
		processTxn.setDayEndId(dayEnd.getDayEndId());
		processTxn.setId(ref);
		processTxn.setBranchId(dayEnd.getBranchId());
		processTxn.setTransactionType(transactionType);
		ProcessTransaction accountSetter = getEquationAccounts(transactionType, dayEnd);
		processTxn.setSourceAccountNumber(accountSetter.getSourceAccountNumber());
		processTxn.setSourceEQ3AccountNumber(accountSetter.getSourceEQ3AccountNumber());
		processTxn.setDestinationAccountNumber(accountSetter.getDestinationAccountNumber());
		processTxn.setDestinationEQ3AccountNumber(accountSetter.getDestinationEQ3AccountNumber());
		processTxn.setTransactionLocationId(dayEnd.getBranchId());
		processTxn.setTransactionLocationType(TransactionLocationType.BANK_BRANCH);
		
		LOG.debug("1. Populate dayendTxn>>>>>>>>>>Equation Source Account Number>>>>>>>>>>>>" + processTxn.getSourceEQ3AccountNumber() + ">>>Equation Destination Account Number>>>>" + processTxn.getDestinationEQ3AccountNumber());
		LOG.debug("2. Populate dayendTxn>>>>>>>>>>Source Account Number>>>>>>>>>>>>" + processTxn.getSourceAccountNumber() + ">>>Destination Account Number>>>>" + processTxn.getDestinationAccountNumber());

		// processTxn.set
		try {
			// processTxn=createProcessTransaction(processTxn, userName);
			processTxn = processUtil.createProcessTransaction(processTxn);
			LOG.debug("TRXN ID>>>>>>>>>>>>" + processTxn.getId() + ">>>>>>>>>type" + processTxn.getTransactionType().name());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return processTxn;
	}

	public ProcessTransaction getEquationAccounts(zw.co.esolutions.ewallet.enums.TransactionType txnType, DayEnd dayEnd) {
		ProcessTransaction processTransaction = new ProcessTransaction();

		String sourceAccountNumber = "";
		String targetAccountNumber = "";
		try {
			switch (txnType) {

			case DAYEND_CASH_TENDERED:
				// LOG.debug("  getting accounts  --------------------------------------------------------txn type  "
				// + txnType.name());
				sourceAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.BRANCH_CASH_ACCOUNT);
				targetAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT);

				break;

			case DAYEND_RECEIPTS:
				// LOG.debug("  getting accounts  --------------------------------------------------------txn type  "
				// + txnType.name());
				sourceAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.BRANCH_CASH_ACCOUNT);
				targetAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT);

				break;
			case DAYEND_PAYOUTS:
				sourceAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT);
				targetAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.BRANCH_CASH_ACCOUNT);
				// LOG.debug(" payout accounting entries    source account     "
				// + sourceAccountNumber);
				// LOG.debug(" payout accounting entries    target account    "
				// + targetAccountNumber);
				break;

			case DAYEND_UNDERPOST:
				targetAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT);
				sourceAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.EWALLET_BALANCING_SUSPENSE_ACCOUNT);

				break;

			case DAYEND_FLOATS:
				// LOG.debug(">>>>>>>>>>floats>>>>>>>>>>>>>>>");
				sourceAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT);
				targetAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.BRANCH_CASH_ACCOUNT);

				break;

			case DAYEND_OVERPOST:
				targetAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.EWALLET_BALANCING_SUSPENSE_ACCOUNT);
				sourceAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT);

				break;
			default:
				break;
			}

			LOG.debug(">>getAccount>>>>>>>>Source Account Number >>>>>>>>>>>>>>>>>>>" + sourceAccountNumber);
			LOG.debug(">>getAccount>>>>>>>>Destination Account Number >>>>>>>>>>>>>>>" + targetAccountNumber);
			processTransaction.setSourceEQ3AccountNumber(sourceAccountNumber);
			processTransaction.setSourceAccountNumber(sourceAccountNumber);
			processTransaction.setDestinationAccountNumber(targetAccountNumber);
			processTransaction.setDestinationEQ3AccountNumber(targetAccountNumber);
		} catch (Exception e1) {

			e1.printStackTrace();
			return processTransaction;

		}
		return processTransaction;
	}

	private BankRequest populateDayEndBankRequest(DayEnd dayEnd, ProcessTransaction processTxn, String userName) {
		LOG.debug("Process equation posting for dayEnd");
		long amount = processTxn.getAmount();
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		Profile profile = profileService.findProfileById(dayEnd.getTellerId());
		String tellerUserName = profile.getUserName();

		String sourceAccountNumber = processTxn.getSourceAccountNumber();

		String targetAccountNumber = processTxn.getDestinationAccountNumber();
		zw.co.esolutions.ewallet.enums.TransactionType txnType = processTxn.getTransactionType();
		BankRequest postRequest = new BankRequest();
		LOG.debug("    --------------------------------------------------------txn type  " + txnType.name());

		try {
			switch (txnType) {

			case DAYEND_CASH_TENDERED:
				// LOG.debug("  getting accounts  --------------------------------------------------------txn type  "
				// + txnType.name());
				sourceAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.BRANCH_CASH_ACCOUNT);
				targetAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT);

				break;

			case DAYEND_RECEIPTS:
				LOG.debug("  getting accounts  --------------------------------------------------------txn type  " + txnType.name());
				sourceAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.BRANCH_CASH_ACCOUNT);
				targetAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT);

				break;
			case DAYEND_PAYOUTS:
				sourceAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT);
				targetAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.BRANCH_CASH_ACCOUNT);
				LOG.debug(" payout accounting entries    source account     " + sourceAccountNumber);
				LOG.debug(" payout accounting entries    target account    " + targetAccountNumber);
				break;

			case DAYEND_UNDERPOST:
				targetAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT);
				sourceAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.EWALLET_BALANCING_SUSPENSE_ACCOUNT);

				break;

			case DAYEND_FLOATS:
				LOG.debug(">>>>>>>>>>floats>>>>>>>>>>>>>>>");
				sourceAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT);
				targetAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.BRANCH_CASH_ACCOUNT);

				break;

			case DAYEND_OVERPOST:
				targetAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.EWALLET_BALANCING_SUSPENSE_ACCOUNT);
				sourceAccountNumber = getAccountByAccountType(dayEnd, zw.co.esolutions.ewallet.enums.BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT);

				break;
			default:
				break;
			}

		} catch (Exception e1) {
			LOG.debug("Exception in sending process txn for " + txnType);
			// processTxn.setStatus(TransactionStatus.MANUAL_RESOLVE);
			e1.printStackTrace();
			return postRequest;

		}

		if (amount != 0) {

			LOG.debug("requeest set fot    " + txnType);
			try {

				String reference = processTxn.getMessageId();
				BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
				BankBranch bankBranch = bankService.findBankBranchById(processTxn.getBranchId());

				String branchName = bankBranch.getName();
				if (bankBranch == null || bankBranch.getId() == null) {
					LOG.debug("STEP : Found a null bank branch, cant proceed " + bankBranch);
					throw new Exception("Bank Branch is could not be found for this teller float.");
				}

				// String narrative = tellerUserName+ "/" + ;
				String narrative = tellerUserName + "/" + userName + "/" + branchName;
				processTxn.setNarrative(narrative);

				LOG.debug("  sourceaccount number  " + sourceAccountNumber);
				LOG.debug("  target account number  " + targetAccountNumber);
				// BankRequest postRequest = new BankRequest();
				postRequest.setAmount(amount);
				postRequest.setSourceAccountNumber(sourceAccountNumber);
				postRequest.setTargetAccountNumber(targetAccountNumber);

				postRequest.setReference(reference);
				postRequest.setTransactionType(txnType);
				LOG.debug("  target account number  " + postRequest.getReference());
				LOG.debug("  target account number  " + postRequest.getTransactionType().name());
				LOG.debug("  target account number  " + postRequest.getAmount());
				LOG.debug("  target account number  " + postRequest.getSourceAccountNumber());
				LOG.debug("  target account number  " + postRequest.getTargetAccountNumber());

				postRequest.setNarrative(narrative);
				LOG.debug("  narrative----------------------------------------for posting  " + postRequest.getNarrative());
				processUtil.submitRequest(postRequest, true);
				LOG.debug("request sent");
				// set process txn status and update processTxn
				processTxn.setStatus(TransactionStatus.BANK_REQUEST);
				// updateProcessTxn(processTxn,userName);
				LOG.debug("requeest set fot    " + txnType);
				// throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
				LOG.debug("Exception in sending process txn for " + txnType);
				processTxn.setStatus(TransactionStatus.MANUAL_RESOLVE);
				// updateProcessTxn(processTxn,userName);
				// throw new Exception(e.getMessage());
				// return processTxn;
			}
		} else if (amount == 0) {
			// posting not done

		}
		return postRequest;

	}

	public long getSumValue(zw.co.esolutions.ewallet.enums.TransactionType type, List<DayEndSummary> summaries) {
		System.out.println("in get SUM VALUE ::::   TYPE " + type.name());
		System.out.println("in get SUM VALUE ::::   Number of summaries " + summaries.size());
		for (DayEndSummary summary : summaries) {
			if (type.equals(summary.getTransactionType())) {
				System.out.println(summary.getTransactionType() + "Value >>>>" + summary.getValueOfTxns());
				return summary.getValueOfTxns();
			}

		}
		return 0;
	}

	private long getSum(List<DayEndSummary> summaries, TransactionType type) {
		System.out.println("in get SUM::::");

		if (summaries != null) {
			for (DayEndSummary summary : summaries) {
				if (type.equals(summary.getTransactionType())) {
					LOG.debug(summary.getTransactionType().name() + "Summmmmm   --------------------" + summary.getValueOfTxns());
					return summary.getValueOfTxns();
				}
			}
		}
		return 0;
	}

	public ProcessTransaction approveMarkedTransaction(String messageId, TransactionStatus newStatus, String narrative, String userName) throws Exception {
		ProcessTransaction txn = this.getProcessTransactionByMessageId(messageId);
		txn.setStatus(newStatus);
		txn.setNarrative(narrative);

		this.updateProcessTxn(txn, userName);
LOG.debug(">>>>>>>>>>>>in approvemarkedTxn returning after update....");
		return txn;
	}
	
	public String scheduleCollectionTimer(Date runDate) {
		LOG.debug("Schedule NHW Collection called.");
		try {
			this.nonHolderTxfProcessor.scheduleTimer(null, null, null, true ,runDate);
		} catch (Exception e) {
			e.printStackTrace();
			return "Schedulling NHW Collection failed, an error occurred.";
		}
		return ResponseCode.E000.name();		
	}
	
	public String  cancellCollectionTimer(){
		LOG.debug("cancel NHW Collection called.");
		try{
			this.nonHolderTxfProcessor.cancelTimer();
		}catch (Exception e) {
			e.printStackTrace();
			return "Cancelling of Non Holder Transfer Collection Timer failed, an error occurred.";
		}
		return ResponseCode.E000.name();
	}
	
	public String  saveCollectionConfigurationData(String email , String timeToRun , String expPeriod){
		LOG.debug("saveCollectionConfigurationData called .");
		try{
			String[] hrMin = timeToRun.split(":");
			config.setConfiguration(SystemConstants.NHW_EMAIL, email);
			config.setConfiguration(SystemConstants.NHW_PROCESSING_HOUR, hrMin[0]);
			config.setConfiguration(SystemConstants.NHW_PROCESSING_MINUTE, hrMin[1]);
			config.setConfiguration(SystemConstants.NHW_REVERSAL_EXPIRATION_PERIOD, expPeriod);
		}catch (Exception e) {
			e.printStackTrace();
			return "Savin of Collection Reversal Timer failed, an error occurred.";
		}
		return ResponseCode.E000.name();
	}

	public String cancellTxnReversalTimer() {
		LOG.debug("cancel NHW Collection called.");
		try{
			this.timedOutTxnProcessor.cancelTimer(TXN_REVERSAL_TIMER);
		}catch (Exception e) {
			e.printStackTrace();
			return "Cancelling of Non Holder Transfer Collection Timer failed, an error occurred.";
		}
		return ResponseCode.E000.name();
	}

	public String scheduleTxnReversalTimer(Date runDate) {
		LOG.debug("schedule Txn Reversal Timer called.");
		try {
			this.timedOutTxnProcessor.scheduleTimer(runDate);
		} catch (Exception e) {
			e.printStackTrace();
			return "Schedulling NHW Collection failed, an error occurred.";
		}
		return ResponseCode.E000.name();		
	}

	public String saveTxnReversalConfigData(String runningTime, String expPeriod) {
		LOG.debug("saveTxnReversalConfigData called . Hour "+expPeriod+" : RunningTime "+runningTime);
		try{
			config.setConfiguration(SystemConstants.TXN_REVERSAL_PROCESSING_HOUR, runningTime);
			config.setConfiguration(SystemConstants.TXN_REVERSAL_EXPIRATION_PERIOD, expPeriod);
		}catch (Exception e) {
			e.printStackTrace();
			return "Savin of Collection Reversal Timer failed, an error occurred.";
		}
		return ResponseCode.E000.name();
		
	}


	public DayEnd updateDayEnd(DayEnd dayEnd, String userName) throws Exception {
		return dayEndBean.disapproveDayEnd(dayEnd, userName);
		
	}
	
	@Override
	public ProcessResponse depositAgentCash(DepositInfo depositInfo) {
		String messageId;
		// Populating Txn Process
		ProcessTransaction processTxn = null;
		ProcessResponse res = new ProcessResponse();
		if (depositInfo == null) {
			res.setResponseCode(ResponseCode.E777.name());
			return res;
		}
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		ReferenceGeneratorServiceSOAPProxy refService = new ReferenceGeneratorServiceSOAPProxy();
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		try {
			LOG.debug("Deposit Info mobile number : " + depositInfo.getMobileNumber() + " and profile id " + depositInfo.getProfileId());

			MobileProfile mobileProfile = customerService.getMobileProfileByMobileNumber(depositInfo.getMobileNumber());
			LOG.debug("Found mobile profile ..." + mobileProfile);
			Profile profile = profileService.findProfileById(depositInfo.getProfileId());
			LOG.debug("Found a profile " + profile);
			
			processTxn = this.createAgentDepositProcessTransaction(depositInfo, mobileProfile, profile);

			LOG.debug("Done populating the txn........");
			// Create Txn Process
			processTxn = this.processUtil.createProcessTransaction(processTxn);
			LOG.debug("Done createProcess txn..");
			this.processUtil.promoteTxnState(processTxn, TransactionStatus.DRAFT, TransactionStatus.DRAFT.name());
			LOG.debug("Done promoting txn state...");

			try {
				processTxn = this.processUtil.findProcessTransactionById(processTxn.getId());
				processTxn = this.processUtil.addApplicableTariffToProcessTxn(processTxn);
				
				Agent agent = agentService.getAgentByCustomerId(mobileProfile.getCustomer().getId());

				// Populating Bank Req
				BankRequest bankReq = this.processUtil.populateBankRequest(processTxn);
				bankReq.setAmount(depositInfo.getAmount());
				bankReq.setSourceBankCode(depositInfo.getBankCode());
				bankReq.setSourceMobileNumber(depositInfo.getMobileNumber());
				bankReq.setTransactionType(zw.co.esolutions.ewallet.enums.TransactionType.AGENT_CASH_DEPOSIT);
				bankReq.setCurrencyISOCode("USD");
				bankReq.setAgentNumber(agent.getAgentNumber());

				GenerateKey.throwsException();
				
				this.sendBankRequestToBank(bankReq);

				this.processUtil.promoteTxnState(processTxn, TransactionStatus.BANK_REQUEST, TransactionStatus.BANK_REQUEST.name());

				// wait for response

				BankResponse bankResponse = processUtil.waitForBankResponse(bankReq);

				if (ResponseCode.E000.equals(bankResponse.getResponseCode())) {
					// post to ewallet
					ResponseCode resp = this.processUtil.postEWalletDepositTxn(processTxn);
					if (!ResponseCode.E000.equals(resp)) {
						res.setResponseCode(resp.name());
						return res;
					}
					// success in ewallet
					res.setResponseCode(resp.name());
					res.setAmount(processTxn.getAmount());
					res.setBalance(processTxn.getBalance());
					res.setMessageId(processTxn.getMessageId());

					LOG.debug(">>>>>>>> Done Deposit Book entry posting");

					// send notif
					if (ResponseCode.E000.equals(resp)) {
						processTxn = processUtil.promoteTxnState(processTxn, TransactionStatus.COMPLETED, bankResponse.getNarrative());
						this.notifyAgentOfDepositSuccess(processTxn, bankResponse);
					}

				} else {

						if (ResponseCode.E830.equals(bankResponse.getResponseCode())) { // txn
																					// has
							LOG.debug("Agent deposit trxn timing out >>>>>>>>"+processTxn.getId());
							// timed
																					// out
						//processUtil.submitRequest(bankReq, true);
						processTxn=markTxnAsTimedOut(processTxn, bankResponse);


					}
					else{
						processTxn = processUtil.promoteTxnState(processTxn, TransactionStatus.FAILED, bankResponse.getNarrative());

						
					}
					res.setResponseCode(bankResponse.getResponseCode().name());
					LOG.debug("DEPOSIT FAILED : ResponseCode > " + bankResponse.getResponseCode().name() + " : " + bankResponse.getNarrative());

				}
			} catch (Exception e) {
				e.printStackTrace();
				res.setResponseCode(ResponseCode.E818.name());
				if(processTxn != null) {
					processTxn = processUtil.promoteTxnState(processTxn, TransactionStatus.FAILED, ResponseCode.E818.getDescription());
				}
				return res;
			}

		} catch (Exception e) {
			e.printStackTrace();
			res.setResponseCode(ResponseCode.E818.name());
			if (processTxn != null) {
				try { processTxn = processUtil.promoteTxnState(processTxn,
						TransactionStatus.FAILED, ResponseCode.E818
								.getDescription()); 
				} catch (Exception e1) {
					// TODO: handle exception
				}
			}
			return res;
		}

		return res;
	}
	
	public String validateAgentDeposit(String sourceMobileId, long amount,String bankId, TransactionLocationType locationType)throws Exception {
		// Local variables to be used
		LOG.debug(">>>>>>>>>>>>>>>> In validate agent deposit >>>>>>>>>>>>");
		Date txnDate = new Date();
		MobileProfile sourceProfile;
		MobileProfile destnProfile;
		Limit limit;
		Limit dailyLimit;
		Limit balanceLimit;
		BankAccount bankAccount;
		long tariffAmount;
		Tariff tariff;
		long dailyAmount;

		try {
			CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
			ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
			LimitServiceSOAPProxy limitService = new LimitServiceSOAPProxy();

			// Retrieve Approppriate Profiles
			sourceProfile = customerService.findMobileProfileById(sourceMobileId);

			dailyAmount = this.getDailyAmountByCustomerIdAndTxnType(sourceProfile.getCustomer().getId(), zw.co.esolutions.ewallet.enums.TransactionType.AGENT_CASH_DEPOSIT);

			// Retrieve Source & Destination Accounts Details
			bankAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(sourceProfile.getCustomer().getId(), BankAccountType.AGENT_EWALLET, OwnerType.AGENT, sourceProfile.getMobileNumber());

			//Setting Bank Account Class to NONE
			bankAccount.setAccountClass(zw.co.esolutions.ewallet.bankservices.service.BankAccountClass.NONE);
			
			// Retrieve Source & Destination Limits
			limit = limitService.getValidLimitOnDateByBankId(TransactionType.AGENT_CASH_DEPOSIT, BankAccountClass.valueOf(bankAccount.getAccountClass().toString()), DateUtil.convertToXMLGregorianCalendar(txnDate), LimitPeriodType.TRANSACTION, bankId);

			if ((limit == null) || (limit.getId() == null)) {
				return ResponseCode.E823.getDescription();
			}

			/*
			 * // Retrieve Source & Destination Limits dailyLimit =
			 * limitService.getValidLimitOnDateByBankId(TransactionType.DEPOSIT,
			 * BankAccountClass
			 * .valueOf(bankAccount.getAccountClass().toString()),
			 * DateUtil.convertToXMLGregorianCalendar(txnDate),
			 * LimitPeriodType.DAILY, bankId);
			 * 
			 * if(dailyLimit == null) { return
			 * ResponseCode.E826.getDescription(); // No daily Limit }
			 */

			balanceLimit = limitService.getValidLimitOnDateByBankId(TransactionType.AGENT_ACCOUNT_BALANCE, BankAccountClass.valueOf(bankAccount.getAccountClass().toString()), DateUtil.convertToXMLGregorianCalendar(txnDate), LimitPeriodType.TRANSACTION, bankId);

			if (balanceLimit == null || balanceLimit.getId() == null) {
				balanceLimit = limitService.getValidLimitOnDateByBankId(TransactionType.AGENT_ACCOUNT_BALANCE, BankAccountClass.valueOf(bankAccount.getAccountClass().toString()), DateUtil.convertToXMLGregorianCalendar(txnDate), LimitPeriodType.DAILY, bankId);
			}
			
			if ((limit == null || limit.getId() == null) || (balanceLimit == null || balanceLimit.getId() == null)) {
				return ResponseCode.E823.getDescription();
			}

			/*
			 * if(dailyLimit.getMaxValue() < dailyAmount + amount) { return
			 * ResponseCode
			 * .E829.getDescription()+" Maximum daily limit is "+MoneyUtil
			 * .convertCentsToDollarsPatternNoCurrency
			 * (dailyLimit.getMaxValue())+ " and you transacted " +
			 * ""+MoneyUtil.
			 * convertCentsToDollarsPatternNoCurrency(dailyAmount)+"."; }
			 */

			// Check for Deposit eligibility
			if (limit.getMinValue() > amount) {
				return ResponseCode.E824.getDescription() + " Minimum deposit is " + MoneyUtil.convertCentsToDollarsPatternNoCurrency(limit.getMinValue()) + "."; // Deposit
																																									// amount
																																									// is
																																									// below
				// min
				// Deposit

			}
			LOG.debug(">>>>>>>>>>>>>>>>>>>>. Max = " + limit.getMaxValue() + " >>>>>>>>>>>>>>>>> Value = " + amount);
			if (limit.getMaxValue() < amount) {
				return ResponseCode.E825.getDescription() + " Maximum deposit allowed is " + MoneyUtil.convertCentsToDollarsPatternNoCurrency(limit.getMaxValue()) + "."; // Deposit
																																											// amount
																																											// is
																																											// above
				// max
				// Deposit

			}
		
//			Customer c = sourceProfile.getCustomer();
//			LOG.debug(">>>>>>>>>>>>>> The value of customer is "+ c.getCustomerClass().name());
			// Applying Tariffs Now
			// Limits are Ok then look for a tariff
			tariff = this.getApplicableTariff(bankId, CustomerClass.AGENT, zw.co.esolutions.ewallet.tariffservices.service.TransactionType.AGENT_CASH_DEPOSIT, locationType, amount);
//			LOG.debug(">>>>>>>>>>>>>> The value of customer is "+ c.getCustomerClass());
			if ((tariff == null) || (tariff.getId() == null)) {
				return ResponseCode.E822.getDescription();
			}

		} catch (Exception e) {
			LOG.debug(">>>>>>>>>>>>>>>>>>>>>>> In Process Service ::: ");
			e.printStackTrace(System.out);
			throw e;
		}

		return ResponseCode.E000.name();

	}
	
	private ProcessTransaction createAgentDepositProcessTransaction(DepositInfo depositInfo, MobileProfile mobileProfile, Profile profile) throws Exception {
		LOG.debug(">>>>>>>>>>>>>>>>>>>>> Running createAgentDepositProcessTxn >>>>>>>>>>>>>");
		ReferenceGeneratorServiceSOAPProxy refProxy = new ReferenceGeneratorServiceSOAPProxy();

		String messageId = refProxy.generateUUID(zw.co.esolutions.ewallet.enums.TransactionType.DEPOSIT.name(), "D", Formats.yearFormatTwoDigit.format(new Date(System.currentTimeMillis())), 0, 1000000000L - 1L);
		ProcessTransaction processTxn = new ProcessTransaction();
		processTxn.setMessageId(messageId);
		processTxn.setId(messageId);
		processTxn.setAmount(depositInfo.getAmount());
		processTxn.setBalance(depositInfo.getRunningBalance());
		processTxn.setNarrative("Agent CASH Deposit");
		processTxn.setBankReference(depositInfo.getBankCode());
		processTxn.setTransactionType(zw.co.esolutions.ewallet.enums.TransactionType.AGENT_CASH_DEPOSIT);
		processTxn.setSourceMobileId(mobileProfile.getId());
		processTxn.setProfileId(depositInfo.getProfileId());
		processTxn.setFromBankId(mobileProfile.getBankId());
		processTxn.setToBankId(mobileProfile.getBankId());
		// we all know that for a deposit the banks are indeer the same
		processTxn.setSourceAccountNumber(mobileProfile.getMobileNumber());
		processTxn.setDestinationAccountNumber(mobileProfile.getMobileNumber());
		processTxn.setSourceEQ3AccountNumber(mobileProfile.getMobileNumber());
		processTxn.setDestinationEQ3AccountNumber(mobileProfile.getMobileNumber());
		processTxn.setCustomerId(mobileProfile.getCustomer().getId());
		// branch Id MUST be set to the customer's branch
		processTxn.setBranchId(mobileProfile.getCustomer().getBranchId());
		// we postto the teller's branch
		processTxn.setTransactionLocationType(TransactionLocationType.BANK_BRANCH);
		processTxn.setTransactionLocationId(profile.getBranchId());
		processTxn.setSourceMobile(depositInfo.getMobileNumber());
		processTxn.setTargetMobileId(processTxn.getSourceMobileId());
		processTxn.setTargetMobile(processTxn.getSourceMobile());
		processTxn.setCustomerName(mobileProfile.getCustomer().getLastName() + " " + mobileProfile.getCustomer().getFirstNames());
		// required to set transaction location id and type
		processTxn = this.processUtil.populateEquationAccoountsByTransactionType(processTxn);

		return processTxn;
	}
	
	private void notifyAgentOfDepositSuccess(ProcessTransaction txn, BankResponse bankResp) {
		try {
			RequestInfo requestInfo = processUtil.populateRequestInfo(txn);
			requestInfo.setTargetMobile(requestInfo.getSourceMobile());
			BankAccount bankAccount = new BankServiceSOAPProxy().getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getCustomerId(), BankAccountType.AGENT_EWALLET, OwnerType.AGENT, txn.getSourceMobile());

			ResponseInfo responseInfo = new ResponseInfo(bankResp.getNarrative(), ResponseCode.E000, requestInfo, ResponseType.NOTIFICATION, bankAccount.getRunningBalance(), bankAccount.getRunningBalance(), 0L, txn.getMessageId());
			LOG.debug("Sending DEPOSIT REPLY (to SWITCH) for transaction...." + txn.getMessageId());
			processUtil.submitResponse(responseInfo);
			LOG.debug("Done.........");
		} catch (Exception e) {
			LOG.debug("FAILED TO NOTIFY CUSTOMER");
		}

	}

	public String cancellCommissionTimer() {
		LOG.debug("schedule Txn Reversal Timer called.");
		try {
			this.agentCommissionProcessor.cancelTimer();
		} catch (Exception e) {
			e.printStackTrace();
			return "Schedulling NHW Collection failed, an error occurred.";
		}
		return ResponseCode.E000.name();
		
	}

	public String scheduleCommissionTimer(Date runDate) {
		LOG.debug("schedule Commission Sweep Timer called.");
		try {
			this.agentCommissionProcessor.scheduleTimer(runDate);
		} catch (Exception e) {
			e.printStackTrace();
			return "Schedulling Commission Sweep Timer failed, an error occurred.";
		}
		return ResponseCode.E000.name();
		
	}

	public String saveCommissionConfigData(String email, String timeToRun,
			String period) {
		LOG.debug("Save Commission Timer Config data called . Hour "+period+" : RunningTime "+timeToRun+" : Email "+email);
		String[] hrMin = timeToRun.split(":");
		try{
			config.setConfiguration(SystemConstants.COMMISSION_SWEEPING_HOUR, hrMin[0]);
			config.setConfiguration(SystemConstants.COMMISSION_SWEEPING_MINUTE, hrMin[1]);
			config.setConfiguration(SystemConstants.COMMISSION_SWEEPING_PERIOD, period);
			config.setConfiguration(SystemConstants.COMMISSION_SWEEPING_EMAIL, email);
		}catch (Exception e) {
			e.printStackTrace();
			return "Schedulling NHW Collection failed, an error occurred.";
		}
		return ResponseCode.E000.name();
		
	}
	
	private MobileProfile getPrimaryMobile(String customerId) {
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		List<MobileProfile> mpList = customerService.getMobileProfileByCustomer(customerId);

		for (MobileProfile mp: mpList) {
			if (mp.isPrimary()) {
				return mp;
			}
		}
		return null;
	}

	public ProcessResponse processAlertRegistration(String bankAccountId, String status) {
		
		ProcessResponse response = new ProcessResponse();
		BankResponse bankResponse = new BankResponse();
		
		try {
			
			LOG.debug("@@ ALERT REG: " + bankAccountId + "|" + status);

			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
			CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
			ReferenceGeneratorServiceSOAPProxy refProxy = new ReferenceGeneratorServiceSOAPProxy();
			
			BankAccount bankAccount = bankService.findBankAccountById(bankAccountId);
			
			LOG.debug("BankAccount is: " + bankAccount.getAccountNumber());
			
			if (NumberUtil.validateMobileNumber(bankAccount.getAccountNumber())) {
				LOG.debug("[REJECT] Mobile Account found: " + bankAccount.getType());
				response.setResponseCode(ResponseCode.E505.name());
				response.setNarrative("Mobile accounts are not enabled to send alerts");
				return response;
			}
			
			if (bankAccountHasActiveAlertOptions(bankAccountId)) {
				LOG.debug("BankAccount has ACTIVE Alert Options..");
				if (EWalletConstants.ALERT_OPTION_STATUS_DISABLED.equals(status)) {
					LOG.debug("ACTION: Ignore Disable alert Option and return success!");
					response.setResponseCode(ResponseCode.E000.name());
					response.setNarrative("Alert option updated successfully");
					return response;	
				}
			}
			
			MobileProfile primaryMobile = this.getPrimaryMobile(bankAccount.getAccountHolderId());
			
			LOG.debug("Primary Mobile is: " + primaryMobile.getMobileNumber());

			String messageId = refProxy.generateUUID(zw.co.esolutions.ewallet.enums.TransactionType.ADJUSTMENT.name(), "A", Formats.yearFormatTwoDigit.format(new Date(System.currentTimeMillis())), 0, 1000000000L - 1L);

			RequestInfo requestInfo = new RequestInfo();
			requestInfo.setMessageId(messageId);
			requestInfo.setBranchId(bankAccount.getBranch().getId());
			requestInfo.setLocationType(TransactionLocationType.BANK_BRANCH);
			requestInfo.setMno(MobileNetworkOperator.ECONET);
			requestInfo.setSourceAccountNumber(bankAccount.getAccountNumber());
			requestInfo.setStatus(TransactionStatus.DRAFT);
			requestInfo.setSourceBankId(bankAccount.getBranch().getBank().getId());
			requestInfo.setSourceMobile(primaryMobile.getMobileNumber());
			
			if (EWalletConstants.ALERT_OPTION_STATUS_ACTIVE.equals(status)) {
				requestInfo.setTransactionType(zw.co.esolutions.ewallet.enums.TransactionType.ALERT_REG);
			} else {
				requestInfo.setTransactionType(zw.co.esolutions.ewallet.enums.TransactionType.ALERT_DEREG);
			}

			LOG.debug("Alert txn type is: " + requestInfo.getTransactionType());

			LOG.debug("Begin populate ProcessTxn");

			ProcessTransaction txn = processUtil.populateProcessTransaction(requestInfo);
			
			LOG.debug("Populated ProcessTxn");

			txn = processUtil.createProcessTransaction(txn);
			
			LOG.debug("Created ProcessTxn");
			
			BankRequest bankRequest = processUtil.populateBankRequest(txn);
			
			this.sendBankRequestToBank(bankRequest);

			this.processUtil.promoteTxnState(txn, TransactionStatus.BANK_REQUEST, TransactionStatus.BANK_REQUEST.name());

			// wait for response

			bankResponse = processUtil.waitForBankResponse(bankRequest);

			if (ResponseCode.E000.equals(bankResponse.getResponseCode()) || ResponseCode.E831.equals(bankResponse.getResponseCode())) {
				LOG.debug("Successful registration or already registered..");
				txn = processUtil.promoteTxnState(txn, TransactionStatus.COMPLETED, bankResponse.getNarrative());
				
			//	this.notifyCustomerOfAlertRegSuccess(txn, bankResponse);
				
				response.setResponseCode(ResponseCode.E000.name());
				response.setNarrative("Alert option updated successfully");
				return response;

			} else {

				txn = processUtil.promoteTxnState(txn, TransactionStatus.FAILED, bankResponse.getNarrative());
					
			}

		} catch (Exception e) {
			e.printStackTrace();
			bankResponse.setResponseCode(ResponseCode.E505);
			LOG.fatal("Failed to perform ALERT REG");
		}
		
		response.setResponseCode(bankResponse.getResponseCode().name());
		response.setNarrative(bankResponse.getResponseCode().getDescription());
		return response;
		
	}
	
	public List<AlertOption> getAlertOptionList(String accountId) {
		
		AlertsServiceSOAPProxy alertsService = new AlertsServiceSOAPProxy();
					
		List<AlertOption> alerts = alertsService.getAlertOptionByBankAccountId(accountId);
			
		return alerts;
		
	}
	
	public boolean bankAccountHasActiveAlertOptions(String accountId) {
		
		for (AlertOption alert: this.getAlertOptionList(accountId)) {
			if (AlertOptionStatus.ACTIVE.equals(alert.getStatus())) {
				return true;
			}
		}
		
		return false;
			
	}
	
	private void notifyCustomerOfAlertRegSuccess(ProcessTransaction txn, BankResponse bankResp) {
		try {
			RequestInfo requestInfo = processUtil.populateRequestInfo(txn);
			requestInfo.setTargetMobile(requestInfo.getSourceMobile());
			BankAccount bankAccount = new BankServiceSOAPProxy().getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getCustomerId(), BankAccountType.E_WALLET, OwnerType.CUSTOMER, txn.getSourceMobile());

			ResponseInfo responseInfo = new ResponseInfo(bankResp.getNarrative(), ResponseCode.E000, requestInfo, ResponseType.NOTIFICATION, 0L, 0L, 0L, txn.getMessageId());
			LOG.debug("Sending ALERT REG REPLY (to SWITCH) for transaction...." + txn.getMessageId());
			processUtil.submitResponse(responseInfo);
			LOG.debug("Done.........");
		} catch (Exception e) {
			LOG.debug("FAILED TO NOTIFY CUSTOMER");
		}

	}
	
}
