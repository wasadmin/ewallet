package zw.co.esolutions.ewallet.services.proxy;

import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;

public class BankServiceProxy {

	private static BankServiceSOAPProxy proxy;
	
	public static final BankServiceSOAPProxy getInstance() {
		if(proxy == null) {
			proxy = new BankServiceSOAPProxy();
		}
		
		return proxy;
	}
}
