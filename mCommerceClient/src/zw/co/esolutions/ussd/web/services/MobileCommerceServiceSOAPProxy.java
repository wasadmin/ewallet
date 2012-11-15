package zw.co.esolutions.ussd.web.services;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import java.util.List;

public class MobileCommerceServiceSOAPProxy{

    protected Descriptor _descriptor;

    public class Descriptor {
        private zw.co.esolutions.ussd.web.services.MobileCommerceService_Service _service = null;
        private zw.co.esolutions.ussd.web.services.MobileCommerceService _proxy = null;
        private Dispatch<Source> _dispatch = null;

        public Descriptor() {
            _service = new zw.co.esolutions.ussd.web.services.MobileCommerceService_Service();
            initCommon();
        }

        public Descriptor(URL wsdlLocation, QName serviceName) {
            _service = new zw.co.esolutions.ussd.web.services.MobileCommerceService_Service(wsdlLocation, serviceName);
            initCommon();
        }

        private void initCommon() {
            _proxy = _service.getMobileCommerceServiceSOAP();
        }

        public zw.co.esolutions.ussd.web.services.MobileCommerceService getProxy() {
            return _proxy;
        }

        public Dispatch<Source> getDispatch() {
            if(_dispatch == null ) {
                QName portQName = new QName("http://services.web.ussd.esolutions.co.zw/", "MobileCommerceServiceSOAP");
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

    public MobileCommerceServiceSOAPProxy() {
        _descriptor = new Descriptor();
    }

    public MobileCommerceServiceSOAPProxy(URL wsdlLocation, QName serviceName) {
        _descriptor = new Descriptor(wsdlLocation, serviceName);
    }

    public Descriptor _getDescriptor() {
        return _descriptor;
    }

    public UssdTransaction createTransaction(UssdTransaction ussdTransaction) throws Exception_Exception {
        return _getDescriptor().getProxy().createTransaction(ussdTransaction);
    }

    public UssdTransaction updateTransaction(UssdTransaction ussdTransaction) throws Exception_Exception {
        return _getDescriptor().getProxy().updateTransaction(ussdTransaction);
    }

    public void deleteTransaction(String uuid) throws Exception_Exception {
        _getDescriptor().getProxy().deleteTransaction(uuid);
    }

    public UssdTransaction findTransaction(String uuid) {
        return _getDescriptor().getProxy().findTransaction(uuid);
    }

    public UssdTransaction getTransactionByUSSDSessionId(String ussdSessionId) {
        return _getDescriptor().getProxy().getTransactionByUSSDSessionId(ussdSessionId);
    }

    public WebSession createWebSession(WebSession webSession) {
        return _getDescriptor().getProxy().createWebSession(webSession);
    }

    public void deleteWebSession(String webSessionId) {
        _getDescriptor().getProxy().deleteWebSession(webSessionId);
    }

    public WebSession findWebSessionById(String webSessionId) {
        return _getDescriptor().getProxy().findWebSessionById(webSessionId);
    }

    public WebSession getFailedWebSession(String mobileNumber, String bankId) {
        return _getDescriptor().getProxy().getFailedWebSession(mobileNumber,bankId);
    }

    public WebSession getWebSessionByReferenceId(String referenceId) {
        return _getDescriptor().getProxy().getWebSessionByReferenceId(referenceId);
    }

    public WebSession updateWebSession(WebSession webSession) {
        return _getDescriptor().getProxy().updateWebSession(webSession);
    }

    public WebSession getWebSessionByMobileAndBankAndStatus(String mobileNumber, String bankId, String status) {
        return _getDescriptor().getProxy().getWebSessionByMobileAndBankAndStatus(mobileNumber,bankId,status);
    }

    public boolean sendTransaction(MobileWebRequestMessage mobileWebRequest) {
        return _getDescriptor().getProxy().sendTransaction(mobileWebRequest);
    }

    public String getTargetBankCodeForTargetAccount(String sessionId, String targetAccount, String targetMobile) {
        return _getDescriptor().getProxy().getTargetBankCodeForTargetAccount(sessionId,targetAccount,targetMobile);
    }

    public boolean isAgentMobile(String bankCode, String formattedMobileNumber) {
        return _getDescriptor().getProxy().isAgentMobile(bankCode,formattedMobileNumber);
    }

    public String validateAgentDeposit(String sourceAccount, String targetMobile) {
        return _getDescriptor().getProxy().validateAgentDeposit(sourceAccount,targetMobile);
    }

    public String validateAgentTransfer(String sourceAccount, String targetAccount, String bankCode) {
        return _getDescriptor().getProxy().validateAgentTransfer(sourceAccount,targetAccount,bankCode);
    }

    public String validateAgentCustomerWithdrawal(String targetAccount) {
        return _getDescriptor().getProxy().validateAgentCustomerWithdrawal(targetAccount);
    }

    public String getAgentNumberByMobileNumberAndBankId(String mobileNumber, String bankCode) {
        return _getDescriptor().getProxy().getAgentNumberByMobileNumberAndBankId(mobileNumber,bankCode);
    }

    public boolean isNonHolderAccount(String targetAccount, String bankCode) {
        return _getDescriptor().getProxy().isNonHolderAccount(targetAccount,bankCode);
    }

    public List<String> getActiveBankNames() {
        return _getDescriptor().getProxy().getActiveBankNames();
    }

}