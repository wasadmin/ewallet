//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.process.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "getProcessTransactionsByAllAttributes", namespace = "http://process.ewallet.esolutions.co.zw/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getProcessTransactionsByAllAttributes", namespace = "http://process.ewallet.esolutions.co.zw/")
public class GetProcessTransactionsByAllAttributes {

    @XmlElement(name = "universalProcessPojo", namespace = "")
    private zw.co.esolutions.ewallet.process.pojo.UniversalProcessSearch universalProcessPojo;

    /**
     * 
     * @return
     *     returns UniversalProcessSearch
     */
    public zw.co.esolutions.ewallet.process.pojo.UniversalProcessSearch getUniversalProcessPojo() {
        return this.universalProcessPojo;
    }

    /**
     * 
     * @param universalProcessPojo
     *     the value for the universalProcessPojo property
     */
    public void setUniversalProcessPojo(zw.co.esolutions.ewallet.process.pojo.UniversalProcessSearch universalProcessPojo) {
        this.universalProcessPojo = universalProcessPojo;
    }

}
