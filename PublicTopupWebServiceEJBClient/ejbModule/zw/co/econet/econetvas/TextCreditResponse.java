//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.econet.econetvas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TextCreditResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TextCreditResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="textCreditRequest" type="{http://econetvas.econet.co.zw/}TextCreditRequest"/>
 *         &lt;element name="responseCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="narrative" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="numberOfTextsAdded" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TextCreditResponse", namespace = "http://econetvas.econet.co.zw/", propOrder = {
    "textCreditRequest",
    "responseCode",
    "narrative",
    "numberOfTextsAdded"
})
public class TextCreditResponse {

    @XmlElement(required = true)
    protected TextCreditRequest textCreditRequest;
    @XmlElement(required = true)
    protected String responseCode;
    @XmlElement(required = true)
    protected String narrative;
    protected int numberOfTextsAdded;

    /**
     * Gets the value of the textCreditRequest property.
     * 
     * @return
     *     possible object is
     *     {@link TextCreditRequest }
     *     
     */
    public TextCreditRequest getTextCreditRequest() {
        return textCreditRequest;
    }

    /**
     * Sets the value of the textCreditRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link TextCreditRequest }
     *     
     */
    public void setTextCreditRequest(TextCreditRequest value) {
        this.textCreditRequest = value;
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
     * Gets the value of the numberOfTextsAdded property.
     * 
     */
    public int getNumberOfTextsAdded() {
        return numberOfTextsAdded;
    }

    /**
     * Sets the value of the numberOfTextsAdded property.
     * 
     */
    public void setNumberOfTextsAdded(int value) {
        this.numberOfTextsAdded = value;
    }

}
