package zw.co.esolutions.ewallet.services.proxy;

import zw.co.esolutions.ewallet.merchantservices.service.MerchantServiceSOAPProxy;

public class MerchantServiceProxy {

	private static MerchantServiceSOAPProxy proxy;
	
	public static final MerchantServiceSOAPProxy getInstance() {
		if(proxy == null) {
			proxy = new MerchantServiceSOAPProxy();
		}
		
		return proxy;
	}
}
