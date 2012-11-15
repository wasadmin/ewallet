//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ussd.web.services;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mobileWebTransactionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="mobileWebTransactionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="TOPUP"/>
 *     &lt;enumeration value="BALANCE"/>
 *     &lt;enumeration value="MINI_STATEMENT"/>
 *     &lt;enumeration value="TRANSFER"/>
 *     &lt;enumeration value="BILLPAY"/>
 *     &lt;enumeration value="CHANGE_PASSCODE"/>
 *     &lt;enumeration value="AGENT_CUSTOMER_NON_HOLDER_WITHDRAWAL"/>
 *     &lt;enumeration value="AGENT_CUSTOMER_DEPOSIT"/>
 *     &lt;enumeration value="AGENT_TRANSFER"/>
 *     &lt;enumeration value="AGENT_CUSTOMER_WITHDRAWAL"/>
 *     &lt;enumeration value="RTGS"/>
 *     &lt;enumeration value="AGENT_SUMMARY"/>
 *     &lt;enumeration value="REGISTER_MERCHANT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "mobileWebTransactionType", namespace = "http://services.web.ussd.esolutions.co.zw/")
@XmlEnum
public enum MobileWebTransactionType {

    TOPUP,
    BALANCE,
    MINI_STATEMENT,
    TRANSFER,
    BILLPAY,
    CHANGE_PASSCODE,
    AGENT_CUSTOMER_NON_HOLDER_WITHDRAWAL,
    AGENT_CUSTOMER_DEPOSIT,
    AGENT_TRANSFER,
    AGENT_CUSTOMER_WITHDRAWAL,
    RTGS,
    AGENT_SUMMARY,
    REGISTER_MERCHANT;

    public String value() {
        return name();
    }

    public static MobileWebTransactionType fromValue(String v) {
        return valueOf(v);
    }

}