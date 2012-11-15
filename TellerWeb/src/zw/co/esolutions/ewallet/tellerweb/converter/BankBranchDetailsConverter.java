package zw.co.esolutions.ewallet.tellerweb.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;

public class BankBranchDetailsConverter implements Converter {
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
			if(value == null || "".equalsIgnoreCase((String)value)) {
				return "null";
			}
			BankBranch bb = bankService.findBankBranchById((String)value);
			if(bb != null) {
				res = bb.getBank().getName()+" : "+bb.getName();
			} else {
				Bank bk = bankService.findBankById((String)value);
				if(bk != null) {
					res = bk.getName();
				} else {
					res = (String)value;
				}
			}
		} catch (NullPointerException e) {
			res = " ";
		} 
		return res;
	}

}


