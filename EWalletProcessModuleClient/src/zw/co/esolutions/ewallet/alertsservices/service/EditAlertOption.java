//
// Generated By:JAX-WS RI 2.2.4-b01 (JAXB RI IBM 2.2.4)
//


package zw.co.esolutions.ewallet.alertsservices.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for editAlertOption complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="editAlertOption">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="alertOption" type="{http://service.alertsservices.ewallet.esolutions.co.zw/}alertOption" minOccurs="0"/>
 *         &lt;element name="username" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "editAlertOption", propOrder = {
    "alertOption",
    "username"
})
public class EditAlertOption {

    protected AlertOption alertOption;
    protected String username;

    /**
     * Gets the value of the alertOption property.
     * 
     * @return
     *     possible object is
     *     {@link AlertOption }
     *     
     */
    public AlertOption getAlertOption() {
        return alertOption;
    }

    /**
     * Sets the value of the alertOption property.
     * 
     * @param value
     *     allowed object is
     *     {@link AlertOption }
     *     
     */
    public void setAlertOption(AlertOption value) {
        this.alertOption = value;
    }

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsername(String value) {
        this.username = value;
    }

}
