//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.process.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "saveCommissionConfigData", namespace = "http://process.ewallet.esolutions.co.zw/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "saveCommissionConfigData", namespace = "http://process.ewallet.esolutions.co.zw/", propOrder = {
    "email",
    "timeToRun",
    "expPeriod"
})
public class SaveCommissionConfigData {

    @XmlElement(name = "email", namespace = "")
    private String email;
    @XmlElement(name = "timeToRun", namespace = "")
    private String timeToRun;
    @XmlElement(name = "expPeriod", namespace = "")
    private String expPeriod;

    /**
     * 
     * @return
     *     returns String
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * 
     * @param email
     *     the value for the email property
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getTimeToRun() {
        return this.timeToRun;
    }

    /**
     * 
     * @param timeToRun
     *     the value for the timeToRun property
     */
    public void setTimeToRun(String timeToRun) {
        this.timeToRun = timeToRun;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getExpPeriod() {
        return this.expPeriod;
    }

    /**
     * 
     * @param expPeriod
     *     the value for the expPeriod property
     */
    public void setExpPeriod(String expPeriod) {
        this.expPeriod = expPeriod;
    }

}
