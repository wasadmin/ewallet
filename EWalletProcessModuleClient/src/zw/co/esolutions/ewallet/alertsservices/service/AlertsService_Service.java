//
// Generated By:JAX-WS RI 2.2.4-b01 (JAXB RI IBM 2.2.4)
//


package zw.co.esolutions.ewallet.alertsservices.service;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

@WebServiceClient(name = "AlertsService", targetNamespace = "http://service.alertsservices.ewallet.esolutions.co.zw/", wsdlLocation = "META-INF/wsdl/AlertsService.wsdl")
public class AlertsService_Service
    extends Service
{

    private final static URL ALERTSSERVICE_WSDL_LOCATION;
    private final static WebServiceException ALERTSSERVICE_EXCEPTION;
    private final static QName ALERTSSERVICE_QNAME = new QName("http://service.alertsservices.ewallet.esolutions.co.zw/", "AlertsService");

    static {
        ALERTSSERVICE_WSDL_LOCATION = zw.co.esolutions.ewallet.alertsservices.service.AlertsService_Service.class.getClassLoader().getResource("META-INF/wsdl/AlertsService.wsdl");
        WebServiceException e = null;
        if (ALERTSSERVICE_WSDL_LOCATION == null) {
            e = new WebServiceException("Cannot find 'META-INF/wsdl/AlertsService.wsdl' wsdl. Place the resource correctly in the classpath.");
        }
        ALERTSSERVICE_EXCEPTION = e;
    }

    public AlertsService_Service() {
        super(__getWsdlLocation(), ALERTSSERVICE_QNAME);
    }

    public AlertsService_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    /**
     * 
     * @return
     *     returns AlertsService
     */
    @WebEndpoint(name = "AlertsServiceSOAP")
    public AlertsService getAlertsServiceSOAP() {
        return super.getPort(new QName("http://service.alertsservices.ewallet.esolutions.co.zw/", "AlertsServiceSOAP"), AlertsService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns AlertsService
     */
    @WebEndpoint(name = "AlertsServiceSOAP")
    public AlertsService getAlertsServiceSOAP(WebServiceFeature... features) {
        return super.getPort(new QName("http://service.alertsservices.ewallet.esolutions.co.zw/", "AlertsServiceSOAP"), AlertsService.class, features);
    }

    private static URL __getWsdlLocation() {
        if (ALERTSSERVICE_EXCEPTION!= null) {
            throw ALERTSSERVICE_EXCEPTION;
        }
        return ALERTSSERVICE_WSDL_LOCATION;
    }

}
