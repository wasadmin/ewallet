//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.process.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "getProcessTransactionsByBranch", namespace = "http://process.ewallet.esolutions.co.zw/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getProcessTransactionsByBranch", namespace = "http://process.ewallet.esolutions.co.zw/")
public class GetProcessTransactionsByBranch {

    @XmlElement(name = "branch", namespace = "")
    private String branch;

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

}
