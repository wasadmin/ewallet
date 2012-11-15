package zw.co.esolutions.ewallet.adminweb.converter;

import java.text.DecimalFormat;
import java.text.ParseException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import zw.co.esolutions.ewallet.util.Formats;
import zw.co.esolutions.ewallet.util.MoneyUtil;

public class DoubleToLongConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		DecimalFormat dFormat = Formats.moneyFormat;
		try {
			Number num = dFormat.parse(value);
			value = num.toString();
			System.out.println(">>>>>>>>>>>>>>>>>>>>> String value = "+value);
			return new Long(MoneyUtil.convertToCents(Double.parseDouble(value)));
		} catch (Exception e) {
			return new Long(0);
		} 
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		String res = null;
		try {
			System.out.println(">>>>>>>>>>>>>>>>>>>>> Object value = "+value);
			res = MoneyUtil.convertCentsToDollarsPatternNoCurrency(Long.parseLong(value.toString()));
		} catch (Exception e) {
			res = "0.00";
		}
		return res;
	}
	
	public static void main(String ...args) {
		try {
			DecimalFormat dFormat = Formats.moneyFormat;
			Number num = dFormat.parse("1,000.00");
			System.out.println("  Number = "+num.toString());
			System.out.println(">>>>>>>>>>>>>> Long = "+MoneyUtil.convertToCents(Double.parseDouble(num.toString())));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
