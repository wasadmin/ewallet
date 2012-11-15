//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.referralservices.service;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for referralStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="referralStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NEW"/>
 *     &lt;enumeration value="REGISTERED"/>
 *     &lt;enumeration value="DEBIT_ORDER"/>
 *     &lt;enumeration value="BANK_RESP"/>
 *     &lt;enumeration value="COMMISSION_POSTED"/>
 *     &lt;enumeration value="DELETED"/>
 *     &lt;enumeration value="EXPIRED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "referralStatus", namespace = "http://service.referralservices.ewallet.esolutions.co.zw/")
@XmlEnum
public enum ReferralStatus {

    NEW,
    REGISTERED,
    DEBIT_ORDER,
    BANK_RESP,
    COMMISSION_POSTED,
    DELETED,
    EXPIRED;

    public String value() {
        return name();
    }

    public static ReferralStatus fromValue(String v) {
        return valueOf(v);
    }

}
