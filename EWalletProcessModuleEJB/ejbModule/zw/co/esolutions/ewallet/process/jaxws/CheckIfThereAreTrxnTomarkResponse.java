//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.process.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "checkIfThereAreTrxnTomarkResponse", namespace = "http://process.ewallet.esolutions.co.zw/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "checkIfThereAreTrxnTomarkResponse", namespace = "http://process.ewallet.esolutions.co.zw/")
public class CheckIfThereAreTrxnTomarkResponse {

    @XmlElement(name = "return", namespace = "")
    private zw.co.esolutions.ewallet.process.model.DayEndResponse _return;

    /**
     * 
     * @return
     *     returns DayEndResponse
     */
    public zw.co.esolutions.ewallet.process.model.DayEndResponse getReturn() {
        return this._return;
    }

    /**
     * 
     * @param _return
     *     the value for the _return property
     */
    public void setReturn(zw.co.esolutions.ewallet.process.model.DayEndResponse _return) {
        this._return = _return;
    }

}
