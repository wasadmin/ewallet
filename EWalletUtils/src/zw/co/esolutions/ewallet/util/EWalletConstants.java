package zw.co.esolutions.ewallet.util;

public class EWalletConstants {
	public static final String FROM_SWITCH_TO_ZB_QUEUE = "jms/ZB.IN.QUEUE"; 
	public static final String FROM_IB_TO_EWALLET_BULK_QUEUE="jms/EWALLET.BULK.Q";
	public static final String FROM_EWALLET_TO_SWITCH_QUEUE = "jms/SWITCH.IN.QUEUE";
	public static final String FROM_BANKMED_TO_EWALLET_QUEUE = "jms/BANKMED.OUT.QUEUE";
	public static final String FROM_EWALLET_TO_BANKMED_QUEUE = "jms/BANKMED.IN.QUEUE";
	public static final String FROM_BANKMED_TO_BANK_SYS_QUEUE = "jms/ACS.MSGS.Q";
	public static final String FROM_BANK_SYS_TO_BANKMED_QUEUE = "jms/REPLY.ACS.Q";
	public static final String FROM_EWALLET_TO_SWITCH_REG_QUEUE = "jms/REG.IN.QUEUE";
	public static final String FROM_SWITCH_REG_TO_ZB_EWALLET_QUEUE = "jms/ZB.REG.OUT.QUEUE";
	public static final String EWALLET_QCF = "jms/EWalletQCF";
	public static final String SMPP_QCF = "jms/SMPPQCF";
	public static final String FROM_USSD_TO_SWITCH_QUEUE = "jms/ECONET.USSD.REQ.Q";
	public static final String MONTHLY_HOUR = "17";
	public static final String MONTHLY_MIN = "00";
	public static final String FROM_EWALLET_TO_REG_QUEUE = "jms/REG.IN.QUEUE";
	public static final String ECONET_SMS_IN_QUEUE = "jms/ECONET.SMPP.IN.QUEUE";
	public static final String ECONET_SMS_OUT_QUEUE = "jms/ECONET.SMPP.OUT.QUEUE";
	public static final String ACS_REPLY_QUEUE = "jms/REPLY.ACS.Q";
	public static final String MESSAGE_SEPERATOR = "|";
	public static final String SYSTEM = "SYSTEM";
	public static final String SYSTEM_BANKS_DELIMITER = "ZB";
	public static final String USD = "USD";
	public static final String DOWNLOAD_BANKS = "DOWNLOAD_BANKS";
	public static final String DOWNLOAD_MERCHANTS = "DOWNLOAD_MERCHANTS";
	public static final String ZB_CODE = "589003";
	public static final String DOWNLOAD_TYPE = "DOWNLOAD_TYPE";
	public static final String DATABASE_SCHEMA = "BANKIF";
	public static final String SUBREPORT = "SUBREPORT";
	public static final String MASTER_REPORT = "MASTER_REPORT";
	public static final String JASPER_CONN = "JASPER_CONN";
;	public static final String EWALLET_BOOK_ENTRY_FAILURE_NARRATIVE="e-Wallet Book entry failed";
	
	public static final String SEQUENCE_NAME_DAY_ENDS = "Day Ends";
	public static final String SEQUENCE_NAME_WITHDRAWAL = "WITHDRAWAL";
	public static final String SEQUENCE_PREFIX_WITHDRAWAL = "Y";	
	public static final String SEQUENCE_PREFIX_DAY_ENDS = "A";
	public static final String SEQUENCE_PREFIX_COMMISSION = "C";
	public static final String SEQUENCE_NAME_CHARGE = "CHARGE";
	public static final String SEQUENCE_NAME_STAN = "STAN";	
	public static final int REPORT_MONTHS = 1;
	public static final String USSD_PREFIX = "U";
	public static final String USSD_SEQUENCE_NAME = "USSD";
	public static final long USSD_MIN_VALUE = 1;
	public static final long USSD_MAX_VALUE = 999999999;
	
	/*
	 * please return
	 */
	//public static final long TRANSACTION_TIMEOUT=50;
	public static final String REF_NARRATIVE = "The reference code is ";
	public static final long RESPONSE_CHECK_INTERVAL = 15000;
	//public static final long RESPONSE_CHECK_INTERVAL = 50;
	public static final int MAX_RESULTS = 10000;
	public static final String EJB_TIMER_ID = "tautteeEjbTimer";
	public static final String NHW_TIMER_ID = "nhwReversalTimer";
	public static final int TIMER_INCREMENT_SECONDS = 20;
	public static final String AGENT_COMMISSION = "AGENT_COMMISSION";
	public static final String AGENT_CHARGE = "AGENT_CHARGE";
	public static final long PERCENTAGE_DIVISOR= 10000L;
	public static final String STATUS_FAILED = "FAILED";
	public static final String STATUS_COMPLETED = "COMPLETED";
	public static final String BULK_BATCH_RESPONSE_NO_REF="Missing batch reference";
	public static final String BULK_BATCH_RESPONSE_NO_BULK_BATCH_ITEMS="No bulk batch items present";
	public static final String CHECK_STATUS_SUCCESS = "SUCCESS";
	public static final String CHECK_STATUS_FAILED = "FAILED";
	public static final String ZB_BANK = "ZB BANK";
	public static final String CARD_SERVICES_BRANCH_CODE = "4131";
	
	public static final String FROM_EWALLET_TO_IB_QUEUE = "jms/IBBULK.REPLY.Q";
	public static final String CHANGE_PIN = "CHGPIN";
	public static final String JASPER_COLLECTION_DATASOURSE = "JASPER_COLLECTION_DATASOURSE";
	
	public static final String MERCHANT_NAME_DSTV = "DSTV";
	public static final String ALERT_OPTION_STATUS_ACTIVE = "ACTIVE";
	public static final String ALERT_OPTION_STATUS_DISABLED = "DISABLED";

	
}
