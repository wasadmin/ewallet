package zw.co.esolutions.ewallet.tellerweb;

import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.process.DayEndResponse;
import zw.co.esolutions.ewallet.process.ProcessServiceSOAPProxy;
import zw.co.esolutions.ewallet.process.ProcessTransaction;
import zw.co.esolutions.ewallet.process.TransactionStatus;
import zw.co.esolutions.ewallet.process.TransactionType;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.MoneyUtil;

public class TellerStartOfDay extends PageCodeBase {
	private double floatAmount;
	private Profile profile;
	

private static Logger LOG ;
	
	static{
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(TellerStartOfDay.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + TellerStartOfDay.class);
		}
	}
	
	public double getFloatAmount() {
		return floatAmount;
	}




	public void setFloatAmount(double floatAmount) {
		this.floatAmount = floatAmount;
	}




	public Profile getProfile() {
		ProfileServiceSOAPProxy profileService= new ProfileServiceSOAPProxy();
		
		this.profile=profileService.getProfileByUserName(getJaasUserName());
		return profile;
	}




	public void setProfile(Profile profile) {
		this.profile = profile;
	}




	public String saveStartOfDay(){
		//LOG.debug(" saving teller start of day---------------------------");
		//LOG.debug("float value -----------------------is------------------"+floatAmount);
		if(floatAmount>0){
			ProcessServiceSOAPProxy processProxy= new ProcessServiceSOAPProxy();
			ProfileServiceSOAPProxy profileService= new ProfileServiceSOAPProxy();
			Profile profile=profileService.getProfileByUserName(super.getJaasUserName());
			LOG.debug("profile id ,,,,,,,,......."+profile.getId());
			String profileId=profile.getId();
			LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>profile id   "+profileId);
			
			
			DayEndResponse response=	processProxy.isPreviousDayEndRun(getJaasUserName(), DateUtil.convertToXMLGregorianCalendar(new Date()));
			boolean ispreviousRun=response.isStatus();
			if(ispreviousRun){
				//need to run past dayEnds
				super.setInformationMessage("Day end has not  been run for  "+DateUtil.convertDateToString(DateUtil.convertToDate(response.getDayEndDate())));
				return "";
			}
			boolean dayEndResult=processProxy.checkTellerDayEndsPendingApproval(profileId);
			LOG.debug(">>>>>>>>>>>>>>>>>>>start day resuly    bbvcccccccc  "+dayEndResult);
			
			if(!dayEndResult){
			ProcessTransaction startDayTxn= new ProcessTransaction();
			startDayTxn.setAmount(MoneyUtil.convertToCents(floatAmount));
		//LOG.debug("view startdaySummary-------------float amount in cents ---------------"+startDayTxn.getAmount());
		//LOG.debug(" view startdaysummary---------start of day amount >>>>>>>>>>>>>>>>>>>>>>>>>"+startDayTxn.getAmount());
					startDayTxn.setTransactionType(TransactionType.START_OF_DAY_FLOAT_IN);
					startDayTxn.setBranchId(profile.getBranchId());
					startDayTxn.setProfileId(profile.getId());
		
					startDayTxn.setStatus(TransactionStatus.AWAITING_APPROVAL);
		/*
		 * 
		 * after ProcessTxn IS RETURNED SET ID IN SESSIONSCOPE AND
		 */
						try {
							startDayTxn=processProxy.createStartOfDayTransaction(startDayTxn, getJaasUserName());
			/*LOG.debug("startday txn-----------------------------"+startDayTxn);
			LOG.debug("---------------------start day txn   id "+startDayTxn.getId());
			LOG.debug("---------------------start day txn   float "+startDayTxn.getAmount());
			LOG.debug("---------------------start day txn   branchid "+startDayTxn.getBranchId());
			LOG.debug("---------------------start day txn   profileid "+startDayTxn.getProfileId());
			*/
			
							getRequestScope().put("processTxnId", startDayTxn.getId());
							super.setInformationMessage("Float received successfully added to the system.");
			
							gotoPage("/teller/viewStartDaySummary.jspx");
							} catch (Exception e) {
						super.setErrorMessage("Error occured operation aborted.");
						e.printStackTrace();
						}
			}else if(dayEndResult){
				super.setErrorMessage("You have day ends that have not been approved. Please contact your supervisor.");
			}
			
		}
		else{
			super.setInformationMessage("Please enter a float value greater than 0.00");
		}
		
		return "";
	}
	
	public String cancel(){
		return "";
	}
}
