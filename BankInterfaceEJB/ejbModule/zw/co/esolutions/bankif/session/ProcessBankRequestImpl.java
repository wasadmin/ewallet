package zw.co.esolutions.bankif.session;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.bankif.model.BankRequestMessage;
import zw.co.esolutions.bankif.model.CommissionMessage;
import zw.co.esolutions.bankif.model.TransactionInfo;
import zw.co.esolutions.bankif.util.BankMessageFactory;
import zw.co.esolutions.bankif.util.InterfaceConstants;
import zw.co.esolutions.bankif.util.MessageSenderThread;
import zw.co.esolutions.bankif.util.Response;
import zw.co.esolutions.bankif.zb.ZBBankMessageFactory;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.msg.BankRequest;
import zw.co.esolutions.ewallet.msg.BankResponse;
import zw.co.esolutions.ewallet.msg.Commission;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.GenerateKey;
import zw.co.esolutions.mcommerce.refgen.service.ReferenceGeneratorServiceSOAPProxy;

/**
 * Session Bean implementation class ProcessBankRequestImpl
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class ProcessBankRequestImpl implements ProcessBankRequest {
	@PersistenceContext
	private EntityManager em;

	@EJB
	private ProcessBankResponse processBankResponse;

	
	private static Logger LOG ;
	
	static{
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/hostInterface.log.properties");
			LOG = Logger.getLogger(ProcessBankRequestImpl.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + ProcessBankRequestImpl.class);
		}
	}
	
	/**
	 * Default constructor.
	 */
	public ProcessBankRequestImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Response processBankRequest(BankRequest bankRequest) {
		System.out.println("1 Hey BANK REQ has a charge...." + bankRequest.getCommissions());
		if (bankRequest.isReversal()) {
			LOG.debug("2 In ProcessBankRequestImpl >>>>>>>>>>>>>>>>>>>Is a Reversal Transaction");
			return this.processReversalRequest(bankRequest);
		} else {
			LOG.debug("In ProcessBankRequestImpl >>>>>>>>>>>>>>>>>>>Is a New BankRequest Transaction");
			return this.processNewBankRequest(bankRequest);
		}
	}

	@SuppressWarnings("unchecked")
	private Response processReversalRequest(BankRequest bankRequest) {
		BankRequestMessage existingRequest;
		BankRequestMessage reversal;
		List<CommissionMessage> charges = null;
		LOG.debug("A In ProcessBankRequestImpl >>>>>>>>>>>>>>>>>>>In Process Reversal...");
		
			existingRequest = (BankRequestMessage) em.createNamedQuery("findBankRequestInfoByReference").setParameter("reference", bankRequest.getReference()).getSingleResult();
			reversal = new BankRequestMessage(bankRequest);
			if (existingRequest == null) {
				LOG.debug("OG TXN NOT FOUND... REVERSAL FAILED");
//				reversal.setReference(reversal.getReference() + " - R");
//				reversal.setStatus(InterfaceConstants.STATUS_REVERSAL_FAILED);
//				reversal.setAdditionalData(ResponseCode.E501.getDescription());
//				em.persist(reversal);
//				return new Response(ResponseCode.E501);
				return null;
			} 
			
			else{
				
				try{
				LOG.debug("passed the first check IF >>>>>>>>>>>>>>>");
				BankMessageFactory factory = ZBBankMessageFactory.getInstance();
				LOG.debug("bankmessage factory:::::::::::::::::::::"+factory);
				charges = (List<CommissionMessage>) em.createNamedQuery("getCommissionMessagesByBankRequestReference").setParameter("reference", existingRequest.getReference()).getResultList();
				LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>charges>>>>>>>>>>>>>>>>>"+charges);
				if (reversal.getSourceAccountNumber().equalsIgnoreCase(existingRequest.getSourceAccountNumber()) && reversal.getAmount() == existingRequest.getAmount()) {
					LOG.debug("passed the second check IF >>>>>>>>>>>>>>>");
					if (reversal.getSourceAccountNumber().equals(reversal.getTargetAccountNumber())) {	//same target EQ3 account
						LOG.debug("REVERSAL HAS SAME SOURCE AND TARGET ACCOUNTS... do fake reversal.");

						existingRequest.setStatus(InterfaceConstants.STATUS_REVERSAL_SUCCESSFUL);
						em.merge(existingRequest);
						
						LOG.debug("NOW DO CHARGE REVERSALS");

					} else {
						LOG.debug("This reversal is of type : " + existingRequest.getTransactionType().name());
						LOG.debug("Update TXN to STATUS_REVERSAL_REQUEST");
						
						existingRequest.setStatus(InterfaceConstants.STATUS_REVERSAL_REQUEST);
						em.merge(existingRequest);
						
						LOG.debug("DONE Updating TXN to STATUS_REVERSAL_REQUEST");

						LOG.debug("$$$$$$$$$   START SEND TXN Reversal TO BANK");
						
						this.sendBankRequestToBankSysQ(factory.createMainTransactionReversalMessage(existingRequest));

						LOG.debug("$$$$$$$$$  TXN Reversal SENT TO BANK");

					}
					
					if (charges != null) {
						LOG.debug("TXN has Charges..");
						
						for (CommissionMessage charge: charges) { 
							LOG.debug("Update Charge to STATUS_REVERSAL_REQUEST");

							charge.setStatus(InterfaceConstants.STATUS_REVERSAL_REQUEST);
							em.merge(charge);
							
							LOG.debug("DONE Updating Charge to STATUS_REVERSAL_REQUEST");

							LOG.debug("$$$$$$$$$   START SEND Charge Reversal TO BANK");
							
							this.sendBankRequestToBankSysQ(factory.createChargesReversal(charge));

							LOG.debug("$$$$$$$$$  CHARGE Reversal SENT TO BANK");

						}
					}
					
//					return new Response(ResponseCode.E838, "MAIN TXN AND ALL CHARGES");	//reversals sent
					return null;
					
				} else {	//reversals failed
					existingRequest.setStatus(InterfaceConstants.STATUS_REVERSAL_FAILED);
					em.merge(existingRequest);
					
					if (charges != null) {			//mark charge reversals as failed
						
						for (CommissionMessage charge: charges) { 

							LOG.debug("Update Charge to STATUS_REVERSAL_FAILED");
	
							charge.setStatus(InterfaceConstants.STATUS_REVERSAL_FAILED);
							em.merge(charge);
							
							LOG.debug("DONE Updating Charge to STATUS_REVERSAL_FAILED");
						}
						
					}
					
					return new Response(ResponseCode.E504, factory.createReversalMessage(existingRequest));
				}
			
				}catch(Exception e){
					LOG.debug(">>>>>>>>>>>>>>>"+e.getMessage());
					LOG.debug(">>>>>>>>>>>>>>>"+e.getMessage());
					
					e.printStackTrace();
					return new Response(ResponseCode.E501);
				}
				
			}
		
		
		
		
	}

	private Response processNewBankRequest(BankRequest bankRequest) {

		// check for duplicates
		boolean exists;
		try {
			BankRequestMessage existingRequest = (BankRequestMessage) em.createNamedQuery("findBankRequestInfoByReference").setParameter("reference", bankRequest.getReference()).getSingleResult();
			if (existingRequest == null) {
				exists = false;
			} else {
				exists = true;
			}
		} catch (Exception e) {
			exists = false;
		}

		if (exists) {
			// we can't continue here...
			// create an on the run response and return to MDB
			return new Response(ResponseCode.E500);
		} else {
			
			BankRequestMessage info = new BankRequestMessage(bankRequest);
			
			info.setMessageType(InterfaceConstants.MSG_TYPE_0200);
			
			info.setId(GenerateKey.generateEntityId());
			
			try {
				
				LOG.debug("Original Transaction Type : "+bankRequest.getTransactionType());
				
				TransactionType transactionType = bankRequest.getTransactionType();
				
				if (EWalletConstants.MERCHANT_NAME_DSTV.equals(bankRequest.getUtilityName())) {
					transactionType = TransactionType.DSTV_BILLPAY;
				} 
				
				LOG.debug("Transaction Type Converted to: " + transactionType);
				
				info.setProcessingCode(this.findTransactionInfoByTransactionType(transactionType));
				
				
			} catch (Exception e) {
				// transaction Code is not defined on interfaces
				e.printStackTrace();
				return new Response(ResponseCode.E503);
			}
			
			LOG.debug("PCODE is: " + info.getProcessingCode());
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy");
			Date txnDate = new Date(System.currentTimeMillis());
			String date = simpleDateFormat.format(txnDate);
			ReferenceGeneratorServiceSOAPProxy refProxy = new ReferenceGeneratorServiceSOAPProxy();
			info.setSystemTraceAuditNumber(refProxy.getNextNumberInSequence(EWalletConstants.SEQUENCE_NAME_STAN, date, 1, 1000000L - 1L));
			info.setStatus(InterfaceConstants.STATUS_SENT_TO_HOST);
			info.setTransactionType(bankRequest.getTransactionType());
			info.setTransactionDate(txnDate);
			info.setBouquetCode(bankRequest.getBouquetCode());
			info.setBeneficiaryName(bankRequest.getBeneficiaryName());
			
			LOG.debug("BOUQUET Code is: " + info.getBouquetCode());
			
			em.persist(info);
			
			if (bankRequest.getCommissions() != null) {
				// create the commission
				CommissionMessage commissionMessage;
				for (Commission commission : bankRequest.getCommissions()) {
					commissionMessage = this.populateCommissionInfo(info, bankRequest, commission);
					commissionMessage.setBankRequest(info);
					em.persist(commissionMessage);
				}
				
			}
			
			BankMessageFactory factory = ZBBankMessageFactory.getInstance();
			return new Response(ResponseCode.E000, factory.createBankMessage(info));
		}
	}

	private CommissionMessage populateCommissionInfo(BankRequestMessage bankRequestMessage, BankRequest bankRequest, Commission commission) {
		CommissionMessage commissionMessage = new CommissionMessage();
		commissionMessage.setBankRequest(bankRequestMessage);
		commissionMessage.setTargetEq3Account(commission.getTargetEq3Account());
		commissionMessage.setSourceEq3Account(commission.getSourceEq3Account());
		commissionMessage.setAmount(commission.getCommissionAmount());
		commissionMessage.setSourceCustomerAccount(commission.getSourceCustomerAccount());
		try {
			commissionMessage.setProcessingCode(this.findTransactionInfoByTransactionType(commission.getTransactionType()));
		} catch (Exception e) {
			LOG.debug("Failed to set processing code for charge.");
		}
		commissionMessage.setReference(commission.getReference());
		commissionMessage.setStatus(InterfaceConstants.STATUS_NOT_YET_POSTED);
		commissionMessage.setValueDate(new Date(System.currentTimeMillis()));
		commissionMessage.setAgentNumber(commission.getAgentNumber());
		
		return commissionMessage;
	}

	private String findTransactionInfoByTransactionType(TransactionType transactionType) throws Exception {
		TransactionInfo info = (TransactionInfo) em.createNamedQuery("findTransactionInfoByTransactionType").setParameter("transactionType", transactionType).getSingleResult();
		return info.getProcessingCode();
	}

	@Override
	public TransactionInfo findTransactionInfoByProcessingCode(String processingCode) throws Exception {
		TransactionInfo info = (TransactionInfo) em.createNamedQuery("findTransactionInfoByProcessingCode").setParameter("processingCode", processingCode).getSingleResult();
		return info;
	}
	
	@Override
	public BankResponse processEWalletToEWalletBankRequest(BankRequest bankRequest) {
		Response response = this.processNewBankRequest(bankRequest);
		BankRequestMessage info = (BankRequestMessage) em.createNamedQuery("findBankRequestInfoByReference").setParameter("reference", bankRequest.getReference()).getSingleResult();
		BankResponse bankResp = new BankResponse();
		bankResp.setResponseCode(ResponseCode.E000);
		bankResp.setBankResponseCode(InterfaceConstants.RC_OK);
		bankResp.setBankRequest(bankRequest);
		bankResp.setBankReference(bankRequest.getReference());
		if (info != null) {
			info.setStatus(InterfaceConstants.STATUS_SUCCESSFUL);
			response.setResponseCode(zw.co.esolutions.ewallet.sms.ResponseCode.E000);
			try {
				zw.co.esolutions.bankif.model.BankResponseCode resp = processBankResponse.findBankResponseCode(bankResp.getBankResponseCode());
				if (resp != null) {
					info.setResponseCode(resp);
					bankResp.setNarrative(info.getResponseCode().getNarrative());
				}
			} catch (Exception e) {
				// narrative is not really neccessary, Ignore it
				e.printStackTrace();
			}
			info.setBankReference(info.getReference());

			info.setValueDate(new Date(System.currentTimeMillis()));
			bankResp.getBankRequest().setTransactionType(info.getTransactionType());
			bankResp.getBankRequest().setSourceMobileNumber(info.getSourceMobileNumber());
			bankResp.getBankRequest().setTargetMobileNumber(info.getTargetMobileNumber());
			
			info.setMessageType(InterfaceConstants.MSG_TYPE_0210);
			
			em.merge(info);
			
		} else {
			System.out.println("No matching record for the response from bank \n " + bankRequest.getReference());
		}

		return bankResp;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void sendBankRequestToBankSysQ(String formattedBankReqMessage) throws Exception {
		LOG.debug("######### SENDING REQUEST TO BANK SYS Q");

		new Thread(new MessageSenderThread(EWalletConstants.FROM_BANKMED_TO_BANK_SYS_QUEUE, formattedBankReqMessage)).start();
		
		LOG.debug("#########  REQUEST SENT TO BANK SYS Q");

	}

}
