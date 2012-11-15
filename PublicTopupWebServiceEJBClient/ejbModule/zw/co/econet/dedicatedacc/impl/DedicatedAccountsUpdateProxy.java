package zw.co.econet.dedicatedacc.impl;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

public class DedicatedAccountsUpdateProxy{

    protected Descriptor _descriptor;

    public class Descriptor {
        private zw.co.econet.dedicatedacc.impl.DedicatedAccountsUpdateService _service = null;
        private zw.co.econet.dedicatedacc.impl.DedicatedAccountsService _proxy = null;
        private Dispatch<Source> _dispatch = null;

        public Descriptor() {
            _service = new zw.co.econet.dedicatedacc.impl.DedicatedAccountsUpdateService();
            initCommon();
        }

        public Descriptor(URL wsdlLocation, QName serviceName) {
            _service = new zw.co.econet.dedicatedacc.impl.DedicatedAccountsUpdateService(wsdlLocation, serviceName);
            initCommon();
        }

        private void initCommon() {
            _proxy = _service.getDedicatedAccountsUpdate();
        }

        public zw.co.econet.dedicatedacc.impl.DedicatedAccountsService getProxy() {
            return _proxy;
        }

        public Dispatch<Source> getDispatch() {
            if(_dispatch == null ) {
                QName portQName = new QName("http://impl.dedicatedacc.econet.co.zw/", "DedicatedAccountsUpdate");
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

    public DedicatedAccountsUpdateProxy() {
        _descriptor = new Descriptor();
    }

    public DedicatedAccountsUpdateProxy(URL wsdlLocation, QName serviceName) {
        _descriptor = new Descriptor(wsdlLocation, serviceName);
    }

    public Descriptor _getDescriptor() {
        return _descriptor;
    }

    public PlatformInfo getPlatFormInfo(String arg0) {
        return _getDescriptor().getProxy().getPlatFormInfo(arg0);
    }

    public int getDedicatedAccountBalance(QueryDABalance arg0) {
        return _getDescriptor().getProxy().getDedicatedAccountBalance(arg0);
    }

    public ResultWrapper voidUpdateDA(UpdateDARequest arg0) {
        return _getDescriptor().getProxy().voidUpdateDA(arg0);
    }

    public ResultWrapper reverseUpdateDA(UpdateDARequest arg0) {
        return _getDescriptor().getProxy().reverseUpdateDA(arg0);
    }

    public boolean updateDA(UpdateDARequest arg0) {
        return _getDescriptor().getProxy().updateDA(arg0);
    }

}