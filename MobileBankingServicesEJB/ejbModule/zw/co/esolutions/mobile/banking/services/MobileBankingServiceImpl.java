package zw.co.esolutions.mobile.banking.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebService;

import org.apache.log4j.Logger;

import zw.co.esolutions.ewallet.bankservices.proxy.BankServiceProxy;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountStatus;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountType;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.bankservices.service.BankStatus;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.customerservices.proxy.CustomerServiceProxy;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.CustomerClass;
import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfileStatus;
import zw.co.esolutions.ewallet.customerservices.service.ResponseCode;
import zw.co.esolutions.ewallet.enums.MobileNetworkOperator;
import zw.co.esolutions.ewallet.enums.USSDTransactionType;
import zw.co.esolutions.ewallet.merchantservices.service.BankMerchantStatus;
import zw.co.esolutions.ewallet.merchantservices.proxy.MerchantServiceProxy;
import zw.co.esolutions.ewallet.merchantservices.service.BankMerchant;
import zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchant;
import zw.co.esolutions.ewallet.merchantservices.service.Merchant;
import zw.co.esolutions.ewallet.merchantservices.service.MerchantServiceSOAPProxy;
import zw.co.esolutions.ewallet.merchantservices.service.MerchantStatus;
import zw.co.esolutions.ewallet.msg.USSDRequestMessage;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.EncryptAndDecrypt;
import zw.co.esolutions.ewallet.util.GenerateKey;
import zw.co.esolutions.ewallet.util.NumberUtil;
import zw.co.esolutions.mobile.banking.msg.BankInfo;
import zw.co.esolutions.mobile.banking.msg.BankRequest;
import zw.co.esolutions.mobile.banking.msg.BankResponse;
import zw.co.esolutions.mobile.banking.msg.MerchantInfo;
import zw.co.esolutions.mobile.banking.msg.MerchantRequest;
import zw.co.esolutions.mobile.banking.msg.MerchantResponse;
import zw.co.esolutions.mobile.banking.msg.Pareq;
import zw.co.esolutions.mobile.banking.msg.Pares;
import zw.co.esolutions.mobile.banking.msg.TransactionRequest;
import zw.co.esolutions.mobile.banking.msg.TransactionResponse;
import zw.co.esolutions.mobile.banking.msg.Vereq;
import zw.co.esolutions.mobile.banking.msg.Veres;
import zw.co.esolutions.mobile.banking.msg.enums.TransactionType;
import zw.co.esolutions.mobile.thread.utils.MessageSenderThread;
import zw.co.esolutions.ussd.entities.USSDTransaction;
import zw.co.esolutions.ussd.mobile.banking.conf.MobileBankingUSSDConfiguration;
import zw.co.esolutions.ussd.util.FlowStatus;
import zw.co.esolutions.ussd.util.LoggerFactory;
import zw.co.esolutions.ussd.util.SystemConstants;
import zw.co.esolutions.ussd.web.services.MobileCommerceService;

/**
 * Session Bean implementation class MobileBankingServiceImpl
 */
@WebService(serviceName = "MobileBankingService", endpointInterface = "zw.co.esolutions.mobile.banking.services.MobileBankingService", 
		portName = "MobileBankingServiceSOAP")
@Stateless
public class MobileBankingServiceImpl implements MobileBankingService {

	@EJB
	private MobileCommerceService ussdService;
	private String USSD_ERROR= "Error occured. Session terminated";

	private Logger logger = LoggerFactory.getLogger(getClass());
	
    /**
     * Default constructor. 
     */
    public MobileBankingServiceImpl() {
        // TODO Auto-generated constructor stub
    }

	public MerchantResponse getMerchants(MerchantRequest merchantRequest) {
		MerchantInfo [] merchants = null;
		List<Merchant> mList = null;
		List<CustomerMerchant> cmList = null;
		
		MerchantResponse merchantResponse = new MerchantResponse();
		try {
			
			merchantResponse.setTransactionTimestamp(new Date(System.currentTimeMillis()));
			
			CustomerServiceSOAPProxy customerProxy = CustomerServiceProxy.getInstance();
			USSDTransaction txn = ussdService.getTransactionByUSSDSessionId(merchantRequest.getUssdSessionId());
			String bankId = getBankId(txn.getBankCode());
			String mobileNumber = NumberUtil.formatMobileNumber(txn.getSourceMobile());
			MobileProfile mobileProfile = customerProxy.getMobileProfileByBankAndMobileNumber(bankId, mobileNumber);
			if(merchantRequest.isThirdParty()) {
				mList = this.getAllActiveMerchantsByBankId(bankId);
			} else {
				cmList = this.getRegisteredMerchants(mobileProfile.getCustomer().getId());
			}
			
			logger.debug(">>>>>>>>>>>>>>>>>>>>> Merchants = "+mList +", Customer merchants = "+cmList);
			List<MerchantInfo> tempList = new ArrayList<MerchantInfo>();
			if(mList == null || mList.isEmpty()) {
				if(cmList != null ) {
					for(CustomerMerchant cm : cmList) {
						MerchantInfo info = new MerchantInfo();
						info.setMerchantName(cm.getBankMerchant().getMerchant().getShortName());
						info.setUtilityAccount(cm.getCustomerAccountNumber());
						tempList.add(info);
					}
					merchants = tempList.toArray(new MerchantInfo[1]);
				}
			} else {
				
				for(Merchant m : mList) {
					MerchantInfo info = new MerchantInfo();
					info.setMerchantName(m.getShortName());
					tempList.add(info);
				}
				merchants = tempList.toArray(new MerchantInfo[1]);
			}
			merchantResponse.setMerchants(merchants);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return merchantResponse;
		
	}

	public Veres verifyEnrolment(Vereq vereq) {
		Veres veres = new  Veres();
		
		try {
			veres.setUssdSessionId(vereq.getUssdSessionId());
			veres.setTransactionTimestamp(new Date(System.currentTimeMillis()));
			logger.debug(">>>>>>>>>>>>>>>>>>>>>>>> New Status.......");
			CustomerServiceSOAPProxy customerProxy = CustomerServiceProxy.getInstance();
			Bank bank = BankServiceProxy.getInstance().getBankByCode(vereq.getBankCode());
			String bankId = bank.getId();
			String mobileNumber = NumberUtil.formatMobileNumber(vereq.getMobileNumber());
			MobileProfile mobileProfile = customerProxy.getMobileProfileByBankAndMobileNumber(bankId, mobileNumber);
			
			if(mobileProfile == null || mobileProfile.getId() == null) {
				veres.setEnrolmentResult(false);
			} else {
				veres.setEnrolmentResult(true);
				
				//It's a fix for Econet only
				if(zw.co.esolutions.ewallet.util.SystemConstants.ECONET_ACQUIRER_ID.equalsIgnoreCase(vereq.getAcquirerId())) {
					// Handle The Econet Way
					veres.setUssdSessionId(mobileProfile.getCustomer().getId());
					
				}
				
				USSDTransaction txn = new USSDTransaction();
				txn.setSessionId(vereq.getUssdSessionId());
				txn.setFlowStatus(FlowStatus.NEW_STATUS);
				txn.setBankCode(vereq.getBankCode());
				txn.setSourceMobile(vereq.getMobileNumber());
				txn.setAquirerId(vereq.getAcquirerId());
				txn.setUuid(GenerateKey.generateEntityId());
				txn.setMno(vereq.getSourceMobileNetworkOperator().toString());
				
				ussdService.createTransaction(txn);
			}
		} catch (Exception e) {
			e.printStackTrace();
			veres.setEnrolmentResult(false);
		}
		
		return veres;
		
	}

	public TransactionResponse submitTransaction(
			TransactionRequest transactionRequest) {
		TransactionResponse transactionResponse = new TransactionResponse();
		MobileBankingUSSDConfiguration config;
		String menu = null;
		
		try {
			USSDTransaction txn = ussdService.getTransactionByUSSDSessionId(transactionRequest.getUssdSessionId());
			config = MobileBankingUSSDConfiguration.getInstance(txn.getBankCode());
			String bankId = getBankId(txn.getBankCode());
			
			USSDRequestMessage msg = populateUssdMessage(transactionRequest, txn);
			
			if(TransactionType.PASSWORD_CHANGE.equals(transactionRequest.getTransactionType())) {
				
				CustomerServiceSOAPProxy cs = CustomerServiceProxy.getInstance();
				MobileProfile mp = cs.getMobileProfileByBankIdMobileNumberAndStatus(bankId, txn.getSourceMobile(), MobileProfileStatus.ACTIVE);
				mp.setSecretCode(EncryptAndDecrypt.encrypt(transactionRequest.getNewPassword(), txn.getSourceMobile()));
				cs.updateMobileProfile(mp, EWalletConstants.SYSTEM);
			}
			//ussdService.deleteTransaction(txn.getUuid());
			
			boolean msgSent = sendMessage(msg);
			if(!msgSent){
				menu = config.getStringValueOf(SystemConstants.FAILED_TO_SEND_MSG);
			} else {
				
				if(TransactionType.BALANCE.equals(transactionRequest.getTransactionType())) {
					
					USSDTransaction txnWait = waitForBankResponse(msg.getUuid());
					logger.debug("###################################### Wait response is = "+txnWait);
					if(txnWait == null) {
						logger.debug("###################################### Now In If "+txnWait);
						menu = getUSSDLastMessage(txn, transactionRequest);
					} else {
						logger.debug("###################################### Now in Esle = "+txnWait);
						menu = txnWait.getMessage();
					}
				} else {
					menu = getUSSDLastMessage(txn, transactionRequest);
				}
			}
			transactionResponse.setNarrative(menu);
			transactionResponse.setUssdSessionId(transactionRequest.getUssdSessionId());
			transactionResponse.setTransactionTimestamp(new Date(System.currentTimeMillis()));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return transactionResponse;
		
	}

	public Pares verifyPassword(Pareq pareq) {
		Pares pares = new Pares();
		pares.setVerificationResult(false);
		pares.setUssdSessionId(pareq.getUssdSessionId());
		pares.setTransactionTimestamp(new Date(System.currentTimeMillis()));
		try {
			USSDTransaction txn = ussdService.getTransactionByUSSDSessionId(pareq.getUssdSessionId());
			MobileBankingUSSDConfiguration config = MobileBankingUSSDConfiguration.getInstance(txn.getBankCode());
			Bank bank = BankServiceProxy.getInstance().getBankByCode(txn.getBankCode());
			String bankId = bank.getId();
			
			MobileProfile mobileProfile = CustomerServiceProxy.getInstance().getMobileProfileByBankAndMobileNumber(bankId, txn.getSourceMobile());
			String menu = null;
			
			if(mobileProfile.getStatus().equals(MobileProfileStatus.ACTIVE)) {
				
				// Validate Pin
				pares = validatePin(pares, mobileProfile, pareq, txn);
				menu = pares.getNarrative();
				
			} else if (MobileProfileStatus.LOCKED.equals(mobileProfile.getStatus())) {
				
				logger.debug("Mobile Profile Locked...");
					if (new Date().after(DateUtil.convertToDate(mobileProfile.getTimeout()))) {
						
						// Validate Pin
						pares = validatePin(pares, mobileProfile, pareq, txn);
						
						logger.debug("Mobile Profile unloked on timeout.");
						
						
					} else {
						
						logger.debug("Mobile Profile Locked, timeout not yet expired.");
						menu = config.getStringValueOf(SystemConstants.ACCOUNT_LOCKED_MSG);
						String bankName = bank.getName();
						menu += " Please visit your nearest " + bankName  + " branch for assistance.";
						
					}
				
				
			} else {
				//account inactive
				menu = config.getStringValueOf(SystemConstants.MOBILE_INACTIVE_MSG);
				String bankName = bank.getName();
				menu += " Please visit your nearest " + bankName  + " branch for assistance.";
				
			}
			pares.setNarrative(menu);
		
		} catch (Exception e) {
			e.printStackTrace();
			pares.setNarrative(USSD_ERROR);
		}
		return pares;
		
	}
	
	private Pares validatePin(Pares pares, MobileProfile mobileProfile, Pareq pareq, USSDTransaction txn) {
		try {
			MobileBankingUSSDConfiguration config = MobileBankingUSSDConfiguration.getInstance(txn.getBankCode());
			List<String> accs = new ArrayList<String>();
			boolean isAgent = false;
			Bank bank = BankServiceProxy.getInstance().getBankByCode(txn.getBankCode());
			Customer customer;
			String menu = null;
			CustomerServiceSOAPProxy customerProxy = CustomerServiceProxy.getInstance();
			logger.debug(">>>>>>>>>>>>>>>>>>>>>>>> Bank Id = "+mobileProfile.getBankId()+", Mobile = "+mobileProfile.getMobileNumber());
			
			ResponseCode responseCode = customerProxy.authenticateMobileProfile(mobileProfile.getBankId(), mobileProfile.getMobileNumber(), pareq.getPassword());
			
			logger.debug(">>>>>>>>>>>>>>>>>>>>>>>> Auth Response Code ....... "+responseCode);
			
			if(ResponseCode.E_000.equals(responseCode)){
				//success
				customer = mobileProfile.getCustomer();
				List<BankAccount> eWalletAccounts = null;
				List<BankAccount> bankAccounts = null;
				BankServiceSOAPProxy bankProxy = BankServiceProxy.getInstance();
				
				clearMobleLocks(customerProxy, mobileProfile);
				
				if(CustomerClass.AGENT.equals(customer.getCustomerClass())) {
					logger.debug(">>>>>>>>>>>>>>>>>>>>>>>> Is agent.......");
					 isAgent = true;
					 eWalletAccounts = bankProxy.getBankAccountsByAccountHolderIdAndOwnerTypeAndStatusAndBankAccountType(customer.getId(),OwnerType.AGENT,BankAccountStatus.ACTIVE,BankAccountType.AGENT_EWALLET);
					 logger.debug(">>>>>>>>>>>>>>>>>>>>>>> Ewallets = "+eWalletAccounts);
					 
					 bankAccounts = bankProxy.getBankAccountsByAccountHolderIdAndOwnerTypeAndStatus(customer.getId(), OwnerType.AGENT, BankAccountStatus.ACTIVE);
					
				} else {
					logger.debug(">>>>>>>>>>>>>>>>>>>>>> Non Agent ");
					 eWalletAccounts = bankProxy.getBankAccountsByAccountHolderIdAndOwnerTypeAndStatusAndBankAccountType(customer.getId(),OwnerType.CUSTOMER,BankAccountStatus.ACTIVE,BankAccountType.E_WALLET);
					 bankAccounts = bankProxy.getBankAccountsByAccountHolderIdAndOwnerTypeAndStatus(customer.getId(), OwnerType.CUSTOMER, BankAccountStatus.ACTIVE);
				}
			   			    
				boolean hasEwalletAccounts = eWalletAccounts != null ? eWalletAccounts.size()> 0 : false; 
				boolean hasBankAccounts = bankAccounts != null ? bankAccounts.size()> 0 : false; 
				
				if(hasEwalletAccounts){
					
					for(BankAccount account : eWalletAccounts){
						accs.add(account.getAccountNumber());
					}
				}
				
				if(hasBankAccounts){
					
					for(BankAccount account : bankAccounts){
						accs.add(account.getAccountNumber());
					}
				}
				
				if(isAgent) {
					pares.setAgent(true);
					
				}
				
				if(mobileProfile.getMobileProfileEditBranch() != null && 
						EWalletConstants.CHANGE_PIN.equalsIgnoreCase(mobileProfile.getMobileProfileEditBranch())) {
					pares.setAutoPinChange(true);
				}
				
				if(accs != null && !accs.isEmpty()) {
					String[] accounts = accs.toArray( new String[1]);
					pares.setVerificationResult(true);
					pares.setBankAccounts(accounts);
				}else{
					menu = config.getStringValueOf(SystemConstants.NO_ACTIVE_ACCOUNTS);
					
				}
			   				
			}else if( ResponseCode.E_403.equals(responseCode)){
				//account locked
				menu = config.getStringValueOf(SystemConstants.ACCOUNT_LOCKED_MSG);
				String bankName = bank.getName();
				menu += " Please visit your nearest " + bankName  + " branch for assistance.";
				
				
			}else if(ResponseCode.E_401.equals(responseCode) || ResponseCode.E_402.equals(responseCode)){
				//account inactive
				menu = config.getStringValueOf(SystemConstants.ACCOUNT_INACTIVE_MSG);
				String bankName = bank.getName();
				menu += " Please visit your nearest " + bankName  + " branch for assistance.";
				
				
			}else if(ResponseCode.E_400.equals(responseCode)){
				//invalid pin
				menu = config.getStringValueOf(SystemConstants.INVALID_PIN_MSG);
				
				
			} else {
				menu = USSD_ERROR;
			}
			
			pares.setNarrative(menu);
		} catch (Exception e) {
			e.printStackTrace();
			pares.setNarrative(USSD_ERROR);
		}
		return pares;
	}
	
	private void clearMobleLocks(CustomerServiceSOAPProxy customerProxy , MobileProfile mobileProfile) {
		try {
			if(mobileProfile != null && mobileProfile.getPasswordRetryCount() > 0) {
				mobileProfile.setStatus(MobileProfileStatus.ACTIVE);
				mobileProfile.setPasswordRetryCount(0);
				customerProxy.updateMobileProfile(mobileProfile, EWalletConstants.SYSTEM);
			}
		} catch (Exception e) {
			
		}
		
	
	}
	
	private String getBankId(String bankCode) {
		String bankId = null;
		try {
			bankId = BankServiceProxy.getInstance().getBankByCode(bankCode).getId();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return bankId;
	}
	
	private boolean sendMessage(USSDRequestMessage ussdMsg){
		logger.debug(">>>>>>>>>>> Send Message to Switch");
		try{
			new Thread(new MessageSenderThread(ussdMsg)).start();
		}catch(Exception e){
			return false;
		}
		logger.debug(">>>>>>>>>>> Send Message to Switch");
		return true;
	}
	
	private USSDRequestMessage populateUssdMessage(TransactionRequest transactionRequest, USSDTransaction txn) {
		String newUssdSessionId = GenerateKey.generateEntityId();
		USSDRequestMessage msg = new USSDRequestMessage();
		msg.setSourceBankId(getBankId(txn.getBankCode()));
		msg.setMno(MobileNetworkOperator.valueOf(txn.getMno().toString()));
		msg.setAgentNumber(transactionRequest.getAgentNumber());
		msg.setNewPin(transactionRequest.getNewPassword());
		msg.setPaymentRef(transactionRequest.getPaymentReference());
		msg.setSourceBankAccount(transactionRequest.getSourceAccountNumber());
		msg.setTargetBankAccount(transactionRequest.getTargetAccountNumber());
		msg.setTargetBankId(getBankId(transactionRequest.getTargetBankCode()));
		msg.setCustomerUtilityAccount(transactionRequest.getTargetCustomerMerchantAccount());
		msg.setUtilityName(transactionRequest.getTargetMerchant());
		msg.setTargetMobileNumber(transactionRequest.getTargetMobileNumber());
		msg.setAmount(transactionRequest.getTransactionAmount());
		msg.setSourceMobileNumber(txn.getSourceMobile());
		msg.setSecretCode(transactionRequest.getSecretCode());
		
		//Beneficiary
		if(TransactionType.RTGS.equals(transactionRequest.getTransactionType())) {
			msg.setDestinationBankName(transactionRequest.getTargetBankCode());
			msg.setBeneficiaryName(transactionRequest.getBeneficiaryName());
		}
		if(TransactionType.PASSWORD_CHANGE.equals(transactionRequest.getTransactionType())) {
			msg.setTransactionType(USSDTransactionType.CHANGE_PASSCODE);
		} else if(TransactionType.DEPOSIT.equals(transactionRequest.getTransactionType())) {
			msg.setTransactionType(USSDTransactionType.AGENT_CUSTOMER_DEPOSIT);
		} else if(TransactionType.CASHOUT.equals(transactionRequest.getTransactionType())) {
			msg.setTransactionType(USSDTransactionType.AGENT_CUSTOMER_NON_HOLDER_WITHDRAWAL);
		} else if(TransactionType.CASH_WITHDRAWAL.equals(transactionRequest.getTransactionType())) {
			msg.setTransactionType(USSDTransactionType.AGENT_CUSTOMER_WITHDRAWAL);
		} else if(TransactionType.MERCHANT_REG.equals(transactionRequest.getTransactionType())) {
			msg.setTransactionType(USSDTransactionType.REGISTER_MERCHANT);
		} else if(TransactionType.MINISTATEMENT.equals(transactionRequest.getTransactionType())) {
			msg.setTransactionType(USSDTransactionType.MINI_STATEMENT);
		}else {
			msg.setTransactionType(USSDTransactionType.valueOf(transactionRequest.getTransactionType().toString()));
		}
		msg.setUuid(newUssdSessionId);
		return msg;
	}
	
	private USSDTransaction waitForBankResponse(String sessionId) {
		USSDTransaction txn = null;
		logger.debug("#########      IN waitForBankResponse method");
		try {
			MobileBankingUSSDConfiguration conf = getConfigUSSDInstance();
			logger.debug(">>>>>>>>>>>>>>>>>>>>> cofig = "+conf);

			long TRANSACTION_TIMEOUT = Long.parseLong(conf.getStringValueOf("TRANSACTION_TIMEOUT"));
			long RESPONSE_CHECK_INTERVAL = Long.parseLong(conf.getStringValueOf("RESPONSE_CHECK_INTERVAL"));
			
			long count = RESPONSE_CHECK_INTERVAL;

			boolean responseArrived = false;

			while (count <= TRANSACTION_TIMEOUT) {

				Thread.sleep(RESPONSE_CHECK_INTERVAL); // wait for configured time
														// interval

				logger.debug("#########      WAITED : " + (count / 1000) + " sec");

				count += RESPONSE_CHECK_INTERVAL;

				txn = ussdService.getTransactionByUSSDSessionId(sessionId);
				
				logger.debug("###########  USSD Transaction = "+txn);
				if(txn == null) {
					logger.debug("###########  USSD Transaction is NULL, continue waiting.");
					continue;
				}
				
				if (FlowStatus.COMPLETED.equals(txn.getFlowStatus())) {
					responseArrived = true;
					ussdService.deleteTransaction(txn.getUuid());
					break;
				} else {
					logger.debug("#######  RESPONSE NOT YET ARRIVED");

				}
			}
			
			logger.debug("#########      OUT of WAIT LOOP");
			
			if(responseArrived) {
				return txn;
			} else {
				if (txn != null && txn.getUuid() != null) {
					txn.setSendSms(true);
					ussdService.updateTransaction(txn);
					txn = null;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return txn;
	}
	
	private MobileBankingUSSDConfiguration getConfigUSSDInstance() {
		return MobileBankingUSSDConfiguration.getInstance(SystemConstants.USSD_FILE_SERVICE_CODE_CASE);
		
	}
	
	private String getUSSDLastMessage(USSDTransaction txn, TransactionRequest transactionRequest) {
		String menu = null;
		String bankCode = null;
		MobileBankingUSSDConfiguration config;
		try {
			bankCode = txn.getBankCode();
			logger.debug("############################################## Bank Code : "+bankCode);
			config = MobileBankingUSSDConfiguration.getInstance(bankCode);
			logger.debug("############################################## Transaction Type : "+transactionRequest.getTransactionType()+" , Config File : "+config);
			if(TransactionType.BALANCE.equals(transactionRequest.getTransactionType())) {
				logger.debug("############################################## This is : "+transactionRequest.getTransactionType());
				menu = config.getStringValueOf(SystemConstants.BALANCE_TOO_LONGLAST_MSG);
				logger.debug("############################################## Menu = "+menu);
			} else if(TransactionType.MINISTATEMENT.equals(transactionRequest.getTransactionType())) {
				menu = config.getStringValueOf(SystemConstants.MINISTATEMENT_LAST_MSG);
			}else if(TransactionType.TRANSFER.equals(transactionRequest.getTransactionType())) {
				menu = config.getStringValueOf(SystemConstants.TRANSFER_LAST_MSG);
			}else if(TransactionType.RTGS.equals(transactionRequest.getTransactionType())) {
				menu = config.getStringValueOf(SystemConstants.RTGS_LAST_MSG);
			}else if(TransactionType.BILLPAY.equals(transactionRequest.getTransactionType())) {
				menu = config.getStringValueOf(SystemConstants.BILLPAYMENT_LAST_MSG);
			}else if(TransactionType.MERCHANT_REG.equals(transactionRequest.getTransactionType())) {
				menu = config.getStringValueOf(SystemConstants.REGISTER_MERCHANT_LAST_MSG);
			}else if(TransactionType.TOPUP.equals(transactionRequest.getTransactionType()) || 
					TransactionType.TOPUP_TXT.equals(transactionRequest.getTransactionType())) {
				menu = config.getStringValueOf(SystemConstants.TOPUP_LAST_MSG);
			}else if(TransactionType.CASH_WITHDRAWAL.equals(transactionRequest.getTransactionType())) {
				menu = config.getStringValueOf(SystemConstants.WITHDRAWAL_LAST_MSG);
			}else if(TransactionType.CASHOUT.equals(transactionRequest.getTransactionType())) {
				menu = config.getStringValueOf(SystemConstants.AGENT_WITH_NON_LAST_MSG);
			}else if(TransactionType.DEPOSIT.equals(transactionRequest.getTransactionType())) {
				menu = config.getStringValueOf(SystemConstants.DEPOSIT_LAST_MSG);
			}else if(TransactionType.PASSWORD_CHANGE.equals(transactionRequest.getTransactionType())) {
				menu = config.getStringValueOf(SystemConstants.PIN_CHANGE_LAST_MSG);
			} else if(TransactionType.AGENT_SUMMARY.equals(transactionRequest.getTransactionType())) {
				menu = config.getStringValueOf(SystemConstants.AGENT_SUMMARY_LAST_MSG);
			} else if(TransactionType.AGENT_TRANSFER.equals(transactionRequest.getTransactionType())) {
				menu = config.getStringValueOf(SystemConstants.TRANSFER_LAST_MSG);
			}
		} catch (Exception e) {
			e.printStackTrace();
			menu = USSD_ERROR;
		}
		return menu;
	}

	private List<Merchant> getAllActiveMerchantsByBankId(String bankId) {
		try {
			MerchantServiceSOAPProxy merchantProxy = MerchantServiceProxy.getInstance(); 
			List<Merchant> merchants = new ArrayList<Merchant>();
			
			List<BankMerchant> bankMerchants = merchantProxy.getBankMerchantByBankId(bankId);
			if(bankMerchants != null && !bankMerchants.isEmpty()) {
				for(BankMerchant bm : bankMerchants) {
					if(BankMerchantStatus.ACTIVE.equals(bm.getStatus())) {
						merchants.add(bm.getMerchant());
					}
				}
				
				if(!merchants.isEmpty()) {
					return merchants;
				}
			} 
		} catch (Exception e) {
			return null;
		}
		return null;
	}
	
	private List<CustomerMerchant> getRegisteredMerchants(String customerId) {
		List<Merchant> merchants = new ArrayList<Merchant>();
		try {
			MerchantServiceSOAPProxy merchantProxy = MerchantServiceProxy.getInstance(); 
			List<CustomerMerchant> cmerchants = merchantProxy.getCustomerMerchantByCustomerId(customerId);
			for(CustomerMerchant cmerchant : cmerchants){
				Merchant merchant = cmerchant.getBankMerchant().getMerchant();
				if(MerchantStatus.ACTIVE.equals(merchant.getStatus())) {
					return cmerchants;
				}
			}
			if(merchants.isEmpty()) {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}
	
	
	public BankResponse getBanks(BankRequest bankRequest) {
		BankResponse bankResponse = new BankResponse();
		try {
			bankResponse.setUssdSessionId(bankRequest.getUssdSessionId());
			bankResponse.setTransactionTimestamp(new Date(System.currentTimeMillis()));
			List<Bank> banks = BankServiceProxy.getInstance().getBankByStatus(BankStatus.ACTIVE);
			List<BankInfo> bankInfoList = new ArrayList<BankInfo>();
			for(Bank bank : banks) {
				BankInfo info = new BankInfo();
				info.setBankCode(bank.getCode());
				info.setBankName(bank.getName());
				bankInfoList.add(info);
			}
			BankInfo [] tmp = bankInfoList.toArray(new BankInfo[1]);
			bankResponse.setBanks(tmp);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return bankResponse;
	}
}
