//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.customerservices.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for generateTxnPassCodeResp complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="generateTxnPassCodeResp">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="firstIndex" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="secondIndex" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "generateTxnPassCodeResp", namespace = "http://service.customerservices.ewallet.esolutions.co.zw/", propOrder = {
    "firstIndex",
    "secondIndex"
})
public class GenerateTxnPassCodeResp {

    protected int firstIndex;
    protected int secondIndex;

    /**
     * Gets the value of the firstIndex property.
     * 
     */
    public int getFirstIndex() {
        return firstIndex;
    }

    /**
     * Sets the value of the firstIndex property.
     * 
     */
    public void setFirstIndex(int value) {
        this.firstIndex = value;
    }

    /**
     * Gets the value of the secondIndex property.
     * 
     */
    public int getSecondIndex() {
        return secondIndex;
    }

    /**
     * Sets the value of the secondIndex property.
     * 
     */
    public void setSecondIndex(int value) {
        this.secondIndex = value;
    }

}