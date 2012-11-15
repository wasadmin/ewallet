package zw.co.esolutions.ewallet.security;

public class Test {
	public static void main(String[] args) {
		AesEncrypter aes = new AesEncrypter();
		String temp = "Russel is the best programmer at esolutions";
		System.out.println("......."+aes.encrypt(temp));
		//System.out.println("........");
	}
}
