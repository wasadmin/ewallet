//
// Generated By:JAX-WS RI IBM 2.1.6 in JDK 6 (JAXB RI IBM JAXB 2.1.10 in JDK 6)
//


package zw.co.esolutions.ewallet.merchantservices.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for deleteBankMerchant complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="deleteBankMerchant">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="bankMerchant" type="{http://service.merchantservices.ewallet.esolutions.co.zw/}bankMerchant" minOccurs="0"/>
 *         &lt;element name="userName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "deleteBankMerchant", namespace = "http://service.merchantservices.ewallet.esolutions.co.zw/", propOrder = {
    "bankMerchant",
    "userName"
})
public class DeleteBankMerchant {

    protected BankMerchant bankMerchant;
    protected String userName;

    /**
     * Gets the value of the bankMerchant property.
     * 
     * @return
     *     possible object is
     *     {@link BankMerchant }
     *     
     */
    public BankMerchant getBankMerchant() {
        return bankMerchant;
    }

    /**
     * Sets the value of the bankMerchant property.
     * 
     * @param value
     *     allowed object is
     *     {@link BankMerchant }
     *     
     */
    public void setBankMerchant(BankMerchant value) {
        this.bankMerchant = value;
    }

    /**
     * Gets the value of the userName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the value of the userName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserName(String value) {
        this.userName = value;
    }

}
