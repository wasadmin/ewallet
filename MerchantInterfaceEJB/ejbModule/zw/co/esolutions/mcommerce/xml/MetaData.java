//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vIBM 2.2.3-05/12/2011 11:54 AM(foreman)- 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.04.13 at 02:21:41 PM CAT 
//


package zw.co.esolutions.mcommerce.xml;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MetaData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MetaData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="postingBranch" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="replyQueue" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="requestQueue" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="source" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetaData", propOrder = {
    "postingBranch",
    "replyQueue",
    "requestQueue",
    "source"
})
@Embeddable
public class MetaData {

    @XmlElement(required = true)
    @Column(length = 20)
    protected String postingBranch;
    
    @XmlElement(required = true)
    @Column(length = 50)
    protected String replyQueue;
    
    @XmlElement(required = true)
    @Column(length = 50)
    protected String requestQueue;
    
    @XmlElement(required = true)
    @Column(length = 50)
    protected String source;

    /**
     * Gets the value of the postingBranch property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostingBranch() {
        return postingBranch;
    }

    /**
     * Sets the value of the postingBranch property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostingBranch(String value) {
        this.postingBranch = value;
    }

    /**
     * Gets the value of the replyQueue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReplyQueue() {
        return replyQueue;
    }

    /**
     * Sets the value of the replyQueue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReplyQueue(String value) {
        this.replyQueue = value;
    }

    /**
     * Gets the value of the requestQueue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestQueue() {
        return requestQueue;
    }

    /**
     * Sets the value of the requestQueue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestQueue(String value) {
        this.requestQueue = value;
    }

    /**
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSource(String value) {
        this.source = value;
    }

}
