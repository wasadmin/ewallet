//
// Generated By:JAX-WS RI 2.2.4-b01 (JAXB RI IBM 2.2.4)
//


package zw.co.esolutions.ewallet.process;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for txnFamily.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="txnFamily">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="TRANSFERS"/>
 *     &lt;enumeration value="DEPOSITS"/>
 *     &lt;enumeration value="WITHDRAWALS"/>
 *     &lt;enumeration value="AGENT_CASH_DEPOSIT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "txnFamily")
@XmlEnum
public enum TxnFamily {

    TRANSFERS,
    DEPOSITS,
    WITHDRAWALS,
    AGENT_CASH_DEPOSIT;

    public String value() {
        return name();
    }

    public static TxnFamily fromValue(String v) {
        return valueOf(v);
    }

}