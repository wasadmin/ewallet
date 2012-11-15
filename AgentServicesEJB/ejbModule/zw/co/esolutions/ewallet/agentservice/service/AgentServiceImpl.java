package zw.co.esolutions.ewallet.agentservice.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import zw.co.esolutions.ewallet.agentservice.model.Agent;
import zw.co.esolutions.ewallet.agentservice.model.AgentClass;
import zw.co.esolutions.ewallet.agentservice.model.AgentNumber;
import zw.co.esolutions.ewallet.audit.AuditEvents;
import zw.co.esolutions.ewallet.audittrailservices.service.AuditTrailServiceSOAPProxy;
import zw.co.esolutions.ewallet.audittrailservices.service.Exception_Exception;
import zw.co.esolutions.ewallet.enums.AgentClassStatus;
import zw.co.esolutions.ewallet.enums.AgentLevel;
import zw.co.esolutions.ewallet.enums.AgentType;
import zw.co.esolutions.ewallet.enums.ProfileStatus;
import zw.co.esolutions.ewallet.util.GenerateKey;

/**
 * Session Bean implementation class AgentServiceImpl
 */
@Stateless
@WebService(endpointInterface = "zw.co.esolutions.ewallet.agentservice.service.AgentService",serviceName = "AgentService", portName = "AgentServiceSOAP")
public class AgentServiceImpl implements AgentService {
	@PersistenceContext
	private EntityManager em;
	public static final String AGENT_NUMBER = "AGENT_NUMBER";
    /**
     * Default constructor. 
     */
    public AgentServiceImpl() {
        // TODO Auto-generated constructor stub
    }
    
    public Agent createAgent(Agent agent ,String username) throws Exception{
    	
    	if(agent== null){
    		throw new Exception("agent cannot be null");
    	}else{
	    	try{
	    		
	    		if(agent.getId()== null){
	    			agent.setId(GenerateKey.generateEntityId());
	    		}
//		    	agent.setStatus(ProfileStatus.AWAITING_APPROVAL);
		    	agent.setDateCreated(new Date());
		    	if(agent.getAgentLevel().equals(AgentLevel.CORPORATE)){
		    		agent.setFieldsToUpperCase();
		    	}
		    	
	    		em.persist(agent);
	    		
	    		//Audit Trail
				AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
				auditService.logActivity(username, AuditEvents.CREATE_AGENT, agent.getId(), agent.getEntityName(), null, agent.getAuditableAttributesString(), agent.getInstanceName());
	    	}catch(Exception e){
	    		e.printStackTrace();
	    		throw new Exception();
	    	}
	    	
    	return agent;
    	}
    }
    
//    @Interceptors(LoggingInterceptor.class)
    public Agent updateAgent(Agent agent , String username) throws Exception{
    	String oldAgent = this.findAgentById(agent.getId()).getAuditableAttributesString();	
    	agent = update(agent);  	
    	//Audit Trail
		try {
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(username, AuditEvents.UPDATE_AGENT, agent.getId(), agent.getEntityName(), oldAgent, agent.getAuditableAttributesString(), agent.getInstanceName());
		} catch (Exception_Exception e) {
			e.printStackTrace();
			throw new Exception();
		}
    	return agent;
    }
    
    public String deleteAgent(Agent agent , String username) throws Exception{
    	try {
    		String oldAgent = this.findAgentById(agent.getId()).getAuditableAttributesString();
	    	agent.setStatus(ProfileStatus.DELETED);
	//    	em.remove(agent);
	    	agent = update(agent);
    	
    	//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(username, AuditEvents.DELETE_AGENT, agent.getId(), agent.getEntityName(), oldAgent, agent.getAuditableAttributesString(), agent.getInstanceName());
		} catch (Exception_Exception e) {
			e.printStackTrace();
			throw new Exception();
		}
    	return "success";
    }
    
    public Agent approveAgent(Agent agent , String username)throws Exception{
    	try{
    		String oldAgent = this.findAgentById(agent.getId()).getAuditableAttributesString();
    		agent.setStatus(ProfileStatus.ACTIVE);
    		agent = update(agent);
    		//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(username, AuditEvents.APPROVE_AGENT, agent.getId(), agent.getEntityName(), oldAgent, agent.getAuditableAttributesString(), agent.getInstanceName());
		} catch (Exception_Exception e) {
			e.printStackTrace();
			throw new Exception();
		}
    	return agent;
    }
    
    public Agent rejectAgent(Agent agent, String username)throws Exception{
    	try{
    		String oldAgent = this.findAgentById(agent.getId()).getAuditableAttributesString();
    		agent.setStatus(ProfileStatus.DISAPPROVED);
    		agent = update(agent);
    	
    		//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(username, AuditEvents.REJECT_AGENT, agent.getId(), agent.getEntityName(), oldAgent, agent.getAuditableAttributesString(), agent.getInstanceName());
		} catch (Exception_Exception e) {
			e.printStackTrace();
			throw new Exception();
		}
    	return agent;
    }
    
    public Agent findAgentById(String id){
    	Agent agent = null;
    	try{
    		agent = em.find(Agent.class, id);
    	}catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return agent;
    }
    
    @SuppressWarnings("unchecked")
	public List<Agent> getAgentByName(String firstname , String lastname){
    	List<Agent> results = null;
    	try{
    		Query q = em.createNamedQuery("getAgentByName");
        	q.setParameter("firstname", firstname);
        	q.setParameter("lastname", lastname);
        	results = (List<Agent>)q.getResultList();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	return results;
    }
    
	public Agent getAgentByAgentNumber(String agentNumber){
    	Agent result = null;
    	try{
    		Query q = em.createNamedQuery("getAgentByAgentNumber");
        	q.setParameter("agentNumber", agentNumber);
        	result = (Agent)q.getSingleResult();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @SuppressWarnings("unchecked")
	public List<Agent> getAgentByStatus(String status){
    	
    	List<Agent> tmpResults = null;
    	try{
    		ProfileStatus param = ProfileStatus.valueOf(status);
        	Query q = em.createNamedQuery("getAgentByStatus");
        	q.setParameter("status",param);
        	tmpResults = q.getResultList();
    	}catch(Exception e){
    		e.printStackTrace();
    	} 	
    	return tmpResults;
    }
    
	public Agent getAgentByNationalId(String nationalId){
    	Agent result = null;
    	try{
    		Query q = em.createNamedQuery("getAgentByNationalId");
        	q.setParameter("nationalId", nationalId);
        	result = (Agent)q.getSingleResult();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    public Agent getAgentByCustomerId(String customerId){
    	
    	Agent results = null;
    	try{
    		Query q = em.createNamedQuery("getAgentByCustomerId");
        	q.setParameter("customerId", customerId);
        	results = (Agent)q.getSingleResult();
    	}catch (Exception e) {
			e.printStackTrace();
		}
    
    	return results;
    }
    
    @SuppressWarnings("unchecked")
	public List<Agent> getAllAgents(){
    	List<Agent>tmpResults = null;
    	try{
    		Query q = em.createNamedQuery("getAllAgents");
    		tmpResults = q.getResultList();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return filterAgents(tmpResults);
    }
    
    @SuppressWarnings("unchecked")
	public List<Agent> getAgentByAgentType(String agentType){
    	List<Agent> results = null;
    	AgentType param = AgentType.valueOf(agentType);
    	try{
    		Query q = em.createNamedQuery("getAgentByAgentType");
        	q.setParameter("agentType", param);
        	results = q.getResultList();

    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return filterAgents(results);
    }
    
    @SuppressWarnings("unchecked")
	public List<Agent> getAgentByBankId(String bankId){
    	List<Agent> results = null;
    	try{
    		Query q = em.createNamedQuery("getAgentByBankId");
        	q.setParameter("bankId", bankId);
        	results = q.getResultList();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	return results;
    }
    
    @SuppressWarnings("unchecked")
	public List<Agent> getAgentByLastName(String lastName){
    	List<Agent> tmpResults = null;
    	try{
    		Query q = em.createNamedQuery("getAgentByLastName");
        	q.setParameter("lastName", lastName);
        	tmpResults = q.getResultList();
    	}catch (Exception e) {
			e.printStackTrace();
		}
    	return filterAgents(tmpResults);
    }
    
    @SuppressWarnings("unchecked")
	public List<Agent> getSubAgentBySuperAgentId(String superAgentId){
    	List<Agent> results = null;
    	try{
    		Query q = em.createNamedQuery("getSubAgentBySuperAgentId");
        	q.setParameter("superAgentId", superAgentId);
        	results = q.getResultList();
    	}catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return results;
    }
    
	public Agent getSubAgentByNationalId(String superAgentId , String nationalId){
    	Agent result = null;
    	try{
    		Query q = em.createNamedQuery("getSubAgentByNationalId");
        	q.setParameter("superAgentId", superAgentId);
        	q.setParameter("nationalId", nationalId);
        	result = (Agent)q.getSingleResult();
    	}catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return result;
    }
    
    @SuppressWarnings("unchecked")
	public List<Agent> getSubAgentByStatus(String superAgentId ,String status){
    	List<Agent> results = null;
    	ProfileStatus param = ProfileStatus.valueOf(status);
    	try{
    		Query q = em.createNamedQuery("getSubAgentByStatus");
        	q.setParameter("superAgentId", superAgentId);
        	q.setParameter("status",param);
        	
        	results = q.getResultList();
    	}catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return results;
    }
    
	public Agent getSubAgentByAgentNumber(String superAgentId , String agentNumber){
    	Agent results = null;
//    	Query q = em.createNamedQuery("getSubAgentByLastName");
//    	q.setParameter("superAgentId", superAgentId);
//    	q.setParameter("agentNumber", agentNumber);
//    	results = (Agent)q.getSingleResult();
    	return results;
    }
    
    public Agent getAgentByMobileNumber(String mobileNumber){
    	Agent agent = null;
    	try{
    		Query q = em.createNamedQuery("getAgentByMobileNumber");
        	q.setParameter("mobileNumber", mobileNumber);
        	agent = (Agent)q.getSingleResult();
    	}catch (Exception e) {
			e.printStackTrace();
		} 	
    	return agent;
    }
    
	public Agent getAgentByProfileId(String profileId){
    	Agent result = null;
    	try{
    		Query q = em.createNamedQuery("getAgentByProfileId");
        	q.setParameter("profileId", profileId);
        	result = (Agent)q.getSingleResult();
    	}catch(Exception e){
    		e.printStackTrace();
    	}    	
    	return result;
    }
    
    private Agent update(Agent agent) throws Exception{
    	agent.setFieldsToUpperCase();
    	try{
    		agent = em.merge(agent);
    	}catch(Exception e){
    		e.printStackTrace();
    		throw new Exception();
    	}
    	return agent;
    }
    
//    private String generateAgentNumber(Agent agent){
//    	//Implementation of AgentNumber
//    	String agentNumber = agent.getDateOfBirth().toString()+"-"+agent.getMobileNumber();
//    	String agentNumber = "AGNT-";//+agent.getMobileNumber();
//    	return agentNumber;
//    }

	@SuppressWarnings("unchecked")
	@Override
	public List<Agent> getAllSubAgents(String superAgentId) {
		List<Agent> results = null;
		try{
			Query q = em.createNamedQuery("getAllSubAgents");
			q.setParameter("superAgentId", superAgentId);
			results = (List<Agent>)q.getResultList(); 
		}catch(Exception e){
			e.printStackTrace();
		}	
		return results;
	}

	@Override
	public Agent approveDeregistration(String agentId, String username) throws Exception{
		Agent agent = findAgentById(agentId);
		try{
			String oldAgent = this.findAgentById(agent.getId()).getAuditableAttributesString();
			agent = update(agent);
		
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(username, AuditEvents.APPROVE_AGENT_DEREGEISTRATION, agent.getId(), agent.getEntityName(), oldAgent, agent.getAuditableAttributesString(), agent.getInstanceName());
		} catch (Exception_Exception e) {
			e.printStackTrace();
			throw new Exception();
		}
		
		return agent;
	}

	@Override
	public Agent deregisterAgent(String agentId , String username) throws Exception{
		Agent agent = findAgentById(agentId);
		try{
			String oldAgent = this.findAgentById(agent.getId()).getAuditableAttributesString();
			agent.setStatus(ProfileStatus.DELETED);
			agent = update(agent);
		
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(username, AuditEvents.DEREGISTER_AGENT, agent.getId(), agent.getEntityName(), oldAgent, agent.getAuditableAttributesString(), agent.getInstanceName());
		} catch (Exception_Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return agent;
	}

//	@Override
	public Agent cancelDeregistration(String agentId , String username) throws Exception{
		Agent agent = findAgentById(agentId);
		try{
			String oldAgent = this.findAgentById(agent.getId()).getAuditableAttributesString();
			agent = update(agent);
		
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(username, AuditEvents.CANCEL_DEREGISTRATION, agent.getId(), agent.getEntityName(), oldAgent, agent.getAuditableAttributesString(), agent.getInstanceName());
		} catch (Exception_Exception e) {
			e.printStackTrace();
			throw new Exception();
		}
		return agent;
	}
	
	private List<Agent>filterAgents(List<Agent> tmpResults){
		
		List<Agent> results = new ArrayList<Agent>();
		try{
			for(Agent agent: tmpResults){
//	    		if(agent.getAgentType().equals(AgentType.SUB_AGENT)||agent.getStatus().equals(ProfileStatus.DELETED)){
				if(agent.getStatus().equals(ProfileStatus.DELETED)|| agent.getStatus().equals(ProfileStatus.DISAPPROVED)
						|| agent.getStatus().equals(ProfileStatus.AWAITING_EDIT_APPROVAL)){
	    			continue;
	    		}
	    		results.add(agent);
	    	}
		}catch (Exception e) {
			e.printStackTrace();
		}
				
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<AgentClass> getAllAgentClasses() {
		List<AgentClass> tmpResults = null;
		try{
			Query q = em.createNamedQuery("getAllAgentClasses");
			tmpResults = q.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
		}

		return filterAgentClass(tmpResults);
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Agent> getAgentsByAgentClass(String className) {
		List<Agent> tmpResults = null;
		try{
			AgentClass agentClass = getAgentClassByName(className);
			Query q = em.createNamedQuery("getAgentsByAgentClass");
			q.setParameter("agentClassId", agentClass.getId());
			tmpResults = (List<Agent>)q.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return filterAgents(tmpResults);
		
	}

	public AgentClass createAgentClass(AgentClass agentClass, String username) throws Exception{
		
		try{
			System.out.println("Running the create Agent class method");
			agentClass.setFieldsToUpperCase();
			agentClass.setId(GenerateKey.generateEntityId());
			agentClass.setStatus(AgentClassStatus.AWAITING_APPROVAL);
			em.persist(agentClass);
			System.out.println("The persisisted value is "+agentClass.getId());
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(username, AuditEvents.CREATE_AGENTCLASS, agentClass.getId(), agentClass.getEntityName(), null, agentClass.getAuditableAttributesString(), agentClass.getInstanceName());
		}catch(Exception e){
			System.out.println("Exception occured >>>");
			e.printStackTrace();
			throw new Exception();
		}
		return agentClass;
		
	}

	public AgentClass deleteAgentClass(String agentClassId, String username) throws Exception{
		AgentClass agentClass = em.find(AgentClass.class, agentClassId);
		String oldClass = agentClass.getAuditableAttributesString();
		try{
			agentClass.setStatus(AgentClassStatus.DELETED);
			agentClass = updateClass(agentClass);
			
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(username, AuditEvents.DELETE_AGENTCLASS, agentClass.getId(), agentClass.getEntityName(),oldClass, agentClass.getAuditableAttributesString(), agentClass.getInstanceName());
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception();
		}
		return agentClass;
		
	}

	public AgentClass updateAgentClass(AgentClass agentClass, String username) throws Exception{
		try{
			agentClass = updateClass(agentClass);
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception();
		}
		return null;
		
	}

	public AgentClass findAgentClassById(String agentClassId) {
		AgentClass agentClass = em.find(AgentClass.class, agentClassId);
		return agentClass;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<AgentClass> getAgentClassByStatus(String status){
		List<AgentClass> results = null;
		try{
			AgentClassStatus param = AgentClassStatus.valueOf(status);
			Query q = em.createNamedQuery("getAgentClassByStatus");
			q.setParameter("status",param);
			results = q.getResultList();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return results;
	}
	@Override
	public AgentClass getAgentClassByName(String className){
		AgentClass result = null;
		try{
			Query q = em.createNamedQuery("getAgentClassByName");
			q.setParameter("className", className);
			result = (AgentClass) q.getSingleResult();
		}catch(Exception e){
			e.printStackTrace();
		}	
		return result;
	}
	
	//Method for updating agentClasses 
	private AgentClass updateClass(AgentClass agentClass) throws Exception{
		System.out.println("##############################33 "+agentClass.getName());
		agentClass.setFieldsToUpperCase();
		try{
			agentClass = em.merge(agentClass);
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception();
		}		
		return agentClass;
	}
	
	private List<AgentClass>filterAgentClass(List<AgentClass> tmpResults){
		
		List<AgentClass> results = new ArrayList<AgentClass>();
		try{
			for(AgentClass agentClass: tmpResults){
	    		if(agentClass.getStatus().equals(AgentClassStatus.DELETED)){
	    			continue;
	    		}
	    		results.add(agentClass);
	    	}
		}catch (Exception e) {
			e.printStackTrace();
		}	
		return results;
	}

	public AgentClass approveAgentClass(AgentClass agentClass, String username)
			throws Exception {
		try{
			String oldClass = this.findAgentClassById(agentClass.getId()).getAuditableAttributesString();
			agentClass.setStatus(AgentClassStatus.ACTIVE);
			agentClass = updateAgentClass(agentClass, username);
			
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(username, AuditEvents.APPROVE_AGENTCLASS, agentClass.getId(), agentClass.getEntityName(),oldClass, agentClass.getAuditableAttributesString(), agentClass.getInstanceName());
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return agentClass;
		
	}

	public AgentClass rejectAgentClass(AgentClass agentClass, String username)
			throws Exception {
		try{
			String oldClass = this.findAgentClassById(agentClass.getId()).getAuditableAttributesString();
			
			agentClass.setStatus(AgentClassStatus.DISAPPROVED);
			agentClass=updateAgentClass(agentClass, username);
			
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(username, AuditEvents.DELETE_AGENTCLASS, agentClass.getId(), agentClass.getEntityName(),oldClass, agentClass.getAuditableAttributesString(), agentClass.getInstanceName());
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return agentClass;
		
	}

	@SuppressWarnings("unchecked")
	public List<Agent> getAgentByLevel(String agentLevel) {
		List<Agent> results = null;
		try{
			AgentLevel param = AgentLevel.valueOf(agentLevel);
			Query q = em.createNamedQuery("getAgentByLevel");	
			q.setParameter("agentLevel", param);
			results = (List<Agent>)q.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return results;
		
	}

	public String generateAgentNumber() throws Exception {
		AgentNumber am = null;
		long num = 1;
		try{
			am = em.find(AgentNumber.class, AGENT_NUMBER);
			if(am == null){
				am = new AgentNumber();
				am.setDateCreated(new Date());
				am.setId(AGENT_NUMBER);
				am.setPrefix("6");
				am.setNumber(num);
				em.persist(am);
			}else{
				num = genNumber(am.getNumber());
				am.setNumber(num);
				em.merge(am);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	
		return format(am.getPrefix(),num);
	}
	
	private long genNumber(long number) throws Exception{
		if(number < 99999){
			number = number + 1;
		}else{
			throw new Exception("Number sequence exhausted !!!");
		}
		 
		return number;
		
	}
	
	private String format(String prefix , long num){
		String number = new Long(num).toString();
		String prepend = "";
		
		for(int i = 0; i < (5 -number.length()); i++){
			prepend = prepend + "0";
		}
		number = prepend+num;
		return prefix+number;
	}

}
