//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.merchantservices.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for deleteCustomerMerchant complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="deleteCustomerMerchant">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="customerMerchant" type="{http://service.merchantservices.ewallet.esolutions.co.zw/}customerMerchant" minOccurs="0"/>
 *         &lt;element name="userName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "deleteCustomerMerchant", namespace = "http://service.merchantservices.ewallet.esolutions.co.zw/", propOrder = {
    "customerMerchant",
    "userName"
})
public class DeleteCustomerMerchant {

    protected CustomerMerchant customerMerchant;
    protected String userName;

    /**
     * Gets the value of the customerMerchant property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerMerchant }
     *     
     */
    public CustomerMerchant getCustomerMerchant() {
        return customerMerchant;
    }

    /**
     * Sets the value of the customerMerchant property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerMerchant }
     *     
     */
    public void setCustomerMerchant(CustomerMerchant value) {
        this.customerMerchant = value;
    }

    /**
     * Gets the value of the userName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the value of the userName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserName(String value) {
        this.userName = value;
    }

}