package zw.co.esolutions.ewallet.merchantservices.service;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;
import java.util.List;

public class MerchantServiceSOAPProxy{

    protected Descriptor _descriptor;

    public class Descriptor {
        private zw.co.esolutions.ewallet.merchantservices.service.MerchantService_Service _service = null;
        private zw.co.esolutions.ewallet.merchantservices.service.MerchantService _proxy = null;
        private Dispatch<Source> _dispatch = null;

        public Descriptor() {
            init();
        }

        public Descriptor(URL wsdlLocation, QName serviceName) {
            _service = new zw.co.esolutions.ewallet.merchantservices.service.MerchantService_Service(wsdlLocation, serviceName);
            initCommon();
        }

        public void init() {
            _service = null;
            _proxy = null;
            _dispatch = null;
            _service = new zw.co.esolutions.ewallet.merchantservices.service.MerchantService_Service();
            initCommon();
        }

        private void initCommon() {
            _proxy = _service.getMerchantServiceSOAP();
        }

        public zw.co.esolutions.ewallet.merchantservices.service.MerchantService getProxy() {
            return _proxy;
        }

        public Dispatch<Source> getDispatch() {
            if (_dispatch == null ) {
                QName portQName = new QName("http://service.merchantservices.ewallet.esolutions.co.zw/", "MerchantServiceSOAP");
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

    public MerchantServiceSOAPProxy() {
        _descriptor = new Descriptor();
        _descriptor.setMTOMEnabled(true);
    }

    public MerchantServiceSOAPProxy(URL wsdlLocation, QName serviceName) {
        _descriptor = new Descriptor(wsdlLocation, serviceName);
        _descriptor.setMTOMEnabled(true);
    }

    public Descriptor _getDescriptor() {
        return _descriptor;
    }

    public Merchant createMerchant(Merchant merchant, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().createMerchant(merchant,userName);
    }

    public String deleteMerchant(Merchant merchant, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().deleteMerchant(merchant,userName);
    }

    public Merchant approveMerchant(Merchant merchant, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().approveMerchant(merchant,userName);
    }

    public Merchant disapproveMerchant(Merchant merchant, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().disapproveMerchant(merchant,userName);
    }

    public Merchant editMerchant(Merchant merchant, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().editMerchant(merchant,userName);
    }

    public Merchant findMerchantById(String id) {
        return _getDescriptor().getProxy().findMerchantById(id);
    }

    public Merchant getMerchantByShortName(String shortName) throws Exception_Exception {
        return _getDescriptor().getProxy().getMerchantByShortName(shortName);
    }

    public List<Merchant> getMerchantByName(String name) throws Exception_Exception {
        return _getDescriptor().getProxy().getMerchantByName(name);
    }

    public List<Merchant> getAllMerchants() throws Exception_Exception {
        return _getDescriptor().getProxy().getAllMerchants();
    }

    public List<Merchant> getMerchantByStatus(MerchantStatus status) throws Exception_Exception {
        return _getDescriptor().getProxy().getMerchantByStatus(status);
    }

    public CustomerMerchant createCustomerMerchant(CustomerMerchant customerMerchant, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().createCustomerMerchant(customerMerchant,userName);
    }

    public CustomerMerchant approveCustomerMerchant(CustomerMerchant customerMerchant, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().approveCustomerMerchant(customerMerchant,userName);
    }

    public CustomerMerchant disapproveCustomerMerchant(CustomerMerchant customerMerchant, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().disapproveCustomerMerchant(customerMerchant,userName);
    }

    public CustomerMerchant editCustomerMerchant(CustomerMerchant customerMerchant, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().editCustomerMerchant(customerMerchant,userName);
    }

    public String deleteCustomerMerchant(CustomerMerchant customerMerchant, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().deleteCustomerMerchant(customerMerchant,userName);
    }

    public CustomerMerchant findCustomerMerchantById(String id) {
        return _getDescriptor().getProxy().findCustomerMerchantById(id);
    }

    public CustomerMerchant getCustomerMerchantByBankMerchantIdAndCustomerIdAndCustomerAccountNumber(String bankMerchantId, String customerId, String customerAccountNumber) throws Exception_Exception {
        return _getDescriptor().getProxy().getCustomerMerchantByBankMerchantIdAndCustomerIdAndCustomerAccountNumber(bankMerchantId,customerId,customerAccountNumber);
    }

    public List<CustomerMerchant> getCustomerMerchantByBankId(String bankId) throws Exception_Exception {
        return _getDescriptor().getProxy().getCustomerMerchantByBankId(bankId);
    }

    public List<CustomerMerchant> getCustomerMerchantByCustomerId(String customerId) throws Exception_Exception {
        return _getDescriptor().getProxy().getCustomerMerchantByCustomerId(customerId);
    }

    public List<CustomerMerchant> getCustomerMerchantByBankMerchantId(String bankMerchantId) throws Exception_Exception {
        return _getDescriptor().getProxy().getCustomerMerchantByBankMerchantId(bankMerchantId);
    }

    public List<CustomerMerchant> getCustomerMerchantByCustomerAccountNumber(String customerAccountNumber) throws Exception_Exception {
        return _getDescriptor().getProxy().getCustomerMerchantByCustomerAccountNumber(customerAccountNumber);
    }

    public List<CustomerMerchant> getCustomerMerchantByStatus(CustomerMerchantStatus status) throws Exception_Exception {
        return _getDescriptor().getProxy().getCustomerMerchantByStatus(status);
    }

    public BankMerchant createBankMerchant(BankMerchant bankMerchant, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().createBankMerchant(bankMerchant,userName);
    }

    public BankMerchant approveBankMerchant(BankMerchant bankMerchant, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().approveBankMerchant(bankMerchant,userName);
    }

    public BankMerchant deleteBankMerchant(BankMerchant bankMerchant, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().deleteBankMerchant(bankMerchant,userName);
    }

    public BankMerchant editBankMerchant(BankMerchant bankMerchant, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().editBankMerchant(bankMerchant,userName);
    }

    public BankMerchant disapproveBankMerchant(BankMerchant bankMerchant, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().disapproveBankMerchant(bankMerchant,userName);
    }

    public BankMerchant findBankMerchantById(String bankMerchantId) {
        return _getDescriptor().getProxy().findBankMerchantById(bankMerchantId);
    }

    public BankMerchant getBankMerchantByStatusAndBankIdAndMerchantId(BankMerchantStatus status, String bankId, String merchantId) {
        return _getDescriptor().getProxy().getBankMerchantByStatusAndBankIdAndMerchantId(status,bankId,merchantId);
    }

    public BankMerchant getBankMerchantByBankIdAndMerchantId(String bankId, String merchantId) {
        return _getDescriptor().getProxy().getBankMerchantByBankIdAndMerchantId(bankId,merchantId);
    }

    public List<BankMerchant> getBankMerchantByStatus(BankMerchantStatus status) {
        return _getDescriptor().getProxy().getBankMerchantByStatus(status);
    }

    public List<BankMerchant> getBankMerchantByBankId(String arg0) throws Exception_Exception {
        return _getDescriptor().getProxy().getBankMerchantByBankId(arg0);
    }

    public List<BankMerchant> getBankMerchantByMerchantId(String arg0) throws Exception_Exception {
        return _getDescriptor().getProxy().getBankMerchantByMerchantId(arg0);
    }

    public BankMerchant getBankMerchantByBankIdAndShortNameAndStatus(String arg0, String arg1, BankMerchantStatus arg2) throws Exception_Exception {
        return _getDescriptor().getProxy().getBankMerchantByBankIdAndShortNameAndStatus(arg0,arg1,arg2);
    }

    public CustomerMerchant getCustomerMerchantByCustomerIdAndBankMerchantIdAndStatus(String arg0, String arg1, CustomerMerchantStatus arg2) throws Exception_Exception {
        return _getDescriptor().getProxy().getCustomerMerchantByCustomerIdAndBankMerchantIdAndStatus(arg0,arg1,arg2);
    }

    public List<Merchant> getMerchantByCustomerId(String arg0) throws Exception_Exception {
        return _getDescriptor().getProxy().getMerchantByCustomerId(arg0);
    }

    public CustomerMerchant getCustomerMerchantByCustomerIdAndMerchantShortNameAndStatus(String customerId, String merchantShortName, CustomerMerchantStatus status) throws Exception_Exception {
        return _getDescriptor().getProxy().getCustomerMerchantByCustomerIdAndMerchantShortNameAndStatus(customerId,merchantShortName,status);
    }

}