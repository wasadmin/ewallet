package zw.co.esolutions.ewallet.alertsservices.service;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import java.util.List;

public class AlertsServiceSOAPProxy{

    protected Descriptor _descriptor;

    public class Descriptor {
        private zw.co.esolutions.ewallet.alertsservices.service.AlertsService_Service _service = null;
        private zw.co.esolutions.ewallet.alertsservices.service.AlertsService _proxy = null;
        private Dispatch<Source> _dispatch = null;

        public Descriptor() {
            _service = new zw.co.esolutions.ewallet.alertsservices.service.AlertsService_Service();
            initCommon();
        }

        public Descriptor(URL wsdlLocation, QName serviceName) {
            _service = new zw.co.esolutions.ewallet.alertsservices.service.AlertsService_Service(wsdlLocation, serviceName);
            initCommon();
        }

        private void initCommon() {
            _proxy = _service.getAlertsServiceSOAP();
        }

        public zw.co.esolutions.ewallet.alertsservices.service.AlertsService getProxy() {
            return _proxy;
        }

        public Dispatch<Source> getDispatch() {
            if(_dispatch == null ) {
                QName portQName = new QName("http://service.alertsservices.ewallet.esolutions.co.zw/", "AlertsServiceSOAP");
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

    public AlertsServiceSOAPProxy() {
        _descriptor = new Descriptor();
    }

    public AlertsServiceSOAPProxy(URL wsdlLocation, QName serviceName) {
        _descriptor = new Descriptor(wsdlLocation, serviceName);
    }

    public Descriptor _getDescriptor() {
        return _descriptor;
    }

    public void createAlertOptionsForAccount(String accountId, String mobileProfileId, String username) throws Exception_Exception {
        _getDescriptor().getProxy().createAlertOptionsForAccount(accountId,mobileProfileId,username);
    }

    public AlertOption createAlertOption(AlertOption alertOption, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().createAlertOption(alertOption,username);
    }

    public AlertOption editAlertOption(AlertOption alertOption, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().editAlertOption(alertOption,username);
    }

    public AlertOption approveAlertOption(AlertOption alertOption, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().approveAlertOption(alertOption,username);
    }

    public AlertOption rejectAlertOption(AlertOption alertOption, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().rejectAlertOption(alertOption,username);
    }

    public AlertOption deleteAlertOption(AlertOption alertOption, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().deleteAlertOption(alertOption,username);
    }

    public AlertOption enableAlertOption(AlertOption alertOption, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().enableAlertOption(alertOption,username);
    }

    public AlertOption disableAlertOption(AlertOption alertOption, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().disableAlertOption(alertOption,username);
    }

    public AlertOption findAlertOptionById(String alertOptionId) {
        return _getDescriptor().getProxy().findAlertOptionById(alertOptionId);
    }

    public AlertOption getAlertOptionByBankAccountAndMobileProfileAndTransactionType(String bankAccountId, String mobileProfileId, String transactionTypeId) {
        return _getDescriptor().getProxy().getAlertOptionByBankAccountAndMobileProfileAndTransactionType(bankAccountId,mobileProfileId,transactionTypeId);
    }

    public List<AlertOption> getAlertOptionByStatus(AlertOptionStatus status) {
        return _getDescriptor().getProxy().getAlertOptionByStatus(status);
    }

    public List<AlertOption> getAlertOptionByBankAccountId(String bankAccountId) {
        return _getDescriptor().getProxy().getAlertOptionByBankAccountId(bankAccountId);
    }

    public List<AlertOption> getAlertOptionByMobileProfileId(String mobileProfileId) {
        return _getDescriptor().getProxy().getAlertOptionByMobileProfileId(mobileProfileId);
    }

    public List<AlertOption> getAlertOptionByTransationTypeId(String transactionTypeId) {
        return _getDescriptor().getProxy().getAlertOptionByTransationTypeId(transactionTypeId);
    }

    public TransactionType createTransactionType(TransactionType transactionType, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().createTransactionType(transactionType,username);
    }

    public TransactionType editTransactionType(TransactionType transactionType, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().editTransactionType(transactionType,username);
    }

    public TransactionType approveTransactionType(TransactionType transactionType, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().approveTransactionType(transactionType,username);
    }

    public TransactionType rejectTransactionType(TransactionType transactionType, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().rejectTransactionType(transactionType,username);
    }

    public TransactionType deleteTransactionType(TransactionType transactionType, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().deleteTransactionType(transactionType,username);
    }

    public TransactionType enableTransactionType(TransactionType transactionType, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().enableTransactionType(transactionType,username);
    }

    public TransactionType disableTransactionType(TransactionType transactionType, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().disableTransactionType(transactionType,username);
    }

    public TransactionType findTransactionType(String transactionTypeId) {
        return _getDescriptor().getProxy().findTransactionType(transactionTypeId);
    }

    public TransactionType getTransactionTypeByCode(String transactionTypeCode) {
        return _getDescriptor().getProxy().getTransactionTypeByCode(transactionTypeCode);
    }

    public TransactionType getTransactionTypeByAlertOption(AlertOption alertOption) {
        return _getDescriptor().getProxy().getTransactionTypeByAlertOption(alertOption);
    }

    public List<TransactionType> getTransactionTypeByStatus(TransactionTypeStatus status) {
        return _getDescriptor().getProxy().getTransactionTypeByStatus(status);
    }

    public List<TransactionType> getAllTransactionTypes() {
        return _getDescriptor().getProxy().getAllTransactionTypes();
    }

}