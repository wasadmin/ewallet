//
// Generated By:JAX-WS RI 2.2.4-b01 (JAXB RI IBM 2.2.4)
//


package zw.co.esolutions.ussd.web.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for updateWebSessionResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="updateWebSessionResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://services.web.ussd.esolutions.co.zw/}webSession" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "updateWebSessionResponse", propOrder = {
    "_return"
})
public class UpdateWebSessionResponse {

    @XmlElement(name = "return")
    protected WebSession _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link WebSession }
     *     
     */
    public WebSession getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link WebSession }
     *     
     */
    public void setReturn(WebSession value) {
        this._return = value;
    }

}
