package zw.co.esolutions.ewallet.tellerweb.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.util.NumberUtil;

public class CustomerNameConverter implements Converter {
	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
	
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
        MobileProfile mobileProfile = null;
        CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
    	
        try {
   			if (value instanceof String) {
   				NumberUtil.formatMobileNumber((String)value);
				return "Non-Holder: "+(String)value;				
			}
		} catch (Exception e) {
			mobileProfile = customerService.findMobileProfileById((String)value);
			if(mobileProfile != null) {
				return mobileProfile.getCustomer().getLastName()+" "+mobileProfile.getCustomer().getFirstNames();
			}
		}
		return null;
	}

}

