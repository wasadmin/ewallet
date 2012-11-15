package zim;

import zw.co.esolutions.ewallet.sms.SmppServiceImpl;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
       try {
		new SmppServiceImpl().loadConfiguration();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}

}
