//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.topup.ws.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for reversalResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="reversalResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="airtimeBalance" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="initialBalance" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="narrative" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="responseCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reversalRequest" type="{http://impl.ws.topup.esolutions.co.zw/}reversalRequest" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "reversalResponse", namespace = "http://impl.ws.topup.esolutions.co.zw/", propOrder = {
    "airtimeBalance",
    "initialBalance",
    "narrative",
    "responseCode",
    "reversalRequest"
})
public class ReversalResponse {

    protected double airtimeBalance;
    protected double initialBalance;
    protected String narrative;
    protected String responseCode;
    protected ReversalRequest reversalRequest;

    /**
     * Gets the value of the airtimeBalance property.
     * 
     */
    public double getAirtimeBalance() {
        return airtimeBalance;
    }

    /**
     * Sets the value of the airtimeBalance property.
     * 
     */
    public void setAirtimeBalance(double value) {
        this.airtimeBalance = value;
    }

    /**
     * Gets the value of the initialBalance property.
     * 
     */
    public double getInitialBalance() {
        return initialBalance;
    }

    /**
     * Sets the value of the initialBalance property.
     * 
     */
    public void setInitialBalance(double value) {
        this.initialBalance = value;
    }

    /**
     * Gets the value of the narrative property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNarrative() {
        return narrative;
    }

    /**
     * Sets the value of the narrative property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNarrative(String value) {
        this.narrative = value;
    }

    /**
     * Gets the value of the responseCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponseCode() {
        return responseCode;
    }

    /**
     * Sets the value of the responseCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponseCode(String value) {
        this.responseCode = value;
    }

    /**
     * Gets the value of the reversalRequest property.
     * 
     * @return
     *     possible object is
     *     {@link ReversalRequest }
     *     
     */
    public ReversalRequest getReversalRequest() {
        return reversalRequest;
    }

    /**
     * Sets the value of the reversalRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReversalRequest }
     *     
     */
    public void setReversalRequest(ReversalRequest value) {
        this.reversalRequest = value;
    }

}
