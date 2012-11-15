package zw.co.esolutions.mobile.transactions.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountStatus;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountType;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.CustomerClass;
import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.Exception_Exception;
import zw.co.esolutions.ewallet.customerservices.service.GenerateTxnPassCodeResp;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfileStatus;
import zw.co.esolutions.ewallet.customerservices.service.ValidateTxnPassCodeReq;
import zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchant;
import zw.co.esolutions.ewallet.merchantservices.service.CustomerMerchantStatus;
import zw.co.esolutions.ewallet.merchantservices.service.Merchant;
import zw.co.esolutions.ewallet.services.proxy.BankServiceProxy;
import zw.co.esolutions.ewallet.services.proxy.CustomerServiceProxy;
import zw.co.esolutions.ewallet.services.proxy.MerchantServiceProxy;
import zw.co.esolutions.ewallet.services.proxy.MobileWebServiceProxy;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.mobile.msgs.LoginInfo;
import zw.co.esolutions.mobile.web.conf.MobileWebConfiguration;
import zw.co.esolutions.mobile.web.utils.WebConstants;
import zw.co.esolutions.mobile.web.utils.WebStatus;
import zw.co.esolutions.ussd.util.LoggerFactory;
import zw.co.esolutions.ussd.web.services.MobileCommerceServiceSOAPProxy;
import zw.co.esolutions.ussd.web.services.WebSession;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

public class LoginUtils {

	static Logger logger = LoggerFactory.getLogger(LoginUtils.class);
	
	public LoginUtils() {
		// TODO Auto-generated constructor stub
	}

	
	private static final String formatPasswordPart(int number) {
		if (number == 1) {
			return "1st";
		} else if (number == 2) {
			return "2nd";
		} else if (number == 3) {
			return "3rd";
		} else if (number == 5) {
			return "last";
		}
		return number + "th";
	}

	
	public static final String getJson(GenerateTxnPassCodeResp pass) {
		System.out.println(" >>>>>>>>>>>>>>>>>>> Parts :::: "+pass);
		String json = "";
		String narrative = getPasscodeNarrative(pass);
		String parts = pass.getFirstIndex()+""+pass.getSecondIndex();
			JSONObject jsonObj = new JSONObject();
			if(narrative != null) {
				jsonObj.put("narrative", narrative);
			}
			if (parts != null) {
				jsonObj.put("parts", parts);
			}
			try {
				json = jsonObj.serialize();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		System.out.println(">>>>>>>>>>>>>>>>> Json  = "+json);
		return json;
	}
	
	public static final  String getPasscodeNarrative(GenerateTxnPassCodeResp pass) {
		if(pass != null) {
			return "(" + formatPasswordPart(pass.getFirstIndex()) + " and " + formatPasswordPart(pass.getSecondIndex())+")";
		} else {
			return "Error occured.";
		}
		
	}
	
	public final static boolean passcodeIsValid(LoginInfo info) throws Exception {
		try {
			CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
			String passcodeParts = info.getParts().trim();
			ValidateTxnPassCodeReq req = new ValidateTxnPassCodeReq();
			req.setMobileNumber(info.getSourceMobile());
			req.setFirstIndex(Integer.parseInt(info.getPasscodePrompt().substring(0, 1)));
			req.setSecondIndex(Integer.parseInt(info.getPasscodePrompt().substring(1)));
			req.setFirstValue(Integer.parseInt(passcodeParts.substring(0, 1)));
			req.setSecondValue(Integer.parseInt(passcodeParts.substring(1)));

			boolean isValid = customerService.txnPassCodeIsValid(req);
			return isValid;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static String formatNationalId(String idNumber){
		
		idNumber = idNumber.toUpperCase() ;
		StringBuffer temp = new StringBuffer();
		for(int i =0;i<idNumber.length();i++){
			Character c =idNumber.charAt(i);
			if(Character.isDigit(c) || Character.isLetter(c)){
				if(Character.isLetter(c)){
					temp.append( c );  
				}else{
					temp.append(c);
				}
			}
		}
		return temp.toString();
	
   }
	
	public static final String processMobileLock(WebSession wSession, MobileProfile mobileProfile) {
		try {
			CustomerServiceSOAPProxy customerService = CustomerServiceProxy.getInstance();
			MobileCommerceServiceSOAPProxy mobileWebService = MobileWebServiceProxy.getInstance();
			wSession.setPasswordRetryCount(wSession.getPasswordRetryCount() + 1);
			//wSession.setTime(DateUtil.getNextTimeout(new Timestamp(System.currentTimeMillis()), 3));
			WebSession session = mobileWebService.getFailedWebSession(wSession.getMobileNumber(), wSession.getBankId());
			
			if(session == null) {
				
				session = mobileWebService.createWebSession(wSession);
							
			} else {
				
				session.setPasswordRetryCount(wSession.getPasswordRetryCount() + 1);
				
				if(session.getPasswordRetryCount() > 3) {
					   session.setPasswordRetryCount(1);
				}
				session = mobileWebService.updateWebSession(session);
				
			}
			  			
			   
		 if (session.getPasswordRetryCount() < 3) {
					
					String message = "Wrong password parts. Mobile number will be locked after 3 invalid attempts";
					return message;
					
		} else {
					// passcode entered incorrectly 3 times, lock the profile
					mobileProfile.setPasswordRetryCount(3);
					mobileProfile.setStatus(MobileProfileStatus.LOCKED);
					mobileProfile.setTimeout(DateUtil.convertToXMLGregorianCalendar(DateUtil.addHours(new Date(), 24)));
					customerService.updateMobileProfile(mobileProfile, EWalletConstants.SYSTEM);
					
					return ResponseCode.E707.getDescription();
		}
				
			
		} catch (Exception_Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static final void clearMobileLock(MobileProfile mobileProfile) {
		try {
			CustomerServiceSOAPProxy customerService = CustomerServiceProxy.getInstance();
			MobileCommerceServiceSOAPProxy mobileWebService = MobileWebServiceProxy.getInstance();
			WebSession session = mobileWebService.getFailedWebSession(mobileProfile.getMobileNumber(), mobileProfile.getBankId());
			if(session != null) {
				mobileWebService.deleteWebSession(session.getId());
			}
			if(mobileProfile.getStatus().equals(MobileProfileStatus.LOCKED)) {
				mobileProfile.setStatus(MobileProfileStatus.ACTIVE);
				mobileProfile.setPasswordRetryCount(0);
				customerService.updateMobileProfile(mobileProfile, EWalletConstants.SYSTEM);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static final JSONObject populateInitialLoginJson(JSONObject jb, MobileProfile mp) {
		try {
			Customer customer = mp.getCustomer();
			List<BankAccount> eWalletAccounts = null;
			List<BankAccount> bankAccounts = null;
			boolean isAgent = false;
			BankServiceSOAPProxy bankProxy = BankServiceProxy.getInstance();
			
			if(CustomerClass.AGENT.equals(customer.getCustomerClass())) {
				 eWalletAccounts = bankProxy.getBankAccountsByAccountHolderIdAndOwnerTypeAndStatusAndBankAccountType(customer.getId(),OwnerType.AGENT,BankAccountStatus.ACTIVE,BankAccountType.AGENT_EWALLET);
				 bankAccounts = bankProxy.getBankAccountsByAccountHolderIdAndOwnerTypeAndStatus(customer.getId(), OwnerType.AGENT, BankAccountStatus.ACTIVE);
				 isAgent = true;
			} else {
				 eWalletAccounts = bankProxy.getBankAccountsByAccountHolderIdAndOwnerTypeAndStatusAndBankAccountType(customer.getId(),OwnerType.CUSTOMER,BankAccountStatus.ACTIVE,BankAccountType.E_WALLET);
				  bankAccounts = bankProxy.getBankAccountsByAccountHolderIdAndOwnerTypeAndStatus(customer.getId(), OwnerType.CUSTOMER, BankAccountStatus.ACTIVE);
				 
				 
			}
			
			boolean hasEwalletAccounts = eWalletAccounts != null ? eWalletAccounts.size()> 0 : false; 
			boolean hasBankAccounts = bankAccounts != null ? bankAccounts.size()> 0 : false; 
						
			List<String> list = new ArrayList<String>();
			
			if(hasEwalletAccounts) {
				for(BankAccount ac : eWalletAccounts) {
					if(ac.isPrimaryAccount()) {
						jb.put("primary", ac.getAccountNumber());
					}
					list.add(ac.getAccountNumber());
				}
				
			}
			
			if(hasBankAccounts) {
				for(BankAccount ac : bankAccounts) {
					if(ac.isPrimaryAccount()) {
						jb.put("primary", ac.getAccountNumber());
					}
					list.add(ac.getAccountNumber());
				}
				
			}
			JSONArray accounts = new JSONArray();
			accounts.addAll(list);
			jb.put("accounts", accounts.serialize());
			jb.put("isAgent", isAgent);
			
			//Merchants
			List<String> merchants = getMerchantShortNames(mp.getCustomer().getId());
			if(merchants == null) {
				jb.put("hasMerchants", new Boolean(false));
			} else {
				jb.put("hasMerchants", new Boolean(true));
				JSONArray merchantsArray = new JSONArray();
				merchantsArray.addAll(merchants);
				jb.put("merchants", merchantsArray.serialize());
			}
						
		} catch (Exception e) {
			// TODO: handle exception
		}
		return jb;
	}
	
	public static final boolean hasActiveAccounts(MobileProfile mp) {
		boolean hasAccounts = false;
		try {
			Customer customer = mp.getCustomer();
			logger.debug(">>>>>>>>>>>> In hasActiveAccounts, Customer : "+customer);
			List<BankAccount> eWalletAccounts = null;
			List<BankAccount> bankAccounts = null;
			BankServiceSOAPProxy bankProxy = BankServiceProxy.getInstance();
			
			if(CustomerClass.AGENT.equals(customer.getCustomerClass())) {
				 eWalletAccounts = bankProxy.getBankAccountsByAccountHolderIdAndOwnerTypeAndStatusAndBankAccountType(customer.getId(),OwnerType.AGENT,BankAccountStatus.ACTIVE,BankAccountType.AGENT_EWALLET);
				 
				 if(eWalletAccounts != null && !eWalletAccounts.isEmpty()) {
					 return true;
				 }
				 
				 bankAccounts = bankProxy.getBankAccountsByAccountHolderIdAndOwnerTypeAndStatus(customer.getId(), OwnerType.AGENT, BankAccountStatus.ACTIVE);
				
				 if(bankAccounts != null && !bankAccounts.isEmpty()) {
					 return true;
				 }
				 
			} else {
				 eWalletAccounts = bankProxy.getBankAccountsByAccountHolderIdAndOwnerTypeAndStatusAndBankAccountType(customer.getId(),OwnerType.CUSTOMER,BankAccountStatus.ACTIVE,BankAccountType.E_WALLET);
				 logger.debug(">>>>>> Ewallet Accounts Found = "+eWalletAccounts);
				 if(eWalletAccounts != null && !eWalletAccounts.isEmpty()) {
					 return true;
				 }
				 
				 bankAccounts = bankProxy.getBankAccountsByAccountHolderIdAndOwnerTypeAndStatus(customer.getId(), OwnerType.CUSTOMER, BankAccountStatus.ACTIVE);
				
				 logger.debug(">>>>>> Bank Accounts Accounts Found = "+bankAccounts);
				 if(bankAccounts != null && !bankAccounts.isEmpty()) {
					 return true;
				 }
				 
			}
		} catch (Exception e) {
			logger.debug("Exception In hasActiveAccounts() : "+e.getMessage());
		}
		return hasAccounts;
	}

	public static final JSONObject authenticateMobile(JSONObject jb, String bankConf, String mobile, String natId, String pin, String passwordPrompt) throws Exception{
		
		logger.debug(">>>>>>>>>>> Mobile : "+mobile+" , Bank Conf : "+bankConf);
		MobileWebConfiguration conf = MobileWebConfiguration.getInstance();
		String bankId = conf.getStringValueOf(bankConf); 
		
		MobileProfile mobileProfile = null;
		String message = null;
		
		if(mobile != null) {
			mobileProfile = CustomerServiceProxy.getInstance().getMobileProfileByBankAndMobileNumber(bankId, mobile);
		} 
		logger.debug("Found Mobile Profile 1 : "+mobileProfile);
		if(mobileProfile == null || mobileProfile.getId() == null) {
			bankId = conf.getStringValueOf(WebConstants.BANK_ID_2);
			mobileProfile = CustomerServiceProxy.getInstance().getMobileProfileByBankAndMobileNumber(bankId, mobile);
		}
		logger.debug("Found Mobile Profile 2 : "+mobileProfile);
		if(mobileProfile == null || mobileProfile.getId() == null ) {
			
			message = conf.getStringValueOf(WebConstants.NO_MOBILE_PROFILE_MSG);
					message +=  " "+conf.getStringValueOf(WebConstants.PRODUCT_NAME);
					
			jb.put("loginResponse", WebConstants.FAILURE_CODE);
			jb.put("message", message);
		} else {
			
			jb.put("bankId", bankId);
			
			boolean lockTimeout = false;
			if (mobileProfile.getStatus().equals(MobileProfileStatus.LOCKED) && 
					new Date(System.currentTimeMillis()).after(DateUtil.convertToDate(mobileProfile.getTimeout()))) {
					lockTimeout = true;			
			} 
			
			// Mobile is ACTIVE
			if(mobileProfile.getStatus().equals(MobileProfileStatus.ACTIVE) || lockTimeout) {
			 	
			 	LoginInfo info = new LoginInfo(pin, mobile, passwordPrompt);
			 	boolean isPasscodeValid = LoginUtils.passcodeIsValid(info);
			 	
			 	// Parts Correct
			 	if(isPasscodeValid) {
			 		
			 		natId = LoginUtils.formatNationalId(natId);
			 		
			 		if(natId.equalsIgnoreCase(mobileProfile.getCustomer().getNationalId())) {
			 						 			
			 			//Check if there are accounts
			 			if(LoginUtils.hasActiveAccounts(mobileProfile)) {
			 			
			 				jb.put("loginResponse", WebConstants.SUCCESS_CODE);
			 				LoginUtils.populateInitialLoginJson(jb,mobileProfile);
			 				
			 			} else {
			 			
			 				message = "Login Failed. No active bank acconts found";
			 				jb.put("loginResponse", WebConstants.FAILURE_CODE);
			 				jb.put("message", message);
			 				
			 			}
			 		} else {
			 			message = "Login Failed. Invalid National ID";
			 			jb.put("loginResponse", WebConstants.FAILURE_CODE);
						jb.put("message", message);
			 		}
			 		LoginUtils.clearMobileLock(mobileProfile);
			 		
			 	} else {
			 	
			 		// Handle Inavalid Password Parts
			 		WebSession webSession = new WebSession(); 
			 		webSession.setBankId(bankId);
			 		webSession.setFirstIndex(Integer.parseInt(passwordPrompt.substring(0,1)));
			 		webSession.setMobileNumber(mobile);
			 		webSession.setStatus(WebStatus.FAILED.toString());
			 		webSession.setSecondIndex(Integer.parseInt(passwordPrompt.substring(1,2)));
			 		
			 		message = LoginUtils.processMobileLock(webSession, mobileProfile);
			 		
			 		jb.put("loginResponse", WebConstants.FAILURE_CODE);
					jb.put("message", message);
			 		
			 	}
			} else if (MobileProfileStatus.LOCKED.equals(mobileProfile.getStatus())) {
				message = "Mobile Profile Locked, timeout not yet expired";
				jb.put("loginResponse", WebConstants.FAILURE_CODE);
				jb.put("message", message);
						
						
			} else {
				//account inactive
				message = "Login Failed. Your Mobile is inactive";
				jb.put("loginResponse", WebConstants.FAILURE_CODE);
				jb.put("message", message);
    		}
			
		}

		return jb;
	}
	
	public static List<Merchant> getMerchants(String customerId) {
		List<Merchant> merchants = new ArrayList<Merchant>();
		try {
			List<CustomerMerchant> cms = MerchantServiceProxy.getInstance().getCustomerMerchantByCustomerId(customerId);
			
			if(cms == null || cms.isEmpty()) {
				return null;
			} else {
				for(CustomerMerchant cm : cms) {
					if(CustomerMerchantStatus.ACTIVE.equals(cm.getStatus())) {
						if(cm != null && cm.getId() != null) {
							merchants.add(cm.getBankMerchant().getMerchant());
						}
					}
				}
			}
			
			if(merchants.isEmpty() ) {
				merchants = null;
			}
			
			return merchants;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	public static List<String> getMerchantShortNames(String customerId) {
		List<String> merchants = new ArrayList<String>();
		try {
			List<CustomerMerchant> cms = MerchantServiceProxy.getInstance().getCustomerMerchantByCustomerId(customerId);
			
			if(cms == null || cms.isEmpty()) {
				return null;
			} else {
				for(CustomerMerchant cm : cms) {
					if(CustomerMerchantStatus.ACTIVE.equals(cm.getStatus())) {
						if(cm != null && cm.getId() != null) {
							merchants.add(cm.getBankMerchant().getMerchant().getShortName());
						}
					}
				}
			}
			
			if(merchants.isEmpty() ) {
				merchants = null;
			}
			
			return merchants;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
