//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.econet.econetvas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for billPayReversalResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="billPayReversalResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="billPayReversalResponse" type="{http://econetvas.econet.co.zw/}BillPaymentReversalResponse" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "billPayReversalResponse", namespace = "http://econetvas.econet.co.zw/", propOrder = {
    "billPayReversalResponse"
})
public class BillPayReversalResponse {

    protected BillPaymentReversalResponse billPayReversalResponse;

    /**
     * Gets the value of the billPayReversalResponse property.
     * 
     * @return
     *     possible object is
     *     {@link BillPaymentReversalResponse }
     *     
     */
    public BillPaymentReversalResponse getBillPayReversalResponse() {
        return billPayReversalResponse;
    }

    /**
     * Sets the value of the billPayReversalResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link BillPaymentReversalResponse }
     *     
     */
    public void setBillPayReversalResponse(BillPaymentReversalResponse value) {
        this.billPayReversalResponse = value;
    }

}
