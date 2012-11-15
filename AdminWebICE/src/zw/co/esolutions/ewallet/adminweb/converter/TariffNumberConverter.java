/**
 * 
 */
package zw.co.esolutions.ewallet.adminweb.converter;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import zw.co.esolutions.ewallet.tariffservices.service.Tariff;
import zw.co.esolutions.ewallet.tariffservices.service.TariffServiceSOAPProxy;

/**
 * @author tauttee
 *
 */
public class TariffNumberConverter implements Converter {
	
	private TariffServiceSOAPProxy tariffService = new TariffServiceSOAPProxy();
	
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
	
		return null;
	}

	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		if (value instanceof String) {
			String tariffTableId = null;
			try {
				tariffTableId = (String) value;
				List<Tariff> res = null;
				res = this.tariffService.getTariffsByTariffTableId(tariffTableId);
				if(res == null) {
					return 0+"";
				} else {
					return res.size()+"";
				}
				
			} catch (Exception e) {
				
			}
		}
		return null;
	}

}
