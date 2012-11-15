package zw.co.esolutions.merchants.util;

import java.util.Date;

import zw.co.esolutions.mcommerce.xml.ISOMarshaller;
import zw.co.esolutions.mcommerce.xml.ISOMarshallerException;
import zw.co.esolutions.mcommerce.xml.ISOMsg;
import zw.co.esolutions.mcommerce.xml.Messages;
import zw.co.esolutions.mcommerce.xml.MetaData;

public class Main {

	/**
	 * @param args
	 */
//	public static void main(String[] args) {
//		Messages msgs = new Messages();
//		
//
//		
//		//SET META DATA INFO
//        MetaData meta = new MetaData();
//        meta.setSource("EWALLET");
//        meta.setPostingBranch("TXN Location ID");
//        meta.setReplyQueue("MERCHANT.REPLY.QUEUE");
//        meta.setRequestQueue("MERCHANT.RQST.QUEUE");
//		
//      //Header INFO - REF and MSG TYPE [0200, 0420]
//		ISOMsg isoMsg = new ISOMsg();
//        isoMsg.setRetrievalReference("E12000453003");
//        isoMsg.setMti("0200");
//        
//		//PAN
//		isoMsg.setISOField(InterfaceConstants.ISO_FIELD_PRIMARY_ACCOUNT_NUMBER, "");
//		
//		//PCODE
//		isoMsg.setISOField(InterfaceConstants.ISO_FIELD_PROCESSING_CODE, "300000");
//		
//		//FIELD 4 is TXN AMOUNT in cents
//		isoMsg.setISOField(InterfaceConstants.ISO_FIELD_TRANSACTION_AMOUNT, "2000");
//		
//        //FIELD 7 is DATE in the format MMddHHmmss, 10 charactors
//		isoMsg.setISOField(InterfaceConstants.ISO_FIELD_TRANSMISSION_DATE, Formats.equationDateFormat.format(new Date(System.currentTimeMillis())));
//				
//        //FIELD 11 is STAN in the format
//        isoMsg.setISOField(11, Formats.intFormat.format(1));
//		
//        //FIELD 18 is MERCHANT TYPE
//        isoMsg.setISOField(18, "ZESA");
//		
//        //FIELD 32 is Acquirer ID
//        isoMsg.setISOField(32, "e-Solutions");
//                
//        //FIELD 38 is authorisation identification response
//        isoMsg.setISOField(38, "");
//        
//        //FIELD 41 is Card Acceptor Terminal
//        isoMsg.setISOField(41, "Bank X");
//                
//        //FIELD 46 is Additional Data 
//        isoMsg.setISOField(46, "");
//        
//        //FIELD 49 is Currency
//        isoMsg.setISOField(49, "USD");
//        
//        //FIELD 54 is Additional Amounts
//        isoMsg.setISOField(54, Formats.intFormat.format(0));
//		
//        //FIELD 102 is source account 
//        isoMsg.setISOField(102, "VVVVV");
//        
//        //FIELD 103 is source account 
//        isoMsg.setISOField(103, "");
//        
//        //FIELD 104 is Narratives 
//        isoMsg.setISOField(InterfaceConstants.ISO_FIELD_TRANSACTION_DESCRIPTION, "Narrative");
//        
//        try {
//			System.out.print(ISOMarshaller.marshal(isoMsg, meta));
//		} catch (ISOMarshallerException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	
//
//	}

	public static void main(String[] args) {
		
	}
}
