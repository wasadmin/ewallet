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

@XmlRootElement(name = "getTellerDayEnd", namespace = "http://process.ewallet.esolutions.co.zw/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getTellerDayEnd", namespace = "http://process.ewallet.esolutions.co.zw/", propOrder = {
    "bankName",
    "profileId",
    "txnDate",
    "msgType"
})
public class GetTellerDayEnd {

    @XmlElement(name = "bankName", namespace = "")
    private String bankName;
    @XmlElement(name = "profileId", namespace = "")
    private String profileId;
    @XmlElement(name = "txnDate", namespace = "")
    private Date txnDate;
    @XmlElement(name = "msgType", namespace = "")
    private zw.co.esolutions.ewallet.enums.TransactionType msgType;

    /**
     * 
     * @return
     *     returns String
     */
    public String getBankName() {
        return this.bankName;
    }

    /**
     * 
     * @param bankName
     *     the value for the bankName property
     */
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getProfileId() {
        return this.profileId;
    }

    /**
     * 
     * @param profileId
     *     the value for the profileId property
     */
    public void setProfileId(String profileId) {
        this.profileId = profileId;
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
    public zw.co.esolutions.ewallet.enums.TransactionType getMsgType() {
        return this.msgType;
    }

    /**
     * 
     * @param msgType
     *     the value for the msgType property
     */
    public void setMsgType(zw.co.esolutions.ewallet.enums.TransactionType msgType) {
        this.msgType = msgType;
    }

}
