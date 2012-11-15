package zw.co.esolutions.ewallet.process;
import javax.ejb.Local;

import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.msg.RequestInfo;
import zw.co.esolutions.ewallet.sms.ResponseCode;

@Local
public interface AutoRegService {

	Customer processCustomerAutoReg(RequestInfo requestInfo) throws Exception;

	MobileProfile processMobileProfileAutoReg(Customer customer, RequestInfo requestInfo) throws Exception;

	BankAccount processBankAccountAutoReg(Customer customer, RequestInfo requestInfo, MobileProfile mobileProfile) throws Exception;

	void deleteCustomer(Customer customer);

	void deleteMobileProfile(MobileProfile mobileProfile);

	void synchAutoRegDetails(Customer customer, MobileProfile mobileProfile, BankAccount bankAccount) throws Exception;

	ContactDetails processContactDetailsAutoReg(Customer customer) throws Exception;
	
	ResponseCode processNonHolderAutoRegistration(RequestInfo requestInfo);

	
}
