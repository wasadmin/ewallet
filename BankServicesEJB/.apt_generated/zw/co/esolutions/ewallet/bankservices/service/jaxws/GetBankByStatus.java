//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.bankservices.service.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "getBankByStatus", namespace = "http://service.bankservices.ewallet.esolutions.co.zw/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getBankByStatus", namespace = "http://service.bankservices.ewallet.esolutions.co.zw/")
public class GetBankByStatus {

    @XmlElement(name = "status", namespace = "")
    private zw.co.esolutions.ewallet.enums.BankStatus status;

    /**
     * 
     * @return
     *     returns BankStatus
     */
    public zw.co.esolutions.ewallet.enums.BankStatus getStatus() {
        return this.status;
    }

    /**
     * 
     * @param status
     *     the value for the status property
     */
    public void setStatus(zw.co.esolutions.ewallet.enums.BankStatus status) {
        this.status = status;
    }

}