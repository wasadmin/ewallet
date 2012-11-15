//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.zimswitch.airtimeportal;

import java.math.BigDecimal;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

@WebService(name = "AirtimePortalSoap", targetNamespace = "http://www.zimswitch.co.zw/AirtimePortal")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface AirtimePortalSoap {


    /**
     * 
     * @param msdn
     * @param provider
     * @param username
     * @param amount
     * @param reference
     * @param password
     * @return
     *     returns zw.co.zimswitch.airtimeportal.AirtimeResponse
     */
    @WebMethod(operationName = "DirectTopup", action = "http://www.zimswitch.co.zw/AirtimePortal/DirectTopup")
    @WebResult(name = "DirectTopupResult", targetNamespace = "http://www.zimswitch.co.zw/AirtimePortal")
    @RequestWrapper(localName = "DirectTopup", targetNamespace = "http://www.zimswitch.co.zw/AirtimePortal", className = "zw.co.zimswitch.airtimeportal.DirectTopup")
    @ResponseWrapper(localName = "DirectTopupResponse", targetNamespace = "http://www.zimswitch.co.zw/AirtimePortal", className = "zw.co.zimswitch.airtimeportal.DirectTopupResponse")
    public AirtimeResponse directTopup(
        @WebParam(name = "msdn", targetNamespace = "http://www.zimswitch.co.zw/AirtimePortal")
        String msdn,
        @WebParam(name = "provider", targetNamespace = "http://www.zimswitch.co.zw/AirtimePortal")
        String provider,
        @WebParam(name = "amount", targetNamespace = "http://www.zimswitch.co.zw/AirtimePortal")
        BigDecimal amount,
        @WebParam(name = "username", targetNamespace = "http://www.zimswitch.co.zw/AirtimePortal")
        String username,
        @WebParam(name = "password", targetNamespace = "http://www.zimswitch.co.zw/AirtimePortal")
        String password,
        @WebParam(name = "reference", targetNamespace = "http://www.zimswitch.co.zw/AirtimePortal")
        String reference);

}
