//
// Generated By:JAX-WS RI IBM 2.1.6 in JDK 6 (JAXB RI IBM JAXB 2.1.10 in JDK 6)
//


package zw.co.esolutions.ewallet.merchantservices.service;

import javax.xml.ws.WebFault;

@WebFault(name = "Exception", targetNamespace = "http://service.merchantservices.ewallet.esolutions.co.zw/")
public class Exception_Exception
    extends java.lang.Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private zw.co.esolutions.ewallet.merchantservices.service.Exception faultInfo;

    /**
     * 
     * @param faultInfo
     * @param message
     */
    public Exception_Exception(String message, zw.co.esolutions.ewallet.merchantservices.service.Exception faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param faultInfo
     * @param message
     * @param cause
     */
    public Exception_Exception(String message, zw.co.esolutions.ewallet.merchantservices.service.Exception faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: zw.co.esolutions.ewallet.merchantservices.service.Exception
     */
    public zw.co.esolutions.ewallet.merchantservices.service.Exception getFaultInfo() {
        return faultInfo;
    }

}
