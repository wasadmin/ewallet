//
// Generated By:JAX-WS RI 2.2.4-b01 (JAXB RI IBM 2.2.4)
//


package zw.co.esolutions.ewallet.process;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getProcessTransactionsByAllAttributes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getProcessTransactionsByAllAttributes">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="universalProcessPojo" type="{http://process.ewallet.esolutions.co.zw/}universalProcessSearch" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getProcessTransactionsByAllAttributes", propOrder = {
    "universalProcessPojo"
})
public class GetProcessTransactionsByAllAttributes {

    protected UniversalProcessSearch universalProcessPojo;

    /**
     * Gets the value of the universalProcessPojo property.
     * 
     * @return
     *     possible object is
     *     {@link UniversalProcessSearch }
     *     
     */
    public UniversalProcessSearch getUniversalProcessPojo() {
        return universalProcessPojo;
    }

    /**
     * Sets the value of the universalProcessPojo property.
     * 
     * @param value
     *     allowed object is
     *     {@link UniversalProcessSearch }
     *     
     */
    public void setUniversalProcessPojo(UniversalProcessSearch value) {
        this.universalProcessPojo = value;
    }

}
