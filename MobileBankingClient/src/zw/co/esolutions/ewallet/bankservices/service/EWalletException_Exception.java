//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.bankservices.service;

import javax.xml.ws.WebFault;

@WebFault(name = "EWalletException", targetNamespace = "http://service.bankservices.ewallet.esolutions.co.zw/")
public class EWalletException_Exception
    extends java.lang.Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private EWalletException faultInfo;

    /**
     * 
     * @param faultInfo
     * @param message
     */
    public EWalletException_Exception(String message, EWalletException faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param faultInfo
     * @param message
     * @param cause
     */
    public EWalletException_Exception(String message, EWalletException faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: zw.co.esolutions.ewallet.bankservices.service.EWalletException
     */
    public EWalletException getFaultInfo() {
        return faultInfo;
    }

}