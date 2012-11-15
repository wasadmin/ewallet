package zw.co.esolutions.ewallet.adminweb.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;

public class ProfileNameConverter implements Converter{

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		String profileName = "";
		try {
			String id = (String)value;
			if(id != null){
				ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
				Profile profile = profileService.findProfileById(id);
				if(profile != null){
					profileName = profile.getLastName()+" "+profile.getFirstNames();
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return profileName;
	}

}
