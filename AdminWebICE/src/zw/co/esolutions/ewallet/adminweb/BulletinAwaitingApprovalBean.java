package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.List;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.enums.BulletinStatus;
import zw.co.esolutions.ewallet.profileservices.service.Bulletin;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;

public class BulletinAwaitingApprovalBean extends PageCodeBase{
	
private List<Bulletin> bulletinList;
	
	public List<Bulletin> getBulletinList() {
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		
		try{
			bulletinList = profileService.getBulletinByStatus(BulletinStatus.AWAITING_APPROVAL.name());
						
			if (bulletinList == null || bulletinList.isEmpty()) {
				bulletinList = new ArrayList<Bulletin>();
				super.setInformationMessage("No Bulletin to approve");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return bulletinList;
	}
	public void setBulletinList(List<Bulletin> bulletinList) {
		this.bulletinList = bulletinList;
	}
	
	@SuppressWarnings("unchecked")
	public String viewBulletin() {
		String bulletinId = (String) super.getRequestParam().get("bulletinId");
		super.getRequestParam().put("bulletinId", bulletinId);
		this.gotoPage("/admin/viewBulletin.jspx");
		
		return "viewBulletin";
	}
}
