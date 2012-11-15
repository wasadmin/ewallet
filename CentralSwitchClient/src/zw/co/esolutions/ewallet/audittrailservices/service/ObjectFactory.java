//
// Generated By:JAX-WS RI IBM 2.1.1 in JDK 6 (JAXB RI IBM JAXB 2.1.3 in JDK 1.6)
//


package zw.co.esolutions.ewallet.audittrailservices.service;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the zw.co.esolutions.ewallet.audittrailservices.service package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetByEntityNameAndInstanceNameResponse_QNAME = new QName("http://service.audittrailservices.ewallet.esolutions.co.zw/", "getByEntityNameAndInstanceNameResponse");
    private final static QName _GetByTimePeriod_QNAME = new QName("http://service.audittrailservices.ewallet.esolutions.co.zw/", "getByTimePeriod");
    private final static QName _EditActivityResponse_QNAME = new QName("http://service.audittrailservices.ewallet.esolutions.co.zw/", "editActivityResponse");
    private final static QName _FindActivityById_QNAME = new QName("http://service.audittrailservices.ewallet.esolutions.co.zw/", "findActivityById");
    private final static QName _GetByEntityNameAndEntityId_QNAME = new QName("http://service.audittrailservices.ewallet.esolutions.co.zw/", "getByEntityNameAndEntityId");
    private final static QName _LogActivityWithNarrative_QNAME = new QName("http://service.audittrailservices.ewallet.esolutions.co.zw/", "logActivityWithNarrative");
    private final static QName _GetAllActivitiesResponse_QNAME = new QName("http://service.audittrailservices.ewallet.esolutions.co.zw/", "getAllActivitiesResponse");
    private final static QName _LogActivityResponse_QNAME = new QName("http://service.audittrailservices.ewallet.esolutions.co.zw/", "logActivityResponse");
    private final static QName _GetByEntityNameAndInstanceNameAndTimePeriod_QNAME = new QName("http://service.audittrailservices.ewallet.esolutions.co.zw/", "getByEntityNameAndInstanceNameAndTimePeriod");
    private final static QName _GetByActivityAndTimePeriod_QNAME = new QName("http://service.audittrailservices.ewallet.esolutions.co.zw/", "getByActivityAndTimePeriod");
    private final static QName _GetByUsernameAndTimePeriodResponse_QNAME = new QName("http://service.audittrailservices.ewallet.esolutions.co.zw/", "getByUsernameAndTimePeriodResponse");
    private final static QName _GetByUsername_QNAME = new QName("http://service.audittrailservices.ewallet.esolutions.co.zw/", "getByUsername");
    private final static QName _GetByTimePeriodResponse_QNAME = new QName("http://service.audittrailservices.ewallet.esolutions.co.zw/", "getByTimePeriodResponse");
    private final static QName _GetByEntityNameAndInstanceName_QNAME = new QName("http://service.audittrailservices.ewallet.esolutions.co.zw/", "getByEntityNameAndInstanceName");
    private final static QName _GetByEntityNameAndEntityIdResponse_QNAME = new QName("http://service.audittrailservices.ewallet.esolutions.co.zw/", "getByEntityNameAndEntityIdResponse");
    private final static QName _FindActivityByIdResponse_QNAME = new QName("http://service.audittrailservices.ewallet.esolutions.co.zw/", "findActivityByIdResponse");
    private final static QName _LogActivityWithNarrativeResponse_QNAME = new QName("http://service.audittrailservices.ewallet.esolutions.co.zw/", "logActivityWithNarrativeResponse");
    private final static QName _EditActivity_QNAME = new QName("http://service.audittrailservices.ewallet.esolutions.co.zw/", "editActivity");
    private final static QName _GetAllActivities_QNAME = new QName("http://service.audittrailservices.ewallet.esolutions.co.zw/", "getAllActivities");
    private final static QName _LogActivity_QNAME = new QName("http://service.audittrailservices.ewallet.esolutions.co.zw/", "logActivity");
    private final static QName _Exception_QNAME = new QName("http://service.audittrailservices.ewallet.esolutions.co.zw/", "Exception");
    private final static QName _GetByActivityAndTimePeriodResponse_QNAME = new QName("http://service.audittrailservices.ewallet.esolutions.co.zw/", "getByActivityAndTimePeriodResponse");
    private final static QName _GetByEntityNameAndInstanceNameAndTimePeriodResponse_QNAME = new QName("http://service.audittrailservices.ewallet.esolutions.co.zw/", "getByEntityNameAndInstanceNameAndTimePeriodResponse");
    private final static QName _GetByUsernameResponse_QNAME = new QName("http://service.audittrailservices.ewallet.esolutions.co.zw/", "getByUsernameResponse");
    private final static QName _GetByUsernameAndTimePeriod_QNAME = new QName("http://service.audittrailservices.ewallet.esolutions.co.zw/", "getByUsernameAndTimePeriod");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: zw.co.esolutions.ewallet.audittrailservices.service
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetByUsernameAndTimePeriod }
     * 
     */
    public GetByUsernameAndTimePeriod createGetByUsernameAndTimePeriod() {
        return new GetByUsernameAndTimePeriod();
    }

    /**
     * Create an instance of {@link Activity }
     * 
     */
    public Activity createActivity() {
        return new Activity();
    }

    /**
     * Create an instance of {@link GetByEntityNameAndInstanceNameAndTimePeriod }
     * 
     */
    public GetByEntityNameAndInstanceNameAndTimePeriod createGetByEntityNameAndInstanceNameAndTimePeriod() {
        return new GetByEntityNameAndInstanceNameAndTimePeriod();
    }

    /**
     * Create an instance of {@link GetByActivityAndTimePeriod }
     * 
     */
    public GetByActivityAndTimePeriod createGetByActivityAndTimePeriod() {
        return new GetByActivityAndTimePeriod();
    }

    /**
     * Create an instance of {@link GetByUsername }
     * 
     */
    public GetByUsername createGetByUsername() {
        return new GetByUsername();
    }

    /**
     * Create an instance of {@link EditActivityResponse }
     * 
     */
    public EditActivityResponse createEditActivityResponse() {
        return new EditActivityResponse();
    }

    /**
     * Create an instance of {@link GetByTimePeriodResponse }
     * 
     */
    public GetByTimePeriodResponse createGetByTimePeriodResponse() {
        return new GetByTimePeriodResponse();
    }

    /**
     * Create an instance of {@link LogActivityResponse }
     * 
     */
    public LogActivityResponse createLogActivityResponse() {
        return new LogActivityResponse();
    }

    /**
     * Create an instance of {@link FindActivityByIdResponse }
     * 
     */
    public FindActivityByIdResponse createFindActivityByIdResponse() {
        return new FindActivityByIdResponse();
    }

    /**
     * Create an instance of {@link Exception }
     * 
     */
    public Exception createException() {
        return new Exception();
    }

    /**
     * Create an instance of {@link LogActivity }
     * 
     */
    public LogActivity createLogActivity() {
        return new LogActivity();
    }

    /**
     * Create an instance of {@link GetByTimePeriod }
     * 
     */
    public GetByTimePeriod createGetByTimePeriod() {
        return new GetByTimePeriod();
    }

    /**
     * Create an instance of {@link LogActivityWithNarrative }
     * 
     */
    public LogActivityWithNarrative createLogActivityWithNarrative() {
        return new LogActivityWithNarrative();
    }

    /**
     * Create an instance of {@link GetAllActivities }
     * 
     */
    public GetAllActivities createGetAllActivities() {
        return new GetAllActivities();
    }

    /**
     * Create an instance of {@link EditActivity }
     * 
     */
    public EditActivity createEditActivity() {
        return new EditActivity();
    }

    /**
     * Create an instance of {@link GetByActivityAndTimePeriodResponse }
     * 
     */
    public GetByActivityAndTimePeriodResponse createGetByActivityAndTimePeriodResponse() {
        return new GetByActivityAndTimePeriodResponse();
    }

    /**
     * Create an instance of {@link AuditTrail }
     * 
     */
    public AuditTrail createAuditTrail() {
        return new AuditTrail();
    }

    /**
     * Create an instance of {@link GetByEntityNameAndInstanceNameResponse }
     * 
     */
    public GetByEntityNameAndInstanceNameResponse createGetByEntityNameAndInstanceNameResponse() {
        return new GetByEntityNameAndInstanceNameResponse();
    }

    /**
     * Create an instance of {@link GetAllActivitiesResponse }
     * 
     */
    public GetAllActivitiesResponse createGetAllActivitiesResponse() {
        return new GetAllActivitiesResponse();
    }

    /**
     * Create an instance of {@link GetByUsernameAndTimePeriodResponse }
     * 
     */
    public GetByUsernameAndTimePeriodResponse createGetByUsernameAndTimePeriodResponse() {
        return new GetByUsernameAndTimePeriodResponse();
    }

    /**
     * Create an instance of {@link GetByEntityNameAndInstanceNameAndTimePeriodResponse }
     * 
     */
    public GetByEntityNameAndInstanceNameAndTimePeriodResponse createGetByEntityNameAndInstanceNameAndTimePeriodResponse() {
        return new GetByEntityNameAndInstanceNameAndTimePeriodResponse();
    }

    /**
     * Create an instance of {@link LogActivityWithNarrativeResponse }
     * 
     */
    public LogActivityWithNarrativeResponse createLogActivityWithNarrativeResponse() {
        return new LogActivityWithNarrativeResponse();
    }

    /**
     * Create an instance of {@link GetByEntityNameAndInstanceName }
     * 
     */
    public GetByEntityNameAndInstanceName createGetByEntityNameAndInstanceName() {
        return new GetByEntityNameAndInstanceName();
    }

    /**
     * Create an instance of {@link GetByEntityNameAndEntityId }
     * 
     */
    public GetByEntityNameAndEntityId createGetByEntityNameAndEntityId() {
        return new GetByEntityNameAndEntityId();
    }

    /**
     * Create an instance of {@link GetByUsernameResponse }
     * 
     */
    public GetByUsernameResponse createGetByUsernameResponse() {
        return new GetByUsernameResponse();
    }

    /**
     * Create an instance of {@link GetByEntityNameAndEntityIdResponse }
     * 
     */
    public GetByEntityNameAndEntityIdResponse createGetByEntityNameAndEntityIdResponse() {
        return new GetByEntityNameAndEntityIdResponse();
    }

    /**
     * Create an instance of {@link FindActivityById }
     * 
     */
    public FindActivityById createFindActivityById() {
        return new FindActivityById();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetByEntityNameAndInstanceNameResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", name = "getByEntityNameAndInstanceNameResponse")
    public JAXBElement<GetByEntityNameAndInstanceNameResponse> createGetByEntityNameAndInstanceNameResponse(GetByEntityNameAndInstanceNameResponse value) {
        return new JAXBElement<GetByEntityNameAndInstanceNameResponse>(_GetByEntityNameAndInstanceNameResponse_QNAME, GetByEntityNameAndInstanceNameResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetByTimePeriod }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", name = "getByTimePeriod")
    public JAXBElement<GetByTimePeriod> createGetByTimePeriod(GetByTimePeriod value) {
        return new JAXBElement<GetByTimePeriod>(_GetByTimePeriod_QNAME, GetByTimePeriod.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EditActivityResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", name = "editActivityResponse")
    public JAXBElement<EditActivityResponse> createEditActivityResponse(EditActivityResponse value) {
        return new JAXBElement<EditActivityResponse>(_EditActivityResponse_QNAME, EditActivityResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindActivityById }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", name = "findActivityById")
    public JAXBElement<FindActivityById> createFindActivityById(FindActivityById value) {
        return new JAXBElement<FindActivityById>(_FindActivityById_QNAME, FindActivityById.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetByEntityNameAndEntityId }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", name = "getByEntityNameAndEntityId")
    public JAXBElement<GetByEntityNameAndEntityId> createGetByEntityNameAndEntityId(GetByEntityNameAndEntityId value) {
        return new JAXBElement<GetByEntityNameAndEntityId>(_GetByEntityNameAndEntityId_QNAME, GetByEntityNameAndEntityId.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LogActivityWithNarrative }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", name = "logActivityWithNarrative")
    public JAXBElement<LogActivityWithNarrative> createLogActivityWithNarrative(LogActivityWithNarrative value) {
        return new JAXBElement<LogActivityWithNarrative>(_LogActivityWithNarrative_QNAME, LogActivityWithNarrative.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllActivitiesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", name = "getAllActivitiesResponse")
    public JAXBElement<GetAllActivitiesResponse> createGetAllActivitiesResponse(GetAllActivitiesResponse value) {
        return new JAXBElement<GetAllActivitiesResponse>(_GetAllActivitiesResponse_QNAME, GetAllActivitiesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LogActivityResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", name = "logActivityResponse")
    public JAXBElement<LogActivityResponse> createLogActivityResponse(LogActivityResponse value) {
        return new JAXBElement<LogActivityResponse>(_LogActivityResponse_QNAME, LogActivityResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetByEntityNameAndInstanceNameAndTimePeriod }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", name = "getByEntityNameAndInstanceNameAndTimePeriod")
    public JAXBElement<GetByEntityNameAndInstanceNameAndTimePeriod> createGetByEntityNameAndInstanceNameAndTimePeriod(GetByEntityNameAndInstanceNameAndTimePeriod value) {
        return new JAXBElement<GetByEntityNameAndInstanceNameAndTimePeriod>(_GetByEntityNameAndInstanceNameAndTimePeriod_QNAME, GetByEntityNameAndInstanceNameAndTimePeriod.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetByActivityAndTimePeriod }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", name = "getByActivityAndTimePeriod")
    public JAXBElement<GetByActivityAndTimePeriod> createGetByActivityAndTimePeriod(GetByActivityAndTimePeriod value) {
        return new JAXBElement<GetByActivityAndTimePeriod>(_GetByActivityAndTimePeriod_QNAME, GetByActivityAndTimePeriod.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetByUsernameAndTimePeriodResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", name = "getByUsernameAndTimePeriodResponse")
    public JAXBElement<GetByUsernameAndTimePeriodResponse> createGetByUsernameAndTimePeriodResponse(GetByUsernameAndTimePeriodResponse value) {
        return new JAXBElement<GetByUsernameAndTimePeriodResponse>(_GetByUsernameAndTimePeriodResponse_QNAME, GetByUsernameAndTimePeriodResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetByUsername }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", name = "getByUsername")
    public JAXBElement<GetByUsername> createGetByUsername(GetByUsername value) {
        return new JAXBElement<GetByUsername>(_GetByUsername_QNAME, GetByUsername.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetByTimePeriodResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", name = "getByTimePeriodResponse")
    public JAXBElement<GetByTimePeriodResponse> createGetByTimePeriodResponse(GetByTimePeriodResponse value) {
        return new JAXBElement<GetByTimePeriodResponse>(_GetByTimePeriodResponse_QNAME, GetByTimePeriodResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetByEntityNameAndInstanceName }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", name = "getByEntityNameAndInstanceName")
    public JAXBElement<GetByEntityNameAndInstanceName> createGetByEntityNameAndInstanceName(GetByEntityNameAndInstanceName value) {
        return new JAXBElement<GetByEntityNameAndInstanceName>(_GetByEntityNameAndInstanceName_QNAME, GetByEntityNameAndInstanceName.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetByEntityNameAndEntityIdResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", name = "getByEntityNameAndEntityIdResponse")
    public JAXBElement<GetByEntityNameAndEntityIdResponse> createGetByEntityNameAndEntityIdResponse(GetByEntityNameAndEntityIdResponse value) {
        return new JAXBElement<GetByEntityNameAndEntityIdResponse>(_GetByEntityNameAndEntityIdResponse_QNAME, GetByEntityNameAndEntityIdResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindActivityByIdResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", name = "findActivityByIdResponse")
    public JAXBElement<FindActivityByIdResponse> createFindActivityByIdResponse(FindActivityByIdResponse value) {
        return new JAXBElement<FindActivityByIdResponse>(_FindActivityByIdResponse_QNAME, FindActivityByIdResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LogActivityWithNarrativeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", name = "logActivityWithNarrativeResponse")
    public JAXBElement<LogActivityWithNarrativeResponse> createLogActivityWithNarrativeResponse(LogActivityWithNarrativeResponse value) {
        return new JAXBElement<LogActivityWithNarrativeResponse>(_LogActivityWithNarrativeResponse_QNAME, LogActivityWithNarrativeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EditActivity }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", name = "editActivity")
    public JAXBElement<EditActivity> createEditActivity(EditActivity value) {
        return new JAXBElement<EditActivity>(_EditActivity_QNAME, EditActivity.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllActivities }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", name = "getAllActivities")
    public JAXBElement<GetAllActivities> createGetAllActivities(GetAllActivities value) {
        return new JAXBElement<GetAllActivities>(_GetAllActivities_QNAME, GetAllActivities.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LogActivity }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", name = "logActivity")
    public JAXBElement<LogActivity> createLogActivity(LogActivity value) {
        return new JAXBElement<LogActivity>(_LogActivity_QNAME, LogActivity.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Exception }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", name = "Exception")
    public JAXBElement<Exception> createException(Exception value) {
        return new JAXBElement<Exception>(_Exception_QNAME, Exception.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetByActivityAndTimePeriodResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", name = "getByActivityAndTimePeriodResponse")
    public JAXBElement<GetByActivityAndTimePeriodResponse> createGetByActivityAndTimePeriodResponse(GetByActivityAndTimePeriodResponse value) {
        return new JAXBElement<GetByActivityAndTimePeriodResponse>(_GetByActivityAndTimePeriodResponse_QNAME, GetByActivityAndTimePeriodResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetByEntityNameAndInstanceNameAndTimePeriodResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", name = "getByEntityNameAndInstanceNameAndTimePeriodResponse")
    public JAXBElement<GetByEntityNameAndInstanceNameAndTimePeriodResponse> createGetByEntityNameAndInstanceNameAndTimePeriodResponse(GetByEntityNameAndInstanceNameAndTimePeriodResponse value) {
        return new JAXBElement<GetByEntityNameAndInstanceNameAndTimePeriodResponse>(_GetByEntityNameAndInstanceNameAndTimePeriodResponse_QNAME, GetByEntityNameAndInstanceNameAndTimePeriodResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetByUsernameResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", name = "getByUsernameResponse")
    public JAXBElement<GetByUsernameResponse> createGetByUsernameResponse(GetByUsernameResponse value) {
        return new JAXBElement<GetByUsernameResponse>(_GetByUsernameResponse_QNAME, GetByUsernameResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetByUsernameAndTimePeriod }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.audittrailservices.ewallet.esolutions.co.zw/", name = "getByUsernameAndTimePeriod")
    public JAXBElement<GetByUsernameAndTimePeriod> createGetByUsernameAndTimePeriod(GetByUsernameAndTimePeriod value) {
        return new JAXBElement<GetByUsernameAndTimePeriod>(_GetByUsernameAndTimePeriod_QNAME, GetByUsernameAndTimePeriod.class, null, value);
    }

}
