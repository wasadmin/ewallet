//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.mobile.banking.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for submitTransaction complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="submitTransaction">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="transactionRequest" type="{http://services.banking.mobile.esolutions.co.zw/}transactionRequest" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "submitTransaction", namespace = "http://services.banking.mobile.esolutions.co.zw/", propOrder = {
    "transactionRequest"
})
public class SubmitTransaction {

    protected TransactionRequest transactionRequest;

    /**
     * Gets the value of the transactionRequest property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionRequest }
     *     
     */
    public TransactionRequest getTransactionRequest() {
        return transactionRequest;
    }

    /**
     * Sets the value of the transactionRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionRequest }
     *     
     */
    public void setTransactionRequest(TransactionRequest value) {
        this.transactionRequest = value;
    }

}
