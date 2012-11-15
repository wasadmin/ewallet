//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.mobile.banking.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for transactionRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="transactionRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="agentNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="beneficiaryName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="newPassword" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paymentReference" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="secretCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sourceAccountNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="targetAccountNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="targetBankCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="targetCustomerMerchantAccount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="targetMerchant" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="targetMobileNetworkOperator" type="{http://services.banking.mobile.esolutions.co.zw/}mobileNetworkOperator" minOccurs="0"/>
 *         &lt;element name="targetMobileNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="transactionAmount" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="transactionTimestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="transactionType" type="{http://services.banking.mobile.esolutions.co.zw/}transactionType" minOccurs="0"/>
 *         &lt;element name="ussdSessionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transactionRequest", namespace = "http://services.banking.mobile.esolutions.co.zw/", propOrder = {
    "agentNumber",
    "beneficiaryName",
    "newPassword",
    "paymentReference",
    "secretCode",
    "sourceAccountNumber",
    "targetAccountNumber",
    "targetBankCode",
    "targetCustomerMerchantAccount",
    "targetMerchant",
    "targetMobileNetworkOperator",
    "targetMobileNumber",
    "transactionAmount",
    "transactionTimestamp",
    "transactionType",
    "ussdSessionId"
})
public class TransactionRequest {

    protected String agentNumber;
    protected String beneficiaryName;
    protected String newPassword;
    protected String paymentReference;
    protected String secretCode;
    protected String sourceAccountNumber;
    protected String targetAccountNumber;
    protected String targetBankCode;
    protected String targetCustomerMerchantAccount;
    protected String targetMerchant;
    protected MobileNetworkOperator targetMobileNetworkOperator;
    protected String targetMobileNumber;
    protected long transactionAmount;
    protected XMLGregorianCalendar transactionTimestamp;
    protected TransactionType transactionType;
    protected String ussdSessionId;

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
     * Gets the value of the beneficiaryName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    /**
     * Sets the value of the beneficiaryName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBeneficiaryName(String value) {
        this.beneficiaryName = value;
    }

    /**
     * Gets the value of the newPassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * Sets the value of the newPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewPassword(String value) {
        this.newPassword = value;
    }

    /**
     * Gets the value of the paymentReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentReference() {
        return paymentReference;
    }

    /**
     * Sets the value of the paymentReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentReference(String value) {
        this.paymentReference = value;
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
     * Gets the value of the targetBankCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetBankCode() {
        return targetBankCode;
    }

    /**
     * Sets the value of the targetBankCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetBankCode(String value) {
        this.targetBankCode = value;
    }

    /**
     * Gets the value of the targetCustomerMerchantAccount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetCustomerMerchantAccount() {
        return targetCustomerMerchantAccount;
    }

    /**
     * Sets the value of the targetCustomerMerchantAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetCustomerMerchantAccount(String value) {
        this.targetCustomerMerchantAccount = value;
    }

    /**
     * Gets the value of the targetMerchant property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetMerchant() {
        return targetMerchant;
    }

    /**
     * Sets the value of the targetMerchant property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetMerchant(String value) {
        this.targetMerchant = value;
    }

    /**
     * Gets the value of the targetMobileNetworkOperator property.
     * 
     * @return
     *     possible object is
     *     {@link MobileNetworkOperator }
     *     
     */
    public MobileNetworkOperator getTargetMobileNetworkOperator() {
        return targetMobileNetworkOperator;
    }

    /**
     * Sets the value of the targetMobileNetworkOperator property.
     * 
     * @param value
     *     allowed object is
     *     {@link MobileNetworkOperator }
     *     
     */
    public void setTargetMobileNetworkOperator(MobileNetworkOperator value) {
        this.targetMobileNetworkOperator = value;
    }

    /**
     * Gets the value of the targetMobileNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetMobileNumber() {
        return targetMobileNumber;
    }

    /**
     * Sets the value of the targetMobileNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetMobileNumber(String value) {
        this.targetMobileNumber = value;
    }

    /**
     * Gets the value of the transactionAmount property.
     * 
     */
    public long getTransactionAmount() {
        return transactionAmount;
    }

    /**
     * Sets the value of the transactionAmount property.
     * 
     */
    public void setTransactionAmount(long value) {
        this.transactionAmount = value;
    }

    /**
     * Gets the value of the transactionTimestamp property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTransactionTimestamp() {
        return transactionTimestamp;
    }

    /**
     * Sets the value of the transactionTimestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTransactionTimestamp(XMLGregorianCalendar value) {
        this.transactionTimestamp = value;
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
     * Gets the value of the ussdSessionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUssdSessionId() {
        return ussdSessionId;
    }

    /**
     * Sets the value of the ussdSessionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUssdSessionId(String value) {
        this.ussdSessionId = value;
    }

}
