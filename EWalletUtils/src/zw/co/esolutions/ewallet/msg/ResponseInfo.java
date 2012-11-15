package zw.co.esolutions.ewallet.msg;

import java.io.Serializable;

import zw.co.esolutions.ewallet.enums.ResponseType;
import zw.co.esolutions.ewallet.sms.ResponseCode;

public class ResponseInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String narrative;
	private ResponseCode responseCode;
	private RequestInfo requestInfo;
	private ResponseType responseType;
	private long ledgerBalance;
	private long availableBalance;
	private long targetAccountAvailableBalance;
	private long merchantAccBalance;
	private String messageId;
	private boolean autoregPerformed;
	private String autoregPassword;
	
	public ResponseInfo() {

	}
	
	public ResponseInfo(String narrative, ResponseCode responseCode, RequestInfo requestInfo, ResponseType responseType, long ledgerBalance, long availableBalance, long targetAccountAvailableBalance, String messageId) {
		super();
		this.narrative = narrative;
		this.responseCode = responseCode;
		this.requestInfo = requestInfo;
		this.responseType = responseType;
		this.ledgerBalance = ledgerBalance;
		this.availableBalance = availableBalance;
		this.targetAccountAvailableBalance = targetAccountAvailableBalance;
		this.messageId = messageId;
	}

	/**
	 * @return the narrative
	 */
	public String getNarrative() {
		if(this.narrative != null) {
			if(this.narrative.endsWith(".")) {
				this.narrative = narrative.substring(0, narrative.length() - 1);
			}
		}
		return narrative;
	}

	/**
	 * @param narrative
	 *            the narrative to set
	 */
	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}

	/**
	 * @return the responseCode
	 */
	public ResponseCode getResponseCode() {
		return responseCode;
	}

	/**
	 * @param responseCode
	 *            the responseCode to set
	 */
	public void setResponseCode(ResponseCode responseCode) {
		this.responseCode = responseCode;
	}

	/**
	 * @return the requestInfo
	 */
	public RequestInfo getRequestInfo() {
		return requestInfo;
	}

	/**
	 * @param requestInfo
	 *            the requestInfo to set
	 */
	public void setRequestInfo(RequestInfo requestInfo) {
		this.requestInfo = requestInfo;
	}

	/**
	 * @return the responseType
	 */
	public ResponseType getResponseType() {
		return responseType;
	}

	/**
	 * @param responseType
	 *            the responseType to set
	 */
	public void setResponseType(ResponseType responseType) {
		this.responseType = responseType;
	}



	/**
	 * @return the ledgerBalance
	 */
	public long getLedgerBalance() {
		return ledgerBalance;
	}



	/**
	 * @param ledgerBalance the ledgerBalance to set
	 */
	public void setLedgerBalance(long ledgerBalance) {
		this.ledgerBalance = ledgerBalance;
	}



	/**
	 * @return the availableBalance
	 */
	public long getAvailableBalance() {
		return availableBalance;
	}



	/**
	 * @param availableBalance the availableBalance to set
	 */
	public void setAvailableBalance(long availableBalance) {
		this.availableBalance = availableBalance;
	}



	/**
	 * @return the targetAccountAvailableBalance
	 */
	public long getTargetAccountAvailableBalance() {
		return targetAccountAvailableBalance;
	}



	/**
	 * @param targetAccountAvailableBalance the targetAccountAvailableBalance to set
	 */
	public void setTargetAccountAvailableBalance(long targetAccountAvailableBalance) {
		this.targetAccountAvailableBalance = targetAccountAvailableBalance;
	}

	public void setMerchantAccBalance(long merchantAccBalance) {
		this.merchantAccBalance = merchantAccBalance;
	}

	public long getMerchantAccBalance() {
		return merchantAccBalance;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setAutoregPerformed(boolean autoregPerformed) {
		this.autoregPerformed = autoregPerformed;
	}

	public boolean isAutoregPerformed() {
		return autoregPerformed;
	}

	public void setAutoregPassword(String autoregPassword) {
		this.autoregPassword = autoregPassword;
	}

	public String getAutoregPassword() {
		return autoregPassword;
	}

}
