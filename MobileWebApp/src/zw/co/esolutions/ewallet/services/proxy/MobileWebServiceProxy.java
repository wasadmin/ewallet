package zw.co.esolutions.ewallet.services.proxy;

import zw.co.esolutions.ussd.web.services.MobileCommerceServiceSOAPProxy;

public class MobileWebServiceProxy {

	private static MobileCommerceServiceSOAPProxy proxy;
	
	public static final MobileCommerceServiceSOAPProxy getInstance() {
		if(proxy == null) {
			proxy = new MobileCommerceServiceSOAPProxy();
		}
		
		return proxy;
	}
}
