package zw.co.esolutions.ewallet.audittrailservices.service;
import java.util.Date;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;
import zw.co.esolutions.ewallet.audittrailservices.model.Activity;
import zw.co.esolutions.ewallet.audittrailservices.model.AuditTrail;

@WebService(name = "AuditTrailService")
public interface AuditTrailService {

	AuditTrail logActivity(@WebParam(name="username")String username, @WebParam(name="activityName")String activityName, @WebParam(name="entityId")String entityID,
			@WebParam(name="entityName")String entityName, @WebParam(name="oldObject")String oldObject, @WebParam(name="newObject")String newObject,
			@WebParam(name="instanceName")String instanceName) throws Exception;
	
	AuditTrail logActivityWithNarrative(@WebParam(name="username")String username, @WebParam(name="activityName")String activityName, @WebParam(name="narrative")String narrative) throws Exception;

	List<AuditTrail> getByEntityNameAndInstanceName(@WebParam(name="entityName")String entityName,
			@WebParam(name="instanceName")String instanceName) throws Exception;

	List<AuditTrail> getByEntityNameAndEntityId(@WebParam(name="entityName")String entityName,
			@WebParam(name="entityId")String entityId) throws Exception;
	
	List<AuditTrail> getByEntityNameAndInstanceNameAndTimePeriod(
			@WebParam(name="entityName")String entityName, @WebParam(name="instanceName")String instanceName, @WebParam(name="startTime")Date startTime, @WebParam(name="endTime")Date endTime)
			throws Exception;

	List<AuditTrail> getByUsername(@WebParam(name="username")String username) throws Exception;

	List<AuditTrail> getByUsernameAndTimePeriod(@WebParam(name="username")String username,
			@WebParam(name="startTime")Date startTime, @WebParam(name="endTime")Date endTime) throws Exception;
	
	List<AuditTrail> getByActivityAndTimePeriod(@WebParam(name="activityId")String activityId,
			@WebParam(name="startTime")Date startTime, @WebParam(name="endTime")Date endTime) throws Exception;

	List<AuditTrail> getByTimePeriod(@WebParam(name="startTime")Date startTime, @WebParam(name="endTime")Date endTime)
			throws Exception;

	Activity findActivityById(@WebParam(name="id")String id) throws Exception;

	Activity editActivity(@WebParam(name="activity")Activity activity, @WebParam(name="username")String username) throws Exception;

	List<Activity> getAllActivities() throws Exception;
}
