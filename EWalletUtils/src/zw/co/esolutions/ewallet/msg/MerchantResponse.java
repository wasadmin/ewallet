/**
 * 
 */
package zw.co.esolutions.ewallet.msg;

import java.io.Serializable;

import zw.co.esolutions.ewallet.enums.ResponseType;
import zw.co.esolutions.ewallet.sms.ResponseCode;

/**
 * @author blessing
 *
 */
public class MerchantResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MerchantRequest merchantRequest;
	private String merchantResponseCode;
	private ResponseCode responseCode;
	private String narrative;
	private long currentBalance;
	private long preBalance;
	private long postBalance;
	private ResponseType responseType;
	private ResponseInfo responseInfo;
	
	
	public MerchantResponse() {
		// TODO Auto-generated constructor stub
	}


	public MerchantRequest getMerchantRequest() {
		return merchantRequest;
	}


	public void setMerchantRequest(MerchantRequest merchantRequest) {
		this.merchantRequest = merchantRequest;
	}


	public String getMerchantResponseCode() {
		return merchantResponseCode;
	}


	public void setMerchantResponseCode(String merchantResponseCode) {
		this.merchantResponseCode = merchantResponseCode;
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


	public long getCurrentBalance() {
		return currentBalance;
	}


	public void setCurrentBalance(long currentBalance) {
		this.currentBalance = currentBalance;
	}


	public long getPreBalance() {
		return preBalance;
	}


	public void setPreBalance(long preBalance) {
		this.preBalance = preBalance;
	}


	public long getPostBalance() {
		return postBalance;
	}


	public void setPostBalance(long postBalance) {
		this.postBalance = postBalance;
	}


	public void setResponseType(ResponseType responseType) {
		this.responseType = responseType;
	}


	public ResponseType getResponseType() {
		return responseType;
	}


	public void setResponseInfo(ResponseInfo responseInfo) {
		this.responseInfo = responseInfo;
	}


	public ResponseInfo getResponseInfo() {
		return responseInfo;
	}

}
