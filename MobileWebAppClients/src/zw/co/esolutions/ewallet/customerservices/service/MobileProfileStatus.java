//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.customerservices.service;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mobileProfileStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="mobileProfileStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="AWAITING_APPROVAL"/>
 *     &lt;enumeration value="ACTIVE"/>
 *     &lt;enumeration value="INACTIVE"/>
 *     &lt;enumeration value="LOCKED"/>
 *     &lt;enumeration value="DISAPPROVED"/>
 *     &lt;enumeration value="DELETED"/>
 *     &lt;enumeration value="HOT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "mobileProfileStatus", namespace = "http://service.customerservices.ewallet.esolutions.co.zw/")
@XmlEnum
public enum MobileProfileStatus {

    AWAITING_APPROVAL,
    ACTIVE,
    INACTIVE,
    LOCKED,
    DISAPPROVED,
    DELETED,
    HOT;

    public String value() {
        return name();
    }

    public static MobileProfileStatus fromValue(String v) {
        return valueOf(v);
    }

}
