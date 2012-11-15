package zw.co.esolutions.ewallet.contactdetailsservices.service;
import javax.jws.WebParam;
import javax.jws.WebService;

import zw.co.esolutions.ewallet.contactdetailsservices.model.ContactDetails;

@WebService(name="ContactDetailsService")
public interface ContactDetailsService {

	ContactDetails createContactDetails(@WebParam(name="contactDetails") ContactDetails contactDetails,@WebParam(name="userName")String userName)
			throws Exception;

	String deleteContactDetails(@WebParam(name="contactDetails") ContactDetails contactDetails) throws Exception;

	ContactDetails editContactDetails(@WebParam(name="contactDetails") ContactDetails contactDetails,@WebParam(name="userName")String userName)
	throws Exception;
	
	ContactDetails updateContactDetails(@WebParam(name="contactDetails") ContactDetails contactDetails)
			throws Exception;

	ContactDetails findContactDetailsById(@WebParam(name="id") String id);

}
