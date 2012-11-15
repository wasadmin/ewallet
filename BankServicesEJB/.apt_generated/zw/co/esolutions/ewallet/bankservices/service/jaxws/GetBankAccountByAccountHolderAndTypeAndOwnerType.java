//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.bankservices.service.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "getBankAccountByAccountHolderAndTypeAndOwnerType", namespace = "http://service.bankservices.ewallet.esolutions.co.zw/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getBankAccountByAccountHolderAndTypeAndOwnerType", namespace = "http://service.bankservices.ewallet.esolutions.co.zw/", propOrder = {
    "accountHolderId",
    "type",
    "ownerType",
    "accountNumber"
})
public class GetBankAccountByAccountHolderAndTypeAndOwnerType {

    @XmlElement(name = "accountHolderId", namespace = "")
    private String accountHolderId;
    @XmlElement(name = "type", namespace = "")
    private zw.co.esolutions.ewallet.enums.BankAccountType type;
    @XmlElement(name = "ownerType", namespace = "")
    private zw.co.esolutions.ewallet.enums.OwnerType ownerType;
    @XmlElement(name = "accountNumber", namespace = "")
    private String accountNumber;

    /**
     * 
     * @return
     *     returns String
     */
    public String getAccountHolderId() {
        return this.accountHolderId;
    }

    /**
     * 
     * @param accountHolderId
     *     the value for the accountHolderId property
     */
    public void setAccountHolderId(String accountHolderId) {
        this.accountHolderId = accountHolderId;
    }

    /**
     * 
     * @return
     *     returns BankAccountType
     */
    public zw.co.esolutions.ewallet.enums.BankAccountType getType() {
        return this.type;
    }

    /**
     * 
     * @param type
     *     the value for the type property
     */
    public void setType(zw.co.esolutions.ewallet.enums.BankAccountType type) {
        this.type = type;
    }

    /**
     * 
     * @return
     *     returns OwnerType
     */
    public zw.co.esolutions.ewallet.enums.OwnerType getOwnerType() {
        return this.ownerType;
    }

    /**
     * 
     * @param ownerType
     *     the value for the ownerType property
     */
    public void setOwnerType(zw.co.esolutions.ewallet.enums.OwnerType ownerType) {
        this.ownerType = ownerType;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getAccountNumber() {
        return this.accountNumber;
    }

    /**
     * 
     * @param accountNumber
     *     the value for the accountNumber property
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

}
