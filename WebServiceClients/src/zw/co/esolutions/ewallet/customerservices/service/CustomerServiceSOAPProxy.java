package zw.co.esolutions.ewallet.customerservices.service;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import java.util.List;

public class CustomerServiceSOAPProxy{

    protected Descriptor _descriptor;

    public class Descriptor {
        private zw.co.esolutions.ewallet.customerservices.service.CustomerService_Service _service = null;
        private zw.co.esolutions.ewallet.customerservices.service.CustomerService _proxy = null;
        private Dispatch<Source> _dispatch = null;

        public Descriptor() {
            _service = new zw.co.esolutions.ewallet.customerservices.service.CustomerService_Service();
            initCommon();
        }

        public Descriptor(URL wsdlLocation, QName serviceName) {
            _service = new zw.co.esolutions.ewallet.customerservices.service.CustomerService_Service(wsdlLocation, serviceName);
            initCommon();
        }

        private void initCommon() {
            _proxy = _service.getCustomerServiceSOAP();
        }

        public zw.co.esolutions.ewallet.customerservices.service.CustomerService getProxy() {
            return _proxy;
        }

        public Dispatch<Source> getDispatch() {
            if(_dispatch == null ) {
                QName portQName = new QName("http://service.customerservices.ewallet.esolutions.co.zw/", "CustomerServiceSOAP");
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

    public CustomerServiceSOAPProxy() {
        _descriptor = new Descriptor();
    }

    public CustomerServiceSOAPProxy(URL wsdlLocation, QName serviceName) {
        _descriptor = new Descriptor(wsdlLocation, serviceName);
    }

    public Descriptor _getDescriptor() {
        return _descriptor;
    }

    public Customer createCustomer(Customer customer, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().createCustomer(customer,userName);
    }

    public String deleteCustomer(Customer customer, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().deleteCustomer(customer,userName);
    }

    public Customer updateCustomer(Customer customer, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().updateCustomer(customer,userName);
    }

    public Customer findCustomerById(String id) {
        return _getDescriptor().getProxy().findCustomerById(id);
    }

    public List<Customer> getCustomerByLastName(String lastName) {
        return _getDescriptor().getProxy().getCustomerByLastName(lastName);
    }

    public List<Customer> getCustomerByNationalId(String nationalId) {
        return _getDescriptor().getProxy().getCustomerByNationalId(nationalId);
    }

    public List<Customer> getCustomerByDateOfBirth(String dateOfBirth) {
        return _getDescriptor().getProxy().getCustomerByDateOfBirth(dateOfBirth);
    }

    public List<Customer> getCustomerByGender(String gender) {
        return _getDescriptor().getProxy().getCustomerByGender(gender);
    }

    public MobileProfile createMobileProfile(MobileProfile mobileProfile, String source, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().createMobileProfile(mobileProfile,source,userName);
    }

    public MobileProfile resetMobileProfilePin(MobileProfile mobileProfile, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().resetMobileProfilePin(mobileProfile,userName);
    }

    public String deleteMobileProfile(MobileProfile mobileProfile, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().deleteMobileProfile(mobileProfile,userName);
    }

    public MobileProfile updateMobileProfile(MobileProfile mobileProfile, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().updateMobileProfile(mobileProfile,userName);
    }

    public MobileProfile findMobileProfileById(String id) {
        return _getDescriptor().getProxy().findMobileProfileById(id);
    }

    public MobileProfile getMobileProfileByMobileNumber(String mobileNumber) {
        return _getDescriptor().getProxy().getMobileProfileByMobileNumber(mobileNumber);
    }

    public List<MobileProfile> getMobileProfileByStatus(String status) {
        return _getDescriptor().getProxy().getMobileProfileByStatus(status);
    }

    public List<MobileProfile> getMobileProfileByCustomer(String customerId) {
        return _getDescriptor().getProxy().getMobileProfileByCustomer(customerId);
    }

    public List<Customer> getCustomerByBranchId(String branchId) {
        return _getDescriptor().getProxy().getCustomerByBranchId(branchId);
    }

    public GenerateTxnPassCodeResp generateTxnPassCode(String mobileId) {
        return _getDescriptor().getProxy().generateTxnPassCode(mobileId);
    }

    public boolean txnPassCodeIsValid(ValidateTxnPassCodeReq request) {
        return _getDescriptor().getProxy().txnPassCodeIsValid(request);
    }

    public boolean mobileProfileIsActive(String mobileNumber) {
        return _getDescriptor().getProxy().mobileProfileIsActive(mobileNumber);
    }

    public List<Customer> getCustomerByStatus(CustomerStatus status) {
        return _getDescriptor().getProxy().getCustomerByStatus(status);
    }

    public MobileProfile getMobileProfileByBankAndMobileNumber(String bankId, String mobileNumber) {
        return _getDescriptor().getProxy().getMobileProfileByBankAndMobileNumber(bankId,mobileNumber);
    }

    public List<MobileProfile> getMobileProfileListByMobileNumber(String mobileNumber) {
        return _getDescriptor().getProxy().getMobileProfileListByMobileNumber(mobileNumber);
    }

    public List<Customer> getCustomersByWrapper(CustomerWrapper customerWrapper, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().getCustomersByWrapper(customerWrapper,userName);
    }

    public MobileProfile hotMobileNumber(MobileProfile mobileprofile, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().hotMobileNumber(mobileprofile,userName);
    }

    public MobileProfile coldMobileNumber(MobileProfile mobileprofile, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().coldMobileNumber(mobileprofile,userName);
    }

    public Customer deregisterCustomer(Customer customer, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().deregisterCustomer(customer,userName);
    }

    public Customer suspendCustomer(Customer customer, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().suspendCustomer(customer,userName);
    }

    public Customer activateCustomer(Customer customer, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().activateCustomer(customer,userName);
    }

    public MobileProfile getMobileProfileByBankIdMobileNumberAndStatus(String bankId, String mobileNumber, MobileProfileStatus status) {
        return _getDescriptor().getProxy().getMobileProfileByBankIdMobileNumberAndStatus(bankId,mobileNumber,status);
    }

    public ResponseCode authenticateMobileProfile(String bankId, String mobileNumber, String secretCode) {
        return _getDescriptor().getProxy().authenticateMobileProfile(bankId,mobileNumber,secretCode);
    }

    public Customer approveCustomer(Customer customer, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().approveCustomer(customer,userName);
    }

    public Customer rejectCustomer(Customer customer, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().rejectCustomer(customer,userName);
    }

    public MobileProfile approveMobileNumber(MobileProfile mobileprofile, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().approveMobileNumber(mobileprofile,userName);
    }

    public MobileProfile rejectMobileNumber(MobileProfile mobileprofile, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().rejectMobileNumber(mobileprofile,userName);
    }

    public List<Customer> getCustomerByStatusAndLastBranch(CustomerStatus status, String lastBranch) {
        return _getDescriptor().getProxy().getCustomerByStatusAndLastBranch(status,lastBranch);
    }

    public MobileProfile deRegisterMobileProfile(MobileProfile mobileprofile, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().deRegisterMobileProfile(mobileprofile,userName);
    }

    public MobileProfile unLockMobileProfile(MobileProfile mobileProfile, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().unLockMobileProfile(mobileProfile,userName);
    }

}