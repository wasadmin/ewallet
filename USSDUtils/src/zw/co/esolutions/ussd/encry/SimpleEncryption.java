/**
 * 
 */
package zw.co.esolutions.ussd.encry;

/**
 * @author taurai
 *
 */
public class SimpleEncryption {

	private static final int number = 7;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String source = "Tauttee 33333";
		source = encryt(source);
		System.out.println(">>>>>>>>>>>>> Encrypted = "+source);
		source = decrypt(source);
		System.out.println(">>>>>>>>>>>>> Decrypted = "+source);

	}
	
	public static String encryt(String plain) {
		StringBuffer encrypted = new StringBuffer();
		if(plain != null) {
			for(char ch : plain.toCharArray()) {
				ch = (char)(ch + number);
				encrypted.append(ch);
			}
		}
		return encrypted.toString();
	}
	
	public static String decrypt(String encrypted) {
		StringBuffer decrypted = new StringBuffer();
		if(encrypted != null) {
			for(char ch : encrypted.toCharArray()) {
				decrypted.append((char)(ch-number));
			}
		}
		return decrypted.toString();
	}
	

}
