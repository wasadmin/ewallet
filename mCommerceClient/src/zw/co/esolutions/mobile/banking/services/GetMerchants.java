//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.mobile.banking.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getMerchants complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getMerchants">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="merchantRequest" type="{http://services.banking.mobile.esolutions.co.zw/}merchantRequest" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getMerchants", namespace = "http://services.banking.mobile.esolutions.co.zw/", propOrder = {
    "merchantRequest"
})
public class GetMerchants {

    protected MerchantRequest merchantRequest;

    /**
     * Gets the value of the merchantRequest property.
     * 
     * @return
     *     possible object is
     *     {@link MerchantRequest }
     *     
     */
    public MerchantRequest getMerchantRequest() {
        return merchantRequest;
    }

    /**
     * Sets the value of the merchantRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link MerchantRequest }
     *     
     */
    public void setMerchantRequest(MerchantRequest value) {
        this.merchantRequest = value;
    }

}
