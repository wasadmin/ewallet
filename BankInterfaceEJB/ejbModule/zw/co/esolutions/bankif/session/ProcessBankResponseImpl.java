package zw.co.esolutions.bankif.session;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.bankif.mdbs.HandleBankRequest;
import zw.co.esolutions.bankif.model.BankRequestMessage;
import zw.co.esolutions.bankif.model.BankResponseCode;
import zw.co.esolutions.bankif.model.CommissionMessage;
import zw.co.esolutions.bankif.model.TransactionInfo;
import zw.co.esolutions.bankif.util.BankMessageFactory;
import zw.co.esolutions.bankif.util.BankResponseHandlerResponse;
import zw.co.esolutions.bankif.util.InterfaceConstants;
import zw.co.esolutions.bankif.util.Response;
import zw.co.esolutions.bankif.zb.ZBBankMessageFactory;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.msg.BankRequest;
import zw.co.esolutions.ewallet.msg.BankResponse;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.mcommerce.refgen.service.ReferenceGeneratorServiceSOAPProxy;

/**
 * Session Bean implementation class ProcessBankResponseImpl
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class ProcessBankResponseImpl implements ProcessBankResponse {
	@PersistenceContext
	private EntityManager em;

	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/hostInterface.log.properties");
			LOG = Logger.getLogger(HandleBankRequest.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + HandleBankRequest.class);
		}
	}

	public ProcessBankResponseImpl() {

	}

	public BankResponseHandlerResponse handleBankResponse(BankResponse bankResponse) {
		try {
			LOG.info("Transaction reference " + bankResponse.getBankReference());
			BankRequestMessage existingRequest = (BankRequestMessage) em.createNamedQuery("findBankRequestInfoByReference").setParameter("reference", bankResponse.getBankReference()).getSingleResult();
		
			if (existingRequest != null) {
				if (InterfaceConstants.STATUS_SENT_TO_HOST.equalsIgnoreCase(existingRequest.getStatus())) {
					if (InterfaceConstants.RC_OK.equalsIgnoreCase(bankResponse.getBankResponseCode())) {
						existingRequest.setStatus(InterfaceConstants.STATUS_SUCCESSFUL);
						bankResponse.setResponseCode(zw.co.esolutions.ewallet.sms.ResponseCode.E000);
					} else {
						existingRequest.setStatus(InterfaceConstants.STATUS_FAILED);
						bankResponse.setResponseCode(zw.co.esolutions.ewallet.sms.ResponseCode.E505);
					}
					try {
						BankResponseCode resp = this.findBankResponseCode(bankResponse.getBankResponseCode());
						if (resp != null) {
							bankResponse.setResponseCode(resp.geteWtResponseCode());
							existingRequest.setResponseCode(resp);
							bankResponse.setNarrative(existingRequest.getResponseCode().getNarrative());
						}
					} catch (Exception e) {
						// narrative is not really neccessary, Ignore it
						e.printStackTrace();
					}
					
					existingRequest.setBankReference(existingRequest.getReference());
					existingRequest.setBalance(bankResponse.getAvailableBalance());
					existingRequest.setValueDate(bankResponse.getValueDate());
					existingRequest.setLedgerBalance(bankResponse.getLedgerBalance());
					existingRequest.setAdditionalData(bankResponse.getAdditionalData());
					bankResponse.getBankRequest().setTransactionType(existingRequest.getTransactionType());
					bankResponse.getBankRequest().setSourceMobileNumber(existingRequest.getSourceMobileNumber());
					bankResponse.getBankRequest().setTargetMobileNumber(existingRequest.getTargetMobileNumber());
					existingRequest.setMessageType(bankResponse.getMessageType());
					LOG.debug("In handleBankResponse 34>>>>>>>>>>>>>>>>>>>>>:::::::::"+bankResponse.getBankRequest().getTransactionType());
					LOG.debug("In handleBankResponse 34>>>>>>>>>>>>>>>>>>>>>:::::::::"+bankResponse.getBankRequest().getTransactionType());
					em.merge(existingRequest);
					List<CommissionMessage> commissions = this.getCommissionMessagesByBankRequestReference(existingRequest.getReference());
					LOG.debug("Commissions found for request with ref : " + commissions.size());
					
					LOG.debug("Existing transaction >>>>>>>>>>>>>>>>>.........."+bankResponse.getBankRequest().getTransactionType());
					
					return new BankResponseHandlerResponse(bankResponse, commissions);
				} else {
					LOG.debug("Response already committed for reference ....... " + existingRequest.getReference());
					return null;
				}
			} 
		} catch (NoResultException e) {
			e.printStackTrace(System.err);
			LOG.warn("No result : No matching record for the response from bank : " + bankResponse.getBankReference());
			return null;
		} catch (Exception e) {
			LOG.warn("Exception thrown : ", e);
			e.printStackTrace(System.err);
		}
		return null;
	}

	public BankResponse processTransactionAdvice(BankResponse bankResponse) {
		LOG.debug("Transaction alerts....");
		
		return bankResponse;
	}

	public BankResponse processReversalResponse(BankResponse bankResponse) {
		LOG.debug("Handling reversal response...." + bankResponse.getBankReference()) ;
		
		if (TransactionType.TARIFF.equals(bankResponse.getBankRequest().getTransactionType())) {	//charge response
			LOG.debug("$$    TXN IS A CHARGE");
			CommissionMessage existingCharge = (CommissionMessage) em.find(CommissionMessage.class,bankResponse.getBankRequest().getReference());
			if (InterfaceConstants.STATUS_REVERSAL_REQUEST.equals(existingCharge.getStatus()) ||
					InterfaceConstants.STATUS_SENT_TO_HOST.equals(existingCharge.getStatus())) {
				LOG.debug("In ProcessBankResponseImpl>>>>>>> Status "+existingCharge.getStatus());
				if (InterfaceConstants.RC_OK.equals(bankResponse.getBankResponseCode())) {
					existingCharge.setStatus(InterfaceConstants.STATUS_REVERSAL_SUCCESSFUL);
					LOG.debug("In ProcessBankResponseImpl>>>>>>> Status set to "+existingCharge.getStatus());
					bankResponse.setResponseCode(zw.co.esolutions.ewallet.sms.ResponseCode.E000);
					LOG.debug("In ProcessBankResponseImpl>>>>>>> Bank Response's Response Code set to "+bankResponse.getResponseCode());
				} else {
					existingCharge.setStatus(InterfaceConstants.STATUS_REVERSAL_FAILED);
					LOG.debug("In ProcessBankResponseImpl>>>>>>> Status set to "+existingCharge.getStatus());
					bankResponse.setResponseCode(zw.co.esolutions.ewallet.sms.ResponseCode.E505);
					LOG.debug("In ProcessBankResponseImpl>>>>>>> Bank Resposnse's Response Code set to "+bankResponse.getResponseCode());
				}
				try {
					BankResponseCode resp = this.findBankResponseCode(bankResponse.getBankResponseCode());
					LOG.debug("In ProcessBankResponseImpl>>>>>>> Found Bank Response Code Object = "+resp);
					if (resp != null) {
						LOG.debug("In ProcessBankResponseImpl>>>>>>> Response Code Not Null = "+resp.getResponseCode());
						bankResponse.setResponseCode(resp.geteWtResponseCode());
						LOG.debug("In ProcessBankResponseImpl>>>>>>> BankResponse Code set to EWallet Rsp Code = "+bankResponse.getResponseCode());
						existingCharge.setBankResponseCode(resp);
						LOG.debug("In ProcessBankResponseImpl>>>>>>> Response Code set for Existing Charge In Database = "+existingCharge.getBankResponseCode());
						bankResponse.setNarrative(existingCharge.getBankResponseCode().getNarrative());
						LOG.debug("In ProcessBankResponseImpl>>>>>>> BankResponse Narrative = "+bankResponse.getNarrative());
					}
				} catch (Exception e) {
					// narrative is not really neccessary, Ignore it
					e.printStackTrace();
				}
				
				existingCharge.setValueDate(bankResponse.getValueDate());
				LOG.debug("In ProcessBankResponseImpl>>>>>>> Existing Charge Value Date = "+existingCharge.getValueDate());
				em.merge(existingCharge);
				
				LOG.debug("$$    DONE UPDATING CHARGE");
				LOG.debug("In ProcessBankResponseImpl>>>>>>> Returning "+bankResponse);
				return bankResponse;
			}  else {
				LOG.debug("Response already committed for reference ....... " + existingCharge.getReference());
				return null;
			}
		}else if (TransactionType.COMMISSION.equals(bankResponse.getBankRequest().getTransactionType())) {	//charge response
				LOG.debug("$$    TXN IS A COMMISSION");
				CommissionMessage existingCharge = (CommissionMessage) em.find(CommissionMessage.class,bankResponse.getBankRequest().getReference());
				if (InterfaceConstants.STATUS_REVERSAL_REQUEST.equals(existingCharge.getStatus()) || InterfaceConstants.STATUS_SENT_TO_HOST.equals(existingCharge.getStatus())) {
					LOG.debug("In ProcessBankResponseImpl>>>>>>> Status "+existingCharge.getStatus());
					if (InterfaceConstants.RC_OK.equals(bankResponse.getBankResponseCode())) {
						existingCharge.setStatus(InterfaceConstants.STATUS_REVERSAL_SUCCESSFUL);
						LOG.debug("In ProcessBankResponseImpl>>>>>>> Status set to "+existingCharge.getStatus());
						bankResponse.setResponseCode(zw.co.esolutions.ewallet.sms.ResponseCode.E000);
						LOG.debug("In ProcessBankResponseImpl>>>>>>> Bank Response's Response Code set to "+bankResponse.getResponseCode());
					} else {
						existingCharge.setStatus(InterfaceConstants.STATUS_REVERSAL_FAILED);
						LOG.debug("In ProcessBankResponseImpl>>>>>>> Status set to "+existingCharge.getStatus());
						bankResponse.setResponseCode(zw.co.esolutions.ewallet.sms.ResponseCode.E505);
						LOG.debug("In ProcessBankResponseImpl>>>>>>> Bank Resposnse's Response Code set to "+bankResponse.getResponseCode());
					}
					try {
						BankResponseCode resp = this.findBankResponseCode(bankResponse.getBankResponseCode());
						LOG.debug("In ProcessBankResponseImpl>>>>>>> Found Bank Response Code Object = "+resp);
						if (resp != null) {
							LOG.debug("In ProcessBankResponseImpl>>>>>>> Response Code Not Null = "+resp.getResponseCode());
							bankResponse.setResponseCode(resp.geteWtResponseCode());
							LOG.debug("In ProcessBankResponseImpl>>>>>>> BankResponse Code set to EWallet Rsp Code = "+bankResponse.getResponseCode());
							existingCharge.setBankResponseCode(resp);
							LOG.debug("In ProcessBankResponseImpl>>>>>>> Response Code set for Existing Charge In Database = "+existingCharge.getBankResponseCode());
							bankResponse.setNarrative(existingCharge.getBankResponseCode().getNarrative());
							LOG.debug("In ProcessBankResponseImpl>>>>>>> BankResponse Narrative = "+bankResponse.getNarrative());
						}
					} catch (Exception e) {
						// narrative is not really neccessary, Ignore it
						e.printStackTrace();
					}
					
					existingCharge.setValueDate(bankResponse.getValueDate());
					LOG.debug("In ProcessBankResponseImpl>>>>>>> Existing Charge Value Date = "+existingCharge.getValueDate());
					em.merge(existingCharge);
					
					LOG.debug("$$    DONE UPDATING COMMISSION");
					LOG.debug("In ProcessBankResponseImpl>>>>>>> Returning "+bankResponse);
					return bankResponse;
				}  else {
					LOG.debug("Response already committed for reference ....... " + existingCharge.getReference());
					return null;
				}
				
		}else {
			LOG.debug("$$    TXN IS NOT A CHARGE");
			BankRequestMessage existingRequest = (BankRequestMessage) em.createNamedQuery("findBankRequestInfoByReference").setParameter("reference", bankResponse.getBankReference()).getSingleResult();
			if (InterfaceConstants.STATUS_SENT_TO_HOST.equalsIgnoreCase(existingRequest.getStatus()) || InterfaceConstants.STATUS_REVERSAL_REQUEST.equalsIgnoreCase(existingRequest.getStatus())) {
				if (InterfaceConstants.RC_OK.equalsIgnoreCase(bankResponse.getBankResponseCode())) {
					existingRequest.setStatus(InterfaceConstants.STATUS_REVERSAL_SUCCESSFUL);
					bankResponse.setResponseCode(zw.co.esolutions.ewallet.sms.ResponseCode.E000);
				} else {
					existingRequest.setStatus(InterfaceConstants.STATUS_REVERSAL_FAILED);
					bankResponse.setResponseCode(zw.co.esolutions.ewallet.sms.ResponseCode.E505);
				}
				try {
					BankResponseCode resp = this.findBankResponseCode(bankResponse.getBankResponseCode());
					if (resp != null) {
						bankResponse.setResponseCode(resp.geteWtResponseCode());
						existingRequest.setResponseCode(resp);
						bankResponse.setNarrative(existingRequest.getResponseCode().getNarrative());
					}
				} catch (Exception e) {
					// narrative is not really neccessary, Ignore it
					e.printStackTrace();
				}
				existingRequest.setValueDate(bankResponse.getValueDate());
				bankResponse.getBankRequest().setTransactionType(existingRequest.getTransactionType());
				existingRequest.setMessageType(bankResponse.getMessageType());
				em.merge(existingRequest);
				
				LOG.debug("$$    DONE UPDATING TXN");
	
				return bankResponse;
			} else {
				LOG.debug("Response already committed for reference ....... " + existingRequest.getReference());
				return null;
			}
		}
	
	}

	@Override
	public BankResponseCode findBankResponseCode(String respCode) {
		return em.find(BankResponseCode.class, respCode);
	}

	@Override
	public BankResponseHandlerResponse processTransactionChargeResponse(BankResponse bankResponse) {

		BankRequest bankRequest = bankResponse.getBankRequest();
		if (bankRequest != null) {
			System.out.println("Handling the Commission response....");
			CommissionMessage commissionMessage = em.find(CommissionMessage.class, bankRequest.getReference());
			System.out.println("Existiing commission found..." + commissionMessage);
			if (commissionMessage != null) {
				if (InterfaceConstants.STATUS_SENT_TO_HOST.equalsIgnoreCase(commissionMessage.getStatus())) {
					System.out.println("Found one in pending status for reference " + commissionMessage.getReference());
					commissionMessage.setBankResponseCode(this.findBankResponseCode(bankResponse.getBankResponseCode()));
					if (InterfaceConstants.RC_OK.equalsIgnoreCase(bankResponse.getBankResponseCode())) {
						System.out.println("Success......");
						commissionMessage.setStatus(InterfaceConstants.STATUS_SUCCESSFUL);
						bankResponse.setResponseCode(ResponseCode.E000);
					} else {
						System.out.println("Manual resolve........");
						commissionMessage.setStatus(InterfaceConstants.MANUAL_RESOLVE);
						bankResponse.setResponseCode(ResponseCode.E505);
					}
					commissionMessage.setValueDate(new Date(System.currentTimeMillis()));
				}
			}
			System.out.println("Done sorting out commission response");
		}
		return null;
	}

	@Override
	public BankResponseHandlerResponse processAgentCommissionResponse(BankResponse bankResponse) {

		BankRequest bankRequest = bankResponse.getBankRequest();
		if (bankRequest != null) {
			System.out.println("Handling the Commission response....");
			CommissionMessage commissionMessage = em.find(CommissionMessage.class, bankRequest.getReference());
			System.out.println("Existiing commission found..." + commissionMessage);
			if (commissionMessage != null) {
				if (InterfaceConstants.STATUS_SENT_TO_HOST.equalsIgnoreCase(commissionMessage.getStatus())) {
					System.out.println("Found one in pending status for reference " + commissionMessage.getReference());
					commissionMessage.setBankResponseCode(this.findBankResponseCode(bankResponse.getBankResponseCode()));
					if (InterfaceConstants.RC_OK.equalsIgnoreCase(bankResponse.getBankResponseCode())) {
						System.out.println("Success......");
						commissionMessage.setStatus(InterfaceConstants.STATUS_SUCCESSFUL);
						bankResponse.setResponseCode(ResponseCode.E000);
					} else {
						System.out.println("Manual resolve........");
						commissionMessage.setStatus(InterfaceConstants.MANUAL_RESOLVE);
						bankResponse.setResponseCode(ResponseCode.E505);
					}
					commissionMessage.setValueDate(new Date(System.currentTimeMillis()));
				}
			}
			System.out.println("Done sorting out commission response");
		}
		return null;
	}

	
	@Override
	public List<Response> processTransactionCharge(String bankReference) throws Exception {
		LOG.debug("Looking for commision messages for request with ref " + bankReference);
		List<CommissionMessage> commissions = this.getCommissionMessagesByBankRequestReference(bankReference);
		List<Response> responses = new ArrayList<Response>();
		if (commissions == null || commissions.isEmpty()) {
			LOG.debug("There are no commissions found........");
		} else {
			LOG.debug("Commission are not null, the list : " + commissions.size());

			for (CommissionMessage commissionMessage : commissions) {
				responses.add(this.processChargeTransaction(commissionMessage));
			}
			LOG.debug("Done creating messages for commissions.." + responses.size());
		}
		LOG.debug("Now returning list of responses : " + responses);
		return responses;
	}

	private Response processChargeTransaction(CommissionMessage commissionMessage) throws Exception {

		if (commissionMessage.getProcessingCode() == null) {
			try {
//				commissionMessage.setProcessingCode(this.findProcessingCode(TransactionType.TARIFF));
				commissionMessage.setProcessingCode(this.findProcessingCode(commissionMessage.getTransactionType()));
			} catch (Exception e) {
				commissionMessage.setProcessingCode("T0");
			}
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy");
		Date today = new Date(System.currentTimeMillis());
		String date = simpleDateFormat.format(today);
		ReferenceGeneratorServiceSOAPProxy refProxy = new ReferenceGeneratorServiceSOAPProxy();
		commissionMessage.setSystemTraceAuditNumber(refProxy.getNextNumberInSequence(EWalletConstants.SEQUENCE_NAME_STAN, date, 1, 1000000L - 1L));
		commissionMessage.setStatus(InterfaceConstants.STATUS_SENT_TO_HOST);
		BankMessageFactory factory = ZBBankMessageFactory.getInstance();
		em.merge(commissionMessage);
		return new Response(ResponseCode.E000, factory.createChargesMessage(commissionMessage));
	}

	private List<CommissionMessage> getCommissionMessagesByBankRequestReference(String reference) throws Exception {
		Query query = em.createNamedQuery("getCommissionMessagesByBankRequestReference");
		query.setParameter("reference", reference);
		return (List<CommissionMessage>) query.getResultList();
	}

	private String findProcessingCode(TransactionType transactionType) throws Exception {
		TransactionInfo info = (TransactionInfo) em.createNamedQuery("findTransactionInfoByTransactionType").setParameter("transactionType", transactionType).getSingleResult();
		return info.getProcessingCode();
	}
}
