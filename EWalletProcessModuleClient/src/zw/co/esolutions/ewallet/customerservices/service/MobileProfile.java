//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.customerservices.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for mobileProfile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mobileProfile">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="active" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="approvable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="bankId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="branchId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="coldable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="customer" type="{http://service.customerservices.ewallet.esolutions.co.zw/}customer" minOccurs="0"/>
 *         &lt;element name="dateCreated" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="hottable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="locked" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="mobileNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mobileProfileEditBranch" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mobileProfileRenderApproval" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="network" type="{http://service.customerservices.ewallet.esolutions.co.zw/}mobileNetworkOperator" minOccurs="0"/>
 *         &lt;element name="passwordRetryCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="pendingApproval" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="primary" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="referralCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="referralProcessed" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="responseCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="secretCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://service.customerservices.ewallet.esolutions.co.zw/}mobileProfileStatus" minOccurs="0"/>
 *         &lt;element name="timeout" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
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
@XmlType(name = "mobileProfile", namespace = "http://service.customerservices.ewallet.esolutions.co.zw/", propOrder = {
    "active",
    "approvable",
    "bankId",
    "branchId",
    "coldable",
    "customer",
    "dateCreated",
    "hottable",
    "id",
    "locked",
    "mobileNumber",
    "mobileProfileEditBranch",
    "mobileProfileRenderApproval",
    "network",
    "passwordRetryCount",
    "pendingApproval",
    "primary",
    "referralCode",
    "referralProcessed",
    "responseCode",
    "secretCode",
    "status",
    "timeout",
    "version"
})
public class MobileProfile {

    protected boolean active;
    protected boolean approvable;
    protected String bankId;
    protected String branchId;
    protected boolean coldable;
    protected Customer customer;
    protected XMLGregorianCalendar dateCreated;
    protected boolean hottable;
    protected String id;
    protected boolean locked;
    protected String mobileNumber;
    protected String mobileProfileEditBranch;
    protected boolean mobileProfileRenderApproval;
    protected MobileNetworkOperator network;
    protected int passwordRetryCount;
    protected boolean pendingApproval;
    protected boolean primary;
    protected String referralCode;
    protected boolean referralProcessed;
    protected String responseCode;
    protected String secretCode;
    protected MobileProfileStatus status;
    protected XMLGregorianCalendar timeout;
    protected long version;

    /**
     * Gets the value of the active property.
     * 
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the value of the active property.
     * 
     */
    public void setActive(boolean value) {
        this.active = value;
    }

    /**
     * Gets the value of the approvable property.
     * 
     */
    public boolean isApprovable() {
        return approvable;
    }

    /**
     * Sets the value of the approvable property.
     * 
     */
    public void setApprovable(boolean value) {
        this.approvable = value;
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
     * Gets the value of the coldable property.
     * 
     */
    public boolean isColdable() {
        return coldable;
    }

    /**
     * Sets the value of the coldable property.
     * 
     */
    public void setColdable(boolean value) {
        this.coldable = value;
    }

    /**
     * Gets the value of the customer property.
     * 
     * @return
     *     possible object is
     *     {@link Customer }
     *     
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Sets the value of the customer property.
     * 
     * @param value
     *     allowed object is
     *     {@link Customer }
     *     
     */
    public void setCustomer(Customer value) {
        this.customer = value;
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
     * Gets the value of the hottable property.
     * 
     */
    public boolean isHottable() {
        return hottable;
    }

    /**
     * Sets the value of the hottable property.
     * 
     */
    public void setHottable(boolean value) {
        this.hottable = value;
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
     * Gets the value of the locked property.
     * 
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Sets the value of the locked property.
     * 
     */
    public void setLocked(boolean value) {
        this.locked = value;
    }

    /**
     * Gets the value of the mobileNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMobileNumber() {
        return mobileNumber;
    }

    /**
     * Sets the value of the mobileNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMobileNumber(String value) {
        this.mobileNumber = value;
    }

    /**
     * Gets the value of the mobileProfileEditBranch property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMobileProfileEditBranch() {
        return mobileProfileEditBranch;
    }

    /**
     * Sets the value of the mobileProfileEditBranch property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMobileProfileEditBranch(String value) {
        this.mobileProfileEditBranch = value;
    }

    /**
     * Gets the value of the mobileProfileRenderApproval property.
     * 
     */
    public boolean isMobileProfileRenderApproval() {
        return mobileProfileRenderApproval;
    }

    /**
     * Sets the value of the mobileProfileRenderApproval property.
     * 
     */
    public void setMobileProfileRenderApproval(boolean value) {
        this.mobileProfileRenderApproval = value;
    }

    /**
     * Gets the value of the network property.
     * 
     * @return
     *     possible object is
     *     {@link MobileNetworkOperator }
     *     
     */
    public MobileNetworkOperator getNetwork() {
        return network;
    }

    /**
     * Sets the value of the network property.
     * 
     * @param value
     *     allowed object is
     *     {@link MobileNetworkOperator }
     *     
     */
    public void setNetwork(MobileNetworkOperator value) {
        this.network = value;
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
     * Gets the value of the pendingApproval property.
     * 
     */
    public boolean isPendingApproval() {
        return pendingApproval;
    }

    /**
     * Sets the value of the pendingApproval property.
     * 
     */
    public void setPendingApproval(boolean value) {
        this.pendingApproval = value;
    }

    /**
     * Gets the value of the primary property.
     * 
     */
    public boolean isPrimary() {
        return primary;
    }

    /**
     * Sets the value of the primary property.
     * 
     */
    public void setPrimary(boolean value) {
        this.primary = value;
    }

    /**
     * Gets the value of the referralCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferralCode() {
        return referralCode;
    }

    /**
     * Sets the value of the referralCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferralCode(String value) {
        this.referralCode = value;
    }

    /**
     * Gets the value of the referralProcessed property.
     * 
     */
    public boolean isReferralProcessed() {
        return referralProcessed;
    }

    /**
     * Sets the value of the referralProcessed property.
     * 
     */
    public void setReferralProcessed(boolean value) {
        this.referralProcessed = value;
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
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link MobileProfileStatus }
     *     
     */
    public MobileProfileStatus getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link MobileProfileStatus }
     *     
     */
    public void setStatus(MobileProfileStatus value) {
        this.status = value;
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