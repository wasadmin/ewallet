package zw.co.esolutions.mcommerce.centralswitch.processes;

import java.sql.Timestamp;

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
import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.enums.TransferType;
import zw.co.esolutions.ewallet.enums.USSDTransactionType;
import zw.co.esolutions.ewallet.msg.RequestInfo;
import zw.co.esolutions.ewallet.msg.USSDRequestMessage;
import zw.co.esolutions.ewallet.services.proxy.MobileCommerceProxy;
import zw.co.esolutions.ewallet.util.NumberUtil;
import zw.co.esolutions.mcommerce.centralswitch.model.MessageTransaction;
import zw.co.esolutions.mcommerce.centralswitch.model.TransactionRoutingInfo;
import zw.co.esolutions.mcommerce.xml.ISOMarshaller;
import zw.co.esolutions.ussd.web.services.FlowStatus;
import zw.co.esolutions.ussd.web.services.UssdTransaction;

/**
 * Session Bean implementation class USSDRequestProcessUtilImpl
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class USSDRequestProcessUtilImpl implements USSDRequestProcessUtil {
	@PersistenceContext
	private EntityManager em;

	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/switch.log.properties");
			LOG = Logger.getLogger(USSDRequestProcessUtilImpl.class);
		} catch (Exception e) {
		}
	}

	/**
	 * Default constructor.
	 */
	public USSDRequestProcessUtilImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public MessageTransaction populateMessageTransaction(USSDRequestMessage ussdRequest) throws Exception {
		LOG.debug(">>>> $$$$$$$$$$$$$ In Populate MsgTxn : Source BankId = " + ussdRequest.getSourceBankId() + ", Source Mobile = " + ussdRequest.getSourceMobileNumber());

		MessageTransaction messageTransaction = new MessageTransaction();
		messageTransaction.setAmount(ussdRequest.getAmount());
		messageTransaction.setCustomerUtilityAccount(ussdRequest.getCustomerUtilityAccount());
		messageTransaction.setDateCreated(new Timestamp(System.currentTimeMillis()));
		messageTransaction.setDestinationBankAccount(ussdRequest.getTargetBankAccount());
		// messageTransaction.setNarrative(null);
		messageTransaction.setSecretCode(ussdRequest.getSecretCode());
		messageTransaction.setSourceBankAccount(ussdRequest.getSourceBankAccount());
		messageTransaction.setSourceBankId(ussdRequest.getSourceBankId());
		messageTransaction.setSourceMobileNumber(ussdRequest.getSourceMobileNumber());
		messageTransaction.setTargetBankId(ussdRequest.getTargetBankId());
		messageTransaction.setTargetMobileNumber(ussdRequest.getTargetMobileNumber());
		// messageTransaction.setTimeout(null);
		// messageTransaction.setTransactionLocationId(null);
		messageTransaction.setTransactionLocationType(ussdRequest.getTransactionLocationType());
		messageTransaction.setCustomerUtilityAccount(ussdRequest.getCustomerUtilityAccount());
		messageTransaction.setUtilityName(ussdRequest.getUtilityName());
		messageTransaction.setUuid(ussdRequest.getUuid());
		messageTransaction.setMno(ussdRequest.getMno());
		messageTransaction.setStatus(TransactionStatus.BANK_REQUEST);
		messageTransaction.setAgentNumber(ussdRequest.getAgentNumber());
		LOG.debug(">>>>>>>>> Agent Number = " + messageTransaction.getAgentNumber());
		messageTransaction.setOldMessageId(ussdRequest.getRefCode());
		messageTransaction.setOldPin(ussdRequest.getOldPin());
		
		//Temporary solutions.. Needs to be generic
		//BankInfo bankInfo = this.getBankConfigInfo("6");
		
		//Proposed Solution
		BankInfo bankInfo = this.getBankConfigInfo(ussdRequest.getBankAlias());

		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		
		if (bankInfo == null) {
			throw new Exception("Request Rejected : Requested bank is not properly configured for SMS");			
		}
				
		messageTransaction = this.populateRoutingInfo(messageTransaction, bankInfo, ussdRequest);
		
		LOG.debug(messageTransaction);
		// RTGS
		if (USSDTransactionType.RTGS.equals(ussdRequest.getTransactionType())) {
			LOG.debug(">>>>>>>>>>>>>>>>>>>>>> In RTGS Type ... in populateRequest");
			messageTransaction.setTransactionType(TransactionType.RTGS);
			messageTransaction.setDestinationBankAccount(ussdRequest.getDestinationBankName());

		}

		if (ussdRequest.getSourceMobileNumber() != null) {
			LOG.debug("SRC FI ID :" + ussdRequest.getSourceBankId() +" " + ussdRequest.getSourceMobileNumber());
			MobileProfile mobileProfile = customerService.getMobileProfileByBankIdMobileNumberAndStatus(ussdRequest.getSourceBankId(), ussdRequest.getSourceMobileNumber(), MobileProfileStatus.ACTIVE);
			if (mobileProfile != null && mobileProfile.getId() != null) {
				messageTransaction.setCustomerName(mobileProfile.getCustomer().getLastName() + " " + mobileProfile.getCustomer().getFirstNames());
				messageTransaction.setCustomerId(mobileProfile.getCustomer().getId());
				messageTransaction.setSourceMobileId(mobileProfile.getId());
			} else {
				throw new Exception("Mobile Profile Found is null");
			}
		}

		if (ussdRequest.getSourceBankId() != null) {
			Bank bank = bankService.findBankById(ussdRequest.getSourceBankId());
			if (bank != null && bank.getId() != null) {
				messageTransaction.setSourceBankCode(bank.getCode());
				messageTransaction.setBankReference(bank.getId());
			}
		}

		if (ussdRequest.getTargetMobileNumber() != null) {
			MobileProfile mobileProfile = customerService.getMobileProfileByBankAndMobileNumber(ussdRequest.getTargetBankId(), ussdRequest.getTargetMobileNumber());
			if (mobileProfile != null && mobileProfile.getId() != null) {
				messageTransaction.setToCustomerName(mobileProfile.getCustomer().getLastName() + " " + mobileProfile.getCustomer().getFirstNames());
				messageTransaction.setTargetMobileId(mobileProfile.getId());
			}
		}

		if (ussdRequest.getTargetBankId() != null) {
			Bank bank = bankService.findBankById(ussdRequest.getTargetBankId());
			if (bank != null && bank.getId() != null) {
				messageTransaction.setTargetBankCode(bank.getCode());
			}
		}

		if (USSDTransactionType.AGENT_TRANSFER.equals(ussdRequest.getTransactionType())) {
			if (ussdRequest.getSourceBankId().equals(ussdRequest.getTargetBankId())) {
				messageTransaction.setTransferType(TransferType.INTRABANK);
			} else {
				messageTransaction.setTransferType(TransferType.INTERBANK);
			}
		}
		if (USSDTransactionType.TRANSFER.equals(ussdRequest.getTransactionType())) {
			if (ussdRequest.getSourceBankId().equals(ussdRequest.getTargetBankId())) {
				messageTransaction.setTransferType(TransferType.INTRABANK);
			} else {
				messageTransaction.setTransferType(TransferType.INTERBANK);
			}

			// Determine actual transfer type
			String sourceBankAccount = ussdRequest.getSourceBankAccount();
			String targetBankAccount = ussdRequest.getTargetBankAccount();
			boolean sourceIsAnEwalletAccount = false;

			if (sourceBankAccount != null && targetBankAccount != null) {

				sourceIsAnEwalletAccount = NumberUtil.validateMobileNumber(sourceBankAccount);
				boolean targetIsAMobile = NumberUtil.validateMobileNumber(targetBankAccount);

				if (sourceIsAnEwalletAccount) {
					if (targetIsAMobile) {
						try {
							BankAccount targetAccount = null;
							MobileProfile mobileProfile = customerService.getMobileProfileByBankAndMobileNumber(ussdRequest.getTargetBankId(), targetBankAccount);
							if (mobileProfile != null && mobileProfile.getId() != null) {
								targetAccount = bankService.getBankAccountByAccountHolderAndTypeAndOwnerType(mobileProfile.getCustomer().getId(), BankAccountType.E_WALLET, OwnerType.CUSTOMER, mobileProfile.getMobileNumber());
							}
							if (targetAccount != null && targetAccount.getId() != null) {
								// target is an ewallet account
								messageTransaction.setTransactionType(TransactionType.EWALLET_TO_EWALLET_TRANSFER);
								messageTransaction.setTargetMobileNumber(targetBankAccount);
							} else {
								// target is not an ewallet account, its a
								// nonholder transfer
								messageTransaction.setTransactionType(TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER);
								messageTransaction.setSecretCode(ussdRequest.getSecretCode());
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
							MobileProfile mobileProfile = customerService.getMobileProfileByBankAndMobileNumber(ussdRequest.getTargetBankId(), targetBankAccount);
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
								messageTransaction.setSecretCode(ussdRequest.getSecretCode());
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

	@Override
	public RequestInfo populateRequestInfo(MessageTransaction messageTransaction, USSDRequestMessage ussdRequest) throws Exception {
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setAmount(messageTransaction.getAmount());
		requestInfo.setBankCode(messageTransaction.getSourceBankCode());
		requestInfo.setBankPrefix(null);
		requestInfo.setCustomerUtilityAccount(messageTransaction.getCustomerUtilityAccount());
		requestInfo.setDestinationAccountNumber(messageTransaction.getDestinationBankAccount());
		requestInfo.setLocationType(messageTransaction.getTransactionLocationType());
		requestInfo.setMessageId(messageTransaction.getUuid());
		requestInfo.setMno(messageTransaction.getMno());
		requestInfo.setNewPin(ussdRequest.getNewPin());
		requestInfo.setOldPin(ussdRequest.getOldPin());
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

		requestInfo.setDestinationBankName(ussdRequest.getDestinationBankName());
		requestInfo.setPaymentRef(ussdRequest.getPaymentRef());
		requestInfo.setBeneficiaryName(ussdRequest.getBeneficiaryName());
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

	public UssdTransaction createUSSDTransaction(RequestInfo requestInfo, USSDRequestMessage ussdRequest) {
		try {
			LOG.debug("START CREATING USSD Transaction");
			UssdTransaction txn = MobileCommerceProxy.getInstance().createTransaction(this.populateUSSDTransaction(requestInfo, ussdRequest));
			LOG.debug("DONE CREATING USSD Transaction");
			return txn;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private UssdTransaction populateUSSDTransaction(RequestInfo requestInfo, USSDRequestMessage ussdRequest) {
		LOG.debug(">>>>>>>>>>>> In Populate USSD Transaction ");
		UssdTransaction txn = new UssdTransaction();
		txn.setUuid(requestInfo.getMessageId());
		LOG.debug(">>>>>>>>>>>> Set UUID : " + txn.getUuid());
		txn.setFlowStatus(FlowStatus.BANK_REQUEST);
		LOG.debug(">>>>>>>>>>>> Set Flow Status : " + txn.getFlowStatus());
		txn.setSendSms(false);
		txn.setSessionId(ussdRequest.getUuid());
		LOG.debug(">>>>>>>>>>>> Set Session ID : " + txn.getSessionId());
		txn.setSourceMobile(ussdRequest.getSourceMobileNumber());
		LOG.debug(">>>>>>>>>>>> Done Setting Source Mobile : " + txn.getSourceMobile());
		return txn;
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
	
	public MessageTransaction populateRoutingInfo(MessageTransaction txn, BankInfo bankInfo, USSDRequestMessage ussdRequest) {
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
		
		if (USSDTransactionType.BILLPAY.equals(ussdRequest.getTransactionType()) || USSDTransactionType.REGISTER_MERCHANT.equals(ussdRequest.getTransactionType())) {
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
}
