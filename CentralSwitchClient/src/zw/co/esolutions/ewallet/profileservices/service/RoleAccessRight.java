//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.profileservices.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for roleAccessRight complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="roleAccessRight">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="accessRight" type="{http://service.profileservices.ewallet.esolutions.co.zw/}accessRight" minOccurs="0"/>
 *         &lt;element name="canDo" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="dateCreated" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="role" type="{http://service.profileservices.ewallet.esolutions.co.zw/}role" minOccurs="0"/>
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
@XmlType(name = "roleAccessRight", namespace = "http://service.profileservices.ewallet.esolutions.co.zw/", propOrder = {
    "accessRight",
    "canDo",
    "dateCreated",
    "id",
    "role",
    "version"
})
public class RoleAccessRight {

    protected AccessRight accessRight;
    protected boolean canDo;
    protected XMLGregorianCalendar dateCreated;
    protected String id;
    protected Role role;
    protected long version;

    /**
     * Gets the value of the accessRight property.
     * 
     * @return
     *     possible object is
     *     {@link AccessRight }
     *     
     */
    public AccessRight getAccessRight() {
        return accessRight;
    }

    /**
     * Sets the value of the accessRight property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccessRight }
     *     
     */
    public void setAccessRight(AccessRight value) {
        this.accessRight = value;
    }

    /**
     * Gets the value of the canDo property.
     * 
     */
    public boolean isCanDo() {
        return canDo;
    }

    /**
     * Sets the value of the canDo property.
     * 
     */
    public void setCanDo(boolean value) {
        this.canDo = value;
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
     * Gets the value of the role property.
     * 
     * @return
     *     possible object is
     *     {@link Role }
     *     
     */
    public Role getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     * 
     * @param value
     *     allowed object is
     *     {@link Role }
     *     
     */
    public void setRole(Role value) {
        this.role = value;
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
