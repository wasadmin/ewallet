package zw.co.esolutions.topup.ws.impl;

import java.sql.Timestamp;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebService;

import zw.co.esolutions.topup.ws.TopupUtil;
import zw.co.esolutions.topup.ws.TopupWebService;
import zw.co.esolutions.topup.ws.model.TransactionInfo;
import zw.co.esolutions.topup.ws.util.ReversalRequest;
import zw.co.esolutions.topup.ws.util.ReversalResponse;
import zw.co.esolutions.topup.ws.util.ServiceCommand;
import zw.co.esolutions.topup.ws.util.SystemConstants;
import zw.co.esolutions.topup.ws.util.TopupWebServiceConfig;
import zw.co.esolutions.topup.ws.util.WSRequest;
import zw.co.esolutions.topup.ws.util.WSResponse;

/**
 * Session Bean implementation class TopupWebServiceImpl
 */
@Stateless

@WebService(serviceName = "TopupWebService", portName = "TopupWebServicePort", endpointInterface = "zw.co.esolutions.topup.ws.TopupWebService")
public class TopupWebServiceImpl implements TopupWebService {
//	private static Logger LOG;
//
//	static {
//		try {
//			PropertyConfigurator.configure("/opt/eSolutions/conf/switch.log.properties");
//			LOG = Logger.getLogger(TopupWebServiceImpl.class);
//		} catch (Exception e) {
//		}
//	}

	@EJB
	TopupUtil topupUtil;

	/**
	 * Default constructor.
	 */
	public TopupWebServiceImpl() {
	
	}

	
	public WSResponse processRequest(WSRequest request) {
		// first validate this request
		WSResponse response = this.validateRequest(request);
		response.setRequest(request);
		if (!SystemConstants.RC_OK.equalsIgnoreCase(response.getResponseCode())) {
			// the request is not valid.
			return response;
		}
		System.out.println("Done validating request success");
		// if we get here, then the request looks valid, now need to check for
		// duplicates
		response = this.checkDuplicateTransaction(request);
		
		if (!SystemConstants.RC_OK.equalsIgnoreCase(response.getResponseCode())) {
			// the request already exists.
			return response;
		}
		System.out.println("Done checking duplicates success");
		// clear, proceed to create the TXN
		TransactionInfo info = topupUtil.createTransactionInfo(request);
		// now the Action.execute should come into play
		// the method knows how to route the traffic to the respective MNO
		System.out.println("Done inserting into db");
		ServiceCommand action = request.getServiceCommand();
		try {
			response = action.execute(request);
			System.out.println("Done running execute...");
			if (SystemConstants.RC_OK.equalsIgnoreCase(response.getResponseCode())) {
				// successful topup
				info.setStatus(SystemConstants.STATUS_TOPUP_SUCCESSFUL);
				info.setResponseCode(response.getResponseCode());
				info.setAirtimeBalance(response.getAirtimeBalance());
				info.setInitialBalance(response.getInitialBalance());
				info.setNarrative(response.getNarrative());
				info.setValueDate(new Date(System.currentTimeMillis()));
				// now update the txn in the DB
				info = topupUtil.updateTransactionInfo(info);
				System.out.println("Done topuing up, returning...");
				return response;
				
			} else {
				info.setStatus(SystemConstants.STATUS_TOPUP_FAILED);
				info.setResponseCode(response.getResponseCode());
				info.setAirtimeBalance(response.getAirtimeBalance());
				info.setInitialBalance(response.getInitialBalance());
				info.setNarrative(response.getNarrative());
				info.setValueDate(new Timestamp(System.currentTimeMillis()));
				info = topupUtil.updateTransactionInfo(info);
				return response;
			}
		} catch (Exception e) {
			// if You get Here, you are in serious trouble.
			response.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			response.setNarrative(e.getMessage());
			info.setStatus(SystemConstants.STATUS_TOPUP_FAILED);
			info.setNarrative(response.getNarrative());
			info = topupUtil.updateTransactionInfo(info);
			return response;
		}
	}

	@Override

	public ReversalResponse processReversal(ReversalRequest reversalRequest) {
		ReversalResponse response = this.validateReversalRequest(reversalRequest);
		// validate the reversal request
		if (!SystemConstants.RC_OK.equalsIgnoreCase(response.getResponseCode())) {
			return response;
		}
		WSRequest requestToReverse = reversalRequest.getRequestToReverse();
		TransactionInfo transactionInfo;
		try {
			// find the original transaction.
			transactionInfo = topupUtil.findTransactionInfoByRequest(requestToReverse);
			if (transactionInfo == null) {
				// no such transaction, fail the reversal and proceed.
				response.setNarrative("Original request not found");
				response.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
				return response;
			}
			// we have the original txn,
			// check its status.
			String status = transactionInfo.getStatus();
			if (SystemConstants.STATUS_TOPUP_PENDING.equalsIgnoreCase(status)) {
				// its still pending, not too sure whether this is really the
				// actual reversal,
				// what if it has gone to the platform
				transactionInfo.setStatus(SystemConstants.STATUS_TOPUP_REVERSAL_SUCCESSFUL);
				transactionInfo = topupUtil.updateTransactionInfo(transactionInfo);
				response.setNarrative("Pending topup request canceled successfully");
				response.setResponseCode(SystemConstants.RC_OK);
				return response;
			} else if (SystemConstants.STATUS_TOPUP_SUCCESSFUL.equalsIgnoreCase(status)) {
				// now we know that we once posted this TXN to an MNO service,
				// now we delegate to it again to do the reversal,
				// then we handle its response
				ServiceCommand command = reversalRequest.getServiceCommand();
				WSResponse mnoResponse = command.execute(requestToReverse);
				if (mnoResponse == null) {
					// something like a timeout could have happened, or even
					// something harder than that
					transactionInfo.setStatus(SystemConstants.STATUS_TOPUP_REVERSAL_FAILED);
					transactionInfo = topupUtil.updateTransactionInfo(transactionInfo);
					response.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
					response.setNarrative("Reversal TransactionInfo Failed on a previously successful request.");
					return response;
				} else if (SystemConstants.RC_OK.equalsIgnoreCase(mnoResponse.getResponseCode())) {
					transactionInfo.setStatus(SystemConstants.STATUS_TOPUP_REVERSAL_SUCCESSFUL);
					transactionInfo.setNarrative(mnoResponse.getNarrative());
					transactionInfo = topupUtil.updateTransactionInfo(transactionInfo);
					response.setResponseCode(mnoResponse.getResponseCode());
					response.setNarrative(mnoResponse.getNarrative());
					response.setAirtimeBalance(mnoResponse.getAirtimeBalance());
					response.setInitialBalance(mnoResponse.getInitialBalance());
					return response;
				} else {
					transactionInfo.setStatus(SystemConstants.STATUS_TOPUP_REVERSAL_FAILED);
					transactionInfo.setNarrative(mnoResponse.getNarrative());
					transactionInfo = topupUtil.updateTransactionInfo(transactionInfo);
					response.setResponseCode(mnoResponse.getResponseCode());
					response.setNarrative(mnoResponse.getNarrative());
					response.setAirtimeBalance(mnoResponse.getAirtimeBalance());
					response.setInitialBalance(mnoResponse.getInitialBalance());
					return response;
				}
			} else if (SystemConstants.STATUS_TOPUP_FAILED.equalsIgnoreCase(status)) {
				response.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
				response.setNarrative("Reversal not possible, original transaction failed.");
				return response;
			} else {
				// unknown original transaction
				response.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
				response.setNarrative("Unknown status on original transaction [" + transactionInfo.getStatus() + "]");
				return response;
			}
		} catch (Exception e) {
			response.setNarrative("Internal Error. Reversal Could not be processed.");
			response.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			return response;
		}
	}

	private synchronized WSResponse checkDuplicateTransaction(WSRequest request) {
		WSResponse response = new WSResponse();
		response.setRequest(request);
		TransactionInfo txn = topupUtil.findTransactionInfoByRequest(request);

		if (txn != null) {
			response.setNarrative("Duplicate request");
			response.setResponseCode(SystemConstants.RC_DUPLICATE_TXN);
			return response;
		} else {
			response.setResponseCode(SystemConstants.RC_OK);
			return response;
		}

	}

	private WSResponse validateRequest(WSRequest request) {
		String bankId = request.getBankId();
		String uuid = request.getUuid();
		String mobileNumber = request.getTargetMobileNumber();
		WSResponse response = new WSResponse();
		response.setRequest(request);

		response.setResponseCode(SystemConstants.RC_GENERAL_ERROR);

		if (bankId == null || uuid == null || mobileNumber == null || "".equalsIgnoreCase(bankId.trim()) || "".equalsIgnoreCase(uuid.trim()) || "".equalsIgnoreCase(mobileNumber.trim())) {
			response.setNarrative("Incomplete request details");
//		} else if (TopupWebServiceConfig.getInstance().getStringValueOf(request.getBankId() + ".service.id") == null) {
//			response.setNarrative("Invalid bank Id");
		} else {
			// TODO: validate reference number
			response.setResponseCode(SystemConstants.RC_OK);
		}
		return response;
	}

	private ReversalResponse validateReversalRequest(ReversalRequest reversalRequest) {
		WSRequest request = reversalRequest.getRequestToReverse();
		ReversalResponse response = new ReversalResponse();
		response.setReversalRequest(reversalRequest);

		if (reversalRequest.getRequestToReverse() == null) {
			response.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
			response.setNarrative("WSRequest To Reverse has not been specified.");
		}
		String bankId = request.getBankId();
		String mobileNumber = request.getTargetMobileNumber();
		String uuid = request.getUuid();
		// Hypothesis the is a problem with this request until we check and
		// validate
		response.setResponseCode(SystemConstants.RC_GENERAL_ERROR);
		if (bankId == null || mobileNumber == null || uuid == null || "".equalsIgnoreCase(bankId.trim()) || "".equalsIgnoreCase(mobileNumber.trim()) || "".equalsIgnoreCase(uuid.trim())) {
			response.setNarrative("Incomplete request details");
		} else if (TopupWebServiceConfig.getInstance().getStringValueOf(request.getBankId() + ".service.id") == null) {
			response.setNarrative("Invalid bank Id");
		} else {
			// TODO: validate reference number
			response.setResponseCode(SystemConstants.RC_OK);
		}
		return response;
	}
}
