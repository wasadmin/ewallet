package zw.co.esolutions.ewallet.referralservices.service;

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
import zw.co.esolutions.ewallet.enums.ReferralStatus;
import zw.co.esolutions.ewallet.referralservices.model.Referral;
import zw.co.esolutions.ewallet.referralservices.model.ReferralConfig;
import zw.co.esolutions.ewallet.referralservices.model.ReferralState;
import zw.co.esolutions.ewallet.sms.ResponseCode;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.GenerateKey;


/**
 * Session Bean implementation class ReferralServiceImpl
 */
@Stateless
@WebService(endpointInterface="zw.co.esolutions.ewallet.referralservices.service.ReferralService", serviceName="ReferralService", portName="ReferralServiceSOAP")
public class ReferralServiceImpl implements ReferralService {

	@PersistenceContext private EntityManager em;
    /**
     * Default constructor. 
     */
    public ReferralServiceImpl() {
 
    }
    
    public Referral createReferral(Referral referral) throws Exception {
    	
    	if(referral.getId() == null) {
    		referral.setId(GenerateKey.generateEntityId());
    	}
    	if(referral.getCode() == 0) {
    		referral.setCode(Integer.parseInt(GenerateKey.generateSecurityCode()));
    	}
    	if(referral.getDateCreated() == null) {
    		referral.setDateCreated(new Date());
    	}
		try {
			em.persist(referral);
			this.promoteReferralState(referral, ReferralStatus.NEW);
		} finally {
	
		}
		return referral;
	}

    public String promoteReferralState(Referral referral, ReferralStatus status) throws Exception {
    	
	   	referral.setStatus(status);
	   	ReferralState state = new ReferralState();
    	state.setStatus(status);
    	state.setReferral(referral);
    	try {
    		this.updateReferral(referral, null);
    		this.createReferralState(state);
    	} finally {
    		
    	}
    	return ResponseCode.E000.name();
    }
    
    
	public String deleteReferral(Referral referral, String userName) throws Exception {
	
		try {
			String oldReferral = this.findReferralById(referral.getId()).getAuditableAttributesString();
			referral.setStatus(ReferralStatus.DELETED);
			referral = em.merge(referral);
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.DELETE_REFERRAL, referral.getId(), referral.getEntityName(), oldReferral, referral.getAuditableAttributesString(), referral.getInstanceName());
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return "";
	}

    
	public Referral updateReferral(Referral referral, String userName) throws Exception {
	
		try {
			String oldReferral = this.findReferralById(referral.getId()).getAuditableAttributesString();
			referral = em.merge(referral);
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.UPDATE_REFERRAL, referral.getId(), referral.getEntityName(), oldReferral, referral.getAuditableAttributesString(), referral.getInstanceName());
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return referral;
	}

	public Referral findReferralById(String id) {
		Referral referral = null;
	
		try {
			referral = (Referral) em.find(Referral.class, id);
		} finally {
	
		}
		return referral;
	}

	@SuppressWarnings("unchecked")
	public List<Referral> getReferral() {
		List<Referral> results = null;
		try {
			Query query = em.createNamedQuery("getReferral");
			results = (List<Referral>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		} finally {

		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<Referral> getReferralByReferrerMobileId(String referrerMobileId) {
		List<Referral> results = null;
		try {
			Query query = em.createNamedQuery("getReferralByReferrerMobileId");
			query.setParameter("referrerMobileId", referrerMobileId);
			results = (List<Referral>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		} finally {
	
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<Referral> getReferralByReferredMobile(String referredMobile) {
		List<Referral> results = null;
		try {
			Query query = em.createNamedQuery("getReferralByReferredMobile");
			query.setParameter("referredMobile", referredMobile);
			results = (List<Referral>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		} finally {
	
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<Referral> getReferralByCode(int code) {
		List<Referral> results = null;
		try {
			Query query = em.createNamedQuery("getReferralByCode");
			query.setParameter("code", code);
			results = (List<Referral>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		} finally {
	
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<Referral> getReferralByStatus(ReferralStatus status) {
		List<Referral> results = null;
		try {
			Query query = em.createNamedQuery("getReferralByStatus");
			query.setParameter("status", status);
			results = (List<Referral>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		} finally {
	
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<Referral> getReferralByDateCreated(Date dateCreated) {
		List<Referral> results = null;
		try {
			Query query = em.createNamedQuery("getReferralByDateCreated");
			query.setParameter("dateCreated", dateCreated);
			results = (List<Referral>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		} finally {
	
		}
		return results;
	}
	
	public Referral getReferralByReferredMobileAndCode(String referredMobile, int code) throws Exception {
		Referral result = null;
		try {
			Query query = em.createNamedQuery("getReferralByReferredMobileAndCode");
			query.setParameter("referredMobile", referredMobile);
			query.setParameter("code", code);
			result = (Referral) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} finally {
	
		}
		return result;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Referral> getReferralByReferredMobileAndStatus(String referredMobile, ReferralStatus status) throws Exception {
		List<Referral> results = null;
		try {
			Query query = em.createNamedQuery("getReferralByReferredMobileAndStatus");
			query.setParameter("referredMobile", referredMobile);
			query.setParameter("status", status);
			results = (List<Referral>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		} finally {
	
		}
		return results;
		
	}
	
	public ReferralState createReferralState(ReferralState referralState)
			throws Exception {
		if(referralState.getId() == null) {
			referralState.setId(GenerateKey.generateEntityId());
		}
		if(referralState.getDateCreated() == null) {
			referralState.setDateCreated(new Date());
		}
		try {
			em.persist(referralState);
		} finally {
	
		}
		return referralState;
	}

	public String deleteReferralState(ReferralState referralState)
			throws Exception {
		try {
			referralState = em.merge(referralState);
			em.remove(referralState);
		} finally {
		
		}
		return "";
	}

	public ReferralState updateReferralState(ReferralState referralState)
			throws Exception {
		try {
			referralState = em.merge(referralState);
		} finally {
	
		}
		return referralState;
	}

	public ReferralState findReferralStateById(String id) {
		ReferralState referralState = null;
		try {
			referralState = (ReferralState) em.find(ReferralState.class, id);
		} finally {
	
		}
		return referralState;
	}

	@SuppressWarnings("unchecked")
	public List<ReferralState> getReferralState() {
		List<ReferralState> results = null;
		try {
			Query query = em.createNamedQuery("getReferralState");
			results = (List<ReferralState>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		} finally {
	
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<ReferralState> getReferralStateByReferral(String referral_id) {
		List<ReferralState> results = null;
		try {
			Query query = em.createNamedQuery("getReferralStateByReferral");
			query.setParameter("referral_id", referral_id);
			results = (List<ReferralState>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		} finally {
		
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<ReferralState> getReferralStateByStatus(ReferralStatus status) {
		List<ReferralState> results = null;
		try {
			Query query = em.createNamedQuery("getReferralStateByStatus");
			query.setParameter("status", status);
			results = (List<ReferralState>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		} finally {
		
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<ReferralState> getReferralStateByDateCreated(Date dateCreated) {
		List<ReferralState> results = null;
		try {
			Query query = em.createNamedQuery("getReferralStateByDateCreated");
			query.setParameter("dateCreated", dateCreated);
			results = (List<ReferralState>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		} finally {
	
		}
		return results;
	}
	
	
	public ReferralConfig createReferralConfig(ReferralConfig referralConfig, String userName)
			throws Exception {
		if(referralConfig.getId() == null) {
			referralConfig.setId(GenerateKey.generateEntityId());
		}
		if(referralConfig.getDateCreated() == null) {
			referralConfig.setDateCreated(new Date());
		}
		try {
			em.persist(referralConfig);
			
			//Audit trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.CREATE_REFERRAL_CONFIG, referralConfig.getId(), referralConfig.getEntityName(), null, referralConfig.getAuditableAttributesString(), referralConfig.getInstanceName());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return referralConfig;
	}

	
	public String deleteReferralConfig(ReferralConfig referralConfig, String userName)
			throws Exception {
		try {
			String oldReferralConfig = this.findReferralConfigById(referralConfig.getId()).getAuditableAttributesString();
			referralConfig = em.merge(referralConfig);
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.DELETE_REFERRAL_CONFIG, referralConfig.getId(), referralConfig.getEntityName(), oldReferralConfig, referralConfig.getAuditableAttributesString(), referralConfig.getInstanceName());
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return "";
	}

	
	public ReferralConfig updateReferralConfig(ReferralConfig referralConfig, String userName)
			throws Exception {
		try {
			String oldReferralConfig = this.findReferralConfigById(referralConfig.getId()).getAuditableAttributesString();
			referralConfig = em.merge(referralConfig);
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.UPDATE_REFERRAL_CONFIG, referralConfig.getId(), referralConfig.getEntityName(), oldReferralConfig, referralConfig.getAuditableAttributesString(), referralConfig.getInstanceName());
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return referralConfig;
	}

	public ReferralConfig findReferralConfigById(String id) {
		ReferralConfig referralConfig = null;
		try {
			referralConfig = (ReferralConfig) em.find(ReferralConfig.class, id);
		} finally {
	
		}
		return referralConfig;
	}

	@SuppressWarnings("unchecked")
	public List<ReferralConfig> getReferralConfig() {
		List<ReferralConfig> results = null;
		try {
			Query query = em.createNamedQuery("getReferralConfig");
			results = (List<ReferralConfig>) query.getResultList();
		} catch(NoResultException e) {
			return null;
		} finally {
			
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<ReferralConfig> getReferralConfigByDateFrom(Date dateFrom) {
		List<ReferralConfig> results = null;
		try {
			Query query = em.createNamedQuery("getReferralConfigByDateFrom");
			query.setParameter("dateFrom", dateFrom);
			results = (List<ReferralConfig>) query.getResultList();
		} catch(NoResultException e) {
			return null;
		} finally {
		
		}
		return results;
	}
	
	@SuppressWarnings("unchecked")
	public List<ReferralConfig> getReferralConfigByDateTo(Date dateTo) {
		List<ReferralConfig> results = null;
		try {
			Query query = em.createNamedQuery("getReferralConfigByDateTo");
			query.setParameter("dateTo", dateTo);
			results = (List<ReferralConfig>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		} finally {
			
		}
		return results;
	} 
	
	@SuppressWarnings("unchecked")
	public ReferralConfig getActiveReferralConfig() {
		ReferralConfig result = null;
		try {
			Query query = em.createNamedQuery("getActiveReferralConfig");
			query.setParameter("date", DateUtil.getBeginningOfDay(new Date()));
			List<ReferralConfig> results = (List<ReferralConfig>) query.getResultList();
			if (results == null || results.isEmpty()) {
				return null;
			} else { 
				if (results.size() == 1) {
					return results.get(0);
				} else {
					for (ReferralConfig config: results) {
						if (new Date().before(config.getDateTo())) {
							result = config;
						}
					}
				}
			}
		} catch (NoResultException e) {
			return null;
		} finally {
			
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<ReferralConfig> getReferralConfigBetweenDates(Date dateFrom, Date dateTo) {
		List<ReferralConfig> results = null;
		try {
			Query query = em.createNamedQuery("getReferralConfigBetweenDates");
			query.setParameter("dateTo", dateTo);
			query.setParameter("dateFrom", dateFrom);
			results = (List<ReferralConfig>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		} finally {
			
		}
		return results;
	}

}
