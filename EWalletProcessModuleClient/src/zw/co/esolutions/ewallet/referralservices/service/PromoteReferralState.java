//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.referralservices.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for promoteReferralState complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="promoteReferralState">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="arg0" type="{http://service.referralservices.ewallet.esolutions.co.zw/}referral" minOccurs="0"/>
 *         &lt;element name="arg1" type="{http://service.referralservices.ewallet.esolutions.co.zw/}referralStatus" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "promoteReferralState", namespace = "http://service.referralservices.ewallet.esolutions.co.zw/", propOrder = {
    "arg0",
    "arg1"
})
public class PromoteReferralState {

    protected Referral arg0;
    protected ReferralStatus arg1;

    /**
     * Gets the value of the arg0 property.
     * 
     * @return
     *     possible object is
     *     {@link Referral }
     *     
     */
    public Referral getArg0() {
        return arg0;
    }

    /**
     * Sets the value of the arg0 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Referral }
     *     
     */
    public void setArg0(Referral value) {
        this.arg0 = value;
    }

    /**
     * Gets the value of the arg1 property.
     * 
     * @return
     *     possible object is
     *     {@link ReferralStatus }
     *     
     */
    public ReferralStatus getArg1() {
        return arg1;
    }

    /**
     * Sets the value of the arg1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferralStatus }
     *     
     */
    public void setArg1(ReferralStatus value) {
        this.arg1 = value;
    }

}