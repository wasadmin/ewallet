//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.bankservices.service.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "editBankAccount", namespace = "http://service.bankservices.ewallet.esolutions.co.zw/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "editBankAccount", namespace = "http://service.bankservices.ewallet.esolutions.co.zw/", propOrder = {
    "bankAccount",
    "userName"
})
public class EditBankAccount {

    @XmlElement(name = "bankAccount", namespace = "")
    private zw.co.esolutions.ewallet.bankservices.model.BankAccount bankAccount;
    @XmlElement(name = "userName", namespace = "")
    private String userName;

    /**
     * 
     * @return
     *     returns BankAccount
     */
    public zw.co.esolutions.ewallet.bankservices.model.BankAccount getBankAccount() {
        return this.bankAccount;
    }

    /**
     * 
     * @param bankAccount
     *     the value for the bankAccount property
     */
    public void setBankAccount(zw.co.esolutions.ewallet.bankservices.model.BankAccount bankAccount) {
        this.bankAccount = bankAccount;
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