//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.bankservices.service.jaxws;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "getBankBranchByStatusResponse", namespace = "http://service.bankservices.ewallet.esolutions.co.zw/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getBankBranchByStatusResponse", namespace = "http://service.bankservices.ewallet.esolutions.co.zw/")
public class GetBankBranchByStatusResponse {

    @XmlElement(name = "return", namespace = "")
    private List<zw.co.esolutions.ewallet.bankservices.model.BankBranch> _return;

    /**
     * 
     * @return
     *     returns List<BankBranch>
     */
    public List<zw.co.esolutions.ewallet.bankservices.model.BankBranch> getReturn() {
        return this._return;
    }

    /**
     * 
     * @param _return
     *     the value for the _return property
     */
    public void setReturn(List<zw.co.esolutions.ewallet.bankservices.model.BankBranch> _return) {
        this._return = _return;
    }

}
