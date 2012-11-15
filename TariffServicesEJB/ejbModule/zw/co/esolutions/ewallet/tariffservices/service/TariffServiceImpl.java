package zw.co.esolutions.ewallet.tariffservices.service;

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
import zw.co.esolutions.ewallet.enums.AgentType;
import zw.co.esolutions.ewallet.enums.CustomerClass;
import zw.co.esolutions.ewallet.enums.TariffStatus;
import zw.co.esolutions.ewallet.enums.TariffType;
import zw.co.esolutions.ewallet.enums.TariffValueType;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.serviceexception.EWalletException;
import zw.co.esolutions.ewallet.tariffservices.model.Tariff;
import zw.co.esolutions.ewallet.tariffservices.model.TariffTable;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.GenerateKey;

/**
 * Session Bean implementation class TariffServiceImpl
 */
@Stateless
@WebService(serviceName = "TariffService", endpointInterface = "zw.co.esolutions.ewallet.tariffservices.service.TariffService", 
		portName = "TariffServiceSOAP")
public class TariffServiceImpl implements TariffService {


    @PersistenceContext
	private EntityManager em;

	
	/**
     * Default constructor. 
     */
    public TariffServiceImpl() {
        
    }
    
    
    public Tariff createCommission(Tariff tariff, String userName) throws EWalletException {
		
		try {
			if(tariff == null) {
				throw new EWalletException("Null Tariff") ;
			}
			if(tariff.getTariffTable().getTariffType().equals(TariffType.FIXED_AMOUNT) || 
					tariff.getTariffTable().getTariffType().equals(TariffType.SCALED)) {
				tariff.setValueType(TariffValueType.ABSOLUTE);
			} else if(tariff.getTariffTable().getTariffType().equals(TariffType.FIXED_PERCENTAGE) || 
					tariff.getTariffTable().getTariffType().equals(TariffType.PERCENTAGE_PLUS_LIMITS) ||
					tariff.getTariffTable().getTariffType().equals(TariffType.SCALED_PERCENTAGE)) {
				tariff.setValueType(TariffValueType.PERCENTAGE);
			}
			Tariff existingTariff = null;
			if(!tariff.getTariffTable().getTariffType().equals(TariffType.SCALED)) {
				existingTariff = this.findTariffByTariffTableIdAndTariffAttributes(tariff.getTariffTable().getId(),
						tariff.getValueType(), tariff.getTariffTable().getBankId());
			} else {
				//Scaled Tariffs need to differ in values only
				existingTariff = this.findExactTariff(tariff);
				
			}
			System.out.println(">>>>>>>>>>>>>>>>>>>>> Existing Tariff "+existingTariff);
			if(existingTariff != null) {
				System.out.println(">>>>>>>>>>>>> New Tariff value "+tariff.getValue()+" " +
						"Old Tariff value = "+existingTariff.getValue());
				return null;
			}
			
			tariff = this.createTariff(tariff);
						
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.CREATE_COMMISSION, tariff.getId(), tariff.getEntityName(), null, tariff.getAuditableAttributesString(), tariff.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new EWalletException(e.getMessage());
		}
		return tariff;
	}

    private Tariff createTariff(Tariff tariff) throws EWalletException {
    	if(tariff.getId() == null) {
			tariff.setId(GenerateKey.generateEntityId());
		}
		if(tariff.getDateCreated() == null) {
			tariff.setDateCreated(new Date());
		}
		TariffTable table = this.findTariffTableById(tariff.getTariffTable().getId());
		table.setStatus(TariffStatus.AWAITING_APPROVAL);
		table = em.merge(table);
		tariff.setTariffTable(table);
		em.persist(tariff);
    	return tariff;
    }
    
	public String deleteTariff(String tariffId, String userName) throws EWalletException {
		try {
			Tariff tariff = em.find(Tariff.class, tariffId);
			//tariff.setStatus(TariffStatus.DELETED);
			tariff = em.merge(tariff);
			
		} finally {
			
		}
		return "success";
	}

   private Tariff updateTariff(Tariff tariff, TariffStatus status) throws EWalletException {
		System.out.println(">>>>>>>>>>>>>>>>>>>>>> In Update Tariff");
	   try {
			//Check for duplicate Tariffs on Table
			Tariff duplicateTariff = this.findExactTariff(tariff);
			if(duplicateTariff != null) {
				return null;
			}
			
			System.out.println("Duplicate tariff = "+duplicateTariff);
			
			//Update TariffTable and Populate Tariffs
			TariffTable oldTariffTable = this.findTariffTableById(tariff.getTariffTable().getId());
			
			if(!TariffStatus.AWAITING_APPROVAL.equals(oldTariffTable.getStatus())) {
			//Creating new TariffTable
			TariffTable table = new TariffTable();
			table.setStatus(status);
			table.setAgentType(oldTariffTable.getAgentType());
			table.setBankId(oldTariffTable.getBankId());
			table.setCustomerClass(oldTariffTable.getCustomerClass());
			table.setEffectiveDate(oldTariffTable.getEffectiveDate());
			table.setEndDate(oldTariffTable.getEndDate());
			table.setOldTariffTableId(oldTariffTable.getId());
			table.setTariffType(oldTariffTable.getTariffType());
			table.setTransactionType(oldTariffTable.getTransactionType());
			table = this.createTariffTable(table);
			
			//Create Tariff
			tariff.setTariffTable(table);
			tariff.setId(null);
			tariff.setDateCreated(null);
			tariff.setVersion(0);
			tariff = this.createTariff(tariff);
			
			//Retrieving List of Old Tariffs
			List<Tariff> oldTariffs = this.getTariffsByTariffTableId(oldTariffTable.getId());
			if(oldTariffs != null) {
				if(!oldTariffs.isEmpty()) {
					for(Tariff t : oldTariffs) {
						if(!tariff.getOldTariffId().equalsIgnoreCase(t.getId())) {
							//Values to be populated in createTariff
							Tariff tf = new Tariff();
							tf.setDateCreated(t.getDateCreated());
							tf.setTariffTable(table);
							tf.setOldTariffId(t.getId());
							tf.setValueType(t.getValueType());
							tf.setLowerLimit(t.getLowerLimit());
							tf.setUpperLimit(t.getUpperLimit());
							tf.setValue(t.getValue());
							this.createTariff(tf);
						}
						
					}
				}
			}
		 }	else {
			 tariff.setTariffTable(oldTariffTable);
			 tariff = em.merge(tariff);
		 }
						
		} catch (Exception e) {
			e.printStackTrace();
			throw new EWalletException(e.getMessage());
		}
		return tariff;
	}
	
	public Tariff findTariffById(String id) {
		Tariff tariff = null;
		try {
			tariff = (Tariff) em.find(Tariff.class, id);
			
		} finally {
			
		}
		return tariff;
	}
	
	@SuppressWarnings("unchecked")
	public List<Tariff> getAllTariffs() {
		List<Tariff> results = null;
		try {
			Query query = em.createNamedQuery("getTariff");
			results = (List<Tariff>)query.getResultList();
		} catch (Exception e) {
			return null;
		}
		if(results == null || results.isEmpty()) {
			return null;
		}
		return results;
	}
	
	@SuppressWarnings("unchecked")
	public List<Tariff> getAllTariffsByBankId(String bankId) {
		List<Tariff> results = null;
		try {
			Query query = em.createNamedQuery("getAllTariffsByBankId");
			query.setParameter("bankId", bankId);
			results = (List<Tariff>)query.getResultList();
		} catch (Exception e) {
			return null;
		}
		if(results == null || results.isEmpty()) {
			return null;
		}
		return results;
	}
	
	
	public TariffTable createCommissionTable(TariffTable tariffTable, String userName) throws EWalletException {
		try {
			if(tariffTable == null) {
				throw new EWalletException("Null tariffTable.");
			}
			
					
			System.out.println(">>>>>>>>>>>>>>>>>>>> Now In Creating Table.");
			if(tariffTable.getStatus() == null) {
				tariffTable.setStatus(TariffStatus.DRAFT);
			} if(tariffTable.getId() == null) {
				tariffTable.setId(GenerateKey.generateEntityId());
			} if(tariffTable.getDateCreated() == null) {
				tariffTable.setDateCreated(new Date());
				tariffTable.setEffectiveDate(DateUtil.getBeginningOfDay(tariffTable.getEffectiveDate()));
				tariffTable.setEndDate(DateUtil.getEndOfDay(tariffTable.getEndDate()));
				
			} 
			
			TariffTable exactTable = null;
			exactTable = this.findTariffTableByAllAttributes(
					tariffTable.getTariffType(), tariffTable.getTransactionType(), 
					tariffTable.getEffectiveDate(), tariffTable.getEndDate(), tariffTable.getAgentType(), tariffTable.getCustomerClass(),
					tariffTable.getBankId());
			if (exactTable != null) {
				if(TariffStatus.AWAITING_APPROVAL.equals(exactTable.getStatus())) {
					throw new EWalletException(TariffStatus.AWAITING_APPROVAL.toString());
				}
				if(TariffStatus.DRAFT.equals(exactTable.getStatus())) {
					throw new EWalletException(TariffStatus.DRAFT.toString());
				}
				if (!(TariffStatus.INACTIVE.equals(exactTable.getStatus()) || 
						TariffStatus.DELETED.equals(exactTable.getStatus()) ||
						TariffStatus.DISAPPROVED.equals(exactTable.getStatus()))) {
					return null;
				}
			}
			System.out.println(">>>>>>>>>>>>>>>TariffTable  Not Existing.");

			TariffTable tb = tariffTable;
			tb = this.getExactEffectiveTable(tb);
			
			System.out.println(">>>>>>>>>>>>>>>>>>>> Already Active Table = "+tb);
			
			if(!(tb == null || tb.getId() == null)) {
				tariffTable.setOldTariffTableId(tb.getId());
			}
			
			tariffTable = this.createTariffTable(tariffTable);
			
			//Audit trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.CREATE_COMMISSION_TABLE, tariffTable.getId(), tariffTable.getEntityName(), null, tariffTable.getAuditableAttributesString(), tariffTable.getInstanceName());
		} catch (Exception e) {
			//e.printStackTrace();
			throw new EWalletException(e.getMessage());
		}/*finally {
			
		}*/
		return tariffTable;
	}
	
	
	public TariffTable editCommissionTable(TariffTable tariffTable, String userName) throws EWalletException {
		if(tariffTable == null) {
				throw new EWalletException("Null tariffTable.");
		}
		try{
			TariffTable oldTable = em.find(TariffTable.class, tariffTable.getOldTariffTableId());
			String oldTariffTable = oldTable.getAuditableAttributesString();
			tariffTable = this.updateTariffTable(tariffTable);
			if(tariffTable == null) {
				return null;
			}
			
			//Audit trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.EDIT_COMMISSION_TABLE, tariffTable.getId(), tariffTable.getEntityName(), oldTariffTable, tariffTable.getAuditableAttributesString(), tariffTable.getInstanceName());
		}catch (Exception e) {
			//e.printStackTrace();
			throw new EWalletException(e.getMessage());
		}
		return tariffTable;
			
	}
	
	public TariffTable approveCommission(TariffTable tariffTable, String userName) throws EWalletException {
		if(tariffTable == null || tariffTable.getId() == null) {
				throw new EWalletException("Null tariffTable.");
		}
		TariffTable tab = this.findTariffTableById(tariffTable.getId());
		if (tariffTable.getOldTariffTableId() != null) {
			TariffTable oldTariffTable = this.findTariffTableById(tariffTable
					.getOldTariffTableId());
			if(TariffStatus.AWAITING_APPROVAL.equals(tab.getStatus())) {
				oldTariffTable.setStatus(TariffStatus.INACTIVE);
				//Do nothing on old tariff
				if(oldTariffTable.getEndDate() != null) {
					tariffTable.setDateCreated(oldTariffTable.getEndDate());
				}
				oldTariffTable.setEndDate(DateUtil.addHours(DateUtil.getEndOfDay(tariffTable.getEffectiveDate()), -24));
			} else if(TariffStatus.DISAPPROVED.equals(tab.getStatus())) {
				
				oldTariffTable.setStatus(TariffStatus.INACTIVE);
				oldTariffTable.setEndDate(DateUtil.addHours(DateUtil.getEndOfDay(tariffTable.getEffectiveDate()), -24));
				
			}
			//oldTariffTable.setStatus(TariffStatus.INACTIVE);
			//oldTariffTable.setEndDate(DateUtil.addHours(DateUtil.getEndOfDay(tariffTable.getEffectiveDate()), -24));
			em.merge(oldTariffTable);
		}
		try{
			String oldTariffTableString = tariffTable.getAuditableAttributesString();
			tariffTable.setStatus(TariffStatus.ACTIVE);
			tariffTable = em.merge(tariffTable);
			
			//Audit trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.APPROVE_COMMISSION_TABLE, tariffTable.getId(), tariffTable.getEntityName(), oldTariffTableString, tariffTable.getAuditableAttributesString(), tariffTable.getInstanceName());
		}catch (Exception e) {
			//e.printStackTrace();
			throw new EWalletException("Approve Error.");
		}
		return tariffTable;
			
	} 
	
	public TariffTable disapproveCommission(TariffTable tariffTable, String userName) throws EWalletException {
		if(tariffTable == null) {
				throw new EWalletException("Null tariffTable.");
		}
		TariffTable tab = this.findTariffTableById(tariffTable.getId());
		if (tariffTable.getOldTariffTableId() != null) {
			TariffTable oldTariffTable = this.findTariffTableById(tariffTable.getOldTariffTableId());
			if(TariffStatus.AWAITING_APPROVAL.equals(tab.getStatus())) {
				//Do nothing on original Table, just pick end Date
				if(oldTariffTable.getEndDate() !=  null){
					tariffTable.setDateCreated(oldTariffTable.getEndDate());
				}
			} else if(TariffStatus.ACTIVE.equals(tab.getStatus())) {
				if (DateUtil.isEndDate(tariffTable.getDateCreated())) {
					oldTariffTable.setStatus(TariffStatus.INACTIVE);
					oldTariffTable.setEndDate(tariffTable.getDateCreated());
				} else {
					oldTariffTable.setStatus(TariffStatus.ACTIVE);
					oldTariffTable.setEndDate(null);
				}
			}
			//oldTariffTable.setStatus(TariffStatus.ACTIVE);
			oldTariffTable = em.merge(oldTariffTable);
		}
		try{
			String oldTariffTableString = tariffTable.getAuditableAttributesString();
			tariffTable.setStatus(TariffStatus.DISAPPROVED);
			tariffTable = em.merge(tariffTable);
			
			//Audit trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.REJECT_COMMISSION_TABLE, tariffTable.getId(), tariffTable.getEntityName(), oldTariffTableString, tariffTable.getAuditableAttributesString(), tariffTable.getInstanceName());
		}catch (Exception e) {
			//e.printStackTrace();
			throw new EWalletException("Rejection Error.");
		}
		return tariffTable;
	}
	
	public Tariff editCommission(Tariff tariff, String userName) throws EWalletException {
		System.out.println(">>>>>>>>>>>>>> In Edit");
		if(tariff == null) {
			throw new EWalletException("Null tariff.");
	   }
		try{
			Tariff oldTariff = em.find(Tariff.class, tariff.getOldTariffId());
			String oldTariffString = oldTariff.getAuditableAttributesString();
			tariff = this.updateTariff(tariff, TariffStatus.AWAITING_APPROVAL);
			System.out.println(">>>>>>>>>>>>>>>>> tariff = "+tariff);
			if(tariff == null) {
				return null;
			}
			//Audit trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.EDIT_COMMISION, tariff.getId(), tariff.getEntityName(), oldTariffString, tariff.getAuditableAttributesString(), tariff.getInstanceName());
		}catch (Exception e) {
			//e.printStackTrace();
			throw new EWalletException(e.getMessage());
		}
		return tariff;
	}

	private TariffTable createTariffTable(TariffTable tariffTable) throws EWalletException {
		 if(tariffTable.getId() == null) {
				tariffTable.setId(GenerateKey.generateEntityId());
		 } if(tariffTable.getDateCreated() == null) {
				tariffTable.setDateCreated(new Date());
				tariffTable.setEffectiveDate(DateUtil.getBeginningOfDay(tariffTable.getEffectiveDate()));
				tariffTable.setEndDate(DateUtil.getEndOfDay(tariffTable.getEndDate()));
				
		} 
		em.persist(tariffTable);
		return tariffTable;
	}
	
	public String deleteTariffTable(String tariffTableId, String userName) throws EWalletException {
		try{
			TariffTable  tariffTable = em.find(TariffTable.class, tariffTableId);
			String oldTariffTableString = tariffTable.getAuditableAttributesString();
			tariffTable.setStatus(TariffStatus.DELETED);
			tariffTable = em.merge(tariffTable);
			//Audit trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.DELETE_COMMISSION_TABLE, tariffTable.getId(), tariffTable.getEntityName(), oldTariffTableString, tariffTable.getAuditableAttributesString(), tariffTable.getInstanceName());
		}catch (Exception e) {
			//e.printStackTrace();
			throw new EWalletException("Commission Table Deletion Error.");
		}
		return "success";
	}

	private TariffTable updateTariffTable(TariffTable tariffTable) throws EWalletException {
		try {
			System.out.println(">>>>>>>>>>>>>>>>>>>> bank id = "+tariffTable.getBankId());
			TariffTable tb = em.find(TariffTable.class, tariffTable.getId());	
			 TariffTable effectiveTable = null;
			 effectiveTable = tariffTable;
			 tariffTable.setEffectiveDate(DateUtil.getBeginningOfDay(tariffTable.getEffectiveDate()));
			 effectiveTable = this.getExactEffectiveTable(effectiveTable);
					    
		    TariffTable exactTable = this.findTariffTableByAllAttributes(tariffTable.getTariffType(), tariffTable.getTransactionType(),
		    		DateUtil.getBeginningOfDay(tariffTable.getEffectiveDate()), DateUtil.getEndOfDay(tariffTable.getEndDate()), tariffTable.getAgentType(), tariffTable.getCustomerClass(), tariffTable.getBankId());
			
		    if(exactTable != null) {
		    	if(TariffStatus.AWAITING_APPROVAL.equals(exactTable.getStatus())) {
		    		throw new EWalletException(TariffStatus.AWAITING_APPROVAL.toString());
		    	}
		    	if(TariffStatus.DRAFT.equals(exactTable.getStatus())) {
		    		throw new EWalletException(TariffStatus.DRAFT.toString());
		    	}
 		    }
		    
		  //Old Table
		   TariffTable oldTable = tb;
		    
		  if(TariffStatus.ACTIVE.equals(oldTable.getStatus())) {
		    		if(effectiveTable == null || effectiveTable.getId() == null) {
		    			//Old table must be null or its ID
		    			return this.updateActiveTable(tariffTable, oldTable, true);
		    		} else {
		    			if(tb.getId().equalsIgnoreCase(effectiveTable.getId())) {
		    			  return this.updateActiveTable(tariffTable, oldTable, false);
		    			} else {
		    				oldTable = effectiveTable;
		    				return this.updateActiveTable(tariffTable, oldTable, false);
		    			}
		    		}
		  } else {		  
		  System.out.println(">>>>>>>>>>>>>>>> tariff table merge");
		    	if(effectiveTable == null || effectiveTable.getId() == null) {
		    		tariffTable.setOldTariffTableId(null);
		    	} else {
		    		tariffTable.setOldTariffTableId(effectiveTable.getId());
		    	}
		    	tariffTable.setId(tb.getId());
		    	tariffTable.setStatus(tb.getStatus());
				tariffTable.setVersion(tb.getVersion());
				tariffTable.setDateCreated(tb.getDateCreated());
				System.out.println(">>>>>>>>>>>>>>>> OldTariffTableId= "+tariffTable.getOldTariffTableId());
				//tariffTable = this.updateTable(tariffTable);
				tariffTable = this.forceTableUpdate(tariffTable);
				System.out.println(">>>>>>>>>>>>>>>>After Merege OldTariffTableId= "+tariffTable.getOldTariffTableId());
				tariffTable = this.updateTarrifs(tariffTable);
				System.out.println(">>>>>>>>>>>>>>>> After Update OldTariffTableId= "+tariffTable.getOldTariffTableId());
		   }
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw new EWalletException(e.getMessage());
		}
		return tariffTable;
	}

	public TariffTable findTariffTableById(String id) {
		TariffTable tariffTable = null;
		try {
			tariffTable = (TariffTable) em.find(TariffTable.class, id);
		} catch (Exception e) {
			return null;
		} 
		return tariffTable;
	}
	
	@SuppressWarnings("unchecked")
	public List<TariffTable> getAllTariffTables() {
		List<TariffTable> results = null;
		try {
			Query query = em.createNamedQuery("getTariffTable");
			results = (List<TariffTable>)query.getResultList();
		} catch (Exception e) {
			return null;
		}
		if(results == null || results.isEmpty()) {
			return null;
		}
		return results;
	}
	
	@SuppressWarnings("unchecked")
	public List<TariffTable> getAllTariffTablesByBankId(String bankId) {
		List<TariffTable> results = null;
		try {
			Query query = em.createNamedQuery("getAllTariffTablesByBankId");
			query.setParameter("bankId", bankId);
			results = (List<TariffTable>)query.getResultList();
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
	public List<Tariff> getTariffByTariffTableAndBankId(String tariffTableId, String bankId) {
		List<Tariff> results = null;
		try {
			Query query = em.createNamedQuery("getTariffByTariffTableAndBankId");
			query.setParameter("tariffTableId", tariffTableId);
			query.setParameter("bankId", bankId);
			results = (List<Tariff>)query.getResultList();
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
	public List<Tariff> getTariffByTariffTableIdValueTypeAndBankId(
			String tariffTableId, TariffValueType valueType, String bankId) {
		List<Tariff> results = null;
		try {
			Query query = em.createNamedQuery("getTariffByTariffTableIdValueTypeAndBankId");
			query.setParameter("tariffTableId", tariffTableId);
			query.setParameter("valueType", valueType);
			query.setParameter("bankId", bankId);
			results = (List<Tariff>)query.getResultList();
		} catch (Exception e) {
			return null;
		}
		if(results == null || results.isEmpty()) {
			return null;
		}
		return results;
	}

	public TariffTable getTariffTableByTariffTypeTransactionTypeEffectiveDateAndBankId(
			TariffType tariffType, TransactionType transactionType,
			Date effectiveDate, AgentType agentType, String bankId) {
		TariffTable result = null;
		try {
			Query query = em.createNamedQuery("getTariffTableByTariffTypeTransactionTypeEffectiveDateAndBankId");
			query.setParameter("tariffType", tariffType);
			query.setParameter("transactionType", transactionType);
			query.setParameter("effectiveDate", DateUtil.getBeginningOfDay(effectiveDate));
			query.setParameter("agentType", agentType);
			query.setParameter("bankId", bankId);
			result = (TariffTable)query.getSingleResult();
		}catch (NoResultException nrhe) {
			return null;
		} catch (Exception e) {
			
		}
		
		return result;
		
	}

	public TariffTable getTariffTableByTariffTypeEffectiveDateAndBankId(
			TariffType tariffType, Date effectiveDate, AgentType agentType, String bankId) {
		TariffTable result = null;
		try {
			Query query = em.createNamedQuery("getTariffTableByTariffTypeEffectiveDateAndBankId");
			query.setParameter("tariffType", tariffType);
			query.setParameter("effectiveDate", DateUtil.getBeginningOfDay(effectiveDate));
			query.setParameter("agentType", agentType);
			query.setParameter("bankId", bankId);
			result = (TariffTable)query.getSingleResult();
		}catch (NoResultException nrhe) {
			return null;
		} catch (Exception e) {
			
		}
		
		return result;
		
	}

	@SuppressWarnings("unchecked")
	public List<TariffTable> getTariffTableByTariffTypeAndBankId(TariffType tariffType, String bankId) {
		List<TariffTable> results = null;
		try {
			Query query = em.createNamedQuery("getTariffTableByTariffTypeAndBankId");
			query.setParameter("tariffType", tariffType);
			query.setParameter("bankId", "%"+bankId+"%");
			results = (List<TariffTable>)query.getResultList();
		} catch (Exception e) {
			return null;
		}
		if(results == null || results.isEmpty()) {
			return null;
		}
		return results;
		
	}

	@SuppressWarnings("unchecked")
	public List<TariffTable> getTariffTableByTransactionTypeAndBankId(
			TransactionType transactionType, String bankId) {
		List<TariffTable> results = null;
		try {
			Query query = em.createNamedQuery("getTariffTableByTransactionTypeAndBankId");
			query.setParameter("transactionType", transactionType);
			query.setParameter("bankId", "%"+bankId+"%");
			results = (List<TariffTable>)query.getResultList();
		} catch (Exception e) {
			return null;
		}
		if(results == null || results.isEmpty()) {
			return null;
		}
		return results;
		
	}

	public TariffTable getTariffTableByTariffTypeTransactionTypeEffectiveDateEndDateAndBankId(
			TariffType tariffType, TransactionType transactionType,
			Date effectiveDate, Date endDate, AgentType agentType, String bankId) {
		TariffTable result = null;
		try {
			Query query = em.createNamedQuery("getTariffTableByTariffTypeTransactionTypeEffectiveDateEndDateAndBankId");
			query.setParameter("tariffType", tariffType);
			query.setParameter("transactionType", transactionType);
			query.setParameter("effectiveDate", DateUtil.getBeginningOfDay(effectiveDate));
			query.setParameter("endDate", DateUtil.getEndOfDay(endDate));
			query.setParameter("agentType", agentType);
			query.setParameter("bankId", bankId);
			result = (TariffTable)query.getSingleResult();
		}catch (NoResultException nre) {
			return null;
		} catch (Exception e) {
			
		}
		
		return result;
		
	}

	@SuppressWarnings("unchecked")
	public List<TariffTable> getTariffTableByTransactionTypeEffectiveDateAgentTypeAndBankId(
			TransactionType transactionType,
			Date effectiveDate, AgentType agentType, String bankId) {
		List<TariffTable> results = null;
		try {
			Query query = em.createNamedQuery("getTariffTableByTransactionTypeEffectiveDateAgentTypeAndBankId");
			query.setParameter("transactionType", transactionType);
			query.setParameter("effectiveDate", DateUtil.getBeginningOfDay(effectiveDate));
			query.setParameter("agentType", agentType);
			query.setParameter("bankId", bankId);
			results = (List<TariffTable>)query.getResultList();
		} catch (Exception e) {
			return null;
		}
		if(results == null || results.isEmpty()) {
			return null;
		}
		return results;
		
	}

	@SuppressWarnings("unchecked")
	public List<TariffTable> getTariffTableByTariffTypeTransactionAgentTypeAndBankId(
			TariffType tariffType, TransactionType transactionType, AgentType agentType, String bankId) {
		List<TariffTable> results = null;
		try {
			Query query = em.createNamedQuery("getTariffTableByTariffTypeTransactionAgentTypeAndBankId");
			query.setParameter("tariffType", tariffType);
			query.setParameter("transactionType", transactionType);
			query.setParameter("agentType", agentType);
			query.setParameter("bankId", bankId);
			results = (List<TariffTable>)query.getResultList();
		} catch (Exception e) {
			return null;
		}
		if(results == null || results.isEmpty()) {
			return null;
		}
		return results;
		
	}
	
	private Tariff findTariffByTariffTableIdAndTariffAttributes(String tableId, TariffValueType valueType, String bankId) {
		Tariff tariff = null;
		try {
			Query query = em.createNamedQuery("getTariffByTariffTableIdValueTypeAndBankId");
			query.setParameter("tariffTableId", tableId);
			query.setParameter("valueType", valueType);
			query.setParameter("bankId", bankId);
			tariff = (Tariff)query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			
		}
		return tariff;
	}
	
	private Tariff findExactTariff(Tariff tariff) {
		try {
			Query query = em.createNamedQuery("getExactTariff");
			query.setParameter("tariffTableId", tariff.getTariffTable().getId());
			query.setParameter("valueType", tariff.getValueType());
			query.setParameter("lowerLimit", tariff.getLowerLimit());
			query.setParameter("upperLimit", tariff.getUpperLimit());
			query.setParameter("value", tariff.getValue());
			query.setParameter("bankId", tariff.getTariffTable().getBankId());
			tariff = (Tariff)query.getSingleResult();
			return tariff;
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	private TariffTable findTariffTableByAllAttributes(
			TariffType tariffType, TransactionType transactionType,
			Date effectiveDate, Date endDate, AgentType agentType, CustomerClass customerClass, 
			String bankId) {
		TariffTable result = null;
		try {
			Query query = em.createNamedQuery("getTariffTableByAllAttributes");
			query.setParameter("transactionType", transactionType);
			query.setParameter("effectiveDate", DateUtil.getBeginningOfDay(effectiveDate));
			query.setParameter("endDate", DateUtil.getEndOfDay(endDate));
			query.setParameter("agentType", agentType);
			query.setParameter("customerClass", customerClass);
			query.setParameter("bankId", bankId);
			result = (TariffTable)query.getSingleResult();
		}catch (NoResultException nre) {
		
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
		return result;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<TariffTable> getTariffTableByDateRangeAndBankId(
			Date fromDate, Date toDate, String bankId) {
		List<TariffTable> results = null;
		try {
			Query query = em.createNamedQuery("getTariffTableByDateRangeAndBankIdRevised");
			query.setParameter("fromDate", DateUtil.getBeginningOfDay(fromDate));
			query.setParameter("transactionType", DateUtil.getEndOfDay(toDate));
			query.setParameter("bankId", bankId);
			query.setParameter("active", TariffStatus.ACTIVE);
			query.setParameter("inActive", TariffStatus.INACTIVE);
			results = (List<TariffTable>)query.getResultList();
		} catch (Exception e) {
			return null;
		}
		if(results == null || results.isEmpty()) {
			return null;
		}
		return results;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<TariffTable> getEffectiveTariffTablesForBank(String bankId) {
		List<TariffTable> results = null;
    	try {
			Query query = em.createNamedQuery("getEffectiveTariffTablesForBank");
			query.setParameter("toDate", DateUtil.getEndOfDay(new Date()));
			query.setParameter("fromDate", DateUtil.getBeginningOfDay(new Date()));
			query.setParameter("bankId", "%"+bankId+"%");
			query.setParameter("active", TariffStatus.ACTIVE);
			query.setParameter("inActive", TariffStatus.INACTIVE);
			results = (List<TariffTable>)query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if(results == null || results.isEmpty()) {
			return null;
		}
		return results;
		
	}
	

	public long calculateTariffCharge(CustomerClass customerClass, TransactionType txnType, AgentType agentType, long amount, String bankId) 
	throws EWalletException {
		long value = 0L;
		Date onDate = new Date();
		Tariff tariff = null;
		try {
			Query query = em.createNamedQuery("getActualTariff");
			query.setParameter("customerClass", customerClass);
			query.setParameter("transactionType", txnType);
			query.setParameter("scaled", TariffType.SCALED);
			query.setParameter("scaledPerc", TariffType.SCALED_PERCENTAGE);
			query.setParameter("agentType", agentType);
			query.setParameter("amount", amount);
			query.setParameter("fixedPerc", TariffType.FIXED_PERCENTAGE);
			query.setParameter("percPlusLimits", TariffType.PERCENTAGE_PLUS_LIMITS);
			query.setParameter("fixedAmount", TariffType.FIXED_AMOUNT);
			query.setParameter("onDateStart", DateUtil.getBeginningOfDay(onDate));
			query.setParameter("onDateEnd", DateUtil.getEndOfDay(onDate));
			query.setParameter("active", TariffStatus.ACTIVE);
			query.setParameter("inActive", TariffStatus.INACTIVE);
			query.setParameter("bankId", bankId);
			tariff = (Tariff)query.getSingleResult();
		} catch (NoResultException nre) {
			nre.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			throw new EWalletException("Tariff not found due to errors.");
		}
		if(tariff != null) {
			if(tariff.getTariffTable().getTariffType().equals(TariffType.FIXED_AMOUNT) || 
					tariff.getTariffTable().getTariffType().equals(TariffType.SCALED) ) {
				value = tariff.getValue();
			} else if(tariff.getTariffTable().getTariffType().equals(TariffType.FIXED_PERCENTAGE) ||
					tariff.getTariffTable().getTariffType().equals(TariffType.SCALED_PERCENTAGE)){
				value = (tariff.getValue()*amount) / EWalletConstants.PERCENTAGE_DIVISOR;
			}else if(tariff.getTariffTable().getTariffType().equals(TariffType.PERCENTAGE_PLUS_LIMITS) ){
				value = (tariff.getValue()*amount) / EWalletConstants.PERCENTAGE_DIVISOR;
				
				if(value < tariff.getLowerLimit()) {
					value = tariff.getLowerLimit();
				} else if(value > tariff.getUpperLimit()) {
					value = tariff.getUpperLimit();
				} else {
				
				}
			}
		}
		return value;
	}
	
	
	public Tariff retrieveAppropriateTariff(CustomerClass customerClass, TransactionType txnType, AgentType agentType, long amount, String bankId) throws EWalletException {
		Date onDate = new Date();
		Tariff tariff = null;
		System.out.println("Retrieving Tariff : CUST CLASS > " + customerClass + " TXN TYPE > " + txnType + " AGNT > " + agentType +" AMT > " + amount + " Bank ID > " + bankId);
		try {
			Query query = em.createNamedQuery("getActualTariff");
			query.setParameter("customerClass", customerClass);
			query.setParameter("transactionType", txnType);
			query.setParameter("scaled", TariffType.SCALED);
			query.setParameter("scaledPerc", TariffType.SCALED_PERCENTAGE);
			query.setParameter("agentType", agentType);
			query.setParameter("amount", amount);
			query.setParameter("fixedPerc", TariffType.FIXED_PERCENTAGE);
			query.setParameter("percPlusLimits", TariffType.PERCENTAGE_PLUS_LIMITS);
			query.setParameter("fixedAmount", TariffType.FIXED_AMOUNT);
			query.setParameter("onDateStart", DateUtil.getBeginningOfDay(onDate));
			query.setParameter("onDateEnd", DateUtil.getEndOfDay(onDate));
			query.setParameter("active", TariffStatus.ACTIVE);
			query.setParameter("inActive", TariffStatus.INACTIVE);
			query.setParameter("bankId", bankId);
			tariff = (Tariff)query.getSingleResult();
		} catch (NoResultException nre) {
			nre.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EWalletException("Tariff not found due to errors.");
		}
		return tariff;
	}
	
	@SuppressWarnings("unchecked")
	public List<Tariff> getTariffsByTariffTableId(String tableId) {
		List<Tariff> results = null;
		try {
			Query query = em.createNamedQuery("getTariffsByTariffTableId");
			query.setParameter("tableId", tableId);
			results = (List<Tariff>)query.getResultList();
		} catch (Exception e) {
			return null;
		}
		if(results == null || results.isEmpty()) {
			return null;
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<TariffTable> getTariffTableByCustomerClassAndBankId(CustomerClass customerClass, String bankId) {
		List<TariffTable> results = null;
		try {
			Query query = em.createNamedQuery("getTariffTableByCustomerClassAndBankId");
			query.setParameter("customerClass", customerClass);
			query.setParameter("bankId", "%"+bankId+"%");
			results = (List<TariffTable>)query.getResultList();
		} catch (Exception e) {
			return null;
		}
		if(results == null || results.isEmpty()) {
			return null;
		}
		return results;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<TariffTable> getTariffTableByTariffStatusAndBankId(TariffStatus status, String bankId) {
		List<TariffTable> results = null;
		try {
			Query query = em.createNamedQuery("getTariffTableByTariffStatusAndBankId");
			query.setParameter("status", status);
			query.setParameter("bankId", "%"+bankId+"%");
			results = (List<TariffTable>)query.getResultList();
		} catch (Exception e) {
			return null;
		}
		if(results == null || results.isEmpty()) {
			return null;
		}
		return results;
		
	}
	
	private TariffTable getExactEffectiveTable(TariffTable table) {
		System.out.println(" Effective Date = "+table.getEffectiveDate()+" , Bank Id = "+table.getBankId()+" , Txn type = "+table.getTransactionType()+" , CustomerClass = "+table.getCustomerClass());
		String sql = "SELECT t FROM TariffTable t WHERE t.effectiveDate <= :fromDate AND (t.endDate IS NULL OR t.endDate >= :toDate) " +
				"AND t.bankId LIKE :bankId AND (t.status = :active OR t.status = :inActive) AND t.transactionType = :txnType " +
				"AND t.customerClass = :custClass";
		try { 
			Query query = em.createQuery(sql);
			query.setParameter("toDate", DateUtil.getEndOfDay(table.getEffectiveDate()));
			query.setParameter("fromDate", DateUtil.getBeginningOfDay(table.getEffectiveDate()));
			query.setParameter("bankId", "%"+table.getBankId()+"%");
			query.setParameter("active", TariffStatus.ACTIVE);
			query.setParameter("inActive", TariffStatus.INACTIVE);
			query.setParameter("txnType", table.getTransactionType());
			query.setParameter("custClass", table.getCustomerClass());
			table = (TariffTable) query.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return table;
	}
	
	private Tariff getAdjustedTariff(TariffTable tariffTable, Tariff tariff) {
		
		if(tariff.getTariffTable().getTariffType().equals(TariffType.FIXED_AMOUNT) || 
				tariff.getTariffTable().getTariffType().equals(TariffType.SCALED)) {
			
			tariff.setValueType(TariffValueType.ABSOLUTE);
			
			if(tariff.getTariffTable().getTariffType().equals(TariffType.FIXED_AMOUNT)) {
				tariff.setLowerLimit(0);
				tariff.setUpperLimit(0);
			}
			
		} else if(tariff.getTariffTable().getTariffType().equals(TariffType.FIXED_PERCENTAGE) ||
				tariff.getTariffTable().getTariffType().equals(TariffType.PERCENTAGE_PLUS_LIMITS) ||
				tariff.getTariffTable().getTariffType().equals(TariffType.SCALED_PERCENTAGE)) {
			
			tariff.setValueType(TariffValueType.PERCENTAGE);
			tariff.setLowerLimit(0);
			tariff.setUpperLimit(0);
		}
		return tariff;
	}
	
	private TariffTable updateTarrifs(TariffTable tariffTable) {
		//Retrieving List of Old Tariffs
		List<Tariff> tariffTables = this.getTariffsByTariffTableId(tariffTable.getId());
		tariffTable = this.findTariffTableById(tariffTable.getId());
		if(tariffTables != null) {
			if(!tariffTables.isEmpty()) {
				for(Tariff tariff : tariffTables) {
					
					tariff.setTariffTable(tariffTable);
					
					tariff = getAdjustedTariff(tariffTable, tariff);
					
					if((tariff.getTariffTable().getTariffType().equals(TariffType.FIXED_AMOUNT) || 
							tariff.getTariffTable().getTariffType().equals(TariffType.PERCENTAGE_PLUS_LIMITS) ||
							tariff.getTariffTable().getTariffType().equals(TariffType.FIXED_PERCENTAGE)) &&
							tariffTables.size() > 1) {
						tariff.setValue(0);
						em.merge(tariff);
						this.deleteOtherTariffs(tariff);						
						break;
					
					}
					em.merge(tariff);
				}
			}
		}
			return tariffTable;
	}

	private void deleteOtherTariffs(Tariff tariff) {
		List<Tariff> tariffs = this.getTariffsByTariffTableId(tariff.getTariffTable().getId());
		if(tariffs != null) {
			if(!tariffs.isEmpty()) {
				for(Tariff t : tariffs) {
				
					if(!tariff.getId().equalsIgnoreCase(t.getId())) {
						t = em.merge(t);
						em.remove(t);
					}
				}
			}
		}
	}
	
	private TariffTable updateActiveTable(TariffTable tariffTable, TariffTable oldTable, boolean isOldIdNull) throws Exception{
        if(TariffStatus.ACTIVE.equals(oldTable.getStatus())) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>> Old Table is Active, Update now");
		    //Creating New table
		    tariffTable.setEffectiveDate(DateUtil.getBeginningOfDay(tariffTable.getEffectiveDate()));
			tariffTable.setEndDate(DateUtil.getEndOfDay(tariffTable.getEndDate()));
			tariffTable.setStatus(TariffStatus.AWAITING_APPROVAL);
		    tariffTable.setVersion(0);
		    tariffTable.setId(null);
		    if(isOldIdNull) {
		    	tariffTable.setOldTariffTableId(null);
		    } else {
		    	tariffTable.setOldTariffTableId(oldTable.getId());
		    }
		    tariffTable = this.createTariffTable(tariffTable);
		    
			tariffTable = this.findTariffTableById(tariffTable.getId());
			
			//Retrieving List of Old Tariffs
			if(oldTable.getId() == null) {
				oldTable.setId(oldTable.getOldTariffTableId());
			}
			List<Tariff> oldTariffs = this.getTariffsByTariffTableId(oldTable.getId());
			if(oldTariffs != null) {
				if(!oldTariffs.isEmpty()) {
					for(Tariff t : oldTariffs) {
						Tariff tariff = new Tariff();
						//Values to be populated in createTariff
						tariff.setId(null);
						tariff.setDateCreated(null);
						tariff.setTariffTable(tariffTable);
						tariff.setOldTariffId(tariff.getId());
						tariff.setLowerLimit(t.getLowerLimit());
						tariff.setUpperLimit(t.getUpperLimit());
						tariff.setValue(t.getValue());
						tariff.setValueType(t.getValueType());
						
						tariff = getAdjustedTariff(tariffTable, tariff);
						
						if((tariffTable.getTariffType().equals(TariffType.FIXED_AMOUNT) ||
								tariff.getTariffTable().getTariffType().equals(TariffType.PERCENTAGE_PLUS_LIMITS) ||
								tariffTable.getTariffType().equals(TariffType.FIXED_PERCENTAGE)) &&
								oldTariffs.size() > 1) {
							tariff.setValue(0);
							this.createTariff(tariff);
							break;
						
						}
						
						this.createTariff(tariff);
					}
				}
			}
			
		    }
		return tariffTable;
	}
	
	private TariffTable forceTableUpdate(TariffTable tariffTable) throws Exception {
		this.removeTarifftable(tariffTable);
		tariffTable.setVersion(0);
		return this.createTariffTable(tariffTable);
	}
	
	private void removeTarifftable(TariffTable tariffTable) throws Exception {
		tariffTable = this.findTariffTableById(tariffTable.getId());
		tariffTable = em.merge(tariffTable);
		em.remove(tariffTable);
	}
}
