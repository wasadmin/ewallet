//
// Generated By:JAX-WS RI 2.2.4-b01 (JAXB RI IBM 2.2.4)
//


package zw.co.esolutions.ussd.web.services;

import javax.xml.ws.WebFault;

@WebFault(name = "Exception", targetNamespace = "http://services.web.ussd.esolutions.co.zw/")
public class Exception_Exception
    extends java.lang.Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private zw.co.esolutions.ussd.web.services.Exception faultInfo;

    /**
     * 
     * @param faultInfo
     * @param message
     */
    public Exception_Exception(String message, zw.co.esolutions.ussd.web.services.Exception faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param faultInfo
     * @param message
     * @param cause
     */
    public Exception_Exception(String message, zw.co.esolutions.ussd.web.services.Exception faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: zw.co.esolutions.ussd.web.services.Exception
     */
    public zw.co.esolutions.ussd.web.services.Exception getFaultInfo() {
        return faultInfo;
    }

}
