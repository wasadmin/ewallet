package zw.co.esolutions.merchants.beans;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.Version;

import zw.co.esolutions.mcommerce.xml.ISOMsg;

/**
 * Entity implementation class for Entity: TransactionState
 *
 */
@Entity
public class TransactionState implements Serializable {

	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;
	
	@Column(length = 20)
	private String status;
	
	@Temporal(TIMESTAMP)
	private Date txnStateTimestamp;
	
	@Column(length = 200)
	private String narrative;
	
	@Version
	private long version;

	@ManyToOne
	@JoinColumn(name = "txnRef", referencedColumnName = "txnRef")
	private ISOMsg isoMsg;
	
	public TransactionState() {
		super();
	}
	
	public TransactionState(String status, Date txnStateTimestamp, String narrative) {
		super();
		this.status = status;
		this.txnStateTimestamp = txnStateTimestamp;
		this.narrative = narrative;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getTxnStateTimestamp() {
		return txnStateTimestamp;
	}

	public void setTxnStateTimestamp(Date txnStateTimestamp) {
		this.txnStateTimestamp = txnStateTimestamp;
	}

	public String getNarrative() {
		return narrative;
	}

	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public ISOMsg getIsoMsg() {
		return isoMsg;
	}

	public void setIsoMsg(ISOMsg isoMsg) {
		this.isoMsg = isoMsg;
	}
	
}
