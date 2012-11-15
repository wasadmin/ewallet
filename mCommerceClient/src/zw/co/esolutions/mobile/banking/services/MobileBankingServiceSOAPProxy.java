package zw.co.esolutions.mobile.banking.services;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

public class MobileBankingServiceSOAPProxy{

    protected Descriptor _descriptor;

    public class Descriptor {
        private zw.co.esolutions.mobile.banking.services.MobileBankingService_Service _service = null;
        private zw.co.esolutions.mobile.banking.services.MobileBankingService _proxy = null;
        private Dispatch<Source> _dispatch = null;

        public Descriptor() {
            _service = new zw.co.esolutions.mobile.banking.services.MobileBankingService_Service();
            initCommon();
        }

        public Descriptor(URL wsdlLocation, QName serviceName) {
            _service = new zw.co.esolutions.mobile.banking.services.MobileBankingService_Service(wsdlLocation, serviceName);
            initCommon();
        }

        private void initCommon() {
            _proxy = _service.getMobileBankingServiceSOAP();
        }

        public zw.co.esolutions.mobile.banking.services.MobileBankingService getProxy() {
            return _proxy;
        }

        public Dispatch<Source> getDispatch() {
            if(_dispatch == null ) {
                QName portQName = new QName("http://services.banking.mobile.esolutions.co.zw/", "MobileBankingServiceSOAP");
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

    public MobileBankingServiceSOAPProxy() {
        _descriptor = new Descriptor();
    }

    public MobileBankingServiceSOAPProxy(URL wsdlLocation, QName serviceName) {
        _descriptor = new Descriptor(wsdlLocation, serviceName);
    }

    public Descriptor _getDescriptor() {
        return _descriptor;
    }

    public Veres verifyEnrolment(Vereq vereq) {
        return _getDescriptor().getProxy().verifyEnrolment(vereq);
    }

    public Pares verifyPassword(Pareq pareq) {
        return _getDescriptor().getProxy().verifyPassword(pareq);
    }

    public TransactionResponse submitTransaction(TransactionRequest transactionRequest) {
        return _getDescriptor().getProxy().submitTransaction(transactionRequest);
    }

    public MerchantResponse getMerchants(MerchantRequest merchantRequest) {
        return _getDescriptor().getProxy().getMerchants(merchantRequest);
    }

    public BankResponse getBanks(BankRequest bankRequest) {
        return _getDescriptor().getProxy().getBanks(bankRequest);
    }

}