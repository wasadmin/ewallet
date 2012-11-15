package zw.co.esolutions.ussd.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

import org.apache.commons.httpclient.util.DateUtil;

import zw.co.esolutions.ewallet.bankservices.proxy.BankServiceProxy;

/**
 * Entity implementation class for Entity: WebSession
 *
 */
@Entity

public class WebSession implements Serializable {

	   
	@Id
	@Column(length = 30)
	private String id;
	@Version
	private long version;
	@Column(length = 2)
	private int firstIndex;
	@Column(length = 2)
	private int secondIndex;
	@Column(length = 20)
	private String mobileNumber;
	@Column(length = 30)
	private String bankId;
	@Column(length = 20)
	private String status;
	private Date dateCreated;
	@Column(length = 250)
	private String message;
	@Column(length = 30)
	private String referenceId;
	private int passwordRetryCount;
	private Date timeout;
	private boolean sendSms;
	private static final long serialVersionUID = 1L;

	public WebSession() {
		super();
	}   
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}   
	public long getVersion() {
		return this.version;
	}

	public void setVersion(long version) {
		this.version = version;
	}   
	public int getFirstIndex() {
		return this.firstIndex;
	}

	public void setFirstIndex(int firstIndex) {
		this.firstIndex = firstIndex;
	}   
	public int getSecondIndex() {
		return this.secondIndex;
	}

	public void setSecondIndex(int secondIndex) {
		this.secondIndex = secondIndex;
	}   
	public String getMobileNumber() {
		return this.mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}   
	public String getBankId() {
		return this.bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}   
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}   
	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}   
	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}
	public String getReferenceId() {
		return referenceId;
	}
	public void setPasswordRetryCount(int passwordRetryCount) {
		this.passwordRetryCount = passwordRetryCount;
	}
	public int getPasswordRetryCount() {
		return passwordRetryCount;
	}
	public void setTimeout(Date timeout) {
		this.timeout = timeout;
	}
	public Date getTimeout() {
		return timeout;
	}
	public void setSendSms(boolean sendSms) {
		this.sendSms = sendSms;
	}
	public boolean isSendSms() {
		return sendSms;
	}
	
}
