//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.agentservice.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for approveAgentClass complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="approveAgentClass">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="agentClass" type="{http://service.agentservice.ewallet.esolutions.co.zw/}agentClass" minOccurs="0"/>
 *         &lt;element name="username" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "approveAgentClass", namespace = "http://service.agentservice.ewallet.esolutions.co.zw/", propOrder = {
    "agentClass",
    "username"
})
public class ApproveAgentClass {

    protected AgentClass agentClass;
    protected String username;

    /**
     * Gets the value of the agentClass property.
     * 
     * @return
     *     possible object is
     *     {@link AgentClass }
     *     
     */
    public AgentClass getAgentClass() {
        return agentClass;
    }

    /**
     * Sets the value of the agentClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link AgentClass }
     *     
     */
    public void setAgentClass(AgentClass value) {
        this.agentClass = value;
    }

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsername(String value) {
        this.username = value;
    }

}
