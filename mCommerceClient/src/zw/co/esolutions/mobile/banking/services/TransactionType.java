//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.mobile.banking.services;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for transactionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="transactionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="MINISTATEMENT"/>
 *     &lt;enumeration value="BALANCE"/>
 *     &lt;enumeration value="BILLPAY"/>
 *     &lt;enumeration value="TOPUP"/>
 *     &lt;enumeration value="TRANSFER"/>
 *     &lt;enumeration value="RTGS"/>
 *     &lt;enumeration value="PASSWORD_CHANGE"/>
 *     &lt;enumeration value="MERCHANT_REG"/>
 *     &lt;enumeration value="DEPOSIT"/>
 *     &lt;enumeration value="CASH_WITHDRAWAL"/>
 *     &lt;enumeration value="CASHOUT"/>
 *     &lt;enumeration value="AGENT_SUMMARY"/>
 *     &lt;enumeration value="AGENT_TRANSFER"/>
 *     &lt;enumeration value="TOPUP_TXT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "transactionType", namespace = "http://services.banking.mobile.esolutions.co.zw/")
@XmlEnum
public enum TransactionType {

    MINISTATEMENT,
    BALANCE,
    BILLPAY,
    TOPUP,
    TRANSFER,
    RTGS,
    PASSWORD_CHANGE,
    MERCHANT_REG,
    DEPOSIT,
    CASH_WITHDRAWAL,
    CASHOUT,
    AGENT_SUMMARY,
    AGENT_TRANSFER,
    TOPUP_TXT;

    public String value() {
        return name();
    }

    public static TransactionType fromValue(String v) {
        return valueOf(v);
    }

}