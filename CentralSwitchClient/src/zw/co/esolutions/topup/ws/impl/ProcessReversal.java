//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.topup.ws.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for processReversal complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="processReversal">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
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
@XmlType(name = "processReversal", namespace = "http://impl.ws.topup.esolutions.co.zw/", propOrder = {
    "reversalRequest"
})
public class ProcessReversal {

    protected ReversalRequest reversalRequest;

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
