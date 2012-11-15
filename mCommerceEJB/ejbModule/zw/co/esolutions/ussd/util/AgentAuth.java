/**
 * 
 */
package zw.co.esolutions.ussd.util;

import org.apache.log4j.Logger;

import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ussd.services.BalanceEnquiryService;
import zw.co.esolutions.ussd.web.services.proxy.UssdServiceProxy;

/**
 * @author taurai
 *
 */
public class AgentAuth {


	static Logger logger = LoggerFactory.getLogger(BalanceEnquiryService.class);
	
	public static String getAgentByMobileNumberAndBankId(String mobileNumber, String bankId) {
		try {
			return UssdServiceProxy.getInstance().getAgentNumberByMobileNumberAndBankId(mobileNumber, bankId);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static boolean isAgentMobile(String bankId, String formattedMobileNumber) {
		try {
			return UssdServiceProxy.getInstance().isAgentMobile(bankId, formattedMobileNumber);
		} catch (Exception e) {
			logger.error("Exception occured " + e);
			return false;
		}
	}
	
	public static String validateAgentDeposit(String sourceAccount, String targetMobile) {
		return UssdServiceProxy.getInstance().validateAgentDeposit(sourceAccount, targetMobile);
		
	}
	
	public static String validateAgentTransfer(String sourceAccount, String targetAccount, String bankId) {
		return UssdServiceProxy.getInstance().validateAgentTransfer(sourceAccount, targetAccount, bankId);
		
	}
	
	
	public static String validateAgentCustomerWithdrawal(String agentNumber) {
		return UssdServiceProxy.getInstance().validateAgentCustomerWithdrawal(agentNumber);
		
	}
	
	public static String validateOldRef(String ref) {
		
		return ResponseCode.E000.name();
		
	}
	
	public static String validateAgentSummary(String ddmmyyDate) {
		logger.debug("Date supplied.. parse it: " + ddmmyyDate);
		
		if (ddmmyyDate.trim().length() != 6) {
			logger.debug("Date is not of length 6.. REJECT");
			return SystemConstants.AGENT_AUTH_AGENT_SUMMARY+" \n Invalid date "+ddmmyyDate+" \nDate must be in the ddMMyy format including leading zeros e.g. 020512 \nEnter Date";
		}
		
		return ResponseCode.E000.name();
		
	}
	
	
	
}
