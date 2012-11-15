package zw.co.esolutions.ewallet.contactdetailsservices.service;

import java.util.Date;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import zw.co.esolutions.ewallet.audit.AuditEvents;
import zw.co.esolutions.ewallet.audittrailservices.service.AuditTrailServiceSOAPProxy;
import zw.co.esolutions.ewallet.contactdetailsservices.model.ContactDetails;
import zw.co.esolutions.ewallet.util.GenerateKey;

/**
 * Session Bean implementation class ContactDetailsServiceImpl
 */
@Stateless
@WebService(endpointInterface="zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetailsService", serviceName="ContactDetailsService", portName="ContactDetailsServiceSOAP")
public class ContactDetailsServiceImpl implements ContactDetailsService {
	
	@PersistenceContext(unitName="ContactDetailsServicesEJB") 
	private EntityManager em;
    /**
     * Default constructor. 
     */
    public ContactDetailsServiceImpl() {
    
    }
    
	public ContactDetails createContactDetails(ContactDetails contactDetails,String userName)
			throws Exception {
		if(contactDetails.getId() == null) {
			contactDetails.setId(GenerateKey.generateEntityId());
		}
		if(contactDetails.getDateCreated() == null) {
			contactDetails.setDateCreated(new Date());
		}
    	contactDetails.setFieldsToUpperCase();
		try {
			em.persist(contactDetails);
			
			//Audit trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.CREATE_CONTACT_DETAILS, contactDetails.getOwnerId(), contactDetails.getEntityName(), null, contactDetails.getAuditableAttributesString(), contactDetails.getInstanceName());
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} 
	
		return contactDetails;
	}

	public String deleteContactDetails(ContactDetails contactDetails)
			throws Exception {
	
		try {
			contactDetails = em.merge(contactDetails);
			em.remove(contactDetails);
		} finally {
	
		}
		return "";
	}

	public ContactDetails editContactDetails(ContactDetails contactDetails,String userName) throws Exception{
		
		String oldContactDetails = this.findContactDetailsById(contactDetails.getId()).getAuditableAttributesString();
		
		contactDetails = this.updateContactDetails(contactDetails);
		
		//Audit trail
		AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
		auditService.logActivity(userName, AuditEvents.EDIT_CONTACT_DETAILS, contactDetails.getOwnerId(), contactDetails.getEntityName(), oldContactDetails, contactDetails.getAuditableAttributesString(), contactDetails.getInstanceName());
		
		return contactDetails;
	}
	public ContactDetails updateContactDetails(ContactDetails contactDetails)
			throws Exception {
		contactDetails.setFieldsToUpperCase();
		try {
			contactDetails = em.merge(contactDetails);
		} finally {
	
		}
		return contactDetails;
	}

	public ContactDetails findContactDetailsById(String id) {
		ContactDetails contactDetails = null;
	
		try {
			contactDetails = (ContactDetails) em.find(ContactDetails.class, id);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		finally {
			
		}
		return contactDetails;
	}

}
