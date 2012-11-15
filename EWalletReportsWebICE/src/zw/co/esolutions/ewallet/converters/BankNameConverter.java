package zw.co.esolutions.ewallet.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;

public class BankNameConverter implements Converter {
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
	
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		String res = null; 
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		try {
			res = bankService.findBankById((String)value).getName();
		} catch (NullPointerException e) {
			res = "";
		}
		return res;
	}

}


