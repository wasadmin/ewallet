package zw.co.esolutions.ewallet.crsweb.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;

public class MobileProfileConverter implements Converter {
	
	private CustomerServiceSOAPProxy customerService;
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
	
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		
		MobileProfile mobileProfile = null;
		
		try {
	 customerService = new CustomerServiceSOAPProxy();
			if (value instanceof String) {
				mobileProfile = this.customerService.findMobileProfileById((String)value);
				if(mobileProfile == null || mobileProfile.getId() == null) {
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

