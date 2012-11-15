/**
 * 
 */
package zw.co.esolutions.topup.mno;

import zw.co.esolutions.topup.ws.util.MNOName;

/**
 * @author blessing
 *
 */
public class MNOInterfaceFactory {

	public static MNOInterface getMNOInstance(MNOName mnoName) throws Exception {
		//its been ages before I saw a switch in action
		switch(mnoName){
			case ECONET :
				return EconetMNOPlatform.getMnoPlatform();
			case TELECEL :
				return EconetMNOPlatform.getMnoPlatform();
			case NETONE :
				return EconetMNOPlatform.getMnoPlatform();
			default :	
				throw new Exception("Request cannot be processed. Unknown MNO : " + mnoName);
		}
	}

}
