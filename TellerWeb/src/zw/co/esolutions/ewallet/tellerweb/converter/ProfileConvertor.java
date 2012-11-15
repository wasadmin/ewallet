package zw.co.esolutions.ewallet.tellerweb.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.util.NumberUtil;

public class ProfileConvertor   implements Converter{
	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
	
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
        Profile profile = null;
        ProfileServiceSOAPProxy proxy = new ProfileServiceSOAPProxy();
    	
        try {
   			if (value instanceof String) {
   				NumberUtil.formatMobileNumber((String)value);
				return "Non-Holder: "+(String)value;				
			}
		} catch (Exception e) {
			profile = proxy.findProfileById((String)value);
			if(profile != null) {
				return profile.getLastName()+" "+profile.getFirstNames();
			}
		}
		return null;
	}
}
