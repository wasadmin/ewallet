//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.customerservices.service;

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
@XmlType(name = "mobileNetworkOperator", namespace = "http://service.customerservices.ewallet.esolutions.co.zw/")
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
