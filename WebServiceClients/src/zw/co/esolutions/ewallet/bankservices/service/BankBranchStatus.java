//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.bankservices.service;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for bankBranchStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="bankBranchStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ACTIVE"/>
 *     &lt;enumeration value="DISABLED"/>
 *     &lt;enumeration value="AWAITING_APPROVAL"/>
 *     &lt;enumeration value="DISAPPROVED"/>
 *     &lt;enumeration value="INACTIVE"/>
 *     &lt;enumeration value="DELETED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "bankBranchStatus", namespace = "http://service.bankservices.ewallet.esolutions.co.zw/")
@XmlEnum
public enum BankBranchStatus {

    ACTIVE,
    DISABLED,
    AWAITING_APPROVAL,
    DISAPPROVED,
    INACTIVE,
    DELETED;

    public String value() {
        return name();
    }

    public static BankBranchStatus fromValue(String v) {
        return valueOf(v);
    }

}
