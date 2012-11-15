package zw.co.econet.econetvas;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

public class EconetvasSOAPProxy{

    protected Descriptor _descriptor;

    public class Descriptor {
        private zw.co.econet.econetvas.Econetvas_Service _service = null;
        private zw.co.econet.econetvas.Econetvas _proxy = null;
        private Dispatch<Source> _dispatch = null;

        public Descriptor() {
            _service = new zw.co.econet.econetvas.Econetvas_Service();
            initCommon();
        }

        public Descriptor(URL wsdlLocation, QName serviceName) {
            _service = new zw.co.econet.econetvas.Econetvas_Service(wsdlLocation, serviceName);
            initCommon();
        }

        private void initCommon() {
            _proxy = _service.getEconetvasSOAP();
        }

        public zw.co.econet.econetvas.Econetvas getProxy() {
            return _proxy;
        }

        public Dispatch<Source> getDispatch() {
            if(_dispatch == null ) {
                QName portQName = new QName("http://econetvas.econet.co.zw/", "econetvasSOAP");
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

    public EconetvasSOAPProxy() {
        _descriptor = new Descriptor();
    }

    public EconetvasSOAPProxy(URL wsdlLocation, QName serviceName) {
        _descriptor = new Descriptor(wsdlLocation, serviceName);
    }

    public Descriptor _getDescriptor() {
        return _descriptor;
    }

    public BalanceResponse balanceEnquiry(BalanceRequest balanceRequest) {
        return _getDescriptor().getProxy().balanceEnquiry(balanceRequest);
    }

    public BillPaymentResponse billPay(BillPaymentRequest billPayRequest) {
        return _getDescriptor().getProxy().billPay(billPayRequest);
    }

    public BillPaymentReversalResponse billPayReversal(BillPaymentReversalRequest billPayReversalRequest) {
        return _getDescriptor().getProxy().billPayReversal(billPayReversalRequest);
    }

    public CreditResponse creditSubscriber(CreditRequest creditSubscriberRequest) {
        return _getDescriptor().getProxy().creditSubscriber(creditSubscriberRequest);
    }

    public DebitResponse debitSubscriber(DebitRequest debitSubscriberRequest) {
        return _getDescriptor().getProxy().debitSubscriber(debitSubscriberRequest);
    }

    public TextCreditResponse txtCredit(TextCreditRequest txtCreditRequest) {
        return _getDescriptor().getProxy().txtCredit(txtCreditRequest);
    }

    public CreditReversalResult creditReversal(CreditRequest originalCredit) {
        return _getDescriptor().getProxy().creditReversal(originalCredit);
    }

}