package zw.co.esolutions.ewallet.customerservices.service;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import zw.co.esolutions.ewallet.customerservices.model.Customer;
import zw.co.esolutions.ewallet.customerservices.model.MobileProfile;
import zw.co.esolutions.ewallet.customerservices.util.GenerateTxnPassCodeResp;
import zw.co.esolutions.ewallet.customerservices.util.ValidateTxnPassCodeReq;
import zw.co.esolutions.ewallet.customerservices.wrapper.CustomerWrapper;
import zw.co.esolutions.ewallet.enums.CustomerStatus;
import zw.co.esolutions.ewallet.enums.MobileProfileStatus;
import zw.co.esolutions.ewallet.sms.ResponseCode;

@WebService(name="CustomerService")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
public interface CustomerService {

	Customer createCustomer(@WebParam(name="customer") Customer customer, @WebParam(name="userName") String userName) throws Exception;

	String deleteCustomer(@WebParam(name="customer") Customer customer, @WebParam(name="userName") String userName) throws Exception;

	Customer updateCustomer(@WebParam(name="customer") Customer customer, @WebParam(name="userName") String userName) throws Exception;

	Customer findCustomerById(@WebParam(name="id") String id);
	
	List<Customer> getCustomerByLastName(@WebParam(name="lastName") String lastName);
	
	List<Customer> getCustomerByNationalId(@WebParam(name="nationalId") String nationalId);
	
	List<Customer> getCustomerByDateOfBirth(@WebParam(name="dateOfBirth") String dateOfBirth);
	
	List<Customer> getCustomerByGender(@WebParam(name="gender") String gender);

	MobileProfile createMobileProfile(@WebParam(name="mobileProfile") MobileProfile mobileProfile,@WebParam(name="source") String source ,@WebParam(name="userName") String userName)
			throws Exception;
	
	MobileProfile resetMobileProfilePin(@WebParam(name="mobileProfile") MobileProfile mobileProfile, @WebParam(name="userName") String userName) throws Exception;

	String deleteMobileProfile(@WebParam(name="mobileProfile") MobileProfile mobileProfile, @WebParam(name="userName") String userName) throws Exception;

	MobileProfile updateMobileProfile(@WebParam(name="mobileProfile") MobileProfile mobileProfile, @WebParam(name="userName") String userName)
			throws Exception; 

	MobileProfile findMobileProfileById(@WebParam(name="id") String id);
	
	MobileProfile getMobileProfileByMobileNumber(@WebParam(name="mobileNumber") String mobileNumber);
	
	List<MobileProfile> getMobileProfileByStatus(@WebParam(name="status") String status);
	
	List<MobileProfile> getMobileProfileByCustomer(@WebParam(name="customerId") String customerId);

	List<Customer> getCustomerByBranchId(@WebParam(name="branchId") String branchId);

	GenerateTxnPassCodeResp generateTxnPassCode(@WebParam(name="mobileId") String mobileId);

	boolean txnPassCodeIsValid(@WebParam(name="request") ValidateTxnPassCodeReq request);

//	String formatMobileNumber(@WebParam(name="mobileNumber") String mobileNumber) throws Exception;

	boolean mobileProfileIsActive(@WebParam(name="mobileNumber") String mobileNumber);

	List<Customer> getCustomerByStatus(@WebParam(name="status")CustomerStatus status);

	MobileProfile getMobileProfileByBankAndMobileNumber(@WebParam(name="bankId")String bankId,
			@WebParam(name="mobileNumber")String mobileNumber);

	List<MobileProfile> getMobileProfileListByMobileNumber(@WebParam(name="mobileNumber")String mobileNumber);
	List<Customer> getCustomersByWrapper(@WebParam(name="customerWrapper")CustomerWrapper customerWrapper, @WebParam(name="userName") String userName) throws Exception;
	MobileProfile hotMobileNumber(@WebParam(name="mobileprofile")MobileProfile mobileProfile, @WebParam(name="userName") String userName)throws Exception;
	MobileProfile coldMobileNumber(@WebParam(name="mobileprofile")MobileProfile mobileProfile, @WebParam(name="userName") String userName) throws Exception;
	Customer deregisterCustomer(@WebParam(name="customer")Customer customer, @WebParam(name="userName")String userName) throws Exception;
	Customer suspendCustomer(@WebParam(name="customer")Customer customer, @WebParam(name="userName")String userName) throws Exception;
	Customer activateCustomer(@WebParam(name="customer")Customer customer, @WebParam(name="userName")String userName) throws Exception;
	MobileProfile getMobileProfileByBankIdMobileNumberAndStatus(@WebParam(name="bankId")String bankId,@WebParam(name="mobileNumber")String mobileNumber, @WebParam(name="status")MobileProfileStatus status);
	ResponseCode authenticateMobileProfile(@WebParam(name="bankId")String bankId,@WebParam(name="mobileNumber")String mobileNumber, @WebParam(name="secretCode")String secretCode);
	Customer approveCustomer(@WebParam(name="customer")Customer customer, @WebParam(name="userName")String userName) throws Exception;
	Customer rejectCustomer(@WebParam(name="customer")Customer customer, @WebParam(name="userName")String userName) throws Exception;
	MobileProfile approveMobileNumber(@WebParam(name="mobileprofile")MobileProfile mobileProfile, @WebParam(name="userName") String userName) throws Exception;
	MobileProfile rejectMobileNumber(@WebParam(name="mobileprofile")MobileProfile mobileProfile, @WebParam(name="userName") String userName) throws Exception;
	List<Customer> getCustomerByStatusAndLastBranch(@WebParam(name="status")CustomerStatus status,@WebParam(name="lastBranch")
			String customerLastBranch);
	MobileProfile deRegisterMobileProfile(@WebParam(name="mobileprofile")MobileProfile mobileProfile, @WebParam(name="userName") String userName) throws Exception;
	MobileProfile unLockMobileProfile(@WebParam(name="mobileProfile")MobileProfile mobileProfile, @WebParam(name="userName") String userName) throws Exception;
	
	
}
