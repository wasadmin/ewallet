package zw.co.esolutions.ewallet.util;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import zw.co.esolutions.ewallet.enums.CustomerClass;
import zw.co.esolutions.ewallet.enums.TransactionType;

public class SystemConstants {

	public static final String SMS_REQUEST_TYPE_REBOOT = "REBOOT";

	public static final String STATUS_TOPUP_REQUEST = "TOPUP REQUEST";

	public static final String SMS_KEY = "S";

	public static final String MPG_KEY = "M";

	public static final String NO_MORE_WORK = "Finito";

	public static final String STATUS_PING = "PING";

	public static final String ENCODE_KEY = "10tu5n0t35";
	public static final String IBBULK ="IBBULK";

	/*
	 * 
	 * 
	 * TXN Types
	 */

	public static final String TXN_TYPE_TOPUP = "TOPUP";

	public static final String TXN_BAL_ENQ = "BAL";

	public static final String TXN_TYPE_BILLPAY = "BILLPAY";

	public static final String TXN_TYPE_MINIREQ = "MINI";

	/*
	 * 
	 * 
	 * STATUS
	 */

	public static final String STATUS_TXN_VEREQ = "VEREQ";

	public static final String STATUS_TXN_VERES = "VERES";

	public static final String STATUS_DEREGISTERED = "DELETED";

	public static final String STATUS_TXN_PINCHANGE_REQ = "PINCHANGE_REQUEST";

	public static final String STATUS_TXN_PARES = "PARES";

	public static final String STATUS_LOCKED = "LOCKED";

	public static final String STATUS_TXN_PAREQ = "PAREQ";

	public static final String STATUS_TXN_BALREQ = "BALREQ";

	public static final String STATUS_TXN_DEBITREQ = "DEBITREQ";

	public static final String STATUS_TXN_MINIREQ = "MINIREQ";

	public static final String STATUS_TXN_CREDITRES = "CREDIT_ADVICE";

	public static final String STATUS_TXN_CREDITREQ = "CREDITREQ";

	public static final String STATUS_TXN_CREDITADVICE = "CREDITADVICE";

	public static final String STATUS_TXN_COMPLETED = "COMPLETED";

	/*
	 * TAP STATUS 
	 */
	public static final String TAP_STATUS_ON = "ON";
	public static final String TAP_STATUS_OFF = "OFF";
	
	/*
	 * 
	 * 
	 * Configuration variables
	 */

	public static final String CONF_PWD_LENGTH = "acs.passwdLength";

	public static final String CONF_PWD_RETRY_LIMIT = "acs.passwdRetryLimit";

	public static final String CONF_ACC_LOCK_TIMEOUT = "acs.accountLockTimeout";

	public static final String CONF_BANK_INTERFACE_CLASS = "acs.bank.interface";

	public static final String CONF_LOG_4_J_PROPS = "acs.log4j.properties";

	public static final String CONF_BANK_ID = "acs.bank.id";

	/*
	 * 
	 * 
	 * msgs
	 */

	public static final String MSG_NOT_REGISTERED = "msg.notRegistered";

	public static final String MSG_ERR_MERCHANT_NOT_FOUND = "msg.merchantNotFound";

	public static final String MSG_ERROR_IN_REG = "msg.errorInReg";

	public static final String MSG_ACC_NOT_ACTIVE = "msg.accountInactive";

	public static final String MSG_ACC_PASSWD_PROMPT = "msg.passwdPrompt";

	public static final String MSG_REG_INVALID_NEW_PASSWD = "msg.invalidNewPasswd";

	public static final String MSG_ACC_LOCKED = "msg.accountLocked";

	public static final String MSG_REG_INVALID_OLD_PASSWD = "msg.invalidOldPasswd";

	public static final String MSG_ACC_WRONG_PASSWD_PROMPT = "msg.wrongPasswdSupplied";

	public static final String MSG_ECONET_TOPUP = "msg.econet.topup";

	public static final String MSG_ECONET_BILLPAY = "msg.econet.billpay";

	public static final String MSG_ECONET_BILLPAY_OTHER = "msg.econet.billpay.other";

	public static final String MSG_ECONET_TOPUP_OTHER = "msg.econet.topup.other";

	public static final String MSG_ECONET_TOPUP_OTHER_BENEFICIARY = "msg.econet.topup.other.beneficiary";

	public static final String MSG_ECONET_BILLPAY_OTHER_BENEFICIARY = "msg.econet.billpay.other.beneficiary";

	/**
	 * Some general error has occured.
	 */
	public static final String RC_GENERAL_ERROR = "999";

	public static final String REQUEST_TYPE_BILL_PAYMENT = "BILLPAY";

	public static final String REQUEST_TYPE_TOPUP = "TOPUP";

	public static final String REQUEST_TYPE_SMS = "SMS";

	public static final String REQUEST_TYPE_TRANSFER = "TRANSFER";

	public static final String REQUEST_TYPE_BALANCE_ENQUIRY = "BAL_ENQ";

	public static final String REQUEST_TYPE_MINI_STATEMENT = "MINI_ENQ";

	public static final String SMS_REQUEST_TYPE_BALANCE_ENQUIRY = "BAL";

	public static final String SMS_REQUEST_TYPE_BALANCE_ENQUIRY_LONG = "BALANCE";

	public static final String SMS_REQUEST_TYPE_BILLPAYMENT = "PAY";
	
	public static final String SMS_REQUEST_TYPE_DSTV_BILLPAY = "DSTV";

	public static final String SMS_REQUEST_TYPE_HELP = "HELP";

	public static final String SMS_REQUEST_TYPE_MINI_STATEMENT = "MINI";

	public static final String SMS_REQUEST_TYPE_PINCHANGE = "PIN";

	public static final String SMS_REQUEST_TYPE_PINCHANGE_LONG = "PINCHANGE";

	public static final String SMS_REQUEST_TYPE_TOPUP = "TOPUP";

	public static final String SMSC_SERVICE = "mpg.smsc.service";

	public static final String SMS_REQUEST_EASTER_EGG = "EE";

	public static final String SMS_REQUEST_TYPE_STATEMENT = "STATEMENT";

	public static final String SMS_REQUEST_TYPE_TRANSFER = "TRANSFER";

	public static final String SMS_REQUEST_TYPE_TRANSFER_SHORT = "TXF";

	public static final String SMS_REQUEST_TYPE_TRANSFER_TRF = "TRF";

	public static final String LOGS_FOLDER = "/opt/eSolutions/var/log/";

	public static final String SMS_REQUEST_TYPE_REGISTRATION = "REG";

	// RESPONSE CODES
	public static final String RESPONSE_CODE_OK_SHORT = "00";
	public static final String RESPONSE_CODE_OK_LONG = "000";

	public static final String RESPONSE_CODE_GENERAL_FAILURE = "096";

	public static final String RESPONSE_CODE_VAS_NACK = "001";

	// public static final String STATUS_REJECTED = "REJECTED";
	//
	// public static final String STATUS_SMS_REJECTED = "SMS_REJECTED";
	//
	// public static final String STATUS_DEBIT_RESPONSE = "DEBIT_RESPONSE";
	//
	// public static final String STATUS_CREDIT_ADVICE = "CREDIT_ADVICE";
	//
	// public static final String STATUS_TOPUP_RESPONSE = "TOPUP_RESPONSE";
	//
	// public static final String STATUS_VALIDATION_RESPONSE =
	// "VALIDATION_RESPONSE";
	//
	// public static final String STATUS_REVERSAL_RESPONSE =
	// "TOPUP_REVERSAL_RESPONSE";
	//
	// public static final String STATUS_PARES = "PARES";
	//
	// public static final String STATUS_PAREQ = "PAREQ";
	//
	// public static final String STATUS_VEREQ = "VEREQ";
	//
	// public static final String STATUS_PINCHANGE = "PINCHANGE";
	//
	// public static final String STATUS_PHONE_VALIDATION = "PHONE_VALIDATION";
	//
	// public static final String STATUS_GOOD_MSG = "GOOD_MESSAGE";
	//
	// public static final String STATUS_AWAIT_DEBIT_RESPONSE =
	// "AWAIT_DEBIT_RESPONSE";
	//
	// public static final String STATUS_REVERSAL_REQUEST = "REVERSAL_REQUEST";

	public static final String HELP_GENERAL = "HELP";

	public static final String HELP_TOPUP = "TOPUP";

	public static final String HELP_BILL = "BILL";

	public static final String HELP_PIN = "PIN";

	public static final String HELP_PINCHANGE = "PINCHANGE";

	public static final String HELP_BAL = "BAL";

	public static final String HELP_MINI = "MINI";

	public static final String HELP_TXF = "TXF";

	/**
	 * pcode P100
	 */
	public static final String HOST_PRE_PAYMENT_REQUEST = "Pre Paid Request";

	/**
	 * pcode U500
	 */
	public static final String HOST_BILL_PAYMENT_REQUEST = "Bill Payment Request";

	/**
	 * pcode P200
	 */
	public static final String HOST_MINI_REQUEST = "Mini Statement Request";

	/**
	 * pcode P300
	 */
	public static final String HOST_BALANCE_ENQ_REQUEST = "Balance Enquiry Request";

	public static final String HOST_REVERSAL_REQUEST = "Reversal Request";

	public static final String HOST_TRANSFER_REQUEST = "Transfer Request";

	public static final String HOST_FULL_STATEMENT_REQUEST = "Full Statement Request";

	public static final String HOST_PAYMENT_REQUEST = "Payment Request";

	public static final String STATUS_NEW = "NEW";

	public static final String STATUS_NOT_REGISTERED = "NO_REG";

	public static final String STATUS_MULTIPLE_REGISTRATIONS = "MULTI_REG";

	public static final String STATUS_ALERTED = "ALERTED";

	public static final String STATUS_NO_BANK_ACC = "NO_BANK_ACC";

	public static final String CONF_HANDLER_PREFIX = "acs.alerts.merchant";

	public static final int CONF_HANDLER_PREFIX_LEN = 20;

	public static final String STATUS_ERROR_ALERTING = "ERROR";

	public static final String COMMAND_RESTART = "RESTART";

	public static final String COMMAND_APP_ALL = "ALL";

	public static final String SMS_TYPE_MTSM = "MTSM";

	public static final String SMS_TYPE_MOSM = "MOSM";

	public static final String HOST_ALERT_REGISTRATION = "Alert Registration";

	public static final String SMS_OUT_KEY = "O";

	public static final String EWALLET = "EWALLET";

	public static final String SMS_REQUEST_TYPE_SEND_CASH = "SEND";

	// Agent Constants
	public static final String SMS_REQUEST_TYPE_AGENT_DEPOSIT = "DEPOSIT";
	public static final String SMS_REQUEST_TYPE_AGENT_DEPOSIT_SHORT = "DEP";

	public static final String SMS_REQUEST_TYPE_AGENT_WITHDRAWAL = "WITHDRAW";
	public static final String SMS_REQUEST_TYPE_AGENT_WITHDRAWAL_SHORT = "WITH";

	public static final String SMS_REQUEST_TYPE_AGENT_NONHOLDER_WITHDRAWAL = "CASH";
	public static final String SMS_REQUEST_TYPE_AGENT_NONHOLDER_WITHDRAWAL_SHORT = "CSH";

	public static final String SMS_REQUEST_TYPE_AGENT_SUMMARY = "SUMMARY";
	public static final String SMS_REQUEST_TYPE_AGENT_SUMMARY_SHORT = "SUM";
	public static final String SMS_REQUEST_TYPE_AGENT_SUMMARY_MEDIUM = "SMRY";

	// Secret Code
	public static final String SMS_REQUEST_TYPE_SECRET_CODE_RESET = "SECRETCODE";
	public static final String SMS_REQUEST_TYPE_SECRET_CODE_RESET_SHORT = "CODE";
	public static final String SMS_REQUEST_TYPE_SECRET_CODE_RESET_MEDIUM = "SECRET";

	// Switch Tap
	public static final String SMS_REQUEST_TYPE_TAP = "TAP";
	public static final String SMS_REQUEST_TYPE_SWITCH = "SWITCH";
	public static final String SMPP_IN_TAP = "SMPP_IN_TAP";

	public static final String ACTION_BULK_APPROVE = "Bulk Approve";
	public static final String WEEKDAY_SUNDAY = "Sunday";

	public static final String WEEKDAY_MONDAY = "Monday";

	public static final String WEEKDAY_TUESDAY = "Tuesday";

	public static final String WEEKDAY_WEDNESDAY = "Wednesday";

	public static final String WEEKDAY_THURSDAY = "Thursday";

	public static final String WEEKDAY_FRIDAY = "Friday";

	public static final String WEEKDAY_SATURDAY = "Saturday";

	public static final String HOST_PCODE_BAL_ENQ = "300000";

	public static final String HOST_PCODE_STATEMENT = "P40000";

	public static final String HOST_PCODE_TRANSFER = "400000";

	public static final String HOST_PCODE_BILLPAY = "U50000";

	public static final String HOST_PCODE_TOPUP = "P10000";

	public static final String STATUS_SUCCESSFUL = "SUCCESSFUL";

	public static final String MERCHANT_TYPE_UTILITY = "Utility";

	public static final String STATUS_INACTIVE = "Inactive";

	public static final String REQUEST_TYPE_BULKPAYMENT = "BulkPayment";

	public static final String STATUS_REGISTRATION_INCOMPLETE = "Registration Incomplete";

	public static final String REQUEST_TYPE_NEW_CLIENT = "New client";

	public static final String REQUEST_TYPE_NEW_PAYEE = "New Payee";

	public static final String REQUEST_TYPE_NEW_BANK = "New Bank";

	public static final String BULK_PAYMENT = "Bulk Payment";

	public static final String APPROVAL_TYPE_APPROVAL = "Approval";

	public static final String APPROVAL_TYPE_DISAPPROVAL = "Disapproval";

	public static final String REQUEST_TYPE_NEW_ADMIN_PROFILE = "New Admin Profile";

	public static final String REQUEST_TYPE_NEW_CLIENT_PROFILE = "New Client Profile";

	public static final String ADMIN_INITIATOR_PROFILE = "Admin Initiator";

	public static final String ADMIN_APPROVER_PROFILE = "Admin Approver";

	public static final String CLIENT_APPROVER_PROFILE = "Client Approver";

	public static final String CLIENT_INITIATOR_PROFILE = "Client Initiator";

	public static final String DISPATCHER_TYPE_MANUAL = "Manual";

	public static final String DISPATCHER_TYPE_AUTOMATIC = "Automatic";

	public static final String STATUS_SENT_FOR_PROCESSING = "Send For Processing";

	public static final String STATUS_AWAITING_DOWNLOAD = "Awaiting Download";

	public static final String STATUS_RESPONSE_SENT = "Response Sent";

	public static final String DESTINATION_BANK_CODE = "DESTINATION_BANK_CODE";

	public static final String SOURCE_BANK_CODE = "Source_Bank_Code";

	public static final String BATCH_NUMBER = "Batch_Number";

	public static final String REQUEST_TYPE_NEW_LIMIT = "New Limit";

	public static final String PROFILE_CHANGE = "Profile Change";

	public static final String BANK_CODE = "BANK_CODE";

	public static final String NEW_BANK = "New_Bank";

	public static final String CONN_FACTORY = "jms/Mq";

	public static final String CENTRAL_QUEUE = "jms/MessageQueue";

	public static final String BATCH_TYPE_INCOMING = "Incoming";

	public static final String BATCH_TYPE_OUTGOING = "Outgoing";

	public static final String BATCH_TYPE = "BatchType";

	public static final String BATCH_TYPE_RESPONSES = "Responses";

	public static final String PAYMENT_TYPE_NORMAL = "Normal";

	public static final String PAYMENT_TYPE_SOURCEACC_TO_TREASUARY = "Source To Treasuary";

	public static final String PAYMENT_TYPE_BATCH_PAYMENT = "Batch Payment";

	public static final String PAYMENT_TYPE_TREASUARY_TO_BANK_SUSPENSE = "Treasuary To Bank Suspense";

	public static final String PAYMENT_TYPE_ROLLBACK = "RollBack";

	public static final String DOWNLOADED_BATCH = "DOWNLOADED BATCH";

	public static final String INCOMING_BATCH_REQUEST = "INCOMING BATCH REQUEST";

	public static final String QUERY_PARAMETER_CLIENTID = "clientId";

	public static final String QUERY_PARAMETER_STATUS = "status";

	public static final String QUERY_PARAMETER_SOURCE_ACC = "sourceAccount";

	public static final String QUERY_PARAMETER_STARTDATE = "startDate";

	public static final String QUERY_PARAMETER_ENDDATE = "endDate";

	public static final String DISPATCH_TIMER = "Dispatch Timer";

	public static final String DISPATCH_SETTING_HOURLY = "Hourly";

	public static final String DISPATCH_SETTING_DAILY = "Daily";

	public static final String STATUS_FAILED_INSUFFICIENT_FUNDS = "FAILED_INSUFFICIENT_FUNDS";

	public static final String STATUS_FAILED_INVALID_ACCOUNT = "FAILED_INVALID_ACCOUNT";

	public static final String CREDIT = "Credit";

	public static final String DEBIT = "Debit";

	public static final String STATUS_AWAITING_DISPATCH = "Awaiting Dispatch";

	public static final String STATUS_PROCESSED = "Processed";

	public static final String CLIENT_TYPE_CORPORATE = "Corporate";

	public static final String CLIENT_TYPE_INDIVIDUAL = "Individual";

	public static final String QUERY_PARAMETER_CLIENT_NAME = "clientName";

	public static final String QUERY_PARAMETER_CLIENT_TYPE = "clientType";

	public static final String USER_ROLE_CLIENT_APPROVER = "clientApprover";

	public static final String USER_ROLE_CLIENT_INITIATOR = "clientInitiator";

	public static final String USER_ROLE_ADMIN_APPROVER = "adminApprover";

	public static final String USER_ROLE_BACK_OFFICE_ARROVER = "backOfficeApprover";

	public static String USER_ROLE_BACK_OFFICE_INITIATOR = "backOfficeInitiator";

	public static final String STYLE_SUCCESS = "success";

	public static final String STYLE_FAIL = "fail";

	public static final String STYLE_NORMAL = "normal";

	public static final String USER_ROLE_ADMIN_INITIATOR = "adminInitiator";

	public static final String PAYMENT_TYPE_SETTLEMENT = "settlement";

	public static final String GLOBAL_FIXED_WEEKLY_CHARGE = "Fixed Weekly Charge";

	public static final String GLOBAL_FIXED_MONTHLY_CHARGE = "Fixed Monthly Charge";

	public static final String GLOBAL_FIXED_BULKPAYMENT_CHARGE = "Fixed BulkPayment Charge";

	public static final String GLOBAL_WEEKLY_PERCENTAGE_CHARGE = "Weekly Percentage Charge";

	public static final String GLOBAL_MONTHLY_PECENTAGE_CHARGE = "Monthly Percentage Charge";

	public static final String GLOBAL_BULKPAYMENT_PERCENTAGE_CHARGE = "Bulk Payment Percentage Charge";

	public static final String CHARGE_TYPE_FIXED_PER_BULK = "Fixed amount per bulk";

	public static final String CHARGE_TYPE_FIXED_PER_ENTRY = "Fixed amount per entry";

	public static final String CHARGE_TYPE_PERCENTAGE = "Percentage";

	public static final String CHARGE_FREQUENCY_WEEKLY = "Weekly";

	public static final String CHARGE_FREQUENCY_MONTHLY = "Monthly";

	public static final String CHARGE_FREQUENCY_BULKPAYMENT = "Per BulkPayment";

	public static final String LEVEL_GLOBAL = "Global";

	public static final String LEVEL_LOCAL = "Local";

	public static final String CHARGE_TYPE_FIXED = "Fixed";

	public static final String PAYMENT_TYPE_CHARGE = "Charge";

	public static final String LAST_DAY_OF_MONTH = "Last Day of Month";

	public static final String MONTHLY_PROCESSING_DAY = "MONTHLY_PROCESSING_DAY";

	public static final String MONTHLY_PROCESSING_HOUR = "MONTHLY_PROCESSING_HOUR";

	public static final String MONTHLY_PROCESSING_MINUTE = "MONTHLY_PROCESSING_MINUTE";

	public static final String MONTHLY_PROCESSING_LAST_CHARGING_DATE = "MONTHLY_PROCESSING_LAST_CHARGING_DATE";

	public static final String WEEKLY_PROCESSING_DAY = "WEEKLY_PROCESSING_DAY";

	public static final String WEEKLY_PROCESSING_HOUR = "WEEKLY_PROCESSING_HOUR";

	public static final String WEEKLY_PROCESSING_MINUTE = "WEEKLY_PROCESSING_MINUTE";

	public static final String WEEKLY_PROCESSING_LAST_CHARGING_DATE = "WEEKLY_PROCESSING_LAST_CHARGING_DATE";

	public static final String SETTLEMENT_PROCESSING_HOUR = "SETTLEMENT_PROCESSING_HOUR";

	public static final String SETTLEMENT_PROCESSING_MINUTE = "SETTLEMENT_PROCESSING_MINUTE";

	public static final String BATCH_DISPATCH_HOUR = "BATCH_DISPATCH_HOUR";

	public static final String BATCH_DISPATCH_MINUTE = "BATCH_DISPATCH_MINUTE";

	public static final String BATCH_DISPATCH_INTERVAL = "BATCH_DISPATCH_INTERVAL";

	public static final String PAYMENT_TYPE_COMMISSION = "Commission";

	public static final String DAILY_PROCESSING_HOUR = "DAILY_PROCESSING_HOUR";

	public static final String DAILY_PROCESSING_MINUTE = "DAILY_PROCESSING_MINUTE";

	public static final String DAILY_PROCESSING_INTERVAL = "DAILY_PROCESSING_INTERVAL";

	public static final String CHARGE = "CHARGE";

	public static final String COMMISSION = "COMMISSION";

	public static final String COMMISSION_NAME = "COMMISSION_NAME";

	public static final String COMMISSION_FREQUENCY = "COMMISSION_FREQUENCY";

	public static final String COMMISSION_CALCULATION_BASIS = "COMMISSION_CALCULATION_BASIS";

	public static final String COMMISSION_START_DATE = "COMMISSION_START_DATE";

	public static final String COMMISSION_END_DATE = "COMMISSION_END_DATE";

	public static final String COMMISSION_VALUE = "COMMISSION_VALUE";

	public static final String REQUEST_TYPE_BATCH = "BATCH";

	public static final String BACKOFFICE_APPROVER_PROFILE = "Back Office Approver";

	public static final String TOTAL_BATCH_VALUE = "TOTAL_BATCH_VALUE";

	public static final String BATCH_REPORT = "BATCH_REPORT";

	public static final String TOTAL_BATCH_COMMISSION = "TOTAL_BATCH_COMMISSION";

	public static final String BATCH_TYPE_INTERNAL = "Internal";

	public static final String SETTLEMENT = "SETTLEMENT";

	public static final String DISPATCH_METHOD = "DISPATCH_METHOD";

	public static final String DISPATCH_METHOD_RTGS = "RTGS";

	public static final String DISPATCH_METHOD_FILES = "FILES";

	public static final String NO_OF_PAYMENTS = "NO_OF_PAYMENTS";

	public static final String BANK = "BANK";

	public static final String ACCOUNT = "ACCOUNT";

	public static final String PAYEE = "PAYEE";

	public static final String REFERENCE = "REFERENCE";

	public static final String AMOUNT = "AMOUNT";

	public static final String EXCEL = "EXCEL";

	public static final String SFI = "SFI";

	public static final String PAYMENT_TYPE_SHADOW_PAYMENT = "SHADOW";

	public static final String CSV = "CSV";

	public static final String STATUS_OVERRIDDEN_BY_NEW_REQUEST = "OVERRIDDEN";

	public static final String STATUS_DEACTIVED_PENDING_APPROVAL = "Deactivated and Pending Approval";

	public static final String STATUS_ACTIVED_PENDING_APPROVAL = "Activated and Pending Approval";

	public static final String STATUS_DEACTIVE = "Deactive";

	public static final String REQUEST_TYPE_EDIT_PROFILE = "Edit Profile";

	public static final String REQUEST_TYPE_EDIT_ACCOUNT = "Edit Account";

	public static final String REQUEST_TYPE_NEW_ACCOUNT = "New Account";

	public static final String REQUEST_TYPE_EDIT_BANK = "Edit Bank";

	public static final String REQUEST_TYPE_COMMISSION = "COMMISSION";

	public static final String REQUEST_TYPE_CHARGE = "CHARGE";

	public static final String BATCH_TYPE_RTGS_BATCH = "RTGS_BATCH";

	public static final String AUTH_STATUS_AUTHENTICATED = "Authenticated";
	public static final String AUTH_STATUS_INVALID_CREDENTIALS = "Invalid username or password";
	public static final String AUTH_STATUS_NETWORK_PROBLEM = "Network Problem";
	public static final String AUTH_STATUS_ACCOUNT_LOCKED = "Account has been locked";
	public static final String AUTH_STATUS_CHANGE_PASSWORD = "Should change password";
	public static final String AUTH_STATUS_SYSTEM_ERROR = "System error occured";
	public static final String CHANGE_PASSWORD_SUCCESS = "Change password success";
	public static final String CHANGE_PASSWORD_FAILURE = "Change password failure";
	public static final String INVALID_OLD_PASSWORD = "Incorrect old password";
	public static final String PASSWORD_IN_HISTORY = "Password has already been used";
	public static final String RESET_PASSWORD_SUCCESS = "Reset password success";
	public static final String RESET_PASSWORD_FAILURE = "Reset password failure";
	public static final String AUTH_STATUS_PROFILE_EXPIRED = "Profile Expired";

	public static Properties configParams;
	public static String SYSTEM_CONFIG_FOLDER;
	public static String SYSTEM_CONFIG_FILE;
	public static int ERR_PASSWD_NOT_COMPLEX = 1;
	public static String BUSINESS_DAY_START_HOUR = "BUSINESS_DAY_START_HOUR";
	public static String BUSINESS_DAY_START_MINUTE = "BUSINESS_DAY_START_MINUTE";
	public static String BUSINESS_DAY_START_SECOND = "BUSINESS_DAY_START_SECOND";
	public static String BUSINESS_DAY_START_MILLISECOND = "BUSINESS_DAY_START_MILLISECOND";

	public static String BUSINESS_DAY_END_HOUR = "BUSINESS_DAY_END_HOUR";
	public static String BUSINESS_DAY_END_MINUTE = "BUSINESS_DAY_END_MINUTE";
	public static String BUSINESS_DAY_END_SECOND = "BUSINESS_DAY_END_SECOND";
	public static String BUSINESS_DAY_END_MILLISECOND = "BUSINESS_DAY_END_MILLISECOND";
	public static String SOURCE_APPLICATION_BANK = "BANK";
	public static String SOURCE_APPLICATION_SWITCH = "SWITCH";
	public static final String PROXY_ON = "PROXY_ON";

	public static final String MONTH_ = "MONTH";
	public static String MAX_BATCH_SIZE = "MAX_BATCH_SIZE";
	public static final String NHW_REVERSAL_EXPIRATION_PERIOD = "REVERSAL_EXPIRATION_PERIOD";
	public static final String NHW_PROCESSING_HOUR = "NHW_PROCESSING_HOUR";
	public static final String NHW_PROCESSING_MINUTE = "NHW_PROCESSING_MINUTE";
	public static final String NHW_LAST_PROCCESSING_DATE = "NHW_LAST_PROCCESSING_DATE";
	public static final String NHW_EMAIL = "NHW_EMAIL";
	public static final String TXN_REVERSAL_PROCESSING_HOUR = "TXN_REVERSAL_PROCESSING_HOUR";
	public static final String TXN_REVERSAL_EXPIRATION_PERIOD = "TXN_REVERSAL_EXPIRATION_PERIOD";
	public static final String TXN_LAST_PROCCESSING_TIME = "TXN_LAST_PROCCESSING_TIME";

	public static final String COMMISSION_SWEEPING_HOUR = "COMMISSION_SWEEPING_HOUR";
	public static final String COMMISSION_SWEEPING_MINUTE = "COMMISSION_SWEEPING_MINUTE";
	public static final String COMMISSION_SWEEPING_PERIOD = "COMMISSION_SWEEPING_PERIOD";
	public static final String COMMISSION_SWEEPING_EMAIL = "COMMISSION_SWEEPING_EMAIL";
	
	public static final String AGENT_STATEMENT_HOUR = "AGENT_STATEMENT_HOUR";
	public static final String AGENT_STATEMENT_MINUTE = "AGENT_STATEMENT_MINUTE";
	public static final String AGENT_STATEMENT_EMAIL = "AGENT_STATEMENT_EMAIL";
	
	public static final String AUTOREG_NAME = "AUTO REG";

	public static final String ALREADY_LOOGED_ON = "User is already logged on";

	static {
		String fileSep = System.getProperties().getProperty("file.separator");
		String root = fileSep;
		if (fileSep.equals("\\")) {
			fileSep = "\\\\";
			root = "c:\\";
		}
		SYSTEM_CONFIG_FOLDER = root + "opt" + fileSep + "eSolutions" + fileSep + "conf" + fileSep;
		SYSTEM_CONFIG_FILE = SystemConstants.SYSTEM_CONFIG_FOLDER + "ewallet.conf";

		// read the system configurations
		Properties config = new Properties();
		try {
			config.load(new FileInputStream(SystemConstants.SYSTEM_CONFIG_FILE));
			configParams = config;
			// System.out.println("config:: " + config);
		} catch (Exception ex) {
			System.out.println("Error: Could not read the system configuration file '" + SystemConstants.SYSTEM_CONFIG_FILE + "'. Make sure it exists.");
			System.out.println("Specific error: " + ex);
		}
	}
	public static final String LOG_PROPERTIES_FILE = "/opt/eSolutions/conf/ussd.log.properties";
	public static final String ECONET_ACQUIRER_ID = "Econet";

	public static final String PCODE30 = "300000";
	public static final String PCODEU5 = "U50000";

	public static final int ISO_FIELD_PRIMARY_ACCOUNT_NUMBER = 2;

	public static final int ISO_FIELD_PROCESSING_CODE = 3;

	public static final int ISO_FIELD_TRANSACTION_AMOUNT = 4;

	public static final int ISO_FIELD_TRANSMISSION_DATE = 7;

	public static final int ISO_FIELD_SYSTEMS_TRACE_AUDIT = 11;

	public static final int ISO_FIELD_MERCHANT_TYPE = 18;

	public static final int ISO_FIELD_ACQUIRING_INSTITUTION_ID = 32;

	public static final int ISO_FIELD_FORWARDING_INSTITUTION_ID = 33;

	public static final int ISO_FIELD_PRIMARY_ACCOUNT_EXTENDED = 34;

	public static final int ISO_FIELD_RETRIEVAL_REF = 37;

	public static final int ISO_FIELD_AUTHORIZATION_ID_RESPONSE = 38;

	public static final int ISO_FIELD_RESPONSE_CODE = 39;

	public static final int ISO_FIELD_CARD_ACCEPTOR_TERMINAL_ID = 41;

	public static final int ISO_FIELD_CARD_ACCEPTOR_ID = 42;

	public static final int ISO_FIELD_CARD_ACCEPTOR_NAME_LOCATION = 43;

	public static final int ISO_FIELD_ADDITIONAL_RESPONSE_DATA = 44;

	public static final int ISO_FIELD_ADDITIONAL_DATA_ISO = 46;

	public static final int ISO_FIELD_TRANSACTION_CURRENCY_CODE = 49;

	public static final int ISO_FIELD_ADDITIONAL_AMOUNTS = 54;

	public static final int ISO_FIELD_OTHER_AMOUNT = 61;

	public static final int ISO_FIELD_ORIGINAL_DATA_ELEMENTS = 90;

	public static final int ISO_FIELD_REPLACEMENT_AMOUNTS = 95;

	/** Source Account or From Account. */
	public static final int ISO_FIELD_ACCOUNT_IDENTIFICATION_1 = 102;

	/** Target Account or To Account. */
	public static final int ISO_FIELD_ACCOUNT_IDENTIFICATION_2 = 103;

	/** Utility Account */
	public static final int ISO_FIELD_TRANSACTION_DESCRIPTION = 104;

	public static final String MTI_TXN_RQST = "0200";
	public static final String MTI_TXN_RESP = "0210";
	public static final String MTI_REVERSAL_RQST = "0420";
	public static final String MTI_REVERSAL_RESP = "0430";

	static TransactionType[] agentTransactions = { TransactionType.AGENT_ACCOUNT_BALANCE, TransactionType.AGENT_CASH_DEPOSIT, TransactionType.AGENT_CUSTOMER_DEPOSIT, TransactionType.AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL, TransactionType.AGENT_CUSTOMER_WITHDRAWAL, TransactionType.AGENT_EMONEY_TRANSFER, TransactionType.AGENT_EWALLET_TO_BANKACCOUNT_TRANSFER, TransactionType.AGENT_SUMMARY, TransactionType.CHANGE_PASSCODE};
	
	public static boolean canDo(CustomerClass customerClass, TransactionType txnType) {
		boolean canDo = false;
		if (CustomerClass.AGENT.equals(customerClass)) {
			for (TransactionType transactionType : agentTransactions) {
				if (transactionType.equals(txnType)) {
					canDo = true;
					break;
				}
			}
			return canDo;
		}else{
			return true;
		}
	}
	
}
