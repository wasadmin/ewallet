package zw.co.esolutions.topup.ws.model;

import java.io.Serializable;
import java.lang.String;
import java.util.List;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: MobileNetworkOperator
 * 
 */
@Entity
@Table(schema = "TOPUPWS")
public class MobileNetworkOperator implements Serializable {

	@Id
	@Column(name = "mnoID", length = 30)
	private String mnoID;
	
	@Column(name = "mnoName", length = 60)
	private String mnoName;

	@OneToMany(mappedBy = "targetMNO")
	private List<TransactionInfo> transactionInfos;

	private static final long serialVersionUID = 1L;

	public MobileNetworkOperator() {
		super();
	}

	public String getMnoID() {
		return this.mnoID;
	}

	public void setMnoID(String mnoID) {
		this.mnoID = mnoID;
	}

	public String getMnoName() {
		return this.mnoName;
	}

	public void setMnoName(String mnoName) {
		this.mnoName = mnoName;
	}

	public List<TransactionInfo> getTransactionInfos() {
		return transactionInfos;
	}

	public void setTransactionInfos(List<TransactionInfo> transactionInfos) {
		this.transactionInfos = transactionInfos;
	}

}
