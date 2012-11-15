package zw.co.esolutions.ewallet.adminweb;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.profileservices.service.Bulletin;
import zw.co.esolutions.ewallet.profileservices.service.Profile;

public class ViewBulletinBean extends PageCodeBase{
	
	private String bulletinId;
	private Bulletin bulletin;
	private boolean approver;
	private boolean auditor;
			
	public String getBulletinId() {
		if (bulletinId == null) {
			bulletinId = (String) super.getRequestScope().get("bulletinId");
		}
		if (bulletinId == null) {
			bulletinId = (String) super.getRequestParam().get("bulletinId");
		}
		return bulletinId;
	}
	
	public void setBulletin(Bulletin bulletin) {
		this.bulletin = bulletin;
	}
	
	public Bulletin getBulletin() {
		if (bulletin == null && getBulletinId() != null) {
			try {
				bulletin = super.getProfileService().findBulletinById(getBulletinId());
			} catch (Exception e) {
				e.printStackTrace();
				super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			}
		}
		
		return bulletin;
	}
	
	public String ok() {
		bulletin=null;
		bulletinId=null;
		super.gotoPage("/admin/adminHome.jspx");
		return "ok";
	}
		
	public String logs(){
		super.gotoPage("/admin/viewLogs.jspx");
		return "logs";
	}
	
	public String approveBulletin() {
		
		Profile p = super.getProfileService().getProfileByUserName(getJaasUserName());
		Bulletin bulletin;
		try {
		
			bulletin = super.getProfileService().findBulletinById(getBulletinId());
			bulletin.setApproverId(p.getId());
			bulletin = super.getProfileService().approveBulletin(bulletin, getJaasUserName());
			
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage("Bulletin approved successfully.");
		return "approveBulletin";
	}
	
	public String rejectBulletin() throws Exception {
		
		Profile p = super.getProfileService().getProfileByUserName(getJaasUserName());
		Bulletin bulletin;
		try {
		
			bulletin = super.getProfileService().findBulletinById(getBulletinId());
			System.out.println("XXXXXXXXXXXXXXXXX"+bulletin);
			bulletin.setApproverId(p.getId());
			bulletin = super.getProfileService().disapproveBulletin(bulletin, getJaasUserName());
			
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage("Bulletin disapproved");
		return "success";
	}
	
	public void setBulletinId(String bulletinId) {
		this.bulletinId = bulletinId;
	}

	public void setApprover(boolean approver) {
		this.approver = approver;
	}

	public boolean isApprover() {
		boolean approver = super.getProfileService().canDo(super.getJaasUserName(), "APPROVE");
		return approver;
	}
	
}
