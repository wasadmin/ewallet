//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.process.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "createProcessTransaction", namespace = "http://process.ewallet.esolutions.co.zw/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "createProcessTransaction", namespace = "http://process.ewallet.esolutions.co.zw/", propOrder = {
    "processTxn",
    "userName"
})
public class CreateProcessTransaction {

    @XmlElement(name = "processTxn", namespace = "")
    private zw.co.esolutions.ewallet.process.model.ProcessTransaction processTxn;
    @XmlElement(name = "userName", namespace = "")
    private String userName;

    /**
     * 
     * @return
     *     returns ProcessTransaction
     */
    public zw.co.esolutions.ewallet.process.model.ProcessTransaction getProcessTxn() {
        return this.processTxn;
    }

    /**
     * 
     * @param processTxn
     *     the value for the processTxn property
     */
    public void setProcessTxn(zw.co.esolutions.ewallet.process.model.ProcessTransaction processTxn) {
        this.processTxn = processTxn;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * 
     * @param userName
     *     the value for the userName property
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

}
