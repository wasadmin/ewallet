package zw.co.esolutions.ewallet.limitservices.service;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import zw.co.esolutions.ewallet.audit.AuditEvents;
import zw.co.esolutions.ewallet.audittrailservices.service.AuditTrailServiceSOAPProxy;
import zw.co.esolutions.ewallet.enums.BankAccountClass;
import zw.co.esolutions.ewallet.enums.LimitPeriodType;
import zw.co.esolutions.ewallet.enums.LimitStatus;
import zw.co.esolutions.ewallet.enums.LimitValueType;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.limitservices.model.Limit;
import zw.co.esolutions.ewallet.serviceexception.EWalletException;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.GenerateKey;

/**
 * Session Bean implementation class LimitServiceImpl
 */
@WebService(serviceName = "LimitService", endpointInterface = "zw.co.esolutions.ewallet.limitservices.service.LimitService", 
		portName = "LimitServiceSOAP")
@Stateless
public class LimitServiceImpl implements LimitService {

	@PersistenceContext
	private EntityManager em;
	
	/**
     * Default constructor. 
     */
    public LimitServiceImpl() {
       
    }
    
    

	
    public Limit createLimit(Limit limit, String userName) throws EWalletException {
		System.out.println(">>>>>>>>>>>>>>>>>>>> In Create Limit = "+limit);
		try {
			if(limit == null) {
				throw new EWalletException("Null Limit");
			}
			//Populating Limit
			if(limit.getStatus() == null) {
				limit.setStatus(LimitStatus.AWAITING_APPROVAL);
			}
			limit.setEffectiveDate(DateUtil.getBeginningOfDay(limit.getEffectiveDate()));
			
			if(limit.getEndDate() == null) {
				limit.setEndDate(null);
			}
			
			//Checking for already active existing system
			Limit existingLimit = null;
			existingLimit = this.getActiveLimitByAccountClassTypeValueTypeEffectiveDateEndDateStatusPeriodTypeAndBankId(limit.getAccountClass(), limit.getType(),
					limit.getValueType(), limit.getEffectiveDate(), limit.getEndDate(), limit.getPeriodType(), limit.getBankId());
			
			System.out.println(">>>>>>>>>>>>>>>>>>>> In Create Existing Limit = "+existingLimit);
			
			
			if(existingLimit != null) {
				return null;
			}
			System.out.println(">>>>>>>>>>>>>>>>>>>> In Create But Limit Not existing"+limit);
			
			//Check for limits awaiting approval its status
			List<Limit> awaitingApprovalLimit = null;
			awaitingApprovalLimit = this.getLimitByAccountClassTypeValueTypeStatusPeriodTypeAndBankId(limit.getAccountClass(), limit.getType(), limit.getValueType(), LimitStatus.AWAITING_APPROVAL,
					limit.getPeriodType(), limit.getBankId());
			if(awaitingApprovalLimit != null) {
				if(!awaitingApprovalLimit.isEmpty()) {
					throw new EWalletException(LimitStatus.AWAITING_APPROVAL.toString());
				}
			}
			
			//Check for previous limit and update its status
			List<Limit> previousLimit = null;
			previousLimit = this.getLimitByAccountClassTypeValueTypeStatusPeriodTypeAndBankId(limit.getAccountClass(), limit.getType(), limit.getValueType(), LimitStatus.ACTIVE,
					limit.getPeriodType(), limit.getBankId());
			if(previousLimit != null && limit.getStatus().equals(LimitStatus.AWAITING_APPROVAL)) {
				
				System.out.println(">>>>>>>>>>>>>>>>>>>> In Create But Limit Previous Limit"+limit);
				for(Limit l : previousLimit) {
					l.setStatus(LimitStatus.INACTIVE);
					//l.setEndDate(DateUtil.addHours(DateUtil.getEndOfDay(limit.getEffectiveDate()), -24));
					this.em.merge(l);
				}
			}
			System.out.println(">>>>>>>>>>>>>>>>>>>> In Create Previous Limit = "+previousLimit);
			
			Limit activeLimit = this.getExactEffectiveLimit(limit);
			System.out.println(">>>>>>>>>>>>>>>>>>>> Already Active Limit = "+activeLimit);
			if(!(activeLimit == null || activeLimit.getId() == null)) {
				limit.setOldLimitId(activeLimit.getId());
			}
			limit = this.createLimit(limit);
			
			System.out.println(">>>>>>>>>>>>>>>>>>>> In Create Limit Finish "+limit);
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.CREATE_LIMIT, limit.getId(), limit.getEntityName(), null, limit.getAuditableAttributesString(), limit.getInstanceName());
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new EWalletException(e.getMessage());
		}
		
		return limit;
	}
	
	
    public Limit editLimit(Limit limit, String userName) throws EWalletException{
		try {
			System.out.println(">>>>>>> In edit Limit>>>>>>>>>>>> Effective Date = "+DateUtil.convertToShortUploadDateFormatNumbersOnly(limit.getEffectiveDate())+", Bank Id = "+limit.getBankId());
			System.out.println(">>>>>>>>>>>>>>> Lower Min value = "+limit.getMinValue()+", MAX value = "+limit.getMaxValue());
			Date effectiveDate = DateUtil.getBeginningOfDay(limit.getEffectiveDate());
			Limit actualLimit = this.findLimitById(limit.getOldLimitId());
			Limit effectiveLimit = null;
			/*if(!actualLimit.getType().equals(limit.getType())) {
				 effectiveLimit = limit;
				 effectiveLimit = this.getExactEffectiveLimit(effectiveLimit);
					
					System.out.println(">>>>>>>>>>>>>>>>>>>> Already Active Limit = "+effectiveLimit);
					
					if((effectiveLimit == null || effectiveLimit.getId() == null)) {
						effectiveLimit = null;
					} else {
						limit.setOldLimitId(effectiveLimit.getId());
						System.out.println(">>>>>>>>>>>>>>>>>>>> Already Active Limit ID = "+effectiveLimit.getId());
					}
				}*/
			effectiveLimit = limit;
			effectiveLimit = this.getExactEffectiveLimit(effectiveLimit);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>. Before checking existence");
			//Checking for already active existing system
			Limit existingLimit = null;
			existingLimit = this.getActiveLimitByAccountClassTypeValueTypeEffectiveDateEndDateStatusPeriodTypeAndBankId(limit.getAccountClass(), limit.getType(), limit.getValueType(), limit.getEffectiveDate(), limit.getEndDate(), limit.getPeriodType(), limit.getBankId());
			if (existingLimit != null) {
				if (existingLimit.getMaxValue() == limit.getMaxValue() && existingLimit.getMinValue() == limit.getMinValue()) {
					return null;
				}
			}
			System.out.println(">>>>>>>>>>>>>>>>>>>>> No existing limit of type = "+limit.getType());
			//Check for limits awaiting approval its status
			List<Limit> awaitingApprovalLimit = null;
			awaitingApprovalLimit = this.getLimitByAccountClassTypeValueTypeStatusPeriodTypeAndBankId(limit.getAccountClass(), limit.getType(), limit.getValueType(), LimitStatus.AWAITING_APPROVAL, limit.getPeriodType(), limit.getBankId());
			if (awaitingApprovalLimit != null) {
				if (!awaitingApprovalLimit.isEmpty()) {
					if (((DateUtil.daysBetween(awaitingApprovalLimit.get(0).getEffectiveDate(), effectiveDate)== 0l)) && awaitingApprovalLimit.get(0).getMaxValue() == limit.getMaxValue() && awaitingApprovalLimit.get(0).getMinValue() == limit.getMinValue()) {
						throw new EWalletException(LimitStatus.AWAITING_APPROVAL.toString());
					}

				}
			}
			
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>> No Awaiting approval limit");
			
			//Old Limit
			String oldLimitString;
			oldLimitString = actualLimit.getAuditableAttributesString(); //For audit trail
			
			if(LimitStatus.ACTIVE.equals(actualLimit.getStatus()) && (effectiveLimit == null || effectiveLimit.getId() == null) ) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> In create For no effective limit = "+effectiveLimit);
			//oldLimitString = actualLimit.getAuditableAttributesString(); //For audit trail
			limit.setVersion(0);
			
			if(limit.getBankId().equalsIgnoreCase(actualLimit.getBankId())) {
				limit.setOldLimitId(actualLimit.getId());
				
			} else {
				limit.setOldLimitId(null);
			}
			limit = this.createLimit(limit);
			//Audit Trail
			} else if(LimitStatus.ACTIVE.equals(actualLimit.getStatus()) && effectiveLimit != null && effectiveLimit.getId() != null ) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> In create = "+effectiveDate);
				
				if(actualLimit.getId().equalsIgnoreCase(effectiveLimit.getId())) {
					System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> In create For the same Active Limit Selected by user = "+effectiveLimit.getId());
					//oldLimitString = actualLimit.getAuditableAttributesString(); //For audit trail
					limit.setVersion(0);
					limit.setId(null);
					if(limit.getBankId().equalsIgnoreCase(actualLimit.getBankId())) {
						limit.setOldLimitId(actualLimit.getId());
						
					} else {
						limit.setOldLimitId(effectiveLimit.getId());
					}
					limit = this.createLimit(limit);
				} else {
					System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> In create For different Active Limit Selected by user = "+effectiveLimit.getId());
					//oldLimitString = effectiveLimit.getAuditableAttributesString(); //For audit trail
					limit.setVersion(0);
					limit.setId(null);
					limit.setOldLimitId(effectiveLimit.getId());
					limit = this.createLimit(limit);
				}
				
				//Audit Trail
			} else {
				//oldLimitString = actualLimit.getAuditableAttributesString(); //For audit trail
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> In merge");
				if(effectiveLimit == null || effectiveLimit.getId() == null) {
	    			System.out.println(">>>>>>>>>>>>>>>> Effective Limit is NULL ");
	    			if(actualLimit.getBankId().equalsIgnoreCase(limit.getBankId())) {
	    				System.out.println(">>>>>>>>>>>>>>>> Same bank ID ");
	    				limit.setOldLimitId(null);
	    				
	    			} else {
	    				System.out.println(">>>>>>>>>>>>>>>> Diff bank ID");
	    				System.out.println(">>>>>>>>>>>>>>>> Effective Limit is NULL ");
	    				limit.setOldLimitId(null);
	    				
	    			}
	    			System.out.println(">>>>>>>>>>>>>>>> Tab Old Limit Id= "+limit.getOldLimitId());
	    		} else {
	    			System.out.println(">>>>>>>>>>>>>>>> Effective Limit NOT NULL");
	    			if(actualLimit.getBankId().equalsIgnoreCase(limit.getBankId())) {
	    				System.out.println(">>>>>>>>>>>>>>>> Same bank ID ");
	    				limit.setOldLimitId(effectiveLimit.getId());
	    			}  else {
	    				System.out.println(">>>>>>>>>>>>>>>> Diff bank ID");
	    				limit.setOldLimitId(effectiveLimit.getId());
	    				
	    			}
	    		}
				limit.setId(actualLimit.getId());
				limit.setStatus(LimitStatus.AWAITING_APPROVAL);
				limit.setVersion(actualLimit.getVersion());
				limit.setEffectiveDate(effectiveDate);
				System.out.println(">>>>>>>>>>>>>>> Before merge Lower Min value = "+limit.getMinValue()+", MAX value = "+limit.getMaxValue());
				//limit = em.merge(limit);
				limit = this.forceLimitUpdate(limit);
				System.out.println(">>>>>>>>>>>>>>> After merge Lower Min value = "+limit.getMinValue()+", MAX value = "+limit.getMaxValue());
			}
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.EDIT_LIMIT, limit.getId(), limit.getEntityName(), oldLimitString, limit.getAuditableAttributesString(), limit.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw new EWalletException(e.getMessage());
		}
		return limit;
	}
	
	
	public Limit approveLimit(Limit limit, String userName) throws Exception {
		try {
			Limit l = this.findLimitById(limit.getId());
			if(limit.getOldLimitId() != null) {
				Limit oldLimit  = this.findLimitById(limit.getOldLimitId());
				if(LimitStatus.AWAITING_APPROVAL.equals(l.getStatus())) {
					oldLimit.setStatus(LimitStatus.INACTIVE);
					//Do nothing on old limit
					if(oldLimit.getEndDate() != null) {
						limit.setDateCreated(oldLimit.getEndDate());
					}
					oldLimit.setEndDate(DateUtil.addHours(DateUtil.getEndOfDay(limit.getEffectiveDate()), -24));
				} else if(LimitStatus.DISAPPROVED.equals(l.getStatus())) {
					
						oldLimit.setStatus(LimitStatus.INACTIVE);
						oldLimit.setEndDate(DateUtil.addHours(DateUtil.getEndOfDay(limit.getEffectiveDate()), -24));
					
				}
				em.merge(oldLimit);
			}
			String oldLimitString = limit.getAuditableAttributesString(); //For Audit trail
			limit.setStatus(LimitStatus.ACTIVE);
			limit = em.merge(limit);
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.APPROVE_LIMIT, limit.getId(), limit.getEntityName(), oldLimitString, limit.getAuditableAttributesString(), limit.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return  limit;
	}
	
	
	public Limit disapproveLimit(Limit limit, String userName) throws Exception {
		Limit oldLimit = null;
		Limit l = this.findLimitById(limit.getId());
		if(limit.getOldLimitId() != null) {
			oldLimit = this.findLimitById(limit.getOldLimitId());
			if(LimitStatus.AWAITING_APPROVAL.equals(l.getStatus())) {
				//Do nothing on old limit
				if(oldLimit.getEndDate() != null) {
					limit.setDateCreated(oldLimit.getEndDate());
				}
			} else if(LimitStatus.ACTIVE.equals(l.getStatus())){
				
				if (DateUtil.isEndDate(limit.getDateCreated())) {
					oldLimit.setStatus(LimitStatus.INACTIVE);
					oldLimit.setEndDate(limit.getDateCreated());
				} else {
					oldLimit.setStatus(LimitStatus.ACTIVE);
					oldLimit.setEndDate(null);
				}
				
			}
			oldLimit = em.merge(oldLimit);
		}
		String oldLimitString = limit.getAuditableAttributesString(); //For Audit trail
		limit.setStatus(LimitStatus.DISAPPROVED);
		limit = em.merge(limit);
		
		//Audit Trail
		AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
		auditService.logActivity(userName, AuditEvents.REJECT_LIMIT, limit.getId(), limit.getEntityName(), oldLimitString, limit.getAuditableAttributesString(), limit.getInstanceName());
		
		return  limit;
	}
	
	
	public Limit activateLimit(Limit limit, String userName) throws Exception {
		String oldLimitString = limit.getAuditableAttributesString(); //For Audit trail
		limit.setStatus(LimitStatus.ACTIVE);
		limit = em.merge(limit);
		
		//Audit Trail
		AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
		auditService.logActivity(userName, AuditEvents.ACTIVATE_LIMIT, limit.getId(), limit.getEntityName(), oldLimitString, limit.getAuditableAttributesString(), limit.getInstanceName());
		
		return  limit;
	}
	
	private Limit createLimit(Limit limit) throws EWalletException {
		System.out.println(">>>>>>>>>>>>>>>>>>>> In Create Limit = "+limit);
		try {
			if(limit == null) {
				throw new EWalletException("Null Limit");
			}
			if(limit.getId() == null) {
				limit.setId(GenerateKey.generateEntityId());
			}
			if(limit.getDateCreated() == null) {
				limit.setDateCreated(new Date());
			} if(limit.getEffectiveDate() == null) {
				limit.setEffectiveDate(DateUtil.getBeginningOfDay(limit.getEffectiveDate()));
			}
			if(limit.getEndDate() == null) {
				limit.setEndDate(null);
			}
			em.persist(limit);
			
			System.out.println(">>>>>>>>>>>>>>>>>>>> In Create Limit Finish "+limit);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new EWalletException(e.getMessage());
		}
		
		return limit;
	}


	
	public String deleteLimit(String limitId, String userName) throws EWalletException {
		try {
			Limit limit = em.find(Limit.class, limitId);
			String oldLimitString = limit.getAuditableAttributesString(); //For Audit trail
			limit.setStatus(LimitStatus.DELETED);
			limit = em.merge(limit);
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.DELETE_LIMIT, limit.getId(), limit.getEntityName(), oldLimitString, limit.getAuditableAttributesString(), limit.getInstanceName());
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return "success";
	}

	public Limit findLimitById(String id) {
		Limit limit = null;
		try {
			limit = (Limit) em.find(Limit.class, id);
		} finally {
			
		}
		return limit;
	}
	
	@SuppressWarnings("unchecked")
	public List<Limit> getAllLimits() {
		List<Limit> results = null;
		try {
			Query query = em.createNamedQuery("getLimit");
			results = (List<Limit>)query.getResultList();
		} catch (Exception e) {
			return null;
		}
		if(results == null || results.isEmpty()) {
			return null;
		}
		return results;
	}
	
	@SuppressWarnings("unchecked")
	public List<Limit> getAllLimitsByBankId(String bankId) {
		List<Limit> results = null;
		try {
			Query query = em.createNamedQuery("getLimitByBankId");
			query.setParameter("bankId", "%"+bankId+"%");
			query.setParameter("inactive", LimitStatus.INACTIVE);
			results = (List<Limit>)query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if(results == null || results.isEmpty()) {
			return null;
		}
		return results;
	}



	@SuppressWarnings("unchecked")
	@Override
	public List<Limit> getLimitByTypeAndStatusAndBankId(TransactionType type, LimitStatus status, String bankId) {
		List<Limit> results = null;
		try {
			Query query = em.createNamedQuery("getLimitByTypeAndStatusAndBankId");
			query.setParameter("type", type);
			query.setParameter("status", status);
			query.setParameter("bankId", bankId);
			results = (List<Limit>)query.getResultList();
		} catch (Exception e) {
			return null;
		}
		if(results == null || results.isEmpty()) {
			return null;
		}
		return results;
	}



	@SuppressWarnings("unchecked")
	@Override
	public List<Limit> getLimitByValueTypeAndStatusAndBankId(LimitValueType valueType, LimitStatus status, String bankId) {
		List<Limit> results = null;
		try {
			Query query = em.createNamedQuery("getLimitByValueTypeAndStatusAndBankId");
			query.setParameter("valueType", valueType);
			query.setParameter("status", status);
			query.setParameter("bankId", bankId);
			results = (List<Limit>)query.getResultList();
		} catch (Exception e) {
			return null;
		}
		if(results == null || results.isEmpty()) {
			return null;
		}
		return results;
	}



	@SuppressWarnings("unchecked")
	
	public List<Limit> getLimitByAccountClassTypeValueTypeStatusPeriodTypeAndBankId(BankAccountClass custcClass, TransactionType type,
			LimitValueType valueType, LimitStatus status, LimitPeriodType periodType, String bankId) {
		List<Limit> results = null;
		try {
			Query query = em.createNamedQuery("getLimitByAccountClassTypeValueTypeStatusPeriodTypeAndBankId");
			query.setParameter("type", type);
			query.setParameter("valueType", valueType);
			query.setParameter("status", status);
			query.setParameter("accountClass", custcClass);
			query.setParameter("periodType", periodType);
			query.setParameter("bankId", bankId);
			results = (List<Limit>)query.getResultList();
		} catch (Exception e) {
			return null;
		}
		if(results == null || results.isEmpty()) {
			return null;
		}
		return results;
	}



	@SuppressWarnings("unchecked")
	@Override
	public List<Limit> getLimitByValueTypeEffectiveDateStatusAndBankId(
			LimitValueType valueType, Date effectiveDate, LimitStatus status, String bankId) {
		List<Limit> results = null;
		try {
			Query query = em.createNamedQuery("getLimitByValueTypeEffectiveDateStatusAndBankId");
			query.setParameter("valueType", valueType);
			query.setParameter("effectiveDate", DateUtil.getBeginningOfDay(effectiveDate));
			query.setParameter("status", status);
			query.setParameter("bankId", bankId);
			results = (List<Limit>)query.getResultList();
		} catch (Exception e) {
			return null;
		}
		if(results == null || results.isEmpty()) {
			return null;
		}
		return results;
	}



	@SuppressWarnings("unchecked")
	@Override
	public List<Limit> getLimitByTypeEffectiveDateStatusAndBankId(
			TransactionType type, Date effectiveDate, LimitStatus status, String bankId) {
		List<Limit> results = null;
		try {
			Query query = em.createNamedQuery("getLimitByTypeEffectiveDateStatusAndBankId");
			query.setParameter("type", type);
			query.setParameter("effectiveDate", DateUtil.getBeginningOfDay(effectiveDate));
			query.setParameter("status", status);
			query.setParameter("bankId", bankId);
			results = (List<Limit>)query.getResultList();
		} catch (Exception e) {
			return null;
		}
		if(results == null || results.isEmpty()) {
			return null;
		}
		return results;
	}



	@SuppressWarnings("unchecked")
	@Override
	public List<Limit> getLimitByTypeValueTypeEffectiveDateStatusAndBankId(TransactionType type, LimitValueType valueType, Date effectiveDate, LimitStatus status, String bankId) {
		List<Limit> results = null;
		try {
			Query query = em.createNamedQuery("getLimitByTypeValueTypeEffectiveDateStatusAndBankId");
			query.setParameter("type", type);
			query.setParameter("valueType", valueType);
			query.setParameter("effectiveDate", DateUtil.getBeginningOfDay(effectiveDate));
			query.setParameter("status", status);
			query.setParameter("bankId", bankId);
			results = (List<Limit>)query.getResultList();
		}   catch (Exception e) {
			
		}
		return results;
	}



	

@Override
	public Limit getActiveLimitByAccountClassTypeValueTypeEffectiveDateEndDateStatusPeriodTypeAndBankId(BankAccountClass accountClass,
			TransactionType type, LimitValueType valueType, Date effectiveDate,
			Date endDate, LimitPeriodType periodType, String bankId) {
		Limit result = null;
		try {
			Query query = em.createNamedQuery("getActiveLimitByAccountClassTypeValueTypeEffectiveDateEndDateStatusPeriodTypeAndBankId");
			query.setParameter("type", type);
			query.setParameter("valueType", valueType);
			query.setParameter("effectiveDate", DateUtil.getBeginningOfDay(effectiveDate));
			query.setParameter("endDate", DateUtil.getEndOfDay(endDate));
			query.setParameter("status", LimitStatus.ACTIVE);
			query.setParameter("accountClass", accountClass);
			query.setParameter("periodType", periodType);
			query.setParameter("bankId", bankId);
			result = (Limit)query.getSingleResult();
		}  catch (NoResultException nre) {
			return null;
		} catch (Exception e) {
			
		}
		return result;
	}
	
	public Limit getValidLimitOnDateByBankId(TransactionType type, BankAccountClass accountClass, Date onDate, LimitPeriodType periodType, 
			String bankId) {
		Limit limit = null;
		try {
			Query query = em.createNamedQuery("getLimitByValidityMinAttributes");
			query.setParameter("type", type);
			query.setParameter("accountClass", accountClass);
			query.setParameter("onDateStart", DateUtil.getBeginningOfDay(onDate));
			query.setParameter("onDateEnd", DateUtil.getEndOfDay(onDate));
			query.setParameter("periodType", periodType);
			query.setParameter("bankId", bankId);
			query.setParameter("inactive", LimitStatus.INACTIVE);
			query.setParameter("awaitingApproval", LimitStatus.AWAITING_APPROVAL);
			query.setParameter("disapproved", LimitStatus.DISAPPROVED);
			query.setParameter("deleted", LimitStatus.DELETED);
			limit = (Limit)query.getSingleResult();
		} catch (NoResultException nre) {
		   return null;
		}  catch (Exception e) {
			e.printStackTrace();
		}
		 
		return limit;
	}



	@SuppressWarnings("unchecked")
	@Override
	public List<Limit> getLimitByAccountClassTypeValueTypeEffectiveDateEndDateStatusAndBankId(
			BankAccountClass accountClass, TransactionType type, LimitValueType valueType,
			Date effectiveDate, Date endDate, LimitStatus status, String bankId) {
		List<Limit> limits = null;
		try {
			Query query = em.createNamedQuery("getLimitByAccountClassTypeValueTypeEffectiveDateEndDateStatusAndBankId");
			query.setParameter("type", type);
			query.setParameter("valueType", valueType);
			query.setParameter("effectiveDate", DateUtil.getBeginningOfDay(effectiveDate));
			query.setParameter("endDate", DateUtil.getEndOfDay(endDate));
			query.setParameter("status", status);
			query.setParameter("accountClass", accountClass);
			query.setParameter("bankId", bankId);
			limits = (List<Limit>)query.getResultList();
		} catch (Exception e) {
			return null;
		}
		if(limits == null || limits.isEmpty()) {
		    limits = null;
		} 
		return limits;
	}


	@SuppressWarnings("unchecked")
	public List<Limit> getLimitByTypeAndBankId(TransactionType type, String bankId) {
		List<Limit> results = null;
		try {
			Query query = em.createNamedQuery("getLimitByTypeAndBankId");
			query.setParameter("type", type);
			query.setParameter("bankId", "%"+bankId+"%");
			query.setParameter("inactive", LimitStatus.INACTIVE);
			query.setParameter("accountClass", BankAccountClass.NONE);
			results = (List<Limit>)query.getResultList();
		} catch (Exception e) {
			return null;
		}
		if(results == null || results.isEmpty()) {
			return null;
		}
		return results;
	}
	
	@SuppressWarnings("unchecked")
	public List<Limit> getLimitByAccountClassAndBankId( BankAccountClass accountClass, String bankId) {
		List<Limit> results = null;
		try {
			Query query = em.createNamedQuery("getLimitByAccountClassAndBankId");
			query.setParameter("accountClass", accountClass);
			query.setParameter("bankId", "%"+bankId+"%");
			query.setParameter("inactive", LimitStatus.INACTIVE);
			results = (List<Limit>)query.getResultList();
		} catch (Exception e) {
			return null;
		}
		if(results == null || results.isEmpty()) {
			return null;
		}
		return results;
	}
	
	@SuppressWarnings("unchecked")
	public List<Limit> getEffectiveLimitsByBankId(Date limitDate, String bankId) {
		List<Limit> results = null;
		try {
			Query query = em.createNamedQuery("getEffectiveLimitsByBankId");
			query.setParameter("fromDate", DateUtil.getBeginningOfDay(limitDate));
			query.setParameter("toDate", DateUtil.getEndOfDay(limitDate));
			query.setParameter("bankId", "%"+bankId+"%");
			query.setParameter("inactive", LimitStatus.INACTIVE);
			query.setParameter("awaitingApproval", LimitStatus.AWAITING_APPROVAL);
			query.setParameter("disapproved", LimitStatus.DISAPPROVED);
			query.setParameter("deleted", LimitStatus.DELETED);
			query.setParameter("accountClass", BankAccountClass.NONE);
			results = (List<Limit>)query.getResultList();
		} catch (Exception e) {
			return null;
		}
		if(results == null || results.isEmpty()) {
			return null;
		}
		return results;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Limit> getLimitByValueTypeAndBankId(LimitValueType valueType, String bankId) {
		List<Limit> results = null;
		try {
			Query query = em.createNamedQuery("getLimitByValueTypeAndBankId");
			query.setParameter("valueType", valueType);
			query.setParameter("bankId", bankId);
			results = (List<Limit>)query.getResultList();
		} catch (Exception e) {
			return null;
		}
		if(results == null || results.isEmpty()) {
			return null;
		}
		return results;
	}

@SuppressWarnings("unchecked")
	public List<Limit> getLimitByStatusAndBankId(LimitStatus status,
			String bankId) {
		List<Limit> results = null;
		try {
			Query query = em.createNamedQuery("getLimitByStatusAndBankId");
			query.setParameter("status", status);
			query.setParameter("bankId", "%"+bankId+"%");
			results = (List<Limit>)query.getResultList();
		} catch (Exception e) {
			return null;
		}
		if(results == null || results.isEmpty()) {
			return null;
		}
		return results;
		
	}

private Limit getExactEffectiveLimit(Limit limit) {
	Limit results = null;
	String sql = "SELECT l FROM Limit l WHERE l.effectiveDate <= :fromDate AND (l.endDate IS NULL OR l.endDate >= :toDate) AND l.bankId LIKE :bankId " +
	"AND ((l.endDate IS NULL AND (l.status NOT IN (:inactive))) OR (l.endDate IS NOT NULL AND l.status = :inactive)) " +
	"AND l.status NOT IN (:awaitingApproval) AND l.status NOT IN (:disapproved) AND l.status NOT IN (:deleted) AND l.type = :type AND l.accountClass = :accountClass AND l.periodType = :periodType";  
	Date limitDate = limit.getEffectiveDate();
	try {
		Query query = em.createQuery(sql);
		query.setParameter("fromDate", DateUtil.getBeginningOfDay(limitDate));
		query.setParameter("toDate", DateUtil.getEndOfDay(limitDate));
		query.setParameter("bankId", "%"+limit.getBankId()+"%");
		query.setParameter("inactive", LimitStatus.INACTIVE);
		query.setParameter("awaitingApproval", LimitStatus.AWAITING_APPROVAL);
		query.setParameter("disapproved", LimitStatus.DISAPPROVED);
		query.setParameter("deleted", LimitStatus.DELETED);
		query.setParameter("type", limit.getType());
		query.setParameter("accountClass", limit.getAccountClass());
		query.setParameter("periodType", limit.getPeriodType());
		
		results = (Limit)query.getSingleResult();
	} catch (Exception e) {
		e.printStackTrace();
		return null;
	}
	if(results == null || results.getId() == null) {
		return null;
	}
	return results;
}

private Limit forceLimitUpdate(Limit limit) throws Exception{
	this.removeLimit(limit);
	limit.setVersion(0);
	return this.createLimit(limit);
	
}

private void removeLimit(Limit limit) throws Exception {
	limit = this.findLimitById(limit.getId());
	limit = em.merge(limit);
	em.remove(limit);
}

	
}
