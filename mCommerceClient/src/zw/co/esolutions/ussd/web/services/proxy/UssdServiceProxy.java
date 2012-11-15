package zw.co.esolutions.ussd.web.services.proxy;

import zw.co.esolutions.ussd.web.services.MobileCommerceServiceSOAPProxy;

public class UssdServiceProxy {
	
	private static MobileCommerceServiceSOAPProxy proxy;
	
	public static final MobileCommerceServiceSOAPProxy getInstance() {
		if(proxy == null) {
			proxy = new MobileCommerceServiceSOAPProxy();
		}
		return proxy;
	}
}

