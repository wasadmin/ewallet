//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.bankservices.service.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "getAccountBalanceByAccount", namespace = "http://service.bankservices.ewallet.esolutions.co.zw/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAccountBalanceByAccount", namespace = "http://service.bankservices.ewallet.esolutions.co.zw/")
public class GetAccountBalanceByAccount {

    @XmlElement(name = "accountId", namespace = "")
    private String accountId;

    /**
     * 
     * @return
     *     returns String
     */
    public String getAccountId() {
        return this.accountId;
    }

    /**
     * 
     * @param accountId
     *     the value for the accountId property
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

}
