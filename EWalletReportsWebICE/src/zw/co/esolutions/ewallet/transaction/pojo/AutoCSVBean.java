/**
 * 
 */
package zw.co.esolutions.ewallet.transaction.pojo;


/**
 * @author taurai
 *
 */
public class AutoCSVBean {

	private String reference;
	private long amount;
	private String stringFrom1;
	private String stringFrom2;
	private String stringFrom3;
	private String stringTo1;
	private String stringTo2;
	private String stringTo3;
	private String transactionDate;
	private String postCode;
	private String valueDate;
	/**
	 * 
	 */
	public AutoCSVBean() {
		// TODO Auto-generated constructor stub
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}
	public String getTransactionDate() {
		return transactionDate;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	public String getPostCode() {
		return postCode;
	}
	public String getStringFrom1() {
		return stringFrom1;
	}
	public void setStringFrom1(String stringFrom1) {
		this.stringFrom1 = stringFrom1;
	}
	public String getStringFrom2() {
		return stringFrom2;
	}
	public void setStringFrom2(String stringFrom2) {
		this.stringFrom2 = stringFrom2;
	}
	public String getStringFrom3() {
		return stringFrom3;
	}
	public void setStringFrom3(String stringFrom3) {
		this.stringFrom3 = stringFrom3;
	}
	public String getStringTo1() {
		return stringTo1;
	}
	public void setStringTo1(String stringTo1) {
		this.stringTo1 = stringTo1;
	}
	public String getStringTo2() {
		return stringTo2;
	}
	public void setStringTo2(String stringTo2) {
		this.stringTo2 = stringTo2;
	}
	public String getStringTo3() {
		return stringTo3;
	}
	public void setStringTo3(String stringTo3) {
		this.stringTo3 = stringTo3;
	}
	public void setValueDate(String valueDate) {
		this.valueDate = valueDate;
	}
	public String getValueDate() {
		return valueDate;
	}
	

}
