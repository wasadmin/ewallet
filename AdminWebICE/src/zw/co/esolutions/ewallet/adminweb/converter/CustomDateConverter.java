package zw.co.esolutions.ewallet.adminweb.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.DateTimeConverter;
import javax.faces.convert.ConverterException;


public class CustomDateConverter extends DateTimeConverter{

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,String value) {
		SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Date date;
		try {
			date = sf.parse(value);
		} catch (ParseException e) {
			throw new ConverterException("Invalid date format use dd/MM/yyyy HH:mm");
		}
		return date;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,Object value) {
		if(value instanceof Date){
			if(value == null) {
				return "";
			}
			Date date = (Date)value;
			SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			return sf.format(date);
		}
		return "";
	}

}
