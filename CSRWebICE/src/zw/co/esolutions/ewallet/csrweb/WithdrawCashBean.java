/**
 * 
 */
package zw.co.esolutions.ewallet.csrweb;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.util.NumberUtil;

/**
 * @author tauttee
 *
 */
public class WithdrawCashBean extends PageCodeBase{

	private String mobileNumber;
	
	public WithdrawCashBean() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	public String nextAction() {
		if(this.getMobileNumber() == null || this.getMobileNumber().equalsIgnoreCase("")) {
			super.setErrorMessage("To continue, mobile number is required.");
			return "failure";
		} try {
			this.setMobileNumber(NumberUtil.formatMobileNumber(this.getMobileNumber()));
		} catch (Exception e) {
			super.setErrorMessage(e.getMessage());
			return "failure";
		}
		try {
			MobileProfile mp = super.getCustomerService().getMobileProfileByMobileNumber(this.getMobileNumber());
			if( mp == null || mp.getId() ==  null) {
				super.setInformationMessage("Mobile number "+this.getMobileNumber()+" is invalid.");
				return "failure";
			}
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage("An error has occured. Operation aborted.");
			return "failure";
		}
		super.getRequestScope().put("mobileNumber", this.getMobileNumber());
		super.gotoPage("/csr/confirmCashWithdrawal.jspx");
		return "success";
	}
	
	public String cancelAction() {
		this.setMobileNumber(null);
		return "success";
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}
}
