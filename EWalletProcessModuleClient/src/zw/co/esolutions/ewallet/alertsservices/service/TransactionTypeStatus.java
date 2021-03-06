//
// Generated By:JAX-WS RI 2.2.4-b01 (JAXB RI IBM 2.2.4)
//


package zw.co.esolutions.ewallet.alertsservices.service;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for transactionTypeStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="transactionTypeStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ENABLED"/>
 *     &lt;enumeration value="DISABLED"/>
 *     &lt;enumeration value="AWAITING_APPROVAL"/>
 *     &lt;enumeration value="DISAPPROVED"/>
 *     &lt;enumeration value="DELETED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "transactionTypeStatus")
@XmlEnum
public enum TransactionTypeStatus {

    ENABLED,
    DISABLED,
    AWAITING_APPROVAL,
    DISAPPROVED,
    DELETED;

    public String value() {
        return name();
    }

    public static TransactionTypeStatus fromValue(String v) {
        return valueOf(v);
    }

}
