package zw.co.esolutions.bankif.model;

import java.io.Serializable;
import java.lang.String;
import javax.persistence.*;

import zw.co.esolutions.ewallet.enums.TransactionType;
import static javax.persistence.EnumType.STRING;

/**
 * Entity implementation class for Entity: TransactionInfo
 * 
 */
@Entity(name = "TransactionInfo")
@NamedQueries({ @NamedQuery(name = "findTransactionInfoByTransactionType", query = "SELECT t FROM TransactionInfo t WHERE t.transactionType = :transactionType"), @NamedQuery(name = "findTransactionInfoByProcessingCode", query = "SELECT t FROM TransactionInfo t WHERE t.processingCode = :processingCode") })
public class TransactionInfo implements Serializable {

	@Id
	@Column(length = 30)
	private String id;

	@Column(length = 50, unique = true)
	@Enumerated(STRING)
	private TransactionType transactionType;

	@Column(length = 5)
	private String processingCode;

	private static final long serialVersionUID = 1L;

	public TransactionInfo() {
		super();
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProcessingCode() {
		return this.processingCode;
	}

	public void setProcessingCode(String processingCode) {
		this.processingCode = processingCode;
	}

	/**
	 * @return the transactionType
	 */
	public TransactionType getTransactionType() {
		return transactionType;
	}

	/**
	 * @param transactionType
	 *            the transactionType to set
	 */
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

}
