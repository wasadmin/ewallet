//
// Generated By:JAX-WS RI 2.2.4-b01 (JAXB RI IBM 2.2.4)
//


package zw.co.esolutions.ewallet.process;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getDayEndByDayEndStatus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getDayEndByDayEndStatus">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dayEndStatus" type="{http://process.ewallet.esolutions.co.zw/}dayEndStatus" minOccurs="0"/>
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
@XmlType(name = "getDayEndByDayEndStatus", propOrder = {
    "dayEndStatus",
    "userName"
})
public class GetDayEndByDayEndStatus {

    protected DayEndStatus dayEndStatus;
    protected String userName;

    /**
     * Gets the value of the dayEndStatus property.
     * 
     * @return
     *     possible object is
     *     {@link DayEndStatus }
     *     
     */
    public DayEndStatus getDayEndStatus() {
        return dayEndStatus;
    }

    /**
     * Sets the value of the dayEndStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link DayEndStatus }
     *     
     */
    public void setDayEndStatus(DayEndStatus value) {
        this.dayEndStatus = value;
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
