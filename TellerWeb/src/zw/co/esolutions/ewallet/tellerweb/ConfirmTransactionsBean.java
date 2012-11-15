/**
 * 
 */
package zw.co.esolutions.ewallet.tellerweb;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.process.ProcessResponse;

/**
 * @author taurai
 *
 */
public class ConfirmTransactionsBean extends PageCodeBase{

	private BankAccount bankAccount;
	private String message;
	private String header;
	private String bankAccountId;
	private String mobileProfileId;
	private MobileProfile mobileProfile;
	private ProcessResponse response;
	/**
	 * 
	 */
	public ConfirmTransactionsBean() {
		if(this.getBankAccountId() == null) {
			this.setBankAccountId((String)super.getRequestScope().get("bankAccountId"));
		}
		if(this.getHeader() == null) {
			this.setHeader((String)super.getRequestScope().get("header"));
		}
		if(this.getMobileProfileId() == null) {
			this.setMobileProfileId((String)super.getRequestScope().get("mobileProfileId"));
		}
		if(this.getResponse() == null) {
			this.setResponse((ProcessResponse)super.getRequestScope().get("processResponse"));
		}
	}
	
	public BankAccount getBankAccount() {
		if(this.bankAccount == null && this.getBankAccountId() != null) {
			this.bankAccount = super.getBankService().findBankAccountById(this.getBankAccountId());
		}
		return bankAccount;
	}
	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}

	public void setBankAccountId(String bankAccountId) {
		this.bankAccountId = bankAccountId;
	}

	public String getBankAccountId() {
		return bankAccountId;
	}

	public void setMobileProfileId(String mobileProfileId) {
		this.mobileProfileId = mobileProfileId;
	}

	public String getMobileProfileId() {
		return mobileProfileId;
	}

	public void setMobileProfile(MobileProfile mobileProfile) {
		this.mobileProfile = mobileProfile;
	}

	public MobileProfile getMobileProfile() {
		if(this.mobileProfile == null && this.getMobileProfileId() != null) {
			this.mobileProfile = super.getCustomerService().findMobileProfileById(this.getMobileProfileId());
		}
		return mobileProfile;
	}

	public void setResponse(ProcessResponse response) {
		this.response = response;
	}

	public ProcessResponse getResponse() {
		return response;
	}

	public String submit() {
		super.gotoPage("/teller/tellerHome.jspx");
		return "success";
	}
 
}
