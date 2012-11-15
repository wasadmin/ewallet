package zw.co.esolutions.ewallet.crsweb.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;

public class BranchNameConverter implements Converter {
	
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
	
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		String res = null; 
		try {
			BankServiceSOAPProxy service = new BankServiceSOAPProxy();
			res = service.findBankBranchById((String)value).getName();
		} catch (NullPointerException e) {
			res = "error";
		}
		return res;
	}

	
	
	
	
}


