package zw.co.esolutions.ewallet.security;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AesEncrypter {
	 static Cipher ecipher;
	    static Cipher dcipher;

	   static {
	        try {
	            ecipher = Cipher.getInstance("AES");
	            dcipher = Cipher.getInstance("AES");
	            ecipher.init(Cipher.ENCRYPT_MODE, getKey());
	            dcipher.init(Cipher.DECRYPT_MODE, getKey());
	        } catch (Exception e) {
	        
	        }
	    }

	    public static String encrypt(String str) {
	        try {
	            // Encode the string into bytes using utf-8
	            byte[] utf8 = str.getBytes("UTF8");

	            // Encrypt
	            byte[] enc = ecipher.doFinal(utf8);

	            // Encode bytes to base64 to get a string
	            return new sun.misc.BASE64Encoder().encode(enc);
	        } catch (javax.crypto.BadPaddingException e) {
	        	e.printStackTrace();
	        } catch (IllegalBlockSizeException e) {
	        	e.printStackTrace();
	        } catch (UnsupportedEncodingException e) {
	        	e.printStackTrace();
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	        return null;
	    }

	    public static String decrypt(String str) {
	        try {
	            // Decode base64 to get bytes
	            byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);

	            // Decrypt
	            byte[] utf8 = dcipher.doFinal(dec);

	            // Decode using utf-8
	            return new String(utf8, "UTF8");
	        } catch (javax.crypto.BadPaddingException e) {
	        } catch (IllegalBlockSizeException e) {
	        } catch (UnsupportedEncodingException e) {
	        } catch (java.io.IOException e) {
	        }
	        return null;
	    }
	    public static void main(String[] args) {
	    	try {				
				AesEncrypter encrypter = new AesEncrypter();
				String data = "Don't tell anybody!";
				String encrypted = encrypter.encrypt(data);
				System.out.println("ENCR : " + data + " -----------> " + encrypted);
				String decrypted = encrypter.decrypt(encrypted);
				System.out.println("DECR : " + encrypted + " -----------> " + decrypted);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	    public static SecretKey getKey()throws Exception{
	    	try {
	    		/*
	    		AESKeySpec keySpec = new AESKeySpec(EWalletConstants.TOPIT.getBytes("UTF8"));
				SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("AES");
				return keyFactory.generateSecret(keySpec);
				*/
	    		return new SecretKeySpec("legrandducvondebangs".getBytes("UTF8"), "AES");
			} catch (Exception e) {
				e.printStackTrace();			
				throw new Exception(e);
			}
	    }
}
