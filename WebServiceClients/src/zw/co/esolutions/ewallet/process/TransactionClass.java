//
// Generated By:JAX-WS RI 2.2.4-b01 (JAXB RI IBM 2.2.4)
//


package zw.co.esolutions.ewallet.process;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for transactionClass.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="transactionClass">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="EWALLET"/>
 *     &lt;enumeration value="EQUATION"/>
 *     &lt;enumeration value="BOTH"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "transactionClass")
@XmlEnum
public enum TransactionClass {

    EWALLET,
    EQUATION,
    BOTH;

    public String value() {
        return name();
    }

    public static TransactionClass fromValue(String v) {
        return valueOf(v);
    }

}