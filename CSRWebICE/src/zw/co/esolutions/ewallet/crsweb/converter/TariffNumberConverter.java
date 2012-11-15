/**
 * 
 */
package zw.co.esolutions.ewallet.crsweb.converter;

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
	
	private TariffServiceSOAPProxy tariffService;
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
	
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		if (value instanceof String) {
			String tariffTableId = (String) value;
			try {
				List<Tariff> res = null;
				res = this.tariffService.getTariffsByTariffTableId(tariffTableId);
				if(res == null || res.isEmpty()) {
					return 0+"";
				} else {
					return res.size()+"";
				}
				
			} catch (Exception e) {
				
			}
		}
		return null;
	}

	public TariffServiceSOAPProxy getTariffService() {
		if(this.tariffService==null){
			this.tariffService=new TariffServiceSOAPProxy();
		}
		return tariffService;
	}

	public void setTariffService(TariffServiceSOAPProxy tariffService) {
		this.tariffService = tariffService;
	}

}
