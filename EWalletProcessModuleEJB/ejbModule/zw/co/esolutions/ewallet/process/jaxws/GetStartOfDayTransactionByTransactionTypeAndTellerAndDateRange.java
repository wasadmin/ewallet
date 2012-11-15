//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.process.jaxws;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "getStartOfDayTransactionByTransactionTypeAndTellerAndDateRange", namespace = "http://process.ewallet.esolutions.co.zw/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getStartOfDayTransactionByTransactionTypeAndTellerAndDateRange", namespace = "http://process.ewallet.esolutions.co.zw/", propOrder = {
    "transactionType",
    "tellerId",
    "startDate",
    "endDate",
    "txnStatus"
})
public class GetStartOfDayTransactionByTransactionTypeAndTellerAndDateRange {

    @XmlElement(name = "transactionType", namespace = "")
    private zw.co.esolutions.ewallet.enums.TransactionType transactionType;
    @XmlElement(name = "tellerId", namespace = "")
    private String tellerId;
    @XmlElement(name = "startDate", namespace = "")
    private Date startDate;
    @XmlElement(name = "endDate", namespace = "")
    private Date endDate;
    @XmlElement(name = "txnStatus", namespace = "")
    private zw.co.esolutions.ewallet.enums.TransactionStatus txnStatus;

    /**
     * 
     * @return
     *     returns TransactionType
     */
    public zw.co.esolutions.ewallet.enums.TransactionType getTransactionType() {
        return this.transactionType;
    }

    /**
     * 
     * @param transactionType
     *     the value for the transactionType property
     */
    public void setTransactionType(zw.co.esolutions.ewallet.enums.TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getTellerId() {
        return this.tellerId;
    }

    /**
     * 
     * @param tellerId
     *     the value for the tellerId property
     */
    public void setTellerId(String tellerId) {
        this.tellerId = tellerId;
    }

    /**
     * 
     * @return
     *     returns Date
     */
    public Date getStartDate() {
        return this.startDate;
    }

    /**
     * 
     * @param startDate
     *     the value for the startDate property
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * 
     * @return
     *     returns Date
     */
    public Date getEndDate() {
        return this.endDate;
    }

    /**
     * 
     * @param endDate
     *     the value for the endDate property
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * 
     * @return
     *     returns TransactionStatus
     */
    public zw.co.esolutions.ewallet.enums.TransactionStatus getTxnStatus() {
        return this.txnStatus;
    }

    /**
     * 
     * @param txnStatus
     *     the value for the txnStatus property
     */
    public void setTxnStatus(zw.co.esolutions.ewallet.enums.TransactionStatus txnStatus) {
        this.txnStatus = txnStatus;
    }

}