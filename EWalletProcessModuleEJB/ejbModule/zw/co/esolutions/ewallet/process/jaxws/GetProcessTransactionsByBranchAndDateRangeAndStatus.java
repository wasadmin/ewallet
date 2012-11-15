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

@XmlRootElement(name = "getProcessTransactionsByBranchAndDateRangeAndStatus", namespace = "http://process.ewallet.esolutions.co.zw/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getProcessTransactionsByBranchAndDateRangeAndStatus", namespace = "http://process.ewallet.esolutions.co.zw/", propOrder = {
    "fromDate",
    "toDate",
    "branch",
    "status"
})
public class GetProcessTransactionsByBranchAndDateRangeAndStatus {

    @XmlElement(name = "fromDate", namespace = "")
    private Date fromDate;
    @XmlElement(name = "toDate", namespace = "")
    private Date toDate;
    @XmlElement(name = "branch", namespace = "")
    private String branch;
    @XmlElement(name = "status", namespace = "")
    private zw.co.esolutions.ewallet.enums.TransactionStatus status;

    /**
     * 
     * @return
     *     returns Date
     */
    public Date getFromDate() {
        return this.fromDate;
    }

    /**
     * 
     * @param fromDate
     *     the value for the fromDate property
     */
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * 
     * @return
     *     returns Date
     */
    public Date getToDate() {
        return this.toDate;
    }

    /**
     * 
     * @param toDate
     *     the value for the toDate property
     */
    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getBranch() {
        return this.branch;
    }

    /**
     * 
     * @param branch
     *     the value for the branch property
     */
    public void setBranch(String branch) {
        this.branch = branch;
    }

    /**
     * 
     * @return
     *     returns TransactionStatus
     */
    public zw.co.esolutions.ewallet.enums.TransactionStatus getStatus() {
        return this.status;
    }

    /**
     * 
     * @param status
     *     the value for the status property
     */
    public void setStatus(zw.co.esolutions.ewallet.enums.TransactionStatus status) {
        this.status = status;
    }

}
