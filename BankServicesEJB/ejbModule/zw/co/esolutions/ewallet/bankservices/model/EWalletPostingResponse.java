package zw.co.esolutions.ewallet.bankservices.model;

import java.io.Serializable;
import java.util.List;

import zw.co.esolutions.ewallet.sms.ResponseCode;

public class EWalletPostingResponse implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private TransactionPostingInfo [] transactionPostingInfos;
	private ChargePostingInfo [] chargePostingInfos;
	
	private ResponseCode responseCode;
	private String narrative;

	public EWalletPostingResponse(TransactionPostingInfo [] transactionPostingInfos, ChargePostingInfo [] chargePostingInfos, ResponseCode responseCode, String narrative) {
		super();
		this.transactionPostingInfos = transactionPostingInfos;
		this.chargePostingInfos = chargePostingInfos;
		this.responseCode = responseCode;
		this.narrative = narrative;
	}

	public EWalletPostingResponse() {
		// TODO Auto-generated constructor stub
	}

	public ResponseCode getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(ResponseCode responseCode) {
		this.responseCode = responseCode;
	}

	public String getNarrative() {
		return narrative;
	}

	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}

	public TransactionPostingInfo[] getTransactionPostingInfos() {
		return transactionPostingInfos;
	}

	public void setTransactionPostingInfos(TransactionPostingInfo[] transactionPostingInfos) {
		this.transactionPostingInfos = transactionPostingInfos;
	}

	public ChargePostingInfo[] getChargePostingInfos() {
		return chargePostingInfos;
	}

	public void setChargePostingInfos(ChargePostingInfo[] chargePostingInfos) {
		this.chargePostingInfos = chargePostingInfos;
	}

	
}
