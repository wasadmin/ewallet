package zw.co.esolutions.ewallet.customerservices.proxy;

import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;

public class CustomerServiceProxy {

	private static CustomerServiceSOAPProxy proxy;
	
	public static final CustomerServiceSOAPProxy getInstance() {
		if(proxy == null) {
			proxy = new CustomerServiceSOAPProxy();
		}
		
		return proxy;
	}
}
