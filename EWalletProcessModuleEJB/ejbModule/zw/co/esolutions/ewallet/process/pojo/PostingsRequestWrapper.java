package zw.co.esolutions.ewallet.process.pojo;

import java.io.Serializable;
import java.util.List;

import zw.co.esolutions.ewallet.bankservices.service.ChargePostingInfo;
import zw.co.esolutions.ewallet.bankservices.service.TransactionPostingInfo;
import zw.co.esolutions.ewallet.sms.ResponseCode;

public class PostingsRequestWrapper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1331885576757493198L;
	private List<TransactionPostingInfo> transactionPostingInfos;
	private List<ChargePostingInfo> chargePostingsInfos;
	private ResponseCode responseCode;
	
	public PostingsRequestWrapper() {
		// TODO Auto-generated constructor stub
	}

	public List<TransactionPostingInfo> getTransactionPostingInfos() {
		return transactionPostingInfos;
	}

	public void setTransactionPostingInfos(
			List<TransactionPostingInfo> transactionPostingInfos) {
		this.transactionPostingInfos = transactionPostingInfos;
	}

	public List<ChargePostingInfo> getChargePostingsInfos() {
		return chargePostingsInfos;
	}

	public void setChargePostingsInfos(List<ChargePostingInfo> chargePostingsInfos) {
		this.chargePostingsInfos = chargePostingsInfos;
	}

	public void setResponseCode(ResponseCode responseCode) {
		this.responseCode = responseCode;
	}

	public ResponseCode getResponseCode() {
		return responseCode;
	}

	
	
	
}
