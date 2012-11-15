package zw.co.esolutions.ewallet.alertsservices.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import zw.co.esolutions.ewallet.alertsservices.model.AlertOption;
import zw.co.esolutions.ewallet.alertsservices.model.TransactionType;
import zw.co.esolutions.ewallet.audit.AuditEvents;
import zw.co.esolutions.ewallet.audittrailservices.service.AuditTrailServiceSOAPProxy;
import zw.co.esolutions.ewallet.enums.AlertOptionStatus;
import zw.co.esolutions.ewallet.enums.TransactionTypeStatus;
import zw.co.esolutions.ewallet.util.GenerateKey;

/**
 * Session Bean implementation class AlertsServiceImp
 */
@Stateless
@WebService(endpointInterface="zw.co.esolutions.ewallet.alertsservices.service.AlertsService", serviceName="AlertsService",portName="AlertsServiceSOAP")
public class AlertsServiceImp implements AlertsService {

	@PersistenceContext(unitName="AlertsServicesEJB")
	private EntityManager em;
	
    /**
     * Default constructor. 
     */
    public AlertsServiceImp() {
        // TODO Auto-generated constructor stub
    }

	@Override
	public AlertOption approveAlertOption(AlertOption alertOption,
			String username) throws Exception {
		String oldAlertOption = this.findAlertOptionById(alertOption.getId()).getAuditableAttributesString();
		alertOption.setStatus(AlertOptionStatus.ACTIVE);
		alertOption = this.updateAlertOption(alertOption);
		
		//Audit Trail
		AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
		auditService.logActivity(username, AuditEvents.APPROVE_ALERT_OPTION, alertOption.getBankAccountId(), alertOption.getEntityName(), oldAlertOption, alertOption.getAuditableAttributesString(), alertOption.getInstanceName());
		
		return alertOption;
	}
	
	@Override
	public AlertOption rejectAlertOption(AlertOption alertOption,
			String username) throws Exception {
		String oldAlertOption = this.findAlertOptionById(alertOption.getId()).getAuditableAttributesString();
		alertOption.setStatus(AlertOptionStatus.DISAPPROVED);
		alertOption = this.updateAlertOption(alertOption);
		
		//Audit Trail
		AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
		auditService.logActivity(username, AuditEvents.REJECT_ALERT_OPTION, alertOption.getBankAccountId(), alertOption.getEntityName(), oldAlertOption, alertOption.getAuditableAttributesString(), alertOption.getInstanceName());
		
		return alertOption;
	}

	@Override
	public TransactionType approveTransactionType(
			TransactionType transactionType, String username) throws Exception {
		String oldTransactionType = this.findTransactionType(transactionType.getId()).getAuditableAttributesString();
		transactionType.setStatus(TransactionTypeStatus.ENABLED);
		transactionType = this.updateTransactionType(transactionType);
		
		//Audit Trail
		AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
		auditService.logActivity(username, AuditEvents.APPROVE_TRANSACTION_TYPE, transactionType.getId(), transactionType.getEntityName(), oldTransactionType, transactionType.getAuditableAttributesString(), transactionType.getInstanceName());
		
		return transactionType;
	}
	
	@Override
	public TransactionType rejectTransactionType(
			TransactionType transactionType, String username) throws Exception {
		String oldTransactionType = this.findTransactionType(transactionType.getId()).getAuditableAttributesString();
		transactionType.setStatus(TransactionTypeStatus.DISAPPROVED);
		transactionType = this.updateTransactionType(transactionType);
		
		//Audit Trail
		AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
		auditService.logActivity(username, AuditEvents.REJECT_TRANSACTION_TYPE, transactionType.getId(), transactionType.getEntityName(), oldTransactionType, transactionType.getAuditableAttributesString(), transactionType.getInstanceName());
		
		return transactionType;
	}

	@Override
	public AlertOption createAlertOption(AlertOption alertOption,
			String username) throws Exception {
		
		if(alertOption.getId()==null){
			alertOption.setId(GenerateKey.generateEntityId());
		}
		AlertOption a = this.getAlertOptionByBankAccountAndMobileProfileAndTransactionType(alertOption.getBankAccountId(), alertOption.getMobileProfileId(), alertOption.getTransactionType().getId());
		if(a!=null){
			//throw new EntityExistsException();
			return alertOption;
		}
		try {
			em.persist(alertOption);
			
			//Audit Trail
			//AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			//auditService.logActivity(username, AuditEvents.CREATE_ALERT_OPTION, alertOption.getBankAccountId(), alertOption.getEntityName(), null, alertOption.getAuditableAttributesString(), alertOption.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return alertOption;
	}

	@Override
	public TransactionType createTransactionType(
			TransactionType transactionType, String username) throws Exception {
		if(transactionType.getId()==null){
			transactionType.setId(GenerateKey.generateEntityId());
		}
		TransactionType t = this.getTransactionTypeByCode(transactionType.getTransactionCode());
		if(t!=null){
			throw new EntityExistsException();
		}
		transactionType.setFieldsToUpper();
		try{
			em.persist(transactionType);
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(username, AuditEvents.CREATE_TRANSACTION_TYPE, transactionType.getId(), transactionType.getEntityName(), null, transactionType.getAuditableAttributesString(), transactionType.getInstanceName());
			
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return transactionType;
	}

	@Override
	public AlertOption deleteAlertOption(AlertOption alertOption,
			String username) throws Exception {
		String oldAlertOption = this.findAlertOptionById(alertOption.getId()).getAuditableAttributesString();
		alertOption.setStatus(AlertOptionStatus.DELETED);
		alertOption = this.updateAlertOption(alertOption);
		
		//Audit Trail
		AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
		auditService.logActivity(username, AuditEvents.DELETE_ALERT_OPTION, alertOption.getBankAccountId(), alertOption.getEntityName(), oldAlertOption, alertOption.getAuditableAttributesString(), alertOption.getInstanceName());
		
		return alertOption;		
	}

	@Override
	public TransactionType deleteTransactionType(
			TransactionType transactionType, String username) throws Exception {
		String oldTransactionType = this.findTransactionType(transactionType.getId()).getAuditableAttributesString();
		transactionType.setStatus(TransactionTypeStatus.DELETED);
		transactionType = this.updateTransactionType(transactionType);
		
		//Audit Trail
		AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
		auditService.logActivity(username, AuditEvents.DELETE_TRANSACTION_TYPE, transactionType.getId(), transactionType.getEntityName(), oldTransactionType, transactionType.getAuditableAttributesString(), transactionType.getInstanceName());
		
		return transactionType;
	}

	@Override
	public AlertOption editAlertOption(AlertOption alertOption, String username)
			throws Exception {
		String oldAlertOption = this.findAlertOptionById(alertOption.getId()).getAuditableAttributesString();
		alertOption = this.updateAlertOption(alertOption);
		
		//Audit Trail
		//AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
		//auditService.logActivity(username, AuditEvents.EDIT_ALERT_OPTION, alertOption.getBankAccountId(), alertOption.getEntityName(), oldAlertOption, alertOption.getAuditableAttributesString(), alertOption.getInstanceName());
		
		return alertOption;
	}

	@Override
	public TransactionType editTransactionType(TransactionType transactionType,
			String username) throws Exception {
		String oldTransactionType = this.findTransactionType(transactionType.getId()).getAuditableAttributesString();
		transactionType = this.updateTransactionType(transactionType);
		
		//Audit Trail
		AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
		auditService.logActivity(username, AuditEvents.EDIT_TRANSACTION_TYPE, transactionType.getId(), transactionType.getEntityName(), oldTransactionType, transactionType.getAuditableAttributesString(), transactionType.getInstanceName());
		
		return transactionType;
	}

	@Override
	public AlertOption findAlertOptionById(String alertOptionId) {
		AlertOption alertOption= null;
		try {
			alertOption = em.find(AlertOption.class, alertOptionId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return alertOption;
	}

	@Override
	public TransactionType findTransactionType(String transactionTypeId) {
		TransactionType transactionType = null;
		try {
			transactionType = em.find(TransactionType.class, transactionTypeId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return transactionType;
	}

	@Override
	public AlertOption getAlertOptionByBankAccountAndMobileProfileAndTransactionType(
			String bankAccountId, String mobileProfileId,
			String transactionTypeId) {
		
		AlertOption alertOption = null;
		try {
			Query query = em.createNamedQuery("getAlertOptionByBankAccountAndMobileProfileAndTransactionType");
			query.setParameter("bankAccountId", bankAccountId);
			query.setParameter("mobileProfileId", mobileProfileId);
			query.setParameter("transactionTypeId", transactionTypeId);
			alertOption = (AlertOption)query.getSingleResult();
		}catch(NoResultException ne){
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return alertOption;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<AlertOption> getAlertOptionByBankAccountId(String bankAccountId) {
		List<AlertOption> alertOptionList = null;
		try {
			Query query = em.createNamedQuery("getAlertOptionByBankAccountId");
			query.setParameter("bankAccountId", bankAccountId);
			alertOptionList = (List<AlertOption>)query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return alertOptionList;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<AlertOption> getAlertOptionByMobileProfileId(
			String mobileProfileId) {
		List<AlertOption> alertOptionList = null;
		try {
			Query query = em.createNamedQuery("getAlertOptionByMobileProfileId");
			query.setParameter("mobileProfileId", mobileProfileId);
			alertOptionList = (List<AlertOption>)query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return alertOptionList;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<AlertOption> getAlertOptionByStatus(AlertOptionStatus status) {
		List<AlertOption> alertOptionList = null;
		try {
			Query query = em.createNamedQuery("getAlertOptionByStatus");
			query.setParameter("status", status);
			alertOptionList = (List<AlertOption>)query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return alertOptionList;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<AlertOption> getAlertOptionByTransationTypeId(
			String transactionTypeId) {
		List<AlertOption> alertOptionList = null;
		try {
			Query query = em.createNamedQuery("getAlertOptionByTransationTypeId");
			query.setParameter("transactionTypeId", transactionTypeId);
			alertOptionList = (List<AlertOption>)query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return alertOptionList;
	}

	@Override
	public TransactionType getTransactionTypeByCode(String transactionTypeCode) {
		TransactionType transactionType = null;
		try {
			Query query = em.createNamedQuery("getTransactionTypeByCode");
			query.setParameter("transactionCode", transactionTypeCode);
			transactionType = (TransactionType)query.getSingleResult();
		}catch (NoResultException ne) {
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return transactionType;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<TransactionType> getTransactionTypeByStatus(
			TransactionTypeStatus status) {
		List<TransactionType> transactionTypeList = null;
		try {
			Query query = em.createNamedQuery("getTransactionTypeByStatus");
			query.setParameter("status", status);
			transactionTypeList = (List<TransactionType>)query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return transactionTypeList;
	}
	
	private AlertOption updateAlertOption(AlertOption alertOption) throws Exception{
		try {
			alertOption = em.merge(alertOption);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return alertOption;		
	}
	
	private TransactionType updateTransactionType(TransactionType transactionType) throws Exception{
		try {
			transactionType = em.merge(transactionType);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return transactionType;
	}

	@Override
	public AlertOption disableAlertOption(AlertOption alertOption,
			String username) throws Exception {
		//String oldAlertOption = this.findAlertOptionById(alertOption.getId()).getAuditableAttributesString();
		alertOption.setStatus(AlertOptionStatus.DISABLED);
		alertOption = this.updateAlertOption(alertOption);
		
		//Audit Trail
		//AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
		//auditService.logActivity(username, AuditEvents.DISABLE_ALERT_OPTION, alertOption.getBankAccountId(), alertOption.getEntityName(), oldAlertOption, alertOption.getAuditableAttributesString(), alertOption.getInstanceName());
		
		return alertOption;	
	}

	@Override
	public TransactionType disableTransactionType(
			TransactionType transactionType, String username) throws Exception {
		String oldTransactionType = this.findTransactionType(transactionType.getId()).getAuditableAttributesString();
		transactionType.setStatus(TransactionTypeStatus.DISABLED);
		transactionType = this.updateTransactionType(transactionType);
		
		//Audit Trail
		AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
		auditService.logActivity(username, AuditEvents.DISABLE_ALERT_OPTION, transactionType.getId(), transactionType.getEntityName(), oldTransactionType, transactionType.getAuditableAttributesString(), transactionType.getInstanceName());
		
		return transactionType;
	}

	@Override
	public AlertOption enableAlertOption(AlertOption alertOption,
			String username) throws Exception {
		//String oldAlertOption = this.findAlertOptionById(alertOption.getId()).getAuditableAttributesString();
		alertOption.setStatus(AlertOptionStatus.ACTIVE);
		alertOption = this.updateAlertOption(alertOption);
		
		//Audit Trail
		//AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
		//auditService.logActivity(username, AuditEvents.ENABLE_ALERT_OPTION, alertOption.getBankAccountId(), alertOption.getEntityName(), oldAlertOption, alertOption.getAuditableAttributesString(), alertOption.getInstanceName());
		
		return alertOption;			
	}

	@Override
	public TransactionType enableTransactionType(
			TransactionType transactionType, String username) throws Exception {
		String oldTransactionType = this.findTransactionType(transactionType.getId()).getAuditableAttributesString();
		transactionType.setStatus(TransactionTypeStatus.ENABLED);
		transactionType = this.updateTransactionType(transactionType);
		
		//Audit Trail
		AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
		auditService.logActivity(username, AuditEvents.ENABLE_TRANSACTION, transactionType.getId(), transactionType.getEntityName(), oldTransactionType, transactionType.getAuditableAttributesString(), transactionType.getInstanceName());
		
		return transactionType;
	}

	@SuppressWarnings("unchecked")
	public List<TransactionType> getAllTransactionTypes() {
		List<TransactionType> transactionTypeList = null;
		try {
			Query query = em.createNamedQuery("getAllTransactionTypes");
			query.setParameter("enabled", TransactionTypeStatus.ENABLED);
			query.setParameter("disabled", TransactionTypeStatus.DISABLED);
			transactionTypeList = (List<TransactionType>)query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return transactionTypeList;
		
	}
	
	public TransactionType getTransactionTypeByAlertOption(AlertOption alertOption){
		alertOption = em.merge(alertOption);
		return alertOption.getTransactionType();
	}

	public void createAlertOptionsForAccount(String accountId,
			String mobileProfileId, String username) throws Exception {
		for(TransactionType transactionType:this.getAllTransactionTypes()){
			System.out.println(">>>>>>>>>"+transactionType);
			AlertOption alertOption = new AlertOption();
			alertOption.setTransactionType(transactionType);
			alertOption.setBankAccountId(accountId);
			alertOption.setMobileProfileId(mobileProfileId);
			alertOption.setStatus(AlertOptionStatus.DISABLED);
			this.createAlertOption(alertOption, username);
		}
				
	}

}
