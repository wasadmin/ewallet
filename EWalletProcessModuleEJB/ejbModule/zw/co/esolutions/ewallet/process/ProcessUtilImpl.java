package zw.co.esolutions.ewallet.process;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.ewallet.agentservice.service.Agent;
import zw.co.esolutions.ewallet.agentservice.service.AgentServiceSOAPProxy;
import zw.co.esolutions.ewallet.agentservice.service.ProfileStatus;
import zw.co.esolutions.ewallet.bankservices.service.AccountBalance;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountStatus;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountType;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.bankservices.service.BankStatus;
import zw.co.esolutions.ewallet.bankservices.service.ChargePostingInfo;
import zw.co.esolutions.ewallet.bankservices.service.EWalletPostingResponse;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.bankservices.service.Transaction;
import zw.co.esolutions.ewallet.bankservices.service.TransactionCategory;
import zw.co.esolutions.ewallet.bankservices.service.TransactionPostingInfo;
import zw.co.esolutions.ewallet.bankservices.service.TransactionType;
import zw.co.esolutions.ewallet.bankservices.service.TransactionUniversalPojo;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.CustomerAutoRegStatus;
import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.CustomerStatus;
import zw.co.esolutions.ewallet.customerservices.service.GenerateTxnPassCodeResp;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfileStatus;
import zw.co.esolutions.ewallet.customerservices.service.ValidateTxnPassCodeReq;
import zw.co.esolutions.ewallet.enums.DayEndStatus;
import zw.co.esolutions.ewallet.enums.TransactionLocationType;
import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.enums.TransferType;
import zw.co.esolutions.ewallet.enums.TxnFamily;
import zw.co.esolutions.ewallet.limitservices.service.BankAccountClass;
import zw.co.esolutions.ewallet.limitservices.service.Limit;
import zw.co.esolutions.ewallet.limitservices.service.LimitPeriodType;
import zw.co.esolutions.ewallet.limitservices.service.LimitServiceSOAPProxy;
import zw.co.esolutions.ewallet.merchantservices.service.BankMerchant;
import zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchant;
import zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchantStatus;
import zw.co.esolutions.ewallet.merchantservices.service.Merchant;
import zw.co.esolutions.ewallet.merchantservices.service.MerchantServiceSOAPProxy;
import zw.co.esolutions.ewallet.msg.BankRequest;
import zw.co.esolutions.ewallet.msg.BankResponse;
import zw.co.esolutions.ewallet.msg.Commission;
import zw.co.esolutions.ewallet.msg.ProcessResponse;
import zw.co.esolutions.ewallet.msg.RequestInfo;
import zw.co.esolutions.ewallet.msg.ResponseInfo;
import zw.co.esolutions.ewallet.process.model.DayEnd;
import zw.co.esolutions.ewallet.process.model.ProcessTransaction;
import zw.co.esolutions.ewallet.process.model.TransactionCharge;
import zw.co.esolutions.ewallet.process.model.TransactionState;
import zw.co.esolutions.ewallet.process.pojo.ManualPojo;
import zw.co.esolutions.ewallet.process.pojo.PostingsRequestWrapper;
import zw.co.esolutions.ewallet.process.pojo.UniversalProcessSearch;
import zw.co.esolutions.ewallet.process.util.CustomerRegResponse;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.referralservices.service.Referral;
import zw.co.esolutions.ewallet.referralservices.service.ReferralConfig;
import zw.co.esolutions.ewallet.referralservices.service.ReferralServiceSOAPProxy;
import zw.co.esolutions.ewallet.referralservices.service.ReferralStatus;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.tariffservices.service.AgentType;
import zw.co.esolutions.ewallet.tariffservices.service.CustomerClass;
import zw.co.esolutions.ewallet.tariffservices.service.Tariff;
import zw.co.esolutions.ewallet.tariffservices.service.TariffServiceSOAPProxy;
import zw.co.esolutions.ewallet.tariffservices.service.TariffType;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.EmailSender;
import zw.co.esolutions.ewallet.util.EncryptAndDecrypt;
import zw.co.esolutions.ewallet.util.Formats;
import zw.co.esolutions.ewallet.util.GenerateKey;
import zw.co.esolutions.ewallet.util.MoneyUtil;
import zw.co.esolutions.ewallet.util.NumberUtil;
import zw.co.esolutions.ewallet.util.SystemConstants;
import zw.co.esolutions.mcommerce.refgen.service.ReferenceGeneratorServiceSOAPProxy;

/**
 * Session Bean implementation class ProcessUtilImpl
 * 
 * @author stanford
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class ProcessUtilImpl implements ProcessUtil {

	@PersistenceContext(unitName = "EWalletProcessModuleEJB")
	private EntityManager em;

	private static Properties config = SystemConstants.configParams;

	@Resource(mappedName = EWalletConstants.EWALLET_QCF)
	private QueueConnectionFactory jmsQueueConnectionFactory;
	
	@Resource(mappedName = EWalletConstants.FROM_EWALLET_TO_SWITCH_QUEUE)
	private Queue switchResponseQueue;
	
	@Resource(mappedName = EWalletConstants.FROM_EWALLET_TO_BANKMED_QUEUE)
	private Queue bankRequestsQueue;
	
	private Connection jmsConnection;
	
	/**
	 * Default constructor.
	 */
	public ProcessUtilImpl() {

	}

	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(ProcessUtilImpl.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + ProcessServiceImpl.class);
		}
	}

	public String checkMobileProfileEligibility(String mobileNumber) throws Exception {
		if (mobileNumber != null) {
			CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
			MobileProfile profile = customerService.getMobileProfileByMobileNumber(mobileNumber);
			LOG.debug(">>>>>>>>>>>>Profile = " + profile);
			if (profile != null && profile.getBankId() != null) {

				LOG.debug(">>>>>>>>>>>>Chk pt 1");
				if (CustomerStatus.ACTIVE.equals(profile.getCustomer().getStatus())) {
					LOG.debug(">>>>>>>>>>>>Chk pt 2");
					// check if mobile profile is locked; unlock it on
					// timeout.
					if (MobileProfileStatus.LOCKED.equals(profile.getStatus())) {
						LOG.debug(">>>>>>>>>>>>Chk pt 3");
						if (new Date().after(DateUtil.convertToDate(profile.getTimeout()))) {
							profile.setStatus(MobileProfileStatus.ACTIVE);
							profile.setPasswordRetryCount(0);
							LOG.debug(">>>>>>>>>>>>Chk pt 4");
							customerService.updateMobileProfile(profile, EWalletConstants.SYSTEM);
						} else {
							LOG.debug(">>>>>>>>>>>>Chk pt 5");
							return ResponseCode.E707.name();
						}
					}
					LOG.debug(">>>>>>>>>>>>Chk pt 6");
					return ResponseCode.E000.name(); // SUCCESS

				} else {
					LOG.debug(">>>>>>>>>>>>Chk pt 7");
					return ResponseCode.E712.name(); // customer is not
					// active. please
					// activate your
					// account
				}

			} else {
				LOG.debug(">>>>>>>>>>>>Chk pt 9");
				return ResponseCode.E700.name(); // mobile profile does not
				// exist
			}
		}
		LOG.debug(">>>>>>>>>>>>Chk pt 10");
		return ResponseCode.E700.name();
	}

	public RequestInfo populateRequestWithMerchantInfo(RequestInfo info) throws Exception {
		MerchantServiceSOAPProxy merchantService = new MerchantServiceSOAPProxy();
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		LOG.debug("Merchant Short Name : " + info.getUtilityName());
		Merchant merchant = merchantService.getMerchantByShortName(info.getUtilityName());
		if (merchant == null || merchant.getId() == null) {
			throw new Exception("Merchant " + info.getUtilityName() + " is not available on e-Solutions Mobile Commerce.");
		}
		info.setBankMerchantId(merchant.getId());
		LOG.debug("Merchant ID is : " + info.getBankMerchantId());
		List<Bank> banks = bankService.getBankByStatus(BankStatus.ACTIVE);
		if (banks == null || banks.isEmpty()) {
			throw new Exception("Error occurred while processing request. Please try again later.");
		}
		BankMerchant bankMerchant = null;
		for (Bank bank : banks) {
			bankMerchant = merchantService.getBankMerchantByBankIdAndMerchantId(bank.getId(), merchant.getId());
			if (bankMerchant != null && bankMerchant.getId() != null) {
				break;
			}
		}
		LOG.debug("Found the bank merchant to be " + bankMerchant);
		if (bankMerchant == null || bankMerchant.getId() == null) {
			// merchant not available for bank
			throw new Exception("Merchant " + info.getUtilityName() + " is not registered on this bank.");
		}
		LOG.debug("The bank merchant isn't null but has status " + bankMerchant.getStatus());
		// we get here this merchant is actually registered
		LOG.debug("Going for the customer utility account : so far set as : " + info.getCustomerUtilityAccount());
		if (info.getCustomerUtilityAccount() == null) {
			CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
			MobileProfile mobileProfile = customerService.getMobileProfileByMobileNumber(info.getSourceMobile());

			// the customer did not specify so we go for their registered
			// account.
			LOG.debug("Looking for a registered one");
			CustomerMerchant customerMerchant;
			try {
				LOG.debug("Mobile Profile  : " + mobileProfile + " CUSTOMER : " + mobileProfile.getCustomer() + " BANK MERCHANT ID : " + bankMerchant.getId());
				LOG.debug("Params : CUSTOMER ID : " + mobileProfile.getCustomer().getId() + " BANK MERCHANT ID : " + bankMerchant.getId());
				customerMerchant = merchantService.getCustomerMerchantByCustomerIdAndBankMerchantIdAndStatus(mobileProfile.getCustomer().getId(), bankMerchant.getId(), zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchantStatus.ACTIVE);
				LOG.debug("The registered one is found to be " + customerMerchant);
				if (customerMerchant == null || customerMerchant.getId() == null) {
					throw new Exception("Your " + info.getUtilityName() + " account is not registered for bill payment with this bank.");
				}
				LOG.debug("We found it so we set the customer utility account to : " + customerMerchant.getCustomerAccountNumber());
				info.setCustomerUtilityAccount(customerMerchant.getCustomerAccountNumber());
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("Error getting registered customer utility account.");
			}

		} else {
			// here whatever you entered we deem it correct and proceed.
			// Tichaibata pa Account Validation.
		}
		return info;
	}

	// @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private ProcessTransaction checkTxnLimits(ProcessTransaction txn, BankAccount bankAccount) throws Exception {
		LimitServiceSOAPProxy limitService = new LimitServiceSOAPProxy();
		Date txnDate = new Date(System.currentTimeMillis());
		zw.co.esolutions.ewallet.limitservices.service.TransactionType txnType = zw.co.esolutions.ewallet.limitservices.service.TransactionType.fromValue(txn.getTransactionType().name());
		
		//Setting Bank Account Class to NONE
		bankAccount.setAccountClass(zw.co.esolutions.ewallet.bankservices.service.BankAccountClass.NONE);
		
		Limit srcTxnLimit = limitService.getValidLimitOnDateByBankId(txnType, BankAccountClass.valueOf(bankAccount.getAccountClass().toString()), DateUtil.convertToXMLGregorianCalendar(txnDate), LimitPeriodType.TRANSACTION, txn.getFromBankId());
		if (srcTxnLimit != null && srcTxnLimit.getId() != null) {
			// Check for transfer values eligibility
			if (srcTxnLimit.getMinValue() > txn.getAmount()) {
				txn.setResponseCode(ResponseCode.E814.name());
				txn.setNarrative("Transaction amount is below minimum of "+MoneyUtil.convertCentsToDollarsPattern(srcTxnLimit.getMinValue()));
				return txn;
			}
			if (srcTxnLimit.getMaxValue() < txn.getAmount()) {
				txn.setResponseCode(ResponseCode.E815.name());
				txn.setNarrative("Transaction amount is greater than maximum of "+MoneyUtil.convertCentsToDollarsPattern(srcTxnLimit.getMaxValue()));
				return txn; // Transfer amount is above max
			}
			txn.setNarrative("Txn limits validation successful.");
		} else {
			LOG.debug("Transfer amount is limitless....");
			txn.setNarrative("Txn is limitless.");
		}
		txn.setResponseCode(ResponseCode.E000.name());
		return txn;
	}

	private ProcessTransaction checkFundsAvailability(ProcessTransaction txn, BankAccount bankAccount) throws Exception {
		LimitServiceSOAPProxy limitService = new LimitServiceSOAPProxy();
		Date txnDate = new Date(System.currentTimeMillis());
		
		//Setting Bank Account Class to NONE
		bankAccount.setAccountClass(zw.co.esolutions.ewallet.bankservices.service.BankAccountClass.NONE);
		
		Limit sourceBalLimit = limitService.getValidLimitOnDateByBankId(zw.co.esolutions.ewallet.limitservices.service.TransactionType.BALANCE, BankAccountClass.valueOf(bankAccount.getAccountClass().toString()), DateUtil.convertToXMLGregorianCalendar(txnDate), LimitPeriodType.TRANSACTION, txn.getFromBankId());
		long transferAmount = this.getTotalTransactionAmount(txn);
		double balanceAfter = bankAccount.getRunningBalance() - transferAmount;
		if (sourceBalLimit != null && sourceBalLimit.getId() != null) {
			if (sourceBalLimit.getMinValue() > balanceAfter) {
				txn.setResponseCode(ResponseCode.E808.name());
				txn.setNarrative("Insufficient funds");
				return txn;
			} else {
				txn.setResponseCode(ResponseCode.E000.name());
				txn.setNarrative("Funds availability validation successful.");
				return txn;
			}
		} else {
			if (0 > balanceAfter) {
				txn.setResponseCode(ResponseCode.E808.name());
				txn.setNarrative("Insufficient funds");
				return txn;
			} else {
				txn.setResponseCode(ResponseCode.E000.name());
				txn.setNarrative("Funds availability validation successful.");
				return txn;
			}
		}
	}

	
	
	private ProcessTransaction checkDestinationBalanceLimits(ProcessTransaction txn, BankAccount bankAccount) throws Exception {

		LimitServiceSOAPProxy limitService = new LimitServiceSOAPProxy();
		Date txnDate = new Date(System.currentTimeMillis());
		Limit destnBalLimit = limitService.getValidLimitOnDateByBankId(zw.co.esolutions.ewallet.limitservices.service.TransactionType.BALANCE, BankAccountClass.valueOf(bankAccount.getAccountClass().toString()), DateUtil.convertToXMLGregorianCalendar(txnDate), LimitPeriodType.TRANSACTION, txn.getToBankId());
		long transferAmount = this.getTotalTransactionAmount(txn);
		double balanceAfter = bankAccount.getRunningBalance() + transferAmount;
		if (destnBalLimit != null && destnBalLimit.getId() != null) {
			if (destnBalLimit.getMaxValue() < balanceAfter) {
				txn.setResponseCode(ResponseCode.E808.name());
				txn.setNarrative("The transaction will push beneficiary account balance above maximum");
				return txn;
			} else {
				txn.setResponseCode(ResponseCode.E000.name());
				txn.setNarrative("Limits validation successful.");
				return txn;
			}
		} else {
			txn.setResponseCode(ResponseCode.E000.name());
			txn.setNarrative("Balance limit not defined.");
			return txn;
		}
	}

	public ProcessTransaction populateProcessTransaction(RequestInfo requestInfo) throws Exception {
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		MobileProfile profile = customerService.getMobileProfileByBankAndMobileNumber(requestInfo.getSourceBankId(), requestInfo.getSourceMobile());

		ProcessTransaction tranx = new ProcessTransaction();
		tranx.setId(requestInfo.getMessageId());
		tranx.setMessageId(requestInfo.getMessageId());
		tranx.setTransactionType(requestInfo.getTransactionType());
		tranx.setSourceMobileId(profile.getId());
		tranx.setTransactionLocationType(requestInfo.getLocationType());
		tranx.setAmount(requestInfo.getAmount());
		tranx.setBankReference(requestInfo.getBankCode());
		tranx.setBranchId(profile.getCustomer().getBranchId());
		tranx.setCustomerName(profile.getCustomer().getLastName() + " " + profile.getCustomer().getFirstNames());
		tranx.setFromBankId(requestInfo.getSourceBankId());
		tranx.setToBankId(requestInfo.getTargetBankId());
		tranx.setSourceAccountNumber(requestInfo.getSourceAccountNumber());
		tranx.setDestinationAccountNumber(requestInfo.getDestinationAccountNumber());
		tranx.setSourceMobile(requestInfo.getSourceMobile());
		tranx.setTargetMobile(requestInfo.getTargetMobile());
		tranx.setUtilityAccount(requestInfo.getCustomerUtilityAccount());
		tranx.setUtilityName(requestInfo.getUtilityName());
		tranx.setBankMerchantId(requestInfo.getBankMerchantId());
		tranx.setProfileId(requestInfo.getProfileId());
		tranx.setCustomerId(profile.getCustomer().getId());
		tranx.setAgentNumber(requestInfo.getAgentNumber());
		tranx.setBouquetName(requestInfo.getBouquetName());
		tranx.setBouquetCode(requestInfo.getBouquetCode());
		tranx.setNumberOfMonths(requestInfo.getNumberOfMonths());
		tranx.setCommission(requestInfo.getCommission());
		tranx.setToCustomerName(requestInfo.getBeneficiaryName());
		
		if (requestInfo.getProfileId() != null) {
			Profile p = profileService.findProfileById(requestInfo.getProfileId());
			if (p != null && p.getId() != null) {
				tranx.setTransactionLocationId(p.getBranchId());
			}
		} else {
			tranx.setTransactionLocationId(this.getSmsLocationId(requestInfo));
		}

		if (requestInfo.getTargetMobile() != null) {
			MobileProfile mobileProfile = customerService.getMobileProfileByBankAndMobileNumber(requestInfo.getTargetBankId(), requestInfo.getTargetMobile());
			// MobileProfile mobileProfile =
			// customerService.findMobileProfileById(requestInfo.getSourceMobileProfileId());
			if (mobileProfile == null || mobileProfile.getId() == null) {
				// do nothing here, this is just a non holder fing
			} else {
				tranx.setToCustomerName(mobileProfile.getCustomer().getLastName() + " " + mobileProfile.getCustomer().getFirstNames());
				tranx.setTargetMobileId(mobileProfile.getId());
			}
		}
		tranx.setDateCreated(new Date());
		tranx = this.populateEquationAccoountsByTransactionType(tranx);
//		try {
//			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
//			if (zw.co.esolutions.ewallet.enums.TransactionType.DEPOSIT.equals(tranx.getTransactionType())) {
//				// a deposit will debit Branch EWALLET CASH ACCOUNT
//				LOG.debug("Got a deposit txn, setting the source account");
////				BankAccount sourceAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getBranchId(), BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT, OwnerType.BANK_BRANCH, null);
//				BankAccount sourceAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getTransactionLocationId(), BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT, OwnerType.BANK_BRANCH, null);
//				LOG.debug("Branch cash account " + sourceAccount);
//				tranx.setSourceEQ3AccountNumber(sourceAccount.getAccountNumber());
//				LOG.debug("Branch SRC Account Has been set...... going for the pool account");
//				// and Credit EWALLET POOL ACCOUNT
//				LOG.debug("Find POOL ACC for " + tranx.getToBankId() + " " + BankAccountType.POOL_CONTROL + " " + OwnerType.BANK);
//				BankAccount targetAcc = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getToBankId(), BankAccountType.POOL_CONTROL, OwnerType.BANK, null);
//				LOG.debug("Found the pool account for sure..." + targetAcc);
//				tranx.setDestinationEQ3AccountNumber(targetAcc.getAccountNumber());
//				LOG.debug("Done setting the dest accont number.");
//			} else if (zw.co.esolutions.ewallet.enums.TransactionType.WITHDRAWAL.equals(tranx.getTransactionType())) {
//				// a withdrawal will debit EWALLET CONTROL ACCOUNT
//				LOG.debug("Got a WITHDRA txn, setting the source account");
//				BankAccount sourceAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.POOL_CONTROL, OwnerType.BANK, null);
//				LOG.debug("EWALLET POOL account " + sourceAccount);
//				tranx.setSourceEQ3AccountNumber(sourceAccount.getAccountNumber());
//				LOG.debug("POOL Account Has been set...... going for the Branch Cash account");
//				// and Credit EWALLET POOL ACCOUNT
//				BankAccount targetAcc = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getTransactionLocationId(), BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT, OwnerType.BANK_BRANCH, null);
//				LOG.debug("Found the Branch Cash account for sure..." + targetAcc);
//				tranx.setDestinationEQ3AccountNumber(targetAcc.getAccountNumber());
//				LOG.debug("Done setting the dest accont number.");
//			} else if (zw.co.esolutions.ewallet.enums.TransactionType.WITHDRAWAL_NONHOLDER.equals(tranx.getTransactionType())) {
//				// a non-holder withdrawal will debit PAYOUT SUSPENSE ACCOUNT
//				LOG.debug("Got a NON-HLDR-WITHD txn, setting the source account");
//				BankAccount sourceAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.PAYOUT_CONTROL, OwnerType.BANK, null);
//				LOG.debug("PAYOUT SUSPENSE account " + sourceAccount);
//				tranx.setSourceEQ3AccountNumber(sourceAccount.getAccountNumber());
//				// bankRequest.setSourceAccountNumber(sourceAccount.getAccountNumber());
//				LOG.debug("POOL Account Has been set...... going for the Branch Cash account");
//				// and Credit BRANCH CASH ACCOUNT
//				BankAccount targetAcc = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getTransactionLocationId(), BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT, OwnerType.BANK_BRANCH, null);
//				LOG.debug("Found the Branch Cash account for sure..." + targetAcc);
//				tranx.setDestinationEQ3AccountNumber(targetAcc.getAccountNumber());
//				LOG.debug("Done setting the dest accont number.");
//			} else if (zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER.equals(tranx.getTransactionType())) {
//				// a non HLDR TXF will debit EWALLET POOL
//				LOG.debug("Got a EWAL-NON-HLDR txn, setting the source account");
//				BankAccount sourceAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.POOL_CONTROL, OwnerType.BANK, null);
//				LOG.debug("EWALLET POOL account " + sourceAccount);
//				tranx.setSourceEQ3AccountNumber(sourceAccount.getAccountNumber());
//				LOG.debug("POOL Account Has been set...... going for the Branch Cash account");
//				// AND CREDIT CASH PAYOUT
//				BankAccount targetAcc = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.PAYOUT_CONTROL, OwnerType.BANK, null);
//				LOG.debug("Found the CASH PAYOUT Control for sure..." + targetAcc);
//				tranx.setDestinationEQ3AccountNumber(targetAcc.getAccountNumber());
//				LOG.debug("Done setting the dest accont number.");
//			} else if (zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TO_EWALLET_TRANSFER.equals(tranx.getTransactionType())) {
//				// a non HLDR TXF will debit EWALLET POOL
//				LOG.debug("Got a EWAL-NON-HLDR txn, setting the source account");
//				BankAccount sourceAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.POOL_CONTROL, OwnerType.BANK, null);
//				LOG.debug("EWALLET POOL account " + sourceAccount);
//				tranx.setSourceEQ3AccountNumber(sourceAccount.getAccountNumber());
//				LOG.debug("POOL Account Has been set...... going for the Branch Cash account");
//				// AND CREDIT EWALLET POOL
//				BankAccount targetAcc = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getToBankId(), BankAccountType.POOL_CONTROL, OwnerType.BANK, null);
//				tranx.setDestinationEQ3AccountNumber(targetAcc.getAccountNumber());
//				LOG.debug("Done setting the dest accont number.");
//			} else if (zw.co.esolutions.ewallet.enums.TransactionType.MINI_STATEMENT.equals(tranx.getTransactionType()) || zw.co.esolutions.ewallet.enums.TransactionType.BALANCE.equals(tranx.getTransactionType())) {
//				// a non HLDR TXF will debit EWALLET POOL
//				tranx.setSourceEQ3AccountNumber(tranx.getSourceAccountNumber());
//				// AND CREDIT EWALLET POOL
//			} else if (zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER.equals(tranx.getTransactionType())) {
//				// a non HLDR TXF will debit EWALLET POOL
//				LOG.debug("Got a EWAL-NON-HLDR txn, setting the source account");
//				BankAccount sourceAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.POOL_CONTROL, OwnerType.BANK, null);
//				LOG.debug("EWALLET POOL account " + sourceAccount);
//				tranx.setSourceEQ3AccountNumber(sourceAccount.getAccountNumber());
//				LOG.debug("POOL Account Has been set...... going for the Branch Cash account");
//				// AND CREDIT Beneficiary EQ3 Account
//				tranx.setDestinationEQ3AccountNumber(tranx.getDestinationAccountNumber());
//				LOG.debug("Done setting the dest accont number.");
//			}
//
//			else if (zw.co.esolutions.ewallet.enums.TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER.equals(tranx.getTransactionType())) {
//				// a non BA TO NON-HLDR TXF will debit Customer Account in EQ3
//				LOG.debug("Src Acc Number : " + tranx.getSourceAccountNumber());
//				// AND CREDIT CASH PAYOUT
//				tranx.setSourceEQ3AccountNumber(tranx.getSourceAccountNumber());
//				BankAccount targetAcc = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.PAYOUT_CONTROL, OwnerType.BANK, null);
//				LOG.debug("Found the CASH PAYOUT account for sure..." + targetAcc);
//				// bankRequest.setTargetAccountNumber(targetAcc.getAccountNumber());
//				tranx.setDestinationEQ3AccountNumber(targetAcc.getAccountNumber());
//				LOG.debug("Done setting the dest accont number to " + tranx.getDestinationEQ3AccountNumber());
//			} else if (zw.co.esolutions.ewallet.enums.TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER.equals(tranx.getTransactionType())) {
//				// a non BA TO EWAL HLDR TXF will debit Customer Account in EQ3
//				LOG.debug("Src Acc Number : " + tranx.getSourceAccountNumber());
//				tranx.setSourceEQ3AccountNumber(tranx.getSourceAccountNumber());
//				// AND CREDIT EWALLET POOL ACC
//				BankAccount targetAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getToBankId(), BankAccountType.POOL_CONTROL, OwnerType.BANK, null);
//				LOG.debug("EWALLET POOL account " + targetAccount);
//				// bankRequest.setTargetAccountNumber(targetAccount.getAccountNumber());
//				tranx.setDestinationEQ3AccountNumber(targetAccount.getAccountNumber());
//				LOG.debug("Done setting the dest accont number to " + tranx.getDestinationEQ3AccountNumber());
//			} else if (zw.co.esolutions.ewallet.enums.TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER.equals(tranx.getTransactionType())) {
//				// a non BA TO EWAL HLDR TXF will debit Customer Account in EQ3
//				LOG.debug("Src Acc Number : " + tranx.getSourceAccountNumber());
//				tranx.setSourceEQ3AccountNumber(tranx.getSourceAccountNumber());
//				// AND CREDIT Beneficiary Acc in EQ3
//				LOG.debug("Dest Acc Number : " + tranx.getDestinationAccountNumber());
//				tranx.setDestinationEQ3AccountNumber(tranx.getDestinationAccountNumber());
//			} else if (zw.co.esolutions.ewallet.enums.TransactionType.TOPUP.equals(tranx.getTransactionType())) {
//
//				BankAccount bankAccount = bankService.getUniqueBankAccountByAccountNumber(tranx.getSourceAccountNumber());
//				if (BankAccountType.E_WALLET.equals(bankAccount.getType())) {
//					LOG.debug("Found bank account of type EWALLET " + bankAccount);
//					// an EWALLET TOPUP Requestn EQ3
//					// a non HLDR TXF will debit EWALLET POOL
//					LOG.debug("Got a EWALLET TOPUP......");
//					LOG.debug("Debit ewallet pool control POOL Account Has been set...... going for the Branch Cash account");
//					BankAccount sourceAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.POOL_CONTROL, OwnerType.BANK, null);
//					LOG.debug("EWALLET POOL account " + sourceAccount);
//					tranx.setSourceEQ3AccountNumber(sourceAccount.getAccountNumber());
//
//					// AND CREDIT TOPUP SUSPENSE ACCOUNT
//					LOG.debug("Dest Acc Number AND CREDIT TOPUP SUSPENSE ACCOUNT: " + tranx.getDestinationAccountNumber());
//					BankAccount targetAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.TOPUP_SUSPENSE_ACCOUNT, OwnerType.BANK, null);
//					// bankRequest.setTargetAccountNumber(targetAccount.getAccountNumber());
//					tranx.setDestinationEQ3AccountNumber(targetAccount.getAccountNumber());
//					LOG.debug("Done setting the dest accont number to " + tranx.getDestinationEQ3AccountNumber());
//				} else {
//					LOG.debug("Got an EQ3 TOPUP......");
//					LOG.debug("Debit Customer EQ3 Account ");
//					LOG.debug("EWALLET POOL account " + tranx.getSourceAccountNumber());
//					tranx.setSourceEQ3AccountNumber(tranx.getSourceAccountNumber());
//
//					// AND CREDIT TOPUP SUSPENSE ACCOUNT
//					LOG.debug("Dest Acc Number AND CREDIT TOPUP SUSPENSE ACCOUNT: " + tranx.getDestinationAccountNumber());
//					BankAccount targetAcc = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.TOPUP_SUSPENSE_ACCOUNT, OwnerType.BANK, null);
//					// bankRequest.setTargetAccountNumber(targetAccount.getAccountNumber());
//					tranx.setDestinationEQ3AccountNumber(targetAcc.getAccountNumber());
//					LOG.debug("Done setting the dest accont number to " + tranx.getDestinationEQ3AccountNumber());
//				}
//
//			} else if (zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TOPUP.equals(tranx.getTransactionType())) {
//				
//			} else if (zw.co.esolutions.ewallet.enums.TransactionType.BILLPAY.equals(tranx.getTransactionType())) {
//				BankAccount bankAccount = bankService.getUniqueBankAccountByAccountNumber(tranx.getSourceAccountNumber());
//				if (BankAccountType.E_WALLET.equals(bankAccount.getType())) {
//					LOG.debug("Found bank account of type EWALLET " + bankAccount);
//					LOG.debug("Got an EWALLET BILLPAY Request......");
//					LOG.debug("Debit EQ3 POOL Account with amount.");
//					BankAccount sourceAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.POOL_CONTROL, OwnerType.BANK, null);
//					LOG.debug("EWALLET POOL account " + sourceAccount);
//					// bankRequest.setSourceAccountNumber(sourceAccount.getAccountNumber());
//					tranx.setSourceEQ3AccountNumber(sourceAccount.getAccountNumber());
//					LOG.debug("The merchant id to get account for credit " + tranx.getBankMerchantId());
//					// AND CREDIT Merchant SUSPENSE ACCOUNT
//					LOG.debug("Dest Acc Number AND CREDIT MERCHANT SUSPENSE ACCOUNT: " + tranx.getDestinationEQ3AccountNumber() + " FOR Utility " + tranx.getUtilityName());
//					BankAccount targetAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getBankMerchantId(), BankAccountType.MERCHANT_SUSPENSE, OwnerType.MERCHANT, null);
//					LOG.debug("BANK MERCHANT SUSPENSE ACCOUNT " + targetAccount);
//					// bankRequest.setTargetAccountNumber(targetAccount.getAccountNumber());
//					tranx.setDestinationEQ3AccountNumber(targetAccount.getAccountNumber());
//					LOG.debug("Done setting the dest accont number to " + tranx.getDestinationEQ3AccountNumber());
//				} else {
//					LOG.debug("Got an EQ3 BILLPAY Request......");
//					LOG.debug("Debit EQ3 Customer Account with amount.");
//					// bankRequest.setSourceAccountNumber(txn.getSourceAccountNumber());
//					tranx.setSourceEQ3AccountNumber(tranx.getSourceAccountNumber());
//
//					// AND CREDIT Merchant SUSPENSE ACCOUNT
//					BankAccount targetAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getBankMerchantId(), BankAccountType.MERCHANT_SUSPENSE, OwnerType.MERCHANT, null);
//					LOG.debug("BANK MERCHANT SUSPENSE ACCOUNT " + targetAccount);
//					// bankRequest.setTargetAccountNumber(targetAccount.getAccountNumber());
//					tranx.setDestinationEQ3AccountNumber(targetAccount.getAccountNumber());
//					LOG.debug("Done setting the dest accont number to " + tranx.getDestinationEQ3AccountNumber());
//				}
//			} else if (zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_BILLPAY.equals(tranx.getTransactionType())) {
//				
//			}
//		} catch (Exception_Exception e) {
//			throw new Exception("Exception in populate method");
//		}

		return tranx;
	}
	
	
	public ProcessTransaction populateEquationAccoountsByTransactionType(ProcessTransaction tranx) throws Exception{

		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
//		if (zw.co.esolutions.ewallet.enums.TransactionType.DEPOSIT.equals(tranx.getTransactionType())) {
//			// a deposit will debit Branch EWALLET CASH ACCOUNT
//			LOG.debug("Got a deposit txn, setting the source account");
//			LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>trxn branch ID>>>>>>>>>>>>>>>>>>>>>>>>>>"+tranx.getBranchId());
//			
//			BankAccount sourceAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getBranchId(), BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT, OwnerType.BANK_BRANCH, null);
//			LOG.debug("Branch cash account " + sourceAccount);
//			tranx.setSourceEQ3AccountNumber(sourceAccount.getAccountNumber());
//			LOG.debug("Branch SRC Account Has been set...... going for the pool account");
//			// and Credit EWALLET POOL ACCOUNT
//			LOG.debug("Find POOL ACC for " + tranx.getToBankId() + " " + BankAccountType.POOL_CONTROL + " " + OwnerType.BANK);
//			BankAccount targetAcc = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getToBankId(), BankAccountType.POOL_CONTROL, OwnerType.BANK, null);
//			LOG.debug("Found the pool account for sure..." + targetAcc);
//			tranx.setDestinationEQ3AccountNumber(targetAcc.getAccountNumber());
//			LOG.debug("Done setting the dest accont number.");
//		} else if (zw.co.esolutions.ewallet.enums.TransactionType.WITHDRAWAL.equals(tranx.getTransactionType())) {
//			// a withdrawal will debit EWALLET CONTROL ACCOUNT
//			LOG.debug("Got a WITHDRA txn, setting the source account");
//			BankAccount sourceAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.POOL_CONTROL, OwnerType.BANK, null);
//			LOG.debug("EWALLET POOL account " + sourceAccount);
//			tranx.setSourceEQ3AccountNumber(sourceAccount.getAccountNumber());
//			LOG.debug("POOL Account Has been set...... going for the Branch Cash account");
//			// and Credit EWALLET POOL ACCOUNT
//			BankAccount targetAcc = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getBranchId(), BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT, OwnerType.BANK_BRANCH, null);
//			LOG.debug("Found the Branch Cash account for sure..." + targetAcc);
//			tranx.setDestinationEQ3AccountNumber(targetAcc.getAccountNumber());
//			LOG.debug("Done setting the dest accont number.");
//		} else if (zw.co.esolutions.ewallet.enums.TransactionType.WITHDRAWAL_NONHOLDER.equals(tranx.getTransactionType())) {
//			// a non-holder withdrawal will debit PAYOUT SUSPENSE ACCOUNT
//			LOG.debug("Got a NON-HLDR-WITHD txn, setting the source account");
//			BankAccount sourceAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.PAYOUT_CONTROL, OwnerType.BANK, null);
//			LOG.debug("PAYOUT SUSPENSE account " + sourceAccount);
//			tranx.setSourceEQ3AccountNumber(sourceAccount.getAccountNumber());
//			//bankRequest.setSourceAccountNumber(sourceAccount.getAccountNumber());
//			LOG.debug("POOL Account Has been set...... going for the Branch Cash account");
//			// and Credit BRANCH CASH ACCOUNT
//			BankAccount targetAcc = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getBranchId(), BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT, OwnerType.BANK_BRANCH, null);
//			LOG.debug("Found the Branch Cash account for sure..." + targetAcc);
////		bankRequest.setTargetAccountNumber(targetAcc.getAccountNumber());
//			tranx.setDestinationEQ3AccountNumber(targetAcc.getAccountNumber());
//			LOG.debug("Done setting the dest accont number.");
//		}
		if (zw.co.esolutions.ewallet.enums.TransactionType.DEPOSIT.equals(tranx.getTransactionType())) {
			// a deposit will debit Branch EWALLET CASH ACCOUNT
			LOG.debug("Got a deposit txn, setting the source account");
//			BankAccount sourceAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getBranchId(), BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT, OwnerType.BANK_BRANCH, null);
			BankAccount sourceAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getTransactionLocationId(), BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT, OwnerType.BANK_BRANCH, null);
			LOG.debug("Branch cash account " + sourceAccount);
			tranx.setSourceEQ3AccountNumber(sourceAccount.getAccountNumber());
			LOG.debug("Branch SRC Account Has been set...... going for the pool account");
			// and Credit EWALLET POOL ACCOUNT
			LOG.debug("Find POOL ACC for " + tranx.getToBankId() + " " + BankAccountType.POOL_CONTROL + " " + OwnerType.BANK);
			BankAccount targetAcc = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getToBankId(), BankAccountType.POOL_CONTROL, OwnerType.BANK, null);
			LOG.debug("Found the pool account for sure..." + targetAcc);
			tranx.setDestinationEQ3AccountNumber(targetAcc.getAccountNumber());
			LOG.debug("Done setting the dest accont number.");
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.WITHDRAWAL.equals(tranx.getTransactionType())) {
			// a withdrawal will debit EWALLET CONTROL ACCOUNT
			LOG.debug("Got a WITHDRA txn, setting the source account");
			BankAccount sourceAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.POOL_CONTROL, OwnerType.BANK, null);
			LOG.debug("EWALLET POOL account " + sourceAccount);
			tranx.setSourceEQ3AccountNumber(sourceAccount.getAccountNumber());
			LOG.debug("POOL Account Has been set...... going for the Branch Cash account");
			// and Credit EWALLET POOL ACCOUNT
			BankAccount targetAcc = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getTransactionLocationId(), BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT, OwnerType.BANK_BRANCH, null);
			LOG.debug("Found the Branch Cash account for sure..." + targetAcc);
			tranx.setDestinationEQ3AccountNumber(targetAcc.getAccountNumber());
			LOG.debug("Done setting the dest accont number.");
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.WITHDRAWAL_NONHOLDER.equals(tranx.getTransactionType())) {
			// a non-holder withdrawal will debit PAYOUT SUSPENSE ACCOUNT
			LOG.debug("Got a NON-HLDR-WITHD txn, setting the source account");
			BankAccount sourceAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.PAYOUT_CONTROL, OwnerType.BANK, null);
			LOG.debug("PAYOUT SUSPENSE account " + sourceAccount);
			tranx.setSourceEQ3AccountNumber(sourceAccount.getAccountNumber());
			// bankRequest.setSourceAccountNumber(sourceAccount.getAccountNumber());
			LOG.debug("POOL Account Has been set...... going for the Branch Cash account");
			// and Credit BRANCH CASH ACCOUNT
			BankAccount targetAcc = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getTransactionLocationId(), BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT, OwnerType.BANK_BRANCH, null);
			LOG.debug("Found the Branch Cash account for sure..." + targetAcc+" , LOCATION ID = "+tranx.getTransactionLocationId());
			tranx.setDestinationEQ3AccountNumber(targetAcc.getAccountNumber());
			LOG.debug("Done setting the dest accont number.");
		}
		else if (zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER.equals(tranx.getTransactionType())) {
			// a non HLDR TXF will debit EWALLET POOL
			LOG.debug("Got a EWAL-NON-HLDR txn, setting the source account");
			BankAccount sourceAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.POOL_CONTROL, OwnerType.BANK, null);
			LOG.debug("EWALLET POOL account " + sourceAccount);
//			bankRequest.setSourceAccountNumber(sourceAccount.getAccountNumber());
			tranx.setSourceEQ3AccountNumber(sourceAccount.getAccountNumber());
			LOG.debug("POOL Account Has been set...... going for target account");
			
			BankAccount targetAcc;
//			if (MobileNetworkOperator.ECONET.equals(NumberUtil.getMNO(tranx.getTargetMobile()))) {
//				LOG.debug("$$$$$$$$        NONHOLDER TRANSFER TO ECONET NUMBER, CHANGE TARGET ACC");
//				//CREDIT POOL CONTROL
//				 targetAcc = sourceAccount;
//				LOG.debug("Found the POOL Control for sure..." + targetAcc);
//			} else {
				// CREDIT CASH PAYOUT
				targetAcc = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.PAYOUT_CONTROL, OwnerType.BANK, null);
				LOG.debug("Found the CASH PAYOUT Control for sure..." + targetAcc);
//			}
			tranx.setDestinationEQ3AccountNumber(targetAcc.getAccountNumber());
			LOG.debug("Done setting the dest accont number.");
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TO_EWALLET_TRANSFER.equals(tranx.getTransactionType())) {
			// a non HLDR TXF will debit EWALLET POOL
			LOG.debug("Got a EWAL-NON-HLDR txn, setting the source account");
			BankAccount sourceAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.POOL_CONTROL, OwnerType.BANK, null);
			LOG.debug("EWALLET POOL account " + sourceAccount);
//		bankRequest.setSourceAccountNumber(sourceAccount.getAccountNumber());
			tranx.setSourceEQ3AccountNumber(sourceAccount.getAccountNumber());
			LOG.debug("POOL Account Has been set...... going for the Branch Cash account");
			// AND CREDIT EWALLET POOL
			BankAccount targetAcc = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getToBankId(), BankAccountType.POOL_CONTROL, OwnerType.BANK, null);
//		bankRequest.setTargetAccountNumber(sourceAccount.getAccountNumber());
			tranx.setDestinationEQ3AccountNumber(targetAcc.getAccountNumber());
			LOG.debug("Done setting the dest accont number.");
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER.equals(tranx.getTransactionType())) {
			// a non HLDR TXF will debit EWALLET POOL
			LOG.debug("Got a EWAL-NON-HLDR txn, setting the source account");
			BankAccount sourceAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.POOL_CONTROL, OwnerType.BANK, null);
			LOG.debug("EWALLET POOL account " + sourceAccount);
//		bankRequest.setSourceAccountNumber(sourceAccount.getAccountNumber());
			tranx.setSourceEQ3AccountNumber(sourceAccount.getAccountNumber());
			LOG.debug("POOL Account Has been set...... going for the Branch Cash account");
			// AND CREDIT Beneficiary EQ3 Account
//		LOG.debug("Dest Acc Number : " + bankRequest.getTargetAccountNumber());
			tranx.setDestinationEQ3AccountNumber(tranx.getDestinationAccountNumber());
			LOG.debug("Done setting the dest accont number.");
		}

		else if (zw.co.esolutions.ewallet.enums.TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER.equals(tranx.getTransactionType())) {
			// a non BA TO NON-HLDR TXF will debit Customer Account in EQ3
			LOG.debug("Src Acc Number : " + tranx.getSourceAccountNumber());
			tranx.setSourceEQ3AccountNumber(tranx.getSourceAccountNumber());

			LOG.debug("Going for target acc");
			BankAccount targetAcc;
//			if (MobileNetworkOperator.ECONET.equals(NumberUtil.getMNO(tranx.getTargetMobile()))) {
//				LOG.debug("$$$$$$$$        NONHOLDER TRANSFER TO ECONET NUMBER, CHANGE TARGET ACC");
//				//CREDIT POOL CONTROL
//				targetAcc = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.POOL_CONTROL, OwnerType.BANK, null);
//				LOG.debug("Found the POOL Control for sure..." + targetAcc);
//
//			} else {
				//CREDIT CASH PAYOUT
				targetAcc = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.PAYOUT_CONTROL, OwnerType.BANK, null);
				LOG.debug("Found the CASH PAYOUT account for sure..." + targetAcc);
//			}
			tranx.setDestinationEQ3AccountNumber(targetAcc.getAccountNumber());
			LOG.debug("Done setting the dest accont number to " + tranx.getDestinationEQ3AccountNumber());
				
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER.equals(tranx.getTransactionType())) {
			// a non BA TO EWAL HLDR TXF will debit Customer Account in EQ3
			LOG.debug("Src Acc Number : " + tranx.getSourceAccountNumber());
			tranx.setSourceEQ3AccountNumber(tranx.getSourceAccountNumber());
			// AND CREDIT EWALLET POOL ACC
			BankAccount targetAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getToBankId(), BankAccountType.POOL_CONTROL, OwnerType.BANK, null);
			LOG.debug("EWALLET POOL account " + targetAccount);
//		bankRequest.setTargetAccountNumber(targetAccount.getAccountNumber());
			tranx.setDestinationEQ3AccountNumber(targetAccount.getAccountNumber());
			LOG.debug("Done setting the dest accont number to " + tranx.getDestinationEQ3AccountNumber());
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER.equals(tranx.getTransactionType())) {
			// a non BA TO EWAL HLDR TXF will debit Customer Account in EQ3
			LOG.debug("Src Acc Number : " + tranx.getSourceAccountNumber());
			tranx.setSourceEQ3AccountNumber(tranx.getSourceAccountNumber());
			// AND CREDIT Beneficiary Acc in EQ3
			LOG.debug("Dest Acc Number : " + tranx.getDestinationAccountNumber());
			tranx.setDestinationEQ3AccountNumber(tranx.getDestinationAccountNumber());
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.TOPUP.equals(tranx.getTransactionType())) {

			BankAccount bankAccount = bankService.getUniqueBankAccountByAccountNumber(tranx.getSourceAccountNumber());
				if (BankAccountType.E_WALLET.equals(bankAccount.getType())) {
					LOG.debug("Found bank account of type EWALLET " + bankAccount);
					// an EWALLET TOPUP Requestn EQ3
					// a non HLDR TXF will debit EWALLET POOL
					LOG.debug("Got a EWALLET TOPUP......");
					LOG.debug("Debit ewallet pool control POOL Account Has been set...... going for the Branch Cash account");
					BankAccount sourceAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.POOL_CONTROL, OwnerType.BANK, null);
					LOG.debug("EWALLET POOL account " + sourceAccount);
					tranx.setSourceEQ3AccountNumber(sourceAccount.getAccountNumber());

					// AND CREDIT TOPUP SUSPENSE ACCOUNT
					LOG.debug("Dest Acc Number AND CREDIT TOPUP SUSPENSE ACCOUNT: " + tranx.getDestinationAccountNumber());
					BankAccount targetAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.TOPUP_SUSPENSE_ACCOUNT, OwnerType.BANK, null);
//				bankRequest.setTargetAccountNumber(targetAccount.getAccountNumber());
					tranx.setDestinationEQ3AccountNumber(targetAccount.getAccountNumber());
					LOG.debug("Done setting the dest accont number to " + tranx.getDestinationEQ3AccountNumber());
				} else {
					LOG.debug("Got an EQ3 TOPUP......");
					LOG.debug("Debit Customer EQ3 Account ");
					LOG.debug("EWALLET POOL account " + tranx.getSourceAccountNumber());
					tranx.setSourceEQ3AccountNumber(tranx.getSourceAccountNumber());

					// AND CREDIT TOPUP SUSPENSE ACCOUNT
					LOG.debug("Dest Acc Number AND CREDIT TOPUP SUSPENSE ACCOUNT: " + tranx.getDestinationAccountNumber());
					BankAccount targetAcc = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.TOPUP_SUSPENSE_ACCOUNT, OwnerType.BANK, null);
//				bankRequest.setTargetAccountNumber(targetAccount.getAccountNumber());
					tranx.setDestinationEQ3AccountNumber(targetAcc.getAccountNumber());
					LOG.debug("Done setting the dest accont number to " + tranx.getDestinationEQ3AccountNumber());
				}
			
		}  else if (zw.co.esolutions.ewallet.enums.TransactionType.BILLPAY.equals(tranx.getTransactionType())) {
			BankAccount bankAccount = bankService.getUniqueBankAccountByAccountNumber(tranx.getSourceAccountNumber());
			if (BankAccountType.E_WALLET.equals(bankAccount.getType())) {
				LOG.debug("Found bank account of type EWALLET " + bankAccount);
				LOG.debug("Got an EWALLET BILLPAY Request......");
				LOG.debug("Debit EQ3 POOL Account with amount.");
				BankAccount sourceAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.POOL_CONTROL, OwnerType.BANK, null);
				LOG.debug("EWALLET POOL account " + sourceAccount);
//			bankRequest.setSourceAccountNumber(sourceAccount.getAccountNumber());
				tranx.setSourceEQ3AccountNumber(sourceAccount.getAccountNumber());
				LOG.debug("The merchant id to get account for credit " + tranx.getBankMerchantId());
				// AND CREDIT Merchant SUSPENSE ACCOUNT
				LOG.debug("Dest Acc Number AND CREDIT MERCHANT SUSPENSE ACCOUNT: " + tranx.getDestinationEQ3AccountNumber() + " FOR Utility " + tranx.getUtilityName());
				BankAccount targetAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getBankMerchantId(), BankAccountType.MERCHANT_SUSPENSE, OwnerType.MERCHANT, null);
				LOG.debug("BANK MERCHANT SUSPENSE ACCOUNT " + targetAccount);
//			bankRequest.setTargetAccountNumber(targetAccount.getAccountNumber());
				tranx.setDestinationEQ3AccountNumber(targetAccount.getAccountNumber());
				LOG.debug("Done setting the dest accont number to " + tranx.getDestinationEQ3AccountNumber());
			} else {
				LOG.debug("Got an EQ3 BILLPAY Request......");
				LOG.debug("Debit EQ3 Customer Account with amount.");
//			bankRequest.setSourceAccountNumber(txn.getSourceAccountNumber());
				tranx.setSourceEQ3AccountNumber(tranx.getSourceAccountNumber());

				// AND CREDIT Merchant SUSPENSE ACCOUNT
				BankAccount targetAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getBankMerchantId(), BankAccountType.MERCHANT_SUSPENSE, OwnerType.MERCHANT, null);
				LOG.debug("BANK MERCHANT SUSPENSE ACCOUNT " + targetAccount);
//			bankRequest.setTargetAccountNumber(targetAccount.getAccountNumber());
				tranx.setDestinationEQ3AccountNumber(targetAccount.getAccountNumber());
				LOG.debug("Done setting the dest accont number to " + tranx.getDestinationEQ3AccountNumber());
			}
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.AGENT_CUSTOMER_DEPOSIT.equals(tranx.getTransactionType())) {
			// DR eWallet POOL CONTROL
			LOG.debug("Got an AGENT_CUSTOMER_DEPOSIT txn, setting the source account");
			BankAccount sourceAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.POOL_CONTROL, OwnerType.BANK, null);
			LOG.debug("EWALLET POOL account " + sourceAccount);
			tranx.setSourceEQ3AccountNumber(sourceAccount.getAccountNumber());
			LOG.debug("Source set as POOL Account..");
			// AND CREDIT EWALLET POOL
			BankAccount targetAcc = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getToBankId(), BankAccountType.POOL_CONTROL, OwnerType.BANK, null);
			tranx.setDestinationEQ3AccountNumber(targetAcc.getAccountNumber());
			LOG.debug("Done setting the dest accont number.");
		}  else if (zw.co.esolutions.ewallet.enums.TransactionType.AGENT_CUSTOMER_WITHDRAWAL.equals(tranx.getTransactionType())) {
			// DR eWallet POOL CONTROL
			LOG.debug("Got an AGENT_CUSTOMER_WITHDRAWAL txn, setting the source account");
			BankAccount sourceAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.POOL_CONTROL, OwnerType.BANK, null);
			LOG.debug("EWALLET POOL account " + sourceAccount);
			tranx.setSourceEQ3AccountNumber(sourceAccount.getAccountNumber());
			LOG.debug("Source set as POOL Account..");
			// AND CREDIT EWALLET POOL
			BankAccount targetAcc = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getToBankId(), BankAccountType.POOL_CONTROL, OwnerType.BANK, null);
			tranx.setDestinationEQ3AccountNumber(targetAcc.getAccountNumber());
			LOG.debug("Done setting the dest accont number.");
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL.equals(tranx.getTransactionType())) {
			// DR PAYOUT CONTROL
			LOG.debug("Got an AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL txn, setting the source account");
			BankAccount sourceAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.PAYOUT_CONTROL, OwnerType.BANK, null);
			LOG.debug("PAYOUT CONTROL account " + sourceAccount);
			tranx.setSourceEQ3AccountNumber(sourceAccount.getAccountNumber());
			LOG.debug("Source set as POOL Account..");
			// AND CREDIT EWALLET POOL
			BankAccount targetAcc = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.POOL_CONTROL, OwnerType.BANK, null);
			tranx.setDestinationEQ3AccountNumber(targetAcc.getAccountNumber());
			LOG.debug("Done setting the dest accont number.");
		}else if(zw.co.esolutions.ewallet.enums.TransactionType.AGENT_CASH_DEPOSIT.equals(tranx.getTransactionType())){
			// a deposit will debit Branch EWALLET CASH ACCOUNT
			LOG.debug("Got an agent deposit txn, setting the source account");
//			BankAccount sourceAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getBranchId(), BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT, OwnerType.BANK_BRANCH, null);
			BankAccount sourceAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getTransactionLocationId(), BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT, OwnerType.BANK_BRANCH, null);
			LOG.debug("Branch cash account " + sourceAccount);
			tranx.setSourceEQ3AccountNumber(sourceAccount.getAccountNumber());
			LOG.debug("Branch SRC Account Has been set...... going for the pool account");
			// and Credit EWALLET POOL ACCOUNT
			LOG.debug("Find POOL ACC for " + tranx.getToBankId() + " " + BankAccountType.POOL_CONTROL + " " + OwnerType.BANK);
			BankAccount targetAcc = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getToBankId(), BankAccountType.POOL_CONTROL, OwnerType.BANK, null);
			LOG.debug("Found the pool account for sure..." + targetAcc);
			tranx.setDestinationEQ3AccountNumber(targetAcc.getAccountNumber());
			LOG.debug("Done setting the dest accont number.");
		}  else if (zw.co.esolutions.ewallet.enums.TransactionType.AGENT_EWALLET_TO_BANKACCOUNT_TRANSFER.equals(tranx.getTransactionType())) {
			// a non HLDR TXF will debit EWALLET POOL
			LOG.debug("Got a EWAL-NON-HLDR txn, setting the source account");
			BankAccount sourceAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.POOL_CONTROL, OwnerType.BANK, null);
			LOG.debug("EWALLET POOL account " + sourceAccount);
//		bankRequest.setSourceAccountNumber(sourceAccount.getAccountNumber());
			tranx.setSourceEQ3AccountNumber(sourceAccount.getAccountNumber());
			LOG.debug("POOL Account Has been set...... going for the Branch Cash account");
			// AND CREDIT Beneficiary EQ3 Account
//		LOG.debug("Dest Acc Number : " + bankRequest.getTargetAccountNumber());
			tranx.setDestinationEQ3AccountNumber(tranx.getDestinationAccountNumber());
			LOG.debug("Done setting the dest accont number.");
		}else if(zw.co.esolutions.ewallet.enums.TransactionType.COMMISSION_SWEEPING.equals(tranx.getTransactionType())){
			LOG.debug("Got a COMMISSION SWEEPING txn ");
			//Dr Agents Commission	Suspense Account with Total Amount
			BankAccount sourceAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(),BankAccountType.AGENT_COMMISSION_SUSPENSE , OwnerType.BANK,null);
			LOG.debug("Agents Commission Suspense Account  "+sourceAccount);
			//Cr e-Wallet Pool Account with Total Amount		
			BankAccount destAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(tranx.getFromBankId(), BankAccountType.POOL_CONTROL, OwnerType.BANK, null);
			LOG.debug("EWallet Pool Account "+destAccount);
			tranx.setSourceEQ3AccountNumber(sourceAccount.getAccountNumber());
			LOG.debug("Done setting the src account number.");
			tranx.setDestinationEQ3AccountNumber(destAccount.getAccountNumber());
			LOG.debug("Done setting the dest account number.");
		}
		return tranx;
	}

	public ProcessTransaction createProcessTransaction(ProcessTransaction tranx) throws Exception {
		if (tranx.getId() == null) {
			tranx.setId(GenerateKey.generateEntityId());
		}
		if (tranx.getDateCreated() == null) {
			tranx.setDateCreated(new Date());
		}
		try {
			em.persist(tranx);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return tranx;
	}

	public ProcessTransaction updateProcessTransactionWith(String processTxnId, String responseCode, Date valueDate, String narrative) throws Exception {

		ProcessTransaction txn = this.findProcessTransactionById(processTxnId);

		try {

			txn.setResponseCode(responseCode);
			txn.setValueDate(valueDate);
			txn.setNarrative(narrative);

			txn = em.merge(txn);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return txn;
	}

	public ProcessTransaction updateProcessTransaction(ProcessTransaction tranx) throws Exception {
		LOG.debug("Upadting Process Trancsaction... ID = "+tranx.getId());
		try {
			ProcessTransaction txn = this.findProcessTransactionById(tranx.getId());
			tranx.setVersion(txn.getVersion());
			tranx = em.merge(tranx);
			LOG.debug("Update was successful ID = "+tranx.getId()+", Status = "+tranx.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return tranx;
	}

	public TransactionState createTransactionState(TransactionState tranxState) throws Exception {
		tranxState.setId(GenerateKey.generateEntityId());
		tranxState.setDateCreated(new Date());
		try {
			em.persist(tranxState);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return tranxState;
	}

	public TransactionState updateTransactionState(TransactionState tranxState) throws Exception {
		try {
			em.merge(tranxState);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return tranxState;
	}

	public ProcessTransaction findProcessTransactionById(String txnId) {
		ProcessTransaction txn;
		try {
			txn = em.find(ProcessTransaction.class, txnId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return txn;
	}

	public String processPinChangeRequest(ProcessTransaction txn, String oldPin, String newPin) throws Exception {
		txn = this.getProcessTransactionByMessageId(txn.getMessageId());
		if (txn != null) {
			if (!(txn.getResponseCode() == null || ResponseCode.E000.equals(ResponseCode.valueOf(txn.getResponseCode())))) {
				return txn.getResponseCode();
			}
		}
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		MobileProfile mobileProfile = customerService.getMobileProfileByBankAndMobileNumber(txn.getFromBankId(), txn.getSourceMobile());
		if (mobileProfile == null || mobileProfile.getId() == null) {
			throw new Exception("Mobile Profile not found");
		}
		String encPin = EncryptAndDecrypt.encrypt(newPin, mobileProfile.getMobileNumber());
		mobileProfile.setSecretCode(encPin);
		customerService.updateMobileProfile(mobileProfile, EWalletConstants.SYSTEM);
		txn.setStatus(TransactionStatus.COMPLETED);
		txn = this.updateProcessTransaction(txn);
		txn = this.promoteTxnState(txn, TransactionStatus.COMPLETED, "Pin Change Successful.");
		return ResponseCode.E000.name();

	}

	public String processMobileTxnPasscodeRequest(ProcessTransaction txn) throws Exception {
		txn = this.getProcessTransactionByMessageId(txn.getMessageId());

		if (txn != null) {
			if (!(txn.getResponseCode() == null || ResponseCode.E000.equals(ResponseCode.valueOf(txn.getResponseCode())))) {
				return txn.getResponseCode();
			}
		}
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		GenerateTxnPassCodeResp passcodeResp = customerService.generateTxnPassCode(txn.getSourceMobileId());
		LOG.debug(">>>>>>>>>>>>>>>> Arrived :" + passcodeResp);
		if (passcodeResp != null) {
			LOG.debug(">>>>>>>> Not NUll");
			txn.setPasscodePrompt(String.valueOf(passcodeResp.getFirstIndex()) + String.valueOf(passcodeResp.getSecondIndex()));
			txn.setTimeout(DateUtil.addHours(new Date(), 3));
			txn = this.updateProcessTransaction(txn);

			LOG.debug(">>>>>>>>>>>>>> UPDATED TXN");

			String response = "Please submit " + this.formatPasswordPart(passcodeResp.getFirstIndex()) + " and " + this.formatPasswordPart(passcodeResp.getSecondIndex()) + " parts of your password eg 6*73. ZB e-Wallet";

			this.promoteTxnState(txn, TransactionStatus.VERES, response);

			return response;

		} else {
			LOG.debug("######## Failed Passcode Generation");
			return ResponseCode.E704.name(); // failed to generate txn passcode
		}

	}

	private String formatPasswordPart(int number) {
		if (number == 1) {
			return "1st";
		} else if (number == 2) {
			return "2nd";
		} else if (number == 3) {
			return "3rd";
		}
		return number + "th";
	}
	
	
	public ProcessTransaction updateFailedEwalletTxn(ProcessTransaction txn, String narrative,TransactionStatus status) throws Exception{
		LOG.debug("Transaction ID : " + txn.getMessageId() + " " + txn.getId() + " Narrative : " + narrative);
		

		try {
			txn = this.getProcessTransactionByMessageId(txn.getMessageId());
			LOG.debug("TXN After the finder : " + txn);
			txn.setStatus(status);
			txn.setNarrative(narrative);
			txn = this.updateProcessTransaction(txn);
			
		} catch (Exception e) {
			throw e;
		}
		return txn;
	}

	public ProcessTransaction updateDayEndTxnState(ProcessTransaction txn, TransactionStatus status, String narrative) throws Exception {
		LOG.debug("Transaction ID : " + txn.getMessageId() + " " + txn.getId() + " Narrative : " + narrative);
		txn = this.getProcessTransactionByMessageId(txn.getMessageId());
		LOG.debug("TXN After the finder : " + txn);
		txn.setStatus(status);
		txn.setNarrative(narrative);

		try {
			txn = this.updateProcessTransaction(txn);
			
		} catch (Exception e) {
			throw e;
		}
		return txn;
	}
	
	public ProcessTransaction updateAdjustmentTxnState(ProcessTransaction txn, TransactionStatus status, String narrative) throws Exception {
		LOG.debug("1. postAdjustmenttnxs toewallet :::::::::: updateAdjustmentTxnState");
		LOG.debug("Transaction ID : " + txn.getMessageId() + " " + txn.getId() + " Narrative : " + narrative);
		txn = this.getProcessTransactionByMessageId(txn.getMessageId());
		LOG.debug("TXN After the finder : " + txn);
		txn.setStatus(status);
		txn.setNarrative(narrative);
		LOG.debug("2. postAdjustmenttnxs toewallet :::::::::: updating");
		try {
			txn = this.updateProcessTransaction(txn);
			LOG.debug("3. postAdjustmenttnxs toewallet :::::::::: shld be done updating");
		} catch (Exception e) {
			LOG.debug("4. postAdjustmenttnxs toewallet :::::::::: we got an exception "+e.getMessage());
			e.printStackTrace();
			throw e;
		}
		return txn;
	}
	
	
	
	
	public ProcessTransaction promoteTxnState(ProcessTransaction txn, TransactionStatus status, String narrative) throws Exception {
		LOG.debug("Transaction ID : " + txn.getMessageId() + " " + txn.getId() + " Narrative : " + narrative);
		txn = this.getProcessTransactionByMessageId(txn.getMessageId());
		LOG.debug("TXN After the finder : " + txn);
		txn.setStatus(status);
		TransactionState state = new TransactionState();
		state.setStatus(status);
		if (narrative != null) {
			state.setNarrative(narrative);
		}

		try {
			txn = this.updateProcessTransaction(txn);
			state.setProcessTransaction(txn);
			this.createTransactionState(state);
		} catch (Exception e) {
			throw e;
		}
		return txn;
	}

	@SuppressWarnings("unchecked")
	public List<TransactionState> getTransactionStatesByProcessTxnId(String processTxnId) throws Exception {
		List<TransactionState> results = null;
		try {
			Query query = em.createNamedQuery("getTransactionStateByTransaction");
			query.setParameter("transaction_id", processTxnId);
			results = (List<TransactionState>) query.getResultList();
		} catch (NoResultException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}

	public TransactionState getLatestTransactionStateByProcessTxnId(String processTxnId) throws Exception {
		return (TransactionState) this.getTransactionStatesByProcessTxnId(processTxnId).get(0);
	}

	public boolean passcodeIsValid(RequestInfo requestInfo, ProcessTransaction txn) throws Exception {
		String passcodeParts = requestInfo.getPasswordParts().trim();
		ValidateTxnPassCodeReq req = new ValidateTxnPassCodeReq();
		req.setMobileNumber(requestInfo.getSourceMobile());
		req.setFirstIndex(Integer.parseInt(txn.getPasscodePrompt().substring(0, 1)));
		req.setSecondIndex(Integer.parseInt(txn.getPasscodePrompt().substring(1)));
		req.setFirstValue(Integer.parseInt(passcodeParts.substring(0, 1)));
		req.setSecondValue(Integer.parseInt(passcodeParts.substring(1)));

		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();

		boolean isValid = customerService.txnPassCodeIsValid(req);

		return isValid;
	}

	public boolean customerAccountIsActive(ProcessTransaction txn) throws Exception {
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();

		MobileProfile mobileProfile = customerService.findMobileProfileById(txn.getSourceMobileId());
		BankAccount customerAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(mobileProfile.getCustomer().getId(), BankAccountType.E_WALLET, OwnerType.CUSTOMER, mobileProfile.getMobileNumber());

		// check whether account is active
		if (BankAccountStatus.ACTIVE.equals(customerAccount.getStatus())) {
			return true;
		} else {
			return false; // bank account is not active
		}
	}

	public String processPasswordRetry(ProcessTransaction txn) throws Exception {
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		if (txn != null) {
			if (!(txn.getResponseCode() == null || ResponseCode.E000.equals(ResponseCode.valueOf(txn.getResponseCode())))) {
				return txn.getResponseCode();
			}
		}
		if (txn.getPasswordRetryCount() != 3) {
			txn.setPasswordRetryCount(txn.getPasswordRetryCount() + 1);
			txn.setTimeout(DateUtil.addHours(new Date(), 3));
			this.updateProcessTransaction(txn);

			String message = "Passcode incorrect. Enter " + txn.getPasscodePrompt().substring(0, 1) + " and " + txn.getPasscodePrompt().substring(1) + " of your passcode.";

			return message;

		} else {
			// passcode entered incorrectly 3 times, lock the profile
			MobileProfile mobileProfile = customerService.findMobileProfileById(txn.getSourceMobileId());
			mobileProfile.setPasswordRetryCount(3);
			mobileProfile.setStatus(MobileProfileStatus.LOCKED);
			mobileProfile.setTimeout(DateUtil.convertToXMLGregorianCalendar(DateUtil.addHours(new Date(), 24)));

			customerService.updateMobileProfile(mobileProfile, EWalletConstants.SYSTEM);

			return ResponseCode.E707.name(); // passcode entered 3 times
			// incorrectly, profile locked
		}

	}

	
	public TransactionState getTransactionState(TransactionStatus status, String targetMobile, String secretCode, long amount, String reference) throws Exception {
		TransactionState state = null;
		try {
			Query query = em.createNamedQuery("getTransactionStateByVariables");
			query.setParameter("status", status);
			query.setParameter("transaction_targetMobile", targetMobile);
			query.setParameter("transaction_secretCode", secretCode);
			query.setParameter("transaction_amount", amount);
			query.setParameter("transaction_messageId", reference);

			state = (TransactionState) query.getSingleResult();
		} catch (NoResultException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return state;
	}

	@Override
	public long processEWalletBalanceRequest(ProcessTransaction txn, String customerAccountNumber) throws Exception {
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		BankAccount bankAccount = bankService.getUniqueBankAccountByAccountNumber(txn.getSourceAccountNumber());
		if (bankAccount != null && bankAccount.getId() != null) {
			return bankAccount.getRunningBalance();
		}
		return 0;
	}

	public BankRequest processBankAccountBalanceRequest(ProcessTransaction txn) throws Exception {
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		MobileProfile mobileProfile = customerService.findMobileProfileById(txn.getSourceMobileId());
		BankRequest bankRequest = new BankRequest();
		bankRequest.setSourceBankCode(txn.getBankReference());
		bankRequest.setTransactionType(zw.co.esolutions.ewallet.enums.TransactionType.BALANCE);
		bankRequest.setReference(txn.getMessageId());
		bankRequest.setSourceAccountNumber(txn.getSourceAccountNumber());
		bankRequest.setSourceMobileNumber(mobileProfile.getMobileNumber());

		return bankRequest;
	}

	@Override
	public ProcessTransaction validateEWalletToNonHolderTransferReq(ProcessTransaction txn) throws Exception {
		LOG.debug("In validate eWallet to Non Holder request");
		txn = this.addApplicableTariffToProcessTxn(txn);
		LOG.debug("Added the eWallet to Non Holder tariff");
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		BankAccount bankAccount = bankService.getBankAccountByAccountNumberAndOwnerType(txn.getSourceAccountNumber(), OwnerType.CUSTOMER);
		txn = this.checkTxnLimits(txn, bankAccount);
		LOG.debug("Check tcn limits returned " + txn.getResponseCode());
		if (!ResponseCode.E000.name().equalsIgnoreCase(txn.getResponseCode())) {
			return txn;
		}
		txn = this.checkFundsAvailability(txn, bankAccount);
		LOG.debug("Done check Funds availability and got " + txn.getResponseCode());
		return txn;
	}

	@Override
	public ProcessTransaction validateBillPayRequest(ProcessTransaction txn, BankAccount bankAccount) throws Exception {
		LOG.debug("In validate BillPay request");
		
		if (EWalletConstants.MERCHANT_NAME_DSTV.equals(txn.getUtilityName())) {
			LOG.debug("DSTV Billpay.. nw adding Bank Commission as tariff..");
			
			if (txn.getCommission() > 0) {
		
				LOG.debug("DSTV Commission is specified.. charge this one..");
				
				TransactionCharge transactionCharge = new TransactionCharge();
				transactionCharge.setStatus(txn.getStatus());
				transactionCharge.setTransactionType(zw.co.esolutions.ewallet.enums.TransactionType.TARIFF);
				transactionCharge.setTariffAmount(txn.getCommission());
				transactionCharge.setProcessTransaction(txn);
								
				transactionCharge = this.createTransactionCharge(transactionCharge);
				
				LOG.debug("DSTV Bank commission added successfully..");
				
			} else {
				LOG.debug("DSTV Bank commission is ZERO..");
				LOG.debug("Adding Tariff to DSTV BILL PAY");
				txn = this.addApplicableTariffToProcessTxn(txn);
				LOG.debug("Added the billpay tariff to DSTV BILL PAY");

			}
		} else {
			LOG.debug("Adding Tariff to EWALLET BILL PAY");
			txn = this.addApplicableTariffToProcessTxn(txn);
			LOG.debug("Added the billpay tariff to EWALLET BILL PAY");
		}
		
		txn = this.checkTxnLimits(txn, bankAccount);
		LOG.debug("Check tcn limits returned " + txn.getResponseCode());
		if (!ResponseCode.E000.name().equalsIgnoreCase(txn.getResponseCode())) {
			return txn;
		}
		if (BankAccountType.E_WALLET.equals(bankAccount.getType())) {
			LOG.debug("Bank account is ewallet... we validate funds availability");
			txn = this.checkFundsAvailability(txn, bankAccount);
			LOG.debug("Done check Funds availability and got " + txn.getResponseCode());
		}
		return txn;
	}

	@Override
	public ProcessTransaction validateTopupRequest(ProcessTransaction txn, BankAccount bankAccount) throws Exception {
		LOG.debug("In validate " + txn.getTransactionType());
		try {
			txn = this.addApplicableTariffToProcessTxn(txn);
			LOG.debug("Added the topup tariff");
		} catch (Exception e) {
			LOG.debug("No tariff defined for topup.... ignore it.");
		}

		txn = this.checkTxnLimits(txn, bankAccount);
		LOG.debug("Check tcn limits returned " + txn.getResponseCode());
		if (!ResponseCode.E000.name().equalsIgnoreCase(txn.getResponseCode())) {
			return txn;
		}
		if (BankAccountType.E_WALLET.equals(bankAccount.getType())) {
			LOG.debug("Bank account is ewallet... we validate funds availability");
			txn = this.checkFundsAvailability(txn, bankAccount);
			LOG.debug("Done check Funds availability and got " + txn.getResponseCode());
		}
		return txn;
	}

	public String processDepositTxnResponse(BankResponse bankResp) throws Exception {
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		BankRequest request = bankResp.getBankRequest();
		// retrieve txn and update
		ProcessTransaction txn = this.getProcessTransactionByMessageId(request.getReference());

		if (txn != null) {
			if (!(txn.getResponseCode() == null || ResponseCode.E000.equals(ResponseCode.valueOf(txn.getResponseCode())))) {
				return txn.getResponseCode();
			}
		}
		MobileProfile mobileProfile = customerService.getMobileProfileByBankAndMobileNumber(txn.getBankReference(), request.getSourceMobileNumber());

		txn.setBalance(this.getCustomerRunningBalance(mobileProfile.getId(), BankAccountType.E_WALLET) + request.getAmount());
		txn.setResponseCode(bankResp.getResponseCode().name());
		txn.setStatus(TransactionStatus.BANK_RESPONSE);
		txn = this.updateProcessTransaction(txn);
		txn = this.promoteTxnState(txn, TransactionStatus.BANK_RESPONSE, bankResp.getNarrative());

		// check whether deposit not already performed
		TransactionState latestState = this.getLatestTransactionStateByProcessTxnId(txn.getId());
		if (TransactionStatus.BANK_RESPONSE.equals(latestState.getStatus())) {
			if (bankResp.getValueDate() == null) {
				bankResp.setValueDate(new Date());
			}
			txn.setValueDate(bankResp.getValueDate());
			txn.setResponseCode(bankResp.getResponseCode().name());
			txn.setStatus(TransactionStatus.COMPLETED);
			// Update Process Txn
			txn = this.updateProcessTransaction(txn);
			LOG.debug(">>>>>>>> Finished updating Txn " + txn.getStatus().toString());
			this.promoteTxnState(txn, TransactionStatus.COMPLETED, ResponseCode.E000.getDescription());

		} else {
			return ResponseCode.E711.name(); // transaction already performed
		}

		return ResponseCode.E000.name();
	}

	public String initializeWithdrawalDetails(RequestInfo info) throws Exception {
		// Initialize the Process Txn Object
		ProcessTransaction processTxn = this.populateProcessTransaction(info);
		processTxn.setAmount(info.getAmount());
		// processTxn.setBankReference(info.getBankCode());
		processTxn.setNarrative("Registered Withdrawal");
		processTxn = this.createProcessTransaction(processTxn);
		if (processTxn == null || processTxn.getId() == null) {
			throw new Exception("Failed to create process txn.");
		}
		// Generate passcode prompt algorithm
		String response = this.processMobileTxnPasscodeRequest(processTxn);
		this.promoteTxnState(processTxn, TransactionStatus.VEREQ, response);
		return response;
	}

	public ProcessTransaction handleEwalletToEwalletTransfer(RequestInfo info) throws Exception {
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();

		LOG.debug("TARGET MOBILE : " + info.getTargetMobile() + " BANK ID : " + info.getTargetBankId());
		MobileProfile targetMobileProfile = customerService.getMobileProfileByBankAndMobileNumber(info.getTargetBankId(), info.getTargetMobile());

		if (targetMobileProfile == null || targetMobileProfile.getId() == null) {
			throw new Exception("Error processing transfer request, invalid target mobile.");
		}

		BankAccount srcAcc = bankService.getUniqueBankAccountByAccountNumberAndBankId(info.getSourceAccountNumber(), info.getSourceBankId());

		if (srcAcc == null || srcAcc.getId() == null) {
			throw new Exception("Error processing transfer request, invalid source bank account.");
		}
		BankAccount destnAcc = bankService.getUniqueBankAccountByAccountNumberAndBankId(info.getTargetMobile(), info.getTargetBankId());

		if (destnAcc == null || destnAcc.getId() == null) {
			throw new Exception("Error processing transfer request, invalid source mobile.");
		}

		// Initialize the Process Txn Object
		ProcessTransaction processTxn = this.populateProcessTransaction(info);
		processTxn.setAmount(info.getAmount());
		processTxn.setTargetMobileId(targetMobileProfile.getId());
		processTxn.setBankReference(info.getBankCode());
		processTxn.setNarrative("e-Wallet to e-Wallet Transfer.");
		if (srcAcc.getBranch().getBank().getId().equals(destnAcc.getBranch().getBank().getId())) {
			processTxn.setTransferType(TransferType.INTRABANK);
		} else {
			processTxn.setTransferType(TransferType.INTERBANK);
		}
		processTxn.setFromBankId(srcAcc.getBranch().getBank().getId());
		processTxn.setToBankId(destnAcc.getBranch().getBank().getId());
		processTxn.setStatus(TransactionStatus.BANK_REQUEST);
		processTxn = this.createProcessTransaction(processTxn);
		if (processTxn == null || processTxn == null) {
			throw new Exception("Failed to create process txn.");
		}

		this.promoteTxnState(processTxn, TransactionStatus.DRAFT, TransactionStatus.DRAFT.toString());
		// Ignoring Generate passcode prompt algorithm
		// return this.processMobileTxnPasscodeRequest(processTxn);
		processTxn = this.findProcessTransactionById(processTxn.getId());
		return processTxn;
	}

	public String processWithdrawalReq(ProcessTransaction processTxn) throws Exception {
		String response = null;
		response = this.validateRegisteredWithdrawal(processTxn);
		return response;
	}

	public String validateRegisteredWithdrawal(ProcessTransaction txn) throws Exception {

		Date withDate = new Date();
		Limit limit = null;
		Limit balanceLimit = null;
		String customerId = null;
		long value = txn.getAmount();
		BankAccount bankAccount = null;
		String bankId = null;

		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		LimitServiceSOAPProxy limitService = new LimitServiceSOAPProxy();

		txn = this.addApplicableTariffToProcessTxn(txn);
		if (txn != null) {
			if (!(txn.getResponseCode() == null || ResponseCode.E000.equals(ResponseCode.valueOf(txn.getResponseCode())))) {
				return txn.getResponseCode();
			}
		}

		LOG.debug(">>>>>>>>>>>>>>>>>>>> Ta Edda ");
		customerId = customerService.findMobileProfileById(txn.getSourceMobileId()).getCustomer().getId();

		LOG.debug(">>>>>>>>>>>>>>>>>>>> Id " + customerId);
		bankAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(customerId, BankAccountType.E_WALLET, OwnerType.CUSTOMER, txn.getSourceMobile());

		LOG.debug(">>>>>>>>>>>>>>>>>>>> Bank Acc " + bankAccount);

		bankId = bankAccount.getBranch().getBank().getId();
		limit = limitService.getValidLimitOnDateByBankId(zw.co.esolutions.ewallet.limitservices.service.TransactionType.WITHDRAWAL, BankAccountClass.valueOf(bankAccount.getAccountClass().toString()), DateUtil.convertToXMLGregorianCalendar(withDate), LimitPeriodType.TRANSACTION, bankId);

		balanceLimit = limitService.getValidLimitOnDateByBankId(zw.co.esolutions.ewallet.limitservices.service.TransactionType.BALANCE, BankAccountClass.valueOf(bankAccount.getAccountClass().toString()), DateUtil.convertToXMLGregorianCalendar(withDate), LimitPeriodType.TRANSACTION, bankId);

		if (limit.getMinValue() > value) {
			txn.setResponseCode(ResponseCode.E804.name());
			txn.setNarrative(ResponseCode.E804.getDescription() + " Minimum " + txn.getTransactionType().toString().replace("_", " ") + " amount is " + MoneyUtil.convertCentsToDollarsPatternNoCurrency(limit.getMinValue()) + ".");
			this.promoteTxnState(txn, TransactionStatus.FAILED, null);
			return ResponseCode.E804.name(); // Withdrawal amount is below min
			// withdrawal

		} else if (limit.getMaxValue() < value) {
			txn.setResponseCode(ResponseCode.E805.name());
			txn.setNarrative(ResponseCode.E805.getDescription() + " Maximum " + txn.getTransactionType().toString().replace("_", " ") + " amount is " + MoneyUtil.convertCentsToDollarsPatternNoCurrency(limit.getMaxValue()) + ".");
			this.promoteTxnState(txn, TransactionStatus.FAILED, null);
			return ResponseCode.E805.name(); // Withdrawal amount is above max
			// withdrawal

		} else if (0 > (bankAccount.getRunningBalance() - (value + txn.getTariffAmount()))) {
			txn.setResponseCode(ResponseCode.E808.name());
			txn.setNarrative(ResponseCode.E808.getDescription());
			this.promoteTxnState(txn, TransactionStatus.FAILED, null);
			return ResponseCode.E808.name(); // No funds in account.
		} else if ((bankAccount.getRunningBalance() - (value + txn.getTariffAmount())) < balanceLimit.getMinValue()) {

			if ((bankAccount.getRunningBalance() - balanceLimit.getMinValue()) >= balanceLimit.getMinValue() && (bankAccount.getRunningBalance() - balanceLimit.getMinValue()) >= limit.getMinValue()) {

				// Problem is gonna arise here
				ProcessTransaction tempTxn = txn;
				long max = bankAccount.getRunningBalance() - balanceLimit.getMinValue();
				long expectedAmount = 0;
				txn.setAmount(max);
				txn.setResponseCode(null);
				txn = this.addApplicableTariffToProcessTxn(txn);

				if (!ResponseCode.E000.equals(ResponseCode.valueOf(txn.getResponseCode()))) {
					// If the second trial fails then do as usual
					txn.setNarrative(ResponseCode.E810.getDescription());

					// than the expected one
				} else {
					expectedAmount = max - txn.getTariffAmount();
					if (bankAccount.getRunningBalance() - expectedAmount >= balanceLimit.getMinValue()) {
						txn.setNarrative(ResponseCode.E810.getDescription() + " Expected maximum withdrawal is " + MoneyUtil.convertCentsToDollarsPatternNoCurrency(expectedAmount) + ".");
					} else {
						txn.setNarrative(ResponseCode.E810.getDescription());
					}
				}

				txn.setResponseCode(ResponseCode.E810.name());
				txn.setAmount(tempTxn.getAmount());
				txn.setTariffId(tempTxn.getTariffId());
				txn.setTariffAmount(tempTxn.getTariffAmount());
				this.promoteTxnState(txn, TransactionStatus.FAILED, null);
				return ResponseCode.E810.name(); // Withdrawal amount is higher

			} else {
				txn.setResponseCode(ResponseCode.E801.name());
				txn.setNarrative(ResponseCode.E801.getDescription());
				this.promoteTxnState(txn, TransactionStatus.FAILED, null);
				return ResponseCode.E801.name();// Insufficient funds to
				// withdrawal
			}

		}

		return ResponseCode.E000.name();
	}

	public ProcessTransaction addApplicableTariffToProcessTxn(ProcessTransaction txn) throws Exception {
		Tariff tariff;
		long value = 0;
		TransactionLocationType locationType;

		if (txn != null) {
			if (!(txn.getResponseCode() == null || ResponseCode.E000.equals(ResponseCode.valueOf(txn.getResponseCode())))) {
				return txn;
			}
		}

		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		TariffServiceSOAPProxy tariffService = new TariffServiceSOAPProxy();

		Customer customer = customerService.findMobileProfileById(txn.getSourceMobileId()).getCustomer();
		if (TransactionLocationType.ATM.equals(txn.getTransactionLocationType()) || TransactionLocationType.SMS.equals(txn.getTransactionLocationType()) ||
				TransactionLocationType.USSD.equals(txn.getTransactionLocationType()) || TransactionLocationType.MOBILE_WEB.equals(txn.getTransactionLocationType())) {
			locationType = TransactionLocationType.BANK_BRANCH;
		} else {
			locationType = txn.getTransactionLocationType();
		}
		LOG.debug("Looking for tariff>>>>>>>>>>>>>>>>>>>> TXN TYPE in txn " + txn.getTransactionType().toString() + " txn type send to tariff " + zw.co.esolutions.ewallet.tariffservices.service.TransactionType.valueOf(txn.getTransactionType().toString()));

		if(zw.co.esolutions.ewallet.enums.TransactionType.AGENT_CASH_DEPOSIT.equals(txn.getTransactionType())) {
			tariff = tariffService.retrieveAppropriateTariff(CustomerClass.AGENT, zw.co.esolutions.ewallet.tariffservices.service.TransactionType.valueOf(txn.getTransactionType().toString()), AgentType.valueOf(locationType.toString()), txn.getAmount(), txn.getFromBankId());
		} else if (zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TO_EWALLET_TRANSFER.equals(txn.getTransactionType())
				|| zw.co.esolutions.ewallet.enums.TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER.equals(txn.getTransactionType())) { 
			LOG.debug("Transaction is of type " + txn.getTransactionType() + "... check target customer AUTO_REG status..");
			
			Customer targetCustomer = customerService.findMobileProfileById(txn.getTargetMobileId()).getCustomer();
			
			LOG.debug("Target Customer is in AUTOREG STATUS :   " + targetCustomer.getCustomerAutoRegStatus());
			
			if (CustomerAutoRegStatus.YES.equals(targetCustomer.getCustomerAutoRegStatus())) {
				LOG.debug("Charge NONHOLDER tariff for this transaction.");
				if (zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TO_EWALLET_TRANSFER.equals(txn.getTransactionType())) {
					tariff = tariffService.retrieveAppropriateTariff(CustomerClass.valueOf(customer.getCustomerClass().toString()), zw.co.esolutions.ewallet.tariffservices.service.TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER, AgentType.valueOf(locationType.toString()), txn.getAmount(), txn.getFromBankId());
					LOG.debug("Found Tariff EW_TO_NON HOLDER: " + tariff);
				} else {
					tariff = tariffService.retrieveAppropriateTariff(CustomerClass.valueOf(customer.getCustomerClass().toString()), zw.co.esolutions.ewallet.tariffservices.service.TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER, AgentType.valueOf(locationType.toString()), txn.getAmount(), txn.getFromBankId());
					LOG.debug("Found Tariff BANK_TO_NON HOLDER: " + tariff);
				}
			} else {
				LOG.debug("Charge NORMAL Tariff for this transaction..");
				tariff = tariffService.retrieveAppropriateTariff(CustomerClass.valueOf(customer.getCustomerClass().toString()), zw.co.esolutions.ewallet.tariffservices.service.TransactionType.valueOf(txn.getTransactionType().toString()), AgentType.valueOf(locationType.toString()), txn.getAmount(), txn.getFromBankId());
			}
		} else {
			AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
			Agent agent = agentService.getAgentByCustomerId(customer.getId());
			if(agent != null && agent.getId()!= null){
				LOG.debug("Customer is an Agent we set the customer class");
				tariff = tariffService.retrieveAppropriateTariff(CustomerClass.AGENT, zw.co.esolutions.ewallet.tariffservices.service.TransactionType.valueOf(txn.getTransactionType().toString()), AgentType.AGENT, txn.getAmount(), txn.getFromBankId());
			}else{
				LOG.debug("Customer not an agent");
				tariff = tariffService.retrieveAppropriateTariff(CustomerClass.valueOf(customer.getCustomerClass().toString()), zw.co.esolutions.ewallet.tariffservices.service.TransactionType.valueOf(txn.getTransactionType().toString()), AgentType.valueOf(locationType.toString()), txn.getAmount(), txn.getFromBankId());
			}
			
			
		}
		
		LOG.debug("Tariff FOUND : " + tariff);
		if (tariff != null && tariff.getId() != null) {
			value = this.getTariffAmount(tariff, txn.getAmount());
			LOG.debug("Adding trariff as transaction charge : " + value);
			// Populating process txn
			txn.setTariffId(tariff.getId());
			txn.setTariffAmount(value);
			if (value > 0) {
				TransactionCharge transactionCharge = new TransactionCharge();
				transactionCharge.setStatus(txn.getStatus());
				transactionCharge.setTransactionType(zw.co.esolutions.ewallet.enums.TransactionType.TARIFF);
				transactionCharge.setTariffId(tariff.getId());
				transactionCharge.setTariffAmount(value);
				
				transactionCharge.setProcessTransaction(txn);
				
				LOG.debug("Add agentNumber if applicable.." + txn.getAgentNumber());
				transactionCharge.setAgentNumber(txn.getAgentNumber());
				
				transactionCharge = this.createTransactionCharge(transactionCharge);
			}
			txn = this.updateProcessTransaction(txn);
			LOG.debug("Done adding trariff as transaction charge : ");
		} else {
			// the tariff is not available, assume a non-tariffable txn
			LOG.debug("Tariff is null, gone to else : ");
			txn.setResponseCode(ResponseCode.E821.name());
			txn.setNarrative(ResponseCode.E821.getDescription());
			txn = this.promoteTxnState(txn, TransactionStatus.FAILED, null);
		}

		LOG.debug("Returning  : ");
		return txn;
	}

	public long getTotalTransactionAmount(ProcessTransaction txn) throws Exception {
		long amt = txn.getAmount();
		List<TransactionCharge> charges = this.getTransactionChargeByProcessTransactionId(txn.getId());
		if (charges != null) {
			for (TransactionCharge transactionCharge : charges) {
				amt += transactionCharge.getTariffAmount();
			}
		}
		LOG.debug("Total txn Amt for " + txn.getMessageId() + " TXN TYPE " + txn.getTransactionType() + " is " + amt);
		return amt;
	}

	// public ProcessTransaction validateAccountTransfer(ProcessTransaction txn)
	// throws Exception {
	//
	// CustomerServiceSOAPProxy customerService = new
	// CustomerServiceSOAPProxy();
	// BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
	// LimitServiceSOAPProxy limitService = new LimitServiceSOAPProxy();
	//
	// Date txnDate = new Date();
	// String sourceCustId;
	// String destnCustId;
	// long transferAmount;
	// Limit sourceTransferLimit;
	// Limit sourceBalLimit;
	// BankAccount sourceBankAccount;
	// Limit destnBalLimit;
	// BankAccount destnBankAccount;
	// String bankId;
	//
	// txn = this.addApplicableTariffToProcessTxn(txn);
	//
	// if (txn != null) {
	// if (!(txn.getResponseCode() == null ||
	// ResponseCode.E000.equals(ResponseCode.valueOf(txn.getResponseCode())))) {
	// return txn.getResponseCode();
	// }
	// }
	//
	// sourceCustId =
	// customerService.findMobileProfileById(txn.getSourceMobileId()).getCustomer().getId();
	// transferAmount = txn.getAmount();
	//
	// // Retrieve Source Details
	// sourceBankAccount =
	// bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(sourceCustId,
	// BankAccountType.E_WALLET, OwnerType.CUSTOMER);
	// bankId = sourceBankAccount.getBranch().getBank().getId();
	// ResponseCode responseCode;
	// sourceBalLimit =
	// limitService.getValidLimitOnDateByBankId(zw.co.esolutions.ewallet.limitservices.service.TransactionType.BALANCE,
	// BankAccountClass.valueOf(sourceBankAccount.getAccountClass().toString()),
	// DateUtil.convertToXMLGregorianCalendar(txnDate),
	// LimitPeriodType.TRANSACTION, bankId);
	// transferAmount = this.getTotalTransactionAmount(txn);
	// double balanceAfter = sourceBankAccount.getRunningBalance() -
	// transferAmount;
	// if(sourceBalLimit != null ){
	// if(sourceBalLimit.getMinValue() > balanceAfter){
	// txn.setResponseCode(ResponseCode.E808.name());
	// txn.setNarrative("Insufficient funds. The transaction will push your account balance below minimum.");
	// txn = this.updateProcessTransaction(txn);
	// this.promoteTxnState(txn, TransactionStatus.FAILED, txn.getNarrative());
	// responseCode = ResponseCode.E808; // No funds in the account
	// }else{
	// responseCode = ResponseCode.E000;
	// }
	// }else{
	// //use zero coz no limit is set
	// if(0 > balanceAfter){
	// txn.setResponseCode(ResponseCode.E808.name());
	// txn.setNarrative("Insufficient funds. The transaction will push your account balance below minimum.");
	// txn = this.updateProcessTransaction(txn);
	// this.promoteTxnState(txn, TransactionStatus.FAILED, txn.getNarrative());
	// responseCode = ResponseCode.E808; // No funds in the account
	// }else{
	// responseCode = ResponseCode.E000;
	// }
	// }
	// if(!ResponseCode.E000.equals(responseCode)){
	// return responseCode.name();
	// }
	// // For destn customer who is an E-Wallet Holder
	// if
	// (zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TO_EWALLET_TRANSFER.equals(txn.getTransactionType()))
	// {
	// destnCustId =
	// customerService.findMobileProfileById(txn.getTargetMobileId()).getCustomer().getId();
	//
	// sourceBalLimit =
	// limitService.getValidLimitOnDateByBankId(zw.co.esolutions.ewallet.limitservices.service.TransactionType.BALANCE,
	// BankAccountClass.valueOf(sourceBankAccount.getAccountClass().toString()),
	// DateUtil.convertToXMLGregorianCalendar(txnDate),
	// LimitPeriodType.TRANSACTION, bankId);
	//
	// sourceTransferLimit =
	// limitService.getValidLimitOnDateByBankId(zw.co.esolutions.ewallet.limitservices.service.TransactionType.valueOf(txn.getTransactionType().toString()),
	// BankAccountClass.valueOf(sourceBankAccount.getAccountClass().toString()),
	// DateUtil.convertToXMLGregorianCalendar(txnDate),
	// LimitPeriodType.TRANSACTION, bankId);
	//
	// destnBankAccount =
	// bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(destnCustId,
	// BankAccountType.E_WALLET, OwnerType.CUSTOMER);
	// bankId = destnBankAccount.getBranch().getBank().getId();
	//
	// destnBalLimit =
	// limitService.getValidLimitOnDateByBankId(zw.co.esolutions.ewallet.limitservices.service.TransactionType.BALANCE,
	// BankAccountClass.valueOf(destnBankAccount.getAccountClass().toString()),
	// DateUtil.convertToXMLGregorianCalendar(txnDate),
	// LimitPeriodType.TRANSACTION, bankId);
	//
	// // Transfer to registered user
	// return this.validateRegisteredTransfer(sourceTransferLimit,
	// sourceBankAccount, sourceBalLimit, destnBankAccount, destnBalLimit,
	// transferAmount, txn.getTariffAmount(), txn);
	//
	// } else if
	// (zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER.equals(txn.getTransactionType()))
	// {
	// LOG.debug("Non holder transfer : validate if statement : ");
	//
	// sourceBalLimit =
	// limitService.getValidLimitOnDateByBankId(zw.co.esolutions.ewallet.limitservices.service.TransactionType.BALANCE,
	// BankAccountClass.valueOf(sourceBankAccount.getAccountClass().toString()),
	// DateUtil.convertToXMLGregorianCalendar(txnDate),
	// LimitPeriodType.TRANSACTION, bankId);
	// LOG.debug("Source Balance Limit : " + sourceBalLimit);
	// sourceTransferLimit =
	// limitService.getValidLimitOnDateByBankId(zw.co.esolutions.ewallet.limitservices.service.TransactionType.valueOf(txn.getTransactionType().toString()),
	// BankAccountClass.valueOf(sourceBankAccount.getAccountClass().toString()),
	// DateUtil.convertToXMLGregorianCalendar(txnDate),
	// LimitPeriodType.TRANSACTION, bankId);
	// LOG.debug("Source Transfer Limit : " + sourceTransferLimit);
	// // transfer to non-holder
	// return this.validateNonHolderTransfer(sourceTransferLimit,
	// sourceBankAccount, sourceBalLimit, transferAmount, txn.getTariffAmount(),
	// txn);
	// } else if
	// (zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER.equals(txn.getTransactionType()))
	// {
	// LOG.debug("PARAMS : " + txn.getTransactionType() + " " +
	// sourceBankAccount.getAccountClass() + " " + txnDate + " " + bankId);
	// sourceTransferLimit =
	// limitService.getValidLimitOnDateByBankId(zw.co.esolutions.ewallet.limitservices.service.TransactionType.valueOf(txn.getTransactionType().toString()),
	// BankAccountClass.valueOf(sourceBankAccount.getAccountClass().toString()),
	// DateUtil.convertToXMLGregorianCalendar(txnDate),
	// LimitPeriodType.TRANSACTION, bankId);
	//
	// // transfer to non-holder
	// return this.validateBankAccountTransfer(sourceTransferLimit,
	// transferAmount, txn);
	// } else if
	// (zw.co.esolutions.ewallet.enums.TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER.equals(txn.getTransactionType()))
	// {
	// LOG.debug("Validating limits on the BA to NHT ... ");
	// sourceTransferLimit =
	// limitService.getValidLimitOnDateByBankId(zw.co.esolutions.ewallet.limitservices.service.TransactionType.valueOf(txn.getTransactionType().toString()),
	// BankAccountClass.valueOf(sourceBankAccount.getAccountClass().toString()),
	// DateUtil.convertToXMLGregorianCalendar(txnDate),
	// LimitPeriodType.TRANSACTION, bankId);
	// LOG.debug("Found a limit " + sourceTransferLimit);
	// // transfer to non-holder
	// return this.validateBankAccountTransfer(sourceTransferLimit,
	// transferAmount, txn);
	// } else if
	// (zw.co.esolutions.ewallet.enums.TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER.equals(txn.getTransactionType()))
	// {
	//
	// sourceTransferLimit =
	// limitService.getValidLimitOnDateByBankId(zw.co.esolutions.ewallet.limitservices.service.TransactionType.valueOf(txn.getTransactionType().toString()),
	// BankAccountClass.valueOf(sourceBankAccount.getAccountClass().toString()),
	// DateUtil.convertToXMLGregorianCalendar(txnDate),
	// LimitPeriodType.TRANSACTION, bankId);
	//
	// // transfer to non-holder
	// return this.validateBankAccountTransfer(sourceTransferLimit,
	// transferAmount, txn);
	// } else if
	// (zw.co.esolutions.ewallet.enums.TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER.equals(txn.getTransactionType()))
	// {
	// LOG.debug("destination mobile id " + txn.getTargetMobileId());
	//
	// destnCustId =
	// customerService.getMobileProfileByBankAndMobileNumber(txn.getToBankId(),
	// txn.getTargetMobile()).getCustomer().getId();
	// sourceTransferLimit =
	// limitService.getValidLimitOnDateByBankId(zw.co.esolutions.ewallet.limitservices.service.TransactionType.valueOf(txn.getTransactionType().toString()),
	// BankAccountClass.valueOf(sourceBankAccount.getAccountClass().toString()),
	// DateUtil.convertToXMLGregorianCalendar(txnDate),
	// LimitPeriodType.TRANSACTION, bankId);
	//
	// destnBankAccount =
	// bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(destnCustId,
	// BankAccountType.E_WALLET, OwnerType.CUSTOMER);
	// bankId = destnBankAccount.getBranch().getBank().getId();
	//
	// destnBalLimit =
	// limitService.getValidLimitOnDateByBankId(zw.co.esolutions.ewallet.limitservices.service.TransactionType.BALANCE,
	// BankAccountClass.valueOf(destnBankAccount.getAccountClass().toString()),
	// DateUtil.convertToXMLGregorianCalendar(txnDate),
	// LimitPeriodType.TRANSACTION, bankId);
	//
	// // Transfer to registered user
	// return this.validateBankToEWalletTransfer(sourceTransferLimit,
	// destnBankAccount, destnBalLimit, transferAmount, txn);
	// }
	// return ResponseCode.E506.name();
	//
	// }

	// private String validateRegisteredTransfer(Limit srcTrfLimit, BankAccount
	// sourceBankAccount, Limit sourceBalLimit, BankAccount destnbBankAccount,
	// Limit destnBalLimit, long value, long tariffAmount, ProcessTransaction
	// processTxn) throws Exception {
	// if(sourceBalLimit == null){
	// // Check for balance limits in source account
	// if ((sourceBankAccount.getRunningBalance() - (value + tariffAmount)) < 0)
	// {
	// processTxn.setResponseCode(ResponseCode.E808.name());
	// processTxn.setNarrative("Insufficient funds. The transaction will push your account balance below minimum.");
	// // processTxn = this.updateProcessTransaction(processTxn);
	// this.promoteTxnState(processTxn, TransactionStatus.FAILED,
	// processTxn.getNarrative());
	// return ResponseCode.E808.name(); // No funds in the account
	// }
	// }else{
	// if ((sourceBankAccount.getRunningBalance() - (value + tariffAmount)) <
	// sourceBalLimit.getMinValue()) {
	// processTxn.setResponseCode(ResponseCode.E800.name());
	// processTxn.setNarrative("Insufficient funds. The transaction will push your account balance below minimum.");
	// this.promoteTxnState(processTxn, TransactionStatus.FAILED,
	// processTxn.getNarrative());
	// return ResponseCode.E800.name(); // Insufficient funds only
	// }
	// }

	// if (srcTrfLimit == null || sourceBalLimit == null || destnBalLimit ==
	// null) {
	// processTxn.setResponseCode(ResponseCode.E820.name());
	// processTxn.setNarrative(ResponseCode.E820.getDescription());
	// this.promoteTxnState(processTxn, TransactionStatus.FAILED,
	// processTxn.getNarrative());
	// LOG.debug("Limit is null");
	// throw new Exception("Invalid transaction limits. Limits NULL.");
	// }
	// if (srcTrfLimit != null ){
	// // Check for transfer values eligibility
	// if (srcTrfLimit.getMinValue() > processTxn.getAmount()) {
	// processTxn.setResponseCode(ResponseCode.E814.name());
	// processTxn.setNarrative("Transaction amount is below minimum.");
	// this.promoteTxnState(processTxn, TransactionStatus.FAILED,
	// processTxn.getNarrative());
	// return ResponseCode.E814.name(); // Transfer amount is below minimum
	// }
	// if (srcTrfLimit.getMaxValue() < processTxn.getAmount()) {
	// processTxn.setResponseCode(ResponseCode.E815.name());
	// processTxn.setNarrative("Transaction amount is greater than maximim.");
	// this.promoteTxnState(processTxn, TransactionStatus.FAILED,
	// processTxn.getNarrative());
	// return ResponseCode.E815.name(); // Transfer amount is above max
	// }
	// }else{
	// LOG.debug("Transfer amount is limitless....");
	// }
	//
	//
	// // check for account balances of destination state
	// if ((destnbBankAccount.getRunningBalance() + value) >
	// destnBalLimit.getMaxValue()) {
	// processTxn.setResponseCode(ResponseCode.E807.name());
	// processTxn.setNarrative("Transfer will exceed maximum possible account balance.");
	// this.promoteTxnState(processTxn, TransactionStatus.FAILED,
	// processTxn.getNarrative());
	// return ResponseCode.E807.name(); // Transfer amount will exceed
	// // maximum balance in the
	// // destination account
	// }
	// if (destnbBankAccount.getRunningBalance() == destnBalLimit.getMaxValue())
	// {
	// processTxn.setResponseCode(ResponseCode.E813.name());
	// processTxn.setNarrative(ResponseCode.E813.getDescription());
	// this.promoteTxnState(processTxn, TransactionStatus.FAILED,
	// processTxn.getNarrative());
	// return ResponseCode.E813.name(); // Account is full, cannot accept
	// // transfers
	// }
	//
	// return ResponseCode.E000.name();
	// }

	// private String validateBankToEWalletTransfer(Limit srcTrfLimit,
	// BankAccount destnbBankAccount, Limit destnBalLimit, long value,
	// ProcessTransaction processTxn) throws Exception {
	// if (srcTrfLimit == null || destnBalLimit == null) {
	// processTxn.setResponseCode(ResponseCode.E820.name());
	// processTxn.setNarrative(ResponseCode.E820.getDescription());
	// this.promoteTxnState(processTxn, TransactionStatus.FAILED,
	// "Invalid transaction limits. Limits NULL");
	// LOG.debug("Limit is null");
	// throw new Exception("Invalid transaction limits. Limits NULL.");
	// }
	// // Check for transfer values eligibility
	// if (srcTrfLimit.getMinValue() > value) {
	// processTxn.setResponseCode(ResponseCode.E814.name());
	// processTxn.setNarrative(ResponseCode.E814.getDescription() + " Minimum "
	// + processTxn.getTransactionType().toString().replace("_", " ") +
	// " amount is " +
	// MoneyUtil.convertCentsToDollarsPatternNoCurrency(srcTrfLimit.getMinValue())
	// + ".");
	// this.promoteTxnState(processTxn, TransactionStatus.FAILED, null);
	// return ResponseCode.E814.name(); // Transfer amount is below minimum
	// }
	// if (srcTrfLimit.getMaxValue() < value) {
	// processTxn.setResponseCode(ResponseCode.E815.name());
	// processTxn.setNarrative(ResponseCode.E815.getDescription() + " Maximum "
	// + processTxn.getTransactionType().toString().replace("_", " ") +
	// " amount is " +
	// MoneyUtil.convertCentsToDollarsPatternNoCurrency(srcTrfLimit.getMaxValue())
	// + ".");
	// this.promoteTxnState(processTxn, TransactionStatus.FAILED, null);
	// return ResponseCode.E815.name(); // Transfer amount is above max
	// }
	//
	// // check for account balances of destination state
	// if ((destnbBankAccount.getRunningBalance() + value) >
	// destnBalLimit.getMaxValue()) {
	// long txfAmount = destnBalLimit.getMaxValue() -
	// destnbBankAccount.getRunningBalance();
	// processTxn.setResponseCode(ResponseCode.E807.name());
	// processTxn.setNarrative(ResponseCode.E807.getDescription() +
	// " Destination account accepts a maximum " +
	// processTxn.getTransactionType().toString().replace("_", " ") +
	// " amount of " +
	// MoneyUtil.convertCentsToDollarsPatternNoCurrency(txfAmount) +
	// " at the momemt.");
	// this.promoteTxnState(processTxn, TransactionStatus.FAILED, null);
	// return ResponseCode.E807.name(); // Transfer amount will execeed
	// // maximum balance in the
	// // destination account
	// }
	// if (destnbBankAccount.getRunningBalance() == destnBalLimit.getMaxValue())
	// {
	// processTxn.setResponseCode(ResponseCode.E813.name());
	// processTxn.setNarrative(ResponseCode.E813.getDescription());
	// this.promoteTxnState(processTxn, TransactionStatus.FAILED, null);
	// return ResponseCode.E813.name(); // Account is full, cannot accept
	// // transfers
	// }
	//
	// return ResponseCode.E000.name();
	// }

	// private String validateNonHolderTransfer(Limit srcTrfLimit, BankAccount
	// sourceBankAccount, Limit sourceBalLimit, long value, long tariffAmount,
	// ProcessTransaction processTxn) throws Exception {
	// if(sourceBalLimit == null){
	// // Check for balance limits in source account
	// if ((sourceBankAccount.getRunningBalance() - (value + tariffAmount)) < 0)
	// {
	// processTxn.setResponseCode(ResponseCode.E808.name());
	// processTxn.setNarrative("Insufficient funds. The transaction will push your account balance below minimum.");
	// // processTxn = this.updateProcessTransaction(processTxn);
	// this.promoteTxnState(processTxn, TransactionStatus.FAILED,
	// processTxn.getNarrative());
	// return ResponseCode.E808.name(); // No funds in the account
	// }
	// }else{
	// if ((sourceBankAccount.getRunningBalance() - (value + tariffAmount)) <
	// sourceBalLimit.getMinValue()) {
	// processTxn.setResponseCode(ResponseCode.E800.name());
	// processTxn.setNarrative("Insufficient funds. The transaction will push your account balance below minimum.");
	// this.promoteTxnState(processTxn, TransactionStatus.FAILED,
	// processTxn.getNarrative());
	// return ResponseCode.E800.name(); // Insufficient funds only
	// }
	// }
	// if (srcTrfLimit == null || sourceBalLimit == null) {
	// processTxn.setResponseCode(ResponseCode.E820.name());
	// processTxn.setNarrative(ResponseCode.E820.getDescription());
	// processTxn = this.promoteTxnState(processTxn, TransactionStatus.FAILED,
	// processTxn.getNarrative());
	// LOG.debug("Limit is null");
	// throw new Exception("Invalid transaction limits. Limits NULL.");
	// }
	// // Check for transfer values eligibility
	// if (srcTrfLimit.getMinValue() > value) {
	// LOG.debug("Value of TXN is less than the minimum ");
	// processTxn.setResponseCode(ResponseCode.E814.name());
	// processTxn.setNarrative("Transaction amount is below minimun.");
	// processTxn = this.promoteTxnState(processTxn, TransactionStatus.FAILED,
	// processTxn.getNarrative());
	// LOG.debug("Return TXF AMT Below Min");
	// return ResponseCode.E814.name(); // Transfer amount is below minimum
	// }
	// if (srcTrfLimit.getMaxValue() < value) {
	// processTxn.setResponseCode(ResponseCode.E815.name());
	// processTxn.setNarrative("Transaction amount is greater that maximum.");
	// processTxn = this.promoteTxnState(processTxn, TransactionStatus.FAILED,
	// processTxn.getNarrative());
	// LOG.debug("Return TXF AMT Below Min");
	// return ResponseCode.E815.name(); // Transfer amount is above max
	// }
	//
	// // Check for balance limits in source account
	// if ((sourceBankAccount.getRunningBalance() - (value + tariffAmount)) < 0)
	// {
	// LOG.debug("Complex insufficient funds.");
	// processTxn.setResponseCode(ResponseCode.E808.name());
	// processTxn.setNarrative("Insufficient funds.");
	// processTxn = this.promoteTxnState(processTxn, TransactionStatus.FAILED,
	// processTxn.getNarrative());
	// LOG.debug("Return Insufficient funds");
	// return ResponseCode.E808.name(); // No funds in the account
	// }
	// if ((sourceBankAccount.getRunningBalance() - (value + tariffAmount)) <
	// sourceBalLimit.getMinValue()) {
	// LOG.debug("Insufficient funds ....... ");
	// processTxn.setResponseCode(ResponseCode.E800.name());
	// processTxn.setNarrative("Insufficient funds.");
	// processTxn = this.promoteTxnState(processTxn, TransactionStatus.FAILED,
	// processTxn.getNarrative());
	// LOG.debug("Insufficient funds.....");
	// return ResponseCode.E800.name(); // Insufficient funds only
	// }
	// LOG.debug("Return SUCCESS in validation : " + processTxn.getMessageId());
	// return ResponseCode.E000.name();
	// }

	// private String validateBankAccountTransfer(Limit srcTrfLimit, long value,
	// ProcessTransaction processTxn) throws Exception {
	// if (srcTrfLimit == null) {
	// processTxn.setResponseCode(ResponseCode.E820.name());
	// processTxn.setNarrative(ResponseCode.E820.getDescription());
	// this.promoteTxnState(processTxn, TransactionStatus.FAILED,
	// processTxn.getNarrative());
	// LOG.debug("Limit is null");
	// return ResponseCode.E820.name();
	// }
	// // Check for transfer values eligibility
	// if (srcTrfLimit.getMinValue() > value) {
	// processTxn.setResponseCode(ResponseCode.E814.name());
	// processTxn.setNarrative("Transaction amount is less than minimum.");
	// this.promoteTxnState(processTxn, TransactionStatus.FAILED,
	// processTxn.getNarrative());
	// LOG.debug("Amount below min...");
	// return ResponseCode.E814.name(); // Transfer amount is below minimum
	// }
	// if (srcTrfLimit.getMaxValue() < value) {
	// processTxn.setResponseCode(ResponseCode.E815.name());
	// processTxn.setNarrative("Transaction amount is greater than maximum.");
	// this.promoteTxnState(processTxn, TransactionStatus.FAILED,
	// processTxn.getNarrative());
	// LOG.debug("Amt above limit.........");
	// return ResponseCode.E815.name(); // Transfer amount is above max
	// }
	// LOG.debug("Success");
	// return ResponseCode.E000.name();
	// }

	@Override
	public BankRequest populateBankRequest(ProcessTransaction txn) throws Exception {
		BankRequest bankRequest = new BankRequest();

		bankRequest.setTransactionType(txn.getTransactionType());
		bankRequest.setSourceMobileNumber(txn.getSourceMobile());
		bankRequest.setTargetMobileNumber(txn.getTargetMobile());
		bankRequest.setReference(txn.getMessageId());
		bankRequest.setSourceBankCode(txn.getBankReference());
		bankRequest.setAmount(txn.getAmount());

		bankRequest.setUtilityName(txn.getUtilityName());
		bankRequest.setCustomerUtilityAccount(txn.getUtilityAccount());

		bankRequest.setReferredCommission(txn.getReferredCommission());
		bankRequest.setReferrerCommission(txn.getReferrerCommission());
		bankRequest.setCurrencyISOCode(EWalletConstants.USD);
		bankRequest.setAgentNumber(txn.getAgentNumber());
		bankRequest.setBouquetCode(txn.getBouquetCode());
		bankRequest.setBeneficiaryName(txn.getToCustomerName());
		
		String sourceEQAccount = txn.getSourceEQ3AccountNumber();
		String destEQAccount = txn.getDestinationEQ3AccountNumber();
		if(sourceEQAccount == null){
			sourceEQAccount = txn.getSourceAccountNumber();
		}
		if(destEQAccount == null){
			destEQAccount = txn.getDestinationAccountNumber();
		}
		
		//Switching of accounts to allow proper EQ posting Manu's posting bug 
		if(TransactionType.DEPOSIT.toString().equals(txn.getTransactionType().toString())){
			LOG.debug("TXN is a deposit so switch accounts");
			bankRequest.setTargetAccountNumber(sourceEQAccount);
			bankRequest.setSourceAccountNumber(destEQAccount);
		}else{
			bankRequest.setTargetAccountNumber(destEQAccount);
			bankRequest.setSourceAccountNumber(sourceEQAccount);
		}

		
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		LOG.debug("TXN TYPE : " + bankRequest.getTransactionType() + " SRC EQ3 ACC : " + bankRequest.getSourceAccountNumber() + " DEST EQ3 ACC : " + bankRequest.getTargetAccountNumber());
		BankAccount tariffAcc = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.TARIFFS_CONTROL, OwnerType.BANK, null);
		LOG.debug("Tariff Account found = "+tariffAcc);
		List<TransactionCharge> transactionCharges = this.getTransactionChargeByProcessTransactionId(txn.getId());

		if (transactionCharges != null) {
			for (TransactionCharge txnCharge : transactionCharges) {
				LOG.debug("Tarif acc for txn with ref : " + txn.getMessageId() + " and tariff ref : " + txnCharge.getId() + " is tariff acc num " + tariffAcc.getAccountNumber());
				LOG.debug("TXN CHARGE TYPE : " + txnCharge.getTransactionType() + ".");
				
				if (zw.co.esolutions.ewallet.enums.TransactionType.TARIFF.equals(txnCharge.getTransactionType())) {
					LOG.debug("Setting Bank Account info for TARIFF.");
					txnCharge.setFromEQ3Account(bankRequest.getSourceAccountNumber());
					LOG.debug("Set TARIFF SRC Bank Account info to " + txnCharge.getFromEQ3Account() + " : SRC " + txn.getSourceAccountNumber());
					BankAccount targetAcc = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.TARIFFS_CONTROL, OwnerType.BANK, null);
					LOG.debug("DONE FINDING TARGET ACCOUNT .... " + targetAcc);
					txnCharge.setToEQ3Account(targetAcc.getAccountNumber());
					LOG.debug("TARIFF ACCOUNTS : SRC : " + txnCharge.getToEQ3Account() + ": TARGET : " + targetAcc.getAccountNumber());
					// txnCharge.setAgentType(zw.co.esolutions.ewallet.enums.AgentType.BANK_BRANCH);
					
				} else if (zw.co.esolutions.ewallet.enums.TransactionType.COMMISSION.equals(txnCharge.getTransactionType())) {
					
					BankAccount agentCommSuspenseAcc = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.AGENT_COMMISSION_SUSPENSE, OwnerType.BANK, null);
					BankAccount agentCommSourceAcc = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.AGENT_COMMISSION_SOURCE, OwnerType.BANK, null);
					LOG.debug("AGENT_COMM_SUSPENSE_ACC :" + agentCommSuspenseAcc);
					LOG.debug("AGENT_COMM_SOURCE_ACC :" + agentCommSourceAcc);
					
					LOG.debug("Setting Bank Account info for COMMISSION.");
					txnCharge.setFromEQ3Account(agentCommSourceAcc.getAccountNumber());
					LOG.debug("Set COMMISSION SRC Bank Account info to " + txnCharge.getFromEQ3Account());
					LOG.debug("Setting TARGET ACCOUNT .... " + agentCommSuspenseAcc);
					txnCharge.setToEQ3Account(agentCommSuspenseAcc.getAccountNumber());
					LOG.debug("COMMISSION ACCOUNTS : SRC : " + txnCharge.getToEQ3Account());
					
				} else {
					
					LOG.debug("UNKOWN TRANSACTION CHARGE " + txnCharge.getTransactionType());
				
				}
				
				txnCharge.setStatus(TransactionStatus.BANK_REQUEST);
				this.updateTransactionCharge(txnCharge);
				bankRequest.addCommission(new Commission(txnCharge.getId(), txn.getMessageId(), txnCharge.getTariffAmount(), txn.getSourceAccountNumber(), txnCharge.getFromEQ3Account(), txnCharge.getToEQ3Account(), bankRequest.getNarrative(), txnCharge.getTransactionType(), txnCharge.getAgentNumber()));
			}
		}
		return bankRequest;
	}

	public ProcessTransaction getProcessTransactionByMessageId(String messageId) throws Exception {
		LOG.debug("The message Id is : " + messageId);
		ProcessTransaction txn = null;
		try {
			Query query = em.createNamedQuery("getProcessTransactionByMessageId");
			query.setParameter("messageId", messageId);
			txn = (ProcessTransaction) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return txn;
	}

	public ProcessTransaction updateProcessTxnWithBankRespInfo(ProcessTransaction txn, BankResponse bankResp) throws Exception {
		txn = this.getProcessTransactionByMessageId(txn.getMessageId());
		txn.setResponseCode(bankResp.getResponseCode().name());
		txn.setValueDate(bankResp.getValueDate());
		txn = this.updateProcessTransaction(txn);
		return txn;
	}


	@SuppressWarnings("unchecked")
	public List<ProcessTransaction> getXLatestProcessTransactionsByAccountNumber(int number, String accountNumber) throws Exception {
		List<ProcessTransaction> results = null;
		try {
			Query query = em.createNamedQuery("getXLatestProcessTransactionByAccountNumber");
			query.setParameter("accountNumber", accountNumber);
//			query.setParameter("nonHolderTxf", zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER);
//			query.setParameter("holderTransfer", zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TO_EWALLET_TRANSFER);
//			query.setParameter("deposit", zw.co.esolutions.ewallet.enums.TransactionType.DEPOSIT);
//			query.setParameter("withdrawal", zw.co.esolutions.ewallet.enums.TransactionType.WITHDRAWAL);
//			query.setParameter("txfToBankAccount", zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER);
//			query.setParameter("txfFromBankAccount", zw.co.esolutions.ewallet.enums.TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER);
//			query.setParameter("adjustment", zw.co.esolutions.ewallet.enums.TransactionType.ADJUSTMENT);
//			query.setParameter("topup", zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TOPUP);
//			query.setParameter("billpay", zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_BILLPAY);
			query.setParameter("statusCompleted", TransactionStatus.COMPLETED);
			query.setParameter("statusAwaitingCollection", TransactionStatus.AWAITING_COLLECTION);
			
			query.setParameter("balance", zw.co.esolutions.ewallet.enums.TransactionType.BALANCE);
			query.setParameter("mini", zw.co.esolutions.ewallet.enums.TransactionType.MINI_STATEMENT);

			results = (List<ProcessTransaction>) query.getResultList();

			if (results != null) {
				List<ProcessTransaction> firstXTxns = new ArrayList<ProcessTransaction>();
				for (int i = 0; i < results.size() && i < number; i++) {
					firstXTxns.add(results.get(i));
				}
				return firstXTxns;
			}
		} catch (NoResultException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}

	public String processMiniStatementRequest(ProcessTransaction txn) throws Exception {
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();

		MobileProfile profile = customerService.findMobileProfileById(txn.getSourceMobileId());
		BankAccount account = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(profile.getCustomer().getId(), BankAccountType.E_WALLET, OwnerType.CUSTOMER, profile.getMobileNumber());
		List<ProcessTransaction> last5txns = this.getXLatestProcessTransactionsByAccountNumber(5, account.getAccountNumber());

		String message = "eWallet Mini Stmt : [nl] ";

		if (last5txns != null) {
			LOG.debug("%%%%%%%%%   FOUND TXNS :" + last5txns.size());
			Transaction transaction = null;
			TransactionUniversalPojo pojo = new TransactionUniversalPojo();
			pojo.setAccountId(account.getId());

			int count = 1;

			for (ProcessTransaction p : last5txns) {
				LOG.debug("%%%%%%%%%   IN MAIN LOOP " + count++);

				LOG.debug("%%%%%%%%%   TXN :" + p.getTransactionType().name());
				transaction = null;
				pojo.setProcessTxnReference(p.getMessageId());
				pojo.setType(TransactionType.valueOf(p.getTransactionType().name()));
				List<Transaction> txns = bankService.getTransactionsByAllAttributes(pojo);

				if (txns == null || txns.isEmpty()) {
					LOG.debug("%%%%%%%%%   NO BKENTRY TXNS FOUND FOR THIS PROCESSTXN");
					continue;
				}

				for (Transaction t : txns) {
					LOG.debug("%%%%%%%%%   IN INNER LOOP");

					LOG.debug("%%%%%%%%%   FOUND BOOKENTRYTXN :" + t.getType().name() + " " + t.getAmount());

					LOG.debug("%%%%%%%%%   PROCESSTXN AMOUNT :" + p.getAmount());

					if (Math.abs(t.getAmount()) == p.getAmount()) {
						transaction = t;
						LOG.debug("%%%%%%%%%   AMOUNTS ARE EQUAL.. ASSIGNMENT DONE");

						break;
					} else {
						LOG.debug("%%%%%%%%%   AMOUNTS NOT EQUAL.. CONTINUE");

						continue;
					}
				}
				LOG.debug("%%%%%%%%%   BACK IN MAIN LOOP");

				if (transaction != null) {
					LOG.debug("%%%%%%%%%   APPENDING STMNT");

					message += DateUtil.convertDateToSimpleDayWithNoTime(p.getDateCreated());
					message += ": " + this.getTxnDescription(p.getTransactionType());
					message += " " + MoneyUtil.convertCentsToDollarsPatternNoCurrency(transaction.getAmount());
					message += " [nl] ";

					LOG.debug("####### FORMATTED VALUE : " + MoneyUtil.convertCentsToDollarsPatternNoCurrency(transaction.getAmount()));
				}
			}
		} else {
			message += "No recent transactions found. [nl] ";
			LOG.debug("%%%%%%%%%   NO TXNS FOUND");

		}

		message += "AVAIL BAL: " + MoneyUtil.convertCentsToDollarsPatternNoCurrency(account.getRunningBalance());

		LOG.debug("%%%%%%%%%   FINISHED");

		return message;

	}

	private String getTxnDescription(zw.co.esolutions.ewallet.enums.TransactionType transactionType) {
		if (zw.co.esolutions.ewallet.enums.TransactionType.DEPOSIT.equals(transactionType)) {
			return "CSH DEP";
		}
		if (zw.co.esolutions.ewallet.enums.TransactionType.WITHDRAWAL.equals(transactionType)) {
			return "CSH WD";
		}
		if (zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_BILLPAY.equals(transactionType)) {
			return "BILLPAY";
		}
		if (zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TOPUP.equals(transactionType)) {
			return "TOPUP";
		}
		if (zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER.equals(transactionType)) {
			return "TXF";
		}
		if (zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TO_EWALLET_TRANSFER.equals(transactionType)) {
			return "TXF";
		}
		if (zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER.equals(transactionType)) {
			return "TXF NHD";
		}
		if (zw.co.esolutions.ewallet.enums.TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER.equals(transactionType)) {
			return "TXF";
		}
		if (zw.co.esolutions.ewallet.enums.TransactionType.ADJUSTMENT.equals(transactionType)) {
			return "ADJ";
		}
		if (zw.co.esolutions.ewallet.enums.TransactionType.TARIFF.equals(transactionType)) {
			return "TXN CHRG";
		} else {
			return transactionType.name();
		}
	}

//	@Override
//	public void runEWalletsMonthlyAccountBalance(Date date) {
//		LOG.debug(">>>>>>>>>>>>>> Run EWallets Account Balances Started..... Date = " + DateUtil.convertToDateWithTime(date));
//		BookEntryServiceSOAPProxy bookEntryService = new BookEntryServiceSOAPProxy();
//		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
//		List<Bank> banks;
//		List<BankAccount> accounts = null;
//		AccountBalance bal;
//		BankAccount poolAccount = null;
//		long poolAmount = 0;
//		long count = 0;
//		long totalCount = 0;
//		Batch batch = null;
//		Batch b1 = null;
//		Batch b2 = null;
//		List<String> ids = new ArrayList<String>(); // List of bank ids
//		String bId = null; // Bank Id
//		try {
//			date = DateUtil.getEndOfDay(date);
//			banks = bankService.getBank();
//
//			for (Bank bank : banks) {
//				if (bank.getName().contains(EWalletConstants.SYSTEM_BANKS_DELIMITER)) { // For
//																						// ZB
//																						// Bank
//																						// and
//																						// ZBBS
//																						// only
//					LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> Populating bank IDs : " + bank.getName());
//					// Setting Ids for finding batch
//					ids.add(bank.getId());
//				}
//			}
//			LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> Banks found " + banks);
//			banx: for (Bank bank : banks) {
//				if (bank.getName().contains(EWalletConstants.SYSTEM_BANKS_DELIMITER)) { // For
//																						// ZB
//																						// Bank
//																						// and
//																						// ZBBS
//																						// only
//					LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> Account Balance Run for : " + bank.getName());
//
//					// Find Latest Incomplete Batch and Days between must not be
//					// greater than a day
//					batch = this.batchProcessor.getLatestBatchByEntityIdAndOwnerType(bank.getId(), zw.co.esolutions.ewallet.enums.OwnerType.BANK);
//					if (batch != null && batch.getId() != null) {
//						if (DateUtil.daysBetween(batch.getBatchDate(), date) >= 1) {
//							batch = null; // This is an old batch
//						}
//					}
//
//					if (batch == null || batch.getId() == null) { // Its a new
//																	// batch run
//						LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> A New Batch for : " + bank.getName());
//						// Start index = 0;
//						accounts = bankService.getBankAccountsByMinAttributes(bank.getId(), null, BankAccountType.E_WALLET, null, null, BankAccountStatus.ACTIVE, 0, Integer.parseInt(SystemConstants.configParams.getProperty(SystemConstants.MAX_BATCH_SIZE)));
//
//						// Populate new Batch and Create
//						batch = new Batch();
//						batch.setBatchDate(DateUtil.getEndOfDay(date));
//						batch.setComplete(false);
//						batch.setEntityId(bank.getId());
//						batch.setOwnerType(zw.co.esolutions.ewallet.enums.OwnerType.BANK);
//						batch.setMaxValue(Integer.parseInt(SystemConstants.configParams.getProperty(SystemConstants.MAX_BATCH_SIZE)));
//						batch = this.batchProcessor.createBatch(batch);
//						LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> " + bank.getName() + " Created batch : " + batch);
//
//					} else if (!batch.isComplete()) {
//						LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> Incomplete Batch : " + bank.getName());
//						// Pass start index from batch
//						accounts = bankService.getBankAccountsByMinAttributes(bank.getId(), null, BankAccountType.E_WALLET, null, null, BankAccountStatus.ACTIVE, batch.getStartIndex(), batch.getMaxValue());
//
//					} else if (batch.isComplete()) {
//						// Batch is complete
//						LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> " + bank.getName() + " Break, Batch Already Completed.");
//						// continue banx;
//
//						// Switch Banks Here If the other has completed its
//						// batches
//						bankIdLoop: for (String id : ids) {
//							if (!id.equalsIgnoreCase(batch.getEntityId())) {
//								bId = id;
//								break bankIdLoop;
//							}
//						}
//						b1 = batch;
//
//						bank = bankService.findBankById(bId);
//						LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> Bank Switched to : " + bank.getName());
//
//						// Find Latest Incomplete Batch and Days between must
//						// not be greater than a day
//						batch = this.batchProcessor.getLatestBatchByEntityIdAndOwnerType(bank.getId(), zw.co.esolutions.ewallet.enums.OwnerType.BANK);
//						if (batch != null && batch.getId() != null) {
//							if (DateUtil.daysBetween(batch.getBatchDate(), date) >= 1) {
//								batch = null; // This is an old batch
//							}
//						}
//
//						b2 = batch;
//						LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> Latest Batch for the other bank : " + b2);
//						if (b2 == null || b2.getId() == null) {
//							LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> Latest batch is null but it's an impossible case : " + bank.getName());
//							break banx;
//						} else if ((b1 != null && b2 != null) && (b1.isComplete() && b2.isComplete())) {
//							LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> All Banks' batches completed successfully : ");
//							break banx;
//						}
//
//						// Pass start index from batch
//						accounts = bankService.getBankAccountsByMinAttributes(bank.getId(), null, BankAccountType.E_WALLET, null, null, BankAccountStatus.ACTIVE, batch.getStartIndex(), batch.getMaxValue());
//
//					}
//					batch = this.batchProcessor.findBatchById(batch.getId());
//					count = batch.getCountValue();
//					LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> " + bank.getName() + " EWallet Accounts Found");
//					if (accounts != null && !accounts.isEmpty()) {
//						for (BankAccount acc : accounts) {
//							LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> EWallet Account " + acc.getAccountName() + " : " + acc.getAccountNumber() + " , Bank : " + acc.getBranch().getBank().getName());
//							bal = new AccountBalance();
//							bal.setAccountId(acc.getId());
//							LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> Done Setting account id = " + bal.getAccountId());
//							bal.setAmount(acc.getRunningBalance());
//							LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> Done Setting balance amount = " + bal.getAmount());
//							bal.setBalanceDate(DateUtil.convertToXMLGregorianCalendar(batch.getBatchDate()));
//							LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> Done setting balance date = " + bal.getBalanceDate());
//							bal = bookEntryService.createAccountBalance(bal);
//							LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> Account Balance created successfully for : " + acc.getAccountNumber());
//							poolAmount += bal.getAmount();
//							bal = null;
//							count += 1;
//							LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> Count = " + count);
//
//						}
//
//					}
//				}
//				totalCount += count;
//
//				if (accounts == null || accounts.isEmpty()) { // No Accounts
//																// Found,
//																// Complete
//																// Batch
//					LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> Complete Batch");
//					batch.setComplete(true);
//					batch.setCountValue(count);
//					batch = this.batchProcessor.updateBatch(batch);
//					poolAmount = batch.getTotalPoolAmount();
//
//				} else { // Process Incomplete, Just update Batch
//					LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> Update Batch Only. ID = " + batch.getId());
//					batch.setStartIndex(batch.getStartIndex() + Integer.parseInt(SystemConstants.configParams.getProperty(SystemConstants.MAX_BATCH_SIZE)));
//					batch.setTotalPoolAmount(batch.getTotalPoolAmount() + poolAmount);
//					batch.setCountValue(count);
//					batch = this.batchProcessor.updateBatch(batch);
//					poolAmount = batch.getTotalPoolAmount();
//
//				}
//
//				LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> Done creating Account Balances for " + bank.getName() + " , Total Pool = " + poolAmount);
//				accounts = null;
//				poolAccount = bankService.getBankAccountsByAccountHolderIdAndOwnerTypeAndBankAccountType(bank.getId(), OwnerType.BANK, BankAccountType.POOL_CONTROL);
//				if (poolAccount != null && poolAccount.getId() != null && batch.isComplete()) {
//					LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> Bank Account for " + bank.getName() + " POOL = " + poolAccount.getAccountNumber() + " Status = " + poolAccount.getStatus());
//					bal = new AccountBalance();
//					bal.setAccountId(poolAccount.getId());
//					LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> Done Setting account id = " + bal.getAccountId());
//					bal.setAmount(poolAmount);
//					LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> Done Setting balance amount = " + bal.getAmount());
//					bal.setBalanceDate(DateUtil.convertToXMLGregorianCalendar(batch.getBatchDate()));
//					LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> Done setting balance date = " + bal.getBalanceDate());
//					bal = bookEntryService.createAccountBalance(bal);
//					LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> POOL Account Balance created successfully for : " + poolAccount.getAccountNumber());
//
//				}
//				count = 0;
//				poolAmount = 0;
//				poolAccount = null;
//				bal = null;
//
//			}
//			int index = 0;
//			for (String bankId : ids) {
//				LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> id = " + bankId);
//				if (index == 0) {
//					b1 = this.batchProcessor.getLatestBatchByEntityIdAndOwnerType(bankId, zw.co.esolutions.ewallet.enums.OwnerType.BANK);
//				} else if (index == 1) {
//					b2 = this.batchProcessor.getLatestBatchByEntityIdAndOwnerType(bankId, zw.co.esolutions.ewallet.enums.OwnerType.BANK);
//				}
//				index++;
//			}
//
//			LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> Batch 1 = " + b1 + ", Batch 2 = " + b2);
//			// Schedule Timer
//			if ((b1 != null && b1.isComplete()) && (b2 != null && b2.isComplete())) { // Reschedule
//																						// Next
//																						// Run
//																						// Timer
//				// Original Nxt Run Date
//				LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> Reschedule for next month ");
//				batch = this.batchProcessor.findBatchById(EWalletConstants.EJB_TIMER_ID);
//				date = batch.getBatchDate();
//				this.monthlyProcessor.scheduleNextMonthBatchTimer(date);
//
//			} else if ((b1 != null && !b1.isComplete()) || (b2 != null && !b2.isComplete())) {
//				LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> Reschedule for the next seconds ");
//				date = new Date(System.currentTimeMillis());
//				date = DateUtil.add(date, Calendar.SECOND, EWalletConstants.TIMER_INCREMENT_SECONDS);
//				this.monthlyProcessor.scheduleBatchTimer(date);
//
//			}
//
//			LOG.debug("In  Run EWallets Account Balances >>>>>>> Finished Running Account Balances. Total Ewallets = " + (b1.getCountValue() + b2.getCountValue()) + " And Total Pool = 2");
//		} catch (Exception e) {
//			LOG.debug("In  Run EWallets Account Balances >>>>>>>>>>>>>>>>>>>>> Exception thrown. ");
//			e.printStackTrace();
//		}
//
//	}

	@Override
	public ProcessTransaction getLatestProcessTransactionBySourceMobileId(String sourceMobileId) throws Exception {
		ProcessTransaction txn = null;
		try {
			List<ProcessTransaction> results = this.getProcessTransactionBySourceMobileId(sourceMobileId);
			if (results != null) {
				return results.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return txn;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProcessTransaction> getProcessTransactionBySourceMobileId(String sourceMobileId) throws Exception {
		List<ProcessTransaction> results = null;
		try {
			Query query = em.createNamedQuery("getProcessTransactionBySourceMobileId");
			query.setParameter("sourceMobileId", sourceMobileId);
			results = (List<ProcessTransaction>) query.getResultList();
		} catch (NoResultException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;

	}

	@Override
	public MobileProfile getMobileProfile(String mobileNumber) {
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		MobileProfile mobileProfile = customerService.getMobileProfileByMobileNumber(mobileNumber);
		return mobileProfile;
	}

	public String processReferralRequest(RequestInfo info) throws Exception {
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		ReferralServiceSOAPProxy referralService = new ReferralServiceSOAPProxy();

		MobileProfile sourceProfile = customerService.getMobileProfileByMobileNumber(info.getSourceMobile());
		MobileProfile targetMobile = customerService.getMobileProfileByMobileNumber(info.getTargetMobile());

		if (targetMobile != null) {
			return ResponseCode.E717.name(); // Mobile already registered.
		}

		Referral referral = null;

		boolean customerHasTransacted = this.customerHasTransactedAtLeastOnce(sourceProfile.getId());

		LOG.debug("########### boolean : " + customerHasTransacted);

		if (customerHasTransacted) {
			// check max referrals
			List<Referral> referrals = referralService.getReferralByReferredMobile(info.getTargetMobile());
			if (referrals != null) {
				ReferralConfig currentConfig = referralService.getActiveReferralConfig();
				if (currentConfig != null && currentConfig.getId() != null) {
					if (currentConfig.getMaxReferrals() == referrals.size()) {
						return ResponseCode.E719.name(); // Maximum number of
						// referrals for
						// this mobile
						// reached.
					}
				}
			}
			referral = new Referral();
			referral.setReferrerMobileId(sourceProfile.getId());
			referral.setReferredMobile(info.getTargetMobile());
			referral.setStatus(ReferralStatus.NEW);

			referral = referralService.createReferral(referral); // this //
			// method
			// promotes
			// referral
			// state
			// also
		} else {
			return ResponseCode.E718.name(); // You have not transacted at least
			// once
		}

		return "CODE:" + referral.getCode();
	}

	public boolean customerHasTransactedAtLeastOnce(String mobileId) {
		LOG.debug("############# in method custHasTxcted");
		List<ProcessTransaction> txns = this.getProcessTransactionBySourceMobileIdAndTransactionTypeAndStatus(mobileId, zw.co.esolutions.ewallet.enums.TransactionType.DEPOSIT, TransactionStatus.COMPLETED);
		LOG.debug("############# txns " + txns);
		if (txns == null || txns.isEmpty()) {
			txns = this.getProcessTransactionBySourceMobileIdAndTransactionTypeAndStatus(mobileId, zw.co.esolutions.ewallet.enums.TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER, TransactionStatus.COMPLETED);
		}
		if (txns == null || txns.isEmpty()) {
			txns = this.getProcessTransactionBySourceMobileIdAndTransactionTypeAndStatus(mobileId, zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TO_EWALLET_TRANSFER, TransactionStatus.COMPLETED);
		}
		LOG.debug("############# txns2 " + txns);
		if (txns == null || txns.isEmpty()) {
			return false; // the customer has not transacted
		}
		return true; // the customer has transacted.
	}

	@SuppressWarnings("unchecked")
	public List<ProcessTransaction> getProcessTransactionBySourceMobileIdAndTransactionTypeAndStatus(String sourceMobileId, zw.co.esolutions.ewallet.enums.TransactionType transactionType, TransactionStatus status) {
		LOG.debug("###############DEBUG>>>>>>>>>>>>> in method");
		LOG.debug("$$$$$$$$$ mobile :" + sourceMobileId);
		LOG.debug("$$$$$$$$$ type :" + transactionType);
		LOG.debug("$$$$$$$$$ status :" + status);
		List<ProcessTransaction> results = null;
		try {
			Query query = em.createNamedQuery("getProcessTransactionBySourceMobileIdAndTransactionTypeAndStatus");
			query.setParameter("sourceMobileId", sourceMobileId);
			query.setParameter("transactionType", transactionType);
			query.setParameter("status", status);
			results = (List<ProcessTransaction>) query.getResultList();
		} catch (NoResultException e) {
			LOG.debug("############DEBUG>>>>>>>>>>>>> NoResultException");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		LOG.debug("#############DEBUG>>>>>>>>>>>>> " + results);
		return results;

	}

	public BankRequest checkAndProcessPendingReferral(RequestInfo info) throws Exception {
		BankRequest bankRequest;
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		ReferralServiceSOAPProxy referralService = new ReferralServiceSOAPProxy();

		MobileProfile targetProfile = customerService.getMobileProfileByMobileNumber(info.getSourceMobile());
		if (targetProfile.getReferralCode() != null && !targetProfile.isReferralProcessed()) {
			if (this.customerHasTransactedAtLeastOnce(targetProfile.getId())) {
				Referral referral = referralService.getReferralByReferredMobileAndCode(info.getSourceMobile(), Integer.parseInt(targetProfile.getReferralCode()));
				MobileProfile sourceProfile = customerService.findMobileProfileById(referral.getReferrerMobileId());
				if (referral.getStatus().equals(ReferralStatus.REGISTERED)) {
					if (DateUtil.daysBetween(new Date(), DateUtil.convertToDate(referral.getDateCreated())) <= 10) {
						ReferralConfig currentConfig = referralService.getActiveReferralConfig();
						if (currentConfig != null && currentConfig.getId() != null) {
							bankRequest = new BankRequest();
							bankRequest.setSourceMobileNumber(sourceProfile.getMobileNumber());
							bankRequest.setTargetMobileNumber(targetProfile.getMobileNumber());
							bankRequest.setSourceBankCode(info.getBankCode());
							bankRequest.setTransactionType(zw.co.esolutions.ewallet.enums.TransactionType.REFERRAL);
							bankRequest.setReference(referral.getId());

							long referrerCommission = currentConfig.getAmount() * currentConfig.getReferrerRatio() / (currentConfig.getReferrerRatio() + currentConfig.getReferredRatio());
							bankRequest.setReferrerCommission(referrerCommission);
							bankRequest.setReferredCommission(currentConfig.getAmount() - referrerCommission);
							return bankRequest;
						} else {
							return null; // process another time
						}

					} else {
						referralService.promoteReferralState(referral, ReferralStatus.EXPIRED);
					}
					targetProfile.setReferralProcessed(true);
					customerService.updateMobileProfile(targetProfile, EWalletConstants.SYSTEM);
				}
			}
		}
		return null;
	}

	public String promoteReferralState(String referralId, ReferralStatus status) throws Exception {
		ReferralServiceSOAPProxy referralService = new ReferralServiceSOAPProxy();
		referralService.promoteReferralState(referralService.findReferralById(referralId), status);
		return ResponseCode.E000.name();
	}

	public long getCustomerRunningBalance(String mobileProfileId, BankAccountType accountType) {
		long amount = 0;
		MobileProfile mobileProfile;
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		try {
			mobileProfile = customerService.findMobileProfileById(mobileProfileId);
			amount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(mobileProfile.getCustomer().getId(), accountType, OwnerType.CUSTOMER, mobileProfile.getMobileNumber()).getRunningBalance();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return amount;
	}

	/*public long getRunningBalance(String mobileNumber, BankAccountType accountType) {
		long amount = 0;
		MobileProfile mobileProfile;
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>getRunningBalance >>>>>>>>>>>>>>>>>>>>>>>>>..summy test....mobilenumber.."+mobileNumber);
		//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>getRunningBalance >>>>>>>>>>>>>>>>>>>>>>>>>..summy test....bankaccount type.."+accountType.toString());
		try {
			mobileProfile = customerService.getMobileProfileByMobileNumber(mobileNumber);
			BankAccount bankAccount =bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(mobileProfile.getCustomer().getId(), accountType, OwnerType.CUSTOMER, mobileProfile.getMobileNumber());
			AccountBalance balance = bankService.getClosingBalance(bankAccount.getId(), DateUtil.convertToXMLGregorianCalendar(new Date()));
			amount=balance.getAmount();
			
			//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>getRunningBalance >>>>>>>>>>>>>>>>>>>>>>>>>..summy test......"+amount);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return amount;
	}*/
	
	
	public long getRunningBalance(String mobileNumber, BankAccountType accountType) {
		long amount = 0;
		MobileProfile mobileProfile;
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		try {
			mobileProfile = customerService.getMobileProfileByMobileNumber(mobileNumber);
			amount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(mobileProfile.getCustomer().getId(), accountType, OwnerType.CUSTOMER, mobileProfile.getMobileNumber()).getRunningBalance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return amount;
	}
	
	
	public String getUserName(String profileId) {
		String name = null;
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		try {
			name = profileService.findProfileById(profileId).getUserName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}

	@Override
	public ProcessTransaction validateBankAccountToNonHolderTransferReq(ProcessTransaction txn) throws Exception {
		LOG.debug("In validate " + txn.getTransactionType() + "eWallet to Non Holder request");
		txn = this.addApplicableTariffToProcessTxn(txn);
		LOG.debug("Added the " + txn.getTransactionType() + " tariff");
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		BankAccount bankAccount = bankService.getBankAccountByAccountNumberAndOwnerType(txn.getSourceAccountNumber(), OwnerType.CUSTOMER);
		txn = this.checkTxnLimits(txn, bankAccount);
		LOG.debug("Check tcn limits returned " + txn.getResponseCode());
		return txn;
	}

	public BankAccount getCustomerBankAccountByDeatails(String bankId, String mobileNumber) throws Exception {
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		MobileProfile profile = customerService.getMobileProfileByBankAndMobileNumber(bankId, mobileNumber);
		return bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(profile.getCustomer().getId(), BankAccountType.E_WALLET, OwnerType.CUSTOMER, profile.getMobileNumber());
	}

	@Override
	public ProcessTransaction validateBankAccountToEWalletTransferReq(ProcessTransaction txn) throws Exception {
		LOG.debug("In validate " + txn.getTransferType() + " request");
		txn = this.addApplicableTariffToProcessTxn(txn);
		LOG.debug("Added the " + txn.getTransferType() + " tariff");
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		BankAccount bankAccount = bankService.getBankAccountByAccountNumberAndOwnerType(txn.getSourceAccountNumber(), OwnerType.CUSTOMER);
		txn = this.checkTxnLimits(txn, bankAccount);
		LOG.debug("Check tcn limits returned " + txn.getResponseCode());
		if (!ResponseCode.E000.name().equalsIgnoreCase(txn.getResponseCode())) {
			return txn;
		}
		BankAccount destBankAccount = bankService.getBankAccountByAccountNumberAndOwnerType(txn.getDestinationAccountNumber(), OwnerType.CUSTOMER);
		txn = this.checkDestinationBalanceLimits(txn, destBankAccount);
		LOG.debug("Done check Funds availability and got " + txn.getResponseCode());
		return txn;
	}

	@Override
	public ProcessTransaction validateBankAccountToBankAccountTransferReq(ProcessTransaction txn) throws Exception {
		LOG.debug("In validate " + txn.getTransactionType() + " eWallet to Non Holder request");
		txn = this.addApplicableTariffToProcessTxn(txn);
		LOG.debug("Added the " + txn.getTransactionType() + " tariff");
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		BankAccount bankAccount = bankService.getBankAccountByAccountNumberAndOwnerType(txn.getSourceAccountNumber(), OwnerType.CUSTOMER);
		txn = this.checkTxnLimits(txn, bankAccount);
		LOG.debug("Check tcn limits returned " + txn.getResponseCode());
		return txn;
	}

	@Override
	public ProcessTransaction validateEwalletAccountToBankAccountTransferReq(ProcessTransaction txn) throws Exception {
		LOG.debug("In validate eWallet to Non Holder request");
		txn = this.addApplicableTariffToProcessTxn(txn);
		LOG.debug("Added the eWallet to Non Holder tariff");
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		BankAccount bankAccount = bankService.getUniqueBankAccountByAccountNumber(txn.getSourceAccountNumber());
		txn = this.checkTxnLimits(txn, bankAccount);
		LOG.debug("Check tcn limits returned " + txn.getResponseCode());
		if (!ResponseCode.E000.name().equalsIgnoreCase(txn.getResponseCode())) {
			return txn;
		}
		txn = this.checkFundsAvailability(txn, bankAccount);
		LOG.debug("Done check Funds availability and got " + txn.getResponseCode());
		LOG.debug("Now returning from validate method.");
		return txn;
	}

	@Override
	public ProcessTransaction validateEWalletToEWalletTransfer(ProcessTransaction txn) throws Exception {
		LOG.debug("In validate " + txn.getTransactionType());
		txn = this.addApplicableTariffToProcessTxn(txn);
		LOG.debug("Added the tariff to the " + txn.getTransactionType());
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		BankAccount bankAccount = bankService.getBankAccountByAccountNumberAndOwnerType(txn.getSourceAccountNumber(), OwnerType.CUSTOMER);
		LOG.debug("Going to check against limits");
		txn = this.checkTxnLimits(txn, bankAccount);
		LOG.debug("Check tcn limits returned " + txn.getResponseCode());
		if (!ResponseCode.E000.name().equalsIgnoreCase(txn.getResponseCode())) {
			return txn;
		}
		txn = this.checkFundsAvailability(txn, bankAccount);
		LOG.debug("Done check Funds availability and got " + txn.getResponseCode());
		if (!ResponseCode.E000.name().equalsIgnoreCase(txn.getResponseCode())) {
			return txn;
		}
		BankAccount destBankAccount = bankService.getBankAccountByAccountNumberAndOwnerType(txn.getDestinationAccountNumber(), OwnerType.CUSTOMER);
		txn = this.checkDestinationBalanceLimits(txn, destBankAccount);
		LOG.debug("Done check destination account balance constrains " + txn.getResponseCode());
		return txn;
	}

//	public ResponseCode postBankAccountToEWalletTransferTxns(ProcessTransaction txn) throws Exception {
//
//		if (txn != null) {
//			if (!(txn.getResponseCode() == null || ResponseCode.E000.equals(ResponseCode.valueOf(txn.getResponseCode())))) {
//				return ResponseCode.valueOf(txn.getResponseCode());
//			}
//		}
//		try {
//			// Debit the BANK TO MOBILE CONTROL ACCOUNT.
//			this.doBookEntryPosting(BankAccountType.BANK_TO_MOBILE_CONTROL, TransactionActionType.DEBIT, zw.co.esolutions.ewallet.enums.TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER, txn.getAmount(), null, txn, TransferOrder.FROM, txn.getStatus().toString(), TransactionCategory.MAIN);
//			LOG.debug("Done debiting Bank to Mobile control Account");
//			// Credit Destn Customer Account
//			this.doBookEntryPosting(BankAccountType.E_WALLET, TransactionActionType.CREDIT, zw.co.esolutions.ewallet.enums.TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER, txn.getAmount(), txn.getTargetMobileId(), txn, TransferOrder.TO, null, TransactionCategory.MAIN);
//			LOG.debug("Done Crediting DESTN Customer EWALLET.");
//
//			// throw new Exception("Bank Account To Ewallet Account");
//
//		} catch (Exception e) {
//
//			return this.rollbackEWalletAndEQ3Postings(txn);
//
//		}
//		return ResponseCode.E000;
//	}
	
	@SuppressWarnings("unchecked")
	public List<ProcessTransaction> getProcessTransactionsByApplicableParameters(Date fromDate, Date toDate, TransactionStatus status, zw.co.esolutions.ewallet.enums.TransactionType txnType, TxnFamily txnFamily, String tellerId, String bankId, String branchId) {
		fromDate = DateUtil.getBeginningOfDay(fromDate);
		toDate = DateUtil.getEndOfDay(toDate);
		List<ProcessTransaction> results = null;
		String qStr = "SELECT p ";
		String orderBy = "ORDER BY p.dateCreated DESC";
		try {

			Query query = this.getProcessTransactionsQuery(qStr, orderBy, fromDate, toDate, status, txnType, txnFamily, tellerId, bankId, branchId);
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


	public long getTotalAmountByApplicableParameters(Date fromDate, Date toDate, TransactionStatus status, zw.co.esolutions.ewallet.enums.TransactionType txnType, TxnFamily txnFamily, String tellerId, String bankId, String branchId) {
		long results = 0;
		;
		String qStr = "SELECT SUM(p.amount) ";
		String orderBy = "";
		try {
			Query query = this.getProcessTransactionsQuery(qStr, orderBy, fromDate, toDate, status, txnType, txnFamily, tellerId, bankId, branchId);
			Object tmp = query.getSingleResult();
			LOG.debug(">>>>>>>>>>>>>>>. Tmp = " + tmp);
			results = (Long) tmp;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}

		return results;
	}
	
	private Query getProcessTransactionsQuery(String selectClause, String orderBy, Date fromDate, Date toDate, TransactionStatus status, zw.co.esolutions.ewallet.enums.TransactionType txnType, TxnFamily txnFamily, String tellerId, String bankId, String branchId) throws Exception {
		fromDate = DateUtil.getBeginningOfDay(fromDate);
		toDate = DateUtil.getEndOfDay(toDate);
		Query results = null;
		String qStr = selectClause + "FROM ProcessTransaction p WHERE p.dateCreated >= :fromDate AND p.dateCreated <= :toDate ";

		try {
			LOG.debug(">>>>>>>>>>>>>> tellerId = " + tellerId + " bankId = " + bankId + " branchId = " + branchId + " txnType = " + txnType + " fromDate = " + fromDate + " toDate = " + toDate + " txnFamily = " + txnFamily + " status = " + status);

			if (tellerId != null) {
				qStr = qStr + "AND p.profileId = :profileId ";
			}
			if (bankId != null) {
				qStr = qStr + "AND p.fromBankId = :fromBankId ";
			}
			if (branchId != null) {
				qStr = qStr + "AND p.transactionLocationId = :branchId ";
			}
			if (txnFamily != null) {
				if (TxnFamily.WITHDRAWALS.equals(txnFamily)) {
					qStr = qStr + "AND (p.transactionType = :WITHDRAWALp " + "OR p.transactionType = :WITHDRAWAL_NONHOLDERp) ";
				}

				if (TxnFamily.TRANSFERS.equals(txnFamily)) {
					qStr = qStr + "AND (p.transactionType = :EWALLET_TO_BANKACCOUNT_TRANSFERp " + "OR p.transactionType = :EWALLET_TO_EWALLET_TRANSFERp " + "OR p.transactionType = :EWALLET_TO_NON_HOLDER_TRANSFERp " + "OR p.transactionType = :BANKACCOUNT_TO_BANKACCOUNT_TRANSFERp " + "OR p.transactionType = :BANKACCOUNT_TO_EWALLET_TRANSFERp " + "OR p.transactionType = :BANKACCOUNT_TO_NONHOLDER_TRANSFERp) ";

				}
			}
			if (status != null) {
				qStr = qStr + "AND p.status = :status ";
			}
			if (txnType != null) {
				qStr = qStr + "AND p.transactionType = :transactionType ";
			}
			qStr = qStr + orderBy;
			Query query = em.createQuery(qStr);
			query.setParameter("fromDate", fromDate);
			query.setParameter("toDate", toDate);

			if (txnFamily != null) {
				if (TxnFamily.WITHDRAWALS.equals(txnFamily)) {
					query.setParameter("WITHDRAWALp", zw.co.esolutions.ewallet.enums.TransactionType.WITHDRAWAL);
					query.setParameter("WITHDRAWAL_NONHOLDERp", zw.co.esolutions.ewallet.enums.TransactionType.WITHDRAWAL_NONHOLDER);

				}

				if (TxnFamily.TRANSFERS.equals(txnFamily)) {
					query.setParameter("EWALLET_TO_BANKACCOUNT_TRANSFERp", zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER);
					query.setParameter("EWALLET_TO_EWALLET_TRANSFERp", zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TO_EWALLET_TRANSFER);
					query.setParameter("EWALLET_TO_NON_HOLDER_TRANSFERp", zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER);
					query.setParameter("BANKACCOUNT_TO_BANKACCOUNT_TRANSFERp", zw.co.esolutions.ewallet.enums.TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER);
					query.setParameter("BANKACCOUNT_TO_EWALLET_TRANSFERp", zw.co.esolutions.ewallet.enums.TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER);
					query.setParameter("BANKACCOUNT_TO_NONHOLDER_TRANSFERp", zw.co.esolutions.ewallet.enums.TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER);

				}
			}

			if (tellerId != null) {
				query.setParameter("profileId", tellerId);
			}
			if (status != null) {
				query.setParameter("status", status);
			}
			if (txnType != null) {
				query.setParameter("transactionType", txnType);
			}
			if (bankId != null) {
				query.setParameter("fromBankId", bankId);
			}
			if (branchId != null) {
				query.setParameter("branchId", branchId);
			}
			results = query;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return results;
	}

	private ChargePostingInfo populateEWalletChargePostingRequest(ProcessTransaction txn, TransactionCharge transactionCharge, BankAccount source, BankAccount destinationAccount) {
		ChargePostingInfo request = new ChargePostingInfo();
		request.setAmount(transactionCharge.getTariffAmount());
		request.setOriginalTxnRef(txn.getId());
		request.setSourceAccountNumber(source.getAccountNumber());
		request.setSrcAccountId(source.getId());
		request.setTargetAccountId(destinationAccount.getId());
		request.setTargetAccountNumber(destinationAccount.getAccountNumber());
		if (zw.co.esolutions.ewallet.enums.TransactionType.COMMISSION.equals(transactionCharge.getTransactionType())) {
			LOG.debug("Populating a COMMISSION..");
			request.setTxnCategory(TransactionCategory.COMMISSION);
		} else {
			LOG.debug("Populating a CHARGE..");
			request.setTxnCategory(TransactionCategory.CHARGE);
		}
		request.setTxnRef(transactionCharge.getId());
		request.setSrcNarrative("Debit Account : "+request.getSourceAccountNumber());
		request.setTargetNarrative("Credit Account : "+request.getTargetAccountNumber());
		//Setting Merchant Narratives
		if(txn.getUtilityAccount() != null) {
			request.setSrcNarrative("Debit. Utitility Account : "+txn.getUtilityAccount()+", Mobile : "+txn.getSourceMobile());
			request.setTargetNarrative("Credit. Utitility Account : "+txn.getUtilityAccount()+", Mobile : "+txn.getSourceMobile());
		}
		// Book Entry Transactions
		TransactionType bookEntryTxnType = TransactionType.valueOf(transactionCharge.getTransactionType().toString());
		request.setTxnType(bookEntryTxnType);
		return request;
	}

	private TransactionPostingInfo populateEWalletMainPostingRequest(ProcessTransaction txn, BankAccount sourceAccount, BankAccount destinationAccount) throws Exception{
		TransactionPostingInfo txnInfo = new TransactionPostingInfo();
		txnInfo.setAmount(txn.getAmount());
		txnInfo.setOriginalTxnRef(txn.getOldMessageId() == null ? txn.getId() : txn.getOldMessageId());
		txnInfo.setSourceAccountNumber(sourceAccount.getAccountNumber());
		txnInfo.setSrcAccountId(sourceAccount.getId());
		txnInfo.setTargetAccountId(destinationAccount.getId());
		txnInfo.setTargetAccountNumber(destinationAccount.getAccountNumber());
		txnInfo.setTxnCategory(TransactionCategory.MAIN);
		txnInfo.setTxnRef(txn.getId());
		txnInfo.setSrcNarrative("Debit Account : "+txnInfo.getSourceAccountNumber());
		txnInfo.setTargetNarrative("Credit Account : "+txnInfo.getTargetAccountNumber());
		//Setting Merchant Narratives
		if(txn.getUtilityAccount() != null) {
			txnInfo.setSrcNarrative("Debit. Utitility Account : "+txn.getUtilityAccount()+", Mobile : "+txn.getSourceMobile());
			txnInfo.setTargetNarrative("Creit. Utitility Account : "+txn.getUtilityAccount()+", Mobile : "+txn.getSourceMobile());
		}
		// Book Entry Transactions
		TransactionType bankTxnType = TransactionType.valueOf(txn.getTransactionType().toString());
		txnInfo.setTxnType(bankTxnType);

		return txnInfo;
	}
	
	@Override
	public ResponseCode postAdjustmentTxnsToEwallet(ManualPojo manual, ProcessTransaction txn) throws Exception {
		LOG.debug("1. postAdjustmenttnxs toewallet :::::::::: entry");
		
		txn = this.getProcessTransactionByMessageId(txn.getMessageId());

		if (txn != null) {
			if (!(txn.getResponseCode() == null || ResponseCode.E000.equals(ResponseCode.valueOf(txn.getResponseCode())))) {
				return ResponseCode.valueOf(txn.getResponseCode());
			}
		}
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		BankAccount source = bankService.getUniqueBankAccountByAccountNumber(txn.getSourceAccountNumber());
		BankAccount dest = bankService.getUniqueBankAccountByAccountNumber(txn.getDestinationAccountNumber());
		BankAccount tariffAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.TARIFFS_CONTROL, OwnerType.BANK, null);

		LOG.debug("2. postAdjustmenttnxs toewallet :::::::::: accounts retrieved");
		if(source == null || source.getId() == null){
			throw new Exception("BANK TO NON MOBILE CONTROL Account could not be found.");
		} 
		if(dest == null || dest.getId() == null){
			throw new Exception("PAYOUT CONTROL Account could not be found with account number.");
		}
		if(tariffAccount == null || tariffAccount.getId() == null){
			throw new Exception("TARIFF CONTROL Account could not be found");
		}
		TransactionPostingInfo request = this.populateEWalletMainPostingRequest(txn, source, dest);
		LOG.debug("3. postAdjustmenttnxs toewallet :::::::::: request populated");
		List<TransactionPostingInfo> txnInfos = new ArrayList<TransactionPostingInfo>();
		txnInfos.add(request);
		List<ChargePostingInfo> chargeInfos = new ArrayList<ChargePostingInfo>();
		
		List<TransactionCharge> txnCharges = this.getTransactionChargeByProcessTransactionId(txn.getId());
		LOG.debug("4. postAdjustmenttnxs toewallet :::::::::: getting charges");
		if(txnCharges == null || txnCharges.isEmpty()){
			LOG.debug("No charges to post to EWALLET for txn with reference " + txn.getId());
		}else{
			LOG.debug("Found " + txnCharges.size() + " charges for txn with ref " + txn.getId());
			ChargePostingInfo txnChargeReq;
			for (TransactionCharge transactionCharge : txnCharges) {
				txnChargeReq = this.populateEWalletChargePostingRequest(txn, transactionCharge, source, tariffAccount);
				chargeInfos.add(txnChargeReq);
				transactionCharge.setFromEwalletAccount(txnChargeReq.getSourceAccountNumber());
				transactionCharge.setToEwalletAccount(txnChargeReq.getTargetAccountNumber());
				transactionCharge = this.updateTransactionCharge(transactionCharge);
			}
			LOG.debug("DONE Posting charges for the txn " + txn.getId());
		}
		try {
			LOG.debug("5. postAdjustmenttnxs toewallet :::::::::: posting book entries");
			GenerateKey.throwsDirectEwalletPostingsException();
			EWalletPostingResponse response = bankService.postEWalletEntries(txnInfos, chargeInfos);
			LOG.debug("%%%%%%%%      Returned successfully from postEwalletEntries>>>>>>>>narrative>>>>"+response.getNarrative());
			LOG.debug("%%%%%%%%      Returned successfully from postEwalletEntries>>>>>>>>"+response.getResponseCode().name());
			LOG.debug("%%%%%%%%      Returned successfully from postEwalletEntries>>>>>>>>"+response.getResponseCode().toString());
			LOG.debug("%%%%%%%%      Begin promote txn status to COMPLETED");
			txn = this.promoteTxnState(txn, TransactionStatus.COMPLETED, "Transaction completed successfully. ");
			LOG.debug("6 . postAdjustmenttnxs toewallet :::::::::: updating adjstment txn");
			//txn=this.updateAdjustmentTxnState(txn, TransactionStatus.COMPLETED, "Transaction completed successfully. ");
			LOG.debug("%%%%%%%%      COMPLETED:       POSTING TXN Adjustment was successful "+txn);
			LOG.debug("7. postAdjustmenttnxs toewallet :::::::::: exiting after this");
						return ResponseCode.E000;
		} catch (Exception e1) {
			LOG.debug("8 exception occurs here need to roll back");
			LOG.debug("Exception io postAdjustment >>>>>>>>>>>>>>>>"+e1.getMessage());
			return ResponseCode.E836;
		}
		
	}
	
	@Override
	public ResponseCode postDayEndCashTendered(ProcessTransaction txn) throws Exception {

		if (txn != null) {
			if (!(txn.getResponseCode() == null || ResponseCode.E000.equals(ResponseCode.valueOf(txn.getResponseCode())))) {
				return ResponseCode.valueOf(txn.getResponseCode());
			}
		}
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		BankAccount source = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getTransactionLocationId(), BankAccountType.BRANCH_CASH_ACCOUNT, OwnerType.BANK_BRANCH, null);
		BankAccount dest = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getTransactionLocationId(), BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT, OwnerType.BANK_BRANCH, null);
		
		if(source == null || source.getId() == null){
			throw new Exception("Source account cannot be null.");
		} 
		if(dest == null || dest.getId() == null){
			throw new Exception("Destination account cannot be null.");
		}
		
		TransactionPostingInfo request = this.populateEWalletMainPostingRequest(txn, source, dest);
		List<TransactionPostingInfo> txnInfos = new ArrayList<TransactionPostingInfo>();
		txnInfos.add(request);
		
		try {
			EWalletPostingResponse response = bankService.postEWalletEntries(txnInfos, null);
			LOG.debug("POSTING TELLER FLOAT was successful");
			return ResponseCode.E000;
		} catch (Exception e1) {
			return ResponseCode.E836;
		}
	
//	
//		processUtil.doBookEntryPosting(BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT, TransactionActionType.CREDIT, txnType, trxn.getAmount(), null, trxn, null, null, TransactionCategory.MAIN);
//		processUtil.doBookEntryPosting(BankAccountType.BRANCH_CASH_ACCOUNT, TransactionActionType.DEBIT, txnType, trxn.getAmount(), null, trxn, null, null, TransactionCategory.MAIN);
		
	}

	@Override
	public ResponseCode postDayEndUnderPost(ProcessTransaction txn) throws Exception {
		if (txn != null) {
			if (!(txn.getResponseCode() == null || ResponseCode.E000.equals(ResponseCode.valueOf(txn.getResponseCode())))) {
				return ResponseCode.valueOf(txn.getResponseCode());
			}
		}
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		BankAccount source = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getTransactionLocationId(), BankAccountType.EWALLET_BALANCING_SUSPENSE_ACCOUNT, OwnerType.BANK_BRANCH, null);
		BankAccount dest = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getTransactionLocationId(), BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT, OwnerType.BANK_BRANCH, null);
		
		if(source == null || source.getId() == null){
			throw new Exception("Source account cannot be null.");
		} 
		if(dest == null || dest.getId() == null){
			throw new Exception("Destination account cannot be null.");
		}
		
		TransactionPostingInfo request = this.populateEWalletMainPostingRequest(txn, source, dest);
		List<TransactionPostingInfo> txnInfos = new ArrayList<TransactionPostingInfo>();
		txnInfos.add(request);
		
		try {
			EWalletPostingResponse response = bankService.postEWalletEntries(txnInfos, null);
			LOG.debug("POSTING TELLER FLOAT was successful");
			return ResponseCode.E000;
		} catch (Exception e1) {
			return ResponseCode.E836;
		}
//		processUtil.doBookEntryPosting(BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT, TransactionActionType.CREDIT, txnType, trxn.getAmount(), null, trxn, null, null, TransactionCategory.MAIN);
//		processUtil.doBookEntryPosting(BankAccountType.EWALLET_BALANCING_SUSPENSE_ACCOUNT, TransactionActionType.DEBIT, txnType, trxn.getAmount(), null, trxn, null, null, TransactionCategory.MAIN);
	}

	@Override
	public ResponseCode postDayEndOverPost(ProcessTransaction txn) throws Exception {
		if (txn != null) {
			if (!(txn.getResponseCode() == null || ResponseCode.E000.equals(ResponseCode.valueOf(txn.getResponseCode())))) {
				return ResponseCode.valueOf(txn.getResponseCode());
			}
		}
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		BankAccount source = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getTransactionLocationId(), BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT, OwnerType.BANK_BRANCH, null);
		BankAccount dest = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getTransactionLocationId(), BankAccountType.EWALLET_BALANCING_SUSPENSE_ACCOUNT, OwnerType.BANK_BRANCH, null);
		
		if(source == null || source.getId() == null){
			throw new Exception("Source account cannot be null.");
		} 
		if(dest == null || dest.getId() == null){
			throw new Exception("Destination account cannot be null.");
		}
		
		TransactionPostingInfo request = this.populateEWalletMainPostingRequest(txn, source, dest);
		List<TransactionPostingInfo> txnInfos = new ArrayList<TransactionPostingInfo>();
		txnInfos.add(request);
		
		try {
			EWalletPostingResponse response = bankService.postEWalletEntries(txnInfos, null);
			LOG.debug("POSTING TELLER FLOAT was successful");
			return ResponseCode.E000;
		} catch (Exception e1) {
			return ResponseCode.E836;
		}
//		processUtil.doBookEntryPosting(BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT, TransactionActionType.DEBIT, txnType, trxn.getAmount(), null, trxn, null, null, TransactionCategory.MAIN);
//		processUtil.doBookEntryPosting(BankAccountType.EWALLET_BALANCING_SUSPENSE_ACCOUNT, TransactionActionType.CREDIT, txnType, trxn.getAmount(), null, trxn, null, null, TransactionCategory.MAIN);

	}

	
	@Override
	public ResponseCode postBankAccountToBankAccountTransfer(ProcessTransaction txn) throws Exception {
		// To be handled by EQUATION
		return ResponseCode.E000;
	}

	@Override
	public ResponseCode postTellerFloatToEwallet(ProcessTransaction txn, BankAccount source, BankAccount dest) throws Exception {
	
		try {
			
			//GenerateKey.throwsDayEndEWalletPostingsException();
			if (txn != null) {
			if (!(txn.getResponseCode() == null || ResponseCode.E000.equals(ResponseCode.valueOf(txn.getResponseCode())))) {
				return ResponseCode.valueOf(txn.getResponseCode());
			}
		}
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();

		if(source == null || source.getId() == null){
			throw new Exception("Source account cannot be null.");
		} 
		if(dest == null || dest.getId() == null){
			throw new Exception("Destination account cannot be null.");
		}
		
		TransactionPostingInfo request = this.populateEWalletMainPostingRequest(txn, source, dest);
		List<TransactionPostingInfo> txnInfos = new ArrayList<TransactionPostingInfo>();
		txnInfos.add(request);
		
			LOG.debug("Coount of items in list to post.." + txnInfos.size());
			EWalletPostingResponse response = bankService.postEWalletEntries(txnInfos, null);
			LOG.debug("POSTING TELLER FLOAT was successful");
			return ResponseCode.E000;
		} catch (Exception e1) {
			LOG.debug("rolling beck");
			this.rollbackEQ3Postings(txn);
			return ResponseCode.E836;
		}
	
	}
	
	@Override
	public ResponseCode postBankAccountToNonHolderTransfer(ProcessTransaction txn) throws Exception{
		try {
			if (txn != null) {
				if (!(txn.getResponseCode() == null || ResponseCode.E000.equals(ResponseCode.valueOf(txn.getResponseCode())))) {
					return ResponseCode.valueOf(txn.getResponseCode());
				}
			}
			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
			BankAccount source = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.BANK_TO_NON_MOBILE_CONTROL, OwnerType.BANK, null);
			BankAccount dest = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.PAYOUT_CONTROL, OwnerType.BANK, null);
			BankAccount tariffAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.TARIFFS_CONTROL, OwnerType.BANK, null);
	
	
			if(source == null || source.getId() == null){
				throw new Exception("BANK TO NON MOBILE CONTROL Account could not be found.");
			} 
			if(dest == null || dest.getId() == null){
				throw new Exception("PAYOUT CONTROL Account could not be found with account number.");
			}
			if(tariffAccount == null || tariffAccount.getId() == null){
				throw new Exception("TARIFF CONTROL Account could not be found");
			}
			TransactionPostingInfo request = this.populateEWalletMainPostingRequest(txn, source, dest);
			List<TransactionPostingInfo> txnInfos = new ArrayList<TransactionPostingInfo>();
			txnInfos.add(request);
			List<ChargePostingInfo> chargeInfos = new ArrayList<ChargePostingInfo>();
			
			List<TransactionCharge> txnCharges = this.getTransactionChargeByProcessTransactionId(txn.getId());
			if(txnCharges == null || txnCharges.isEmpty()){
				LOG.debug("No charges to post to EWALLET for txn with reference " + txn.getId());
			}else{
				LOG.debug("Found " + txnCharges.size() + " charges for txn with ref " + txn.getId());
				ChargePostingInfo txnChargeReq;
				for (TransactionCharge transactionCharge : txnCharges) {
					txnChargeReq = this.populateEWalletChargePostingRequest(txn, transactionCharge, source, tariffAccount);
					chargeInfos.add(txnChargeReq);
					transactionCharge.setFromEwalletAccount(txnChargeReq.getSourceAccountNumber());
					transactionCharge.setToEwalletAccount(txnChargeReq.getTargetAccountNumber());
					transactionCharge = this.updateTransactionCharge(transactionCharge);
				}
				LOG.debug("DONE Posting charges for the txn " + txn.getId());
			}
			
			//**********   BUG
			GenerateKey.throwsEWalletPostingsException();
			//DO POSTS
			EWalletPostingResponse response = bankService.postEWalletEntries(txnInfos, chargeInfos);
			LOG.debug("POSTING BA TO NHL was successful");
			return ResponseCode.E000;
			
		} catch (Exception e) {
			
			this.rollbackEWalletAndEQ3Postings(txn);
			return ResponseCode.E836;
		}
	
	}
	
	public ResponseCode postBankAccountToEWalletTransfer(ProcessTransaction txn) throws Exception {
		try {
			if (txn != null) {
				if (!(txn.getResponseCode() == null || ResponseCode.E000.equals(ResponseCode.valueOf(txn.getResponseCode())))) {
					return ResponseCode.valueOf(txn.getResponseCode());
				}
			}
			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
			BankAccount source = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.BANK_TO_MOBILE_CONTROL, OwnerType.BANK, null);
			BankAccount dest = bankService.getUniqueBankAccountByAccountNumber(txn.getDestinationAccountNumber());
			BankAccount tariffAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.TARIFFS_CONTROL, OwnerType.BANK, null);
			
			if(source == null || source.getId() == null){
				throw new Exception("BANK TO MOBILE CONTROL Account could not be found.");
			} 
			if(dest == null || dest.getId() == null){
				throw new Exception("EWALLET Account could not be found with account number " + txn.getDestinationAccountNumber());
			}
			
			if(tariffAccount == null || tariffAccount.getId() == null){
				throw new Exception("TARIFF CONTROL Account could not be found");
			}
			
			TransactionPostingInfo request = this.populateEWalletMainPostingRequest(txn, source, dest);
			List<TransactionPostingInfo> txnInfos = new ArrayList<TransactionPostingInfo>();
			txnInfos.add(request);
			List<ChargePostingInfo> chargeInfos = new ArrayList<ChargePostingInfo>();
			
			List<TransactionCharge> txnCharges = this.getTransactionChargeByProcessTransactionId(txn.getId());
			if(txnCharges == null || txnCharges.isEmpty()){
				LOG.debug("No charges to post to EWALLET for txn with reference " + txn.getId());
			}else{
				LOG.debug("Found " + txnCharges.size() + " charges for txn with ref " + txn.getId());
				ChargePostingInfo txnChargeReq;
				for (TransactionCharge transactionCharge : txnCharges) {
					txnChargeReq = this.populateEWalletChargePostingRequest(txn, transactionCharge, source, tariffAccount);
					chargeInfos.add(txnChargeReq);
					transactionCharge.setFromEwalletAccount(txnChargeReq.getSourceAccountNumber());
					transactionCharge.setToEwalletAccount(txnChargeReq.getTargetAccountNumber());
					transactionCharge = this.updateTransactionCharge(transactionCharge);
				}
				LOG.debug("DONE Posting charges for the txn " + txn.getId());
			}
			//**********   BUG
			GenerateKey.throwsEWalletPostingsException();
			
			//DO POSTINGS
			EWalletPostingResponse response = bankService.postEWalletEntries(txnInfos, chargeInfos);			
			
			LOG.debug("POSTING BA TO NHL was successful");
			
			return ResponseCode.E000;
			
		} catch (Exception e) {
			
			this.rollbackEWalletAndEQ3Postings(txn);
			return ResponseCode.E836;
		}
	
	}
	
	@Override
	public ResponseCode postEWalletWithdrawal(ProcessTransaction txn) throws Exception {
		try { LOG.debug("Processing Book Entry for EWT WDL.");
		if (txn != null) {
			if (!(txn.getResponseCode() == null || ResponseCode.E000.equals(ResponseCode.valueOf(txn.getResponseCode())))) {
				return ResponseCode.valueOf(txn.getResponseCode());
			}
		}
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		BankAccount source = bankService.getUniqueBankAccountByAccountNumber(txn.getSourceAccountNumber());
		BankAccount dest = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getTransactionLocationId(), BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT, OwnerType.BANK_BRANCH, null);
		BankAccount tariffAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.TARIFFS_CONTROL, OwnerType.BANK, null);
		
		if(source == null || source.getId() == null){
			throw new Exception("EWALLET Account could not be found  with account number " + txn.getSourceAccountNumber());
		} 
		if(dest == null || dest.getId() == null){
			throw new Exception("EWALLET BRANCH CASH Account could not be found.");
		}
		
		if(tariffAccount == null || tariffAccount.getId() == null){
			throw new Exception("TARIFF CONTROL Account could not be found");
		}
		
		TransactionPostingInfo request = this.populateEWalletMainPostingRequest(txn, source, dest);
		List<TransactionPostingInfo> txnInfos = new ArrayList<TransactionPostingInfo>();
		txnInfos.add(request);
		List<ChargePostingInfo> chargeInfos = new ArrayList<ChargePostingInfo>();
		
		List<TransactionCharge> txnCharges = this.getTransactionChargeByProcessTransactionId(txn.getId());
		if(txnCharges == null || txnCharges.isEmpty()){
			LOG.debug("No charges to post to EWALLET for txn with reference " + txn.getId());
		}else{
			LOG.debug("Found " + txnCharges.size() + " charges for txn with ref " + txn.getId());
			ChargePostingInfo txnChargeReq;
			for (TransactionCharge transactionCharge : txnCharges) {
				txnChargeReq = this.populateEWalletChargePostingRequest(txn, transactionCharge, source, tariffAccount);
				chargeInfos.add(txnChargeReq);
				transactionCharge.setFromEwalletAccount(txnChargeReq.getSourceAccountNumber());
				transactionCharge.setToEwalletAccount(txnChargeReq.getTargetAccountNumber());
				transactionCharge = this.updateTransactionCharge(transactionCharge);
			}
			LOG.debug("DONE Posting charges for the txn " + txn.getId());
		}
			GenerateKey.throwsEWalletPostingsException();
			EWalletPostingResponse response = bankService.postEWalletEntries(txnInfos, chargeInfos);
			LOG.debug("POSTING EWT WDL was successful");
			return ResponseCode.E000;
		} catch (Exception e1) {
			
			this.rollbackEWalletAndEQ3Postings(txn);
			return ResponseCode.E836;
		}
	}

	public ResponseCode postEWalletToEWalletTransfer(ProcessTransaction txn) throws Exception {
		try {
			LOG.debug("Processing Book Entry for EWT TO EWT Transfer.");
			if (txn != null) {
				if (!(txn.getResponseCode() == null || ResponseCode.E000.equals(ResponseCode.valueOf(txn.getResponseCode())))) {
					return ResponseCode.valueOf(txn.getResponseCode());
				}
			}
			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
			BankAccount source = bankService.getUniqueBankAccountByAccountNumberAndBankId(txn.getSourceAccountNumber(), txn.getFromBankId());
			BankAccount dest = bankService.getUniqueBankAccountByAccountNumberAndBankId(txn.getDestinationAccountNumber(), txn.getToBankId());
			BankAccount tariffAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.TARIFFS_CONTROL, OwnerType.BANK, null);
			
			if(source == null || source.getId() == null){
				throw new Exception("EWALLET Account could not be found  with account number " + txn.getSourceAccountNumber());
			} 
			if(dest == null || dest.getId() == null){
				throw new Exception("DEST EWALLET Account could not be found  with account number " + txn.getDestinationAccountNumber());
			}
			
			if(tariffAccount == null || tariffAccount.getId() == null){
				throw new Exception("TARIFF CONTROL Account could not be found");
			}
			
			TransactionPostingInfo request = this.populateEWalletMainPostingRequest(txn, source, dest);
			List<TransactionPostingInfo> txnInfos = new ArrayList<TransactionPostingInfo>();
			txnInfos.add(request);
			List<ChargePostingInfo> chargeInfos = new ArrayList<ChargePostingInfo>();
			
			List<TransactionCharge> txnCharges = this.getTransactionChargeByProcessTransactionId(txn.getId());
			if(txnCharges == null || txnCharges.isEmpty()){
				LOG.debug("No charges to post to EWALLET for txn with reference " + txn.getId());
			}else{
				LOG.debug("Found " + txnCharges.size() + " charges for txn with ref " + txn.getId());
				ChargePostingInfo txnChargeReq;
				for (TransactionCharge transactionCharge : txnCharges) {
					txnChargeReq = this.populateEWalletChargePostingRequest(txn, transactionCharge, source, tariffAccount);
					chargeInfos.add(txnChargeReq);
					transactionCharge.setFromEwalletAccount(txnChargeReq.getSourceAccountNumber());
					transactionCharge.setToEwalletAccount(txnChargeReq.getTargetAccountNumber());
					transactionCharge = this.updateTransactionCharge(transactionCharge);
				}
				LOG.debug("DONE Posting charges for the txn " + txn.getId());
			}
			//DO POSTS
			GenerateKey.throwsEWalletPostingsException();
			EWalletPostingResponse response = bankService.postEWalletEntries(txnInfos, chargeInfos);
			LOG.debug("POSTING EWT TO EWT Transfer was successful");
			return ResponseCode.E000;
			
		} catch (Exception e) {
			
			this.rollbackEWalletAndEQ3Postings(txn);
			return ResponseCode.E836;

		}
	}

	public ResponseCode postNonHolderWithdrawal(ProcessTransaction txn) throws Exception {
		try { LOG.debug("Processing Book Entry for NON-HOLDER Withdrawal.");
		if (txn != null) {
			if (!(txn.getResponseCode() == null || ResponseCode.E000.equals(ResponseCode.valueOf(txn.getResponseCode())))) {
				return ResponseCode.valueOf(txn.getResponseCode());
			}
		}
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		BankAccount source = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.PAYOUT_CONTROL, OwnerType.BANK, null);
		BankAccount dest = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getTransactionLocationId(), BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT, OwnerType.BANK_BRANCH, null);
		BankAccount tariffAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.TARIFFS_CONTROL, OwnerType.BANK, null);
		
		if(source == null || source.getId() == null){
			throw new Exception("PAYOUT CONTROL Account could not be found.");
		} 
		if(dest == null || dest.getId() == null){
			throw new Exception("EWALLET BRANCH CASH Account could not be found with account number.");
		}
		
		if(tariffAccount == null || tariffAccount.getId() == null){
			throw new Exception("TARIFF CONTROL Account could not be found");
		}
		
		TransactionPostingInfo request = this.populateEWalletMainPostingRequest(txn, source, dest);
		List<TransactionPostingInfo> txnInfos = new ArrayList<TransactionPostingInfo>();
		txnInfos.add(request);
		List<ChargePostingInfo> chargeInfos = new ArrayList<ChargePostingInfo>();
		
		List<TransactionCharge> txnCharges = this.getTransactionChargeByProcessTransactionId(txn.getId());
		if(txnCharges == null || txnCharges.isEmpty()){
			LOG.debug("No charges to post to EWALLET for txn with reference " + txn.getId());
		}else{
			LOG.debug("Found " + txnCharges.size() + " charges for txn with ref " + txn.getId());
			ChargePostingInfo txnChargeReq;
			for (TransactionCharge transactionCharge : txnCharges) {
				txnChargeReq = this.populateEWalletChargePostingRequest(txn, transactionCharge, source, tariffAccount);
				chargeInfos.add(txnChargeReq);
				transactionCharge.setFromEwalletAccount(txnChargeReq.getSourceAccountNumber());
				transactionCharge.setToEwalletAccount(txnChargeReq.getTargetAccountNumber());
				transactionCharge = this.updateTransactionCharge(transactionCharge);
			}
			LOG.debug("DONE Posting charges for the txn " + txn.getId());
		}
			GenerateKey.throwsEWalletPostingsException();
			EWalletPostingResponse response = bankService.postEWalletEntries(txnInfos, chargeInfos);
			LOG.debug("POSTING NHL WDL was successful");
			return ResponseCode.E000;
		} catch (Exception e1) {
			
			this.rollbackEWalletAndEQ3Postings(txn);
			return ResponseCode.E836;
		}
	
	}

	
	public ResponseCode postEWalletAccountToBankAccountTransfer(ProcessTransaction txn) throws Exception {

	 try { if (txn != null) {
			if (!(txn.getResponseCode() == null || ResponseCode.E000.equals(ResponseCode.valueOf(txn.getResponseCode())))) {
				return ResponseCode.valueOf(txn.getResponseCode());
			}
		}
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		BankAccount source = bankService.getUniqueBankAccountByAccountNumber(txn.getSourceAccountNumber());
		
		BankAccount dest = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.MOBILE_TO_BANK_CONTROL, OwnerType.BANK, null);
		BankAccount tariffAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.TARIFFS_CONTROL, OwnerType.BANK, null);
		
		if(source == null || source.getId() == null){
			throw new Exception("EWALLET Account could not be found with account number " + txn.getDestinationAccountNumber());
		}
		
		if(dest == null || dest.getId() == null){
			throw new Exception("MOBILE TO BANK CONTROL Account could not be found.");
		} 
		
		if(tariffAccount == null || tariffAccount.getId() == null){
			throw new Exception("TARIFF CONTROL Account could not be found");
		}
		
		TransactionPostingInfo request = this.populateEWalletMainPostingRequest(txn, source, dest);
		List<TransactionPostingInfo> txnInfos = new ArrayList<TransactionPostingInfo>();
		txnInfos.add(request);
		List<ChargePostingInfo> chargeInfos = new ArrayList<ChargePostingInfo>();
		
		List<TransactionCharge> txnCharges = this.getTransactionChargeByProcessTransactionId(txn.getId());
		if(txnCharges == null || txnCharges.isEmpty()){
			LOG.debug("No charges to post to EWALLET for txn with reference " + txn.getId());
		}else{
			LOG.debug("Found " + txnCharges.size() + " charges for txn with ref " + txn.getId());
			ChargePostingInfo txnChargeReq;
			for (TransactionCharge transactionCharge : txnCharges) {
				txnChargeReq = this.populateEWalletChargePostingRequest(txn, transactionCharge, source, tariffAccount);
				chargeInfos.add(txnChargeReq);
				transactionCharge.setFromEwalletAccount(txnChargeReq.getSourceAccountNumber());
				transactionCharge.setToEwalletAccount(txnChargeReq.getTargetAccountNumber());
				transactionCharge = this.updateTransactionCharge(transactionCharge);
			}
			LOG.debug("DONE Posting charges for the txn " + txn.getId());
		}
		GenerateKey.throwsEWalletPostingsException();
		EWalletPostingResponse response = bankService.postEWalletEntries(txnInfos, chargeInfos);
			LOG.debug("POSTING EWT TO BA was successful");
			return ResponseCode.E000;
		} catch (Exception e1) {
			
			this.rollbackEWalletAndEQ3Postings(txn);
			return ResponseCode.E836;
		}
	}
	
	public ResponseCode postEWalletToNonHolderTransfer(ProcessTransaction txn) throws Exception {
		try {
			if (txn != null) {
				if (!(txn.getResponseCode() == null || ResponseCode.E000.equals(ResponseCode.valueOf(txn.getResponseCode())))) {
					return ResponseCode.valueOf(txn.getResponseCode());
				}
			}
			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
			BankAccount source = bankService.getUniqueBankAccountByAccountNumber(txn.getSourceAccountNumber());
			BankAccount dest = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.PAYOUT_CONTROL, OwnerType.BANK, null);
			BankAccount tariffAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.TARIFFS_CONTROL, OwnerType.BANK, null);
			
			if(source == null || source.getId() == null){
				throw new Exception("EWALLET Account could not be found with account number " + txn.getSourceAccountNumber());
			}
			
			if(dest == null || dest.getId() == null){
				throw new Exception("PAYOUT CONTROL Account could not be found.");
			} 
			
			if(tariffAccount == null || tariffAccount.getId() == null){
				throw new Exception("TARIFF CONTROL Account could not be found");
			}
			
			TransactionPostingInfo request = this.populateEWalletMainPostingRequest(txn, source, dest);
			List<TransactionPostingInfo> txnInfos = new ArrayList<TransactionPostingInfo>();
			txnInfos.add(request);
			List<ChargePostingInfo> chargeInfos = new ArrayList<ChargePostingInfo>();
			
			List<TransactionCharge> txnCharges = this.getTransactionChargeByProcessTransactionId(txn.getId());
			if(txnCharges == null || txnCharges.isEmpty()){
				LOG.debug("No charges to post to EWALLET for txn with reference " + txn.getId());
			}else{
				LOG.debug("Found " + txnCharges.size() + " charges for txn with ref " + txn.getId());
				ChargePostingInfo txnChargeReq;
				for (TransactionCharge transactionCharge : txnCharges) {
					txnChargeReq = this.populateEWalletChargePostingRequest(txn, transactionCharge, source, tariffAccount);
					chargeInfos.add(txnChargeReq);
					transactionCharge.setFromEwalletAccount(txnChargeReq.getSourceAccountNumber());
					transactionCharge.setToEwalletAccount(txnChargeReq.getTargetAccountNumber());
					transactionCharge = this.updateTransactionCharge(transactionCharge);
				}
				LOG.debug("DONE Posting charges for the txn " + txn.getId());
			}
			//DO POSTS
			GenerateKey.throwsEWalletPostingsException();
			EWalletPostingResponse response = bankService.postEWalletEntries(txnInfos, chargeInfos);
			LOG.debug("POSTING EWT TO NHL was successful");
			return ResponseCode.E000;

		} catch (Exception e) {
			
			this.rollbackEWalletAndEQ3Postings(txn);
			return ResponseCode.E836;
		}
	}

	@Override
	public ResponseCode postEWalletTopupRequest(ProcessTransaction txn) throws Exception {
		System.out.println("2 Getting top up response code from ewallet>>>>>>>>>>>>.");
		try { if (txn != null) {
			if (!(txn.getResponseCode() == null || ResponseCode.E000.equals(ResponseCode.valueOf(txn.getResponseCode())))) {
				return ResponseCode.valueOf(txn.getResponseCode());
			}
		}
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		BankAccount source = bankService.getUniqueBankAccountByAccountNumber(txn.getSourceAccountNumber());
		BankAccount dest = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.TOPUP_SUSPENSE_ACCOUNT, OwnerType.BANK, null);
		BankAccount tariffAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.TARIFFS_CONTROL, OwnerType.BANK, null);
		
		if(source == null || source.getId() == null){
			throw new Exception("EWALLET Account could not be found with account number " + txn.getDestinationAccountNumber());
		}
		
		if(dest == null || dest.getId() == null){
			throw new Exception("TOPUP SUSPENSE Account could not be found.");
		} 
		
		if(tariffAccount == null || tariffAccount.getId() == null){
			throw new Exception("TARIFF CONTROL Account could not be found");
		}
		
		TransactionPostingInfo request = this.populateEWalletMainPostingRequest(txn, source, dest);
		List<TransactionPostingInfo> txnInfos = new ArrayList<TransactionPostingInfo>();
		txnInfos.add(request);
		List<ChargePostingInfo> chargeInfos = new ArrayList<ChargePostingInfo>();
		
		List<TransactionCharge> txnCharges = this.getTransactionChargeByProcessTransactionId(txn.getId());
		if(txnCharges == null || txnCharges.isEmpty()){
			LOG.debug("No charges to post to EWALLET for txn with reference " + txn.getId());
		}else{
			LOG.debug("Found " + txnCharges.size() + " charges for txn with ref " + txn.getId());
			ChargePostingInfo txnChargeReq;
			for (TransactionCharge transactionCharge : txnCharges) {
				txnChargeReq = this.populateEWalletChargePostingRequest(txn, transactionCharge, source, tariffAccount);
				chargeInfos.add(txnChargeReq);
				transactionCharge.setFromEwalletAccount(txnChargeReq.getSourceAccountNumber());
				transactionCharge.setToEwalletAccount(txnChargeReq.getTargetAccountNumber());
				transactionCharge = this.updateTransactionCharge(transactionCharge);
			}
			LOG.debug("DONE Ading charge Postings for the txn " + txn.getId());
		}
			GenerateKey.throwsEWalletPostingsException();
			EWalletPostingResponse response = bankService.postEWalletEntries(txnInfos, chargeInfos);
			LOG.debug("POSTING EWT TOPUP was successful");
			return ResponseCode.E000;
		} catch (Exception e1) {
			
			this.rollbackEWalletAndEQ3Postings(txn);
			return ResponseCode.E836;
		}
	}

	public ResponseCode postEWalletDepositTxn(ProcessTransaction txn) throws Exception {
		try { if (txn != null) {
			if (!(txn.getResponseCode() == null || ResponseCode.E000.equals(ResponseCode.valueOf(txn.getResponseCode())))) {
				return ResponseCode.valueOf(txn.getResponseCode());
			}
		}
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		BankAccount source = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getTransactionLocationId(), BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT, OwnerType.BANK_BRANCH, null);
		BankAccount dest = bankService.getUniqueBankAccountByAccountNumber(txn.getDestinationAccountNumber());
		BankAccount tariffAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.TARIFFS_CONTROL, OwnerType.BANK, null);
		
		if(source == null || source.getId() == null){
			throw new Exception("EWALLET BRANCH CASH Account could not be found.");
		}
		
		if(dest == null || dest.getId() == null){
			throw new Exception("EWALLET ACCOUNT could not be found with account number " + txn.getDestinationAccountNumber());
		} 
		
		if(tariffAccount == null || tariffAccount.getId() == null){
			throw new Exception("TARIFF CONTROL Account could not be found");
		}
		
		TransactionPostingInfo request = this.populateEWalletMainPostingRequest(txn, source, dest);
		List<TransactionPostingInfo> txnInfos = new ArrayList<TransactionPostingInfo>();
		txnInfos.add(request);
		List<ChargePostingInfo> chargeInfos = new ArrayList<ChargePostingInfo>();
		
		List<TransactionCharge> txnCharges = this.getTransactionChargeByProcessTransactionId(txn.getId());
		if(txnCharges == null || txnCharges.isEmpty()){
			LOG.debug("No charges to post to EWALLET for txn with reference " + txn.getId());
		}else{
			LOG.debug("Found " + txnCharges.size() + " charges for txn with ref " + txn.getId());
			ChargePostingInfo txnChargeReq;
			for (TransactionCharge transactionCharge : txnCharges) {
				txnChargeReq = this.populateEWalletChargePostingRequest(txn, transactionCharge, source, tariffAccount);
				chargeInfos.add(txnChargeReq);
				transactionCharge.setFromEwalletAccount(txnChargeReq.getSourceAccountNumber());
				transactionCharge.setToEwalletAccount(txnChargeReq.getTargetAccountNumber());
				transactionCharge = this.updateTransactionCharge(transactionCharge);
			}
			LOG.debug("DONE Ading charge Postings for the txn " + txn.getId());
		}
		
			GenerateKey.throwsEWalletPostingsException();
			EWalletPostingResponse response = bankService.postEWalletEntries(txnInfos, chargeInfos);
			LOG.debug("POSTING EWT DEPOSIT was successful");
			return ResponseCode.E000;
		} catch (Exception e1) {
			
			this.rollbackEWalletAndEQ3Postings(txn);
			return ResponseCode.E836;
		}
	}
	
	@Override
	public ResponseCode postEwalletBillPayRequest(ProcessTransaction txn) throws Exception {
	 try { if (txn != null) {
			if (!(txn.getResponseCode() == null || ResponseCode.E000.equals(ResponseCode.valueOf(txn.getResponseCode())))) {
				return ResponseCode.valueOf(txn.getResponseCode());
			}
		}
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		BankAccount source = bankService.getUniqueBankAccountByAccountNumber(txn.getSourceAccountNumber());
		BankAccount dest = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getBankMerchantId(), BankAccountType.MERCHANT_SUSPENSE, OwnerType.MERCHANT, null);
		BankAccount tariffAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.TARIFFS_CONTROL, OwnerType.BANK, null);
		
		if(dest == null || dest.getId() == null){
			throw new Exception("MERCHANT SUSPENSE Account could not be found.");
		}
		
		if(source == null || source.getId() == null){
			throw new Exception("EWALLET ACCOUNT could not be found with account number " + txn.getDestinationAccountNumber());
		} 
		
		if(tariffAccount == null || tariffAccount.getId() == null){
			throw new Exception("TARIFF CONTROL Account could not be found");
		}
		
		TransactionPostingInfo request = this.populateEWalletMainPostingRequest(txn, source, dest);
		List<TransactionPostingInfo> txnInfos = new ArrayList<TransactionPostingInfo>();
		txnInfos.add(request);
		List<ChargePostingInfo> chargeInfos = new ArrayList<ChargePostingInfo>();
		
		List<TransactionCharge> txnCharges = this.getTransactionChargeByProcessTransactionId(txn.getId());
		if(txnCharges == null || txnCharges.isEmpty()){
			LOG.debug("No charges to post to EWALLET for txn with reference " + txn.getId());
		}else{
			LOG.debug("Found " + txnCharges.size() + " charges for txn with ref " + txn.getId());
			ChargePostingInfo txnChargeReq;
			for (TransactionCharge transactionCharge : txnCharges) {
				txnChargeReq = this.populateEWalletChargePostingRequest(txn, transactionCharge, source, tariffAccount);
				chargeInfos.add(txnChargeReq);
				transactionCharge.setFromEwalletAccount(txnChargeReq.getSourceAccountNumber());
				transactionCharge.setToEwalletAccount(txnChargeReq.getTargetAccountNumber());
				transactionCharge = this.updateTransactionCharge(transactionCharge);
			}
			LOG.debug("DONE Ading charge Postings for the txn " + txn.getId());
		}
			GenerateKey.throwsEWalletPostingsException();
			EWalletPostingResponse response = bankService.postEWalletEntries(txnInfos, chargeInfos);
			LOG.debug("POSTING EWT BILLPAY was successful");
			return ResponseCode.E000;
		} catch (Exception e1) {
			
			this.rollbackEWalletAndEQ3Postings(txn);
			return ResponseCode.E836;
		}

	}
	
	@Override
	public ProcessTransaction postEWalletReversal(ProcessTransaction txn) throws Exception{
		LOG.debug("The reversal of a TRANSACTION in EWALLET is still a stub");
		return txn;
	}

	public RequestInfo populateRequestInfo(ProcessTransaction txn) {
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setMessageId(txn.getMessageId());
		requestInfo.setAmount(txn.getAmount());
		requestInfo.setBankCode(txn.getBankReference());
		requestInfo.setCustomerUtilityAccount(txn.getUtilityAccount());
		requestInfo.setDestinationAccountNumber(txn.getDestinationAccountNumber());
		requestInfo.setUtilityName(txn.getUtilityName());
		requestInfo.setLocationType(txn.getTransactionLocationType());
		requestInfo.setMno(NumberUtil.getMNO(txn.getSourceMobile()));
		requestInfo.setCustomerUtilityAccount(txn.getUtilityAccount());
		requestInfo.setProfileId(txn.getProfileId());
		requestInfo.setSecretCode(txn.getSecretCode());
		requestInfo.setSourceAccountNumber(txn.getSourceAccountNumber());
		requestInfo.setSourceBankId(txn.getFromBankId());
		requestInfo.setSourceMobile(txn.getSourceMobile());
		requestInfo.setSourceMobileProfileId(txn.getSourceMobileId());
		requestInfo.setStatus(txn.getStatus());
		requestInfo.setTargetBankId(txn.getToBankId());
		requestInfo.setTargetMobile(txn.getTargetMobile());
		requestInfo.setTransactionType(txn.getTransactionType());
		requestInfo.setAgentNumber(txn.getAgentNumber());
		requestInfo.setBouquetCode(txn.getBouquetCode());
		requestInfo.setBouquetName(txn.getBouquetName());
		requestInfo.setNumberOfMonths(txn.getNumberOfMonths());
		requestInfo.setCommission(txn.getCommission());

		return requestInfo;
	}

//	@Override
//	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
//	public void doDayEndBookEntry(DayEnd dayEnd, long amount, zw.co.esolutions.ewallet.enums.BankAccountType bankAccountType, zw.co.esolutions.ewallet.enums.TransactionType txnType, TransactionActionType actionType, String reference, String message) throws Exception {
//		LOG.debug("day end book entry begins  ");
//		BankAccount bankAccount = null;
//		Transaction transaction = new Transaction();
//		zw.co.esolutions.ewallet.bankservices.service.OwnerType ownerType1 = null;
//		String ownerId = null;
//		BookEntryServiceSOAPProxy bookEntryService = new BookEntryServiceSOAPProxy();
//		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
//		// Book Entry Transactions
//		// TransactionType bookEntryTxnType =
//		// TransactionType.valueOf(txnType.toString());
//		TransactionType bookEntryTxnType = TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER;
//		// Populating Txn
//		// Retrieving appropriate bank account
//		// bankAccount =
//		// bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(bank.getId(),
//		// bankAccountType, OwnerType.BANK);
//		BankAccountType accountType = null;
//
//		for (BankAccountType accountT : BankAccountType.values()) {
//			if (accountT.toString().equalsIgnoreCase(bankAccountType.toString())) {
//				LOG.debug(" got account   " + accountT);
//				accountType = accountT;
//			}
//		}
//		if (zw.co.esolutions.ewallet.enums.BankAccountType.EWALLET_BALANCING_SUSPENSE_ACCOUNT.equals(bankAccountType)) {
//			ownerType1 = zw.co.esolutions.ewallet.bankservices.service.OwnerType.BANK;
//			ownerId = bankService.findBankBranchById(dayEnd.getBranchId()).getBank().getId();
//			LOG.debug("bank id is   " + ownerId);
//		} else {
//			ownerType1 = zw.co.esolutions.ewallet.bankservices.service.OwnerType.BANK_BRANCH;
//			ownerId = dayEnd.getBranchId();
//		}
//
//		bankAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(ownerId, accountType, ownerType1, null);
//		LOG.debug(" got bank account  " + bankAccount);
//		transaction.setAccountId(bankAccount.getId());
//		transaction.setType(bookEntryTxnType);
//		transaction.setProcessTxnReference(reference);
//		if (TransactionActionType.CREDIT.equals(actionType)) {
//			transaction.setAmount(-amount);
//			if (message == null) {
//				transaction.setNarrative("Credit " + bankAccountType.toString());
//			} else {
//				transaction.setNarrative(message);
//			}
//		} else if (TransactionActionType.DEBIT.equals(actionType)) {
//			transaction.setAmount(amount);
//			if (message == null) {
//				transaction.setNarrative("Debit " + bankAccountType.toString());
//			} else {
//				transaction.setNarrative(message);
//			}
//		}
//
//		// Creating transaction
//		transaction = bookEntryService.createTransaction(transaction);
//
//		if (transaction == null || transaction.getId() == null) {
//			throw new Exception("Error occured, failed to create transaction");
//		}
//
//		LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>> Acc Type = " + bankAccountType);
//
//		// If successful transaction adjust balance
//		if (BankAccountType.E_WALLET.equals(bankAccountType)) {
//			if (TransactionActionType.CREDIT.equals(actionType)) {
//				bankAccount.setRunningBalance(bankAccount.getRunningBalance() + amount);
//			} else {
//				bankAccount.setRunningBalance(bankAccount.getRunningBalance() - amount);
//			}
//		}
//		// Update bankaaccount
//		bankAccount = bankService.editBankAccount(bankAccount, EWalletConstants.SYSTEM);
//
//		if (bankAccount == null || bankAccount.getId() == null) {
//			throw new Exception("Error occured, failed to update bank account");
//		}
//		LOG.debug("Book entry done   ");
//	}

	

//	@SuppressWarnings("unchecked")
//	public List<ProcessTransaction> getProcessTransactionsByDayEndId(DayEnd dayEnd) {
//
//		String dayEndId = dayEnd.getDayEndId();
//		LOG.debug("checking dayend object if it not nulllll:::::::::" + dayEnd);
//		if (dayEnd != null) {
//			LOG.debug("checking dayend object if it not nulllll:::::::::" + dayEnd.getDayEndId());
//		}
//		List<ProcessTransaction> results = null;
//		try {
//
//			Query query = em.createNamedQuery("getProcessTransactionsByDayEndId");
//			query.setParameter("dayEndId", dayEndId);
//			query.setParameter("dayEndReceipts", zw.co.esolutions.ewallet.enums.TransactionType.DAYEND_RECEIPTS);
//			query.setParameter("dayEndPayOuts", zw.co.esolutions.ewallet.enums.TransactionType.DAYEND_PAYOUTS);
//			query.setParameter("dayEndUnderPost", zw.co.esolutions.ewallet.enums.TransactionType.DAYEND_UNDERPOST);
//			query.setParameter("dayEndOverPost", zw.co.esolutions.ewallet.enums.TransactionType.DAYEND_OVERPOST);
//			query.setParameter("dayEndFloats", zw.co.esolutions.ewallet.enums.TransactionType.DAYEND_FLOATS);
//			query.setParameter("dayEndCashTendered", zw.co.esolutions.ewallet.enums.TransactionType.DAYEND_CASH_TENDERED);
//
//			results = (List<ProcessTransaction>) query.getResultList();
//			if (results != null) {
//				LOG.debug("checking result:::number of dayend trxns:::::::::::" + results.size());
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//		if (results == null || results.isEmpty()) {
//			results = null;
//		}
//		return results;
//	}

	public boolean checkIfProcessTransactionsSuccessful(DayEnd dayEnd) {
		List<ProcessTransaction> processTransactions = this.getProcessTransactionsByDayEndId(dayEnd);
		if (processTransactions == null) {
			return false;
		}
		int count = 0;
		for (ProcessTransaction txn : processTransactions) {
			++count;
			LOG.debug(count + "dayend  txn status>>>>>>>" + txn.getStatus() + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>txn type " + txn.getTransactionType());
			if (!TransactionStatus.COMPLETED.equals(txn.getStatus())) {
				return false;
			}
		}
		return true;
	}

	public TransactionCharge createTransactionCharge(TransactionCharge transactionCharge) throws Exception {
		if (transactionCharge == null) {
			throw new Exception("Transaction Charge is null");
		}
		LOG.debug("Creating transaction charge");
		if (transactionCharge.getId() == null) {
			ReferenceGeneratorServiceSOAPProxy refService = new ReferenceGeneratorServiceSOAPProxy();
			String tariffRef = refService.generateUUID(EWalletConstants.SEQUENCE_NAME_CHARGE, EWalletConstants.SEQUENCE_PREFIX_COMMISSION, Formats.yearFormatTwoDigit.format(new Date(System.currentTimeMillis())), 10000000L, 1000000000L - 1L);
			transactionCharge.setId(tariffRef);
		}
		if (transactionCharge.getDateCreated() == null) {
			transactionCharge.setDateCreated(new Date());
		}
		em.persist(transactionCharge);
		LOG.debug("Created a transaction charge.");
		return transactionCharge;
	}

	public TransactionCharge updateTransactionCharge(TransactionCharge transactionCharge) throws Exception {
		if (transactionCharge == null) {
			throw new Exception("Transaction Charge is null");
		}
		try {
			TransactionCharge tc = em.find(TransactionCharge.class, transactionCharge.getId());
			if(tc != null && tc.getId() != null) {
				transactionCharge.setVersion(tc.getVersion());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		transactionCharge = em.merge(transactionCharge);
		return transactionCharge;
	}

	public void deleteTransactionCharge(TransactionCharge transactionCharge) throws Exception {
		if (transactionCharge == null) {
			throw new Exception("Transaction Charge is null");
		}
		transactionCharge = em.merge(transactionCharge);
		em.remove(transactionCharge);
	}

	public TransactionCharge findtTransactionChargeById(String transactionChargeId) {
		TransactionCharge transactionCharge = null;
		try {
			transactionCharge = em.find(TransactionCharge.class, transactionChargeId);
		} catch (NoResultException nre) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return transactionCharge;
	}

	@SuppressWarnings("unchecked")
	public List<TransactionCharge> getTransactionChargesByMessageId(String messageId) {
		List<TransactionCharge> results = null;
		try {
			Query query = em.createNamedQuery("getTransactionChargesByMessageId");
			query.setParameter("messageId", messageId);
			results = (List<TransactionCharge>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (results == null || results.isEmpty()) {
			return null;
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<TransactionCharge> getTransactionChargesByMessageIdAndAgentType(String messageId, zw.co.esolutions.ewallet.enums.AgentType agentType) {
		List<TransactionCharge> results = null;
		try {
			Query query = em.createNamedQuery("getTransactionChargesByMessageIdAndAgentType");
			query.setParameter("messageId", messageId);
			query.setParameter("agentType", agentType);
			results = (List<TransactionCharge>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (results == null || results.isEmpty()) {
			return null;
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<TransactionCharge> getAllTransactionCharge() {
		List<TransactionCharge> results = null;
		try {
			Query query = em.createNamedQuery("getTransactionCharge");
			results = (List<TransactionCharge>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (results == null || results.isEmpty()) {
			return null;
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<TransactionCharge> getTransactionChargeByProcessTransactionId(String processTransactionId) {
		List<TransactionCharge> results = null;
		try {
			Query query = em.createNamedQuery("getTransactionChargeByProcessTransaction");
			query.setParameter("processTransaction_id", processTransactionId);
			results = (List<TransactionCharge>) query.getResultList();
		} catch (NoResultException no) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (results == null || results.isEmpty()) {
			return null;
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<TransactionCharge> getTransactionChargeByTariffId(String tariffId) {
		List<TransactionCharge> results = null;
		try {
			Query query = em.createNamedQuery("getTransactionChargeByTariffId");
			query.setParameter("tariffId", tariffId);
			results = (List<TransactionCharge>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (results == null || results.isEmpty()) {
			return null;
		}
		return results;
	}

	@Override
	public ResponseCode postCustomerDepositByAgentTxns(ProcessTransaction txn) throws Exception {
		LOG.debug("This is a fake post");
		return ResponseCode.E000;
	}

	@Override
	public ResponseCode postCustomerWidthdrawalByAgentTxns(ProcessTransaction txn) throws Exception {
		LOG.debug("This is a fake post");
		return ResponseCode.E000;
	}

	@Override
	public ResponseCode postTrasnferEmoneyToSubAgentTxns(ProcessTransaction txn) throws Exception {
		LOG.debug("This is a fake post");
		return ResponseCode.E000;
	}

	@Override
	public ResponseCode postAgentWidthdrawTxns(ProcessTransaction txn) throws Exception {
		LOG.debug("Posting AGENT WIDTHDRAWAL FROM OWN ACCOUNT : " + txn.getMessageId());
		return ResponseCode.E000;
	}

	@Override
	public ResponseCode postAgentRegistersCustomerTxns(ProcessTransaction txn) throws Exception {
		LOG.debug("This is a fake post");
		return ResponseCode.E000;
	}

	@Override
	public ResponseCode postAgentRegistersSubAgentTxns(ProcessTransaction txn) throws Exception {
		LOG.debug("This is a fake post");
		return ResponseCode.E000;
	}

	public ProcessTransaction addApplicableCommissionToProcessTxn(ProcessTransaction txn, zw.co.esolutions.ewallet.enums.TransactionType commissionType) throws Exception {
		
		LOG.debug("In addApplicableCommToTxn..");
		
		Tariff commission;
		long value = 0;
		TransactionLocationType locationType = TransactionLocationType.AGENT;

		TariffServiceSOAPProxy tariffService = new TariffServiceSOAPProxy();

		LOG.debug("Looking for this commission:    " + commissionType);

		commission = tariffService.retrieveAppropriateTariff(CustomerClass.AGENT, 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.valueOf(commissionType.name()), 
					AgentType.valueOf(locationType.name()), txn.getAmount(), txn.getFromBankId());
					
		LOG.debug("Commission FOUND : " + commission);
		if (commission != null && commission.getId() != null) {
			LOG.debug("Operating on commission : " + commission);
			if (commission.getTariffTable().getTariffType().equals(TariffType.FIXED_AMOUNT) || commission.getTariffTable().getTariffType().equals(TariffType.SCALED)) {
				value = commission.getValue(); 
				
			} else {
				LOG.debug("Percentage commission on value of amount: " + txn.getAmount());
				value = (commission.getValue() * txn.getAmount())/EWalletConstants.PERCENTAGE_DIVISOR; //Tariff Percentage value is stored as a product of 100 
			}
			
			if (txn.getTariffAmount() > 0 && value >= txn.getTariffAmount()) {
				LOG.debug("Tariff exists..Commission is greater than or equal to tariff.. reject");
				txn.setResponseCode(ResponseCode.E505.name());
				txn.setNarrative(ResponseCode.E505.getDescription());
				txn = this.promoteTxnState(txn, TransactionStatus.FAILED, null);
				return txn;
			}
			
			LOG.debug("Adding commission as transaction charge : " + value);
			// Populating process txn
			txn.setAgentCommissionId(commission.getId());
			txn.setAgentCommissionAmount(value);
			if (value > 0) { 
				TransactionCharge transactionCharge = new TransactionCharge();
				transactionCharge.setStatus(txn.getStatus());
				transactionCharge.setTransactionType(zw.co.esolutions.ewallet.enums.TransactionType.COMMISSION);
				transactionCharge.setTariffId(commission.getId());
				transactionCharge.setTariffAmount(value);

				transactionCharge.setProcessTransaction(txn);
				
				LOG.debug("Add agentNumber if applicable.." + txn.getAgentNumber());
				transactionCharge.setAgentNumber(txn.getAgentNumber());
				
				transactionCharge = this.createTransactionCharge(transactionCharge);
			}
			txn = this.updateProcessTransaction(txn);
			LOG.debug("Done adding commission as transaction charge : ");
		} else {
			// the tariff is not available, assume a non-tariffable txn
			LOG.debug("Commission is null, gone to else : ");
			txn.setResponseCode(ResponseCode.E821.name());
			txn.setNarrative(ResponseCode.E821.getDescription());
			txn = this.promoteTxnState(txn, TransactionStatus.FAILED, null);
		}

		LOG.debug("Returning  : ");
		return txn;
	}

	public ResponseCode processSweepAgentCommission(RequestInfo requestInfo) throws Exception {
//		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
//		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
//		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
//
//		List<BankAccount> agentCommissionAccounts = bankService.getBankAccountByType(BankAccountType.AGENT_COMMISSION_ACCOUNT);
//
//		if (agentCommissionAccounts != null && agentCommissionAccounts.isEmpty()) {
//			for (BankAccount account : agentCommissionAccounts) {
//				Agent agent = agentService.findAgentById(account.getAccountHolderId());
//
//				Agent subAgent = null, superAgent = null;
//				BankAccount superAccount = null;
//				MobileProfile subAgentProfile = null, superAgentProfile = null;
//
//				if (agent != null && agent.getId() != null) {
//					if (zw.co.esolutions.ewallet.agentservice.service.AgentType.SUPER_AGENT.equals(agent.getAgentType())) {
//
//						continue;
//
//					} else {
//						// process subAgent
//						subAgent = agent;
//						Customer subAgentCustomer = customerService.findCustomerById(subAgent.getCustomerId());
//						List<MobileProfile> subAgentProfileList = customerService.getMobileProfileByCustomer(subAgentCustomer.getId());
//						for (MobileProfile profile : subAgentProfileList) {
//							if (profile.isPrimary()) {
//								subAgentProfile = profile;
//							}
//						}
//						// process superAgent
//						superAgent = agentService.findAgentById(subAgent.getSuperAgentId());
//						Customer superAgentCustomer = customerService.findCustomerById(superAgent.getCustomerId());
//						List<MobileProfile> superAgentProfileList = customerService.getMobileProfileByCustomer(superAgentCustomer.getId());
//						for (MobileProfile profile : superAgentProfileList) {
//							if (profile.isPrimary()) {
//								superAgentProfile = profile;
//							}
//						}
//
//						if (superAgent != null && superAgent.getId() != null) {
//							superAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(superAgent.getId(), BankAccountType.AGENT_COMMISSION_ACCOUNT, OwnerType.AGENT, null);
//							// sweep commission
//							if (superAccount != null && superAccount.getId() != null) {
//								long commissionBalance = account.getRunningBalance();
//								if (commissionBalance > 0) {
//
//									// create process txn
//									ProcessTransaction txn = this.populateAndCreateProcessTransaction(requestInfo, superAgent, subAgent, superAgentProfile, subAgentProfile, account, superAccount);
//
//									// sweep
//									LOG.debug("Posting AGENT Commission Sweep : " + txn.getMessageId());
//
//									// DEBIT SubAgent Commission Account with
//									// Agent Commission
//									this.doBookEntryPosting(BankAccountType.AGENT_COMMISSION_ACCOUNT, TransactionActionType.DEBIT, zw.co.esolutions.ewallet.enums.TransactionType.COMMISSION_SWEEPING, txn.getAgentCommissionAmount(), subAgentProfile.getId(), txn, null, null, TransactionCategory.COMMISSION);
//									LOG.debug("DONE with DEBIT Agent Commission Suspense Account with Total Amount : " + txn.getMessageId() + " COMMISSION AMT : " + txn.getAgentCommissionAmount());
//
//									// CREDIT SuperAgent Commission Suspense
//									// Account with Agent Commission
//									this.doBookEntryPosting(BankAccountType.AGENT_COMMISSION_ACCOUNT, TransactionActionType.CREDIT, zw.co.esolutions.ewallet.enums.TransactionType.COMMISSION_SWEEPING, txn.getAgentCommissionAmount(), superAgentProfile.getId(), txn, null, null, TransactionCategory.COMMISSION);
//									LOG.debug("DONE with CREDIT SuperAgent Commission Account with Total Amount : " + txn.getMessageId() + " COMMISSION AMT : " + txn.getAgentCommissionAmount());
//
//									txn = this.promoteTxnState(txn, TransactionStatus.BANK_REQUEST, "Request to send to bank queue");
//
//									this.forwardSweepCommissionTxnsToBank(txn);
//
//									txn = this.promoteTxnState(txn, TransactionStatus.AWAITING_DEBIT_RES, "Awaiting bank response");
//
//									return ResponseCode.E000;
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//
		return ResponseCode.E777;
	}

	public ResponseCode forwardSweepCommissionTxnsToBank(ProcessTransaction txn) throws Exception {

		LOG.debug("Forwarding to bank queue");
		BankRequest bankRequest = this.populateBankRequest(txn);
		this.submitRequest(bankRequest, false);
		LOG.debug("Forwarded to bank queue");
		return ResponseCode.E000;
		
	}

	@Override
	public ProcessTransaction addApplicableAgentCommissionToProcessTxn(ProcessTransaction txn, String agentMobileProfileId) throws Exception {
		// TODO Auto-generated method stub
		LOG.debug("This method is still a stub");
		return txn;
	}

	public String getSmsLocationId(RequestInfo requestInfo) {
		String sms = "";
		try {
			Bank bk = new BankServiceSOAPProxy().findBankById(requestInfo.getSourceBankId());
			sms = bk.getName() + " " + requestInfo.getTransactionType().toString() + " Txns";
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sms;
	}

	public CustomerRegResponse processCustomerMerchatRegistration(ProcessTransaction txn) throws Exception {
		MerchantServiceSOAPProxy merchantService = new MerchantServiceSOAPProxy();
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		List<Bank> banks = bankService.getBankByStatus(BankStatus.ACTIVE);
		if (banks == null || banks.isEmpty()) {
			return new CustomerRegResponse(ResponseCode.E900, "Error occurred while processing request. Please try again later.");
		}
		Merchant merchant = merchantService.getMerchantByShortName(txn.getUtilityName());
		if (merchant == null || merchant.getId() == null) {
			LOG.debug("Merchant with short name " + txn.getUtilityName() + " could not be found. FAIL the operation");
			return new CustomerRegResponse(ResponseCode.E900, txn.getUtilityAccount() + " is not registered on e-Solutions Mobile Commerce.");
		} else {
			BankMerchant bankMerchant = null;
			for (Bank bank : banks) {
				bankMerchant = merchantService.getBankMerchantByBankIdAndMerchantId(bank.getId(), merchant.getId());
				if (bankMerchant != null && bankMerchant.getId() != null) {
					break;
				}
			}

			MobileProfile mobileProfile = customerService.getMobileProfileByMobileNumber(txn.getSourceMobile());

			if (bankMerchant == null || bankMerchant.getId() == null) {
				LOG.debug("Merchant with short name " + merchant.getShortName() + " could is not enabled for this bank. FAIL the operation");
				return new CustomerRegResponse(ResponseCode.E900, txn.getUtilityName() + " is not registered on this bank.");
			} else if (mobileProfile == null || mobileProfile.getId() == null) {
				LOG.debug("Source Mobile Number is not valid at the bank... something messy here. FAIL the operation");
				return new CustomerRegResponse(ResponseCode.E900, "Error occurred while processing request. Please try again later.");
			}

			else {
				CustomerMerchant existing = merchantService.getCustomerMerchantByCustomerIdAndBankMerchantIdAndStatus(mobileProfile.getCustomer().getId(), bankMerchant.getId(), CustomerMerchantStatus.ACTIVE);
				if (existing == null || existing.getId() == null) {
					CustomerMerchant customerMerchant = new CustomerMerchant();
					customerMerchant.setBankId(txn.getFromBankId());
					customerMerchant.setBankMerchant(bankMerchant);
					customerMerchant.setCustomerAccountNumber(txn.getUtilityAccount());
					customerMerchant.setCustomerId(mobileProfile.getCustomer().getId());
					customerMerchant.setStatus(CustomerMerchantStatus.ACTIVE);
					customerMerchant = merchantService.createCustomerMerchant(customerMerchant, EWalletConstants.SYSTEM);
//					MessageSync.populateAndSync(existing, MessageAction.CREATE);
					return new CustomerRegResponse(ResponseCode.E000, "Your " + merchant.getName() + " account number " + customerMerchant.getCustomerAccountNumber() + " has been registered successfully");
				} else {
					LOG.debug("The customer already has a registered account with " + txn.getUtilityName());
//					MessageSync.populateAndSync(existing, MessageAction.UPDATE);
					return new CustomerRegResponse(ResponseCode.E900, "You already have an account registered for " + merchant.getName());
				}
				
			}
		}
	}

	@SuppressWarnings("unused")
	public ProcessTransaction populateProcessTransactionWithManualInfor(ManualPojo manual) throws Exception {
		LOG.debug(">>>>>>>>>>>>> Now in Populate Manual resolve.");
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		MerchantServiceSOAPProxy merchantService = new MerchantServiceSOAPProxy();

		// ProcessTransaction oldTxn = em.find(ProcessTransaction.class,
		// manual.getOldMessageId());
		Profile p = profileService.getProfileByUserName(manual.getUserName());
		LOG.debug(">>>>>>>>>>>>> User name = " + manual.getUserName());
		BankBranch profileBranch = bankService.findBankBranchById(p.getBranchId());
		LOG.debug(">>>>>>>>>>>>> Done getting branch = " + profileBranch);
		BankAccount srcBankAccount = bankService.getUniqueBankAccountByAccountNumber(manual.getSourceAccountNumber());
		LOG.debug(">>>>>>>>>>>>> Main Txn Source Account found = " + srcBankAccount);
		BankAccount destBankAccount = bankService.getUniqueBankAccountByAccountNumber(manual.getDestinationAccountNumber());
		LOG.debug(">>>>>>>>>>>>> Main Txn Desination Account found = " + destBankAccount);

		String fromBankId = null;
		String toBankId = null;
		// Acommodating Customers & Merchants
		Customer srcCustomer = null;
		Customer targetCustomer = null;

		// Acommodating Merchants
		BankMerchant srcBankMerchant = null;
		BankMerchant destBankMerchant = null;

		// Accommodating BankBranches
		BankBranch srcBankBranch = null;
		BankBranch destBankBranch = null;

		// Accommodating Banks
		Bank srcBank = null;
		Bank destBank = null;

		// Finding Customers
		if ((srcBankAccount != null && srcBankAccount.getId() != null) && OwnerType.CUSTOMER.equals(srcBankAccount.getOwnerType())) {
			LOG.debug(">>>>>>>>>>>>> Src Acc is Customer Account = " + srcBankAccount.getAccountNumber());
			srcCustomer = customerService.findCustomerById(srcBankAccount.getAccountHolderId());
			if ((srcCustomer != null && srcCustomer.getId() != null)) {
				fromBankId = bankService.findBankBranchById(srcCustomer.getBranchId()).getBank().getId();
			}
		}
		if ((destBankAccount != null && destBankAccount.getId() != null) && OwnerType.CUSTOMER.equals(destBankAccount.getOwnerType())) {
			LOG.debug(">>>>>>>>>>>>> Dest acc is Customer Account = " + destBankAccount.getAccountName());
			targetCustomer = customerService.findCustomerById(destBankAccount.getAccountHolderId());
			if (targetCustomer != null && targetCustomer.getId() != null) {
				toBankId = bankService.findBankBranchById(targetCustomer.getBranchId()).getBank().getId();
			}
		}
		// Finding Agents
		if ((srcBankAccount != null && srcBankAccount.getId() != null) && OwnerType.AGENT.equals(srcBankAccount.getOwnerType())) {
			LOG.debug(">>>>>>>>>>>>> Src Acc is Agent Account = " + srcBankAccount.getAccountNumber());
			srcCustomer = customerService.findCustomerById(srcBankAccount.getAccountHolderId());
			if (srcCustomer != null && srcCustomer.getId() != null) {
				fromBankId = bankService.findBankBranchById(srcCustomer.getBranchId()).getBank().getId();
			}
		}
		if ((destBankAccount != null && destBankAccount.getId() != null) && OwnerType.AGENT.equals(destBankAccount.getOwnerType())) {
			LOG.debug(">>>>>>>>>>>>> Dest Acc is Agent Account = " + destBankAccount.getAccountNumber());
			targetCustomer = customerService.findCustomerById(destBankAccount.getAccountHolderId());
			if (targetCustomer != null && targetCustomer.getId() != null) {
				toBankId = bankService.findBankBranchById(targetCustomer.getBranchId()).getBank().getId();
			}
		}

		// Finding Merchants
		if ((srcBankAccount != null && srcBankAccount.getId() != null) && OwnerType.MERCHANT.equals(srcBankAccount.getOwnerType())) {
			LOG.debug(">>>>>>>>>>>>> Src Acc is Merchant Account = " + srcBankAccount.getAccountNumber());
			srcBankMerchant = merchantService.findBankMerchantById(srcBankAccount.getAccountHolderId());
		}
		if ((destBankAccount != null && destBankAccount.getId() != null) && OwnerType.MERCHANT.equals(destBankAccount.getOwnerType())) {
			LOG.debug(">>>>>>>>>>>>> Dest Acc is Merchant Account = " + destBankAccount.getAccountNumber());
			destBankMerchant = merchantService.findBankMerchantById(destBankAccount.getAccountHolderId());
		}

		// Finding BankBranches
		if ((srcBankAccount != null && srcBankAccount.getId() != null) && OwnerType.BANK_BRANCH.equals(srcBankAccount.getOwnerType())) {
			LOG.debug(">>>>>>>>>>>>> Src Acc is Bank BranchAccount = " + srcBankAccount.getAccountNumber());
			srcBankBranch = bankService.findBankBranchById(srcBankAccount.getAccountHolderId());
		}
		if ((destBankAccount != null && destBankAccount.getId() != null) && OwnerType.BANK_BRANCH.equals(destBankAccount.getOwnerType())) {
			LOG.debug(">>>>>>>>>>>>> Dest Acc is Bank Branch Account = " + destBankAccount.getAccountNumber());
			destBankBranch = bankService.findBankBranchById(destBankAccount.getAccountHolderId());
		}

		// Finding Banks
		if ((srcBankAccount != null && srcBankAccount.getId() != null) && OwnerType.BANK.equals(srcBankAccount.getOwnerType())) {
			LOG.debug(">>>>>>>>>>>>> Src Acc is Bank Account = " + srcBankAccount.getAccountNumber());
			srcBank = bankService.findBankById(srcBankAccount.getAccountHolderId());
		}
		if ((destBankAccount != null && destBankAccount.getId() != null) && OwnerType.BANK.equals(destBankAccount.getOwnerType())) {
			LOG.debug(">>>>>>>>>>>>> Dest Acc is Bank Account = " + destBankAccount.getAccountNumber());
			destBank = bankService.findBankById(destBankAccount.getAccountHolderId());
		}

		ProcessTransaction tranx = new ProcessTransaction();
		String refId = this.generateReference(manual.getTransactionType());
		LOG.debug(">>>>>>>>>>>>>>>>> Generated message id is " + refId);
		tranx.setId(refId);
		tranx.setMessageId(refId);
		tranx.setOldMessageId(manual.getOldMessageId());
		tranx.setTransactionType(manual.getTransactionType());
		// tranx.setSourceMobileId(oldTxn.getSourceMobileId());
		tranx.setTransactionLocationType(TransactionLocationType.BANK_BRANCH);
		tranx.setAmount(manual.getAmount());

		if (profileBranch != null && profileBranch != null) {
			tranx.setBankReference(profileBranch.getBank().getCode());
		}

		// Initializing Source Customer Specific values
		String customerId = srcCustomer != null ? srcCustomer.getId() : null;
		String customerName = srcCustomer != null ? srcCustomer.getLastName() + " " + srcCustomer.getFirstNames() : null;
		String customerBranchId = srcCustomer != null ? srcCustomer.getBranchId() : null;

		tranx.setBranchId(customerBranchId);
		if (tranx.getBranchId() == null && profileBranch != null) {
			tranx.setBranchId(profileBranch.getId());
		}
		tranx.setCustomerName(customerName);
		tranx.setFromBankId(fromBankId);
		if (tranx.getFromBankId() == null && profileBranch != null) {
			tranx.setFromBankId(profileBranch.getBank().getId());
		}
		tranx.setToBankId(toBankId);
		tranx.setSourceAccountNumber(manual.getSourceAccountNumber());
		tranx.setDestinationAccountNumber(manual.getDestinationAccountNumber());
		tranx.setDestinationEQ3AccountNumber(manual.getDestinationEQ3AccountNumber());
		tranx.setSourceEQ3AccountNumber(manual.getSourceEQ3AccountNumber());

		// tranx.setSourceMobile(oldTxn.getSourceMobile());
		// tranx.setTargetMobile(oldTxn.getTargetMobile());
		// tranx.setUtilityAccount(oldTxn.getUtilityAccount());
		// tranx.setUtilityName(oldTxn.getUtilityName());
		// tranx.setBankMerchantId(oldTxn.getBankMerchantId());

		tranx.setProfileId(p.getId());
		tranx.setCustomerId(customerId);
		tranx.setNonTellerId(p.getId());
		tranx.setTransactionLocationId(p.getBranchId());
		tranx.setTransactionClass(manual.getTransactionClass().toString());

		// Initializing Target Customer Specific values
		// String targetCustomerId = targetCustomer != null
		// ?targetCustomer.getId() : null;
		String targetCustomerName = targetCustomer != null ? targetCustomer.getLastName() + " " + targetCustomer.getFirstNames() : null;
		// String targetCustomerBranchId = targetCustomer !=
		// null?targetCustomer.getBranchId() : null;

		tranx.setToCustomerName(targetCustomerName);
		// tranx.setTargetMobileId(oldTxn.getTargetMobileId());

		tranx.setDateCreated(new Date());
		tranx.setNarrative(manual.getReason());
		tranx.setStatus(TransactionStatus.DRAFT);
		LOG.debug(">>>>>>>>>>>>> Done Populating Manual Resolve.");
		return tranx;
	}

	public String generateReference(zw.co.esolutions.ewallet.enums.TransactionType txnType) {
		ReferenceGeneratorServiceSOAPProxy refProxy = new ReferenceGeneratorServiceSOAPProxy();
		String str = null;
		if (zw.co.esolutions.ewallet.enums.TransactionType.ADJUSTMENT.equals(txnType)) {
			str = refProxy.generateUUID(EWalletConstants.SEQUENCE_NAME_DAY_ENDS, EWalletConstants.SEQUENCE_PREFIX_DAY_ENDS, Formats.yearFormatTwoDigit.format(new Date(System.currentTimeMillis())), 0, 1000000000L - 1L);
			//str = refProxy.generateUUID(zw.co.esolutions.ewallet.enums.TransactionType.DEPOSIT.name(), "A", Formats.yearFormatTwoDigit.format(new Date(System.currentTimeMillis())), 0, 1000000000L - 1L);
		}
		// All deposits
		else if (zw.co.esolutions.ewallet.enums.TransactionType.DEPOSIT.equals(txnType)) {
			str = refProxy.generateUUID(zw.co.esolutions.ewallet.enums.TransactionType.DEPOSIT.name(), "D", Formats.yearFormatTwoDigit.format(new Date(System.currentTimeMillis())), 0, 1000000000L - 1L);
		}
		// All withdrawals
		else if (zw.co.esolutions.ewallet.enums.TransactionType.WITHDRAWAL.equals(txnType)) {
			String year = Formats.yearFormatTwoDigit.format(new Date(System.currentTimeMillis()));
			str = refProxy.generateUUID(txnType.name(), "W", year, 0, 1000000000L - 1L);
		}
		// All Charges
		else if (zw.co.esolutions.ewallet.enums.TransactionType.TARIFF.equals(txnType)) {
			String year = Formats.yearFormatTwoDigit.format(new Date(System.currentTimeMillis()));
			str = refProxy.generateUUID(txnType.name(), "C", year, 0, 1000000000L - 1L);
		}
		return str;
	}

	@Override
	public BankRequest populateAdjustmentBankRequest(ProcessTransaction txn, boolean isReversal) throws Exception {
		BankRequest bankRequest = new BankRequest();

		bankRequest.setTransactionType(txn.getTransactionType());
		bankRequest.setReference(txn.getMessageId());
		bankRequest.setSourceBankCode(txn.getBankReference());
		bankRequest.setAmount(txn.getAmount());

		bankRequest.setCurrencyISOCode(EWalletConstants.USD);
		bankRequest.setSourceAccountNumber(txn.getSourceEQ3AccountNumber());
		bankRequest.setTargetAccountNumber(txn.getDestinationEQ3AccountNumber());

		bankRequest.setOriginalTxnReference(txn.getOldMessageId());
		bankRequest.setReversal(isReversal);

		if (isReversal) {
			bankRequest.setNarrative(txn.getOldMessageId() + "/" + txn.getNarrative());
		} else {
			bankRequest.setNarrative(txn.getOldMessageId() + "/" + txn.getNarrative());
		}


		List<TransactionCharge> transactionCharges = this.getTransactionChargeByProcessTransactionId(txn.getId());

		if (transactionCharges != null) {
			LOG.debug("@@@@@@          There are charges");
			for (TransactionCharge txnCharge : transactionCharges) {
				LOG.debug("Tarif acc for txn with ref : " + txn.getMessageId() + " and tariff ref : " + txnCharge.getId());
				LOG.debug("TXN CHARGE TYPE : " + txnCharge.getTransactionType() + ".");

				txnCharge.setStatus(TransactionStatus.BANK_REQUEST);
				this.updateTransactionCharge(txnCharge);
				bankRequest.addCommission(new Commission(txnCharge.getId(), txn.getMessageId(), txnCharge.getTariffAmount(), txn.getSourceAccountNumber(), txnCharge.getFromEQ3Account(), txnCharge.getToEQ3Account(), bankRequest.getNarrative(), txnCharge.getTransactionType(), txnCharge.getAgentNumber()));
			}
		}


		return bankRequest;
	}

	public BankResponse waitForBankResponse(BankRequest bankRequest) throws Exception {

		LOG.debug("#########      IN waitForBankResponse method");

		long TRANSACTION_TIMEOUT = Long.parseLong(config.getProperty("TRANSACTION_TIMEOUT"));
		long RESPONSE_CHECK_INTERVAL = Long.parseLong(config.getProperty("RESPONSE_CHECK_INTERVAL"));

		LOG.debug("#######  RESPONSE_CHECK_INTERVAL = " + RESPONSE_CHECK_INTERVAL);
		LOG.debug("#######  TRANSACTION_TIMEOUT = " + TRANSACTION_TIMEOUT);

		long count = RESPONSE_CHECK_INTERVAL;

		BankResponse response;
		ProcessTransaction txn = null;
		boolean responseArrived = false;

		while (count <= TRANSACTION_TIMEOUT) {

			Thread.sleep(RESPONSE_CHECK_INTERVAL); // wait for configured time
													// interval

			LOG.debug("#########      WAITED : " + (count / 1000) + " sec");

			count += RESPONSE_CHECK_INTERVAL;

			txn = this.getProcessTransactionByMessageId(bankRequest.getReference());

			if (TransactionStatus.BANK_RESPONSE.equals(txn.getStatus()) || TransactionStatus.FAILED.equals(txn.getStatus())) {
				responseArrived = true;
				break;
			} else {

				LOG.debug("#######  RESPONSE NOT YET ARRIVED");

			}
		}

		LOG.debug("#########      OUT of WAIT LOOP");

		if (txn == null || txn.getId() == null) {
			txn = this.getProcessTransactionByMessageId(bankRequest.getReference());
		}

		String adjustment = "Adjustment", reversal = "Reversal";

		String stringPlaceHolder = "";

		if (zw.co.esolutions.ewallet.enums.TransactionType.ADJUSTMENT.equals(bankRequest.getTransactionType())) {

			stringPlaceHolder = (bankRequest.isReversal()) ? reversal : adjustment;

		} else {

			stringPlaceHolder = bankRequest.getTransactionType().name();

		}

		if (responseArrived) {
			LOG.debug("#########      RESPONSE ARRIVED     : " + txn.getResponseCode());

			if (!ResponseCode.E000.name().equals(txn.getResponseCode())) {

				// mark as failed
				//this.promoteTxnState(txn, TransactionStatus.FAILED, stringPlaceHolder + " failed. ");
				LOG.debug("#########      TXN MARKED AS FAILED");

			}

			response = new BankResponse();
			response.setBankRequest(bankRequest);
			response.setResponseCode(ResponseCode.valueOf(txn.getResponseCode()));
			response.setAvailableBalance(txn.getBalance());
			response.setMessageType(txn.getTransactionType().name());
			response.setNarrative(txn.getNarrative());
			response.setValueDate(txn.getValueDate());

			LOG.debug("#########      RESPONSE RETURNED");

			return response;
		} else {

			// response timeout

			response = new BankResponse();
			response.setBankRequest(bankRequest);
			response.setResponseCode(ResponseCode.E830); // transaction timeout
			response.setMessageType(txn.getTransactionType().name());
			response.setNarrative("Transaction timed out");

			LOG.debug("#########      RESPONSE TIMEOUT RETURNED");

			return response;

		}
	}

	@Override
	public TransactionCharge populateTransactionCharge(ProcessTransaction txn, ManualPojo manual) throws Exception {
		LOG.debug(">>>>>>>>>>>>> Now in Populate Transaction charge");
		TransactionCharge txnCharge = new TransactionCharge();

		// txnCharge.setAgentType(txn.get)
		txnCharge.setChargeTransactionClass(manual.getChargeTransactionClass().toString());
		txnCharge.setFromEQ3Account(manual.getFromEQ3ChargeAccount());
		txnCharge.setFromEwalletAccount(manual.getFromEwalletChargeAccount());
		txnCharge.setProcessTransaction(txn);
		txnCharge.setTariffAmount(manual.getChargeAmount());
		txnCharge.setTariffId(txn.getTariffId());
		txnCharge.setToEQ3Account(manual.getToEQ3ChargeAccount());
		txnCharge.setToEwalletAccount(manual.getToEwalletChargeAccount());
		txnCharge.setTransactionType(zw.co.esolutions.ewallet.enums.TransactionType.TARIFF);

		LOG.debug(">>>>>>>>>>>>> Done Populate Transaction charge");
		return txnCharge;
	}

	public void rollbackEQ3Postings(ProcessTransaction txn) throws Exception {

		LOG.debug("%%%%%%%%%%% BEGIN EQ3 ROLLBACK");

		BankRequest bankRequest = this.populateBankRequest(txn);
		bankRequest.setReversal(true);
		// real txnType inokonzeresa, use this one
		bankRequest.setTransactionType(zw.co.esolutions.ewallet.enums.TransactionType.ADJUSTMENT); 

		this.sendBankRequestToBank(bankRequest);

		LOG.debug("%%%%%%%%%%% EQ3 ROLLBACK INITIATED SUCCESSFULLY");

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void sendBankRequestToBank(BankRequest bankRequest) throws Exception {
		LOG.debug("%%%%%%%%%%% SENDING REQUEST TO BANK");
		
		this.submitRequest(bankRequest, false);

		LOG.debug("%%%%%%%%%%%  REQUEST SENT TO BANK");

	}

	public ResponseCode rollbackEWalletAndEQ3Postings(ProcessTransaction txn) throws Exception {

		try {
			
			if(TransactionType.TOPUP.name().equals(txn.getStatus().name())){
				txn = this.promoteTxnState(txn, TransactionStatus.REVERSAL_REQUEST, "Failed Topup Action . Transaction rolled back.");
			}else{
				txn = this.promoteTxnState(txn, TransactionStatus.FAILED, "Failed Posting to EWallet. Transaction rolled back.");
			}
			
			this.rollbackEQ3Postings(txn);

			LOG.debug("%%%%%%%%%%%  ALL TRANSACTIONS ROLLED BACK... TXN STATUS SET TO FAILED");

			return ResponseCode.E836; // Rollback successful

		} catch (Exception e) {

			this.promoteTxnState(txn, TransactionStatus.MANUAL_RESOLVE, "Failed Posting to EWallet. Error occured during transaction rollback.");

			LOG.debug(e.getMessage());
			LOG.debug("%%%%%%%%%%%  EXCEPTION THROWN DURING ROLLBACK... NOT COMPLETED");

		}

		return ResponseCode.E837; // Errors occured during Rollback

	}

	public DayEnd editTellerDayEnd(DayEnd dayEnd, String userName) {
		LOG.debug(" in update method of day end    " + dayEnd.getStatus());
		try {
			dayEnd = em.merge(dayEnd);
		} catch (Exception e) {
			LOG.debug("Exception in update method");

		}
		LOG.debug("In update method done");
		return dayEnd;
	}

	public List<ProcessTransaction> getProcessTransactionsByDayEndId(DayEnd dayEnd) {
		String dayEndId = dayEnd.getDayEndId();
		List<ProcessTransaction> results = null;
		try {
			//
			Query query = em.createNamedQuery("getProcessTransactionsByDayEndId");
			query.setParameter("dayEndId", dayEndId);
			query.setParameter("dayEndReceipts", zw.co.esolutions.ewallet.enums.TransactionType.DAYEND_RECEIPTS);
			query.setParameter("dayEndPayOuts", zw.co.esolutions.ewallet.enums.TransactionType.DAYEND_PAYOUTS);
			query.setParameter("dayEndUnderPost", zw.co.esolutions.ewallet.enums.TransactionType.DAYEND_UNDERPOST);
			query.setParameter("dayEndOverPost", zw.co.esolutions.ewallet.enums.TransactionType.DAYEND_OVERPOST);
			query.setParameter("dayEndFloats", zw.co.esolutions.ewallet.enums.TransactionType.DAYEND_FLOATS);
			query.setParameter("dayEndCashTendered", zw.co.esolutions.ewallet.enums.TransactionType.DAYEND_CASH_TENDERED);

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
	
	public long getDailyAmountByCustomerIdAndTxnType(String customerId, zw.co.esolutions.ewallet.enums.TransactionType txnType) {
		long dailyAmount = 0;
		List<ProcessTransaction> txns = null;
		Date date = new Date();
		Date fromDate = DateUtil.getBeginningOfDay(date);
		Date toDate = DateUtil.getEndOfDay(date);
		try {
			Query query = em.createQuery("SELECT SUM(p.amount) FROM ProcessTransaction p WHERE p.dateCreated >= :fromDate AND " + "p.dateCreated <= :toDate AND p.transactionType = :txnType AND p.customerId = :customerId");
			query.setParameter("fromDate", fromDate);
			query.setParameter("toDate", toDate);
			query.setParameter("txnType", txnType);
			query.setParameter("customerId", customerId);
			Object obj = query.getSingleResult();
			if (obj != null)
				dailyAmount = (Long) obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dailyAmount;
	}

	public List<ProcessTransaction> getStartOfDayTxnByProfileIdAndDayEndSummaryAndStatus(String profileId, TransactionStatus awaiting_approval) {
		List<ProcessTransaction> results;
		Query query = em.createNamedQuery("getStartOfDayTxnByProfileIdAndStatusAndDayEndSummary");

		query.setParameter("transactionType", zw.co.esolutions.ewallet.enums.TransactionType.START_OF_DAY_FLOAT_IN);

		query.setParameter("profileId", profileId);
		// query.setParameter("statusBankReq", TransactionStatus.BANK_REQUEST);

		query.setParameter("statusAwaitingApproval", TransactionStatus.AWAITING_APPROVAL);
		// use null day end id
		results = (List<ProcessTransaction>) query.getResultList();
		// LOG.debug(" check results   "+results);
		// LOG.debug("done");
		return results;
	}
	
	
	
	public long getTellerSubTotalByTransactionTypeAndDateRangeAndStatus(String profileId, Date fromDate, Date toDate, zw.co.esolutions.ewallet.enums.TransactionType transactionType) {
		long subTotal = 0L;
		LOG.debug("BEGIN : " + transactionType + " " + fromDate + " " + toDate + " " + profileId);
		Query query = em.createNamedQuery("getTellerNetCashByTransactionTypeAndStatus");
		query.setParameter("transactionType", transactionType);
		// query.setParameter("fromDate", fromDate);
		// query.setParameter("toDate", toDate);
		query.setParameter("profileId", profileId);
		// query.setParameter("statusBankReq", TransactionStatus.BANK_REQUEST);
		// query.setParameter("statusManResolve",
		// TransactionStatus.MANUAL_RESOLVE);
		query.setParameter("statusCompl", TransactionStatus.COMPLETED);
		// query.setParameter("statusAWT", TransactionStatus.AWAITING_APPROVAL);
		Object temp = query.getSingleResult();
		LOG.debug("Result : " + temp);
		if (temp != null) {
			subTotal = (Long) temp;
		}
		LOG.debug("Result : " + subTotal);
		return subTotal;
	}
	
	public List<ProcessTransaction> getStartOfDayTransactionByTransactionTypeAndTellerAndDateRange(zw.co.esolutions.ewallet.enums.TransactionType transactionType, String tellerId, Date fromDate, Date toDate, TransactionStatus txnStatus) throws Exception {
		// TODO Auto-generated method stub
		Query query = em.createNamedQuery("getStartOfDayTransactionByTransactionTypeAndTellerAndDateRange");
		query.setParameter("tellerId", tellerId);
		query.setParameter("transactionType", transactionType);
		query.setParameter("fromDate", DateUtil.getBeginningOfDay(fromDate));
		query.setParameter("toDate", DateUtil.getBeginningOfDay(toDate));
		query.setParameter("txnStatus", txnStatus);
		// use null day end id
		return (List<ProcessTransaction>) query.getResultList();

	}
	
	public List<ProcessTransaction> getStartOfDayTransactionByTransactionTypeAndBranchAndStatus(zw.co.esolutions.ewallet.enums.TransactionType transactionType, String branchId, TransactionStatus txnStatus) throws Exception {
		Query query = em.createNamedQuery("getStartOfDayTransactionByTransactionTypeAndBranchAndStatus");
		query.setParameter("transactionType", transactionType);
		query.setParameter("branchId", branchId);
		query.setParameter("txnStatus", txnStatus);
		return (List<ProcessTransaction>) query.getResultList();
	}
	
	public List<ProcessTransaction> getStartDayTxnsByTellerAndDateRangeAndStatus(String tellerId, Date fromDate, Date toDate) throws Exception {
		// TODO Auto-generated method stub
		List<ProcessTransaction> results;
		LOG.debug("  teller id is >>>>>>>>>>>>>>>>>>>" + tellerId);
		LOG.debug("from date >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + DateUtil.getBeginningOfDay(fromDate));
		LOG.debug("to date >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + DateUtil.getBeginningOfDay(toDate));
		Query query = em.createNamedQuery("getStartDayTxnsByTellerAndDateRangeAndStatus");

		query.setParameter("fromDate", DateUtil.getBeginningOfDay(fromDate));
		query.setParameter("toDate", DateUtil.getEndOfDay(toDate));

		query.setParameter("transactionType", zw.co.esolutions.ewallet.enums.TransactionType.START_OF_DAY_FLOAT_IN);

		query.setParameter("profileId", tellerId);
		query.setParameter("statusBankReq", TransactionStatus.BANK_REQUEST);
		query.setParameter("statusManResolve", TransactionStatus.MANUAL_RESOLVE);
		query.setParameter("statusCompl", TransactionStatus.COMPLETED);
		query.setParameter("statusAWT", TransactionStatus.AWAITING_APPROVAL);
		// use null day end id
		results = (List<ProcessTransaction>) query.getResultList();
		LOG.debug("qu");
		return results;

	}
	
	public boolean checkTellerDayEndsPendingApproval(String tellerId) {
		boolean result = false;
		LOG.debug("2 00000000000000000teller id " + tellerId);
		List<DayEnd> dayEnds = getDayEndsByDayEndStatusAndProfileID(tellerId, DayEndStatus.AWAITING_APPROVAL);
		// LOG.debug(">>>>>>>>>>>>>>>"+dayEnds.size());
		if (dayEnds != null && dayEnds.size() > 0) {
			LOG.debug("745747484848484>>>>>>>>>>>yyyyyyyyyyyy   " + dayEnds.size());
			return true;
		}

		return false;

	}

	private List<DayEnd> getDayEndsByDayEndStatusAndProfileID(String tellerId, DayEndStatus status) {
		List<DayEnd> results = null;
		try {
			Query query = em.createNamedQuery("getDayEndsByDayEndStatusAndProfileId");
			query.setParameter("status", status);
			query.setParameter("tellerId", tellerId);

			LOG.debug("---------status34 " + status);
			LOG.debug("-----------Query45  " + query.toString());
			results = (List<DayEnd>) query.getResultList();

			LOG.debug("33344444444444Results ............................9090890..................." + results);
			if (results != null) {
				LOG.debug("33344444444444Results ............................9090890..................." + results);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (results == null || results.isEmpty()) {
			results = null;
		}
		LOG.debug("resultsuuuiii---------      " + results);
		return results;
	}

	
	public boolean checkIfStartofDayExistsAndApproved(String profileId, Date toDay) {
		/*
		 * Start of day txn can be Approved, Bank Request, ManuallyResolve and
		 * Completed
		 */
		List<ProcessTransaction> startOfDayTnxs = getStartOfDayTxnByProfileIdAndStatusAndDateAndDayEndSummary(profileId, toDay);
		if (startOfDayTnxs != null && startOfDayTnxs.size() > 0) {
			return true;
		} else
			return false;
	}
	
	private List<ProcessTransaction> getStartOfDayTxnByProfileIdAndStatusAndDateAndDayEndSummary(String profileId, Date toDay) {
		List<ProcessTransaction> results;
		Query query = em.createNamedQuery("getStartDayTxnsByTellerAndDateRangeAndApprovalStatus");

		query.setParameter("fromDate", DateUtil.getBeginningOfDay(toDay));
		query.setParameter("toDate", DateUtil.getEndOfDay(toDay));

		query.setParameter("transactionType", zw.co.esolutions.ewallet.enums.TransactionType.START_OF_DAY_FLOAT_IN);

		query.setParameter("profileId", profileId);
		query.setParameter("statusBankReq", TransactionStatus.BANK_REQUEST);
		query.setParameter("statusManResolve", TransactionStatus.MANUAL_RESOLVE);
		query.setParameter("statusCompl", TransactionStatus.COMPLETED);
		// query.setParameter("statusAWT", TransactionStatus.AWAITING_APPROVAL);
		// use null day end id
		results = (List<ProcessTransaction>) query.getResultList();
		LOG.debug("qu");
		return results;
	}
	
	@SuppressWarnings("unchecked")
	public List<ProcessTransaction> getProcessTransactionsByAllAttributes(UniversalProcessSearch universal) {
		List<ProcessTransaction> results = null;
		String qStr = "SELECT p ";
		String orderBy = "ORDER BY p.dateCreated DESC";
		try {

			Query query = this.getProcessTransactionsUniversalQuery(universal, qStr, orderBy);
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
	
	private Query getProcessTransactionsUniversalQuery(UniversalProcessSearch uni, String selectClause, String orderBy) throws Exception {
		Date fromDate = DateUtil.getBeginningOfDay(uni.getFromDate());
		Date toDate = DateUtil.getEndOfDay(uni.getToDate());
		Query results = null;
		TransactionStatus status = uni.getStatus();
		zw.co.esolutions.ewallet.enums.TransactionType txnType = uni.getTxnType();
		TxnFamily txnFamily = uni.getTxnFamily();
		String tellerId = uni.getTellerId();
		String bankId = uni.getBankId();
		String branchId = uni.getBranchId();
		String messageId = uni.getMessageId();
		String oldMessageId = uni.getOldMessageId();
		String utilityAccount = uni.getUtilityAccount();
		String sourceMobile = uni.getSourceMobile();
		String targetMobile = uni.getTargetMobile();
		String customerId = uni.getCustomerId();

		String fromEwalletAccount = uni.getFromEwalletAccount();
		String toEwalletAccount = uni.getToEwalletAccount();
		String sourceEQ3AccountNumber = uni.getSourceEQ3AccountNumber();
		String destinationEQ3AccountNumber = uni.getDestinationEQ3AccountNumber();

		boolean isManualResolve = uni.isManualResolve();
		String qStr = selectClause + "FROM ProcessTransaction p WHERE p.dateCreated IS NOT NULL ";

		try {
			LOG.debug(">>>>>>>>>>>>>> tellerId = " + tellerId + " bankId = " + bankId + " branchId = " + branchId + " txnType = " + txnType + " fromDate = " + fromDate + " toDate = " + toDate + " txnFamily = " + txnFamily + " status = " + status + " messageId = " + messageId + " oldMessageId = " + oldMessageId + " Manual Ressove = " + isManualResolve);

			if (destinationEQ3AccountNumber != null) {
				qStr = qStr + "AND p.destinationEQ3AccountNumber = :destinationEQ3AccountNumber";
			}
			if (sourceEQ3AccountNumber != null) {
				qStr = qStr + "AND p.sourceEQ3AccountNumber = :sourceEQ3AccountNumber";
			}
			/*
			 * if(toEwalletAccount != null) { qStr = qStr +
			 * "AND p.toEwalletAccount = :toEwalletAccount"; }
			 * if(fromEwalletAccount != null) { qStr = qStr +
			 * "AND p.fromEwalletAccount = :fromEwalletAccount"; }
			 */
			if (fromDate != null && toDate != null) {
				qStr = qStr + "AND p.dateCreated >= :fromDate AND p.dateCreated <= :toDate ";
			}
			if (messageId != null) {
				qStr = qStr + "AND (p.messageId LIKE :messageId OR p.id LIKE :messageId) ";
			}
			if (oldMessageId != null) {
				qStr = qStr + "AND p.oldMessageId LIKE :oldMessageId ";
			}
			if (tellerId != null) {
				qStr = qStr + "AND p.profileId = :profileId ";
			}
			if (bankId != null) {
				qStr = qStr + "AND p.fromBankId = :fromBankId ";
			}
			if (branchId != null) {
				qStr = qStr + "AND p.transactionLocationId = :branchId ";
			}
			if (status != null) {
				qStr = qStr + "AND p.status = :status ";
			}
			if (txnType != null) {
				qStr = qStr + "AND p.transactionType = :transactionType ";
			}
			if (customerId != null) {
				qStr = qStr + "AND p.customerId = :customerId ";
			}

			if (isManualResolve) {
				qStr = qStr + "AND (" +

				// Withdrawals
				"p.transactionType = :WITHDRAWALp " + "OR p.transactionType = :WITHDRAWAL_NONHOLDERp " +

				// Top up
				"OR p.transactionType = :TOPUP " + "OR p.transactionType = :EWALLET_TOPUP " +

				// Bill Pay
				"OR p.transactionType = :BILLPAY " + "OR p.transactionType = :EWALLET_BILLPAY " +

				// Day End
				"OR p.transactionType = :DAYEND_CASH_TENDERED " + "OR p.transactionType = :DAYEND_FLOATS " + "OR p.transactionType = :DAYEND_OVERPOST " + "OR p.transactionType = :DAYEND_PAYOUTS " + "OR p.transactionType = :DAYEND_RECEIPTS " + "OR p.transactionType = :DAYEND_UNDERPOST " +

				// Deposits
				"OR p.transactionType = :DEPOSIT " +

				// Agents
				"OR p.transactionType = :AGENT_CUSTOMER_DEPOSIT " + "OR p.transactionType = :AGENT_CUSTOMER_WITHDRAWAL " + "OR p.transactionType = :AGENT_EMONEY_TRANSFER " +

				// Transfers
				"OR p.transactionType = :EWALLET_TO_BANKACCOUNT_TRANSFERp " + "OR p.transactionType = :EWALLET_TO_EWALLET_TRANSFERp " + "OR p.transactionType = :EWALLET_TO_NON_HOLDER_TRANSFERp " + "OR p.transactionType = :BANKACCOUNT_TO_BANKACCOUNT_TRANSFERp " + "OR p.transactionType = :BANKACCOUNT_TO_EWALLET_TRANSFERp " + "OR p.transactionType = :BANKACCOUNT_TO_NONHOLDER_TRANSFERp) ";
			}
			qStr = qStr + orderBy;
			// LOG.debug(qStr);
			Query query = em.createQuery(qStr);

			if (isManualResolve) {

				// Withdrawals
				query.setParameter("WITHDRAWALp", zw.co.esolutions.ewallet.enums.TransactionType.WITHDRAWAL);
				query.setParameter("WITHDRAWAL_NONHOLDERp", zw.co.esolutions.ewallet.enums.TransactionType.WITHDRAWAL_NONHOLDER);

				// Transfers
				query.setParameter("EWALLET_TO_BANKACCOUNT_TRANSFERp", zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER);
				query.setParameter("EWALLET_TO_EWALLET_TRANSFERp", zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TO_EWALLET_TRANSFER);
				query.setParameter("EWALLET_TO_NON_HOLDER_TRANSFERp", zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER);
				query.setParameter("BANKACCOUNT_TO_BANKACCOUNT_TRANSFERp", zw.co.esolutions.ewallet.enums.TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER);
				query.setParameter("BANKACCOUNT_TO_EWALLET_TRANSFERp", zw.co.esolutions.ewallet.enums.TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER);
				query.setParameter("BANKACCOUNT_TO_NONHOLDER_TRANSFERp", zw.co.esolutions.ewallet.enums.TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER);

				// Top Up
				query.setParameter("TOPUP", zw.co.esolutions.ewallet.enums.TransactionType.TOPUP);
				query.setParameter("EWALLET_TOPUP", zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TOPUP);

				// Bill Pay
				query.setParameter("BILLPAY", zw.co.esolutions.ewallet.enums.TransactionType.BILLPAY);
				query.setParameter("EWALLET_BILLPAY", zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_BILLPAY);

				// Day End
				query.setParameter("DAYEND_CASH_TENDERED", zw.co.esolutions.ewallet.enums.TransactionType.DAYEND_CASH_TENDERED);
				query.setParameter("DAYEND_FLOATS", zw.co.esolutions.ewallet.enums.TransactionType.DAYEND_FLOATS);
				query.setParameter("DAYEND_OVERPOST", zw.co.esolutions.ewallet.enums.TransactionType.DAYEND_OVERPOST);
				query.setParameter("DAYEND_PAYOUTS", zw.co.esolutions.ewallet.enums.TransactionType.DAYEND_PAYOUTS);
				query.setParameter("DAYEND_RECEIPTS", zw.co.esolutions.ewallet.enums.TransactionType.DAYEND_RECEIPTS);
				query.setParameter("DAYEND_UNDERPOST", zw.co.esolutions.ewallet.enums.TransactionType.DAYEND_UNDERPOST);

				// Deposits
				query.setParameter("DEPOSIT", zw.co.esolutions.ewallet.enums.TransactionType.DEPOSIT);

				// Agents
				query.setParameter("AGENT_CUSTOMER_DEPOSIT", zw.co.esolutions.ewallet.enums.TransactionType.AGENT_CUSTOMER_DEPOSIT);
				query.setParameter("AGENT_CUSTOMER_WITHDRAWAL", zw.co.esolutions.ewallet.enums.TransactionType.AGENT_CUSTOMER_WITHDRAWAL);
				query.setParameter("AGENT_EMONEY_TRANSFER", zw.co.esolutions.ewallet.enums.TransactionType.AGENT_EMONEY_TRANSFER);

			}

			if (tellerId != null) {
				query.setParameter("profileId", tellerId);
			}
			if (status != null) {
				query.setParameter("status", status);
			}
			if (txnType != null) {
				query.setParameter("transactionType", txnType);
			}
			if (bankId != null) {
				query.setParameter("fromBankId", bankId);
			}
			if (branchId != null) {
				query.setParameter("branchId", branchId);
			}
			if (fromDate != null && toDate != null) {
				query.setParameter("fromDate", fromDate);
				query.setParameter("toDate", toDate);
			}
			if (messageId != null) {
				query.setParameter("messageId", "%" + messageId + "%");
			}
			if (oldMessageId != null) {
				query.setParameter("oldMessageId", "%" + oldMessageId + "%");
			}
			if (destinationEQ3AccountNumber != null) {
				query.setParameter("destinationEQ3AccountNumber", destinationEQ3AccountNumber);
			}
			if (sourceEQ3AccountNumber != null) {
				query.setParameter("sourceEQ3AccountNumber", sourceEQ3AccountNumber);
			}
			if (customerId != null) {
				query.setParameter("customerId", customerId);
			}

			results = query;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return results;
	}

	
	@SuppressWarnings("unchecked")
	public List<ProcessTransaction> getProcessTransactionsNotCompletedByOldMessageId(ManualPojo manual) {
		List<ProcessTransaction> txns = null;
		LOG.debug(">>>>>>>>>>In completeTransaction method Message Id = " + manual.getMessageId() + "   Status = " + manual.getStatus());
		try {
			Query query = em.createQuery("SELECT p FROM ProcessTransaction p WHERE p.messageId <> :messageId AND p.oldMessageId = :oldMessageId  AND p.status = :status");
			query.setParameter("messageId", manual.getMessageId());
			query.setParameter("oldMessageId", manual.getOldMessageId());
			query.setParameter("status", TransactionStatus.AWAITING_APPROVAL);
			txns = (List<ProcessTransaction>) query.getResultList();
		} catch (NoResultException no) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (txns == null || txns.isEmpty()) {
			txns = null;
		}
		return txns;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)	
	public void runDailyNHCollectionReversal(Date date,String recipient) {
		LOG.debug("In ProcessUtilImpl >>>>>>>>>>>>>>>>>>>>>>>>>>>> In runDailyNHCollectionReversal Method ");
		//search all txns with status awaiting collection 
//		TransactionStatus status = TransactionStatus.AWAITING_COLLECTION;
//		Query query = em.createNamedQuery("sampleTxn");
		List<ProcessTransaction> processTxns = getNonHolderTxfDueForReversal(date, TransactionStatus.AWAITING_COLLECTION);
		if(processTxns != null){
			LOG.debug("In ProcessUtilImpl >>>>>>>>>>>>>>>>>>>>>>>>>>>> Found expired Txns   "+processTxns.size());
			BankRequest req;
			
			LOG.debug("In ProcessUtilImpl >>>>>>>>>>>>>>>>>>>>>>>>>>>> Found expired Txns   "+processTxns);
			for(ProcessTransaction txn : processTxns){
				LOG.debug("In ProcessUtilImpl >>>>>>>>>>>>>>>>>>>>>>>>>>>> Found expired Txns  "+txn);
//			}
//				ProcessTransaction txn = processTxns.get(0);
				LOG.debug("In ProcessUtilImpl >>>>>>>>>>>>>>>>>>>>>>>>>>>> Found txn  "+txn.getMessageId());
				try {
					req = this.populateBankRequest(txn);
					req.setCommissions(null);
					req.setReversal(true);
					this.sendBankRequestToBank(req);
					txn.setCollectionTimeOut(true);
					txn = this.updateProcessTransaction(txn);
					this.promoteTxnState(txn, TransactionStatus.REVERSAL_REQUEST,"Sending reversal to EQ3");
					LOG.debug("In ProcessUtilImpl >>>>>>>>>>>>>>>>>>>>>>>>>>>> Updated original txn to Reversalrequest");
				} catch (Exception e) {
					double amnt = (txn.getAmount())/100;
					String subject = "Failed Expired Non Holder Reversal "+txn.getMessageId();
					String title = "Failed Expired Non Holder Reversal";
					String heading = " Reference    , Mobile Number , Amount($)";
					String txnInfo = " "+txn.getMessageId()+" , "+txn.getSourceMobile()+"  , "+amnt;
					this.sendReport(recipient, subject, title, txnInfo , heading);
					e.printStackTrace(System.out);
				}
			}
			
		}
		this.compileReport(processTxns, recipient);
		LOG.debug("Reversal Process finished >>>>>>>>>>>>>>>>>>>>>>>>>>>> In runDailyNHCollectionReversal Method ");
	}
	
	@SuppressWarnings("unchecked")
	private List<ProcessTransaction> getNonHolderTxfDueForReversal(Date date, TransactionStatus status ){
		List<ProcessTransaction> results = null;
		try {
			Query query = em.createNamedQuery("getNonHolderTxfDueForReversal");
			query.setParameter("date", date);
			query.setParameter("status", status);
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
	

	@Override
	public List<ProcessTransaction> getTimedOutTxns(Date date){
		List<ProcessTransaction> results = null;
		try {
			Query query = em.createNamedQuery("getExpiredTxnForReversal");
			query.setParameter("date", date);
			query.setParameter("status", TransactionStatus.BANK_REQUEST);
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
	
	private ChargePostingInfo populateEWalletChargePostingRequestReversal(TransactionCharge transactionCharge) {
		ChargePostingInfo request = new ChargePostingInfo();
		try {
			ProcessTransaction txn = transactionCharge.getProcessTransaction();
			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
			BankAccount source = bankService.getUniqueBankAccountByAccountNumber(transactionCharge.getFromEwalletAccount());
			BankAccount destinationAccount = bankService.getUniqueBankAccountByAccountNumber(transactionCharge.getToEwalletAccount());
			if(zw.co.esolutions.ewallet.enums.TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER.equals(txn.getTransactionType())) {
				if (!zw.co.esolutions.ewallet.enums.MobileNetworkOperator.ECONET.equals(NumberUtil.getMNO(txn.getTargetMobile()))) {
					source = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.BANK_TO_NON_MOBILE_CONTROL, OwnerType.BANK, null);
					destinationAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.TARIFFS_CONTROL, OwnerType.BANK, null);
				} else {
					source = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.BANK_TO_MOBILE_CONTROL, OwnerType.BANK, null);
					destinationAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.TARIFFS_CONTROL, OwnerType.BANK, null);
				}
			} else if(zw.co.esolutions.ewallet.enums.TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER.equals(txn.getTransactionType())) {
				source = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.BANK_TO_MOBILE_CONTROL, OwnerType.BANK, null);
				destinationAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.TARIFFS_CONTROL, OwnerType.BANK, null);
				
			}
			request.setAmount(transactionCharge.getTariffAmount());
			request.setOriginalTxnRef(txn.getId());
			request.setSourceAccountNumber(source.getAccountNumber());
			request.setSrcAccountId(source.getId());
			request.setTargetAccountId(destinationAccount.getId());
			request.setTargetAccountNumber(destinationAccount.getAccountNumber());
			request.setTxnCategory(TransactionCategory.CHARGE);
			request.setTxnRef(transactionCharge.getId());
			request.setSrcNarrative("Debit Account : "+request.getSourceAccountNumber());
			request.setTargetNarrative("Credit Account : "+request.getTargetAccountNumber());
			//Setting Merchant Narratives
			if(txn.getUtilityAccount() != null) {
				request.setSrcNarrative("Debit. Utitility Account : "+txn.getUtilityAccount()+", Mobile : "+txn.getSourceMobile());
				request.setTargetNarrative("Credit. Utitility Account : "+txn.getUtilityAccount()+", Mobile : "+txn.getSourceMobile());
			}
			// Book Entry Transactions
			TransactionType bookEntryTxnType = TransactionType.valueOf(transactionCharge.getTransactionType().toString());
			request.setTxnType(bookEntryTxnType);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return request;
	}
	
	public List<TransactionPostingInfo> populateTransactionPostingInfoReversal(ProcessTransaction txn){
		List<TransactionPostingInfo> infoList = new ArrayList<TransactionPostingInfo>();
		try {
			
			//Transactions Without EWallet Postings
			if(zw.co.esolutions.ewallet.enums.TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER.equals(txn.getTransactionType()) ||
					zw.co.esolutions.ewallet.enums.TransactionType.BILLPAY.equals(txn.getTransactionType()) || 
					zw.co.esolutions.ewallet.enums.TransactionType.TOPUP.equals(txn.getTransactionType())) {
				return null;
			}
			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
			TransactionPostingInfo txnPosting = new TransactionPostingInfo();
			BankAccount srcAccount = bankService.getUniqueBankAccountByAccountNumber(txn.getSourceAccountNumber());
			BankAccount destAccount = bankService.getUniqueBankAccountByAccountNumber(txn.getDestinationAccountNumber());
			if(zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER.equals(txn.getTransactionType())) {
				if (!zw.co.esolutions.ewallet.enums.MobileNetworkOperator.ECONET.equals(NumberUtil.getMNO(txn.getTargetMobile()))) {
					LOG.debug("NON ECONET NUMBER =  "+txn.getTargetMobile());
					destAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.PAYOUT_CONTROL, OwnerType.BANK, null);
				}
			} else if(zw.co.esolutions.ewallet.enums.TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER.equals(txn.getTransactionType())) {
				if (!zw.co.esolutions.ewallet.enums.MobileNetworkOperator.ECONET.equals(NumberUtil.getMNO(txn.getTargetMobile()))) {
					srcAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.BANK_TO_NON_MOBILE_CONTROL, OwnerType.BANK, null);
					destAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.PAYOUT_CONTROL, OwnerType.BANK, null);
					
				} else {
					srcAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.BANK_TO_MOBILE_CONTROL, OwnerType.BANK, null);
					destAccount = bankService.getUniqueBankAccountByAccountNumber(txn.getDestinationAccountNumber());
					
				}
			} else if(zw.co.esolutions.ewallet.enums.TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER.equals(txn.getTransactionType())) {
				srcAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.BANK_TO_MOBILE_CONTROL, OwnerType.BANK, null);
				destAccount = bankService.getUniqueBankAccountByAccountNumber(txn.getDestinationAccountNumber());
				
			} else if(zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TOPUP.equals(txn.getTransactionType())) {
				destAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.TOPUP_SUSPENSE_ACCOUNT, OwnerType.BANK, null);
						
			} else if(zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_BILLPAY.equals(txn.getTransactionType())) {
				destAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getBankMerchantId(), BankAccountType.MERCHANT_SUSPENSE, OwnerType.MERCHANT, null);
								
			} else if(zw.co.esolutions.ewallet.enums.TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER.equals(txn.getTransactionType())) {
				destAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.MOBILE_TO_BANK_CONTROL, OwnerType.BANK, null);
			
			}else if(zw.co.esolutions.ewallet.enums.TransactionType.AGENT_EWALLET_TO_BANKACCOUNT_TRANSFER.equals(txn.getTransactionType())) {
				destAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.MOBILE_TO_BANK_CONTROL, OwnerType.BANK, null);
			}
			txnPosting.setAmount(txn.getAmount());
			txnPosting.setSrcNarrative(txn.getNarrative());
			txnPosting.setTargetNarrative(txn.getNarrative());
			txnPosting.setOriginalTxnRef(txn.getMessageId());
			txnPosting.setSourceAccountNumber(txn.getSourceAccountNumber());
			txnPosting.setSrcAccountId(srcAccount.getId());
			txnPosting.setTargetAccountNumber(destAccount.getAccountNumber());
			txnPosting.setTargetAccountId(destAccount.getId());
			txnPosting.setTxnCategory(TransactionCategory.MAIN);
			txnPosting.setTxnType(zw.co.esolutions.ewallet.bankservices.service.TransactionType.valueOf(txn.getTransactionType().name()));
			txnPosting.setTxnRef(txn.getMessageId());
			
			txnPosting.setTransactionDate(DateUtil.convertToXMLGregorianCalendar(txn.getValueDate()));
			infoList.add(txnPosting);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return infoList;
	}
	
	public PostingsRequestWrapper populateReversalPostingsResponse(ProcessTransaction txn) {
		PostingsRequestWrapper ps = new PostingsRequestWrapper();
		List<TransactionPostingInfo> txnInfos = this.populateTransactionPostingInfoReversal(txn);
		LOG.debug(">>>>>>>>>>>>> Transaction Infos = "+txnInfos);
		List<TransactionCharge> txnCharges = this.getTransactionChargeByProcessTransactionId(txn.getId());
		if(txnCharges == null || txnCharges.isEmpty()){
			LOG.debug("No charges to post to EWALLET for txn with reference " + txn.getId());
		}else{
			List<ChargePostingInfo> chargeInfos = new ArrayList<ChargePostingInfo>();
			ChargePostingInfo txnChargeReq;
			for (TransactionCharge transactionCharge : txnCharges) {
				txnChargeReq = this.populateEWalletChargePostingRequestReversal(transactionCharge);
				chargeInfos.add(txnChargeReq);
				
			}
			ps.setChargePostingsInfos(chargeInfos);
			LOG.debug("DONE Putting charges for the txn " + txn.getId());
		}
		ps.setTransactionPostingInfos(txnInfos);
		
		return ps;
	}
	
	public ProcessTransaction handleAgentCustomerDepositRequest(RequestInfo info) throws Exception {
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();

		LOG.debug("TARGET MOBILE : " + info.getTargetMobile() + " BANK ID : " + info.getTargetBankId());
		MobileProfile targetMobileProfile = customerService.getMobileProfileByBankAndMobileNumber(info.getTargetBankId(), info.getTargetMobile());

		if (targetMobileProfile == null || targetMobileProfile.getId() == null) {
			throw new Exception("Error processing transfer request, invalid target mobile.");
		}

		BankAccount srcAcc = bankService.getUniqueBankAccountByAccountNumber(info.getSourceAccountNumber());

		if (srcAcc == null || srcAcc.getId() == null) {
			throw new Exception("Error processing transfer request, invalid source bank account.");
		}
		BankAccount destnAcc = bankService.getUniqueBankAccountByAccountNumber(info.getTargetMobile());

		if (destnAcc == null || destnAcc.getId() == null) {
			throw new Exception("Error processing transfer request, invalid source mobile.");
		}

		// Initialize the Process Txn Object
		info.setLocationType(TransactionLocationType.AGENT);
		ProcessTransaction processTxn = this.populateProcessTransaction(info);
		processTxn.setAmount(info.getAmount());
		processTxn.setTargetMobileId(targetMobileProfile.getId());
		processTxn.setBankReference(info.getBankCode());
		processTxn.setNarrative("Agent Customer Deposit");
		if (srcAcc.getBranch().getBank().getId().equals(destnAcc.getBranch().getBank().getId())) {
			processTxn.setTransferType(TransferType.INTRABANK);
		} else {
			processTxn.setTransferType(TransferType.INTERBANK);
		}
		processTxn.setFromBankId(srcAcc.getBranch().getBank().getId());
		processTxn.setToBankId(destnAcc.getBranch().getBank().getId());
		processTxn.setStatus(TransactionStatus.BANK_REQUEST);
		processTxn = this.createProcessTransaction(processTxn);
		if (processTxn == null || processTxn == null) {
			throw new Exception("Failed to create process txn.");
		}

		this.promoteTxnState(processTxn, TransactionStatus.DRAFT, TransactionStatus.DRAFT.toString());
		// Ignoring Generate passcode prompt algorithm
		// return this.processMobileTxnPasscodeRequest(processTxn);
		processTxn = this.findProcessTransactionById(processTxn.getId());
		return processTxn;
	}
	
	@Override
	public ProcessTransaction validateAgentCustomerDepositRequest(ProcessTransaction txn) throws Exception {
		LOG.debug("In validate " + txn.getTransactionType());
	//	txn = this.addApplicableTariffToProcessTxn(txn);
	//	LOG.debug("Added the tariff to the " + txn.getTransactionType());
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		BankAccount bankAccount = bankService.getBankAccountByAccountNumberAndOwnerType(txn.getSourceAccountNumber(), OwnerType.AGENT);
		LOG.debug("Going to check against limits");
		txn = this.checkTxnLimits(txn, bankAccount);
		LOG.debug("Check tcn limits returned " + txn.getResponseCode());
		if (!ResponseCode.E000.name().equalsIgnoreCase(txn.getResponseCode())) {
			return txn;
		}
		txn = this.checkAgentFundsAvailability(txn, bankAccount);
		LOG.debug("Done check Funds availability and got " + txn.getResponseCode());
		if (!ResponseCode.E000.name().equalsIgnoreCase(txn.getResponseCode())) {
			return txn;
		}
		BankAccount destBankAccount = bankService.getBankAccountByAccountNumberAndOwnerType(txn.getDestinationAccountNumber(), OwnerType.CUSTOMER);
		txn = this.checkDestinationBalanceLimits(txn, destBankAccount);
		LOG.debug("Done check destination account balance constrains " + txn.getResponseCode());
		
		LOG.debug("Adding applicable agent commission..");
		txn = this.addApplicableCommissionToProcessTxn(txn, zw.co.esolutions.ewallet.enums.TransactionType.COMMISSION_AGENT_DEPOSIT);
		LOG.debug("Agent commission added.. PROCEED");
		return txn;
	}
	
	@Override
	public ResponseCode postAgentCustomerDeposit(ProcessTransaction txn) throws Exception {
		try {
			LOG.debug("Processing Book Entry for AGENT CUSTOMER DEPOSIT Txn");
			if (txn != null) {
				if (!(txn.getResponseCode() == null || ResponseCode.E000.equals(ResponseCode.valueOf(txn.getResponseCode())))) {
					return ResponseCode.valueOf(txn.getResponseCode());
				}
			}
			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
			BankAccount source = bankService.getUniqueBankAccountByAccountNumberAndBankId(txn.getSourceAccountNumber(), txn.getFromBankId());
			BankAccount dest = bankService.getUniqueBankAccountByAccountNumberAndBankId(txn.getDestinationAccountNumber(), txn.getToBankId());
			BankAccount agentCommSuspense = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(source.getAccountHolderId(), BankAccountType.AGENT_COMMISSION_SUSPENSE, OwnerType.AGENT, null);
			BankAccount agentCommSource = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.AGENT_COMMISSION_SOURCE, OwnerType.BANK, null);

			
			if(source == null || source.getId() == null){
				throw new Exception("EWALLET Account could not be found  with account number " + txn.getSourceAccountNumber());
			} 
			if(dest == null || dest.getId() == null){
				throw new Exception("DEST EWALLET Account could not be found  with account number " + txn.getDestinationAccountNumber());
			}
			
			if(agentCommSuspense == null || agentCommSuspense.getId() == null){
				throw new Exception("AGENT COMM SUSPENSE Account could not be found");
			}
			
			if(agentCommSource == null || agentCommSource.getId() == null){
				throw new Exception("AGENT COMM SOURCE Account could not be found");
			}
			
			TransactionPostingInfo request = this.populateEWalletMainPostingRequest(txn, source, dest);
			List<TransactionPostingInfo> txnInfos = new ArrayList<TransactionPostingInfo>();
			txnInfos.add(request);
			List<ChargePostingInfo> chargeInfos = new ArrayList<ChargePostingInfo>();
			
			List<TransactionCharge> commissions = this.getTransactionChargeByProcessTransactionId(txn.getId());
			if(commissions == null || commissions.isEmpty()){
				LOG.debug("No charges to post to EWALLET for txn with reference " + txn.getId());
			}else{
				LOG.debug("Found " + commissions.size() + " charges for txn with ref " + txn.getId());
				ChargePostingInfo txnCommReq;
				for (TransactionCharge commission : commissions) {
					txnCommReq = this.populateEWalletChargePostingRequest(txn, commission, agentCommSource, agentCommSuspense);
					chargeInfos.add(txnCommReq);
					commission.setFromEwalletAccount(txnCommReq.getSourceAccountNumber());
					commission.setToEwalletAccount(txnCommReq.getTargetAccountNumber());
					commission = this.updateTransactionCharge(commission);
				}
				LOG.debug("DONE Posting charges for the txn " + txn.getId());
			}
			//DO POSTS
			GenerateKey.throwsEWalletPostingsException();
			EWalletPostingResponse response = bankService.postEWalletEntries(txnInfos, chargeInfos);
			LOG.debug("POSTING AGENT CUSTOMER DEPOSIT was successful");
			return ResponseCode.E000;
			
		} catch (Exception e) {
			
			this.rollbackEWalletAndEQ3Postings(txn);
			return ResponseCode.E836;

		}
	}
	
	@Override
	public ProcessTransaction handleAgentCustomerWithdrawalRequest(RequestInfo info) throws Exception {
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();

		LOG.debug("TARGET MOBILE : " + info.getTargetMobile() + " BANK ID : " + info.getTargetBankId());
		MobileProfile targetMobileProfile = customerService.getMobileProfileByBankAndMobileNumber(info.getTargetBankId(), info.getTargetMobile());

		if (targetMobileProfile == null || targetMobileProfile.getId() == null) {
			throw new Exception("Error processing agent withdrawal request, invalid target mobile.");
		}

		BankAccount srcAcc = bankService.getUniqueBankAccountByAccountNumberAndBankId(info.getSourceAccountNumber(), info.getSourceBankId());

		if (srcAcc == null || srcAcc.getId() == null) {
			throw new Exception("Error processing agent withdrawal request, invalid source bank account.");
		}
		BankAccount destnAcc = bankService.getUniqueBankAccountByAccountNumberAndBankId(info.getTargetMobile(), info.getTargetBankId());

		if (destnAcc == null || destnAcc.getId() == null) {
			throw new Exception("Error processing agent withdrawal request, invalid destination acc.");
		}

		// Initialize the Process Txn Object
		info.setLocationType(TransactionLocationType.AGENT);
		ProcessTransaction processTxn = this.populateProcessTransaction(info);
		processTxn.setAmount(info.getAmount());
		processTxn.setTargetMobileId(targetMobileProfile.getId());
		processTxn.setBankReference(info.getBankCode());
		processTxn.setNarrative("Agent Customer Withdrawal");
		if (srcAcc.getBranch().getBank().getId().equals(destnAcc.getBranch().getBank().getId())) {
			processTxn.setTransferType(TransferType.INTRABANK);
		} else {
			processTxn.setTransferType(TransferType.INTERBANK);
		}
		processTxn.setFromBankId(srcAcc.getBranch().getBank().getId());
		processTxn.setToBankId(destnAcc.getBranch().getBank().getId());
		processTxn.setStatus(TransactionStatus.BANK_REQUEST);
		processTxn = this.createProcessTransaction(processTxn);
		if (processTxn == null || processTxn == null) {
			throw new Exception("Failed to create process txn.");
		}

		this.promoteTxnState(processTxn, TransactionStatus.DRAFT, TransactionStatus.DRAFT.toString());
		// Ignoring Generate passcode prompt algorithm
		// return this.processMobileTxnPasscodeRequest(processTxn);
		processTxn = this.findProcessTransactionById(processTxn.getId());
		return processTxn;
	}
	
	@Override
	public ProcessTransaction validateAgentCustomerWithdrawalRequest(ProcessTransaction txn) throws Exception {
		LOG.debug("In validate " + txn.getTransactionType());
		txn = this.addApplicableTariffToProcessTxn(txn);
		LOG.debug("Added the tariff to the " + txn.getTransactionType());
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		BankAccount bankAccount = bankService.getBankAccountByAccountNumberAndOwnerType(txn.getSourceAccountNumber(), OwnerType.CUSTOMER);
		LOG.debug("Going to check against limits");
		txn = this.checkTxnLimits(txn, bankAccount);
		LOG.debug("Check tcn limits returned " + txn.getResponseCode());
		if (!ResponseCode.E000.name().equalsIgnoreCase(txn.getResponseCode())) {
			return txn;
		}
		txn = this.checkFundsAvailability(txn, bankAccount);
		LOG.debug("Done check Funds availability and got " + txn.getResponseCode());
		if (!ResponseCode.E000.name().equalsIgnoreCase(txn.getResponseCode())) {
			return txn;
		}
		BankAccount destBankAccount = bankService.getBankAccountByAccountNumberAndOwnerType(txn.getDestinationAccountNumber(), OwnerType.AGENT);
		txn = this.checkAgentDestinationBalanceLimits(txn, destBankAccount);
		LOG.debug("Done check destination account balance constrains " + txn.getResponseCode());
		
		LOG.debug("Adding applicable agent commission..");
		txn = this.addApplicableCommissionToProcessTxn(txn, zw.co.esolutions.ewallet.enums.TransactionType.COMMISSION_AGENT_WITHDRAWAL);
		LOG.debug("Agent commission added.. PROCEED");
		return txn;
	}
	
	@Override
	public ResponseCode postAgentCustomerWithdrawal(ProcessTransaction txn) throws Exception {
		try {
			LOG.debug("Processing Book Entry for AGENT CUSTOMER WITHDRAWAL Txn");
			if (txn != null) {
				if (!(txn.getResponseCode() == null || ResponseCode.E000.equals(ResponseCode.valueOf(txn.getResponseCode())))) {
					return ResponseCode.valueOf(txn.getResponseCode());
				}
			}
			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
			BankAccount source = bankService.getUniqueBankAccountByAccountNumberAndBankId(txn.getSourceAccountNumber(), txn.getFromBankId());
			BankAccount dest = bankService.getUniqueBankAccountByAccountNumberAndBankId(txn.getDestinationAccountNumber(), txn.getToBankId());
			BankAccount tariffAcc = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.TARIFFS_CONTROL, OwnerType.BANK, null);
			BankAccount agentCommSuspense = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(dest.getAccountHolderId(), BankAccountType.AGENT_COMMISSION_SUSPENSE, OwnerType.AGENT, null);
			BankAccount agentCommSource = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.AGENT_COMMISSION_SOURCE, OwnerType.BANK, null);

			
			if(source == null || source.getId() == null){
				throw new Exception("EWALLET Account could not be found  with account number " + txn.getSourceAccountNumber());
			} 
			if(dest == null || dest.getId() == null){
				throw new Exception("DEST EWALLET Account could not be found  with account number " + txn.getDestinationAccountNumber());
			}
			
			if(tariffAcc == null || tariffAcc.getId() == null){
				throw new Exception("TARIFFS CONTROL Account could not be found");
			}
			
			if(agentCommSuspense == null || agentCommSuspense.getId() == null){
				throw new Exception("AGENT COMM SUSPENSE Account could not be found");
			}
			
			if(agentCommSource == null || agentCommSource.getId() == null){
				throw new Exception("AGENT COMM SOURCE Account could not be found");
			}
			
			TransactionPostingInfo request = this.populateEWalletMainPostingRequest(txn, source, dest);
			List<TransactionPostingInfo> txnInfos = new ArrayList<TransactionPostingInfo>();
			txnInfos.add(request);
			List<ChargePostingInfo> chargeInfos = new ArrayList<ChargePostingInfo>();
			
			List<TransactionCharge> commissions = this.getTransactionChargeByProcessTransactionId(txn.getId());
			if(commissions == null || commissions.isEmpty()){
				LOG.debug("No charges to post to EWALLET for txn with reference " + txn.getId());
			}else{
				LOG.debug("Found " + commissions.size() + " charges for txn with ref " + txn.getId());
				ChargePostingInfo txnCommReq;
				for (TransactionCharge commission : commissions) {
					if (zw.co.esolutions.ewallet.enums.TransactionType.COMMISSION.equals(commission.getTransactionType())) {
						LOG.debug("Its a Commission..populate it");
						txnCommReq = this.populateEWalletChargePostingRequest(txn, commission, agentCommSource, agentCommSuspense);
					} else {
						LOG.debug("Not a commission, Must be a Tariff..populate it");
						txnCommReq = this.populateEWalletChargePostingRequest(txn, commission, source, tariffAcc); 
					}
					chargeInfos.add(txnCommReq);
					commission.setFromEwalletAccount(txnCommReq.getSourceAccountNumber());
					commission.setToEwalletAccount(txnCommReq.getTargetAccountNumber());
					commission = this.updateTransactionCharge(commission);
				}
				LOG.debug("DONE Posting charges for the txn " + txn.getId());
			}
			//DO POSTS
			GenerateKey.throwsEWalletPostingsException();
			EWalletPostingResponse response = bankService.postEWalletEntries(txnInfos, chargeInfos);
			LOG.debug("POSTING AGENT CUSTOMER WITHDRAWAL was successful");
			return ResponseCode.E000;
			
		} catch (Exception e) {
			
			this.rollbackEWalletAndEQ3Postings(txn);
			return ResponseCode.E836;

		}
	}
	
	@Override
	public ProcessTransaction handleAgentCustomerNonHolderWithdrawalRequest(RequestInfo info, ProcessTransaction processTxn) throws Exception {
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();

		LOG.debug("Finding original nonholder transfer...");
		ProcessTransaction originalTransfer = this.getProcessTransactionByMessageId(info.getOldReference());
		
		BankAccount srcAcc = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(info.getSourceBankId(), BankAccountType.PAYOUT_CONTROL, OwnerType.BANK, null);

		if (srcAcc == null || srcAcc.getId() == null) {
			throw new Exception("Error processing agent nonholder withdrawal request, invalid source bank account.");
		}
		//target account is the source agent mobile
		BankAccount destnAcc = bankService.getUniqueBankAccountByAccountNumberAndBankId(info.getSourceMobile(), info.getSourceBankId());

		if (destnAcc == null || destnAcc.getId() == null) {
			throw new Exception("Error processing agent nonholder withdrawal request, invalid destination acc.");
		}

		// Initialize the Process Txn Object
		info.setLocationType(TransactionLocationType.AGENT);
		LOG.debug("Finished populating.. add amount, oldRef and targetMobile params..");
		processTxn.setAmount(info.getAmount());
		processTxn.setOldMessageId(info.getOldReference());
		processTxn.setTargetMobile(originalTransfer.getTargetMobile());
		LOG.debug("Parameters added.. PROCEED");
		
		processTxn.setSourceAccountNumber(srcAcc.getAccountNumber());
		processTxn.setDestinationAccountNumber(destnAcc.getAccountNumber());
		processTxn.setBankReference(info.getBankCode());
		processTxn.setNarrative("Agent Customer NonHolder Withdrawal");
		if (info.getSourceBankId().equals(destnAcc.getBranch().getBank().getId())) {
			processTxn.setTransferType(TransferType.INTRABANK);
		} else {
			processTxn.setTransferType(TransferType.INTERBANK);
		}
		processTxn.setFromBankId(info.getSourceBankId());
		processTxn.setStatus(TransactionStatus.BANK_REQUEST);

		if (processTxn == null || processTxn == null) {
			throw new Exception("Failed to create process txn.");
		}
		
		LOG.debug("Updating txn..");
		processTxn = this.updateProcessTransaction(processTxn);
		LOG.debug("Updated successfully.. promoting to status DRAFT");
		processTxn = this.promoteTxnState(processTxn, TransactionStatus.DRAFT, TransactionStatus.DRAFT.toString());
		LOG.debug("Promoted.. continue");
		
		return processTxn;  
	}
	
	@Override
	public ProcessTransaction validateAgentCustomerNonHolderWithdrawalRequest(ProcessTransaction txn) throws Exception {
		LOG.debug("In validate " + txn.getTransactionType());

		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();

		BankAccount destBankAccount = bankService.getBankAccountByAccountNumberAndOwnerType(txn.getDestinationAccountNumber(), OwnerType.AGENT);
		txn = this.checkAgentDestinationBalanceLimits(txn, destBankAccount);
		LOG.debug("Done check destination account balance constrains " + txn.getResponseCode());
		
		LOG.debug("Adding applicable agent commission..");
		txn = this.addApplicableCommissionToProcessTxn(txn, zw.co.esolutions.ewallet.enums.TransactionType.COMMISSION_AGENT_WITHDRAWAL);
		LOG.debug("Agent commission added.. PROCEED");
		
		txn.setResponseCode(ResponseCode.E000.name());
		
		LOG.debug("Returning...");
		
		return txn;
	}
	
	@Override
	public ResponseCode postAgentCustomerNonHolderWithdrawal(ProcessTransaction txn) throws Exception {
		try {
			LOG.debug("Processing Book Entry for AGENT CUSTOMER NONHOLDER WITHDRAWAL Txn");
			if (txn != null) {
				if (!(txn.getResponseCode() == null || ResponseCode.E000.equals(ResponseCode.valueOf(txn.getResponseCode())))) {
					return ResponseCode.valueOf(txn.getResponseCode());
				}
			}
			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
			BankAccount source = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.PAYOUT_CONTROL, OwnerType.BANK, null);
			BankAccount dest = bankService.getUniqueBankAccountByAccountNumberAndBankId(txn.getDestinationAccountNumber(), txn.getFromBankId());
			BankAccount agentCommSuspense = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(dest.getAccountHolderId(), BankAccountType.AGENT_COMMISSION_SUSPENSE, OwnerType.AGENT, null);
			BankAccount agentCommSource = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.AGENT_COMMISSION_SOURCE, OwnerType.BANK, null);

			
			if(source == null || source.getId() == null){
				throw new Exception("EWALLET Account could not be found  with account number " + txn.getSourceAccountNumber());
			} 
			if(dest == null || dest.getId() == null){
				throw new Exception("DEST EWALLET Account could not be found  with account number " + txn.getDestinationAccountNumber());
			}
			
			if(agentCommSuspense == null || agentCommSuspense.getId() == null){
				throw new Exception("AGENT COMM SUSPENSE Account could not be found");
			}
			
			if(agentCommSource == null || agentCommSource.getId() == null){
				throw new Exception("AGENT COMM SOURCE Account could not be found");
			}
			
			TransactionPostingInfo request = this.populateEWalletMainPostingRequest(txn, source, dest);
			List<TransactionPostingInfo> txnInfos = new ArrayList<TransactionPostingInfo>();
			txnInfos.add(request);
			List<ChargePostingInfo> chargeInfos = new ArrayList<ChargePostingInfo>();
			
			List<TransactionCharge> commissions = this.getTransactionChargeByProcessTransactionId(txn.getId());
			if(commissions == null || commissions.isEmpty()){
				LOG.debug("No charges to post to EWALLET for txn with reference " + txn.getId());
			}else{
				LOG.debug("Found " + commissions.size() + " charges for txn with ref " + txn.getId());
				ChargePostingInfo txnCommReq;
				for (TransactionCharge commission : commissions) {
					txnCommReq = this.populateEWalletChargePostingRequest(txn, commission, agentCommSource, agentCommSuspense);
					chargeInfos.add(txnCommReq);
					commission.setFromEwalletAccount(txnCommReq.getSourceAccountNumber());
					commission.setToEwalletAccount(txnCommReq.getTargetAccountNumber());
					commission = this.updateTransactionCharge(commission);
				}
				LOG.debug("DONE Posting charges for the txn " + txn.getId());
			}
			//DO POSTS
			GenerateKey.throwsEWalletPostingsException();
			EWalletPostingResponse response = bankService.postEWalletEntries(txnInfos, chargeInfos);
			LOG.debug("POSTING AGENT CUSTOMER NONHOLDER WITHDRAWAL was successful");
			return ResponseCode.E000;
			
		} catch (Exception e) {
			
			this.rollbackEWalletAndEQ3Postings(txn);
			return ResponseCode.E836;

		}
	}

	@Override
	public ResponseCode postEWalletAgentDepositTxn(ProcessTransaction txn)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	private ProcessTransaction checkAgentFundsAvailability(ProcessTransaction txn, BankAccount bankAccount) throws Exception {
		LimitServiceSOAPProxy limitService = new LimitServiceSOAPProxy();
		Date txnDate = new Date(System.currentTimeMillis());
		Limit sourceBalLimit = limitService.getValidLimitOnDateByBankId(zw.co.esolutions.ewallet.limitservices.service.TransactionType.AGENT_ACCOUNT_BALANCE, BankAccountClass.valueOf(bankAccount.getAccountClass().toString()), DateUtil.convertToXMLGregorianCalendar(txnDate), LimitPeriodType.TRANSACTION, txn.getFromBankId());
		long transferAmount = this.getTotalTransactionAmount(txn);
		double balanceAfter = bankAccount.getRunningBalance() - transferAmount;
		if (sourceBalLimit != null && sourceBalLimit.getId() != null) {
			if (sourceBalLimit.getMinValue() > balanceAfter) {
				txn.setResponseCode(ResponseCode.E808.name());
				txn.setNarrative("Insufficient funds. The transaction will push your account balance below minimum");
				return txn;
			} else {
				txn.setResponseCode(ResponseCode.E000.name());
				txn.setNarrative("Funds availability validation successful.");
				return txn;
			}
		} else {
			if (0 > balanceAfter) {
				txn.setResponseCode(ResponseCode.E808.name());
				txn.setNarrative("Insufficient funds. The transaction will push your account balance below minimum");
				return txn;
			} else {
				txn.setResponseCode(ResponseCode.E000.name());
				txn.setNarrative("Funds availability validation successful.");
				return txn;
			}
		}
	}
	
	private ProcessTransaction checkAgentDestinationBalanceLimits(ProcessTransaction txn, BankAccount bankAccount) throws Exception {

		LimitServiceSOAPProxy limitService = new LimitServiceSOAPProxy();
		Date txnDate = new Date(System.currentTimeMillis());
		Limit destnBalLimit = limitService.getValidLimitOnDateByBankId(zw.co.esolutions.ewallet.limitservices.service.TransactionType.AGENT_ACCOUNT_BALANCE, BankAccountClass.valueOf(bankAccount.getAccountClass().toString()), DateUtil.convertToXMLGregorianCalendar(txnDate), LimitPeriodType.TRANSACTION, txn.getToBankId());
		long transferAmount = this.getTotalTransactionAmount(txn);
		double balanceAfter = bankAccount.getRunningBalance() + transferAmount;
		if (destnBalLimit != null && destnBalLimit.getId() != null) {
			if (destnBalLimit.getMaxValue() < balanceAfter) {
				txn.setResponseCode(ResponseCode.E808.name());
				txn.setNarrative("The transaction will push beneficiary account balance above maximum");
				return txn;
			} else {
				txn.setResponseCode(ResponseCode.E000.name());
				txn.setNarrative("Limits validation successful.");
				return txn;
			}
		} else {
			txn.setResponseCode(ResponseCode.E000.name());
			txn.setNarrative("Balance limit not defined.");
			return txn;
		}
	}
	
	public ProcessResponse validateOriginalNonHolderTransfer(RequestInfo info) {
		
		ProcessResponse processResponse = new ProcessResponse();
		
		LOG.debug("Finding original nonholder transfer...");
		
		try {
			ProcessTransaction originalTransfer = this.getProcessTransactionByMessageId(info.getOldReference());
			
			if (originalTransfer == null) {
				processResponse.setResponseCode(ResponseCode.E505.name());
				processResponse.setNarrative("NonHolder transfer with this ref does not exist");
			}
			
			LOG.debug("Original nonholder txf found...");
			
			if (TransactionStatus.AWAITING_COLLECTION.equals(originalTransfer.getStatus())) {
				
				LOG.debug("Transfer is in AWAITING_COLLECTION status.. PROCEED");
				
				if (originalTransfer.getAmount() == info.getAmount()) {
				
					LOG.debug("Amounts are equal.. PROCEED");
					
					if (originalTransfer.getSecretCode().equalsIgnoreCase(info.getSecretCode())) {
					
						LOG.debug("Secret code matches.. PROCEED");
						processResponse.setResponseCode(ResponseCode.E000.name());
						processResponse.setNarrative("Success");
						
					} else {
						
						LOG.debug("Secret code does not match.. REJECT");
						processResponse.setResponseCode(ResponseCode.E505.name());
						processResponse.setNarrative("NonHolder withdrawal failed. Please verify your reference and secret code.");
						
					}
				
				} else {
				
					processResponse.setResponseCode(ResponseCode.E505.name());
					processResponse.setNarrative("Sorry. Transfer with this amount not found. One of your parameters may be incorrect.");
				
				}
				
			} else if (TransactionStatus.FAILED.equals(originalTransfer.getStatus())) {
			
				processResponse.setResponseCode(ResponseCode.E505.name());
				processResponse.setNarrative("Sorry. The original transfer transaction failed.");
			
			} else if (TransactionStatus.COMPLETED.equals(originalTransfer.getStatus())) {
			
				processResponse.setResponseCode(ResponseCode.E505.name());
				processResponse.setNarrative("Sorry. This transfer was withdrawn already.");
				
			} else {
				
				processResponse.setResponseCode(ResponseCode.E505.name());
				processResponse.setNarrative("Sorry. Transfer transaction is in an invalid state. Try again later.");
	
			}
	
		} catch (Exception e) {
			
			processResponse.setResponseCode(ResponseCode.E505.name());
			processResponse.setNarrative("NonHolder transfer with this ref was not found.");
			
		}
		
		LOG.debug("Return NonHolder Transfer Validation ResponseCode: " + processResponse.getResponseCode() + "   " + processResponse.getNarrative());
		
		return processResponse;
	}
	
	@Override
	public ProcessTransaction validateAgentEwalletAccountToBankAccountTransferReq(ProcessTransaction txn) throws Exception {
		LOG.debug("In validate Agent eWallet to bank account request");
		
		txn = this.checkAgentEwalletToBankAccountEligibility(txn);
		if(!ResponseCode.E000.name().equalsIgnoreCase(txn.getResponseCode())) {
			return txn;
		}
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		BankAccount bankAccount = bankService.getBankAccountByAccountNumberAndOwnerType(txn.getSourceAccountNumber(), OwnerType.AGENT);
		LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>> Source account for agent = "+bankAccount);
		txn = this.checkTxnLimits(txn, bankAccount);
		LOG.debug("Check tcn limits returned " + txn.getResponseCode());
		if (!ResponseCode.E000.name().equalsIgnoreCase(txn.getResponseCode())) {
			return txn;
		}
		
		txn = this.checkAgentFundsAvailability(txn, bankAccount);
		LOG.debug("Done check Funds availability and got " + txn.getResponseCode());
		LOG.debug("Now returning from validate method.");
		return txn;
	}
	
	private ProcessTransaction checkAgentEwalletToBankAccountEligibility(ProcessTransaction txn) throws Exception {
		CustomerServiceSOAPProxy cs = new CustomerServiceSOAPProxy();
		AgentServiceSOAPProxy as = new AgentServiceSOAPProxy();
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		MobileProfile agentMobile = cs.findMobileProfileById(txn.getSourceMobileId());
		if(agentMobile != null && agentMobile.getId() != null) {
			Customer customer = agentMobile.getCustomer();
			
			Agent agent = as.getAgentByCustomerId(agentMobile.getCustomer().getId());
			
			if(agent != null && agent.getId() != null) {
				BankAccount acc = bankService.getUniqueBankAccountByAccountNumber(txn.getDestinationAccountNumber());
				if(acc == null || acc.getId() == null) {
					LOG.debug("Agent Bank Account Not Registered = "+txn.getDestinationAccountNumber());
					txn.setNarrative(ResponseCode.E779.getDescription());
					txn.setResponseCode(ResponseCode.E779.name());
					return txn;
					
				} else {
					if(customer.getId().equalsIgnoreCase(acc.getAccountHolderId())) {
						LOG.debug("Target Bank Account belongs to this agent "+acc.getAccountNumber());
						txn.setNarrative("Accounts Validation Successful.");
						txn.setResponseCode(ResponseCode.E000.name());
						return txn;
					} else {
						LOG.debug("Target bank account doesn't belong to this agent");
						txn.setNarrative(ResponseCode.E778.getDescription());
						txn.setResponseCode(ResponseCode.E778.name());
						return txn;
					}
				}
			} else {
				LOG.debug("Agent Not Found");
				txn.setNarrative("Agent Not Found");
				txn.setResponseCode(ResponseCode.E505.name());
				return txn;
			}
		} else {
			LOG.debug("Agent Mobile not found....");
			txn.setNarrative("Agent Mobile not found.");
			txn.setResponseCode(ResponseCode.E505.name());
			return txn;
		}
		
	}
	
	public ResponseCode postAgentEWalletAccountToBankAccountTransfer(ProcessTransaction txn) throws Exception {

		 try { if (txn != null) {
				if (!(txn.getResponseCode() == null || ResponseCode.E000.equals(ResponseCode.valueOf(txn.getResponseCode())))) {
					return ResponseCode.valueOf(txn.getResponseCode());
				}
			}
			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
			BankAccount source = bankService.getUniqueBankAccountByAccountNumber(txn.getSourceAccountNumber());
			
			BankAccount dest = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.MOBILE_TO_BANK_CONTROL, OwnerType.BANK, null);
			BankAccount tariffAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(txn.getFromBankId(), BankAccountType.TARIFFS_CONTROL, OwnerType.BANK, null);
			
			if(source == null || source.getId() == null){
				throw new Exception("EWALLET Account could not be found with account number " + txn.getDestinationAccountNumber());
			}
			
			if(dest == null || dest.getId() == null){
				throw new Exception("MOBILE TO BANK CONTROL Account could not be found.");
			} 
			
			if(tariffAccount == null || tariffAccount.getId() == null){
				throw new Exception("TARIFF CONTROL Account could not be found");
			}
			
			TransactionPostingInfo request = this.populateEWalletMainPostingRequest(txn, source, dest);
			List<TransactionPostingInfo> txnInfos = new ArrayList<TransactionPostingInfo>();
			txnInfos.add(request);
			List<ChargePostingInfo> chargeInfos = new ArrayList<ChargePostingInfo>();
			
			List<TransactionCharge> txnCharges = this.getTransactionChargeByProcessTransactionId(txn.getId());
			if(txnCharges == null || txnCharges.isEmpty()){
				LOG.debug("No charges to post to EWALLET for txn with reference " + txn.getId());
			}else{
				LOG.debug("Found " + txnCharges.size() + " charges for txn with ref " + txn.getId());
				ChargePostingInfo txnChargeReq;
				for (TransactionCharge transactionCharge : txnCharges) {
					txnChargeReq = this.populateEWalletChargePostingRequest(txn, transactionCharge, source, tariffAccount);
					chargeInfos.add(txnChargeReq);
					transactionCharge.setFromEwalletAccount(txnChargeReq.getSourceAccountNumber());
					transactionCharge.setToEwalletAccount(txnChargeReq.getTargetAccountNumber());
					transactionCharge = this.updateTransactionCharge(transactionCharge);
				}
				LOG.debug("DONE Posting charges for the txn " + txn.getId());
			}
			GenerateKey.throwsEWalletPostingsException();
			EWalletPostingResponse response = bankService.postEWalletEntries(txnInfos, chargeInfos);
				LOG.debug("POSTING EWT TO BA was successful");
				return ResponseCode.E000;
			} catch (Exception e1) {
				
				this.rollbackEWalletAndEQ3Postings(txn);
				return ResponseCode.E836;
			}
		}
	
	public long getTariffAmount(Tariff tariff, long amount) throws Exception {
		long value = 0;
		if (tariff != null && tariff.getId() != null) {
			LOG.debug("Operating on tariff : " + tariff);
			if (tariff.getTariffTable().getTariffType().equals(TariffType.FIXED_AMOUNT) || tariff.getTariffTable().getTariffType().equals(TariffType.SCALED)) {
				
				value = tariff.getValue();
				LOG.debug(">>>.>>>>>>>>>>> Tariff "+tariff.getTariffTable().getTariffType()+" ABSOLUTE VALUE, Amount = "+value);
				
			} else if(tariff.getTariffTable().getTariffType().equals(TariffType.FIXED_PERCENTAGE) ||
					tariff.getTariffTable().getTariffType().equals(TariffType.SCALED_PERCENTAGE) ){
				
				LOG.debug(">>>.>>>>>>>>>>> Tariff "+tariff.getTariffTable().getTariffType()+" PERCENTAGE Tariff Percentage = "+tariff.getValue());
				value = (tariff.getValue() * amount) / EWalletConstants.PERCENTAGE_DIVISOR;//Tariff Percentage value is stored as a product of 100
				LOG.debug(">>>.>>>>>>>>>>> Tariff "+tariff.getTariffTable().getTariffType()+" IS PERCENTAGE, Amount = "+value);
				
			} else if(tariff.getTariffTable().getTariffType().equals(TariffType.PERCENTAGE_PLUS_LIMITS)) {
				
				value = (tariff.getValue() * amount) / EWalletConstants.PERCENTAGE_DIVISOR;
				
				if(value < tariff.getLowerLimit()) {
					value = tariff.getLowerLimit();
					LOG.debug(">>>.>>>>>>>>>>> Tariff "+tariff.getTariffTable().getTariffType()+" Value less than lower limit, Amount = "+value);
				} else if(value > tariff.getUpperLimit()) {
					value = tariff.getUpperLimit();
					LOG.debug(">>>.>>>>>>>>>>> Tariff "+tariff.getTariffTable().getTariffType()+" Value Greater than upper Limit, Default Amount = "+value);
				} else {
					LOG.debug(">>>.>>>>>>>>>>> Tariff "+tariff.getTariffTable().getTariffType()+" IN ESLSE, Amount = "+value);
				}
				
			}

		} else {
			throw new Exception("Null Tariff");
		}
		return value;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void runAgentCommsionSweep(String recipient) {
		
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		
		LOG.debug("Running Agent Commission Sweep >>>>>>>>>>>>>>>>");
		//search all account balances with a value greater than zero 
		List<Agent> agentList = agentService.getAgentByStatus(ProfileStatus.ACTIVE.name());
		List<String> successList = new ArrayList<String>();
//		Agent a = agentService.getAgentByAgentNumber("600044");
//		List<Agent> agentList = new ArrayList<Agent>();
//		agentList.add(a);
		if(agentList != null){
			BankRequest req;
			ProcessTransaction txn = null;
			
			
			LOG.debug("In ProcessUtilImpl >>>>>>>>>>>>>>>>>>>>>>>>>>>> Agents >>>   "+agentList.size());
			for(Agent agent : agentList){
				
				try {
					
					txn = this.populateAgentCommissionSweep(agent);
					if(txn == null){
						LOG.debug("Account balance is 0 ignore txn ");
						continue;
					}
					req = this.populateBankRequest(txn);
					txn.setAgentNumber(agent.getAgentNumber());
					txn = this.createProcessTransaction(txn);
					LOG.debug("Done createProcess txn..");
					this.promoteTxnState(txn, TransactionStatus.DRAFT, TransactionStatus.DRAFT.name());
					LOG.debug("Done promoting txn state...");
					
					req = this.populateBankRequest(txn);
					req.setAmount(txn.getAmount());
					req.setSourceBankCode(txn.getBankReference());
					req.setSourceMobileNumber(txn.getSourceAccountNumber());
					req.setTransactionType(zw.co.esolutions.ewallet.enums.TransactionType.COMMISSION_SWEEPING);
					req.setCurrencyISOCode("USD");
					req.setAgentNumber(agent.getAgentNumber());
//					throw new Exception();
					this.sendBankRequestToBank(req);

					this.promoteTxnState(txn, TransactionStatus.BANK_REQUEST, TransactionStatus.BANK_REQUEST.name());
					double amnt = (txn.getAmount())/100.00;
					successList.add(txn.getId()+"    ,"+txn.getSourceMobile()+"  ,"+amnt);
				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					double amnt = (txn.getAmount())/100;
					String subject = "Failed Commission Sweep "+txn.getMessageId();
					String title = "Failed Agent Commission Sweep";
					String heading = " Reference      Mobile Number  Amount($)";
					String txnInfo = " "+txn.getMessageId()+" ,   "+txn.getSourceMobile()+"  ,   "+txn.getAgentNumber()+"       , "+amnt;
					this.sendReport(recipient, subject, title, txnInfo , heading);
					e.printStackTrace(System.out);
					return;
				}
			}
			this.sendCommissionSweep(successList, recipient);
			
		}
		LOG.debug("Commission Sweep Process finished >>>>>>>>>>>>>>>>>>>>>>>>>>>> In runAgentSweepProcess Method ");
	}
	
	public ResponseCode postAgentCommissionSweep(ProcessTransaction txn) throws Exception {
		try {
			if (txn != null) {
				if (!(txn.getResponseCode() == null || ResponseCode.E000.equals(ResponseCode.valueOf(txn.getResponseCode())))) {
					return ResponseCode.valueOf(txn.getResponseCode());
				}
			}
			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
			BankAccount source = bankService.getUniqueBankAccountByAccountNumber(txn.getSourceAccountNumber());
			
			BankAccount dest = bankService.getUniqueBankAccountByAccountNumber(txn.getDestinationAccountNumber());

			LOG.debug("The dest account number is " +txn.getDestinationAccountNumber()+" The src account number "+txn.getSourceAccountNumber());
			if(source == null || source.getId() == null){
				throw new Exception("Source Account could not be found.");
			} 
			if(dest == null || dest.getId() == null){
				throw new Exception("Destination Account could not be found with account number " + txn.getDestinationAccountNumber());
			}
			
			TransactionPostingInfo request = this.populateEWalletMainPostingRequest(txn, source, dest);
			List<TransactionPostingInfo> txnInfos = new ArrayList<TransactionPostingInfo>();
			txnInfos.add(request);
			List<ChargePostingInfo> chargeInfos = new ArrayList<ChargePostingInfo>();
			
			List<TransactionCharge> txnCharges = this.getTransactionChargeByProcessTransactionId(txn.getId());
			if(txnCharges == null || txnCharges.isEmpty()){
				LOG.debug("No charges to post to EWALLET for txn with reference " + txn.getId());
			}else{
				LOG.debug("Found " + txnCharges.size() + " charges for txn with ref " + txn.getId());
			}
			//**********   BUG
			GenerateKey.throwsEWalletPostingsException();
			
			//DO POSTINGS
			EWalletPostingResponse response = bankService.postEWalletEntries(txnInfos, chargeInfos);			
			
			LOG.debug("POSTING BA TO NHL was successful");
			
			return ResponseCode.E000;
			
		} catch (Exception e) {
			
			this.rollbackEWalletAndEQ3Postings(txn);
			return ResponseCode.E836;
		}
	
	}
	
private ProcessTransaction populateAgentCommissionSweep(Agent agent) {
		
		ProcessTransaction processTxn = new ProcessTransaction();
		try{
			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
			CustomerServiceSOAPProxy customeService = new CustomerServiceSOAPProxy();
			ReferenceGeneratorServiceSOAPProxy refProxy = new ReferenceGeneratorServiceSOAPProxy();
			
			LOG.debug(">>>>>>>>>>>>>>>>>>>>> Running populate Agent Commission Sweep ProcessTxn >>>>>>>>>>>>>");
			
			BankAccount commissionAcc = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(agent.getCustomerId(), BankAccountType.AGENT_COMMISSION_SUSPENSE, OwnerType.AGENT, null);
			MobileProfile mobileProfile = customeService.getMobileProfileByCustomer(agent.getCustomerId()).get(0);
			LOG.debug(">>>>>>>>> commission acc"+agent.getCustomerId());
			LOG.debug(">>>>>>>>>>>>>>>>>"+commissionAcc.getId());
			AccountBalance bal = bankService.getClosingBalance(commissionAcc.getId(),DateUtil.convertToXMLGregorianCalendar(new Date()));
			if(bal.getAmount() <= 0){
				return null;
			}
			String messageId = refProxy.generateUUID(EWalletConstants.SEQUENCE_NAME_DAY_ENDS, EWalletConstants.SEQUENCE_PREFIX_DAY_ENDS, Formats.yearFormatTwoDigit.format(new Date(System.currentTimeMillis())), 0, 1000000000L - 1L);
			
			processTxn.setMessageId(messageId);
			processTxn.setId(messageId);
			processTxn.setAmount(bal.getAmount());
			processTxn.setBalance(bal.getAmount());
			processTxn.setNarrative("Agent Commission Sweep Deposit");
			processTxn.setBankReference(commissionAcc.getBankReferenceId());
			processTxn.setTransactionType(zw.co.esolutions.ewallet.enums.TransactionType.COMMISSION_SWEEPING);
			processTxn.setSourceMobileId(mobileProfile.getId());
//			processTxn.setProfileId(depositInfo.getProfileId());
			processTxn.setFromBankId(mobileProfile.getBankId());
			processTxn.setToBankId(mobileProfile.getBankId());
			// for a sweeping txn the banks are indeed the same
			processTxn.setSourceAccountNumber(commissionAcc.getAccountNumber());
			processTxn.setDestinationAccountNumber(mobileProfile.getMobileNumber());
			//Need clarification
			processTxn.setSourceEQ3AccountNumber(mobileProfile.getMobileNumber());
			processTxn.setDestinationEQ3AccountNumber(mobileProfile.getMobileNumber());
			processTxn.setCustomerId(mobileProfile.getCustomer().getId());
			// branch Id MUST be set to the customer's branch
			processTxn.setBranchId(mobileProfile.getCustomer().getBranchId());
			// we postto the teller's branch
			processTxn.setTransactionLocationType(TransactionLocationType.BANK_BRANCH);
			processTxn.setTransactionLocationId(mobileProfile.getBranchId());
			processTxn.setSourceMobile(mobileProfile.getMobileNumber());
			processTxn.setTargetMobileId(processTxn.getSourceMobileId());
			processTxn.setTargetMobile(processTxn.getSourceMobile());
			processTxn.setCustomerName(mobileProfile.getCustomer().getLastName() + " " + mobileProfile.getCustomer().getFirstNames());
			// required to set transaction location id and type
			processTxn = populateEquationAccoountsByTransactionType(processTxn);
		}catch(Exception e){
			LOG.debug("Agent has missing details ignore");
			return null;
		}
		
		return processTxn;
	}
	
	public void sendReport(String recipients , String subject , String title , String txn,String heading){
		subject = subject+" "+Formats.shortDateFormat.format(new Date());
		EmailSender es = new EmailSender();
		String [] to = {recipients};
		String from = "ewallet@zb.co.zw";
		StringBuffer sb = new StringBuffer("");
		sb.append(title);
		sb.append("\n");
		sb.append("\n");
		sb.append("Details :");
		sb.append("\n");
		sb.append(heading);
		sb.append("\n");
		sb.append(txn);
		sb.append("\n");
		
		String message = sb.toString();
		
		es.postMail(to, from, subject, message, null);
	}
	
	private void compileReport(List<ProcessTransaction> txns , String recipient){
		
		String subject = "Expired Non Holder Reversal Summary";
		String title = "Expired Non Holder Reversal: Attempted Reversal List";
		String body = "";
		String heading = " Reference    , Mobile Number , Amount($)";
		if(txns != null && txns.size()> 0){
			StringBuffer sb = new StringBuffer("");
			for(ProcessTransaction txn : txns){
				
				double amnt = (txn.getAmount())/100;					
				String txnInfo = " "+txn.getMessageId()+" , "+txn.getSourceMobile()+"  , "+amnt+" \n";
				sb.append(txnInfo);
			}
			body = sb.toString();
		}else{
			body = "No uncollected Non Holder Transfers to reverse in this run ";
		}
		
		this.sendReport(recipient, subject, title, body , heading);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void sendCommissionSweep(List<String> data, String recipient){
		String subject = "Agent Commission Sweep As AT "+ DateUtil.convertToDateWithTime(new Date());
		String title = "Agent Commission: Attempted Sweep List";
		String body = "";
		String heading = " Reference         , Mobile Number , Amount($)";
		if(data != null && data.size()> 0){
			StringBuffer sb = new StringBuffer("");
			for(String txn : data){
				sb.append(txn);
				sb.append("\n");
			}
			body = sb.toString();
		}else{
			body = "No commissions to sweep in this run ";
		}
		
		this.sendReport(recipient, subject, title, body , heading);
	}
	
	public boolean submitResponse(ResponseInfo responseInfo) {
		Session jmsSession = null;
		MessageProducer jmsProducer = null;
		try {
			jmsSession = jmsConnection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			jmsProducer = jmsSession.createProducer(switchResponseQueue);
			ObjectMessage jmsObjectMessage = jmsSession.createObjectMessage(responseInfo);
			jmsObjectMessage.setStringProperty("transactionType", responseInfo.getResponseType().name());
			jmsProducer.send(jmsObjectMessage);
		} catch (JMSException e) {
			LOG.fatal("FAILED TO SUBMIT REPLY TO EWT " + responseInfo.getRequestInfo().getMessageId(), e);
			e.printStackTrace(System.err);
			return false;
		}finally{
			try {
				if(jmsProducer != null){
					jmsProducer.close();
				}
			} catch (Exception e) {
				LOG.warn("Failed to close JSM Producer");
			}
			try {
				if(jmsConnection != null){
					jmsSession.close();
				}
			} catch (Exception e) {
				LOG.warn("Failed to close JSM Connection");
			}
			
		}
		return true;
	}
	
	public boolean submitRequest(BankRequest bankRequest, boolean reversal) {
		Session jmsSession = null;
		MessageProducer jmsProducer = null;
		try {
			jmsSession = jmsConnection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			jmsProducer = jmsSession.createProducer(bankRequestsQueue);
			bankRequest.setReversal(reversal);
			ObjectMessage jmsObjectMessage = jmsSession.createObjectMessage(bankRequest);
			jmsObjectMessage.setStringProperty("transactionType", bankRequest.getTransactionType().name());
			jmsProducer.send(jmsObjectMessage);
		} catch (JMSException e) {
			LOG.fatal("FAILED TO SUBMIT REQUEST TO BANK " + bankRequest.getReference(), e);
			e.printStackTrace(System.err);
			return false;
		}
		finally{
			try {
				if(jmsProducer != null){
					jmsProducer.close();
				}
			} catch (Exception e) {
				LOG.warn("Failed to close JSM Producer");
			}
			try {
				if(jmsConnection != null){
					jmsSession.close();
				}
			} catch (Exception e) {
				LOG.warn("Failed to close JSM Connection");
			}
			
		}
		return true;
	}

	
	@PostConstruct
	public void initialise(){
		try {
			jmsConnection = jmsQueueConnectionFactory.createConnection();
		} catch (JMSException e) {
			LOG.fatal("Failed to initialise MDB " + this.getClass().getSimpleName(), e);
			e.printStackTrace(System.err);
		}
	}

	@PreDestroy
	public void cleanUp(){
		if(jmsConnection != null){
			try {
				jmsConnection.close();
			} catch (JMSException e) {
				//Ignore this guy
			}
		}
	} 
}
