//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.process.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "processEwalletWithdrawal", namespace = "http://process.ewallet.esolutions.co.zw/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "processEwalletWithdrawal", namespace = "http://process.ewallet.esolutions.co.zw/")
public class ProcessEwalletWithdrawal {

    @XmlElement(name = "requestInfo", namespace = "")
    private zw.co.esolutions.ewallet.msg.RequestInfo requestInfo;

    /**
     * 
     * @return
     *     returns RequestInfo
     */
    public zw.co.esolutions.ewallet.msg.RequestInfo getRequestInfo() {
        return this.requestInfo;
    }

    /**
     * 
     * @param requestInfo
     *     the value for the requestInfo property
     */
    public void setRequestInfo(zw.co.esolutions.ewallet.msg.RequestInfo requestInfo) {
        this.requestInfo = requestInfo;
    }

}
