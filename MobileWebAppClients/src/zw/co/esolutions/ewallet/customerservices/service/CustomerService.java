//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.customerservices.service;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

@WebService(name = "CustomerService", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface CustomerService {


    /**
     * 
     * @param userName
     * @param customer
     * @return
     *     returns zw.co.esolutions.ewallet.customerservices.service.Customer
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "createCustomer", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.CreateCustomer")
    @ResponseWrapper(localName = "createCustomerResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.CreateCustomerResponse")
    public Customer createCustomer(
        @WebParam(name = "customer", targetNamespace = "")
        Customer customer,
        @WebParam(name = "userName", targetNamespace = "")
        String userName)
        throws Exception_Exception
    ;

    /**
     * 
     * @param userName
     * @param customer
     * @return
     *     returns java.lang.String
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "deleteCustomer", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.DeleteCustomer")
    @ResponseWrapper(localName = "deleteCustomerResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.DeleteCustomerResponse")
    public String deleteCustomer(
        @WebParam(name = "customer", targetNamespace = "")
        Customer customer,
        @WebParam(name = "userName", targetNamespace = "")
        String userName)
        throws Exception_Exception
    ;

    /**
     * 
     * @param userName
     * @param customer
     * @return
     *     returns zw.co.esolutions.ewallet.customerservices.service.Customer
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "updateCustomer", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.UpdateCustomer")
    @ResponseWrapper(localName = "updateCustomerResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.UpdateCustomerResponse")
    public Customer updateCustomer(
        @WebParam(name = "customer", targetNamespace = "")
        Customer customer,
        @WebParam(name = "userName", targetNamespace = "")
        String userName)
        throws Exception_Exception
    ;

    /**
     * 
     * @param id
     * @return
     *     returns zw.co.esolutions.ewallet.customerservices.service.Customer
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "findCustomerById", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.FindCustomerById")
    @ResponseWrapper(localName = "findCustomerByIdResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.FindCustomerByIdResponse")
    public Customer findCustomerById(
        @WebParam(name = "id", targetNamespace = "")
        String id);

    /**
     * 
     * @param lastName
     * @return
     *     returns java.util.List<zw.co.esolutions.ewallet.customerservices.service.Customer>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getCustomerByLastName", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.GetCustomerByLastName")
    @ResponseWrapper(localName = "getCustomerByLastNameResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.GetCustomerByLastNameResponse")
    public List<Customer> getCustomerByLastName(
        @WebParam(name = "lastName", targetNamespace = "")
        String lastName);

    /**
     * 
     * @param nationalId
     * @return
     *     returns java.util.List<zw.co.esolutions.ewallet.customerservices.service.Customer>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getCustomerByNationalId", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.GetCustomerByNationalId")
    @ResponseWrapper(localName = "getCustomerByNationalIdResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.GetCustomerByNationalIdResponse")
    public List<Customer> getCustomerByNationalId(
        @WebParam(name = "nationalId", targetNamespace = "")
        String nationalId);

    /**
     * 
     * @param dateOfBirth
     * @return
     *     returns java.util.List<zw.co.esolutions.ewallet.customerservices.service.Customer>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getCustomerByDateOfBirth", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.GetCustomerByDateOfBirth")
    @ResponseWrapper(localName = "getCustomerByDateOfBirthResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.GetCustomerByDateOfBirthResponse")
    public List<Customer> getCustomerByDateOfBirth(
        @WebParam(name = "dateOfBirth", targetNamespace = "")
        String dateOfBirth);

    /**
     * 
     * @param gender
     * @return
     *     returns java.util.List<zw.co.esolutions.ewallet.customerservices.service.Customer>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getCustomerByGender", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.GetCustomerByGender")
    @ResponseWrapper(localName = "getCustomerByGenderResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.GetCustomerByGenderResponse")
    public List<Customer> getCustomerByGender(
        @WebParam(name = "gender", targetNamespace = "")
        String gender);

    /**
     * 
     * @param userName
     * @param mobileProfile
     * @param source
     * @return
     *     returns zw.co.esolutions.ewallet.customerservices.service.MobileProfile
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "createMobileProfile", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.CreateMobileProfile")
    @ResponseWrapper(localName = "createMobileProfileResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.CreateMobileProfileResponse")
    public MobileProfile createMobileProfile(
        @WebParam(name = "mobileProfile", targetNamespace = "")
        MobileProfile mobileProfile,
        @WebParam(name = "source", targetNamespace = "")
        String source,
        @WebParam(name = "userName", targetNamespace = "")
        String userName)
        throws Exception_Exception
    ;

    /**
     * 
     * @param userName
     * @param mobileProfile
     * @return
     *     returns zw.co.esolutions.ewallet.customerservices.service.MobileProfile
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "resetMobileProfilePin", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.ResetMobileProfilePin")
    @ResponseWrapper(localName = "resetMobileProfilePinResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.ResetMobileProfilePinResponse")
    public MobileProfile resetMobileProfilePin(
        @WebParam(name = "mobileProfile", targetNamespace = "")
        MobileProfile mobileProfile,
        @WebParam(name = "userName", targetNamespace = "")
        String userName)
        throws Exception_Exception
    ;

    /**
     * 
     * @param userName
     * @param mobileProfile
     * @return
     *     returns java.lang.String
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "deleteMobileProfile", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.DeleteMobileProfile")
    @ResponseWrapper(localName = "deleteMobileProfileResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.DeleteMobileProfileResponse")
    public String deleteMobileProfile(
        @WebParam(name = "mobileProfile", targetNamespace = "")
        MobileProfile mobileProfile,
        @WebParam(name = "userName", targetNamespace = "")
        String userName)
        throws Exception_Exception
    ;

    /**
     * 
     * @param userName
     * @param mobileProfile
     * @return
     *     returns zw.co.esolutions.ewallet.customerservices.service.MobileProfile
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "updateMobileProfile", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.UpdateMobileProfile")
    @ResponseWrapper(localName = "updateMobileProfileResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.UpdateMobileProfileResponse")
    public MobileProfile updateMobileProfile(
        @WebParam(name = "mobileProfile", targetNamespace = "")
        MobileProfile mobileProfile,
        @WebParam(name = "userName", targetNamespace = "")
        String userName)
        throws Exception_Exception
    ;

    /**
     * 
     * @param id
     * @return
     *     returns zw.co.esolutions.ewallet.customerservices.service.MobileProfile
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "findMobileProfileById", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.FindMobileProfileById")
    @ResponseWrapper(localName = "findMobileProfileByIdResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.FindMobileProfileByIdResponse")
    public MobileProfile findMobileProfileById(
        @WebParam(name = "id", targetNamespace = "")
        String id);

    /**
     * 
     * @param mobileNumber
     * @return
     *     returns zw.co.esolutions.ewallet.customerservices.service.MobileProfile
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getMobileProfileByMobileNumber", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.GetMobileProfileByMobileNumber")
    @ResponseWrapper(localName = "getMobileProfileByMobileNumberResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.GetMobileProfileByMobileNumberResponse")
    public MobileProfile getMobileProfileByMobileNumber(
        @WebParam(name = "mobileNumber", targetNamespace = "")
        String mobileNumber);

    /**
     * 
     * @param status
     * @return
     *     returns java.util.List<zw.co.esolutions.ewallet.customerservices.service.MobileProfile>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getMobileProfileByStatus", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.GetMobileProfileByStatus")
    @ResponseWrapper(localName = "getMobileProfileByStatusResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.GetMobileProfileByStatusResponse")
    public List<MobileProfile> getMobileProfileByStatus(
        @WebParam(name = "status", targetNamespace = "")
        String status);

    /**
     * 
     * @param customerId
     * @return
     *     returns java.util.List<zw.co.esolutions.ewallet.customerservices.service.MobileProfile>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getMobileProfileByCustomer", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.GetMobileProfileByCustomer")
    @ResponseWrapper(localName = "getMobileProfileByCustomerResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.GetMobileProfileByCustomerResponse")
    public List<MobileProfile> getMobileProfileByCustomer(
        @WebParam(name = "customerId", targetNamespace = "")
        String customerId);

    /**
     * 
     * @param branchId
     * @return
     *     returns java.util.List<zw.co.esolutions.ewallet.customerservices.service.Customer>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getCustomerByBranchId", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.GetCustomerByBranchId")
    @ResponseWrapper(localName = "getCustomerByBranchIdResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.GetCustomerByBranchIdResponse")
    public List<Customer> getCustomerByBranchId(
        @WebParam(name = "branchId", targetNamespace = "")
        String branchId);

    /**
     * 
     * @param mobileId
     * @return
     *     returns zw.co.esolutions.ewallet.customerservices.service.GenerateTxnPassCodeResp
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "generateTxnPassCode", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.GenerateTxnPassCode")
    @ResponseWrapper(localName = "generateTxnPassCodeResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.GenerateTxnPassCodeResponse")
    public GenerateTxnPassCodeResp generateTxnPassCode(
        @WebParam(name = "mobileId", targetNamespace = "")
        String mobileId);

    /**
     * 
     * @param request
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "txnPassCodeIsValid", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.TxnPassCodeIsValid")
    @ResponseWrapper(localName = "txnPassCodeIsValidResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.TxnPassCodeIsValidResponse")
    public boolean txnPassCodeIsValid(
        @WebParam(name = "request", targetNamespace = "")
        ValidateTxnPassCodeReq request);

    /**
     * 
     * @param mobileNumber
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "mobileProfileIsActive", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.MobileProfileIsActive")
    @ResponseWrapper(localName = "mobileProfileIsActiveResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.MobileProfileIsActiveResponse")
    public boolean mobileProfileIsActive(
        @WebParam(name = "mobileNumber", targetNamespace = "")
        String mobileNumber);

    /**
     * 
     * @param status
     * @return
     *     returns java.util.List<zw.co.esolutions.ewallet.customerservices.service.Customer>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getCustomerByStatus", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.GetCustomerByStatus")
    @ResponseWrapper(localName = "getCustomerByStatusResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.GetCustomerByStatusResponse")
    public List<Customer> getCustomerByStatus(
        @WebParam(name = "status", targetNamespace = "")
        CustomerStatus status);

    /**
     * 
     * @param bankId
     * @param mobileNumber
     * @return
     *     returns zw.co.esolutions.ewallet.customerservices.service.MobileProfile
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getMobileProfileByBankAndMobileNumber", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.GetMobileProfileByBankAndMobileNumber")
    @ResponseWrapper(localName = "getMobileProfileByBankAndMobileNumberResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.GetMobileProfileByBankAndMobileNumberResponse")
    public MobileProfile getMobileProfileByBankAndMobileNumber(
        @WebParam(name = "bankId", targetNamespace = "")
        String bankId,
        @WebParam(name = "mobileNumber", targetNamespace = "")
        String mobileNumber);

    /**
     * 
     * @param mobileNumber
     * @return
     *     returns java.util.List<zw.co.esolutions.ewallet.customerservices.service.MobileProfile>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getMobileProfileListByMobileNumber", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.GetMobileProfileListByMobileNumber")
    @ResponseWrapper(localName = "getMobileProfileListByMobileNumberResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.GetMobileProfileListByMobileNumberResponse")
    public List<MobileProfile> getMobileProfileListByMobileNumber(
        @WebParam(name = "mobileNumber", targetNamespace = "")
        String mobileNumber);

    /**
     * 
     * @param customerWrapper
     * @param userName
     * @return
     *     returns java.util.List<zw.co.esolutions.ewallet.customerservices.service.Customer>
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getCustomersByWrapper", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.GetCustomersByWrapper")
    @ResponseWrapper(localName = "getCustomersByWrapperResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.GetCustomersByWrapperResponse")
    public List<Customer> getCustomersByWrapper(
        @WebParam(name = "customerWrapper", targetNamespace = "")
        CustomerWrapper customerWrapper,
        @WebParam(name = "userName", targetNamespace = "")
        String userName)
        throws Exception_Exception
    ;

    /**
     * 
     * @param userName
     * @param mobileprofile
     * @return
     *     returns zw.co.esolutions.ewallet.customerservices.service.MobileProfile
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "hotMobileNumber", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.HotMobileNumber")
    @ResponseWrapper(localName = "hotMobileNumberResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.HotMobileNumberResponse")
    public MobileProfile hotMobileNumber(
        @WebParam(name = "mobileprofile", targetNamespace = "")
        MobileProfile mobileprofile,
        @WebParam(name = "userName", targetNamespace = "")
        String userName)
        throws Exception_Exception
    ;

    /**
     * 
     * @param userName
     * @param mobileprofile
     * @return
     *     returns zw.co.esolutions.ewallet.customerservices.service.MobileProfile
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "coldMobileNumber", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.ColdMobileNumber")
    @ResponseWrapper(localName = "coldMobileNumberResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.ColdMobileNumberResponse")
    public MobileProfile coldMobileNumber(
        @WebParam(name = "mobileprofile", targetNamespace = "")
        MobileProfile mobileprofile,
        @WebParam(name = "userName", targetNamespace = "")
        String userName)
        throws Exception_Exception
    ;

    /**
     * 
     * @param userName
     * @param customer
     * @return
     *     returns zw.co.esolutions.ewallet.customerservices.service.Customer
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "deregisterCustomer", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.DeregisterCustomer")
    @ResponseWrapper(localName = "deregisterCustomerResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.DeregisterCustomerResponse")
    public Customer deregisterCustomer(
        @WebParam(name = "customer", targetNamespace = "")
        Customer customer,
        @WebParam(name = "userName", targetNamespace = "")
        String userName)
        throws Exception_Exception
    ;

    /**
     * 
     * @param userName
     * @param customer
     * @return
     *     returns zw.co.esolutions.ewallet.customerservices.service.Customer
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "suspendCustomer", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.SuspendCustomer")
    @ResponseWrapper(localName = "suspendCustomerResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.SuspendCustomerResponse")
    public Customer suspendCustomer(
        @WebParam(name = "customer", targetNamespace = "")
        Customer customer,
        @WebParam(name = "userName", targetNamespace = "")
        String userName)
        throws Exception_Exception
    ;

    /**
     * 
     * @param userName
     * @param customer
     * @return
     *     returns zw.co.esolutions.ewallet.customerservices.service.Customer
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "activateCustomer", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.ActivateCustomer")
    @ResponseWrapper(localName = "activateCustomerResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.ActivateCustomerResponse")
    public Customer activateCustomer(
        @WebParam(name = "customer", targetNamespace = "")
        Customer customer,
        @WebParam(name = "userName", targetNamespace = "")
        String userName)
        throws Exception_Exception
    ;

    /**
     * 
     * @param status
     * @param bankId
     * @param mobileNumber
     * @return
     *     returns zw.co.esolutions.ewallet.customerservices.service.MobileProfile
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getMobileProfileByBankIdMobileNumberAndStatus", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.GetMobileProfileByBankIdMobileNumberAndStatus")
    @ResponseWrapper(localName = "getMobileProfileByBankIdMobileNumberAndStatusResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.GetMobileProfileByBankIdMobileNumberAndStatusResponse")
    public MobileProfile getMobileProfileByBankIdMobileNumberAndStatus(
        @WebParam(name = "bankId", targetNamespace = "")
        String bankId,
        @WebParam(name = "mobileNumber", targetNamespace = "")
        String mobileNumber,
        @WebParam(name = "status", targetNamespace = "")
        MobileProfileStatus status);

    /**
     * 
     * @param bankId
     * @param mobileNumber
     * @param secretCode
     * @return
     *     returns zw.co.esolutions.ewallet.customerservices.service.ResponseCode
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "authenticateMobileProfile", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.AuthenticateMobileProfile")
    @ResponseWrapper(localName = "authenticateMobileProfileResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.AuthenticateMobileProfileResponse")
    public ResponseCode authenticateMobileProfile(
        @WebParam(name = "bankId", targetNamespace = "")
        String bankId,
        @WebParam(name = "mobileNumber", targetNamespace = "")
        String mobileNumber,
        @WebParam(name = "secretCode", targetNamespace = "")
        String secretCode);

    /**
     * 
     * @param userName
     * @param customer
     * @return
     *     returns zw.co.esolutions.ewallet.customerservices.service.Customer
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "approveCustomer", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.ApproveCustomer")
    @ResponseWrapper(localName = "approveCustomerResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.ApproveCustomerResponse")
    public Customer approveCustomer(
        @WebParam(name = "customer", targetNamespace = "")
        Customer customer,
        @WebParam(name = "userName", targetNamespace = "")
        String userName)
        throws Exception_Exception
    ;

    /**
     * 
     * @param userName
     * @param customer
     * @return
     *     returns zw.co.esolutions.ewallet.customerservices.service.Customer
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "rejectCustomer", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.RejectCustomer")
    @ResponseWrapper(localName = "rejectCustomerResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.RejectCustomerResponse")
    public Customer rejectCustomer(
        @WebParam(name = "customer", targetNamespace = "")
        Customer customer,
        @WebParam(name = "userName", targetNamespace = "")
        String userName)
        throws Exception_Exception
    ;

    /**
     * 
     * @param userName
     * @param mobileprofile
     * @return
     *     returns zw.co.esolutions.ewallet.customerservices.service.MobileProfile
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "approveMobileNumber", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.ApproveMobileNumber")
    @ResponseWrapper(localName = "approveMobileNumberResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.ApproveMobileNumberResponse")
    public MobileProfile approveMobileNumber(
        @WebParam(name = "mobileprofile", targetNamespace = "")
        MobileProfile mobileprofile,
        @WebParam(name = "userName", targetNamespace = "")
        String userName)
        throws Exception_Exception
    ;

    /**
     * 
     * @param userName
     * @param mobileprofile
     * @return
     *     returns zw.co.esolutions.ewallet.customerservices.service.MobileProfile
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "rejectMobileNumber", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.RejectMobileNumber")
    @ResponseWrapper(localName = "rejectMobileNumberResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.RejectMobileNumberResponse")
    public MobileProfile rejectMobileNumber(
        @WebParam(name = "mobileprofile", targetNamespace = "")
        MobileProfile mobileprofile,
        @WebParam(name = "userName", targetNamespace = "")
        String userName)
        throws Exception_Exception
    ;

    /**
     * 
     * @param status
     * @param lastBranch
     * @return
     *     returns java.util.List<zw.co.esolutions.ewallet.customerservices.service.Customer>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getCustomerByStatusAndLastBranch", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.GetCustomerByStatusAndLastBranch")
    @ResponseWrapper(localName = "getCustomerByStatusAndLastBranchResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.GetCustomerByStatusAndLastBranchResponse")
    public List<Customer> getCustomerByStatusAndLastBranch(
        @WebParam(name = "status", targetNamespace = "")
        CustomerStatus status,
        @WebParam(name = "lastBranch", targetNamespace = "")
        String lastBranch);

    /**
     * 
     * @param userName
     * @param mobileprofile
     * @return
     *     returns zw.co.esolutions.ewallet.customerservices.service.MobileProfile
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "deRegisterMobileProfile", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.DeRegisterMobileProfile")
    @ResponseWrapper(localName = "deRegisterMobileProfileResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.DeRegisterMobileProfileResponse")
    public MobileProfile deRegisterMobileProfile(
        @WebParam(name = "mobileprofile", targetNamespace = "")
        MobileProfile mobileprofile,
        @WebParam(name = "userName", targetNamespace = "")
        String userName)
        throws Exception_Exception
    ;

    /**
     * 
     * @param userName
     * @param mobileProfile
     * @return
     *     returns zw.co.esolutions.ewallet.customerservices.service.MobileProfile
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "unLockMobileProfile", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.UnLockMobileProfile")
    @ResponseWrapper(localName = "unLockMobileProfileResponse", targetNamespace = "http://service.customerservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.customerservices.service.UnLockMobileProfileResponse")
    public MobileProfile unLockMobileProfile(
        @WebParam(name = "mobileProfile", targetNamespace = "")
        MobileProfile mobileProfile,
        @WebParam(name = "userName", targetNamespace = "")
        String userName)
        throws Exception_Exception
    ;

}
