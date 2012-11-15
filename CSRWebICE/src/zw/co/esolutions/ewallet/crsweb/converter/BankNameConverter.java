package zw.co.esolutions.ewallet.crsweb.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;

public class BankNameConverter implements Converter {
	
	private BankServiceSOAPProxy bankService;
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
			res = this.bankService.findBankById((String)value).getName();
		} catch (NullPointerException e) {
			res = "error";
		}
		return res;
	}

	public BankServiceSOAPProxy getBankService() {
		if(this.bankService==null){
			this.bankService=new BankServiceSOAPProxy();
		}
		return bankService;
	}

	public void setBankService(BankServiceSOAPProxy bankService) {
		this.bankService = bankService;
	}
	
}


