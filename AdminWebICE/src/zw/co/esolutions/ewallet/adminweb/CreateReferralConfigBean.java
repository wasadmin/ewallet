package zw.co.esolutions.ewallet.adminweb;

import java.util.Date;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.referralservices.service.ReferralConfig;
import zw.co.esolutions.ewallet.util.DateUtil;

public class CreateReferralConfigBean extends PageCodeBase {
	
	private ReferralConfig referralConfig = new ReferralConfig();
	private Date dateFrom;
	private String ratio;
	 
	public void setReferralConfig(ReferralConfig referralConfig) {
		this.referralConfig = referralConfig;
	}

	public ReferralConfig getReferralConfig() {
		return referralConfig;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setRatio(String ratio) {
		this.ratio = ratio;
	}

	public String getRatio() {
		return ratio;
	}

	public String submit() {
		
		String[] ratioParts = ratio.split(":");
		Integer referrerRatio = 0;
		Integer referredRatio = 0;
		try {
			referrerRatio = Integer.parseInt(ratioParts[0]);
			referredRatio = Integer.parseInt(ratioParts[1]);
		} catch (Exception e) {
			super.setErrorMessage("ERROR: Referrer to Referred Ratio must be integers.");
			return "failure";
		}
		referralConfig.setReferrerRatio(referrerRatio);
		referralConfig.setReferredRatio(referredRatio);
		referralConfig.setDateFrom(DateUtil.convertToXMLGregorianCalendar(dateFrom));
		try {
			ReferralConfig currentConfig = super.getReferralService().getActiveReferralConfig();
			if (currentConfig != null) {
				if (!new Date().before(dateFrom)) {
					super.setErrorMessage("ERROR: Date From should be a day in the future.");
					return "failure";
				}
				currentConfig.setDateTo(DateUtil.convertToXMLGregorianCalendar(DateUtil.addDays(DateUtil.convertToDate(referralConfig.getDateFrom()), -1)));
				super.getReferralService().updateReferralConfig(currentConfig, super.getJaasUserName());
			}
			super.getReferralService().createReferralConfig(referralConfig, super.getJaasUserName());
		} catch (Exception e) { 
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		System.out.println(">>>>>>>>>>>>>>>>>created");
		super.setInformationMessage("Referral config created successfully.");
		return "submit";
	}
	
	public String cancel() {
		gotoPage("/admin/adminHome.jspx");
		return "cancel";
	}
}