/**
 * 
 */
package zw.co.esolutions.ewallet.adminweb;

import pagecode.PageCodeBase;

/**
 * @author wasadmin
 *
 */
public class ConfirmDownloadBean extends PageCodeBase{

	private String entity;
	
	public ConfirmDownloadBean() {
		super();
		if(entity == null) {
			this.setEntity((String)super.getRequestScope().get("entity"));
		}
		
	}

	public String home() {
		super.gotoPage("/admin/adminHome.jspx");
		return "success";
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getEntity() {
		return entity;
	}
	
	
}
