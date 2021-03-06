//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.econet.econetvas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TextDebitResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TextDebitResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="textDebitRequest" type="{http://econetvas.econet.co.zw/}TextDebitRequest"/>
 *         &lt;element name="responseCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="narrative" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="numberOfTextsRemoved" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TextDebitResponse", namespace = "http://econetvas.econet.co.zw/", propOrder = {
    "textDebitRequest",
    "responseCode",
    "narrative",
    "numberOfTextsRemoved"
})
public class TextDebitResponse {

    @XmlElement(required = true)
    protected TextDebitRequest textDebitRequest;
    @XmlElement(required = true)
    protected String responseCode;
    @XmlElement(required = true)
    protected String narrative;
    protected int numberOfTextsRemoved;

    /**
     * Gets the value of the textDebitRequest property.
     * 
     * @return
     *     possible object is
     *     {@link TextDebitRequest }
     *     
     */
    public TextDebitRequest getTextDebitRequest() {
        return textDebitRequest;
    }

    /**
     * Sets the value of the textDebitRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link TextDebitRequest }
     *     
     */
    public void setTextDebitRequest(TextDebitRequest value) {
        this.textDebitRequest = value;
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
     * Gets the value of the numberOfTextsRemoved property.
     * 
     */
    public int getNumberOfTextsRemoved() {
        return numberOfTextsRemoved;
    }

    /**
     * Sets the value of the numberOfTextsRemoved property.
     * 
     */
    public void setNumberOfTextsRemoved(int value) {
        this.numberOfTextsRemoved = value;
    }

}
