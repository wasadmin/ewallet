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

@XmlRootElement(name = "getDayEndsByDayEndStatusAndDateRangeAndBranch", namespace = "http://process.ewallet.esolutions.co.zw/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getDayEndsByDayEndStatusAndDateRangeAndBranch", namespace = "http://process.ewallet.esolutions.co.zw/", propOrder = {
    "fromDate",
    "toDate",
    "dayEndStatus",
    "branchId"
})
public class GetDayEndsByDayEndStatusAndDateRangeAndBranch {

    @XmlElement(name = "fromDate", namespace = "")
    private Date fromDate;
    @XmlElement(name = "toDate", namespace = "")
    private Date toDate;
    @XmlElement(name = "dayEndStatus", namespace = "")
    private zw.co.esolutions.ewallet.enums.DayEndStatus dayEndStatus;
    @XmlElement(name = "branchId", namespace = "")
    private String branchId;

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
     *     returns DayEndStatus
     */
    public zw.co.esolutions.ewallet.enums.DayEndStatus getDayEndStatus() {
        return this.dayEndStatus;
    }

    /**
     * 
     * @param dayEndStatus
     *     the value for the dayEndStatus property
     */
    public void setDayEndStatus(zw.co.esolutions.ewallet.enums.DayEndStatus dayEndStatus) {
        this.dayEndStatus = dayEndStatus;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getBranchId() {
        return this.branchId;
    }

    /**
     * 
     * @param branchId
     *     the value for the branchId property
     */
    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

}
