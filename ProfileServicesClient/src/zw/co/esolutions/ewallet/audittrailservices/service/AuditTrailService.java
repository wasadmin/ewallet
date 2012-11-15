//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.audittrailservices.service;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

@WebService(name = "AuditTrailService", targetNamespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface AuditTrailService {


    /**
     * 
     * @param instanceName
     * @param oldObject
     * @param username
     * @param activityName
     * @param entityName
     * @param entityId
     * @param newObject
     * @return
     *     returns zw.co.esolutions.ewallet.audittrailservices.service.AuditTrail
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "logActivity", targetNamespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.audittrailservices.service.LogActivity")
    @ResponseWrapper(localName = "logActivityResponse", targetNamespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.audittrailservices.service.LogActivityResponse")
    public AuditTrail logActivity(
        @WebParam(name = "username", targetNamespace = "")
        String username,
        @WebParam(name = "activityName", targetNamespace = "")
        String activityName,
        @WebParam(name = "entityId", targetNamespace = "")
        String entityId,
        @WebParam(name = "entityName", targetNamespace = "")
        String entityName,
        @WebParam(name = "oldObject", targetNamespace = "")
        String oldObject,
        @WebParam(name = "newObject", targetNamespace = "")
        String newObject,
        @WebParam(name = "instanceName", targetNamespace = "")
        String instanceName)
        throws Exception_Exception
    ;

    /**
     * 
     * @param narrative
     * @param username
     * @param activityName
     * @return
     *     returns zw.co.esolutions.ewallet.audittrailservices.service.AuditTrail
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "logActivityWithNarrative", targetNamespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.audittrailservices.service.LogActivityWithNarrative")
    @ResponseWrapper(localName = "logActivityWithNarrativeResponse", targetNamespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.audittrailservices.service.LogActivityWithNarrativeResponse")
    public AuditTrail logActivityWithNarrative(
        @WebParam(name = "username", targetNamespace = "")
        String username,
        @WebParam(name = "activityName", targetNamespace = "")
        String activityName,
        @WebParam(name = "narrative", targetNamespace = "")
        String narrative)
        throws Exception_Exception
    ;

    /**
     * 
     * @param instanceName
     * @param entityName
     * @return
     *     returns java.util.List<zw.co.esolutions.ewallet.audittrailservices.service.AuditTrail>
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getByEntityNameAndInstanceName", targetNamespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.audittrailservices.service.GetByEntityNameAndInstanceName")
    @ResponseWrapper(localName = "getByEntityNameAndInstanceNameResponse", targetNamespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.audittrailservices.service.GetByEntityNameAndInstanceNameResponse")
    public List<AuditTrail> getByEntityNameAndInstanceName(
        @WebParam(name = "entityName", targetNamespace = "")
        String entityName,
        @WebParam(name = "instanceName", targetNamespace = "")
        String instanceName)
        throws Exception_Exception
    ;

    /**
     * 
     * @param entityId
     * @param entityName
     * @return
     *     returns java.util.List<zw.co.esolutions.ewallet.audittrailservices.service.AuditTrail>
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getByEntityNameAndEntityId", targetNamespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.audittrailservices.service.GetByEntityNameAndEntityId")
    @ResponseWrapper(localName = "getByEntityNameAndEntityIdResponse", targetNamespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.audittrailservices.service.GetByEntityNameAndEntityIdResponse")
    public List<AuditTrail> getByEntityNameAndEntityId(
        @WebParam(name = "entityName", targetNamespace = "")
        String entityName,
        @WebParam(name = "entityId", targetNamespace = "")
        String entityId)
        throws Exception_Exception
    ;

    /**
     * 
     * @param instanceName
     * @param endTime
     * @param entityName
     * @param startTime
     * @return
     *     returns java.util.List<zw.co.esolutions.ewallet.audittrailservices.service.AuditTrail>
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getByEntityNameAndInstanceNameAndTimePeriod", targetNamespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.audittrailservices.service.GetByEntityNameAndInstanceNameAndTimePeriod")
    @ResponseWrapper(localName = "getByEntityNameAndInstanceNameAndTimePeriodResponse", targetNamespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.audittrailservices.service.GetByEntityNameAndInstanceNameAndTimePeriodResponse")
    public List<AuditTrail> getByEntityNameAndInstanceNameAndTimePeriod(
        @WebParam(name = "entityName", targetNamespace = "")
        String entityName,
        @WebParam(name = "instanceName", targetNamespace = "")
        String instanceName,
        @WebParam(name = "startTime", targetNamespace = "")
        XMLGregorianCalendar startTime,
        @WebParam(name = "endTime", targetNamespace = "")
        XMLGregorianCalendar endTime)
        throws Exception_Exception
    ;

    /**
     * 
     * @param username
     * @return
     *     returns java.util.List<zw.co.esolutions.ewallet.audittrailservices.service.AuditTrail>
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getByUsername", targetNamespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.audittrailservices.service.GetByUsername")
    @ResponseWrapper(localName = "getByUsernameResponse", targetNamespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.audittrailservices.service.GetByUsernameResponse")
    public List<AuditTrail> getByUsername(
        @WebParam(name = "username", targetNamespace = "")
        String username)
        throws Exception_Exception
    ;

    /**
     * 
     * @param username
     * @param endTime
     * @param startTime
     * @return
     *     returns java.util.List<zw.co.esolutions.ewallet.audittrailservices.service.AuditTrail>
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getByUsernameAndTimePeriod", targetNamespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.audittrailservices.service.GetByUsernameAndTimePeriod")
    @ResponseWrapper(localName = "getByUsernameAndTimePeriodResponse", targetNamespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.audittrailservices.service.GetByUsernameAndTimePeriodResponse")
    public List<AuditTrail> getByUsernameAndTimePeriod(
        @WebParam(name = "username", targetNamespace = "")
        String username,
        @WebParam(name = "startTime", targetNamespace = "")
        XMLGregorianCalendar startTime,
        @WebParam(name = "endTime", targetNamespace = "")
        XMLGregorianCalendar endTime)
        throws Exception_Exception
    ;

    /**
     * 
     * @param endTime
     * @param activityId
     * @param startTime
     * @return
     *     returns java.util.List<zw.co.esolutions.ewallet.audittrailservices.service.AuditTrail>
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getByActivityAndTimePeriod", targetNamespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.audittrailservices.service.GetByActivityAndTimePeriod")
    @ResponseWrapper(localName = "getByActivityAndTimePeriodResponse", targetNamespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.audittrailservices.service.GetByActivityAndTimePeriodResponse")
    public List<AuditTrail> getByActivityAndTimePeriod(
        @WebParam(name = "activityId", targetNamespace = "")
        String activityId,
        @WebParam(name = "startTime", targetNamespace = "")
        XMLGregorianCalendar startTime,
        @WebParam(name = "endTime", targetNamespace = "")
        XMLGregorianCalendar endTime)
        throws Exception_Exception
    ;

    /**
     * 
     * @param endTime
     * @param startTime
     * @return
     *     returns java.util.List<zw.co.esolutions.ewallet.audittrailservices.service.AuditTrail>
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getByTimePeriod", targetNamespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.audittrailservices.service.GetByTimePeriod")
    @ResponseWrapper(localName = "getByTimePeriodResponse", targetNamespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.audittrailservices.service.GetByTimePeriodResponse")
    public List<AuditTrail> getByTimePeriod(
        @WebParam(name = "startTime", targetNamespace = "")
        XMLGregorianCalendar startTime,
        @WebParam(name = "endTime", targetNamespace = "")
        XMLGregorianCalendar endTime)
        throws Exception_Exception
    ;

    /**
     * 
     * @param id
     * @return
     *     returns zw.co.esolutions.ewallet.audittrailservices.service.Activity
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "findActivityById", targetNamespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.audittrailservices.service.FindActivityById")
    @ResponseWrapper(localName = "findActivityByIdResponse", targetNamespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.audittrailservices.service.FindActivityByIdResponse")
    public Activity findActivityById(
        @WebParam(name = "id", targetNamespace = "")
        String id)
        throws Exception_Exception
    ;

    /**
     * 
     * @param username
     * @param activity
     * @return
     *     returns zw.co.esolutions.ewallet.audittrailservices.service.Activity
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "editActivity", targetNamespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.audittrailservices.service.EditActivity")
    @ResponseWrapper(localName = "editActivityResponse", targetNamespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.audittrailservices.service.EditActivityResponse")
    public Activity editActivity(
        @WebParam(name = "activity", targetNamespace = "")
        Activity activity,
        @WebParam(name = "username", targetNamespace = "")
        String username)
        throws Exception_Exception
    ;

    /**
     * 
     * @return
     *     returns java.util.List<zw.co.esolutions.ewallet.audittrailservices.service.Activity>
     * @throws Exception_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getAllActivities", targetNamespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.audittrailservices.service.GetAllActivities")
    @ResponseWrapper(localName = "getAllActivitiesResponse", targetNamespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", className = "zw.co.esolutions.ewallet.audittrailservices.service.GetAllActivitiesResponse")
    public List<Activity> getAllActivities()
        throws Exception_Exception
    ;

}
