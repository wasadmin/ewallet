/**
 * 
 */
package zw.co.esolutions.bankif.zb;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.xmlbeans.XmlException;

import zw.co.datacentre.xml.bet.ISO8583Marshaller;
import zw.co.datacentre.xml.bet.ISOMsg;
import zw.co.datacentre.xml.bet.ISOMsgField;
import zw.co.datacentre.xml.bet.Messages;
import zw.co.datacentre.xml.bet.MetaData;
import zw.co.esolutions.bankif.model.BankRequestMessage;
import zw.co.esolutions.bankif.model.CommissionMessage;
import zw.co.esolutions.bankif.util.BankMessageFactory;
import zw.co.esolutions.bankif.util.InterfaceConstants;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.msg.BankRequest;
import zw.co.esolutions.ewallet.msg.BankResponse;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.Formats;

/**
 * @author blessing
 * 
 */
public class ZBBankMessageFactory implements BankMessageFactory {
	;
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * zw.co.esolutions.bankif.util.BankMessageFactory#createBankMessage(zw.
	 * co.esolutions.ewallet.msg.BankRequest)
	 */
	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/hostInterface.log.properties");
			LOG = Logger.getLogger(ZBBankMessageFactory.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + ZBBankMessageFactory.class);
			e.printStackTrace(System.err);
		}
	}

	private static BankMessageFactory instance;

	public static BankMessageFactory getInstance() {
		if (instance == null) {
			instance = new ZBBankMessageFactory();
		}

		return instance;
	}

	@Override
	public String createBankMessage(zw.co.esolutions.bankif.model.BankRequestMessage bankRequestMessage) {
		try {
			return this.createBETXMLMessage(bankRequestMessage, false);
		} catch (Exception e) {
			LOG.fatal("FAILED to create ISO MSG for " + bankRequestMessage.getReference(), e);
			e.printStackTrace(System.err);
		}
		return "";
	}

	
	private String createBETXMLMessage(zw.co.esolutions.bankif.model.BankRequestMessage bankRequestMessage, boolean isReversal)  throws Exception {
		
		//SET META DATA INFO
        MetaData meta = new MetaData();
        meta.setSource("EWALLET");
        meta.setPostingBranch(EWalletConstants.CARD_SERVICES_BRANCH_CODE);
        meta.setReplyQueue("EWALLET.RESPONSES.QUEUE");
		
      //Header INFO - REF and MSG TYPE [0200, 0420]
		ISOMsg isoMsg = new ISOMsg();
        isoMsg.setRetrievalReference(bankRequestMessage.getReference());
        if(isReversal){
        	isoMsg.setMti("0420");
        }else{
        	if (bankRequestMessage.getMessageType() != null) {
        		isoMsg.setMti(bankRequestMessage.getMessageType());
        	} else {
        		isoMsg.setMti("0200");
        	}
        }
		
        LOG.debug("MTI set: " + isoMsg.getMti());
        
		//PAN
		isoMsg.setISOField(InterfaceConstants.ISO_FIELD_PRIMARY_ACCOUNT_NUMBER, "");
		
		//PCODE
		isoMsg.setISOField(InterfaceConstants.ISO_FIELD_PROCESSING_CODE, this.format(bankRequestMessage.getProcessingCode(), 6, "0", false));
		
		//FIELD 4 is TXN AMOUNT in cents
		isoMsg.setISOField(InterfaceConstants.ISO_FIELD_TRANSACTION_AMOUNT, Formats.intFormat.format(bankRequestMessage.getAmount()));
		
        //FIELD 7 is DATE in the format MMddHHmmss, 10 charactors
		isoMsg.setISOField(InterfaceConstants.ISO_FIELD_TRANSMISSION_DATE, Formats.equationDateFormat.format(bankRequestMessage.getValueDate()));
				
        //FIELD 11 is STAN in the format
        isoMsg.setISOField(11, Formats.intFormat.format(bankRequestMessage.getSystemTraceAuditNumber()));
		
        //FIELD 18 is MERCHANT TYPE
        isoMsg.setISOField(18, bankRequestMessage.getAcquirerId());
		
        //FIELD 32 is Acquirer ID
        isoMsg.setISOField(32, bankRequestMessage.getAcquirerId());
                
        //FIELD 38 is authorisation identification response
        isoMsg.setISOField(38, "");
        
        //FIELD 41 is Card Acceptor Terminal
        isoMsg.setISOField(41, "EWALLET");
                
        //FIELD 46 is Additional Data
        if (bankRequestMessage.getBouquetCode() != null) {
	        isoMsg.setISOField(46, bankRequestMessage.getReference() + "|" + bankRequestMessage.getBeneficiaryName() + "|" + bankRequestMessage.getCustomerUtilityAccount() + "|" + bankRequestMessage.getSourceMobileNumber() + "|" + bankRequestMessage.getBouquetCode() + "|");
        } else if(TransactionType.ALERT_REG.equals(bankRequestMessage.getTransactionType()) || TransactionType.ALERT_DEREG.equals(bankRequestMessage.getTransactionType())) {
	        isoMsg.setISOField(46, bankRequestMessage.getTransactionType().name().replace("ALERT_", ""));
        } else { 
	        isoMsg.setISOField(46, bankRequestMessage.getPaymentRef() + "|" + bankRequestMessage.getBeneficiaryName() + "|" + bankRequestMessage.getDestinationBankName() + "|");
        }
        
        //FIELD 49 is Currency
        isoMsg.setISOField(49, bankRequestMessage.getCurrencyISOAlphabetic() == null ? "USD" : bankRequestMessage.getCurrencyISOAlphabetic());
        
        //FIELD 54 is Additional Amounts
        isoMsg.setISOField(54, Formats.intFormat.format(bankRequestMessage.getTotalChargesAmount()));
		
        //FIELD 102 is source account 
        isoMsg.setISOField(102, bankRequestMessage.getSourceAccountNumber());
        
        //FIELD 103 is target account 
        isoMsg.setISOField(103, bankRequestMessage.getTargetAccountNumber());
        
        //FIELD 104 is Narratives 
        isoMsg.setISOField(InterfaceConstants.ISO_FIELD_TRANSACTION_DESCRIPTION, bankRequestMessage.getNarrative());
        
        return ISO8583Marshaller.marshal(isoMsg, meta);
	}
	
	private String createBETXMLCharge(CommissionMessage info, boolean isReversal) throws Exception {
		
		//SET META DATA INFO
        MetaData meta = new MetaData();
        meta.setSource("EWALLET");
        meta.setPostingBranch(EWalletConstants.CARD_SERVICES_BRANCH_CODE);
        meta.setReplyQueue("EWALLET.RESPONSES.QUEUE");
		
      //Header INFO - REF and MSG TYPE [0200, 0420]
		ISOMsg isoMsg = new ISOMsg();
        isoMsg.setRetrievalReference(info.getReference());
        if(isReversal){
        	isoMsg.setMti("0420");
        }else{
        	String mti = info.getBankRequest().getMessageType();
        	if (mti != null) {
        		if (InterfaceConstants.MSG_TYPE_0230.equals(mti)) {
        			isoMsg.setMti(InterfaceConstants.MSG_TYPE_0220);
        		} else if (InterfaceConstants.MSG_TYPE_0210.equals(mti)) {
        			isoMsg.setMti(InterfaceConstants.MSG_TYPE_0200);
        		} else {
        			isoMsg.setMti(mti);
        		}
        	} else {
        		isoMsg.setMti("0200");
        	}
        }
		
        LOG.debug("MTI for charge: " + isoMsg.getMti());
        
		//PAN
		isoMsg.setISOField(InterfaceConstants.ISO_FIELD_PRIMARY_ACCOUNT_NUMBER, "");
		
		//PCODE
		isoMsg.setISOField(InterfaceConstants.ISO_FIELD_PROCESSING_CODE, this.format(info.getProcessingCode(), 6, "0", false));
		
		//FIELD 4 is TXN AMOUNT in cents
		isoMsg.setISOField(InterfaceConstants.ISO_FIELD_TRANSACTION_AMOUNT, Formats.intFormat.format(info.getAmount()));
		
        //FIELD 7 is DATE in the format MMddHHmmss, 10 charactors
		isoMsg.setISOField(InterfaceConstants.ISO_FIELD_TRANSMISSION_DATE, Formats.equationDateFormat.format(info.getValueDate()));
				
        //FIELD 11 is STAN in the format
        isoMsg.setISOField(11, Formats.intFormat.format(info.getSystemTraceAuditNumber()));
		
        //FIELD 18 is MERCHANT TYPE
        isoMsg.setISOField(18, info.getBankRequest().getAcquirerId());
		
        //FIELD 32 is Acquirer ID
        isoMsg.setISOField(32, info.getBankRequest().getAcquirerId());
                
        //FIELD 38 is authorisation identification response
        isoMsg.setISOField(38, "");
        
        //FIELD 41 is Card Acceptor Terminal
        isoMsg.setISOField(41, "EWALLET");
                
        //FIELD 46 is Additional Data 
        isoMsg.setISOField(46, info.getNarrative());
        
        //FIELD 49 is Currency
        isoMsg.setISOField(49, info.getBankRequest().getCurrencyISOAlphabetic() == null ? "USD" : info.getBankRequest().getCurrencyISOAlphabetic());
        
        //FIELD 54 is Additional Amounts
        isoMsg.setISOField(54, Formats.intFormat.format(0));
		
        //FIELD 102 is source account 
        isoMsg.setISOField(102, info.getSourceEq3Account());
        
        //FIELD 103 is target account 
        isoMsg.setISOField(103, info.getTargetEq3Account());
        
        //FIELD 104 is Narratives 
        isoMsg.setISOField(InterfaceConstants.ISO_FIELD_TRANSACTION_DESCRIPTION, info.getNarrative());
        
        return ISO8583Marshaller.marshal(isoMsg, meta);
	}
	
	
	private String format(String data, int length, String fillCharactor, boolean leftPad) {
		if (fillCharactor == null) {
			fillCharactor = " ";
		}
		if (data == null)
			data = "";
		if (data.length() > length) {
			data = data.substring(0, length);
		}
		while ((length > 0) && (data.length() < length)) {
			if (leftPad) {
				data = fillCharactor + data;
			} else {
				data = data + fillCharactor;
			}
		}
		return data;
	}

	@Override
	public BankResponse parseBankResponse(String receivedText) {
		if(receivedText == null){
			LOG.warn("Received a NULL message");
			return null;
		}
		LOG.debug("Parsing BET XML Response");
		return this.parseBETXMLMessage(receivedText);
	}

	private BankResponse parseBETXMLMessage(String xml){
		// must be parsing a 0210
		try {
			ISOMsg isoMsg = ISO8583Marshaller.unmarshalISO(xml);
			BankResponse response = new BankResponse();
			BankRequest request = new BankRequest();
			response.setBankRequest(request);
			response.setMessageType(isoMsg.getMti());
			LOG.debug("GOT MTI : " + response.getMessageType());
			request.setReference(isoMsg.getRetrievalReference());
			response.setBankReference(isoMsg.getRetrievalReference());
			LOG.debug("GOT REF : " + request.getReference());
			//get field number 3, PCODE
			ISOMsgField field = isoMsg.getISOField(3);
			response.setProcessingCode(field.getValue());
			LOG.debug("GOT PCODE : " + response.getProcessingCode());
			if ("T00000".equals(response.getProcessingCode())) {
				request.setTransactionType(TransactionType.TARIFF);
			}
			if ("T10000".equals(response.getProcessingCode())) {
				request.setTransactionType(TransactionType.COMMISSION);
			}
			if ("P20000".equalsIgnoreCase(response.getProcessingCode())){
				//Now get mini statement data
				field = isoMsg.getISOField(46);
				String additionalData = field.getValue();
				LOG.debug("GOT ADD DATA : " + additionalData);
				response.setAdditionalData(additionalData.replace('|', ' ').trim().replace('/', '-'));
				request.setTransactionType(TransactionType.MINI_STATEMENT);
			}			
			try {
				//FIELD 4 is TXN AMOUNT in cents
				field = isoMsg.getISOField(4);
				LOG.debug("GOT AMT : " + field.getValue());
				request.setAmount(Formats.intFormat.parse(field.getValue()).longValue());
			} catch (ParseException e) {
				request.setAmount(0L);
			}
			
			//FIELD 7 is DATE in the format MMddHHmmss, 10 charactors
	        field = isoMsg.getISOField(7);
	        LOG.debug("GOT VALUE DATE : " + field.getValue());
			try {
				Formats.equationDateFormat.setCalendar(Calendar.getInstance());
				Calendar cal = Calendar.getInstance();
				int year = cal.get(Calendar.YEAR);
				cal.setTime(Formats.equationDateFormat.parse(field.getValue()));
				cal.set(Calendar.YEAR, year);
				response.setValueDate(new Date(cal.getTimeInMillis()));
			} catch (ParseException e) {
				response.setValueDate(new Timestamp(System.currentTimeMillis()));
			}
	
			//FIELD 11 is STAN in the format
	        field = isoMsg.getISOField(11);
	        LOG.debug("GOT STAN : " + field.getValue());
	        try {
				response.setTrace(Formats.intFormat.parse(field.getValue()).intValue());
			} catch (ParseException e) {
				response.setTrace(0);
			}
			
			//RESPONSE CODE
			field = isoMsg.getISOField(39);
			response.setBankResponseCode(field.getValue());
			
			if(InterfaceConstants.MSG_TYPE_0220.equalsIgnoreCase(response.getMessageType())){
				field = isoMsg.getISOField(46);
				String additionalData = field.getValue();
				LOG.debug("GOT ADD DATA : " + additionalData);
				response.setAdditionalData(additionalData.replace('|', ' ').trim().replace('/', '-'));
				response.setNarrative(response.getAdditionalData());
				request.setTransactionType(TransactionType.ALERT);
				field = isoMsg.getISOField(102);
				LOG.debug("GOT SRC ACC NO : " + field.getValue());
			    request.setSourceAccountNumber(field.getValue());
				return response;
			}
			
			//reversals don't show balances so we return here
			if(InterfaceConstants.MSG_TYPE_0430.equalsIgnoreCase(response.getMessageType())){
				if (InterfaceConstants.RC_OK.equalsIgnoreCase(response.getBankResponseCode())){
					response.setNarrative("Reversal Successful");
				}else{
					//FIELD 44 Additional Response data : WHY THE TXN FAILED
			        field = isoMsg.getISOField(44);
					response.setNarrative(field.getValue());
					LOG.debug("GOT NAR : " + response.getNarrative());
				}
				return response;
			}
			
			if (InterfaceConstants.RC_OK.equalsIgnoreCase(response.getBankResponseCode())) {
				//Should get balance info in additional amounts : FIELD 46 or FIELD 54
				
				try {
					
					field = isoMsg.getISOField(54);
					String additionalAmt = field.getValue().trim();
					LOG.debug("GOT ADD AMT : " + additionalAmt);
											
					String availableBalance = additionalAmt.substring(8, 20);
					String availSign = additionalAmt.substring(7, 8);
					String ledg = additionalAmt.substring(28, additionalAmt.length());
					String ledgSign = additionalAmt.substring(27, 28);
					
					response.setAvailableBalance(Long.parseLong(availableBalance));
					response.setLedgerBalance(Long.parseLong(ledg));
					
					if (InterfaceConstants.BALANCE_SIGN_D.equalsIgnoreCase(availSign)) {
						response.setAvailableBalance(-1 * response.getAvailableBalance());
					}
					if (InterfaceConstants.BALANCE_SIGN_D.equalsIgnoreCase(ledgSign)) {
						response.setLedgerBalance(-1 * response.getLedgerBalance());
					}
					
				} catch (Exception e) {
					
					LOG.warn("FAILED to parse field 54... ADDITIONAL AMTS.." + e.getClass());
					
				}
				
				response.setNarrative("Transaction Successful");
				
			}else{
				//FIELD 44 Additional Response data : WHY THE TXN FAILED
		        field = isoMsg.getISOField(44);
				response.setNarrative(field.getValue());
				
			}
			field = isoMsg.getISOField(102);
			LOG.debug("GOT SRC ACC NO : " + field.getValue());
		    request.setSourceAccountNumber(field.getValue());
	        return response;
		} catch (Exception e) {
			e.printStackTrace(System.err);
			LOG.fatal("Failed to parse BANK Response Message \n\t" + xml, e);
			return null;
		}
		
	}
	
	private BankResponse parseBankResponseReversal(String input) {
		return this.parseBETXMLMessage(input);
	}

	@Override
	public String createReversalMessage(BankRequestMessage bankRequestMessage) {
		try {
			return this.createBETXMLMessage(bankRequestMessage, true);
		} catch (Exception e) {
			LOG.fatal("FAILED to create ISO MSG for " + bankRequestMessage.getReference(), e);
			e.printStackTrace(System.err);
		}
		return "";
	}

	@Override
	public String createMainTransactionReversalMessage(BankRequestMessage bankRequestMessage) {
		try {
			return this.createBETXMLMessage(bankRequestMessage, true);
		} catch (Exception e) {
			LOG.fatal("FAILED to create ISO MSG for " + bankRequestMessage.getReference(), e);
			e.printStackTrace(System.err);
		}
		return "";
	}

	@Override
	public String createChargesMessage(CommissionMessage info) {
		
		if (info.getValueDate() == null) {
			info.setValueDate(new Date(System.currentTimeMillis()));
		}
		LOG.debug("The amount :: " + info.getAmount());
		String narrative = info.getBankRequest().getSourceMobileNumber() + "|" + info.getBankRequest().getReference();
		
		if (info.getAgentNumber() != null) {
			narrative += "|" + info.getAgentNumber();
		}
		LOG.debug("CHARGE NARRATIVE:  " + narrative);
		try {
			return this.createBETXMLCharge(info, false);
		}catch (Exception e) {
			LOG.fatal("FAILED to create ISO MSG for " + info.getReference(), e);
			e.printStackTrace(System.err);
		}
		return "";
		
	}

	@Override
	public String createChargesReversal(CommissionMessage info) {
		if (info.getValueDate() == null) {
			info.setValueDate(new Date(System.currentTimeMillis()));
		}
		String narrative = info.getBankRequest().getSourceMobileNumber() + "|" + info.getBankRequest().getReference();
		
		if (info.getAgentNumber() != null) {
			narrative += "|" + info.getAgentNumber();
		}
		
		info.setNarrative(narrative);
		try {
			return this.createBETXMLCharge(info, true);
		} catch (Exception e) {
			LOG.fatal("FAILED to create ISO MSG for " + info.getReference(), e);
			e.printStackTrace(System.err);
		}
		return "";
	}
}
