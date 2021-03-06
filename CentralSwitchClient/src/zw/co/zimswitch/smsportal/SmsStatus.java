//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.zimswitch.smsportal;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SmsStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SmsStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Delivered"/>
 *     &lt;enumeration value="Pending"/>
 *     &lt;enumeration value="Failed"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "SmsStatus", namespace = "http://www.zimswitch.co.zw/SmsPortal")
@XmlEnum
public enum SmsStatus {

    @XmlEnumValue("Delivered")
    DELIVERED("Delivered"),
    @XmlEnumValue("Pending")
    PENDING("Pending"),
    @XmlEnumValue("Failed")
    FAILED("Failed");
    private final String value;

    SmsStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SmsStatus fromValue(String v) {
        for (SmsStatus c: SmsStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
