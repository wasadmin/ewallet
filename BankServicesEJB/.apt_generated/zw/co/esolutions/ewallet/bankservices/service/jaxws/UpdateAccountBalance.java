//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.bankservices.service.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "updateAccountBalance", namespace = "http://service.bankservices.ewallet.esolutions.co.zw/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "updateAccountBalance", namespace = "http://service.bankservices.ewallet.esolutions.co.zw/")
public class UpdateAccountBalance {

    @XmlElement(name = "accountBalance", namespace = "")
    private zw.co.esolutions.ewallet.bankservices.model.AccountBalance accountBalance;

    /**
     * 
     * @return
     *     returns AccountBalance
     */
    public zw.co.esolutions.ewallet.bankservices.model.AccountBalance getAccountBalance() {
        return this.accountBalance;
    }

    /**
     * 
     * @param accountBalance
     *     the value for the accountBalance property
     */
    public void setAccountBalance(zw.co.esolutions.ewallet.bankservices.model.AccountBalance accountBalance) {
        this.accountBalance = accountBalance;
    }

}
