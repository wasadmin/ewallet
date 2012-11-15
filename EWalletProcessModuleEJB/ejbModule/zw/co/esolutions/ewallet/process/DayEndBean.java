package zw.co.esolutions.ewallet.process;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountType;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.enums.DayEndStatus;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.msg.BankRequest;
import zw.co.esolutions.ewallet.process.model.DayEnd;
import zw.co.esolutions.ewallet.process.model.DayEndSummary;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.sms.MessageSender;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.GenerateKey;

/**
 * Session Bean implementation class DayEndBean
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class DayEndBean implements DayEndBeanLocal {
	@PersistenceContext
	private EntityManager em;

	@EJB
	private ProcessUtil processUtil;
	/**
	 * Default constructor.
	 */
	public DayEndBean() {
	}

	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(DayEndBean.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + DayEndBean.class);
		}
	}

	@Override
	public DayEnd createDayEnd(DayEnd dayEnd, String userName) throws Exception {
		try {
			LOG.debug(" in session bean   " + dayEnd.getCashTendered());
			LOG.debug("  username::::" + userName);
			if (dayEnd != null) {
				ProfileServiceSOAPProxy proxy = new ProfileServiceSOAPProxy();
				Profile profile = proxy.getProfileByUserName(userName);
				LOG.debug("profile::::::::::" + profile);
				dayEnd.setDayEndId(GenerateKey.generateEntityId());
				dayEnd.setDateCreated(new Date());
				LOG.debug("2 end method");
				dayEnd.setTellerId(profile.getId());
				dayEnd.setBranchId(profile.getBranchId());
				// dayEnd.setStatus(DayEndStatus.AWAITING_APPROVAL);
				em.persist(dayEnd);
				LOG.debug("end method");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return dayEnd;
	}

	@Override
	public DayEndSummary createDayEndSummary(DayEndSummary summary, String userName) throws Exception {
		try {
			if (summary != null) {
				summary.setId(GenerateKey.generateEntityId());
				summary.setDateCreated(new Date());
				em.persist(summary);
				LOG.debug("summary persisted " + summary.getTransactionType());
			}
		} catch (Exception e) {
			LOG.debug("Summary Exception");
			throw new Exception(e);

		}

		return summary;
	}

	@Override
	public DayEnd editDayEnd(DayEnd dayEnd, String userName) throws Exception {
		return updateDayEnd1(dayEnd);
	}

	public DayEnd updateDayEnd1(DayEnd dayEnd) throws Exception {
		LOG.debug(" in update method of day end    " + dayEnd.getStatus());
		try {
			dayEnd = em.merge(dayEnd);
		} catch (Exception e) {
			LOG.debug("Exception in update method");
			throw new Exception(e);
		}
		LOG.debug("In update method done");
		return dayEnd;
	}

	// @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public DayEnd updateDayEnd(DayEnd dayEnd) throws Exception {
		LOG.debug(" in update method of day end    ");
		try {
			dayEnd = em.merge(dayEnd);
		} catch (Exception e) {
			LOG.debug("Exception in update method");
			throw new Exception(e);
		}
		LOG.debug("In update method done");
		return dayEnd;
	}

	@Override
	public DayEnd findDayEndById(String dayEndId, String userName) {
		DayEnd dayEnd = null;
		try {

			dayEnd = em.find(DayEnd.class, dayEndId);
			/*
			 * for(DayEndSummary summary : dayEnd.getDayendsummaryList()){
			 * summary.getProcesstransactionList(); }
			 */
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
		return dayEnd;
	}

	@Override
	public DayEndSummary editDayEndSummary(DayEndSummary summary, String userName) throws Exception {
		LOG.debug("Updating day summary");
		return updateDayEndSummary(summary);
	}

	public DayEndSummary updateDayEndSummary(DayEndSummary summary) throws Exception {
		LOG.debug("-----------------------" + summary);
		try {
			if (summary != null) {
				summary = em.merge(summary);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.debug("------------------------------summary update exception----------------------------------------");
		}
		LOG.debug("Updating summary    dayEnd VALUE type==========================" + summary.getValueOfTxns());
		LOG.debug("Updating summary    dayEnd NUMBER==========================" + summary.getNumberOfTxn());
		LOG.debug("Updating summary    dayEnd txn type==========================" + summary.getTransactionType().name());
		return summary;

	}

	@Override
	public DayEndSummary findDayEndSummary(String id, String userName) {
		DayEndSummary dayEndSummary = null;
		if (id != null) {
			dayEndSummary = em.find(DayEndSummary.class, id);
		}
		return dayEndSummary;
	}

	@Override
	public List<DayEnd> getDayEndByTellerIdAndDayEndDate(String tellerId, Date dayEndDate) {
		List<DayEnd> dayEnds = new ArrayList<DayEnd>();
		LOG.debug("1");
		try {
			Query query = em.createNamedQuery("getDayEndByTellerIdAndDayEndDate");
			query.setParameter("tellerId", tellerId);
			query.setParameter("dayEndDate", dayEndDate);

			dayEnds = (List<DayEnd>) query.getResultList();
			LOG.debug("2Day End Date   " + dayEnds.size());
		} catch (Exception e) {
			LOG.debug("Exception ;;;" + e.getMessage());

			e.printStackTrace();
			return null;
		}

		return dayEnds;
	}

	@Override
	// @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public DayEnd approveDayEnd(DayEnd dayEnd, String userName) throws Exception {
		LOG.debug("In DayEnd bean");
		/*
		 * //try { String dayEndId=dayEnd.getDayEndId();
		 * LOG.debug("dayend id    "+dayEndId); List<DayEndSummary>
		 * dayEndSummaries=getDayEndSummariesByDayEnd(dayEndId); // do posting
		 * after checking for imbalances
		 * LOG.debug("got day end summaries "+dayEndSummaries);
		 * if(dayEndSummaries!=null){
		 * LOG.debug(" number of summaries   "+dayEndSummaries.size()); } long
		 * withDrawalSum
		 * =getSumValue(TransactionType.WITHDRAWAL,dayEndSummaries); long
		 * nonHolderWithDrawalSum
		 * =getSumValue(TransactionType.WITHDRAWAL_NONHOLDER,dayEndSummaries);
		 * long depositSum=getSumValue(TransactionType.DEPOSIT,dayEndSummaries);
		 * long cashTendered=dayEnd.getCashTendered(); long
		 * balance=depositSum-withDrawalSum-nonHolderWithDrawalSum;
		 * LOG.debug("  withdrawal sum  "+withDrawalSum);
		 * LOG.debug("  nonholder sum  "+nonHolderWithDrawalSum);
		 * LOG.debug("  desposit sum  "+depositSum);
		 * LOG.debug("  cashtendered  sum  "+cashTendered);
		 * LOG.debug("  balance sum  "+balance);
		 * ReferenceGeneratorServiceSOAPProxy referenceProxy=new
		 * ReferenceGeneratorServiceSOAPProxy(); String
		 * year=Formats.yearFormatTwoDigit.format(new Date()); String
		 * sequenceName="Day Ends"; String prefix="A"; String
		 * depositRef=referenceProxy.generateUUID(sequenceName, prefix, year, 0,
		 * 999999999); String
		 * noHolderWithRef=referenceProxy.generateUUID(sequenceName, prefix,
		 * year, 0, 999999999); String
		 * withDrawalRef=referenceProxy.generateUUID(sequenceName, prefix, year,
		 * 0, 999999999); LOG.debug("deposit ref   "+depositRef);
		 * LOG.debug("noHolderWithRef ref   "+noHolderWithRef);
		 * LOG.debug("withDrawalRef ref   "+withDrawalRef);
		 * 
		 * //balances make postings //2 equation and e-wallet
		 * 
		 * 
		 * LOG.debug("Performing euation postings ");
		 * doEquationPostings(dayEnd,depositSum
		 * ,zw.co.esolutions.ewallet.enums.BankAccountType
		 * .EWALLET_BRANCH_CASH_ACCOUNT
		 * ,zw.co.esolutions.ewallet.enums.BankAccountType
		 * .BRANCH_CASH_ACCOUNT,TransactionType.DEPOSIT,depositRef);
		 * doEquationPostings
		 * (dayEnd,withDrawalSum,zw.co.esolutions.ewallet.enums
		 * .BankAccountType.BRANCH_CASH_ACCOUNT
		 * ,zw.co.esolutions.ewallet.enums.BankAccountType
		 * .EWALLET_BRANCH_CASH_ACCOUNT
		 * ,TransactionType.WITHDRAWAL,withDrawalRef);
		 * doEquationPostings(dayEnd,
		 * nonHolderWithDrawalSum,zw.co.esolutions.ewallet
		 * .enums.BankAccountType.
		 * BRANCH_CASH_ACCOUNT,zw.co.esolutions.ewallet.enums
		 * .BankAccountType.EWALLET_BRANCH_CASH_ACCOUNT
		 * ,TransactionType.WITHDRAWAL_NONHOLDER,noHolderWithRef); //book
		 * entries //Deposits LOG.debug("deposit book entries");
		 * doDayEndBookEntry(dayEnd, depositSum,
		 * zw.co.esolutions.ewallet.enums.BankAccountType
		 * .EWALLET_BRANCH_CASH_ACCOUNT, TransactionActionType.CREDIT,
		 * TransactionType.DEPOSIT, depositRef); doDayEndBookEntry(dayEnd,
		 * depositSum,
		 * zw.co.esolutions.ewallet.enums.BankAccountType.BRANCH_CASH_ACCOUNT,
		 * TransactionActionType.DEBIT, TransactionType.DEPOSIT, depositRef); //
		 * Withdrawals LOG.debug("withdrawal book entries");
		 * doDayEndBookEntry(dayEnd, depositSum,
		 * zw.co.esolutions.ewallet.enums.BankAccountType
		 * .EWALLET_BRANCH_CASH_ACCOUNT, TransactionActionType.DEBIT,
		 * TransactionType.WITHDRAWAL, depositRef); doDayEndBookEntry(dayEnd,
		 * depositSum,
		 * zw.co.esolutions.ewallet.enums.BankAccountType.BRANCH_CASH_ACCOUNT,
		 * TransactionActionType.CREDIT, TransactionType.WITHDRAWAL,
		 * depositRef);
		 * 
		 * //Withdrawals Non-Holder LOG.debug("non holder book entries");
		 * doDayEndBookEntry(dayEnd, depositSum,
		 * zw.co.esolutions.ewallet.enums.BankAccountType
		 * .EWALLET_BRANCH_CASH_ACCOUNT, TransactionActionType.DEBIT,
		 * TransactionType.WITHDRAWAL_NONHOLDER, depositRef);
		 * doDayEndBookEntry(dayEnd, depositSum,
		 * zw.co.esolutions.ewallet.enums.BankAccountType.BRANCH_CASH_ACCOUNT,
		 * TransactionActionType.CREDIT, TransactionType.WITHDRAWAL_NONHOLDER,
		 * depositRef);
		 * 
		 * 
		 * if(cashTendered>balance){ LOG.debug("over post"); //overpost
		 * //imbalance ocurrs make appropriate posting String
		 * overpostRef=referenceProxy.generateUUID(sequenceName, prefix, year,
		 * 0, 999999999);
		 * doEquationPostings(dayEnd,balance,zw.co.esolutions.ewallet
		 * .enums.BankAccountType
		 * .EWALLET_BALANCING_SUSPENSE_ACCOUNT,zw.co.esolutions
		 * .ewallet.enums.BankAccountType
		 * .EWALLET_BRANCH_CASH_ACCOUNT,TransactionType
		 * .BANKACCOUNT_TO_BANKACCOUNT_TRANSFER,overpostRef); //Book Entries
		 * doDayEndBookEntry(dayEnd, balance,
		 * zw.co.esolutions.ewallet.enums.BankAccountType
		 * .EWALLET_BALANCING_SUSPENSE_ACCOUNT, TransactionActionType.DEBIT,
		 * TransactionType.WITHDRAWAL_NONHOLDER, depositRef);
		 * doDayEndBookEntry(dayEnd, balance,
		 * zw.co.esolutions.ewallet.enums.BankAccountType
		 * .EWALLET_BRANCH_CASH_ACCOUNT, TransactionActionType.CREDIT,
		 * TransactionType.WITHDRAWAL_NONHOLDER, depositRef);
		 * 
		 * }else if(cashTendered<balance){ LOG.debug("under post"); //underpost
		 * String underpostRef=referenceProxy.generateUUID(sequenceName, prefix,
		 * year, 0, 999999999);
		 * doEquationPostings(dayEnd,balance,zw.co.esolutions
		 * .ewallet.enums.BankAccountType
		 * .EWALLET_BRANCH_CASH_ACCOUNT,zw.co.esolutions
		 * .ewallet.enums.BankAccountType
		 * .EWALLET_BALANCING_SUSPENSE_ACCOUNT,TransactionType
		 * .BANKACCOUNT_TO_BANKACCOUNT_TRANSFER,underpostRef);
		 * 
		 * doDayEndBookEntry(dayEnd, balance,
		 * zw.co.esolutions.ewallet.enums.BankAccountType
		 * .EWALLET_BRANCH_CASH_ACCOUNT, TransactionActionType.DEBIT,
		 * TransactionType.WITHDRAWAL_NONHOLDER, depositRef);
		 * doDayEndBookEntry(dayEnd, balance,
		 * zw.co.esolutions.ewallet.enums.BankAccountType
		 * .EWALLET_BALANCING_SUSPENSE_ACCOUNT, TransactionActionType.CREDIT,
		 * TransactionType.WITHDRAWAL_NONHOLDER, depositRef);
		 * 
		 * } //doDayEndBookEntry(dayEnd);
		 * 
		 * LOG.debug("Setting day end status");
		 */dayEnd.setStatus(DayEndStatus.APPROVED);

		LOG.debug("updating");
		updateDayEnd(dayEnd);
		LOG.debug("update done approve method returning");
		return dayEnd;

	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	private void doEquationPostings(DayEnd dayEnd, long amount, zw.co.esolutions.ewallet.enums.BankAccountType targetAccount, zw.co.esolutions.ewallet.enums.BankAccountType sourceAccount, TransactionType deposit, String reference) throws Exception {

		if (amount != 0) {
			String sourceAccountNumber = getAccountByAccountType(dayEnd, sourceAccount);
			String targetAccountNumber = getAccountByAccountType(dayEnd, targetAccount);
			// LOG.debug(deposit+"  sourceaccount number  "+sourceAccountNumber);
			// LOG.debug(deposit+"  target account number  "+targetAccountNumber);
			BankRequest postRequest = new BankRequest();
			postRequest.setAmount(amount);
			postRequest.setSourceAccountNumber(sourceAccountNumber);
			postRequest.setTargetAccountNumber(targetAccountNumber);
			postRequest.setReference(reference);
			try {
				processUtil.submitRequest(postRequest, false);
				LOG.debug("Message sent for " + deposit + "    target acc" + targetAccountNumber);
			} catch (Exception e) {
				// LOG.debug("Exception in posting to equation   txt type"+deposit);
				e.printStackTrace();
				throw new Exception(e.getMessage());
			}
		} else if (amount == 0) {
			// posting not done
			LOG.debug(" nothing to do for " + deposit);
		}
	}

	private String getAccountByAccountType(DayEnd endDay, zw.co.esolutions.ewallet.enums.BankAccountType sourceAccount) throws Exception {
		BankServiceSOAPProxy proxy = new BankServiceSOAPProxy();
		String sourceAcc = sourceAccount.toString();
		// LOG.debug("Source account type defined   "+sourceAcc);

		String accountNumber = null;
		BankAccountType accountType = null;

		for (BankAccountType accountT : BankAccountType.values()) {
			if (accountT.toString().equalsIgnoreCase(sourceAcc)) {
				accountType = accountT;
			}
		}
		LOG.debug(" branch id" + endDay.getBranchId());
		LOG.debug("Account Type " + accountType);
		LOG.debug(" owner type " + zw.co.esolutions.ewallet.bankservices.service.OwnerType.BANK_BRANCH);
		BankAccount bankAccount = proxy.getBankAccountByAccountHolderAndTypeAndOwnerType(endDay.getBranchId(), accountType, zw.co.esolutions.ewallet.bankservices.service.OwnerType.BANK_BRANCH, null);
		accountNumber = bankAccount.getAccountNumber();
		return accountNumber;
	}

	private long getSumValue(TransactionType withdrawal, List<DayEndSummary> dayEndSummaries) {
		for (DayEndSummary summary : dayEndSummaries) {
			if (withdrawal.equals(summary.getTransactionType())) {
				return summary.getValueOfTxns();
			}
		}
		return 0;
	}

	public List<DayEndSummary> getDayEndSummariesByDayEnd(String dayEndId) {
		LOG.debug("Getting day end summaries");
		List<DayEndSummary> results = null;
		try {
			Query query = em.createNamedQuery("getDayEndSummariesByDayEnd");

			query.setParameter("dayEndId", dayEndId);

			results = (List<DayEndSummary>) query.getResultList();
		} catch (Exception e) {
			LOG.debug("System error retriving dayend summaries  " + dayEndId);
			e.printStackTrace();
			return null;
		}
		if (results == null || results.isEmpty()) {
			results = null;
		}
		return results;
	}

	@Override
	public List<DayEnd> getDayEndByDayEndStatus(DayEndStatus dayEndStatus, String userName) {
		List<DayEnd> results = null;
		try {
			Query query = em.createNamedQuery("getDayEndByDayEndStatus");
			query.setParameter("status", dayEndStatus);
			results = (List<DayEnd>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();

		}
		if (results == null || results.isEmpty()) {
			results = null;
		}
		return results;
	}

	@Override
	public List<DayEnd> getDayEndByDayEndStatusAndDateRange(DayEndStatus dayEndStatus, Date fromDate, Date toDate, String userName) {
		List<DayEnd> results = null;
		try {
			Query query = em.createNamedQuery("getDayEndByDayEndStatusAndDateRange");
			query.setParameter("status", dayEndStatus);
			query.setParameter("fromDate", DateUtil.getBeginningOfDay(fromDate));
			query.setParameter("toDate", DateUtil.getEndOfDay(toDate));
			results = (List<DayEnd>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (results == null || results.isEmpty()) {
			results = null;
		}
		return results;
	}

	@Override
	public List<DayEndSummary> getDayEndSummaryByDayEndId(String dayEndId, String userName) {
		List<DayEndSummary> results = null;
		try {
			Query query = em.createNamedQuery("getDayEndSummariesByDayEnd");

			query.setParameter("dayEndId", dayEndId);

			results = (List<DayEndSummary>) query.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (results == null || results.isEmpty()) {
			results = null;
		}
		return results;
	}

	@Override
	public List<DayEnd> getDayEndByDayEndStatusAndBranch(DayEndStatus dayEndStatus, String branchId) {
		List<DayEnd> results = null;
		try {
			Query query = em.createNamedQuery("getDayEndByDayEndStatusAndBranch");
			query.setParameter("status", dayEndStatus);
			query.setParameter("branchId", branchId);
			LOG.debug("Branch id " + branchId);
			LOG.debug("status " + dayEndStatus);
			LOG.debug("Query  " + query.toString());
			results = (List<DayEnd>) query.getResultList();
			if (results != null) {
				LOG.debug("Results " + results.size());
				for (DayEnd dayEnd : results) {
					// dayEnd.getDayendsummaryList();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (results == null || results.isEmpty()) {
			results = null;
		}
		LOG.debug("results      " + results);
		return results;
	}

	@Override
	public DayEnd disapproveDayEnd(DayEnd dayEnd, String userName) throws Exception {

		dayEnd.setStatus(DayEndStatus.DISAPPROVED);
		updateDayEnd(dayEnd);
		return dayEnd;
	}

	public DayEnd deleteDayEnd(DayEnd dayEnd, String userName) throws Exception {
		if (dayEnd != null) {
			em.remove(dayEnd);
		}
		return dayEnd;
	}

	@Override
	public List<DayEnd> getDayEndsByDayEndStatusAndDateRangeAndBranch(DayEndStatus dayEndStatus, Date fromDate, Date toDate, String branch) {
		List<DayEnd> results = null;
		try {
			Query query = em.createNamedQuery("getDayEndsByDayEndStatusAndDateRangeAndBranch");
			query.setParameter("status", dayEndStatus);
			query.setParameter("branchId", branch);
			query.setParameter("fromDate", DateUtil.getBeginningOfDay(fromDate));
			query.setParameter("toDate", DateUtil.getEndOfDay(toDate));
			LOG.debug("Branch id " + branch);
			LOG.debug("status " + dayEndStatus);
			LOG.debug("Query  " + query.toString());
			results = (List<DayEnd>) query.getResultList();
			if (results != null) {
				LOG.debug("Results " + results.size());
				for (DayEnd dayEnd : results) {
					// dayEnd.getDayendsummaryList();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (results == null || results.isEmpty()) {
			results = null;
		}
		LOG.debug("results      " + results);
		return results;
	}

	@Override
	public List<DayEnd> getDayEndsByDayEndStatusAndDateRangeAndTeller(DayEndStatus dayEndStatus, Date fromDate, Date toDate, String teller) {
		List<DayEnd> results = null;
		try {
			Query query = em.createNamedQuery("getDayEndsByDayEndStatusAndDateRangeAndTeller");
			query.setParameter("status", dayEndStatus);
			query.setParameter("tellerId", teller);
			query.setParameter("fromDate", DateUtil.getBeginningOfDay(fromDate));
			query.setParameter("toDate", DateUtil.getEndOfDay(toDate));
			LOG.debug("status " + dayEndStatus);
			LOG.debug("Query  " + query.toString());
			results = (List<DayEnd>) query.getResultList();
			if (results != null) {
				LOG.debug("Results " + results.size());
				for (DayEnd dayEnd : results) {
					// dayEnd.getDayendsummaryList();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (results == null || results.isEmpty()) {
			results = null;
		}
		LOG.debug("results      " + results);
		return results;
	}

	@Override
	public DayEnd updateDayEnd(DayEnd dayEnd, String userName) throws Exception{
		LOG.debug(" in update method of day end    ");
		try {
			dayEnd = em.merge(dayEnd);
		} catch (Exception e) {
			LOG.debug("Exception in update method");
			throw new Exception(e);
		}
		LOG.debug("In update method done");
		return dayEnd;
		
	}
}
