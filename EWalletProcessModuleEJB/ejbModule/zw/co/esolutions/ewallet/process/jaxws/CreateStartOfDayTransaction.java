//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.process.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "createStartOfDayTransaction", namespace = "http://process.ewallet.esolutions.co.zw/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "createStartOfDayTransaction", namespace = "http://process.ewallet.esolutions.co.zw/", propOrder = {
    "processTransaction",
    "username"
})
public class CreateStartOfDayTransaction {

    @XmlElement(name = "processTransaction", namespace = "")
    private zw.co.esolutions.ewallet.process.model.ProcessTransaction processTransaction;
    @XmlElement(name = "username", namespace = "")
    private String username;

    /**
     * 
     * @return
     *     returns ProcessTransaction
     */
    public zw.co.esolutions.ewallet.process.model.ProcessTransaction getProcessTransaction() {
        return this.processTransaction;
    }

    /**
     * 
     * @param processTransaction
     *     the value for the processTransaction property
     */
    public void setProcessTransaction(zw.co.esolutions.ewallet.process.model.ProcessTransaction processTransaction) {
        this.processTransaction = processTransaction;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * 
     * @param username
     *     the value for the username property
     */
    public void setUsername(String username) {
        this.username = username;
    }

}
