package zw.co.esolutions.centralswitch.config.service;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import java.util.List;

public class SwitchConfigurationServiceSOAPProxy{

    protected Descriptor _descriptor;

    public class Descriptor {
        private zw.co.esolutions.centralswitch.config.service.SwitchConfigurationService_Service _service = null;
        private zw.co.esolutions.centralswitch.config.service.SwitchConfigurationService _proxy = null;
        private Dispatch<Source> _dispatch = null;

        public Descriptor() {
            _service = new zw.co.esolutions.centralswitch.config.service.SwitchConfigurationService_Service();
            initCommon();
        }

        public Descriptor(URL wsdlLocation, QName serviceName) {
            _service = new zw.co.esolutions.centralswitch.config.service.SwitchConfigurationService_Service(wsdlLocation, serviceName);
            initCommon();
        }

        private void initCommon() {
            _proxy = _service.getSwitchConfigurationServiceSOAP();
        }

        public zw.co.esolutions.centralswitch.config.service.SwitchConfigurationService getProxy() {
            return _proxy;
        }

        public Dispatch<Source> getDispatch() {
            if(_dispatch == null ) {
                QName portQName = new QName("http://service.config.centralswitch.esolutions.co.zw/", "SwitchConfigurationServiceSOAP");
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

    public SwitchConfigurationServiceSOAPProxy() {
        _descriptor = new Descriptor();
    }

    public SwitchConfigurationServiceSOAPProxy(URL wsdlLocation, QName serviceName) {
        _descriptor = new Descriptor(wsdlLocation, serviceName);
    }

    public Descriptor _getDescriptor() {
        return _descriptor;
    }

    public List<ConfigInfo> getConfigInfoByUSSDCode(String ussdCode) throws Exception_Exception {
        return _getDescriptor().getProxy().getConfigInfoByUSSDCode(ussdCode);
    }

    public List<ConfigInfo> getConfigInfoBySMSCode(String smsCode) throws Exception_Exception {
        return _getDescriptor().getProxy().getConfigInfoBySMSCode(smsCode);
    }

    public ConfigInfo createConfigInfo(ConfigInfo bankConfigInfo) throws Exception_Exception {
        return _getDescriptor().getProxy().createConfigInfo(bankConfigInfo);
    }

    public ConfigInfo editConfigInfo(ConfigInfo bankConfigInfo) throws Exception_Exception {
        return _getDescriptor().getProxy().editConfigInfo(bankConfigInfo);
    }

    public ConfigInfo approveConfigInfo(ConfigInfo bankConfigInfo) throws Exception_Exception {
        return _getDescriptor().getProxy().approveConfigInfo(bankConfigInfo);
    }

    public ConfigInfo deleteConfigInfo(ConfigInfo bankConfigInfo) throws Exception_Exception {
        return _getDescriptor().getProxy().deleteConfigInfo(bankConfigInfo);
    }

    public ConfigInfo findConfigInfoByOwnerId(String ownerId) throws Exception_Exception {
        return _getDescriptor().getProxy().findConfigInfoByOwnerId(ownerId);
    }

}