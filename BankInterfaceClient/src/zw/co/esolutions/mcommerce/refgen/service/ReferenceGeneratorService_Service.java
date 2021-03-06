//
// Generated By:JAX-WS RI IBM 2.1.6 in JDK 6 (JAXB RI IBM JAXB 2.1.10 in JDK 6)
//


package zw.co.esolutions.mcommerce.refgen.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

@WebServiceClient(name = "ReferenceGeneratorService", targetNamespace = "http://service.refgen.mcommerce.esolutions.co.zw/", wsdlLocation = "META-INF/wsdl/ReferenceGeneratorService.wsdl")
public class ReferenceGeneratorService_Service
    extends Service
{

    private final static URL REFERENCEGENERATORSERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(zw.co.esolutions.mcommerce.refgen.service.ReferenceGeneratorService_Service.class.getName());

    static {
        URL url = null;
        try {
            url = zw.co.esolutions.mcommerce.refgen.service.ReferenceGeneratorService_Service.class.getResource("/META-INF/wsdl/ReferenceGeneratorService.wsdl");
            if (url == null) throw new MalformedURLException("/META-INF/wsdl/ReferenceGeneratorService.wsdl does not exist in the module.");
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'META-INF/wsdl/ReferenceGeneratorService.wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        REFERENCEGENERATORSERVICE_WSDL_LOCATION = url;
    }

    public ReferenceGeneratorService_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ReferenceGeneratorService_Service() {
        super(REFERENCEGENERATORSERVICE_WSDL_LOCATION, new QName("http://service.refgen.mcommerce.esolutions.co.zw/", "ReferenceGeneratorService"));
    }

    /**
     * 
     * @return
     *     returns ReferenceGeneratorService
     */
    @WebEndpoint(name = "ReferenceGeneratorServiceSOAP")
    public ReferenceGeneratorService getReferenceGeneratorServiceSOAP() {
        return super.getPort(new QName("http://service.refgen.mcommerce.esolutions.co.zw/", "ReferenceGeneratorServiceSOAP"), ReferenceGeneratorService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ReferenceGeneratorService
     */
    @WebEndpoint(name = "ReferenceGeneratorServiceSOAP")
    public ReferenceGeneratorService getReferenceGeneratorServiceSOAP(WebServiceFeature... features) {
        return super.getPort(new QName("http://service.refgen.mcommerce.esolutions.co.zw/", "ReferenceGeneratorServiceSOAP"), ReferenceGeneratorService.class, features);
    }

}
