//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.agentservice.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for agent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="agent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="agentClass" type="{http://service.agentservice.ewallet.esolutions.co.zw/}agentClass" minOccurs="0"/>
 *         &lt;element name="agentLevel" type="{http://service.agentservice.ewallet.esolutions.co.zw/}agentLevel" minOccurs="0"/>
 *         &lt;element name="agentName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="agentNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="agentType" type="{http://service.agentservice.ewallet.esolutions.co.zw/}agentType" minOccurs="0"/>
 *         &lt;element name="customerId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dateCreated" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="profileId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://service.agentservice.ewallet.esolutions.co.zw/}profileStatus" minOccurs="0"/>
 *         &lt;element name="superAgentId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="version" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "agent", namespace = "http://service.agentservice.ewallet.esolutions.co.zw/", propOrder = {
    "agentClass",
    "agentLevel",
    "agentName",
    "agentNumber",
    "agentType",
    "customerId",
    "dateCreated",
    "id",
    "profileId",
    "status",
    "superAgentId",
    "version"
})
public class Agent {

    protected AgentClass agentClass;
    protected AgentLevel agentLevel;
    protected String agentName;
    protected String agentNumber;
    protected AgentType agentType;
    protected String customerId;
    protected XMLGregorianCalendar dateCreated;
    protected String id;
    protected String profileId;
    protected ProfileStatus status;
    protected String superAgentId;
    protected long version;

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
     * Gets the value of the agentLevel property.
     * 
     * @return
     *     possible object is
     *     {@link AgentLevel }
     *     
     */
    public AgentLevel getAgentLevel() {
        return agentLevel;
    }

    /**
     * Sets the value of the agentLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link AgentLevel }
     *     
     */
    public void setAgentLevel(AgentLevel value) {
        this.agentLevel = value;
    }

    /**
     * Gets the value of the agentName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgentName() {
        return agentName;
    }

    /**
     * Sets the value of the agentName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgentName(String value) {
        this.agentName = value;
    }

    /**
     * Gets the value of the agentNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgentNumber() {
        return agentNumber;
    }

    /**
     * Sets the value of the agentNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgentNumber(String value) {
        this.agentNumber = value;
    }

    /**
     * Gets the value of the agentType property.
     * 
     * @return
     *     possible object is
     *     {@link AgentType }
     *     
     */
    public AgentType getAgentType() {
        return agentType;
    }

    /**
     * Sets the value of the agentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link AgentType }
     *     
     */
    public void setAgentType(AgentType value) {
        this.agentType = value;
    }

    /**
     * Gets the value of the customerId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Sets the value of the customerId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerId(String value) {
        this.customerId = value;
    }

    /**
     * Gets the value of the dateCreated property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateCreated() {
        return dateCreated;
    }

    /**
     * Sets the value of the dateCreated property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateCreated(XMLGregorianCalendar value) {
        this.dateCreated = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the profileId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProfileId() {
        return profileId;
    }

    /**
     * Sets the value of the profileId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProfileId(String value) {
        this.profileId = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link ProfileStatus }
     *     
     */
    public ProfileStatus getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProfileStatus }
     *     
     */
    public void setStatus(ProfileStatus value) {
        this.status = value;
    }

    /**
     * Gets the value of the superAgentId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSuperAgentId() {
        return superAgentId;
    }

    /**
     * Sets the value of the superAgentId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSuperAgentId(String value) {
        this.superAgentId = value;
    }

    /**
     * Gets the value of the version property.
     * 
     */
    public long getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     */
    public void setVersion(long value) {
        this.version = value;
    }

}
