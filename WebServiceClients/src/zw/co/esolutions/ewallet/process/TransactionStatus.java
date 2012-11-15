//
// Generated By:JAX-WS RI 2.2.4-b01 (JAXB RI IBM 2.2.4)
//


package zw.co.esolutions.ewallet.process;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for transactionStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="transactionStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="VEREQ"/>
 *     &lt;enumeration value="VERES"/>
 *     &lt;enumeration value="PAREQ"/>
 *     &lt;enumeration value="BANK_REQUEST"/>
 *     &lt;enumeration value="DRAFT"/>
 *     &lt;enumeration value="BANK_RESPONSE"/>
 *     &lt;enumeration value="AWAITING_COLLECTION"/>
 *     &lt;enumeration value="CREDIT_REQUEST"/>
 *     &lt;enumeration value="COMPLETED"/>
 *     &lt;enumeration value="FAILED"/>
 *     &lt;enumeration value="AWAITING_APPROVAL"/>
 *     &lt;enumeration value="MANUAL_RESOLVE"/>
 *     &lt;enumeration value="AWAITING_DEBIT_RES"/>
 *     &lt;enumeration value="DISAPPROVED"/>
 *     &lt;enumeration value="AWAITING_COMPLETION_APPROVAL"/>
 *     &lt;enumeration value="AWAITING_FAILURE_APPROVAL"/>
 *     &lt;enumeration value="REVERSED"/>
 *     &lt;enumeration value="REVERSAL_REQUEST"/>
 *     &lt;enumeration value="REVERSAL_RESPONSE"/>
 *     &lt;enumeration value="EXPIRED"/>
 *     &lt;enumeration value="TIMEOUT"/>
 *     &lt;enumeration value="CREDIT_RESPONSE"/>
 *     &lt;enumeration value="ACCOUNT_VALIDATION_RQST"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "transactionStatus")
@XmlEnum
public enum TransactionStatus {

    VEREQ,
    VERES,
    PAREQ,
    BANK_REQUEST,
    DRAFT,
    BANK_RESPONSE,
    AWAITING_COLLECTION,
    CREDIT_REQUEST,
    COMPLETED,
    FAILED,
    AWAITING_APPROVAL,
    MANUAL_RESOLVE,
    AWAITING_DEBIT_RES,
    DISAPPROVED,
    AWAITING_COMPLETION_APPROVAL,
    AWAITING_FAILURE_APPROVAL,
    REVERSED,
    REVERSAL_REQUEST,
    REVERSAL_RESPONSE,
    EXPIRED,
    TIMEOUT,
    CREDIT_RESPONSE,
    ACCOUNT_VALIDATION_RQST;

    public String value() {
        return name();
    }

    public static TransactionStatus fromValue(String v) {
        return valueOf(v);
    }

}
