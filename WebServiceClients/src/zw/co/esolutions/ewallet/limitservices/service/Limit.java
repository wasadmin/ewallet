//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.limitservices.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for limit complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="limit">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="accountClass" type="{http://service.limitservices.ewallet.esolutions.co.zw/}bankAccountClass" minOccurs="0"/>
 *         &lt;element name="bankId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dateCreated" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="effectiveDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="endDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="maxValue" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="minValue" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="oldLimitId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="periodType" type="{http://service.limitservices.ewallet.esolutions.co.zw/}limitPeriodType" minOccurs="0"/>
 *         &lt;element name="status" type="{http://service.limitservices.ewallet.esolutions.co.zw/}limitStatus" minOccurs="0"/>
 *         &lt;element name="type" type="{http://service.limitservices.ewallet.esolutions.co.zw/}transactionType" minOccurs="0"/>
 *         &lt;element name="valueType" type="{http://service.limitservices.ewallet.esolutions.co.zw/}limitValueType" minOccurs="0"/>
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
@XmlType(name = "limit", namespace = "http://service.limitservices.ewallet.esolutions.co.zw/", propOrder = {
    "accountClass",
    "bankId",
    "dateCreated",
    "effectiveDate",
    "endDate",
    "id",
    "maxValue",
    "minValue",
    "oldLimitId",
    "periodType",
    "status",
    "type",
    "valueType",
    "version"
})
public class Limit {

    protected BankAccountClass accountClass;
    protected String bankId;
    protected XMLGregorianCalendar dateCreated;
    protected XMLGregorianCalendar effectiveDate;
    protected XMLGregorianCalendar endDate;
    protected String id;
    protected long maxValue;
    protected long minValue;
    protected String oldLimitId;
    protected LimitPeriodType periodType;
    protected LimitStatus status;
    protected TransactionType type;
    protected LimitValueType valueType;
    protected long version;

    /**
     * Gets the value of the accountClass property.
     * 
     * @return
     *     possible object is
     *     {@link BankAccountClass }
     *     
     */
    public BankAccountClass getAccountClass() {
        return accountClass;
    }

    /**
     * Sets the value of the accountClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link BankAccountClass }
     *     
     */
    public void setAccountClass(BankAccountClass value) {
        this.accountClass = value;
    }

    /**
     * Gets the value of the bankId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBankId() {
        return bankId;
    }

    /**
     * Sets the value of the bankId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBankId(String value) {
        this.bankId = value;
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
     * Gets the value of the effectiveDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEffectiveDate() {
        return effectiveDate;
    }

    /**
     * Sets the value of the effectiveDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEffectiveDate(XMLGregorianCalendar value) {
        this.effectiveDate = value;
    }

    /**
     * Gets the value of the endDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEndDate(XMLGregorianCalendar value) {
        this.endDate = value;
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
     * Gets the value of the maxValue property.
     * 
     */
    public long getMaxValue() {
        return maxValue;
    }

    /**
     * Sets the value of the maxValue property.
     * 
     */
    public void setMaxValue(long value) {
        this.maxValue = value;
    }

    /**
     * Gets the value of the minValue property.
     * 
     */
    public long getMinValue() {
        return minValue;
    }

    /**
     * Sets the value of the minValue property.
     * 
     */
    public void setMinValue(long value) {
        this.minValue = value;
    }

    /**
     * Gets the value of the oldLimitId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOldLimitId() {
        return oldLimitId;
    }

    /**
     * Sets the value of the oldLimitId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOldLimitId(String value) {
        this.oldLimitId = value;
    }

    /**
     * Gets the value of the periodType property.
     * 
     * @return
     *     possible object is
     *     {@link LimitPeriodType }
     *     
     */
    public LimitPeriodType getPeriodType() {
        return periodType;
    }

    /**
     * Sets the value of the periodType property.
     * 
     * @param value
     *     allowed object is
     *     {@link LimitPeriodType }
     *     
     */
    public void setPeriodType(LimitPeriodType value) {
        this.periodType = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link LimitStatus }
     *     
     */
    public LimitStatus getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link LimitStatus }
     *     
     */
    public void setStatus(LimitStatus value) {
        this.status = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionType }
     *     
     */
    public TransactionType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionType }
     *     
     */
    public void setType(TransactionType value) {
        this.type = value;
    }

    /**
     * Gets the value of the valueType property.
     * 
     * @return
     *     possible object is
     *     {@link LimitValueType }
     *     
     */
    public LimitValueType getValueType() {
        return valueType;
    }

    /**
     * Sets the value of the valueType property.
     * 
     * @param value
     *     allowed object is
     *     {@link LimitValueType }
     *     
     */
    public void setValueType(LimitValueType value) {
        this.valueType = value;
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
