/**
 * 
 */
package zw.co.esolutions.ewallet.msg;

import java.io.Serializable;
import java.util.Date;

import zw.co.esolutions.ewallet.sms.ResponseCode;

/**
 * @author tauttee
 *
 */
public class BankResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private BankRequest bankRequest;
	private long availableBalance;
	private long ledgerBalance;
	private ResponseCode responseCode;
	private String bankReference;
	private String narrative;
	private String processingCode;
	private Date valueDate;
	private long referrerCommission;
	private long referredCommission;
	private int trace;
	private String messageType;
	private String additionalData;
	private String bankResponseCode;
	
	public BankResponse() {
		super();
	}

	/**
	 * @return the bankRequest
	 */
	public BankRequest getBankRequest() {
		return bankRequest;
	}

	/**
	 * @param bankRequest the bankRequest to set
	 */
	public void setBankRequest(BankRequest bankRequest) {
		this.bankRequest = bankRequest;
	}

	/**
	 * @return the narrative
	 */
	public String getNarrative() {
		return narrative;
	}

	/**
	 * @param narrative the narrative to set
	 */
	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}

	/**
	 * @return the valueDate
	 */
	public Date getValueDate() {
		return valueDate;
	}

	/**
	 * @param valueDate the valueDate to set
	 */
	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	/**
	 * @return the referrerCommission
	 */
	public long getReferrerCommission() {
		return referrerCommission;
	}

	/**
	 * @param referrerCommission the referrerCommission to set
	 */
	public void setReferrerCommission(long referrerCommission) {
		this.referrerCommission = referrerCommission;
	}

	/**
	 * @return the referredCommission
	 */
	public long getReferredCommission() {
		return referredCommission;
	}

	/**
	 * @param referredCommission the referredCommission to set
	 */
	public void setReferredCommission(long referredCommission) {
		this.referredCommission = referredCommission;
	}

	/**
	 * @return the trace
	 */
	public int getTrace() {
		return trace;
	}

	/**
	 * @param trace the trace to set
	 */
	public void setTrace(int trace) {
		this.trace = trace;
	}

	
	/**
	 * @return the bankReference
	 */
	public String getBankReference() {
		return bankReference;
	}

	/**
	 * @param bankReference the bankReference to set
	 */
	public void setBankReference(String bankReference) {
		this.bankReference = bankReference;
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
	 * @return the messageType
	 */
	public String getMessageType() {
		return messageType;
	}

	/**
	 * @param messageType the messageType to set
	 */
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	/**
	 * @return the additionalData
	 */
	public String getAdditionalData() {
		return additionalData;
	}

	/**
	 * @param additionalData the additionalData to set
	 */
	public void setAdditionalData(String additionalData) {
		this.additionalData = additionalData;
	}

	/**
	 * @return the processingCode
	 */
	public String getProcessingCode() {
		return processingCode;
	}

	/**
	 * @param processingCode the processingCode to set
	 */
	public void setProcessingCode(String processingCode) {
		this.processingCode = processingCode;
	}

	/**
	 * @return the responseCode
	 */
	public ResponseCode getResponseCode() {
		return responseCode;
	}

	/**
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(ResponseCode responseCode) {
		this.responseCode = responseCode;
	}

	/**
	 * @return the bankResponseCode
	 */
	public String getBankResponseCode() {
		return bankResponseCode;
	}

	/**
	 * @param bankResponseCode the bankResponseCode to set
	 */
	public void setBankResponseCode(String bankResponseCode) {
		this.bankResponseCode = bankResponseCode;
	}

	
	@Override
	public String toString() {
		return this.getMessageType() +"|"+ this.getResponseCode() +"|" +this.getBankResponseCode() + "|" + this.getNarrative() + "|" + this.getBankRequest();
	}
}
