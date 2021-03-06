//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.topup.ws.impl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the zw.co.esolutions.topup.ws.impl package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ProcessRequestResponse_QNAME = new QName("http://impl.ws.topup.esolutions.co.zw/", "processRequestResponse");
    private final static QName _ProcessRequest_QNAME = new QName("http://impl.ws.topup.esolutions.co.zw/", "processRequest");
    private final static QName _ProcessReversal_QNAME = new QName("http://impl.ws.topup.esolutions.co.zw/", "processReversal");
    private final static QName _ProcessReversalResponse_QNAME = new QName("http://impl.ws.topup.esolutions.co.zw/", "processReversalResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: zw.co.esolutions.topup.ws.impl
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ProcessRequest }
     * 
     */
    public ProcessRequest createProcessRequest() {
        return new ProcessRequest();
    }

    /**
     * Create an instance of {@link ReversalRequest }
     * 
     */
    public ReversalRequest createReversalRequest() {
        return new ReversalRequest();
    }

    /**
     * Create an instance of {@link ProcessReversal }
     * 
     */
    public ProcessReversal createProcessReversal() {
        return new ProcessReversal();
    }

    /**
     * Create an instance of {@link WsResponse }
     * 
     */
    public WsResponse createWsResponse() {
        return new WsResponse();
    }

    /**
     * Create an instance of {@link ProcessReversalResponse }
     * 
     */
    public ProcessReversalResponse createProcessReversalResponse() {
        return new ProcessReversalResponse();
    }

    /**
     * Create an instance of {@link WsRequest }
     * 
     */
    public WsRequest createWsRequest() {
        return new WsRequest();
    }

    /**
     * Create an instance of {@link ReversalResponse }
     * 
     */
    public ReversalResponse createReversalResponse() {
        return new ReversalResponse();
    }

    /**
     * Create an instance of {@link ProcessRequestResponse }
     * 
     */
    public ProcessRequestResponse createProcessRequestResponse() {
        return new ProcessRequestResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessRequestResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.ws.topup.esolutions.co.zw/", name = "processRequestResponse")
    public JAXBElement<ProcessRequestResponse> createProcessRequestResponse(ProcessRequestResponse value) {
        return new JAXBElement<ProcessRequestResponse>(_ProcessRequestResponse_QNAME, ProcessRequestResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.ws.topup.esolutions.co.zw/", name = "processRequest")
    public JAXBElement<ProcessRequest> createProcessRequest(ProcessRequest value) {
        return new JAXBElement<ProcessRequest>(_ProcessRequest_QNAME, ProcessRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessReversal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.ws.topup.esolutions.co.zw/", name = "processReversal")
    public JAXBElement<ProcessReversal> createProcessReversal(ProcessReversal value) {
        return new JAXBElement<ProcessReversal>(_ProcessReversal_QNAME, ProcessReversal.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessReversalResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.ws.topup.esolutions.co.zw/", name = "processReversalResponse")
    public JAXBElement<ProcessReversalResponse> createProcessReversalResponse(ProcessReversalResponse value) {
        return new JAXBElement<ProcessReversalResponse>(_ProcessReversalResponse_QNAME, ProcessReversalResponse.class, null, value);
    }

}
