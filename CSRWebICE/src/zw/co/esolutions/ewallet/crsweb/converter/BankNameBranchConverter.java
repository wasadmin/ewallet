package zw.co.esolutions.ewallet.crsweb.converter;

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
		BankServiceSOAPProxy bankService=new BankServiceSOAPProxy();
		String res = null; 
		try {
			res = bankService.findBankBranchById((String)value).getBank().getName();
			//System.out.println("Bank name from service is   "+res);
		} catch (NullPointerException e) {
			res = "error";
		}
		return res;
	}
	

}


