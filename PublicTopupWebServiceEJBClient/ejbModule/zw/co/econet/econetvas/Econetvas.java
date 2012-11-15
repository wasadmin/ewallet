//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.econet.econetvas;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

@WebService(name = "econetvas", targetNamespace = "http://econetvas.econet.co.zw/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface Econetvas {


    /**
     * 
     * @param balanceRequest
     * @return
     *     returns zw.co.econet.econetvas.BalanceResponse
     */
    @WebMethod(action = "http://econetvas.econet.co.zw/balanceEnquiry")
    @WebResult(name = "balanceResponse", targetNamespace = "")
    @RequestWrapper(localName = "balanceEnquiry", targetNamespace = "http://econetvas.econet.co.zw/", className = "zw.co.econet.econetvas.BalanceEnquiry")
    @ResponseWrapper(localName = "balanceEnquiryResponse", targetNamespace = "http://econetvas.econet.co.zw/", className = "zw.co.econet.econetvas.BalanceEnquiryResponse")
    public BalanceResponse balanceEnquiry(
        @WebParam(name = "balanceRequest", targetNamespace = "")
        BalanceRequest balanceRequest);

    /**
     * 
     * @param billPayRequest
     * @return
     *     returns zw.co.econet.econetvas.BillPaymentResponse
     */
    @WebMethod(action = "http://econetvas.econet.co.zw/billPay")
    @WebResult(name = "billPayResponse", targetNamespace = "")
    @RequestWrapper(localName = "billPay", targetNamespace = "http://econetvas.econet.co.zw/", className = "zw.co.econet.econetvas.BillPay")
    @ResponseWrapper(localName = "billPayResponse", targetNamespace = "http://econetvas.econet.co.zw/", className = "zw.co.econet.econetvas.BillPayResponse")
    public BillPaymentResponse billPay(
        @WebParam(name = "billPayRequest", targetNamespace = "")
        BillPaymentRequest billPayRequest);

    /**
     * 
     * @param billPayReversalRequest
     * @return
     *     returns zw.co.econet.econetvas.BillPaymentReversalResponse
     */
    @WebMethod(action = "http://econetvas.econet.co.zw/billPayReversal")
    @WebResult(name = "billPayReversalResponse", targetNamespace = "")
    @RequestWrapper(localName = "billPayReversal", targetNamespace = "http://econetvas.econet.co.zw/", className = "zw.co.econet.econetvas.BillPayReversal")
    @ResponseWrapper(localName = "billPayReversalResponse", targetNamespace = "http://econetvas.econet.co.zw/", className = "zw.co.econet.econetvas.BillPayReversalResponse")
    public BillPaymentReversalResponse billPayReversal(
        @WebParam(name = "billPayReversalRequest", targetNamespace = "")
        BillPaymentReversalRequest billPayReversalRequest);

    /**
     * 
     * @param creditSubscriberRequest
     * @return
     *     returns zw.co.econet.econetvas.CreditResponse
     */
    @WebMethod(action = "http://econetvas.econet.co.zw/creditSubscriber")
    @WebResult(name = "creditSubcriberResponse", targetNamespace = "")
    @RequestWrapper(localName = "creditSubscriber", targetNamespace = "http://econetvas.econet.co.zw/", className = "zw.co.econet.econetvas.CreditSubscriber")
    @ResponseWrapper(localName = "creditSubscriberResponse", targetNamespace = "http://econetvas.econet.co.zw/", className = "zw.co.econet.econetvas.CreditSubscriberResponse")
    public CreditResponse creditSubscriber(
        @WebParam(name = "creditSubscriberRequest", targetNamespace = "")
        CreditRequest creditSubscriberRequest);

    /**
     * 
     * @param debitSubscriberRequest
     * @return
     *     returns zw.co.econet.econetvas.DebitResponse
     */
    @WebMethod(action = "http://econetvas.econet.co.zw/debitSubscriber")
    @WebResult(name = "debitSubscriberResponse", targetNamespace = "")
    @RequestWrapper(localName = "debitSubscriber", targetNamespace = "http://econetvas.econet.co.zw/", className = "zw.co.econet.econetvas.DebitSubscriber")
    @ResponseWrapper(localName = "debitSubscriberResponse", targetNamespace = "http://econetvas.econet.co.zw/", className = "zw.co.econet.econetvas.DebitSubscriberResponse")
    public DebitResponse debitSubscriber(
        @WebParam(name = "debitSubscriberRequest", targetNamespace = "")
        DebitRequest debitSubscriberRequest);

    /**
     * 
     * @param txtCreditRequest
     * @return
     *     returns zw.co.econet.econetvas.TextCreditResponse
     */
    @WebMethod(action = "http://econetvas.econet.co.zw/txtCredit")
    @WebResult(name = "txtCreditResponse", targetNamespace = "")
    @RequestWrapper(localName = "txtCredit", targetNamespace = "http://econetvas.econet.co.zw/", className = "zw.co.econet.econetvas.TxtCredit")
    @ResponseWrapper(localName = "txtCreditResponse", targetNamespace = "http://econetvas.econet.co.zw/", className = "zw.co.econet.econetvas.TxtCreditResponse")
    public TextCreditResponse txtCredit(
        @WebParam(name = "txtCreditRequest", targetNamespace = "")
        TextCreditRequest txtCreditRequest);

    /**
     * 
     * @param originalCredit
     * @return
     *     returns zw.co.econet.econetvas.CreditReversalResult
     */
    @WebMethod(action = "http://econetvas.econet.co.zw/creditReversal")
    @WebResult(name = "reversalResult", targetNamespace = "")
    @RequestWrapper(localName = "creditReversal", targetNamespace = "http://econetvas.econet.co.zw/", className = "zw.co.econet.econetvas.CreditReversal")
    @ResponseWrapper(localName = "creditReversalResponse", targetNamespace = "http://econetvas.econet.co.zw/", className = "zw.co.econet.econetvas.CreditReversalResponse")
    public CreditReversalResult creditReversal(
        @WebParam(name = "originalCredit", targetNamespace = "")
        CreditRequest originalCredit);

}