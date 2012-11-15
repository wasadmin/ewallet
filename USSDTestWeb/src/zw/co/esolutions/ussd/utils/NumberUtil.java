package zw.co.esolutions.ussd.utils;


public class NumberUtil {

	public static final double roundDouble(double d, int places) {
		return Math.round(d * Math.pow(10, (double) places)) / Math.pow(10, (double) places);
	}

	public static MobileNetworkOperator getMNO(String mobileNumber) {
		if (mobileNumber.startsWith("26373")) {
			return MobileNetworkOperator.TELECEL;
		} else if (mobileNumber.startsWith("26371")) {
			return MobileNetworkOperator.NETONE;
		} else if (mobileNumber.startsWith("26377")) {
			return MobileNetworkOperator.ECONET;
		} else {
			return null;
		}
	}

	public static String formatMobileNumber(String mobileNumber) throws Exception {
		if (mobileNumber == null) {
			throw new Exception("Mobile number is NULL");
		}

		mobileNumber = mobileNumber.trim().replace(" ", "");

		if (mobileNumber.startsWith("+263")) {
			mobileNumber = mobileNumber.substring(1);
		}

		if (mobileNumber.startsWith("07")) {
			mobileNumber = "263" + mobileNumber.substring(1);
		}

		if (mobileNumber.startsWith("2637")) {
			if (mobileNumber.length() != 12) {
				throw new Exception("Mobile number length must be 12: yours has " + mobileNumber.length());
			}
		} else {
			throw new Exception("Mobile Number is invalid: does not contain 07 prefix.");
		}
		return mobileNumber;
	}

	public static String formatMobile(String mobileNumber) throws Exception {
		if (mobileNumber == null) {
			throw new Exception("Mobile number is NULL");
		}

		mobileNumber = mobileNumber.trim().replace(" ", "");

		if (mobileNumber.startsWith("+263")) {
			mobileNumber = mobileNumber.substring(1);
		}

		if (mobileNumber.startsWith("07")) {
			mobileNumber = "263" + mobileNumber.substring(1);
		}

		return mobileNumber;
	}

	public static boolean validateMobileNumber(String mobileNumber) {
		try {
			formatMobileNumber(mobileNumber);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
