//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.bankservices.service.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "getBankAccountsByAccountHolderIdAndOwnerTypeAndStatus", namespace = "http://service.bankservices.ewallet.esolutions.co.zw/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getBankAccountsByAccountHolderIdAndOwnerTypeAndStatus", namespace = "http://service.bankservices.ewallet.esolutions.co.zw/", propOrder = {
    "accountHolderId",
    "ownerType",
    "status"
})
public class GetBankAccountsByAccountHolderIdAndOwnerTypeAndStatus {

    @XmlElement(name = "accountHolderId", namespace = "")
    private String accountHolderId;
    @XmlElement(name = "ownerType", namespace = "")
    private zw.co.esolutions.ewallet.enums.OwnerType ownerType;
    @XmlElement(name = "status", namespace = "")
    private zw.co.esolutions.ewallet.enums.BankAccountStatus status;

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
     *     returns BankAccountStatus
     */
    public zw.co.esolutions.ewallet.enums.BankAccountStatus getStatus() {
        return this.status;
    }

    /**
     * 
     * @param status
     *     the value for the status property
     */
    public void setStatus(zw.co.esolutions.ewallet.enums.BankAccountStatus status) {
        this.status = status;
    }

}
