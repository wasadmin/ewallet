package zw.co.esolutions.ewallet.bankservices.service;

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

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.ewallet.audit.AuditEvents;
import zw.co.esolutions.ewallet.audittrailservices.service.AuditTrailServiceSOAPProxy;
import zw.co.esolutions.ewallet.bankservices.model.AccountBalance;
import zw.co.esolutions.ewallet.bankservices.model.Bank;
import zw.co.esolutions.ewallet.bankservices.model.BankAccount;
import zw.co.esolutions.ewallet.bankservices.model.BankBranch;
import zw.co.esolutions.ewallet.bankservices.model.ChargePostingInfo;
import zw.co.esolutions.ewallet.bankservices.model.EWalletPostingResponse;
import zw.co.esolutions.ewallet.bankservices.model.Transaction;
import zw.co.esolutions.ewallet.bankservices.model.TransactionPostingInfo;
import zw.co.esolutions.ewallet.bankservices.model.TransactionSummary;
import zw.co.esolutions.ewallet.bankservices.model.TransactionSummaryItem;
import zw.co.esolutions.ewallet.bankservices.model.TransactionUniversalPojo;
import zw.co.esolutions.ewallet.enums.BankAccountLevel;
import zw.co.esolutions.ewallet.enums.BankAccountStatus;
import zw.co.esolutions.ewallet.enums.BankAccountType;
import zw.co.esolutions.ewallet.enums.BankBranchStatus;
import zw.co.esolutions.ewallet.enums.BankStatus;
import zw.co.esolutions.ewallet.enums.OwnerType;
import zw.co.esolutions.ewallet.enums.TransactionCategory;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.serviceexception.EWalletException;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.Formats;
import zw.co.esolutions.ewallet.util.GenerateKey;
import zw.co.esolutions.ewallet.util.MoneyUtil;
import zw.co.esolutions.ewallet.util.NumberUtil;
import zw.co.esolutions.ewallet.util.SystemConstants;

/**
 * Session Bean implementation class BankServiceImpl
 */
@Stateless
@WebService(endpointInterface = "zw.co.esolutions.ewallet.bankservices.service.BankService", serviceName = "BankService", portName = "BankServiceSOAP")
public class BankServiceImpl implements BankService {

	@PersistenceContext(name = "BankServicesEJB")
	private EntityManager em;

	/**
	 * Default constructor.
	 */
	public BankServiceImpl() {

	}

	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(BankServiceImpl.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + BankServiceImpl.class);
		}
	}

	public Bank createBank(Bank bank, String userName) throws Exception {
		if (bank.getId() == null) {
			bank.setId(GenerateKey.generateEntityId());
		}
		if (bank.getDateCreated() == null) {
			bank.setDateCreated(new Date());
		}
		bank.setFieldsToUpperCase();
		try {
			Bank b = this.getBankByCode(bank.getCode());
			if (b != null) {
				throw new EntityExistsException();
			}
			em.persist(bank);

			// Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.CREATE_BANK, bank.getId(), bank.getEntityName(), null, bank.getAuditableAttributesString(), bank.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {

		}
		return bank;
	}

	public String deleteBank(Bank bank, String userName) throws Exception {
		try {
			String oldBank = this.findBankById(bank.getId()).getAuditableAttributesString();
			bank.setStatus(BankStatus.DELETED);
			bank = this.updateBank(bank);

			// Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.DELETE_BANK, bank.getId(), bank.getEntityName(), oldBank, bank.getAuditableAttributesString(), bank.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return "";
	}

	public Bank approveBank(Bank bank, String userName) throws Exception {
		try {
			String oldBank = this.findBankById(bank.getId()).getAuditableAttributesString();
			bank.setStatus(BankStatus.ACTIVE);
			bank = this.updateBank(bank);

			// Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.APPROVE_BANK, bank.getId(), bank.getEntityName(), oldBank, bank.getAuditableAttributesString(), bank.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return bank;
	}

	public Bank editBank(Bank bank, String userName) throws Exception {
		try {
			String oldBank = this.findBankById(bank.getId()).getAuditableAttributesString();
			bank = this.updateBank(bank);

			// Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.EDIT_BANK, bank.getId(), bank.getEntityName(), oldBank, bank.getAuditableAttributesString(), bank.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return bank;
	}

	private Bank updateBank(Bank bank) throws Exception {
		bank.setFieldsToUpperCase();
		try {
			bank = em.merge(bank);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return bank;
	}

	public Bank findBankById(String id) {
		Bank bank = null;
		try {
			bank = (Bank) em.find(Bank.class, id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return bank;
	}

	public BankBranch createBankBranch(BankBranch bankBranch, String userName) throws Exception {
		if (bankBranch.getId() == null) {
			bankBranch.setId(GenerateKey.generateEntityId());
		}
		if (bankBranch.getDateCreated() == null) {
			bankBranch.setDateCreated(new Date());
		}
		bankBranch.setFieldsToUpperCase();
		try {
			BankBranch b = this.getBankBranchByCode(bankBranch.getCode());
			Bank bank = em.find(Bank.class, bankBranch.getBank().getId());
			bankBranch.setBank(bank);
			if (b != null) {
				throw new EntityExistsException();
			}
			em.persist(bankBranch);

			// Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.CREATE_BANK_BRANCH, bankBranch.getId(), bankBranch.getEntityName(), null, bankBranch.getAuditableAttributesString(), bankBranch.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return bankBranch;
	}

	public String deleteBankBranch(BankBranch bankBranch, String userName) throws Exception {
		try {
			String oldBankBranch = this.findBankBranchById(bankBranch.getId()).getAuditableAttributesString();
			bankBranch.setStatus(BankBranchStatus.DELETED);
			bankBranch = this.updateBankBranch(bankBranch);

			// Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.DELETE_BANK_BRANCH, bankBranch.getId(), bankBranch.getEntityName(), oldBankBranch, bankBranch.getAuditableAttributesString(), bankBranch.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return "";
	}

	public BankBranch approveBankBranch(BankBranch bankBranch, String userName) throws Exception {
		try {
			String oldBankBranch = this.findBankBranchById(bankBranch.getId()).getAuditableAttributesString();
			bankBranch.setStatus(BankBranchStatus.ACTIVE);
			bankBranch = this.updateBankBranch(bankBranch);

			// Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.APPROVE_BANK_BRANCH, bankBranch.getId(), bankBranch.getEntityName(), oldBankBranch, bankBranch.getAuditableAttributesString(), bankBranch.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return bankBranch;
	}

	public BankBranch rejectBankBranch(BankBranch bankBranch, String userName) throws Exception {
		try {
			String oldBankBranch = this.findBankBranchById(bankBranch.getId()).getAuditableAttributesString();
			bankBranch.setStatus(BankBranchStatus.DISAPPROVED);
			bankBranch = this.updateBankBranch(bankBranch);

			// Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.REJECT_BANK_BRANCH, bankBranch.getId(), bankBranch.getEntityName(), oldBankBranch, bankBranch.getAuditableAttributesString(), bankBranch.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return bankBranch;
	}

	public BankBranch editBankBranch(BankBranch bankBranch, String userName) throws Exception {
		try {
			String oldBankBranch = this.findBankBranchById(bankBranch.getId()).getAuditableAttributesString();

			bankBranch = this.updateBankBranch(bankBranch);

			// Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.EDIT_BANK_BRANCH, bankBranch.getId(), bankBranch.getEntityName(), oldBankBranch, bankBranch.getAuditableAttributesString(), bankBranch.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return bankBranch;
	}

	private BankBranch updateBankBranch(BankBranch bankBranch) throws Exception {
		bankBranch.setFieldsToUpperCase();
		try {
			Bank bank = this.findBankById(bankBranch.getBank().getId());
			bankBranch.setBank(bank);
			bankBranch = em.merge(bankBranch);
		} catch (Exception e) {
			throw e;
		}
		return bankBranch;
	}

	public BankBranch findBankBranchById(String id) {

		BankBranch bankBranch = null;
		try {
			bankBranch = (BankBranch) em.find(BankBranch.class, id);
		} catch (Exception e) {
		} finally {

		}
		return bankBranch;
	}

	public BankAccount createBankAccount(BankAccount bankAccount, String userName) throws Exception {
		BankAccount account;
		if (bankAccount.getAccountNumber() == null || "".equalsIgnoreCase(bankAccount.getAccountNumber().trim())) {
			throw new Exception("Please specify a valid Bank Account Number");
		}
		account = this.getBankAccountByAccountHolderAndTypeAndOwnerType(bankAccount.getAccountHolderId(), bankAccount.getType(), bankAccount.getOwnerType(), bankAccount.getAccountNumber());
		if (account != null) {
			throw new Exception("Bank Account already exists.");
		}

		if (OwnerType.BANK_BRANCH.equals(bankAccount.getOwnerType())) {
			BankBranch branch;
			try {
				branch = this.findBankBranchById(bankAccount.getAccountHolderId());
				bankAccount.setBranch(branch);
			} catch (Exception e1) {
				LOG.debug("No branch specified.....");
				branch = null;
			}

			if (branch == null) {
				throw new Exception("Please specify a valid bank branch.");
			}
		}

		BankAccount accountCheck = this.getUniqueBankAccountByAccountNumber(bankAccount.getAccountNumber());
		if (accountCheck != null) {
			throw new Exception("Bank Account with account number " + bankAccount.getAccountNumber() + " already exists.");
		}

		if (bankAccount.getId() == null) {
			bankAccount.setId(GenerateKey.generateEntityId());
		}
		if (bankAccount.getDateCreated() == null) {
			bankAccount.setDateCreated(new Date());
		}

		try {
			em.persist(bankAccount);
			this.createAccountBalance(new AccountBalance(GenerateKey.generateEntityId(), bankAccount.getId(), 0, new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), "", 0));
			// Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.CREATE_BANK_ACCOUNT, bankAccount.getId(), bankAccount.getEntityName(), null, bankAccount.getAuditableAttributesString(), bankAccount.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return bankAccount;
	}

	public BankAccount createCustomerBankAccount(BankAccount bankAccount, String source, String userName) throws Exception {
		BankAccount account;

		if (bankAccount.getAccountNumber() == null || "".equalsIgnoreCase(bankAccount.getAccountNumber().trim())) {
			throw new Exception("Account number cannot be null.");
		}
		if (BankAccountType.E_WALLET.equals(bankAccount.getType())) {
			bankAccount.setAccountNumber(NumberUtil.formatMobileNumber(bankAccount.getAccountNumber()));
		}

		BankBranch branch;
		try {
			branch = em.find(BankBranch.class, bankAccount.getBranch().getId());
		} catch (Exception e1) {
			LOG.debug("No branch specified.....");
			branch = null;
		}

		if (branch == null) {
			throw new Exception("Please specify a valid bank branch.");
		}
	
		account = checkIfBankAccountExists(bankAccount, source, branch);
		if (account != null) {
			throw new Exception("Bank Account already exists.");
		}

		if (bankAccount.getId() == null) {
			bankAccount.setId(GenerateKey.generateEntityId());
		}
		if (bankAccount.getDateCreated() == null) {
			bankAccount.setDateCreated(new Date());
		}

		try {
			bankAccount.setBranch(branch);
			em.persist(bankAccount);

			// Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.CREATE_BANK_ACCOUNT, bankAccount.getId(), bankAccount.getEntityName(), null, bankAccount.getAuditableAttributesString(), bankAccount.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return bankAccount;
	}

	private BankAccount checkIfBankAccountExists(BankAccount bankAccount, String source, BankBranch branch) {
		BankAccount account = null;
		
		if (SystemConstants.SOURCE_APPLICATION_BANK.equalsIgnoreCase(source)) {
			LOG.debug("bank part>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + bankAccount.getAccountNumber());
			account = this.getUniqueBankAccountByAccountNumber(bankAccount.getAccountNumber());
		} else if (SystemConstants.SOURCE_APPLICATION_SWITCH.equalsIgnoreCase(source)) {
			LOG.debug("switch part>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + bankAccount.getAccountNumber());
			account = this.getUniqueBankAccountByAccountNumberAndBankId(bankAccount.getAccountNumber(), branch.getBank().getId());
		}
		return account;
	}

	public BankAccount getUniqueBankAccountByAccountNumber(String accountNumber) {
		BankAccount results = null;
		LOG.debug("Lets check for uniqueness");
		try {
			LOG.debug("account number :::::" + accountNumber);

			Query query = em.createNamedQuery("getUniqueBankAccountByAccountNumber");
			query.setParameter("accountNumber", accountNumber);
			// query.setParameter("bankId", bankId);
			query.setParameter("status", BankAccountStatus.DELETED);
			results = (BankAccount) query.getSingleResult();
			LOG.debug("Found Results in EJB Query......" + results);
		} catch (NoResultException e) {
			// e.printStackTrace();
			LOG.debug("uniqueness return null      nnnn....." + e.getMessage());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.debug("exception bringing unqness    " + e.getMessage());
			return null;
		}
		return results;
	}

	public BankAccount getUniqueBankAccountByAccountNumberAndBankId(String accountNumber, String bankId) {
		BankAccount results = null;
		LOG.debug("Lets check for uniqueness");
		try {
			LOG.debug("account number :::::" + accountNumber);
			LOG.debug("bank id result:::::::" + bankId);
			Query query = em.createNamedQuery("getUniqueBankAccountByAccountNumberAndBankId");
			query.setParameter("accountNumber", accountNumber);
			query.setParameter("bankId", bankId);
			query.setParameter("status", BankAccountStatus.DELETED);
			results = (BankAccount) query.getSingleResult();
			LOG.debug("445555555ejb we got somethuing >>>>>>>nnbbb......" + results);
		} catch (NoResultException e) {
			// e.printStackTrace();
			LOG.debug("uniqueness return null      nnnn....." + e.getMessage());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.debug("exception bringing unqness    " + e.getMessage());
			return null;
		}
		return results;
	}

	public BankAccount deleteBankAccount(BankAccount bankAccount, String userName) throws Exception {
		try {
			String oldBankAccount = this.findBankAccountById(bankAccount.getId()).getAuditableAttributesString();
			bankAccount.setStatus(BankAccountStatus.DELETED);
			bankAccount = this.updateBankAccount(bankAccount);

			// Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.DELETE_BANK_ACCOUNT, bankAccount.getId(), bankAccount.getEntityName(), oldBankAccount, bankAccount.getAuditableAttributesString(), bankAccount.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return bankAccount;
	}

	public BankAccount approveBankAccount(BankAccount bankAccount, String userName) throws Exception {
		try {
			String oldBankAccount = this.findBankAccountById(bankAccount.getId()).getAuditableAttributesString();
			bankAccount.setStatus(BankAccountStatus.ACTIVE);
			bankAccount = this.updateBankAccount(bankAccount);

			// Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.APPROVE_BANK_ACCOUNT, bankAccount.getId(), bankAccount.getEntityName(), oldBankAccount, bankAccount.getAuditableAttributesString(), bankAccount.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return bankAccount;
	}

	public BankAccount rejectBankAccount(BankAccount bankAccount, String userName) throws Exception {
		try {
			String oldBankAccount = this.findBankAccountById(bankAccount.getId()).getAuditableAttributesString();
			bankAccount.setStatus(BankAccountStatus.DISAPPROVED);
			bankAccount = this.updateBankAccount(bankAccount);

			// Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.REJECT_BANK_ACCOUNT, bankAccount.getId(), bankAccount.getEntityName(), oldBankAccount, bankAccount.getAuditableAttributesString(), bankAccount.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return bankAccount;
	}

	public BankAccount editBankAccount(BankAccount bankAccount, String userName) throws Exception {
		try {
			String oldBankAccount = this.findBankAccountById(bankAccount.getId()).getAuditableAttributesString();
			bankAccount = this.updateBankAccount(bankAccount);

			// Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.EDIT_BANK_ACCOUNT, bankAccount.getId(), bankAccount.getEntityName(), oldBankAccount, bankAccount.getAuditableAttributesString(), bankAccount.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return bankAccount;
	}

	private BankAccount updateBankAccount(BankAccount bankAccount) throws Exception {

		try {
			bankAccount.setAccountName(bankAccount.getAccountName().toUpperCase());
			BankBranch branch;
			try {
				branch = em.find(BankBranch.class, bankAccount.getBranch().getId());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				branch = null;
			}
			bankAccount.setBranch(branch);
			bankAccount = em.merge(bankAccount);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return bankAccount;
	}

	public BankAccount findBankAccountById(String id) {

		BankAccount bankAccount = null;
		try {
			bankAccount = (BankAccount) em.find(BankAccount.class, id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return bankAccount;
	}

	@SuppressWarnings("unchecked")
	public List<Bank> getBankByName(String name) {
		List<Bank> results = null;
		try {
			Query query = em.createNamedQuery("getBankByName");
			query.setParameter("name", "%" + name.toUpperCase() + "%");
			results = (List<Bank>) query.getResultList();
		} catch (NoResultException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;

	}

	@SuppressWarnings("unchecked")
	public List<BankAccount> getBankAccountByLevel(String level) {
		List<BankAccount> results = null;
		try {
			Query query = em.createNamedQuery("getBankAccountByLevel");
			query.setParameter("level", level);
			results = (List<BankAccount>) query.getResultList();
		} catch (NoResultException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;

	}

	@SuppressWarnings("unchecked")
	public List<Bank> getBank() {
		List<Bank> results = null;
		try {
			Query query = em.createNamedQuery("getBank");
			results = (List<Bank>) query.getResultList();
		} catch (NoResultException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<BankBranch> getBankBranch() {
		List<BankBranch> results = null;
		try {
			Query query = em.createNamedQuery("getBankBranch");
			query.setParameter("status", BankBranchStatus.DELETED);
			results = (List<BankBranch>) query.getResultList();
		} catch (NoResultException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<BankAccount> getBankAccount() {
		List<BankAccount> results = null;
		try {
			Query query = em.createNamedQuery("getBankAccount");
			query.setParameter("status", BankAccountStatus.DELETED);
			results = (List<BankAccount>) query.getResultList();
		} catch (NoResultException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<BankAccount> getBankAccountByType(BankAccountType type) {
		List<BankAccount> results = null;
		try {
			Query query = em.createNamedQuery("getBankAccountByType");
			query.setParameter("type", type);
			results = (List<BankAccount>) query.getResultList();
		} catch (NoResultException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;

	}

	@SuppressWarnings("unchecked")
	public List<BankAccount> getBankAccountByLevelAndStatus(BankAccountLevel level, BankAccountStatus status) {
		List<BankAccount> results = null;
		try {
			Query query = em.createNamedQuery("getBankAccountByLevelAndStatus");
			query.setParameter("level", level);
			query.setParameter("status", status);
			results = (List<BankAccount>) query.getResultList();
		} catch (NoResultException e) {
			e.printStackTrace();
		} finally {

		}
		return results;
	}

	public Bank getBankByCode(String code) {
		Bank results = null;
		try {
			Query query = em.createNamedQuery("getBankByCode");
			query.setParameter("code", code);
			results = (Bank) query.getSingleResult();
		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;

	}

	@SuppressWarnings("unchecked")
	public List<Bank> getBankByStatus(BankStatus status) {
		List<Bank> results = null;
		try {
			Query query = em.createNamedQuery("getBankByStatus");
			query.setParameter("status", status);
			results = (List<Bank>) query.getResultList();
		} catch (NoResultException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;

	}

	@SuppressWarnings("unchecked")
	public List<BankBranch> getBankBranchByBank(String id) {
		List<BankBranch> results = null;
		try {
			Query query = em.createNamedQuery("getBankBranchByBank");
			query.setParameter("status", BankBranchStatus.DELETED);
			query.setParameter("bank_id", id);
			results = (List<BankBranch>) query.getResultList();
		} catch (NoResultException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;

	}

	@SuppressWarnings("unchecked")
	public List<BankBranch> getBankBranchByName(String name) {
		List<BankBranch> results = null;
		try {
			Query query = em.createNamedQuery("getBankBranchByName");
			query.setParameter("name", "%" + name.toUpperCase() + "%");
			query.setParameter("status", BankBranchStatus.DELETED);
			results = (List<BankBranch>) query.getResultList();
		} catch (NoResultException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;

	}

	public BankBranch getBankBranchByCode(String code) {
		BankBranch results = null;
		try {
			Query query = em.createNamedQuery("getBankBranchByCode");
			query.setParameter("code", code.toUpperCase());
			query.setParameter("status", BankBranchStatus.DELETED);
			results = (BankBranch) query.getSingleResult();
		} catch (NoResultException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;

	}

	@SuppressWarnings("unchecked")
	public List<BankBranch> getBankBranchByStatus(BankBranchStatus status) {
		List<BankBranch> results = null;
		try {
			Query query = em.createNamedQuery("getBankBranchByStatus");
			query.setParameter("status", status);
			results = query.getResultList();
		} catch (NoResultException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;

	}

	@SuppressWarnings("unchecked")
	public List<BankAccount> getBankAccountByStatus(BankAccountStatus status) {
		List<BankAccount> results = null;
		try {
			Query query = em.createNamedQuery("getBankAccountByStatus");
			query.setParameter("status", status);
			results = (List<BankAccount>) query.getResultList();
		} catch (NoResultException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;

	}

	@SuppressWarnings("unchecked")
	public List<BankAccount> getBankAccountByStatusAndOwnerType(BankAccountStatus status, OwnerType ownerType) {
		List<BankAccount> results = null;
		try {
			Query query = em.createNamedQuery("getBankAccountByStatusAndOwnerType");
			query.setParameter("status", status);
			query.setParameter("ownerType", ownerType);
			results = (List<BankAccount>) query.getResultList();
		} catch (NoResultException e) {
			// e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;

	}

	@SuppressWarnings("unchecked")
	public List<BankAccount> getBankAccountsAwaitingApproval() {

		List<BankAccount> results = null;
		try {
			Query query = em.createNamedQuery("getBankAccountsAwaitingApproval");
			query.setParameter("status", BankAccountStatus.AWAITING_APPROVAL);
			query.setParameter("ownerType1", OwnerType.BANK);
			query.setParameter("ownerType2", OwnerType.BANK_BRANCH);
			query.setParameter("ownerType3", OwnerType.MERCHANT);

			results = (List<BankAccount>) query.getResultList();

		} catch (NoResultException e) {
			// e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;

	}

	@SuppressWarnings("unchecked")
	public List<BankAccount> getBankAccountByBank(String bankId) {
		List<BankAccount> results = null;
		try {
			Query query = em.createNamedQuery("getBankAccountByBank");
			query.setParameter("bank_id", bankId);
			results = (List<BankAccount>) query.getResultList();
		} catch (NoResultException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;

	}

	@SuppressWarnings("unchecked")
	public List<BankAccount> getBankAccountByAccountHolderIdAndOwnerType(String accountHolderId, OwnerType ownerType) {
		List<BankAccount> results = null;
		try {
			Query query = em.createNamedQuery("getBankAccountByAccountHolderIdAndOwnerType");
			query.setParameter("accountHolderId", accountHolderId);
			query.setParameter("ownerType", ownerType);
			query.setParameter("status", BankAccountStatus.DELETED);
			results = (List<BankAccount>) query.getResultList();
		} catch (NoResultException e) {
			System.err.println("No result exception.......");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;

	}

	@SuppressWarnings("unchecked")
	public List<BankAccount> getBankAccountByBranch(String branchId) {
		List<BankAccount> results = null;
		try {
			Query query = em.createNamedQuery("getBankAccountByBranch");
			query.setParameter("branch_id", branchId);
			results = (List<BankAccount>) query.getResultList();
		} catch (NoResultException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}

	public BankAccount getBankAccountByAccountNumberAndOwnerType(String accountNumber, OwnerType ownerType) {
		BankAccount results = null;
		try {
			Query query = em.createNamedQuery("getBankAccountByAccountNumberAndOwnerType");
			query.setParameter("accountNumber", accountNumber);
			query.setParameter("ownerType", ownerType);
			query.setParameter("status", BankAccountStatus.DELETED);
			results = (BankAccount) query.getSingleResult();
		} catch (NoResultException e) {
			// e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;

	}

	public BankAccount getBankAccountByAccountHolderAndTypeAndOwnerType(String accountHolderId, BankAccountType type, OwnerType ownerType, String accountNumber) throws Exception {
		BankAccount account = null;
		String sql = "SELECT b FROM BankAccount b WHERE b.accountHolderId = :accountHolderId AND b.type = :type AND b.ownerType =:ownerType AND b.status <> :status ";
		try {
			if (accountNumber != null) {
				sql = sql + "AND b.accountNumber = :accountNumber ";
			}
			Query query = em.createQuery(sql);
			query.setParameter("accountHolderId", accountHolderId);
			query.setParameter("type", type);
			query.setParameter("ownerType", ownerType);
			query.setParameter("status", BankAccountStatus.DELETED);

			if (accountNumber != null) {
				query.setParameter("accountNumber", accountNumber);
			}
			account = (BankAccount) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return account;
	}

	public BankAccount getPrimaryAccountByAccountHolderIdAndOwnerType(boolean primaryAccount, String accountHolderId, OwnerType ownerType) {
		BankAccount account = null;
		try {
			Query query = em.createNamedQuery("getPrimaryAccountByAccountHolderIdAndOwnerType");
			query.setParameter("accountHolderId", accountHolderId);
			query.setParameter("primaryAccount", primaryAccount);
			query.setParameter("ownerType", ownerType);
			query.setParameter("status", BankAccountStatus.DELETED);
			account = (BankAccount) query.getSingleResult();
		} catch (NoResultException e) {
			LOG.debug("No result.........................");
			e.printStackTrace();
		} catch (Exception e) {
			LOG.debug("Exception other.........................");
			e.printStackTrace();
		}
		return account;
	}

	public List<BankAccount> getBankAccountsByAccountNumber(String accountNumber) {
		List<BankAccount> results = null;
		try {
			Query query = em.createNamedQuery("getBankAccountsByAccountNumber");
			query.setParameter("accountNumber", "%" + accountNumber + "%");
			results = (List<BankAccount>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;

	}

	public List<BankAccount> deRegisterBankAccountsByOwnerId(String ownerId, String userName) throws Exception {
		LOG.debug("Customer id    " + ownerId);
		List<BankAccount> accounts = getBankAccountsByOwnerid(ownerId);
		List<BankAccount> deletedAccounts = new ArrayList<BankAccount>();
		for (BankAccount account : accounts) {
			LOG.debug("Account Number " + account.getAccountName());
			BankAccount deletedBankAcc = deleteBankAccount(account, userName);
			deletedAccounts.add(deletedBankAcc);
		}

		return deletedAccounts;
	}

	private List<BankAccount> getBankAccountsByOwnerid(String ownerId) {

		List<BankAccount> results = null;
		try {
			Query query = em.createNamedQuery("getBankAccountsByOwnerId");
			query.setParameter("ownerId", ownerId);
			query.setParameter("status", BankAccountStatus.DELETED);
			results = (List<BankAccount>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}

	public List<BankAccount> getBankAccountsByAccountHolderIdAndOwnerTypeAndStatusAndBankAccountType(String accountHolderId, OwnerType ownerType, BankAccountStatus status, BankAccountType bankAccountType) {

		List<BankAccount> results = null;
		try {
			Query query = em.createNamedQuery("getBankAccountsByAccountHolderIdAndOwnerTypeAndStatusAndBankAccountType");
			query.setParameter("accountHolderId", accountHolderId);

			query.setParameter("ownerType", ownerType);
			query.setParameter("status", status);
			query.setParameter("bankAccountType", bankAccountType);
			results = (List<BankAccount>) query.getResultList();
			LOG.debug(">>>>>>>>>>>>>>>>>>>>>results getBankAccountsByAccountHolderIdAndOwnerTypeAndStatusAndBankAccountType >>>>>>>>>>>>>>>" + results.size());
		} catch (NoResultException e) {
			return null;
		}

		catch (Exception e) {
			e.printStackTrace();
			LOG.debug("Error message ::::::::::::::::" + e.getMessage());
		}
		return results;

	}

	public BankAccount getBankAccountsByAccountHolderIdAndOwnerTypeAndBankAccountType(String accountHolderId, OwnerType ownerType, BankAccountType bankAccountType) {

		BankAccount result = null;

		try {
			Query query = em.createNamedQuery("getBankAccountsByAccountHolderIdAndOwnerTypeAndBankAccountType");
			query.setParameter("accountHolderId", accountHolderId);
			query.setParameter("ownerType", ownerType);
			query.setParameter("status", BankAccountStatus.DELETED);
			query.setParameter("bankAccountType", bankAccountType);
			result = (BankAccount) query.getSingleResult();

		} catch (NoResultException e) {
			System.err.println("No result Exception :" + e.getMessage());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.debug("Error message ::::::::::::::::" + e.getMessage());
		}
		return result;

	}

	@SuppressWarnings("unchecked")
	public List<BankAccount> getBankAccountsByAccountHolderIdAndOwnerTypeAndStatus(String accountHolderId, OwnerType ownerType, BankAccountStatus status) {
		/*
		 * non ewallet accounts and no agentewallet
		 */

		LOG.debug("------------------------------------getBankAccountsByAccountHolderIdAndOwnerTypeAndStatus  --------------------------- ");

		List<BankAccount> results = null;
		try {

			LOG.debug("End search value:::::::::::::::::::::::::");
			Query query = em.createNamedQuery("getBankAccountsByAccountHolderIdAndOwnerTypeAndStatus");
			query.setParameter("accountHolderId", accountHolderId);
			query.setParameter("ownerType", ownerType);
			query.setParameter("status", status);
			query.setParameter("ewallet", BankAccountType.E_WALLET);
			query.setParameter("agentewallet", BankAccountType.AGENT_EWALLET);

			results = (List<BankAccount>) query.getResultList();
			LOG.debug("getBankAccountsByAccountHolderIdAndOwnerTypeAndStatus results found     " + results.size());

		} catch (NoResultException e) {
			return null;
		}

		catch (Exception e) {
			e.printStackTrace();
			LOG.debug("    .........error     " + e.getMessage());
		}
		return results;
		/*
		 * Just get a particular accounts using this method i.e just ewallet
		 */
	}

	@SuppressWarnings("unchecked")
	public List<BankAccount> getBankAccountsByMinAttributes(String bankId, String branchId, BankAccountType accType, String accountNumber, String accountName, BankAccountStatus status, int startIndex, int maxValue) {
		List<BankAccount> results = null;
		if (accountName != null) {
			accountName = accountName.toUpperCase();
		}
		LOG.debug(">>>>>>>>>>> BankId = " + bankId + " BranchId = " + branchId + " Acc Type = " + accType + " Acc Number = " + accountNumber + " Account Name = " + accountName + " Status = " + status);
		String qs = "SELECT b FROM BankAccount b WHERE ";
		try {

			if (bankId != null) {
				if (accType != null) {
					if (this.isCustomerAccount(accType) || this.isAgentAccount(accType)) {
						if (qs.endsWith("WHERE ")) {
							qs = qs + "b.branch.bank.id = :bankId ";
						} else {
							qs = qs + "AND b.branch.bank.id = :bankId ";
						}
					} else if (this.isBranchAccount(accType)) {
						if (qs.endsWith("WHERE ")) {
							qs = qs + "(b.ownerType = :branch AND b.branch.bank.id = :bankId) ";
						} else {
							qs = qs + "AND (b.ownerType = :branch AND b.branch.bank.id = :bankId) ";
						}
					} else {
						if (qs.endsWith("WHERE ")) {
							qs = qs + "(b.accountHolderId = :bankId AND b.ownerType = :bank) ";
						} else {
							qs = qs + "AND (b.accountHolderId = :bankId AND b.ownerType = :bank) ";
						}
					}
				}

			}
			if (status != null) {
				if (qs.endsWith("WHERE ")) {
					qs = qs + "b.status = :status ";
				} else {
					qs = qs + "AND b.status = :status ";
				}
			}

			if (accType != null) {
				// Cannot Search for customer
				if (qs.endsWith("WHERE ")) {
					qs = qs + "b.type = :type ";
				} else {
					qs = qs + "AND b.type = :type ";
				}

			}
			if (accountNumber != null) {

				if (qs.endsWith("WHERE ")) {
					qs = qs + "b.accountNumber LIKE :accountNumber ";
				} else {
					qs = qs + "AND b.accountNumber LIKE :accountNumber ";
				}

			}
			if (accountName != null) {

				if (qs.endsWith("WHERE ")) {
					qs = qs + "b.accountName LIKE :accountName ";
				} else {
					qs = qs + "AND b.accountName LIKE :accountName ";
				}

			}
			if (maxValue > 0) {
				qs = qs + "AND b.status <> :deletestatus ORDER BY b.id DESC";
			} else {
				qs = qs + "AND b.status <> :deletestatus ORDER BY b.dateCreated DESC";
			}
			Query query = em.createQuery(qs);
			query.setParameter("deletestatus", BankAccountStatus.DELETED);

			if (maxValue > 0) {
				query.setMaxResults(maxValue);
				query.setFirstResult(startIndex);
			}

			if (bankId != null) {
				if (accType == null) {

					// ?

				} else if (accType != null) {
					if (this.isCustomerAccount(accType)) {
						query.setParameter("bankId", bankId);
					} else if (this.isBranchAccount(accType)) {
						query.setParameter("bankId", bankId);
						query.setParameter("branch", OwnerType.BANK_BRANCH);
					} else {
						query.setParameter("bankId", bankId);
						query.setParameter("bank", OwnerType.BANK);

					}
				}
			}

			if (status != null) {
				query.setParameter("status", status);
			}

			if (accType != null) {
				query.setParameter("type", accType);
			}
			if (accountNumber != null) {
				query.setParameter("accountNumber", "%" + accountNumber + "%");
			}
			if (accountName != null) {
				query.setParameter("accountName", "%" + accountName + "%");
			}

			results = (List<BankAccount>) query.getResultList();
			List<BankAccount> res = new ArrayList<BankAccount>();

			if (accType == null && bankId != null) {
				if (results != null) {
					if (results.isEmpty()) {
						return null;
					} else {
						for (BankAccount acc : results) {
							if (acc.getBranch() == null) {
								if (bankId.equalsIgnoreCase(acc.getAccountHolderId())) {
									res.add(acc);
								}
							} else {
								if (bankId.equalsIgnoreCase(acc.getBranch().getBank().getId())) {
									res.add(acc);
								}
							}
						}
					}
				}
			}

			if (!res.isEmpty()) {
				results = res;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return results;

	}

	private boolean isCustomerAccount(BankAccountType accType) {
		return (BankAccountType.CHEQUE.equals(accType) || BankAccountType.CURRENT.equals(accType) || BankAccountType.SAVINGS.equals(accType) || BankAccountType.MERCHANT_SUSPENSE.equals(accType) || BankAccountType.E_WALLET.equals(accType));
	}
	
	private boolean isAgentAccount(BankAccountType accType) {
		return (BankAccountType.AGENT_EWALLET.equals(accType) || BankAccountType.AGENT_COMMISSION_SUSPENSE.equals(accType));
	}

	private boolean isBranchAccount(BankAccountType accType) {
		return (BankAccountType.BRANCH_CASH_ACCOUNT.equals(accType) || BankAccountType.EWALLET_BALANCING_SUSPENSE_ACCOUNT.equals(accType) || BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT.equals(accType));
	}

	public BankAccount deRegisterBankAccount(BankAccount bankAccount, String userName) throws Exception {
		try {
			String oldBankAccount = this.findBankAccountById(bankAccount.getId()).getAuditableAttributesString();
			bankAccount.setStatus(BankAccountStatus.DELETED);
			bankAccount = this.updateBankAccount(bankAccount);

			// Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.DEREGISTER_BANK_ACCOUNT, bankAccount.getId(), bankAccount.getEntityName(), oldBankAccount, bankAccount.getAuditableAttributesString(), bankAccount.getInstanceName());

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return bankAccount;

	}
	
	public EWalletPostingResponse reverseEWalletEntries(TransactionPostingInfo [] transactionPostingInfos, ChargePostingInfo [] chargePostingInfos) {
		LOG.debug("Received EWALLET posting reversal request ");
		LOG.debug("Transaction Postings : " + transactionPostingInfos);
		if(transactionPostingInfos == null || transactionPostingInfos.length == 0){
			LOG.debug("Posting request has no transactions");
			EWalletPostingResponse response = new EWalletPostingResponse(transactionPostingInfos, chargePostingInfos, ResponseCode.E600, "Reversal Failed.");
			return response;
		}
		LOG.debug("Now running the postings, the request is VALID");
		int postingSequence = 1;
		EWalletPostingResponse response = new EWalletPostingResponse();
		
		main : for (TransactionPostingInfo transactionPostingInfo : transactionPostingInfos) {
			try {
				ResponseCode rc = this.reverseMainTransaction(transactionPostingInfo, postingSequence++);
				response = new EWalletPostingResponse(transactionPostingInfos, chargePostingInfos, rc, "Reversal Successful");
				//return response;
				continue main;
				
			} catch (Exception e) {
				response = new EWalletPostingResponse(transactionPostingInfos, chargePostingInfos, ResponseCode.E601, "Reversal Failed." + e.getMessage());
				return response;
			}
		}
		if(chargePostingInfos == null || chargePostingInfos.length == 0){
			LOG.debug("No Charges in transactions TXN TYPE : " );
			return response;
		}else{
			charge : for (ChargePostingInfo chargePostingInfo : chargePostingInfos) {
				try {
					this.reverseTransactionCharge(chargePostingInfo, postingSequence++);
					continue charge;
				} catch (Exception e) {
					response = new EWalletPostingResponse(transactionPostingInfos, chargePostingInfos, ResponseCode.E602, "Reversal Failed." + e.getMessage());
					return response;
				}
			}			
		}
				
		response = new EWalletPostingResponse(transactionPostingInfos, chargePostingInfos, ResponseCode.E000, "Reversal Successful");
		LOG.debug("Done doing reversal postings.");
		return response;
	}
	
	private ResponseCode reverseTransactionCharge(ChargePostingInfo request, int currentPostingSequence) throws Exception {

		BankAccount source = this.findBankAccountById(request.getSrcAccountId());
		BankAccount dest = this.findBankAccountById(request.getTargetAccountId());
	
		long sourceBal = this.getRunningBalanceByAccountId(source);
		long destBal = this.getRunningBalanceByAccountId(dest);
		if(source == null ||  source.getId() == null){
			throw new Exception("No source account in transaction posting request.");
		}
		
		if(dest == null || dest.getId() == null){
			throw new Exception("No destination account in transaction posting request.");
		}
		LOG.debug("Found the necessary accounts...SRC " + source.getType() + "/" + source.getAccountNumber()  + " DEST " + dest.getType() + "/" + dest.getAccountNumber());
		LOG.debug("Looking for the original posting in source and dest");
		
		Transaction srcPosting = this.getTransactionByAccountIdAndTxnRef(source.getId(), request.getTxnRef());
		if(srcPosting == null || srcPosting.getId() == null){
			//throw new Exception("No source account postings found for this transaction.");
			return ResponseCode.E600; 
		}
		LOG.debug("Found the txn posting for the source account");
		Transaction destPosting = this.getTransactionByAccountIdAndTxnRef(dest.getId(), request.getTxnRef());
		if(destPosting == null || destPosting.getId() == null){
			//throw new Exception("No source account postings found for this transaction.");
			return ResponseCode.E600;
		}
		LOG.debug("Found the txn posting for the destination account");
		
		// create the debit txn
		LOG.debug("Doing book entry reversals in bank service");
		//credit the source
		Transaction credit = new Transaction(GenerateKey.generateEntityId(), request.getTxnType(), "CHARGE REVERSAL/" + request.getOriginalTxnRef() + "/" + request.getTxnRef(), request.getSrcAccountId(), request.getAmount(), request.getTransactionDate(), request.getTxnRef(), new Date(System.currentTimeMillis()), request.getOriginalTxnRef(), request.getTxnCategory());
		credit = this.createTransaction(credit);
		LOG.debug("Done crediting the account SRC AccID : " + credit.getAccountId() + " SRC Acc : " + request.getSourceAccountNumber() + " TXN Amt : " + credit.getAmount() + " TXN Ref : " + request.getTxnRef() + " TXN Type : " + request.getTxnType() + " CATE: " + request.getTxnCategory() + " OLD ID " + credit.getOldMessageId());
		// create the credit txn
		LOG.debug("Posting the debit in dest account");
		Transaction debit = new Transaction(GenerateKey.generateEntityId(), request.getTxnType(), "CHARGE REVERSAL/" + request.getOriginalTxnRef() + "/" + request.getTxnRef(), request.getTargetAccountId(), -1 * request.getAmount(), request.getTransactionDate(), request.getTxnRef(), new Date(System.currentTimeMillis()), request.getOriginalTxnRef(), request.getTxnCategory());
		debit = this.createTransaction(debit);
		LOG.debug("Done debiting the account DEST AccID : " + debit.getAccountId() + " DEST Acc : " + request.getTargetAccountNumber() + " TXN Amt : " + debit.getAmount() + " TXN Ref : " + credit.getProcessTxnReference() + " TXN Type : " + request.getTxnType() + " CATE: " + request.getTxnCategory() + " OLD ID " + debit.getOldMessageId());

		// effect account balance on the debited dest account
		AccountBalance sourceAccBalance = new AccountBalance(GenerateKey.generateEntityId(), request.getSrcAccountId(), sourceBal + request.getAmount(), request.getTransactionDate(), new Date(System.currentTimeMillis()), request.getTxnRef(), currentPostingSequence);
		sourceAccBalance = this.createAccountBalance(sourceAccBalance);
		source.setRunningBalance(sourceAccBalance.getAmount());
		this.em.merge(source);
		LOG.debug("DONE Applying account balance for SRC acc");

		// effect account balance on the credited account
		AccountBalance destAccBalance = new AccountBalance(GenerateKey.generateEntityId(), request.getTargetAccountId(), destBal - request.getAmount(), request.getTransactionDate(), new Date(System.currentTimeMillis()), request.getTxnRef(), currentPostingSequence);
		destAccBalance = this.createAccountBalance(destAccBalance);
		dest.setRunningBalance(destAccBalance.getAmount());
		this.em.merge(dest);
		LOG.debug("DONE Applying account balance for DEST acc, going for the charges");
		
		LOG.debug("DONE doing book entry reversal postings returning");
		return ResponseCode.E000;
	}

	private Transaction getTransactionByAccountIdAndTxnRef(String accountId, String processTxnReference) {
		LOG.debug("In BankServiceImpl>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> getTransactionByAccountIdAndTxnRef AccountId = "+accountId+", Ref = "+processTxnReference);
		Query query;
		try {
			query = em.createNamedQuery("getTransactionByAccountIdAndTxnRef").setParameter("accountId", accountId).setParameter("processTxnReference", processTxnReference);
			Transaction txn = (Transaction)query.getResultList().get(0);
			return txn;
		} catch (Exception e) {
			LOG.debug("Thrown Exception in finding txn");
			e.printStackTrace();
			return null;
		}
		
	}

	private ResponseCode reverseMainTransaction(TransactionPostingInfo request, int currentPostingSequence) throws Exception {

		BankAccount source = this.findBankAccountById(request.getSrcAccountId());
		BankAccount dest = this.findBankAccountById(request.getTargetAccountId());
	
		long sourceBal = this.getRunningBalanceByAccountId(source);
		long destBal = this.getRunningBalanceByAccountId(dest);
		if(source == null ||  source.getId() == null){
			throw new Exception("No source account in transaction posting request.");
		}
		
		if(dest == null || dest.getId() == null){
			throw new Exception("No destination account in transaction posting request.");
		}
		LOG.debug("Found the necessary accounts...SRC " + source.getType() + "/" + source.getAccountNumber()  + " DEST " + dest.getType() + "/" + dest.getAccountNumber());
		LOG.debug("Looking for the original posting in source and dest");
		
		Transaction srcPosting = this.getTransactionByAccountIdAndTxnRef(source.getId(), request.getOriginalTxnRef());
		if(srcPosting == null || srcPosting.getId() == null){
			//throw new Exception("No source account postings found for this transaction.");
			return ResponseCode.E600;
		}
		
		Transaction destPosting = this.getTransactionByAccountIdAndTxnRef(dest.getId(), request.getOriginalTxnRef());
		if(destPosting == null || destPosting.getId() == null){
		//	throw new Exception("No source account postings found for this transaction.");
			return ResponseCode.E600;
		}
		
		
		// create the debit txn
		LOG.debug("Doing book entry reversals in bank service");
		//credit the source
		Transaction credit = new Transaction(GenerateKey.generateEntityId(), request.getTxnType(), "REVERSAL/" + request.getOriginalTxnRef(), request.getSrcAccountId(), request.getAmount(), request.getTransactionDate(), request.getTxnRef(), new Date(System.currentTimeMillis()), request.getOriginalTxnRef(), request.getTxnCategory());
		credit = this.createTransaction(credit);
		LOG.debug("Done crediting the account SRC AccID : " + credit.getAccountId() + " SRC Acc : " + request.getSourceAccountNumber() + " TXN Amt : " + credit.getAmount() + " TXN Ref : " + request.getTxnRef() + " TXN Type : " + request.getTxnType() + " CATE: " + request.getTxnCategory() + " OLD ID " + credit.getOldMessageId());
		// create the credit txn
		LOG.debug("Posting the debit in dest account");
		Transaction debit = new Transaction(GenerateKey.generateEntityId(), request.getTxnType(), "REVERSAL/" + request.getOriginalTxnRef(), request.getTargetAccountId(), -1 * request.getAmount(), request.getTransactionDate(), request.getTxnRef(), new Date(System.currentTimeMillis()), request.getOriginalTxnRef(), request.getTxnCategory());
		debit = this.createTransaction(debit);
		LOG.debug("Done debiting the account DEST AccID : " + debit.getAccountId() + " DEST Acc : " + request.getTargetAccountNumber() + " TXN Amt : " + debit.getAmount() + " TXN Ref : " + credit.getProcessTxnReference() + " TXN Type : " + request.getTxnType() + " CATE: " + request.getTxnCategory() + " OLD ID " + debit.getOldMessageId());

		// effect account balance on the debited dest account
		AccountBalance sourceAccBalance = new AccountBalance(GenerateKey.generateEntityId(), request.getSrcAccountId(), sourceBal + request.getAmount(), request.getTransactionDate(), new Date(System.currentTimeMillis()), request.getTxnRef(), currentPostingSequence);
		sourceAccBalance = this.createAccountBalance(sourceAccBalance);
		source.setRunningBalance(sourceAccBalance.getAmount());
		this.em.merge(source);
		LOG.debug("DONE Applying account balance for SRC acc");

		// effect account balance on the credited account
		AccountBalance destAccBalance = new AccountBalance(GenerateKey.generateEntityId(), request.getTargetAccountId(), destBal - request.getAmount(), request.getTransactionDate(), new Date(System.currentTimeMillis()), request.getTxnRef(), currentPostingSequence);
		destAccBalance = this.createAccountBalance(destAccBalance);
		dest.setRunningBalance(destAccBalance.getAmount());
		this.em.merge(dest);
		LOG.debug("DONE Applying account balance for DEST acc, going for the charges");
		
		LOG.debug("DONE doing book entry reversal postings returning");
		return ResponseCode.E000;
		
	}

	public EWalletPostingResponse postEWalletEntries(TransactionPostingInfo [] transactionPostingInfos, ChargePostingInfo [] chargePostingInfos) throws Exception{
		// create the response
		LOG.debug("Received EWALLET posting request ");
//		if(eWalletPostingRequest == null){
//			LOG.debug("EWALLET Posting request cannot be null");
//			throw new Exception("EWALLET Posting request cannot be null");
//		}
//		List<TransactionPostingInfo> transactionPostingInfos = eWalletPostingRequest.getTransactionPostingInfos();
		LOG.debug("Transaction Postings : " + transactionPostingInfos);
		if(transactionPostingInfos == null || transactionPostingInfos.length == 0){
			LOG.debug("Posting request has no transactions");
			throw new Exception("Posting request has no transactions");
		}
		LOG.debug("Now running the postings, the request is VALID");
		int postingSequence = 1;
		for (TransactionPostingInfo transactionPostingInfo : transactionPostingInfos) {
			this.postMainTransaction(transactionPostingInfo, postingSequence++);
		}
//		List<ChargePostingInfo> chargePostingInfos = request.getChargePostingInfos();
		if(chargePostingInfos == null || chargePostingInfos.length == 0){
			LOG.debug("No Charges in transactions TXN TYPE : " );
//			+ request.getTxnType() + " REF : " + request.getTxnRef());
		}else{
			for (ChargePostingInfo chargePostingInfo : chargePostingInfos) {
				this.postTransactionCharge(chargePostingInfo, postingSequence++);
			}			
		}
		
		
		EWalletPostingResponse response = new EWalletPostingResponse(transactionPostingInfos, chargePostingInfos, ResponseCode.E000, "Postings Successful");
		LOG.debug("DONE Posting to eWallet");
		return response;
	}
	
	private ResponseCode postTransactionCharge(ChargePostingInfo request, int currentPostingSequence) throws Exception {
		
		BankAccount source = this.findBankAccountById(request.getSrcAccountId());
		BankAccount dest = this.findBankAccountById(request.getTargetAccountId());
		
		if(source == null || dest == null){
			throw new Exception("No destination or source account in transaction posting request.");
		}
		
		long sourceBal = this.getRunningBalanceByAccountId(source);
		long destBal = this.getRunningBalanceByAccountId(dest);
		
		LOG.debug("Found the necessary accounts...SRC " + source.getType() + "/" + source.getAccountNumber()  + " DEST " + dest.getType() + "/" + dest.getAccountNumber());
		
		// create the debit txn
		LOG.debug("Doing book entry in book entry service");
		Transaction debit = new Transaction(GenerateKey.generateEntityId(), request.getTxnType(), request.getSrcNarrative(), request.getSrcAccountId(), -1 * request.getAmount(), request.getTransactionDate(), request.getTxnRef(), new Date(System.currentTimeMillis()), request.getOriginalTxnRef(), request.getTxnCategory());
		debit = this.createTransaction(debit);
		LOG.debug("Done debiting the account SRC AccID : " + debit.getAccountId() + " SRC Acc : " + request.getSourceAccountNumber() + " TXN Amt : " + debit.getAmount() + " TXN Ref : " + request.getTxnRef() + " TXN Type : " + request.getTxnType() + " CATE: " + request.getTxnCategory() + " OLD ID " + debit.getOldMessageId());
		// create the credit txn
		Transaction credit = new Transaction(GenerateKey.generateEntityId(), request.getTxnType(), request.getTargetNarrative(), request.getTargetAccountId(), request.getAmount(), request.getTransactionDate(), request.getTxnRef(), new Date(System.currentTimeMillis()), request.getOriginalTxnRef(), request.getTxnCategory());
		credit = this.createTransaction(credit);
		LOG.debug("Done crediting the account DEST AccID : " + credit.getAccountId() + " DEST Acc : " + request.getTargetAccountNumber() + " TXN Amt : " + credit.getAmount() + " TXN Ref : " + credit.getProcessTxnReference() + " TXN Type : " + request.getTxnType() + " CATE: " + request.getTxnCategory() + " OLD ID " + credit.getOldMessageId());
		
		// effect account balance on the debited account
		AccountBalance sourceAccBalance = new AccountBalance(GenerateKey.generateEntityId(), request.getSrcAccountId(), sourceBal - request.getAmount(), request.getTransactionDate(), new Date(System.currentTimeMillis()), request.getTxnRef(), currentPostingSequence);
		sourceAccBalance = this.createAccountBalance(sourceAccBalance);
		source.setRunningBalance(sourceAccBalance.getAmount());
		this.em.merge(source);
		LOG.debug("DONE Applying account balance for SRC acc");

		// effect account balance on the credited account
		AccountBalance destAccBalance = new AccountBalance(GenerateKey.generateEntityId(), request.getTargetAccountId(), destBal + request.getAmount(), request.getTransactionDate(), new Date(System.currentTimeMillis()), request.getTxnRef(), currentPostingSequence);
		destAccBalance = this.createAccountBalance(destAccBalance);
		dest.setRunningBalance(destAccBalance.getAmount());
		this.em.merge(dest);
		LOG.debug("DONE Applying account balance for DEST acc");

		LOG.debug("DONE doing book entry postings returning");
		return ResponseCode.E000;
	
	}
	
	private ResponseCode postMainTransaction(TransactionPostingInfo request, int currentPostingSequence) throws Exception {
		
		BankAccount source = this.findBankAccountById(request.getSrcAccountId());
		BankAccount dest = this.findBankAccountById(request.getTargetAccountId());
		
		long sourceBal = this.getRunningBalanceByAccountId(source);
		long destBal = this.getRunningBalanceByAccountId(dest);
		
		if(source == null || dest == null){
			throw new Exception("No destination or source account in transaction posting request.");
		}
		LOG.debug("Found the necessary accounts...SRC " + source.getType() + "/" + source.getAccountNumber()  + " DEST " + dest.getType() + "/" + dest.getAccountNumber());
		
		// create the debit txn
		LOG.debug("Doing book entry in book entry service");
		Transaction debit = new Transaction(GenerateKey.generateEntityId(), request.getTxnType(), request.getSrcNarrative(), request.getSrcAccountId(), -1 * request.getAmount(), request.getTransactionDate(), request.getTxnRef(), new Date(System.currentTimeMillis()), request.getOriginalTxnRef(), request.getTxnCategory());
		debit = this.createTransaction(debit);
		LOG.debug("Done debiting the account SRC AccID : " + debit.getAccountId() + " SRC Acc : " + request.getSourceAccountNumber() + " TXN Amt : " + debit.getAmount() + " TXN Ref : " + request.getTxnRef() + " TXN Type : " + request.getTxnType() + " CATE: " + request.getTxnCategory() + " OLD ID " + debit.getOldMessageId());
		// create the credit txn
		Transaction credit = new Transaction(GenerateKey.generateEntityId(), request.getTxnType(), request.getTargetNarrative(), request.getTargetAccountId(), request.getAmount(), request.getTransactionDate(), request.getTxnRef(), new Date(System.currentTimeMillis()), request.getOriginalTxnRef(), request.getTxnCategory());
		credit = this.createTransaction(credit);
		LOG.debug("Done crediting the account DEST AccID : " + credit.getAccountId() + " DEST Acc : " + request.getTargetAccountNumber() + " TXN Amt : " + credit.getAmount() + " TXN Ref : " + credit.getProcessTxnReference() + " TXN Type : " + request.getTxnType() + " CATE: " + request.getTxnCategory() + " OLD ID " + credit.getOldMessageId());

		// effect account balance on the debited account
		AccountBalance sourceAccBalance = new AccountBalance(GenerateKey.generateEntityId(), request.getSrcAccountId(), sourceBal - request.getAmount(), request.getTransactionDate(), new Date(System.currentTimeMillis()), request.getTxnRef(), currentPostingSequence);
		sourceAccBalance = this.createAccountBalance(sourceAccBalance);
		source.setRunningBalance(sourceAccBalance.getAmount());
		this.em.merge(source);
		LOG.debug("DONE Applying account balance for SRC acc");

		// effect account balance on the credited account
		AccountBalance destAccBalance = new AccountBalance(GenerateKey.generateEntityId(), request.getTargetAccountId(), destBal + request.getAmount(), request.getTransactionDate(), new Date(System.currentTimeMillis()), request.getTxnRef(), currentPostingSequence);
		destAccBalance = this.createAccountBalance(destAccBalance);
		dest.setRunningBalance(destAccBalance.getAmount());
		this.em.merge(dest);
		LOG.debug("DONE Applying account balance for DEST acc, going for the charges");
		
		LOG.debug("DONE doing book entry postings returning");
		return ResponseCode.E000;
	}

	private long getRunningBalanceByAccountId(BankAccount account) throws Exception{
		LOG.debug("Looking for account balance for " + account.getAccountNumber() +"/" + account.getId());
		if(account == null || account.getId() == null){
			throw new Exception("Failed to retrieve bank account");
		}
		LOG.debug("Going for the query..");
		List<AccountBalance> accountBalances;
		try {
			Query query = em.createNamedQuery("getRunningBalance").setParameter("accountId", account.getId());
			LOG.debug("Query is " + query);
			accountBalances = (List<AccountBalance>)query.getResultList();
			LOG.debug("Query result is " + accountBalances);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.debug("Failed to retrieve balance, bug in query. throwing exception...");
			throw new Exception("Exception occurred while retrieving current balance");
		}
		AccountBalance bal;
		if(accountBalances == null || accountBalances.isEmpty()){
			LOG.debug("Account Balance has not yet been created for account  " + account.getAccountNumber() +"/" + account.getId());
			bal = new AccountBalance(GenerateKey.generateEntityId(), account.getId(), account.getRunningBalance(), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), "", 1);
			bal = this.createAccountBalance(bal);
		}else{
			bal = accountBalances.get(0);
			LOG.debug("Balance found for the account " + account.getAccountNumber() +"/" + account.getId() + " IS " + bal.getAmount() + " as at " + bal.getDateCreated());
		}
		LOG.debug("Returning balance");
		return bal.getAmount();
	}
	
	public AccountBalance createAccountBalance(AccountBalance accountBalance) throws EWalletException {
		try {
			if (accountBalance == null) {
				return null;
			}
			accountBalance.setId(GenerateKey.generateEntityId());
			accountBalance.setDateCreated(new Date());
			if(accountBalance.getBalanceDate() == null){
				accountBalance.setBalanceDate(accountBalance.getDateCreated());
			}
			LOG.debug(accountBalance.toString());
			em.persist(accountBalance);
		} catch (Exception e) {
			e.printStackTrace();
			throw new EWalletException(e.getMessage());
		}
		return accountBalance;
	}

	public String deleteAccountBalance(String accountBalanceId) throws EWalletException {
		try {
			AccountBalance accountBalance = em.find(AccountBalance.class, accountBalanceId);
			accountBalance = em.merge(accountBalance);
			em.remove(accountBalance);
		} catch (Exception e) {
			e.printStackTrace();
			throw new EWalletException(e.getMessage());
		}
		return "success";
	}

	public AccountBalance updateAccountBalance(AccountBalance accountBalance) throws EWalletException {
		try {
			accountBalance = em.merge(accountBalance);
		} catch (Exception e) {
			e.printStackTrace();
			throw new EWalletException(e.getMessage());
		}
		return accountBalance;
	}

	public AccountBalance findAccountBalanceById(String id) {
		AccountBalance accountBalance = null;
		try {
			accountBalance = (AccountBalance) em.find(AccountBalance.class, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accountBalance;
	}

	public Transaction createTransaction(Transaction transaction) throws EWalletException {
		try {
			if (transaction == null) {
				return null;
			}
			if (transaction.getId() == null) {
				transaction.setId(GenerateKey.generateEntityId());
			}
			if (transaction.getDateCreated() == null) {
				transaction.setDateCreated(new Date());
			}
			if (transaction.getTransactionDate() == null) {
				transaction.setTransactionDate(new Date());
			}
			System.out.println("BOOK ENTRY TXN INFO : AccID : " + transaction.getAccountId() + " AMT : " + transaction.getAmount() + " TXN ID : " + transaction.getId() + " NARR : " + transaction.getNarrative() + " OLD MSG ID : " + transaction.getOldMessageId() + " TXN REF : " + transaction.getProcessTxnReference() + " TXN Category " + transaction.getTransactionCategory() + " TXN TYPE " + transaction.getType() + " TXN DATE : " + transaction.getTransactionDate() + " TXN Date Created :  " + transaction.getDateCreated() + " Version : " + transaction.getVersion());
			em.persist(transaction);
		} catch (Exception e) {
			e.printStackTrace();
			throw new EWalletException(e.getMessage());
		}
		return transaction;
	}

	public String deleteTransaction(String transactionId) throws EWalletException {
		try {
			Transaction transaction = em.find(Transaction.class, transactionId);
			transaction = em.merge(transaction);
			em.remove(transaction);
		} catch (Exception e) {
			e.printStackTrace();
			throw new EWalletException(e.getMessage());
		}
		return "success";
	}

	public Transaction updateTransaction(Transaction transaction) throws EWalletException {
		try {
			transaction = em.merge(transaction);
		} catch (Exception e) {
			e.printStackTrace();
			throw new EWalletException(e.getMessage());
		}
		return transaction;
	}

	public Transaction findTransactionById(String id) {
		Transaction transaction = null;
		try {
			transaction = (Transaction) em.find(Transaction.class, id);
		} catch (Exception e) {
			e.printStackTrace();

		}
		return transaction;
	}

	@SuppressWarnings("unchecked")
	public List<Transaction> getTransactionByProcessTransactionMessageId(String messageId) {
		List<Transaction> results = null;
		try {
			Query query = em.createNamedQuery("getTransactionByProcessMessageId");
			query.setParameter("messageId", messageId);
			results = (List<Transaction>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<Transaction> findAllTransactions() {
		List<Transaction> results = null;
		try {
			Query query = em.createNamedQuery("getTransaction");
			results = (List<Transaction>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return results;

	}

	@SuppressWarnings("unchecked")
	public List<AccountBalance> findAllAccountBalances() {
		List<AccountBalance> results = null;
		try {
			Query query = em.createNamedQuery("getAccountBalance");
			results = (List<AccountBalance>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return results;

	}

	public AccountBalance getAccountBalanceByAccountAndDate(String accountId, Date date) throws Exception {
		AccountBalance accountBalance = null;
		try {
			Query query = em.createQuery("SELECT a FROM AccountBalance a WHERE a.accountId = :accountId AND a.balanceDate = :date");
			query.setParameter("accountId", accountId);
			query.setParameter("date", date);
			accountBalance = (AccountBalance) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return accountBalance;
	}

	@SuppressWarnings("unchecked")
	public List<AccountBalance> getAccountBalanceByAccount(String accountId) throws Exception {
		List<AccountBalance> results = null;
		try {
			Query query = em.createQuery("SELECT a FROM AccountBalance a WHERE a.accountId = :accountId");
			query.setParameter("accountId", accountId);
			results = (List<AccountBalance>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {

		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<Transaction> getTransactionsByAllAttributes(TransactionUniversalPojo universal) {
		List<Transaction> results = null;
		String qStr = "SELECT p ";
		String orderBy = "ORDER BY p.dateCreated DESC";
		try {

			Query query = this.getTransactionsUniversalQuery(universal, qStr, orderBy);
			results = (List<Transaction>) query.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (results != null) {
			if (results.isEmpty()) {
				results = null;
			} else {
				for (Transaction t : results) {
					System.out.println("****************   In BookEntry.. Transaction :" + t.getType().name() + " " + t.getAmount());
				}
			}
		}
		return results;
	}

	private Query getTransactionsUniversalQuery(TransactionUniversalPojo uni, String selectClause, String orderBy) throws Exception {
		Date fromDate = DateUtil.getBeginningOfDay(uni.getFromDate());
		Date toDate = DateUtil.getEndOfDay(uni.getToDate());
		Query results = null;
		zw.co.esolutions.ewallet.enums.TransactionType txnType = uni.getType();
		String messageId = uni.getProcessTxnReference();
		String oldMessageId = uni.getOldMessageId();
		String accountId = uni.getAccountId();
		TransactionCategory transactionCategory = uni.getTransactionCategory();
		String qStr = selectClause + "FROM Transaction p WHERE p.dateCreated IS NOT NULL ";

		try {

			if (fromDate != null && toDate != null) {
				qStr = qStr + "AND p.dateCreated >= :fromDate AND p.dateCreated <= :toDate ";
			}
			if (messageId != null) {
				qStr = qStr + "AND p.processTxnReference = :messageId ";
			}
			if (oldMessageId != null) {
				qStr = qStr + "AND p.oldMessageId = :oldMessageId ";
			}
			if (txnType != null) {
				qStr = qStr + "AND p.type = :transactionType ";
			}
			if (accountId != null) {
				qStr = qStr + "AND p.accountId = :accountId ";
			}
			if (transactionCategory != null) {
				qStr = qStr + "AND p.transactionCategory = :transactionCategory ";
			}
			qStr = qStr + orderBy;
			Query query = em.createQuery(qStr);

			if (fromDate != null && toDate != null) {
				query.setParameter("fromDate", fromDate);
				query.setParameter("toDate", toDate);
			}
			if (messageId != null) {
				query.setParameter("messageId", messageId);
			}
			if (oldMessageId != null) {
				query.setParameter("oldMessageId", oldMessageId);
			}
			if (txnType != null) {
				query.setParameter("transactionType", txnType);
			}
			if (accountId != null) {
				query.setParameter("accountId", accountId);
			}
			if (transactionCategory != null) {
				query.setParameter("transactionCategory", transactionCategory);
			}

			results = query;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return results;
	}

	@SuppressWarnings("unchecked")
	public List<AccountBalance> getAccountBalanceByTxnRef(String txnRef) {
		List<AccountBalance> results = null;
		try {
			Query query = em.createQuery("SELECT a FROM AccountBalance a WHERE a.transactionRef = :txnRef");
			query.setParameter("txnRef", txnRef);
			results = (List<AccountBalance>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {

		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<AccountBalance> getAccountBalanceByAccountIdAndTxnRef(String accountId, String txnRef) {
		List<AccountBalance> results = null;
		try {
			Query query = em.createQuery("SELECT a FROM AccountBalance a WHERE a.transactionRef = :txnRef AND a.accountId = :accountId");
			query.setParameter("txnRef", txnRef);
			query.setParameter("accountId", accountId);
			results = (List<AccountBalance>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {

		}
		return results;
	}
	
	public AccountBalance getOpeningBalance(String accountId, Date date) throws Exception {
		AccountBalance accountBalance = null;
		try {
			date = DateUtil.addDays(date, -1);
			accountBalance = this.getClosingBalance(accountId, date);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return accountBalance;
	}

	public AccountBalance getClosingBalance(String accountId, Date date) throws Exception {
		AccountBalance accountBalance = null;
		Date toDate;
		try {
			toDate = DateUtil.getBusinessDayEndOfDay(date);
			Query query = em.createQuery("SELECT a FROM AccountBalance a WHERE a.accountId = :accountId AND a.balanceDate IN " + "(SELECT MAX(a1.balanceDate) FROM AccountBalance a1 WHERE a1.accountId = :accountId AND a1.balanceDate <= :toDate)");
			query.setParameter("accountId", accountId);
			query.setParameter("toDate", toDate);
			accountBalance = (AccountBalance) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return accountBalance;
	}
	
	@Override
	public TransactionSummaryItem getTransactionSummaryItem(String accountId, Date txnDate, TransactionType txnType) throws Exception{
		TransactionSummaryItem txnSummaryItem = null;
		Date txnEndDate = DateUtil.getEndOfDay(txnDate);
		Date txnStartDate = DateUtil.getBeginningOfDay(txnDate);
		Query query;
		try {
			query = em.createNamedQuery("getTransactionSummaryItemByTransactionTypeAndDateRange");
			query = query.setParameter("accountId", accountId).setParameter("txnType", txnType).setParameter("txnStartDate", txnStartDate).setParameter("txnEndDate", txnEndDate);
			txnSummaryItem = (TransactionSummaryItem)query.getSingleResult();
		}catch (NoResultException e) {
			LOG.debug("No result exception, return null ");
		} 		
		catch (Exception e) {
			LOG.debug("Failed to execute query.." + e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return txnSummaryItem;
		
	}

	@Override
	public TransactionSummary getTransactionSummary(String accountId, Date txnDate) throws Exception{
		TransactionSummary txnSummary = new TransactionSummary();
		BankAccount account = this.findBankAccountById(accountId);
		if(account == null || account.getId() == null){
			LOG.debug("Account with id " + accountId +" not found, returning null");
			return null;
		}
		AccountBalance openingBalance = this.getOpeningBalance(accountId, txnDate);
		AccountBalance closingBalance = this.getClosingBalance(accountId, txnDate);
		
		txnSummary.setHeader("TXN SUMMARY for " + account.getAccountNumber() + "(" +Formats.shortDateFormat.format(txnDate) +") as at " + Formats.tautteeShortDateTimeFormat.format(new Date(System.currentTimeMillis())) +"[nl]");
		txnSummary.setOpeningBal(openingBalance == null ? 0L : openingBalance.getAmount());
		txnSummary.setClosingBal(closingBalance == null ? 0L : closingBalance.getAmount());
		
		TransactionSummaryItem dep = this.getTransactionSummaryItem(accountId, txnDate, TransactionType.DEPOSIT);
		TransactionSummaryItem agtDep = this.getTransactionSummaryItem(accountId, txnDate, TransactionType.AGENT_CUSTOMER_DEPOSIT);
		TransactionSummaryItem wdl = this.getTransactionSummaryItem(accountId, txnDate, TransactionType.WITHDRAWAL);
		TransactionSummaryItem agtWDL = this.getTransactionSummaryItem(accountId, txnDate, TransactionType.AGENT_CUSTOMER_WITHDRAWAL);
		TransactionSummaryItem nhlWDL = this.getTransactionSummaryItem(accountId, txnDate, TransactionType.WITHDRAWAL_NONHOLDER);
		TransactionSummaryItem agntNHLWDL = this.getTransactionSummaryItem(accountId, txnDate, TransactionType.AGENT_CUSTOMER_NONHOLDER_WITHDRAWAL);
		
		txnSummary.setNetMovement(this.getTotalValue(dep, agtDep) - this.getTotalValue(wdl, agtWDL, nhlWDL, agntNHLWDL));
		
		String depNarration  = "DEP : " + (this.getTotalCount(dep, agtDep)) + "/" + MoneyUtil.convertToDollars(this.getTotalValue(dep, agtDep)) + "[nl]";
		String wdlNarration  = "WDL : " + (this.getTotalCount(wdl, agtWDL, nhlWDL, agntNHLWDL)) + "/" + MoneyUtil.convertToDollars(this.getTotalValue(wdl, agtWDL, nhlWDL, agntNHLWDL)) + "[nl]"; 
		txnSummary.setSummaryDetails(depNarration + wdlNarration);
		
		txnSummary.setDescription(txnSummary.getHeader() + "OB = " + MoneyUtil.convertToDollars(txnSummary.getOpeningBal()) +"[nl]" + txnSummary.getSummaryDetails() + "NET MVMT = " + MoneyUtil.convertToDollars(txnSummary.getNetMovement()) +"[nl] CB = "+ MoneyUtil.convertToDollars(txnSummary.getClosingBal()) +"[nl]");
		
		return txnSummary;
	}
	
	private long getTotalValue(TransactionSummaryItem ...items){
		long totalAmount = 0L;
		for (TransactionSummaryItem transactionSummaryItem : items) {
			if(transactionSummaryItem == null){
				continue;
			}
			totalAmount += transactionSummaryItem.getTotalTxnValue();
		}
		return totalAmount;
	}
	
	private long getTotalCount(TransactionSummaryItem ...items){
		long totalCount = 0L;
		for (TransactionSummaryItem transactionSummaryItem : items) {
			if(transactionSummaryItem == null){
				continue;
			}
			totalCount += transactionSummaryItem.getNumberOfTransactions();
		}
		return totalCount;
	}
}
