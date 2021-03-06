//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.econet.dedicatedacc.impl;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

@WebServiceClient(name = "DedicatedAccountsUpdateService", targetNamespace = "http://impl.dedicatedacc.econet.co.zw/", wsdlLocation = "file:/META-INF/wsdl/DedicatedAccountsUpdateService.wsdl")
public class DedicatedAccountsUpdateService
    extends Service
{

    private final static URL DEDICATEDACCOUNTSUPDATESERVICE_WSDL_LOCATION;

    static {
        URL url = null;
        try {
            url = new URL("file:/META-INF/wsdl/DedicatedAccountsUpdateService.wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        DEDICATEDACCOUNTSUPDATESERVICE_WSDL_LOCATION = url;
    }

    public DedicatedAccountsUpdateService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public DedicatedAccountsUpdateService() {
        super(DEDICATEDACCOUNTSUPDATESERVICE_WSDL_LOCATION, new QName("http://impl.dedicatedacc.econet.co.zw/", "DedicatedAccountsUpdateService"));
    }

    /**
     * 
     * @return
     *     returns DedicatedAccountsService
     */
    @WebEndpoint(name = "DedicatedAccountsUpdate")
    public DedicatedAccountsService getDedicatedAccountsUpdate() {
        return (DedicatedAccountsService)super.getPort(new QName("http://impl.dedicatedacc.econet.co.zw/", "DedicatedAccountsUpdate"), DedicatedAccountsService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns DedicatedAccountsService
     */
    @WebEndpoint(name = "DedicatedAccountsUpdate")
    public DedicatedAccountsService getDedicatedAccountsUpdate(WebServiceFeature... features) {
        return (DedicatedAccountsService)super.getPort(new QName("http://impl.dedicatedacc.econet.co.zw/", "DedicatedAccountsUpdate"), DedicatedAccountsService.class, features);
    }

}
