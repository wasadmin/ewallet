//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.centralswitch.config.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for approveConfigInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="approveConfigInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="bankConfigInfo" type="{http://service.config.centralswitch.esolutions.co.zw/}configInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "approveConfigInfo", namespace = "http://service.config.centralswitch.esolutions.co.zw/", propOrder = {
    "bankConfigInfo"
})
public class ApproveConfigInfo {

    protected ConfigInfo bankConfigInfo;

    /**
     * Gets the value of the bankConfigInfo property.
     * 
     * @return
     *     possible object is
     *     {@link ConfigInfo }
     *     
     */
    public ConfigInfo getBankConfigInfo() {
        return bankConfigInfo;
    }

    /**
     * Sets the value of the bankConfigInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConfigInfo }
     *     
     */
    public void setBankConfigInfo(ConfigInfo value) {
        this.bankConfigInfo = value;
    }

}
