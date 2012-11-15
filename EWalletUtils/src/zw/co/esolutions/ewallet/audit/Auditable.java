package zw.co.esolutions.ewallet.audit;

import java.util.Map;

public interface Auditable {
	public String getEntityName();
	
	public Map< String, String>  getAuditableAttributesMap();
		
	
	public String getAuditableAttributesString();
	
	public String getInstanceName();

}
