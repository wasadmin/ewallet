//
// Generated By:JAX-WS RI 2.2.4-b01 (JAXB RI IBM 2.2.4)
//


package zw.co.esolutions.ussd.web.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for createTransaction complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="createTransaction">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ussdTransaction" type="{http://services.web.ussd.esolutions.co.zw/}ussdTransaction" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "createTransaction", propOrder = {
    "ussdTransaction"
})
public class CreateTransaction {

    protected UssdTransaction ussdTransaction;

    /**
     * Gets the value of the ussdTransaction property.
     * 
     * @return
     *     possible object is
     *     {@link UssdTransaction }
     *     
     */
    public UssdTransaction getUssdTransaction() {
        return ussdTransaction;
    }

    /**
     * Sets the value of the ussdTransaction property.
     * 
     * @param value
     *     allowed object is
     *     {@link UssdTransaction }
     *     
     */
    public void setUssdTransaction(UssdTransaction value) {
        this.ussdTransaction = value;
    }

}
