//
// Generated By:JAX-WS RI 2.2.4-b01 (JAXB RI IBM 2.2.4)
//


package zw.co.esolutions.ewallet.process;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mobileNetworkOperator.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="mobileNetworkOperator">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ECONET"/>
 *     &lt;enumeration value="NETONE"/>
 *     &lt;enumeration value="TELECEL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "mobileNetworkOperator")
@XmlEnum
public enum MobileNetworkOperator {

    ECONET,
    NETONE,
    TELECEL;

    public String value() {
        return name();
    }

    public static MobileNetworkOperator fromValue(String v) {
        return valueOf(v);
    }

}
