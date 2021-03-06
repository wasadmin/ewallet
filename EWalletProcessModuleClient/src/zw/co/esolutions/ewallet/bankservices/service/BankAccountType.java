//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.bankservices.service;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for bankAccountType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="bankAccountType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="E_WALLET"/>
 *     &lt;enumeration value="POOL_CONTROL"/>
 *     &lt;enumeration value="TARIFFS_CONTROL"/>
 *     &lt;enumeration value="PAYOUT_CONTROL"/>
 *     &lt;enumeration value="BANK_SUSPENSE"/>
 *     &lt;enumeration value="MERCHANT_SUSPENSE"/>
 *     &lt;enumeration value="SAVINGS"/>
 *     &lt;enumeration value="TOPUP_SUSPENSE_ACCOUNT"/>
 *     &lt;enumeration value="CURRENT"/>
 *     &lt;enumeration value="CHEQUE"/>
 *     &lt;enumeration value="BRANCH_CASH_ACCOUNT"/>
 *     &lt;enumeration value="EWALLET_BRANCH_CASH_ACCOUNT"/>
 *     &lt;enumeration value="EWALLET_BALANCING_SUSPENSE_ACCOUNT"/>
 *     &lt;enumeration value="MOBILE_TO_BANK_CONTROL"/>
 *     &lt;enumeration value="BANK_TO_MOBILE_CONTROL"/>
 *     &lt;enumeration value="BANK_TO_NON_MOBILE_CONTROL"/>
 *     &lt;enumeration value="SUSPENSE"/>
 *     &lt;enumeration value="AGENT_EWALLET"/>
 *     &lt;enumeration value="AGENT_COMMISSION_SUSPENSE"/>
 *     &lt;enumeration value="AGENT_COMMISSION_SOURCE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "bankAccountType", namespace = "http://service.bankservices.ewallet.esolutions.co.zw/")
@XmlEnum
public enum BankAccountType {

    E_WALLET,
    POOL_CONTROL,
    TARIFFS_CONTROL,
    PAYOUT_CONTROL,
    BANK_SUSPENSE,
    MERCHANT_SUSPENSE,
    SAVINGS,
    TOPUP_SUSPENSE_ACCOUNT,
    CURRENT,
    CHEQUE,
    BRANCH_CASH_ACCOUNT,
    EWALLET_BRANCH_CASH_ACCOUNT,
    EWALLET_BALANCING_SUSPENSE_ACCOUNT,
    MOBILE_TO_BANK_CONTROL,
    BANK_TO_MOBILE_CONTROL,
    BANK_TO_NON_MOBILE_CONTROL,
    SUSPENSE,
    AGENT_EWALLET,
    AGENT_COMMISSION_SUSPENSE,
    AGENT_COMMISSION_SOURCE;

    public String value() {
        return name();
    }

    public static BankAccountType fromValue(String v) {
        return valueOf(v);
    }

}
