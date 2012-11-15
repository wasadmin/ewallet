/**
 * 
 */
package zw.co.esolutions.ussd.util;

import org.apache.log4j.Logger;

import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.NumberUtil;
import zw.co.esolutions.ussd.services.BalanceEnquiryService;

/**
 * @author taurai
 *
 */
public class AuthUtil {


	static Logger logger = LoggerFactory.getLogger(BalanceEnquiryService.class);
	
	public static String validateMobile(String targetMobile) {
		 try {
			targetMobile = NumberUtil.formatMobileNumber(targetMobile);
		} catch (Exception e) {
			return SystemConstants.TOP_UP+" \nTarget mobile "+targetMobile+" is invalid. \nEnter Target Mobile";
		}
		return ResponseCode.E000.name();
		
	}
	
		
}
