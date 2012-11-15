package zw.co.esolutions.ewallet.extpayments.util;

public class Constants {

	public static final String RC_OK_SHORT = "00";
	public static final String RC_ERROR = "01";
	
	public static final String SUCCESSFUL_SUBMIT_MSG = "Request successfully received for processing";
	public static final String GENERAL_ERROR_MESSAGE = "General transaction error";	
	
	public static final String MESSAGE_SEPARATOR = "|";
	
	public static final String EXTPAYMENTS_REQUEST_Q = "jms/EXTPAY.IN.Q";
	public static final String EXTPAYMENTS_RESPONSE_Q = "jms/EXTPAY.OUT.Q";

	public static final String SWITCH_QCF = "jms/EWalletQCF";

}
