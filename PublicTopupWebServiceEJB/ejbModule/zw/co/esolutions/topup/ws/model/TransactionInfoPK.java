package zw.co.esolutions.topup.ws.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class TransactionInfoPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3298933283325174126L;
	@Column(name = "uuid", length = 30)
	protected String uuid;
	
	@Column(name = "bankId", length = 30)
	protected String bankId;
	
	@Column(name = "transactionType", length = 30)
	protected String transactionType;

	public TransactionInfoPK() {
		
	}

	public TransactionInfoPK(String uuid, String bankId, String transactionType) {
		super();
		this.uuid = uuid;
		this.bankId = bankId;
		this.transactionType = transactionType;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == this){
			return true;
		}	
		if(o == null){
			return false;
		}
		if(o instanceof TransactionInfoPK){
			TransactionInfoPK otherKey = (TransactionInfoPK)o;
			return this.getUuid().equalsIgnoreCase(otherKey.getUuid()) && this.getBankId().equalsIgnoreCase(otherKey.getBankId()) ;//&& this.getTransactionType().equals(otherKey.getTransactionType());
		}else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return this.getUuid().hashCode() + this.getBankId().hashCode(); //+ this.getTransactionType().hashCode();
	}
}
