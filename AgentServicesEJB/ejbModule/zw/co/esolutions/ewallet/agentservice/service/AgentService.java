package zw.co.esolutions.ewallet.agentservice.service;

import javax.jws.WebService;
import javax.jws.WebParam;

import zw.co.esolutions.ewallet.agentservice.model.Agent;
import zw.co.esolutions.ewallet.agentservice.model.AgentClass;

import java.util.List;

@WebService(name = "AgentService")
public interface AgentService {
	
	Agent updateAgent(@WebParam(name="agent")Agent agent, @WebParam(name="username")String username) throws Exception;

	String deleteAgent( @WebParam(name="agent")Agent agent, @WebParam(name="username") String username) throws Exception;

	Agent findAgentById( @WebParam(name="id")String id);

	Agent createAgent( @WebParam(name="agent")Agent agent, @WebParam(name="username") String username) throws Exception;

	List<Agent> getAgentByName( @WebParam(name="firstname")String firstname, @WebParam(name="lastName") String lastName);

	Agent getAgentByAgentNumber( @WebParam(name="agentNumber")String agentNumber);

	List<Agent> getAgentByStatus( @WebParam(name="status")String status);
	
	List<Agent> getAgentByLevel( @WebParam(name="agentLevel")String agentLevel);

	Agent getAgentByNationalId( @WebParam(name="nationalId")String nationalId);

	List<Agent> getAllAgents();

	List<Agent> getAgentByAgentType( @WebParam(name="agentType")String agentType);

	List<Agent> getAgentByBankId( @WebParam(name="bankId")String bankId);

	List<Agent> getAgentByLastName( @WebParam(name="lastName")String lastName);

	List<Agent> getSubAgentBySuperAgentId( @WebParam(name="superAgentId")String superAgentId);

	Agent getSubAgentByNationalId( @WebParam(name="superAgentId")String superAgentId,@WebParam(name="nationalId") String nationalId);

	List<Agent> getSubAgentByStatus( @WebParam(name="superAgentId")String superAgentId,  @WebParam(name="Status")String status);

	Agent getSubAgentByAgentNumber( @WebParam(name="superAgentId")String superAgentId, @WebParam(name="agentNumber") String agentNumber);

	Agent getAgentByMobileNumber(@WebParam(name="mobileNumber")String mobileNumber);

	Agent getAgentByProfileId(@WebParam(name="profileId")String profileId);

	Agent approveAgent(@WebParam(name="agent")Agent agent,@WebParam(name="username") String username) throws Exception;

	Agent rejectAgent(@WebParam(name="agent")Agent agent, @WebParam(name="username")String username) throws Exception;	
	
	List<Agent> getAllSubAgents(@WebParam(name="superAgentId") String superAgentId);
	
	Agent deregisterAgent(@WebParam(name="agentId") String agentId , @WebParam(name ="username")String username) throws Exception;
	
	Agent approveDeregistration(@WebParam(name="agentId") String agentId , @WebParam(name ="username")String username) throws Exception;
	
	Agent cancelDeregistration(@WebParam(name="agentId")String agentId , @WebParam(name ="username")String username) throws Exception;
	
	AgentClass createAgentClass(@WebParam(name="agentClass")AgentClass agentClass, @WebParam(name="username")String username) throws Exception;
	
	AgentClass updateAgentClass(@WebParam(name="agentClass")AgentClass agentClass, @WebParam(name="username")String username)throws Exception;
	
	AgentClass deleteAgentClass(@WebParam(name="agentClassId")String agentClassId, @WebParam(name="username")String username) throws Exception;
	
	AgentClass findAgentClassById(@WebParam(name="agentClassId")String agentClassId);
	
	List<AgentClass> getAllAgentClasses();
	
	List<Agent> getAgentsByAgentClass(@WebParam(name="agentClassId")String className);
	
	AgentClass getAgentClassByName(@WebParam(name="className")String className);
	
	List<AgentClass> getAgentClassByStatus(@WebParam(name="status")String status);
	
	AgentClass approveAgentClass(@WebParam(name="agentClass")AgentClass agentClass,@WebParam(name="username") String username) throws Exception;
	
	AgentClass rejectAgentClass(@WebParam(name="agentClass")AgentClass agentClass, @WebParam(name="username")String username) throws Exception;
	
	Agent getAgentByCustomerId(@WebParam(name="customerId")String customerId) throws Exception;
	
	String generateAgentNumber() throws Exception;
}
