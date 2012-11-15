package zw.co.esolutions.ewallet.services.proxy;

import zw.co.datacentre.dstv.ws.DSTVServiceSOAPProxy;

public class DstvServiceProxy {

private static DSTVServiceSOAPProxy proxy;
	
	public static final DSTVServiceSOAPProxy getInstance() {
		if(proxy == null) {
			proxy = new DSTVServiceSOAPProxy();
		}
		
		return proxy;
	}
}
