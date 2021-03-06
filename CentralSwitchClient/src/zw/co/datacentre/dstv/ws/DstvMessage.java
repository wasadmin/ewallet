//
// Generated By:JAX-WS RI IBM 2.1.6 in JDK 6 (JAXB RI IBM JAXB 2.1.10 in JDK 6)
//


package zw.co.datacentre.dstv.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for dstvMessage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dstvMessage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="amount" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="bouquet" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="commission" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="dstvAccount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="months" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="narrative" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="responseCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dstvMessage", namespace = "http://ws.dstv.datacentre.co.zw/", propOrder = {
    "amount",
    "bouquet",
    "commission",
    "dstvAccount",
    "months",
    "narrative",
    "responseCode"
})
public class DstvMessage {

    protected double amount;
    protected String bouquet;
    protected double commission;
    protected String dstvAccount;
    protected int months;
    protected String narrative;
    protected String responseCode;

    /**
     * Gets the value of the amount property.
     * 
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     * 
     */
    public void setAmount(double value) {
        this.amount = value;
    }

    /**
     * Gets the value of the bouquet property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBouquet() {
        return bouquet;
    }

    /**
     * Sets the value of the bouquet property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBouquet(String value) {
        this.bouquet = value;
    }

    /**
     * Gets the value of the commission property.
     * 
     */
    public double getCommission() {
        return commission;
    }

    /**
     * Sets the value of the commission property.
     * 
     */
    public void setCommission(double value) {
        this.commission = value;
    }

    /**
     * Gets the value of the dstvAccount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDstvAccount() {
        return dstvAccount;
    }

    /**
     * Sets the value of the dstvAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDstvAccount(String value) {
        this.dstvAccount = value;
    }

    /**
     * Gets the value of the months property.
     * 
     */
    public int getMonths() {
        return months;
    }

    /**
     * Sets the value of the months property.
     * 
     */
    public void setMonths(int value) {
        this.months = value;
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

}
