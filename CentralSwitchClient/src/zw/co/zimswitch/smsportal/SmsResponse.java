//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.zimswitch.smsportal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SmsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SmsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Successful" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="ResponseDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SmsStatus" type="{http://www.zimswitch.co.zw/SmsPortal}SmsStatus"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SmsResponse", namespace = "http://www.zimswitch.co.zw/SmsPortal", propOrder = {
    "successful",
    "responseDescription",
    "smsStatus"
})
public class SmsResponse {

    @XmlElement(name = "Successful")
    protected boolean successful;
    @XmlElement(name = "ResponseDescription")
    protected String responseDescription;
    @XmlElement(name = "SmsStatus", required = true)
    protected SmsStatus smsStatus;

    /**
     * Gets the value of the successful property.
     * 
     */
    public boolean isSuccessful() {
        return successful;
    }

    /**
     * Sets the value of the successful property.
     * 
     */
    public void setSuccessful(boolean value) {
        this.successful = value;
    }

    /**
     * Gets the value of the responseDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponseDescription() {
        return responseDescription;
    }

    /**
     * Sets the value of the responseDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponseDescription(String value) {
        this.responseDescription = value;
    }

    /**
     * Gets the value of the smsStatus property.
     * 
     * @return
     *     possible object is
     *     {@link SmsStatus }
     *     
     */
    public SmsStatus getSmsStatus() {
        return smsStatus;
    }

    /**
     * Sets the value of the smsStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link SmsStatus }
     *     
     */
    public void setSmsStatus(SmsStatus value) {
        this.smsStatus = value;
    }

}
