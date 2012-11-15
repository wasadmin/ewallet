package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.referralservices.service.ReferralConfig;
import zw.co.esolutions.ewallet.util.DateUtil;

public class SearchReferralConfigBean extends PageCodeBase {
	
	private Date dateFrom = new Date();
	private Date dateTo = new Date();
	private List<ReferralConfig> configList;
	
	public Date getDateFrom() {
		return dateFrom;
	}
	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}
	public Date getDateTo() {
		return dateTo;
	}
	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}
	public List<ReferralConfig> getConfigList() {
		return configList;
	}
	public void setConfigList(List<ReferralConfig> configList) {
		this.configList = configList;
	}
	
	public String search() {
		if (dateFrom != null && dateTo != null) {
			configList = super.getReferralService().getReferralConfigBetweenDates(DateUtil.convertToXMLGregorianCalendar(dateFrom), DateUtil.convertToXMLGregorianCalendar(dateTo));
			if (configList == null || configList.isEmpty()) {
				super.setInformationMessage("No Referral Config found.");
			} else {
				super.setInformationMessage("Referral config(s) found (" + configList.size() + ")");
			}
		} else {
			super.setInformationMessage("Please supply all parameters.");
		}
		return "search";
	}
	
	public String viewCurrent() {
		ReferralConfig config = super.getReferralService().getActiveReferralConfig();
		System.out.println(">>>>>>>>>>>>config " + config);
		configList = new ArrayList<ReferralConfig>();
		if (config != null) {
			System.out.println(">>>>>>>>> config not null");
			configList.add(config);
			super.setInformationMessage("Current config found.");
		} else {
			super.setInformationMessage("Referral Config not found.");
		}
		return "viewCurrent";
	}
	
}