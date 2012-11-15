//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.econet.econetvas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for BalanceResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BalanceResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="balanceRequest" type="{http://econetvas.econet.co.zw/}BalanceRequest"/>
 *         &lt;element name="currentBalance" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="classOfService" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="responseCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="narrative" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="maximumCredit" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="expiryDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="minimumTopup" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="suscriberType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="maximumTopup" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BalanceResponse", namespace = "http://econetvas.econet.co.zw/", propOrder = {
    "balanceRequest",
    "currentBalance",
    "classOfService",
    "responseCode",
    "narrative",
    "maximumCredit",
    "expiryDate",
    "minimumTopup",
    "suscriberType",
    "maximumTopup"
})
public class BalanceResponse {

    @XmlElement(required = true)
    protected BalanceRequest balanceRequest;
    protected double currentBalance;
    @XmlElement(required = true)
    protected String classOfService;
    @XmlElement(required = true)
    protected String responseCode;
    @XmlElement(required = true)
    protected String narrative;
    protected double maximumCredit;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar expiryDate;
    protected double minimumTopup;
    @XmlElement(required = true)
    protected String suscriberType;
    protected double maximumTopup;

    /**
     * Gets the value of the balanceRequest property.
     * 
     * @return
     *     possible object is
     *     {@link BalanceRequest }
     *     
     */
    public BalanceRequest getBalanceRequest() {
        return balanceRequest;
    }

    /**
     * Sets the value of the balanceRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link BalanceRequest }
     *     
     */
    public void setBalanceRequest(BalanceRequest value) {
        this.balanceRequest = value;
    }

    /**
     * Gets the value of the currentBalance property.
     * 
     */
    public double getCurrentBalance() {
        return currentBalance;
    }

    /**
     * Sets the value of the currentBalance property.
     * 
     */
    public void setCurrentBalance(double value) {
        this.currentBalance = value;
    }

    /**
     * Gets the value of the classOfService property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassOfService() {
        return classOfService;
    }

    /**
     * Sets the value of the classOfService property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassOfService(String value) {
        this.classOfService = value;
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
     * Gets the value of the maximumCredit property.
     * 
     */
    public double getMaximumCredit() {
        return maximumCredit;
    }

    /**
     * Sets the value of the maximumCredit property.
     * 
     */
    public void setMaximumCredit(double value) {
        this.maximumCredit = value;
    }

    /**
     * Gets the value of the expiryDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExpiryDate() {
        return expiryDate;
    }

    /**
     * Sets the value of the expiryDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExpiryDate(XMLGregorianCalendar value) {
        this.expiryDate = value;
    }

    /**
     * Gets the value of the minimumTopup property.
     * 
     */
    public double getMinimumTopup() {
        return minimumTopup;
    }

    /**
     * Sets the value of the minimumTopup property.
     * 
     */
    public void setMinimumTopup(double value) {
        this.minimumTopup = value;
    }

    /**
     * Gets the value of the suscriberType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSuscriberType() {
        return suscriberType;
    }

    /**
     * Sets the value of the suscriberType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSuscriberType(String value) {
        this.suscriberType = value;
    }

    /**
     * Gets the value of the maximumTopup property.
     * 
     */
    public double getMaximumTopup() {
        return maximumTopup;
    }

    /**
     * Sets the value of the maximumTopup property.
     * 
     */
    public void setMaximumTopup(double value) {
        this.maximumTopup = value;
    }

}