//
// Generated By:JAX-WS RI IBM 2.1.6 in JDK 6 (JAXB RI IBM JAXB 2.1.10 in JDK 6)
//


package zw.co.esolutions.mcommerce.refgen.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getNextNumberInSequence complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getNextNumberInSequence">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sequenceName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="year" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="minValue" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="maxValue" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getNextNumberInSequence", namespace = "http://service.refgen.mcommerce.esolutions.co.zw/", propOrder = {
    "sequenceName",
    "year",
    "minValue",
    "maxValue"
})
public class GetNextNumberInSequence {

    protected String sequenceName;
    protected String year;
    protected long minValue;
    protected long maxValue;

    /**
     * Gets the value of the sequenceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSequenceName() {
        return sequenceName;
    }

    /**
     * Sets the value of the sequenceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSequenceName(String value) {
        this.sequenceName = value;
    }

    /**
     * Gets the value of the year property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getYear() {
        return year;
    }

    /**
     * Sets the value of the year property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setYear(String value) {
        this.year = value;
    }

    /**
     * Gets the value of the minValue property.
     * 
     */
    public long getMinValue() {
        return minValue;
    }

    /**
     * Sets the value of the minValue property.
     * 
     */
    public void setMinValue(long value) {
        this.minValue = value;
    }

    /**
     * Gets the value of the maxValue property.
     * 
     */
    public long getMaxValue() {
        return maxValue;
    }

    /**
     * Sets the value of the maxValue property.
     * 
     */
    public void setMaxValue(long value) {
        this.maxValue = value;
    }

}
