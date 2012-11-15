package zw.co.esolutions.ussd.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

import zw.co.esolutions.ewallet.util.GenerateKey;

@Entity
public class UserAccount {
	
	@Id
	private String id;
	@Column(length=30)
	private String accountNumber;
	@Column(length=25)
	private String accountType;
	@Column(length=30)
	private String merchantName;
	
	@Version
	private long version;
	
	
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getAccountNumber() {
		return accountNumber;
	}
	
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public UserAccount() {
		super();
		this.setId(GenerateKey.generateEntityId());
	}

	public UserAccount(String accountNumber, String accountType) {
		this();
		this.accountNumber = accountNumber;
		this.accountType = accountType;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}
	
	
	
	

}
