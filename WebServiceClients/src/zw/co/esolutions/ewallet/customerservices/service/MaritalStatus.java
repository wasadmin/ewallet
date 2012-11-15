//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.customerservices.service;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for maritalStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="maritalStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SINGLE"/>
 *     &lt;enumeration value="MARRIED"/>
 *     &lt;enumeration value="WIDOWED"/>
 *     &lt;enumeration value="UNSPECIFIED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "maritalStatus", namespace = "http://service.customerservices.ewallet.esolutions.co.zw/")
@XmlEnum
public enum MaritalStatus {

    SINGLE,
    MARRIED,
    WIDOWED,
    UNSPECIFIED;

    public String value() {
        return name();
    }

    public static MaritalStatus fromValue(String v) {
        return valueOf(v);
    }

}