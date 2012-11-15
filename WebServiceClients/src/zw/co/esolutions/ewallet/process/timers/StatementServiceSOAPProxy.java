package zw.co.esolutions.ewallet.process.timers;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

public class StatementServiceSOAPProxy{

    protected Descriptor _descriptor;

    public class Descriptor {
        private zw.co.esolutions.ewallet.process.timers.StatementService_Service _service = null;
        private zw.co.esolutions.ewallet.process.timers.StatementService _proxy = null;
        private Dispatch<Source> _dispatch = null;

        public Descriptor() {
            init();
        }

        public Descriptor(URL wsdlLocation, QName serviceName) {
            _service = new zw.co.esolutions.ewallet.process.timers.StatementService_Service(wsdlLocation, serviceName);
            initCommon();
        }

        public void init() {
            _service = null;
            _proxy = null;
            _dispatch = null;
            _service = new zw.co.esolutions.ewallet.process.timers.StatementService_Service();
            initCommon();
        }

        private void initCommon() {
            _proxy = _service.getStatementServiceSOAP();
        }

        public zw.co.esolutions.ewallet.process.timers.StatementService getProxy() {
            return _proxy;
        }

        public Dispatch<Source> getDispatch() {
            if (_dispatch == null ) {
                QName portQName = new QName("http://timers.process.ewallet.esolutions.co.zw/", "StatementServiceSOAP");
                _dispatch = _service.createDispatch(portQName, Source.class, Service.Mode.MESSAGE);

                String proxyEndpointUrl = getEndpoint();
                BindingProvider bp = (BindingProvider) _dispatch;
                String dispatchEndpointUrl = (String) bp.getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
                if (!dispatchEndpointUrl.equals(proxyEndpointUrl))
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

            if (_dispatch != null ) {
                bp = (BindingProvider) _dispatch;
                bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);
            }
        }

        public void setMTOMEnabled(boolean enable) {
            SOAPBinding binding = (SOAPBinding) ((BindingProvider) _proxy).getBinding();
            binding.setMTOMEnabled(enable);
        }
    }

    public StatementServiceSOAPProxy() {
        _descriptor = new Descriptor();
        _descriptor.setMTOMEnabled(true);
    }

    public StatementServiceSOAPProxy(URL wsdlLocation, QName serviceName) {
        _descriptor = new Descriptor(wsdlLocation, serviceName);
        _descriptor.setMTOMEnabled(true);
    }

    public Descriptor _getDescriptor() {
        return _descriptor;
    }

    public boolean checkTimer(String description) {
        return _getDescriptor().getProxy().checkTimer(description);
    }

}