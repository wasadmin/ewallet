package zw.co.esolutions.merchants.util;

public class PaymentRequest {
	private String paymentId;

	private String accountNumber;

	private double amount;

	private int paymentBank;

	private int paymentBranch;

	private String customerName;
	
	private String merchantRef;

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public int getPaymentBank() {
		return paymentBank;
	}

	public void setPaymentBank(int paymentBank) {
		this.paymentBank = paymentBank;
	}

	public int getPaymentBranch() {
		return paymentBranch;
	}

	public void setPaymentBranch(int paymentBranch) {
		this.paymentBranch = paymentBranch;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
	
	public String getMerchantRef() {
		return merchantRef;
	}

	public void setMerchantRef(String merchantRef) {
		this.merchantRef = merchantRef;
	}

	@Override
	public String toString() {
		return "RQST INFO : " + this.getCustomerName() + " | " + this.getAccountNumber() + " | " + this.getPaymentId() +" " + this.getAmount() + " | " + this.getPaymentBank() + " | " + this.getPaymentBank()+" | "+this.getMerchantRef(); 
	}
}
