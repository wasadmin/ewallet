//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.econet.econetvas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for billPayReversal complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="billPayReversal">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="billPayReversalRequest" type="{http://econetvas.econet.co.zw/}BillPaymentReversalRequest" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "billPayReversal", namespace = "http://econetvas.econet.co.zw/", propOrder = {
    "billPayReversalRequest"
})
public class BillPayReversal {

    protected BillPaymentReversalRequest billPayReversalRequest;

    /**
     * Gets the value of the billPayReversalRequest property.
     * 
     * @return
     *     possible object is
     *     {@link BillPaymentReversalRequest }
     *     
     */
    public BillPaymentReversalRequest getBillPayReversalRequest() {
        return billPayReversalRequest;
    }

    /**
     * Sets the value of the billPayReversalRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link BillPaymentReversalRequest }
     *     
     */
    public void setBillPayReversalRequest(BillPaymentReversalRequest value) {
        this.billPayReversalRequest = value;
    }

}