package zw.co.esolutions.bankif.model;

import java.io.Serializable;
import java.lang.String;
import java.util.List;

import javax.persistence.*;

import zw.co.esolutions.ewallet.sms.ResponseCode;
import static javax.persistence.EnumType.STRING;

/**
 * Entity implementation class for Entity: ResponseCode
 * 
 */
@Entity
public class BankResponseCode implements Serializable {

	@Id
	@Column(name = "responseCode", length = 10)
	private String responseCode;
	
	@Column(name = "narrative", length = 250)
	private String narrative;
	
	@Column(name="eWtResponseCode", length=10)
	@Enumerated(STRING)
	private ResponseCode eWtResponseCode;
	
	@OneToMany(mappedBy = "bankResponseCode")
	private List<CommissionMessage> commissionMessages;
	
	@OneToMany(mappedBy = "bankResponseCode")
	private List<BankRequestMessage> bankRequestMessages;
	
	private static final long serialVersionUID = 1L;

	public BankResponseCode() {
		super();
	}

	public String getResponseCode() {
		return this.responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getNarrative() {
		return this.narrative;
	}

	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}

	public List<CommissionMessage> getCommissionMessages() {
		return commissionMessages;
	}

	public void setCommissionMessages(List<CommissionMessage> commissionMessages) {
		this.commissionMessages = commissionMessages;
	}

	public List<BankRequestMessage> getBankRequestMessages() {
		return bankRequestMessages;
	}

	public void setBankRequestMessages(List<BankRequestMessage> bankRequestMessages) {
		this.bankRequestMessages = bankRequestMessages;
	}

	public ResponseCode geteWtResponseCode() {
		return eWtResponseCode;
	}

	public void seteWtResponseCode(ResponseCode eWtResponseCode) {
		this.eWtResponseCode = eWtResponseCode;
	}

}
