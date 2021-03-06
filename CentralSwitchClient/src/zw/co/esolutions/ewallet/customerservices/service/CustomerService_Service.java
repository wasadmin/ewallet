//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.customerservices.service;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

@WebServiceClient(name = "CustomerService", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", wsdlLocation = "http://localhost:9081/CustomerServices/CustomerService/CustomerService.wsdl")
public class CustomerService_Service
    extends Service
{

    private final static URL CUSTOMERSERVICE_WSDL_LOCATION;

    static {
        URL url = null;
        try {
            url = new URL("http://localhost:9081/CustomerServices/CustomerService/CustomerService.wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        CUSTOMERSERVICE_WSDL_LOCATION = url;
    }

    public CustomerService_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public CustomerService_Service() {
        super(CUSTOMERSERVICE_WSDL_LOCATION, new QName("http://service.customerservices.ewallet.esolutions.co.zw/", "CustomerService"));
    }

    /**
     * 
     * @return
     *     returns CustomerService
     */
    @WebEndpoint(name = "CustomerServiceSOAP")
    public CustomerService getCustomerServiceSOAP() {
        return (CustomerService)super.getPort(new QName("http://service.customerservices.ewallet.esolutions.co.zw/", "CustomerServiceSOAP"), CustomerService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns CustomerService
     */
    @WebEndpoint(name = "CustomerServiceSOAP")
    public CustomerService getCustomerServiceSOAP(WebServiceFeature... features) {
        return (CustomerService)super.getPort(new QName("http://service.customerservices.ewallet.esolutions.co.zw/", "CustomerServiceSOAP"), CustomerService.class, features);
    }

}
