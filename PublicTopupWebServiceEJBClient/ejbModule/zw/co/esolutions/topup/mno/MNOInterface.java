/**
 * 
 */
package zw.co.esolutions.topup.mno;

import zw.co.esolutions.topup.ws.util.MNOName;
import zw.co.esolutions.topup.ws.util.WSRequest;
import zw.co.esolutions.topup.ws.util.WSResponse;

/**
 * @author blessing
 *
 */
public interface MNOInterface {
	MNOName getMNOName();
	WSResponse processRequest(WSRequest request);
	WSResponse processReversal(WSRequest request);	
}
