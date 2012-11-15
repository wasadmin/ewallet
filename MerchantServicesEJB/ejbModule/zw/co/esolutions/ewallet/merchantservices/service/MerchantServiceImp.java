package zw.co.esolutions.ewallet.merchantservices.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import zw.co.esolutions.ewallet.audit.AuditEvents;
import zw.co.esolutions.ewallet.audittrailservices.service.AuditTrailServiceSOAPProxy;
import zw.co.esolutions.ewallet.enums.CustomerMerchantStatus;
import zw.co.esolutions.ewallet.enums.MerchantStatus;
import zw.co.esolutions.ewallet.merchantservices.model.CustomerMerchant;
import zw.co.esolutions.ewallet.merchantservices.model.Merchant;
import zw.co.esolutions.ewallet.util.GenerateKey;
import zw.co.esolutions.ewallet.merchantservices.model.BankMerchant;
import zw.co.esolutions.ewallet.enums.BankMerchantStatus;

/**
 * Session Bean implementation class MerchantServiceImp
 */
@Stateless
@WebService(endpointInterface = "zw.co.esolutions.ewallet.merchantservices.service.MerchantService", serviceName = "MerchantService", portName = "MerchantServiceSOAP")
public class MerchantServiceImp implements MerchantService {

	@PersistenceContext(unitName = "MerchantServicesEJB")
	private EntityManager em;

	/**
	 * Default constructor.
	 */
	public MerchantServiceImp() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Merchant createMerchant(Merchant merchant, String userName) throws Exception {
		
		if (merchant.getId() == null) {
			merchant.setId(GenerateKey.generateEntityId());
		}
		if (merchant.getDateCreated() == null) {
			merchant.setDateCreated(new Date());
		}
		merchant.setFieldsToUpper();
		try {
			Merchant m = this.getMerchantByShortName(merchant.getShortName());
			if (m != null) {

				throw new EntityExistsException();
			}

			em.persist(merchant);

			// Audit trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.CREATE_MERCHANT, merchant.getId(), merchant.getEntityName(), null, merchant.getAuditableAttributesString(), merchant.getInstanceName());

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return merchant;
	}

	@Override
	public String deleteMerchant(Merchant merchant, String userName) throws Exception {
		try {
			String oldMerchant = this.findMerchantById(merchant.getId()).getAuditableAttributesString();
			merchant.setStatus(MerchantStatus.DELETED);
			merchant = em.merge(merchant);

			// Audit trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.DELETE_MERCHANT, merchant.getId(), merchant.getEntityName(), oldMerchant, merchant.getAuditableAttributesString(), merchant.getInstanceName());

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return "";
	}

	public Merchant approveMerchant(Merchant merchant, String userName) throws Exception {
		String oldMerchant = this.findMerchantById(merchant.getId()).getAuditableAttributesString();
		merchant.setStatus(MerchantStatus.ACTIVE);
		merchant = this.updateMerchant(merchant);

		// Audit trail
		AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
		auditService.logActivity(userName, AuditEvents.APPROVE_MERCHANT, merchant.getId(), merchant.getEntityName(), oldMerchant, merchant.getAuditableAttributesString(), merchant.getInstanceName());

		return merchant;
	}

	public Merchant editMerchant(Merchant merchant, String userName) throws Exception {
		String oldMerchant = this.findMerchantById(merchant.getId()).getAuditableAttributesString();

		merchant = this.updateMerchant(merchant);

		// Audit trail
		AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
		auditService.logActivity(userName, AuditEvents.EDIT_MERCHANT, merchant.getId(), merchant.getEntityName(), oldMerchant, merchant.getAuditableAttributesString(), merchant.getInstanceName());

		return merchant;
	}

	private Merchant updateMerchant(Merchant merchant) throws Exception {
		merchant.setFieldsToUpper();
		try {
			merchant = em.merge(merchant);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return merchant;
	}

	public CustomerMerchant createCustomerMerchant(CustomerMerchant customerMerchant, String userName) throws Exception {

		CustomerMerchant c = this.getCustomerMerchantByBankMerchantIdAndCustomerIdAndCustomerAccountNumber(customerMerchant.getBankMerchant().getId(), customerMerchant.getCustomerId(), customerMerchant.getCustomerAccountNumber());
		if (c != null) {
			throw new EntityExistsException();
		}

		customerMerchant.setId(GenerateKey.generateEntityId());
		customerMerchant.setDateCreated(new Date());
		em.persist(customerMerchant);

		// Adit Trail
		AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
		auditService.logActivity(userName, AuditEvents.CREATE_CUSTOMER_MERCHANT, customerMerchant.getId(), customerMerchant.getEntityName(), null, customerMerchant.getAuditableAttributesString(), customerMerchant.getInstanceName());

		return customerMerchant;
	}

	public CustomerMerchant approveCustomerMerchant(CustomerMerchant customerMerchant, String userName) throws Exception {
		String oldCustomerMerchant = this.findCustomerMerchantById(customerMerchant.getId()).getAuditableAttributesString();

		customerMerchant.setStatus(CustomerMerchantStatus.ACTIVE);
		customerMerchant = this.updateCustomerMerchant(customerMerchant);

		// Adit Trail
		AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
		auditService.logActivity(userName, AuditEvents.APPROVE_CUSTOMER_MERCHANT, customerMerchant.getId(), customerMerchant.getEntityName(), oldCustomerMerchant, customerMerchant.getAuditableAttributesString(), customerMerchant.getInstanceName());

		return customerMerchant;
	}

	public CustomerMerchant editCustomerMerchant(CustomerMerchant customerMerchant, String userName) throws Exception {
		String oldCustomerMerchant = this.findCustomerMerchantById(customerMerchant.getId()).getAuditableAttributesString();

		customerMerchant = this.updateCustomerMerchant(customerMerchant);

		// Adit Trail
		AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
		auditService.logActivity(userName, AuditEvents.EDIT_CUSTOMER_MERCHANT, customerMerchant.getId(), customerMerchant.getEntityName(), oldCustomerMerchant, customerMerchant.getAuditableAttributesString(), customerMerchant.getInstanceName());

		return customerMerchant;
	}

	public String deleteCustomerMerchant(CustomerMerchant customerMerchant, String userName) throws Exception {
		String oldCustomerMerchant = this.findCustomerMerchantById(customerMerchant.getId()).getAuditableAttributesString();

		customerMerchant.setStatus(CustomerMerchantStatus.DELETED);
		customerMerchant = this.updateCustomerMerchant(customerMerchant);

		// Adit Trail
		AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
		auditService.logActivity(userName, AuditEvents.DELETE_CUSTOMER_MERCHANT, customerMerchant.getId(), customerMerchant.getEntityName(), oldCustomerMerchant, customerMerchant.getAuditableAttributesString(), customerMerchant.getInstanceName());

		return "";
	}

	private CustomerMerchant updateCustomerMerchant(CustomerMerchant customerMerchant) throws Exception {
		try {
			customerMerchant = em.merge(customerMerchant);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return customerMerchant;
	}

	@Override
	public Merchant findMerchantById(String id) {
		Merchant merchant = null;
		try {
			merchant = (Merchant) em.find(Merchant.class, id);
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return merchant;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Merchant> getMerchantByName(String name) throws Exception {
		List<Merchant> merchants = null;
		name = name.toUpperCase();
		try {
			Query query = em.createNamedQuery("getMerchantByName");
			query.setParameter("name", "%" + name + "%");
			query.setParameter("statusDeleted", MerchantStatus.DELETED);
			merchants = (List<Merchant>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return merchants;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Merchant> getMerchantByStatus(MerchantStatus status) throws Exception {
		List<Merchant> merchants = null;

		try {
			Query query = em.createNamedQuery("getMerchantByStatus");
			query.setParameter("status", status);
			merchants = (List<Merchant>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return merchants;
	}

	@Override
	public Merchant getMerchantByShortName(String shortName) throws Exception {
		Merchant merchant = null;
	
		if (shortName == null) {
			return merchant;
		}
		
		try {
			Query query = em.createNamedQuery("getMerchantByShortName");
			query.setParameter("shortName", shortName.toUpperCase());
			query.setParameter("statusDeleted", MerchantStatus.DELETED);
			merchant = (Merchant) query.getSingleResult();
		} catch (NoResultException en) {
			return null;
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		return merchant;
	}

	
	@SuppressWarnings("unchecked")
	public List<Merchant> getAllMerchants() throws Exception {
		List<Merchant> merchants = null;
		try {
			Query query = em.createNamedQuery("getAllMerchant");
			query.setParameter("statusDeleted", MerchantStatus.DELETED);
			merchants = (List<Merchant>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return merchants;
	}

	public CustomerMerchant findCustomerMerchantById(String id){
		CustomerMerchant customerMerchant = null;
		try {
			customerMerchant = em.find(CustomerMerchant.class, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return customerMerchant;
	}

	public CustomerMerchant getCustomerMerchantByBankMerchantIdAndCustomerIdAndCustomerAccountNumber(String bankMerchantId, String customerId, String customerAccountNumber) throws Exception {
		CustomerMerchant customerMerchant = null;
		try {
			Query query = em.createNamedQuery("getCustomerMerchantByBankMerchantIdAndCustomerIdAndCustomerAccountNumber");
			query.setParameter("bankMerchantId", bankMerchantId);
			query.setParameter("customerId", customerId);
			query.setParameter("customerAccountNumber", customerAccountNumber);
			query.setParameter("statusDeleted", CustomerMerchantStatus.DELETED);
			customerMerchant = (CustomerMerchant) query.getSingleResult();

		} catch (NoResultException ne) {
			ne.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return customerMerchant;
	}

	@SuppressWarnings("unchecked")
	public List<CustomerMerchant> getCustomerMerchantByBankId(String bankId) throws Exception {
		List<CustomerMerchant> customerMerchantList = null;
		try {
			Query query = em.createNamedQuery("getCustomerMerchantByBankId");
			query.setParameter("bankId", bankId);
			query.setParameter("statusDeleted", CustomerMerchantStatus.DELETED);
			customerMerchantList = (List<CustomerMerchant>) query.getResultList();
		} catch (NoResultException en) {
			return null;
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		return customerMerchantList;
	}

	@SuppressWarnings("unchecked")
	public List<CustomerMerchant> getCustomerMerchantByCustomerId(String customerId) throws Exception {
		List<CustomerMerchant> customerMerchantList = null;
		try {
			Query query = em.createNamedQuery("getCustomerMerchantByCustomerId");
			query.setParameter("customerId", customerId);
			query.setParameter("statusDeleted", CustomerMerchantStatus.DELETED);
			customerMerchantList = (List<CustomerMerchant>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return customerMerchantList;
	}

	@SuppressWarnings("unchecked")
	public List<CustomerMerchant> getCustomerMerchantByBankMerchantId(String bankMerchantId) throws Exception {
		List<CustomerMerchant> customerMerchantList = null;
		try {
			Query query = em.createNamedQuery("getCustomerMerchantByBankMerchantId");
			query.setParameter("bankMerchant_id", bankMerchantId);
			query.setParameter("statusDeleted", CustomerMerchantStatus.DELETED);
			customerMerchantList = (List<CustomerMerchant>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return customerMerchantList;
	}

	@SuppressWarnings("unchecked")
	public List<CustomerMerchant> getCustomerMerchantByCustomerAccountNumber(String customerAccountNumber) throws Exception {
		List<CustomerMerchant> customerMerchantList = null;
		try {
			Query query = em.createNamedQuery("getCustomerMerchantByCustomerAccountNumber");
			query.setParameter("customerAccountNumber", customerAccountNumber);
			query.setParameter("statusDeleted", CustomerMerchantStatus.DELETED);
			customerMerchantList = (List<CustomerMerchant>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return customerMerchantList;
	}

	@SuppressWarnings("unchecked")
	public List<CustomerMerchant> getCustomerMerchantByStatus(CustomerMerchantStatus status) throws Exception {
		List<CustomerMerchant> customerMerchantList = null;
		try {
			Query query = em.createNamedQuery("getCustomerMerchantByStatus");
			query.setParameter("status", status);
			customerMerchantList = (List<CustomerMerchant>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return customerMerchantList;
	}

	public BankMerchant getBankMerchantByBankIdAndMerchantId(String bankId, String merchantId) {
		BankMerchant bankMerchant = null;
		try {
			Query query = em.createNamedQuery("getBankMerchantByBankIdAndMerchantId");
			query.setParameter("bankId", bankId);
			query.setParameter("merchantId", merchantId);
			query.setParameter("statusDeleted", BankMerchantStatus.DELETED);
			bankMerchant =(BankMerchant)query.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bankMerchant;

	}

	public BankMerchant createBankMerchant(BankMerchant bankMerchant, String username) throws Exception {
		bankMerchant.setId(GenerateKey.generateEntityId());
		
		BankMerchant b = this.getBankMerchantByBankIdAndMerchantId(bankMerchant.getBankId(), bankMerchant.getMerchant().getId());
		if(b!=null){
			throw new EntityExistsException();
		}
		em.persist(bankMerchant);
		
		//Audit trail
		AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
		auditService.logActivity(username, AuditEvents.CREATE_BANK_MERCHANT, bankMerchant.getId(), bankMerchant.getEntityName(), null, bankMerchant.getAuditableAttributesString(), bankMerchant.getInstanceName());
		
		return bankMerchant;

	}

	public BankMerchant approveBankMerchant(BankMerchant bankMerchant, String username) throws Exception {
		String oldBankMerchant = this.findBankMerchantById(bankMerchant.getId()).getAuditableAttributesString();
		bankMerchant.setStatus(BankMerchantStatus.ACTIVE);
		bankMerchant = this.updateBankMerchant(bankMerchant);
		
		//Audit trail
		AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
		auditService.logActivity(username, AuditEvents.EDIT_BANK_MERCHANT, bankMerchant.getId(), bankMerchant.getEntityName(), oldBankMerchant, bankMerchant.getAuditableAttributesString(), bankMerchant.getInstanceName());
		
		return bankMerchant;
	}

	public BankMerchant editBankMerchant(BankMerchant bankMerchant, String username) throws Exception {
		String oldBankMerchant = this.findBankMerchantById(bankMerchant.getId()).getAuditableAttributesString();
		
		bankMerchant = this.updateBankMerchant(bankMerchant);
		
		//Audit trail
		AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
		auditService.logActivity(username, AuditEvents.EDIT_BANK_MERCHANT, bankMerchant.getId(), bankMerchant.getEntityName(), oldBankMerchant, bankMerchant.getAuditableAttributesString(), bankMerchant.getInstanceName());
		
		return bankMerchant;

	}
	
	private BankMerchant updateBankMerchant(BankMerchant bankMerchant) throws Exception{
		try {
			bankMerchant = em.merge(bankMerchant);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return bankMerchant;
	}

	public BankMerchant getBankMerchantByStatusAndBankIdAndMerchantId(BankMerchantStatus status, String bankId, String merchantId) {
		BankMerchant bankMerchant = null;
		try {
			Query query = em.createNamedQuery("getBankMerchantByStatusAndBankIdAndMerchantId");
			query.setParameter("status", status);
			query.setParameter("bankId", bankId);
			query.setParameter("merchantId", merchantId);
			query.setParameter("statusDeleted", BankMerchantStatus.DELETED);
			bankMerchant =(BankMerchant)query.getSingleResult();
		} catch (NoResultException en) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bankMerchant;

	}

	public BankMerchant findBankMerchantById(String bankMerchantId) {
		BankMerchant bankMerchant = null;
		try {
			bankMerchant = em.find(BankMerchant.class, bankMerchantId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bankMerchant;

	}

	public BankMerchant deleteBankMerchant(BankMerchant bankMerchant, String username) throws Exception {
		String oldBankMerchant = this.findBankMerchantById(bankMerchant.getId()).getAuditableAttributesString();
		bankMerchant.setStatus(BankMerchantStatus.DELETED);
		bankMerchant = this.updateBankMerchant(bankMerchant);
		
		//Audit trail
		AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
		auditService.logActivity(username, AuditEvents.DELETE_BANK_MERCHANT, bankMerchant.getId(), bankMerchant.getEntityName(), oldBankMerchant, bankMerchant.getAuditableAttributesString(), bankMerchant.getInstanceName());
		
		return bankMerchant;
	}

	@SuppressWarnings("unchecked")
	public List<BankMerchant> getBankMerchantByStatus(BankMerchantStatus status) {
		List<BankMerchant> bankMerchantList = null;
		try {
			Query query = em.createNamedQuery("getBankMerchantByStatus");
			query.setParameter("status", status);
			bankMerchantList = (List<BankMerchant>)query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bankMerchantList;

	}

	public Merchant disapproveMerchant(Merchant merchant, String userName) throws Exception {
		String oldMerchant = this.findMerchantById(merchant.getId()).getAuditableAttributesString();
		merchant.setStatus(MerchantStatus.DISAPPROVED);
		merchant = this.updateMerchant(merchant);

		// Audit trail
		AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
		auditService.logActivity(userName, AuditEvents.DISAPPROVE_MERCHANT, merchant.getId(), merchant.getEntityName(), oldMerchant, merchant.getAuditableAttributesString(), merchant.getInstanceName());

		return merchant;		
	}

	public BankMerchant disapproveBankMerchant(BankMerchant bankMerchant, String username) throws Exception {
		String oldBankMerchant = this.findBankMerchantById(bankMerchant.getId()).getAuditableAttributesString();
		bankMerchant.setStatus(BankMerchantStatus.DISAPPROVED);
		bankMerchant = this.updateBankMerchant(bankMerchant);
		
		//Audit trail
		AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
		auditService.logActivity(username, AuditEvents.DISAPPROVE_BANK_MERCHANT, bankMerchant.getId(), bankMerchant.getEntityName(), oldBankMerchant, bankMerchant.getAuditableAttributesString(), bankMerchant.getInstanceName());
		
		return bankMerchant;		
	}

	public CustomerMerchant disapproveCustomerMerchant(CustomerMerchant customerMerchant, String userName) throws Exception {
		String oldCustomerMerchant = this.findCustomerMerchantById(customerMerchant.getId()).getAuditableAttributesString();

		customerMerchant.setStatus(CustomerMerchantStatus.DISAPPROVED);
		customerMerchant = this.updateCustomerMerchant(customerMerchant);

		// Adit Trail
		AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
		auditService.logActivity(userName, AuditEvents.DISAPPROVE_CUSTOMER_MERCHANT, customerMerchant.getId(), customerMerchant.getEntityName(), oldCustomerMerchant, customerMerchant.getAuditableAttributesString(), customerMerchant.getInstanceName());

		return customerMerchant;
	}
	
	@SuppressWarnings("unchecked")
	public List<BankMerchant> getBankMerchantByBankId(String bankId) throws Exception {
		List<BankMerchant> results;
		try {
			Query query = em.createNamedQuery("getBankMerchantByBankId");
			query.setParameter("bankId", bankId);
			query.setParameter("statusDeleted", BankMerchantStatus.DELETED);
			results = (List<BankMerchant>) query.getResultList();
		} catch (NoResultException e) {
			System.out.println(">>>>>>>>> No results");
			return null;
		}
		return results;
	}
	
	@SuppressWarnings("unchecked")
	public List<BankMerchant> getBankMerchantByMerchantId(String merchantId) throws Exception {
		List<BankMerchant> results;
		try {
			Query query = em.createNamedQuery("getBankMerchantByMerchantId");
			query.setParameter("merchant_id", merchantId);
			query.setParameter("statusDeleted", BankMerchantStatus.DELETED);
			results = (List<BankMerchant>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
		return results;
	}
	
	public BankMerchant getBankMerchantByBankIdAndShortNameAndStatus(String bankId, String shortName, BankMerchantStatus status) throws Exception {
		BankMerchant bankMerchant;
		if (shortName == null) {
			return null;
		}
		try {
			Query query = em.createNamedQuery("getBankMerchantByBankIdAndShortNameAndStatus");
			query.setParameter("bankId", bankId);
			query.setParameter("shortName", shortName.toUpperCase());
			query.setParameter("status", status);
			bankMerchant = (BankMerchant) query.getSingleResult();
		} catch(NoResultException n) {
			return null;
		}
		return bankMerchant;
	}
	
	public CustomerMerchant getCustomerMerchantByCustomerIdAndBankMerchantIdAndStatus(String customerId, String bankMerchantId, CustomerMerchantStatus status) throws Exception {
		CustomerMerchant customerMerchant;
		try {
			Query query = em.createNamedQuery("getCustomerMerchantByCustomerIdAndBankMerchantIdAndStatus");
			query.setParameter("customerId", customerId);
			query.setParameter("bankMerchant_id", bankMerchantId);
			query.setParameter("status", status);
			customerMerchant = (CustomerMerchant) query.getSingleResult();
		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return customerMerchant;
	}
	
	public CustomerMerchant getCustomerMerchantByCustomerIdAndMerchantShortNameAndStatus(String customerId, String merchantShortName, CustomerMerchantStatus status) throws Exception {
		CustomerMerchant customerMerchant;
		try {
			Query query = em.createNamedQuery("getCustomerMerchantByCustomerIdAndMerchantShortNameAndStatus");
			query.setParameter("customerId", customerId);
			query.setParameter("shortName", merchantShortName);
			query.setParameter("status", status);
			customerMerchant = (CustomerMerchant) query.getSingleResult();
		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return customerMerchant;
	}
	
	public List<Merchant> getMerchantByCustomerId(String customerId) throws Exception {
		List<Merchant> merchantList;
		List<CustomerMerchant> customerMerchantList = this.getCustomerMerchantByCustomerId(customerId);
		
		if (customerMerchantList == null || customerMerchantList.isEmpty()) {
			merchantList = null;
			return merchantList;
		} else {
			merchantList = new ArrayList<Merchant>();
			for (CustomerMerchant cm: customerMerchantList) {
				merchantList.add(cm.getBankMerchant().getMerchant());
			}
			return merchantList;
		}
	}
}
