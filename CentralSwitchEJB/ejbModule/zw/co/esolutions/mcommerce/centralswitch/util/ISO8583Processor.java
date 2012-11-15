package zw.co.esolutions.mcommerce.centralswitch.util;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.ewallet.enums.TransactionStatus;
import zw.co.esolutions.ewallet.util.Formats;
import zw.co.esolutions.ewallet.util.SystemConstants;
import zw.co.esolutions.mcommerce.centralswitch.model.MessageTransaction;
import zw.co.esolutions.mcommerce.centralswitch.model.TransactionRoutingInfo;
import zw.co.esolutions.mcommerce.xml.ISOMarshaller;
import zw.co.esolutions.mcommerce.xml.ISOMarshallerException;
import zw.co.esolutions.mcommerce.xml.ISOMsg;
import zw.co.esolutions.mcommerce.xml.Messages;
import zw.co.esolutions.mcommerce.xml.MetaData;

public class ISO8583Processor {

	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/switch.log.properties");
			LOG = Logger.getLogger(ISO8583Processor.class);
		} catch (Exception e) {
		}
	}

	public static String convertToISO8583XMLString(MessageTransaction txn, boolean isReversal) {
		Messages messages = convertToISO8583Message(txn, isReversal);
		if (messages != null) {
			return convertISOMessageToXML(messages);
		}
		return "";
	}

	public static Messages convertToISO8583Message(MessageTransaction txn, boolean isReversal) {
		Messages messages = new Messages();
		ISOMsg isoMsg = new ISOMsg();
		MetaData metaData = new MetaData();

		// Set the header info
		messages.setIsoMsg(isoMsg);
		messages.setMetadata(metaData);

		TransactionRoutingInfo routingInfo = txn.getTransactionRoutingInfo();
		
		// get ZSW bank code
		metaData.setPostingBranch(""+routingInfo.getZswCode());

		// this must be the source bank name
		metaData.setSource(routingInfo.getBankName() + "|" + txn.getTransactionLocationType());

		// the queue to which the msg will be sent when replying to the switch
		metaData.setReplyQueue(routingInfo.getMerchantReplyQueueName());

		// the queue to which the msg must be sent when going to the merchant
		metaData.setRequestQueue(routingInfo.getMerchantRequestQueueName());

		isoMsg.setRetrievalReference(txn.getUuid());

		if (isReversal) {
			isoMsg.setMti(SystemConstants.MTI_REVERSAL_RQST);
		} else {
			isoMsg.setMti(SystemConstants.MTI_TXN_RQST);
		}

		TransactionStatus txnStatus = txn.getStatus();
		if (TransactionStatus.ACCOUNT_VALIDATION_RQST.equals(txnStatus)) {
			isoMsg.setISOField(SystemConstants.ISO_FIELD_PROCESSING_CODE, SystemConstants.PCODE30);
		} else if (TransactionStatus.CREDIT_REQUEST.equals(txnStatus)) {
			isoMsg.setISOField(SystemConstants.ISO_FIELD_PROCESSING_CODE, SystemConstants.PCODEU5);
		} else if (TransactionStatus.REVERSAL_REQUEST.equals(txnStatus)) {
			isoMsg.setISOField(SystemConstants.ISO_FIELD_PROCESSING_CODE, SystemConstants.PCODEU5);
			isoMsg.setMti(SystemConstants.MTI_REVERSAL_RQST);
		}
		// SET PAN to EMPTY
		isoMsg.setISOField(SystemConstants.ISO_FIELD_PRIMARY_ACCOUNT_NUMBER, "");

		isoMsg.setISOField(SystemConstants.ISO_FIELD_TRANSACTION_AMOUNT, Formats.intFormat.format(txn.getAmount()));
		isoMsg.setISOField(SystemConstants.ISO_FIELD_TRANSMISSION_DATE, Formats.merchantDateFormat.format(txn.getDateCreated()));
		isoMsg.setISOField(SystemConstants.ISO_FIELD_MERCHANT_TYPE, txn.getUtilityName());
		isoMsg.setISOField(SystemConstants.ISO_FIELD_ACQUIRING_INSTITUTION_ID, "e-Solutions");
		isoMsg.setISOField(SystemConstants.ISO_FIELD_AUTHORIZATION_ID_RESPONSE, "");
		isoMsg.setISOField(SystemConstants.ISO_FIELD_CARD_ACCEPTOR_TERMINAL_ID, ""+ routingInfo.getZesaPayCode());
		isoMsg.setISOField(SystemConstants.ISO_FIELD_FORWARDING_INSTITUTION_ID, ""+routingInfo.getZesaPayBranch());
		isoMsg.setISOField(SystemConstants.ISO_FIELD_ADDITIONAL_DATA_ISO, "");
		isoMsg.setISOField(SystemConstants.ISO_FIELD_TRANSACTION_CURRENCY_CODE, "USD");
		isoMsg.setISOField(SystemConstants.ISO_FIELD_ACCOUNT_IDENTIFICATION_1, txn.getCustomerUtilityAccount());

		String description = txn.getCustomerName() + "|" + txn.getSourceMobileNumber();
		isoMsg.setISOField(SystemConstants.ISO_FIELD_TRANSACTION_DESCRIPTION, description);
		return messages;

	}

	public static String convertISOMessageToXML(Messages messages) {
		try {
			return ISOMarshaller.marshal(messages.getIsoMsg(), messages.getMetadata());
		} catch (ISOMarshallerException e) {
			e.printStackTrace(System.err);
			LOG.error("Failed to convers RQST to XML String");
			return "";
		}

	}

}
