//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.bankservices.service.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "createBank", namespace = "http://service.bankservices.ewallet.esolutions.co.zw/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "createBank", namespace = "http://service.bankservices.ewallet.esolutions.co.zw/", propOrder = {
    "bank",
    "userName"
})
public class CreateBank {

    @XmlElement(name = "bank", namespace = "")
    private zw.co.esolutions.ewallet.bankservices.model.Bank bank;
    @XmlElement(name = "userName", namespace = "")
    private String userName;

    /**
     * 
     * @return
     *     returns Bank
     */
    public zw.co.esolutions.ewallet.bankservices.model.Bank getBank() {
        return this.bank;
    }

    /**
     * 
     * @param bank
     *     the value for the bank property
     */
    public void setBank(zw.co.esolutions.ewallet.bankservices.model.Bank bank) {
        this.bank = bank;
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
