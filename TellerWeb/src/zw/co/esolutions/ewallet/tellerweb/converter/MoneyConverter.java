package zw.co.esolutions.ewallet.tellerweb.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import zw.co.esolutions.ewallet.util.MoneyUtil;

public class MoneyConverter implements Converter {
	
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
			res = MoneyUtil.convertCentsToDollarsPattern(Long.parseLong(value.toString())).replace("-", "");
		} catch (NullPointerException e) {
			res = "USD0.00";
		}
		return res;
	}

}


