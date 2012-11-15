//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.topup.ws.impl;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

@WebService(name = "TopupWS", targetNamespace = "http://impl.ws.topup.esolutions.co.zw/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface TopupWS {


    /**
     * 
     * @param request
     * @return
     *     returns zw.co.esolutions.topup.ws.impl.Response
     */
    @WebMethod(action = "ws:processRequest")
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "processRequest", targetNamespace = "http://impl.ws.topup.esolutions.co.zw/", className = "zw.co.esolutions.topup.ws.impl.ProcessRequest")
    @ResponseWrapper(localName = "processRequestResponse", targetNamespace = "http://impl.ws.topup.esolutions.co.zw/", className = "zw.co.esolutions.topup.ws.impl.ProcessRequestResponse")
    public Response processRequest(
        @WebParam(name = "request", targetNamespace = "")
        Request request);

    /**
     * 
     * @param reversalRequest
     * @return
     *     returns zw.co.esolutions.topup.ws.impl.ReversalResponse
     */
    @WebMethod(action = "ws:processReversal")
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "processReversal", targetNamespace = "http://impl.ws.topup.esolutions.co.zw/", className = "zw.co.esolutions.topup.ws.impl.ProcessReversal")
    @ResponseWrapper(localName = "processReversalResponse", targetNamespace = "http://impl.ws.topup.esolutions.co.zw/", className = "zw.co.esolutions.topup.ws.impl.ProcessReversalResponse")
    public ReversalResponse processReversal(
        @WebParam(name = "reversalRequest", targetNamespace = "")
        ReversalRequest reversalRequest);

}
