//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.econet.econetvas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="txtDebitResponse" type="{http://econetvas.econet.co.zw/}TextDebitResponse"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "txtDebitResponse"
})
@XmlRootElement(name = "txtDebitResponse")
public class TxtDebitResponse {

    @XmlElement(required = true)
    protected TextDebitResponse txtDebitResponse;

    /**
     * Gets the value of the txtDebitResponse property.
     * 
     * @return
     *     possible object is
     *     {@link TextDebitResponse }
     *     
     */
    public TextDebitResponse getTxtDebitResponse() {
        return txtDebitResponse;
    }

    /**
     * Sets the value of the txtDebitResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link TextDebitResponse }
     *     
     */
    public void setTxtDebitResponse(TextDebitResponse value) {
        this.txtDebitResponse = value;
    }

}
