//
// Generated By:JAX-WS RI 2.2.4-b01 (JAXB RI IBM 2.2.4)
//


package zw.co.esolutions.ewallet.process;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for transactionCharge complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="transactionCharge">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="agentNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="agentType" type="{http://process.ewallet.esolutions.co.zw/}agentType" minOccurs="0"/>
 *         &lt;element name="chargeTransactionClass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dateCreated" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="fromEQ3Account" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fromEwalletAccount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="processTransaction" type="{http://process.ewallet.esolutions.co.zw/}processTransaction" minOccurs="0"/>
 *         &lt;element name="status" type="{http://process.ewallet.esolutions.co.zw/}transactionStatus" minOccurs="0"/>
 *         &lt;element name="tariffAmount" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="tariffId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="toEQ3Account" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="toEwalletAccount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="transactionType" type="{http://process.ewallet.esolutions.co.zw/}transactionType" minOccurs="0"/>
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
@XmlType(name = "transactionCharge", propOrder = {
    "agentNumber",
    "agentType",
    "chargeTransactionClass",
    "dateCreated",
    "fromEQ3Account",
    "fromEwalletAccount",
    "id",
    "processTransaction",
    "status",
    "tariffAmount",
    "tariffId",
    "toEQ3Account",
    "toEwalletAccount",
    "transactionType",
    "version"
})
public class TransactionCharge {

    protected String agentNumber;
    protected AgentType agentType;
    protected String chargeTransactionClass;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateCreated;
    protected String fromEQ3Account;
    protected String fromEwalletAccount;
    protected String id;
    protected ProcessTransaction processTransaction;
    protected TransactionStatus status;
    protected long tariffAmount;
    protected String tariffId;
    protected String toEQ3Account;
    protected String toEwalletAccount;
    protected TransactionType transactionType;
    protected long version;

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
     * Gets the value of the chargeTransactionClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChargeTransactionClass() {
        return chargeTransactionClass;
    }

    /**
     * Sets the value of the chargeTransactionClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChargeTransactionClass(String value) {
        this.chargeTransactionClass = value;
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
     * Gets the value of the fromEQ3Account property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFromEQ3Account() {
        return fromEQ3Account;
    }

    /**
     * Sets the value of the fromEQ3Account property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromEQ3Account(String value) {
        this.fromEQ3Account = value;
    }

    /**
     * Gets the value of the fromEwalletAccount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFromEwalletAccount() {
        return fromEwalletAccount;
    }

    /**
     * Sets the value of the fromEwalletAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromEwalletAccount(String value) {
        this.fromEwalletAccount = value;
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
     * Gets the value of the processTransaction property.
     * 
     * @return
     *     possible object is
     *     {@link ProcessTransaction }
     *     
     */
    public ProcessTransaction getProcessTransaction() {
        return processTransaction;
    }

    /**
     * Sets the value of the processTransaction property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessTransaction }
     *     
     */
    public void setProcessTransaction(ProcessTransaction value) {
        this.processTransaction = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionStatus }
     *     
     */
    public TransactionStatus getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionStatus }
     *     
     */
    public void setStatus(TransactionStatus value) {
        this.status = value;
    }

    /**
     * Gets the value of the tariffAmount property.
     * 
     */
    public long getTariffAmount() {
        return tariffAmount;
    }

    /**
     * Sets the value of the tariffAmount property.
     * 
     */
    public void setTariffAmount(long value) {
        this.tariffAmount = value;
    }

    /**
     * Gets the value of the tariffId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTariffId() {
        return tariffId;
    }

    /**
     * Sets the value of the tariffId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTariffId(String value) {
        this.tariffId = value;
    }

    /**
     * Gets the value of the toEQ3Account property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToEQ3Account() {
        return toEQ3Account;
    }

    /**
     * Sets the value of the toEQ3Account property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToEQ3Account(String value) {
        this.toEQ3Account = value;
    }

    /**
     * Gets the value of the toEwalletAccount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToEwalletAccount() {
        return toEwalletAccount;
    }

    /**
     * Sets the value of the toEwalletAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToEwalletAccount(String value) {
        this.toEwalletAccount = value;
    }

    /**
     * Gets the value of the transactionType property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionType }
     *     
     */
    public TransactionType getTransactionType() {
        return transactionType;
    }

    /**
     * Sets the value of the transactionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionType }
     *     
     */
    public void setTransactionType(TransactionType value) {
        this.transactionType = value;
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
