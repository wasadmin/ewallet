//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vIBM 2.2.3-05/12/2011 11:54 AM(foreman)- 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.04.13 at 02:21:41 PM CAT 
//


package zw.co.esolutions.mcommerce.xml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for isoFieldType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="isoFieldType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="String"/>
 *     &lt;enumeration value="Number"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "isoFieldType")
@XmlEnum
public enum IsoFieldType {

    @XmlEnumValue("String")
    STRING("String"),
    @XmlEnumValue("Number")
    NUMBER("Number");
    private final String value;

    IsoFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static IsoFieldType fromValue(String v) {
        for (IsoFieldType c: IsoFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
