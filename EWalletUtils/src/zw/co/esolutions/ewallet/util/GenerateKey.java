package zw.co.esolutions.ewallet.util;


import java.util.Random;

import zw.co.esolutions.ewallet.msg.BankResponse;
import zw.co.esolutions.ewallet.sms.ResponseCode;


public class GenerateKey {
	
	private static final char[] symbols = new char[36];

	  static {
	    for (int idx = 0; idx < 10; ++idx)
	      symbols[idx] = (char) ('0' + idx);
	    for (int idx = 10; idx < 36; ++idx)
	      symbols[idx] = (char) ('a' + idx - 10);
	  }

	 private static final Random random = new Random();
	
	 private static String nextString(int length){
		final char[] buf = new char[length];
	    for (int idx = 0; idx < buf.length; ++idx) 
	      buf[idx] = symbols[random.nextInt(symbols.length)];
	    return new String(buf);
	  }
	  
	public static String generateEntityId() {
		Long l = System.currentTimeMillis();
		Integer randomNumber = (int)(Math.random() * 1000);
		String entityId = nextString(4)+l.toString() + randomNumber;
		
		return entityId.toUpperCase();
	}
	
	public static String generateSecurityCode() {
		Long randomNumber = 0L;
		while (randomNumber < 10000) {
			randomNumber = (long)(Math.random() * 100000);
		}
		return randomNumber.toString();
	}
	
	public static void main(String[] args) {
		/*
		MessageDocument mdoc = MessageDocument.Factory.newInstance();
		Message msg = mdoc.addNewMessage();
		MobileOriginatedShortMessage mosm = msg.addNewMobileOriginatedShortMessage();
		
		mosm.setDateReceived(Calendar.getInstance());
		mosm.setDestinationId("440");
		mosm.setMobileNetworkOperator(MobileNetworkOperator.ECONET);
		mosm.setSubscriberId("+263774333363");
		mosm.setShortMessage("6*bal");
		System.out.println(mdoc.toString());
		*/
		for(int i=0;i<10;i++)
			  System.out.println(generateEntityId());
	}
	
   public static void throwsException() throws Exception {
		//throw new Exception(" EWallet Constants >>>>>>>>>>>>>>>>>>>>>>>>>>>>> It Throws from Utils.");
   }
   public static void throwsDayEndEWalletPostingsException() throws Exception {
		//throw new Exception(" EWallet Constants >>>>>>>>>>>>>>>>>>>>>>>>>>>>> It Throws from Utils. EWallet Postings. ");
	}
   
   public static void throwsEWalletPostingsException() throws Exception {
		//throw new Exception(" EWallet Constants >>>>>>>>>>>>>>>>>>>>>>>>>>>>> It Throws from Utils. EWallet Postings. ");
	}
   
   
   public static void throwsDirectEwalletPostingsException() throws Exception {
		//throw new Exception(" EWallet Constants >>>>>>>>>>>>>>>>>>>>>>>>>>>>> It Throws from Utils. EWallet Postings. ");
	}
   
   
   public static void throwsDirectEQ3PostingsException() throws Exception {
		//throw new Exception(" EWallet Constants >>>>>>>>>>>>>>>>>>>>>>>>>>>>> It Throws from Utils. EWallet Postings. ");
	}
  
   
   
   public static BankResponse failResponse(BankResponse bankResp) {
	 /*if(bankResp != null) {
		  bankResp.setResponseCode(ResponseCode.E831);
		  bankResp.setNarrative(ResponseCode.E831.getDescription());
	  }*/
	   return bankResp;
   }
   
   public static BankResponse successResponse(BankResponse bankResp) {
		 /* if(bankResp != null) {
			  bankResp.setResponseCode(ResponseCode.E000);
			  bankResp.setNarrative(ResponseCode.E000.getDescription());
		  }*/
		   return bankResp;
   }
   
   public static void throwsBankResponseException() throws Exception {
		//throw new Exception(" EWallet Constants >>>>>>>>>>>>>>>>>>>>>>>>>>>>> It Throws from Utils. Bank response exception thrown. ");
	}
}
