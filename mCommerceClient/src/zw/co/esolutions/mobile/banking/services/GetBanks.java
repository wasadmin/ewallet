//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.mobile.banking.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getBanks complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getBanks">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="bankRequest" type="{http://services.banking.mobile.esolutions.co.zw/}bankRequest" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getBanks", namespace = "http://services.banking.mobile.esolutions.co.zw/", propOrder = {
    "bankRequest"
})
public class GetBanks {

    protected BankRequest bankRequest;

    /**
     * Gets the value of the bankRequest property.
     * 
     * @return
     *     possible object is
     *     {@link BankRequest }
     *     
     */
    public BankRequest getBankRequest() {
        return bankRequest;
    }

    /**
     * Sets the value of the bankRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link BankRequest }
     *     
     */
    public void setBankRequest(BankRequest value) {
        this.bankRequest = value;
    }

}