//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.bankservices.service.jaxws;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "getTransactionSummaryItem", namespace = "http://service.bankservices.ewallet.esolutions.co.zw/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getTransactionSummaryItem", namespace = "http://service.bankservices.ewallet.esolutions.co.zw/", propOrder = {
    "accountId",
    "txnDate",
    "txnType"
})
public class GetTransactionSummaryItem {

    @XmlElement(name = "accountId", namespace = "")
    private String accountId;
    @XmlElement(name = "txnDate", namespace = "")
    private Date txnDate;
    @XmlElement(name = "txnType", namespace = "")
    private zw.co.esolutions.ewallet.enums.TransactionType txnType;

    /**
     * 
     * @return
     *     returns String
     */
    public String getAccountId() {
        return this.accountId;
    }

    /**
     * 
     * @param accountId
     *     the value for the accountId property
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    /**
     * 
     * @return
     *     returns Date
     */
    public Date getTxnDate() {
        return this.txnDate;
    }

    /**
     * 
     * @param txnDate
     *     the value for the txnDate property
     */
    public void setTxnDate(Date txnDate) {
        this.txnDate = txnDate;
    }

    /**
     * 
     * @return
     *     returns TransactionType
     */
    public zw.co.esolutions.ewallet.enums.TransactionType getTxnType() {
        return this.txnType;
    }

    /**
     * 
     * @param txnType
     *     the value for the txnType property
     */
    public void setTxnType(zw.co.esolutions.ewallet.enums.TransactionType txnType) {
        this.txnType = txnType;
    }

}
