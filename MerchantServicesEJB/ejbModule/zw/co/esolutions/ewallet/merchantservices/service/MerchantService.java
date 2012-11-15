package zw.co.esolutions.ewallet.merchantservices.service;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import zw.co.esolutions.ewallet.enums.BankMerchantStatus;
import zw.co.esolutions.ewallet.enums.CustomerMerchantStatus;
import zw.co.esolutions.ewallet.enums.MerchantStatus;
import zw.co.esolutions.ewallet.merchantservices.model.BankMerchant;
import zw.co.esolutions.ewallet.merchantservices.model.CustomerMerchant;
import zw.co.esolutions.ewallet.merchantservices.model.Merchant;

@WebService(name = "MerchantService")
public interface MerchantService {

	Merchant createMerchant(@WebParam(name = "merchant") Merchant merchant, @WebParam(name = "userName") String userName) throws Exception;

	String deleteMerchant(@WebParam(name = "merchant") Merchant merchant, @WebParam(name = "userName") String userName) throws Exception;

	Merchant approveMerchant(@WebParam(name = "merchant") Merchant merchant, @WebParam(name = "userName") String userName) throws Exception;

	Merchant disapproveMerchant(@WebParam(name = "merchant") Merchant merchant, @WebParam(name = "userName") String userName) throws Exception;

	Merchant editMerchant(@WebParam(name = "merchant") Merchant merchant, @WebParam(name = "userName") String userName) throws Exception;

	Merchant findMerchantById(@WebParam(name = "id") String id);

	Merchant getMerchantByShortName(@WebParam(name = "shortName") String shortName) throws Exception;

	List<Merchant> getMerchantByName(@WebParam(name = "name") String name) throws Exception;

	List<Merchant> getAllMerchants() throws Exception;

	List<Merchant> getMerchantByStatus(@WebParam(name = "status") MerchantStatus status) throws Exception;

	CustomerMerchant createCustomerMerchant(@WebParam(name = "customerMerchant") CustomerMerchant customerMerchant, @WebParam(name = "userName") String userName) throws Exception;

	CustomerMerchant approveCustomerMerchant(@WebParam(name = "customerMerchant") CustomerMerchant customerMerchant, @WebParam(name = "userName") String userName) throws Exception;

	CustomerMerchant disapproveCustomerMerchant(@WebParam(name = "customerMerchant") CustomerMerchant customerMerchant, @WebParam(name = "userName") String userName) throws Exception;

	CustomerMerchant editCustomerMerchant(@WebParam(name = "customerMerchant") CustomerMerchant customerMerchant, @WebParam(name = "userName") String userName) throws Exception;

	String deleteCustomerMerchant(@WebParam(name = "customerMerchant") CustomerMerchant customerMerchant, @WebParam(name = "userName") String userName) throws Exception;

	CustomerMerchant findCustomerMerchantById(@WebParam(name = "id") String id);

	CustomerMerchant getCustomerMerchantByBankMerchantIdAndCustomerIdAndCustomerAccountNumber(@WebParam(name = "bankMerchantId") String bankMerchantId, @WebParam(name = "customerId") String customerId, @WebParam(name = "customerAccountNumber") String customerAccountNumber) throws Exception;

	List<CustomerMerchant> getCustomerMerchantByBankId(@WebParam(name = "bankId") String bankId) throws Exception;

	List<CustomerMerchant> getCustomerMerchantByCustomerId(@WebParam(name = "customerId") String customerId) throws Exception;

	List<CustomerMerchant> getCustomerMerchantByBankMerchantId(@WebParam(name = "bankMerchantId") String bankMerchantId) throws Exception;

	List<CustomerMerchant> getCustomerMerchantByCustomerAccountNumber(@WebParam(name = "customerAccountNumber") String customerAccountNumber) throws Exception;

	List<CustomerMerchant> getCustomerMerchantByStatus(@WebParam(name = "status") CustomerMerchantStatus status) throws Exception;

	BankMerchant createBankMerchant(@WebParam(name = "bankMerchant") BankMerchant bankMerchant, @WebParam(name = "userName") String username) throws Exception;

	BankMerchant approveBankMerchant(@WebParam(name = "bankMerchant") BankMerchant bankMerchant, @WebParam(name = "userName") String username) throws Exception;

	BankMerchant deleteBankMerchant(@WebParam(name = "bankMerchant") BankMerchant bankMerchant, @WebParam(name = "userName") String username) throws Exception;

	BankMerchant editBankMerchant(@WebParam(name = "bankMerchant") BankMerchant bankMerchant, @WebParam(name = "userName") String username) throws Exception;

	BankMerchant disapproveBankMerchant(@WebParam(name = "bankMerchant") BankMerchant bankMerchant, @WebParam(name = "userName") String username) throws Exception;

	BankMerchant findBankMerchantById(@WebParam(name = "bankMerchantId") String bankMerchantId);

	BankMerchant getBankMerchantByStatusAndBankIdAndMerchantId(@WebParam(name = "status") BankMerchantStatus status, @WebParam(name = "bankId") String bankId, @WebParam(name = "merchantId") String merchantId);

	BankMerchant getBankMerchantByBankIdAndMerchantId(@WebParam(name = "bankId") String bankId, @WebParam(name = "merchantId") String merchantId);

	List<BankMerchant> getBankMerchantByStatus(@WebParam(name = "status") BankMerchantStatus status);

	List<BankMerchant> getBankMerchantByBankId(String bankId) throws Exception;

	List<BankMerchant> getBankMerchantByMerchantId(String merchantId) throws Exception;

	BankMerchant getBankMerchantByBankIdAndShortNameAndStatus(String bankId, String shortName, BankMerchantStatus status) throws Exception;

	CustomerMerchant getCustomerMerchantByCustomerIdAndBankMerchantIdAndStatus(String customerId, String bankMerchantId, CustomerMerchantStatus status) throws Exception;

	List<Merchant> getMerchantByCustomerId(String customerId) throws Exception;

	CustomerMerchant getCustomerMerchantByCustomerIdAndMerchantShortNameAndStatus(@WebParam(name = "customerId") String customerId, @WebParam(name = "merchantShortName") String merchantShortName, @WebParam(name = "status") CustomerMerchantStatus status) throws Exception;
}
