package zw.co.esolutions.ewallet.contactdetailsservices.service;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

public class ContactDetailsServiceSOAPProxy{

    protected Descriptor _descriptor;

    public class Descriptor {
        private zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetailsService_Service _service = null;
        private zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetailsService _proxy = null;
        private Dispatch<Source> _dispatch = null;

        public Descriptor() {
            _service = new zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetailsService_Service();
            initCommon();
        }

        public Descriptor(URL wsdlLocation, QName serviceName) {
            _service = new zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetailsService_Service(wsdlLocation, serviceName);
            initCommon();
        }

        private void initCommon() {
            _proxy = _service.getContactDetailsServiceSOAP();
        }

        public zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetailsService getProxy() {
            return _proxy;
        }

        public Dispatch<Source> getDispatch() {
            if(_dispatch == null ) {
                QName portQName = new QName("http://service.contactdetailsservices.ewallet.esolutions.co.zw/", "ContactDetailsServiceSOAP");
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

    public ContactDetailsServiceSOAPProxy() {
        _descriptor = new Descriptor();
    }

    public ContactDetailsServiceSOAPProxy(URL wsdlLocation, QName serviceName) {
        _descriptor = new Descriptor(wsdlLocation, serviceName);
    }

    public Descriptor _getDescriptor() {
        return _descriptor;
    }

    public ContactDetails createContactDetails(ContactDetails contactDetails, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().createContactDetails(contactDetails,userName);
    }

    public String deleteContactDetails(ContactDetails contactDetails) throws Exception_Exception {
        return _getDescriptor().getProxy().deleteContactDetails(contactDetails);
    }

    public ContactDetails editContactDetails(ContactDetails contactDetails, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().editContactDetails(contactDetails,userName);
    }

    public ContactDetails updateContactDetails(ContactDetails contactDetails) throws Exception_Exception {
        return _getDescriptor().getProxy().updateContactDetails(contactDetails);
    }

    public ContactDetails findContactDetailsById(String id) {
        return _getDescriptor().getProxy().findContactDetailsById(id);
    }

}