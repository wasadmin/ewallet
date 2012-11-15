package zw.co.esolutions.ewallet.bankservices.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;

import zw.co.esolutions.ewallet.audit.Auditable;
import zw.co.esolutions.ewallet.enums.BankAccountClass;
import zw.co.esolutions.ewallet.enums.BankAccountLevel;
import zw.co.esolutions.ewallet.enums.BankAccountStatus;
import zw.co.esolutions.ewallet.enums.BankAccountType;
import zw.co.esolutions.ewallet.enums.OwnerType;
import zw.co.esolutions.ewallet.util.MapUtil;

@Entity
@NamedQueries({ @NamedQuery(name = "getBankAccountByLevel", query = "SELECT b FROM BankAccount b WHERE b.level =: level"), 
	@NamedQuery(name = "getBankAccountByType", query = "SELECT b FROM BankAccount b WHERE b.type =: type"), 
	@NamedQuery(name = "getBankAccountByStatus", query = "SELECT b FROM BankAccount b WHERE b.status =: status"), 
	@NamedQuery(name = "getUniqueBankAccountByAccountNumberAndBankId", query = "SELECT b FROM BankAccount b WHERE b.accountNumber =:accountNumber AND b.status <> :status AND b.branch.bank.id = :bankId"), 
	@NamedQuery(name = "getBankAccountByStatusAndOwnerType", query = "SELECT b FROM BankAccount b WHERE b.status =: status AND b.ownerType =:ownerType ORDER BY b.accountHolderId DESC, b.type ASC"), 
	@NamedQuery(name = "getBankAccountsAwaitingApproval", query = "SELECT b FROM BankAccount b WHERE b.status =: status AND (b.ownerType =:ownerType1 OR b.ownerType =:ownerType2 OR b.ownerType =:ownerType3) ORDER BY b.dateCreated DESC"), 
	@NamedQuery(name = "getBankAccountByBank", query = "SELECT b FROM BankAccount b WHERE b.bankBranch.bank.id =: bankBranch_bank_id"), 
	@NamedQuery(name = "getBankAccountByAccountHolderIdAndOwnerType", query = "SELECT b FROM BankAccount b WHERE b.accountHolderId =: accountHolderId AND b.ownerType = :ownerType AND b.status <> :status ORDER BY b.accountHolderId ASC, b.type ASC"), 
	@NamedQuery(name = "getBankAccountByBranch", query = "SELECT b FROM BankAccount b WHERE b.bankBranch.id =: bankBranch_id"), 
	@NamedQuery(name = "getBankAccountByAccountNumberAndOwnerType", query = "SELECT b FROM BankAccount b WHERE b.accountNumber =:accountNumber AND b.status <> :status AND b.ownerType = :ownerType"), 
	@NamedQuery(name = "getBankAccountsByAccountNumber", query = "SELECT b FROM BankAccount b WHERE b.accountNumber LIKE :accountNumber"), 
	@NamedQuery(name = "getBankAccount", query = "SELECT b FROM BankAccount b WHERE b.status <> :status"), 
	@NamedQuery(name = "getBankAccountsByAccountHolderIdAndOwnerTypeAndStatus", query = "SELECT b FROM BankAccount b WHERE  b.accountHolderId =: accountHolderId AND b.status =: status AND b.ownerType =: ownerType AND b.type <> :ewallet AND b.type <> :agentewallet"), 
	@NamedQuery(name = "getBankAccountsByAccountHolderIdAndOwnerTypeAndStatusAndBankAccountType", query = "SELECT b FROM BankAccount b WHERE b.accountHolderId =: accountHolderId AND b.status =: status AND b.type = :bankAccountType AND b.ownerType =: ownerType"), 
	@NamedQuery(name = "getBankAccountsByAccountHolderIdAndOwnerTypeAndBankAccountType", query = "SELECT b FROM BankAccount b WHERE b.accountHolderId =: accountHolderId AND b.status <>: status AND b.type = :bankAccountType AND b.ownerType =: ownerType "),
	@NamedQuery(name = "getUniqueBankAccountByAccountNumber", query = "SELECT b FROM BankAccount b WHERE b.accountNumber =:accountNumber AND b.status <> :status"), 
	@NamedQuery(name = "getBankAccountsByOwnerId", query = "SELECT b FROM BankAccount b WHERE b.accountHolderId = :ownerId AND b.status <> :status ORDER BY b.accountName ASC"), 
	@NamedQuery(name = "getBankAccountByAccountHolderAndTypeAndOwnerType", query = "SELECT b FROM BankAccount b WHERE b.accountHolderId = :accountHolderId AND b.type = :type AND b.ownerType =:ownerType AND b.status <> :status "), 
	@NamedQuery(name = "getBankAccountByLevelAndStatus", query = "SELECT b FROM BankAccount b WHERE b.level = :level AND b.status = :status"), 
	@NamedQuery(name = "getPrimaryAccountByAccountHolderIdAndOwnerType", query = "SELECT b FROM BankAccount b WHERE b.primaryAccount = :primaryAccount AND b.accountHolderId =: accountHolderId AND b.ownerType = :ownerType AND b.status <> :status") })
public class BankAccount implements Auditable {

	@Id
	@Column(length = 30)
	private String id;
	@Column(length = 30)
	private String accountHolderId;
	@Column(length = 50)
	private String accountName;
	@Column(length = 30)
	private String accountNumber;
	@Enumerated(EnumType.STRING)
	@Column(length = 50)
	private BankAccountStatus status;
	@Enumerated(EnumType.STRING)
	@Column(length = 50)
	private BankAccountType type;
	@Enumerated(EnumType.STRING)
	@Column(length = 50)
	private BankAccountLevel level;
	@Enumerated(EnumType.STRING)
	@Column(length = 50)
	private BankAccountClass accountClass;
	@Column(length = 30)
	private String bankReferenceId;
	@Column
	private long runningBalance;
	@ManyToOne
	private BankBranch branch;
	@Column(length = 30)
	private String cardNumber;
	@Column
	private int cardSequence;
	@Column(length = 30)
	private String code;
	@Column(length = 30)
	private String registrationBranchId;
	@Column(length = 40)
	private String bankAccountLastBranch;
	@Column
	private Date dateCreated;
	@Column
	private boolean primaryAccount;
	@Enumerated(EnumType.STRING)
	@Column(length = 50)
	private OwnerType ownerType;
	@Version
	@Column
	private long version;
	@SuppressWarnings("unused")
	private transient boolean approvable;
	private transient boolean bankAccRenderApprovable;
	private transient boolean active;
	private transient boolean pendingApproval;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAccountHolderId() {
		return accountHolderId;
	}

	public void setAccountHolderId(String accountHolderId) {
		this.accountHolderId = accountHolderId;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountName() {
		return accountName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public BankAccountStatus getStatus() {
		return status;
	}

	public void setStatus(BankAccountStatus status) {
		this.status = status;
	}

	public BankAccountType getType() {
		return type;
	}

	public void setType(BankAccountType type) {
		this.type = type;
	}

	public BankAccountLevel getLevel() {
		return level;
	}

	public void setLevel(BankAccountLevel level) {
		this.level = level;
	}

	public void setAccountClass(BankAccountClass accountClass) {
		this.accountClass = accountClass;
	}

	public BankAccountClass getAccountClass() {
		return accountClass;
	}

	public void setBankReferenceId(String bankReferenceId) {
		this.bankReferenceId = bankReferenceId;
	}

	public String getBankReferenceId() {
		return bankReferenceId;
	}

	public void setRunningBalance(long runningBalance) {
		this.runningBalance = runningBalance;
	}

	public long getRunningBalance() {
		return runningBalance;
	}

	public void setBranch(BankBranch branch) {
		this.branch = branch;
	}

	public BankBranch getBranch() {
		return branch;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public int getCardSequence() {
		return cardSequence;
	}

	public void setCardSequence(int cardSequence) {
		this.cardSequence = cardSequence;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public long getVersion() {
		return version;
	}

	public boolean isApprovable() {
		if (BankAccountStatus.AWAITING_APPROVAL.equals(status)) {
			return true;
		}
		return false;
	}

	public void setApprovable(boolean approvable) {
		this.approvable = approvable;
	}

	public void setRegistrationBranchId(String registrationBranchId) {
		this.registrationBranchId = registrationBranchId;
	}

	public String getRegistrationBranchId() {
		return registrationBranchId;
	}

	public boolean isPrimaryAccount() {
		return primaryAccount;
	}

	public void setPrimaryAccount(boolean primaryAccount) {
		this.primaryAccount = primaryAccount;
	}

	public OwnerType getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(OwnerType ownerType) {
		this.ownerType = ownerType;
	}

	@Override
	public Map<String, String> getAuditableAttributesMap() {
		Map<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put("accountName", getAccountName());
		attributesMap.put("accountNumber", getAccountNumber());
		attributesMap.put("status", getStatus().toString());
		attributesMap.put("level", getLevel().toString());
		attributesMap.put("type", getType().toString());
		attributesMap.put("accountClass", getAccountClass().toString());
		attributesMap.put("runningBalance", getRunningBalance() + "");
		attributesMap.put("cardNumber", getCardNumber());
		attributesMap.put("code", getCode());

		return attributesMap;
	}

	@Override
	public String getAuditableAttributesString() {
		return MapUtil.convertAttributesMapToString(getAuditableAttributesMap());
	}

	@Override
	public String getEntityName() {
		return "BANK ACCOUNT";
	}

	@Override
	public String getInstanceName() {
		return this.getAccountNumber();
	}

	public String getBankAccountLastBranch() {
		return bankAccountLastBranch;
	}

	public void setBankAccountLastBranch(String bankAccountLastBranch) {
		this.bankAccountLastBranch = bankAccountLastBranch;
	}

	public boolean isBankAccRenderApprovable() {
		return bankAccRenderApprovable;
	}

	public void setBankAccRenderApprovable(boolean bankAccRenderApprovable) {
		this.bankAccRenderApprovable = bankAccRenderApprovable;
	}

	public boolean isActive() {
		if (BankAccountStatus.ACTIVE.equals(status)) {
			return true;
		}
		return false;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isPendingApproval() {
		if (BankAccountStatus.AWAITING_APPROVAL.equals(status) || BankAccountStatus.DISAPPROVED.equals(status)) {
			return true;
		}
		return false;
	}

	public void setPendingApproval(boolean pendingApproval) {
		this.pendingApproval = pendingApproval;
	}

}
