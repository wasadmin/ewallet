//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.bankservices.service.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "createTransaction", namespace = "http://service.bankservices.ewallet.esolutions.co.zw/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "createTransaction", namespace = "http://service.bankservices.ewallet.esolutions.co.zw/")
public class CreateTransaction {

    @XmlElement(name = "transaction", namespace = "")
    private zw.co.esolutions.ewallet.bankservices.model.Transaction transaction;

    /**
     * 
     * @return
     *     returns Transaction
     */
    public zw.co.esolutions.ewallet.bankservices.model.Transaction getTransaction() {
        return this.transaction;
    }

    /**
     * 
     * @param transaction
     *     the value for the transaction property
     */
    public void setTransaction(zw.co.esolutions.ewallet.bankservices.model.Transaction transaction) {
        this.transaction = transaction;
    }

}
