package zw.co.esolutions.ewallet.tellerweb;

import java.util.Date;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.process.ProcessServiceSOAPProxy;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.MoneyUtil;

public class TellerNetPosition extends PageCodeBase {
	private Profile profile;
	
	private double netPosition;

	public Profile getProfile() {
		ProfileServiceSOAPProxy profileService = new ProfileServiceSOAPProxy();
		this.profile=profileService.getProfileByUserName(super.getJaasUserName());
		return profile;
	}

	public void setProfile(Profile profile) {
		
		this.profile = profile;
	}

	public double getNetPosition() {
		ProcessServiceSOAPProxy processServvice= new ProcessServiceSOAPProxy();
		
		try {
			long netCash=processServvice.getTellerNetCashPosition(getProfile().getId(), DateUtil.convertToXMLGregorianCalendar(new Date()));
			System.out.println("net cash in cents b4  >>>>>"+netCash);
			this.netPosition=MoneyUtil.convertToDollars(netCash);
			System.out.println("net cash of teller >>>>>>>>>>>"+netPosition);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return netPosition;
	}

	public void setNetPosition(double netPosition) {
		this.netPosition = netPosition;
	}
	
	
	

}
