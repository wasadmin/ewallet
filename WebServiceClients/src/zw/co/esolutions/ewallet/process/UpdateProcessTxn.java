//
// Generated By:JAX-WS RI 2.2.4-b01 (JAXB RI IBM 2.2.4)
//


package zw.co.esolutions.ewallet.process;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for updateProcessTxn complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="updateProcessTxn">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="processTxn" type="{http://process.ewallet.esolutions.co.zw/}processTransaction" minOccurs="0"/>
 *         &lt;element name="userName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "updateProcessTxn", propOrder = {
    "processTxn",
    "userName"
})
public class UpdateProcessTxn {

    protected ProcessTransaction processTxn;
    protected String userName;

    /**
     * Gets the value of the processTxn property.
     * 
     * @return
     *     possible object is
     *     {@link ProcessTransaction }
     *     
     */
    public ProcessTransaction getProcessTxn() {
        return processTxn;
    }

    /**
     * Sets the value of the processTxn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessTransaction }
     *     
     */
    public void setProcessTxn(ProcessTransaction value) {
        this.processTxn = value;
    }

    /**
     * Gets the value of the userName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the value of the userName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserName(String value) {
        this.userName = value;
    }

}
