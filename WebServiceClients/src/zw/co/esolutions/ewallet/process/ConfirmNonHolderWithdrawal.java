//
// Generated By:JAX-WS RI 2.2.4-b01 (JAXB RI IBM 2.2.4)
//


package zw.co.esolutions.ewallet.process;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for confirmNonHolderWithdrawal complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="confirmNonHolderWithdrawal">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="txnRefId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="profileId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "confirmNonHolderWithdrawal", propOrder = {
    "txnRefId",
    "profileId"
})
public class ConfirmNonHolderWithdrawal {

    protected String txnRefId;
    protected String profileId;

    /**
     * Gets the value of the txnRefId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTxnRefId() {
        return txnRefId;
    }

    /**
     * Sets the value of the txnRefId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTxnRefId(String value) {
        this.txnRefId = value;
    }

    /**
     * Gets the value of the profileId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProfileId() {
        return profileId;
    }

    /**
     * Sets the value of the profileId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProfileId(String value) {
        this.profileId = value;
    }

}