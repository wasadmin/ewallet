//
// Generated By:JAX-WS RI 2.2.4-b01 (JAXB RI IBM 2.2.4)
//


package zw.co.esolutions.ewallet.process;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for processTransaction complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="processTransaction">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="agentCommissionAmount" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="agentCommissionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="agentNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="amount" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="balance" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="bankMerchantId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="bankReference" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="bouquetCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="bouquetName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="branchId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="collectionTimeOut" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="commission" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="customerId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customerName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dateCreated" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="dayEndId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dayEndSummary" type="{http://process.ewallet.esolutions.co.zw/}dayEndSummary" minOccurs="0"/>
 *         &lt;element name="destinationAccountNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="destinationEQ3AccountNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fromBankId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="merchantRef" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="messageId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="narrative" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nonTellerId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="numberOfMonths" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="oldMessageId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="passcodePrompt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="passwordRetryCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="profileId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="referredCommission" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="referrerCommission" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="responseCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="secretCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sourceAccountNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sourceEQ3AccountNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sourceMobile" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sourceMobileId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="states" type="{http://process.ewallet.esolutions.co.zw/}transactionState" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="status" type="{http://process.ewallet.esolutions.co.zw/}transactionStatus" minOccurs="0"/>
 *         &lt;element name="targetMobile" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="targetMobileId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tariffAmount" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="tariffId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="timedOut" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="timeout" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="toBankId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="toCustomerName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="transactionCharges" type="{http://process.ewallet.esolutions.co.zw/}transactionCharge" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="transactionClass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="transactionLocationId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="transactionLocationType" type="{http://process.ewallet.esolutions.co.zw/}transactionLocationType" minOccurs="0"/>
 *         &lt;element name="transactionType" type="{http://process.ewallet.esolutions.co.zw/}transactionType" minOccurs="0"/>
 *         &lt;element name="transferType" type="{http://process.ewallet.esolutions.co.zw/}transferType" minOccurs="0"/>
 *         &lt;element name="txnSuperType" type="{http://process.ewallet.esolutions.co.zw/}txnSuperType" minOccurs="0"/>
 *         &lt;element name="utilityAccount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="utilityName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="valueDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
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
@XmlType(name = "processTransaction", propOrder = {
    "agentCommissionAmount",
    "agentCommissionId",
    "agentNumber",
    "amount",
    "balance",
    "bankMerchantId",
    "bankReference",
    "bouquetCode",
    "bouquetName",
    "branchId",
    "collectionTimeOut",
    "commission",
    "customerId",
    "customerName",
    "dateCreated",
    "dayEndId",
    "dayEndSummary",
    "destinationAccountNumber",
    "destinationEQ3AccountNumber",
    "fromBankId",
    "id",
    "merchantRef",
    "messageId",
    "narrative",
    "nonTellerId",
    "numberOfMonths",
    "oldMessageId",
    "passcodePrompt",
    "passwordRetryCount",
    "profileId",
    "referredCommission",
    "referrerCommission",
    "responseCode",
    "secretCode",
    "sourceAccountNumber",
    "sourceEQ3AccountNumber",
    "sourceMobile",
    "sourceMobileId",
    "states",
    "status",
    "targetMobile",
    "targetMobileId",
    "tariffAmount",
    "tariffId",
    "timedOut",
    "timeout",
    "toBankId",
    "toCustomerName",
    "transactionCharges",
    "transactionClass",
    "transactionLocationId",
    "transactionLocationType",
    "transactionType",
    "transferType",
    "txnSuperType",
    "utilityAccount",
    "utilityName",
    "valueDate",
    "version"
})
public class ProcessTransaction {

    protected long agentCommissionAmount;
    protected String agentCommissionId;
    protected String agentNumber;
    protected long amount;
    protected long balance;
    protected String bankMerchantId;
    protected String bankReference;
    protected String bouquetCode;
    protected String bouquetName;
    protected String branchId;
    protected boolean collectionTimeOut;
    protected long commission;
    protected String customerId;
    protected String customerName;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateCreated;
    protected String dayEndId;
    protected DayEndSummary dayEndSummary;
    protected String destinationAccountNumber;
    protected String destinationEQ3AccountNumber;
    protected String fromBankId;
    protected String id;
    protected String merchantRef;
    protected String messageId;
    protected String narrative;
    protected String nonTellerId;
    protected int numberOfMonths;
    protected String oldMessageId;
    protected String passcodePrompt;
    protected int passwordRetryCount;
    protected String profileId;
    protected long referredCommission;
    protected long referrerCommission;
    protected String responseCode;
    protected String secretCode;
    protected String sourceAccountNumber;
    protected String sourceEQ3AccountNumber;
    protected String sourceMobile;
    protected String sourceMobileId;
    @XmlElement(nillable = true)
    protected List<TransactionState> states;
    protected TransactionStatus status;
    protected String targetMobile;
    protected String targetMobileId;
    protected long tariffAmount;
    protected String tariffId;
    protected boolean timedOut;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timeout;
    protected String toBankId;
    protected String toCustomerName;
    @XmlElement(nillable = true)
    protected List<TransactionCharge> transactionCharges;
    protected String transactionClass;
    protected String transactionLocationId;
    protected TransactionLocationType transactionLocationType;
    protected TransactionType transactionType;
    protected TransferType transferType;
    protected TxnSuperType txnSuperType;
    protected String utilityAccount;
    protected String utilityName;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar valueDate;
    protected long version;

    /**
     * Gets the value of the agentCommissionAmount property.
     * 
     */
    public long getAgentCommissionAmount() {
        return agentCommissionAmount;
    }

    /**
     * Sets the value of the agentCommissionAmount property.
     * 
     */
    public void setAgentCommissionAmount(long value) {
        this.agentCommissionAmount = value;
    }

    /**
     * Gets the value of the agentCommissionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgentCommissionId() {
        return agentCommissionId;
    }

    /**
     * Sets the value of the agentCommissionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgentCommissionId(String value) {
        this.agentCommissionId = value;
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
     * Gets the value of the balance property.
     * 
     */
    public long getBalance() {
        return balance;
    }

    /**
     * Sets the value of the balance property.
     * 
     */
    public void setBalance(long value) {
        this.balance = value;
    }

    /**
     * Gets the value of the bankMerchantId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBankMerchantId() {
        return bankMerchantId;
    }

    /**
     * Sets the value of the bankMerchantId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBankMerchantId(String value) {
        this.bankMerchantId = value;
    }

    /**
     * Gets the value of the bankReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBankReference() {
        return bankReference;
    }

    /**
     * Sets the value of the bankReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBankReference(String value) {
        this.bankReference = value;
    }

    /**
     * Gets the value of the bouquetCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBouquetCode() {
        return bouquetCode;
    }

    /**
     * Sets the value of the bouquetCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBouquetCode(String value) {
        this.bouquetCode = value;
    }

    /**
     * Gets the value of the bouquetName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBouquetName() {
        return bouquetName;
    }

    /**
     * Sets the value of the bouquetName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBouquetName(String value) {
        this.bouquetName = value;
    }

    /**
     * Gets the value of the branchId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBranchId() {
        return branchId;
    }

    /**
     * Sets the value of the branchId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBranchId(String value) {
        this.branchId = value;
    }

    /**
     * Gets the value of the collectionTimeOut property.
     * 
     */
    public boolean isCollectionTimeOut() {
        return collectionTimeOut;
    }

    /**
     * Sets the value of the collectionTimeOut property.
     * 
     */
    public void setCollectionTimeOut(boolean value) {
        this.collectionTimeOut = value;
    }

    /**
     * Gets the value of the commission property.
     * 
     */
    public long getCommission() {
        return commission;
    }

    /**
     * Sets the value of the commission property.
     * 
     */
    public void setCommission(long value) {
        this.commission = value;
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
     * Gets the value of the customerName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the value of the customerName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerName(String value) {
        this.customerName = value;
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
     * Gets the value of the dayEndId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDayEndId() {
        return dayEndId;
    }

    /**
     * Sets the value of the dayEndId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDayEndId(String value) {
        this.dayEndId = value;
    }

    /**
     * Gets the value of the dayEndSummary property.
     * 
     * @return
     *     possible object is
     *     {@link DayEndSummary }
     *     
     */
    public DayEndSummary getDayEndSummary() {
        return dayEndSummary;
    }

    /**
     * Sets the value of the dayEndSummary property.
     * 
     * @param value
     *     allowed object is
     *     {@link DayEndSummary }
     *     
     */
    public void setDayEndSummary(DayEndSummary value) {
        this.dayEndSummary = value;
    }

    /**
     * Gets the value of the destinationAccountNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestinationAccountNumber() {
        return destinationAccountNumber;
    }

    /**
     * Sets the value of the destinationAccountNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestinationAccountNumber(String value) {
        this.destinationAccountNumber = value;
    }

    /**
     * Gets the value of the destinationEQ3AccountNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestinationEQ3AccountNumber() {
        return destinationEQ3AccountNumber;
    }

    /**
     * Sets the value of the destinationEQ3AccountNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestinationEQ3AccountNumber(String value) {
        this.destinationEQ3AccountNumber = value;
    }

    /**
     * Gets the value of the fromBankId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFromBankId() {
        return fromBankId;
    }

    /**
     * Sets the value of the fromBankId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromBankId(String value) {
        this.fromBankId = value;
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
     * Gets the value of the merchantRef property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMerchantRef() {
        return merchantRef;
    }

    /**
     * Sets the value of the merchantRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMerchantRef(String value) {
        this.merchantRef = value;
    }

    /**
     * Gets the value of the messageId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Sets the value of the messageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageId(String value) {
        this.messageId = value;
    }

    /**
     * Gets the value of the narrative property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNarrative() {
        return narrative;
    }

    /**
     * Sets the value of the narrative property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNarrative(String value) {
        this.narrative = value;
    }

    /**
     * Gets the value of the nonTellerId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNonTellerId() {
        return nonTellerId;
    }

    /**
     * Sets the value of the nonTellerId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNonTellerId(String value) {
        this.nonTellerId = value;
    }

    /**
     * Gets the value of the numberOfMonths property.
     * 
     */
    public int getNumberOfMonths() {
        return numberOfMonths;
    }

    /**
     * Sets the value of the numberOfMonths property.
     * 
     */
    public void setNumberOfMonths(int value) {
        this.numberOfMonths = value;
    }

    /**
     * Gets the value of the oldMessageId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOldMessageId() {
        return oldMessageId;
    }

    /**
     * Sets the value of the oldMessageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOldMessageId(String value) {
        this.oldMessageId = value;
    }

    /**
     * Gets the value of the passcodePrompt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPasscodePrompt() {
        return passcodePrompt;
    }

    /**
     * Sets the value of the passcodePrompt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPasscodePrompt(String value) {
        this.passcodePrompt = value;
    }

    /**
     * Gets the value of the passwordRetryCount property.
     * 
     */
    public int getPasswordRetryCount() {
        return passwordRetryCount;
    }

    /**
     * Sets the value of the passwordRetryCount property.
     * 
     */
    public void setPasswordRetryCount(int value) {
        this.passwordRetryCount = value;
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
     * Gets the value of the referredCommission property.
     * 
     */
    public long getReferredCommission() {
        return referredCommission;
    }

    /**
     * Sets the value of the referredCommission property.
     * 
     */
    public void setReferredCommission(long value) {
        this.referredCommission = value;
    }

    /**
     * Gets the value of the referrerCommission property.
     * 
     */
    public long getReferrerCommission() {
        return referrerCommission;
    }

    /**
     * Sets the value of the referrerCommission property.
     * 
     */
    public void setReferrerCommission(long value) {
        this.referrerCommission = value;
    }

    /**
     * Gets the value of the responseCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponseCode() {
        return responseCode;
    }

    /**
     * Sets the value of the responseCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponseCode(String value) {
        this.responseCode = value;
    }

    /**
     * Gets the value of the secretCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSecretCode() {
        return secretCode;
    }

    /**
     * Sets the value of the secretCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSecretCode(String value) {
        this.secretCode = value;
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
     * Gets the value of the sourceEQ3AccountNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceEQ3AccountNumber() {
        return sourceEQ3AccountNumber;
    }

    /**
     * Sets the value of the sourceEQ3AccountNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceEQ3AccountNumber(String value) {
        this.sourceEQ3AccountNumber = value;
    }

    /**
     * Gets the value of the sourceMobile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceMobile() {
        return sourceMobile;
    }

    /**
     * Sets the value of the sourceMobile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceMobile(String value) {
        this.sourceMobile = value;
    }

    /**
     * Gets the value of the sourceMobileId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceMobileId() {
        return sourceMobileId;
    }

    /**
     * Sets the value of the sourceMobileId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceMobileId(String value) {
        this.sourceMobileId = value;
    }

    /**
     * Gets the value of the states property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the states property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStates().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransactionState }
     * 
     * 
     */
    public List<TransactionState> getStates() {
        if (states == null) {
            states = new ArrayList<TransactionState>();
        }
        return this.states;
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
     * Gets the value of the targetMobile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetMobile() {
        return targetMobile;
    }

    /**
     * Sets the value of the targetMobile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetMobile(String value) {
        this.targetMobile = value;
    }

    /**
     * Gets the value of the targetMobileId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetMobileId() {
        return targetMobileId;
    }

    /**
     * Sets the value of the targetMobileId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetMobileId(String value) {
        this.targetMobileId = value;
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
     * Gets the value of the timedOut property.
     * 
     */
    public boolean isTimedOut() {
        return timedOut;
    }

    /**
     * Sets the value of the timedOut property.
     * 
     */
    public void setTimedOut(boolean value) {
        this.timedOut = value;
    }

    /**
     * Gets the value of the timeout property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTimeout() {
        return timeout;
    }

    /**
     * Sets the value of the timeout property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTimeout(XMLGregorianCalendar value) {
        this.timeout = value;
    }

    /**
     * Gets the value of the toBankId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToBankId() {
        return toBankId;
    }

    /**
     * Sets the value of the toBankId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToBankId(String value) {
        this.toBankId = value;
    }

    /**
     * Gets the value of the toCustomerName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToCustomerName() {
        return toCustomerName;
    }

    /**
     * Sets the value of the toCustomerName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToCustomerName(String value) {
        this.toCustomerName = value;
    }

    /**
     * Gets the value of the transactionCharges property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the transactionCharges property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransactionCharges().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransactionCharge }
     * 
     * 
     */
    public List<TransactionCharge> getTransactionCharges() {
        if (transactionCharges == null) {
            transactionCharges = new ArrayList<TransactionCharge>();
        }
        return this.transactionCharges;
    }

    /**
     * Gets the value of the transactionClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionClass() {
        return transactionClass;
    }

    /**
     * Sets the value of the transactionClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionClass(String value) {
        this.transactionClass = value;
    }

    /**
     * Gets the value of the transactionLocationId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionLocationId() {
        return transactionLocationId;
    }

    /**
     * Sets the value of the transactionLocationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionLocationId(String value) {
        this.transactionLocationId = value;
    }

    /**
     * Gets the value of the transactionLocationType property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionLocationType }
     *     
     */
    public TransactionLocationType getTransactionLocationType() {
        return transactionLocationType;
    }

    /**
     * Sets the value of the transactionLocationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionLocationType }
     *     
     */
    public void setTransactionLocationType(TransactionLocationType value) {
        this.transactionLocationType = value;
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
     * Gets the value of the transferType property.
     * 
     * @return
     *     possible object is
     *     {@link TransferType }
     *     
     */
    public TransferType getTransferType() {
        return transferType;
    }

    /**
     * Sets the value of the transferType property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransferType }
     *     
     */
    public void setTransferType(TransferType value) {
        this.transferType = value;
    }

    /**
     * Gets the value of the txnSuperType property.
     * 
     * @return
     *     possible object is
     *     {@link TxnSuperType }
     *     
     */
    public TxnSuperType getTxnSuperType() {
        return txnSuperType;
    }

    /**
     * Sets the value of the txnSuperType property.
     * 
     * @param value
     *     allowed object is
     *     {@link TxnSuperType }
     *     
     */
    public void setTxnSuperType(TxnSuperType value) {
        this.txnSuperType = value;
    }

    /**
     * Gets the value of the utilityAccount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUtilityAccount() {
        return utilityAccount;
    }

    /**
     * Sets the value of the utilityAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUtilityAccount(String value) {
        this.utilityAccount = value;
    }

    /**
     * Gets the value of the utilityName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUtilityName() {
        return utilityName;
    }

    /**
     * Sets the value of the utilityName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUtilityName(String value) {
        this.utilityName = value;
    }

    /**
     * Gets the value of the valueDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getValueDate() {
        return valueDate;
    }

    /**
     * Sets the value of the valueDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setValueDate(XMLGregorianCalendar value) {
        this.valueDate = value;
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
