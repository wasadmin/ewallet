//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.process.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "runTellerDayEnd", namespace = "http://process.ewallet.esolutions.co.zw/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "runTellerDayEnd", namespace = "http://process.ewallet.esolutions.co.zw/", propOrder = {
    "dayEnd",
    "userName"
})
public class RunTellerDayEnd {

    @XmlElement(name = "dayEnd", namespace = "")
    private zw.co.esolutions.ewallet.process.model.DayEnd dayEnd;
    @XmlElement(name = "userName", namespace = "")
    private String userName;

    /**
     * 
     * @return
     *     returns DayEnd
     */
    public zw.co.esolutions.ewallet.process.model.DayEnd getDayEnd() {
        return this.dayEnd;
    }

    /**
     * 
     * @param dayEnd
     *     the value for the dayEnd property
     */
    public void setDayEnd(zw.co.esolutions.ewallet.process.model.DayEnd dayEnd) {
        this.dayEnd = dayEnd;
    }

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

}
