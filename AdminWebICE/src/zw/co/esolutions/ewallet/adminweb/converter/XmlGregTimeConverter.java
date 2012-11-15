package zw.co.esolutions.ewallet.adminweb.converter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.xml.datatype.XMLGregorianCalendar;

import zw.co.esolutions.ewallet.util.DateUtil;

public class XmlGregTimeConverter implements Converter{

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		try {
			if (value instanceof XMLGregorianCalendar) {
				XMLGregorianCalendar xmlG = (XMLGregorianCalendar)value;
				Date date = DateUtil.convertToDate(xmlG);
				DateFormat df = new SimpleDateFormat("HH:mm:ss");
				return df.format(date)+" Hrs";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
