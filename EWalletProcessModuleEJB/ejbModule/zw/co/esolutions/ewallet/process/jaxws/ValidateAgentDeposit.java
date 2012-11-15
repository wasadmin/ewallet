//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.process.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "validateAgentDeposit", namespace = "http://process.ewallet.esolutions.co.zw/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "validateAgentDeposit", namespace = "http://process.ewallet.esolutions.co.zw/", propOrder = {
    "sourceMobileId",
    "amount",
    "bankId",
    "locationType"
})
public class ValidateAgentDeposit {

    @XmlElement(name = "sourceMobileId", namespace = "")
    private String sourceMobileId;
    @XmlElement(name = "amount", namespace = "")
    private long amount;
    @XmlElement(name = "bankId", namespace = "")
    private String bankId;
    @XmlElement(name = "locationType", namespace = "")
    private zw.co.esolutions.ewallet.enums.TransactionLocationType locationType;

    /**
     * 
     * @return
     *     returns String
     */
    public String getSourceMobileId() {
        return this.sourceMobileId;
    }

    /**
     * 
     * @param sourceMobileId
     *     the value for the sourceMobileId property
     */
    public void setSourceMobileId(String sourceMobileId) {
        this.sourceMobileId = sourceMobileId;
    }

    /**
     * 
     * @return
     *     returns long
     */
    public long getAmount() {
        return this.amount;
    }

    /**
     * 
     * @param amount
     *     the value for the amount property
     */
    public void setAmount(long amount) {
        this.amount = amount;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getBankId() {
        return this.bankId;
    }

    /**
     * 
     * @param bankId
     *     the value for the bankId property
     */
    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    /**
     * 
     * @return
     *     returns TransactionLocationType
     */
    public zw.co.esolutions.ewallet.enums.TransactionLocationType getLocationType() {
        return this.locationType;
    }

    /**
     * 
     * @param locationType
     *     the value for the locationType property
     */
    public void setLocationType(zw.co.esolutions.ewallet.enums.TransactionLocationType locationType) {
        this.locationType = locationType;
    }

}
