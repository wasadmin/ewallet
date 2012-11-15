/**
 * 
 */
package zw.co.esolutions.ewallet.process.pojo;

import java.io.Serializable;

import zw.co.esolutions.ewallet.enums.TransactionClass;
import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.enums.TransactionType;

/**
 * @author taurai
 *
 */
public class ManualPojo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2480347966224105233L;
	private String sourceAccountNumber;
	private String destinationAccountNumber;
	private TransactionType transactionType;
	private boolean applyToEWallet;
	private long amount;
	private String oldMessageId;
	private String messageId;
	private TransactionStatus status;
	private String userName;
	private String reason;
	private String fromEwalletChargeAccount;
	private String toEwalletChargeAccount;
	private String fromEQ3ChargeAccount;
	private String toEQ3ChargeAccount;
	private String sourceEQ3AccountNumber;
	private String destinationEQ3AccountNumber;
	private TransactionClass transactionClass;
	private TransactionClass chargeTransactionClass;
	private long chargeAmount;
	
	/**
	 * 
	 */
	public ManualPojo() {
		// TODO Auto-generated constructor stub
	}
	public TransactionType getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}
	public boolean isApplyToEWallet() {
		return applyToEWallet;
	}
	public void setApplyToEWallet(boolean applyToEWallet) {
		this.applyToEWallet = applyToEWallet;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public String getOldMessageId() {
		return oldMessageId;
	}
	public void setOldMessageId(String oldMessageId) {
		this.oldMessageId = oldMessageId;
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	
	public TransactionStatus getStatus() {
		return status;
	}
	public void setStatus(TransactionStatus status) {
		this.status = status;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getReason() {
		return reason;
	}
	public String getSourceEQ3AccountNumber() {
		return sourceEQ3AccountNumber;
	}
	public void setSourceEQ3AccountNumber(String sourceEQ3AccountNumber) {
		this.sourceEQ3AccountNumber = sourceEQ3AccountNumber;
	}
	public String getDestinationEQ3AccountNumber() {
		return destinationEQ3AccountNumber;
	}
	public void setDestinationEQ3AccountNumber(String destinationEQ3AccountNumber) {
		this.destinationEQ3AccountNumber = destinationEQ3AccountNumber;
	}
	public void setTransactionClass(TransactionClass transactionClass) {
		this.transactionClass = transactionClass;
	}
	public TransactionClass getTransactionClass() {
		return transactionClass;
	}
	public String getFromEwalletChargeAccount() {
		return fromEwalletChargeAccount;
	}
	public void setFromEwalletChargeAccount(String fromEwalletChargeAccount) {
		this.fromEwalletChargeAccount = fromEwalletChargeAccount;
	}
	public String getToEwalletChargeAccount() {
		return toEwalletChargeAccount;
	}
	public void setToEwalletChargeAccount(String toEwalletChargeAccount) {
		this.toEwalletChargeAccount = toEwalletChargeAccount;
	}
	public String getFromEQ3ChargeAccount() {
		return fromEQ3ChargeAccount;
	}
	public void setFromEQ3ChargeAccount(String fromEQ3ChargeAccount) {
		this.fromEQ3ChargeAccount = fromEQ3ChargeAccount;
	}
	public String getToEQ3ChargeAccount() {
		return toEQ3ChargeAccount;
	}
	public void setToEQ3ChargeAccount(String toEQ3ChargeAccount) {
		this.toEQ3ChargeAccount = toEQ3ChargeAccount;
	}
	public long getChargeAmount() {
		return chargeAmount;
	}
	public void setChargeAmount(long chargeAmount) {
		this.chargeAmount = chargeAmount;
	}
	public String getSourceAccountNumber() {
		return sourceAccountNumber;
	}
	public void setSourceAccountNumber(String sourceAccountNumber) {
		this.sourceAccountNumber = sourceAccountNumber;
	}
	public String getDestinationAccountNumber() {
		return destinationAccountNumber;
	}
	public void setDestinationAccountNumber(String destinationAccountNumber) {
		this.destinationAccountNumber = destinationAccountNumber;
	}
	public TransactionClass getChargeTransactionClass() {
		return chargeTransactionClass;
	}
	public void setChargeTransactionClass(TransactionClass chargeTransactionClass) {
		this.chargeTransactionClass = chargeTransactionClass;
	}
	
	
}
