//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.mobile.banking.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for vereq complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="vereq">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="acquirerId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="bankCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mobileNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sourceMobileNetworkOperator" type="{http://services.banking.mobile.esolutions.co.zw/}mobileNetworkOperator" minOccurs="0"/>
 *         &lt;element name="transactionTimestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="ussdSessionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "vereq", namespace = "http://services.banking.mobile.esolutions.co.zw/", propOrder = {
    "acquirerId",
    "bankCode",
    "mobileNumber",
    "sourceMobileNetworkOperator",
    "transactionTimestamp",
    "ussdSessionId"
})
public class Vereq {

    protected String acquirerId;
    protected String bankCode;
    protected String mobileNumber;
    protected MobileNetworkOperator sourceMobileNetworkOperator;
    protected XMLGregorianCalendar transactionTimestamp;
    protected String ussdSessionId;

    /**
     * Gets the value of the acquirerId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAcquirerId() {
        return acquirerId;
    }

    /**
     * Sets the value of the acquirerId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAcquirerId(String value) {
        this.acquirerId = value;
    }

    /**
     * Gets the value of the bankCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBankCode() {
        return bankCode;
    }

    /**
     * Sets the value of the bankCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBankCode(String value) {
        this.bankCode = value;
    }

    /**
     * Gets the value of the mobileNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMobileNumber() {
        return mobileNumber;
    }

    /**
     * Sets the value of the mobileNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMobileNumber(String value) {
        this.mobileNumber = value;
    }

    /**
     * Gets the value of the sourceMobileNetworkOperator property.
     * 
     * @return
     *     possible object is
     *     {@link MobileNetworkOperator }
     *     
     */
    public MobileNetworkOperator getSourceMobileNetworkOperator() {
        return sourceMobileNetworkOperator;
    }

    /**
     * Sets the value of the sourceMobileNetworkOperator property.
     * 
     * @param value
     *     allowed object is
     *     {@link MobileNetworkOperator }
     *     
     */
    public void setSourceMobileNetworkOperator(MobileNetworkOperator value) {
        this.sourceMobileNetworkOperator = value;
    }

    /**
     * Gets the value of the transactionTimestamp property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTransactionTimestamp() {
        return transactionTimestamp;
    }

    /**
     * Sets the value of the transactionTimestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTransactionTimestamp(XMLGregorianCalendar value) {
        this.transactionTimestamp = value;
    }

    /**
     * Gets the value of the ussdSessionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUssdSessionId() {
        return ussdSessionId;
    }

    /**
     * Sets the value of the ussdSessionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUssdSessionId(String value) {
        this.ussdSessionId = value;
    }

}
