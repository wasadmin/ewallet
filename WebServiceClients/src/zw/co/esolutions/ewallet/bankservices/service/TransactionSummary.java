//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.bankservices.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for transactionSummary complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="transactionSummary">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="closingBal" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="header" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="netMovement" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="openingBal" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="summaryDetails" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transactionSummary", namespace = "http://service.bankservices.ewallet.esolutions.co.zw/", propOrder = {
    "closingBal",
    "description",
    "header",
    "netMovement",
    "openingBal",
    "summaryDetails"
})
public class TransactionSummary {

    protected long closingBal;
    protected String description;
    protected String header;
    protected long netMovement;
    protected long openingBal;
    protected String summaryDetails;

    /**
     * Gets the value of the closingBal property.
     * 
     */
    public long getClosingBal() {
        return closingBal;
    }

    /**
     * Sets the value of the closingBal property.
     * 
     */
    public void setClosingBal(long value) {
        this.closingBal = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the header property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHeader() {
        return header;
    }

    /**
     * Sets the value of the header property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHeader(String value) {
        this.header = value;
    }

    /**
     * Gets the value of the netMovement property.
     * 
     */
    public long getNetMovement() {
        return netMovement;
    }

    /**
     * Sets the value of the netMovement property.
     * 
     */
    public void setNetMovement(long value) {
        this.netMovement = value;
    }

    /**
     * Gets the value of the openingBal property.
     * 
     */
    public long getOpeningBal() {
        return openingBal;
    }

    /**
     * Sets the value of the openingBal property.
     * 
     */
    public void setOpeningBal(long value) {
        this.openingBal = value;
    }

    /**
     * Gets the value of the summaryDetails property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSummaryDetails() {
        return summaryDetails;
    }

    /**
     * Sets the value of the summaryDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSummaryDetails(String value) {
        this.summaryDetails = value;
    }

}
