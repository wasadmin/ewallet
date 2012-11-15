/**
 * 
 */
package zw.co.esolutions.ewallet.msg;

import zw.co.esolutions.ewallet.util.Formats;

/**
 * @author wasadmin
 * 
 */
public class AlertInfo {

	private static final long serialVersionUID = 7469103001015509234L;

	private String customerReference;

	private String bankAccountNumber;

	private long amount;

	private long accountBalance;

	private String transactionSource;

	private String transactionType;

	private String transactionTypeDetails;

	private String transactionDate;

	private String bankReference;

	private String narrative;

	private String status;

	private String mobileNumber;

	private String customerId;

	/**
	 * @return the accountBalance
	 */
	public long getAccountBalance() {

		return accountBalance;
	}

	/**
	 * @return the amount
	 */
	public long getAmount() {

		return amount;
	}

	/**
	 * @return the bankAccountNumber
	 */
	public String getBankAccountNumber() {

		return bankAccountNumber;
	}

	/**
	 * @return the bankReference
	 */
	public String getBankReference() {

		return bankReference;
	}

	/**
	 * @return the customerId
	 */
	public String getCustomerId() {

		return customerId;
	}

	/**
	 * @return the customerReference
	 */
	public String getCustomerReference() {

		return customerReference;
	}

	/**
	 * @return the mobileNumber
	 */
	public String getMobileNumber() {

		return mobileNumber;
	}

	/**
	 * @return the narrative
	 */
	public String getNarrative() {

		return narrative;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {

		return status;
	}

	/**
	 * @return the transactionDate
	 */
	public String getTransactionDate() {

		return transactionDate;
	}

	/**
	 * @return the transactionSource
	 */
	public String getTransactionSource() {

		return transactionSource;
	}

	/**
	 * @return the transactionType
	 */
	public String getTransactionType() {

		return transactionType;
	}

	/**
	 * @return the transactionTypeDetails
	 */
	public String getTransactionTypeDetails() {

		return transactionTypeDetails;
	}

	/**
	 * @param accountBalance
	 *            the accountBalance to set
	 */
	public void setAccountBalance(long accountBalance) {

		this.accountBalance = accountBalance;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(long amount) {

		this.amount = amount;
	}

	/**
	 * @param bankAccountNumber
	 *            the bankAccountNumber to set
	 */
	public void setBankAccountNumber(String bankAccountNumber) {

		this.bankAccountNumber = bankAccountNumber;
	}

	/**
	 * @param bankReference
	 *            the bankReference to set
	 */
	public void setBankReference(String bankReference) {

		this.bankReference = bankReference;
	}

	/**
	 * @param customerId
	 *            the customerId to set
	 */
	public void setCustomerId(String customerId) {

		this.customerId = customerId;
	}

	/**
	 * @param customerReference
	 *            the customerReference to set
	 */
	public void setCustomerReference(String customerReference) {

		this.customerReference = customerReference;
	}

	/**
	 * @param mobileNumber
	 *            the mobileNumber to set
	 */
	public void setMobileNumber(String mobileNumber) {

		this.mobileNumber = mobileNumber;
	}

	/**
	 * @param narrative
	 *            the narrative to set
	 */
	public void setNarrative(String narrative) {

		this.narrative = narrative;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {

		this.status = status;
	}

	/**
	 * @param transactionDate
	 *            the transactionDate to set
	 */
	public void setTransactionDate(String transactionDate) {

		this.transactionDate = transactionDate;
	}

	/**
	 * @param transactionSource
	 *            the transactionSource to set
	 */
	public void setTransactionSource(String transactionSource) {

		this.transactionSource = transactionSource;
	}

	/**
	 * @param transactionType
	 *            the transactionType to set
	 */
	public void setTransactionType(String transactionType) {

		this.transactionType = transactionType;
	}

	/**
	 * @param transactionTypeDetails
	 *            the transactionTypeDetails to set
	 */
	public void setTransactionTypeDetails(String transactionTypeDetails) {

		this.transactionTypeDetails = transactionTypeDetails;
	}

	/**
	 * 
	 */
	public AlertInfo() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String toString() {
		
//		 CASH WITHDRAWAL ALERT
//		 Amnt: 500.00 DR
//		 BankAcc: 4565548683200
//		 From: AVONDALE RETAIL
//		 Date: 01-08-11
//		 Ref: 113@AAA00591
//		 ZB SMS Banking. Powered by e-Solutions.
		 String amount = Formats.moneyFormat.format((this.getAmount() / 100.0));
		 return this.getTransactionTypeDetails() + " ALERT[nl]Amnt : " + amount + " " + this.getTransactionType() +"[nl]BankAcc " + this.getBankAccountNumber() + "[nl]From : " + this.getTransactionSource() + "[nl]Date " + this.getTransactionDate() + "[nl]Ref : " + this.getCustomerReference();
	}

}
