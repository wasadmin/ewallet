package zw.co.esolutions.ewallet.reports.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import zw.co.esolutions.ewallet.bankservices.service.BankBranch;

public class NullBankBranchConverter implements Converter {
	
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
	
		return null;
	}

	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		String str = "";
		try { 
			if(value == null) {
				return "";
			}
			BankBranch b = (BankBranch)value;
			str = b.getName();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

}


