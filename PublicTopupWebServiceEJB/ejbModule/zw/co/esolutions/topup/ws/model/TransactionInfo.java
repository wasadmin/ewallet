package zw.co.esolutions.topup.ws.model;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;

import zw.co.esolutions.topup.ws.util.SystemConstants;
import zw.co.esolutions.topup.ws.util.WSRequest;

/**
 * Entity implementation class for Entity: TransactionInfo
 * 
 */
@Entity
@Table(schema = "TOPUPWS")
public class TransactionInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	TransactionInfoPK transactionId;

	@Column(length = 20)
	private String serviceId;

	@Column(length = 30)
	private String serviceProviderId;

	@Column(length = 255)
	private String narrative;

	private boolean notify;

	@Temporal(TIMESTAMP)
	private java.util.Date valueDate;

	@Column(length = 12)
	private String mobileOperatorId;

	@Column(length = 32)
	private String status;

	private long amount;

	@Column(length = 4)
	private String responseCode;

	@Temporal(TIMESTAMP)
	private java.util.Date dateReceived;

	@Column(length = 32)
	private String sourceMobileNumber;

	@Column(length = 32)
	private String targetMobileNumber;

	@Column(length = 50)
	private String operatorReference;

	private double airtimeBalance;

	private double initialBalance;

	@ManyToOne
	@JoinColumn(name = "targetMNO", referencedColumnName = "mnoID")
	private MobileNetworkOperator targetMNO;

	public TransactionInfo() {
		super();
	}

	public TransactionInfo(WSRequest request) {
		TransactionInfoPK pk = new TransactionInfoPK(request.getUuid(), request.getBankId(), request.getServiceCommand().name());
		this.setTransactionId(pk);
		this.setSourceMobileNumber(request.getSourceMobileNumber());
		this.setTargetMobileNumber(request.getTargetMobileNumber());
		this.setAmount(request.getAmount());
		this.setDateReceived(new Date(System.currentTimeMillis()));
		this.setServiceId(request.getServiceId());
		this.setServiceProviderId(request.getServiceProviderId());
		this.setStatus(SystemConstants.STATUS_TOPUP_PENDING);
	}

	public String getMobileOperatorId() {
		return mobileOperatorId;
	}

	public void setMobileOperatorId(String mobileOperatorId) {
		this.mobileOperatorId = mobileOperatorId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getOperatorReference() {
		return operatorReference;
	}

	public void setOperatorReference(String operatorReference) {
		this.operatorReference = operatorReference;
	}

	public double getAirtimeBalance() {
		return airtimeBalance;
	}

	public void setAirtimeBalance(double airtimeBalance) {
		this.airtimeBalance = airtimeBalance;
	}

	public String getNarrative() {
		return narrative;
	}

	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}

	public boolean isNotify() {
		return notify;
	}

	public void setNotify(boolean notify) {
		this.notify = notify;
	}

	public TransactionInfoPK getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(TransactionInfoPK transactionId) {
		this.transactionId = transactionId;
	}

	public MobileNetworkOperator getTargetMNO() {
		return targetMNO;
	}

	public void setTargetMNO(MobileNetworkOperator targetMNO) {
		this.targetMNO = targetMNO;
	}

	public double getInitialBalance() {
		return initialBalance;
	}

	public void setInitialBalance(double initialBalance) {
		this.initialBalance = initialBalance;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceProviderId() {
		return serviceProviderId;
	}

	public void setServiceProviderId(String serviceProviderId) {
		this.serviceProviderId = serviceProviderId;
	}

	public java.util.Date getValueDate() {
		return valueDate;
	}

	public void setValueDate(java.util.Date valueDate) {
		this.valueDate = valueDate;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public java.util.Date getDateReceived() {
		return dateReceived;
	}

	public void setDateReceived(java.util.Date dateReceived) {
		this.dateReceived = dateReceived;
	}

	public String getSourceMobileNumber() {
		return sourceMobileNumber;
	}

	public void setSourceMobileNumber(String sourceMobileNumber) {
		this.sourceMobileNumber = sourceMobileNumber;
	}

	public String getTargetMobileNumber() {
		return targetMobileNumber;
	}

	public void setTargetMobileNumber(String targetMobileNumber) {
		this.targetMobileNumber = targetMobileNumber;
	}

}
