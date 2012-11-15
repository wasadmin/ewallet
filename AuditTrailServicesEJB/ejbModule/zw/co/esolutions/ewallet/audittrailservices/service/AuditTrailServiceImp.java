package zw.co.esolutions.ewallet.audittrailservices.service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import zw.co.esolutions.ewallet.audit.AuditEvents;
import zw.co.esolutions.ewallet.audittrailservices.model.Activity;
import zw.co.esolutions.ewallet.audittrailservices.model.AuditTrail;
import zw.co.esolutions.ewallet.util.GenerateKey;
import zw.co.esolutions.ewallet.util.MapUtil;

/**
 * Session Bean implementation class AuditTrailServiceImp
 */
@Stateless
@WebService(endpointInterface="zw.co.esolutions.ewallet.audittrailservices.service.AuditTrailService", serviceName="AuditTrailService", portName="AuditTrailServiceSOAP")
public class AuditTrailServiceImp implements AuditTrailService {

	@PersistenceContext
	private EntityManager em;
    /**
     * Default constructor. 
     */
    public AuditTrailServiceImp() {
        super();
    }

    public AuditTrail logActivity(String username, String activityName, String entityID,
			String entityName, String oldObject, String newObject,
			String instanceName) throws Exception {
    	System.out.println("******** bla In Audit Trail Service *************");
		AuditTrail auditTrail = null;
		Activity activity = null;
		try{
			activity = this.getActivityByName(activityName);			
		}catch(Exception e){
			e.printStackTrace();			
		}
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>> retrieved Activity = "+activity+", Activity Name = "+activityName);
		//Creat new activity if it doese not exist
		if(activity==null){
			activity = new Activity();
			activity.setId(GenerateKey.generateEntityId());
			activity.setName(activityName);
			activity.setLogged(true);
			em.persist(activity);
		}
		if (activity.isLogged()) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>> Activity is logged");
			if(AuditEvents.RESET_MOBILE_PROFILE_PIN.equals(activityName)){
				auditTrail = logPinReset(entityID, entityName,	activity, username, instanceName,oldObject);
			}else{
				auditTrail = logChanges(newObject, oldObject, entityID, entityName,	activity, username, instanceName);
			}
		}
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Audit Trail = "+auditTrail);
		return auditTrail;
	}
    
    public AuditTrail logActivityWithNarrative(String username, String activityName, String narrative) throws Exception {
    	//System.out.println("******** bla In Audit Trail Service *************");
		AuditTrail auditTrail = null;
		Activity activity = null;
		try{
			activity = this.getActivityByName(activityName);			
		}catch(Exception e){
			e.printStackTrace();			
		}
		//Creat new activity if it doese not exist
		if(activity==null){
			activity = new Activity();
			activity.setId(GenerateKey.generateEntityId());
			activity.setName(activityName);
			activity.setLogged(true);
			em.persist(activity);
		}
		if (activity.isLogged()) {
			try {
				auditTrail = new AuditTrail();
				auditTrail.setAuditTrailID(GenerateKey.generateEntityId());
				auditTrail.setUsername(username.toUpperCase());
				auditTrail.setActivity(activity);
				auditTrail.setTime(new Timestamp(System.currentTimeMillis()));
				auditTrail.setNarrative(narrative.toUpperCase());
				em.persist(auditTrail);
				return auditTrail;
	    	}catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
		return auditTrail;
	}
    
    private AuditTrail logChanges(String newObject, String oldObject,
			String entityId, String entityName, Activity activity, String username,
			String instanceName) throws Exception {
/*    	System.out.println("newObject " + newObject);
    	System.out.println("oldObject " + oldObject);
    	System.out.println("entityId " + entityId);
    	System.out.println("entityName " + entityName);
    	System.out.println("activity " + activity);
    	System.out.println("username " + username);
    	System.out.println("instanceName " + instanceName);
*/
    	//Set the common properties for an audit trail object
    	AuditTrail auditTrail;
    	try {
			auditTrail = new AuditTrail();
			auditTrail.setAuditTrailID(GenerateKey.generateEntityId());
			auditTrail.setUsername(username.toUpperCase());
			auditTrail.setEntityName(entityName);
			auditTrail.setActivity(activity);
			auditTrail.setEntityName(entityName);
			auditTrail.setInstanceName(instanceName);
			auditTrail.setTime(new Timestamp(System.currentTimeMillis()));
			auditTrail.setEntityID(entityId);
			if (oldObject == null) {
				//If its a case of object creation
				Map<String, String> newObjectMap = MapUtil
						.convertAttributesStringToMap(newObject);
				String narrative = "";
				for (Object fieldName : newObjectMap.keySet()) {
					narrative=narrative+fieldName.toString() + " set to "+ newObjectMap.get(fieldName).toString() + ", ";
				}
				
				//System.out.println("##########"+narrative+"##########");
				auditTrail.setNarrative(narrative.toUpperCase());
			} else {
				//In the case of object update
				Map<String, String> newObjectMap = MapUtil
						.convertAttributesStringToMap(newObject);
				Map<String, String> oldObjectMap = MapUtil
						.convertAttributesStringToMap(oldObject);
				String narrative = "";
				for (Object fieldName : newObjectMap.keySet()) {
					String oldPropertyValue = oldObjectMap.get(fieldName)
							.toString();
					String newPropertyValue = newObjectMap.get(fieldName)
							.toString();
					if (!oldPropertyValue.equalsIgnoreCase(newPropertyValue)) {
						narrative=narrative+fieldName.toString() + " changed from "
								+ oldPropertyValue + " to " + newPropertyValue
								+ ", ";
					}
				}
				System.out.println("1 ##########"+oldObject+"##########");
				System.out.println("2 ##########"+newObject+"##########");
				System.out.println("3 ########## Narrative = "+narrative.length()+"##########");
				System.out.println("1 ########## Entity Name = "+entityName.length()+"##########");
				System.out.println("1 ########## User Name = "+username.length()+"##########");
				System.out.println("1 ##########"+oldObject+"##########");
				auditTrail.setNarrative(narrative.toUpperCase());
			}
			em.persist(auditTrail);
			return auditTrail;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
    }
        
    @SuppressWarnings("unchecked")
    public List<AuditTrail> getByEntityNameAndInstanceName(String entityName,String instanceName) throws Exception{
    	List<AuditTrail> audiList = null;
    	try{
    		Query query = em.createNamedQuery("getByEntityNameAndInstanceName");
    		query.setParameter("entityName", entityName);
    		query.setParameter("instanceName", instanceName);
    		//query.setMaxResults(MAX_RESULTS);
    		audiList = query.getResultList();
    	}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
    	return audiList;
    }
    
    @SuppressWarnings("unchecked")
    public List<AuditTrail> getByEntityNameAndInstanceNameAndTimePeriod(String entityName,String instanceName,Date startTime,Date endTime) throws Exception{
    	List<AuditTrail> audiList = null;
    	try{
    		//Set time to max and minimum
    		startTime = this.setMinTime(startTime);
    		endTime = this.setMaxTime(endTime);
    		
    		Query query = em.createNamedQuery("getByEntityNameAndInstanceNameAndTimePeriod");
    		query.setParameter("entityName", entityName);
    		query.setParameter("instanceName", instanceName);
    		query.setParameter("startTime", startTime);
    		query.setParameter("endTime", endTime);	
    		audiList = query.getResultList();
    	}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
    	return audiList;
    }
    
    @SuppressWarnings("unchecked")
    public List<AuditTrail> getByUsername(String username) throws Exception{
    	List<AuditTrail> audiList = null;
    	try{
    		Query query = em.createNamedQuery("getByUsername");
    		query.setParameter("username", username);
    		audiList = query.getResultList();
    		//query.setMaxResults(MAX_RESULTS);
    	}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
    	return audiList;
    }
    
    @SuppressWarnings("unchecked")
    public List<AuditTrail> getByUsernameAndTimePeriod(String username,Date startTime,Date endTime) throws Exception{
    	List<AuditTrail> audiList = null;
    	try{
    		//Set time to max and minimum
    		startTime = this.setMinTime(startTime);
    		endTime = this.setMaxTime(endTime);
    		
    		Query query = em.createNamedQuery("getByUsernameAndTimePeriod");
    		query.setParameter("username", username);
    		query.setParameter("startTime", startTime);
    		query.setParameter("endTime", endTime);
    		audiList = query.getResultList();
    	}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
    	return audiList;
    }
    
    @SuppressWarnings("unchecked")
    public List<AuditTrail> getByActivityAndTimePeriod(String activityId,Date startTime,Date endTime) throws Exception{
    	List<AuditTrail> audiList = null;
    	try{
    		//Set time to max and minimum
    		startTime = this.setMinTime(startTime);
    		endTime = this.setMaxTime(endTime);
    		
    		Query query = em.createNamedQuery("getByActivityAndTimePeriod");
    		query.setParameter("activityId", activityId);
    		query.setParameter("startTime", startTime);
    		query.setParameter("endTime", endTime);
    		audiList = query.getResultList();
    	}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
    	return audiList;
    }
    
    @Override
    @SuppressWarnings("unchecked")
	public List<AuditTrail> getByEntityNameAndEntityId(String entityName,
			String entityId) throws Exception {
    	List<AuditTrail> audiList = null;
    	try{
    		Query query = em.createNamedQuery("getByEntityNameAndEntityId");
    		query.setParameter("entityName", entityName);
    		query.setParameter("entityID", entityId);
    		//query.setMaxResults(MAX_RESULTS);
    		audiList = query.getResultList();
    	}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
    	return audiList;
	}

	@SuppressWarnings("unchecked")
    public List<AuditTrail> getByTimePeriod(Date startTime,Date endTime) throws Exception{
    	List<AuditTrail> audiList = null;
    	try{
    		//Set time to max and minimum
    		startTime = this.setMinTime(startTime);
    		endTime = this.setMaxTime(endTime);
    		
    		Query query = em.createNamedQuery("getByTimePeriod");
    		query.setParameter("startTime", startTime);
    		query.setParameter("endTime", endTime);
    		audiList = query.getResultList();
    	}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
    	return audiList;
    }
    
    public Activity getActivityByName(String name) throws Exception{
    	Activity activity = null;
    	try{
    		Query query = em.createNamedQuery("getActivityByName");
    		query.setParameter("name", name);
    		activity = (Activity)query.getSingleResult();
    	}catch(NoResultException ne){
    		
    	}catch(Exception e){
    		e.printStackTrace();
    		throw e;
    	}
    	return activity;
    }
    
    @SuppressWarnings("unchecked")
    public List<Activity> getAllActivities() throws Exception{
    	List<Activity> activityList = null;
    	try{
    		Query query = em.createNamedQuery("getActivity");
    		activityList = query.getResultList();
    	}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
    	return activityList;
    }
    
    public Activity findActivityById(String id) throws Exception{
    	Activity activity = null;
    	try{
    		activity = em.find(Activity.class, id);
    	}catch(Exception e){
    		e.printStackTrace();
    		throw e;
    	}
    	return activity;
    }

    public Activity editActivity(Activity activity,String username) throws Exception{
    	try{
    		String oldActivity = this.findActivityById(activity.getId()).getAuditableAttributesString();
    		activity = em.merge(activity);
    		this.logActivity(username, AuditEvents.EDIT_ACTIVITY, activity.getId(), activity.getEntityName(), oldActivity, activity.getAuditableAttributesString(), activity.getInstanceName());
    	}catch(Exception e){
    		e.printStackTrace();
    		throw e;
    	}
    	return activity;
    }
    
    private Date setMinTime(Date date){
    	Calendar c = Calendar.getInstance();
    	c.setTime(date);
    	c.set(Calendar.HOUR_OF_DAY, 0);
    	c.set(Calendar.MINUTE, 0);
    	c.set(Calendar.SECOND, 0);
    	return c.getTime();
    }
    
    private Date setMaxTime(Date date){
    	Calendar c = Calendar.getInstance();
    	c.setTime(date);
    	c.set(Calendar.HOUR_OF_DAY, 23);
    	c.set(Calendar.MINUTE, 59);
    	c.set(Calendar.SECOND, 59);
    	return c.getTime();
    }
    
    private AuditTrail logPinReset(String entityID, String entityName,Activity activity,String username,String instanceName,String narrative){
    	AuditTrail auditTrail = null;
		
		if (activity.isLogged()) {
			try {
				auditTrail = new AuditTrail();
				
				auditTrail.setAuditTrailID(GenerateKey.generateEntityId());
				auditTrail.setUsername(username.toUpperCase());
				auditTrail.setActivity(activity);
				auditTrail.setTime(new Timestamp(System.currentTimeMillis()));
				auditTrail.setNarrative(narrative.toUpperCase());
				auditTrail.setInstanceName(instanceName);
				auditTrail.setEntityID(entityID);
				auditTrail.setEntityName(entityName);
				
				em.persist(auditTrail);
				return auditTrail;
	    	}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return auditTrail;
    }
}
