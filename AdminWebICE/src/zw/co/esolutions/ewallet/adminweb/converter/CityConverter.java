package zw.co.esolutions.ewallet.adminweb.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetailsServiceSOAPProxy;

public class CityConverter implements Converter {
	
	private ContactDetailsServiceSOAPProxy contactDetailsService = new ContactDetailsServiceSOAPProxy();
	
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
	
		return null;
	}

	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		if (value instanceof String) {
			String contactDetailsId = null;
			try {
				contactDetailsId = (String) value;
				ContactDetails contactDetails = contactDetailsService.findContactDetailsById(contactDetailsId);
				if (contactDetails != null) {
					return contactDetails.getCity();
				}
			} catch (Exception e) {
				
			}
		}
		return null;
	}

}
