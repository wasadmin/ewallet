package zw.co.esolutions.mcommerce.centralswitch.processes;

import javax.ejb.Stateless;

/**
 * Session Bean implementation class ProcessUtilImpl
 * 
 * @author stanford
 */
@Stateless
//@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class MessageProcessorImpl{
////implements MessageProcessor {
//
//	@PersistenceContext(unitName = "CentralSwitchEJB")
//	private EntityManager em;
//
//	@Resource(mappedName = "jms/EWalletQCF")
//	private QueueConnectionFactory jmsQueueConnectionFactory;
//	
//	@Resource(mappedName = EWalletConstants.ECONET_SMS_OUT_QUEUE)
//	private Queue notificationsQueue;
//
//	private Connection jmsConnection;
//	
//	@PostConstruct
//	public void initialise() {
//		try {
//			jmsConnection = jmsQueueConnectionFactory.createConnection();
//		} catch (JMSException e) {
//			LOG.fatal("Failed to initialise MDB " + this.getClass().getSimpleName(), e);
//			e.printStackTrace(System.err);
//		}
//	}
//
//	@PreDestroy
//	public void cleanUp() {
//		if (jmsConnection != null) {
//			try {
//				jmsConnection.close();
//			} catch (JMSException e) {
//				// Ignore this guy
//			}
//		}
//	}
//	
//	/**
//	 * Default constructor.
//	 */
//	public MessageProcessorImpl() {
//
//	}
//
//	private static Logger LOG;
//
//	static {
//		try {
//			PropertyConfigurator.configure("/opt/eSolutions/conf/switch.log.properties");
//			LOG = Logger.getLogger(MessageProcessorImpl.class);
//		} catch (Exception e) {
//		}
//	}
//	
//
//	@Override
//	public NotificationInfo processPinChangeAdvice(MessageTransaction requestInfo) throws Exception {
//		LOG.debug("PROCESSING PIN CHANGE ADVICE...");
//		// look for the txn
//		MessageTransaction txn = em.find(MessageTransaction.class, requestInfo.getUuid());
//		// update it to the new status
//		if (txn == null) {
//			throw new Exception("Original Transaction with reference [" + requestInfo.getUuid() + "] was not found.");
//		}
//		String message;
//		LOG.debug("Found the PIN Change Request Match...");
//		// promote the txn state according to response.
//		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
//		MobileProfile profile = customerService.getMobileProfileByBankAndMobileNumber(requestInfo.getSourceBankId(), requestInfo.getSourceMobileNumber());
//		String encCode = EncryptAndDecrypt.encrypt(requestInfo.getNewPin(), requestInfo.getSourceMobileNumber());
//		profile.setSecretCode(encCode);
//
//		// This is done to clear First Pin Change
//		if (profile.getMobileProfileEditBranch() != null && EWalletConstants.CHANGE_PIN.equalsIgnoreCase(profile.getMobileProfileEditBranch())) {
//			profile.setMobileProfileEditBranch("");
//		}
//		profile = customerService.updateMobileProfile(profile, EWalletConstants.SYSTEM);
//
//		txn = this.promoteTxnState(txn, TransactionStatus.COMPLETED);
//		LOG.debug("Promoted.......");
//		txn.setResponseCode(SystemConstants.RESPONSE_CODE_OK_SHORT);
//		message = "Your PIN CHANGE Request was successful. Your new ZB e-Wallet PIN is " + requestInfo.getNewPin() + ". ZB e-Wallet - Powered by e-Solutions";
//		if (TransactionLocationType.MOBILE_WEB.equals(txn.getTransactionLocationType())) {
//			LOG.debug("It's MOBILE_WEB Txn ......");
//			MobileCommerceServiceSOAPProxy mobileService = MobileWebServiceProxy.getInstance();
//			WebSession session = mobileService.getWebSessionByReferenceId(txn.getUuid());
//			session.setMessage(message);
//			session.setStatus(WebStatus.COMLETED.toString());
//			session = mobileService.updateWebSession(session);
//			LOG.debug("Promoted MOBILE WEB.......");
//		}
//		txn = this.updateProcessTransaction(txn);
//		LOG.debug("The message : " + message);
//		// notify originator.
//		return new NotificationInfo(requestInfo.getSourceMobileNumber(), message, null, EWalletConstants.ECONET_SMS_OUT_QUEUE);
//	}
//
//	
//
//
//
//	public String processPasscodeChange(MessageTransaction txn) throws Exception {
//		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
//		MobileProfile profile = customerService.getMobileProfileByMobileNumber(txn.getSourceMobileId());
//		List<MobileProfile> profiles = customerService.getMobileProfileByCustomer(profile.getCustomer().getId());
//
//		for (MobileProfile p : profiles) {
//			p.setSecretCode(txn.getSecretCode());
//			customerService.updateMobileProfile(p, EWalletConstants.SYSTEM);
//		}
//
//		return ResponseCode.E000.name();
//	}
//
//	
//
//	public boolean customerHasTransactedAtLeastOnce(String mobileId) {
//		LOG.debug("############# in method custHasTxcted");
//		List<MessageTransaction> txns = this.getProcessTransactionBySourceMobileIdAndMessageTypeAndStatus(mobileId, TransactionType.DEPOSIT, TransactionStatus.COMPLETED);
//		LOG.debug("############# txns " + txns);
//		if (txns == null || txns.isEmpty()) {
//			txns = this.getProcessTransactionBySourceMobileIdAndMessageTypeAndStatus(mobileId, TransactionType.EWALLET_TO_EWALLET_TRANSFER, TransactionStatus.COMPLETED);
//		}
//		LOG.debug("############# txns2 " + txns);
//		if (txns == null || txns.isEmpty()) {
//			return false; // the customer has not transacted
//		}
//		return true; // the customer has transacted.
//	}
//
//
//
//	
//
////	private void handleTransactionsRequest(MessageTransaction txn) {
////		LOG.debug("Transaction Request MDB Has consumed!!!!!!");
////		try {
////			AuthenticationResponse authResponse = this.checkRegistration(txn);
////
////			if (ResponseCode.E000.equals(authResponse.getResponse())) {
////				// now this is a success.
////				txn = this.createProcessTransaction(txn);
////				txn = this.promoteTxnState(txn, TransactionStatus.VEREQ);
////
////				
////				MessageSender.sendSMSToMobileDestination(jmsConnection, narrative + ". ZB e-Wallet - Powered by e-Solutions", txn.getSourceMobileNumber(), notificationsQueue, LOG);
////
////			} else if (ResponseCode.E707.equals(authResponse.getResponse())) {
////				txn.setTransactionType(TransactionType.PASSCODE);
//////				return new SMSProcessorResponse(requestInfo, true, false, false, "", "");
////			} else {
////				// handle the rest of the Exception Scenarios
//////				return new SMSProcessorResponse(requestInfo, false, true, false, authResponse.getNarrative() + ". ZB e-Wallet - Powered by e-Solutions", "");
////			}
////		} catch (Exception e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
//////			return null;
////		}
////
////	}
//
//	private void handlePasscodeResponse(MessageTransaction pareq) {
//		AuthenticationResponse authResponse;
//		ResponseCode response;
//		try {
//
//			CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
//			// find the mobile profile
//			MobileProfile profile = customerService.getMobileProfileByBankAndMobileNumber(pareq.getSourceBankId(), pareq.getSourceMobileNumber());
//
//			LOG.debug("\nFound mobile profile");
//			if (profile != null && profile.getId() != null) {
//				MessageTransaction txn = this.getLatestTransactionPendingAuthorisation(profile.getId(), TransactionStatus.VERES, pareq.getSourceBankCode());
//				LOG.debug("\nFound existing transaction : " + txn);
//				if (txn == null) {
//					// no requests pending authorisation
//					MessageSender.sendTextToQueueDestination(jmsConnection, "You have no request pending authorisation. ZB e-Wallet - Powered by e-Solutions.", txn.getSourceMobileNumber(), LOG);
////					return new SMSProcessorResponse(requestInfo, false, true, false, , "");
//					return;
//				}
//				// validate txn passcode
//				authResponse = this.processPasswordRetry(pareq, txn);
//				response = authResponse.getResponse();
//				TransactionType txnType = txn.getTransactionType();
//				txnRoutingInfo = txn.getTransactionRoutingInfo();
//				if (ResponseCode.E000.equals(authResponse.getResponse())) {
//					if (TransactionType.MERCHANT_REG.equals(txnType) || TransactionType.EWALLET_BILLPAY.equals(txnType) || TransactionType.BILLPAY.equals(txnType)) {
//						if(txnRoutingInfo.isAccountValidationEnabled() && txnRoutingInfo.isStraightThroughEnabled()){
//							LOG.info("Submitting FOR ACCOUNT VALIDATION : [" + txn.getUuid() + " | " + txnType + " | " + txn.getUtilityName() + "]");
//							txn = this.promoteTxnState(txn, TransactionStatus.ACCOUNT_VALIDATION_RQST);
//							txn = this.updateProcessTransaction(txn);
//							MessageSender.sendTextToQueueDestination(jmsConnection, ISO8583Processor.convertToISO8583XMLString(txn, false), txnRoutingInfo.getMerchantRequestQueueName(), LOG);
////							return new SMSProcessorResponse(requestInfo, false, false, true, , messages.getMetadata().getRequestQueue());
//						}
//					} else {
//						txn = this.promoteTxnState(txn, TransactionStatus.BANK_REQUEST);
//						LOG.debug("Submit to bank " + response);
//						MessageSender.sendObjectToDestination(jmsConnection, requestInfo, null, txnRoutingInfo.getBankRequestQueueName(), LOG);
////						return new SMSProcessorResponse(requestInfo, true, false, false, "", "");
//					}
//				} else if (ResponseCode.E707.equals(authResponse.getResponse())) {
//					requestInfo = this.populateRequestInfo(txn);
//					requestInfo.setTransactionType(TransactionType.PASSCODE);
//					MessageSender.sendObjectToDestination(jmsConnection, requestInfo, null, txnRoutingInfo.getBankRequestQueueName(), LOG);
////					return new SMSProcessorResponse(requestInfo, true, false, false, "", "");
//				} else {
//					MessageSender.sendTextToQueueDestination(jmsConnection, authResponse.getNarrative() + ". ZB e-Wallet - Powered by e-Solutions.", txn.getSourceMobileNumber(), LOG);
////					return new SMSProcessorResponse(requestInfo, false, true, false, , "");
//				}
//			} else {
//				MessageSender.sendTextToQueueDestination(jmsConnection, ResponseCode.E700.getDescription() + " ZB e-Wallet - Powered by e-Solutions.", requestInfo.getSourceMobile(), LOG);
////				return new SMSProcessorResponse(requestInfo, false, true, false, , "");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
////			return null;
//		}
//
//	}
//
//	private SMSProcessorResponse handlePinChangeRequest(MessageTransaction requestInfo) {
//		LOG.info("PROCESSING PIN CHANGE Request....");
//		PinChangeValidationResponse pinResponse;
//		try {
//			// check mobile profile existence and status
//			LOG.debug("In MDB Bean Mobile : " + requestInfo.getSourceMobile() + " " + requestInfo.getSourceBankId());
//			pinResponse = this.validatePinChangeRequest(requestInfo);
//			LOG.debug("Transaction Type : " + requestInfo.getTransactionType());
//			if (ResponseCode.E000.equals(pinResponse.getResponseCode())) {
//				// forward to the bank to do the pin change
//				LOG.debug("Performing the actual PIN CHANGE");
//				NotificationInfo notification = this.processPinChangeAdvice(requestInfo);
//
//				LOG.debug("Forwarding to bank queue....");
//				requestInfo.setLocationType(TransactionLocationType.SMS);
//				return new SMSProcessorResponse(requestInfo, true, true, false, notification.getNarrative(), "");
//			} else {
//				// invalid pin change request, return to sender.
//				LOG.debug("Failed.... return to sender......");
//				return new SMSProcessorResponse(requestInfo, false, true, false, pinResponse.getNarrative() + ". ZB e-Wallet - Powered by e-Solutions", "");
//			}
//		} catch (Exception e) {
//			LOG.warn("Exception thrown in process pin change");
//			e.printStackTrace(System.err);
//			return null;
//		}
//	}
//
//	// @PersistenceContext
//	// EntityManager em;
//
//	private static Properties config = SystemConstants.configParams;
//
//
//
//	

}
