//
// Generated By:JAX-WS RI 2.2.4-b01 (JAXB RI IBM 2.2.4)
//


package zw.co.esolutions.ussd.web.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for createWebSession complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="createWebSession">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="webSession" type="{http://services.web.ussd.esolutions.co.zw/}webSession" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "createWebSession", propOrder = {
    "webSession"
})
public class CreateWebSession {

    protected WebSession webSession;

    /**
     * Gets the value of the webSession property.
     * 
     * @return
     *     possible object is
     *     {@link WebSession }
     *     
     */
    public WebSession getWebSession() {
        return webSession;
    }

    /**
     * Sets the value of the webSession property.
     * 
     * @param value
     *     allowed object is
     *     {@link WebSession }
     *     
     */
    public void setWebSession(WebSession value) {
        this.webSession = value;
    }

}
