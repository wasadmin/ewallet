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

@XmlRootElement(name = "checkIfThereAreTrxnTomark", namespace = "http://process.ewallet.esolutions.co.zw/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "checkIfThereAreTrxnTomark", namespace = "http://process.ewallet.esolutions.co.zw/", propOrder = {
    "userName",
    "dayEndDate"
})
public class CheckIfThereAreTrxnTomark {

    @XmlElement(name = "userName", namespace = "")
    private String userName;
    @XmlElement(name = "dayEndDate", namespace = "")
    private Date dayEndDate;

    /**
     * 
     * @return
     *     returns String
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * 
     * @param userName
     *     the value for the userName property
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 
     * @return
     *     returns Date
     */
    public Date getDayEndDate() {
        return this.dayEndDate;
    }

    /**
     * 
     * @param dayEndDate
     *     the value for the dayEndDate property
     */
    public void setDayEndDate(Date dayEndDate) {
        this.dayEndDate = dayEndDate;
    }

}