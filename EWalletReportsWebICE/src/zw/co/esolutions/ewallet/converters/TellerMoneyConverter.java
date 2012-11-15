package zw.co.esolutions.ewallet.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import zw.co.esolutions.ewallet.util.MoneyUtil;

public class TellerMoneyConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {

		try {
			return new Double(Double.parseDouble(value));
		} catch (NumberFormatException e) {
			return new Double(0.0);
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		String res = null;
		try {
			res = MoneyUtil.convertCentsToDollarsPatternNoCurrency(Double.parseDouble(value.toString())).replace("-", "");
		} catch (NullPointerException e) {
			res = "0.00";
		}
		return res;
	}

}
