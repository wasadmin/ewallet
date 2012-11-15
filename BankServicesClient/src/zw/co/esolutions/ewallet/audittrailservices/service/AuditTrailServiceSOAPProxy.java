package zw.co.esolutions.ewallet.audittrailservices.service;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;

public class AuditTrailServiceSOAPProxy{

    protected Descriptor _descriptor;

    public class Descriptor {
        private zw.co.esolutions.ewallet.audittrailservices.service.AuditTrailService_Service _service = null;
        private zw.co.esolutions.ewallet.audittrailservices.service.AuditTrailService _proxy = null;
        private Dispatch<Source> _dispatch = null;

        public Descriptor() {
            _service = new zw.co.esolutions.ewallet.audittrailservices.service.AuditTrailService_Service();
            initCommon();
        }

        public Descriptor(URL wsdlLocation, QName serviceName) {
            _service = new zw.co.esolutions.ewallet.audittrailservices.service.AuditTrailService_Service(wsdlLocation, serviceName);
            initCommon();
        }

        private void initCommon() {
            _proxy = _service.getAuditTrailServiceSOAP();
        }

        public zw.co.esolutions.ewallet.audittrailservices.service.AuditTrailService getProxy() {
            return _proxy;
        }

        public Dispatch<Source> getDispatch() {
            if(_dispatch == null ) {
                QName portQName = new QName("http://service.audittrailservices.ewallet.esolutions.co.zw/", "AuditTrailServiceSOAP");
                _dispatch = _service.createDispatch(portQName, Source.class, Service.Mode.MESSAGE);

                String proxyEndpointUrl = getEndpoint();
                BindingProvider bp = (BindingProvider) _dispatch;
                String dispatchEndpointUrl = (String) bp.getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
                if(!dispatchEndpointUrl.equals(proxyEndpointUrl))
                    bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, proxyEndpointUrl);
            }
            return _dispatch;
        }

        public String getEndpoint() {
            BindingProvider bp = (BindingProvider) _proxy;
            return (String) bp.getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
        }

        public void setEndpoint(String endpointUrl) {
            BindingProvider bp = (BindingProvider) _proxy;
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);

            if(_dispatch != null ) {
            bp = (BindingProvider) _dispatch;
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);
            }
        }
    }

    public AuditTrailServiceSOAPProxy() {
        _descriptor = new Descriptor();
    }

    public AuditTrailServiceSOAPProxy(URL wsdlLocation, QName serviceName) {
        _descriptor = new Descriptor(wsdlLocation, serviceName);
    }

    public Descriptor _getDescriptor() {
        return _descriptor;
    }

    public AuditTrail logActivity(String username, String activityName, String entityId, String entityName, String oldObject, String newObject, String instanceName) throws Exception_Exception {
        return _getDescriptor().getProxy().logActivity(username,activityName,entityId,entityName,oldObject,newObject,instanceName);
    }

    public AuditTrail logActivityWithNarrative(String username, String activityName, String narrative) throws Exception_Exception {
        return _getDescriptor().getProxy().logActivityWithNarrative(username,activityName,narrative);
    }

    public List<AuditTrail> getByEntityNameAndInstanceName(String entityName, String instanceName) throws Exception_Exception {
        return _getDescriptor().getProxy().getByEntityNameAndInstanceName(entityName,instanceName);
    }

    public List<AuditTrail> getByEntityNameAndEntityId(String entityName, String entityId) throws Exception_Exception {
        return _getDescriptor().getProxy().getByEntityNameAndEntityId(entityName,entityId);
    }

    public List<AuditTrail> getByEntityNameAndInstanceNameAndTimePeriod(String entityName, String instanceName, XMLGregorianCalendar startTime, XMLGregorianCalendar endTime) throws Exception_Exception {
        return _getDescriptor().getProxy().getByEntityNameAndInstanceNameAndTimePeriod(entityName,instanceName,startTime,endTime);
    }

    public List<AuditTrail> getByUsername(String username) throws Exception_Exception {
        return _getDescriptor().getProxy().getByUsername(username);
    }

    public List<AuditTrail> getByUsernameAndTimePeriod(String username, XMLGregorianCalendar startTime, XMLGregorianCalendar endTime) throws Exception_Exception {
        return _getDescriptor().getProxy().getByUsernameAndTimePeriod(username,startTime,endTime);
    }

    public List<AuditTrail> getByActivityAndTimePeriod(String activityId, XMLGregorianCalendar startTime, XMLGregorianCalendar endTime) throws Exception_Exception {
        return _getDescriptor().getProxy().getByActivityAndTimePeriod(activityId,startTime,endTime);
    }

    public List<AuditTrail> getByTimePeriod(XMLGregorianCalendar startTime, XMLGregorianCalendar endTime) throws Exception_Exception {
        return _getDescriptor().getProxy().getByTimePeriod(startTime,endTime);
    }

    public Activity findActivityById(String id) throws Exception_Exception {
        return _getDescriptor().getProxy().findActivityById(id);
    }

    public Activity editActivity(Activity activity, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().editActivity(activity,username);
    }

    public List<Activity> getAllActivities() throws Exception_Exception {
        return _getDescriptor().getProxy().getAllActivities();
    }

}