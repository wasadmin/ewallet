package zw.co.esolutions.ewallet.adminweb.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.DateTimeConverter;
import javax.faces.convert.ConverterException;
import javax.xml.datatype.XMLGregorianCalendar;

import zw.co.esolutions.ewallet.util.DateUtil;


public class XMLGregNoTimeDateConverter extends DateTimeConverter{

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,String value) {
		SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
		Date date;
		try {
			date = sf.parse(value);
			return DateUtil.convertToXMLGregorianCalendar(date);
		} catch (ParseException e) {
			throw new ConverterException("Invalid date format use dd/MM/yyyy");
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,Object value) {
		try {
			if(value instanceof XMLGregorianCalendar){
				if(value == null) {
					return "";
				}
				XMLGregorianCalendar xml = (XMLGregorianCalendar)value;
				if(xml != null) {
					Date date = DateUtil.convertToDate(xml);
					SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
					return sf.format(date);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}
