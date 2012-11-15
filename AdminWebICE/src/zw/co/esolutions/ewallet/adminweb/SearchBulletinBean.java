package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.enums.BulletinStatus;
import zw.co.esolutions.ewallet.enums.ProfileStatus;
import zw.co.esolutions.ewallet.profileservices.service.Bulletin;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.JsfUtil;

public class SearchBulletinBean extends PageCodeBase{
	
	private String selectedInitiator;
	private String selectedApprover;
	private Date fromDate;
	private Date toDate;
	private String status;
	private List<SelectItem> statusList;
	private List<Bulletin> bulletinList;
	
	public SearchBulletinBean(){
		this.initializeDates();
	}
	
	public String getSelectedInitiator() {
		return selectedInitiator;
	}

	public void setSelectedInitiator(String selectedInitiator) {
		this.selectedInitiator = selectedInitiator;
	}

	public String getSelectedApprover() {
		return selectedApprover;
	}

	public void setSelectedApprover(String selectedApprover) {
		this.selectedApprover = selectedApprover;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public List<Bulletin> getBulletinList() {
		if (bulletinList == null) {
			bulletinList = new ArrayList<Bulletin>();
		}
		return bulletinList;
	}
	
	public void setBulletinList(List<Bulletin> bulletinList) {
		this.bulletinList = bulletinList;
	}
	
	public String submit() {
		try {
			bulletinList = super.getProfileService().getBulletinByAllFields(null,null, status,DateUtil.convertToXMLGregorianCalendar(getFromDate()),
					DateUtil.convertToXMLGregorianCalendar(getToDate()));
			
			if (bulletinList == null || bulletinList.isEmpty()) {
				bulletinList = new ArrayList<Bulletin>();
				super.setErrorMessage("Oops! No results found.");
				return "failure";
			}
		} catch (Exception e) {
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage("Bulletin(s) found.");
		return "submit";
	}
	
	public String getCurrent(){
		try {
			bulletinList.add(super.getProfileService().getCurrentBulletin());
			if (bulletinList == null || bulletinList.isEmpty()) {
				bulletinList = new ArrayList<Bulletin>();
				super.setErrorMessage("Oops! No results found.");
				return "failure";
			}
		} catch (Exception e) {
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage("Bulletin(s) found.");
		return "getCurrent";
	}
	
	
	
	public String viewBulletin() {
		
		return "viewBulletin";
	}

	public void setStatusList(List<SelectItem> statusList) {
		this.statusList = statusList;
	}

	public List<SelectItem> getStatusList() {
		if(this.statusList == null) {
			this.statusList = new ArrayList<SelectItem>();
			this.statusList.add(new SelectItem("none", "--select--"));
			for(BulletinStatus status : getBulletinStatusArray()) {
				this.statusList.add(new SelectItem(status.toString(), status.toString().replace("_", " ")));
			}
		}
		return statusList;
	}
	
	private BulletinStatus [] getBulletinStatusArray(){
		return new BulletinStatus[]{
				BulletinStatus.ACTIVE,
				BulletinStatus.EXPIRED,
				BulletinStatus.AWAITING_APPROVAL,
				BulletinStatus.DISAPPROVED,
				BulletinStatus.INACTIVE
		};
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	private void initializeDates() {
		Date date = new Date();
		if(this.getFromDate() == null) {
			this.setFromDate(DateUtil.getBeginningOfDay(DateUtil.add(date, Calendar.DAY_OF_MONTH, -1)));
		}
		if(this.getToDate() == null) {
			this.setToDate(DateUtil.getEndOfDay(date));
		}
	}
	
}
