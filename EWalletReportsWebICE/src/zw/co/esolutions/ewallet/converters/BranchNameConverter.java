package zw.co.esolutions.ewallet.converters;

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
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		try {
			//System.out.println("________________--------------------------------------------"+value);
			//System.out.println("Branch id  "+(String)value);
			res = bankService.findBankBranchById((String)value).getName();
		} catch (NullPointerException e) {
			res = "";
		}
		return res;
	}

}


