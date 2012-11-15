package zw.co.esolutions.ewallet.process.model;

import java.io.Serializable;
import java.lang.String;
import java.util.Date;
import javax.persistence.*;

import zw.co.esolutions.ewallet.enums.AgentType;
import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.enums.TransactionType;

/**
 * Entity implementation class for Entity: TransactionCharge
 *
 */
@Entity

@NamedQueries({@NamedQuery(name="getTransactionCharge", query = "SELECT t FROM TransactionCharge t"),
@NamedQuery(name="getTransactionChargeById", query = "SELECT t FROM TransactionCharge t WHERE t.id = :id"),
@NamedQuery(name="getTransactionChargeByProcessTransaction", query = "SELECT t FROM TransactionCharge t WHERE t.processTransaction.id = :processTransaction_id"),
@NamedQuery(name="getTransactionChargesByMessageId", query = "SELECT t FROM TransactionCharge t WHERE t.processTransaction.messageId = :messageId"),
@NamedQuery(name="getTransactionChargesByMessageIdAndAgentType", query = "SELECT t FROM TransactionCharge t WHERE t.processTransaction.messageId = :messageId AND t.agentType = :agentType"),
@NamedQuery(name="getTransactionChargeByTariffId", query = "SELECT t FROM TransactionCharge t WHERE t.tariffId = :tariffId")})
public class TransactionCharge implements Serializable {

	   
	@Id
	@Column(length = 30)
	private String id;
	@Column(length = 30)
	private String tariffId;
	private long tariffAmount;
	private Date dateCreated;
	@Version
	private long version;
	@Enumerated(EnumType.STRING)
	@Column(length = 5)
	private AgentType agentType;
	@Enumerated(EnumType.STRING)
	@Column(length = 30)
	private TransactionType transactionType;
	
	@Column(length = 30)
	private String fromEwalletAccount;
	
	@Column(length = 30)
	private String toEwalletAccount;
	
	@Column(length = 30)
	private String fromEQ3Account;
	
	@Column(length = 30)
	private String toEQ3Account;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 30)
	private TransactionStatus status;
	
	@Column(length = 30)
	private String chargeTransactionClass;
	
	@ManyToOne
	@JoinColumn(name="processTransaction", referencedColumnName = "id")
	private ProcessTransaction processTransaction;
	
	@Column(length=10)
	private String agentNumber;
	
	private static final long serialVersionUID = 1L;

	public TransactionCharge() {
		super();
	}   
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}   
	public String getTariffId() {
		return this.tariffId;
	}

	public void setTariffId(String tariffId) {
		this.tariffId = tariffId;
	}   
	public long getTariffAmount() {
		return this.tariffAmount;
	}

	public void setTariffAmount(long tariffAmount) {
		this.tariffAmount = tariffAmount;
	}   
	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}   
	public long getVersion() {
		return this.version;
	}

	public void setVersion(long version) {
		this.version = version;
	}
	public void setProcessTransaction(ProcessTransaction processTransaction) {
		this.processTransaction = processTransaction;
	}
	public ProcessTransaction getProcessTransaction() {
		return processTransaction;
	}
	public void setAgentType(AgentType agentType) {
		this.agentType = agentType;
	}
	public AgentType getAgentType() {
		return agentType;
	}
	public TransactionType getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}
	public String getFromEQ3Account() {
		return fromEQ3Account;
	}
	public void setFromEQ3Account(String fromEQ3Account) {
		this.fromEQ3Account = fromEQ3Account;
	}
	public String getToEQ3Account() {
		return toEQ3Account;
	}
	public void setToEQ3Account(String toEQ3Account) {
		this.toEQ3Account = toEQ3Account;
	}
	public TransactionStatus getStatus() {
		return status;
	}
	public void setStatus(TransactionStatus status) {
		this.status = status;
	}
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
	public String getFromEwalletAccount() {
		return fromEwalletAccount;
	}
	public void setFromEwalletAccount(String fromEwalletAccount) {
		this.fromEwalletAccount = fromEwalletAccount;
	}
	public String getToEwalletAccount() {
		return toEwalletAccount;
	}
	public void setToEwalletAccount(String toEwalletAccount) {
		this.toEwalletAccount = toEwalletAccount;
	}
	public void setChargeTransactionClass(String chargeTransactionClass) {
		this.chargeTransactionClass = chargeTransactionClass;
	}
	public String getChargeTransactionClass() {
		return chargeTransactionClass;
	}
	public void setAgentNumber(String agentNumber) {
		this.agentNumber = agentNumber;
	}
	public String getAgentNumber() {
		return agentNumber;
	}
}
