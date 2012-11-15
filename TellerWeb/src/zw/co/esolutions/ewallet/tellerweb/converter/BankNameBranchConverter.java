package zw.co.esolutions.ewallet.tellerweb.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;

public class BankNameBranchConverter implements Converter {
	
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
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>id>>>>>>>>>>>>"+(String)value);
		try {
			res = bankService.findBankBranchById((String)value).getBank().getName();
		} catch (NullPointerException e) {
			res = "error";
		}
		return res;
	}

}


