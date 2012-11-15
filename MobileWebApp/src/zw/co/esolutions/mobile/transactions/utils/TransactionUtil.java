/**
 * 
 */
package zw.co.esolutions.mobile.transactions.utils;

import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfileStatus;
import zw.co.esolutions.ewallet.services.proxy.BankServiceProxy;
import zw.co.esolutions.ewallet.services.proxy.CustomerServiceProxy;
import zw.co.esolutions.ewallet.services.proxy.MobileWebServiceProxy;
import zw.co.esolutions.ewallet.util.MoneyUtil;
import zw.co.esolutions.ewallet.util.NumberUtil;
import zw.co.esolutions.mobile.thread.utils.ThreadWaitUtil;
import zw.co.esolutions.mobile.web.conf.MobileWebConfiguration;
import zw.co.esolutions.mobile.web.utils.WebConstants;
import zw.co.esolutions.ussd.web.services.MobileNetworkOperator;
import zw.co.esolutions.ussd.web.services.MobileWebRequestMessage;
import zw.co.esolutions.ussd.web.services.MobileWebTransactionType;
import zw.co.esolutions.ussd.web.services.WebSession;

/**
 * @author taurai
 *
 */
public class TransactionUtil {

	/**
	 * 
	 */
	public TransactionUtil() {
		// TODO Auto-generated constructor stub
	}
	
	public static MobileWebRequestMessage populateBasicMobileWebRequestMessage(String sessionId, 
			String bankId, String sourceMobile, 
			long amount, String sourceAccount, 
			MobileWebTransactionType transactionType) {
		
		MobileWebRequestMessage txn = new MobileWebRequestMessage();
		txn.setSourceBankId(bankId);
		txn.setAmount(amount);
		txn.setUuid(sessionId);
		txn.setSourceBankAccount(sourceAccount);
		txn.setMno(getMNO(sourceMobile));
		txn.setSourceMobileNumber(sourceMobile);
		txn.setTransactionType(transactionType);
		
		return txn;
	}
	
	public static String processTransaction(MobileWebRequestMessage mobileWebReq) {
		String message = "";
		try {
			if(MobileWebServiceProxy.getInstance().sendTransaction(mobileWebReq)) {
				
				if(MobileWebTransactionType.RTGS.equals(mobileWebReq.getTransactionType())) {
					return "RTGS Transaction Request initiated successfully";
				}
				WebSession session = new ThreadWaitUtil().waitForBankResponse(mobileWebReq.getUuid());
				if(session == null) {
					message = "Transaction response took so long.You will receive an sms on mobile : "+mobileWebReq.getSourceMobileNumber();
				} else {
					message = session.getMessage();
					if(message != null) {
						//message = message.replaceAll("[nl]", "\n");
					}
				}
				
			} else {
				message  = "Sorry the banking system is temporarily unavailable. Try later.";
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return message;
	}
	
	public static MobileNetworkOperator getMNO(String mobileNumber){
		if(mobileNumber.startsWith("26373")){
			return MobileNetworkOperator.TELECEL;
		}else if(mobileNumber.startsWith("26371")){
			return MobileNetworkOperator.NETONE;
		}else if(mobileNumber.startsWith("26377")){
			return MobileNetworkOperator.ECONET;
		}else{
			return null;
		}
	}
	
	public static long isValidAmount(String amount) {
		try {
			double am = Double.parseDouble(amount);
			return MoneyUtil.convertToCents(am);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public final static String getTargetBankIdForTargetAccount(String targetAccount, String targetMobile) {
		MobileWebConfiguration confi = MobileWebConfiguration.getInstance();
		String targetBankId = confi.getStringValueOf(WebConstants.BANK_ID);
		String accountNumber = null;
		CustomerServiceSOAPProxy customerService = CustomerServiceProxy.getInstance();
		try {
			
			accountNumber = targetAccount != null ? targetAccount : targetMobile;
			
			String mobileNumber = null;
			try {
						mobileNumber =  NumberUtil.formatMobileNumber(accountNumber);
			} catch (Exception e) {
					
				//It's a bank account But still has issues 
				BankServiceSOAPProxy bankService = BankServiceProxy.getInstance();
				BankAccount account = bankService.getUniqueBankAccountByAccountNumberAndBankId(accountNumber, targetBankId);
				if(account == null ){
					targetBankId = confi.getStringValueOf(WebConstants.BANK_ID_2);
					account = bankService.getUniqueBankAccountByAccountNumberAndBankId(accountNumber, targetBankId);
					if(account == null) {
						return null;
					} else {
						return targetBankId;
					}
				} else {
					return targetBankId;
				}
			}
			
			//This is ewallet now find MobieProfile
			MobileProfile mobileProfile = customerService.getMobileProfileByBankIdMobileNumberAndStatus(targetBankId, mobileNumber, MobileProfileStatus.ACTIVE);
			
			if(mobileProfile == null || mobileProfile.getId() == null) {
				targetBankId = null;
				
				targetBankId = confi.getStringValueOf(WebConstants.BANK_ID_2);
				mobileProfile = customerService.getMobileProfileByBankIdMobileNumberAndStatus(targetBankId, mobileNumber, MobileProfileStatus.ACTIVE);
			}
			
			if(mobileProfile != null && mobileProfile.getId() != null) {
				targetBankId = null;
				targetBankId = mobileProfile.getBankId();
			} else {
				return null;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return targetBankId;
	
	}

}
