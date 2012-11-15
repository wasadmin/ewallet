

package zw.co.esolutions.ewallet.customerservices.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.ewallet.audit.AuditEvents;
import zw.co.esolutions.ewallet.audittrailservices.service.AuditTrailServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.model.Customer;
import zw.co.esolutions.ewallet.customerservices.model.MobileProfile;
import zw.co.esolutions.ewallet.customerservices.util.GenerateTxnPassCodeResp;
import zw.co.esolutions.ewallet.customerservices.util.ValidateTxnPassCodeReq;
import zw.co.esolutions.ewallet.customerservices.wrapper.CustomerWrapper;
import zw.co.esolutions.ewallet.enums.CustomerAutoRegStatus;
import zw.co.esolutions.ewallet.enums.CustomerStatus;
import zw.co.esolutions.ewallet.enums.MobileProfileStatus;
import zw.co.esolutions.ewallet.search.SearchUtil;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.EncryptAndDecrypt;
import zw.co.esolutions.ewallet.util.GenerateKey;
import zw.co.esolutions.ewallet.util.NumberUtil;
import zw.co.esolutions.ewallet.util.SystemConstants;

/**
 * Session Bean implementation class CustomerServiceImpl
 */
@Stateless
@WebService(endpointInterface = "zw.co.esolutions.ewallet.customerservices.service.CustomerService", serviceName = "CustomerService", portName = "CustomerServiceSOAP")
public class CustomerServiceImpl implements CustomerService {

	@PersistenceContext
	private EntityManager em;

	/**
	 * Default constructor.
	 */
	public CustomerServiceImpl() {

	}

	

	private static Logger LOG ;
	
	static{
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(CustomerServiceImpl.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + CustomerServiceImpl.class);
		}
	}
	
	
	public Customer createCustomer(Customer customer, String userName) throws Exception {

		if (customer.getId() == null) {
			customer.setId(GenerateKey.generateEntityId());
		}
		if (customer.getDateCreated() == null) {
			customer.setDateCreated(new Date());
		}
		customer.setFieldsToUpperCase();

		try {
			em.persist(customer);
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.CREATE_CUSTOMER, customer.getId(), customer.getEntityName(), null, customer.getAuditableAttributesString(), customer.getInstanceName());
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} 
		return customer;
	}

	
	public String deleteCustomer(Customer customer, String userName) throws Exception {

		try {
			String oldCustomer = this.findCustomerById(customer.getId()).getAuditableAttributesString();
			customer.setStatus(CustomerStatus.DELETED);
			customer = em.merge(customer);
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.DELETE_CUSTOMER, customer.getId(), customer.getEntityName(), oldCustomer, customer.getAuditableAttributesString(), customer.getInstanceName());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return "";
	}

	
	public Customer updateCustomer(Customer customer, String userName) throws Exception {
		customer.setFieldsToUpperCase();
		try {
			String oldCustomer = this.findCustomerById(customer.getId()).getAuditableAttributesString();
			customer = em.merge(customer);
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.UPDATE_CUSTOMER, customer.getId(), customer.getEntityName(), oldCustomer, customer.getAuditableAttributesString(), customer.getInstanceName());
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.debug("Error:"+e.getMessage());
		} finally {

		}
		return customer;
	}

	public Customer findCustomerById(String id) {
		Customer customer = null;
		try {
			customer = (Customer) em.find(Customer.class, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return customer;
	}

	@SuppressWarnings("unchecked")
	public List<Customer> getCustomerByGender(String gender) {
		List<Customer> results = null;
		try {
			Query query = em.createNamedQuery("getCustomerByGender");
			query.setParameter("gender", gender);
			results = (List<Customer>) query.getResultList();
		} catch (NoResultException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;

	}

	@SuppressWarnings("unchecked")
	public List<Customer> getCustomerByLastName(String lastName) {
		List<Customer> results = null;
		try {
			Query query = em.createNamedQuery("getCustomerByLastName");
			query.setParameter("lastName", "%" + lastName.toUpperCase() + "%");
			results = (List<Customer>) query.getResultList();
		} catch (NoResultException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;

	}

	@SuppressWarnings("unchecked")
	public List<Customer> getCustomerByDateOfBirth(String dateOfBirth) {
		List<Customer> results = null;
		try {
			Query query = em.createNamedQuery("getCustomerByDateOfBirth");
			query.setParameter("dateOfBirth", dateOfBirth);
			results = (List<Customer>) query.getResultList();
		} catch (NoResultException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;

	}

	@SuppressWarnings("unchecked")
	public List<Customer> getCustomerByNationalId(String nationalId) {
		List<Customer> results = null;
		try {
			Query query = em.createNamedQuery("getCustomerByNationalId");
			query.setParameter("nationalId", "%" + nationalId.toUpperCase() + "%");
			results = (List<Customer>) query.getResultList();
		} catch (NoResultException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;

	}

	@SuppressWarnings("unchecked")
	public List<Customer> getCustomerByBranchId(String branchId) {
		List<Customer> results = null;
		try {
			Query query = em.createNamedQuery("getCustomerByBranchId");
			query.setParameter("branchId", branchId);
			results = (List<Customer>) query.getResultList();
		} catch (NoResultException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<Customer> getCustomerByStatus(CustomerStatus status) {
		List<Customer> results = null;
		try {
			Query query = em.createNamedQuery("getCustomerByStatus");
			query.setParameter("status", status);
			results = (List<Customer>) query.getResultList();
		} catch (NoResultException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}

	
	
	
	public MobileProfile createMobileProfile(MobileProfile mobileProfile,String source, String userName) throws Exception {
		if (mobileProfile.getId() == null) {
			mobileProfile.setId(GenerateKey.generateEntityId());
		}
		if (mobileProfile.getDateCreated() == null) {
			mobileProfile.setDateCreated(new Date());
		}
		if (mobileProfile.getSecretCode() == null) {
			String code = GenerateKey.generateSecurityCode();
			if(SystemConstants.SOURCE_APPLICATION_BANK.equals(source)){
				String encryptedCode = EncryptAndDecrypt.encrypt(code,mobileProfile.getMobileNumber());
				mobileProfile.setSecretCode(encryptedCode);
			}else{
				mobileProfile.setSecretCode(code);
			}		
		}

		try {
			//MobileProfile m = this.getMobileProfileByMobileNumber(mobileProfile.getMobileNumber());
			MobileProfile m=checkIfMobileProfileExists(mobileProfile,source);
			LOG.debug("checking >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>mobile profile>>>>>>>>>>>>>>"+m);
			if (m != null) {
				throw new Exception("Mobile Profile with number "+m.getMobileNumber()+" already exists.");
			}
			em.persist(mobileProfile);
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.CREATE_MOBILE_PROFILE, mobileProfile.getId(), mobileProfile.getEntityName(), null, mobileProfile.getAuditableAttributesString(), mobileProfile.getInstanceName());
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		LOG.debug(">>>>>>>>>>>>>>>>>>>> Finished persisting Mobile profile " + mobileProfile.getMobileNumber());
		return mobileProfile;
	}
	
	private MobileProfile checkIfMobileProfileExists(MobileProfile mobileProfile,
			String source){
		LOG.debug(">>>>>>>>>>>>>>>>>>>>>>checking mobile do we have a mobileprofile>>>>>>>>>>>>>>>>>>>>>>>>"+mobileProfile);
		LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>checking mobile whats the source>>>>>>>>>>>>>>>>"+source);
	MobileProfile mobile=null;
	if(SystemConstants.SOURCE_APPLICATION_BANK.equalsIgnoreCase(source)){
		LOG.debug("bank code >>>>>>>>>>>>>>>>>>>>mobile number::::::::::"+mobileProfile.getMobileNumber());
		mobile=this.getMobileProfileByMobileNumber(mobileProfile.getMobileNumber());
	}
	else if(SystemConstants.SOURCE_APPLICATION_SWITCH.equalsIgnoreCase(source)){
	
		LOG.debug("switch code:::::::::");
		LOG.debug("Bank id:::::::::::::::::::::::::"+mobileProfile.getBankId());
		LOG.debug("Mobile Number:::::::::::::::::::"+mobileProfile.getMobileNumber());
		mobile=this.getMobileProfileByBankAndMobileNumber(mobileProfile.getBankId(), mobileProfile.getMobileNumber());
	}
	LOG.debug(">>>>>>>>>>>>>>>>>>>done checking existen of mobileprofile send our result>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+mobile);
		return mobile;
	}


	public MobileProfile resetMobileProfilePin(MobileProfile mobileProfile, String userName) throws Exception {
		
		String code = GenerateKey.generateSecurityCode();
		mobileProfile.setSecretCode(EncryptAndDecrypt.encrypt(code, mobileProfile.getMobileNumber())); // encrypt
		
		try {
			
			mobileProfile=em.merge(mobileProfile);
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			String narrative = "Reset of pin for mobile profile with mobile number "+mobileProfile.getMobileNumber();
			auditService.logActivity(userName, AuditEvents.RESET_MOBILE_PROFILE_PIN, mobileProfile.getId(), mobileProfile.getEntityName(), narrative, mobileProfile.getAuditableAttributesString(), mobileProfile.getInstanceName());
//			auditService.logActivityWithNarrative(userName, AuditEvents.RESET_MOBILE_PROFILE_PIN, "Reset of pin for mobile profile with mobile number "+mobileProfile.getMobileNumber());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		//LOG.debug(">>>>>>>>>>>>>>>>>>>> Finished persisting Mobile profile " + mobileProfile);
		return mobileProfile;
	}

	
	public String deleteMobileProfile(MobileProfile mobileProfile, String userName) throws Exception {

		try {
			String oldMobileProfile = this.findMobileProfileById(mobileProfile.getId()).getAuditableAttributesString();
			mobileProfile.setStatus(MobileProfileStatus.DELETED);
			mobileProfile = em.merge(mobileProfile);
			//em.remove(mobileProfile);
			
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.DELETE_MOBILE_PROFILE, mobileProfile.getId(), mobileProfile.getEntityName(), oldMobileProfile, mobileProfile.getAuditableAttributesString(), mobileProfile.getInstanceName());
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		 finally {

		}
		return "";
	}

	public MobileProfile updateMobileProfile(MobileProfile mobileProfile, String userName) throws Exception {

		try {
			String oldMobileProfile = this.findMobileProfileById(mobileProfile.getId()).getAuditableAttributesString();
			mobileProfile = em.merge(mobileProfile);
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.UPDATE_MOBILE_PROFILE, mobileProfile.getId(), mobileProfile.getEntityName(), oldMobileProfile, mobileProfile.getAuditableAttributesString(), mobileProfile.getInstanceName());
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		 finally {

		}
		return mobileProfile;
	}

	public MobileProfile findMobileProfileById(String id) {
		MobileProfile mobileProfile = null;

		try {
			mobileProfile = (MobileProfile) em.find(MobileProfile.class, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mobileProfile;
	}

	@SuppressWarnings("unchecked")
	public List<MobileProfile> getMobileProfileByStatus(String status) {
		LOG.debug(">>>>>>>>>>>>>in method");
		List<MobileProfile> results = null;
		try {
			Query query = em.createNamedQuery("getMobileProfileByStatus");
			query.setParameter("status", MobileProfileStatus.valueOf(status));
			results = (List<MobileProfile>) query.getResultList();
		} catch (NoResultException e) {
			//System.err.println(e.getMessage());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		LOG.debug(">>>>>>> results " + results.size());
		return results;

	}

	public MobileProfile getMobileProfileByMobileNumber(String mobileNumber) {
		MobileProfile results = null;
		try {
			Query query = em.createNamedQuery("getMobileProfileByMobileNumber");
			query.setParameter("mobileNumber", mobileNumber);
			query.setParameter("status", MobileProfileStatus.DELETED);
			results = (MobileProfile) query.getSingleResult();
		} catch (NoResultException e) {
			//System.err.println(e.getMessage());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return results;

	}

	public MobileProfile getMobileProfileByBankAndMobileNumber(String bankId, String mobileNumber) {
		MobileProfile results = null;
		try {
			Query query = em.createNamedQuery("getMobileProfileByBankAndMobileNumber");
			query.setParameter("bankId", bankId);
			query.setParameter("mobileNumber", mobileNumber);
			query.setParameter("status", MobileProfileStatus.DELETED);
			results = (MobileProfile) query.getSingleResult();
		} catch (NoResultException e) {
			//System.err.println(e.getMessage());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<MobileProfile> getMobileProfileListByMobileNumber(String mobileNumber) {
		List<MobileProfile> results = null;
		try {
			Query query = em.createNamedQuery("getMobileProfileListByMobileNumber");
			query.setParameter("mobileNumber", mobileNumber);
			query.setParameter("status", MobileProfileStatus.DELETED);
			results = (List<MobileProfile>) query.getResultList();
		} catch (NoResultException e) {
			//System.err.println(e.getMessage());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;

	}

	@SuppressWarnings("unchecked")
	public List<MobileProfile> getMobileProfileByCustomer(String customerId) {
		List<MobileProfile> results = null;
		try {
			Query query = em.createNamedQuery("getMobileProfileByCustomer");
			query.setParameter("customer_id", customerId);
			query.setParameter("status", MobileProfileStatus.DELETED);
			results = (List<MobileProfile>) query.getResultList();
		} catch (NoResultException e) {
			//System.err.println(e.getMessage());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;

	}

	public boolean mobileProfileIsActive(String mobileNumber) {
		if (mobileNumber != null) {
			try {
				MobileProfile mobileProfile = this.getMobileProfileByMobileNumber(mobileNumber);
				if (mobileProfile != null) {
					if (mobileProfile.getStatus().equals(MobileProfileStatus.ACTIVE)) {
						Customer customer = mobileProfile.getCustomer();
						if (customer.getStatus().equals(CustomerStatus.ACTIVE)) {
							return true;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public GenerateTxnPassCodeResp generateTxnPassCode(String mobileId) {
		if (mobileId != null) {
			try {
				MobileProfile mobileProfile = this.findMobileProfileById(mobileId);
				if (mobileProfile != null || mobileId != null) {
					Integer firstIndex = 10, secondIndex = 10;
					while (firstIndex < 1 || firstIndex > 5) {
						firstIndex = (int) (Math.random() * 10);
					}
					while (secondIndex < 1 || secondIndex > 5 || firstIndex == secondIndex) {
						secondIndex = (int) (Math.random() * 10);
					}
					LOG.debug("########## " + firstIndex + secondIndex);
					GenerateTxnPassCodeResp response = new GenerateTxnPassCodeResp();
					if (firstIndex > secondIndex) {
						response.setFirstIndex(secondIndex);
						response.setSecondIndex(firstIndex);
					} else {
						response.setFirstIndex(firstIndex);
						response.setSecondIndex(secondIndex);
					}

					return response;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public boolean txnPassCodeIsValid(ValidateTxnPassCodeReq request) {
		if (request != null) {

			try {
				MobileProfile mobileProfile = this.getMobileProfileByMobileNumber(request.getMobileNumber());
				if (mobileProfile != null) {
					String passCode = mobileProfile.getSecretCode();
					LOG.debug(">>>>>>>>>>>>>>>>>>>> Mobile "+request.getMobileNumber());
					passCode = EncryptAndDecrypt.decrypt(passCode,mobileProfile.getMobileNumber());
					//LOG.debug("In Customer Service Impl >>>>>>>>>>>>> PassCode = "+passCode);
					//LOG.debug("In Customer Service Impl >>>>>>>>>>>>> 1st  Index = "+request.getFirstIndex() +" 1st Value = "+request.getFirstValue());
					//LOG.debug("In Customer Service Impl >>>>>>>>>>>>> 2nd  Index = "+request.getSecondIndex() +" 2nd Value = "+request.getSecondValue());
					if (passCode.substring(request.getFirstIndex() - 1, request.getFirstIndex()).equals(request.getFirstValue().toString())) {
						if (passCode.substring(request.getSecondIndex() - 1, request.getSecondIndex()).equals(request.getSecondValue().toString())) {
							//clear the password retry count..
							mobileProfile.setPasswordRetryCount(0);
							this.updateMobileProfile(mobileProfile, EWalletConstants.SYSTEM);
							LOG.debug("Passcode is Correct. Return "+true);
							return true;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		LOG.debug("Passcode Incorrect. Return "+false);
		return false;
	}

	public String formatMobileNumber(String mobileNumber) throws Exception {
		if (mobileNumber == null) {
			throw new Exception("Mobile number is NULL");
		}

		mobileNumber = mobileNumber.trim().replace(" ", "");

		if (mobileNumber.startsWith("+263")) {
			mobileNumber = mobileNumber.substring(1);
		}

		if (mobileNumber.startsWith("07")) {
			mobileNumber = "263" + mobileNumber.substring(1);
		}

		if (mobileNumber.startsWith("2637")) {
			if (mobileNumber.length() != 12) {
				throw new Exception("Mobile number length must be 12: yours has " + mobileNumber.length());
			}
		} else {
			throw new Exception("Mobile Number is invalid: does not contain 07 prefix.");
		}
		return mobileNumber;
	}

	public List<Customer> getCustomersByWrapper(
			CustomerWrapper customerWrapper, String userName) throws Exception {
		SearchUtil searchUtil= new SearchUtil();
		boolean customerStatus=false;
		boolean customerClass=false;
		boolean dateStatus=false;
		
		//String queryMessage = "SELECT b FROM Customer b WHERE";
		LOG.debug(" start date ::::"+customerWrapper.getStartDate());
		LOG.debug(" end date ::::"+customerWrapper.getEndDate());
		String queryMessage="SELECT DISTINCT m.customer FROM MobileProfile m WHERE ";
		if (customerWrapper.getMobileNumber() != null) {

			if (searchUtil.appendAnd(queryMessage)) {
				queryMessage += " AND ";
			}
			
			queryMessage += " m.mobileNumber LIKE '%"
					+ NumberUtil.formatMobile(customerWrapper.getMobileNumber()) + "%'";
		}
		
		
		if (customerWrapper.getLastName() != null) {

			if (searchUtil.appendAnd(queryMessage)) {
				queryMessage += " AND ";
			}
			
			queryMessage += " m.customer.lastName LIKE '%"
					+ customerWrapper.getLastName().toUpperCase() + "%'";
		}

		if (customerWrapper.getCustomerClass() != null) {
			

			if (searchUtil.appendAnd(queryMessage)) {
				queryMessage += " AND ";
			}

			queryMessage += " m.customer.customerClass =: customerClass ";
			customerClass=true;
		}
		
		if (customerWrapper.getStatus() != null) {
			

			if (searchUtil.appendAnd(queryMessage)) {
				queryMessage += " AND ";
			}

			queryMessage += " m.customer.status =: status";
			customerStatus=true;
		}
		
		if (customerWrapper.getStartDate() != null && customerWrapper.getEndDate() != null) {
			LOG.debug("date range Query Message :::::::  "+queryMessage);
			if (searchUtil.appendAnd(queryMessage)) {
				queryMessage += " AND ";
			}

			queryMessage += "m.customer.dateCreated >=: fromDate AND m.customer.dateCreated <=: toDate ";
			dateStatus=true;
		}
		queryMessage+=" ORDER BY m.customer.lastName ASC";
		
		LOG.debug("Query Message :::::::  "+queryMessage);
		Query query = em.createQuery(queryMessage);
		if(customerStatus){
		query.setParameter("status", customerWrapper.getStatus());
		}
		if(customerClass){
		query.setParameter("customerClass", customerWrapper.getCustomerClass());
		}
		if(dateStatus){
			query.setParameter("fromDate", DateUtil.getBeginningOfDay(customerWrapper.getStartDate()));
			query.setParameter("toDate", DateUtil.getEndOfDay(customerWrapper.getEndDate()));
		}
		List<Customer> customers = query.getResultList();

		if (customers.size() > 0) {
			return customers;
		} else {
			return null;
		}
		
	}


	public MobileProfile coldMobileNumber(MobileProfile mobileProfile, String userName) throws Exception{
		try {
			mobileProfile.setStatus(MobileProfileStatus.ACTIVE);
			mobileProfile=updateMobileProfile(mobileProfile, userName);

			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.COLD_MOBILE_PROFILE, mobileProfile.getId(), mobileProfile.getEntityName(), null, mobileProfile.getAuditableAttributesString(), mobileProfile.getInstanceName());
		
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		
		return mobileProfile;
		
	}


	public MobileProfile hotMobileNumber(MobileProfile mobileProfile, String userName) throws Exception {
		try {
			mobileProfile.setStatus(MobileProfileStatus.HOT);
			mobileProfile= em.merge(mobileProfile);

			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.HALT_MOBILE_PROFILE, mobileProfile.getId(), mobileProfile.getEntityName(), null, mobileProfile.getAuditableAttributesString(), mobileProfile.getInstanceName());
		
		} catch (Exception e) {
		
			e.printStackTrace();
			throw new Exception(e);
		}
		
		return mobileProfile;
		
	}


	public Customer deregisterCustomer(Customer customer, String userName) throws Exception {
		try {
			
			//deregister mobile profile
			deregisterMobileProfile(customer,userName);
			customer.setStatus(CustomerStatus.DELETED);
			customer = em.merge(customer);
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.DEREGISTER_CUSTOMER, customer.getId(), customer.getEntityName(), null, customer.getAuditableAttributesString(), customer.getInstanceName());
		} catch (Exception e) {
		
			e.printStackTrace();
			throw new Exception(e);
		}
		
		return customer;
		
	}




	private void deregisterMobileProfile(Customer customer,String userName) throws Exception {
	try {
		List<MobileProfile> mobileProfiles= getMobileProfileByCustomer(customer.getId());
		for(MobileProfile mobile : mobileProfiles){
		
			deleteMobileProfile(mobile, userName);
		}
	} catch (Exception e) {
		
		e.printStackTrace();
		throw new Exception(e);
	}
		
	}


	public Customer activateCustomer(Customer customer, String userName) throws Exception {
		try {
			String oldCustomer = customer.getAuditableAttributesString();
			customer.setStatus(CustomerStatus.ACTIVE);
			customer = em.merge(customer);
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.ACTIVATE_CUSTOMER, customer.getId(), customer.getEntityName(), oldCustomer, customer.getAuditableAttributesString(), customer.getInstanceName());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return customer;
		
	}


	public Customer suspendCustomer(Customer customer, String userName) throws Exception {
		try {
			String oldCustomer = customer.getAuditableAttributesString();
			customer.setStatus(CustomerStatus.SUSPENDED);
			LOG.debug("Customer before ::::"+customer.getStatus());
			customer =updateCustomer(customer, userName);
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.SUSPEND_CUSTOMER, customer.getId(), customer.getEntityName(), oldCustomer, customer.getAuditableAttributesString(), customer.getInstanceName());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return customer;
		
	}


	public MobileProfile getMobileProfileByBankIdMobileNumberAndStatus(String bankId, String mobileNumber, MobileProfileStatus status) {
		
		/*
		 * This is a stub write tthe in two weeks
		 * Response code should state 1. retrieval successsful
		 * 2. either mobileprofile or customer is not active
		 * 3. in valid secret code parts
		 * 
		 * 
		 * Only get non ewallet accounts
		 */
		MobileProfile results = null;
		try {
			Query query = em.createNamedQuery("getMobileProfileByBankIdMobileNumberAndStatus");

			query.setParameter("bankId", bankId);
			query.setParameter("mobileNumber", mobileNumber);
			query.setParameter("status", status);
		
			results = (MobileProfile) query.getSingleResult();
		} catch (NoResultException e) {
			//System.err.println(e.getMessage());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return results;
		
		
	}


	public ResponseCode authenticateMobileProfile(String bankId, String mobileNumber, String secretCode) {
		/*
		 * method used to authenticate mobile profile
		 * shld increment the passwordretry count for every incorrect authentication attempt and update the profile
		 * upon successful authentication set the value to zero
		 * then if the count has reached 3 lock the mobile profile
		 * 
		 */
		int passwordCount=0;
		MobileProfile mobileProfile=getMobileProfileByBankIdMobileNumberAndStatus(bankId, mobileNumber, MobileProfileStatus.ACTIVE);
		LOG.debug("   mobile profile ....>>>>>>>>>>>>>>>>>>>>>>"+mobileProfile);
		try {
			if(mobileProfile==null){
				LOG.debug("mobile profile>>>>>>>>null mobile profile no active mobile profile");
				return ResponseCode.E401;
			}else if(mobileProfile!=null){
				LOG.debug("mobile profile is nt null....>>>>>>>>>>>>>>>>>>>>>>");
				String mobileSecretCode=mobileProfile.getSecretCode();
				LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>secret>>>>>>>"+mobileSecretCode);
				passwordCount=mobileProfile.getPasswordRetryCount();
				LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>passwordCount>>>>>>>>>>>>>>"+passwordCount);
				
				if(mobileSecretCode.equalsIgnoreCase(EncryptAndDecrypt.encrypt(secretCode, mobileNumber))){
					LOG.debug(">>>>>>>>>>>>>>>>>success>>>>>>>>>>>>>>>>>>>>>>>");
					LOG.debug(">>>>>>>>>>>>>>>>sucess>>>>>>>>>>>>>>>>>>success");
					return ResponseCode.E000;
				}else{
					++passwordCount;
					if(passwordCount>=3){
						mobileProfile.setStatus(MobileProfileStatus.LOCKED);
						mobileProfile.setTimeout(DateUtil.getNextTimeout(new Timestamp(System.currentTimeMillis()), 3));
						this.updateMobileProfile(mobileProfile, EWalletConstants.SYSTEM);
						LOG.debug(">>>>>>>>>>>>>>>>>>>>Account lockecked now >>>>>>>>>>>>>>>>>>>>>>>");
						return ResponseCode.E403;
					}
					LOG.debug(">>>>>>>>>>>>>>>>>>>>invalid pin>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
					mobileProfile.setPasswordRetryCount(passwordCount);
					incrementMobileProfilePasswordCount(mobileProfile);
					return ResponseCode.E400;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}


	private MobileProfile incrementMobileProfilePasswordCount(MobileProfile mobileProfile) {
		mobileProfile=em.merge(mobileProfile);
		return mobileProfile;
		
	} 
	
	private MobileProfile lockMobileProfile(MobileProfile mobileProfile){
		mobileProfile=em.merge(mobileProfile);
		return mobileProfile;
	}

	public List<Customer> getCustomerByStatusAndLastBranch(CustomerStatus status, String customerLastBranch) {
		List<Customer> results = null;
		try {
			Query query = em.createNamedQuery("getCustomerByStatusAndLastBranch");
			query.setParameter("status", status);
			query.setParameter("customerLastBranch", customerLastBranch);
			query.setMaxResults(20);
			results = (List<Customer>) query.getResultList();
		} catch (NoResultException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
		
	}

	@Override
	public Customer approveCustomer(Customer customer, String userName)
			throws Exception {
		customer.setFieldsToUpperCase();
		try {
			String oldCustomer = this.findCustomerById(customer.getId()).getAuditableAttributesString();
			customer.setStatus(CustomerStatus.ACTIVE);
			customer = this.checkAutoRegistration(customer);
			customer = em.merge(customer);
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.APPROVE_CUSTOMER, customer.getId(), customer.getEntityName(), oldCustomer, customer.getAuditableAttributesString(), customer.getInstanceName());
			
		} catch (Exception e) {
			throw e;
		} finally {

		}
		return customer;
	}

	private Customer checkAutoRegistration(Customer customer) {
		//check auto-reg
		if (CustomerAutoRegStatus.YES.equals(customer.getCustomerAutoRegStatus())) {
			customer.setCustomerAutoRegStatus(CustomerAutoRegStatus.NO);
		}
		return customer;
		
	}

	@Override
	public Customer rejectCustomer(Customer customer, String userName)
			throws Exception {
		customer.setFieldsToUpperCase();
		try {
			String oldCustomer = this.findCustomerById(customer.getId()).getAuditableAttributesString();
			customer.setStatus(CustomerStatus.DISAPPROVED);
			customer = em.merge(customer);
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.REJECT_CUSTOMER, customer.getId(), customer.getEntityName(), oldCustomer, customer.getAuditableAttributesString(), customer.getInstanceName());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return customer;
	}


	@Override
	public MobileProfile approveMobileNumber(MobileProfile mobileProfile,
			String userName) throws Exception {
		try {
			String oldMobileProfile = this.findMobileProfileById(mobileProfile.getId()).getAuditableAttributesString();
			mobileProfile.setStatus(MobileProfileStatus.ACTIVE);
			mobileProfile = em.merge(mobileProfile);
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.APPROVE_MOBILE_PROFILE, mobileProfile.getId(), mobileProfile.getEntityName(), oldMobileProfile, mobileProfile.getAuditableAttributesString(), mobileProfile.getInstanceName());
			
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		 finally {

		}
		return mobileProfile;
	}


	@Override
	public MobileProfile rejectMobileNumber(MobileProfile mobileProfile,
			String userName) throws Exception {
		try {
			String oldMobileProfile = this.findMobileProfileById(mobileProfile.getId()).getAuditableAttributesString();
			mobileProfile.setStatus(MobileProfileStatus.DISAPPROVED);
			mobileProfile = em.merge(mobileProfile);
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.REJECT_MOBILE_PROFILE, mobileProfile.getId(), mobileProfile.getEntityName(), oldMobileProfile, mobileProfile.getAuditableAttributesString(), mobileProfile.getInstanceName());
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		 finally {

		}
		return mobileProfile;
	}


	public MobileProfile unLockMobileProfile(MobileProfile mobileProfile,
			String userName) throws Exception {
		try {
			String oldMobileProfile = this.findMobileProfileById(mobileProfile.getId()).getAuditableAttributesString();
			mobileProfile.setStatus(MobileProfileStatus.ACTIVE);
			mobileProfile = em.merge(mobileProfile);
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.UNLOCK_MOBILE_PROFILE, mobileProfile.getId(), mobileProfile.getEntityName(), oldMobileProfile, mobileProfile.getAuditableAttributesString(), mobileProfile.getInstanceName());
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		 finally {

		}
		return mobileProfile;
		
		
	}


	public MobileProfile deRegisterMobileProfile(MobileProfile mobileProfile,
			String userName) throws Exception {
		try {
			String oldMobileProfile = this.findMobileProfileById(mobileProfile.getId()).getAuditableAttributesString();
			mobileProfile.setStatus(MobileProfileStatus.DELETED);
			mobileProfile = em.merge(mobileProfile);
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.DEREGISTER_MOBILE_PROFILE, mobileProfile.getId(), mobileProfile.getEntityName(), oldMobileProfile, mobileProfile.getAuditableAttributesString(), mobileProfile.getInstanceName());
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		 finally {

		}
		return mobileProfile;
		
	}
	
	
}
