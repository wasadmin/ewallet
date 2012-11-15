package zw.co.esolutions.ewallet.adminweb;

import java.util.Date;

import java.util.Calendar;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.profileservices.service.Bulletin;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.util.DateUtil;

public class CreateBulletinBean extends PageCodeBase{
	
	private Bulletin bulletin = new Bulletin();
	private Date expDate;
	
	public CreateBulletinBean() {
		this.initialiseDate();
	}
		
	public Bulletin getBulletin() {
		return bulletin;
	}

	public void setBulletin(Bulletin bulletin) {
		this.bulletin = bulletin;
	}

	@SuppressWarnings("unchecked")
	public String submit() {
		
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		
		try {
			Profile p = profileService.getProfileByUserName(getJaasUserName());
			bulletin.setInitiatorId(p.getId());
			System.out.println("The expDate"+getExpDate());
			bulletin.setExpirationDate(DateUtil.convertToXMLGregorianCalendar(getExpDate()));
			bulletin = profileService.createBulletin(bulletin, getJaasUserName());
			
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.getRequestScope().put("bulletinId", bulletin.getId());
		super.setInformationMessage("Bulletin created successfully.");
		super.gotoPage("/admin/viewBulletin.jspx");
		return "submit";
	}
	
	public String cancel() {
		super.gotoPage("/admin/adminHome.jspx");
		return "cancel";
	}
	
	private void initialiseDate(){
		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXX");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DAY_OF_MONTH,1);
		setExpDate(expDate);
	}

	public Date getExpDate() {
		return expDate;
	}

	public void setExpDate(Date expDate) {
		this.expDate = expDate;
	}
	
	
}
