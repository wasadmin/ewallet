package zw.co.esolutions.ussd.utils;


import java.util.Random;


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
	
   
}
