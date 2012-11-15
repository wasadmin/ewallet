package zw.co.esolutions.bankif.model;

import java.io.Serializable;
import java.lang.String;
import java.util.Date;

import javax.persistence.*;

import zw.co.esolutions.bankif.model.BankRequestMessage;
import zw.co.esolutions.ewallet.enums.TransactionType;

/**
 * Entity implementation class for Entity: CommissionMessage
 *
 */
@Entity

@NamedQuery(name = "getCommissionMessagesByBankRequestReference", query = "SELECT c FROM CommissionMessage c WHERE c.bankRequest.reference = :reference")
public class CommissionMessage implements Serializable {
	   
	@Id
	@Column(length = 30)
	private String reference;
	
	@ManyToOne
	@JoinColumn(name="bankRequest", referencedColumnName = "id")
	private BankRequestMessage bankRequest;
	
	@Column(name = "commissionAmount")
	private long amount;
	
	private long systemTraceAuditNumber;
	
	@Column(length = 10)
	private String processingCode;
	
	@Column(length = 30)
	private String status;

	@ManyToOne	
	@JoinColumn(name = "bankResponseCode", referencedColumnName = "responseCode")
	private BankResponseCode bankResponseCode;

	@Column(length = 30)
	private String sourceCustomerAccount;
	
	@Column(length = 30)
	private String sourceEq3Account;
	
	@Column(length = 30)
	private String targetEq3Account;

	@Column(length = 30)
	private String narrative;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 30)
	private TransactionType transactionType;
	
	private Date valueDate;
	
	@Column(length=10)
	private String agentNumber;
	
	private static final long serialVersionUID = 1L;

	public CommissionMessage() {
		super();
	}   
	public String getReference() {
		return this.reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}   
	public BankRequestMessage getBankRequest() {
		return this.bankRequest;
	}

	public void setBankRequest(BankRequestMessage bankRequest) {
		this.bankRequest = bankRequest;
	}   

	/**
	 * @return the systemTraceAuditNumber
	 */
	public long getSystemTraceAuditNumber() {
		return systemTraceAuditNumber;
	}
	/**
	 * @param systemTraceAuditNumber the systemTraceAuditNumber to set
	 */
	public void setSystemTraceAuditNumber(long systemTraceAuditNumber) {
		this.systemTraceAuditNumber = systemTraceAuditNumber;
	}
	public void setProcessingCode(String processingCode) {
		this.processingCode = processingCode;
	}
	/**
	 * @return the processingCode
	 */
	public String getProcessingCode() {
		return processingCode;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the bankResponseCode
	 */
	public BankResponseCode getBankResponseCode() {
		return bankResponseCode;
	}
	/**
	 * @param bankResponseCode the bankResponseCode to set
	 */
	public void setBankResponseCode(BankResponseCode bankResponseCode) {
		this.bankResponseCode = bankResponseCode;
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
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	/**
	 * @return the sourceCustomerAccount
	 */
	public String getSourceCustomerAccount() {
		return sourceCustomerAccount;
	}
	/**
	 * @param sourceCustomerAccount the sourceCustomerAccount to set
	 */
	public void setSourceCustomerAccount(String sourceCustomerAccount) {
		this.sourceCustomerAccount = sourceCustomerAccount;
	}
	/**
	 * @return the sourceEq3Account
	 */
	public String getSourceEq3Account() {
		return sourceEq3Account;
	}
	/**
	 * @param sourceEq3Account the sourceEq3Account to set
	 */
	public void setSourceEq3Account(String sourceEq3Account) {
		this.sourceEq3Account = sourceEq3Account;
	}
	/**
	 * @return the targetEq3Account
	 */
	public String getTargetEq3Account() {
		return targetEq3Account;
	}
	/**
	 * @param targetEq3Account the targetEq3Account to set
	 */
	public void setTargetEq3Account(String targetEq3Account) {
		this.targetEq3Account = targetEq3Account;
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
	 * @return the transactionType
	 */
	public TransactionType getTransactionType() {
		return transactionType;
	}
	/**
	 * @param transactionType the transactionType to set
	 */
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}
	public void setAgentNumber(String agentNumber) {
		this.agentNumber = agentNumber;
	}
	public String getAgentNumber() {
		return agentNumber;
	}
   
}
