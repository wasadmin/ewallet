//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.process.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "getProcessTransactionsByTellerID", namespace = "http://process.ewallet.esolutions.co.zw/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getProcessTransactionsByTellerID", namespace = "http://process.ewallet.esolutions.co.zw/")
public class GetProcessTransactionsByTellerID {

    @XmlElement(name = "tellerId", namespace = "")
    private String tellerId;

    /**
     * 
     * @return
     *     returns String
     */
    public String getTellerId() {
        return this.tellerId;
    }

    /**
     * 
     * @param tellerId
     *     the value for the tellerId property
     */
    public void setTellerId(String tellerId) {
        this.tellerId = tellerId;
    }

}
