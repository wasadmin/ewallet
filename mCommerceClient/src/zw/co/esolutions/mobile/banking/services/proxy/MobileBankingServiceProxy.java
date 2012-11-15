package zw.co.esolutions.mobile.banking.services.proxy;

import zw.co.esolutions.mobile.banking.services.MobileBankingServiceSOAPProxy;

public class MobileBankingServiceProxy {
	
	private static MobileBankingServiceSOAPProxy proxy;
	
	public static final MobileBankingServiceSOAPProxy getInstance() {
		if(proxy == null) {
			proxy = new MobileBankingServiceSOAPProxy();
		}
		return proxy;
	}
}

