/**
 * 
 */
package zw.co.esolutions.bankif.util;

/**
 * @author blessing
 *
 */
public interface InterfaceConstants {

	String STATUS_SENT_TO_HOST = "SENT_TO_HOST";
	String STATUS_REVERSAL_REQUEST = "REVERSAL_REQUEST";
	String STATUS_REVERSAL_FAILED = "REVERSAL_FAILED";
	String STATUS_SUCCESSFUL = "SUCCESSFUL";
	String STATUS_FAILED = "FAILED";
	String MANUAL_RESOLVE = "MANUAL_RESOLVE";
	String STATUS_NOT_YET_POSTED = "NOT YET POSTED";
	String STATUS_REVERSAL_SUCCESSFUL = "REVERSAL_SUCCESSFUL";
	
	String MSG_TYPE_0210 = "0210";
	String MSG_TYPE_0430 = "0430";
	String MSG_TYPE_0220 = "0220";
	String MSG_TYPE_0230 = "0230";
	String MSG_TYPE_0200 = "0200";
	String MSG_TYPE_0420 = "0420";
	
	String RC_OK = "00";
	String RC_OK_LONG = "000";
	
	String BALANCE_SIGN_D = "D";
	String BALANCE_SIGN_C = "C";
	
	
	
	
	
	public static final int ISO_FIELD_PRIMARY_ACCOUNT_NUMBER = 2;

	public static final int ISO_FIELD_PROCESSING_CODE = 3;

	public static final int ISO_FIELD_TRANSACTION_AMOUNT = 4;

	public static final int ISO_FIELD_TRANSMISSION_DATE = 7;

	public static final int ISO_FIELD_SYSTEMS_TRACE_AUDIT = 11;

	public static final int ISO_FIELD_MERCHANT_TYPE = 18;

	public static final int ISO_FIELD_ACQUIRING_INSTITUTION_ID = 32;

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
	
	
}
