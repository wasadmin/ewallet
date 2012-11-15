package zw.co.esolutions.mcommerce.centralswitch.processes;

import java.sql.Timestamp;
import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.config.bank.BankInfo;
import zw.co.esolutions.config.merchant.MerchantInfo;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountType;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfileStatus;
import zw.co.esolutions.ewallet.enums.MobileWebTransactionType;
import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.enums.TransferType;
import zw.co.esolutions.ewallet.msg.MobileWebRequestMessage;
import zw.co.esolutions.ewallet.msg.RequestInfo;
import zw.co.esolutions.ewallet.services.proxy.MobileCommerceProxy;
import zw.co.esolutions.ewallet.util.NumberUtil;
import zw.co.esolutions.mcommerce.centralswitch.model.MessageTransaction;
import zw.co.esolutions.mcommerce.centralswitch.model.TransactionRoutingInfo;
import zw.co.esolutions.mcommerce.xml.ISOMarshaller;
import zw.co.esolutions.mobile.web.utils.WebStatus;
import zw.co.esolutions.ussd.web.services.WebSession;

/**
 * Session Bean implementation class USSDRequestProcessUtilImpl
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class MobileWebRequestProcessUtilImpl implements MobileWebRequestProcessUtil {
	@PersistenceContext
	private EntityManager em;

	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/switch.log.properties");
			LOG = Logger.getLogger(MobileWebRequestProcessUtilImpl.class);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
	/**
	 * Default constructor.
	 */
	public MobileWebRequestProcessUtilImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public MessageTransaction populateMessageTransaction(MobileWebRequestMessage webRequest) throws Exception {
		LOG.debug(">>>> $$$$$$$$$$$$$ In Populate MsgTxn : Source BankId = "+webRequest.getSourceBankId()+", Source Mobile = "+webRequest.getSourceMobileNumber());
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();

		MessageTransaction messageTransaction = new MessageTransaction();
		messageTransaction.setAmount(webRequest.getAmount());
		messageTransaction.setCustomerUtilityAccount(webRequest.getCustomerUtilityAccount());
		messageTransaction.setDateCreated(new Timestamp(new Date().getTime()));
		messageTransaction.setDestinationBankAccount(webRequest.getTargetBankAccount());
		// messageTransaction.setNarrative(null);
		messageTransaction.setSecretCode(webRequest.getSecretCode());
		messageTransaction.setSourceBankAccount(webRequest.getSourceBankAccount());
		messageTransaction.setSourceBankId(webRequest.getSourceBankId());
		messageTransaction.setSourceMobileNumber(webRequest.getSourceMobileNumber());
		messageTransaction.setTargetBankId(webRequest.getTargetBankId());
		messageTransaction.setTargetMobileNumber(webRequest.getTargetMobileNumber());
		// messageTransaction.setTimeout(null);
		// messageTransaction.setTransactionLocationId(null);
		messageTransaction.setTransactionLocationType(webRequest.getTransactionLocationType());
		messageTransaction.setStatus(TransactionStatus.BANK_REQUEST);
		messageTransaction.setCustomerUtilityAccount(webRequest.getCustomerUtilityAccount());
		messageTransaction.setUtilityName(webRequest.getUtilityName());
		messageTransaction.setUuid(webRequest.getUuid());
		messageTransaction.setMno(webRequest.getMno());
		messageTransaction.setAgentNumber(webRequest.getAgentNumber());
		LOG.debug(">>>>>>>>> Agent Number = "+messageTransaction.getAgentNumber());
		messageTransaction.setOldMessageId(webRequest.getRefCode());
		messageTransaction.setOldPin(webRequest.getOldPin());
		//Temporary solutions.. Needs to be generic
		BankInfo bankInfo = this.getBankConfigInfo("6");

		if (bankInfo == null) {
			throw new Exception("Request Rejected : Requested bank is not properly configured for SMS");			
		}
		
		TransactionRoutingInfo routingInfo = messageTransaction.getTransactionRoutingInfo();
		if(routingInfo == null){
			routingInfo = new TransactionRoutingInfo();
			messageTransaction.setTransactionRoutingInfo(routingInfo);
		}
		routingInfo.setBankName(bankInfo.getBankName());
		routingInfo.setBankReplyQueueName(bankInfo.getRequestQueueName());
		routingInfo.setBankRequestQueueName(bankInfo.getRequestQueueName());
		
		if (MobileWebTransactionType.BILLPAY.equals(webRequest.getTransactionType()) || MobileWebTransactionType.REGISTER_MERCHANT.equals(webRequest.getTransactionType())) {
			MerchantInfo merchantInfo = this.getMerchantConfigInfo(messageTransaction.getUtilityName());
			if (merchantInfo != null) {
				routingInfo.setAccountValidationEnabled(merchantInfo.isEnableAccountValidation());
				routingInfo.setNotificationEnabled(merchantInfo.isEnableNotification());
				routingInfo.setStraightThroughEnabled(merchantInfo.isEnableStraightThrough());
				routingInfo.setMerchantReplyQueueName(merchantInfo.getReplyQueueName());
				routingInfo.setMerchantRequestQueueName(merchantInfo.getRequestQueueName());
			}
		}
		
		//RTGS
		if(MobileWebTransactionType.RTGS.equals(webRequest.getTransactionType())) {
			LOG.debug(">>>>>>>>>>>>>>>>>>>>>> In RTGS Type ... in populateRequest");
			messageTransaction.setTransactionType(TransactionType.RTGS);
			messageTransaction.setDestinationBankAccount(webRequest.getDestinationBankName());
			
		}

		if (webRequest.getSourceMobileNumber() != null) {
			MobileProfile mobileProfile = customerService.getMobileProfileByBankIdMobileNumberAndStatus(webRequest.getSourceBankId(), webRequest.getSourceMobileNumber(), MobileProfileStatus.ACTIVE);
			if (mobileProfile != null && mobileProfile.getId() != null) {
				messageTransaction.setCustomerName(mobileProfile.getCustomer().getLastName() + " " + mobileProfile.getCustomer().getFirstNames());
				messageTransaction.setCustomerId(mobileProfile.getCustomer().getId());
				messageTransaction.setSourceMobileId(mobileProfile.getId());
			} else {
				throw new Exception("Mobile Profile Found is null");
			}
		} 

		if (webRequest.getSourceBankId() != null) {
			Bank bank = bankService.findBankById(webRequest.getSourceBankId());
			if (bank != null && bank.getId() != null) {
				messageTransaction.setSourceBankCode(bank.getCode());
				messageTransaction.setBankReference(bank.getId());
			}
		}

		if (webRequest.getTargetMobileNumber() != null) {
			MobileProfile mobileProfile = customerService.getMobileProfileByBankAndMobileNumber(webRequest.getTargetBankId(), webRequest.getTargetMobileNumber());
			if (mobileProfile != null && mobileProfile.getId() != null) {
				messageTransaction.setToCustomerName(mobileProfile.getCustomer().getLastName() + " " + mobileProfile.getCustomer().getFirstNames());
				messageTransaction.setTargetMobileId(mobileProfile.getId());
			}
		}

		if (webRequest.getTargetBankId() != null) {
			Bank bank = bankService.findBankById(webRequest.getTargetBankId());
			if (bank != null && bank.getId() != null) {
				messageTransaction.setTargetBankCode(bank.getCode());
			}
		}

		if (MobileWebTransactionType.AGENT_TRANSFER.equals(webRequest.getTransactionType())) {
			if (webRequest.getSourceBankId().equals(webRequest.getTargetBankId())) {
				messageTransaction.setTransferType(TransferType.INTRABANK);
			} else {
				messageTransaction.setTransferType(TransferType.INTERBANK);
			}
		}
		
		if (MobileWebTransactionType.TRANSFER.equals(webRequest.getTransactionType())) {
			if (webRequest.getSourceBankId().equals(webRequest.getTargetBankId())) {
				messageTransaction.setTransferType(TransferType.INTRABANK);
			} else {
				messageTransaction.setTransferType(TransferType.INTERBANK);
			}

			// Determine actual transfer type
			String sourceBankAccount = webRequest.getSourceBankAccount();
			String targetBankAccount = webRequest.getTargetBankAccount();
			boolean sourceIsAnEwalletAccount = false;
			LOG.debug(">>>>>>>>>>>>>>>>>>> Src acc = "+sourceBankAccount+", Tatrget Account = "+targetBankAccount);
			if (sourceBankAccount != null && targetBankAccount != null) {

				sourceIsAnEwalletAccount = NumberUtil.validateMobileNumber(sourceBankAccount);
				boolean targetIsAMobile = NumberUtil.validateMobileNumber(targetBankAccount);

				if (sourceIsAnEwalletAccount) {
					LOG.debug(">>>>>>>>>>>>>>>>>>> Src Account is eWallet : "+sourceIsAnEwalletAccount);
					if (targetIsAMobile) {
						LOG.debug(">>>>>>>>>>>>>>>>>>> Target Account is Mobile : "+targetIsAMobile);
						try {
							BankAccount targetAccount = null;
							MobileProfile mobileProfile = customerService.getMobileProfileByBankAndMobileNumber(webRequest.getTargetBankId(), targetBankAccount);
							if (mobileProfile != null && mobileProfile.getId() != null) {
								targetAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(mobileProfile.getCustomer().getId(), BankAccountType.E_WALLET, OwnerType.CUSTOMER, mobileProfile.getMobileNumber());
							}
							LOG.debug(">>>>>>>>>>>>>>>>>>> Tatrget Account = "+targetAccount);
							if (targetAccount != null && targetAccount.getId() != null) {
								// target is an ewallet account
								messageTransaction.setTransactionType(TransactionType.EWALLET_TO_EWALLET_TRANSFER);
								messageTransaction.setTargetMobileNumber(targetBankAccount);
							} else {
								// target is not an ewallet account, its a
								// nonholder transfer
								LOG.debug(">>>>>>>>>>>>>>>>>>> In Non-Holder");
								messageTransaction.setTransactionType(TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER);
								messageTransaction.setSecretCode(webRequest.getSecretCode());
								messageTransaction.setTargetMobileNumber(targetBankAccount);
							}
						} catch (Exception e) {
							throw e;
						}
					} else {
						// target is not a mobile number, its a regular bank
						// account
						messageTransaction.setTransactionType(TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER);
					}
				} else {
					// source account is a regular bankaccount
					if (targetIsAMobile) {
						try {
							BankAccount targetAccount = null;
							MobileProfile mobileProfile = customerService.getMobileProfileByBankAndMobileNumber(webRequest.getTargetBankId(), targetBankAccount);
							if (mobileProfile != null && mobileProfile.getId() != null) {
								targetAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(mobileProfile.getCustomer().getId(), BankAccountType.E_WALLET, OwnerType.CUSTOMER, mobileProfile.getMobileNumber());
							}

							if (targetAccount != null && targetAccount.getId() != null) {
								// target is an ewallet account
								messageTransaction.setTransactionType(TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER);
								messageTransaction.setTargetMobileNumber(targetBankAccount);
							} else {
								// target is not an ewallet account, its a
								// nonholder transfer
								messageTransaction.setTransactionType(TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER);
								messageTransaction.setTargetMobileNumber(targetBankAccount);
								messageTransaction.setSecretCode(webRequest.getSecretCode());
							}
						} catch (Exception e) {
							throw e;
						}
					} else {
						// target is not a mobile number, its a regular bank
						// account
						messageTransaction.setTransactionType(TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER);
						
					}
				}
			} else {
				// one or both accounts are null
				throw new Exception("One or both accounts are null");
			}
		} else {
			messageTransaction.setTransferType(TransferType.NONE);
		}

		return messageTransaction;
	}


	private BankInfo getBankConfigInfo(String bankPrefix) {
		String path = "/opt/eSolutions/conf/bank." + bankPrefix + ".config.xml";
		zw.co.esolutions.config.bank.BankInfo bankInfo = ISOMarshaller.unmarshalBankInfo(path);
		if (bankInfo == null) {
			LOG.fatal("Bank Config for SMS CODE : " + bankPrefix + " could not be loaded");
			return null;
		}
		return bankInfo;
	}
	
	private MerchantInfo getMerchantConfigInfo(String utilityName) {
		String path = "/opt/eSolutions/conf/merchant." + utilityName.toLowerCase() + ".config.xml";
		MerchantInfo merchantInfo = ISOMarshaller.unmarshalMerchantInfo(path);
		if (merchantInfo == null) {
			LOG.fatal("MERCHANT Config for MERCHANT : " + utilityName + " could not be loaded");
			return null;
		}
		return merchantInfo;
	}
	
	@Override
	public RequestInfo populateRequestInfo(MessageTransaction messageTransaction, MobileWebRequestMessage webRequest) throws Exception {
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setAmount(messageTransaction.getAmount());
		requestInfo.setBankCode(messageTransaction.getSourceBankCode());
		requestInfo.setCustomerUtilityAccount(messageTransaction.getCustomerUtilityAccount());
		requestInfo.setDestinationAccountNumber(messageTransaction.getDestinationBankAccount());
		requestInfo.setLocationType(messageTransaction.getTransactionLocationType());
		requestInfo.setMessageId(messageTransaction.getUuid());
		requestInfo.setMno(messageTransaction.getMno());
		requestInfo.setNewPin(webRequest.getNewPin());
		requestInfo.setOldPin(webRequest.getOldPin());
		requestInfo.setPasswordParts(messageTransaction.getPasscodePrompt());
		// requestInfo.setProfileId(null);
		requestInfo.setSecretCode(messageTransaction.getSecretCode());
		requestInfo.setSourceAccountNumber(messageTransaction.getSourceBankAccount());
		requestInfo.setSourceBankId(messageTransaction.getSourceBankId());
		requestInfo.setSourceMobile(messageTransaction.getSourceMobileNumber());
		requestInfo.setSourceMobileProfileId(messageTransaction.getSourceMobileId());
		requestInfo.setStatus(messageTransaction.getStatus());
		requestInfo.setTargetBankId(messageTransaction.getTargetBankId());
		requestInfo.setTargetMobile(messageTransaction.getTargetMobileNumber());
		requestInfo.setTransactionType(messageTransaction.getTransactionType());
		requestInfo.setUtilityName(messageTransaction.getUtilityName());
		requestInfo.setAgentNumber(messageTransaction.getAgentNumber());
		requestInfo.setOldReference(messageTransaction.getOldMessageId());
		

		requestInfo.setDestinationBankName(webRequest.getDestinationBankName());
		requestInfo.setPaymentRef(webRequest.getPaymentRef());
		requestInfo.setBeneficiaryName(webRequest.getBeneficiaryName());
		requestInfo.setDateCreated(messageTransaction.getDateCreated());
		
		return requestInfo;
	}

	public MessageTransaction createMessageTransaction(MessageTransaction messageTransaction) throws Exception {
		if (messageTransaction != null) {
			try {
				em.persist(messageTransaction);
			} catch (Exception e) {
				throw e;
			}
		}
		return messageTransaction;
	}

	public WebSession createMobileWebSession(RequestInfo requestInfo, MobileWebRequestMessage webRequest) {
		try {
			LOG.debug("START CREATING USSD Transaction");
			WebSession txn = MobileCommerceProxy.getInstance().createWebSession(this.populateMobileWebSession(requestInfo, webRequest));
			LOG.debug("DONE CREATING USSD Transaction");
			return txn;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	private WebSession populateMobileWebSession(RequestInfo requestInfo,
			MobileWebRequestMessage webRequest) {
	LOG.debug(">>>>>>>>>>>> In Populate USSD Transaction ");
		WebSession txn = new WebSession();
		txn.setReferenceId(requestInfo.getMessageId());
		LOG.debug(">>>>>>>>>>>> Set UUID : "+txn.getReferenceId());
		txn.setStatus(WebStatus.PENDING_COMPLETION.toString());
		LOG.debug(">>>>>>>>>>>> Set Flow Status : "+txn.getStatus());
		txn.setSendSms(false);
		txn.setId(webRequest.getUuid());
		LOG.debug(">>>>>>>>>>>> Set Session ID : "+txn.getId());
		txn.setMobileNumber(webRequest.getSourceMobileNumber());
		LOG.debug(">>>>>>>>>>>> Done Setting Source Mobile : "+txn.getMobileNumber());
		return txn;
	}
}
