package zw.co.esolutions.ewallet.crsweb.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class NullValueConverter implements Converter {
	
	static final String PLACE_HOLDER = "--Please Provide--";
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		
		if(value != null && PLACE_HOLDER.equals(value)) {
			value = "";
		}
	
		return value;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		
		System.out.println("value :::::::::::     " + value);
		String strValue = (String) value;
		
		System.out.println("strValue :::::::::::     " + strValue);

		
		if (strValue == null || "".equals(strValue.trim())) {

			System.out.println("in if :::::::::::     ");

			strValue = PLACE_HOLDER;
			
		}
		
		System.out.println("return value :::::::::::     " + strValue);

		return strValue;
	}

}
