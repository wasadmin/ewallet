//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.process.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "getTransactionChargeByProcessTransactionId", namespace = "http://process.ewallet.esolutions.co.zw/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getTransactionChargeByProcessTransactionId", namespace = "http://process.ewallet.esolutions.co.zw/")
public class GetTransactionChargeByProcessTransactionId {

    @XmlElement(name = "processTxnId", namespace = "")
    private String processTxnId;

    /**
     * 
     * @return
     *     returns String
     */
    public String getProcessTxnId() {
        return this.processTxnId;
    }

    /**
     * 
     * @param processTxnId
     *     the value for the processTxnId property
     */
    public void setProcessTxnId(String processTxnId) {
        this.processTxnId = processTxnId;
    }

}
