package zw.co.esolutions.topup.ws;
import javax.ejb.Local;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import zw.co.esolutions.topup.ws.util.ReversalRequest;
import zw.co.esolutions.topup.ws.util.ReversalResponse;
import zw.co.esolutions.topup.ws.util.WSRequest;
import zw.co.esolutions.topup.ws.util.WSResponse;


@Local
@WebService(name = "TopupWebService", targetNamespace = "http://impl.ws.topup.esolutions.co.zw/")
public interface TopupWebService {

	@WebMethod(operationName = "processRequest", action = "ws:processRequest")
	public WSResponse processRequest(@WebParam(name = "request") WSRequest topupRequest);
	
	@WebMethod(operationName = "processReversal", action = "ws:processReversal")
	public ReversalResponse processReversal(@WebParam(name = "reversalRequest") ReversalRequest topupRequest);

}
