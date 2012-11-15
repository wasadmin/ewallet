package zw.co.esolutions.ewallet.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import zw.co.esolutions.ewallet.process.ProcessServiceSOAPProxy;
import zw.co.esolutions.ewallet.process.ProcessTransaction;

public class ReferenceToTxnTypeConverter implements Converter {
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
	
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		String res = null; 
		ProcessServiceSOAPProxy serv= new ProcessServiceSOAPProxy();
		try {
			ProcessTransaction p = serv.getProcessTransactionByMessageId((String)value);
			res = p.getTransactionType().toString();
			res = res.replace("_", " ");
		} catch (NullPointerException e) {
			res = "";
		} catch (Exception e) {
			res = "";
		}
		return res;
	}

}


