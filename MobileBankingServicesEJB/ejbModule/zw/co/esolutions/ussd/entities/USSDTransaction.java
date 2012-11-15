package zw.co.esolutions.ussd.entities;

import java.io.Serializable;
import java.lang.String;
import java.util.Date;
import javax.persistence.*;

import zw.co.esolutions.ussd.util.FlowStatus;

/**
 * Entity implementation class for Entity: USSDTransaction
 * Author Tau
 */
@Entity

public class USSDTransaction implements Serializable {

	   
	@Id
	@Column(length = 30)
	private String uuid;
	@Column(length = 30)
	private String sessionId;
	@Column(length=250)
	private String message;
	@Column(length=30)
	@Enumerated(EnumType.STRING)
	FlowStatus flowStatus;
	@Column(length=20)
	private String sourceMobile;
	private boolean sendSms;
	@Version
	private long version;
	private Date dateCreated;
	@Column(length=30)
	private String bankCode;
	@Column(length=30)
	private String aquirerId;
	@Column(length=30)
	private String mno;
	private static final long serialVersionUID = 1L;

	public USSDTransaction() {
		super();
	}  
	
	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getAquirerId() {
		return aquirerId;
	}

	public void setAquirerId(String aquirerId) {
		this.aquirerId = aquirerId;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}   
	public long getVersion() {
		return this.version;
	}

	public void setVersion(long version) {
		this.version = version;
	}   
	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public FlowStatus getFlowStatus() {
		return flowStatus;
	}

	public void setFlowStatus(FlowStatus flowStatus) {
		this.flowStatus = flowStatus;
	}

	public void setSourceMobile(String sourceMobile) {
		this.sourceMobile = sourceMobile;
	}

	public String getSourceMobile() {
		return sourceMobile;
	}

	public void setSendSms(boolean sendSms) {
		this.sendSms = sendSms;
	}

	public boolean isSendSms() {
		return sendSms;
	}

	public void setMno(String mno) {
		this.mno = mno;
	}

	public String getMno() {
		return mno;
	}
   
}
