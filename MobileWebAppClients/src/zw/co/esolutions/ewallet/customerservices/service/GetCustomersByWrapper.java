//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.customerservices.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getCustomersByWrapper complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getCustomersByWrapper">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="customerWrapper" type="{http://service.customerservices.ewallet.esolutions.co.zw/}customerWrapper" minOccurs="0"/>
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
@XmlType(name = "getCustomersByWrapper", namespace = "http://service.customerservices.ewallet.esolutions.co.zw/", propOrder = {
    "customerWrapper",
    "userName"
})
public class GetCustomersByWrapper {

    protected CustomerWrapper customerWrapper;
    protected String userName;

    /**
     * Gets the value of the customerWrapper property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerWrapper }
     *     
     */
    public CustomerWrapper getCustomerWrapper() {
        return customerWrapper;
    }

    /**
     * Sets the value of the customerWrapper property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerWrapper }
     *     
     */
    public void setCustomerWrapper(CustomerWrapper value) {
        this.customerWrapper = value;
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
