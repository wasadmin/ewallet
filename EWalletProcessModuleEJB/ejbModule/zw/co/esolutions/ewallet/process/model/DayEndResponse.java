package zw.co.esolutions.ewallet.process.model;

import java.util.Date;

public class DayEndResponse {
	private boolean status;
	private Date dayEndDate;
	private String description;
	private String tellerId;
	private String userName;
	private String dayEndId;
	private String narrative;
	
	
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public Date getDayEndDate() {
		return dayEndDate;
	}
	public void setDayEndDate(Date dayEndDate) {
		this.dayEndDate = dayEndDate;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTellerId() {
		return tellerId;
	}
	public void setTellerId(String tellerId) {
		this.tellerId = tellerId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getDayEndId() {
		return dayEndId;
	}
	public void setDayEndId(String dayEndId) {
		this.dayEndId = dayEndId;
	}
	public String getNarrative() {
		return narrative;
	}
	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}
	
	

}
