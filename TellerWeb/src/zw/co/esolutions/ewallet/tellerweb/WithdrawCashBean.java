/**
 * 
 */
package zw.co.esolutions.ewallet.tellerweb;

import java.util.Date;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.customerservices.service.CustomerAutoRegStatus;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.process.ProcessServiceSOAPProxy;
import zw.co.esolutions.ewallet.process.TransactionType;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.DateUtil;
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
		
		ProcessServiceSOAPProxy processService= new ProcessServiceSOAPProxy();
		ProfileServiceSOAPProxy profileService= new ProfileServiceSOAPProxy();
		Profile profile=profileService.getProfileByUserName(getJaasUserName());
		Date toDay= new Date();
		String result=processService.canTellerMakeTransact(profile.getId(), DateUtil.convertToXMLGregorianCalendar(toDay), TransactionType.WITHDRAWAL);
		//System.out.println(">>>>>>>>>>>>result::::::::::::::::::::"+result);
		if(ResponseCode.E000.toString().equalsIgnoreCase(result)){
		
		//System.out.println(">>>>>>>>>success successs>>>>>>>");
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
			//Profile profile = super.getProfileService().getProfileByUserName(this.getJaasUserName());
			if(profile == null){
				throw new Exception();
			}
			BankBranch bankBranch = super.getBankService().findBankBranchById(profile.getBranchId());
			if(bankBranch == null){
				throw new Exception();
			}
			MobileProfile mobileProfile = super.getCustomerService().getMobileProfileByMobileNumber(this.mobileNumber);
			if(mobileProfile == null|| mobileProfile.getId()== null) {
				super.setInformationMessage("Mobile number "+this.getMobileNumber()+" is invalid.");
				return "failure";
			}
			
			//check auto-reg
			if (CustomerAutoRegStatus.YES.equals(mobileProfile.getCustomer().getCustomerAutoRegStatus())) {
				super.setInformationMessage("Please encourage this customer to register fully.");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage("An error has occured. Operation aborted.");
			return "failure";
		}
		super.getRequestScope().put("mobileNumber", this.getMobileNumber());
		super.gotoPage("/teller/confirmCashWithdrawal.jspx");
		
		
		
		
		return "success";
		
		} else{
			
			super.setInformationMessage(result);
			return "cannotTransact";
		}
		
		
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
