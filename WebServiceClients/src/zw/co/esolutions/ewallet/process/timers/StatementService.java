//
// Generated By:JAX-WS RI IBM 2.1.6 in JDK 6 (JAXB RI IBM JAXB 2.1.10 in JDK 6)
//


package zw.co.esolutions.ewallet.process.timers;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

@WebService(name = "StatementService", targetNamespace = "http://timers.process.ewallet.esolutions.co.zw/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface StatementService {


    /**
     * 
     * @param description
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "checkTimer", targetNamespace = "http://timers.process.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.process.timers.CheckTimer")
    @ResponseWrapper(localName = "checkTimerResponse", targetNamespace = "http://timers.process.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.process.timers.CheckTimerResponse")
    public boolean checkTimer(
        @WebParam(name = "description", targetNamespace = "")
        String description);

}
