//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.bankservices.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for deleteAccountBalance complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="deleteAccountBalance">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="accountBalanceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "deleteAccountBalance", namespace = "http://service.bankservices.ewallet.esolutions.co.zw/", propOrder = {
    "accountBalanceId"
})
public class DeleteAccountBalance {

    protected String accountBalanceId;

    /**
     * Gets the value of the accountBalanceId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountBalanceId() {
        return accountBalanceId;
    }

    /**
     * Sets the value of the accountBalanceId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountBalanceId(String value) {
        this.accountBalanceId = value;
    }

}
