package zw.co.esolutions.ewallet.process.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;

import zw.co.esolutions.ewallet.enums.TransactionStatus;

@Entity
@NamedQueries({ @NamedQuery(name = "getTransactionState", query = "SELECT t FROM TransactionState t"), @NamedQuery(name = "getTransactionStateById", query = "SELECT t FROM TransactionState t WHERE t.id = :id"), @NamedQuery(name = "getTransactionStateByStatus", query = "SELECT t FROM TransactionState t WHERE t.status = :status"), @NamedQuery(name = "getTransactionStateByTransaction", query = "SELECT t FROM TransactionState t WHERE t.processTransaction.id = :transaction_id ORDER BY t.dateCreated DESC"), @NamedQuery(name = "getTransactionStateByDateCreated", query = "SELECT t FROM TransactionState t WHERE t.dateCreated = :dateCreated"), @NamedQuery(name = "getTransactionStateByVersion", query = "SELECT t FROM TransactionState t WHERE t.version = :version"), @NamedQuery(name = "getTransactionStateByVariables", query = "SELECT t FROM TransactionState t WHERE t.status = :status AND t.transaction.targetMobileId = :transaction_targetMobile AND t.transaction.secretCode = :transaction_secretCode AND t.transaction.amount = :transaction_amount AND t.transaction.messageId = :transaction_messageId") })
public class TransactionState implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(length = 30)
	private String id;
	@Enumerated(EnumType.STRING)
	@Column(length = 50)
	private TransactionStatus status;
	@ManyToOne
	@JoinColumn(name = "TRANSACTION_ID", referencedColumnName = "id")
	private ProcessTransaction processTransaction;
	@Column(length = 250, name = "narrative")
	private String narrative;
	@Column
	private Date dateCreated;
	@Version
	private long version;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public void setStatus(TransactionStatus status) {
		this.status = status;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	/**
	 * @return the narrative
	 */
	public String getNarrative() {
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
	 * @return the processTransaction
	 */
	public ProcessTransaction getProcessTransaction() {
		return processTransaction;
	}

	/**
	 * @param processTransaction
	 *            the processTransaction to set
	 */
	public void setProcessTransaction(ProcessTransaction processTransaction) {
		this.processTransaction = processTransaction;
	}

}
