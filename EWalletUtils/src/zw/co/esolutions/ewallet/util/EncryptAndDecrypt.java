package zw.co.esolutions.ewallet.util;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class EncryptAndDecrypt {
	
	 private static final String ALGORITHM = "AES";
	 private static final int ITERATIONS = 1;
	 private static final byte[] keyValue = 
	        new byte[] { 'Z', 'b', 'U', 's', 'e', 'r','*', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y'};
	 public static void main(String[] args) {
		 try {
			String value = "ptmzGOfcJe/H+b6OR2NCaOkLn/EWtGXLPC0HLlXKeMU=";
			String salt = "263773303584";
		//	System.out.println(value = encrypt(value, salt));
			
			System.out.println(decrypt(value, salt));
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }
	 public static String encrypt(String value, String salt) throws Exception {
		 	String mobileNumber = NumberUtil.formatMobileNumber(salt);
	        Key key = generateKey();
	        Cipher c = Cipher.getInstance(ALGORITHM);  
	        c.init(Cipher.ENCRYPT_MODE, key);
	  
	        String valueToEnc = null;
	        String eValue = value;
	        for (int i = 0; i < ITERATIONS; i++) {
	            valueToEnc = mobileNumber + eValue;
	            byte[] encValue = c.doFinal(valueToEnc.getBytes());
	            eValue = new BASE64Encoder().encode(encValue);
	        }
	        return eValue;
	 }
	 
	 public static String decrypt(String value, String salt) throws Exception {
		 	String mobileNumber = NumberUtil.formatMobileNumber(salt);
	        Key key = generateKey();
	        Cipher c = Cipher.getInstance(ALGORITHM);
	        c.init(Cipher.DECRYPT_MODE, key);
	  
	        String dValue = null;
	        String valueToDecrypt = value;
	        for (int i = 0; i < ITERATIONS; i++) {
	            byte[] decordedValue = new BASE64Decoder().decodeBuffer(valueToDecrypt);
	            byte[] decValue = c.doFinal(decordedValue);
	            dValue = new String(decValue).substring(mobileNumber.length());
	            valueToDecrypt = dValue;
	        }
	        return dValue;
	 }

	 private static Key generateKey() throws Exception {
		 Key key = new SecretKeySpec(keyValue, ALGORITHM);
	     // SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
	     // key = keyFactory.generateSecret(new DESKeySpec(keyValue));
		 return key;
	 }
	 
//	 public static void main(String[] args) throws Exception {
//	    	
//	    	
//	        String password = "12345";
//	        String passwordEnc = EncryptAndDecrypt.encrypt(password,"263773407374");
//	        System.out.println("Plain Text : " + password);
//	        System.out.println("Encrypted : " + passwordEnc);
//	        
//	        
//	    	
//	    String passwordDec = EncryptAndDecrypt.decrypt(passwordEnc,"0773407374");
//	    System.out.println("Decrypted : " + passwordDec);   
//	 }
}
