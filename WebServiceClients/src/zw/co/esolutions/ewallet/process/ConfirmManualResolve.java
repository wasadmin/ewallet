//
// Generated By:JAX-WS RI 2.2.4-b01 (JAXB RI IBM 2.2.4)
//


package zw.co.esolutions.ewallet.process;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for confirmManualResolve complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="confirmManualResolve">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="manualPojo" type="{http://process.ewallet.esolutions.co.zw/}manualPojo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "confirmManualResolve", propOrder = {
    "manualPojo"
})
public class ConfirmManualResolve {

    protected ManualPojo manualPojo;

    /**
     * Gets the value of the manualPojo property.
     * 
     * @return
     *     possible object is
     *     {@link ManualPojo }
     *     
     */
    public ManualPojo getManualPojo() {
        return manualPojo;
    }

    /**
     * Sets the value of the manualPojo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ManualPojo }
     *     
     */
    public void setManualPojo(ManualPojo value) {
        this.manualPojo = value;
    }

}
