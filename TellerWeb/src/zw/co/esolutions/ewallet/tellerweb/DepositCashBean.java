
/**
 * 
 */
package zw.co.esolutions.ewallet.tellerweb;

import java.util.Date;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.CustomerAutoRegStatus;
import zw.co.esolutions.ewallet.customerservices.service.CustomerClass;
import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.CustomerStatus;
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
public class DepositCashBean extends PageCodeBase{

	private String mobileNumber;
	
	public DepositCashBean() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	public String nextAction() {
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		ProcessServiceSOAPProxy processService= new ProcessServiceSOAPProxy();
		ProfileServiceSOAPProxy profileService= new ProfileServiceSOAPProxy();
		Profile profile=profileService.getProfileByUserName(getJaasUserName());
		Date toDay= new Date();
		String result=processService.canTellerMakeTransact(profile.getId(), DateUtil.convertToXMLGregorianCalendar(toDay), TransactionType.DEPOSIT);
		System.out.println(">>>>>>>>>>>>result::::::::::::::::::::"+result);
		if(ResponseCode.E000.toString().equalsIgnoreCase(result)){
		
		System.out.println(">>>>>>>>>success successs>>>>>>>");
		
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
			MobileProfile p = customerService.getMobileProfileByMobileNumber(this.getMobileNumber());
			if( p == null || p.getId()==null) {
				super.setInformationMessage("Mobile number "+this.getMobileNumber()+" is invalid.");
				return "failure";
			}
			//check auto-reg
			if (CustomerAutoRegStatus.YES.equals(p.getCustomer().getCustomerAutoRegStatus())) {
				super.setErrorMessage("This customer is not fully registered. Cannot deposit funds.");
				return "failure";
			}
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage("An error has occured. Operation aborted.");
			return "failure";
		}
		
		try {
			MobileProfile mobileProfile = getCustomerService().getMobileProfileByMobileNumber(getMobileNumber());
			if(mobileProfile != null && mobileProfile.getId() != null){
				Customer c = mobileProfile.getCustomer();
				if(c.getCustomerClass().name().equals(CustomerClass.AGENT.name())){
					super.setInformationMessage("Customer is an agent use AGENT DEPOSIT link .");
					return "failure";
				}
			}
			BankAccount bankAccount = bankService.getBankAccountByAccountNumberAndOwnerType(this.getMobileNumber(), OwnerType.CUSTOMER);
			if(bankAccount == null || bankAccount.getId() == null){
				super.setInformationMessage("No eWallet Account found for mobile number "+this.getMobileNumber()+".");
				return "failure";
			}
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage("An error has occured. Operation aborted.");
			return "failure";
		}
		super.getRequestScope().put("mobileNumber", this.getMobileNumber());
		super.gotoPage("/teller/confirmCashDeposit.jspx");
		return "success";
		
		}else{
			super.setInformationMessage(result);
			return "";
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
