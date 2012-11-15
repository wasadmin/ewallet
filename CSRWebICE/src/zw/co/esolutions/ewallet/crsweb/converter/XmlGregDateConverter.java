package zw.co.esolutions.ewallet.crsweb.converter;

import java.util.Calendar;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.xml.datatype.XMLGregorianCalendar;

import zw.co.esolutions.ewallet.util.DateUtil;

public class XmlGregDateConverter implements Converter {
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
	
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		if(value == null) {
			Date date = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.YEAR, 5);
			return DateUtil.convertDateToString(cal.getTime());
			
		}
		if (value instanceof XMLGregorianCalendar) {
			XMLGregorianCalendar xmlG = (XMLGregorianCalendar)value;
			return DateUtil.convertDateToString(DateUtil.convertToDate(xmlG));
		}
		return null;
	}

}

