package zw.co.esolutions.ewallet.tellerweb.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;

public class MobileProfileConverter implements Converter {
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
	
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		
		MobileProfile mobileProfile = null;
		
		try {
			if (value instanceof String) {
				mobileProfile = customerService.findMobileProfileById((String)value);
				if(mobileProfile == null) {
					return (String)value;
				}
				return mobileProfile.getMobileNumber();
				
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

}

