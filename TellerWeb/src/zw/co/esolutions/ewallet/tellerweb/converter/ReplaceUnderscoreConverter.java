package zw.co.esolutions.ewallet.tellerweb.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import zw.co.esolutions.ewallet.limitservices.service.TransactionType;

public class ReplaceUnderscoreConverter implements Converter {
	
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
	
		return null;
	}

	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		if(value == null) {
			return "";
		}
		String str = value.toString();
		if((value instanceof TransactionType)&&TransactionType.BALANCE.equals((TransactionType)value)) {
			str = "ACCOUNT BALANCE";
		} else if(value instanceof zw.co.esolutions.ewallet.tariffservices.service.TransactionType && 
				zw.co.esolutions.ewallet.tariffservices.service.TransactionType.BALANCE.
				equals((zw.co.esolutions.ewallet.tariffservices.service.TransactionType)value)) {
			str = "BALANCE ENQUIRY";
		}
		return str.replace("_", " ");
	}

}


