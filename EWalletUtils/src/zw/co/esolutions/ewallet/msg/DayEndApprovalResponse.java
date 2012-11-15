package zw.co.esolutions.ewallet.msg;

import zw.co.esolutions.ewallet.enums.DayEndStatus;

public class DayEndApprovalResponse {
	private String dayEndReponse;
	private String narrative;
	private String detailsCashTenndered;
	private String detailsImbalance;
	private String dayEndId;
	private String teller;
	private String status;
	private DayEndStatus dayEndStatus;
	public String getDayEndReponse() {
		return dayEndReponse;
	}
	public void setDayEndReponse(String dayEndReponse) {
		this.dayEndReponse = dayEndReponse;
	}
	public String getNarrative() {
		return narrative;
	}
	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}
	
	
	public String getDetailsCashTenndered() {
		return detailsCashTenndered;
	}
	public void setDetailsCashTenndered(String detailsCashTenndered) {
		this.detailsCashTenndered = detailsCashTenndered;
	}
	public String getDetailsImbalance() {
		return detailsImbalance;
	}
	public void setDetailsImbalance(String detailsImbalance) {
		this.detailsImbalance = detailsImbalance;
	}
	public String getDayEndId() {
		return dayEndId;
	}
	public void setDayEndId(String dayEndId) {
		this.dayEndId = dayEndId;
	}
	public String getTeller() {
		return teller;
	}
	public void setTeller(String teller) {
		this.teller = teller;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public DayEndStatus getDayEndStatus() {
		return dayEndStatus;
	}
	public void setDayEndStatus(DayEndStatus dayEndStatus) {
		this.dayEndStatus = dayEndStatus;
	}
	
	
}
