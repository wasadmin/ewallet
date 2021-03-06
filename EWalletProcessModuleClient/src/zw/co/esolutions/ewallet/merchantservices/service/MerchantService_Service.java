//
// Generated By:JAX-WS RI IBM 2.1.6 in JDK 6 (JAXB RI IBM JAXB 2.1.10 in JDK 6)
//


package zw.co.esolutions.ewallet.merchantservices.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

@WebServiceClient(name = "MerchantService", targetNamespace = "http://service.merchantservices.ewallet.esolutions.co.zw/", wsdlLocation = "META-INF/wsdl/MerchantService.wsdl")
public class MerchantService_Service
    extends Service
{

    private final static URL MERCHANTSERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(zw.co.esolutions.ewallet.merchantservices.service.MerchantService_Service.class.getName());

    static {
        URL url = null;
        try {
            url = zw.co.esolutions.ewallet.merchantservices.service.MerchantService_Service.class.getResource("/META-INF/wsdl/MerchantService.wsdl");
            if (url == null) throw new MalformedURLException("/META-INF/wsdl/MerchantService.wsdl does not exist in the module.");
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'META-INF/wsdl/MerchantService.wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        MERCHANTSERVICE_WSDL_LOCATION = url;
    }

    public MerchantService_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public MerchantService_Service() {
        super(MERCHANTSERVICE_WSDL_LOCATION, new QName("http://service.merchantservices.ewallet.esolutions.co.zw/", "MerchantService"));
    }

    /**
     * 
     * @return
     *     returns MerchantService
     */
    @WebEndpoint(name = "MerchantServiceSOAP")
    public MerchantService getMerchantServiceSOAP() {
        return super.getPort(new QName("http://service.merchantservices.ewallet.esolutions.co.zw/", "MerchantServiceSOAP"), MerchantService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns MerchantService
     */
    @WebEndpoint(name = "MerchantServiceSOAP")
    public MerchantService getMerchantServiceSOAP(WebServiceFeature... features) {
        return super.getPort(new QName("http://service.merchantservices.ewallet.esolutions.co.zw/", "MerchantServiceSOAP"), MerchantService.class, features);
    }

}
