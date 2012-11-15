package zw.co.esolutions.ewallet.crsweb.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.MobileProfile;
import zw.co.esolutions.ewallet.util.NumberUtil;

public class CustomerNameConverter implements Converter {
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
   			if (value instanceof String) {
   				NumberUtil.formatMobileNumber((String)value);
				return "Non-Holder: "+(String)value;				
			}
		} catch (Exception e) {
			mobileProfile = this.customerService.findMobileProfileById((String)value);
			if(mobileProfile != null && mobileProfile.getId() != null) {
				return mobileProfile.getCustomer().getLastName()+" "+mobileProfile.getCustomer().getFirstNames();
			}
		}
		return null;
	}

	public CustomerServiceSOAPProxy getCustomerService() {
		if(this.customerService==null){
			customerService=new CustomerServiceSOAPProxy();
		}
		return customerService;
	}

	public void setCustomerService(CustomerServiceSOAPProxy customerService) {
		this.customerService = customerService;
	}

}

