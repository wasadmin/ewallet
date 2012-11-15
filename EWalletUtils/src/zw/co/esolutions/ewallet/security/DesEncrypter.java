package zw.co.esolutions.ewallet.security;

import java.io.UnsupportedEncodingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class DesEncrypter {
    static Cipher ecipher;
    static Cipher dcipher;

   public DesEncrypter() {
        
    }
   
   	public static void initialise() {
   		try {
   			SecretKey key = KeyGenerator.getInstance("DES").generateKey();
   		
            ecipher = Cipher.getInstance("DES");
            dcipher = Cipher.getInstance("DES");
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            dcipher.init(Cipher.DECRYPT_MODE, key);

        } catch (javax.crypto.NoSuchPaddingException e) {
        } catch (java.security.NoSuchAlgorithmException e) {
        } catch (java.security.InvalidKeyException e) {
        }
   	}

    public static String encrypt(String str) {
        try {
        	//initialise
        	if (ecipher == null) {
        		DesEncrypter.initialise();
        	}
        	
            // Encode the string into bytes using utf-8
            byte[] utf8 = str.getBytes("UTF8");

            // Encrypt
            byte[] enc = ecipher.doFinal(utf8);

            // Encode bytes to base64 to get a string
            return new sun.misc.BASE64Encoder().encode(enc);
        } catch (javax.crypto.BadPaddingException e) {
        } catch (IllegalBlockSizeException e) {
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }

    public static String decrypt(String str) {
        try {
        	//initialise
        	if (dcipher == null) {
        		DesEncrypter.initialise();
        	}
        	
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
			
			 String encrypted = DesEncrypter.encrypt("24492");
			 System.out.println(">>>Encr  :" + encrypted);
			 String decrypted = DesEncrypter.decrypt(encrypted);
			 System.out.println(">>>Decr  :" + decrypted);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}