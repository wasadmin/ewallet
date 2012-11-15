package zw.co.esolutions.ewallet.adminweb.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import zw.co.esolutions.ewallet.util.MoneyUtil;

public class MoneyConverter implements Converter {
	
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
	
		return null;
	}

	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		
		//return Double.toString(MoneyUtil.convertToDollars(Long.parseLong(value.toString())));
		try {
			return MoneyUtil.convertCentsToDollarsPattern(Long.parseLong(value.toString())).replaceFirst("USD", "");
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "0.00";
		
	}

}


