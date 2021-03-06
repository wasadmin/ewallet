//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.bankservices.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for transactionPostingInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="transactionPostingInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="amount" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="chargePostingInfo" type="{http://service.bankservices.ewallet.esolutions.co.zw/}chargePostingInfo" minOccurs="0"/>
 *         &lt;element name="originalTxnRef" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sourceAccountNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="srcAccountId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="srcNarrative" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="targetAccountId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="targetAccountNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="targetNarrative" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="transactionDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="txnCategory" type="{http://service.bankservices.ewallet.esolutions.co.zw/}transactionCategory" minOccurs="0"/>
 *         &lt;element name="txnRef" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="txnType" type="{http://service.bankservices.ewallet.esolutions.co.zw/}transactionType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transactionPostingInfo", namespace = "http://service.bankservices.ewallet.esolutions.co.zw/", propOrder = {
    "amount",
    "chargePostingInfo",
    "originalTxnRef",
    "sourceAccountNumber",
    "srcAccountId",
    "srcNarrative",
    "targetAccountId",
    "targetAccountNumber",
    "targetNarrative",
    "transactionDate",
    "txnCategory",
    "txnRef",
    "txnType"
})
public class TransactionPostingInfo {

    protected long amount;
    protected ChargePostingInfo chargePostingInfo;
    protected String originalTxnRef;
    protected String sourceAccountNumber;
    protected String srcAccountId;
    protected String srcNarrative;
    protected String targetAccountId;
    protected String targetAccountNumber;
    protected String targetNarrative;
    protected XMLGregorianCalendar transactionDate;
    protected TransactionCategory txnCategory;
    protected String txnRef;
    protected TransactionType txnType;

    /**
     * Gets the value of the amount property.
     * 
     */
    public long getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     * 
     */
    public void setAmount(long value) {
        this.amount = value;
    }

    /**
     * Gets the value of the chargePostingInfo property.
     * 
     * @return
     *     possible object is
     *     {@link ChargePostingInfo }
     *     
     */
    public ChargePostingInfo getChargePostingInfo() {
        return chargePostingInfo;
    }

    /**
     * Sets the value of the chargePostingInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargePostingInfo }
     *     
     */
    public void setChargePostingInfo(ChargePostingInfo value) {
        this.chargePostingInfo = value;
    }

    /**
     * Gets the value of the originalTxnRef property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOriginalTxnRef() {
        return originalTxnRef;
    }

    /**
     * Sets the value of the originalTxnRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOriginalTxnRef(String value) {
        this.originalTxnRef = value;
    }

    /**
     * Gets the value of the sourceAccountNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceAccountNumber() {
        return sourceAccountNumber;
    }

    /**
     * Sets the value of the sourceAccountNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceAccountNumber(String value) {
        this.sourceAccountNumber = value;
    }

    /**
     * Gets the value of the srcAccountId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSrcAccountId() {
        return srcAccountId;
    }

    /**
     * Sets the value of the srcAccountId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSrcAccountId(String value) {
        this.srcAccountId = value;
    }

    /**
     * Gets the value of the srcNarrative property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSrcNarrative() {
        return srcNarrative;
    }

    /**
     * Sets the value of the srcNarrative property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSrcNarrative(String value) {
        this.srcNarrative = value;
    }

    /**
     * Gets the value of the targetAccountId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetAccountId() {
        return targetAccountId;
    }

    /**
     * Sets the value of the targetAccountId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetAccountId(String value) {
        this.targetAccountId = value;
    }

    /**
     * Gets the value of the targetAccountNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetAccountNumber() {
        return targetAccountNumber;
    }

    /**
     * Sets the value of the targetAccountNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetAccountNumber(String value) {
        this.targetAccountNumber = value;
    }

    /**
     * Gets the value of the targetNarrative property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetNarrative() {
        return targetNarrative;
    }

    /**
     * Sets the value of the targetNarrative property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetNarrative(String value) {
        this.targetNarrative = value;
    }

    /**
     * Gets the value of the transactionDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTransactionDate() {
        return transactionDate;
    }

    /**
     * Sets the value of the transactionDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTransactionDate(XMLGregorianCalendar value) {
        this.transactionDate = value;
    }

    /**
     * Gets the value of the txnCategory property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionCategory }
     *     
     */
    public TransactionCategory getTxnCategory() {
        return txnCategory;
    }

    /**
     * Sets the value of the txnCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionCategory }
     *     
     */
    public void setTxnCategory(TransactionCategory value) {
        this.txnCategory = value;
    }

    /**
     * Gets the value of the txnRef property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTxnRef() {
        return txnRef;
    }

    /**
     * Sets the value of the txnRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTxnRef(String value) {
        this.txnRef = value;
    }

    /**
     * Gets the value of the txnType property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionType }
     *     
     */
    public TransactionType getTxnType() {
        return txnType;
    }

    /**
     * Sets the value of the txnType property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionType }
     *     
     */
    public void setTxnType(TransactionType value) {
        this.txnType = value;
    }

}
